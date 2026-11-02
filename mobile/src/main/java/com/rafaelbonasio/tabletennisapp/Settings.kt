package com.rafaelbonasio.tabletennisapp

import android.app.Application
import android.content.Context
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.FancyTopAppBar
import com.rafaelbonasio.tabletennisapp.ui.LocalMinimumPadding
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme
import com.rafaelbonasio.tabletennisapp.ui.theme.Theme
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.roundToInt

@Serializable
data object SettingsRoute : NavKey

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _dataStore = application.applicationContext.preferencesDataStore

    val uiState = _dataStore.data.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = Preferences())

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            _dataStore.updateData { uiState -> uiState.copy(theme = theme) }
        }
    }

    fun updateDynamicColor(isDynamicColorEnabled: Boolean) {
        viewModelScope.launch {
            _dataStore.updateData { uiState -> uiState.copy(isDynamicColorEnabled = isDynamicColorEnabled) }
        }
    }

    fun updateBlurRadius(blurRadiusDp: Float) {
        viewModelScope.launch {
            _dataStore.updateData { uiState -> uiState.copy(blurRadiusDp = blurRadiusDp) }
        }
    }
}

@Serializable
data class Preferences(
    val theme: Theme = Theme.SYSTEM,
    val isDynamicColorEnabled: Boolean = true,
    val blurRadiusDp: Float = 16f
)

data object SettingsSharedTransitionKey

const val PREFERENCES_DATA_STORE_FILE_NAME = "Preferences"

object PreferencesSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = Preferences()

    override suspend fun readFrom(input: InputStream): Preferences {
        val text = input.readBytes().decodeToString()
        return if (text.isEmpty()) {
            defaultValue
        } else {
            Json.decodeFromString(Preferences.serializer(), text)
        }
    }

    override suspend fun writeTo(
        t: Preferences,
        output: OutputStream
    ) {
        output.write(Json.encodeToString(Preferences.serializer(), t).toByteArray())
    }
}

val Context.preferencesDataStore: DataStore<Preferences> by dataStore(
    fileName = PREFERENCES_DATA_STORE_FILE_NAME,
    serializer = PreferencesSerializer
)

@Composable
fun SettingsSection(
    label: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(PaddingValues(start = LocalMinimumPadding.current))
            .padding(PaddingValues(top = LocalMinimumPadding.current)) // TODO
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(LocalMinimumPadding.current))
        Text(
            label,
            style = MaterialTheme.typography.titleMedium
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rememberDisplayRoundedCornerShape())
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
fun SettingsSectionItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).clip(rememberDisplayRoundedCornerShape())) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGridApi::class)
@Composable
fun SettingsPage(viewModel: SettingsViewModel, sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope, onNavigateBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val hazeState = rememberHazeState()

    with (sharedTransitionScope) {
        FancyScaffold(
            hazeState = hazeState,
            modifier = Modifier.sharedBounds(
                rememberSharedContentState(SettingsSharedTransitionKey),
                animatedVisibilityScope,
                boundsTransform = asymmetricBoundsTransform,
                clipInOverlayDuringTransition = OverlayClip(rememberDisplayRoundedCornerShape())
            ),
            topBar = {
                FancyTopAppBar(
                    title = {
                        Text(stringResource(R.string.settings_title))
                    },
                    hazeState = hazeState,
                    navigationIcon = {
                        BackButton(onNavigateBack)
                    }
                )
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()

            Grid(
                config = {
                    column(paddingValues.calculateLeftPadding(LayoutDirection.Ltr))
                    column(1.fr)
                    column(paddingValues.calculateRightPadding(LayoutDirection.Ltr))

                    row(paddingValues.calculateTopPadding())
                    row(1.fr)
                    row(paddingValues.calculateBottomPadding())
                },
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier.gridItem(row = 2, column = 2)
                ) {
                    SettingsSection(stringResource(R.string.settings_section_appearance), Icons.Default.Face) {
                        SettingsSectionItem {
                            Column {
                                Row {
                                    for (theme in listOf(Theme.DARK, Theme.LIGHT)) {
                                        ThemePreviewCard(
                                            theme,
                                            state.isDynamicColorEnabled,
                                            state.blurRadiusDp.dp,
                                            state.theme == theme,
                                            { viewModel.updateTheme(theme) })
                                    }
                                }
                                Row {
                                    Text(Theme.SYSTEM.name)
                                    RadioButton(
                                        state.theme == Theme.SYSTEM,
                                        { viewModel.updateTheme(Theme.SYSTEM) })
                                }
                            }
                        }

                        SettingsSectionItem {
                            Row {
                                Text(stringResource(R.string.settings_dynamic_color))
                                Switch(
                                    state.isDynamicColorEnabled,
                                    { viewModel.updateDynamicColor(!state.isDynamicColorEnabled) })
                            }
                        }
                    }

                    SettingsSection(stringResource(R.string.settings_section_performance), Icons.Default.Build) {
                        SettingsSectionItem {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.settings_blur_radius))
                                Slider(
                                    value = state.blurRadiusDp,
                                    onValueChange = { value -> viewModel.updateBlurRadius(value.roundToInt().toFloat()) },
                                    modifier = Modifier.weight(1f),
                                    valueRange = 0f..64f
                                )
                                Text("${state.blurRadiusDp} dp")
                            }
                        }
                    }

                    repeat(2) {
                        OutlinedCard(modifier = Modifier.padding(8.dp).width(120.dp)) {
                            Column {
                                Box(
                                    modifier = Modifier
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
    }
}

@Composable
fun ThemePreviewCard(theme: Theme, dynamicColor: Boolean, blurRadius: Dp, isSelected: Boolean, onClick: () -> Unit) {
    /*
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val scale = width / configuration.screenWidthDp.toFloat()

    val height = screenHeight * scale
    */

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.width(120.dp),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
        shape = rememberDisplayRoundedCornerShape()
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
                        .clip(rememberDisplayRoundedCornerShape())
            ) {
                TableTennisAppTheme(theme, dynamicColor, blurRadius) {
                    SharedTransitionLayout {
                        RootScreen(this@SharedTransitionLayout,
                            LocalNavAnimatedContentScope.current, {}, { _, _, _ -> })
                    }
                }
            }

            Row {
                RadioButton(isSelected, onClick)
                Text(theme.name)
            }
        }
    }
}
