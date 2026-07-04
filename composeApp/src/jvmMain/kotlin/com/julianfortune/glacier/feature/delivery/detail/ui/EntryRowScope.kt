package com.julianfortune.glacier.feature.delivery.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


sealed interface EntryRowScope {
    @Composable
    fun SelectionCell(content: @Composable () -> Unit)

    @Composable
    fun ItemNameCell(content: @Composable () -> Unit)

    @Composable
    fun ProgramCell(content: @Composable () -> Unit)

    @Composable
    fun PurchasingAccountCell(content: @Composable () -> Unit)

    @Composable
    fun UnitCountCell(content: @Composable () -> Unit)

    @Composable
    fun EntryWeightCell(content: @Composable () -> Unit)

    @Composable
    fun EntryCostCell(content: @Composable () -> Unit)

    @Composable
    fun ActionCell(content: @Composable () -> Unit)
}


internal class EntryRowScopeImpl(private val rowScope: RowScope) : EntryRowScope {

    private fun getColumnWeight(column: String): Float {
        return when (column) {
            "item" -> 2f
            "program" -> 1.5f
            "account" -> 1.5f
            "cost" -> 1f
            else -> 0.8f
        }
    }

    @Composable
    override fun SelectionCell(content: @Composable () -> Unit) {
        Row(
            modifier = Modifier.width(36.dp).padding(start = 8.dp),
        ) {
            content()
        }
    }

    @Composable
    override fun ItemNameCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier
                    .weight(getColumnWeight("item"))
                    .padding(start = 16.dp),
            ) {
                content()
            }
        }
    }

    @Composable
    override fun ProgramCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier.weight(getColumnWeight("program"))
            ) {
                content()
            }
        }
    }

    @Composable
    override fun PurchasingAccountCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier.weight(getColumnWeight("account"))
            ) {
                content()
            }
        }
    }

    @Composable
    override fun UnitCountCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier.weight(getColumnWeight("count")),
                horizontalArrangement = Arrangement.End,
            ) {
                content()
            }
        }
    }

    @Composable
    override fun EntryWeightCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier.weight(getColumnWeight("weight")),
                horizontalArrangement = Arrangement.End,
            ) {
                content()
            }
        }
    }

    @Composable
    override fun EntryCostCell(content: @Composable (() -> Unit)) {
        with(rowScope) {
            Row(
                modifier = Modifier.weight(getColumnWeight("cost")),
                horizontalArrangement = Arrangement.End,
            ) {
                content()
            }
        }
    }

    @Composable
    override fun ActionCell(content: @Composable () -> Unit) {
        Row(
            modifier = Modifier.width(36.dp).padding(end = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            content()
        }
    }

}