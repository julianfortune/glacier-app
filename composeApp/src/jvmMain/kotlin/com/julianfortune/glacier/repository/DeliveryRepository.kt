package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.Delivery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Delivery>> {
        return database.deliveryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // TODO: Probably need to look into error handling ...
    suspend fun deleteById(id: Long) {
        database.deliveryQueries.deleteById(id)
    }

    suspend fun insert(
        receivedDate: String,
        supplierId: Long?,
        taxesCents: Long?,
        feesCents: Long?,
        creationDateTime: String,
        updateDateTime: String
    ): Long {
        return database.deliveryQueries.insert(
            receivedDate,
            supplierId,
            taxesCents,
            feesCents,
            creationDateTime,
            updateDateTime
        )
    }
}