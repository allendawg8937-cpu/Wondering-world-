package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.screens.InteractiveColorWheelPicker
import com.example.ui.theme.CURATED_3D_COLOR_PALETTES
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.findSecondaryAccentForHex
import com.example.ui.theme.parseThemeHex
import kotlin.math.roundToInt

data class UserCreatedCustomProfileTheme(
    val id: String,
    val themeName: String,
    val tagline: String,
    val creatorName: String,
    val primaryHex: String,
    val secondaryHex: String,
    val visualStyleId: String,
    val transparencyLevel: Float = 0.82f,
    val preferDarkMode: Boolean = true
)

val DEFAULT_USER_CREATED_PROFILE_THEMES = listOf(
    UserCreatedCustomProfileTheme(
        id = "custom_theme_1",
        themeName = "Crystal Unity Hypercube",
        tagline = "Clear glassmorphic 6D tesseract with electric cyan & neon rose",
        creatorName = "Community Creator",
        primaryHex = "#00F5D4",
        secondaryHex = "#EC4899",
        visualStyleId = "HYPERCUBE_6D",
        transparencyLevel = 0.84f,
        preferDarkMode = true
    ),
    UserCreatedCustomProfileTheme(
        id = "custom_theme_2",
        themeName = "Golden Horizon Synth",
        tagline = "Warm sunset synthwave rays with clear transparent cards",
        creatorName = "Veteran & Youth Studio",
        primaryHex = "#F59E0B",
        secondaryHex = "#8B5CF6",
        visualStyleId = "CYBER_SYNTHWAVE",
        transparencyLevel = 0.80f,
        preferDarkMode = true
    ),
    UserCreatedCustomProfileTheme(
        id = "custom_theme_3",
        themeName = "Emerald Starlight Aurora",
        tagline = "Peaceful aurora waves with mint & sky blue glow",
        creatorName = "Student STEM Lab",
        primaryHex = "#10B981",
        secondaryHex = "#38BDF8",
        visualStyleId = "AURORA_WAVE",
        transparencyLevel = 0.85f,
        preferDarkMode = true
    )
)

data class ThreeDProfileThemePreset(
    val id: String,
    val name: String,
    val badge: String,
    val description: String,
    val visualStyleId: String,
    val primaryHex: String,
    val secondaryHex: String,
    val defaultTransparency: Float,
    val preferDarkMode: Boolean = true
)

val THREE_D_PROFILE_THEME_PRESETS = listOf(
    ThreeDProfileThemePreset(
        id = "preset_hypercube_6d",
        name = "6D Hypercube Classic",
        badge = "6D TESSERACT",
        description = "Rotating 6D tesseract projection with royal blue & holographic cyan laser grids",
        visualStyleId = "HYPERCUBE_6D",
        primaryHex = "#2563EB",
        secondaryHex = "#06B6D4",
        defaultTransparency = 0.84f,
        preferDarkMode = true
    ),
    ThreeDProfileThemePreset(
        id = "preset_synthwave_sunset",
        name = "Synthwave Sunset 3D",
        badge = "RETRO SUNSET",
        description = "Outrun solar horizon with neon pink scanlines and golden amber atmospheric glow",
        visualStyleId = "CYBER_SYNTHWAVE",
        primaryHex = "#EC4899",
        secondaryHex = "#F59E0B",
        defaultTransparency = 0.80f,
        preferDarkMode = true
    ),
    ThreeDProfileThemePreset(
        id = "preset_galaxy_vortex",
        name = "Starlight Galaxy Vortex",
        badge = "COSMIC NEBULA",
        description = "4-arm swirling deep-space spiral nebula with violet & magenta starfields",
        visualStyleId = "GALAXY_VORTEX",
        primaryHex = "#8B5CF6",
        secondaryHex = "#EC4899",
        defaultTransparency = 0.86f,
        preferDarkMode = true
    ),
    ThreeDProfileThemePreset(
        id = "preset_crystal_prism",
        name = "Crystal Ocean Prism",
        badge = "SACRED PRISM",
        description = "Refractive diamond octahedron geometry with cool aqua cyan & emerald facets",
        visualStyleId = "CRYSTAL_PRISM",
        primaryHex = "#06B6D4",
        secondaryHex = "#10B981",
        defaultTransparency = 0.82f,
        preferDarkMode = true
    ),
    ThreeDProfileThemePreset(
        id = "preset_aurora_forest",
        name = "Bioluminescent Aurora",
        badge = "AURORA WAVE",
        description = "Flowing emerald & sky-cyan northern light ribbons across a calm 3D horizon",
        visualStyleId = "AURORA_WAVE",
        primaryHex = "#10B981",
        secondaryHex = "#38BDF8",
        defaultTransparency = 0.85f,
        preferDarkMode = true
    ),
    ThreeDProfileThemePreset(
        id = "preset_matrix_rings",
        name = "Quantum Orbital Rings",
        badge = "GYRO MATRIX",
        description = "Concentric 3D gyroscopic orbital rings and cybernetic lime data streams",
        visualStyleId = "MATRIX_CYBERNETIC",
        primaryHex = "#84CC16",
        secondaryHex = "#10B981",
        defaultTransparency = 0.78f,
        preferDarkMode = true
    )
)

/**
 * Dedicated Profile Settings UI Component allowing users to toggle between different
 * 3D Profile Theme Presets with live 3D spatial previews, quick-cycle controls, and cloud sync.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThreeDProfileThemePresetsToggleCard(
    currentVisualStyleId: String,
    currentThemeHex: String,
    isDarkMode: Boolean,
    backgroundTransparency: Float = 0.82f,
    onSelectVisualStyle: (String) -> Unit,
    onSelectThemeColor: (String) -> Unit,
    onTransparencyChange: ((Float) -> Unit)? = null,
    onSetDarkMode: ((Boolean) -> Unit)? = null,
    onPersistThemeToFirestore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val activePresetIndex = remember(currentVisualStyleId, currentThemeHex) {
        val exactIdx = THREE_D_PROFILE_THEME_PRESETS.indexOfFirst {
            it.visualStyleId == currentVisualStyleId &&
                it.primaryHex.equals(currentThemeHex, ignoreCase = true)
        }
        if (exactIdx >= 0) {
            exactIdx
        } else {
            THREE_D_PROFILE_THEME_PRESETS.indexOfFirst {
                it.visualStyleId == currentVisualStyleId
            }.coerceAtLeast(0)
        }
    }
    val activePreset = THREE_D_PROFILE_THEME_PRESETS[activePresetIndex]
    val activePrimaryColor = parseThemeHex(currentThemeHex)

    fun applyPreset(preset: ThreeDProfileThemePreset) {
        onSelectVisualStyle(preset.visualStyleId)
        onSelectThemeColor(preset.primaryHex)
        onTransparencyChange?.invoke(preset.defaultTransparency)
        onSetDarkMode?.invoke(preset.preferDarkMode)
        onPersistThemeToFirestore()
        statusBanner = "Activated '${preset.name}' (${preset.badge}) • Synced to Profile"
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, activePrimaryColor, NeonPink, CyberEmerald)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.1f)
            .testTag("three_d_profile_theme_presets_toggle_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row with Quick Preset Cycle Toggle Button
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
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                Brush.linearGradient(listOf(HoloCyan, activePrimaryColor, NeonPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "3D Profile Theme Presets",
                            tint = Color.Black,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "3D Profile Theme Presets Toggle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Active Preset ${activePresetIndex + 1}/${THREE_D_PROFILE_THEME_PRESETS.size}: ${activePreset.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        val nextIdx = (activePresetIndex + 1) % THREE_D_PROFILE_THEME_PRESETS.size
                        applyPreset(THREE_D_PROFILE_THEME_PRESETS[nextIdx])
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("cycle_next_3d_theme_preset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Cycle Next 3D Theme Preset",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Toggle Next Preset",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Quick Toggle Pill Strip (1-Tap Toggle Chips for All 6 Presets)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                THREE_D_PROFILE_THEME_PRESETS.forEachIndexed { idx, preset ->
                    val isSelected = idx == activePresetIndex
                    val presetColor = parseThemeHex(preset.primaryHex)
                    FilterChip(
                        selected = isSelected,
                        onClick = { applyPreset(preset) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(presetColor)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        },
                        label = {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("toggle_3d_preset_chip_${preset.id}")
                    )
                }
            }

            // 2x3 Interactive 3D Preset Live Preview Cards Grid
            THREE_D_PROFILE_THEME_PRESETS.chunked(2).forEach { rowPresets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPresets.forEach { preset ->
                        val isActive = preset.id == activePreset.id
                        val primaryColor = parseThemeHex(preset.primaryHex)
                        val secondaryColor = parseThemeHex(preset.secondaryHex)

                        Surface(
                            color = if (isActive) {
                                primaryColor.copy(alpha = 0.20f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            },
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                width = if (isActive) 2.2.dp else 1.dp,
                                brush = Brush.horizontalGradient(
                                    if (isActive) {
                                        listOf(primaryColor, secondaryColor, CyberEmerald)
                                    } else {
                                        listOf(primaryColor.copy(alpha = 0.45f), secondaryColor.copy(alpha = 0.45f))
                                    }
                                )
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { applyPreset(preset) }
                                .testTag("toggle_3d_preset_card_${preset.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Mini Live 3D Spatial Canvas Preview
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(68.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            primaryColor.copy(alpha = 0.6f),
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    ThreeDimensionalBackground(
                                        accentHex = preset.primaryHex,
                                        visualStyleId = preset.visualStyleId,
                                        isDarkMode = isDarkMode,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.68f),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(5.dp)
                                    ) {
                                        Text(
                                            text = preset.badge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = primaryColor,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Active / Toggle Status Indicator Pill
                                    Surface(
                                        color = if (isActive) CyberEmerald else Color.Black.copy(alpha = 0.65f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isActive) Color.White else primaryColor.copy(alpha = 0.7f)
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(5.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            if (isActive) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Active Preset",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                            Text(
                                                text = if (isActive) "ON" else "USE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isActive) Color.Black else Color.White,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clip(CircleShape)
                                                .background(primaryColor)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clip(CircleShape)
                                                .background(secondaryColor)
                                        )
                                    }
                                }

                                Text(
                                    text = preset.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Quick Lighting & Glass Clarity Row for Active Preset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusBanner
                        ?: "Tap any 3D preset card above to toggle geometry, color & glass clarity (${(backgroundTransparency * 100).roundToInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (statusBanner != null) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (statusBanner != null) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                if (onSetDarkMode != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isDarkMode,
                        onClick = { onSetDarkMode(!isDarkMode) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isDarkMode) "Dark 3D" else "Daylight 3D",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.testTag("preset_toggle_dark_mode_chip")
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThreeDThemeStyleSelectorMenuCard(
    currentVisualStyleId: String,
    currentThemeHex: String,
    isDarkMode: Boolean,
    onSelectVisualStyle: (String) -> Unit,
    onSelectThemeColor: (String) -> Unit,
    onPersistThemeToFirestore: () -> Unit = {},
    userDisplayName: String = "My Profile",
    backgroundTransparency: Float = 0.82f,
    onTransparencyChange: ((Float) -> Unit)? = null,
    onSetDarkMode: ((Boolean) -> Unit)? = null,
    onShareCustomThemeToWall: ((headline: String, body: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isColorGridExpanded by remember { mutableStateOf(true) }
    var isCustomThemeBuilderExpanded by remember { mutableStateOf(true) }
    var selectedColorCategory by remember { mutableStateOf("ALL") }
    var customHexInput by remember { mutableStateOf("") }
    var lastSavedBanner by remember { mutableStateOf<String?>(null) }

    val activeStyle = THREE_D_THEME_VISUAL_STYLES.firstOrNull { it.id == currentVisualStyleId }
        ?: THREE_D_THEME_VISUAL_STYLES.first()
    val activeAccentColor = parseThemeHex(currentThemeHex)
    val activeColorName = EXPANDED_THEME_COLOR_OPTIONS.firstOrNull {
        it.hex.equals(currentThemeHex, ignoreCase = true)
    }?.name ?: "Custom Hex ($currentThemeHex)"

    val filteredColors = remember(selectedColorCategory) {
        if (selectedColorCategory == "ALL") {
            EXPANDED_THEME_COLOR_OPTIONS
        } else {
            EXPANDED_THEME_COLOR_OPTIONS.filter { it.category == selectedColorCategory }
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(activeAccentColor, HoloCyan, NeonPink, CyberEmerald)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.15f)
            .testTag("three_d_theme_style_selector_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header & Firestore Sync Badge
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(activeAccentColor, HoloCyan, NeonPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "3D Profile Theme Visual Style Menu",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "WereEqual Theme Selection & Basic Colors",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                color = CyberEmerald.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "AUTO-SYNC",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Theme: ${activeStyle.title} • Color: $activeColorName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Live 3D Visual Style Preview Window + Interactive Dropdown Selection Menu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(activeAccentColor, HoloCyan, NeonPink)),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                ThreeDimensionalBackground(
                    accentHex = currentThemeHex,
                    visualStyleId = currentVisualStyleId,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SixDimensionalHdBadge(label = activeStyle.badge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeStyle.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = activeStyle.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) Color.White.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dropdown Selection Menu Trigger
                    Box {
                        Button(
                            onClick = { isDropdownExpanded = true },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activeAccentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("open_3d_style_dropdown_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Select Theme",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Icon(
                                imageVector = if (isDropdownExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            THREE_D_THEME_VISUAL_STYLES.forEach { option ->
                                val isSelected = option.id == currentVisualStyleId
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${if (isSelected) "✓ " else ""}${option.title} (${option.badge})",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = if (isSelected) parseThemeHex(option.recommendedHex) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = option.subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    onClick = {
                                        isDropdownExpanded = false
                                        onSelectVisualStyle(option.id)
                                        lastSavedBanner = "Persisted Theme '${option.title}' to Firestore"
                                    },
                                    modifier = Modifier.testTag("dropdown_3d_style_${option.id.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }

            // 3. Horizontal 1-Tap Basic Theme Selection Cards Carousel
            Text(
                text = "1. Basic Theme Selections (Tap Any Theme Card):",
                style = MaterialTheme.typography.labelMedium,
                color = HoloCyan,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                THREE_D_THEME_VISUAL_STYLES.forEach { styleOption ->
                    val isSelected = styleOption.id == currentVisualStyleId
                    val styleColor = parseThemeHex(styleOption.recommendedHex)

                    Surface(
                        color = if (isSelected) {
                            styleColor.copy(alpha = 0.20f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) styleColor else styleColor.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .width(195.dp)
                            .clickable {
                                onSelectVisualStyle(styleOption.id)
                                lastSavedBanner = "Saved '${styleOption.title}' to Firestore"
                            }
                            .testTag("visual_style_card_${styleOption.id.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = styleColor.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = styleOption.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = styleColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active Theme",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = styleOption.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = styleOption.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            OutlinedButton(
                                onClick = {
                                    onSelectVisualStyle(styleOption.id)
                                    onSelectThemeColor(styleOption.recommendedHex)
                                    lastSavedBanner = "Synced '${styleOption.title}' + ${styleOption.recommendedHex} to Firestore"
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, styleColor.copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .testTag("apply_style_and_color_${styleOption.id.lowercase()}")
                            ) {
                                Text(
                                    text = if (isSelected) "Active Theme" else "Theme + Match Color",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = styleColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3B. Curated 3D Multi-Swatch Color Palettes (1-Tap Palette + 3D Geometry Sync to Firestore)
            Text(
                text = "2. Curated 3D Color Palettes (Tap Palette to Sync & Transform Profile UI):",
                style = MaterialTheme.typography.labelMedium,
                color = CyberEmerald,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CURATED_3D_COLOR_PALETTES.forEach { palette ->
                    val isPaletteActive = currentThemeHex.equals(palette.primaryHex, ignoreCase = true) &&
                        currentVisualStyleId == palette.visualStyleId
                    val primarySwatch = parseThemeHex(palette.primaryHex)

                    Surface(
                        color = if (isPaletteActive) {
                            primarySwatch.copy(alpha = 0.22f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                        },
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            width = if (isPaletteActive) 2.2.dp else 1.dp,
                            brush = Brush.horizontalGradient(palette.swatchColors)
                        ),
                        modifier = Modifier
                            .width(215.dp)
                            .clickable {
                                onSelectThemeColor(palette.primaryHex)
                                onSelectVisualStyle(palette.visualStyleId)
                                lastSavedBanner = "Synced 3D Palette '${palette.name}' (${palette.primaryHex}) to Firestore!"
                            }
                            .testTag("curated_3d_palette_${palette.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top row: Badge + Active Check
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = primarySwatch.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = palette.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = primarySwatch,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                if (isPaletteActive) {
                                    Surface(
                                        color = CyberEmerald,
                                        shape = CircleShape,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active 3D Palette",
                                                tint = Color.Black,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 4-Color Swatch Preview Bar with 3D Gradient Pill
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.28f),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                palette.swatchColors.forEach { swatchColor ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .background(swatchColor)
                                    )
                                }
                            }

                            Text(
                                text = palette.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = palette.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Active Dynamic Profile Palette Token Strip
            val secondaryAccent = findSecondaryAccentForHex(currentThemeHex)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, activeAccentColor.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_3d_palette_harmonic_strip")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Profile UI Palette Harmonics",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = activeAccentColor
                        )
                        Text(
                            text = "Primary $currentThemeHex • 3D Style: ${activeStyle.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            activeAccentColor,
                            secondaryAccent,
                            CyberEmerald,
                            MaterialTheme.colorScheme.surface
                        ).forEach { tokenColor ->
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(tokenColor)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                        }
                    }
                }
            }

            // 4. Basic Color & Expanded 32-Color Selection Studio + Category Filter + Custom Hex Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = activeAccentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "3. Individual 32-Color Swatches ($activeColorName)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isColorGridExpanded) "Hide Colors ▲" else "Show All Colors ▼",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoloCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isColorGridExpanded = !isColorGridExpanded }
                )
            }

            AnimatedVisibility(visible = isColorGridExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Color Spectrum Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "🌈 All 32 Colors",
                            "Basic Colors" to "🎨 Basic Colors (12)",
                            "Neon & Cyber" to "⚡ Neon & Cyber",
                            "Cosmic & Royal" to "🌌 Cosmic & Royal",
                            "Ocean & Mint" to "🌊 Ocean & Mint",
                            "Solar & Warm" to "☀️ Solar & Warm",
                            "Luxury & Gold" to "👑 Luxury & Gold"
                        ).forEach { (catKey, catLabel) ->
                            FilterChip(
                                selected = selectedColorCategory == catKey,
                                onClick = { selectedColorCategory = catKey },
                                label = {
                                    Text(
                                        text = catLabel,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.testTag("color_cat_${catKey.lowercase().replace(" ", "_")}")
                            )
                        }
                    }

                    // 18-Color Swatch Grid
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredColors.forEach { option ->
                            val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                            Surface(
                                color = if (isSelected) {
                                    option.color.copy(alpha = 0.24f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) option.color else option.color.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clickable {
                                        onSelectThemeColor(option.hex)
                                        lastSavedBanner = "Saved '${option.name}' (${option.hex}) to Firestore"
                                    }
                                    .testTag("expanded_color_swatch_${option.hex.removePrefix("#").lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(option.color)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color.White else Color.Transparent,
                                                shape = CircleShape
                                            )
                                    )
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Custom #HEX Color Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customHexInput,
                            onValueChange = { customHexInput = it.take(7) },
                            label = { Text("Custom #RRGGBB Hex Color (e.g. #00F5D4)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_hex_color_input")
                        )
                        Button(
                            onClick = {
                                val normalized = if (customHexInput.startsWith("#")) {
                                    customHexInput.trim().uppercase()
                                } else {
                                    "#${customHexInput.trim().uppercase()}"
                                }
                                if (normalized.length == 7) {
                                    onSelectThemeColor(normalized)
                                    lastSavedBanner = "Saved Custom Color $normalized to Firestore"
                                    customHexInput = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activeAccentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("apply_custom_hex_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apply Hex", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4B. CREATE YOUR OWN CUSTOM PROFILE THEME STUDIO (Available to All Users on Their Profile Page)
            CustomProfileThemeCreatorSection(
                userDisplayName = userDisplayName,
                currentVisualStyleId = currentVisualStyleId,
                currentThemeHex = currentThemeHex,
                isDarkMode = isDarkMode,
                backgroundTransparency = backgroundTransparency,
                isExpanded = isCustomThemeBuilderExpanded,
                onToggleExpanded = { isCustomThemeBuilderExpanded = !isCustomThemeBuilderExpanded },
                onSelectVisualStyle = onSelectVisualStyle,
                onSelectThemeColor = onSelectThemeColor,
                onTransparencyChange = onTransparencyChange,
                onSetDarkMode = onSetDarkMode,
                onPersistThemeToFirestore = onPersistThemeToFirestore,
                onShareCustomThemeToWall = onShareCustomThemeToWall,
                onStatusMessage = { msg -> lastSavedBanner = msg }
            )

            // 5. Explicit Save / Persist to Firestore Confirmation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lastSavedBanner ?: "☁️ Theme style & color automatically sync to Firestore",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (lastSavedBanner != null) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (lastSavedBanner != null) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        onPersistThemeToFirestore()
                        lastSavedBanner = "✓ Saved '${activeStyle.title}' & $currentThemeHex to Firestore!"
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("persist_3d_theme_firestore_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Save Theme to Cloud",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

/**
 * Interactive "Create Your Own Custom Profile Theme" Studio embedded on every user's Profile Page.
 * Lets any user design, name, preview in 3D, adjust transparency, save to their Custom Themes Gallery,
 * sync to Firestore, and share their custom profile theme to the Community Wall.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomProfileThemeCreatorSection(
    userDisplayName: String,
    currentVisualStyleId: String,
    currentThemeHex: String,
    isDarkMode: Boolean,
    backgroundTransparency: Float = 0.82f,
    isExpanded: Boolean = true,
    onToggleExpanded: () -> Unit = {},
    onSelectVisualStyle: (String) -> Unit,
    onSelectThemeColor: (String) -> Unit,
    onTransparencyChange: ((Float) -> Unit)? = null,
    onSetDarkMode: ((Boolean) -> Unit)? = null,
    onPersistThemeToFirestore: () -> Unit = {},
    onShareCustomThemeToWall: ((headline: String, body: String) -> Unit)? = null,
    onStatusMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val firstName = userDisplayName.substringBefore(" ").ifBlank { "Explorer" }
    var customThemeName by remember(firstName) { mutableStateOf("$firstName's Custom 3D Theme") }
    var customThemeTagline by remember {
        mutableStateOf("Crystal-clear transparent 3D profile theme • We are all equal")
    }
    var customPrimaryHex by remember(currentThemeHex) { mutableStateOf(currentThemeHex) }
    var customSecondaryHex by remember { mutableStateOf("#00F5D4") }
    var customVisualStyleId by remember(currentVisualStyleId) { mutableStateOf(currentVisualStyleId) }
    var localTransparency by remember(backgroundTransparency) { mutableFloatStateOf(backgroundTransparency) }
    var showEmbeddedColorWheel by remember { mutableStateOf(false) }

    val userCreatedThemes = remember {
        mutableStateListOf<UserCreatedCustomProfileTheme>().apply {
            addAll(DEFAULT_USER_CREATED_PROFILE_THEMES)
        }
    }

    val previewAccent = parseThemeHex(customPrimaryHex)
    val previewSecondary = parseThemeHex(customSecondaryHex)
    val selectedStyleObj = THREE_D_THEME_VISUAL_STYLES.firstOrNull { it.id == customVisualStyleId }
        ?: THREE_D_THEME_VISUAL_STYLES.first()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, previewAccent, NeonPink, CyberEmerald)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("create_your_own_profile_theme_studio")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() },
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(HoloCyan, previewAccent, NeonPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Create Your Own Profile Theme",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "4. ✨ Create Your Own Custom Profile Theme",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "All users can design, name, preview & save their own custom 3D profile theme",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan
                        )
                    }
                }

                Surface(
                    color = HoloCyan.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, HoloCyan),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isExpanded) "Creator Open ▲" else "Create Theme ▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Live Custom Theme 3D Preview Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.5.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(previewAccent, previewSecondary, HoloCyan)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        ThreeDimensionalBackground(
                            accentHex = customPrimaryHex,
                            visualStyleId = customVisualStyleId,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = previewAccent.copy(alpha = 0.26f),
                                    border = BorderStroke(1.dp, previewAccent),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "✨ CUSTOM PROFILE THEME PREVIEW • ${selectedStyleObj.badge}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "$customPrimaryHex • ${(localTransparency * 100).roundToInt()}% Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column {
                                Text(
                                    text = customThemeName.ifBlank { "$firstName's Custom 3D Theme" },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = customThemeTagline.ifBlank { "Custom user-created 3D theme on WereEqual" },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.88f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Step A: Name Your Custom Profile Theme & Tagline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customThemeName,
                            onValueChange = { customThemeName = it },
                            label = { Text("Your Custom Theme Name") },
                            placeholder = { Text("e.g., Nebula Starlight 6D") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_profile_theme_name_input")
                        )
                        OutlinedTextField(
                            value = customThemeTagline,
                            onValueChange = { customThemeTagline = it },
                            label = { Text("Theme Vibe / Tagline") },
                            placeholder = { Text("e.g., Clear glass & neon cyan") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_profile_theme_tagline_input")
                        )
                    }

                    // Step B: Choose 3D / 6D Spatial Geometry Style for Your Custom Theme
                    Text(
                        text = "Step 1: Choose Your Custom Theme's 3D / 6D Geometry Engine:",
                        style = MaterialTheme.typography.labelMedium,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        THREE_D_THEME_VISUAL_STYLES.forEach { styleOpt ->
                            val selected = customVisualStyleId == styleOpt.id
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    customVisualStyleId = styleOpt.id
                                    onSelectVisualStyle(styleOpt.id)
                                },
                                label = {
                                    Text(
                                        text = "◈ ${styleOpt.title}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("custom_theme_geometry_${styleOpt.id.lowercase()}")
                            )
                        }
                    }

                    // Step C: Choose Primary & Secondary Colors + Toggle Real 360° Color Wheel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step 2: Pick Primary ($customPrimaryHex) & Secondary ($customSecondaryHex) Colors:",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { showEmbeddedColorWheel = !showEmbeddedColorWheel },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, NeonPink),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("toggle_custom_theme_color_wheel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showEmbeddedColorWheel) "Hide 360° Wheel" else "🎡 Open 360° Color Wheel",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPink,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    AnimatedVisibility(visible = showEmbeddedColorWheel) {
                        InteractiveColorWheelPicker(
                            currentHex = customPrimaryHex,
                            onColorSelected = { newHex ->
                                customPrimaryHex = newHex
                                onSelectThemeColor(newHex)
                            },
                            wheelSize = 200.dp
                        )
                    }

                    // Quick Primary & Secondary Swatch Strips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Primary:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        EXPANDED_THEME_COLOR_OPTIONS.take(16).forEach { opt ->
                            val isSelected = customPrimaryHex.equals(opt.hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        customPrimaryHex = opt.hex
                                        onSelectThemeColor(opt.hex)
                                    }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Secondary Glow:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        EXPANDED_THEME_COLOR_OPTIONS.reversed().take(16).forEach { opt ->
                            val isSelected = customSecondaryHex.equals(opt.hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        customSecondaryHex = opt.hex
                                    }
                            )
                        }
                    }

                    // Step D: Adjust Custom Theme Background Transparency & Light/Dark Mode so Theme Is Crystal Clear
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step 3: Theme Background Transparency (${(localTransparency * 100).roundToInt()}% Clear):",
                                style = MaterialTheme.typography.labelMedium,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                            if (onSetDarkMode != null) {
                                Surface(
                                    color = if (isDarkMode) HoloCyan.copy(alpha = 0.2f) else SolarAmber.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, if (isDarkMode) HoloCyan else SolarAmber),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.clickable { onSetDarkMode(!isDarkMode) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = null,
                                            tint = if (isDarkMode) HoloCyan else SolarAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isDarkMode) "Dark 3D" else "Light 3D",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDarkMode) HoloCyan else SolarAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Slider(
                            value = localTransparency,
                            onValueChange = { value ->
                                val coerced = value.coerceIn(0f, 1f)
                                localTransparency = coerced
                                onTransparencyChange?.invoke(coerced)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = HoloCyan,
                                activeTrackColor = HoloCyan
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_theme_transparency_slider")
                        )
                    }

                    // Step E: Save & Activate Custom Profile Theme + Share to Community Wall
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val cleanName = customThemeName.trim().ifBlank { "$firstName's Custom 3D Theme" }
                                val cleanTagline = customThemeTagline.trim().ifBlank {
                                    "Custom 3D profile theme with $customPrimaryHex & ${selectedStyleObj.title}"
                                }
                                val created = UserCreatedCustomProfileTheme(
                                    id = "user_theme_${System.currentTimeMillis()}",
                                    themeName = cleanName,
                                    tagline = cleanTagline,
                                    creatorName = userDisplayName.ifBlank { "Community Member" },
                                    primaryHex = customPrimaryHex,
                                    secondaryHex = customSecondaryHex,
                                    visualStyleId = customVisualStyleId,
                                    transparencyLevel = localTransparency,
                                    preferDarkMode = isDarkMode
                                )
                                userCreatedThemes.add(0, created)
                                onSelectThemeColor(customPrimaryHex)
                                onSelectVisualStyle(customVisualStyleId)
                                onTransparencyChange?.invoke(localTransparency)
                                onPersistThemeToFirestore()
                                onStatusMessage("✓ Created & Activated Custom Profile Theme '$cleanName' ($customPrimaryHex)!")
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_and_activate_custom_profile_theme_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Create & Apply My Theme",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        if (onShareCustomThemeToWall != null) {
                            OutlinedButton(
                                onClick = {
                                    val cleanName = customThemeName.trim().ifBlank { "$firstName's Custom 3D Theme" }
                                    onShareCustomThemeToWall(
                                        "🎨 Created Custom Profile Theme: $cleanName ($customPrimaryHex)",
                                        "${customThemeTagline.ifBlank { "Designed my own 3D Profile Theme!" }} • Style: ${selectedStyleObj.title} • Primary: $customPrimaryHex • Secondary: $customSecondaryHex"
                                    )
                                    onStatusMessage("✓ Shared '$cleanName' to the Community Wall!")
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.2.dp, CyberEmerald),
                                modifier = Modifier.testTag("share_custom_profile_theme_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Share Theme",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Saved Custom Profile Themes Gallery (Tap Any User-Created Theme to Apply Instantly)
                    Text(
                        text = "Your Saved & Community Custom Profile Themes (${userCreatedThemes.size}) — Tap to Apply:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        userCreatedThemes.forEach { item ->
                            val isCurrent = currentThemeHex.equals(item.primaryHex, ignoreCase = true) &&
                                currentVisualStyleId == item.visualStyleId
                            val primaryC = parseThemeHex(item.primaryHex)
                            val secondaryC = parseThemeHex(item.secondaryHex)

                            Surface(
                                color = if (isCurrent) primaryC.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    brush = Brush.horizontalGradient(listOf(primaryC, secondaryC, HoloCyan))
                                ),
                                modifier = Modifier
                                    .width(210.dp)
                                    .clickable {
                                        customThemeName = item.themeName
                                        customThemeTagline = item.tagline
                                        customPrimaryHex = item.primaryHex
                                        customSecondaryHex = item.secondaryHex
                                        customVisualStyleId = item.visualStyleId
                                        localTransparency = item.transparencyLevel
                                        onSelectThemeColor(item.primaryHex)
                                        onSelectVisualStyle(item.visualStyleId)
                                        onTransparencyChange?.invoke(item.transparencyLevel)
                                        onPersistThemeToFirestore()
                                        onStatusMessage("✓ Applied Custom Theme '${item.themeName}'!")
                                    }
                                    .testTag("saved_custom_profile_theme_${item.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryC)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(secondaryC)
                                            )
                                        }
                                        Text(
                                            text = "By ${item.creatorName.take(14)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HoloCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = item.themeName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.tagline,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

