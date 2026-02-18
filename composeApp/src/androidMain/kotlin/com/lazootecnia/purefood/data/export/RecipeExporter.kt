package com.lazootecnia.purefood.data.export

import android.os.Environment
import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

actual class RecipeExporter : IRecipeExporter {
    override suspend fun exportRecipesToJson(recipes: List<Recipe>): Result<String> {
        return try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString<List<Recipe>>(recipes)

            // Save to Downloads folder
            val fileName = "purefood_recipes_${System.currentTimeMillis()}.json"
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            if (downloadDir != null && (downloadDir.exists() || downloadDir.mkdirs())) {
                val file = File(downloadDir, fileName)
                file.writeText(jsonString)
                Result.success(file.absolutePath)
            } else {
                Result.failure(Exception("No se pudo acceder a la carpeta de Descargas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
