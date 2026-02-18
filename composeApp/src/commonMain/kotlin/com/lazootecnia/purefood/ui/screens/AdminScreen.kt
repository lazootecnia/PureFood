package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.ui.viewmodels.RecipesUiState
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    uiState: RecipesUiState,
    viewModel: RecipesViewModel,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar messages
    LaunchedEffect(uiState.adminSuccess, uiState.adminError) {
        uiState.adminSuccess?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAdminMessages()
        }
        uiState.adminError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAdminMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔧 Administración de Recetas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                actions = {
                    // Export button
                    IconButton(onClick = { viewModel.exportRecipes() }) {
                        Text("📥", style = MaterialTheme.typography.titleMedium)
                    }
                    // Logout button
                    IconButton(onClick = { viewModel.logout() }) {
                        Text("🚪", style = MaterialTheme.typography.titleMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCreatingRecipe() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("➕", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { paddingValues ->
        if (uiState.editingRecipe != null) {
            // Show recipe editor form
            RecipeEditorForm(
                recipe = uiState.editingRecipe,
                onSave = { viewModel.saveRecipe(it) },
                onCancel = { viewModel.cancelEditing() },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            // Show recipe management list
            AdminRecipeList(
                recipes = uiState.recipes,
                onEditRecipe = { viewModel.startEditingRecipe(it) },
                onDeleteRecipe = { viewModel.deleteRecipe(it.id) },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun AdminRecipeList(
    recipes: List<Recipe>,
    onEditRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Total de recetas: ${recipes.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(recipes, key = { it.id }) { recipe ->
            AdminRecipeCard(
                recipe = recipe,
                onEdit = { onEditRecipe(recipe) },
                onDelete = { onDeleteRecipe(recipe) }
            )
        }
    }
}

@Composable
private fun AdminRecipeCard(
    recipe: Recipe,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar '${recipe.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ID: ${recipe.id} • ${recipe.ingredients.size} ingredientes • ${recipe.steps.size} pasos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Text("✏️")
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Text("🗑️")
                }
            }
        }
    }
}
