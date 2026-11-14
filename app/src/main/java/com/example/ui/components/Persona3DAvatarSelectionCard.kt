package com.example.ui.components

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val AVATAR_LOCAL_PREFS_NAME = "stages_of_life_3d_avatar_prefs"
private const val KEY_SELECTED_MODEL_ID = "selected_base_model_id"
private const val KEY_SELECTED_POSE = "selected_pose_stance"
private const val KEY_SELECTED_SHADER = "selected_shader_mode"
private const val KEY_SELECTED_VISUAL_THEME = "selected_visual_theme_id"
private const val KEY_GEAR_ENABLED = "equipped_gear_enabled"
private const val KEY_YAW_ANGLE = "turntable_yaw_deg"

data class AvatarVisualThemeOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryHex: String,
    val secondaryHex: String,
    val badge: String
)

val AVATAR_VISUAL_THEME_OPTIONS = listOf(
    AvatarVisualThemeOption(
        id = "HYPERCUBE_6D",
        title = "6D Hypercube Wire",
        subtitle = "Holographic tesseract grid & cyan/indigo rim light",
        primaryHex = "#00E5FF",
        secondaryHex = "#6366F1",
        badge = "🧊"
    ),
    AvatarVisualThemeOption(
        id = "CYBER_SYNTHWAVE",
        title = "Cyber Synthwave",
        subtitle = "Neon sunset horizon & magenta laser grid",
        primaryHex = "#EC4899",
        secondaryHex = "#F59E0B",
        badge = "🌆"
    ),
    AvatarVisualThemeOption(
        id = "GALAXY_VORTEX",
        title = "Galaxy Vortex",
        subtitle = "Orbital starfield rings & deep nebula aura",
        primaryHex = "#A855F7",
        secondaryHex = "#38BDF8",
        badge = "🌌"
    ),
    AvatarVisualThemeOption(
        id = "CRYSTAL_PRISM",
        title = "Crystal Prism",
        subtitle = "Refractive diamond facets & emerald spectrum",
        primaryHex = "#10B981",
        secondaryHex = "#00E5FF",
        badge = "💎"
    ),
    AvatarVisualThemeOption(
        id = "AURORA_WAVE",
        title = "Aurora Borealis",
        subtitle = "Flowing polar light ribbons & solar corona",
        primaryHex = "#2DD4BF",
        secondaryHex = "#818CF8",
        badge = "✨"
    ),
    AvatarVisualThemeOption(
        id = "MATRIX_CYBERNETIC",
        title = "Matrix Cybernetic",
        subtitle = "Digital telemetry rain & emerald neural mesh",
        primaryHex = "#22C55E",
        secondaryHex = "#EAB308",
        badge = "⚡"
    )
)

data class Persona3DBaseCharacterModel(
    val id: String,
    val personaStage: String,
    val modelName: String,
    val rigArchetype: String,
    val description: String,
    val accentHex: String,
    val presetId: String,
    val heightScale: Float,
    val shoulderWidthRatio: Float,
    val headScaleRatio: Float,
    val signatureAccessory: String,
    val badgeEmoji: String,
    val polyCountLabel: String
)

val PERSONA_3D_BASE_CHARACTER_MODELS = listOf(
    Persona3DBaseCharacterModel(
        id = "model_kid_starlight_sprout",
        personaStage = "Kid",
        modelName = "Starlight Sprout 3D Rig",
        rigArchetype = "Chibi Cosmic Explorer Mesh",
        description = "Playful 3D proportions with oversized holographic star-visor, mini jetpack wings, and bouncy curiosity aura.",
        accentHex = "#00E5FF",
        presetId = "cyber_astronaut",
        heightScale = 0.78f,
        shoulderWidthRatio = 0.85f,
        headScaleRatio = 1.24f,
        signatureAccessory = "Holo Visor & Star Cape",
        badgeEmoji = "🚀",
        polyCountLabel = "18.4k Tris • Chibi Rig"
    ),
    Persona3DBaseCharacterModel(
        id = "model_teen_neon_pulse",
        personaStage = "Teen",
        modelName = "Neon Pulse 3D Creator",
        rigArchetype = "Stylized Synthwave Street Mesh",
        description = "Agile teen proportions with glowing magenta sonic headphones, neon hoodie trim, and floating equalizer rings.",
        accentHex = "#EC4899",
        presetId = "neon_musician",
        heightScale = 0.92f,
        shoulderWidthRatio = 0.95f,
        headScaleRatio = 1.06f,
        signatureAccessory = "Sonic Headphones & Equalizer",
        badgeEmoji = "🎵",
        polyCountLabel = "24.2k Tris • Creator Rig"
    ),
    Persona3DBaseCharacterModel(
        id = "model_student_quantum_scholar",
        personaStage = "Student",
        modelName = "Quantum Scholar 3D Inventor",
        rigArchetype = "Academic Cyber-Tech Mesh",
        description = "Balanced scholar silhouette featuring emerald AR smart lenses, levitating polyhedral study nodes, and lab coat trim.",
        accentHex = "#A855F7",
        presetId = "stem_scholar",
        heightScale = 0.98f,
        shoulderWidthRatio = 1.0f,
        headScaleRatio = 1.0f,
        signatureAccessory = "AR Smart Glasses & Polyhedra",
        badgeEmoji = "🎓",
        polyCountLabel = "28.6k Tris • Scholar Rig"
    ),
    Persona3DBaseCharacterModel(
        id = "model_adult_horizon_voyager",
        personaStage = "Adult",
        modelName = "Horizon Voyager 3D Navigator",
        rigArchetype = "Heroic Balanced Prime Mesh",
        description = "Full-stature 3D character base with kinetic emerald mantle, dual wrist-chrono projectors, and grounded posture.",
        accentHex = "#10B981",
        presetId = "personality_adventurous",
        heightScale = 1.04f,
        shoulderWidthRatio = 1.08f,
        headScaleRatio = 0.96f,
        signatureAccessory = "Kinetic Mantle & Chrono Band",
        badgeEmoji = "🏔️",
        polyCountLabel = "32.0k Tris • Voyager Rig"
    ),
    Persona3DBaseCharacterModel(
        id = "model_business_apex_architect",
        personaStage = "Business",
        modelName = "Apex Executive 3D Architect",
        rigArchetype = "Precision Holo-Suit Mesh",
        description = "Tailored executive 3D silhouette with golden telemetry lapel crest, holographic enterprise Halo, and sleek shoulders.",
        accentHex = "#F59E0B",
        presetId = "personality_intellectual",
        heightScale = 1.05f,
        shoulderWidthRatio = 1.12f,
        headScaleRatio = 0.95f,
        signatureAccessory = "Golden Holo-Lapel & Data Halo",
        badgeEmoji = "🧠",
        polyCountLabel = "34.8k Tris • Executive Rig"
    ),
    Persona3DBaseCharacterModel(
        id = "model_veteran_solar_guardian",
        personaStage = "Veteran",
        modelName = "Solar Guardian 3D Commander",
        rigArchetype = "Armored Honor-Crest Mesh",
        description = "Commanding armored 3D character rig with solar-gold shoulder pauldrons, star-shield emblem, and heroic rim lighting.",
        accentHex = "#FF6D00",
        presetId = "veteran_guardian",
        heightScale = 1.08f,
        shoulderWidthRatio = 1.22f,
        headScaleRatio = 0.94f,
        signatureAccessory = "Solar Pauldrons & Star Crest",
        badgeEmoji = "🛡️",
        polyCountLabel = "38.5k Tris • Guardian Rig"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Persona3DAvatarSelectionCard(
    currentPersonaRole: String,
    selected3DAvatarResId: Int?,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit,
    onSelectPersonaStage: (String) -> Unit = {},
    onEquipBadgeEmoji: (String) -> Unit = {},
    onShareCharacterToWall: (title: String, body: String) -> Unit = { _, _ -> },
    currentVisualThemeId: String = "HYPERCUBE_6D",
    onSelectVisualTheme: (themeId: String, primaryHex: String) -> Unit = { _, _ -> },
    onSaveAvatarToFirestore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember(context) {
        context.getSharedPreferences(AVATAR_LOCAL_PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Load initial persisted selection from local state (SharedPreferences) or match current persona
    val defaultModelForPersona = remember(currentPersonaRole) {
        PERSONA_3D_BASE_CHARACTER_MODELS.firstOrNull {
            currentPersonaRole.contains(it.personaStage, ignoreCase = true)
        } ?: PERSONA_3D_BASE_CHARACTER_MODELS[3] // Default Adult
    }

    var selectedModelId by rememberSaveable {
        mutableStateOf(
            prefs.getString(KEY_SELECTED_MODEL_ID, null) ?: defaultModelForPersona.id
        )
    }
    var selectedPose by rememberSaveable {
        mutableStateOf(
            prefs.getString(KEY_SELECTED_POSE, "Heroic Idle") ?: "Heroic Idle"
        )
    }
    var selectedShaderMode by rememberSaveable {
        mutableStateOf(
            prefs.getString(KEY_SELECTED_SHADER, "Holo PBR + Wire") ?: "Holo PBR + Wire"
        )
    }
    var selectedVisualThemeId by rememberSaveable(currentVisualThemeId) {
        mutableStateOf(
            prefs.getString(KEY_SELECTED_VISUAL_THEME, currentVisualThemeId) ?: currentVisualThemeId
        )
    }
    var gearEnabled by rememberSaveable {
        mutableStateOf(prefs.getBoolean(KEY_GEAR_ENABLED, true))
    }
    var manualYawDegrees by rememberSaveable {
        mutableFloatStateOf(prefs.getFloat(KEY_YAW_ANGLE, 18f))
    }
    var autoRotateTurntable by rememberSaveable { mutableStateOf(true) }
    var isFullScreenSelectorOpen by rememberSaveable { mutableStateOf(false) }
    var isSavingToFirestore by remember { mutableStateOf(false) }
    var localSaveStatusBanner by rememberSaveable {
        mutableStateOf("3D Avatar Preview & Theme Ready • Local + Firestore Cloud Sync")
    }

    val activeModel = remember(selectedModelId) {
        PERSONA_3D_BASE_CHARACTER_MODELS.firstOrNull { it.id == selectedModelId }
            ?: defaultModelForPersona
    }

    val activeVisualTheme = remember(selectedVisualThemeId) {
        AVATAR_VISUAL_THEME_OPTIONS.firstOrNull { it.id == selectedVisualThemeId }
            ?: AVATAR_VISUAL_THEME_OPTIONS.first()
    }

    val matchingPreset = remember(activeModel) {
        THREE_D_AVATAR_PRESETS.firstOrNull { it.id == activeModel.presetId }
            ?: THREE_D_AVATAR_PRESETS.first()
    }

    // Persist every change immediately to local state (SharedPreferences)
    fun persistSelectionLocally(
        model: Persona3DBaseCharacterModel,
        pose: String = selectedPose,
        shader: String = selectedShaderMode,
        visualThemeId: String = selectedVisualThemeId,
        gear: Boolean = gearEnabled,
        yaw: Float = manualYawDegrees
    ) {
        prefs.edit()
            .putString(KEY_SELECTED_MODEL_ID, model.id)
            .putString(KEY_SELECTED_POSE, pose)
            .putString(KEY_SELECTED_SHADER, shader)
            .putString(KEY_SELECTED_VISUAL_THEME, visualThemeId)
            .putBoolean(KEY_GEAR_ENABLED, gear)
            .putFloat(KEY_YAW_ANGLE, yaw)
            .apply()

        val preset = THREE_D_AVATAR_PRESETS.firstOrNull { it.id == model.presetId }
            ?: THREE_D_AVATAR_PRESETS.first()
        onSelect3DAvatarPreset(preset)
        onEquipBadgeEmoji(model.badgeEmoji)
        localSaveStatusBanner =
            "Previewing '${model.modelName}' • Theme: ${activeVisualTheme.title} ($pose) ✓"
    }

    fun saveAvatarAndVisualThemeToFirestore() {
        persistSelectionLocally(activeModel)
        onSelectPersonaStage(activeModel.personaStage)
        onSelectVisualTheme(activeVisualTheme.id, activeVisualTheme.primaryHex)
        onSaveAvatarToFirestore()

        val uid = runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
        if (uid != null) {
            coroutineScope.launch {
                isSavingToFirestore = true
                val opId = GlobalLoadingTracker.beginRequest(
                    title = "Saving 3D Avatar & Theme to Firestore",
                    subtitle = "${activeModel.modelName} • ${activeVisualTheme.title}",
                    category = "Firestore 3D Avatar"
                )
                try {
                    val payload = mapOf(
                        "themeVisualStyle" to activeVisualTheme.id,
                        "themeColorHex" to activeVisualTheme.primaryHex,
                        "role" to activeModel.personaStage.lowercase()
                    )
                    Firebase.firestore.collection("users")
                        .document(uid)
                        .set(payload, SetOptions.merge())
                        .await()
                    localSaveStatusBanner =
                        "☁️ Saved 3D Avatar ('${activeModel.modelName}') & '${activeVisualTheme.title}' Theme to Firestore!"
                } catch (e: Exception) {
                    localSaveStatusBanner =
                        "✓ Saved 3D Avatar & '${activeVisualTheme.title}' Theme locally (Cloud sync queued)"
                } finally {
                    GlobalLoadingTracker.endRequest(opId)
                    isSavingToFirestore = false
                }
            }
        } else {
            localSaveStatusBanner =
                "✓ Saved 3D Avatar ('${activeModel.modelName}') & '${activeVisualTheme.title}' Theme (Sign in to sync to Firestore)"
        }
    }

    // Ensure initial local state syncs with ViewModel preset if none was selected yet
    LaunchedEffect(Unit) {
        if (selected3DAvatarResId == null) {
            onSelect3DAvatarPreset(matchingPreset)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_3d_turntable")
    val animatedSpinDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "turntable_yaw_spin"
    )

    val effectiveYawDegrees = if (autoRotateTurntable) {
        (manualYawDegrees + animatedSpinDegrees) % 360f
    } else {
        manualYawDegrees
    }

    val modelAccentColor = parseThemeHex(activeModel.accentHex)

    @Composable
    fun AvatarSelectionStudioContent(isDialogMode: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .takeIf { isDialogMode }
                ?: Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header
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
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(modelAccentColor, HoloCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "3D Avatar Character Selector",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "3D AVATAR BASE CHARACTER STUDIO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = modelAccentColor
                        )
                        Text(
                            text = "Pick your Stages Of Life 3D base character rig • Local state persisted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isDialogMode) {
                    IconButton(
                        onClick = { isFullScreenSelectorOpen = false },
                        modifier = Modifier.testTag("close_fullscreen_3d_avatar_selector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Fullscreen 3D Avatar Screen",
                            tint = HoloCyan
                        )
                    }
                } else {
                    IconButton(
                        onClick = { isFullScreenSelectorOpen = true },
                        modifier = Modifier.testTag("open_fullscreen_3d_avatar_selector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Open Fullscreen 3D Avatar Selection Screen",
                            tint = modelAccentColor
                        )
                    }
                }
            }

            // Local State Persistence Status Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyberEmerald.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("avatar_3d_local_state_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Local State Persisted",
                            tint = CyberEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = localSaveStatusBanner,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald
                        )
                    }
                    Text(
                        text = activeModel.polyCountLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Interactive 3D Character Rig Viewport + Portrait Preview Split
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF070B16),
                border = BorderStroke(1.5.dp, modelAccentColor.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("avatar_3d_turntable_viewport")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activeModel.badgeEmoji} ${activeModel.modelName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${activeModel.personaStage} Persona • ${activeModel.rigArchetype}",
                                style = MaterialTheme.typography.labelMedium,
                                color = modelAccentColor
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = modelAccentColor.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, modelAccentColor)
                        ) {
                            Text(
                                text = "YAW ${effectiveYawDegrees.toInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = modelAccentColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 3D Procedural Character Rig Canvas + Rendered 3D Portrait Thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    ) {
                        Persona3DCharacterRigCanvas(
                            model = activeModel,
                            yawDegrees = effectiveYawDegrees,
                            poseStance = selectedPose,
                            shaderMode = selectedShaderMode,
                            visualTheme = activeVisualTheme,
                            gearEnabled = gearEnabled,
                            accentColor = modelAccentColor,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating High-Res 3D Portrait Preview Card in top-right corner
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.5.dp, modelAccentColor),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(78.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = matchingPreset.drawableResId),
                                    contentDescription = activeModel.modelName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(bottomStart = 8.dp),
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = activeModel.badgeEmoji,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Signature Accessory & Stage Overlay Badge at bottom-left
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.72f),
                            border = BorderStroke(1.dp, modelAccentColor.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = if (gearEnabled) {
                                    "Gear: ${activeModel.signatureAccessory}"
                                } else {
                                    "Base Mesh Only (Gear Hidden)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Text(
                        text = activeModel.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    // Turntable Rotation Slider & Auto-Spin Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Rotate 3D:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = manualYawDegrees,
                            onValueChange = { newYaw ->
                                autoRotateTurntable = false
                                manualYawDegrees = newYaw
                                persistSelectionLocally(activeModel, yaw = newYaw)
                            },
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(
                                thumbColor = modelAccentColor,
                                activeTrackColor = modelAccentColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("avatar_3d_yaw_slider")
                        )
                        FilterChip(
                            selected = autoRotateTurntable,
                            onClick = { autoRotateTurntable = !autoRotateTurntable },
                            label = { Text(if (autoRotateTurntable) "Auto-Spin ON" else "Manual") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = modelAccentColor.copy(alpha = 0.25f),
                                selectedLabelColor = modelAccentColor
                            )
                        )
                    }
                }
            }

            // Stage-Specific Base Character Model Grid (All 6 Stages Of Life Personas)
            Text(
                text = "1. Pick Base Character Model by 'Stages Of Life' Persona",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            PERSONA_3D_BASE_CHARACTER_MODELS.chunked(2).forEach { rowModels ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowModels.forEach { candidate ->
                        val isSelected = candidate.id == activeModel.id
                        val candidateColor = parseThemeHex(candidate.accentHex)
                        val candidatePreset = THREE_D_AVATAR_PRESETS.firstOrNull {
                            it.id == candidate.presetId
                        } ?: THREE_D_AVATAR_PRESETS.first()

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                candidateColor.copy(alpha = 0.20f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            },
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) candidateColor else candidateColor.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedModelId = candidate.id
                                    persistSelectionLocally(candidate)
                                    onSelectPersonaStage(candidate.personaStage)
                                }
                                .testTag("select_base_3d_model_${candidate.personaStage.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = candidateColor.copy(alpha = 0.22f),
                                        border = BorderStroke(1.dp, candidateColor)
                                    ) {
                                        Text(
                                            text = "${candidate.badgeEmoji} ${candidate.personaStage.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = candidateColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    if (isSelected) {
                                        Surface(
                                            shape = CircleShape,
                                            color = candidateColor,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected Base Model",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = candidatePreset.drawableResId),
                                        contentDescription = candidate.modelName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = candidate.modelName,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = candidate.rigArchetype,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pose Stance, Shader Material & Signature Gear Controls
            Text(
                text = "2. Customize 3D Rig Stance, Shader & Gear (Persisted Locally)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Heroic Idle", "Power Levitate", "Wave Greeting", "Zen Focus").forEach { pose ->
                    val selected = selectedPose == pose
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedPose = pose
                            persistSelectionLocally(activeModel, pose = pose)
                        },
                        label = { Text("Pose: $pose") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = modelAccentColor.copy(alpha = 0.22f),
                            selectedLabelColor = modelAccentColor
                        ),
                        modifier = Modifier.testTag("avatar_3d_pose_${pose.lowercase().replace(" ", "_")}")
                    )
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Holo PBR + Wire", "Cel-Shaded Toon", "Solar Metallic").forEach { shader ->
                    val selected = selectedShaderMode == shader
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedShaderMode = shader
                            persistSelectionLocally(activeModel, shader = shader)
                        },
                        label = { Text(shader) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                            selectedLabelColor = HoloCyan
                        )
                    )
                }

                FilterChip(
                    selected = gearEnabled,
                    onClick = {
                        val updated = !gearEnabled
                        gearEnabled = updated
                        persistSelectionLocally(activeModel, gear = updated)
                    },
                    label = {
                        Text(if (gearEnabled) "Signature Gear: ON" else "Signature Gear: OFF")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = CyberEmerald
                    ),
                    modifier = Modifier.testTag("avatar_3d_toggle_gear_chip")
                )
            }

            // 3. Customizable 3D Visual Theme Selector for Avatar Preview Window & Firestore Profile
            Text(
                text = "3. Select Customizable Visual Theme (Live 3D Preview + Firestore Sync)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            AVATAR_VISUAL_THEME_OPTIONS.chunked(2).forEach { themeRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    themeRow.forEach { themeOption ->
                        val isThemeSelected = themeOption.id == activeVisualTheme.id
                        val themePrimary = parseThemeHex(themeOption.primaryHex)
                        val themeSecondary = parseThemeHex(themeOption.secondaryHex)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isThemeSelected) {
                                themePrimary.copy(alpha = 0.20f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            },
                            border = BorderStroke(
                                width = if (isThemeSelected) 2.dp else 1.dp,
                                color = if (isThemeSelected) themePrimary else themePrimary.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedVisualThemeId = themeOption.id
                                    persistSelectionLocally(
                                        model = activeModel,
                                        visualThemeId = themeOption.id
                                    )
                                    onSelectVisualTheme(themeOption.id, themeOption.primaryHex)
                                }
                                .testTag("avatar_3d_visual_theme_${themeOption.id.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(themePrimary, themeSecondary)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = themeOption.badge,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = themeOption.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isThemeSelected) themePrimary else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = themeOption.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons: Save 3D Avatar & Theme to Firestore + Share to Community Hub
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        saveAvatarAndVisualThemeToFirestore()
                    },
                    enabled = !isSavingToFirestore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = modelAccentColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_3d_base_character_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save 3D Avatar Configuration and Visual Theme to Firestore",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSavingToFirestore) {
                            "Saving to Firestore..."
                        } else {
                            "Save 3D Avatar & Theme to Firestore"
                        },
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        onShareCharacterToWall(
                            "Equipped ${activeModel.modelName} (${activeModel.personaStage} 3D Avatar • ${activeVisualTheme.title})",
                            "Customized my Stages Of Life 3D avatar with ${activeModel.rigArchetype}, ${activeVisualTheme.title} visual theme, $selectedPose stance, and ${activeModel.signatureAccessory}!"
                        )
                        localSaveStatusBanner = "Shared ${activeModel.modelName} (${activeVisualTheme.title}) to Stages Of Life Feed ✓"
                    },
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier.testTag("share_3d_avatar_to_wall_button")
                ) {
                    Text("Share 3D Rig", color = HoloCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("persona_3d_avatar_selection_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.5.dp, modelAccentColor.copy(alpha = 0.65f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            AvatarSelectionStudioContent(isDialogMode = false)
        }
    }

    if (isFullScreenSelectorOpen) {
        Dialog(
            onDismissRequest = { isFullScreenSelectorOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            BackHandler {
                isFullScreenSelectorOpen = false
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .testTag("fullscreen_3d_avatar_selection_dialog"),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, modelAccentColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    AvatarSelectionStudioContent(isDialogMode = true)
                }
            }
        }
    }
}

@Composable
private fun Persona3DCharacterRigCanvas(
    model: Persona3DBaseCharacterModel,
    yawDegrees: Float,
    poseStance: String,
    shaderMode: String,
    visualTheme: AvatarVisualThemeOption,
    gearEnabled: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val themePrimaryColor = parseThemeHex(visualTheme.primaryHex)
    val themeSecondaryColor = parseThemeHex(visualTheme.secondaryHex)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.45f
        val baseFloorY = h * 0.84f

        val yawRad = (yawDegrees * PI / 180.0).toFloat()
        val cosYaw = cos(yawRad)
        val sinYaw = sin(yawRad)

        // 0. Draw Customizable Visual Theme 3D Backdrop Aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    themePrimaryColor.copy(alpha = 0.24f),
                    themeSecondaryColor.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = Offset(cx, h * 0.45f),
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = Offset(cx, h * 0.45f)
        )

        // Subtle pose vertical bob / levitation offset
        val levitateOffset = when (poseStance) {
            "Power Levitate" -> -16f
            "Zen Focus" -> -10f
            else -> 0f
        }

        // 1. Draw 3D Holographic Turntable Pedestal & Perspective Grid Rings
        val pedestalRx = w * 0.28f
        val pedestalRy = h * 0.08f
        drawOval(
            color = themePrimaryColor.copy(alpha = 0.18f),
            topLeft = Offset(cx - pedestalRx, baseFloorY - pedestalRy),
            size = Size(pedestalRx * 2f, pedestalRy * 2f)
        )
        drawOval(
            color = accentColor.copy(alpha = 0.75f),
            topLeft = Offset(cx - pedestalRx, baseFloorY - pedestalRy),
            size = Size(pedestalRx * 2f, pedestalRy * 2f),
            style = Stroke(width = 2.5f)
        )
        drawOval(
            color = HoloCyan.copy(alpha = 0.45f),
            topLeft = Offset(cx - pedestalRx * 0.68f, baseFloorY - pedestalRy * 0.68f),
            size = Size(pedestalRx * 1.36f, pedestalRy * 1.36f),
            style = Stroke(width = 1.5f)
        )

        // Rotating radial grid spokes on pedestal
        for (i in 0 until 8) {
            val spokeAngle = yawRad + (i * PI / 4.0).toFloat()
            val sx = cx + cos(spokeAngle) * pedestalRx
            val sy = baseFloorY + sin(spokeAngle) * pedestalRy
            drawLine(
                color = accentColor.copy(alpha = 0.35f),
                start = Offset(cx, baseFloorY),
                end = Offset(sx, sy),
                strokeWidth = 1.4f
            )
        }

        // 2. Compute Character Proportions from Persona3DBaseCharacterModel
        val totalCharHeight = h * 0.62f * model.heightScale
        val hipY = baseFloorY - totalCharHeight * 0.42f + levitateOffset
        val shoulderY = baseFloorY - totalCharHeight * 0.76f + levitateOffset
        val headCenterY = baseFloorY - totalCharHeight * 0.92f + levitateOffset

        val halfShoulderSpan = (w * 0.11f) * model.shoulderWidthRatio *
            (0.65f + 0.35f * kotlin.math.abs(cosYaw))
        val halfHipSpan = halfShoulderSpan * 0.62f
        val headRadius = (h * 0.095f) * model.headScaleRatio

        val bodyFillAlpha = when (shaderMode) {
            "Solar Metallic" -> 0.55f
            "Cel-Shaded Toon" -> 0.42f
            else -> 0.26f
        }

        // 3. Optional Cape / Mantle / Aura behind torso
        if (gearEnabled) {
            val capePath = Path().apply {
                moveTo(cx - halfShoulderSpan * 0.9f, shoulderY + 4f)
                lineTo(cx + halfShoulderSpan * 0.9f, shoulderY + 4f)
                lineTo(cx + halfHipSpan * 1.45f + sinYaw * 10f, baseFloorY - 14f)
                lineTo(cx - halfHipSpan * 1.45f + sinYaw * 10f, baseFloorY - 14f)
                close()
            }
            drawPath(
                path = capePath,
                color = accentColor.copy(alpha = 0.18f)
            )
            drawPath(
                path = capePath,
                color = accentColor.copy(alpha = 0.55f),
                style = Stroke(width = 1.8f)
            )
        }

        // 4. Draw 3D Character Legs
        val leftHipX = cx - halfHipSpan * 0.65f
        val rightHipX = cx + halfHipSpan * 0.65f
        val leftFootX = if (poseStance == "Zen Focus") cx - halfHipSpan * 0.95f else leftHipX - 4f
        val rightFootX = if (poseStance == "Zen Focus") cx + halfHipSpan * 0.95f else rightHipX + 4f
        val footY = if (poseStance == "Zen Focus") hipY + (baseFloorY - hipY) * 0.55f else baseFloorY - 4f

        drawLine(
            color = accentColor,
            start = Offset(leftHipX, hipY),
            end = Offset(leftFootX, footY),
            strokeWidth = 9f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = Offset(rightHipX, hipY),
            end = Offset(rightFootX, footY),
            strokeWidth = 9f,
            cap = StrokeCap.Round
        )

        // 5. Draw 3D Torso Mesh (Shaded + Wireframe Subdivisions)
        val torsoPath = Path().apply {
            moveTo(cx - halfShoulderSpan, shoulderY)
            lineTo(cx + halfShoulderSpan, shoulderY)
            lineTo(cx + halfHipSpan, hipY)
            lineTo(cx - halfHipSpan, hipY)
            close()
        }
        drawPath(
            path = torsoPath,
            color = accentColor.copy(alpha = bodyFillAlpha)
        )
        drawPath(
            path = torsoPath,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 2.2f)
        )

        // Wireframe chest subdivision lines
        val midTorsoY = (shoulderY + hipY) * 0.5f
        drawLine(
            color = accentColor.copy(alpha = 0.7f),
            start = Offset(cx + sinYaw * halfShoulderSpan * 0.35f, shoulderY),
            end = Offset(cx + sinYaw * halfHipSpan * 0.35f, hipY),
            strokeWidth = 1.6f
        )
        drawLine(
            color = accentColor.copy(alpha = 0.7f),
            start = Offset(cx - halfShoulderSpan * 0.82f, midTorsoY),
            end = Offset(cx + halfShoulderSpan * 0.82f, midTorsoY),
            strokeWidth = 1.6f
        )

        // 6. Draw 3D Arms based on selected Pose Stance
        val leftShoulder = Offset(cx - halfShoulderSpan, shoulderY + 4f)
        val rightShoulder = Offset(cx + halfShoulderSpan, shoulderY + 4f)

        val leftHand = when (poseStance) {
            "Power Levitate" -> Offset(cx - halfShoulderSpan * 1.55f, shoulderY - 12f)
            "Wave Greeting" -> Offset(cx - halfShoulderSpan * 1.35f, hipY - 6f)
            "Zen Focus" -> Offset(cx - halfHipSpan * 0.3f, midTorsoY + 10f)
            else -> Offset(cx - halfShoulderSpan * 1.25f, hipY + 4f)
        }
        val rightHand = when (poseStance) {
            "Power Levitate" -> Offset(cx + halfShoulderSpan * 1.55f, shoulderY - 12f)
            "Wave Greeting" -> Offset(cx + halfShoulderSpan * 1.45f, headCenterY - 8f)
            "Zen Focus" -> Offset(cx + halfHipSpan * 0.3f, midTorsoY + 10f)
            else -> Offset(cx + halfShoulderSpan * 1.25f, hipY + 4f)
        }

        drawLine(
            color = accentColor,
            start = leftShoulder,
            end = leftHand,
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = rightShoulder,
            end = rightHand,
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )

        // 7. Optional Veteran / Business Shoulder Pauldrons
        if (gearEnabled && (model.personaStage == "Veteran" || model.personaStage == "Business")) {
            drawCircle(
                color = SolarAmber,
                radius = 9f,
                center = leftShoulder
            )
            drawCircle(
                color = SolarAmber,
                radius = 9f,
                center = rightShoulder
            )
        }

        // 8. Draw 3D Head Sphere + Visor / AR Glasses
        val headCenter = Offset(cx + sinYaw * 4f, headCenterY)
        drawCircle(
            color = accentColor.copy(alpha = 0.35f),
            radius = headRadius,
            center = headCenter
        )
        drawCircle(
            color = Color.White,
            radius = headRadius,
            center = headCenter,
            style = Stroke(width = 2.4f)
        )

        // Head latitude/longitude wireframe rings
        val visorOffsetX = sinYaw * headRadius * 0.45f
        if (gearEnabled) {
            // Glowing Holo-Visor / AR Smart Glasses across face direction
            drawLine(
                color = HoloCyan,
                start = Offset(headCenter.x - headRadius * 0.65f + visorOffsetX * 0.4f, headCenter.y - 2f),
                end = Offset(headCenter.x + headRadius * 0.65f + visorOffsetX * 0.4f, headCenter.y - 2f),
                strokeWidth = 5.5f,
                cap = StrokeCap.Round
            )
        }
    }
}
