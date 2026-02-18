package com.lazootecnia.purefood.di

import com.lazootecnia.purefood.data.createDataStore
import com.lazootecnia.purefood.data.export.RecipeExporter
import com.lazootecnia.purefood.data.repositories.AuthRepository
import com.lazootecnia.purefood.data.repositories.FavoritesRepository
import com.lazootecnia.purefood.data.repositories.IAuthRepository
import com.lazootecnia.purefood.data.repositories.IFavoritesRepository
import com.lazootecnia.purefood.data.repositories.IRecipeDataSource
import com.lazootecnia.purefood.data.repositories.IRecipeRepository
import com.lazootecnia.purefood.data.repositories.JsonRecipeDataSource
import com.lazootecnia.purefood.data.repositories.RecipeRepository
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel

object ServiceLocator {
    private val recipeDataSource: IRecipeDataSource by lazy {
        JsonRecipeDataSource()
    }

    private val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(recipeDataSource)
    }

    private val dataStore by lazy {
        createDataStore()
    }

    private val favoritesRepository: IFavoritesRepository by lazy {
        FavoritesRepository(dataStore)
    }

    private val authRepository: IAuthRepository by lazy {
        AuthRepository(dataStore)
    }

    private val recipeExporter by lazy {
        RecipeExporter()
    }

    fun getRecipesViewModel(): RecipesViewModel {
        return RecipesViewModel(
            recipeRepository,
            recipeRepository,
            favoritesRepository,
            authRepository,
            recipeExporter
        )
    }
}