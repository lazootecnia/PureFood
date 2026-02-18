package com.lazootecnia.purefood.data.repositories

import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.serialization.json.Json

interface IRecipeRepository {
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun getRecipeById(id: Int): Recipe?
    suspend fun getRecipesByCategory(category: String): List<Recipe>
}

interface IRecipeWriteRepository {
    suspend fun addRecipe(recipe: Recipe): Result<Recipe>
    suspend fun updateRecipe(recipe: Recipe): Result<Recipe>
    suspend fun deleteRecipe(recipeId: Int): Result<Unit>
    suspend fun getAllRecipesForExport(): List<Recipe>
}

class RecipeRepository(
    private val dataSource: IRecipeDataSource
) : IRecipeRepository, IRecipeWriteRepository {

    private var cachedRecipes: MutableList<Recipe> = mutableListOf()
    private var nextId: Int = 1
    private var isInitialized = false

    override suspend fun getAllRecipes(): List<Recipe> {
        if (!isInitialized) {
            cachedRecipes = dataSource.loadRecipes().toMutableList()
            nextId = (cachedRecipes.maxOfOrNull { it.id } ?: 0) + 1
            isInitialized = true
        }
        return cachedRecipes.toList()
    }

    override suspend fun getRecipeById(id: Int): Recipe? {
        return getAllRecipes().find { it.id == id }
    }

    override suspend fun getRecipesByCategory(category: String): List<Recipe> {
        return getAllRecipes().filter { recipe ->
            recipe.categories.any { it.equals(category, ignoreCase = true) }
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val newRecipe = recipe.copy(id = nextId++)
            cachedRecipes.add(newRecipe)
            Result.success(newRecipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val index = cachedRecipes.indexOfFirst { it.id == recipe.id }
            if (index != -1) {
                cachedRecipes[index] = recipe
                Result.success(recipe)
            } else {
                Result.failure(Exception("Receta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRecipe(recipeId: Int): Result<Unit> {
        return try {
            val removed = cachedRecipes.removeIf { it.id == recipeId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Receta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllRecipesForExport(): List<Recipe> {
        getAllRecipes() // Ensure initialized
        return cachedRecipes.toList()
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