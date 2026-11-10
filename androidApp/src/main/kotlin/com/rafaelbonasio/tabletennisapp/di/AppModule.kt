package com.rafaelbonasio.tabletennisapp.di

import androidx.datastore.core.DataStore
import com.rafaelbonasio.tabletennisapp.HistoryViewModel
import com.rafaelbonasio.tabletennisapp.MatchViewModel
import com.rafaelbonasio.tabletennisapp.Preferences
import com.rafaelbonasio.tabletennisapp.SettingsViewModel
import com.rafaelbonasio.tabletennisapp.data.MatchRepository
import com.rafaelbonasio.tabletennisapp.data.database.MatchDao
import com.rafaelbonasio.tabletennisapp.data.database.PlayerDao
import com.rafaelbonasio.tabletennisapp.data.database.TableTennisAppDatabase
import com.rafaelbonasio.tabletennisapp.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import kotlin.uuid.Uuid

val appModule = module {
    single<DataStore<Preferences>> {
        androidContext().preferencesDataStore
    }

    single<TableTennisAppDatabase> {
        DatabaseProvider.getDatabase(androidContext())
    }

    single<MatchDao> {
        get<TableTennisAppDatabase>().matchDao()
    }

    single<PlayerDao> {
        get<TableTennisAppDatabase>().playerDao()
    }

    single { MatchRepository(get(), get()) }

    viewModel<SettingsViewModel>()
    viewModel<HistoryViewModel>()
    viewModel { (matchId: Uuid) ->
        MatchViewModel(
            get(),
            matchId
        )
    }
}
