package com.rafaelbonasio.tabletennisapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.WebLocalStorage
import androidx.datastore.core.okio.WebSerializer
import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.rafaelbonasio.tabletennisapp.PREFERENCES_DATA_STORE_FILE_NAME
import com.rafaelbonasio.tabletennisapp.Preferences
import com.rafaelbonasio.tabletennisapp.createPreferencesDataStore
import com.rafaelbonasio.tabletennisapp.data.database.DATABASE_FILE_NAME
import com.rafaelbonasio.tabletennisapp.data.database.TableTennisAppDatabase
import org.koin.dsl.module
import org.w3c.dom.Worker

actual val platformModule = module {
    single<DataStore<Preferences>> {
        createPreferencesDataStore(
            storage = WebLocalStorage(
                WebSerializer(Preferences.serializer(), Preferences()),
                PREFERENCES_DATA_STORE_FILE_NAME
            )
        )
    }

    single<TableTennisAppDatabase> {
        Room.databaseBuilder<TableTennisAppDatabase>(DATABASE_FILE_NAME)
            .setDriver(WebWorkerSQLiteDriver(get<SqliteWorkerFactory>().create()))
            .build()
    }
}

fun interface SqliteWorkerFactory {
    fun create(): Worker
}