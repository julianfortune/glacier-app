package com.julianfortune.glacier.view

import androidx.compose.runtime.Composable

// Example of composable element with child content
@Composable
fun FooBar(children: @Composable () -> Unit) {
    children()
}
