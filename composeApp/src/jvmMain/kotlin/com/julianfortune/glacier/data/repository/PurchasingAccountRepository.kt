package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.Query
import com.julianfortune.glacier.data.domain.PurchasingAccount
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.PurchasingAccount as DbPurchasingAccount

class PurchasingAccountRepository(database: Database) : NamedEntityRepository<DbPurchasingAccount, PurchasingAccount> {
    override val entityName = "PurchasingAccount"

    override fun fromRow(row: DbPurchasingAccount): PurchasingAccount {
        return PurchasingAccount(row.id, row.name)
    }

    override val getAllFn: () -> Query<DbPurchasingAccount> = database.purchasingAccountQueries::getAll
    override val insertFn = database.purchasingAccountQueries::insert
    override val updateFn = database.purchasingAccountQueries::updateById
    override val deleteFn = database.purchasingAccountQueries::deleteById
}