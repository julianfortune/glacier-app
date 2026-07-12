package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val database: Database) {

    fun getAll(): Flow<List<ItemHeadline>> {
        return database.itemQueries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { items ->
                items.map {
                    ItemHeadline(it.id, it.name)
                }
            }
    }

    fun getById(id: Long): Flow<Item> {
        return database.itemQueries.getItemWithCategoriesById(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val first = rows.firstOrNull() ?: throw RuntimeException("Unable to find an Item with id=$id")
                val categories = rows.map { Category(it.categoryId!!, it.categoryName!!) }

                Item(first.id, first.name, categories, emptyList())
            }

    }

    fun searchByName(searchString: String): Flow<List<Item>> {
        return database.itemQueries.getAllItemsWithCategoriesByQueryingName(
            query = "%$searchString%",
            startsWith = "$searchString%",
            exactMatch = searchString,
            limit = 50,
        )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.id }.map { (itemId, itemRows) ->
                    val firstRow = itemRows.first()
                    val categories = rows.map { Category(it.categoryId!!, it.categoryName!!) }

                    Item(
                        id = itemId,
                        name = firstRow.name,
                        categories = categories,
                        savedWeights = emptyList(), // TODO
                    )
                }
            }
    }

    suspend fun insert(name: String, categoryIds: Set<Long>): Result<Long> {
        return Result.runCatching {
            database.transactionWithResult {
                val itemId = database.itemQueries.insert(name, null).awaitAsOne()

                categoryIds.forEach { categoryId ->
                    database.itemCategoryQueries.insert(itemId, categoryId)
                }

                itemId
            }
        }
    }

    suspend fun update(itemId: Long, name: String, categoryIds: Set<Long>): Result<Long> {
        return runCatching {
            database.transactionWithResult {
                database.itemQueries.updateById(name, null, itemId)

                database.itemCategoryQueries.deleteByItemId(itemId)
                categoryIds.forEach { categoryId ->
                    database.itemCategoryQueries.insert(itemId, categoryId)
                }

                itemId
            }
        }
    }

    suspend fun deleteById(id: Long): Boolean {
        val rowsUpdated = database.itemQueries.deleteById(id) // `ItemCategories` are deleted automatically by CASCADE-ing

        return rowsUpdated > 1
    }
}
