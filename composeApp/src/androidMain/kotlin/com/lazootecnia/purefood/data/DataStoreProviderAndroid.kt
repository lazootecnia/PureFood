package com.lazootecnia.purefood.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val DATA_STORE_NAME = "purefood_preferences"
private var dataStore: DataStore<Preferences>? = null
private lateinit var applicationContext: Context

fun initDataStore(context: Context) {
    applicationContext = context
}

// Simple in-memory DataStore implementation
private class SimpleDataStore : DataStore<Preferences> {
    private val prefs = MutableStateFlow(mutablePreferencesOf())

    override val data: Flow<Preferences> = prefs.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val current = prefs.value
        val updated = transform(current)
        prefs.value = updated as androidx.datastore.preferences.core.MutablePreferences
        return updated
    }
}

actual fun createDataStore(): DataStore<Preferences> {
    return dataStore ?: SimpleDataStore().also { dataStore = it }
}
