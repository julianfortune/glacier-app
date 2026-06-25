package com.julianfortune.glacier

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.julianfortune.glacier.config.Configuration
import com.julianfortune.glacier.config.Environment
import com.julianfortune.glacier.db.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {
    // Makes app bar match system theme on macOS
    System.setProperty("apple.awt.application.appearance", "system")

    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val environment = Environment.fromSystem(Environment.RELEASE)
    val configuration = Configuration.load(mapper, environment)

    // Debug output
    println("environment=$environment")
    println("configuration=$configuration")

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
