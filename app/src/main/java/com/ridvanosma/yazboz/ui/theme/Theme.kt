package com.ridvanosma.yazboz.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = YazbozPrimary,
    onPrimary = YazbozOnPrimary,
    primaryContainer = Color(0xFFD8EEFA),
    onPrimaryContainer = YazbozAccent,
    secondary = YazbozAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7E5FF),
    onSecondaryContainer = YazbozAccent,
    background = YazbozBackground,
    onBackground = YazbozText,
    surface = YazbozBackground,
    onSurface = YazbozText,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF55555D),
    outline = YazbozBorder
)

private val DarkColors = darkColorScheme(
    primary = YazbozDarkPrimary,
    onPrimary = YazbozDarkOnPrimary,
    primaryContainer = Color(0xFF173F54),
    onPrimaryContainer = Color(0xFFD7F0FF),
    secondary = YazbozDarkAccent,
    onSecondary = Color(0xFF2D2A64),
    secondaryContainer = Color(0xFF37346E),
    onSecondaryContainer = Color(0xFFE7E5FF),
    background = YazbozDarkBackground,
    onBackground = Color(0xFFE7E7EC),
    surface = YazbozDarkSurface,
    onSurface = Color(0xFFE7E7EC),
    surfaceVariant = Color(0xFF292B33),
    onSurfaceVariant = Color(0xFFC5C6CE),
    outline = Color(0xFF666874)
)

@Composable
fun YazbozTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = YazbozTypography,
        content = content
    )
}
