package com.lazootecnia.purefood.data

import java.io.File

class JvmFileSystem : IFileSystem {
    override fun getAppDataDir(): File = File(System.getProperty("user.home"), ".purefood")
}

actual fun provideFileSystem(): IFileSystem = JvmFileSystem()
