package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SupplierRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Entity<Supplier>>> {
        return database.supplierQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { suppliers ->
                suppliers.map { s ->
                    Entity(s.id, Supplier(s.name))
                }
            }
    }

    suspend fun deleteById(id: Long): Boolean {
        val deletedId = database.supplierQueries.deleteById(id)

        return deletedId == id
    }
}
