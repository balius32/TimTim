package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Accent color options
data class AccentColorOption(
    val id: String,
    val name: String,
    val color: Color,
    val lightContainer: Color,
    val darkPrimary: Color
)

val ACCENT_COLOR_OPTIONS = listOf(
    AccentColorOption("BLUE", "Royal Blue", Color(0xFF0066EE), Color(0xFFE0EDFF), Color(0xFF3B82F6)),
    AccentColorOption("INDIGO", "Royal Indigo", Color(0xFF6366F1), Color(0xFFE0E7FF), Color(0xFF818CF8)),
    AccentColorOption("EMERALD", "Emerald Green", Color(0xFF059669), Color(0xFFD1FAE5), Color(0xFF10B981)),
    AccentColorOption("AMBER", "Sunset Amber", Color(0xFFD97706), Color(0xFFFEF3C7), Color(0xFFF59E0B)),
    AccentColorOption("ROSE", "Rose Berry", Color(0xFFE11D48), Color(0xFFFFE4E6), Color(0xFFF43F5E)),
    AccentColorOption("TEAL", "Teal Ocean", Color(0xFF0D9488), Color(0xFFCCFBF1), Color(0xFF14B8A6))
)

fun parseThemeSettings(themeMode: String): Pair<String, String> {
    val upper = themeMode.uppercase().trim()
    if (upper.contains("|")) {
        val parts = upper.split("|")
        val mode = parts.getOrNull(0) ?: "SYSTEM"
        val color = parts.getOrNull(1) ?: "BLUE"
        return Pair(mode, color)
    }
    return when (upper) {
        "LIGHT" -> Pair("LIGHT", "BLUE")
        "DARK" -> Pair("DARK", "BLUE")
        "EMERALD" -> Pair("LIGHT", "EMERALD")
        "INDIGO", "PURPLE" -> Pair("DARK", "INDIGO")
        "AMBER", "SUNSET" -> Pair("LIGHT", "AMBER")
        else -> Pair("SYSTEM", "BLUE")
    }
}

fun buildThemeModeString(mode: String, color: String): String {
    return "${mode.uppercase()}|${color.uppercase()}"
}

// Custom App Color Palette supporting dynamic theme switching
data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceSubtle: Color,
    val outline: Color,
    val outlineVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val heroOvertimeColor: Color,
    val heroDeficitColor: Color,
    val isDark: Boolean
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        primary = Color(0xFF0066EE),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0EDFF),
        onPrimaryContainer = Color(0xFF003063),
        background = Color(0xFFF6F9FD),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F5F9),
        surfaceSubtle = Color(0xFFF8FAFC),
        outline = Color(0xFFCBD5E1),
        outlineVariant = Color(0xFFE2E8F0),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF334155),
        textTertiary = Color(0xFF64748B),
        heroOvertimeColor = Color(0xFF4ADE80),
        heroDeficitColor = Color(0xFFFCA5A5),
        isDark = false
    )
}

/**
 * Custom-tuned Overtime (green) color specifically calibrated for each primary theme color
 * to guarantee optimal contrast, legibility, and aesthetics in both Light and Dark modes.
 */
fun getHeroOvertimeColor(accentId: String, isDark: Boolean): Color {
    return when (accentId.uppercase()) {
        "BLUE" -> if (isDark) Color(0xFF86EFAC) else Color(0xFF4ADE80)      // Fresh lime-mint on Royal Blue
        "INDIGO" -> if (isDark) Color(0xFFA7F3D0) else Color(0xFF86EFAC)    // Luminous mint on Indigo
        "EMERALD" -> if (isDark) Color(0xFFD1FAE5) else Color(0xFFA7F3D0)   // Ice mint on Emerald Green
        "AMBER" -> if (isDark) Color(0xFF86EFAC) else Color(0xFF4ADE80)     // Cool vibrant mint on Sunset Amber
        "ROSE" -> if (isDark) Color(0xFF86EFAC) else Color(0xFF4ADE80)      // Crisp complementary green on Rose Berry
        "TEAL" -> if (isDark) Color(0xFF86EFAC) else Color(0xFF4ADE80)      // Bright spring green on Teal Ocean
        else -> if (isDark) Color(0xFF86EFAC) else Color(0xFF4ADE80)
    }
}

/**
 * Custom-tuned Deficit (red) color specifically calibrated for each primary theme color
 * to guarantee optimal contrast, legibility, and aesthetics in both Light and Dark modes.
 */
fun getHeroDeficitColor(accentId: String, isDark: Boolean): Color {
    return when (accentId.uppercase()) {
        "BLUE" -> if (isDark) Color(0xFFFECDD3) else Color(0xFFFCA5A5)      // Soft coral rose on Royal Blue
        "INDIGO" -> if (isDark) Color(0xFFFFCDD2) else Color(0xFFFCA5A5)    // Soft coral red on Indigo
        "EMERALD" -> if (isDark) Color(0xFFFFCDD2) else Color(0xFFFECDD3)   // Luminous coral red on Emerald Green
        "AMBER" -> if (isDark) Color(0xFFFFCDD2) else Color(0xFFFCA5A5)     // Soft crimson on Sunset Amber
        "ROSE" -> if (isDark) Color(0xFFFFE4E6) else Color(0xFFFFCDD2)      // Pastel rose-white on Rose Berry
        "TEAL" -> if (isDark) Color(0xFFFECDD3) else Color(0xFFFCA5A5)      // Soft salmon red on Teal Ocean
        else -> if (isDark) Color(0xFFFECDD3) else Color(0xFFFCA5A5)
    }
}

private fun createAppColors(isDark: Boolean, accent: AccentColorOption): AppColors {
    val overtimeColor = getHeroOvertimeColor(accent.id, isDark)
    val deficitColor = getHeroDeficitColor(accent.id, isDark)

    return if (isDark) {
        AppColors(
            primary = accent.darkPrimary,
            onPrimary = Color.White,
            primaryContainer = accent.color.copy(alpha = 0.22f),
            onPrimaryContainer = accent.darkPrimary,
            background = BentoBackgroundDark,
            surface = BentoSurfaceDark,
            surfaceVariant = BentoSurfaceVariantDark,
            surfaceSubtle = BentoSurfaceSubtleDark,
            outline = BentoOutlineDark,
            outlineVariant = BentoOutlineVariantDark,
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFFCBD5E1),
            textTertiary = Color(0xFF94A3B8),
            heroOvertimeColor = overtimeColor,
            heroDeficitColor = deficitColor,
            isDark = true
        )
    } else {
        AppColors(
            primary = accent.color,
            onPrimary = Color.White,
            primaryContainer = accent.lightContainer,
            onPrimaryContainer = Color(0xFF0F172A),
            background = Color(0xFFF6F9FD),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            surfaceSubtle = Color(0xFFF8FAFC),
            outline = Color(0xFFCBD5E1),
            outlineVariant = Color(0xFFE2E8F0),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF334155),
            textTertiary = Color(0xFF64748B),
            heroOvertimeColor = overtimeColor,
            heroDeficitColor = deficitColor,
            isDark = false
        )
    }
}

val BentoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "SYSTEM",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val (mode, colorId) = parseThemeSettings(themeMode)
    val systemInDark = isSystemInDarkTheme()

    val isDark = when (mode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemInDark
    }

    val selectedAccent = ACCENT_COLOR_OPTIONS.firstOrNull { it.id.equals(colorId, ignoreCase = true) }
        ?: ACCENT_COLOR_OPTIONS[0]

    val appColors = createAppColors(isDark, selectedAccent)

    val colorScheme: ColorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (isDark) {
        darkColorScheme(
            primary = appColors.primary,
            onPrimary = appColors.onPrimary,
            primaryContainer = appColors.primaryContainer,
            onPrimaryContainer = appColors.onPrimaryContainer,
            secondary = Color(0xFFCBD5E1),
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = Color(0xFF334155),
            onSecondaryContainer = Color(0xFFE2E8F0),
            tertiary = appColors.primary,
            onTertiary = Color(0xFF00344F),
            tertiaryContainer = Color(0xFF1E293B),
            onTertiaryContainer = Color(0xFFE2E8F0),
            background = appColors.background,
            surface = appColors.surface,
            surfaceVariant = appColors.surfaceVariant,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.outline,
            outlineVariant = appColors.outlineVariant
        )
    } else {
        lightColorScheme(
            primary = appColors.primary,
            onPrimary = appColors.onPrimary,
            primaryContainer = appColors.primaryContainer,
            onPrimaryContainer = appColors.onPrimaryContainer,
            secondary = BentoSecondary,
            onSecondary = BentoOnSecondary,
            secondaryContainer = BentoSecondaryContainer,
            onSecondaryContainer = BentoOnSecondaryContainer,
            tertiary = appColors.primary,
            onTertiary = Color.White,
            tertiaryContainer = appColors.primaryContainer,
            onTertiaryContainer = appColors.onPrimaryContainer,
            background = appColors.background,
            surface = appColors.surface,
            surfaceVariant = appColors.surfaceVariant,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.outline,
            outlineVariant = appColors.outlineVariant
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = appColors.background.toArgb()
                window.navigationBarColor = appColors.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = BentoShapes,
            content = content
        )
    }
}
