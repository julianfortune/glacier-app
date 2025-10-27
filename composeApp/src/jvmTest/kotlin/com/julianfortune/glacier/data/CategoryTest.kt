package com.julianfortune.glacier.data

import com.julianfortune.glacier.createTestDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CategoryTest {

    lateinit var database: Database

    @BeforeEach
    fun setUp() {
        database = createTestDatabase()

        // Clear any initial test data
        database.categoryQueries.deleteAll()
    }

    // TODO: Use `runTest` instead ?
    @Test
    fun insert(): Unit = runBlocking {
        // WHEN
        database.categoryQueries.insert("Foobar")

        // THEN
        val rows = database.categoryQueries.getAll().executeAsList()
        assertThat(rows).containsExactly(Category(1, "Foobar"))
    }

    @Nested
    inner class WithExistingRows {

        @BeforeEach
        fun setUp() {
            database.categoryQueries.insert("Agriculture")
            database.categoryQueries.insert("Bulk")
            database.categoryQueries.insert("Canned")
        }

        @Test
        fun getAll(): Unit = runBlocking {
            // WHEN
            val rows = database.categoryQueries.getAll().executeAsList()

            // THEN
            assertThat(rows).hasSize(3)
        }
    }
}