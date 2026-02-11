package com.lazootecnia.purefood.data.repositories

import android.content.Context

private lateinit var appContext: Context

fun initRecipeDataSource(context: Context) {
    appContext = context
}

actual suspend fun getRecipesJsonContent(): String {
    return try {
        // Intentar desde assets primero (como en build)
        val assetManager = appContext.assets
        assetManager.open("recipes_data/recipes.json").bufferedReader().use {
            it.readText()
        }
    } catch (e: Exception) {
        try {
            // Fallback: intentar desde resources
            val resource = object {}.javaClass.classLoader.getResourceAsStream("recipes_data/recipes.json")
            resource?.bufferedReader().use {
                it?.readText() ?: "[]"
            } ?: "[]"
        } catch (e2: Exception) {
            "[]"
        }
    }
}