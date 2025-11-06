package com.julianfortune.glacier.repository

import com.julianfortune.glacier.data.CostStatus
import com.julianfortune.glacier.data.DeliveryEntry as NewDeliveryEntry
import com.julianfortune.glacier.data.DeliveryEntryAggregation
import com.julianfortune.glacier.data.WeightUnit
import com.julianfortune.glacier.data.persisted.Category
import com.julianfortune.glacier.data.persisted.CategoryLink
import com.julianfortune.glacier.data.persisted.Item
import com.julianfortune.glacier.data.persisted.ProgramAllocation
import com.julianfortune.glacier.data.persisted.PurchasingAccountAllocation
import com.julianfortune.glacier.data.persisted.Entry as PersistedEntry
import com.julianfortune.glacier.db.Database

class DeliveryEntryRepository(private val database: Database) {

    // TODO: All this mapping crap needs to live in one place
    private fun getItemById(id: Long): Item {
        val item = database.itemQueries.getById(id).executeAsOne()
        val itemWeight = when (item.weightUnits) {
            "OZ" -> WeightUnit.OZ
            "LB" -> WeightUnit.LB
            else -> throw Exception("Unrecognized weightUnits: ${item.weightUnits}")
        }
        val itemCategories =
            database.itemCategoryQueries.getAllByItemId(item.id).executeAsList().map { itemCategory ->
                val category = database.categoryQueries.getById(itemCategory.categoryId).executeAsOne()
                CategoryLink(itemCategory.id, Category(category.id, category.name))
            }

        return Item(item.id, item.name, item.weightHundredths, itemWeight, itemCategories)
    }

    // TODO: Should this be done using a Flow ...?
    fun getAllByDeliveryId(deliveryId: Long): List<PersistedEntry> {
        val entries = database.deliveryEntryQueries.getByDeliveryId(deliveryId).executeAsList()

        return entries.map { entry ->
            // TODO: All this mapping crap needs to live in one place
            val costStatus = when (entry.costStatus) {
                "PURCHASED" -> CostStatus.PURCHASED
                "NO_COST" -> CostStatus.NO_COST
                else -> throw Exception("Unrecognized costStatus: ${entry.costStatus}")
            }
            val aggregate = if (entry.aggregateLabel != null && entry.aggregateCount != null) {
                DeliveryEntryAggregation(
                    entry.aggregateLabel,
                    entry.aggregateCount
                )
            } else null

            val purchasingAccounts = emptyList<PurchasingAccountAllocation>() // TODO
            val programs = emptyList<ProgramAllocation>() // TODO

            PersistedEntry(
                entry.id,
                entry.itemCount,
                costStatus,
                entry.itemCostCents,
                aggregate,
                getItemById(entry.itemId),
                purchasingAccounts,
                programs,
            )
        }
    }

    suspend fun insert(
        deliveryId: Long,
        itemId: Long,
        itemCount: Long,
        costStatus: CostStatus,
        itemCostCents: Long,
        aggregate: DeliveryEntryAggregation?,
    ): Long {
        return database.deliveryEntryQueries.insert(
            deliveryId,
            itemId,
            itemCount,
            costStatus.toString(),
            itemCostCents,
            aggregate?.label,
            aggregate?.aggregateCount,
        )
    }
}