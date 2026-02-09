package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.codec.CostStatusCodec
import com.julianfortune.glacier.codec.LocalDateCodec
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.data.domain.entry.Allocation
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.data.domain.entry.EntryAggregation
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
            // TODO(P2): Gather foreign keys for `purchasing_account` and `program`
        ) { deliveryResult, entriesResult ->
            val deliveryRow = deliveryResult.executeAsOneOrNull() ?: return@combine null
            val entryRows = entriesResult.executeAsList()

            val entries = entryRows.map { entry ->
                val costStatus = CostStatusCodec.deserialize(entry.costStatus).orThrow()
                val aggregate = if (entry.aggregateLabel != null && entry.aggregateCount != null) {
                    EntryAggregation(
                        entry.aggregateLabel,
                        entry.aggregateCount
                    )
                } else null

                // TODO(P2): Plug in the foreign key results
                val purchasingAccounts = emptyList<Allocation<Long>>()
                val programs = emptyList<Allocation<Long>>()

                Entry(
                    entry.itemId,
                    entry.itemCount,
                    costStatus,
                    entry.itemCostCents,
                    aggregate,
                    purchasingAccounts,
                    programs,
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
        ).executeAsOneOrNull()

        if (deliveryId == null) {
            // TODO(P3): Proper error handling
            throw RuntimeException("Failed to insert")
        }

        delivery.entries?.forEach { entry ->
            insertDeliveryEntry(deliveryId, entry)
        }

        return deliveryId
    }

    suspend fun update(delivery: Entity<DeliveryDetail>) {
        val now = Instant.now()

        val updateResult = database.deliveryQueries.update(
            LocalDateCodec.serialize(delivery.data.receivedDate),
            delivery.data.supplierId,
            delivery.data.taxesCents,
            delivery.data.feesCents,
            now.toString(),
        )

        println("updateResult=$updateResult")

        // Delete all the entries and insert all new ones
        // TODO: Maybe it would be helpful to assign each entry an 'index' because order matters, and then
        //  we can do mutations more efficiently
        database.deliveryEntryQueries.deleteByDeliveryId(delivery.id)
        delivery.data.entries?.forEach { entry ->
            insertDeliveryEntry(delivery.id, entry)
        }
    }

    suspend fun insertDeliveryEntry(deliveryId: Long, entry: Entry) {
        val costStatus = CostStatusCodec.serialize(entry.costStatus)
        val entryId = database.deliveryEntryQueries.insert(
            deliveryId,
            entry.itemId,
            entry.itemCount,
            costStatus,
            entry.itemCostCents,
            entry.aggregate?.label,
            entry.aggregate?.count,
        )

        entry.programAllocations?.forEach { allocation ->
            database.deliveryEntryProgramQueries.insert(
                entryId,
                allocation.allocatedTo,
                allocation.percentage.valueInHundredths.toLong()
            )
        }

        entry.purchasingAccountAllocations?.forEach { allocation ->
            database.deliveryEntryPurchasingAccountQueries.insert(
                entryId,
                allocation.allocatedTo,
                allocation.percentage.valueInHundredths.toLong()
            )
        }
    }

    suspend fun deleteById(id: Long): Boolean {
        // TODO(P2): Maybe look into error handling or toast system ...
        val rowsDeleted = database.deliveryQueries.deleteById(id)

        return rowsDeleted > 0
    }

}
