package com.lazootecnia.purefood.data.images

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            val originalImage = ImageIO.read(ByteArrayInputStream(imageData))
                ?: return@withContext Result.failure(Exception("No se pudo decodificar imagen"))

            // Redimensionar: max 800x600
            val maxWidth = 800
            val maxHeight = 600
            val ratio = (maxWidth.toFloat() / originalImage.width).coerceAtMost(
                maxHeight.toFloat() / originalImage.height
            )

            val newWidth = (originalImage.width * ratio).toInt().coerceAtLeast(1)
            val newHeight = (originalImage.height * ratio).toInt().coerceAtLeast(1)

            val resized = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
            val g2d = resized.createGraphics()
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
            g2d.dispose()

            // Convertir a WebP
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(resized, "webp", outputStream)

            Result.success(outputStream.toByteArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
