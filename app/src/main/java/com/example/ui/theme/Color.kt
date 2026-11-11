package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Base Dark Mode Material 3 Palette
val DeepSpaceBg = Color(0xFF070B19)
val DeepSlateSurface = Color(0xFF0F172A)
val ElevatedCardBg = Color(0xFF1E293B)
val DarkSurfaceContainerLowest = Color(0xFF050814)
val DarkSurfaceContainerLow = Color(0xFF0B1120)
val DarkSurfaceContainer = Color(0xFF131C31)
val DarkSurfaceContainerHigh = Color(0xFF1E293B)
val DarkSurfaceContainerHighest = Color(0xFF283548)
val SubtleBorderSlate = Color(0xFF334155)
val SubtleOutlineVariantDark = Color(0xFF1E293B)
val TextPrimaryLight = Color(0xFFF8FAFC)
val TextSecondaryMuted = Color(0xFF94A3B8)

// Base Light Mode Material 3 Palette
val LightSpaceBg = Color(0xFFF8FAFC)
val LightSlateSurface = Color(0xFFFFFFFF)
val LightElevatedCardBg = Color(0xFFE2E8F0)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF1F5F9)
val LightSurfaceContainer = Color(0xFFE8EEF5)
val LightSurfaceContainerHigh = Color(0xFFE2E8F0)
val LightSurfaceContainerHighest = Color(0xFFCBD5E1)
val LightBorderSlate = Color(0xFFCBD5E1)
val LightOutlineVariant = Color(0xFFE2E8F0)
val TextPrimaryDark = Color(0xFF0F172A)
val TextSecondaryDark = Color(0xFF475569)

// Core & Expanded 32-Color FHD/HD Dynamic Theme Accents
val ElectricIndigo = Color(0xFF6366F1)
val NeonPink = Color(0xFFEC4899)
val CyberEmerald = Color(0xFF10B981)
val SolarAmber = Color(0xFFF59E0B)
val HoloCyan = Color(0xFF06B6D4)
val VioletNebula = Color(0xFF8B5CF6)
val RosePulse = Color(0xFFF43F5E)
val HyperCobalt = Color(0xFF3B82F6)
val QuantumTeal = Color(0xFF14B8A6)
val MatrixLime = Color(0xFF84CC16)
val SupernovaGold = Color(0xFFEAB308)
val PlasmaOrange = Color(0xFFF97316)
val CrimsonLaser = Color(0xFFEF4444)
val OrchidSynth = Color(0xFFD946EF)
val CosmicAmethyst = Color(0xFFA855F7)
val ArcticSky = Color(0xFF0EA5E9)
val BioMint = Color(0xFF22C55E)
val SunsetFlamingo = Color(0xFFFB7185)

// Additional 14 Ultra-HD Designer Colors (Total 32 Colors)
val ElectricAqua = Color(0xFF00F5D4)
val RoyalChampagneGold = Color(0xFFFFD166)
val ImperialRuby = Color(0xFFE11D48)
val LaserViolet = Color(0xFF7C3AED)
val MidnightSapphire = Color(0xFF2563EB)
val LagoonTurquoise = Color(0xFF2DD4BF)
val JadeAurora = Color(0xFF34D399)
val PeachHorizon = Color(0xFFFB923C)
val RoseGoldBlush = Color(0xFFF472B6)
val StarlightPeriwinkle = Color(0xFF818CF8)
val AndromedaMagenta = Color(0xFFC026D3)
val NeonChartreuse = Color(0xFFA3E635)
val GlacierIce = Color(0xFF38BDF8)
val TangerineFlare = Color(0xFFFF6B35)

// Solid Classic Colors
val PureSolidWhite = Color(0xFFFFFFFF)
val PureSolidBlack = Color(0xFF111827)
val SolidCrimsonRed = Color(0xFFDC2626)
val SolidBrightYellow = Color(0xFFEAB308)
val SolidVibrantOrange = Color(0xFFF97316)
val SolidKellyGreen = Color(0xFF16A34A)
val SolidRoyalBlue = Color(0xFF2563EB)
val SolidNeutralGray = Color(0xFF6B7280)
val SolidCharcoalGray = Color(0xFF374151)
val SolidSilverGray = Color(0xFF9CA3AF)
val SolidRoyalPurple = Color(0xFF7C3AED)
val SolidDeepNavy = Color(0xFF1E3A8A)
val SolidEarthBrown = Color(0xFF92400E)
val SolidClassicMaroon = Color(0xFF991B1B)
val SolidForestGreen = Color(0xFF15803D)
val SolidClassicTeal = Color(0xFF0D9488)
val SolidWarmGold = Color(0xFFCA8A04)
val SolidClassicPink = Color(0xFFDB2777)

data class ThemeColorOption(
    val hex: String,
    val name: String,
    val category: String,
    val color: Color,
    val secondaryColor: Color = SolidRoyalBlue
)

val EXPANDED_THEME_COLOR_OPTIONS = listOf(
    // 1. Solid Classic Colors (White, Black, Red, Yellow, Orange, Green, Blue, Gray & More)
    ThemeColorOption("#FFFFFF", "Solid White", "Solid Colors", PureSolidWhite, SolidRoyalBlue),
    ThemeColorOption("#111827", "Solid Black", "Solid Colors", PureSolidBlack, SolidNeutralGray),
    ThemeColorOption("#DC2626", "Solid Red", "Solid Colors", SolidCrimsonRed, SolidBrightYellow),
    ThemeColorOption("#EAB308", "Solid Yellow", "Solid Colors", SolidBrightYellow, SolidVibrantOrange),
    ThemeColorOption("#F97316", "Solid Orange", "Solid Colors", SolidVibrantOrange, SolidBrightYellow),
    ThemeColorOption("#16A34A", "Solid Green", "Solid Colors", SolidKellyGreen, SolidClassicTeal),
    ThemeColorOption("#2563EB", "Solid Blue", "Solid Colors", SolidRoyalBlue, ArcticSky),
    ThemeColorOption("#6B7280", "Solid Gray", "Solid Colors", SolidNeutralGray, SolidSilverGray),
    ThemeColorOption("#374151", "Charcoal Gray", "Solid Colors", SolidCharcoalGray, SolidNeutralGray),
    ThemeColorOption("#9CA3AF", "Silver Gray", "Solid Colors", SolidSilverGray, SolidNeutralGray),
    ThemeColorOption("#7C3AED", "Solid Purple", "Solid Colors", SolidRoyalPurple, VioletNebula),
    ThemeColorOption("#1E3A8A", "Solid Navy", "Solid Colors", SolidDeepNavy, SolidRoyalBlue),
    ThemeColorOption("#92400E", "Solid Brown", "Solid Colors", SolidEarthBrown, SolidWarmGold),
    ThemeColorOption("#991B1B", "Solid Maroon", "Solid Colors", SolidClassicMaroon, SolidCrimsonRed),
    ThemeColorOption("#15803D", "Forest Green", "Solid Colors", SolidForestGreen, SolidKellyGreen),
    ThemeColorOption("#0D9488", "Solid Teal", "Solid Colors", SolidClassicTeal, SolidKellyGreen),
    ThemeColorOption("#CA8A04", "Solid Gold", "Solid Colors", SolidWarmGold, SolidBrightYellow),
    ThemeColorOption("#DB2777", "Solid Pink", "Solid Colors", SolidClassicPink, RosePulse),

    // 2. Basic Colors (Additional Classic Shades)
    ThemeColorOption("#EF4444", "Crimson Red", "Basic Colors", CrimsonLaser, SupernovaGold),
    ThemeColorOption("#10B981", "Emerald Green", "Basic Colors", CyberEmerald, HoloCyan),
    ThemeColorOption("#8B5CF6", "Classic Purple", "Basic Colors", VioletNebula, NeonPink),
    ThemeColorOption("#06B6D4", "Sky Cyan", "Basic Colors", HoloCyan, NeonPink),
    ThemeColorOption("#EC4899", "Hot Pink", "Basic Colors", NeonPink, ElectricAqua),
    ThemeColorOption("#14B8A6", "Teal Mint", "Basic Colors", QuantumTeal, SupernovaGold),
    ThemeColorOption("#6366F1", "Navy Indigo", "Basic Colors", ElectricIndigo, HoloCyan),
    ThemeColorOption("#F43F5E", "Coral Rose", "Basic Colors", RosePulse, HoloCyan),
    ThemeColorOption("#84CC16", "Lime Green", "Basic Colors", MatrixLime, HoloCyan),

    // 3. Ocean & Sky
    ThemeColorOption("#0EA5E9", "Arctic Sky", "Ocean & Mint", ArcticSky, CyberEmerald),
    ThemeColorOption("#3B82F6", "Cobalt Blue", "Ocean & Mint", HyperCobalt, ElectricAqua),
    ThemeColorOption("#22C55E", "Meadow Green", "Ocean & Mint", BioMint, HoloCyan),
    ThemeColorOption("#2DD4BF", "Lagoon Turquoise", "Ocean & Mint", LagoonTurquoise, VioletNebula),
    ThemeColorOption("#34D399", "Jade Green", "Ocean & Mint", JadeAurora, SolarAmber),
    ThemeColorOption("#38BDF8", "Glacier Blue", "Ocean & Mint", GlacierIce, NeonPink),

    // 4. Warm & Gold
    ThemeColorOption("#F59E0B", "Solar Amber", "Solar & Warm", SolarAmber, HoloCyan),
    ThemeColorOption("#FB923C", "Peach Orange", "Solar & Warm", PeachHorizon, CyberEmerald),
    ThemeColorOption("#FF6B35", "Tangerine", "Solar & Warm", TangerineFlare, ElectricAqua),
    ThemeColorOption("#FFD166", "Champagne Gold", "Luxury & Gold", RoyalChampagneGold, OrchidSynth),
    ThemeColorOption("#E11D48", "Ruby Red", "Luxury & Gold", ImperialRuby, RoyalChampagneGold),
    ThemeColorOption("#F472B6", "Rose Blush", "Luxury & Gold", RoseGoldBlush, ElectricAqua)
)

data class ThreeDColorPalettePreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val badge: String,
    val primaryHex: String,
    val secondaryHex: String,
    val tertiaryHex: String,
    val visualStyleId: String,
    val swatchColors: List<Color>
)

val CURATED_3D_COLOR_PALETTES = listOf(
    ThreeDColorPalettePreset(
        id = "palette_wereequal_hypercube",
        name = "WereEqual Quantum Blue",
        subtitle = "Royal Blue, Electric Aqua & Sky Cyan 6D Hypercube",
        badge = "SIGNATURE 3D",
        primaryHex = "#2563EB",
        secondaryHex = "#00F5D4",
        tertiaryHex = "#06B6D4",
        visualStyleId = "HYPERCUBE_6D",
        swatchColors = listOf(MidnightSapphire, ElectricAqua, HoloCyan, DeepSpaceBg)
    ),
    ThreeDColorPalettePreset(
        id = "palette_cyber_synthwave",
        name = "Neon Sunset Synthwave",
        subtitle = "Hot Pink, Solar Amber & Orchid Synth Horizon",
        badge = "SYNTHWAVE 3D",
        primaryHex = "#EC4899",
        secondaryHex = "#F59E0B",
        tertiaryHex = "#D946EF",
        visualStyleId = "CYBER_SYNTHWAVE",
        swatchColors = listOf(NeonPink, SolarAmber, OrchidSynth, DeepSpaceBg)
    ),
    ThreeDColorPalettePreset(
        id = "palette_galaxy_nebula",
        name = "Cosmic Amethyst Nebula",
        subtitle = "Laser Violet, Andromeda Magenta & Starlight Glow",
        badge = "GALAXY 3D",
        primaryHex = "#8B5CF6",
        secondaryHex = "#C026D3",
        tertiaryHex = "#818CF8",
        visualStyleId = "GALAXY_VORTEX",
        swatchColors = listOf(VioletNebula, AndromedaMagenta, StarlightPeriwinkle, DeepSpaceBg)
    ),
    ThreeDColorPalettePreset(
        id = "palette_crystal_ocean",
        name = "Crystal Lagoon Prism",
        subtitle = "Sky Cyan, Lagoon Turquoise & Glacier Ice Octahedron",
        badge = "PRISM 3D",
        primaryHex = "#06B6D4",
        secondaryHex = "#2DD4BF",
        tertiaryHex = "#38BDF8",
        visualStyleId = "CRYSTAL_PRISM",
        swatchColors = listOf(HoloCyan, LagoonTurquoise, GlacierIce, DeepSpaceBg)
    ),
    ThreeDColorPalettePreset(
        id = "palette_emerald_aurora",
        name = "Bioluminescent Aurora",
        subtitle = "Emerald Green, Bio Mint & Jade Aurora Ribbons",
        badge = "AURORA 3D",
        primaryHex = "#10B981",
        secondaryHex = "#22C55E",
        tertiaryHex = "#34D399",
        visualStyleId = "AURORA_WAVE",
        swatchColors = listOf(CyberEmerald, BioMint, JadeAurora, DeepSpaceBg)
    ),
    ThreeDColorPalettePreset(
        id = "palette_royal_gold",
        name = "Imperial Champagne & Ruby",
        subtitle = "Royal Champagne Gold, Imperial Ruby & Tangerine Flare",
        badge = "LUXURY 3D",
        primaryHex = "#FFD166",
        secondaryHex = "#E11D48",
        tertiaryHex = "#FF6B35",
        visualStyleId = "MATRIX_CYBERNETIC",
        swatchColors = listOf(RoyalChampagneGold, ImperialRuby, TangerineFlare, DeepSpaceBg)
    )
)

fun parseThemeHex(hex: String): Color {
    val clean = hex.trim().uppercase()
    EXPANDED_THEME_COLOR_OPTIONS.firstOrNull { it.hex.uppercase() == clean }?.let {
        return it.color
    }
    val hexDigits = clean.removePrefix("#")
    if (hexDigits.length == 6) {
        val rgb = hexDigits.toLongOrNull(16)
        if (rgb != null) {
            return Color(0xFF000000 or rgb)
        }
    }
    return ElectricIndigo
}

fun findSecondaryAccentForHex(hex: String): Color {
    val clean = hex.trim().uppercase()
    EXPANDED_THEME_COLOR_OPTIONS.firstOrNull { it.hex.uppercase() == clean }?.let {
        return it.secondaryColor
    }
    val primary = parseThemeHex(hex)
    return lerp(primary, HoloCyan, 0.55f)
}
