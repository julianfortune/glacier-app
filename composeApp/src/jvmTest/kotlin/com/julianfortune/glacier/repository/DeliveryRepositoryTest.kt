package com.julianfortune.glacier.repository

import com.julianfortune.glacier.codec.CostStatusCodec
import com.julianfortune.glacier.codec.LocalDateCodec
import com.julianfortune.glacier.createTestDatabase
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
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
        database.deliveryEntryProgramQueries.deleteAll()
        database.deliveryEntryPurchasingAccountQueries.deleteAll()
        database.deliveryEntryQueries.deleteAll()
        database.deliveryQueries.deleteAll()

        repository = DeliveryRepository(database)
    }

    // TODO: Parameterize this
    @Test
    fun insert() {
        // GIVEN
        val itemId = runBlocking {
            database.itemQueries.insert("Food", "Box", 32L)
        }
        val supplierId = runBlocking {
            database.supplierQueries.insert("Foo Bar")
        }

        val entry = Entry(
            itemId,
            10,
            "crate",
            1500L,
            null,
            CostStatus.NO_COST,
            8000,
            emptyList(),
            emptyList(),
        )
        val delivery = DeliveryDetail(
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