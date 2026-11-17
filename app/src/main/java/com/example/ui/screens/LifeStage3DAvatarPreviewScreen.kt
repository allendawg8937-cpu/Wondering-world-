package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.DashboardLifeStage
import com.example.ui.components.defaultPlaceholderAvatarResIdForRole
import kotlin.math.cos
import kotlin.math.sin

enum class AvatarPreviewPose(val id: String, val label: String) {
    IDLE("idle", "Idle"),
    WAVE("wave", "Wave"),
    LEVITATE("levitate", "Levitate"),
    HEROIC("heroic", "Heroic")
}

enum class Stage3DGearType(val label: String) {
    HOLO_VISOR("Holo Visor & Orbit Ring"),
    SONIC_HEADPHONES("Neon Sonic Headphones"),
    AR_SMART_GLASSES("Quantum AR Glasses & Prism"),
    HORIZON_HALO("Aurora Compass Halo"),
    SOVEREIGN_CROWN("Executive Geometric Crown"),
    SOLAR_SHIELD_CREST("Solar Shield & Pauldrons")
}

data class LifeStage3DModelSpec(
    val stage: DashboardLifeStage,
    val archetypeName: String,
    val ageBandLabel: String,
    val heightScale: Float,
    val headScale: Float,
    val shoulderScale: Float,
    val torsoTaper: Float,
    val primarySuitColor: Color,
    val secondarySuitColor: Color,
    val auraGlowColor: Color,
    val gearType: Stage3DGearType,
    val silhouetteLabel: String
)

fun buildLifeStage3DModelSpec(stage: DashboardLifeStage): LifeStage3DModelSpec = when (stage) {
    DashboardLifeStage.KID -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Starlight Explorer",
        ageBandLabel = "Ages 5–12",
        heightScale = 0.78f,
        headScale = 1.18f,
        shoulderScale = 0.84f,
        torsoTaper = 0.82f,
        primarySuitColor = Color(0xFF10B981),
        secondarySuitColor = Color(0xFF065F46),
        auraGlowColor = Color(0xFF34D399),
        gearType = Stage3DGearType.HOLO_VISOR,
        silhouetteLabel = "Compact Explorer Jacket"
    )
    DashboardLifeStage.TEEN -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Neon Creator",
        ageBandLabel = "Ages 13–17",
        heightScale = 0.90f,
        headScale = 1.06f,
        shoulderScale = 0.94f,
        torsoTaper = 0.90f,
        primarySuitColor = Color(0xFFEC4899),
        secondarySuitColor = Color(0xFF831843),
        auraGlowColor = Color(0xFFF472B6),
        gearType = Stage3DGearType.SONIC_HEADPHONES,
        silhouetteLabel = "Sonic Street Hoodie"
    )
    DashboardLifeStage.STUDENT -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Quantum Scholar",
        ageBandLabel = "Ages 18–24",
        heightScale = 0.97f,
        headScale = 1.00f,
        shoulderScale = 0.98f,
        torsoTaper = 0.95f,
        primarySuitColor = Color(0xFF6366F1),
        secondarySuitColor = Color(0xFF312E81),
        auraGlowColor = Color(0xFF818CF8),
        gearType = Stage3DGearType.AR_SMART_GLASSES,
        silhouetteLabel = "STEM Varsity Tech Shell"
    )
    DashboardLifeStage.ADULT -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Horizon Voyager",
        ageBandLabel = "Ages 25–54",
        heightScale = 1.02f,
        headScale = 0.96f,
        shoulderScale = 1.04f,
        torsoTaper = 1.00f,
        primarySuitColor = Color(0xFF06B6D4),
        secondarySuitColor = Color(0xFF164E63),
        auraGlowColor = Color(0xFF22D3EE),
        gearType = Stage3DGearType.HORIZON_HALO,
        silhouetteLabel = "Modern Horizon Coat"
    )
    DashboardLifeStage.BUSINESS -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Apex Founder",
        ageBandLabel = "Enterprise & Founder",
        heightScale = 1.04f,
        headScale = 0.95f,
        shoulderScale = 1.15f,
        torsoTaper = 1.08f,
        primarySuitColor = Color(0xFFF59E0B),
        secondarySuitColor = Color(0xFF1E293B),
        auraGlowColor = Color(0xFFFBBF24),
        gearType = Stage3DGearType.SOVEREIGN_CROWN,
        silhouetteLabel = "Tailored Executive Blazer"
    )
    DashboardLifeStage.VETERAN -> LifeStage3DModelSpec(
        stage = stage,
        archetypeName = "Solar Mentor",
        ageBandLabel = "Service & Legacy",
        heightScale = 1.05f,
        headScale = 0.96f,
        shoulderScale = 1.12f,
        torsoTaper = 1.06f,
        primarySuitColor = Color(0xFF8B5CF6),
        secondarySuitColor = Color(0xFF3B0764),
        auraGlowColor = Color(0xFFA78BFA),
        gearType = Stage3DGearType.SOLAR_SHIELD_CREST,
        silhouetteLabel = "Heritage Flight Jacket"
    )
}

private data class Vec3(val x: Float, val y: Float, val z: Float)

private data class ProjectedPoint(
    val x: Float,
    val y: Float,
    val depth: Float,
    val scale: Float
)

private fun projectPoint3D(
    point: Vec3,
    yawRad: Float,
    pitchRad: Float,
    centerX: Float,
    centerY: Float,
    baseScale: Float
): ProjectedPoint {
    val cosY = cos(yawRad)
    val sinY = sin(yawRad)
    val rx = point.x * cosY + point.z * sinY
    val rz1 = -point.x * sinY + point.z * cosY

    val cosP = cos(pitchRad)
    val sinP = sin(pitchRad)
    val ry = point.y * cosP - rz1 * sinP
    val rz = point.y * sinP + rz1 * cosP

    val cameraDist = 4.2f
    val perspective = cameraDist / (cameraDist - rz).coerceAtLeast(1.2f)
    return ProjectedPoint(
        x = centerX + rx * baseScale * perspective,
        y = centerY + ry * baseScale * perspective,
        depth = rz,
        scale = perspective
    )
}

private data class DepthDrawable(
    val depth: Float,
    val draw: DrawScope.() -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LifeStage3DAvatarPreviewScreen(
    initialRoleOrStageId: String = "adult",
    onBack: (() -> Unit)? = null,
    onApplyStagePersona: (DashboardLifeStage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var activeStage by rememberSaveable {
        mutableStateOf(DashboardLifeStage.fromRoleOrStageId(initialRoleOrStageId))
    }
    LaunchedEffect(initialRoleOrStageId) {
        activeStage = DashboardLifeStage.fromRoleOrStageId(initialRoleOrStageId)
    }

    var activePose by rememberSaveable { mutableStateOf(AvatarPreviewPose.IDLE) }
    var wireframeEnabled by rememberSaveable { mutableStateOf(false) }
    var autoRotateEnabled by rememberSaveable { mutableStateOf(true) }
    var manualYawDeg by remember { mutableFloatStateOf(22f) }
    var manualPitchDeg by remember { mutableFloatStateOf(10f) }
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    val spec = remember(activeStage) { buildLifeStage3DModelSpec(activeStage) }

    // Smooth animated transitions whenever the user toggles between life stages
    val animHeight by animateFloatAsState(
        targetValue = spec.heightScale,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "avatar_height_scale"
    )
    val animHead by animateFloatAsState(
        targetValue = spec.headScale,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "avatar_head_scale"
    )
    val animShoulder by animateFloatAsState(
        targetValue = spec.shoulderScale,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "avatar_shoulder_scale"
    )
    val animPrimary by animateColorAsState(
        targetValue = spec.primarySuitColor,
        animationSpec = tween(durationMillis = 280),
        label = "avatar_primary_color"
    )
    val animSecondary by animateColorAsState(
        targetValue = spec.secondarySuitColor,
        animationSpec = tween(durationMillis = 280),
        label = "avatar_secondary_color"
    )
    val animAura by animateColorAsState(
        targetValue = spec.auraGlowColor,
        animationSpec = tween(durationMillis = 280),
        label = "avatar_aura_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_3d_turntable")
    val turntableAngleDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_spin_deg"
    )
    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_breath_phase"
    )

    val effectiveYawRad = Math.toRadians(
        (manualYawDeg + if (autoRotateEnabled) turntableAngleDeg else 0f).toDouble()
    ).toFloat()
    val effectivePitchRad = Math.toRadians(manualPitchDeg.toDouble()).toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onBack != null) {
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 620.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header bar with optional back navigation & reset controls
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.90f),
                border = BorderStroke(1.dp, animPrimary.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("preview_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.avatar_preview_back_desc)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.avatar_preview_screen_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${activeStage.emoji} ${spec.archetypeName} • ${spec.ageBandLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = animAura
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            manualYawDeg = 22f
                            manualPitchDeg = 10f
                            activePose = AvatarPreviewPose.IDLE
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("reset_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset 3D camera angle",
                            tint = animPrimary
                        )
                    }
                }
            }

            // Interactive 3D Canvas Viewport
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF070C17)
                ),
                border = BorderStroke(1.5.dp, animPrimary.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    manualYawDeg = (manualYawDeg + dragAmount.x * 0.45f) % 360f
                                    manualPitchDeg = (manualPitchDeg + dragAmount.y * 0.28f)
                                        .coerceIn(-28f, 34f)
                                }
                            }
                            .semantics {
                                contentDescription =
                                    "Interactive 3D Canvas Avatar for ${activeStage.label} stage: ${spec.archetypeName} wearing ${spec.gearType.label}"
                            }
                            .testTag("avatar_3d_canvas")
                    ) {
                        val cx = size.width * 0.5f
                        val cy = size.height * 0.50f
                        val baseScale = minOf(size.width, size.height) * 0.28f

                        // Background radial aura backdrop
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    animAura.copy(alpha = 0.22f),
                                    animSecondary.copy(alpha = 0.30f),
                                    Color(0xFF050912)
                                ),
                                center = Offset(cx, cy),
                                radius = size.maxDimension * 0.72f
                            )
                        )

                        // Perspective 3D Stage Floor Grid & Holo Pedestal Ring
                        drawStagePedestal3D(
                            yawRad = effectiveYawRad,
                            pitchRad = effectivePitchRad,
                            cx = cx,
                            cy = cy,
                            baseScale = baseScale,
                            accent = animPrimary,
                            aura = animAura
                        )

                        // Volumetric 3D Character Rig + Stage-Specific Gear
                        drawArticulatedStageAvatar3D(
                            spec = spec,
                            pose = activePose,
                            breathPhase = breathPhase,
                            yawRad = effectiveYawRad,
                            pitchRad = effectivePitchRad,
                            cx = cx,
                            cy = cy,
                            baseScale = baseScale,
                            heightScale = animHeight,
                            headScale = animHead,
                            shoulderScale = animShoulder,
                            primaryColor = animPrimary,
                            secondaryColor = animSecondary,
                            auraColor = animAura,
                            wireframeOnly = wireframeEnabled
                        )
                    }

                    // Top-left live stage chip overlay
                    Surface(
                        color = Color(0xCC0F172A),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, animAura.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${activeStage.emoji} ${activeStage.label} Stage • Drag to Orbit 3D",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    // Top-right 3D telemetry chip + Default 3D Placeholder portrait thumbnail
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Surface(
                            color = Color(0xCC0F172A),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Pose: ${activePose.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = animAura,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            color = Color(0xCC0F172A),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, animAura.copy(alpha = 0.65f)),
                            modifier = Modifier.testTag("avatar_3d_default_placeholder_thumb")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = defaultPlaceholderAvatarResIdForRole(activeStage.stageId)
                                    ),
                                    contentDescription = "Default 3D Placeholder for ${activeStage.label} stage",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, animAura, CircleShape)
                                )
                                Text(
                                    text = "Default 3D",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Pose & Render Controls Row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarPreviewPose.entries.forEach { pose ->
                    FilterChip(
                        selected = activePose == pose,
                        onClick = { activePose = pose },
                        label = { Text(pose.label) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("pose_chip_${pose.id}")
                    )
                }
                FilterChip(
                    selected = wireframeEnabled,
                    onClick = { wireframeEnabled = !wireframeEnabled },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Wireframe mesh overlay",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Wireframe") },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("wireframe_toggle_chip")
                )
                FilterChip(
                    selected = autoRotateEnabled,
                    onClick = { autoRotateEnabled = !autoRotateEnabled },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Auto rotate turntable",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Auto-Spin") },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("auto_spin_toggle_chip")
                )
            }

            // 6 Life Stage Persona Toggles
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.avatar_preview_choose_stage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val columns = if (maxWidth >= 460.dp) 3 else 2
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DashboardLifeStage.entries.chunked(columns).forEach { rowStages ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowStages.forEach { stage ->
                                        val isSelected = stage == activeStage
                                        val stageSpec = remember(stage) { buildLifeStage3DModelSpec(stage) }
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                activeStage = stage
                                                statusFeedback = "Loaded ${stage.label} 3D model (${stageSpec.gearType.label})."
                                            },
                                            leadingIcon = {
                                                Text(
                                                    text = stage.emoji,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stage.label,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = stageSpec.primarySuitColor.copy(alpha = 0.22f),
                                                selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                selectedBorderColor = stageSpec.primarySuitColor,
                                                selectedBorderWidth = 1.6.dp
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .minimumInteractiveComponentSize()
                                                .semantics {
                                                    contentDescription = "Preview ${stage.label} 3D avatar"
                                                }
                                                .testTag("persona_preview_toggle_${stage.stageId}")
                                        )
                                    }
                                    repeat(columns - rowStages.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Live 3D Model Attributes Inspector Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                ),
                border = BorderStroke(1.dp, animPrimary.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("avatar_3d_attributes_card")
            ) {
                AnimatedContent(
                    targetState = spec,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                    label = "stage_attributes_transition"
                ) { currentSpec ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${currentSpec.stage.emoji} ${currentSpec.stage.label} 3D Attributes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "${currentSpec.archetypeName} • ${currentSpec.silhouetteLabel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = currentSpec.primarySuitColor.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, currentSpec.primarySuitColor)
                            ) {
                                Text(
                                    text = currentSpec.stage.guardrailBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = currentSpec.primarySuitColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        AttributeMetricBar(
                            label = "Height Scale (${String.format("%.2fx", animHeight)})",
                            progress = (animHeight / 1.15f).coerceIn(0f, 1f),
                            tint = currentSpec.primarySuitColor
                        )
                        AttributeMetricBar(
                            label = "Stylized Head Ratio (${String.format("%.2fx", animHead)})",
                            progress = (animHead / 1.25f).coerceIn(0f, 1f),
                            tint = currentSpec.auraGlowColor
                        )
                        AttributeMetricBar(
                            label = "Shoulder Rig Span (${String.format("%.2fx", animShoulder)})",
                            progress = (animShoulder / 1.25f).coerceIn(0f, 1f),
                            tint = currentSpec.primarySuitColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    activeStage = DashboardLifeStage.fromRoleOrStageId(currentSpec.stage.stageId)
                                    statusFeedback = "Saved camera preset for ${currentSpec.stage.label}"
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Active gear",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentSpec.gearType.label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onApplyStagePersona(currentSpec.stage)
                                statusFeedback = "Applied ${currentSpec.stage.label} (${currentSpec.archetypeName}) to your profile!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentSpec.primarySuitColor,
                                contentColor = Color(0xFF040711)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("apply_stage_avatar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Apply stage persona",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply ${currentSpec.stage.label} Persona & Theme",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        if (statusFeedback != null) {
                            Text(
                                text = statusFeedback!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = currentSpec.auraGlowColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.testTag("avatar_preview_status_text")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeMetricBar(
    label: String,
    progress: Float,
    tint: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        LinearProgressIndicator(
            progress = { progress },
            color = tint,
            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}

private fun DrawScope.drawStagePedestal3D(
    yawRad: Float,
    pitchRad: Float,
    cx: Float,
    cy: Float,
    baseScale: Float,
    accent: Color,
    aura: Color
) {
    val pedestalY = 1.28f
    val ringSegments = 36
    val outerPath = Path()
    val innerPath = Path()

    for (i in 0..ringSegments) {
        val angle = (i.toFloat() / ringSegments) * (2f * Math.PI).toFloat()
        val pOuter = projectPoint3D(
            Vec3(cos(angle) * 1.18f, pedestalY, sin(angle) * 1.18f),
            yawRad, pitchRad, cx, cy, baseScale
        )
        val pInner = projectPoint3D(
            Vec3(cos(angle) * 0.72f, pedestalY, sin(angle) * 0.72f),
            yawRad, pitchRad, cx, cy, baseScale
        )
        if (i == 0) {
            outerPath.moveTo(pOuter.x, pOuter.y)
            innerPath.moveTo(pInner.x, pInner.y)
        } else {
            outerPath.lineTo(pOuter.x, pOuter.y)
            innerPath.lineTo(pInner.x, pInner.y)
        }
    }
    drawPath(outerPath, color = accent.copy(alpha = 0.55f), style = Stroke(width = 2.5f))
    drawPath(innerPath, color = aura.copy(alpha = 0.75f), style = Stroke(width = 2.0f))

    // Radial holographic spoke lines on turntable deck
    for (spoke in 0 until 6) {
        val angle = spoke * (Math.PI / 3.0).toFloat()
        val pStart = projectPoint3D(
            Vec3(cos(angle) * 0.2f, pedestalY, sin(angle) * 0.2f),
            yawRad, pitchRad, cx, cy, baseScale
        )
        val pEnd = projectPoint3D(
            Vec3(cos(angle) * 1.18f, pedestalY, sin(angle) * 1.18f),
            yawRad, pitchRad, cx, cy, baseScale
        )
        drawLine(
            color = accent.copy(alpha = 0.26f),
            start = Offset(pStart.x, pStart.y),
            end = Offset(pEnd.x, pEnd.y),
            strokeWidth = 1.5f
        )
    }
}

private fun DrawScope.drawArticulatedStageAvatar3D(
    spec: LifeStage3DModelSpec,
    pose: AvatarPreviewPose,
    breathPhase: Float,
    yawRad: Float,
    pitchRad: Float,
    cx: Float,
    cy: Float,
    baseScale: Float,
    heightScale: Float,
    headScale: Float,
    shoulderScale: Float,
    primaryColor: Color,
    secondaryColor: Color,
    auraColor: Color,
    wireframeOnly: Boolean
) {
    val levitateOffset = if (pose == AvatarPreviewPose.LEVITATE) {
        -0.22f + sin(breathPhase) * 0.08f
    } else {
        sin(breathPhase) * 0.025f
    }

    val scaleY = heightScale
    val headCenter3D = Vec3(0f, (-0.78f * scaleY) + levitateOffset, 0f)
    val neck3D = Vec3(0f, (-0.42f * scaleY) + levitateOffset, 0f)
    val chestCenter3D = Vec3(0f, (-0.08f * scaleY) + levitateOffset, 0f)
    val pelvis3D = Vec3(0f, (0.36f * scaleY) + levitateOffset, 0f)

    val shoulderHalf = 0.38f * shoulderScale
    val leftShoulder3D = Vec3(-shoulderHalf, (-0.34f * scaleY) + levitateOffset, 0f)
    val rightShoulder3D = Vec3(shoulderHalf, (-0.34f * scaleY) + levitateOffset, 0f)

    val waveSwing = sin(breathPhase * 2f) * 0.14f
    val leftHand3D = when (pose) {
        AvatarPreviewPose.HEROIC -> Vec3(-shoulderHalf - 0.18f, (-0.15f * scaleY) + levitateOffset, 0.14f)
        AvatarPreviewPose.LEVITATE -> Vec3(-shoulderHalf - 0.26f, (-0.28f * scaleY) + levitateOffset, 0.10f)
        else -> Vec3(-shoulderHalf - 0.14f, (0.28f * scaleY) + levitateOffset, 0.06f)
    }
    val rightHand3D = when (pose) {
        AvatarPreviewPose.WAVE -> Vec3(shoulderHalf + 0.28f, (-0.82f * scaleY) + levitateOffset + waveSwing, 0.16f)
        AvatarPreviewPose.HEROIC -> Vec3(shoulderHalf + 0.24f, (-0.52f * scaleY) + levitateOffset, 0.18f)
        AvatarPreviewPose.LEVITATE -> Vec3(shoulderHalf + 0.26f, (-0.28f * scaleY) + levitateOffset, 0.10f)
        else -> Vec3(shoulderHalf + 0.14f, (0.28f * scaleY) + levitateOffset, 0.06f)
    }

    val hipHalf = 0.17f * spec.torsoTaper.coerceAtMost(1.05f)
    val leftHip3D = Vec3(-hipHalf, (0.38f * scaleY) + levitateOffset, 0f)
    val rightHip3D = Vec3(hipHalf, (0.38f * scaleY) + levitateOffset, 0f)
    val footSpread = if (pose == AvatarPreviewPose.HEROIC) 0.26f else 0.19f
    val leftFoot3D = Vec3(-footSpread, (1.14f * scaleY) + levitateOffset, 0.04f)
    val rightFoot3D = Vec3(footSpread, (1.14f * scaleY) + levitateOffset, -0.02f)

    fun p(v: Vec3) = projectPoint3D(v, yawRad, pitchRad, cx, cy, baseScale)

    val headP = p(headCenter3D)
    val neckP = p(neck3D)
    val chestP = p(chestCenter3D)
    val pelvisP = p(pelvis3D)
    val lShoulderP = p(leftShoulder3D)
    val rShoulderP = p(rightShoulder3D)
    val lHandP = p(leftHand3D)
    val rHandP = p(rightHand3D)
    val lHipP = p(leftHip3D)
    val rHipP = p(rightHip3D)
    val lFootP = p(leftFoot3D)
    val rFootP = p(rightFoot3D)

    val drawList = mutableListOf<DepthDrawable>()

    // Left Leg
    drawList += DepthDrawable((lHipP.depth + lFootP.depth) * 0.5f) {
        drawLimb3D(lHipP, lFootP, 18f * lHipP.scale, secondaryColor, primaryColor, wireframeOnly)
    }
    // Right Leg
    drawList += DepthDrawable((rHipP.depth + rFootP.depth) * 0.5f) {
        drawLimb3D(rHipP, rFootP, 18f * rHipP.scale, secondaryColor, primaryColor, wireframeOnly)
    }

    // Torso Volumetric Prism / Coat
    drawList += DepthDrawable(chestP.depth) {
        val torsoPath = Path().apply {
            moveTo(lShoulderP.x, lShoulderP.y)
            lineTo(rShoulderP.x, rShoulderP.y)
            lineTo(rHipP.x, rHipP.y)
            lineTo(lHipP.x, lHipP.y)
            close()
        }
        if (!wireframeOnly) {
            drawPath(
                path = torsoPath,
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    start = Offset(lShoulderP.x, lShoulderP.y),
                    end = Offset(rHipP.x, rHipP.y)
                )
            )
        }
        drawPath(
            path = torsoPath,
            color = if (wireframeOnly) auraColor else auraColor.copy(alpha = 0.7f),
            style = Stroke(width = if (wireframeOnly) 2.8f else 2.0f, join = StrokeJoin.Round)
        )
        // Chest core emblem
        drawCircle(
            color = auraColor.copy(alpha = 0.88f),
            radius = 9f * chestP.scale,
            center = Offset(chestP.x, chestP.y)
        )
    }

    // Arms
    drawList += DepthDrawable((lShoulderP.depth + lHandP.depth) * 0.5f) {
        drawLimb3D(lShoulderP, lHandP, 14f * lShoulderP.scale, primaryColor, auraColor, wireframeOnly)
    }
    drawList += DepthDrawable((rShoulderP.depth + rHandP.depth) * 0.5f) {
        drawLimb3D(rShoulderP, rHandP, 14f * rShoulderP.scale, primaryColor, auraColor, wireframeOnly)
    }

    // Neck + 3D Head Sphere
    drawList += DepthDrawable(headP.depth) {
        drawLine(
            color = Color(0xFFD6A888),
            start = Offset(neckP.x, neckP.y),
            end = Offset(headP.x, headP.y),
            strokeWidth = 10f * headP.scale,
            cap = StrokeCap.Round
        )
        val headRadius = baseScale * 0.27f * headScale * headP.scale
        if (!wireframeOnly) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFF6D2B5), Color(0xFFC58C68), Color(0xFF7D5037)),
                    center = Offset(headP.x - headRadius * 0.25f, headP.y - headRadius * 0.25f),
                    radius = headRadius * 1.45f
                ),
                radius = headRadius,
                center = Offset(headP.x, headP.y)
            )
        }
        drawCircle(
            color = if (wireframeOnly) auraColor else primaryColor.copy(alpha = 0.6f),
            radius = headRadius,
            center = Offset(headP.x, headP.y),
            style = Stroke(width = if (wireframeOnly) 2.5f else 1.8f)
        )

        // Stage-Specific 3D Accessory / Gear overlay
        drawStageGearOnHead3D(
            gearType = spec.gearType,
            headCenter3D = headCenter3D,
            headRadius = headRadius,
            yawRad = yawRad,
            pitchRad = pitchRad,
            cx = cx,
            cy = cy,
            baseScale = baseScale,
            primaryColor = primaryColor,
            auraColor = auraColor
        )
    }

    // Orbiting 3D Aura Energy Node
    val orbitAngle = breathPhase + yawRad * 0.6f
    val orbitNode3D = Vec3(
        x = cos(orbitAngle) * 0.88f,
        y = (-0.15f * scaleY) + sin(breathPhase * 1.5f) * 0.16f + levitateOffset,
        z = sin(orbitAngle) * 0.88f
    )
    val orbitP = p(orbitNode3D)
    drawList += DepthDrawable(orbitP.depth) {
        val nodeR = 11f * orbitP.scale
        drawCircle(color = auraColor.copy(alpha = 0.28f), radius = nodeR * 2.1f, center = Offset(orbitP.x, orbitP.y))
        drawCircle(color = auraColor, radius = nodeR, center = Offset(orbitP.x, orbitP.y))
    }

    // Painter's depth-sort: back-to-front
    drawList.sortedBy { it.depth }.forEach { it.draw(this) }
}

private fun DrawScope.drawLimb3D(
    start: ProjectedPoint,
    end: ProjectedPoint,
    thickness: Float,
    fill: Color,
    highlight: Color,
    wireframeOnly: Boolean
) {
    val p1 = Offset(start.x, start.y)
    val p2 = Offset(end.x, end.y)
    if (!wireframeOnly) {
        drawLine(color = fill, start = p1, end = p2, strokeWidth = thickness, cap = StrokeCap.Round)
    }
    drawLine(
        color = if (wireframeOnly) highlight else highlight.copy(alpha = 0.45f),
        start = p1,
        end = p2,
        strokeWidth = if (wireframeOnly) 2.6f else 2.0f,
        cap = StrokeCap.Round
    )
    drawCircle(color = highlight, radius = thickness * 0.38f, center = p2)
}

private fun DrawScope.drawStageGearOnHead3D(
    gearType: Stage3DGearType,
    headCenter3D: Vec3,
    headRadius: Float,
    yawRad: Float,
    pitchRad: Float,
    cx: Float,
    cy: Float,
    baseScale: Float,
    primaryColor: Color,
    auraColor: Color
) {
    val eyeLeft = projectPoint3D(
        Vec3(headCenter3D.x - 0.11f, headCenter3D.y - 0.02f, headCenter3D.z + 0.25f),
        yawRad, pitchRad, cx, cy, baseScale
    )
    val eyeRight = projectPoint3D(
        Vec3(headCenter3D.x + 0.11f, headCenter3D.y - 0.02f, headCenter3D.z + 0.25f),
        yawRad, pitchRad, cx, cy, baseScale
    )
    val headTop = projectPoint3D(
        Vec3(headCenter3D.x, headCenter3D.y - 0.30f, headCenter3D.z),
        yawRad, pitchRad, cx, cy, baseScale
    )

    when (gearType) {
        Stage3DGearType.HOLO_VISOR -> {
            drawLine(
                color = auraColor.copy(alpha = 0.92f),
                start = Offset(eyeLeft.x - headRadius * 0.18f, eyeLeft.y),
                end = Offset(eyeRight.x + headRadius * 0.18f, eyeRight.y),
                strokeWidth = headRadius * 0.36f,
                cap = StrokeCap.Round
            )
        }
        Stage3DGearType.SONIC_HEADPHONES -> {
            val earL = projectPoint3D(
                Vec3(headCenter3D.x - 0.30f, headCenter3D.y, headCenter3D.z),
                yawRad, pitchRad, cx, cy, baseScale
            )
            val earR = projectPoint3D(
                Vec3(headCenter3D.x + 0.30f, headCenter3D.y, headCenter3D.z),
                yawRad, pitchRad, cx, cy, baseScale
            )
            val arc = Path().apply {
                moveTo(earL.x, earL.y)
                quadraticTo(headTop.x, headTop.y - headRadius * 0.45f, earR.x, earR.y)
            }
            drawPath(arc, color = auraColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
            drawCircle(color = primaryColor, radius = headRadius * 0.28f, center = Offset(earL.x, earL.y))
            drawCircle(color = primaryColor, radius = headRadius * 0.28f, center = Offset(earR.x, earR.y))
        }
        Stage3DGearType.AR_SMART_GLASSES -> {
            val frameRadius = headRadius * 0.26f
            drawCircle(color = auraColor, radius = frameRadius, center = Offset(eyeLeft.x, eyeLeft.y), style = Stroke(3.5f))
            drawCircle(color = auraColor, radius = frameRadius, center = Offset(eyeRight.x, eyeRight.y), style = Stroke(3.5f))
            drawLine(
                color = auraColor,
                start = Offset(eyeLeft.x + frameRadius, eyeLeft.y),
                end = Offset(eyeRight.x - frameRadius, eyeRight.y),
                strokeWidth = 3f
            )
        }
        Stage3DGearType.HORIZON_HALO -> {
            val haloPath = Path()
            for (i in 0..24) {
                val a = (i / 24f) * (2f * Math.PI).toFloat()
                val hp = projectPoint3D(
                    Vec3(cos(a) * 0.44f, headCenter3D.y - 0.42f, sin(a) * 0.44f),
                    yawRad, pitchRad, cx, cy, baseScale
                )
                if (i == 0) haloPath.moveTo(hp.x, hp.y) else haloPath.lineTo(hp.x, hp.y)
            }
            drawPath(haloPath, color = auraColor, style = Stroke(width = 4.5f))
        }
        Stage3DGearType.SOVEREIGN_CROWN -> {
            val crownPath = Path().apply {
                moveTo(headTop.x - headRadius * 0.55f, headTop.y)
                lineTo(headTop.x - headRadius * 0.45f, headTop.y - headRadius * 0.52f)
                lineTo(headTop.x - headRadius * 0.16f, headTop.y - headRadius * 0.20f)
                lineTo(headTop.x, headTop.y - headRadius * 0.65f)
                lineTo(headTop.x + headRadius * 0.16f, headTop.y - headRadius * 0.20f)
                lineTo(headTop.x + headRadius * 0.45f, headTop.y - headRadius * 0.52f)
                lineTo(headTop.x + headRadius * 0.55f, headTop.y)
                close()
            }
            drawPath(crownPath, color = Color(0xFFFBBF24))
            drawPath(crownPath, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = 2f))
        }
        Stage3DGearType.SOLAR_SHIELD_CREST -> {
            val shieldCenter = projectPoint3D(
                Vec3(headCenter3D.x, headCenter3D.y - 0.44f, headCenter3D.z + 0.10f),
                yawRad, pitchRad, cx, cy, baseScale
            )
            val shieldPath = Path().apply {
                moveTo(shieldCenter.x, shieldCenter.y - headRadius * 0.36f)
                lineTo(shieldCenter.x + headRadius * 0.34f, shieldCenter.y - headRadius * 0.12f)
                lineTo(shieldCenter.x, shieldCenter.y + headRadius * 0.26f)
                lineTo(shieldCenter.x - headRadius * 0.34f, shieldCenter.y - headRadius * 0.12f)
                close()
            }
            drawPath(shieldPath, color = auraColor)
            drawPath(shieldPath, color = Color(0xFFFBBF24), style = Stroke(width = 2.5f))
        }
    }
}
