package com.example.tp_b2a.ui.theme

import android.app.Activity
import android.os.Build
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

// ─── Thème clair ──────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = BleuPrimaire,
    onPrimary        = Color.White,
    primaryContainer = BleuClair,
    secondary        = CouleurDelegue,
    tertiary         = CouleurEnseignant,
    background       = GrisFond,
    surface          = BlancCard,
    onBackground     = Color(0xFF1C1B1F),
    onSurface        = Color(0xFF1C1B1F),
    error            = RougeAbsent,
)

// ─── Thème sombre ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = DarkBleuClair,
    onPrimary        = Color.White,
    primaryContainer = BleuPrimaire,
    secondary        = Color(0xFFCE93D8),
    tertiary         = Color(0xFF80CBC4),
    background       = DarkBackground,
    surface          = DarkSurface,
    onBackground     = Color(0xFFE6E1E5),
    onSurface        = Color(0xFFE6E1E5),
    error            = DarkRougeAbsent,
)

@Composable
fun TP_B2ATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}