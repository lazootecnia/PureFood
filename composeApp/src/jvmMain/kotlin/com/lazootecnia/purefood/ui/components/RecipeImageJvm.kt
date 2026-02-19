package com.lazootecnia.purefood.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.skia.Image as SkiaImage
import java.io.File

@Composable
actual fun RecipeImageContent(
    recipeId: Int,
    modifier: Modifier
) {
    val imageFile = getLocalRecipeImagePath(recipeId)

    if (imageFile.exists()) {
        val painter = remember(recipeId) {
            try {
                val bytes = imageFile.readBytes()
                val skiaImage = SkiaImage.makeFromEncoded(bytes)
                BitmapPainter(skiaImage.toComposeImageBitmap())
            } catch (_: Exception) {
                null
            }
        }

        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Recipe image",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        } else {
            ImageErrorPlaceholder(modifier = modifier)
        }
    } else {
        ImageErrorPlaceholder(modifier = modifier)
    }
}

private fun getLocalRecipeImagePath(recipeId: Int): File {
    val imageName = "${recipeId}.webp"
    val purefoodDir = File(System.getProperty("user.home"), ".purefood")
    return File(purefoodDir, "recipe_images").resolve(imageName)
}
