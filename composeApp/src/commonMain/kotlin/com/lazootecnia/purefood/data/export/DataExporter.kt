package com.lazootecnia.purefood.data.export

import com.lazootecnia.purefood.data.models.Recipe

interface IDataExporter {
    suspend fun exportAllDataAsZip(
        recipes: List<Recipe>,
        progressCallback: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): Result<String>
}

expect class DataExporter() : IDataExporter
