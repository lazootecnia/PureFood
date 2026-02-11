package com.lazootecnia.purefood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.data.repositories.IFavoritesRepository
import com.lazootecnia.purefood.data.repositories.IRecipeRepository
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
    val showDetailScreen: Boolean = false
)

class RecipesViewModel(
    private val repository: IRecipeRepository,
    private val favoritesRepository: IFavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
        observeFavorites()
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
}