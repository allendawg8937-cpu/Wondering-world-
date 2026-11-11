package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber

/**
 * Compact system-wide Material 3 Dark Mode, Late-Night Eye Comfort & Solid Color Theme Bar
 * displayed at the top of the Stages Of Life platform across all screens.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemWideDarkModeHeaderBar(
    isDarkMode: Boolean,
    followSystemTheme: Boolean,
    isLateNightEyeComfort: Boolean,
    nightDimmerAlpha: Float,
    useDynamicColor: Boolean,
    currentThemeHex: String = "#2563EB",
    onSelectThemeHex: (String) -> Unit = {},
    backgroundTransparency: Float = 0.0f,
    onTransparencyChange: (Float) -> Unit = {},
    onSetDarkMode: (Boolean) -> Unit,
    onSetFollowSystemTheme: (Boolean) -> Unit,
    onSetLateNightEyeComfort: (Boolean) -> Unit,
    onSetNightDimmerAlpha: (Float) -> Unit,
    onSetUseDynamicColor: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val systemDark = isSystemInDarkTheme()
    var isExpanded by remember { mutableStateOf(false) }
    val solidColors = remember {
        EXPANDED_THEME_COLOR_OPTIONS.filter { it.category == "Solid Colors" }
    }

    val headerAlpha = (0.96f * (1f - backgroundTransparency.coerceIn(0f, 1f))).coerceIn(0.0f, 0.96f)
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = headerAlpha),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("system_wide_dark_mode_header_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDarkMode) HoloCyan.copy(alpha = 0.2f) else SolarAmber.copy(alpha = 0.22f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Stages Of Life System-Wide Theme Mode",
                            tint = if (isDarkMode) HoloCyan else SolarAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isDarkMode) {
                                if (isLateNightEyeComfort) "🌙 Stages Of Life • Late-Night Dark Mode" else "🌙 Stages Of Life • Solid Dark Mode"
                            } else {
                                "☀️ Stages Of Life • Solid Light Mode"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap any solid color below (White, Black, Red, Yellow, Orange, Green, Blue, Gray)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (isLateNightEyeComfort) SolarAmber.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            1.dp,
                            if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .clickable {
                                val next = !isLateNightEyeComfort
                                onSetLateNightEyeComfort(next)
                                if (next && !isDarkMode) {
                                    onSetFollowSystemTheme(false)
                                    onSetDarkMode(true)
                                }
                            }
                            .testTag("header_late_night_comfort_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightlightRound,
                                contentDescription = "Late-Night Eye Comfort",
                                tint = if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isLateNightEyeComfort) "Night Care ON" else "Night Care",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { isExpanded = !isExpanded }
                            .testTag("header_dark_mode_expand_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Expand Theme Options",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            onSetFollowSystemTheme(false)
                            onSetDarkMode(enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = HoloCyan,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = SolarAmber
                        ),
                        modifier = Modifier.testTag("system_wide_dark_mode_switch")
                    )
                }
            }

            // Always-Visible 1-Tap Solid Color Swatches Row (White, Black, Red, Yellow, Orange, Green, Blue, Gray, Purple, Brown, etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                solidColors.forEach { option ->
                    val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                    val isLightSwatch = option.hex.equals("#FFFFFF", ignoreCase = true) ||
                        option.hex.equals("#EAB308", ignoreCase = true) ||
                        option.hex.equals("#9CA3AF", ignoreCase = true)
                    Surface(
                        color = option.color,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = if (isSelected) 2.2.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .clickable {
                                onSelectThemeHex(option.hex)
                                if (option.hex.equals("#FFFFFF", ignoreCase = true)) {
                                    onSetFollowSystemTheme(false)
                                    onSetDarkMode(false)
                                } else if (option.hex.equals("#111827", ignoreCase = true)) {
                                    onSetFollowSystemTheme(false)
                                    onSetDarkMode(true)
                                }
                            }
                            .testTag("header_solid_color_${option.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isLightSwatch) Color.Black else Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = option.name.removePrefix("Solid "),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLightSwatch) Color.Black else Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Always-Visible Option to Make the Entire Background & All Card Backgrounds Transparent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Backgrounds:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val transparencyPresets = listOf(
                    "100% Transparent (All Clear)" to 1.0f,
                    "85% Clear Glass" to 0.85f,
                    "50% Half Transparent" to 0.50f,
                    "0% Solid Color" to 0.0f
                )
                transparencyPresets.forEach { (label, value) ->
                    val isSelected = kotlin.math.abs(backgroundTransparency - value) < 0.08f
                    Surface(
                        color = if (isSelected) CyberEmerald.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = if (isSelected) 1.6.dp else 1.dp,
                            color = if (isSelected) CyberEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .clickable { onTransparencyChange(value) }
                            .testTag("header_bg_transparency_${(value * 100).toInt()}")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) CyberEmerald else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = isDarkMode && !followSystemTheme,
                            onClick = {
                                onSetFollowSystemTheme(false)
                                onSetDarkMode(true)
                            },
                            label = { Text("🌙 Dark Mode", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_dark_chip")
                        )
                        FilterChip(
                            selected = !isDarkMode && !followSystemTheme,
                            onClick = {
                                onSetFollowSystemTheme(false)
                                onSetDarkMode(false)
                                onSetLateNightEyeComfort(false)
                            },
                            label = { Text("☀️ Light Mode", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_light_chip")
                        )
                        FilterChip(
                            selected = followSystemTheme,
                            onClick = {
                                val nextFollow = !followSystemTheme
                                onSetFollowSystemTheme(nextFollow)
                                if (nextFollow) {
                                    onSetDarkMode(systemDark)
                                }
                            },
                            label = { Text("📱 Follow OS", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_system_chip")
                        )
                        FilterChip(
                            selected = useDynamicColor,
                            onClick = { onSetUseDynamicColor(!useDynamicColor) },
                            label = { Text("🎨 M3 Dynamic Color", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_dynamic_color_chip")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Late-Night Warm Dimmer (${(nightDimmerAlpha * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = nightDimmerAlpha,
                            onValueChange = {
                                onSetNightDimmerAlpha(it)
                                if (it > 0.05f && !isLateNightEyeComfort) {
                                    onSetLateNightEyeComfort(true)
                                }
                            },
                            valueRange = 0f..0.55f,
                            colors = SliderDefaults.colors(
                                thumbColor = SolarAmber,
                                activeTrackColor = SolarAmber
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("header_night_dimmer_slider")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full Material 3 System-Wide Dark Mode & Solid Color Theme Configuration Card
 * for the Stages Of Life Dashboard and Profile Studio.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemWideDarkModeControlCard(
    isDarkMode: Boolean,
    onSetDarkMode: (Boolean) -> Unit,
    backgroundTransparency: Float = 0.15f,
    onTransparencyChange: (Float) -> Unit = {},
    currentThemeHex: String = "#2563EB",
    onSelectThemeColor: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val systemDark = isSystemInDarkTheme()
    var followSystem by remember { mutableStateOf(false) }
    var lateNightComfort by remember { mutableStateOf(true) }
    val solidColors = remember {
        EXPANDED_THEME_COLOR_OPTIONS.filter { it.category == "Solid Colors" }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_wide_dark_mode_control_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Stages Of Life Solid Color & Dark Mode",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Stages Of Life • Solid Color & Dark Mode Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Solid White, Black, Red, Yellow, Orange, Green, Blue, Gray & Material 3 Dark Mode",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { enabled ->
                        followSystem = false
                        onSetDarkMode(enabled)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyberEmerald,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SolarAmber
                    ),
                    modifier = Modifier.testTag("card_system_dark_mode_switch")
                )
            }

            // Solid Color Palette Grid
            Text(
                text = "Select Solid Platform Color (White • Black • Red • Yellow • Orange • Green • Blue • Gray & More)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                solidColors.forEach { option ->
                    val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                    val isLightSwatch = option.hex.equals("#FFFFFF", ignoreCase = true) ||
                        option.hex.equals("#EAB308", ignoreCase = true) ||
                        option.hex.equals("#9CA3AF", ignoreCase = true)
                    Surface(
                        color = option.color,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clickable {
                                onSelectThemeColor(option.hex)
                                if (option.hex.equals("#FFFFFF", ignoreCase = true)) {
                                    onSetDarkMode(false)
                                } else if (option.hex.equals("#111827", ignoreCase = true)) {
                                    onSetDarkMode(true)
                                }
                            }
                            .testTag("card_solid_color_${option.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isLightSwatch) Color.Black else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLightSwatch) Color.Black else Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Preset Mode Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isDarkMode && !followSystem,
                    onClick = {
                        followSystem = false
                        onSetDarkMode(true)
                    },
                    label = {
                        Text(
                            text = "🌙 Late-Night Dark (M3)",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                        selectedLabelColor = HoloCyan
                    ),
                    modifier = Modifier.testTag("card_dark_mode_preset_dark")
                )

                FilterChip(
                    selected = !isDarkMode && !followSystem,
                    onClick = {
                        followSystem = false
                        onSetDarkMode(false)
                    },
                    label = {
                        Text(
                            text = "☀️ Daylight Light (M3)",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SolarAmber.copy(alpha = 0.22f),
                        selectedLabelColor = SolarAmber
                    ),
                    modifier = Modifier.testTag("card_dark_mode_preset_light")
                )

                FilterChip(
                    selected = followSystem,
                    onClick = {
                        followSystem = true
                        onSetDarkMode(systemDark)
                    },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrightnessAuto,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Follow System (${if (systemDark) "Dark" else "Light"})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = CyberEmerald
                    ),
                    modifier = Modifier.testTag("card_dark_mode_preset_system")
                )
            }
        }
    }
}
