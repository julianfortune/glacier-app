package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.NewCategory
import com.julianfortune.glacier.data.persisted.SavedCategory

import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<SavedCategory>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories ->
                categories.map { SavedCategory(it.id, it.name) }
            }
    }

    suspend fun delete(category: SavedCategory): Boolean {
        val deletedId = database.categoryQueries.deleteById(category.id)

        return deletedId == category.id
    }

    suspend fun insert(newCategory: NewCategory): Long {
        return database.categoryQueries.insert(newCategory.name)
    }
}