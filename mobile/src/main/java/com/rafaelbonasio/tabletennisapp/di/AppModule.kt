package com.rafaelbonasio.tabletennisapp.di

import androidx.datastore.core.DataStore
import com.rafaelbonasio.tabletennisapp.GameViewModel
import com.rafaelbonasio.tabletennisapp.HistoryViewModel
import com.rafaelbonasio.tabletennisapp.Preferences
import com.rafaelbonasio.tabletennisapp.SettingsViewModel
import com.rafaelbonasio.tabletennisapp.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<DataStore<Preferences>> {
        androidContext().preferencesDataStore
    }

    viewModel<SettingsViewModel>()
    viewModel<HistoryViewModel>()
    viewModel<GameViewModel>()
}
