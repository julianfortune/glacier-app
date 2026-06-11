package com.julianfortune.glacier.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val database: Database) : NamedEntityRepository<Category> {

    // TODO: Understand Flows and coroutine contexts
    override fun getAll(): Flow<List<Entity<Category>>> {
        return database.categoryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories ->
                categories.map { c ->
                    Entity(c.id, Category(c.name))
                }
            }
    }

    override suspend fun insert(data: Category): Long {
        return database.categoryQueries.insert(data.name)
    }

    override suspend fun update(entity: Entity<Category>) {
        database.categoryQueries.updateById(entity.data.name, entity.id)
    }

    override suspend fun deleteById(id: Long): Boolean {
        val deletedId = database.categoryQueries.deleteById(id)

        return deletedId == id
    }

}
