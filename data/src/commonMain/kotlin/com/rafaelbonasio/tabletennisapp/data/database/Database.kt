package com.rafaelbonasio.tabletennisapp.data.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase

const val DATABASE_FILE_NAME = "Database.db"

@Database(entities = [MatchEntity::class, PlayerEntity::class], version = 1)
@ColumnTypeConverters( TypeConverters::class)
abstract class TableTennisAppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao

    abstract fun playerDao(): PlayerDao
}