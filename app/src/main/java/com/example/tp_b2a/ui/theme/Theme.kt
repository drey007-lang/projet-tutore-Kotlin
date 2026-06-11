package com.example.tp_b2a.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ─── Schéma clair ────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = BleuPrimaire,
    onPrimary        = BlancCard,
    primaryContainer = Color(0xFFD8E6FF),
    secondary        = BleuClair,
    onSecondary      = BlancCard,
    background       = GrisFond,
    surface          = BlancCard,
    onBackground     = Color(0xFF1C1B1F),
    onSurface        = Color(0xFF1C1B1F),
    error            = RougeAbsent,
)

// ─── Schéma sombre ───────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = BleuClair,
    onPrimary        = BlancCard,
    primaryContainer = BleuPrimaire,
    secondary        = Color(0xFF90CAF9),
    onSecondary      = Color(0xFF1A3A6B),
    background       = GrisFondDark,
    surface          = SurfaceDark,
    onBackground     = Color(0xFFE6E1E5),
    onSurface        = Color(0xFFE6E1E5),
    error            = Color(0xFFEF9A9A),
)

@Composable
fun TP_B2ATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}