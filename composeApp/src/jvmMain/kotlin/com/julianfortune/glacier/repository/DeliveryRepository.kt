package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.codec.LocalDateCodec
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.EntryAggregation
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.Delivery
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

    suspend fun deleteById(id: Long): Boolean {
        // TODO(P2): Probably need to look into error handling ...
        val deletedId = database.deliveryQueries.deleteById(id)

        return deletedId == id
    }

    suspend fun insert(
        receivedDate: String,
        supplierId: Long?,
        taxesCents: Long?,
        feesCents: Long?,
    ): Long {
        val now = Instant.now()

        return database.deliveryQueries.insert(
            receivedDate,
            supplierId,
            taxesCents,
            feesCents,
            now.toString(),
            now.toString(),
        )
    }

    fun getById(deliveryId: Long): Flow<Entity<Delivery>> {
        val entries = database.deliveryEntryQueries.getByDeliveryId(deliveryId).executeAsList()

        return TODO()
//        return entries.map { entry ->
//            // TODO: All this mapping crap needs to live in one place
//            val costStatus = when (entry.costStatus) {
//                "PURCHASED" -> CostStatus.PURCHASED
//                "NO_COST" -> CostStatus.NO_COST
//                else -> throw Exception("Unrecognized costStatus: ${entry.costStatus}")
//            }
//            val aggregate = if (entry.aggregateLabel != null && entry.aggregateCount != null) {
//                EntryAggregation(
//                    entry.aggregateLabel,
//                    entry.aggregateCount
//                )
//            } else null
//
//            val purchasingAccounts = emptyList<PurchasingAccountAllocation>() // TODO
//            val programs = emptyList<ProgramAllocation>() // TODO
//
//            PersistedEntry(
//                entry.id,
//                entry.itemCount,
//                costStatus,
//                entry.itemCostCents,
//                aggregate,
//                getItemById(entry.itemId),
//                purchasingAccounts,
//                programs,
//            )
//        }
    }

    suspend fun insert(
        deliveryId: Long,
        itemId: Long,
        itemCount: Long,
        costStatus: CostStatus,
        itemCostCents: Long,
        aggregate: EntryAggregation?,
    ): Long {
        return database.deliveryEntryQueries.insert(
            deliveryId,
            itemId,
            itemCount,
            costStatus.toString(),
            itemCostCents,
            aggregate?.label,
            aggregate?.aggregateCount,
        )
    }
}