package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ItemRepositoryTest {

    val database = runBlocking { createTestDatabase() }

    val itemRepository = ItemRepository(database)

    @Test
    fun getAll(): Unit = runBlocking {
        // GIVEN
        val id1 = database.itemQueries.insert("Apple", null).awaitAsOne()
        val id2 = database.itemQueries.insert("Banana", null).awaitAsOne()

        // WHEN
        val result = itemRepository.getAll().first()

        // THEN
        assertThat(result).containsExactlyInAnyOrder(
            ItemHeadline(id1, "Apple"),
            ItemHeadline(id2, "Banana")
        )
    }

    @Test
    fun getById(): Unit = runBlocking {
        // GIVEN
        val categoryId = database.categoryQueries.insert("Produce").awaitAsOne()
        val itemId = database.itemQueries.insert("Apple", null).awaitAsOne()
        database.itemCategoryQueries.insert(itemId, categoryId)

        // WHEN
        val item = itemRepository.getById(itemId).first()

        // THEN
        assertThat(item.id).isEqualTo(itemId)
        assertThat(item.name).isEqualTo("Apple")
        assertThat(item.categories).containsExactly(Category(categoryId, "Produce"))
    }

    @Test
    fun searchByName(): Unit = runBlocking {
        // GIVEN
        val categoryId = database.categoryQueries.insert("Dairy").awaitAsOne()
        val itemId = database.itemQueries.insert("Butter", null).awaitAsOne()
        database.itemCategoryQueries.insert(itemId, categoryId)

        // Unrelated item that shouldn't match
        database.itemQueries.insert("Apple", null).awaitAsOne()

        // WHEN
        val results = itemRepository.searchByName("But").first()

        // THEN
        assertThat(results).hasSize(1)
        val firstResult = results.first()
        assertThat(firstResult.id).isEqualTo(itemId)
        assertThat(firstResult.name).isEqualTo("Butter")
        assertThat(firstResult.categories).containsExactly(Category(categoryId, "Dairy"))
    }

    @Test
    fun insert(): Unit = runBlocking {
        // GIVEN
        val categoryId = database.categoryQueries.insert("Dairy").awaitAsOne()

        // WHEN
        val id = itemRepository.insert("Butter", setOf(categoryId)).getOrThrow()

        // THEN
        val rows = database.itemQueries.getAllItems().awaitAsList()
        assertThat(rows).hasSize(1)
        assertThat(rows.first().id).isEqualTo(id)
        assertThat(rows.first().name).isEqualTo("Butter")

        // Confirm bridge table record matches mapping expectations
        val itemCategoryRows = database.itemCategoryQueries.getAllByItemId(id).awaitAsList()
        assertThat(itemCategoryRows).hasSize(1)
        assertThat(itemCategoryRows.first().categoryId).isEqualTo(categoryId)
    }

    @Test
    fun update(): Unit = runBlocking {
        // GIVEN
        val initialCategoryId = database.categoryQueries.insert("Dairy").awaitAsOne()
        val itemId = database.itemQueries.insert("Butter", null).awaitAsOne()
        database.itemCategoryQueries.insert(itemId, initialCategoryId)

        val newCategoryId = database.categoryQueries.insert("Perishable").awaitAsOne()

        // WHEN
        val id = itemRepository.update(itemId, "Margarine", setOf(newCategoryId)).getOrThrow()

        // THEN
        assertThat(id).isEqualTo(itemId)

        val item = itemRepository.getById(itemId).first()
        assertThat(item.name).isEqualTo("Margarine")
        assertThat(item.categories).containsExactly(Category(newCategoryId, "Perishable"))
    }

    @Test
    fun deleteById(): Unit = runBlocking {
        // GIVEN
        val categoryId = database.categoryQueries.insert("Dairy").awaitAsOne()
        val itemId = database.itemQueries.insert("Butter", null).awaitAsOne()
        database.itemCategoryQueries.insert(itemId, categoryId)

        // WHEN
        itemRepository.deleteById(itemId)

        // THEN
        val items = database.itemQueries.getAllItems().awaitAsList()
        assertThat(items).isEmpty()

        // Cascade rule check: deleting an item should orphan and clear bridge items automatically
        val bridgeRows = database.itemCategoryQueries.getAllByItemId(itemId).awaitAsList()
        assertThat(bridgeRows).isEmpty()
    }
}
