package com.lazootecnia.purefood.di

import com.lazootecnia.purefood.data.createDataStore
import com.lazootecnia.purefood.data.repositories.FavoritesRepository
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

    private val recipeRepository: IRecipeRepository by lazy {
        RecipeRepository(recipeDataSource)
    }

    private val dataStore by lazy {
        createDataStore()
    }

    private val favoritesRepository: IFavoritesRepository by lazy {
        FavoritesRepository(dataStore)
    }

    fun getRecipesViewModel(): RecipesViewModel {
        return RecipesViewModel(recipeRepository, favoritesRepository)
    }
}