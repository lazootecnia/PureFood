package com.lazootecnia.purefood.data.images

interface IImageProcessor {
    suspend fun processImage(
        imageData: ByteArray,
        imageId: Int
    ): Result<ByteArray>
}

expect class ImageProcessor() : IImageProcessor
