package com.rafaelbonasio.tabletennisapp

import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

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