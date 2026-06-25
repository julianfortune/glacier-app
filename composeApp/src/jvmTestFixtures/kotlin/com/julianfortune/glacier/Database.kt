package com.julianfortune.glacier

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.julianfortune.glacier.db.Database
import java.util.*

suspend fun createTestDatabase(): Database {
    val properties = Properties().apply {
        setProperty("foreign_keys", "on")
    }

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, properties).also {
        Database.Schema.create(it).await()
    }

    driver.executeQuery(
        identifier = null,
        sql = "PRAGMA foreign_keys;",
        mapper = { cursor ->
            val isEnabled = if (cursor.next().value) cursor.getLong(0) == 1L else false
            println("⚙️ [Database Connection Startup] PRAGMA foreign_keys = $isEnabled")
            QueryResult.Unit
        },
        parameters = 0
    )

    return Database(driver)
}
