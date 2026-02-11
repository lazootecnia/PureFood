package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.ui.viewmodels.RecipesUiState
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel

@Composable
fun RecipeListScreen(
    uiState: RecipesUiState,
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.padding(16.dp)
        )

        if (uiState.filteredRecipes.isEmpty()) {
            EmptyStateMessage(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            RecipeList(
                recipes = uiState.filteredRecipes,
                favoriteIds = uiState.favoriteIds,
                onRecipeClick = { recipe ->
                    viewModel.openRecipeDetail(recipe)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🔍", modifier = Modifier.padding(end = 4.dp))

        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Buscar recetas...") },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        if (searchQuery.isNotEmpty()) {
            TextButton(
                onClick = { onSearchQueryChanged("") },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text("✕", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    favoriteIds: Set<Int>,
    onRecipeClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeCard(
                recipe = recipe,
                isFavorite = favoriteIds.contains(recipe.id),
                onClick = { onRecipeClick(recipe) }
            )
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (isFavorite) {
                    Text("❤️", modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (recipe.categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    recipe.categories.take(3).forEach { category ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    category,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🥘 ${recipe.ingredients.size} ingredientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "📝 ${recipe.steps.size} pasos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😕",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "No hay recetas",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Intenta cambiar los filtros o tu búsqueda",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
