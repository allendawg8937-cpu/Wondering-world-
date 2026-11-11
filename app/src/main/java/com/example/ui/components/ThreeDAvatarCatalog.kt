package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

data class ThreeDAvatarPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val drawableResId: Int,
    val tags: List<String>,
    val accentColor: Color,
    val badgeEmoji: String
)

val THREE_D_AVATAR_PRESETS = listOf(
    ThreeDAvatarPreset(
        id = "cyber_astronaut",
        title = "Cosmic 3D Explorer",
        subtitle = "Holographic visor & electric indigo nebula lighting",
        drawableResId = R.drawable.img_avatar_cyber_astronaut,
        tags = listOf("Cosmic Explorer", "Pixar 3D Render", "Holo Visor", "Deep Space Nebula"),
        accentColor = HoloCyan,
        badgeEmoji = "🚀"
    ),
    ThreeDAvatarPreset(
        id = "neon_musician",
        title = "Synthwave 3D Producer",
        subtitle = "Glowing magenta headphones & sonic studio waves",
        drawableResId = R.drawable.img_avatar_neon_musician,
        tags = listOf("Music Producer", "Neon Rim Light", "Glowing Headphones", "Synthwave Studio"),
        accentColor = NeonPink,
        badgeEmoji = "🎵"
    ),
    ThreeDAvatarPreset(
        id = "stem_scholar",
        title = "STEM 3D Scholar",
        subtitle = "Emerald AR smart glasses & floating geometric cubes",
        drawableResId = R.drawable.img_avatar_stem_scholar,
        tags = listOf("STEM Inventor", "Holographic Glow", "AR Smart Glasses", "Quantum Library"),
        accentColor = CyberEmerald,
        badgeEmoji = "🎓"
    ),
    ThreeDAvatarPreset(
        id = "veteran_guardian",
        title = "Solar 3D Guardian",
        subtitle = "Golden star crest jacket & heroic warm rim light",
        drawableResId = R.drawable.img_avatar_veteran_guardian,
        tags = listOf("Veteran Mentor", "Solar Golden Hour", "Solar Gold Crest", "Aurora Horizon"),
        accentColor = SolarAmber,
        badgeEmoji = "🛡️"
    ),
    ThreeDAvatarPreset(
        id = "personality_adventurous",
        title = "Adventurous Explorer",
        subtitle = "Rugged gear & vibrant action lighting",
        drawableResId = R.drawable.img_avatar_adventurous,
        tags = listOf("Adventurous & Bold", "Cosmic Explorer", "Action Lighting", "Deep Space Nebula"),
        accentColor = HoloCyan,
        badgeEmoji = "🏔️"
    ),
    ThreeDAvatarPreset(
        id = "personality_zen",
        title = "Zen Mediator",
        subtitle = "Calm aura & soft cherry blossom petals",
        drawableResId = R.drawable.img_avatar_zen,
        tags = listOf("Calm & Zen", "Meditation Aura", "Soft Ethereal Light", "Aurora Horizon"),
        accentColor = CyberEmerald,
        badgeEmoji = "🧘"
    ),
    ThreeDAvatarPreset(
        id = "personality_energetic",
        title = "Energetic Spirit",
        subtitle = "Dynamic pose & bright spark energy",
        drawableResId = R.drawable.img_avatar_energetic,
        tags = listOf("Energetic & Playful", "Dynamic Spark", "Synthwave Studio", "Neon Rim Light"),
        accentColor = NeonPink,
        badgeEmoji = "⚡"
    ),
    ThreeDAvatarPreset(
        id = "personality_intellectual",
        title = "Intellectual Sage",
        subtitle = "Sleek glasses & floating holographic formulas",
        drawableResId = R.drawable.img_avatar_intellectual,
        tags = listOf("Intellectual & Wise", "Quantum Library", "Holographic Glow", "AR Smart Glasses"),
        accentColor = SolarAmber,
        badgeEmoji = "🧠"
    )
)

val AVATAR_DESCRIPTIVE_TAG_GROUPS: List<Pair<String, List<String>>> = listOf(
    "Personality Traits" to listOf(
        "Adventurous & Bold",
        "Calm & Zen",
        "Energetic & Playful",
        "Intellectual & Wise",
        "Mysterious & Moody",
        "Friendly & Warm"
    ),
    "Character Archetype" to listOf(
        "Cosmic Explorer",
        "Music Producer",
        "STEM Inventor",
        "Veteran Mentor",
        "Digital Artist"
    ),
    "3D Render & Lighting" to listOf(
        "Pixar 3D Render",
        "Neon Rim Light",
        "Holographic Glow",
        "Solar Golden Hour"
    ),
    "3D Gear & Accessories" to listOf(
        "Holo Visor",
        "Glowing Headphones",
        "AR Smart Glasses",
        "Solar Gold Crest"
    ),
    "Dimension Backdrop" to listOf(
        "Deep Space Nebula",
        "Synthwave Studio",
        "Quantum Library",
        "Aurora Horizon"
    )
)

fun build3DAvatarPromptFromTags(tags: Set<String>): String {
    if (tags.isEmpty()) {
        return "3D stylized character avatar portrait, Cosmic Explorer, Pixar 3D Render, Holo Visor, Deep Space Nebula, close-up centered composition, high detail 3D digital art"
    }
    
    val traitTags = tags.filter { tag ->
        AVATAR_DESCRIPTIVE_TAG_GROUPS.find { it.first == "Personality Traits" }?.second?.contains(tag) == true
    }
    val otherTags = tags.filter { !traitTags.contains(it) }
    
    val personalityDesc = if (traitTags.isNotEmpty()) {
        "with a ${traitTags.joinToString(" and ").lowercase()} personality and expression"
    } else ""
    
    val physicalDesc = if (otherTags.isNotEmpty()) {
        "featuring ${otherTags.joinToString(", ")}"
    } else ""
    
    return "3D stylized character avatar portrait $personalityDesc, $physicalDesc, close-up centered composition, high detail 3D digital art, Pixar style render, vibrant cinematic lighting"
}

fun findBestMatching3DAvatarPreset(selectedTags: Set<String>): ThreeDAvatarPreset {
    if (selectedTags.isEmpty()) return THREE_D_AVATAR_PRESETS.first()
    return THREE_D_AVATAR_PRESETS.maxByOrNull { preset ->
        preset.tags.count { tag -> selectedTags.contains(tag) }
    } ?: THREE_D_AVATAR_PRESETS.first()
}
