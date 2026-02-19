package com.lazootecnia.purefood.data.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual class ImageProcessor : IImageProcessor {

    override suspend fun processImage(
        imageData: ByteArray,
        imageId: Int
    ): Result<ByteArray> = withContext(Dispatchers.Default) {
        return@withContext try {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                ?: return@withContext Result.failure(Exception("No se pudo decodificar imagen"))

            // Redimensionar: max 800x600 para mostrar bien en cualquier dispositivo
            val maxWidth = 800
            val maxHeight = 600
            val ratio = (maxWidth.toFloat() / bitmap.width).coerceAtMost(
                maxHeight.toFloat() / bitmap.height
            )

            val newWidth = (bitmap.width * ratio).toInt().coerceAtLeast(1)
            val newHeight = (bitmap.height * ratio).toInt().coerceAtLeast(1)

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

            // Convertir a WebP con compresión
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 85, outputStream)

            if (bitmap != scaledBitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            Result.success(outputStream.toByteArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
