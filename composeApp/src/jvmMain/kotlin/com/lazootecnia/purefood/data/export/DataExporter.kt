package com.lazootecnia.purefood.data.export

import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual class DataExporter : IDataExporter {

    override suspend fun exportAllDataAsZip(
        recipes: List<Recipe>,
        progressCallback: (Int, Int) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val fileName = "purefood_export_${System.currentTimeMillis()}.zip"
            val documentsDir = File(System.getProperty("user.home"), "Documents")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }

            val outputFile = File(documentsDir, fileName)

            ZipOutputStream(outputFile.outputStream()).use { zipOutput ->
                // 1. Agregar recipes.json (sin las rutas locales file://)
                val recipesForExport = recipes.map { recipe ->
                    recipe.copy(imageUrl = "assets/images/${recipe.id.toString().padStart(3, '0')}.webp")
                }

                val recipesJson = Json { prettyPrint = true }
                val jsonEntry = ZipEntry("recipes/recipes.json")
                zipOutput.putNextEntry(jsonEntry)
                zipOutput.write(recipesJson.encodeToString(recipesForExport).toByteArray())
                zipOutput.closeEntry()

                // 2. Agregar imágenes
                val appDir = File(System.getProperty("user.home"), ".purefood")
                val imagesDir = File(appDir, "recipe_images")
                val imageFiles = imagesDir.listFiles()?.sortedBy {
                    it.nameWithoutExtension.toLongOrNull() ?: Long.MAX_VALUE
                } ?: emptyList()

                var processedCount = 0
                for (imageFile in imageFiles) {
                    processedCount++
                    progressCallback(processedCount, recipes.size + imageFiles.size)

                    val zipEntry = ZipEntry("images/${imageFile.name}")
                    zipOutput.putNextEntry(zipEntry)

                    if (imageFile.exists() && imageFile.length() > 0) {
                        imageFile.inputStream().use { it.copyTo(zipOutput) }
                    }
                    zipOutput.closeEntry()
                }

                // Notificar último progreso
                progressCallback(recipes.size + imageFiles.size, recipes.size + imageFiles.size)
            }

            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
