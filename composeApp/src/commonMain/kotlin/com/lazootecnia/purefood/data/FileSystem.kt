package com.lazootecnia.purefood.data

import java.io.File

interface IFileSystem {
    fun getAppDataDir(): File
}

expect fun provideFileSystem(): IFileSystem
