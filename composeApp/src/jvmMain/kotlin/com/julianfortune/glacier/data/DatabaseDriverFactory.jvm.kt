package com.julianfortune.glacier.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // TODO: Eventually need to have a sensible path, e.g.:
        // val databasePath = File(System.getProperty("user.home"), ".your-app-name/your-database.db")
        // databasePath.parentFile?.mkdirs()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db", Properties(), Database.Schema)
        return driver
    }
}