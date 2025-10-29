package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Category
import com.julianfortune.glacier.data.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAll(): Flow<List<Category>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // TODO: Probably need to look into error handling ...
    fun deleteById(id: Long) {
        database.categoryQueries.deleteById(id)
    }

    fun insert(name: String): Long {
        return database.categoryQueries.insert(name).value
    }
}