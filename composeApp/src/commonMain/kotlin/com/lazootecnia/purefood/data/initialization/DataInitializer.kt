package com.lazootecnia.purefood.data.initialization

interface IDataInitializer {
    suspend fun downloadAndInitializeAppData(
        progressCallback: (downloaded: Long, total: Long) -> Unit = { _, _ -> }
    ): Result<Unit>

    suspend fun refreshAppData(
        progressCallback: (downloaded: Long, total: Long) -> Unit = { _, _ -> }
    ): Result<Unit>

    suspend fun isDataInitialized(): Boolean
}

expect class DataInitializer() : IDataInitializer
