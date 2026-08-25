package com.example.malayalamcalendar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = DeepBrown,
    primaryContainer = GoldPale,
    onPrimaryContainer = Ink,
    secondary = Rust,
    onSecondary = Cream,
    secondaryContainer = ReminderLight,
    onSecondaryContainer = ReminderBlue,
    tertiary = GreenMid,
    onTertiary = Cream,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = CreamDarker,
    onSurfaceVariant = Ink,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = DeepBrownDark,
    primaryContainer = DeepBrown,
    onPrimaryContainer = GoldLight,
    secondary = RustLight,
    onSecondary = Cream,
    secondaryContainer = DeepBrown,
    onSecondaryContainer = GoldLight,
    tertiary = GreenDark,
    onTertiary = Cream,
    background = DeepBrownDark,
    onBackground = Cream,
    surface = DeepBrown,
    onSurface = Cream,
    surfaceVariant = DeepBrownDark,
    onSurfaceVariant = GoldPale,
    outline = Rust
)

@Composable
fun MalayalamCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepBrown.toArgb()
            window.navigationBarColor = DeepBrown.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
