package com.julianfortune.glacier

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.julianfortune.glacier.db.Database
import java.util.Properties

suspend fun createTestDatabase(): Database {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties()).also {
        Database.Schema.create(it).await()
    }

    return Database(driver)
}
