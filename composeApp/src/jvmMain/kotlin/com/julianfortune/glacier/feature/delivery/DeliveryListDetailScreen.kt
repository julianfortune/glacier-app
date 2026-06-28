package com.julianfortune.glacier.feature.delivery

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.headline.DeliveryHeadlineList
import com.julianfortune.glacier.feature.delivery.page.DeliveryPage
import com.julianfortune.glacier.ui.layout.ListDetailScaffold
import com.julianfortune.glacier.feature.delivery.page.DeliveryPageViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeliveryListDetailView(
    detailViewModel: DeliveryPageViewModel = koinViewModel(),
) {
    ListDetailScaffold(
        detailViewModel,
        listWidth = 320.dp,
        listView = { selectedId, onSelect ->
            DeliveryHeadlineList(selectedId, onSelect)
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        DeliveryPage(detailViewModel)
    }
}