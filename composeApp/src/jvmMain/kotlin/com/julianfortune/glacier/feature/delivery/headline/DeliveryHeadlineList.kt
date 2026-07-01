package com.julianfortune.glacier.feature.delivery.headline

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.julianfortune.glacier.feature.delivery.headline.ui.NewDelivery
import com.julianfortune.glacier.ui.common.formatLocalDate
import com.julianfortune.glacier.ui.common.CollectionView
import com.julianfortune.glacier.ui.common.Dialog
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHeadlineList(
    selectedId: Long?,
    onSelect: (id: Long) -> Unit,
    clearSelection: () -> Unit,
    viewModel: DeliveryHeadlineListViewModel = koinViewModel(),
) {
    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())
    val supplierMap by viewModel.supplierMap.collectAsState()

    var creationDialogIsOpen by remember { mutableStateOf(false) }

    // Allow the detail view model to notify the scaffold if an entity is deleted
    LaunchedEffect(selectedId, deliveryHeadlines) {
        if (selectedId != null && deliveryHeadlines.none { it.id == selectedId }) {
            clearSelection()
        }
    }

    CollectionView(
        "Deliveries",
        deliveryHeadlines,
        selectedId,
        content = { c, modifier, elevation ->
            val dateString = formatLocalDate(c.data.receivedDate)
            // Prevent re-draws when inserting a new delivery at the top
            key(c.id) {
                ListItem(
                    headlineContent = {
                        Text(dateString)
                    },
                    supportingContent = {
                        val supplier = (c.data.supplierId).let { supplierMap[it] }
                        Text(supplier?.data?.name ?: "Unknown Supplier")
                    },
                    modifier = modifier.clickable(
                        enabled = true,
                        onClick = { onSelect(c.id) }
                    ),
                    tonalElevation = elevation,
                )
            }
        },
        onClickCreateNew = {
            creationDialogIsOpen = true
        }
    )

    if (creationDialogIsOpen) {
        Dialog(
            onDismissRequest = { creationDialogIsOpen = false },
        ) {
            NewDelivery(
                onCancel = {
                    creationDialogIsOpen = false
                },
                onSuccess = { newDeliveryId ->
                    creationDialogIsOpen = false
                    // Automatically update selected ID to the newly created delivery
                    onSelect(newDeliveryId)
                }
            )
        }
    }

}
