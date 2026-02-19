package com.lazootecnia.purefood.data.initialization

import android.content.Context
import com.lazootecnia.purefood.data.provideFileSystemWithContext

actual class DataInitializer : IDataInitializer {
    companion object {
        var appContext: Context? = null
    }

    private val impl = DataInitializerImpl(provideFileSystemWithContext(appContext ?: throw RuntimeException("Context no disponible")))

    override suspend fun downloadAndInitializeAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.downloadAndInitializeAppData(progressCallback)

    override suspend fun refreshAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.refreshAppData(progressCallback)

    override suspend fun isDataInitialized(): Boolean = impl.isDataInitialized()
}
