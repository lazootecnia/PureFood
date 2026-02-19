package com.lazootecnia.purefood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.AsyncImagePainter
import java.io.File

@Composable
fun RecipeImageContent(
    recipeId: Int,
    modifier: Modifier = Modifier
) {
    val imagePath = getLocalRecipeImagePath(recipeId)
    val imageUrl = "file://${imagePath.absolutePath}"

    val painter = rememberAsyncImagePainter(imageUrl)
    val state by painter.state.collectAsState()

    Box(modifier = modifier) {
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                ImageLoadingPlaceholder(modifier = Modifier.fillMaxSize())
            }
            is AsyncImagePainter.State.Error -> {
                ImageErrorPlaceholder(modifier = Modifier.fillMaxSize())
            }
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = "Recipe image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is AsyncImagePainter.State.Empty -> {
                ImageErrorPlaceholder(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun RecipeImageWithOverlay(
    recipeId: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Image content
        RecipeImageContent(
            recipeId = recipeId,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay para transición suave
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f)
                        ),
                        startY = 200f
                    )
                )
        )
    }
}

@Composable
fun ImageLoadingPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ImageErrorPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "🍽️",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

// Expect function to get local recipe image path (platform-specific)
@Composable
expect fun getLocalRecipeImagePath(recipeId: Int): File
