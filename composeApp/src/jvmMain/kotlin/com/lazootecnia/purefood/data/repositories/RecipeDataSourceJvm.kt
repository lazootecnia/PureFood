package com.lazootecnia.purefood.data.repositories

actual suspend fun getRecipesJsonContent(): String {
    return try {
        // Prioridad 1: Datos descargados localmente
        val localJsonFile = java.io.File(System.getProperty("user.home"), ".purefood/recipes.json")
        if (localJsonFile.exists()) {
            return localJsonFile.readText()
        }

        // Prioridad 2: Resources
        val resource = object {}.javaClass.classLoader.getResourceAsStream("recipes_data/recipes.json")
        resource?.bufferedReader().use {
            it?.readText() ?: "[]"
        }
    } catch (e: Exception) {
        "[]"
    }
}