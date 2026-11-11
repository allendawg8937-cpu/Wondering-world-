package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext

fun buildWonderingWorldColorScheme(
    accentColor: Color,
    secondaryColor: Color,
    isDarkMode: Boolean,
    surfaceAlpha: Float = 0.62f,
    background: Color = if (isDarkMode) lerp(DeepSpaceBg, accentColor, 0.14f) else lerp(LightSpaceBg, accentColor, 0.08f),
    surface: Color = if (isDarkMode) lerp(DeepSlateSurface, accentColor, 0.18f) else lerp(LightSlateSurface, accentColor, 0.10f),
    surfaceVariant: Color = if (isDarkMode) lerp(ElevatedCardBg, accentColor, 0.22f) else lerp(LightElevatedCardBg, accentColor, 0.14f),
    onBackground: Color = if (isDarkMode) TextPrimaryLight else TextPrimaryDark,
    onSurface: Color = if (isDarkMode) TextPrimaryLight else TextPrimaryDark,
    onSurfaceVariant: Color = if (isDarkMode) TextSecondaryMuted else TextSecondaryDark,
    outline: Color = if (isDarkMode) lerp(SubtleBorderSlate, accentColor, 0.35f) else lerp(LightBorderSlate, accentColor, 0.30f)
): ColorScheme {
    val effectiveSurfaceAlpha = surfaceAlpha.coerceIn(0.0f, 1.0f)
    val effectiveVariantAlpha = (surfaceAlpha * 0.88f).coerceIn(0.0f, 1.0f)
    val containerAlpha = (surfaceAlpha * 0.24f).coerceIn(0.0f, 0.28f)
    return if (isDarkMode) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = Color.Black,
            primaryContainer = accentColor.copy(alpha = containerAlpha),
            onPrimaryContainer = TextPrimaryLight,
            secondary = secondaryColor,
            onSecondary = Color.Black,
            secondaryContainer = secondaryColor.copy(alpha = containerAlpha),
            onSecondaryContainer = TextPrimaryLight,
            tertiary = CyberEmerald,
            onTertiary = Color.Black,
            tertiaryContainer = CyberEmerald.copy(alpha = containerAlpha),
            onTertiaryContainer = TextPrimaryLight,
            background = Color.Transparent,
            onBackground = onBackground,
            surface = surface.copy(alpha = effectiveSurfaceAlpha),
            onSurface = onSurface,
            surfaceVariant = surfaceVariant.copy(alpha = effectiveVariantAlpha),
            onSurfaceVariant = onSurfaceVariant,
            surfaceContainerLowest = lerp(DarkSurfaceContainerLowest, accentColor, 0.10f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerLow = lerp(DarkSurfaceContainerLow, accentColor, 0.14f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainer = lerp(DarkSurfaceContainer, accentColor, 0.18f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerHigh = lerp(DarkSurfaceContainerHigh, accentColor, 0.22f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerHighest = lerp(DarkSurfaceContainerHighest, accentColor, 0.26f).copy(alpha = effectiveSurfaceAlpha),
            outline = outline,
            outlineVariant = lerp(SubtleOutlineVariantDark, accentColor, 0.25f),
            inverseSurface = LightSlateSurface,
            inverseOnSurface = TextPrimaryDark,
            inversePrimary = accentColor
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.copy(alpha = 0.18f),
            onPrimaryContainer = TextPrimaryDark,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = secondaryColor.copy(alpha = 0.18f),
            onSecondaryContainer = TextPrimaryDark,
            tertiary = CyberEmerald,
            onTertiary = Color.White,
            tertiaryContainer = CyberEmerald.copy(alpha = 0.16f),
            onTertiaryContainer = TextPrimaryDark,
            background = Color.Transparent,
            onBackground = onBackground,
            surface = surface.copy(alpha = effectiveSurfaceAlpha),
            onSurface = onSurface,
            surfaceVariant = surfaceVariant.copy(alpha = effectiveVariantAlpha),
            onSurfaceVariant = onSurfaceVariant,
            surfaceContainerLowest = LightSurfaceContainerLowest.copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerLow = lerp(LightSurfaceContainerLow, accentColor, 0.08f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainer = lerp(LightSurfaceContainer, accentColor, 0.12f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerHigh = lerp(LightSurfaceContainerHigh, accentColor, 0.16f).copy(alpha = effectiveSurfaceAlpha),
            surfaceContainerHighest = lerp(LightSurfaceContainerHighest, accentColor, 0.20f).copy(alpha = effectiveSurfaceAlpha),
            outline = outline,
            outlineVariant = lerp(LightOutlineVariant, accentColor, 0.22f),
            inverseSurface = DeepSlateSurface,
            inverseOnSurface = TextPrimaryLight,
            inversePrimary = accentColor
        )
    }
}

@Composable
fun WonderingWorldTheme(
    accentHex: String = "#6366F1",
    isDarkMode: Boolean = isSystemInDarkTheme(),
    backgroundTransparency: Float = 0.82f,
    useDynamicColor: Boolean = false,
    isLateNightEyeComfort: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val warmNightAmber = Color(0xFFFFB74D)
    val cleanHex = accentHex.trim().uppercase()
    val isPureWhiteTheme = cleanHex == "#FFFFFF"
    val isPureBlackTheme = cleanHex == "#111827" || cleanHex == "#000000"

    val baseAccent = when {
        isPureWhiteTheme && !isDarkMode -> SolidRoyalBlue
        isPureBlackTheme && isDarkMode -> Color(0xFFE2E8F0)
        else -> parseThemeHex(accentHex)
    }
    val targetAccent = if (isDarkMode && isLateNightEyeComfort) {
        lerp(baseAccent, warmNightAmber, 0.35f)
    } else {
        baseAccent
    }
    val targetSecondary = findSecondaryAccentForHex(accentHex)
    // Support 0% Solid (1.0f opacity) through 100% Transparent All Backgrounds (0.0f opacity)
    val surfaceAlpha = (1f - backgroundTransparency.coerceIn(0f, 1f)).coerceIn(0f, 1f)

    val darkBaseBg = when {
        isPureBlackTheme -> Color(0xFF05070B)
        isLateNightEyeComfort -> Color(0xFF040508)
        else -> lerp(DeepSpaceBg, parseThemeHex(accentHex), 0.28f)
    }
    val darkBaseSurface = when {
        isPureBlackTheme -> Color(0xFF111827)
        isLateNightEyeComfort -> Color(0xFF090C12)
        else -> lerp(DeepSlateSurface, parseThemeHex(accentHex), 0.25f)
    }
    val darkBaseVariant = when {
        isPureBlackTheme -> Color(0xFF1F2937)
        isLateNightEyeComfort -> Color(0xFF101520)
        else -> lerp(ElevatedCardBg, parseThemeHex(accentHex), 0.30f)
    }

    val lightBaseBg = if (isPureWhiteTheme) Color(0xFFFFFFFF) else lerp(LightSpaceBg, parseThemeHex(accentHex), 0.22f)
    val lightBaseSurface = if (isPureWhiteTheme) Color(0xFFFFFFFF) else lerp(LightSlateSurface, parseThemeHex(accentHex), 0.16f)
    val lightBaseVariant = if (isPureWhiteTheme) Color(0xFFF1F5F9) else lerp(LightElevatedCardBg, parseThemeHex(accentHex), 0.24f)

    val animatedPrimary by animateColorAsState(
        targetValue = targetAccent,
        animationSpec = tween(durationMillis = 380),
        label = "theme_primary_anim"
    )
    val animatedSecondary by animateColorAsState(
        targetValue = targetSecondary,
        animationSpec = tween(durationMillis = 380),
        label = "theme_secondary_anim"
    )
    val animatedBackground by animateColorAsState(
        targetValue = if (isDarkMode) darkBaseBg else lightBaseBg,
        animationSpec = tween(durationMillis = 380),
        label = "theme_bg_anim"
    )
    val animatedSurface by animateColorAsState(
        targetValue = if (isDarkMode) darkBaseSurface else lightBaseSurface,
        animationSpec = tween(durationMillis = 380),
        label = "theme_surface_anim"
    )
    val animatedSurfaceVariant by animateColorAsState(
        targetValue = if (isDarkMode) darkBaseVariant else lightBaseVariant,
        animationSpec = tween(durationMillis = 380),
        label = "theme_surface_variant_anim"
    )
    val animatedOnBackground by animateColorAsState(
        targetValue = if (isDarkMode) {
            if (isLateNightEyeComfort) Color(0xFFF5E6D3) else TextPrimaryLight
        } else {
            TextPrimaryDark
        },
        animationSpec = tween(durationMillis = 380),
        label = "theme_on_bg_anim"
    )
    val animatedOnSurface by animateColorAsState(
        targetValue = if (isDarkMode) {
            if (isLateNightEyeComfort) Color(0xFFF5E6D3) else TextPrimaryLight
        } else {
            TextPrimaryDark
        },
        animationSpec = tween(durationMillis = 380),
        label = "theme_on_surface_anim"
    )
    val animatedOnSurfaceVariant by animateColorAsState(
        targetValue = if (isDarkMode) TextSecondaryMuted else TextSecondaryDark,
        animationSpec = tween(durationMillis = 380),
        label = "theme_on_surface_variant_anim"
    )
    val animatedOutline by animateColorAsState(
        targetValue = if (isDarkMode) lerp(SubtleBorderSlate, targetAccent, 0.45f) else lerp(LightBorderSlate, targetAccent, 0.42f),
        animationSpec = tween(durationMillis = 380),
        label = "theme_outline_anim"
    )

    val customColorScheme = buildWonderingWorldColorScheme(
        accentColor = animatedPrimary,
        secondaryColor = animatedSecondary,
        isDarkMode = isDarkMode,
        surfaceAlpha = surfaceAlpha,
        background = animatedBackground,
        surface = animatedSurface,
        surfaceVariant = animatedSurfaceVariant,
        onBackground = animatedOnBackground,
        onSurface = animatedOnSurface,
        onSurfaceVariant = animatedOnSurfaceVariant,
        outline = animatedOutline
    )

    val colorScheme = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicBase = if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dynamicBase.copy(
            background = Color.Transparent,
            surface = dynamicBase.surface.copy(alpha = surfaceAlpha),
            surfaceVariant = dynamicBase.surfaceVariant.copy(alpha = (surfaceAlpha * 0.92f).coerceIn(0.0f, 1f))
        )
    } else {
        customColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

