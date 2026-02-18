package com.lazootecnia.purefood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.data.repositories.IAuthRepository
import com.lazootecnia.purefood.data.repositories.IFavoritesRepository
import com.lazootecnia.purefood.data.repositories.IRecipeRepository
import com.lazootecnia.purefood.data.repositories.IRecipeWriteRepository
import com.lazootecnia.purefood.data.export.IRecipeExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val availableCategories: List<String> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val showOnlyFavorites: Boolean = false,
    val selectedRecipe: Recipe? = null,
    val showDetailScreen: Boolean = false,
    val showAdminScreen: Boolean = false,
    val showAuthDialog: Boolean = false,
    val isAuthenticated: Boolean = false,
    val editingRecipe: Recipe? = null,
    val adminError: String? = null,
    val adminSuccess: String? = null
)

class RecipesViewModel(
    private val repository: IRecipeRepository,
    private val writeRepository: IRecipeWriteRepository,
    private val favoritesRepository: IFavoritesRepository,
    private val authRepository: IAuthRepository,
    private val recipeExporter: IRecipeExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
        observeFavorites()
        observeAuthState()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavoriteIds().collect { favorites ->
                _uiState.value = _uiState.value.copy(favoriteIds = favorites)
                applyFilters()
            }
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val recipes = repository.getAllRecipes()
                val categories = extractCategories(recipes)
                _uiState.value = _uiState.value.copy(
                    recipes = recipes,
                    availableCategories = categories,
                    isLoading = false
                )
                applyFilters()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategorySelected(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            if (_uiState.value.favoriteIds.contains(recipeId)) {
                favoritesRepository.removeFavorite(recipeId)
            } else {
                favoritesRepository.addFavorite(recipeId)
            }
        }
    }

    fun toggleShowOnlyFavorites() {
        _uiState.value = _uiState.value.copy(showOnlyFavorites = !_uiState.value.showOnlyFavorites)
        applyFilters()
    }

    fun openRecipeDetail(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(
            selectedRecipe = recipe,
            showDetailScreen = true
        )
    }

    fun closeRecipeDetail() {
        _uiState.value = _uiState.value.copy(
            selectedRecipe = null,
            showDetailScreen = false
        )
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.recipes

        // Filtrar por búsqueda
        if (state.searchQuery.isNotEmpty()) {
            filtered = filtered.filter { recipe ->
                recipe.title.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Filtrar por categoría
        if (state.selectedCategory != null) {
            filtered = filtered.filter { recipe ->
                recipe.categories.any { it.equals(state.selectedCategory, ignoreCase = true) }
            }
        }

        // Filtrar por favoritos
        if (state.showOnlyFavorites) {
            filtered = filtered.filter { recipe ->
                state.favoriteIds.contains(recipe.id)
            }
        }

        _uiState.value = _uiState.value.copy(filteredRecipes = filtered)
    }

    private fun extractCategories(recipes: List<Recipe>): List<String> {
        return recipes
            .flatMap { it.categories }
            .distinct()
            .sorted()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthState().collect { isAuth ->
                _uiState.value = _uiState.value.copy(isAuthenticated = isAuth)
            }
        }
    }

    // Admin navigation methods
    fun requestAdminAccess() {
        if (_uiState.value.isAuthenticated) {
            openAdminScreen()
        } else {
            _uiState.value = _uiState.value.copy(showAuthDialog = true)
        }
    }

    fun dismissAuthDialog() {
        _uiState.value = _uiState.value.copy(showAuthDialog = false)
    }

    fun authenticate(password: String) {
        viewModelScope.launch {
            val isValid = authRepository.validatePassword(password)
            if (isValid) {
                authRepository.setAuthenticated(true)
                _uiState.value = _uiState.value.copy(
                    showAuthDialog = false,
                    adminError = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    adminError = "Contraseña incorrecta"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.setAuthenticated(false)
            _uiState.value = _uiState.value.copy(
                showAdminScreen = false,
                editingRecipe = null
            )
        }
    }

    private fun openAdminScreen() {
        _uiState.value = _uiState.value.copy(
            showAdminScreen = true,
            showDetailScreen = false,
            editingRecipe = null
        )
    }

    fun closeAdminScreen() {
        _uiState.value = _uiState.value.copy(
            showAdminScreen = false,
            editingRecipe = null,
            adminError = null,
            adminSuccess = null
        )
    }

    fun startCreatingRecipe() {
        _uiState.value = _uiState.value.copy(
            editingRecipe = Recipe(
                id = 0,
                title = "",
                categories = emptyList(),
                imageUrl = "",
                ingredients = emptyList(),
                steps = emptyList(),
                notes = emptyList()
            )
        )
    }

    fun startEditingRecipe(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(editingRecipe = recipe)
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(editingRecipe = null)
    }

    fun saveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val result = if (recipe.id == 0) {
                writeRepository.addRecipe(recipe)
            } else {
                writeRepository.updateRecipe(recipe)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        editingRecipe = null,
                        adminSuccess = "Receta guardada correctamente"
                    )
                    loadRecipes()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        adminError = error.message ?: "Error al guardar"
                    )
                }
            )
        }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            val result = writeRepository.deleteRecipe(recipeId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        adminSuccess = "Receta eliminada"
                    )
                    loadRecipes()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        adminError = error.message ?: "Error al eliminar"
                    )
                }
            )
        }
    }

    fun exportRecipes() {
        viewModelScope.launch {
            val result = recipeExporter.exportRecipesToJson(
                writeRepository.getAllRecipesForExport()
            )
            result.fold(
                onSuccess = { path ->
                    _uiState.value = _uiState.value.copy(
                        adminSuccess = "JSON exportado a: $path"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        adminError = "Error al exportar: ${error.message}"
                    )
                }
            )
        }
    }

    fun clearAdminMessages() {
        _uiState.value = _uiState.value.copy(
            adminError = null,
            adminSuccess = null
        )
    }
}