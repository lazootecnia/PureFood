package com.lazootecnia.purefood.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun getLocalRecipeImagePath(recipeId: Int): File {
    val context = LocalContext.current
    val imageName = "${recipeId}.webp"
    return File(context.filesDir, "recipe_images/$imageName")
}
