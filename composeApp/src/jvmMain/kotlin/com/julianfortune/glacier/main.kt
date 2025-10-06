package com.julianfortune.glacier

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() = application {
    // Initialize Koin DI
    startKoin {
        modules(appModule)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Glacier (Dev build)",
    ) {
        App()
    }
}