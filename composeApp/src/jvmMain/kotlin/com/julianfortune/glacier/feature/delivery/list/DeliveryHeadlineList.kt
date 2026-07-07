package com.julianfortune.glacier.feature.delivery.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.common.ui.DeliveryForm
import com.julianfortune.glacier.ui.common.CollectionView
import com.julianfortune.glacier.ui.common.Dialog
import com.julianfortune.glacier.ui.common.formatLocalDate
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHeadlineList(
    selectedId: Long?,
    onSelect: (id: Long) -> Unit,
    clearSelection: () -> Unit,
    viewModel: DeliveryHeadlineListViewModel = koinViewModel(),
) {
    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())
    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())

    var creationDialogIsOpen by remember { mutableStateOf(false) }

    // Allow the detail view model to notify the scaffold if an entity is deleted
    LaunchedEffect(selectedId, deliveryHeadlines) {
        if (selectedId != null && deliveryHeadlines.none { it.id == selectedId }) {
            clearSelection()
        }
    }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DeliveryHeadlineListViewModel.UiEvent.DeliveryCreated -> {
                    onSelect(event.id) // Automatically select the new delivery
                    creationDialogIsOpen = false // Close the dialog safely
                }

                else -> {}
            }
        }
    }

    CollectionView(
        "Deliveries",
        deliveryHeadlines,
        selectedId,
        content = { c, modifier, elevation ->
            val dateString = formatLocalDate(c.received)
            // Prevent re-draws when inserting a new delivery at the top
            key(c.id) {
                ListItem(
                    headlineContent = {
                        Text(dateString)
                    },
                    supportingContent = {
                        Text(c.supplier?.name ?: "None")
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
            DeliveryForm(
                title = "New Delivery",
                supplierOptions = supplierOptions,
                initialDelivery = null,
                onCancel = {
                    creationDialogIsOpen = false
                },
                onSubmit = { newDelivery ->
                    viewModel.saveNewDelivery(newDelivery)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}
