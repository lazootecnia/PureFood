package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import com.lazootecnia.purefood.ui.utils.PlatformBackHandler
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import purefood.composeapp.generated.resources.Res
import purefood.composeapp.generated.resources.all_recipes
import purefood.composeapp.generated.resources.app_name
import purefood.composeapp.generated.resources.categories_title
import purefood.composeapp.generated.resources.my_favorites
import com.lazootecnia.purefood.ui.utils.CategoryLocalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent() {
    val viewModel = remember { ServiceLocator.getRecipesViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val selectedRecipe = uiState.selectedRecipe
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button press
    PlatformBackHandler(enabled = uiState.showDetailScreen || uiState.showAdminScreen) {
        when {
            uiState.showAdminScreen -> viewModel.closeAdminScreen()
            uiState.showDetailScreen -> viewModel.closeRecipeDetail()
        }
    }

    // Show authentication dialog if needed
    if (uiState.showAuthDialog) {
        AuthenticationDialog(
            onDismiss = { viewModel.dismissAuthDialog() },
            onAuthenticate = { password -> viewModel.authenticate(password) },
            error = uiState.adminError
        )
    }

    // Show sync progress if syncing
    if (uiState.isSyncingData) {
        SyncProgressScreen(
            title = "⬇️ Descargando recetas...",
            progress = uiState.syncProgress,
            progressText = uiState.syncProgressText
        )
        return
    }

    // Show export progress if exporting
    if (uiState.isExportingData) {
        SyncProgressScreen(
            title = "📦 Exportando datos...",
            progress = uiState.exportProgress,
            progressText = uiState.exportProgressText
        )
        return
    }

    // Priority: Admin > Detail > List
    when {
        uiState.showAdminScreen -> {
            AdminScreen(
                uiState = uiState,
                viewModel = viewModel,
                onBack = { viewModel.closeAdminScreen() }
            )
        }
        uiState.showDetailScreen && selectedRecipe != null -> {
            // Mostrar pantalla de detalle
            RecipeDetailContent(
                recipe = selectedRecipe,
                isFavorite = uiState.favoriteIds.contains(selectedRecipe.id),
                onToggleFavorite = { viewModel.toggleFavorite(selectedRecipe.id) },
                onBack = { viewModel.closeRecipeDetail() }
            )
        }
        else -> {
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
                    },
                    onAdminClick = {
                        viewModel.requestAdminAccess()
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
                },
                onAdminClick = {
                    viewModel.requestAdminAccess()
                    scope.launch { drawerState.close() }
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
                title = { Text(stringResource(Res.string.app_name)) },
                navigationIcon = {
                    if (showMenuIcon) {
                        IconButton(onClick = onMenuClick) {
                            Text(
                                text = "☰",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncRecipesFromRepository() },
                        enabled = !uiState.isSyncingData
                    ) {
                        Text("🔄", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
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
    onToggleFavorites: () -> Unit,
    onAdminClick: () -> Unit = {}
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.categories_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }

            item {
                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.all_recipes)) },
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            items(categories) { category ->
                val categoryStringRes = CategoryLocalizer.getCategoryStringResource(category)
                NavigationDrawerItem(
                    label = {
                        Text(
                            if (categoryStringRes != null) {
                                stringResource(categoryStringRes)
                            } else {
                                category
                            }
                        )
                    },
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.my_favorites)) },
                    selected = showOnlyFavorites,
                    onClick = { onToggleFavorites() },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("🔧 Administración") },
                    selected = false,
                    onClick = onAdminClick,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
