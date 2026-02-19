package com.lazootecnia.purefood.ui.components

import androidx.compose.runtime.Composable
import java.io.File

@Composable
actual fun getLocalRecipeImagePath(recipeId: Int): File {
    val imageName = "${recipeId}.webp"
    val purefoodDir = File(System.getProperty("user.home"), ".purefood")
    return File(purefoodDir, "recipe_images/$imageName")
}
