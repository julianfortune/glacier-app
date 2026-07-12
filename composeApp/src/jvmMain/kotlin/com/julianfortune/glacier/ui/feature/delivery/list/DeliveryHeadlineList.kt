package com.julianfortune.glacier.ui.feature.delivery.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.CollectionView
import com.julianfortune.glacier.ui.common.Dialog
import com.julianfortune.glacier.ui.common.formatLocalDate
import com.julianfortune.glacier.ui.feature.delivery.form.DeliveryForm
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHeadlineList(
    viewModel: DeliveryHeadlineListViewModel = koinViewModel(),
) {
    val selectedId by viewModel.selectedId.collectAsState(null)

    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())
    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())

    var creationDialogIsOpen by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            println("Received uiEvent: $event")
            when (event) {
                is DeliveryHeadlineListViewModel.UiEvent.DeliveryCreated -> {
                    creationDialogIsOpen = false // Close the dialog safely
                }

                null -> {
                    // Ignore
                }
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
                        Text(c.supplier.name)
                    },
                    modifier = modifier.clickable(
                        enabled = true,
                        onClick = {
                            // TODO
                            viewModel.onSelect(c.id)
                        }
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
