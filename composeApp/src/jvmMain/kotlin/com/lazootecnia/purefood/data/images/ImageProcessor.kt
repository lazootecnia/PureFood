package com.lazootecnia.purefood.data.images

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class ImageProcessor : IImageProcessor {

    override suspend fun processImage(
        imageData: ByteArray,
        imageId: Int
    ): Result<ByteArray> = withContext(Dispatchers.Default) {
        // En JVM, simplemente devolver la imagen original
        // (ImageIO en JVM puede no soportar WebP)
        return@withContext Result.success(imageData)
    }
}
