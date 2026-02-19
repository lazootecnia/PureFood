package com.lazootecnia.purefood.data.export

import android.content.Context
import android.os.Environment
import com.lazootecnia.purefood.data.models.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual class DataExporter : IDataExporter {

    companion object {
        var appContext: Context? = null
    }

    override suspend fun exportAllDataAsZip(
        recipes: List<Recipe>,
        progressCallback: (Int, Int) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val fileName = "purefood_export_${System.currentTimeMillis()}.zip"
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            if (downloadDir != null && (downloadDir.exists() || downloadDir.mkdirs())) {
                val outputFile = File(downloadDir, fileName)

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
                    val imagesDir = File(appContext?.filesDir ?: throw RuntimeException("Context no disponible"), "recipe_images")
                    val imageFiles = imagesDir.listFiles()?.sortedBy {
                        it.nameWithoutExtension.toIntOrNull() ?: Int.MAX_VALUE
                    } ?: emptyList()

                    var processedCount = 0
                    for (imageFile in imageFiles) {
                        processedCount++
                        progressCallback(processedCount, recipes.size + imageFiles.size)

                        val zipEntry = ZipEntry("images/${imageFile.name}")
                        zipOutput.putNextEntry(zipEntry)
                        imageFile.inputStream().use { it.copyTo(zipOutput) }
                        zipOutput.closeEntry()
                    }

                    // Notificar último progreso
                    progressCallback(recipes.size + imageFiles.size, recipes.size + imageFiles.size)
                }

                Result.success(outputFile.absolutePath)
            } else {
                Result.failure(Exception("No se puede acceder a la carpeta de Descargas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
