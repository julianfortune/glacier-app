package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.QueryResult
import com.julianfortune.glacier.data.Category
import com.julianfortune.glacier.data.Database
import com.julianfortune.glacier.data.Supplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SupplierRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Supplier>> {
        return database.supplierQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // TODO: Probably need to look into error handling ...
    fun deleteById(id: Long) {
        database.supplierQueries.deleteById(id)
    }
}