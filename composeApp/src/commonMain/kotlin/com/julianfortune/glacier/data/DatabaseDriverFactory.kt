package com.julianfortune.glacier.data

import app.cash.sqldelight.db.SqlDriver

// Based on https://sqldelight.github.io/sqldelight/2.1.0/multiplatform_sqlite/ and
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(databaseDriverFactory: DatabaseDriverFactory): Database {
    val driver = databaseDriverFactory.createDriver()
    val database = Database(driver)

    // Do more work with the database (see below).

    return database
}
