package com.lazootecnia.purefood.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun RecipeImageContent(
    recipeId: Int,
    modifier: Modifier
) {
    val context = LocalContext.current
    val imageFile = getLocalRecipeImagePath(context, recipeId)

    if (imageFile.exists()) {
        val bitmap = remember(recipeId) {
            try {
                BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap,
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

private fun getLocalRecipeImagePath(context: Context, recipeId: Int): File {
    val imageName = recipeId.toString().padStart(3, '0') + ".webp"
    return File(context.cacheDir, "recipe_images").resolve(imageName)
}
