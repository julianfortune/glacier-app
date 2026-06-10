package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.view.shared.CollectionView
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.data.DeliveryAction
import com.julianfortune.glacier.viewModel.data.DeliveryEntryAction
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesListDetailView(viewModel: DeliveryViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())
    val selectedDeliveryId by viewModel.selectedDeliveryId.collectAsState()

    val itemMap by viewModel.itemMap.collectAsState()
    val supplierMap by viewModel.supplierMap.collectAsState()

    val deliveryDetail by viewModel.selectedDeliveryDetail.collectAsState()
    val deliveryAction by viewModel.deliveryAction
    val deliveryEntryAction by viewModel.deliveryEntryAction

    if (deliveryAction != null) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (deliveryAction) {
                    is DeliveryAction.CreateNew -> {
                        NewDeliveryForm(
                            viewModel,
                            "New Delivery",
                            "Create",
                            onSubmit = { newDelivery ->
                                coroutineScope.launch {
                                    val delivery = DeliveryDetail(
                                        newDelivery.receivedDate,
                                        newDelivery.supplierId,
                                        newDelivery.taxesCents,
                                        newDelivery.feesCents,
                                        emptyList()
                                    )
                                    val newDeliveryId = viewModel.saveDelivery(delivery)
                                    viewModel.newDeliveryCreated(newDeliveryId)
                                }
                            })
                    }

                    is DeliveryAction.Edit -> {
                        val delivery = (deliveryAction as DeliveryAction.Edit).delivery
                        val headline = DeliveryHeadline(
                            delivery.data.receivedDate,
                            delivery.data.supplierId,
                            delivery.data.taxesCents,
                            delivery.data.feesCents,
                        )
                        NewDeliveryForm(
                            viewModel,
                            "Edit Delivery",
                            "Save",
                            initialDelivery = headline,
                            onSubmit = { updated ->
                                coroutineScope.launch {
                                    val delivery = Entity(
                                        delivery.id, DeliveryDetail(
                                            updated.receivedDate,
                                            updated.supplierId,
                                            updated.taxesCents,
                                            updated.feesCents,
                                            delivery.data.entries
                                        )
                                    )
                                    viewModel.updateDelivery(delivery)
                                    viewModel.newDeliveryCreated(delivery.id)
                                }
                            }
                        )
                    }

                    is DeliveryAction.Delete -> {
                        val deliveryId = (deliveryAction as DeliveryAction.Delete).deliveryId
                        DeleteDeliveryForm(
                            deliveryId,
                            viewModel,
                            onConfirm = {
                                coroutineScope.launch {
                                    viewModel.deleteDelivery(deliveryId)
                                    viewModel.deliveryDeleted(deliveryId)
                                }
                            }
                        )
                    }

                    else -> throw Error("`deliveryAction` must not be `null`")
                }
            }
        }
    }

    if (deliveryEntryAction != null) {
        if (selectedDeliveryId == null) {
            throw IllegalStateException("Unable to create an entry without a delivery selected")
        }

        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (deliveryEntryAction) {
                    is DeliveryEntryAction.CreateNew -> {
                        NewEntryForm(
                            viewModel,
                            "New Entry",
                            "Create",
                            onSubmit = { entry ->
                                coroutineScope.launch {
                                    viewModel.saveEntry(selectedDeliveryId!!, entry)
                                    viewModel.dismissEntryModal()
                                }
                            }
                        )
                    }

                    is DeliveryEntryAction.Edit -> {
                        NewEntryForm(
                            viewModel,
                            "Edit Entry",
                            "Update",
                            initialEntry = (deliveryEntryAction as DeliveryEntryAction.Edit).entry,
                            onSubmit = { newEntry ->
                                val currentDeliveryDetail: Entity<DeliveryDetail> =
                                    deliveryDetail
                                        ?: throw AssertionError("Unable to delete an entry because `deliveryDetail` is unexpectedly null")

                                val updatedDelivery = currentDeliveryDetail.copy(
                                    data = currentDeliveryDetail.data.copy(
                                        entries = currentDeliveryDetail.data.entries?.mapIndexed { i, e ->
                                            when {
                                                i == (deliveryEntryAction as DeliveryEntryAction.Edit).index -> newEntry
                                                else -> e
                                            }
                                        }
                                    )
                                )

                                coroutineScope.launch {
                                    viewModel.updateDelivery(updatedDelivery)
                                    viewModel.dismissEntryModal()
                                }
                            }
                        )
                    }

                    else -> throw Error("`deliveryEntryAction` must not be `null`")
                }
            }
        }
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            // TODO(P4): Add support for dropdown ellipses menu with support for delete and edit (configurable)
            Column(modifier = Modifier.width(260.dp)) {
                CollectionView(
                    "Deliveries",
                    deliveryHeadlines,
                    selectedDeliveryId,
                    content = { c, modifier, elevation ->
                        val dateString = formatLocalDate(c.data.receivedDate)
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
                                onClick = {
                                    viewModel.selectDelivery(c.id)
                                }
                            ),
                            tonalElevation = elevation,
                        )
                    },
                    onClickCreateNew = {
                        viewModel.showNewDelivery()
                    }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            if (deliveryDetail == null) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No delivery selected.")
                }
            } else {
                DeliveryView(
                    deliveryDetail!!, // TODO(P3): Better understand delegation and nullability
                    itemMap,
                    supplierMap,
                    viewModel,
                )
            }

        }
    }
}