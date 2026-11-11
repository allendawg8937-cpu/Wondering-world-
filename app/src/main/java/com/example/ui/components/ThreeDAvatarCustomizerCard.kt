package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class AvatarAccessoryCategory(val label: String, val emoji: String) {
    HEADGEAR("Headgear", "👑"),
    EYEWEAR("Visors & Eyes", "🥽"),
    AURA_ORBIT("3D Aura & Pets", "🛸"),
    OUTFIT_RIG("Armor & Rig", "⚡")
}

data class ThreeDAvatarAccessoryOption(
    val id: String,
    val category: AvatarAccessoryCategory,
    val title: String,
    val emoji: String,
    val badgeSymbol: String,
    val description: String
)

data class AvatarColorSwatch(
    val name: String,
    val hex: String,
    val color: Color
)

private val AVATAR_3D_ACCESSORIES = listOf(
    // HEADGEAR
    ThreeDAvatarAccessoryOption("head_none", AvatarAccessoryCategory.HEADGEAR, "No Headgear", "✨", "🌟", "Clean 3D sculpted head mesh"),
    ThreeDAvatarAccessoryOption("head_crown", AvatarAccessoryCategory.HEADGEAR, "6D Sovereign Crown", "👑", "👑", "Floating golden-neon multi-spire 3D crown"),
    ThreeDAvatarAccessoryOption("head_halo", AvatarAccessoryCategory.HEADGEAR, "Quantum Halo Ring", "😇", "😇", "Tilted luminous torus ring hovering above the head"),
    ThreeDAvatarAccessoryOption("head_headphones", AvatarAccessoryCategory.HEADGEAR, "Bass-Pulse 3D Cans", "🎧", "🎧", "Over-ear studio headphones with reactive neon rings"),
    ThreeDAvatarAccessoryOption("head_helmet", AvatarAccessoryCategory.HEADGEAR, "Orbital Astro Dome", "🧑‍🚀", "🚀", "Transparent 3D glass space helmet with specular rim"),
    ThreeDAvatarAccessoryOption("head_beret", AvatarAccessoryCategory.HEADGEAR, "Honor Vanguard Crest", "🎖️", "🛡️", "Tactical holographic crest & star emblem"),

    // EYEWEAR
    ThreeDAvatarAccessoryOption("eye_visor", AvatarAccessoryCategory.EYEWEAR, "Cyber Laser Visor", "🥽", "🥽", "Wrap-around 3D glowing synthwave visor"),
    ThreeDAvatarAccessoryOption("eye_shades", AvatarAccessoryCategory.EYEWEAR, "Neon Aviator Shades", "🕶️", "🕶️", "Dual-lens reflective geometric sunglasses"),
    ThreeDAvatarAccessoryOption("eye_hud", AvatarAccessoryCategory.EYEWEAR, "6D AR HUD Monocle", "👁️", "🔮", "Holographic targeting reticle over the right eye"),
    ThreeDAvatarAccessoryOption("eye_pixel", AvatarAccessoryCategory.EYEWEAR, "Retro Voxel Glasses", "👾", "👾", "8-bit pixelated 3D shades"),
    ThreeDAvatarAccessoryOption("eye_none", AvatarAccessoryCategory.EYEWEAR, "Luminous Cyber Eyes", "⚡", "⚡", "Natural glowing 3D avatar eyes"),

    // AURA & ORBIT
    ThreeDAvatarAccessoryOption("aura_drones", AvatarAccessoryCategory.AURA_ORBIT, "Dual Orbiting Drones", "🛸", "🛸", "Two 3D companion satellites orbiting in perspective"),
    ThreeDAvatarAccessoryOption("aura_wings", AvatarAccessoryCategory.AURA_ORBIT, "Holo Energy Wings", "🦋", "🦋", "Articulated geometric light wings behind shoulders"),
    ThreeDAvatarAccessoryOption("aura_stars", AvatarAccessoryCategory.AURA_ORBIT, "Supernova Starfield", "🌟", "🌟", "Orbiting 6D constellation nodes & stardust"),
    ThreeDAvatarAccessoryOption("aura_shield", AvatarAccessoryCategory.AURA_ORBIT, "Equal Forcefield", "🛡️", "🛡️", "Hexagonal 3D wireframe energy dome"),
    ThreeDAvatarAccessoryOption("aura_none", AvatarAccessoryCategory.AURA_ORBIT, "Minimal Pedestal", "⭕", "✨", "Clean 3D projection grid"),

    // OUTFIT / RIG
    ThreeDAvatarAccessoryOption("rig_pauldrons", AvatarAccessoryCategory.OUTFIT_RIG, "Vanguard Pauldrons", "⚡", "⚡", "Sculpted 3D cyber shoulder armor plates"),
    ThreeDAvatarAccessoryOption("rig_cape", AvatarAccessoryCategory.OUTFIT_RIG, "Cyber Mantle Cape", "🦸", "🦸", "Flowing gradient holo-cape with glowing collar"),
    ThreeDAvatarAccessoryOption("rig_medallion", AvatarAccessoryCategory.OUTFIT_RIG, "EqualCoins Chain", "🪙", "🪙", "3D EqualCoins emblem suspended on a neon chain"),
    ThreeDAvatarAccessoryOption("rig_collar", AvatarAccessoryCategory.OUTFIT_RIG, "Creator High-Collar", "🧥", "🎨", "High-tech upturned neon jacket collar"),
    ThreeDAvatarAccessoryOption("rig_none", AvatarAccessoryCategory.OUTFIT_RIG, "Sleek Core Suit", "👕", "🌍", "Streamlined 3D bodyshell")
)

private val AVATAR_COLOR_PALETTE = listOf(
    AvatarColorSwatch("Holo Cyan", "#00E5FF", Color(0xFF00E5FF)),
    AvatarColorSwatch("Cyber Emerald", "#00E676", Color(0xFF00E676)),
    AvatarColorSwatch("Neon Magenta", "#FF2E93", Color(0xFFFF2E93)),
    AvatarColorSwatch("Solar Gold", "#FFB300", Color(0xFFFFB300)),
    AvatarColorSwatch("Electric Violet", "#8B5CF6", Color(0xFF8B5CF6)),
    AvatarColorSwatch("Crimson Pulse", "#FF1744", Color(0xFFFF1744)),
    AvatarColorSwatch("Arctic Cobalt", "#38BDF8", Color(0xFF38BDF8)),
    AvatarColorSwatch("Titanium Pearl", "#E2E8F0", Color(0xFFE2E8F0))
)

/**
 * Interactive 3D Avatar Customizer interface using Jetpack Compose 3D capabilities
 * (`graphicsLayer` 3D rotationX/rotationY/rotationZ/cameraDistance + perspective 3D Canvas projection).
 * Allows users to customize headgear, visors, 3D orbiting auras, shoulder rigs, and multi-zone colors
 * for their profile identity, and bake/apply the customized 3D avatar directly to their profile.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThreeDAvatarCustomizerCard(
    userDisplayName: String,
    currentThemeHex: String,
    currentAvatarEmoji: String,
    onApplyPrimaryThemeHex: (String) -> Unit,
    onApplyAvatarBadgeEmoji: (String) -> Unit,
    onApplyCustomizedAvatarBitmap: (Bitmap) -> Unit,
    onToggleAvatarAccessoryTag: (String) -> Unit = {},
    onShareAvatarToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    // Multi-zone colors
    var coreSuitHex by remember(currentThemeHex) {
        mutableStateOf(currentThemeHex.ifBlank { "#00E5FF" })
    }
    var visorGlowHex by remember { mutableStateOf("#FF2E93") }
    var auraOrbitHex by remember { mutableStateOf("#00E676") }
    var activeColorZone by remember { mutableIntStateOf(0) } // 0 = Suit Core, 1 = Visor/Accessory Glow, 2 = 3D Aura & Pedestal

    // Selected Accessories per category
    var selectedHeadgearId by remember { mutableStateOf("head_crown") }
    var selectedEyewearId by remember { mutableStateOf("eye_visor") }
    var selectedAuraId by remember { mutableStateOf("aura_drones") }
    var selectedRigId by remember { mutableStateOf("rig_pauldrons") }
    var activeCategory by remember { mutableStateOf(AvatarAccessoryCategory.HEADGEAR) }

    // 3D Transform State (Compose 3D rotationX, rotationY, rotationZ, scale, auto-turntable)
    var rotationYDeg by remember { mutableFloatStateOf(14f) }
    var rotationXDeg by remember { mutableFloatStateOf(-8f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var isAutoSpinEnabled by remember { mutableStateOf(true) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_3d_turntable")
    val orbitPhaseRad by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_phase_rad"
    )
    val autoTurntableSwing by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auto_turntable_swing"
    )

    val effectiveRotationY = if (isAutoSpinEnabled) {
        (rotationYDeg + autoTurntableSwing).coerceIn(-45f, 45f)
    } else {
        rotationYDeg
    }

    val coreSuitColor = parseThemeHex(coreSuitHex)
    val visorGlowColor = parseThemeHex(visorGlowHex)
    val auraOrbitColor = parseThemeHex(auraOrbitHex)

    val selectedHeadgear = remember(selectedHeadgearId) {
        AVATAR_3D_ACCESSORIES.find { it.id == selectedHeadgearId } ?: AVATAR_3D_ACCESSORIES.first()
    }
    val selectedEyewear = remember(selectedEyewearId) {
        AVATAR_3D_ACCESSORIES.find { it.id == selectedEyewearId } ?: AVATAR_3D_ACCESSORIES[6]
    }
    val selectedAura = remember(selectedAuraId) {
        AVATAR_3D_ACCESSORIES.find { it.id == selectedAuraId } ?: AVATAR_3D_ACCESSORIES[11]
    }
    val selectedRig = remember(selectedRigId) {
        AVATAR_3D_ACCESSORIES.find { it.id == selectedRigId } ?: AVATAR_3D_ACCESSORIES[16]
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(
                listOf(coreSuitColor, visorGlowColor, auraOrbitColor, HoloCyan)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("three_d_avatar_customizer_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Row
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
                                Brush.linearGradient(
                                    listOf(coreSuitColor, visorGlowColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FaceRetouchingNatural,
                            contentDescription = "3D Avatar Customizer",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "3D Avatar Identity Customizer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Compose 3D Perspective Mesh • Drag to Orbit • Multi-Zone Colors & Accessories",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = coreSuitColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, coreSuitColor.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { isAutoSpinEnabled = !isAutoSpinEnabled }
                        .testTag("avatar_3d_autospin_toggle")
                ) {
                    Text(
                        text = if (isAutoSpinEnabled) "3D AUTO-ORBIT ON" else "MANUAL 3D TILT",
                        style = MaterialTheme.typography.labelSmall,
                        color = coreSuitColor,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // 2. Interactive 3D Viewport Stage (Supports Touch Drag to Rotate in 3D + Compose graphicsLayer)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF060913),
                                Color(0xFF0E172A),
                                Color(0xFF070B16)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(coreSuitColor.copy(alpha = 0.85f), visorGlowColor.copy(alpha = 0.85f), auraOrbitColor.copy(alpha = 0.85f))
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            isAutoSpinEnabled = false
                            rotationYDeg = (rotationYDeg + dragAmount.x * 0.25f).coerceIn(-45f, 45f)
                            rotationXDeg = (rotationXDeg - dragAmount.y * 0.20f).coerceIn(-30f, 30f)
                        }
                    }
                    .testTag("avatar_3d_interactive_viewport"),
                contentAlignment = Alignment.Center
            ) {
                // Compose 3D Transformed Stage Layer
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = effectiveRotationY
                            rotationX = rotationXDeg
                            scaleX = zoomScale
                            scaleY = zoomScale
                            cameraDistance = 14f * density
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h * 0.50f
                    val yawRad = Math.toRadians(effectiveRotationY.toDouble()).toFloat()
                    val pitchRad = Math.toRadians(rotationXDeg.toDouble()).toFloat()
                    val parallaxX = sin(yawRad) * 28f
                    val parallaxY = sin(pitchRad) * 18f

                    // A. 3D Perspective Floor Pedestal Grid
                    val pedestalY = h * 0.84f
                    for (ringIdx in 1..3) {
                        val rx = w * (0.14f + ringIdx * 0.08f)
                        val ry = h * (0.035f + ringIdx * 0.015f)
                        drawOval(
                            color = auraOrbitColor.copy(alpha = (0.45f - ringIdx * 0.1f).coerceAtLeast(0.12f)),
                            topLeft = Offset(cx - rx, pedestalY - ry + parallaxY * 0.3f),
                            size = Size(rx * 2f, ry * 2f),
                            style = Stroke(width = (3.5f - ringIdx * 0.7f))
                        )
                    }

                    // Radial floor beams
                    for (angleDeg in 0 until 360 step 45) {
                        val rad = Math.toRadians((angleDeg + effectiveRotationY * 0.8).toDouble()).toFloat()
                        val endX = cx + cos(rad) * (w * 0.36f)
                        val endY = pedestalY + sin(rad) * (h * 0.08f)
                        drawLine(
                            color = auraOrbitColor.copy(alpha = 0.22f),
                            start = Offset(cx, pedestalY),
                            end = Offset(endX, endY),
                            strokeWidth = 1.5f
                        )
                    }

                    // B. Background 3D Aura & Wings Layer (Behind Avatar)
                    when (selectedAuraId) {
                        "aura_wings" -> {
                            // Left & Right articulated 3D Holographic Energy Wings
                            for (side in listOf(-1f, 1f)) {
                                for (feather in 0..2) {
                                    val wingPath = Path().apply {
                                        val startX = cx + side * 26f + parallaxX * 0.4f
                                        val startY = cy + 16f + feather * 14f
                                        moveTo(startX, startY)
                                        lineTo(
                                            cx + side * (115f - feather * 16f) + parallaxX * 0.8f,
                                            cy - 42f + feather * 26f + parallaxY
                                        )
                                        lineTo(
                                            cx + side * (86f - feather * 12f) + parallaxX * 0.6f,
                                            cy + 34f + feather * 22f + parallaxY
                                        )
                                        close()
                                    }
                                    drawPath(
                                        path = wingPath,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                auraOrbitColor.copy(alpha = 0.65f - feather * 0.15f),
                                                visorGlowColor.copy(alpha = 0.25f)
                                            )
                                        )
                                    )
                                    drawPath(
                                        path = wingPath,
                                        color = auraOrbitColor,
                                        style = Stroke(width = 2f)
                                    )
                                }
                            }
                        }
                        "aura_shield" -> {
                            drawCircle(
                                color = auraOrbitColor.copy(alpha = 0.14f),
                                radius = 98f,
                                center = Offset(cx + parallaxX * 0.3f, cy + parallaxY * 0.3f)
                            )
                            drawCircle(
                                color = auraOrbitColor.copy(alpha = 0.65f),
                                radius = 98f,
                                center = Offset(cx + parallaxX * 0.3f, cy + parallaxY * 0.3f),
                                style = Stroke(width = 2.5f)
                            )
                        }
                        "aura_stars" -> {
                            for (idx in 0 until 8) {
                                val starAngle = orbitPhaseRad + idx * (Math.PI / 4f).toFloat()
                                val sx = cx + cos(starAngle) * 105f
                                val sy = cy + sin(starAngle) * 62f
                                drawCircle(
                                    color = auraOrbitColor,
                                    radius = if (idx % 2 == 0) 5.5f else 3.5f,
                                    center = Offset(sx, sy)
                                )
                            }
                        }
                    }

                    // C. Outfit / Cape Back Layer
                    if (selectedRigId == "rig_cape") {
                        val capePath = Path().apply {
                            moveTo(cx - 52f + parallaxX * 0.4f, cy + 32f)
                            lineTo(cx + 52f + parallaxX * 0.4f, cy + 32f)
                            lineTo(cx + 82f - parallaxX * 0.5f, pedestalY + 6f)
                            lineTo(cx - 82f - parallaxX * 0.5f, pedestalY + 6f)
                            close()
                        }
                        drawPath(
                            path = capePath,
                            brush = Brush.verticalGradient(
                                listOf(visorGlowColor.copy(alpha = 0.75f), coreSuitColor.copy(alpha = 0.2f))
                            )
                        )
                    }

                    // D. 3D Sculpted Torso / Shoulders Bust
                    val torsoPath = Path().apply {
                        moveTo(cx - 68f + parallaxX * 0.4f, pedestalY - 6f)
                        cubicTo(
                            cx - 64f + parallaxX * 0.5f, cy + 36f,
                            cx - 32f + parallaxX * 0.5f, cy + 28f,
                            cx + parallaxX * 0.5f, cy + 28f
                        )
                        cubicTo(
                            cx + 32f + parallaxX * 0.5f, cy + 28f,
                            cx + 64f + parallaxX * 0.5f, cy + 36f,
                            cx + 68f + parallaxX * 0.4f, pedestalY - 6f
                        )
                        close()
                    }
                    drawPath(
                        path = torsoPath,
                        brush = Brush.linearGradient(
                            listOf(
                                coreSuitColor,
                                coreSuitColor.copy(alpha = 0.45f),
                                Color(0xFF1E293B)
                            )
                        )
                    )
                    drawPath(
                        path = torsoPath,
                        color = Color.White.copy(alpha = 0.45f),
                        style = Stroke(width = 2f)
                    )

                    // Outfit Rig Overlays (Pauldrons, Medallion, High Collar)
                    when (selectedRigId) {
                        "rig_pauldrons" -> {
                            for (side in listOf(-1f, 1f)) {
                                drawRoundRect(
                                    color = visorGlowColor.copy(alpha = 0.85f),
                                    topLeft = Offset(cx + side * 56f - 22f + parallaxX * 0.55f, cy + 28f),
                                    size = Size(44f, 26f),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(cx + side * 56f - 22f + parallaxX * 0.55f, cy + 28f),
                                    size = Size(44f, 26f),
                                    cornerRadius = CornerRadius(12f, 12f),
                                    style = Stroke(width = 2f)
                                )
                            }
                        }
                        "rig_medallion" -> {
                            drawLine(
                                color = SolarAmber,
                                start = Offset(cx - 24f + parallaxX * 0.5f, cy + 32f),
                                end = Offset(cx + parallaxX * 0.65f, cy + 64f),
                                strokeWidth = 3.5f
                            )
                            drawLine(
                                color = SolarAmber,
                                start = Offset(cx + 24f + parallaxX * 0.5f, cy + 32f),
                                end = Offset(cx + parallaxX * 0.65f, cy + 64f),
                                strokeWidth = 3.5f
                            )
                            drawCircle(
                                color = SolarAmber,
                                radius = 13f,
                                center = Offset(cx + parallaxX * 0.65f, cy + 66f)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 13f,
                                center = Offset(cx + parallaxX * 0.65f, cy + 66f),
                                style = Stroke(width = 2f)
                            )
                        }
                        "rig_collar" -> {
                            val collarPath = Path().apply {
                                moveTo(cx - 38f + parallaxX * 0.5f, cy + 38f)
                                lineTo(cx - 48f + parallaxX * 0.6f, cy + 8f)
                                lineTo(cx - 22f + parallaxX * 0.5f, cy + 28f)
                                moveTo(cx + 38f + parallaxX * 0.5f, cy + 38f)
                                lineTo(cx + 48f + parallaxX * 0.6f, cy + 8f)
                                lineTo(cx + 22f + parallaxX * 0.5f, cy + 28f)
                            }
                            drawPath(
                                path = collarPath,
                                color = visorGlowColor,
                                style = Stroke(width = 4f, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // E. 3D Sculpted Head Sphere with Specular 3D Shading
                    val headCenterX = cx + parallaxX * 0.75f
                    val headCenterY = cy - 16f + parallaxY * 0.75f
                    val headRadius = 42f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.90f),
                                coreSuitColor,
                                Color(0xFF0F172A)
                            ),
                            center = Offset(headCenterX - 14f + parallaxX * 0.2f, headCenterY - 14f),
                            radius = headRadius * 1.35f
                        ),
                        radius = headRadius,
                        center = Offset(headCenterX, headCenterY)
                    )
                    drawCircle(
                        color = coreSuitColor.copy(alpha = 0.9f),
                        radius = headRadius,
                        center = Offset(headCenterX, headCenterY),
                        style = Stroke(width = 2.2f)
                    )

                    // F. 3D Eyewear / Visor Layer (With Extra Forward Parallax for True 3D Depth)
                    val faceFrontX = headCenterX + parallaxX * 0.32f
                    val faceFrontY = headCenterY + parallaxY * 0.25f

                    when (selectedEyewearId) {
                        "eye_visor" -> {
                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    listOf(visorGlowColor, Color.White, visorGlowColor)
                                ),
                                topLeft = Offset(faceFrontX - 30f, faceFrontY - 10f),
                                size = Size(60f, 16f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(faceFrontX - 30f, faceFrontY - 10f),
                                size = Size(60f, 16f),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(width = 2f)
                            )
                        }
                        "eye_shades" -> {
                            drawRoundRect(
                                color = visorGlowColor,
                                topLeft = Offset(faceFrontX - 28f, faceFrontY - 10f),
                                size = Size(24f, 15f),
                                cornerRadius = CornerRadius(5f, 5f)
                            )
                            drawRoundRect(
                                color = visorGlowColor,
                                topLeft = Offset(faceFrontX + 4f, faceFrontY - 10f),
                                size = Size(24f, 15f),
                                cornerRadius = CornerRadius(5f, 5f)
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(faceFrontX - 4f, faceFrontY - 4f),
                                end = Offset(faceFrontX + 4f, faceFrontY - 4f),
                                strokeWidth = 2.5f
                            )
                        }
                        "eye_hud" -> {
                            // Left normal cyber eye
                            drawCircle(
                                color = Color.White,
                                radius = 5f,
                                center = Offset(faceFrontX - 14f, faceFrontY - 2f)
                            )
                            // Right 6D AR HUD Monocle
                            drawCircle(
                                color = visorGlowColor.copy(alpha = 0.38f),
                                radius = 14f,
                                center = Offset(faceFrontX + 14f, faceFrontY - 2f)
                            )
                            drawCircle(
                                color = visorGlowColor,
                                radius = 14f,
                                center = Offset(faceFrontX + 14f, faceFrontY - 2f),
                                style = Stroke(width = 2.5f)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = Offset(faceFrontX + 14f, faceFrontY - 2f)
                            )
                        }
                        "eye_pixel" -> {
                            for (b in -2..2) {
                                if (b != 0) {
                                    drawRect(
                                        color = visorGlowColor,
                                        topLeft = Offset(faceFrontX + b * 11f - 5f, faceFrontY - 8f),
                                        size = Size(10f, 10f)
                                    )
                                }
                            }
                        }
                        else -> {
                            drawCircle(
                                color = visorGlowColor,
                                radius = 5.5f,
                                center = Offset(faceFrontX - 14f, faceFrontY - 2f)
                            )
                            drawCircle(
                                color = visorGlowColor,
                                radius = 5.5f,
                                center = Offset(faceFrontX + 14f, faceFrontY - 2f)
                            )
                        }
                    }

                    // G. 3D Headgear Layer
                    when (selectedHeadgearId) {
                        "head_crown" -> {
                            val crownBaseY = headCenterY - headRadius + 4f
                            val crownPath = Path().apply {
                                moveTo(headCenterX - 28f, crownBaseY)
                                lineTo(headCenterX - 24f, crownBaseY - 24f)
                                lineTo(headCenterX - 10f, crownBaseY - 10f)
                                lineTo(headCenterX, crownBaseY - 30f)
                                lineTo(headCenterX + 10f, crownBaseY - 10f)
                                lineTo(headCenterX + 24f, crownBaseY - 24f)
                                lineTo(headCenterX + 28f, crownBaseY)
                                close()
                            }
                            drawPath(
                                path = crownPath,
                                brush = Brush.verticalGradient(listOf(SolarAmber, visorGlowColor))
                            )
                            drawPath(
                                path = crownPath,
                                color = Color.White,
                                style = Stroke(width = 2f)
                            )
                        }
                        "head_halo" -> {
                            drawOval(
                                color = SolarAmber,
                                topLeft = Offset(headCenterX - 36f, headCenterY - headRadius - 22f),
                                size = Size(72f, 18f),
                                style = Stroke(width = 4.5f)
                            )
                            drawOval(
                                color = Color.White,
                                topLeft = Offset(headCenterX - 36f, headCenterY - headRadius - 22f),
                                size = Size(72f, 18f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                        "head_headphones" -> {
                            // Headband arc
                            drawArc(
                                color = visorGlowColor,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(headCenterX - 46f, headCenterY - 46f),
                                size = Size(92f, 84f),
                                style = Stroke(width = 6f, cap = StrokeCap.Round)
                            )
                            // Ear cups
                            drawRoundRect(
                                color = visorGlowColor,
                                topLeft = Offset(headCenterX - 52f, headCenterY - 12f),
                                size = Size(14f, 28f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                            drawRoundRect(
                                color = visorGlowColor,
                                topLeft = Offset(headCenterX + 38f, headCenterY - 12f),
                                size = Size(14f, 28f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                        "head_helmet" -> {
                            drawCircle(
                                color = HoloCyan.copy(alpha = 0.22f),
                                radius = headRadius + 14f,
                                center = Offset(headCenterX, headCenterY)
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.75f),
                                radius = headRadius + 14f,
                                center = Offset(headCenterX, headCenterY),
                                style = Stroke(width = 2.5f)
                            )
                        }
                        "head_beret" -> {
                            drawOval(
                                color = visorGlowColor,
                                topLeft = Offset(headCenterX - 34f, headCenterY - headRadius - 8f),
                                size = Size(68f, 22f)
                            )
                            drawCircle(
                                color = SolarAmber,
                                radius = 5f,
                                center = Offset(headCenterX, headCenterY - headRadius + 3f)
                            )
                        }
                    }

                    // H. Foreground 3D Orbiting Drones (When selected)
                    if (selectedAuraId == "aura_drones") {
                        for (droneIdx in 0..1) {
                            val angle = orbitPhaseRad + droneIdx * Math.PI.toFloat()
                            val dx = cx + cos(angle) * 96f
                            val dy = cy + sin(angle) * 28f
                            drawCircle(
                                color = auraOrbitColor.copy(alpha = 0.35f),
                                radius = 14f,
                                center = Offset(dx, dy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6f,
                                center = Offset(dx, dy)
                            )
                            drawCircle(
                                color = auraOrbitColor,
                                radius = 10f,
                                center = Offset(dx, dy),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }

                // Top-Left Overlay HUD Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.68f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, coreSuitColor.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${selectedHeadgear.emoji} ${selectedEyewear.emoji} ${selectedAura.emoji} ${selectedRig.emoji} • Y:${effectiveRotationY.toInt()}° X:${rotationXDeg.toInt()}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Bottom-Right Quick Reset / Randomize Row
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .clickable {
                                rotationYDeg = 0f
                                rotationXDeg = 0f
                                zoomScale = 1.0f
                                isAutoSpinEnabled = true
                            }
                            .testTag("avatar_3d_reset_camera_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset 3D Camera",
                                tint = HoloCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Reset 3D View",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .clickable {
                                val heads = AVATAR_3D_ACCESSORIES.filter { it.category == AvatarAccessoryCategory.HEADGEAR }
                                val eyes = AVATAR_3D_ACCESSORIES.filter { it.category == AvatarAccessoryCategory.EYEWEAR }
                                val auras = AVATAR_3D_ACCESSORIES.filter { it.category == AvatarAccessoryCategory.AURA_ORBIT }
                                val rigs = AVATAR_3D_ACCESSORIES.filter { it.category == AvatarAccessoryCategory.OUTFIT_RIG }
                                selectedHeadgearId = heads[Random.nextInt(heads.size)].id
                                selectedEyewearId = eyes[Random.nextInt(eyes.size)].id
                                selectedAuraId = auras[Random.nextInt(auras.size)].id
                                selectedRigId = rigs[Random.nextInt(rigs.size)].id
                                coreSuitHex = AVATAR_COLOR_PALETTE[Random.nextInt(AVATAR_COLOR_PALETTE.size)].hex
                                visorGlowHex = AVATAR_COLOR_PALETTE[Random.nextInt(AVATAR_COLOR_PALETTE.size)].hex
                                auraOrbitHex = AVATAR_COLOR_PALETTE[Random.nextInt(AVATAR_COLOR_PALETTE.size)].hex
                                statusBanner = "🎲 Randomized 3D Avatar accessories & multi-zone colors!"
                            }
                            .testTag("avatar_3d_randomize_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Randomize 3D Avatar",
                                tint = SolarAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Randomize",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. 3D Rotation & Zoom Precision Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "3D Yaw Orbit (${effectiveRotationY.toInt()}°)",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = rotationYDeg,
                        onValueChange = {
                            isAutoSpinEnabled = false
                            rotationYDeg = it
                        },
                        valueRange = -45f..45f,
                        colors = SliderDefaults.colors(
                            thumbColor = HoloCyan,
                            activeTrackColor = HoloCyan
                        ),
                        modifier = Modifier.testTag("avatar_3d_yaw_slider")
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "3D Zoom (${String.format(java.util.Locale.US, "%.1fx", zoomScale)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = zoomScale,
                        onValueChange = { zoomScale = it },
                        valueRange = 0.8f..1.35f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberEmerald,
                            activeTrackColor = CyberEmerald
                        ),
                        modifier = Modifier.testTag("avatar_3d_zoom_slider")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // 4. Accessory Category Tabs & Selector Chips
            Text(
                text = "1. Select 3D Avatar Accessories",
                style = MaterialTheme.typography.labelMedium,
                color = HoloCyan,
                fontWeight = FontWeight.ExtraBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarAccessoryCategory.entries.forEach { category ->
                    val isSelected = activeCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeCategory = category },
                        label = {
                            Text(
                                text = "${category.emoji} ${category.label}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = coreSuitColor.copy(alpha = 0.22f),
                            selectedLabelColor = coreSuitColor
                        ),
                        modifier = Modifier.testTag("avatar_3d_category_${category.name.lowercase()}")
                    )
                }
            }

            val categoryOptions = remember(activeCategory) {
                AVATAR_3D_ACCESSORIES.filter { it.category == activeCategory }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryOptions.forEach { option ->
                    val isSelected = when (option.category) {
                        AvatarAccessoryCategory.HEADGEAR -> selectedHeadgearId == option.id
                        AvatarAccessoryCategory.EYEWEAR -> selectedEyewearId == option.id
                        AvatarAccessoryCategory.AURA_ORBIT -> selectedAuraId == option.id
                        AvatarAccessoryCategory.OUTFIT_RIG -> selectedRigId == option.id
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            when (option.category) {
                                AvatarAccessoryCategory.HEADGEAR -> selectedHeadgearId = option.id
                                AvatarAccessoryCategory.EYEWEAR -> selectedEyewearId = option.id
                                AvatarAccessoryCategory.AURA_ORBIT -> selectedAuraId = option.id
                                AvatarAccessoryCategory.OUTFIT_RIG -> selectedRigId = option.id
                            }
                            onToggleAvatarAccessoryTag(option.title)
                        },
                        label = {
                            Text(
                                text = "${option.emoji} ${option.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = visorGlowColor.copy(alpha = 0.24f),
                            selectedLabelColor = visorGlowColor
                        ),
                        modifier = Modifier.testTag("avatar_3d_accessory_${option.id}")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // 5. Multi-Zone Color Customizer
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
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "2. Customize 3D Color Zones",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "Suit & Core",
                    1 to "Visor & Trim",
                    2 to "3D Aura & Grid"
                ).forEach { (zoneIdx, zoneLabel) ->
                    val isZoneActive = activeColorZone == zoneIdx
                    val zonePreviewColor = when (zoneIdx) {
                        0 -> coreSuitColor
                        1 -> visorGlowColor
                        else -> auraOrbitColor
                    }
                    FilterChip(
                        selected = isZoneActive,
                        onClick = { activeColorZone = zoneIdx },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(zonePreviewColor)
                                )
                                Text(
                                    text = zoneLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isZoneActive) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            }
                        },
                        modifier = Modifier.testTag("avatar_3d_color_zone_$zoneIdx")
                    )
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AVATAR_COLOR_PALETTE.forEach { swatch ->
                    val activeHexForZone = when (activeColorZone) {
                        0 -> coreSuitHex
                        1 -> visorGlowHex
                        else -> auraOrbitHex
                    }
                    val isSwatchSelected = activeHexForZone.equals(swatch.hex, ignoreCase = true)
                    Surface(
                        color = swatch.color.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = if (isSwatchSelected) 2.dp else 1.dp,
                            color = if (isSwatchSelected) Color.White else swatch.color.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .clickable {
                                when (activeColorZone) {
                                    0 -> {
                                        coreSuitHex = swatch.hex
                                        onApplyPrimaryThemeHex(swatch.hex)
                                    }
                                    1 -> visorGlowHex = swatch.hex
                                    else -> auraOrbitHex = swatch.hex
                                }
                            }
                            .testTag("avatar_3d_swatch_${swatch.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(swatch.color)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Text(
                                text = swatch.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSwatchSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            statusBanner?.let { msg ->
                Surface(
                    color = CyberEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 6. Primary Action Buttons: Bake & Apply 3D Avatar to Profile Identity + Share to Community Wall
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val renderedBitmap = renderCustomized3DAvatarBitmap(
                            coreColor = coreSuitColor,
                            visorColor = visorGlowColor,
                            auraColor = auraOrbitColor,
                            headgearId = selectedHeadgearId,
                            eyewearId = selectedEyewearId,
                            auraId = selectedAuraId,
                            rigId = selectedRigId,
                            yawDeg = effectiveRotationY
                        )
                        onApplyCustomizedAvatarBitmap(renderedBitmap)
                        onApplyPrimaryThemeHex(coreSuitHex)
                        onApplyAvatarBadgeEmoji(selectedHeadgear.badgeSymbol)
                        statusBanner = "✅ Applied your custom 3D Avatar (${selectedHeadgear.title} + ${selectedEyewear.title}) & colors to your Profile Identity!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("avatar_3d_apply_to_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FaceRetouchingNatural,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply 3D Avatar to Profile",
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        val headline = "✨ New 3D Avatar Loadout for ${userDisplayName.ifBlank { "Jordan Vance" }}!"
                        val body = "Customized my 3D Profile Identity with ${selectedHeadgear.emoji} ${selectedHeadgear.title}, ${selectedEyewear.emoji} ${selectedEyewear.title}, ${selectedAura.emoji} ${selectedAura.title}, and ${selectedRig.emoji} ${selectedRig.title} in ${coreSuitHex.uppercase()}!"
                        onShareAvatarToWall(headline, body)
                        statusBanner = "Shared your 3D Avatar loadout to the Community Wall!"
                    },
                    modifier = Modifier.testTag("avatar_3d_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Renders the user's customized 3D Avatar (accessories, multi-zone colors, lighting, and perspective tilt)
 * into a crisp 480x480 Android [Bitmap] so it can immediately be set as their active profile portrait.
 */
private fun renderCustomized3DAvatarBitmap(
    coreColor: Color,
    visorColor: Color,
    auraColor: Color,
    headgearId: String,
    eyewearId: String,
    auraId: String,
    rigId: String,
    yawDeg: Float
): Bitmap {
    val sizePx = 480
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val cx = sizePx / 2f
    val cy = sizePx * 0.50f
    val parallaxX = sin(Math.toRadians(yawDeg.toDouble())).toFloat() * 34f

    // 1. Deep 3D Space Background
    paint.shader = LinearGradient(
        0f, 0f, 0f, sizePx.toFloat(),
        intArrayOf(0xFF060913.toInt(), 0xFF0E172A.toInt(), 0xFF070B16.toInt()),
        null,
        Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)
    paint.shader = null

    // 2. Aura & Pedestal Glow Ring
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 6f
    paint.color = auraColor.copy(alpha = 0.65f).toArgb()
    canvas.drawOval(RectF(cx - 170f, sizePx * 0.80f, cx + 170f, sizePx * 0.90f), paint)

    if (auraId == "aura_shield" || auraId == "aura_wings") {
        paint.color = auraColor.copy(alpha = 0.35f).toArgb()
        paint.strokeWidth = 5f
        canvas.drawCircle(cx, cy, 175f, paint)
    }

    // 3. Sculpted 3D Shoulders / Torso
    paint.style = Paint.Style.FILL
    paint.shader = LinearGradient(
        cx - 120f, cy + 40f, cx + 120f, sizePx.toFloat(),
        coreColor.toArgb(),
        0xFF1E293B.toInt(),
        Shader.TileMode.CLAMP
    )
    canvas.drawRoundRect(
        RectF(cx - 125f + parallaxX * 0.4f, cy + 54f, cx + 125f + parallaxX * 0.4f, sizePx * 0.92f),
        64f,
        64f,
        paint
    )
    paint.shader = null

    if (rigId == "rig_pauldrons") {
        paint.color = visorColor.toArgb()
        canvas.drawRoundRect(RectF(cx - 145f + parallaxX * 0.5f, cy + 50f, cx - 75f + parallaxX * 0.5f, cy + 98f), 22f, 22f, paint)
        canvas.drawRoundRect(RectF(cx + 75f + parallaxX * 0.5f, cy + 50f, cx + 145f + parallaxX * 0.5f, cy + 98f), 22f, 22f, paint)
    } else if (rigId == "rig_medallion") {
        paint.color = SolarAmber.toArgb()
        canvas.drawCircle(cx + parallaxX * 0.6f, cy + 120f, 24f, paint)
    }

    // 4. 3D Head Sphere with Radial Highlight
    val headX = cx + parallaxX * 0.7f
    val headY = cy - 28f
    val headR = 84f
    paint.shader = RadialGradient(
        headX - 24f,
        headY - 24f,
        headR * 1.3f,
        intArrayOf(android.graphics.Color.WHITE, coreColor.toArgb(), 0xFF0F172A.toInt()),
        floatArrayOf(0.05f, 0.55f, 1.0f),
        Shader.TileMode.CLAMP
    )
    canvas.drawCircle(headX, headY, headR, paint)
    paint.shader = null

    // 5. Eyewear / Visor
    val visorX = headX + parallaxX * 0.3f
    paint.color = visorColor.toArgb()
    when (eyewearId) {
        "eye_visor" -> {
            canvas.drawRoundRect(RectF(visorX - 58f, headY - 18f, visorX + 58f, headY + 14f), 16f, 16f, paint)
        }
        "eye_shades" -> {
            canvas.drawRoundRect(RectF(visorX - 54f, headY - 18f, visorX - 6f, headY + 12f), 10f, 10f, paint)
            canvas.drawRoundRect(RectF(visorX + 6f, headY - 18f, visorX + 54f, headY + 12f), 10f, 10f, paint)
        }
        else -> {
            canvas.drawCircle(visorX - 26f, headY - 4f, 11f, paint)
            canvas.drawCircle(visorX + 26f, headY - 4f, 11f, paint)
        }
    }

    // 6. Headgear
    when (headgearId) {
        "head_crown" -> {
            val crownPath = AndroidPath().apply {
                val baseY = headY - headR + 10f
                moveTo(headX - 54f, baseY)
                lineTo(headX - 46f, baseY - 46f)
                lineTo(headX - 18f, baseY - 20f)
                lineTo(headX, baseY - 58f)
                lineTo(headX + 18f, baseY - 20f)
                lineTo(headX + 46f, baseY - 46f)
                lineTo(headX + 54f, baseY)
                close()
            }
            paint.color = SolarAmber.toArgb()
            canvas.drawPath(crownPath, paint)
        }
        "head_halo" -> {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 9f
            paint.color = SolarAmber.toArgb()
            canvas.drawOval(RectF(headX - 68f, headY - headR - 42f, headX + 68f, headY - headR - 10f), paint)
        }
        "head_helmet" -> {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.color = android.graphics.Color.WHITE
            canvas.drawCircle(headX, headY, headR + 22f, paint)
        }
    }

    return bitmap
}
