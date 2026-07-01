package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val database: Database) {

    fun getAll(): Flow<List<Entity<Item>>> {
        return database.itemQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { items ->
                items.map { i ->
                    // TODO(P1): Fetch categories as well
                    val categoryIds = emptyList<Long>()
                    Entity(i.id, Item(i.name, categoryIds))
                }
            }
    }

    suspend fun getById(id: Long): Entity<Item> {
        val item = database.itemQueries.getById(id).awaitAsOne()
        val itemCategoryIds = database
            .itemCategoryQueries
            .getAllByItemId(item.id)
            .awaitAsList()
            .map { it.categoryId }

        return Entity(item.id, Item(item.name, itemCategoryIds))
    }

    suspend fun insert(item: Item): Long {
        val newItemId = database.itemQueries.insert(item.name, null).awaitAsOne()

        for (categoryId in item.categoryIds) {
            database.itemCategoryQueries.insert(newItemId, categoryId)
        }

        return newItemId
    }

    suspend fun update(item: Entity<Item>) {
        database.itemQueries.updateById(item.data.name, null, item.id)
        database.itemCategoryQueries.deleteByItemId(item.id)

        for (categoryId in item.data.categoryIds) {
            database.itemCategoryQueries.insert(item.id, categoryId)
        }
    }

    suspend fun deleteById(id: Long): Boolean {
        val rowsUpdated = database.itemQueries.deleteById(id) // `ItemCategories` are deleted by CASCADE-ing

        return rowsUpdated > 1
    }
}
