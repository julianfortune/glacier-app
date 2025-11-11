package com.julianfortune.glacier.repository

import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.db.Category as DbCategory
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.db.Database
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
        repository.insert(Category("Foobar"))

        // THEN
        val rows = database.categoryQueries.getAll().executeAsList()
        assertThat(rows).containsExactly(DbCategory(1, "Foobar"))
    }

    @Test
    fun deleteByIdOnNonExistentCategoryFailsGracefully(): Unit = runBlocking {
        // WHEN
        val wasSuccessful = repository.deleteById(3L)

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