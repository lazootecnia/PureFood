package com.lazootecnia.purefood

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.di.ServiceLocator
import com.lazootecnia.purefood.ui.screens.HomeScreenContent

@Composable
@Preview
fun App() {
    MaterialTheme {
        HomeScreenContent()
    }
}