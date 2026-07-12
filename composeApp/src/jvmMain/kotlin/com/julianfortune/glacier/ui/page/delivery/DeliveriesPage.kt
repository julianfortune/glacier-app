package com.julianfortune.glacier.ui.page.delivery

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.layout.ListDetailScaffold
import com.julianfortune.glacier.ui.feature.delivery.detail.DeliveryDetail
import com.julianfortune.glacier.ui.feature.delivery.list.DeliveryHeadlineList


@Composable
fun DeliveriesPage() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = {
            DeliveryHeadlineList()
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        DeliveryDetail()
    }
}
