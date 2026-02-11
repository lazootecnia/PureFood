package com.lazootecnia.purefood.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

// Simple in-memory DataStore replacement for JVM
private class SimpleDataStore : DataStore<Preferences> {
    private val prefs = MutableStateFlow(mutablePreferencesOf())

    init {
        val dataStoreDir = File(System.getProperty("user.home"), ".purefood")
        if (!dataStoreDir.exists()) {
            dataStoreDir.mkdirs()
        }
    }

    override val data: Flow<Preferences> = prefs.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val current = prefs.value
        val updated = transform(current)
        prefs.value = updated as MutablePreferences
        return updated
    }
}

private typealias MutablePreferences = androidx.datastore.preferences.core.MutablePreferences

actual fun createDataStore(): DataStore<Preferences> {
    return SimpleDataStore()
}
