package com.lazootecnia.purefood.data

import android.content.Context
import java.io.File

class AndroidFileSystem(private val context: Context) : IFileSystem {
    override fun getAppDataDir(): File = context.filesDir
}

actual fun provideFileSystem(): IFileSystem {
    // Se inyectará desde ServiceLocator con contexto
    throw UnsupportedOperationException("Use provideFileSystemWithContext(context)")
}

fun provideFileSystemWithContext(context: Context): IFileSystem = AndroidFileSystem(context)
