package com.lazootecnia.purefood.ui.utils

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // JVM/Desktop no tiene back button físico
    // Esta función no hace nada en estas plataformas
}
