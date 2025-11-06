package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Category
import com.julianfortune.glacier.data.persisted.Category as PersistedCategory

import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<PersistedCategory>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories ->
                categories.map { PersistedCategory(it.id, it.name) }
            }
    }

    suspend fun delete(category: PersistedCategory): Boolean {
        val deletedId = database.categoryQueries.deleteById(category.id)

        return deletedId == category.id
    }

    suspend fun insert(category: Category): Long {
        return database.categoryQueries.insert(category.name)
    }
}