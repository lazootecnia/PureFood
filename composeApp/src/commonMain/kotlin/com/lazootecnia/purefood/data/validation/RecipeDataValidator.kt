package com.lazootecnia.purefood.data.validation

import com.lazootecnia.purefood.data.models.Recipe

object RecipeDataValidator {

    fun validateRecipeImages(
        recipes: List<Recipe>,
        availableImages: Set<String>
    ): Result<Unit> {
        for (recipe in recipes) {
            val imageFileName = formatImageName(recipe.id)

            // Buscar imagen con cualquier extensión (.webp, .jpg, .png)
            val hasImage = availableImages.any { imageName ->
                val nameWithoutExt = imageName.substringBeforeLast(".")
                nameWithoutExt == imageFileName
            }

            if (!hasImage) {
                return Result.failure(
                    Exception("Receta ${recipe.id} (${recipe.title}) sin imagen $imageFileName")
                )
            }
        }
        return Result.success(Unit)
    }

    fun formatImageName(id: Int): String {
        return id.toString().padStart(3, '0')  // 1 → "001", 2569 → "2569"
    }
}
