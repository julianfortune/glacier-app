package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.Query
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.Supplier as DbSupplier

class SupplierRepository(database: Database) : NamedEntityRepository<DbSupplier, Supplier> {
    override val entityName = "Supplier"

    override fun fromRow(row: DbSupplier): Supplier {
        return Supplier(row.id, row.name)
    }

    override val getAllFn: () -> Query<DbSupplier> = database.supplierQueries::getAll
    override val insertFn = database.supplierQueries::insert
    override val updateFn = database.supplierQueries::updateById
    override val deleteFn = database.supplierQueries::deleteById
}