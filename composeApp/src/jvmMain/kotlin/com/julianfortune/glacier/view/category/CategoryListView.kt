package com.julianfortune.glacier.view.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.julianfortune.glacier.view.shared.CollectionView
import com.julianfortune.glacier.viewModel.CategoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListView(viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState(emptyList())

    Column {
        CollectionView(
            "Categories",
            categories,
            null,
            content = { c, modifier, elevation ->
                ListItem(
                    headlineContent = {
                        Text(c.data.name)
                    },
                    modifier = modifier.clickable(
                        enabled = true,
                        onClick = {}
                    ),
                    tonalElevation = elevation,
                )
            }
        )
    }
}
