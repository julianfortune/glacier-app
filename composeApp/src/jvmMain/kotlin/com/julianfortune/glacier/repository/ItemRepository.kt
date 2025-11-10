package com.julianfortune.glacier.repository

import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.item.WeightUnit
import com.julianfortune.glacier.db.Database

class ItemRepository(private val database: Database) {

    private fun weightUnitFromString(input: String): WeightUnit {
        return when (input) {
            "OZ" -> WeightUnit.OZ
            "LB" -> WeightUnit.LB
            else -> throw Exception("Unrecognized weightUnits: ${input}")
        }
    }

    fun getById(id: Long): Entity<Item> {
        val item = database.itemQueries.getById(id).executeAsOne()
        val units = weightUnitFromString(item.weightUnits)
        val itemCategoryIds = database
            .itemCategoryQueries
            .getAllByItemId(item.id)
            .executeAsList()
            .map { it.categoryId }

        return Entity(item.id, Item(item.name, item.weightHundredths, units, itemCategoryIds))
    }
}