package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.ui.utils.CategoryLocalizer
import com.lazootecnia.purefood.ui.components.RecipeImageWithOverlay
import org.jetbrains.compose.resources.stringResource
import purefood.composeapp.generated.resources.Res
import purefood.composeapp.generated.resources.ingredients
import purefood.composeapp.generated.resources.notes
import purefood.composeapp.generated.resources.steps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val scrollState = rememberLazyListState()
    val completedSteps = remember { mutableStateOf(setOf<Int>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main scrolling content
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Item 1: Parallax Image Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .graphicsLayer {
                            val scrollOffset = if (scrollState.firstVisibleItemIndex == 0) {
                                scrollState.firstVisibleItemScrollOffset.toFloat()
                            } else {
                                300f
                            }
                            translationY = -(scrollOffset * 0.4f)
                            val maxFadeScroll = 300f
                            val alpha = ((maxFadeScroll - scrollOffset) / maxFadeScroll).coerceIn(0f, 1f)
                            this.alpha = alpha
                        }
                ) {
                    RecipeImageWithOverlay(
                        recipeId = recipe.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Item 2: Recipe Meta (categories)
            if (recipe.categories.isNotEmpty()) {
                item {
                    RecipeMeta(categories = recipe.categories)
                }
            }

            // Ingredients
            if (recipe.ingredients.isNotEmpty()) {
                item {
                    Text(
                        text = "🥘 ${stringResource(Res.string.ingredients)} (${recipe.ingredients.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                items(recipe.ingredients) { ingredient ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "✓ $ingredient",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Pasos
            if (recipe.steps.isNotEmpty()) {
                item {
                    Text(
                        text = "📝 ${stringResource(Res.string.steps)} (${recipe.steps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                items(recipe.steps.mapIndexed { index, step -> index to (index + 1 to step) }) { (stepIndex, pair) ->
                    val (stepNumber, step) = pair
                    val isCompleted = completedSteps.value.contains(stepIndex)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                // Toggle el estado del paso
                                completedSteps.value = if (isCompleted) {
                                    completedSteps.value - stepIndex
                                } else {
                                    completedSteps.value + stepIndex
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCompleted) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Checkbox
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = {
                                    completedSteps.value = if (it) {
                                        completedSteps.value + stepIndex
                                    } else {
                                        completedSteps.value - stepIndex
                                    }
                                },
                                modifier = Modifier.size(24.dp),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            // Contenido del paso
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Paso $stepNumber",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCompleted) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                    }
                }
            }

            // Notas
            if (recipe.notes.isNotEmpty()) {
                item {
                    Text(
                        text = "📌 ${stringResource(Res.string.notes)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                items(recipe.notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Sticky TopAppBar overlay
        TopAppBar(
            title = {
                Text(
                    text = recipe.title,
                    modifier = Modifier.graphicsLayer {
                        alpha = getTitleAlpha(scrollState)
                    }
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = onToggleFavorite) {
                    Text(
                        text = if (isFavorite) "❤️" else "🤍",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

// Helper: Calculate title alpha based on scroll
private fun getTitleAlpha(scrollState: androidx.compose.foundation.lazy.LazyListState): Float {
    // Siempre mostrar el título con alpha = 1
    return 1f
}


// Composable: Recipe Meta (categories)
@Composable
private fun RecipeMeta(categories: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { category ->
                val categoryStringRes = CategoryLocalizer.getCategoryStringResource(category)
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (categoryStringRes != null) {
                                stringResource(categoryStringRes)
                            } else {
                                category
                            }
                        )
                    }
                )
            }
        }
    }
}
