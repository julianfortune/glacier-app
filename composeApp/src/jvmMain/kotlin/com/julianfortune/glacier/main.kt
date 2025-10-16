package com.julianfortune.glacier

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() {
    // Makes app bar match system theme on macOS
    System.setProperty("apple.awt.application.appearance", "system")

    application {
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
}