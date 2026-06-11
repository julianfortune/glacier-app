package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.PurchasingAccount
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PurchasingAccountRepository(private val database: Database) : NamedEntityRepository<PurchasingAccount> {

    // TODO: Understand Flows and coroutine contexts
    override fun getAll(): Flow<List<Entity<PurchasingAccount>>> {
        return database.purchasingAccountQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { accounts ->
                accounts.map { s ->
                    Entity(s.id, PurchasingAccount(s.name))
                }
            }
    }

    override suspend fun insert(data: PurchasingAccount): Long {
        return database.purchasingAccountQueries.insert(data.name).awaitAsOne()
    }

    override suspend fun update(entity: Entity<PurchasingAccount>) {
        database.purchasingAccountQueries.updateById(entity.data.name, entity.id)
    }

    override suspend fun deleteById(id: Long): Boolean {
        val rowsUpdated = database.purchasingAccountQueries.deleteById(id)

        return rowsUpdated > 1
    }
}
