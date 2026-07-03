package com.julianfortune.glacier.feature.delivery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.headline.DeliveryHeadlineList
import com.julianfortune.glacier.feature.delivery.page.DeliveryPage
import com.julianfortune.glacier.ui.layout.ListDetailScaffold
import com.julianfortune.glacier.ui.common.TopBar


@Composable
fun DeliveriesScreen() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = { selectedId, onSelect, clearSelection ->
            DeliveryHeadlineList(selectedId, onSelect, clearSelection)
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
        emptyContent = {
            Column(Modifier.fillMaxSize()) {
                TopBar("")

                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Nothing selected",
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { selectedId ->
        DeliveryPage(selectedId)
    }
}
