package com.example.fespace.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color


// Elegant Homey Dark Color Scheme (Primary Theme)
private val ElegantHomeyDarkScheme = darkColorScheme(
    primary = Terracotta,
    onPrimary = Cream,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = WarmWhite,
    
    secondary = SageGreen,
    onSecondary = DarkCharcoal,
    secondaryContainer = SageGreenDark,
    onSecondaryContainer = Cream,
    
    tertiary = Gold,
    onTertiary = DarkCharcoal,
    tertiaryContainer = Copper,
    onTertiaryContainer = Cream,
    
    background = DarkCharcoal,
    onBackground = Cream,
    
    surface = DarkGray,
    onSurface = Cream,
    surfaceVariant = DarkGrayLight,
    onSurfaceVariant = Beige,
    surfaceTint = Terracotta,
    
    surfaceDim = DarkGrayDark,
    surfaceBright = DarkGrayLight,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = DarkGrayLight,
    surfaceContainerLow = DarkGray,
    surfaceContainerLowest = DarkCharcoal,
    
    error = AccentRed,
    onError = Cream,
    errorContainer = Color(0xFF4A2020),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Gray600,
    outlineVariant = Gray700,
    
    scrim = Color(0x99000000),
    inverseSurface = Cream,
    inverseOnSurface = DarkCharcoal,
    inversePrimary = TerracottaDark
)

// Light Color Scheme (Fallback - rarely used)
private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = White,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = DarkCharcoal,
    
    secondary = SageGreen,
    onSecondary = White,
    secondaryContainer = SageGreenLight,
    onSecondaryContainer = DarkCharcoal,
    
    tertiary = Gold,
    onTertiary = DarkCharcoal,
    
    background = WarmWhite,
    onBackground = DarkCharcoal,
    
    surface = Cream,
    onSurface = DarkCharcoal,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    
    error = AccentRed,
    onError = White,
    
    outline = Gray400,
    outlineVariant = Gray300
)

@Composable
fun FeSpaceTheme(
    darkTheme: Boolean = true, // Default to dark theme for Elegant Homey
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ElegantHomeyDarkScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dark status bar for elegant homey theme
            window.statusBarColor = DarkCharcoal.toArgb()
            window.navigationBarColor = DarkCharcoal.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}