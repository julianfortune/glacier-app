package com.julianfortune.glacier.feature.delivery.headline

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.julianfortune.glacier.ui.common.formatLocalDate
import com.julianfortune.glacier.ui.common.CollectionView
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHeadlineList(
    selectedId: Long?,
    onSelect: (id: Long) -> Unit,
    viewModel: DeliveryHeadlineListViewModel = koinViewModel(),
) {
    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())

    val supplierMap by viewModel.supplierMap.collectAsState()

    CollectionView(
        "Deliveries",
        deliveryHeadlines,
        selectedId,
        content = { c, modifier, elevation ->
            val dateString = formatLocalDate(c.data.receivedDate)
            // TODO(?): `key(c.id)` to prevent a bunch of redraws when inserting a new delivery at the top
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
        },
        onClickCreateNew = {
//           viewModel.showNewDelivery()
        }
    )

    // TODO(!!): Add back in creating new deliveries

//    if (deliveryAction != null) {
//        BasicAlertDialog(
//            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
//        ) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                shape = RoundedCornerShape(16.dp),
//            ) {
//                when (deliveryAction) {
//                    is EntityOperation.CreateNew -> {
//                        NewDeliveryForm(
//                            viewModel,
//                            "New Delivery",
//                            "Create",
//                            onSubmit = { newDelivery ->
//                                coroutineScope.launch {
//                                    val delivery = DeliveryDetail(
//                                        newDelivery.receivedDate,
//                                        newDelivery.supplierId,
//                                        newDelivery.taxesCents,
//                                        newDelivery.feesCents,
//                                        emptyList()
//                                    )
//                                    val newDeliveryId = viewModel.saveDelivery(delivery)
//                                    viewModel.newDeliveryCreated(newDeliveryId)
//                                }
//                            })
//                    }
//
//                    is EntityOperation.Edit -> {
//                        val delivery = (deliveryAction as EntityOperation.Edit).entity
//                        val headline = DeliveryHeadline(
//                            delivery.data.receivedDate,
//                            delivery.data.supplierId,
//                            delivery.data.taxesCents,
//                            delivery.data.feesCents,
//                        )
//                        NewDeliveryForm(
//                            viewModel,
//                            "Edit Delivery",
//                            "Save",
//                            initialDelivery = headline,
//                            onSubmit = { updated ->
//                                coroutineScope.launch {
//                                    val delivery = Entity(
//                                        delivery.id, DeliveryDetail(
//                                            updated.receivedDate,
//                                            updated.supplierId,
//                                            updated.taxesCents,
//                                            updated.feesCents,
//                                            delivery.data.entries
//                                        )
//                                    )
//                                    viewModel.updateDelivery(delivery)
//                                    viewModel.newDeliveryCreated(delivery.id)
//                                }
//                            }
//                        )
//                    }
//
//                    is EntityOperation.Delete -> {
//                        val deliveryId = (deliveryAction as EntityOperation.Delete).id
//                        ConfirmDeleteEntityForm(
//                            deliveryId,
//                            "Delete Delivery",
//                            onCancel = {
//                                viewModel.cancelDeliveryAction()
//                            },
//                            onConfirm = {
//                                coroutineScope.launch {
//                                    viewModel.deleteDelivery(deliveryId)
//                                    viewModel.deliveryDeleted(deliveryId)
//                                }
//                            }
//                        )
//                    }
//
//                    else -> throw Error("`deliveryAction` must not be `null`")
//                }
//            }
//        }
//    }
//
}
