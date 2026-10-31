package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = Color.White,
    primaryContainer = IndigoHeader,
    onPrimaryContainer = Color.White,
    secondary = SaffronGold,
    onSecondary = Color.White,
    secondaryContainer = SaffronLight,
    onSecondaryContainer = SaffronDark,
    tertiary = PaidGreen,
    onTertiary = Color.White,
    tertiaryContainer = PaidGreenLight,
    onTertiaryContainer = PaidGreenDark,
    background = BackgroundSlate,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMuted,
    outline = BorderSlate,
    error = PendingRed,
    onError = Color.White,
    errorContainer = PendingRedLight,
    onErrorContainer = PendingRedDark
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = DeepNavy,
    onPrimaryContainer = Color.White,
    secondary = SaffronGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = SaffronLight,
    tertiary = PaidGreen,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    surface = SurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    error = PendingRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
