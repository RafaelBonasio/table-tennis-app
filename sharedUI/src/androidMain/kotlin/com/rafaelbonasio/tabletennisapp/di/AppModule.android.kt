package com.rafaelbonasio.tabletennisapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.room3.Room
import com.rafaelbonasio.tabletennisapp.PREFERENCES_DATA_STORE_FILE_NAME
import com.rafaelbonasio.tabletennisapp.Preferences
import com.rafaelbonasio.tabletennisapp.PreferencesSerializer
import com.rafaelbonasio.tabletennisapp.createPreferencesDataStore
import com.rafaelbonasio.tabletennisapp.data.database.DATABASE_FILE_NAME
import com.rafaelbonasio.tabletennisapp.data.database.TableTennisAppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> {
        createPreferencesDataStore(
            FileStorage(
                serializer = PreferencesSerializer,
                produceFile = { androidContext().filesDir.resolve(PREFERENCES_DATA_STORE_FILE_NAME) }
            )
        )
    }

    single<TableTennisAppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            TableTennisAppDatabase::class.java,
            DATABASE_FILE_NAME
        ).build()
    }
}
