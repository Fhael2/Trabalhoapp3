package com.example.brotoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Forest80,
    onPrimary = Color(0xFF0F3A10),
    primaryContainer = ContainerEscuro,
    onPrimaryContainer = ContainerClaro,
    secondary = Moss80,
    tertiary = Cream80,
    background = FundoEscuro,
    onBackground = TextoEscuro,
    surface = FundoEscuro,
    onSurface = TextoEscuro,
    surfaceVariant = CardEscuro,
    onSurfaceVariant = TextoEscuro
)

private val LightColorScheme = lightColorScheme(
    primary = Forest40,
    onPrimary = Color.White,
    primaryContainer = ContainerClaro,
    onPrimaryContainer = Color(0xFF0F3A10),
    secondary = Moss40,
    tertiary = Cream40,
    background = FundoClaro,
    onBackground = TextoClaro,
    surface = FundoClaro,
    onSurface = TextoClaro,
    surfaceVariant = CardClaro,
    onSurfaceVariant = TextoClaro
)

@Composable
fun BrotoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
