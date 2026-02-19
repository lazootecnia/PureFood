package com.lazootecnia.purefood.data.initialization

import android.content.Context
import com.lazootecnia.purefood.data.provideFileSystemWithContext

actual class DataInitializer(private val context: Context) : IDataInitializer {
    private val impl = DataInitializerImpl(provideFileSystemWithContext(context))

    override suspend fun downloadAndInitializeAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.downloadAndInitializeAppData(progressCallback)

    override suspend fun refreshAppData(
        progressCallback: (Long, Long) -> Unit
    ): Result<Unit> = impl.refreshAppData(progressCallback)

    override suspend fun isDataInitialized(): Boolean = impl.isDataInitialized()
}
