package com.julianfortune.glacier.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.glacier.core.codec.CostStatusCodec
import com.julianfortune.glacier.core.codec.LocalDateCodec
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Entry
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DeliveryRepositoryTest {

    lateinit var database: Database
    lateinit var repository: DeliveryRepository

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        database = createTestDatabase()

        // Blow away test data defined in `.sq` files
        database.deliveryEntryQueries.deleteAll()
        database.deliveryQueries.deleteAll()

        repository = DeliveryRepository(database)
    }

    // TODO: Parameterize this
    @Test
    fun insert() {
        // GIVEN
        val itemId = runBlocking {
            database.itemQueries.insert("Food", null).awaitAsOne()
        }
        val supplierId = runBlocking {
            database.supplierQueries.insert("Foo Bar").awaitAsOne()
        }

        val entry = Entry(
            itemId,
            10,
            "crate",
            Weight.ofCentigrams(150000),
            CostStatus.NO_COST,
            8000,
            null,
            null,
            null,
            null,
        )
        val delivery = Delivery(
            LocalDate.of(2025, 10, 26),
            supplierId,
            2000L,
            1500L,
            listOf(entry)
        )

        // TODO: Make sure `id` is a number larger than `1` or randomized in some way to catch bug where
        //   number of rows modified is returned instead of ID

        // WHEN
        val id = runBlocking {
            repository.insert(delivery)
        }

        // THEN
        val storedDelivery = database.deliveryQueries.getById(id).executeAsOne()
        assertThat(storedDelivery.receivedDate).isEqualTo(LocalDateCodec.serialize(delivery.receivedDate))
        assertThat(storedDelivery.supplierId).isEqualTo(delivery.supplierId)
        assertThat(storedDelivery.taxesCents).isEqualTo(delivery.taxesCents)
        assertThat(storedDelivery.feesCents).isEqualTo(delivery.feesCents)

        val deliveryEntry = database.deliveryEntryQueries.getByDeliveryId(id).executeAsOne()
        assertThat(deliveryEntry.itemId).isEqualTo(entry.itemId)
        assertThat(deliveryEntry.unitCount).isEqualTo(entry.unitCount)
        assertThat(deliveryEntry.costStatus).isEqualTo(CostStatusCodec.serialize(entry.costStatus))
        // TODO: Aggregate

        // TODO: Check all various rows were updated correctly ...
    }

}