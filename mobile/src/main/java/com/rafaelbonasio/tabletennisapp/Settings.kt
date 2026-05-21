package com.rafaelbonasio.tabletennisapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(currentTheme: Theme, onThemeChange: (Theme) -> Unit, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onNavigateBack)
                },
                title = {
                    Text("Configurações")
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp, 0.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text("Aparência", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                for (theme in listOf(Theme.Dark, Theme.Light)) {
                    ThemePreviewCard(theme, currentTheme == theme, { onThemeChange(theme) })
                }

            }
            Row {
                Text(Theme.System.name)
                Switch(currentTheme == Theme.System, { onThemeChange(Theme.System) })
            }

            repeat(2) {
                OutlinedCard(modifier = Modifier.padding(8.dp).width(120.dp)) {
                    Column {
                        Box(modifier = Modifier
                            .size(120.dp, 240.dp)
                            .requiredSize(360.dp, 720.dp)
                            .graphicsLayer {
                                scaleX = 1f / 3f
                                scaleY = 1f / 3f
                            }
                        ) {
                            App()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemePreviewCard(theme: Theme, isSelected: Boolean, onClick: () -> Unit) {
    /*
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val scale = width / configuration.screenWidthDp.toFloat()

    val height = screenHeight * scale
    */

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.padding(8.dp).width(120.dp),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier =
                    Modifier
                        .size(120.dp, 240.dp)
                        .requiredSize(360.dp, 720.dp)
                        .graphicsLayer {
                            scaleX = 1f / 3f
                            scaleY = 1f / 3f
                        }
            ) {
                TableTennisAppTheme(!theme.isLight()) {
                    RootScreen({}, { _, _, _ -> })
                }
            }

            Row {
                RadioButton(isSelected, onClick)
                Text(theme.name)
            }
        }
    }
}

enum class Theme {
    Dark,
    Light,
    System;

    @Composable
    fun isLight(): Boolean {
        return when (this) {
            Dark -> false
            Light -> true
            System -> !isSystemInDarkTheme()
        }
    }
}
