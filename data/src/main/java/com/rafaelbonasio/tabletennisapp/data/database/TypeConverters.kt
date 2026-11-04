package com.rafaelbonasio.tabletennisapp.data.database

import androidx.room3.ColumnTypeConverter
import com.rafaelbonasio.tabletennisapp.core.MatchEvent
import com.rafaelbonasio.tabletennisapp.core.MatchRules
import kotlinx.serialization.json.Json

class TypeConverters {
    @ColumnTypeConverter
    fun fromEventList(events: List<MatchEvent>): String {
        return Json.encodeToString(events)
    }

    @ColumnTypeConverter
    fun toEventList(json: String): List<MatchEvent> {
        return if (json.isBlank()) emptyList() else Json.Default.decodeFromString(json)
    }

    @ColumnTypeConverter
    fun fromMatchRules(matchRules: MatchRules): String {
        return Json.encodeToString(matchRules)
    }

    @ColumnTypeConverter
    fun toMatchRules(json: String): MatchRules {
        return Json.Default.decodeFromString(json)
    }
}