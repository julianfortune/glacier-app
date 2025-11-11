package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Category

import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val database: Database) {

    // TODO: Probably need pagination ...?
    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Entity<Category>>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories ->
                categories.map { c ->
                    Entity(c.id, Category(c.name))
                }
            }
    }

    suspend fun deleteById(id: Long): Boolean {
        val deletedId = database.categoryQueries.deleteById(id)

        return deletedId == id
    }

    suspend fun insert(category: Category): Long {
        return database.categoryQueries.insert(category.name)
    }
}
