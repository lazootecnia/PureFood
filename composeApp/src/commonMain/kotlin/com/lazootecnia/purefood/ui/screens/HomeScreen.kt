package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.di.ServiceLocator
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel
import com.lazootecnia.purefood.ui.screens.RecipeDetailContent

@Composable
fun HomeScreenContent() {
    val viewModel = remember { ServiceLocator.getRecipesViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val selectedRecipe = uiState.selectedRecipe

    if (uiState.showDetailScreen && selectedRecipe != null) {
        // Mostrar pantalla de detalle
        RecipeDetailContent(
            recipe = selectedRecipe,
            isFavorite = uiState.favoriteIds.contains(selectedRecipe.id),
            onToggleFavorite = { viewModel.toggleFavorite(selectedRecipe.id) },
            onBack = { viewModel.closeRecipeDetail() }
        )
    } else {
        // Mostrar lista de recetas
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                Text(
                    text = "PureFood 🍳",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(32.dp)
                    )
                } else if (uiState.error != null) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    RecipeListScreen(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }
}
