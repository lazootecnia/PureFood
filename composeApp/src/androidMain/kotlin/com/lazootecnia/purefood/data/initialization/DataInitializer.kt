package com.lazootecnia.purefood.data.initialization

import android.content.Context
import com.lazootecnia.purefood.data.images.ImageProcessor
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.data.validation.RecipeDataValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

actual class DataInitializer(private val context: Context) : IDataInitializer {

    companion object {
        private const val REPO_URL = "https://villaface.duckdns.org/recipes.zip"
        private const val MAX_RETRIES = 3
        private val TIMEOUT = 5.minutes  // 43MB necesita más tiempo
        private const val BUFFER_SIZE = 1024 * 1024  // 1MB chunks
    }

    override suspend fun downloadAndInitializeAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext downloadWithRetry(progressCallback, attempt = 0)
    }

    override suspend fun refreshAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        deleteLocalData()
        return@withContext downloadWithRetry(progressCallback, attempt = 0)
    }

    override suspend fun isDataInitialized(): Boolean = withContext(Dispatchers.IO) {
        val jsonFile = File(context.filesDir, "recipes.json")
        return@withContext jsonFile.exists()
    }

    private suspend fun downloadWithRetry(
        progressCallback: (Long, Long) -> Unit,
        attempt: Int
    ): Result<Unit> {
        return try {
            downloadAndExtractZip(progressCallback)
        } catch (e: Exception) {
            if (attempt < MAX_RETRIES) {
                delay(2000)
                downloadWithRetry(progressCallback, attempt + 1)
            } else {
                Result.failure(
                    Exception("Error descargando después de $MAX_RETRIES intentos: ${e.message}")
                )
            }
        }
    }

    private suspend fun downloadAndExtractZip(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL(REPO_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = TIMEOUT.inWholeMilliseconds.toInt()
            connection.readTimeout = TIMEOUT.inWholeMilliseconds.toInt()
            connection.requestMethod = "GET"

            val totalSize = connection.contentLength.toLong()
            if (totalSize <= 0) {
                return@withContext Result.failure(Exception("No se pudo determinar tamaño del archivo"))
            }

            var downloadedSize = 0L

            try {
                val zipBytes = ByteArrayOutputStream(totalSize.toInt())
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                var lastProgressUpdate = 0L

                connection.inputStream.use { input ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        zipBytes.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead

                        // Update progress cada 1MB para evitar demasiadas actualizaciones
                        if (downloadedSize - lastProgressUpdate >= BUFFER_SIZE) {
                            progressCallback(downloadedSize, totalSize)
                            lastProgressUpdate = downloadedSize
                        }
                    }
                }
                // Notificar progreso final
                progressCallback(downloadedSize, totalSize)

                // Extraer y validar
                val recipes = mutableListOf<Recipe>()
                val images = mutableMapOf<String, ByteArray>()
                val imageFileNames = mutableSetOf<String>()

                ZipInputStream(ByteArrayInputStream(zipBytes.toByteArray())).use { zipInput ->
                    var entry = zipInput.nextEntry

                    while (entry != null) {
                        when {
                            entry.name == "recipes/recipes.json" -> {
                                val jsonContent = zipInput.readBytes().decodeToString()
                                recipes.addAll(Json.decodeFromString<List<Recipe>>(jsonContent))
                            }
                            entry.name.startsWith("images/") -> {
                                val fileName = entry.name.substringAfterLast("/")
                                images[fileName] = zipInput.readBytes()
                                imageFileNames.add(fileName)
                            }
                        }
                        entry = zipInput.nextEntry
                    }
                }

                // Validar: cada receta tiene imagen
                RecipeDataValidator.validateRecipeImages(recipes, imageFileNames).getOrThrow()

                // Procesar imágenes (redimensionar, convertir a WebP)
                val imageProcessor = ImageProcessor()
                val cacheDir = File(context.filesDir, "recipe_images").apply { mkdirs() }

                for ((fileName, imageData) in images) {
                    val recipeId = fileName.substringBeforeLast(".").toIntOrNull() ?: continue

                    // Si es pequeño, guardar como está (probablemente ya es WebP o JPG)
                    val outputFile = File(cacheDir, "$recipeId.webp")

                    try {
                        val processed = imageProcessor.processImage(imageData, recipeId)
                        processed.fold(
                            onSuccess = { outputFile.writeBytes(it) },
                            onFailure = {
                                // Si falla el procesamiento, guardar original
                                outputFile.writeBytes(imageData)
                            }
                        )
                    } catch (e: Exception) {
                        // Si falla completamente, guardar original
                        outputFile.writeBytes(imageData)
                    }
                }

                // Guardar JSON con rutas locales actualizadas
                val recipesWithLocalPaths = recipes.map { recipe ->
                    recipe.copy(imageUrl = "file://${cacheDir.absolutePath}/${recipe.id}.webp")
                }

                val jsonFile = File(context.filesDir, "recipes.json")
                val recipesJson = Json { prettyPrint = true }
                jsonFile.writeText(recipesJson.encodeToString(recipesWithLocalPaths))

                Result.success(Unit)

            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun deleteLocalData() = withContext(Dispatchers.IO) {
        File(context.filesDir, "recipes.json").delete()
        File(context.filesDir, "recipe_images").deleteRecursively()
    }
}

private class ByteArrayOutputStream : java.io.ByteArrayOutputStream()
