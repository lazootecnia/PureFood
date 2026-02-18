package com.lazootecnia.purefood.data.export

import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

actual class RecipeExporter : IRecipeExporter {
    override suspend fun exportRecipesToJson(recipes: List<Recipe>): Result<String> {
        return try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString<List<Recipe>>(recipes)

            // For JVM, save to user's home directory in Documents
            val documentsDir = File(System.getProperty("user.home"), "Documents")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }

            val fileName = "purefood_recipes_${System.currentTimeMillis()}.json"
            val file = File(documentsDir, fileName)
            file.writeText(jsonString)

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
