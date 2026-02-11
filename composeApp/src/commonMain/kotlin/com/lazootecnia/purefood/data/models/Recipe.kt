package com.lazootecnia.purefood.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val title: String,
    val categories: List<String> = emptyList(),
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)
