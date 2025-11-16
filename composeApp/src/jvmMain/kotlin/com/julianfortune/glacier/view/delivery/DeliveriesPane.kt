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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesPane(viewModel: DeliveryViewModel) {
    val dialogIsOpen by viewModel.newDeliveryDialogIsVisible

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
                HorizontalDivider(thickness = 1.dp)
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
        VerticalDivider(thickness = 1.dp)
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Delivery goes here")
            }
        }
    }
}