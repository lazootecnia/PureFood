package com.lazootecnia.purefood.di

import com.lazootecnia.purefood.data.createDataStore
import com.lazootecnia.purefood.data.export.RecipeExporter
import com.lazootecnia.purefood.data.export.DataExporter
import com.lazootecnia.purefood.data.initialization.DataInitializer
import com.lazootecnia.purefood.data.repositories.AuthRepository
import com.lazootecnia.purefood.data.repositories.FavoritesRepository
import com.lazootecnia.purefood.data.repositories.IAuthRepository
import com.lazootecnia.purefood.data.repositories.IFavoritesRepository
import com.lazootecnia.purefood.data.repositories.IRecipeDataSource
import com.lazootecnia.purefood.data.repositories.IRecipeRepository
import com.lazootecnia.purefood.data.repositories.JsonRecipeDataSource
import com.lazootecnia.purefood.data.repositories.RecipeRepository
import com.lazootecnia.purefood.ui.viewmodels.RecipesViewModel

expect object ServiceLocator {
    fun getRecipesViewModel(): RecipesViewModel
}