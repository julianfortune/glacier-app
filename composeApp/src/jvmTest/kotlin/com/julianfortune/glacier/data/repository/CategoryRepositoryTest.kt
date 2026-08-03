package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.Database.Companion.invoke
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Properties
import kotlin.properties.Delegates

class CategoryRepositoryTest {

    val database = runBlocking { createTestDatabase() }
    val repository = CategoryRepository(database)

    @Nested
    inner class GivenACategory {

        private var categoryId by Delegates.notNull<Long>()

        @BeforeEach
        fun setUp() {
            categoryId = runBlocking {
                database.categoryQueries.insert("Example").awaitAsOne()
            }
        }

        @Nested
        inner class WithAReferencingReport {

            private var reportId by Delegates.notNull<Long>()

            @BeforeEach
            fun setUp() {
                reportId = runBlocking {
                    database.basicReportQueries.insert(
                        "Example Report",
                        "2026-01-01",
                        "2026-01-31",
                        null,
                        categoryId,
                        null,
                        null,
                        null,
                        null,
                        Instant.now().epochSecond,
                        Instant.now().epochSecond,
                    ).awaitAsOne()
                }

                runBlocking {
                    val report = database.basicReportQueries.getById(reportId).awaitAsOne()
                    println(report)
                }
            }

            @Test
            fun deletingCategoryFails() {
                // WHEN
                val result = runBlocking { repository.delete(categoryId) }

                // THEN
                Assertions.assertThat(result.isFailure).isTrue()
            }
        }
    }
}