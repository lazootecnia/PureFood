package com.lazootecnia.purefood.data.repositories

actual suspend fun getRecipesJsonContent(): String {
    return try {
        val resource = object {}.javaClass.classLoader.getResourceAsStream("recipes_data/recipes.json")
        resource?.bufferedReader().use {
            it?.readText() ?: "[]"
        }
    } catch (e: Exception) {
        "[]"
    }
}