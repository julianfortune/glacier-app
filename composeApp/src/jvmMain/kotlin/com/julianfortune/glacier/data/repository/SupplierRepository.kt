package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SupplierRepository(private val database: Database) : NamedEntityRepository<Supplier> {

    // TODO: Understand Flows and coroutine contexts
    override fun getAll(): Flow<List<Entity<Supplier>>> {
        return database.supplierQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { suppliers ->
                suppliers.map { s ->
                    Entity(s.id, Supplier(s.name))
                }
            }
    }

    override suspend fun insert(data: Supplier): Long {
        return database.supplierQueries.insert(data.name).awaitAsOne()
    }

    override suspend fun update(entity: Entity<Supplier>) {
        database.supplierQueries.updateById(entity.data.name, entity.id)
    }

    override suspend fun deleteById(id: Long): Boolean {
        val rowsUpdated = database.supplierQueries.deleteById(id)

        return rowsUpdated > 1
    }
}
