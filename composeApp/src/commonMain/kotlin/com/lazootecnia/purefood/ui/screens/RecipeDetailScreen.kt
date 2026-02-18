package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.ui.utils.CategoryLocalizer
import org.jetbrains.compose.resources.stringResource
import purefood.composeapp.generated.resources.Res
import purefood.composeapp.generated.resources.ingredients
import purefood.composeapp.generated.resources.notes
import purefood.composeapp.generated.resources.steps

@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    // Estado local para rastrear pasos completados (solo en sesión actual)
    val completedSteps = remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con botón atrás y favorito
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "🍳",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = if (isFavorite) "❤️" else "🤍",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            item {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Categorías
            if (recipe.categories.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Categorías",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            recipe.categories.forEach { category ->
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
            }

            // Ingredientes
            if (recipe.ingredients.isNotEmpty()) {
                item {
                    Text(
                        text = "🥘 ${stringResource(Res.string.ingredients)} (${recipe.ingredients.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(recipe.ingredients) { ingredient ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(recipe.steps.mapIndexed { index, step -> index to (index + 1 to step) }) { (stepIndex, pair) ->
                    val (stepNumber, step) = pair
                    val isCompleted = completedSteps.value.contains(stepIndex)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
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
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(recipe.notes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
