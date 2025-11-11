package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class DeliveryRepository(private val database: Database) {

    // TODO(P3): Understand Flows and coroutine contexts
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

    suspend fun getById(deliveryId: Long): Entity<DeliveryDetail> {
        val entryRows = database
            .deliveryEntryQueries
            .getByDeliveryId(deliveryId)
            .awaitAsList()

        val deliveryRow = database
            .deliveryQueries
            .getById(deliveryId)
            .awaitAsOne()

        val entries = entryRows.map { entry ->
            val costStatus = CostStatusCodec.deserialize(entry.costStatus).orThrow()
            val aggregate = if (entry.aggregateLabel != null && entry.aggregateCount != null) {
                EntryAggregation(
                    entry.aggregateLabel,
                    entry.aggregateCount
                )
            } else null

            // TODO: Gather foreign keys
            val purchasingAccounts = emptyList<Allocation<Long>>() // TODO
            val programs = emptyList<Allocation<Long>>() // TODO

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

        return Entity(
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

    suspend fun insert(delivery: DeliveryDetail): Long {
        val now = Instant.now()

        val deliveryId = database.deliveryQueries.insert(
            LocalDateCodec.serialize(delivery.receivedDate),
            delivery.supplierId,
            delivery.taxesCents,
            delivery.feesCents,
            now.toString(),
            now.toString(),
        )

        delivery.entries?.forEach { entry ->
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

        return deliveryId
    }

    suspend fun deleteById(id: Long): Boolean {
        // TODO(P2): Probably need to look into error handling ...
        val deletedId = database.deliveryQueries.deleteById(id)

        return deletedId == id
    }

}
