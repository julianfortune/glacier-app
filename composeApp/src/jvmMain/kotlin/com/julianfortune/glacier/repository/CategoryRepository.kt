package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Category
import com.julianfortune.glacier.data.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val database: Database) {

    // TODO: Understand Flows and coroutine contexts
    fun getAllTasks(): Flow<List<Category>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

//    suspend fun getTaskById(id: Long): Task? {
//        return database.taskQueries.selectTaskById(id)
//            .executeAsOneOrNull()
//    }
//
//    suspend fun insertTask(title: String, description: String?) {
//        database.taskQueries.insertTask(
//            title = title,
//            description = description,
//            completed = false,
//            created_at = System.currentTimeMillis()
//        )
//    }
//
//    suspend fun updateTaskCompleted(id: Long, completed: Boolean) {
//        database.taskQueries.updateTaskCompleted(completed, id)
//    }
//
//    suspend fun deleteTask(id: Long) {
//        database.taskQueries.deleteTask(id)
//    }
}