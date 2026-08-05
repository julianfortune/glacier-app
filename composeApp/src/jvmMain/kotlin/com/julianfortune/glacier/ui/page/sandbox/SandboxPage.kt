package com.julianfortune.glacier.ui.page.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.AutocompleteSelectV2
import com.julianfortune.glacier.ui.common.input.Dynamic

@Composable
fun SandboxPage() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        AutocompleteSelectV2<Long>(
            Dynamic.Loading,
            options = Dynamic.Loading,
            onOptionSelected = {

            },
            label = { Text("Loading Autocomplete") }
        )

        AutocompleteSelectV2(
            Dynamic.Present(Option(1L, "Option 1")),
            options = Dynamic.Loading,
            onOptionSelected = {

            },
            label = { Text("Pre-filled Loading Autocomplete") }
        )

        AutocompleteSelectV2(
            Dynamic.Present(Option(1L, "Option 1")),
            options = Dynamic.Present(listOf(Option(1L, "Option 1"), Option(2L, "Option 2"))),
            onOptionSelected = {

            },
            label = { Text("Pre-filled Autocomplete") }
        )
    }
}
