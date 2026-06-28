package com.julianfortune.glacier.view.deliveries

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.delivery.DeliveryView
import com.julianfortune.glacier.view.shared.ListDetailScaffold
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeliveryListDetailView(
    detailViewModel: DeliveryViewModel = koinViewModel(),
) {
    ListDetailScaffold(
        detailViewModel,
        listWidth = 260.dp,
        listView = { selectedId, onSelect ->
            DeliveryListView(selectedId, onSelect)
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        DeliveryView(detailViewModel)
    }
}