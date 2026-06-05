package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.julianfortune.glacier.db.Supplier as DbSupplier

class SupplierRepositoryTest {

    lateinit var database: Database

    lateinit var repository: SupplierRepository

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        database = createTestDatabase()

        // Blow away test data defined in `.sq` files
        database.supplierQueries.deleteAll()

        repository = SupplierRepository(database)
    }

    // TODO: Use `runTest` instead ?
    @Test
    fun insert(): Unit = runBlocking {
        // GIVEN
        val supplier = Supplier("FoodSource LLC")

        // WHEN
        val id = repository.insert(supplier)

        // THEN
        val rows = database.supplierQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(DbSupplier(id, supplier.name))
    }

    @Test
    fun update(): Unit = runBlocking {
        // GIVEN
        val supplier = Supplier("FoodSource LLC")
        val id = repository.insert(supplier)

        val updatedSupplier = Supplier("FoodSource, LLC.")

        // WHEN
        repository.update(Entity(id, updatedSupplier))

        // THEN
        val rows = database.supplierQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(DbSupplier(id, updatedSupplier.name))
    }

    @Test
    fun deleteById(): Unit = runBlocking {
        // GIVEN
        val supplier = Supplier("FoodSource LLC")
        val id = repository.insert(supplier)

        // WHEN
        val result = repository.deleteById(id)

        // THEN
        val rows = database.supplierQueries.getAll().awaitAsList()
        assertThat(rows).hasSize(0)
    }

}