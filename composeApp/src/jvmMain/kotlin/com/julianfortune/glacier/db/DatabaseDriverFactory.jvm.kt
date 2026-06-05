package com.julianfortune.glacier.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        // TODO(P4): Eventually need to have a sensible path, e.g.:
        // val databasePath = File(System.getProperty("user.home"), ".your-app-name/your-database.db")
        // databasePath.parentFile?.mkdirs()

        val properties = Properties().apply {
            setProperty("foreign_keys", "on") // NOTE: `foreign_keys` are disabled by default
        }

        val driver = JdbcSqliteDriver("jdbc:sqlite:test.db", properties).also {
            Database.Schema.create(it).await()
        }

        return driver
    }
}
