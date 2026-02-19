package com.lazootecnia.purefood.data.repositories

import android.content.Context

private lateinit var appContext: Context

fun initRecipeDataSource(context: Context) {
    appContext = context
}

actual suspend fun getRecipesJsonContent(): String {
    return try {
        // Prioridad 1: Datos descargados localmente
        val localJsonFile = java.io.File(appContext.filesDir, "recipes.json")
        if (localJsonFile.exists()) {
            return localJsonFile.readText()
        }

        // Prioridad 2: Assets
        val assetManager = appContext.assets
        assetManager.open("recipes_data/recipes.json").bufferedReader().use {
            it.readText()
        }
    } catch (e: Exception) {
        try {
            // Prioridad 3: Resources
            val resource = object {}.javaClass.classLoader.getResourceAsStream("recipes_data/recipes.json")
            resource?.bufferedReader().use {
                it?.readText() ?: "[]"
            } ?: "[]"
        } catch (e2: Exception) {
            "[]"
        }
    }
}