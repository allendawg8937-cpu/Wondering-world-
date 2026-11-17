package com.example.ui.studio

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

enum class StudioPersonaStage(
    val id: String,
    val displayName: String,
    val badgeEmoji: String,
    val styleHeadline: String,
    val accentColor: Color
) {
    KID(
        id = "kid",
        displayName = "Kid",
        badgeEmoji = "🚀",
        styleHeadline = "Playful & Cosmic Filters",
        accentColor = Color(0xFF10B981)
    ),
    TEEN(
        id = "teen",
        displayName = "Teen",
        badgeEmoji = "⚡",
        styleHeadline = "Creator Neon & Arcade Filters",
        accentColor = Color(0xFFEC4899)
    ),
    STUDENT(
        id = "student",
        displayName = "Student",
        badgeEmoji = "🎓",
        styleHeadline = "Campus & Academia Filters",
        accentColor = Color(0xFF6366F1)
    ),
    ADULT(
        id = "adult",
        displayName = "Adult",
        badgeEmoji = "🏡",
        styleHeadline = "Editorial & Lifestyle Filters",
        accentColor = Color(0xFF06B6D4)
    ),
    BUSINESS(
        id = "business",
        displayName = "Business",
        badgeEmoji = "💼",
        styleHeadline = "Executive & Pro Portrait Filters",
        accentColor = Color(0xFFF59E0B)
    ),
    VETERAN(
        id = "veteran",
        displayName = "Veteran",
        badgeEmoji = "🎖️",
        styleHeadline = "Heritage & Tactical Filters",
        accentColor = Color(0xFF8B5CF6)
    );

    companion object {
        fun fromRole(role: String?): StudioPersonaStage {
            val lower = role.orEmpty().trim().lowercase()
            return when {
                lower.contains("business") || lower.contains("founder") || lower.contains("exec") -> BUSINESS
                lower.contains("veteran") || lower.contains("military") -> VETERAN
                lower.contains("student") || lower.contains("college") -> STUDENT
                lower.contains("teen") || lower.contains("youth") -> TEEN
                lower.contains("kid") || lower.contains("child") -> KID
                else -> ADULT
            }
        }
    }
}

enum class StudioFilterFrameStyle(
    val id: String,
    val label: String
) {
    CLEAN("clean", "Clean"),
    PERSONA_SIGNATURE("persona_signature", "Persona Frame"),
    EXECUTIVE_PINSTRIPE("executive_pinstripe", "Executive Crest"),
    CINEMA_SPROCKET("cinema_sprocket", "Cinema Film"),
    NEON_HUD("neon_hud", "Cyber HUD"),
    VIGNETTE_FOCUS("vignette_focus", "Soft Vignette")
}

data class PersonaStylizedFilter(
    val id: String,
    val name: String,
    val subtitle: String,
    val personaStage: StudioPersonaStage,
    val badge: String,
    val accentColor: Color,
    val colorMatrix: FloatArray,
    val defaultFrameStyle: StudioFilterFrameStyle,
    val defaultContrastBoost: Float = 0.06f,
    val vignetteStrength: Float = 0.25f
) {
    fun buildAdjustedMatrix(
        intensity: Float,
        brightnessOffset: Float,
        contrastBoost: Float
    ): FloatArray {
        val t = intensity.coerceIn(0f, 1f)
        val identity = IDENTITY_MATRIX
        val blended = FloatArray(20) { i ->
            identity[i] + (colorMatrix[i] - identity[i]) * t
        }
        val contrastScale = 1f + contrastBoost.coerceIn(0f, 0.5f)
        val contrastTranslate = (-0.5f * contrastScale + 0.5f) * 255f
        blended[0] *= contrastScale
        blended[6] *= contrastScale
        blended[12] *= contrastScale
        blended[4] += contrastTranslate + brightnessOffset
        blended[9] += contrastTranslate + brightnessOffset
        blended[14] += contrastTranslate + brightnessOffset
        return blended
    }
}

private val IDENTITY_MATRIX = floatArrayOf(
    1f, 0f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f, 0f,
    0f, 0f, 1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

val PERSONA_STYLIZED_FILTERS: List<PersonaStylizedFilter> = listOf(
    // --- BUSINESS PERSONA (Professional / Executive Portrait Suite) ---
    PersonaStylizedFilter(
        id = "biz_executive_slate",
        name = "Executive Slate",
        subtitle = "Crisp cool slate shadows & boardroom clarity",
        personaStage = StudioPersonaStage.BUSINESS,
        badge = "PRO",
        accentColor = Color(0xFF1E3A8A),
        defaultFrameStyle = StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE,
        defaultContrastBoost = 0.08f,
        vignetteStrength = 0.28f,
        colorMatrix = floatArrayOf(
            0.94f, 0.04f, 0.08f, 0f, -2f,
            0.03f, 1.02f, 0.07f, 0f, 2f,
            0.08f, 0.12f, 1.18f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "biz_boardroom_warmth",
        name = "Boardroom Warmth",
        subtitle = "Polished studio key-light & natural skin tones",
        personaStage = StudioPersonaStage.BUSINESS,
        badge = "STUDIO",
        accentColor = SolarAmber,
        defaultFrameStyle = StudioFilterFrameStyle.VIGNETTE_FOCUS,
        defaultContrastBoost = 0.06f,
        vignetteStrength = 0.22f,
        colorMatrix = floatArrayOf(
            1.12f, 0.05f, 0f, 0f, 8f,
            0.03f, 1.05f, 0.02f, 0f, 4f,
            0f, 0.02f, 0.92f, 0f, -4f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "biz_editorial_mono",
        name = "Editorial Mono Pro",
        subtitle = "High-contrast black & white keynote headshot",
        personaStage = StudioPersonaStage.BUSINESS,
        badge = "MONO",
        accentColor = Color(0xFFE2E8F0),
        defaultFrameStyle = StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE,
        defaultContrastBoost = 0.14f,
        vignetteStrength = 0.35f,
        colorMatrix = floatArrayOf(
            0.34f, 0.56f, 0.16f, 0f, 4f,
            0.34f, 0.56f, 0.16f, 0f, 4f,
            0.34f, 0.56f, 0.16f, 0f, 4f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "biz_champagne_prestige",
        name = "Champagne Prestige",
        subtitle = "Refined amber highlights & crisp contrast",
        personaStage = StudioPersonaStage.BUSINESS,
        badge = "GOLD",
        accentColor = Color(0xFFEAB308),
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.09f,
        vignetteStrength = 0.25f,
        colorMatrix = floatArrayOf(
            1.14f, 0.08f, 0f, 0f, 10f,
            0.04f, 1.06f, 0.02f, 0f, 6f,
            0f, 0.04f, 0.88f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),

    // --- ADULT PERSONA (Lifestyle & Editorial) ---
    PersonaStylizedFilter(
        id = "adult_golden_hour",
        name = "Golden Hour",
        subtitle = "Warm sunset glow & creamy contrast",
        personaStage = StudioPersonaStage.ADULT,
        badge = "SOLAR",
        accentColor = SolarAmber,
        defaultFrameStyle = StudioFilterFrameStyle.VIGNETTE_FOCUS,
        defaultContrastBoost = 0.05f,
        vignetteStrength = 0.20f,
        colorMatrix = floatArrayOf(
            1.18f, 0.09f, 0f, 0f, 16f,
            0.04f, 1.06f, 0f, 0f, 8f,
            0f, 0f, 0.84f, 0f, -10f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "adult_urban_matte",
        name = "Urban Matte Film",
        subtitle = "Modern editorial film with lifted shadows",
        personaStage = StudioPersonaStage.ADULT,
        badge = "FILM",
        accentColor = HoloCyan,
        defaultFrameStyle = StudioFilterFrameStyle.CINEMA_SPROCKET,
        defaultContrastBoost = 0.04f,
        vignetteStrength = 0.26f,
        colorMatrix = floatArrayOf(
            0.96f, 0.08f, 0.04f, 0f, 8f,
            0.02f, 1.02f, 0.06f, 0f, 8f,
            0.04f, 0.06f, 1.04f, 0f, 12f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "adult_bio_emerald",
        name = "Emerald Fresh",
        subtitle = "Crisp outdoor greens & clean balance",
        personaStage = StudioPersonaStage.ADULT,
        badge = "FRESH",
        accentColor = CyberEmerald,
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.06f,
        vignetteStrength = 0.15f,
        colorMatrix = floatArrayOf(
            0.88f, 0.08f, 0f, 0f, -2f,
            0.06f, 1.24f, 0.06f, 0f, 10f,
            0f, 0.08f, 1.02f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "adult_studio_natural",
        name = "Studio Natural HD",
        subtitle = "True-to-life natural color fidelity",
        personaStage = StudioPersonaStage.ADULT,
        badge = "HD",
        accentColor = Color(0xFF38BDF8),
        defaultFrameStyle = StudioFilterFrameStyle.CLEAN,
        defaultContrastBoost = 0.03f,
        vignetteStrength = 0.08f,
        colorMatrix = IDENTITY_MATRIX
    ),

    // --- STUDENT PERSONA (Campus & Academia) ---
    PersonaStylizedFilter(
        id = "student_academia_indigo",
        name = "Academia Indigo",
        subtitle = "Intellectual indigo tones & crisp detail",
        personaStage = StudioPersonaStage.STUDENT,
        badge = "SCHOLAR",
        accentColor = Color(0xFF6366F1),
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.08f,
        vignetteStrength = 0.22f,
        colorMatrix = floatArrayOf(
            1.08f, 0.04f, 0.16f, 0f, 8f,
            0.04f, 0.98f, 0.10f, 0f, 0f,
            0.16f, 0.08f, 1.28f, 0f, 18f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "student_library_sepia",
        name = "Library Vintage",
        subtitle = "Cozy analog warmth for study snapshots",
        personaStage = StudioPersonaStage.STUDENT,
        badge = "ANALOG",
        accentColor = SolarAmber,
        defaultFrameStyle = StudioFilterFrameStyle.CINEMA_SPROCKET,
        defaultContrastBoost = 0.05f,
        vignetteStrength = 0.30f,
        colorMatrix = floatArrayOf(
            0.88f, 0.34f, 0.10f, 0f, 12f,
            0.18f, 0.84f, 0.08f, 0f, 6f,
            0.08f, 0.20f, 0.68f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "student_campus_morning",
        name = "Campus Morning",
        subtitle = "Bright airy daylight boost",
        personaStage = StudioPersonaStage.STUDENT,
        badge = "BRIGHT",
        accentColor = HoloCyan,
        defaultFrameStyle = StudioFilterFrameStyle.CLEAN,
        defaultContrastBoost = 0.04f,
        vignetteStrength = 0.10f,
        colorMatrix = floatArrayOf(
            1.08f, 0.04f, 0.02f, 0f, 14f,
            0.04f, 1.12f, 0.04f, 0f, 14f,
            0.04f, 0.06f, 1.16f, 0f, 16f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "student_blueprint_crisp",
        name = "Blueprint Crisp",
        subtitle = "Cool cyan architectural accent",
        personaStage = StudioPersonaStage.STUDENT,
        badge = "STEM",
        accentColor = Color(0xFF7C3AED),
        defaultFrameStyle = StudioFilterFrameStyle.NEON_HUD,
        defaultContrastBoost = 0.10f,
        vignetteStrength = 0.18f,
        colorMatrix = floatArrayOf(
            0.84f, 0.04f, 0.12f, 0f, -4f,
            0.06f, 1.14f, 0.14f, 0f, 10f,
            0.12f, 0.16f, 1.30f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        )
    ),

    // --- TEEN PERSONA (Creator Lab & Street Neon) ---
    PersonaStylizedFilter(
        id = "teen_cyber_neon",
        name = "Cyber Neon",
        subtitle = "Electric magenta & cyan rim glow",
        personaStage = StudioPersonaStage.TEEN,
        badge = "NEON",
        accentColor = NeonPink,
        defaultFrameStyle = StudioFilterFrameStyle.NEON_HUD,
        defaultContrastBoost = 0.10f,
        vignetteStrength = 0.25f,
        colorMatrix = floatArrayOf(
            1.24f, 0f, 0.16f, 0f, 12f,
            0f, 0.92f, 0.12f, 0f, -6f,
            0.18f, 0f, 1.34f, 0f, 22f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "teen_synthwave_glow",
        name = "Sunset Synthwave",
        subtitle = "Retro rose & amber horizon tint",
        personaStage = StudioPersonaStage.TEEN,
        badge = "SYNTH",
        accentColor = RosePulse,
        defaultFrameStyle = StudioFilterFrameStyle.CINEMA_SPROCKET,
        defaultContrastBoost = 0.09f,
        vignetteStrength = 0.28f,
        colorMatrix = floatArrayOf(
            1.28f, 0.08f, 0f, 0f, 16f,
            0f, 0.86f, 0.06f, 0f, -8f,
            0.10f, 0.10f, 1.22f, 0f, 18f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "teen_tangerine_pop",
        name = "Tangerine Pop",
        subtitle = "High-energy warm street punch",
        personaStage = StudioPersonaStage.TEEN,
        badge = "POP",
        accentColor = Color(0xFFF97316),
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.10f,
        vignetteStrength = 0.18f,
        colorMatrix = floatArrayOf(
            1.32f, 0.08f, 0.02f, 0f, 18f,
            0.06f, 1.08f, 0.04f, 0f, 10f,
            0.02f, 0.06f, 0.86f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "teen_holo_prism",
        name = "Holo Prism",
        subtitle = "Iridescent aqua & violet sheen",
        personaStage = StudioPersonaStage.TEEN,
        badge = "HOLO",
        accentColor = HoloCyan,
        defaultFrameStyle = StudioFilterFrameStyle.NEON_HUD,
        defaultContrastBoost = 0.07f,
        vignetteStrength = 0.20f,
        colorMatrix = floatArrayOf(
            0.84f, 0.02f, 0.12f, 0f, -4f,
            0.06f, 1.20f, 0.14f, 0f, 12f,
            0.12f, 0.14f, 1.30f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        )
    ),

    // --- KID PERSONA (Playful & Cosmic Wonder) ---
    PersonaStylizedFilter(
        id = "kid_toon_sunshine",
        name = "Toon Sunshine",
        subtitle = "Cheerful sunny pop & vivid brightness",
        personaStage = StudioPersonaStage.KID,
        badge = "SUNNY",
        accentColor = Color(0xFFEAB308),
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.06f,
        vignetteStrength = 0.05f,
        colorMatrix = floatArrayOf(
            1.24f, 0.12f, 0.02f, 0f, 20f,
            0.06f, 1.22f, 0.02f, 0f, 18f,
            0.02f, 0.06f, 1.14f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "kid_starlight_explorer",
        name = "Starlight Explorer",
        subtitle = "Cosmic teal & emerald glow",
        personaStage = StudioPersonaStage.KID,
        badge = "SPACE",
        accentColor = CyberEmerald,
        defaultFrameStyle = StudioFilterFrameStyle.NEON_HUD,
        defaultContrastBoost = 0.05f,
        vignetteStrength = 0.12f,
        colorMatrix = floatArrayOf(
            0.90f, 0.06f, 0.06f, 0f, 6f,
            0.08f, 1.24f, 0.10f, 0f, 14f,
            0.06f, 0.12f, 1.26f, 0f, 18f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "kid_candy_pastel",
        name = "Candy Pastel",
        subtitle = "Soft dreamy cotton-candy highlights",
        personaStage = StudioPersonaStage.KID,
        badge = "PASTEL",
        accentColor = NeonPink,
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.02f,
        vignetteStrength = 0.08f,
        colorMatrix = floatArrayOf(
            1.14f, 0.08f, 0.08f, 0f, 22f,
            0.06f, 1.08f, 0.08f, 0f, 18f,
            0.10f, 0.08f, 1.18f, 0f, 22f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "kid_comic_pop",
        name = "Comic Pop",
        subtitle = "Bold superhero color punch",
        personaStage = StudioPersonaStage.KID,
        badge = "COMIC",
        accentColor = HoloCyan,
        defaultFrameStyle = StudioFilterFrameStyle.CLEAN,
        defaultContrastBoost = 0.14f,
        vignetteStrength = 0.10f,
        colorMatrix = floatArrayOf(
            1.28f, 0f, 0f, 0f, 8f,
            0f, 1.24f, 0f, 0f, 8f,
            0f, 0f, 1.26f, 0f, 8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),

    // --- VETERAN PERSONA (Heritage & Tactical Honor) ---
    PersonaStylizedFilter(
        id = "vet_tactical_olive",
        name = "Tactical Olive HDR",
        subtitle = "Grounded olive tones & sharp outdoor clarity",
        personaStage = StudioPersonaStage.VETERAN,
        badge = "HONOR",
        accentColor = Color(0xFF15803D),
        defaultFrameStyle = StudioFilterFrameStyle.PERSONA_SIGNATURE,
        defaultContrastBoost = 0.10f,
        vignetteStrength = 0.25f,
        colorMatrix = floatArrayOf(
            0.90f, 0.16f, 0.05f, 0f, 4f,
            0.10f, 1.20f, 0.08f, 0f, 8f,
            0.04f, 0.12f, 0.80f, 0f, -6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "vet_heritage_silver",
        name = "Heritage Silver",
        subtitle = "Dignified silver-gelatin monochrome",
        personaStage = StudioPersonaStage.VETERAN,
        badge = "SILVER",
        accentColor = Color(0xFFCBD5E1),
        defaultFrameStyle = StudioFilterFrameStyle.VIGNETTE_FOCUS,
        defaultContrastBoost = 0.14f,
        vignetteStrength = 0.34f,
        colorMatrix = floatArrayOf(
            0.38f, 0.52f, 0.16f, 0f, 6f,
            0.38f, 0.52f, 0.16f, 0f, 6f,
            0.38f, 0.52f, 0.16f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "vet_valor_bronze",
        name = "Valor Bronze",
        subtitle = "Warm ceremonial bronze & brass glow",
        personaStage = StudioPersonaStage.VETERAN,
        badge = "VALOR",
        accentColor = SolarAmber,
        defaultFrameStyle = StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE,
        defaultContrastBoost = 0.08f,
        vignetteStrength = 0.26f,
        colorMatrix = floatArrayOf(
            1.16f, 0.14f, 0.02f, 0f, 10f,
            0.08f, 1.04f, 0.02f, 0f, 4f,
            0.02f, 0.06f, 0.78f, 0f, -10f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PersonaStylizedFilter(
        id = "vet_horizon_crisp",
        name = "Horizon Crisp",
        subtitle = "Clean violet-slate clarity & contrast",
        personaStage = StudioPersonaStage.VETERAN,
        badge = "CRISP",
        accentColor = Color(0xFF8B5CF6),
        defaultFrameStyle = StudioFilterFrameStyle.CLEAN,
        defaultContrastBoost = 0.09f,
        vignetteStrength = 0.16f,
        colorMatrix = floatArrayOf(
            1.04f, 0.02f, 0.06f, 0f, 2f,
            0.02f, 1.04f, 0.04f, 0f, 2f,
            0.06f, 0.04f, 1.14f, 0f, 8f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

fun filtersForPersona(stage: StudioPersonaStage): List<PersonaStylizedFilter> =
    PERSONA_STYLIZED_FILTERS.filter { it.personaStage == stage }

fun orderedFiltersForPersona(stage: StudioPersonaStage): List<PersonaStylizedFilter> {
    val primary = PERSONA_STYLIZED_FILTERS.filter { it.personaStage == stage }
    val others = PERSONA_STYLIZED_FILTERS.filter { it.personaStage != stage }
    return primary + others
}

fun scaleBitmapSafely(source: Bitmap, maxDimension: Int = 1440): Bitmap {
    val w = source.width.coerceAtLeast(1)
    val h = source.height.coerceAtLeast(1)
    if (w <= maxDimension && h <= maxDimension) {
        return if (source.config == Bitmap.Config.ARGB_8888) {
            source
        } else {
            source.copy(Bitmap.Config.ARGB_8888, false)
        }
    }
    val ratio = maxDimension.toFloat() / maxOf(w, h).toFloat()
    val targetW = (w * ratio).toInt().coerceAtLeast(1)
    val targetH = (h * ratio).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(source, targetW, targetH, true)
}

fun decodeAndOrientImageProxy(image: ImageProxy, mirrorHorizontal: Boolean): Bitmap? {
    return try {
        val raw = image.toBitmap()
        val rotation = image.imageInfo.rotationDegrees.toFloat()
        if (rotation == 0f && !mirrorHorizontal) {
            scaleBitmapSafely(raw, 1440)
        } else {
            val matrix = Matrix().apply {
                if (rotation != 0f) postRotate(rotation)
                if (mirrorHorizontal) postScale(-1f, 1f)
            }
            val transformed = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
            if (transformed != raw) raw.recycle()
            scaleBitmapSafely(transformed, 1440)
        }
    } catch (_: Exception) {
        try {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
            val rotation = image.imageInfo.rotationDegrees.toFloat()
            val matrix = Matrix().apply {
                if (rotation != 0f) postRotate(rotation)
                if (mirrorHorizontal) postScale(-1f, 1f)
            }
            val transformed = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
            if (transformed != decoded) decoded.recycle()
            scaleBitmapSafely(transformed, 1440)
        } catch (_: Exception) {
            null
        }
    }
}

fun renderPersonaStylizedBitmap(
    source: Bitmap,
    filter: PersonaStylizedFilter,
    intensity: Float = 0.9f,
    brightnessOffset: Float = 0f,
    contrastBoost: Float = filter.defaultContrastBoost,
    frameStyle: StudioFilterFrameStyle = filter.defaultFrameStyle
): Bitmap {
    val safeSource = scaleBitmapSafely(source, 1280)
    val width = safeSource.width.coerceAtLeast(1)
    val height = safeSource.height.coerceAtLeast(1)
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val matrixValues = filter.buildAdjustedMatrix(
        intensity = intensity,
        brightnessOffset = brightnessOffset,
        contrastBoost = contrastBoost
    )
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(ColorMatrix(matrixValues))
    }
    canvas.drawBitmap(safeSource, 0f, 0f, paint)

    val w = width.toFloat()
    val h = height.toFloat()
    val effVignette = (filter.vignetteStrength * intensity.coerceIn(0.2f, 1f)).coerceIn(0f, 0.75f)
    if (effVignette > 0.04f || frameStyle == StudioFilterFrameStyle.VIGNETTE_FOCUS) {
        val vignetteAlpha = ((if (frameStyle == StudioFilterFrameStyle.VIGNETTE_FOCUS) maxOf(effVignette, 0.42f) else effVignette) * 200)
            .toInt()
            .coerceIn(0, 210)
        val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f,
                h * 0.48f,
                maxOf(w, h) * 0.65f,
                intArrayOf(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.argb(vignetteAlpha / 2, 10, 15, 30),
                    android.graphics.Color.argb(vignetteAlpha, 5, 8, 18)
                ),
                floatArrayOf(0.45f, 0.78f, 1.0f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, vignettePaint)
    }

    drawStudioFrameOverlayOnCanvas(
        canvas = canvas,
        w = w,
        h = h,
        frameStyle = frameStyle,
        personaStage = filter.personaStage,
        accentColor = filter.accentColor
    )

    return output
}

private fun drawStudioFrameOverlayOnCanvas(
    canvas: Canvas,
    w: Float,
    h: Float,
    frameStyle: StudioFilterFrameStyle,
    personaStage: StudioPersonaStage,
    accentColor: Color
) {
    if (frameStyle == StudioFilterFrameStyle.CLEAN || frameStyle == StudioFilterFrameStyle.VIGNETTE_FOCUS) return

    val argbAccent = android.graphics.Color.argb(
        220,
        (accentColor.red * 255).toInt(),
        (accentColor.green * 255).toInt(),
        (accentColor.blue * 255).toInt()
    )

    when (frameStyle) {
        StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE -> {
            val gold = android.graphics.Color.argb(215, 234, 179, 8)
            val inset = w.coerceAtMost(h) * 0.04f
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (w.coerceAtMost(h) * 0.008f).coerceAtLeast(3f)
                color = gold
            }
            canvas.drawRect(inset, inset, w - inset, h - inset, borderPaint)

            val innerInset = inset * 1.65f
            val thinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (w.coerceAtMost(h) * 0.003f).coerceAtLeast(1.5f)
                color = android.graphics.Color.argb(150, 226, 232, 240)
            }
            canvas.drawRect(innerInset, innerInset, w - innerInset, h - innerInset, thinPaint)

            // Subtle top executive crest diamond
            val crest = Path().apply {
                moveTo(w * 0.5f, inset * 0.45f)
                lineTo(w * 0.54f, inset * 1.1f)
                lineTo(w * 0.5f, inset * 1.75f)
                lineTo(w * 0.46f, inset * 1.1f)
                close()
            }
            val crestPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = gold
            }
            canvas.drawPath(crest, crestPaint)
        }

        StudioFilterFrameStyle.CINEMA_SPROCKET -> {
            val barH = h * 0.055f
            val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(215, 10, 10, 14)
            }
            canvas.drawRect(0f, 0f, w, barH, barPaint)
            canvas.drawRect(0f, h - barH, w, h, barPaint)

            val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb(200, 248, 250, 252)
            }
            val holeW = w * 0.045f
            val holeH = barH * 0.48f
            val count = 8
            val spacing = w / count
            for (i in 0 until count) {
                val left = i * spacing + spacing * 0.28f
                canvas.drawRoundRect(
                    RectF(left, barH * 0.26f, left + holeW, barH * 0.26f + holeH),
                    4f, 4f, holePaint
                )
                canvas.drawRoundRect(
                    RectF(left, h - barH * 0.74f, left + holeW, h - barH * 0.74f + holeH),
                    4f, 4f, holePaint
                )
            }
        }

        StudioFilterFrameStyle.NEON_HUD -> {
            val strokeW = (w.coerceAtMost(h) * 0.009f).coerceAtLeast(3.5f)
            val hudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = strokeW
                strokeCap = Paint.Cap.ROUND
                shader = LinearGradient(
                    0f, 0f, w, h,
                    intArrayOf(
                        android.graphics.Color.parseColor("#06B6D4"),
                        android.graphics.Color.parseColor("#EC4899"),
                        android.graphics.Color.parseColor("#10B981")
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            val pad = w.coerceAtMost(h) * 0.055f
            val len = w.coerceAtMost(h) * 0.13f
            // Corner brackets
            canvas.drawLine(pad, pad, pad + len, pad, hudPaint)
            canvas.drawLine(pad, pad, pad, pad + len, hudPaint)
            canvas.drawLine(w - pad, pad, w - pad - len, pad, hudPaint)
            canvas.drawLine(w - pad, pad, w - pad, pad + len, hudPaint)
            canvas.drawLine(pad, h - pad, pad + len, h - pad, hudPaint)
            canvas.drawLine(pad, h - pad, pad, h - pad - len, hudPaint)
            canvas.drawLine(w - pad, h - pad, w - pad - len, h - pad, hudPaint)
            canvas.drawLine(w - pad, h - pad, w - pad, h - pad - len, hudPaint)
        }

        StudioFilterFrameStyle.PERSONA_SIGNATURE -> {
            val strokeW = (w.coerceAtMost(h) * 0.018f).coerceAtLeast(5f)
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = strokeW
                color = argbAccent
            }
            val half = strokeW / 2f
            canvas.drawRoundRect(
                RectF(half, half, w - half, h - half),
                w.coerceAtMost(h) * 0.06f,
                w.coerceAtMost(h) * 0.06f,
                borderPaint
            )
            // Corner dot accents matched to persona
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = argbAccent
            }
            val inset = strokeW * 2.1f
            val r = (w.coerceAtMost(h) * 0.014f).coerceAtLeast(4f)
            canvas.drawCircle(inset, inset, r, dotPaint)
            canvas.drawCircle(w - inset, inset, r, dotPaint)
            canvas.drawCircle(inset, h - inset, r, dotPaint)
            canvas.drawCircle(w - inset, h - inset, r, dotPaint)

            if (personaStage == StudioPersonaStage.KID) {
                val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = android.graphics.Color.argb(220, 250, 204, 21)
                }
                canvas.drawCircle(w * 0.14f, h * 0.14f, r * 1.4f, starPaint)
                canvas.drawCircle(w * 0.86f, h * 0.16f, r * 1.1f, starPaint)
            }
        }

        StudioFilterFrameStyle.CLEAN,
        StudioFilterFrameStyle.VIGNETTE_FOCUS -> Unit
    }
}
