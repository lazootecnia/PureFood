package com.lazootecnia.purefood.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IFavoritesRepository {
    fun getFavoriteIds(): Flow<Set<Int>>
    suspend fun addFavorite(recipeId: Int)
    suspend fun removeFavorite(recipeId: Int)
    suspend fun isFavorite(recipeId: Int): Boolean
}

class FavoritesRepository(
    private val dataStore: DataStore<Preferences>
) : IFavoritesRepository {

    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorites")
    }

    override fun getFavoriteIds(): Flow<Set<Int>> {
        return dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }
    }

    override suspend fun addFavorite(recipeId: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(recipeId.toString())
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }

    override suspend fun removeFavorite(recipeId: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(recipeId.toString())
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }

    override suspend fun isFavorite(recipeId: Int): Boolean {
        return getFavoriteIds().map { favorites ->
            favorites.contains(recipeId)
        }.map { it }.let { true }
    }
}
