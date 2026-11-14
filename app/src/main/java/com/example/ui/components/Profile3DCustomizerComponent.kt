package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Profile3DConfig
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlin.math.cos
import kotlin.math.sin

data class Profile3DBaseModel(
    val id: String,
    val name: String,
    val archetypeTag: String,
    val emoji: String,
    @DrawableRes val thumbnailResId: Int,
    val defaultPrimaryHex: String,
    val defaultSecondaryHex: String,
    val defaultAuraHex: String,
    val shoulderSpan: Float,
    val headRadiusRatio: Float
)

enum class Profile3DAccessoryCategory(val label: String, val emoji: String) {
    HEADGEAR("Headgear", "👑"),
    EYEWEAR("Visor & Eyes", "🥽"),
    AURA("3D Aura", "🛸"),
    RIG("Armor & Rig", "⚡")
}

data class Profile3DAccessoryItem(
    val id: String,
    val category: Profile3DAccessoryCategory,
    val title: String,
    val emoji: String,
    val subtitle: String
)

data class Profile3DColorSwatch(
    val name: String,
    val hex: String,
    val color: Color
)

val PROFILE_3D_BASE_MODELS = listOf(
    Profile3DBaseModel(
        id = "cyber_astronaut",
        name = "Cyber Astronaut",
        archetypeTag = "Explorer Dome",
        emoji = "🚀",
        thumbnailResId = R.drawable.img_avatar_cyber_astronaut,
        defaultPrimaryHex = "#00E5FF",
        defaultSecondaryHex = "#FF2E93",
        defaultAuraHex = "#00E676",
        shoulderSpan = 1.0f,
        headRadiusRatio = 1.0f
    ),
    Profile3DBaseModel(
        id = "holo_scholar",
        name = "Holo Scholar",
        archetypeTag = "Quantum Mesh",
        emoji = "🎓",
        thumbnailResId = R.drawable.img_avatar_stem_scholar,
        defaultPrimaryHex = "#8B5CF6",
        defaultSecondaryHex = "#00E5FF",
        defaultAuraHex = "#FFB300",
        shoulderSpan = 0.94f,
        headRadiusRatio = 1.02f
    ),
    Profile3DBaseModel(
        id = "cosmic_pioneer",
        name = "Cosmic Pioneer",
        archetypeTag = "Crystal Frame",
        emoji = "🌌",
        thumbnailResId = R.drawable.img_avatar_adventurous,
        defaultPrimaryHex = "#38BDF8",
        defaultSecondaryHex = "#FFB300",
        defaultAuraHex = "#FF2E93",
        shoulderSpan = 1.10f,
        headRadiusRatio = 0.96f
    ),
    Profile3DBaseModel(
        id = "neon_tactician",
        name = "Neon Tactician",
        archetypeTag = "Stealth Armor",
        emoji = "⚡",
        thumbnailResId = R.drawable.img_avatar_veteran_guardian,
        defaultPrimaryHex = "#FF1744",
        defaultSecondaryHex = "#00E5FF",
        defaultAuraHex = "#FFB300",
        shoulderSpan = 1.18f,
        headRadiusRatio = 0.95f
    ),
    Profile3DBaseModel(
        id = "eco_guardian",
        name = "Eco Guardian",
        archetypeTag = "Bio Shield",
        emoji = "🌿",
        thumbnailResId = R.drawable.img_avatar_zen,
        defaultPrimaryHex = "#00E676",
        defaultSecondaryHex = "#FFB300",
        defaultAuraHex = "#00E5FF",
        shoulderSpan = 1.02f,
        headRadiusRatio = 1.0f
    ),
    Profile3DBaseModel(
        id = "street_innovator",
        name = "Street Innovator",
        archetypeTag = "Synth Rig",
        emoji = "🎧",
        thumbnailResId = R.drawable.img_avatar_neon_musician,
        defaultPrimaryHex = "#FF2E93",
        defaultSecondaryHex = "#8B5CF6",
        defaultAuraHex = "#00E5FF",
        shoulderSpan = 0.96f,
        headRadiusRatio = 1.04f
    )
)

val PROFILE_3D_ACCESSORIES = listOf(
    // Headgear
    Profile3DAccessoryItem("head_none", Profile3DAccessoryCategory.HEADGEAR, "Clean Mesh", "✨", "Pure base sculpt"),
    Profile3DAccessoryItem("head_crown", Profile3DAccessoryCategory.HEADGEAR, "6D Crown", "👑", "Multi-spire neon crown"),
    Profile3DAccessoryItem("head_halo", Profile3DAccessoryCategory.HEADGEAR, "Quantum Halo", "😇", "Hovering torus ring"),
    Profile3DAccessoryItem("head_headphones", Profile3DAccessoryCategory.HEADGEAR, "Synth Cans", "🎧", "Studio over-ear cans"),
    Profile3DAccessoryItem("head_helmet", Profile3DAccessoryCategory.HEADGEAR, "Astro Dome", "🧑‍🚀", "Glass orbital bubble"),
    Profile3DAccessoryItem("head_beret", Profile3DAccessoryCategory.HEADGEAR, "Honor Crest", "🎖️", "Vanguard star beret"),

    // Eyewear
    Profile3DAccessoryItem("eye_visor", Profile3DAccessoryCategory.EYEWEAR, "Laser Visor", "🥽", "Wrap-around neon visor"),
    Profile3DAccessoryItem("eye_shades", Profile3DAccessoryCategory.EYEWEAR, "Aviator Shades", "🕶️", "Dual-lens reflective shades"),
    Profile3DAccessoryItem("eye_hud", Profile3DAccessoryCategory.EYEWEAR, "AR HUD Lens", "👁️", "Targeting holo monocle"),
    Profile3DAccessoryItem("eye_pixel", Profile3DAccessoryCategory.EYEWEAR, "Voxel Shades", "👾", "Retro pixelated glasses"),
    Profile3DAccessoryItem("eye_none", Profile3DAccessoryCategory.EYEWEAR, "Cyber Eyes", "⚡", "Glowing optic cores"),

    // Aura
    Profile3DAccessoryItem("aura_drones", Profile3DAccessoryCategory.AURA, "Orbit Drones", "🛸", "Dual 3D satellites"),
    Profile3DAccessoryItem("aura_wings", Profile3DAccessoryCategory.AURA, "Holo Wings", "🦋", "Articulated light wings"),
    Profile3DAccessoryItem("aura_stars", Profile3DAccessoryCategory.AURA, "Starfield", "🌟", "Orbiting constellation"),
    Profile3DAccessoryItem("aura_shield", Profile3DAccessoryCategory.AURA, "Forcefield", "🛡️", "Protective energy sphere"),
    Profile3DAccessoryItem("aura_none", Profile3DAccessoryCategory.AURA, "Clean Pedestal", "⭕", "Projection grid only"),

    // Rig
    Profile3DAccessoryItem("rig_pauldrons", Profile3DAccessoryCategory.RIG, "Pauldrons", "⚡", "Heavy shoulder armor"),
    Profile3DAccessoryItem("rig_cape", Profile3DAccessoryCategory.RIG, "Holo Cape", "🦸", "Flowing gradient mantle"),
    Profile3DAccessoryItem("rig_medallion", Profile3DAccessoryCategory.RIG, "Gold Chain", "🪙", "Suspended gold emblem"),
    Profile3DAccessoryItem("rig_collar", Profile3DAccessoryCategory.RIG, "High Collar", "🧥", "Upturned cyber jacket"),
    Profile3DAccessoryItem("rig_none", Profile3DAccessoryCategory.RIG, "Core Suit", "👕", "Sleek silhouette")
)

val PROFILE_3D_SWATCHES = listOf(
    Profile3DColorSwatch("Holo Cyan", "#00E5FF", Color(0xFF00E5FF)),
    Profile3DColorSwatch("Cyber Emerald", "#00E676", Color(0xFF00E676)),
    Profile3DColorSwatch("Neon Magenta", "#FF2E93", Color(0xFFFF2E93)),
    Profile3DColorSwatch("Solar Gold", "#FFB300", Color(0xFFFFB300)),
    Profile3DColorSwatch("Electric Violet", "#8B5CF6", Color(0xFF8B5CF6)),
    Profile3DColorSwatch("Crimson Pulse", "#FF1744", Color(0xFFFF1744)),
    Profile3DColorSwatch("Arctic Cobalt", "#38BDF8", Color(0xFF38BDF8)),
    Profile3DColorSwatch("Solar Orange", "#F97316", Color(0xFFF97316)),
    Profile3DColorSwatch("Teal Wave", "#14B8A6", Color(0xFF14B8A6)),
    Profile3DColorSwatch("Titanium Pearl", "#E2E8F0", Color(0xFFE2E8F0))
)

/**
 * Dedicated 3D Profile Customizer Component enabling users to pick a base 3D character model,
 * customize multi-zone colors, surface metallic finish, scale, and 4 accessory layers, and
 * persist active & preset configurations to Cloud Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Profile3DCustomizerComponent(
    userDisplayName: String,
    activeConfig: Profile3DConfig,
    savedConfigs: List<Profile3DConfig>,
    isAuthenticated: Boolean,
    isSavingToCloud: Boolean,
    cloudStatusMessage: String?,
    onSaveConfigToFirestore: (Profile3DConfig, docId: String) -> Unit,
    onDeleteSavedConfig: (String) -> Unit,
    onApplyPrimaryThemeHex: (String) -> Unit,
    onApplyAvatarBadgeEmoji: (String) -> Unit,
    onApplyCustomizedAvatarBitmap: (Bitmap) -> Unit,
    onShareLookToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    var configName by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.configName.ifBlank { "My 3D Identity" })
    }
    var baseModelId by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.baseModelId.ifBlank { "cyber_astronaut" })
    }
    var primaryHex by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.primaryColorHex.ifBlank { "#00E5FF" })
    }
    var secondaryHex by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.secondaryColorHex.ifBlank { "#FF2E93" })
    }
    var auraHex by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.auraColorHex.ifBlank { "#00E676" })
    }
    var headgearId by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.headgearId.ifBlank { "head_crown" })
    }
    var eyewearId by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.eyewearId.ifBlank { "eye_visor" })
    }
    var auraId by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.auraId.ifBlank { "aura_drones" })
    }
    var rigId by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.rigId.ifBlank { "rig_pauldrons" })
    }
    var metallicFinish by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableFloatStateOf(activeConfig.metallicFinish.toFloat().coerceIn(0f, 1f))
    }
    var scaleFactor by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableFloatStateOf(activeConfig.scaleFactor.toFloat().coerceIn(0.75f, 1.35f))
    }
    var autoRotate by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableStateOf(activeConfig.autoRotate)
    }
    var rotationYDeg by remember(activeConfig.id, activeConfig.updatedAt) {
        mutableFloatStateOf(activeConfig.turntableYawDeg.toFloat().coerceIn(-45f, 45f))
    }
    var rotationXDeg by remember { mutableFloatStateOf(-8f) }

    var activeColorZone by remember { mutableIntStateOf(0) } // 0 = Suit, 1 = Visor/Trim, 2 = Aura
    var activeAccessoryCategory by remember { mutableStateOf(Profile3DAccessoryCategory.HEADGEAR) }
    var localActionFeedback by remember { mutableStateOf<String?>(null) }

    // Synchronize if cloud updates activeConfig asynchronously
    LaunchedEffect(activeConfig) {
        if (activeConfig.configName.isNotBlank()) configName = activeConfig.configName
        if (activeConfig.baseModelId.isNotBlank()) baseModelId = activeConfig.baseModelId
        if (activeConfig.primaryColorHex.isNotBlank()) primaryHex = activeConfig.primaryColorHex
        if (activeConfig.secondaryColorHex.isNotBlank()) secondaryHex = activeConfig.secondaryColorHex
        if (activeConfig.auraColorHex.isNotBlank()) auraHex = activeConfig.auraColorHex
        if (activeConfig.headgearId.isNotBlank()) headgearId = activeConfig.headgearId
        if (activeConfig.eyewearId.isNotBlank()) eyewearId = activeConfig.eyewearId
        if (activeConfig.auraId.isNotBlank()) auraId = activeConfig.auraId
        if (activeConfig.rigId.isNotBlank()) rigId = activeConfig.rigId
        metallicFinish = activeConfig.metallicFinish.toFloat().coerceIn(0f, 1f)
        scaleFactor = activeConfig.scaleFactor.toFloat().coerceIn(0.75f, 1.35f)
        autoRotate = activeConfig.autoRotate
        rotationYDeg = activeConfig.turntableYawDeg.toFloat().coerceIn(-45f, 45f)
    }

    val activeBaseModel = remember(baseModelId) {
        PROFILE_3D_BASE_MODELS.firstOrNull { it.id == baseModelId } ?: PROFILE_3D_BASE_MODELS.first()
    }
    val selectedHeadgear = remember(headgearId) {
        PROFILE_3D_ACCESSORIES.firstOrNull { it.id == headgearId } ?: PROFILE_3D_ACCESSORIES[1]
    }
    val selectedEyewear = remember(eyewearId) {
        PROFILE_3D_ACCESSORIES.firstOrNull { it.id == eyewearId } ?: PROFILE_3D_ACCESSORIES[6]
    }
    val selectedAura = remember(auraId) {
        PROFILE_3D_ACCESSORIES.firstOrNull { it.id == auraId } ?: PROFILE_3D_ACCESSORIES[11]
    }
    val selectedRig = remember(rigId) {
        PROFILE_3D_ACCESSORIES.firstOrNull { it.id == rigId } ?: PROFILE_3D_ACCESSORIES[16]
    }

    val primaryColor = parseThemeHex(primaryHex)
    val secondaryColor = parseThemeHex(secondaryHex)
    val auraColor = parseThemeHex(auraHex)

    val infiniteTransition = rememberInfiniteTransition(label = "profile_3d_turntable")
    val orbitPhaseRad by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "profile_3d_orbit_phase"
    )
    val autoTurntableSwing by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "profile_3d_turntable_swing"
    )

    val effectiveRotationY = if (autoRotate) {
        (rotationYDeg + autoTurntableSwing).coerceIn(-45f, 45f)
    } else {
        rotationYDeg
    }

    fun buildCurrentConfigPayload(docId: String, isActiveFlag: Boolean): Profile3DConfig {
        return Profile3DConfig(
            id = docId,
            userId = activeConfig.userId,
            configName = configName.trim().ifEmpty { "${activeBaseModel.name} Build" }.take(60),
            baseModelId = activeBaseModel.id,
            baseModelName = activeBaseModel.name,
            primaryColorHex = primaryHex,
            secondaryColorHex = secondaryHex,
            auraColorHex = auraHex,
            headgearId = headgearId,
            eyewearId = eyewearId,
            auraId = auraId,
            rigId = rigId,
            metallicFinish = metallicFinish.toDouble(),
            scaleFactor = scaleFactor.toDouble(),
            autoRotate = autoRotate,
            turntableYawDeg = rotationYDeg.toDouble(),
            isActive = isActiveFlag
        )
    }

    fun loadConfigIntoEditor(config: Profile3DConfig) {
        configName = config.configName
        baseModelId = config.baseModelId
        primaryHex = config.primaryColorHex
        secondaryHex = config.secondaryColorHex
        auraHex = config.auraColorHex
        headgearId = config.headgearId
        eyewearId = config.eyewearId
        auraId = config.auraId
        rigId = config.rigId
        metallicFinish = config.metallicFinish.toFloat().coerceIn(0f, 1f)
        scaleFactor = config.scaleFactor.toFloat().coerceIn(0.75f, 1.35f)
        autoRotate = config.autoRotate
        rotationYDeg = config.turntableYawDeg.toFloat().coerceIn(-45f, 45f)
        localActionFeedback = "Loaded '${config.configName}' into 3D Studio!"
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor, auraColor))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_3d_customizer_component")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Studio Header + Auto-Spin Toggle
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
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(primaryColor, secondaryColor))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FaceRetouchingNatural,
                            contentDescription = "3D Profile Customizer Studio",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "3D Profile Customizer Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${activeBaseModel.emoji} ${activeBaseModel.name} • Drag 3D View • Firestore Sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                FilterChip(
                    selected = autoRotate,
                    onClick = { autoRotate = !autoRotate },
                    label = {
                        Text(
                            text = if (autoRotate) "AUTO 3D" else "MANUAL",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor.copy(alpha = 0.2f),
                        selectedLabelColor = primaryColor
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("profile_3d_autospin_chip")
                )
            }

            // 2. Interactive 3D Viewport Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(276.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF050814), Color(0xFF0F172A), Color(0xFF070B16))
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(primaryColor.copy(alpha = 0.85f), secondaryColor.copy(alpha = 0.85f), auraColor.copy(alpha = 0.85f))
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            autoRotate = false
                            rotationYDeg = (rotationYDeg + dragAmount.x * 0.28f).coerceIn(-45f, 45f)
                            rotationXDeg = (rotationXDeg - dragAmount.y * 0.22f).coerceIn(-30f, 30f)
                        }
                    }
                    .testTag("profile_3d_viewport"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = effectiveRotationY
                            rotationX = rotationXDeg
                            scaleX = scaleFactor
                            scaleY = scaleFactor
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
                            color = auraColor.copy(alpha = (0.48f - ringIdx * 0.1f).coerceAtLeast(0.14f)),
                            topLeft = Offset(cx - rx, pedestalY - ry + parallaxY * 0.3f),
                            size = Size(rx * 2f, ry * 2f),
                            style = Stroke(width = (3.5f - ringIdx * 0.7f))
                        )
                    }
                    for (angleDeg in 0 until 360 step 45) {
                        val rad = Math.toRadians((angleDeg + effectiveRotationY * 0.8).toDouble()).toFloat()
                        val endX = cx + cos(rad) * (w * 0.36f)
                        val endY = pedestalY + sin(rad) * (h * 0.08f)
                        drawLine(
                            color = auraColor.copy(alpha = 0.24f),
                            start = Offset(cx, pedestalY),
                            end = Offset(endX, endY),
                            strokeWidth = 1.5f
                        )
                    }

                    // B. Aura Layer Behind Character
                    when (auraId) {
                        "aura_wings" -> {
                            for (side in listOf(-1f, 1f)) {
                                for (feather in 0..2) {
                                    val wingPath = Path().apply {
                                        val startX = cx + side * 26f + parallaxX * 0.4f
                                        val startY = cy + 16f + feather * 14f
                                        moveTo(startX, startY)
                                        lineTo(
                                            cx + side * (116f - feather * 16f) + parallaxX * 0.8f,
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
                                            listOf(auraColor.copy(alpha = 0.68f - feather * 0.15f), secondaryColor.copy(alpha = 0.28f))
                                        )
                                    )
                                    drawPath(path = wingPath, color = auraColor, style = Stroke(width = 2f))
                                }
                            }
                        }
                        "aura_shield" -> {
                            drawCircle(
                                color = auraColor.copy(alpha = 0.14f),
                                radius = 98f,
                                center = Offset(cx + parallaxX * 0.3f, cy + parallaxY * 0.3f)
                            )
                            drawCircle(
                                color = auraColor.copy(alpha = 0.7f),
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
                                    color = auraColor,
                                    radius = if (idx % 2 == 0) 5.5f else 3.5f,
                                    center = Offset(sx, sy)
                                )
                            }
                        }
                    }

                    // C. Cape Back Layer
                    if (rigId == "rig_cape") {
                        val capePath = Path().apply {
                            moveTo(cx - 54f * activeBaseModel.shoulderSpan + parallaxX * 0.4f, cy + 32f)
                            lineTo(cx + 54f * activeBaseModel.shoulderSpan + parallaxX * 0.4f, cy + 32f)
                            lineTo(cx + 84f - parallaxX * 0.5f, pedestalY + 6f)
                            lineTo(cx - 84f - parallaxX * 0.5f, pedestalY + 6f)
                            close()
                        }
                        drawPath(
                            path = capePath,
                            brush = Brush.verticalGradient(
                                listOf(secondaryColor.copy(alpha = 0.78f), primaryColor.copy(alpha = 0.22f))
                            )
                        )
                    }

                    // D. Base Character Model Distinct 3D Torso & Chest Core
                    val span = activeBaseModel.shoulderSpan
                    val torsoPath = Path().apply {
                        moveTo(cx - 68f * span + parallaxX * 0.4f, pedestalY - 6f)
                        if (activeBaseModel.id == "neon_tactician" || activeBaseModel.id == "cosmic_pioneer") {
                            // Faceted angular tactical armor shoulders
                            lineTo(cx - 72f * span + parallaxX * 0.5f, cy + 36f)
                            lineTo(cx - 34f * span + parallaxX * 0.5f, cy + 26f)
                            lineTo(cx + 34f * span + parallaxX * 0.5f, cy + 26f)
                            lineTo(cx + 72f * span + parallaxX * 0.5f, cy + 36f)
                            lineTo(cx + 68f * span + parallaxX * 0.4f, pedestalY - 6f)
                        } else {
                            // Sculpted smooth shell torso
                            cubicTo(
                                cx - 64f * span + parallaxX * 0.5f, cy + 36f,
                                cx - 32f * span + parallaxX * 0.5f, cy + 28f,
                                cx + parallaxX * 0.5f, cy + 28f
                            )
                            cubicTo(
                                cx + 32f * span + parallaxX * 0.5f, cy + 28f,
                                cx + 64f * span + parallaxX * 0.5f, cy + 36f,
                                cx + 68f * span + parallaxX * 0.4f, pedestalY - 6f
                            )
                        }
                        close()
                    }
                    val specularHighlight = Color.White.copy(alpha = 0.22f + metallicFinish * 0.48f)
                    drawPath(
                        path = torsoPath,
                        brush = Brush.linearGradient(
                            listOf(specularHighlight, primaryColor, Color(0xFF1E293B))
                        )
                    )
                    drawPath(
                        path = torsoPath,
                        color = Color.White.copy(alpha = 0.35f + metallicFinish * 0.4f),
                        style = Stroke(width = 2.2f)
                    )

                    // Base Model Signature Chest Emblem
                    val emblemX = cx + parallaxX * 0.55f
                    val emblemY = cy + 56f
                    when (activeBaseModel.id) {
                        "cyber_astronaut" -> {
                            drawCircle(color = secondaryColor, radius = 12f, center = Offset(emblemX, emblemY))
                            drawCircle(color = Color.White, radius = 6f, center = Offset(emblemX, emblemY))
                        }
                        "holo_scholar" -> {
                            val diamond = Path().apply {
                                moveTo(emblemX, emblemY - 14f)
                                lineTo(emblemX + 12f, emblemY)
                                lineTo(emblemX, emblemY + 14f)
                                lineTo(emblemX - 12f, emblemY)
                                close()
                            }
                            drawPath(diamond, color = secondaryColor)
                            drawPath(diamond, color = Color.White, style = Stroke(width = 2f))
                        }
                        "cosmic_pioneer" -> {
                            drawRoundRect(
                                color = secondaryColor,
                                topLeft = Offset(emblemX - 16f, emblemY - 10f),
                                size = Size(32f, 20f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                        "neon_tactician" -> {
                            val chevron = Path().apply {
                                moveTo(emblemX - 18f, emblemY - 8f)
                                lineTo(emblemX, emblemY + 8f)
                                lineTo(emblemX + 18f, emblemY - 8f)
                            }
                            drawPath(chevron, color = secondaryColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
                        }
                        "eco_guardian" -> {
                            drawOval(
                                color = secondaryColor,
                                topLeft = Offset(emblemX - 10f, emblemY - 14f),
                                size = Size(20f, 28f)
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(emblemX, emblemY - 10f),
                                end = Offset(emblemX, emblemY + 10f),
                                strokeWidth = 2f
                            )
                        }
                        else -> {
                            for (bar in -1..1) {
                                drawLine(
                                    color = secondaryColor,
                                    start = Offset(emblemX + bar * 9f, emblemY + 8f),
                                    end = Offset(emblemX + bar * 9f, emblemY - 8f - (1 - kotlin.math.abs(bar)) * 6f),
                                    strokeWidth = 4.5f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }

                    // E. Outfit Rig Overlays
                    when (rigId) {
                        "rig_pauldrons" -> {
                            for (side in listOf(-1f, 1f)) {
                                drawRoundRect(
                                    color = secondaryColor.copy(alpha = 0.88f),
                                    topLeft = Offset(cx + side * 56f * span - 22f + parallaxX * 0.55f, cy + 28f),
                                    size = Size(44f, 26f),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(cx + side * 56f * span - 22f + parallaxX * 0.55f, cy + 28f),
                                    size = Size(44f, 26f),
                                    cornerRadius = CornerRadius(12f, 12f),
                                    style = Stroke(width = 2f)
                                )
                            }
                        }
                        "rig_medallion" -> {
                            drawLine(color = SolarAmber, start = Offset(cx - 24f + parallaxX * 0.5f, cy + 32f), end = Offset(cx + parallaxX * 0.65f, cy + 64f), strokeWidth = 3.5f)
                            drawLine(color = SolarAmber, start = Offset(cx + 24f + parallaxX * 0.5f, cy + 32f), end = Offset(cx + parallaxX * 0.65f, cy + 64f), strokeWidth = 3.5f)
                            drawCircle(color = SolarAmber, radius = 13f, center = Offset(cx + parallaxX * 0.65f, cy + 66f))
                            drawCircle(color = Color.White, radius = 13f, center = Offset(cx + parallaxX * 0.65f, cy + 66f), style = Stroke(width = 2f))
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
                            drawPath(path = collarPath, color = secondaryColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
                        }
                    }

                    // F. 3D Sculpted Head Sphere with Metallic Specular Shading
                    val headCenterX = cx + parallaxX * 0.75f
                    val headCenterY = cy - 16f + parallaxY * 0.75f
                    val headRadius = 42f * activeBaseModel.headRadiusRatio

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.62f + metallicFinish * 0.35f),
                                primaryColor,
                                Color(0xFF0F172A)
                            ),
                            center = Offset(headCenterX - 14f + parallaxX * 0.2f, headCenterY - 14f),
                            radius = headRadius * 1.35f
                        ),
                        radius = headRadius,
                        center = Offset(headCenterX, headCenterY)
                    )
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.92f),
                        radius = headRadius,
                        center = Offset(headCenterX, headCenterY),
                        style = Stroke(width = 2.2f)
                    )

                    // G. Eyewear Layer
                    val faceFrontX = headCenterX + parallaxX * 0.32f
                    val faceFrontY = headCenterY + parallaxY * 0.25f
                    when (eyewearId) {
                        "eye_visor" -> {
                            drawRoundRect(
                                brush = Brush.horizontalGradient(listOf(secondaryColor, Color.White, secondaryColor)),
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
                            drawRoundRect(color = secondaryColor, topLeft = Offset(faceFrontX - 28f, faceFrontY - 10f), size = Size(24f, 15f), cornerRadius = CornerRadius(5f, 5f))
                            drawRoundRect(color = secondaryColor, topLeft = Offset(faceFrontX + 4f, faceFrontY - 10f), size = Size(24f, 15f), cornerRadius = CornerRadius(5f, 5f))
                            drawLine(color = Color.White, start = Offset(faceFrontX - 4f, faceFrontY - 4f), end = Offset(faceFrontX + 4f, faceFrontY - 4f), strokeWidth = 2.5f)
                        }
                        "eye_hud" -> {
                            drawCircle(color = Color.White, radius = 5f, center = Offset(faceFrontX - 14f, faceFrontY - 2f))
                            drawCircle(color = secondaryColor.copy(alpha = 0.38f), radius = 14f, center = Offset(faceFrontX + 14f, faceFrontY - 2f))
                            drawCircle(color = secondaryColor, radius = 14f, center = Offset(faceFrontX + 14f, faceFrontY - 2f), style = Stroke(width = 2.5f))
                            drawCircle(color = Color.White, radius = 4f, center = Offset(faceFrontX + 14f, faceFrontY - 2f))
                        }
                        "eye_pixel" -> {
                            for (b in -2..2) {
                                if (b != 0) {
                                    drawRect(color = secondaryColor, topLeft = Offset(faceFrontX + b * 11f - 5f, faceFrontY - 8f), size = Size(10f, 10f))
                                }
                            }
                        }
                        else -> {
                            drawCircle(color = secondaryColor, radius = 5.5f, center = Offset(faceFrontX - 14f, faceFrontY - 2f))
                            drawCircle(color = secondaryColor, radius = 5.5f, center = Offset(faceFrontX + 14f, faceFrontY - 2f))
                        }
                    }

                    // H. Headgear Layer
                    when (headgearId) {
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
                            drawPath(path = crownPath, brush = Brush.verticalGradient(listOf(SolarAmber, secondaryColor)))
                            drawPath(path = crownPath, color = Color.White, style = Stroke(width = 2f))
                        }
                        "head_halo" -> {
                            drawOval(color = SolarAmber, topLeft = Offset(headCenterX - 36f, headCenterY - headRadius - 22f), size = Size(72f, 18f), style = Stroke(width = 4.5f))
                            drawOval(color = Color.White, topLeft = Offset(headCenterX - 36f, headCenterY - headRadius - 22f), size = Size(72f, 18f), style = Stroke(width = 1.5f))
                        }
                        "head_headphones" -> {
                            drawArc(
                                color = secondaryColor,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(headCenterX - headRadius - 4f, headCenterY - headRadius - 4f),
                                size = Size((headRadius + 4f) * 2f, (headRadius + 2f) * 2f),
                                style = Stroke(width = 6f, cap = StrokeCap.Round)
                            )
                            drawRoundRect(color = secondaryColor, topLeft = Offset(headCenterX - headRadius - 10f, headCenterY - 12f), size = Size(14f, 28f), cornerRadius = CornerRadius(6f, 6f))
                            drawRoundRect(color = secondaryColor, topLeft = Offset(headCenterX + headRadius - 4f, headCenterY - 12f), size = Size(14f, 28f), cornerRadius = CornerRadius(6f, 6f))
                        }
                        "head_helmet" -> {
                            drawCircle(color = HoloCyan.copy(alpha = 0.22f), radius = headRadius + 14f, center = Offset(headCenterX, headCenterY))
                            drawCircle(color = Color.White.copy(alpha = 0.78f), radius = headRadius + 14f, center = Offset(headCenterX, headCenterY), style = Stroke(width = 2.5f))
                        }
                        "head_beret" -> {
                            drawOval(color = secondaryColor, topLeft = Offset(headCenterX - 34f, headCenterY - headRadius - 8f), size = Size(68f, 22f))
                            drawCircle(color = SolarAmber, radius = 5f, center = Offset(headCenterX, headCenterY - headRadius + 3f))
                        }
                    }

                    // I. Foreground Orbiting Drones
                    if (auraId == "aura_drones") {
                        for (droneIdx in 0..1) {
                            val angle = orbitPhaseRad + droneIdx * Math.PI.toFloat()
                            val dx = cx + cos(angle) * 96f
                            val dy = cy + sin(angle) * 28f
                            drawCircle(color = auraColor.copy(alpha = 0.35f), radius = 14f, center = Offset(dx, dy))
                            drawCircle(color = Color.White, radius = 6f, center = Offset(dx, dy))
                            drawCircle(color = auraColor, radius = 10f, center = Offset(dx, dy), style = Stroke(width = 2f))
                        }
                    }
                }

                // Overlay HUD Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${activeBaseModel.emoji} ${activeBaseModel.name} • ${selectedHeadgear.emoji} ${selectedEyewear.emoji} ${selectedAura.emoji} ${selectedRig.emoji}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Reset Camera / Randomize Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.75f)),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable {
                                rotationYDeg = 14f
                                rotationXDeg = -8f
                                scaleFactor = 1.0f
                                autoRotate = true
                            }
                            .testTag("profile_3d_reset_camera_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset 3D Camera",
                                tint = HoloCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Reset View",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.75f)),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable {
                                val randomModel = PROFILE_3D_BASE_MODELS.random()
                                baseModelId = randomModel.id
                                primaryHex = PROFILE_3D_SWATCHES.random().hex
                                secondaryHex = PROFILE_3D_SWATCHES.random().hex
                                auraHex = PROFILE_3D_SWATCHES.random().hex
                                headgearId = PROFILE_3D_ACCESSORIES.filter { it.category == Profile3DAccessoryCategory.HEADGEAR }.random().id
                                eyewearId = PROFILE_3D_ACCESSORIES.filter { it.category == Profile3DAccessoryCategory.EYEWEAR }.random().id
                                auraId = PROFILE_3D_ACCESSORIES.filter { it.category == Profile3DAccessoryCategory.AURA }.random().id
                                rigId = PROFILE_3D_ACCESSORIES.filter { it.category == Profile3DAccessoryCategory.RIG }.random().id
                                localActionFeedback = "Remixed random 3D style (${randomModel.name})!"
                            }
                            .testTag("profile_3d_randomize_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Randomize 3D Build",
                                tint = SolarAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Remix",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Base Character Model Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Base Character Model",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    modifier = Modifier.testTag("profile_3d_base_model_row")
                ) {
                    items(PROFILE_3D_BASE_MODELS, key = { it.id }) { model ->
                        val isSelected = model.id == baseModelId
                        val accent = parseThemeHex(model.defaultPrimaryHex)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accent else accent.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .width(156.dp)
                                .minimumInteractiveComponentSize()
                                .clickable {
                                    baseModelId = model.id
                                    primaryHex = model.defaultPrimaryHex
                                    secondaryHex = model.defaultSecondaryHex
                                    auraHex = model.defaultAuraHex
                                    localActionFeedback = "Selected base model: ${model.name}"
                                }
                                .testTag("profile_3d_model_${model.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = model.thumbnailResId),
                                    contentDescription = model.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${model.emoji} ${model.name}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = model.archetypeTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Multi-Zone Colors & 3D Material Finish
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "2. Colors & Surface Material",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(0, "Suit Shell", primaryColor),
                        Triple(1, "Visor & Trim", secondaryColor),
                        Triple(2, "3D Aura", auraColor)
                    ).forEach { (idx, label, zoneColor) ->
                        FilterChip(
                            selected = activeColorZone == idx,
                            onClick = { activeColorZone = idx },
                            label = { Text(label, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = zoneColor.copy(alpha = 0.22f),
                                selectedLabelColor = zoneColor
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("profile_3d_color_zone_$idx")
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PROFILE_3D_SWATCHES.forEach { swatch ->
                        val activeZoneHex = when (activeColorZone) {
                            0 -> primaryHex
                            1 -> secondaryHex
                            else -> auraHex
                        }
                        val isSelected = activeZoneHex.equals(swatch.hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(swatch.color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .semantics {
                                    contentDescription = "Select ${swatch.name} color"
                                }
                                .clickable {
                                    when (activeColorZone) {
                                        0 -> primaryHex = swatch.hex
                                        1 -> secondaryHex = swatch.hex
                                        else -> auraHex = swatch.hex
                                    }
                                }
                                .testTag("profile_3d_swatch_${swatch.hex.removePrefix("#").lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected swatch",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Metallic Finish & 3D Zoom Scale Sliders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Metallic Sheen: ${(metallicFinish * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = metallicFinish,
                            onValueChange = { metallicFinish = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = primaryColor,
                                activeTrackColor = primaryColor
                            ),
                            modifier = Modifier.testTag("profile_3d_metallic_slider")
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "3D Scale: ${(scaleFactor * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = scaleFactor,
                            onValueChange = { scaleFactor = it },
                            valueRange = 0.75f..1.35f,
                            colors = SliderDefaults.colors(
                                thumbColor = secondaryColor,
                                activeTrackColor = secondaryColor
                            ),
                            modifier = Modifier.testTag("profile_3d_scale_slider")
                        )
                    }
                }
            }

            // 5. 4-Category Accessories Studio
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "3. Accessories & Gear",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Profile3DAccessoryCategory.entries.forEach { category ->
                        FilterChip(
                            selected = activeAccessoryCategory == category,
                            onClick = { activeAccessoryCategory = category },
                            label = { Text("${category.emoji} ${category.label}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = secondaryColor.copy(alpha = 0.22f),
                                selectedLabelColor = secondaryColor
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("profile_3d_cat_${category.name.lowercase()}")
                        )
                    }
                }

                val categoryItems = remember(activeAccessoryCategory) {
                    PROFILE_3D_ACCESSORIES.filter { it.category == activeAccessoryCategory }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryItems.forEach { item ->
                        val isEquipped = when (item.category) {
                            Profile3DAccessoryCategory.HEADGEAR -> headgearId == item.id
                            Profile3DAccessoryCategory.EYEWEAR -> eyewearId == item.id
                            Profile3DAccessoryCategory.AURA -> auraId == item.id
                            Profile3DAccessoryCategory.RIG -> rigId == item.id
                        }
                        FilterChip(
                            selected = isEquipped,
                            onClick = {
                                when (item.category) {
                                    Profile3DAccessoryCategory.HEADGEAR -> headgearId = item.id
                                    Profile3DAccessoryCategory.EYEWEAR -> eyewearId = item.id
                                    Profile3DAccessoryCategory.AURA -> auraId = item.id
                                    Profile3DAccessoryCategory.RIG -> rigId = item.id
                                }
                            },
                            label = { Text("${item.emoji} ${item.title}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                                selectedLabelColor = CyberEmerald
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("profile_3d_accessory_${item.id}")
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 6. Save & Manage Configurations in Firestore
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "4. Save 3D Configuration to Firestore",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                OutlinedTextField(
                    value = configName,
                    onValueChange = { if (it.length <= 60) configName = it },
                    label = { Text("3D Profile Preset Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_3d_config_name_input")
                )

                if (!isAuthenticated) {
                    Surface(
                        color = SolarAmber.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sign in with Google to persist your 3D configurations across devices via Cloud Firestore.",
                            style = MaterialTheme.typography.labelMedium,
                            color = SolarAmber,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val activePayload = buildCurrentConfigPayload(docId = "active", isActiveFlag = true)
                            onApplyPrimaryThemeHex(primaryHex)
                            onApplyAvatarBadgeEmoji(activeBaseModel.emoji)
                            onSaveConfigToFirestore(activePayload, "active")
                            localActionFeedback = if (isAuthenticated) {
                                "Syncing '${activePayload.configName}' to Cloud Firestore..."
                            } else {
                                "Applied '${activePayload.configName}' locally!"
                            }
                        },
                        enabled = !isSavingToCloud,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("profile_3d_save_active_button")
                    ) {
                        if (isSavingToCloud) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "Save Active 3D Profile",
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val slotId = "preset_${System.currentTimeMillis()}"
                            val presetPayload = buildCurrentConfigPayload(docId = slotId, isActiveFlag = false)
                            onSaveConfigToFirestore(presetPayload, slotId)
                            localActionFeedback = "Saving named preset '${presetPayload.configName}'..."
                        },
                        enabled = !isSavingToCloud,
                        border = BorderStroke(1.2.dp, secondaryColor),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_3d_save_preset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Save Preset Slot",
                            tint = secondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Preset", color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }

                // Apply Bitmap + Share to Wall
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val bmp = bakeProfile3DAvatarBitmap(
                                baseModel = activeBaseModel,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                auraColor = auraColor,
                                headgearId = headgearId,
                                eyewearId = eyewearId,
                                auraId = auraId,
                                rigId = rigId,
                                metallicFinish = metallicFinish
                            )
                            onApplyCustomizedAvatarBitmap(bmp)
                            onApplyPrimaryThemeHex(primaryHex)
                            onApplyAvatarBadgeEmoji(activeBaseModel.emoji)
                            localActionFeedback = "Applied 3D render portrait to your Profile Avatar!"
                        },
                        border = BorderStroke(1.2.dp, CyberEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("profile_3d_apply_portrait_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Set Profile Portrait", color = CyberEmerald, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val headline = "${userDisplayName.ifBlank { "Explorer" }}'s 3D ${activeBaseModel.name} Build"
                            val body = "Preset '$configName' • Base: ${activeBaseModel.name} • Gear: ${selectedHeadgear.title}, ${selectedEyewear.title}, ${selectedAura.title}, ${selectedRig.title}!"
                            onShareLookToWall(headline, body)
                            localActionFeedback = "Shared 3D profile look to Community Wall!"
                        },
                        border = BorderStroke(1.2.dp, HoloCyan),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_3d_share_wall_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share 3D Look to Wall",
                            tint = HoloCyan,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = HoloCyan, fontWeight = FontWeight.Bold)
                    }
                }

                // Saved Firestore Configurations Shelf
                if (savedConfigs.isNotEmpty()) {
                    Text(
                        text = "Saved Cloud 3D Configurations (${savedConfigs.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("profile_3d_saved_configs_row")
                    ) {
                        items(savedConfigs, key = { it.id }) { cfg ->
                            val swatchColor = parseThemeHex(cfg.primaryColorHex)
                            val modelInfo = PROFILE_3D_BASE_MODELS.firstOrNull { it.id == cfg.baseModelId }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = swatchColor.copy(alpha = 0.14f),
                                border = BorderStroke(
                                    width = if (cfg.id == "active") 1.8.dp else 1.dp,
                                    color = swatchColor.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickable { loadConfigIntoEditor(cfg) }
                                    .testTag("profile_3d_saved_item_${cfg.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${modelInfo?.emoji ?: "🧊"} ${cfg.configName}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    if (cfg.id == "active") {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = "Active Cloud Profile",
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        IconButton(
                                            onClick = { onDeleteSavedConfig(cfg.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete preset ${cfg.configName}",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val banner = cloudStatusMessage ?: localActionFeedback
                if (!banner.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_3d_status_banner")
                    ) {
                        Text(
                            text = banner,
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberEmerald,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun bakeProfile3DAvatarBitmap(
    baseModel: Profile3DBaseModel,
    primaryColor: Color,
    secondaryColor: Color,
    auraColor: Color,
    headgearId: String,
    eyewearId: String,
    auraId: String,
    rigId: String,
    metallicFinish: Float
): Bitmap {
    val sizePx = 512
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    // Backdrop
    paint.shader = RadialGradient(
        cx, cy, sizePx * 0.65f,
        intArrayOf(auraColor.copy(alpha = 0.45f).toArgb(), Color(0xFF0A0F1F).toArgb()),
        null, Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)
    paint.shader = null

    if (auraId == "aura_shield") {
        paint.color = auraColor.copy(alpha = 0.55f).toArgb()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawCircle(cx, cy, 196f, paint)
        paint.style = Paint.Style.FILL
    }

    if (rigId == "rig_cape") {
        paint.color = secondaryColor.copy(alpha = 0.72f).toArgb()
        val cape = AndroidPath().apply {
            moveTo(cx - 108f, cy + 54f)
            lineTo(cx + 108f, cy + 54f)
            lineTo(cx + 160f, sizePx.toFloat())
            lineTo(cx - 160f, sizePx.toFloat())
            close()
        }
        canvas.drawPath(cape, paint)
    }

    // Shoulders
    val torso = AndroidPath().apply {
        moveTo(cx - 135f * baseModel.shoulderSpan, sizePx.toFloat())
        cubicTo(cx - 125f, cy + 72f, cx - 64f, cy + 54f, cx, cy + 54f)
        cubicTo(cx + 64f, cy + 54f, cx + 125f, cy + 72f, cx + 135f * baseModel.shoulderSpan, sizePx.toFloat())
        close()
    }
    paint.shader = LinearGradient(
        0f, cy + 40f, 0f, sizePx.toFloat(),
        primaryColor.toArgb(), Color(0xFF1E293B).toArgb(), Shader.TileMode.CLAMP
    )
    canvas.drawPath(torso, paint)
    paint.shader = null

    // Head Sphere
    val headY = cy - 28f
    val headR = 88f * baseModel.headRadiusRatio
    paint.shader = RadialGradient(
        cx - 28f, headY - 28f, headR * 1.4f,
        intArrayOf(
            Color.White.copy(alpha = 0.65f + metallicFinish * 0.3f).toArgb(),
            primaryColor.toArgb(),
            Color(0xFF0F172A).toArgb()
        ),
        null, Shader.TileMode.CLAMP
    )
    canvas.drawCircle(cx, headY, headR, paint)
    paint.shader = null

    // Eyewear
    paint.color = secondaryColor.toArgb()
    when (eyewearId) {
        "eye_visor" -> canvas.drawRoundRect(RectF(cx - 60f, headY - 20f, cx + 60f, headY + 12f), 14f, 14f, paint)
        "eye_shades" -> {
            canvas.drawRoundRect(RectF(cx - 56f, headY - 18f, cx - 8f, headY + 10f), 8f, 8f, paint)
            canvas.drawRoundRect(RectF(cx + 8f, headY - 18f, cx + 56f, headY + 10f), 8f, 8f, paint)
        }
        else -> {
            canvas.drawCircle(cx - 28f, headY - 4f, 11f, paint)
            canvas.drawCircle(cx + 28f, headY - 4f, 11f, paint)
        }
    }

    // Headgear
    when (headgearId) {
        "head_crown" -> {
            paint.color = SolarAmber.toArgb()
            val crown = AndroidPath().apply {
                moveTo(cx - 56f, headY - headR + 10f)
                lineTo(cx - 48f, headY - headR - 42f)
                lineTo(cx - 18f, headY - headR - 14f)
                lineTo(cx, headY - headR - 56f)
                lineTo(cx + 18f, headY - headR - 14f)
                lineTo(cx + 48f, headY - headR - 42f)
                lineTo(cx + 56f, headY - headR + 10f)
                close()
            }
            canvas.drawPath(crown, paint)
        }
        "head_halo" -> {
            paint.color = SolarAmber.toArgb()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 9f
            canvas.drawOval(RectF(cx - 68f, headY - headR - 44f, cx + 68f, headY - headR - 10f), paint)
            paint.style = Paint.Style.FILL
        }
        "head_helmet" -> {
            paint.color = HoloCyan.copy(alpha = 0.25f).toArgb()
            canvas.drawCircle(cx, headY, headR + 24f, paint)
            paint.color = Color.White.toArgb()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            canvas.drawCircle(cx, headY, headR + 24f, paint)
            paint.style = Paint.Style.FILL
        }
    }

    return bmp
}
