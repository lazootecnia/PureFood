package com.lazootecnia.purefood.data.initialization

import com.lazootecnia.purefood.data.provideFileSystem

actual class DataInitializer : IDataInitializer {
    private val impl = DataInitializerImpl(provideFileSystem())

    override suspend fun downloadAndInitializeAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.downloadAndInitializeAppData(progressCallback)

    override suspend fun refreshAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.refreshAppData(progressCallback)

    override suspend fun isDataInitialized(): Boolean = impl.isDataInitialized()
}
