package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.di.ServiceLocator
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent() {
    val viewModel = remember { ServiceLocator.getRecipesViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val selectedRecipe = uiState.selectedRecipe
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (uiState.showDetailScreen && selectedRecipe != null) {
        // Mostrar pantalla de detalle
        RecipeDetailContent(
            recipe = selectedRecipe,
            isFavorite = uiState.favoriteIds.contains(selectedRecipe.id),
            onToggleFavorite = { viewModel.toggleFavorite(selectedRecipe.id) },
            onBack = { viewModel.closeRecipeDetail() }
        )
    } else {
        // Mostrar lista de recetas con drawer responsivo
        BoxWithConstraints {
            val isLargeScreen = maxWidth >= 840.dp

            if (isLargeScreen) {
                LargeScreenLayout(
                    uiState = uiState,
                    viewModel = viewModel
                )
            } else {
                SmallScreenLayout(
                    uiState = uiState,
                    viewModel = viewModel,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LargeScreenLayout(
    uiState: com.lazootecnia.purefood.ui.viewmodels.RecipesUiState,
    viewModel: RecipesViewModel
) {
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    categories = uiState.availableCategories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { category ->
                        viewModel.onCategorySelected(category)
                    },
                    showOnlyFavorites = uiState.showOnlyFavorites,
                    onToggleFavorites = {
                        viewModel.toggleShowOnlyFavorites()
                    }
                )
            }
        }
    ) {
        MainContent(
            uiState = uiState,
            viewModel = viewModel,
            showMenuIcon = false
        )
    }
}

@Composable
private fun SmallScreenLayout(
    uiState: com.lazootecnia.purefood.ui.viewmodels.RecipesUiState,
    viewModel: RecipesViewModel,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                categories = uiState.availableCategories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.onCategorySelected(category)
                    scope.launch { drawerState.close() }
                },
                showOnlyFavorites = uiState.showOnlyFavorites,
                onToggleFavorites = {
                    viewModel.toggleShowOnlyFavorites()
                }
            )
        }
    ) {
        MainContent(
            uiState = uiState,
            viewModel = viewModel,
            showMenuIcon = true,
            onMenuClick = { scope.launch { drawerState.open() } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    uiState: com.lazootecnia.purefood.ui.viewmodels.RecipesUiState,
    viewModel: RecipesViewModel,
    showMenuIcon: Boolean = true,
    onMenuClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PureFood 🍳") },
                navigationIcon = {
                    if (showMenuIcon) {
                        IconButton(onClick = onMenuClick) {
                            Text(
                                text = "☰",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            RecipeListScreen(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun DrawerContent(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    showOnlyFavorites: Boolean,
    onToggleFavorites: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                NavigationDrawerItem(
                    label = { Text("Todas las recetas") },
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(categories) { category ->
                NavigationDrawerItem(
                    label = { Text(category) },
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = { Text("❤️ Mis Favoritos") },
            selected = showOnlyFavorites,
            onClick = { onToggleFavorites() },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
