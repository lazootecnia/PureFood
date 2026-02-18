package com.lazootecnia.purefood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lazootecnia.purefood.data.initDataStore
import com.lazootecnia.purefood.data.repositories.initRecipeDataSource
import com.lazootecnia.purefood.di.ServiceLocator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicializar DataStore, RecipeDataSource y ServiceLocator
        initDataStore(this)
        initRecipeDataSource(this)
        ServiceLocator.initialize(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}