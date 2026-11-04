package com.rafaelbonasio.tabletennisapp.di

import android.content.Context
import androidx.room3.Room
import com.rafaelbonasio.tabletennisapp.data.database.TableTennisAppDatabase

object DatabaseProvider {
    @Volatile
    private var INSTANCE: TableTennisAppDatabase? = null

    fun getDatabase(context: Context): TableTennisAppDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, TableTennisAppDatabase::class.java, "Database.db")
                .build()
                .also { database -> INSTANCE = database }
        }
    }
}
