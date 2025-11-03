package com.julianfortune.glacier.db

import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.NewCategory
import com.julianfortune.glacier.data.persisted.SavedCategory
import com.julianfortune.glacier.repository.CategoryRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CategoryRepositoryTest {

    lateinit var database: Database
    lateinit var repository: CategoryRepository

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        database = createTestDatabase()

        // Blow away test data defined in `.sq` files
        database.categoryQueries.deleteAll()

        repository = CategoryRepository(database)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        // Clear any test data
        database.categoryQueries.deleteAll()
    }

    // TODO: Use `runTest` instead ?
    @Test
    fun insert(): Unit = runBlocking {
        // WHEN
        repository.insert(NewCategory("Foobar"))

        // THEN
        val rows = database.categoryQueries.getAll().executeAsList()
        assertThat(rows).containsExactly(Category(1, "Foobar"))
    }

    @Test
    fun deleteOnNonExistentCategoryFailsGracefully(): Unit = runBlocking {
        // WHEN
        val wasSuccessful = repository.delete(SavedCategory(3L, "Foobar"))

        // THEN
        assertThat(wasSuccessful).isFalse
    }

    @Nested
    inner class WithExistingRows {

        @BeforeEach
        fun setUp(): Unit = runBlocking {
            database.categoryQueries.insert("Agriculture")
            database.categoryQueries.insert("Bulk")
            database.categoryQueries.insert("Canned")
        }

        @Test
        fun getAll() {
            // WHEN
            val rows = database.categoryQueries.getAll().executeAsList()

            // THEN
            assertThat(rows).hasSize(3)
        }
    }
}