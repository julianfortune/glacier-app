package com.julianfortune.glacier.feature.delivery

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.headline.DeliveryHeadlineList
import com.julianfortune.glacier.feature.delivery.detail.DeliveryDetail
import com.julianfortune.glacier.ui.layout.ListDetailScaffold
import com.julianfortune.glacier.feature.delivery.detail.DeliveryDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeliveryListDetailView(
    detailViewModel: DeliveryDetailViewModel = koinViewModel(),
) {
    ListDetailScaffold(
        detailViewModel,
        listWidth = 260.dp,
        listView = { selectedId, onSelect ->
            DeliveryHeadlineList(selectedId, onSelect)
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        DeliveryDetail(detailViewModel)
    }
}