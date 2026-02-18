package com.lazootecnia.purefood.data.export

import com.lazootecnia.purefood.data.models.Recipe

interface IRecipeExporter {
    suspend fun exportRecipesToJson(recipes: List<Recipe>): Result<String>
}

expect class RecipeExporter() : IRecipeExporter
