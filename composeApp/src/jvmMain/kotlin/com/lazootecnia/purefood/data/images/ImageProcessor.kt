package com.lazootecnia.purefood.data.images

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual class ImageProcessor : IImageProcessor {

    override suspend fun processImage(
        imageData: ByteArray,
        imageId: Int
    ): Result<ByteArray> = withContext(Dispatchers.Default) {
        return@withContext try {
            // Leer imagen desde ByteArray
            val originalImage = ImageIO.read(ByteArrayInputStream(imageData))
                ?: return@withContext Result.failure(Exception("No se pudo decodificar imagen"))

            // Redimensionar: max 800x600 para mostrar bien en cualquier dispositivo
            val maxWidth = 800
            val maxHeight = 600
            val ratio = (maxWidth.toFloat() / originalImage.width).coerceAtMost(
                maxHeight.toFloat() / originalImage.height
            )

            val newWidth = (originalImage.width * ratio).toInt().coerceAtLeast(1)
            val newHeight = (originalImage.height * ratio).toInt().coerceAtLeast(1)

            // Crear imagen redimensionada
            val scaledImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
            val graphics2D = scaledImage.createGraphics()
            graphics2D.drawImage(
                originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
                0, 0, null
            )
            graphics2D.dispose()

            // Intentar guardar como WebP primero, fallback a PNG
            val outputStream = ByteArrayOutputStream()
            val format = try {
                if (ImageIO.write(scaledImage, "webp", outputStream)) {
                    "webp"
                } else {
                    // Fallback a PNG si WebP no está disponible
                    ImageIO.write(scaledImage, "png", outputStream)
                    "png"
                }
            } catch (e: Exception) {
                // Fallback a PNG si algo falla
                outputStream.reset()
                ImageIO.write(scaledImage, "png", outputStream)
                "png"
            }

            Result.success(outputStream.toByteArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
