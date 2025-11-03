package com.julianfortune.glacier.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        // TODO: Eventually need to have a sensible path, e.g.:
        // val databasePath = File(System.getProperty("user.home"), ".your-app-name/your-database.db")
        // databasePath.parentFile?.mkdirs()

        return JdbcSqliteDriver("jdbc:sqlite:test.db").also {
            Database.Schema.create(it).await()
        }
    }
}