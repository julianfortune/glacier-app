package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.codec.LocalDateCodec
import com.julianfortune.glacier.codec.WeightUnitCodec
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.db.Database
import dev.forkhandles.result4k.orThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val database: Database) {

    fun getAll(): Flow<List<Entity<Item>>> {
        return database.itemQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { items ->
                items.map { i ->
                    val weightUnits = WeightUnitCodec.deserialize(i.weightUnits).orThrow()

                    // TODO(P1): Fetch categories
                    Entity(i.id, Item(i.name, i.weightHundredths, weightUnits, emptyList()))
                }
            }
    }

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
