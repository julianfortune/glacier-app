package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.julianfortune.glacier.core.codec.CostStatusCodec
import com.julianfortune.glacier.core.codec.LocalDateCodec
import com.julianfortune.glacier.data.common.EntityMetadata
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.DeliveryHeadline
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.DeliveryEntry
import dev.forkhandles.result4k.orThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate

class DeliveryRepository(private val database: Database) {

    fun getAllAsHeadlines(): Flow<List<DeliveryHeadline>> {
        return database.deliveryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { deliveries ->
                deliveries.map { d ->
                    // TODO(P3): Better error handling
                    val receivedDate = LocalDateCodec.deserialize(d.receivedDate).orThrow()
                    val supplier = supplierFromJoinedRow(d.supplierId, d.supplierName)
                    val metadata = EntityMetadata.ofEpochSeconds(d.createdAtEpochSeconds, d.updatedAtEpochSeconds)

                    DeliveryHeadline(d.id, receivedDate, supplier, d.taxesCents, d.feesCents, metadata)
                }
            }
    }

    fun getDeliveryById(deliveryId: Long): Flow<Delivery> {
        val deliveryFlow = database.deliveryQueries.getById(deliveryId)
            .asFlow()
            .mapToOne(Dispatchers.IO)

        val entriesFlow = database.deliveryEntryQueries.getByDeliveryId(deliveryId)
            .asFlow()
            .mapToList(Dispatchers.IO)

        return combine(deliveryFlow, entriesFlow) { deliveryRow, entryRows ->
            val supplier = supplierFromJoinedRow(deliveryRow.supplierId, deliveryRow.supplierName)
            val entries = entryRows.map(this::entryFromRow)
            val metadata = EntityMetadata.ofEpochSeconds(
                deliveryRow.createdAtEpochSeconds,
                deliveryRow.updatedAtEpochSeconds,
            )

            Delivery(
                deliveryRow.id,
                LocalDateCodec.deserialize(deliveryRow.receivedDate).orThrow(),
                supplier,
                deliveryRow.taxesCents,
                deliveryRow.feesCents,
                entries,
                metadata
            )
        }.distinctUntilChanged() // Only emit when data actually changes
    }

    private fun supplierFromJoinedRow(supplierId: Long?, supplierName: String?): Supplier? {
        return if (supplierId != null) {
            require(supplierName != null) {
                "Supplier name must be defined when supplierId is defined due to foreign key constraint"
            }
            Supplier(supplierId, supplierName)
        } else null
    }

    private fun entryFromRow(entry: DeliveryEntry): Delivery.Entry {
        val costStatus = CostStatusCodec.deserialize(entry.costStatus).orThrow()
        val itemWeight = entry.itemWeightCentigrams?.let(Weight::ofCentigrams)
        val unitWeight = entry.unitWeightCentigrams.let(Weight::ofCentigrams)
        val metadata = EntityMetadata.ofEpochSeconds(entry.createdAtEpochSeconds, entry.updatedAtEpochSeconds)

        return Delivery.Entry(
            entry.itemId,
            ItemHeadline(0, "TODO"), // TODO
            entry.unitCount,
            unitWeight,
            costStatus,
            entry.unitCostCents,
            itemWeight,
            entry.itemsPerUnit,
            null, // TODO
            null, // TODO
            metadata
        )
    }

//    suspend fun insert(delivery: Delivery): Long {
//        val now = Instant.now()
//
//        val deliveryId = database.deliveryQueries.insert(
//            LocalDateCodec.serialize(delivery.receivedDate),
//            delivery.supplierId,
//            delivery.taxesCents,
//            delivery.feesCents,
//            now.epochSecond,
//            now.epochSecond,
//        ).awaitAsOne()
//
//        delivery.entries?.forEach { entry ->
//            insertDeliveryEntry(deliveryId, entry)
//        }
//
//        return deliveryId
//    }

//    suspend fun update(delivery: Delivery) {
//        val now = Instant.now()
//
//        database.deliveryQueries.updateById(
//            LocalDateCodec.serialize(delivery.receivedDate),
//            delivery.supplierId,
//            delivery.taxesCents,
//            delivery.feesCents,
//            now.epochSecond,
//            delivery.id,
//        )
//
//        database.deliveryEntryQueries.deleteByDeliveryId(delivery.id)
//        delivery.entries.forEach { entry ->
//            insertDeliveryEntry(delivery.id, entry)
//        }
//    }

    // TODO: Method for appending new entry

    // TODO: Method for modifying an entry

    // TODO: Method for deleting an entry

    suspend fun updateDelivery(
        id: Long,
        receivedDate: LocalDate,
        supplierId: Long,
        taxesCents: Long?,
        feesCents: Long?,
    ) {
        val now = Instant.now()

        database.deliveryQueries.updateById(
            LocalDateCodec.serialize(receivedDate),
            supplierId,
            taxesCents,
            feesCents,
            now.epochSecond,
            id,
        )
    }

//    suspend fun insertDeliveryEntry(deliveryId: Long, entry: Entry) {
//        val now = Instant.now()
//
//        val costStatus = CostStatusCodec.serialize(entry.costStatus)
//        database.deliveryEntryQueries.insert(
//            deliveryId,
//            entry.itemId,
//            entry.unitCount,
//            entry.itemsPerUnit,
//            entry.itemWeight?.centigrams,
//            entry.unitWeight.centigrams,
//            costStatus,
//            entry.unitCostCents,
//            entry.programId,
//            entry.purchasingAccountId,
//            now.epochSecond,
//            now.epochSecond,
//        )
//    }

    suspend fun deleteById(id: Long): Boolean {
        // TODO(P2): Maybe look into error handling or toast system ...
        val rowsDeleted = database.deliveryQueries.deleteById(id)

        return rowsDeleted > 0
    }

}
