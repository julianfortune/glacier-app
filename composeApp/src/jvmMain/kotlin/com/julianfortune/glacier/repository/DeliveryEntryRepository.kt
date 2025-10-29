package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Database
import com.julianfortune.glacier.data.DeliveryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

enum class CostStatus {
    PURCHASED,
    NO_COST;

    override fun toString(): String {
        return when (this) {
            PURCHASED -> "PURCHASED"
            NO_COST -> "NO_COST"
        }
    }
}

class DeliveryEntryRepository(private val database: Database) {

    fun getAll(): Flow<List<DeliveryEntry>> {
        return database.deliveryEntryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // TODO: Should this be done using a Flow ...?
    fun getAllByDeliveryId(deliveryId: Long): List<DeliveryEntry> {
        return database.deliveryEntryQueries.getByDeliveryId(deliveryId).executeAsList()
    }

    fun insert(
        deliveryId: Long,
        itemId: Long,
        itemCount: Long,
        costStatus: CostStatus,
        itemCostCents: Long,
        aggregateLabel: String?,
        aggregateCount: Long?,
    ): Long {
        return database.deliveryEntryQueries.insert(
            deliveryId,
            itemId,
            itemCount,
            costStatus.toString(),
            itemCostCents,
            aggregateLabel,
            aggregateCount,
        ).value
    }
}