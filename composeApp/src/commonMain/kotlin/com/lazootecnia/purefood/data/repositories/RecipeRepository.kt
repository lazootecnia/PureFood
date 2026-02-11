package com.lazootecnia.purefood.data.repositories

import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.serialization.json.Json

interface IRecipeRepository {
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun getRecipeById(id: Int): Recipe?
    suspend fun getRecipesByCategory(category: String): List<Recipe>
}

class RecipeRepository(
    private val dataSource: IRecipeDataSource
) : IRecipeRepository {

    override suspend fun getAllRecipes(): List<Recipe> {
        return dataSource.loadRecipes()
    }

    override suspend fun getRecipeById(id: Int): Recipe? {
        return getAllRecipes().find { it.id == id }
    }

    override suspend fun getRecipesByCategory(category: String): List<Recipe> {
        return getAllRecipes().filter { recipe ->
            recipe.categories.any { it.equals(category, ignoreCase = true) }
        }
    }
}

interface IRecipeDataSource {
    suspend fun loadRecipes(): List<Recipe>
}

class JsonRecipeDataSource(
    private val json: Json = Json { ignoreUnknownKeys = true }
) : IRecipeDataSource {

    override suspend fun loadRecipes(): List<Recipe> {
        return try {
            val jsonString = loadJsonString()
            json.decodeFromString<List<Recipe>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun loadJsonString(): String {
        return getRecipesJsonContent()
    }
}

expect suspend fun getRecipesJsonContent(): String