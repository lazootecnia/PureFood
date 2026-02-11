package com.lazootecnia.purefood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Light color scheme with warm coral and cream tones
 * inspired by the PureFood design reference
 */
private fun pureFoodLightColorScheme() = lightColorScheme(
    // Primary coral/orange
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralContainer,
    onPrimaryContainer = CoralDark,

    // Secondary olive green
    secondary = OliveGreen,
    onSecondary = Color.White,
    secondaryContainer = OliveGreenLight,
    onSecondaryContainer = OliveGreenDark,

    // Tertiary (optional accent)
    tertiary = CoralLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEDEB),
    onTertiaryContainer = Color(0xFF5D1F18),

    // Background
    background = CreamBackground,
    onBackground = DarkBrown,

    // Surface
    surface = WarmWhite,
    onSurface = DarkBrown,
    surfaceVariant = BeigeLight,
    onSurfaceVariant = WarmGray,

    // Outline and borders
    outline = LightGray,
    outlineVariant = Color(0xFFD7CCC8),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

/**
 * Dark color scheme with warm tones for night mode
 */
private fun pureFoodDarkColorScheme() = darkColorScheme(
    // Primary coral/orange (lighter for dark mode)
    primary = DarkCoralPrimary,
    onPrimary = CoralDark,
    primaryContainer = CoralDark,
    onPrimaryContainer = DarkCoralPrimary,

    // Secondary olive green
    secondary = OliveGreenLight,
    onSecondary = OliveGreenDark,
    secondaryContainer = OliveGreenDark,
    onSecondaryContainer = OliveGreenLight,

    // Tertiary
    tertiary = CoralLight,
    onTertiary = CoralDark,
    tertiaryContainer = CoralDark,
    onTertiaryContainer = CoralLight,

    // Background (dark warm)
    background = DarkBackground,
    onBackground = Color(0xFFFFF8F0),

    // Surface
    surface = DarkSurface,
    onSurface = Color(0xFFFFF8F0),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD7CCC8),

    // Outline
    outline = Color(0xFF9E8B83),
    outlineVariant = Color(0xFF635250),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

/**
 * Custom shapes with more rounded corners for a softer appearance
 */
private val PureFoodShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/**
 * PureFood Theme - Warm, cozy design with coral and cream palette
 */
@Composable
fun PureFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        pureFoodDarkColorScheme()
    } else {
        pureFoodLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = PureFoodShapes,
        content = content
    )
}
