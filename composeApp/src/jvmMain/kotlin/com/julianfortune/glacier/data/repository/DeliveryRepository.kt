package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.core.codec.CostStatusCodec
import com.julianfortune.glacier.core.codec.LocalDateCodec
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.db.Database
import dev.forkhandles.result4k.orThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.Instant

class DeliveryRepository(private val database: Database) {

    fun getAllAsHeadlines(): Flow<List<Entity<DeliveryHeadline>>> {
        return database.deliveryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { deliveries ->
                deliveries.map { d ->
                    // TODO: Better error handling
                    val receivedDate = LocalDateCodec.deserialize(d.receivedDate).orThrow()

                    Entity(d.id, DeliveryHeadline(receivedDate, d.supplierId, d.taxesCents, d.feesCents))
                }
            }
    }

    fun getDeliveryDetailById(deliveryId: Long): Flow<Entity<DeliveryDetail>> {
        return combine(
            database.deliveryQueries.getById(deliveryId).asFlow(),
            database.deliveryEntryQueries.getByDeliveryId(deliveryId).asFlow()
        ) { deliveryResult, entriesResult ->
            val deliveryRow = deliveryResult.awaitAsOne()
            val entryRows = entriesResult.awaitAsList()

            val entries = entryRows.map { entry ->
                val costStatus = CostStatusCodec.deserialize(entry.costStatus).orThrow()
                val itemWeight = entry.itemWeightCentigrams?.let(Weight::ofCentigrams)
                val unitWeight = entry.unitWeightCentigrams.let(Weight::ofCentigrams)

                Entry(
                    entry.itemId,
                    entry.unitCount,
                    entry.unitName,
                    unitWeight,
                    costStatus,
                    entry.unitCostCents,
                    itemWeight,
                    entry.itemsPerUnit,
                    entry.programId,
                    entry.purchasingAccountId,
                )
            }

            Entity(
                deliveryRow.id,
                DeliveryDetail(
                    LocalDateCodec.deserialize(deliveryRow.receivedDate).orThrow(),
                    deliveryRow.supplierId,
                    deliveryRow.taxesCents,
                    deliveryRow.feesCents,
                    entries
                )
            )
        }
            .filterNotNull()
            .distinctUntilChanged() // Only emit when data actually changes
    }

    suspend fun insert(delivery: DeliveryDetail): Long {
        val now = Instant.now()

        val deliveryId = database.deliveryQueries.insert(
            LocalDateCodec.serialize(delivery.receivedDate),
            delivery.supplierId,
            delivery.taxesCents,
            delivery.feesCents,
            now.toString(),
            now.toString(),
        ).awaitAsOne()

        delivery.entries?.forEach { entry ->
            insertDeliveryEntry(deliveryId, entry)
        }

        return deliveryId
    }

    suspend fun update(delivery: Entity<DeliveryDetail>) {
        val now = Instant.now()

        database.deliveryQueries.updateById(
            LocalDateCodec.serialize(delivery.data.receivedDate),
            delivery.data.supplierId,
            delivery.data.taxesCents,
            delivery.data.feesCents,
            now.toString(),
            delivery.id,
        )

        // Delete all the entries and insert all new ones
        // TODO(P4): It might be helpful to assign each entry an 'index' because order matters, and then
        //  we can do mutations more efficiently
        database.deliveryEntryQueries.deleteByDeliveryId(delivery.id)
        delivery.data.entries?.forEach { entry ->
            insertDeliveryEntry(delivery.id, entry)
        }
    }

    suspend fun updateDetailsOnly(delivery: Entity<DeliveryDetail>) {
        val now = Instant.now()

        database.deliveryQueries.updateById(
            LocalDateCodec.serialize(delivery.data.receivedDate),
            delivery.data.supplierId,
            delivery.data.taxesCents,
            delivery.data.feesCents,
            now.toString(),
            delivery.id,
        )
    }

    suspend fun insertDeliveryEntry(deliveryId: Long, entry: Entry) {
        val costStatus = CostStatusCodec.serialize(entry.costStatus)
        database.deliveryEntryQueries.insert(
            deliveryId,
            entry.itemId,
            entry.unitCount,
            entry.unitName,
            entry.itemsPerUnit,
            entry.itemWeight?.centigrams,
            entry.unitWeight.centigrams,
            costStatus,
            entry.unitCostCents,
            entry.programId,
            entry.purchasingAccountId,
        )
    }

    suspend fun deleteById(id: Long): Boolean {
        // TODO(P2): Maybe look into error handling or toast system ...
        val rowsDeleted = database.deliveryQueries.deleteById(id)

        return rowsDeleted > 0
    }

}
