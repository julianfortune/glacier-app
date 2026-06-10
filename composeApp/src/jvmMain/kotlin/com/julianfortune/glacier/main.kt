package com.julianfortune.glacier

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.julianfortune.glacier.db.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {
    // Makes app bar match system theme on macOS
    System.setProperty("apple.awt.application.appearance", "system")
    val driver = runBlocking {
        DatabaseDriverFactory().createDriver()
    }

    startKoin {
        modules(
            appModule,
            module { single { driver } }
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Glacier (Dev build)",
        ) {
            App()
        }
    }
}
