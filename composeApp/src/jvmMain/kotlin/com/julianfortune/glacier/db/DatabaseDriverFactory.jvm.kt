package com.julianfortune.glacier.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.nio.file.Path
import java.util.Properties

actual class DatabaseDriverFactory(val databaseDirectory: Path) {

    companion object {
        const val FILE_NAME = "default.db"
    }

    actual suspend fun createDriver(): SqlDriver {
        val databasePath = databaseDirectory.resolve(FILE_NAME)
        println("Using database at: '$databasePath'")

        val properties = Properties().apply {
            setProperty("foreign_keys", "on") // NOTE: `foreign_keys` are disabled by default
        }

        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath}", properties).also {
            Database.Schema.create(it).await()
        }

        return driver
    }

}
