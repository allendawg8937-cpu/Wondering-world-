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
    val badgeEmoji: String,
    val lifeStageId: String? = null
)

data class LifeStageAvatarPreset(
    val stageId: String,
    val stageLabel: String,
    val presetId: String,
    val characterTitle: String,
    val tagline: String,
    val badgeEmoji: String,
    val defaultAge: Long,
    val accentHex: String,
    val accentColor: Color,
    @DrawableRes val drawableResId: Int,
    val tags: List<String>
) {
    fun toThreeDAvatarPreset(): ThreeDAvatarPreset = ThreeDAvatarPreset(
        id = presetId,
        title = "$stageLabel • $characterTitle",
        subtitle = tagline,
        drawableResId = drawableResId,
        tags = tags,
        accentColor = accentColor,
        badgeEmoji = badgeEmoji,
        lifeStageId = stageId
    )
}

val LIFE_STAGE_3D_AVATARS: List<LifeStageAvatarPreset> = listOf(
    LifeStageAvatarPreset(
        stageId = "kid",
        stageLabel = "Kid",
        presetId = "stage_kid",
        characterTitle = "Starlight Explorer",
        tagline = "Emerald explorer jacket & glowing holo visor",
        badgeEmoji = "🚀",
        defaultAge = 10L,
        accentHex = "#10B981",
        accentColor = CyberEmerald,
        drawableResId = R.drawable.img_stage_avatar_kid,
        tags = listOf("Energetic & Playful", "Cosmic Explorer", "Pixar 3D Render", "Holo Visor")
    ),
    LifeStageAvatarPreset(
        stageId = "teen",
        stageLabel = "Teen",
        presetId = "stage_teen",
        characterTitle = "Neon Creator",
        tagline = "Magenta sonic headphones & tech hoodie",
        badgeEmoji = "🎵",
        defaultAge = 16L,
        accentHex = "#EC4899",
        accentColor = NeonPink,
        drawableResId = R.drawable.img_stage_avatar_teen,
        tags = listOf("Energetic & Playful", "Music Producer", "Neon Rim Light", "Glowing Headphones")
    ),
    LifeStageAvatarPreset(
        stageId = "student",
        stageLabel = "Student",
        presetId = "stage_student",
        characterTitle = "Quantum Scholar",
        tagline = "Indigo AR smart glasses & futuristic varsity crest",
        badgeEmoji = "🎓",
        defaultAge = 21L,
        accentHex = "#6366F1",
        accentColor = Color(0xFF6366F1),
        drawableResId = R.drawable.img_stage_avatar_student,
        tags = listOf("Intellectual & Wise", "STEM Inventor", "Holographic Glow", "AR Smart Glasses")
    ),
    LifeStageAvatarPreset(
        stageId = "adult",
        stageLabel = "Adult",
        presetId = "stage_adult",
        characterTitle = "Horizon Voyager",
        tagline = "Cyan-trimmed modern blazer & aurora balance",
        badgeEmoji = "🧭",
        defaultAge = 30L,
        accentHex = "#00E5FF",
        accentColor = HoloCyan,
        drawableResId = R.drawable.img_stage_avatar_adult,
        tags = listOf("Adventurous & Bold", "Friendly & Warm", "Pixar 3D Render", "Aurora Horizon")
    ),
    LifeStageAvatarPreset(
        stageId = "business",
        stageLabel = "Business",
        presetId = "stage_business",
        characterTitle = "Apex Founder",
        tagline = "Tailored midnight suit & golden geometric lapel",
        badgeEmoji = "💼",
        defaultAge = 38L,
        accentHex = "#F59E0B",
        accentColor = SolarAmber,
        drawableResId = R.drawable.img_stage_avatar_business,
        tags = listOf("Intellectual & Wise", "Solar Golden Hour", "Pixar 3D Render", "Solar Gold Crest")
    ),
    LifeStageAvatarPreset(
        stageId = "veteran",
        stageLabel = "Veteran",
        presetId = "stage_veteran",
        characterTitle = "Solar Mentor",
        tagline = "Heritage flight jacket & antique-gold wisdom crest",
        badgeEmoji = "🛡️",
        defaultAge = 64L,
        accentHex = "#A855F7",
        accentColor = Color(0xFFA855F7),
        drawableResId = R.drawable.img_stage_avatar_veteran,
        tags = listOf("Calm & Zen", "Veteran Mentor", "Solar Gold Crest", "Aurora Horizon")
    )
)

fun findLifeStageAvatarForRole(role: String): LifeStageAvatarPreset {
    val normalized = role.trim().lowercase()
    return when {
        normalized.contains("kid") || normalized.contains("child") -> LIFE_STAGE_3D_AVATARS[0]
        normalized.contains("teen") || normalized.contains("youth") -> LIFE_STAGE_3D_AVATARS[1]
        normalized.contains("student") || normalized.contains("college") || normalized.contains("scholar") -> LIFE_STAGE_3D_AVATARS[2]
        normalized.contains("business") || normalized.contains("founder") || normalized.contains("exec") -> LIFE_STAGE_3D_AVATARS[4]
        normalized.contains("veteran") || normalized.contains("senior") || normalized.contains("mentor") -> LIFE_STAGE_3D_AVATARS[5]
        else -> LIFE_STAGE_3D_AVATARS[3]
    }
}

val THREE_D_AVATAR_PRESETS = LIFE_STAGE_3D_AVATARS.map { it.toThreeDAvatarPreset() } + listOf(
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
