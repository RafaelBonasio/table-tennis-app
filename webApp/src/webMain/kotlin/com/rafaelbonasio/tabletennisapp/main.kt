package com.rafaelbonasio.tabletennisapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.rafaelbonasio.tabletennisapp.di.appModule
import com.rafaelbonasio.tabletennisapp.di.webModule
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(
            appModule,
            webModule
        )
    }

    ComposeViewport {
        App()
    }
}