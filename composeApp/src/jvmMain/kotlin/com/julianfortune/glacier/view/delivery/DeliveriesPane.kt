package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.flow.map
import kotlin.collections.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesPane(viewModel: DeliveryViewModel) {
    val dialogIsOpen by viewModel.newDeliveryDialogIsVisible

    // TODO(NEXT): Ability to select a delivery to open the detail view

    if (dialogIsOpen) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                NewDeliveryForm(viewModel)
            }
        }
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Column(modifier = Modifier.width(240.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(8.dp),
                ) {
                    FilledTonalButton(
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.height(32.dp).fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = {
                            viewModel.showNewDelivery()
                        },
                    ) {
                        Text("New")
                    }
                }
                ScrollableColumn(
                    viewModel.allDeliveries.map { deliveries ->
                        deliveries.map {
                            val name = it.data.receivedDate.toString()
                            Item(name, onClick = {
                                println("Clicked on delivery: ${it.id} ...")
                            })
                        }
                    }.collectAsState(emptyList())
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
        ) {

            // TODO(NEXT): Display date and supplier (can skip editing for now)
            // TODO(P2): Display editable cost information

            // TODO(NEXT): Display editable entries list
            //  -> focus first on editing and saving functionality before an quality / UX work

            Text(
                "Entries",
                style = MaterialTheme.typography.headlineSmall,
            )

//            Column {
//                entries.mapIndexed { index, entry ->
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        modifier = Modifier,
//                    ) {
//                        Text("${index + 1}")
//                        AutoCompleteDropdownField(
//                            label = { Text("Item") },
//                            options = items.map {
//                                Option(it.id, it.data.name)
//                            },
//                            onSelectedChange = { newId ->
//                                println("Selected: $newId")
//                            }
//                        )
//                        OutlinedTextField(
//                            value = firstItemCount,
//                            onValueChange = { firstItemCount = it },
//                            label = { Text("Count") },
//                            singleLine = true,
//                            modifier = Modifier.height(64.dp), // Fixes a dumb glitch where the height changes
//                            colors = OutlinedTextFieldDefaults.colors()
//                        )
//                        // TODO: CostStatus dropdown
//                        IconButton(
//                            modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
//                            onClick = {
//                                TODO("Delete entry")
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete entry"
//                            )
//                        }
//                    }
//                }
//
//                Row {
//                    Button(
//                        onClick = {
//                            TODO("Add new entry")
//                        }
//                    ) {
//                        Text("New Entry")
//                    }
//                }
//            }

        }
    }
}