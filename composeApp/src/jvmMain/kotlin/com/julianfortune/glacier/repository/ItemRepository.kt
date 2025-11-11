package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.glacier.codec.WeightUnitCodec
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.item.WeightUnit
import com.julianfortune.glacier.db.Database
import dev.forkhandles.result4k.orThrow

class ItemRepository(private val database: Database) {

    suspend fun getById(id: Long): Entity<Item> {
        val item = database.itemQueries.getById(id).awaitAsOne()
        val units = WeightUnitCodec.deserialize(item.weightUnits).orThrow()
        val itemCategoryIds = database
            .itemCategoryQueries
            .getAllByItemId(item.id)
            .awaitAsList()
            .map { it.categoryId }

        return Entity(item.id, Item(item.name, item.weightHundredths, units, itemCategoryIds))
    }

}
