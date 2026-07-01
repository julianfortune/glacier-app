package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.julianfortune.glacier.db.Item as DbItem

class ItemRepositoryTest {

    lateinit var database: Database

    lateinit var itemRepository: ItemRepository
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        database = createTestDatabase()

        // Blow away test data defined in `.sq` files
        database.itemCategoryQueries.deleteAll()
        database.itemQueries.deleteAll()
        database.categoryQueries.deleteAll()

        itemRepository = ItemRepository(database)
        categoryRepository = CategoryRepository(database)
    }

    // TODO: Use `runTest` instead ?
    @Test
    fun insert(): Unit = runBlocking {
        // GIVEN
        val categoryId = categoryRepository.insert(Category("Dairy"))
        val item = Item("Butter", listOf(categoryId))

        // WHEN
        val id = itemRepository.insert(item)

        // THEN
        val rows = database.itemQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(DbItem(id, item.name, null))

        val itemCategoryRows = database.itemCategoryQueries.getAll().awaitAsList()
        assertThat(itemCategoryRows).hasSize(1)
        assertThat(itemCategoryRows).matches { rows ->
            rows.first().itemId == id && rows.first().categoryId == categoryId
        }
    }

    @Test
    fun update(): Unit = runBlocking {
        // GIVEN
        val categoryId = categoryRepository.insert(Category("Dairy"))
        val item = Item("Butter", listOf(categoryId))
        val id = itemRepository.insert(item)

        val updatedCategoryId = categoryRepository.insert(Category("Perishable"))
        val updatedItem = Item("Margerine", listOf(updatedCategoryId))

        // WHEN
        itemRepository.update(Entity(id, updatedItem))

        // THEN
        val rows = database.itemQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(
            DbItem(
                id,
                updatedItem.name,
                null,
            )
        )

        val itemCategoryRows = database.itemCategoryQueries.getAll().awaitAsList()
        assertThat(itemCategoryRows).hasSize(1)
        assertThat(itemCategoryRows).matches { rows ->
            rows.first().itemId == id && rows.first().categoryId == updatedCategoryId
        }
    }

    @Test
    fun deleteById(): Unit = runBlocking {
        // GIVEN
        val categoryId = categoryRepository.insert(Category("Dairy"))
        val item = Item("Butter", listOf(categoryId))
        val itemId = itemRepository.insert(item)

        // WHEN
        val result = itemRepository.deleteById(itemId)

        // THEN
        val rows = database.itemQueries.getAll().awaitAsList()
        assertThat(rows).hasSize(0)

        val itemCategoryRows = database.itemCategoryQueries.getAll().awaitAsList()
        assertThat(itemCategoryRows).hasSize(0)
    }

}