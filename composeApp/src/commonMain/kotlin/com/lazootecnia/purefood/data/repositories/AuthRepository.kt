package com.lazootecnia.purefood.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IAuthRepository {
    fun getAuthState(): Flow<Boolean>
    suspend fun setAuthenticated(isAuth: Boolean)
    suspend fun validatePassword(password: String): Boolean
}

class AuthRepository(
    private val dataStore: DataStore<Preferences>
) : IAuthRepository {

    companion object {
        private val AUTH_KEY = booleanPreferencesKey("is_admin_authenticated")
        private const val ADMIN_PASSWORD = "123"
    }

    override fun getAuthState(): Flow<Boolean> =
        dataStore.data.map { it[AUTH_KEY] ?: false }

    override suspend fun setAuthenticated(isAuth: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTH_KEY] = isAuth
        }
    }

    override suspend fun validatePassword(password: String): Boolean {
        return password == ADMIN_PASSWORD
    }
}
