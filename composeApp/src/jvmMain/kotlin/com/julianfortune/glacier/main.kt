package com.julianfortune.glacier

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.JPopupTextMenu
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.julianfortune.glacier.core.config.Configuration
import com.julianfortune.glacier.core.config.Constants
import com.julianfortune.glacier.core.config.Environment
import com.julianfortune.glacier.core.config.FileLocation
import com.julianfortune.glacier.db.DatabaseDriverFactory
import com.julianfortune.glacier.core.system.AppDataManager
import com.julianfortune.glacier.core.system.Platform
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.nio.file.Paths

@OptIn(ExperimentalFoundationApi::class)
fun main() {
    // Makes app bar match system theme on macOS
    System.setProperty("apple.awt.application.appearance", "system")

    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val environment = Environment.fromSystem(Environment.RELEASE)
    val configuration = Configuration.load(mapper, environment)

    val adm = AppDataManager(Platform.current)

    val databaseDirectory = when (configuration.db.location) {
        FileLocation.APP_DATA -> {
            adm.initialize()
            adm.appDataPath
        }
        FileLocation.WORKING_DIRECTORY -> Paths.get("")
    }

    // Debug output
    println("osName=${Platform.osName}")
    println("environment=$environment")
    println("configuration=$configuration")

    val driver = runBlocking {
        DatabaseDriverFactory(databaseDirectory).createDriver()
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
            title = configuration.windowTitle,
        ) {
            App()
        }
    }
}
