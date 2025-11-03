package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.Supplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SupplierRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Supplier>> {
        return database.supplierQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun deleteById(id: Long): Boolean {
        val deletedId = database.supplierQueries.deleteById(id)

        return deletedId == id
    }
}