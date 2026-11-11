package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.DeepSpaceBg
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.LightSpaceBg
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlin.math.cos
import kotlin.math.sin

data class ThreeDThemeVisualStyleOption(
    val id: String,
    val title: String,
    val badge: String,
    val subtitle: String,
    val recommendedHex: String,
    val secondaryColor: Color
)

val THREE_D_THEME_VISUAL_STYLES = listOf(
    ThreeDThemeVisualStyleOption(
        id = "HYPERCUBE_6D",
        title = "WereEqual Classic Theme",
        badge = "WEREEQUAL THEME",
        subtitle = "Signature WereEqual theme with balanced Royal Blue & Sky Cyan tones",
        recommendedHex = "#2563EB",
        secondaryColor = HoloCyan
    ),
    ThreeDThemeVisualStyleOption(
        id = "CYBER_SYNTHWAVE",
        title = "WereEqual Sunset Theme",
        badge = "SUNSET THEME",
        subtitle = "Warm golden horizon sun with clean sunset & rose glow",
        recommendedHex = "#EC4899",
        secondaryColor = SolarAmber
    ),
    ThreeDThemeVisualStyleOption(
        id = "GALAXY_VORTEX",
        title = "WereEqual Starlight Theme",
        badge = "STARLIGHT THEME",
        subtitle = "Calm starlight galaxy spiral with soft purple & royal glow",
        recommendedHex = "#8B5CF6",
        secondaryColor = NeonPink
    ),
    ThreeDThemeVisualStyleOption(
        id = "CRYSTAL_PRISM",
        title = "WereEqual Ocean Theme",
        badge = "OCEAN THEME",
        subtitle = "Clean crystal diamond theme with cool ocean cyan & sky tones",
        recommendedHex = "#06B6D4",
        secondaryColor = CyberEmerald
    ),
    ThreeDThemeVisualStyleOption(
        id = "AURORA_WAVE",
        title = "WereEqual Forest Theme",
        badge = "FOREST THEME",
        subtitle = "Peaceful emerald green aurora waves with calm natural light",
        recommendedHex = "#10B981",
        secondaryColor = HoloCyan
    ),
    ThreeDThemeVisualStyleOption(
        id = "MATRIX_CYBERNETIC",
        title = "WereEqual Modern Theme",
        badge = "MODERN THEME",
        subtitle = "Smooth concentric orbital rings with crisp mint & lime",
        recommendedHex = "#84CC16",
        secondaryColor = CyberEmerald
    )
)

/**
 * Applies a subtle 6-Dimensional (6DoF: X, Y, Z translation + Pitch, Yaw, Roll rotation)
 * spatial perspective transform and high-definition depth elevation to cards and panels.
 */
fun Modifier.sixDimensionalCardEffect(
    enableMotion: Boolean = true,
    tiltFactor: Float = 1.6f
): Modifier = composed {
    if (!enableMotion) {
        this.graphicsLayer {
            cameraDistance = 18f * density
        }
    } else {
        val transition = rememberInfiniteTransition(label = "6d_card_dof")
        val pitch by transition.animateFloat(
            initialValue = -tiltFactor,
            targetValue = tiltFactor,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "6d_pitch"
        )
        val yaw by transition.animateFloat(
            initialValue = tiltFactor * 0.8f,
            targetValue = -tiltFactor * 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "6d_yaw"
        )
        this.graphicsLayer {
            cameraDistance = 20f * density
            rotationX = pitch
            rotationY = yaw
        }
    }
}

@Composable
fun SixDimensionalHdBadge(
    label: String = "FHD 1080p • HD 6D",
    modifier: Modifier = Modifier
) {
    Surface(
        color = HoloCyan.copy(alpha = 0.16f),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, NeonPink, CyberEmerald))
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "◈ FHD • HD • $label",
                style = MaterialTheme.typography.labelSmall,
                color = HoloCyan,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/**
 * 3D / Multi-Dimensional "Stages Of Life" Pyramid Icon Emblem featuring the custom 3D dimensional
 * pyramid artwork saying "CREATE YOUR LIFE" and "LIVE • LIFE • LOVE" in and around the icon
 * across all 3 dimensions (X, Y, Z), with animated 6D gyroscopic orbital rings and 3D spatial tilt.
 */
@Composable
fun WereAllEquals3DMultiDimensionalIcon(
    iconSize: Dp = 52.dp,
    showRibbonText: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stages_of_life_3d_pyramid_motion")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pyramid_orbit_angle"
    )
    val tiltX by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pyramid_tilt_x"
    )
    val tiltY by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pyramid_tilt_y"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .testTag("wereallequals_3d_multidimensional_icon")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    cameraDistance = 16f * density
                    rotationX = tiltX
                    rotationY = tiltY
                }
        ) {
            // Outer Multi-Dimensional 3D Pyramid & 3-Axis (X, Y, Z) Gyroscopic Rings Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension * 0.48f
                val centerPt = Offset(size.width * 0.5f, size.height * 0.5f)
                val rad = Math.toRadians(orbitAngle.toDouble()).toFloat()

                // Volumetric 3D halo around the Pyramid
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            HoloCyan.copy(alpha = 0.44f),
                            CyberEmerald.copy(alpha = 0.32f),
                            NeonPink.copy(alpha = 0.24f),
                            SolarAmber.copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        center = centerPt,
                        radius = r * 1.22f
                    ),
                    radius = r * 1.22f,
                    center = centerPt
                )

                // Dimension 1 (X-Axis): Horizontal LIVE orbital ring
                val ry1 = (r * (0.42f + 0.35f * kotlin.math.abs(sin(rad)))).coerceAtLeast(4f)
                drawOval(
                    brush = Brush.sweepGradient(
                        listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink, HoloCyan)
                    ),
                    topLeft = Offset(centerPt.x - r, centerPt.y - ry1),
                    size = Size(r * 2f, ry1 * 2f),
                    style = Stroke(width = 2.4f)
                )

                // Dimension 2 (Y-Axis): Vertical LIFE orbital ring
                val rx2 = (r * (0.42f + 0.35f * kotlin.math.abs(cos(rad)))).coerceAtLeast(4f)
                drawOval(
                    brush = Brush.sweepGradient(
                        listOf(CyberEmerald, SolarAmber, NeonPink, HoloCyan, CyberEmerald)
                    ),
                    topLeft = Offset(centerPt.x - rx2, centerPt.y - r),
                    size = Size(rx2 * 2f, r * 2f),
                    style = Stroke(width = 2.0f)
                )

                // Dimension 3 (Z-Axis): 3D Wireframe Pyramid Apex & Edges
                val apex = Offset(centerPt.x, centerPt.y - r * 0.96f)
                val baseLeft = Offset(centerPt.x - r * 0.90f, centerPt.y + r * 0.72f)
                val baseRight = Offset(centerPt.x + r * 0.90f, centerPt.y + r * 0.72f)
                val baseMid = Offset(centerPt.x + sin(rad) * r * 0.25f, centerPt.y + r * 0.92f)
                drawLine(HoloCyan.copy(alpha = 0.85f), apex, baseLeft, strokeWidth = 2.2f)
                drawLine(NeonPink.copy(alpha = 0.85f), apex, baseRight, strokeWidth = 2.2f)
                drawLine(CyberEmerald.copy(alpha = 0.85f), apex, baseMid, strokeWidth = 2.2f)
                drawLine(SolarAmber.copy(alpha = 0.75f), baseLeft, baseMid, strokeWidth = 1.8f)
                drawLine(SolarAmber.copy(alpha = 0.75f), baseMid, baseRight, strokeWidth = 1.8f)
            }

            // Core 3D Dimensional Pyramid Artwork ("LIVE • LIFE • LOVE" & "CREATE YOUR LIFE" in 3D)
            Surface(
                shape = RoundedCornerShape((iconSize.value * 0.28f).dp),
                border = BorderStroke(
                    width = 1.8.dp,
                    brush = Brush.linearGradient(
                        listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink)
                    )
                ),
                color = Color(0xFF0D1128),
                modifier = Modifier.size(iconSize * 0.82f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.live_life_love_pyramid_1794367033353),
                    contentDescription = "3D Dimensional Pyramid Icon Saying Create Your Life and Live Life Love",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 3D "LIVE • LIFE • LOVE" & "CREATE YOUR LIFE" Badges In & Around the Pyramid Icon
        if (showRibbonText) {
            val microFontSize = (iconSize.value * 0.115f).coerceIn(6f, 9.5f).sp

            // Top Apex 3D Pill: "LIVE • LIFE • LOVE"
            Surface(
                color = Color(0xFF0D1128).copy(alpha = 0.92f),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, NeonPink))
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        rotationX = tiltX * 1.2f
                        rotationY = tiltY * 1.2f
                    }
            ) {
                Text(
                    text = "LIVE • LIFE • LOVE",
                    style = TextStyle(
                        fontSize = microFontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        shadow = Shadow(
                            color = NeonPink,
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    ),
                    color = HoloCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }

            // Bottom Base 3D Ribbon: "CREATE YOUR LIFE"
            Surface(
                color = Color(0xFF0D1128).copy(alpha = 0.95f),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(SolarAmber, HoloCyan, CyberEmerald, NeonPink)
                    )
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = iconSize * 0.74f)
                    .graphicsLayer {
                        rotationX = -tiltX * 0.9f
                        rotationY = tiltY * 1.1f
                    }
            ) {
                Text(
                    text = "CREATE YOUR LIFE",
                    style = TextStyle(
                        fontSize = (iconSize.value * 0.13f).coerceIn(6.5f, 11f).sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        shadow = Shadow(
                            color = SolarAmber,
                            offset = Offset(1.5f, 1.5f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Interactive All-3-Dimensions ("LIVE • LIFE • LOVE" + "CREATE YOUR LIFE") Pyramid Showcase Banner
 * for "Stages Of Life", displaying the 3D Pyramid with LIVE (1D X-Axis), LIFE (2D Y-Axis), and
 * LOVE (3D Z-Axis) sculpted in and around the icon.
 */
@Composable
fun WereAllEquals3DHeroShowcaseBanner(
    modifier: Modifier = Modifier
) {
    val dimensionModes = remember {
        listOf(
            "All 3 Dimensions: LIVE • LIFE • LOVE" to "1D X-Axis: LIVE • 2D Y-Axis: LIFE • 3D Z-Axis: LOVE",
            "1st Dimension (X-Axis): LIVE" to "Vitality, energy & daily action across the 3D Pyramid",
            "2nd Dimension (Y-Axis): LIFE" to "Growth, education, career & purpose at every stage",
            "3rd Dimension (Z-Axis): LOVE" to "Community, empathy, charity & connection in 3D space"
        )
    }
    var selectedModeIndex by remember { mutableIntStateOf(0) }
    val (modeTitle, modeSubtitle) = dimensionModes[selectedModeIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.4f)
            .testTag("wereallequals_3d_showcase_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedModeIndex = (selectedModeIndex + 1) % dimensionModes.size
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WereAllEquals3DMultiDimensionalIcon(
                    iconSize = 84.dp,
                    showRibbonText = true,
                    onClick = {
                        selectedModeIndex = (selectedModeIndex + 1) % dimensionModes.size
                    }
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = SolarAmber.copy(alpha = 0.20f),
                            border = BorderStroke(1.dp, SolarAmber),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "▲ 3D PYRAMID",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = modeTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Multi-Dimensional Extruded 3D Typography for "STAGES OF LIFE • CREATE YOUR LIFE"
                    Box {
                        Text(
                            text = "STAGES OF LIFE • CREATE YOUR LIFE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.0.sp,
                                shadow = Shadow(
                                    color = NeonPink.copy(alpha = 0.85f),
                                    offset = Offset(3f, 3f),
                                    blurRadius = 6f
                                )
                            ),
                            color = HoloCyan
                        )
                        Text(
                            text = "STAGES OF LIFE • CREATE YOUR LIFE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.0.sp,
                                shadow = Shadow(
                                    color = HoloCyan.copy(alpha = 0.9f),
                                    offset = Offset(-1.5f, -1.5f),
                                    blurRadius = 4f
                                )
                            ),
                            color = Color.White
                        )
                    }

                    Text(
                        text = modeSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3-Dimensional "LIVE • LIFE • LOVE" Interactive 3D Axis Badges Around the Icon Showcase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("1D • X-AXIS", "LIVE", HoloCyan),
                    Triple("2D • Y-AXIS", "LIFE", CyberEmerald),
                    Triple("3D • Z-AXIS", "LOVE", NeonPink)
                ).forEachIndexed { idx, (axisLabel, word, color) ->
                    Surface(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, color.copy(alpha = 0.75f)),
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                rotationX = -5f
                                rotationY = (idx - 1) * 8f
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = axisLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = color.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = word,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp,
                                    shadow = Shadow(
                                        color = color,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 5f
                                    )
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ultra-HD 6-Dimensional (6D) Spatial Background Engine supporting 6 distinct
 * Firestore-persisted 3D visual styles and 18+ dynamic theme colors.
 */
@Composable
fun ThreeDimensionalBackground(
    accentHex: String,
    visualStyleId: String = "HYPERCUBE_6D",
    isDarkMode: Boolean = true,
    backgroundTransparency: Float = 0.0f,
    modifier: Modifier = Modifier
) {
    val bgAlpha = (1f - backgroundTransparency.coerceIn(0f, 1f)).coerceIn(0f, 1f)
    val cleanHex = accentHex.trim().uppercase()
    val rawSelectedColor = parseThemeHex(accentHex)
    val accentColor by animateColorAsState(
        targetValue = rawSelectedColor,
        animationSpec = tween(durationMillis = 400),
        label = "6d_bg_accent"
    )
    val targetSolidBg = when {
        cleanHex == "#FFFFFF" -> if (isDarkMode) Color(0xFF1E293B) else Color(0xFFFFFFFF)
        cleanHex == "#111827" || cleanHex == "#000000" -> Color(0xFF090D16)
        isDarkMode -> androidx.compose.ui.graphics.lerp( Color(0xFF0A0F1D), rawSelectedColor, 0.42f)
        else -> androidx.compose.ui.graphics.lerp(Color(0xFFF8FAFC), rawSelectedColor, 0.36f)
    }
    val baseBgColor by animateColorAsState(
        targetValue = targetSolidBg,
        animationSpec = tween(durationMillis = 400),
        label = "6d_bg_surface"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "6d_spatial_motion")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "6d_grid_phase"
    )
    val angle6D by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2.0).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "6d_hypercube_angle"
    )

    if (bgAlpha <= 0.01f) {
        Box(modifier = modifier.fillMaxSize().background(Color.Transparent))
        return
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = bgAlpha }
    ) {
        val w = size.width
        val h = size.height

        // 1. Rich Solid or Transparent Color Base Backdrop
        drawRect(color = baseBgColor.copy(alpha = bgAlpha))

        // 2. Subtle Solid Color Depth Glow
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = if (isDarkMode) 0.28f else 0.22f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.24f),
                radius = w.coerceAtLeast(h) * 0.78f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = if (isDarkMode) 0.18f else 0.14f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.82f),
                radius = w.coerceAtLeast(h) * 0.70f
            )
        )

        // 3. Upper & Lower 6D Perspective Laser Grids
        val horizonY = h * 0.34f
        val skyHorizonY = h * 0.18f
        val yawShift = sin(angle6D) * (w * 0.035f)
        val vanishingX = w * 0.5f + yawShift

        val rayCount = 14
        val floorRayAlpha = if (isDarkMode) 0.34f else 0.30f
        val skyRayAlpha = if (isDarkMode) 0.22f else 0.20f

        for (i in -rayCount..rayCount) {
            val normalized = i.toFloat() / rayCount.toFloat()
            val bottomX = vanishingX + normalized * w * 1.65f
            val topX = vanishingX + normalized * w * 1.35f

            drawLine(
                color = accentColor.copy(alpha = floorRayAlpha),
                start = Offset(vanishingX, horizonY),
                end = Offset(bottomX, h),
                strokeWidth = 1.8f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = HoloCyan.copy(alpha = skyRayAlpha),
                start = Offset(vanishingX, skyHorizonY),
                end = Offset(topX, 0f),
                strokeWidth = 1.4f,
                cap = StrokeCap.Round
            )
        }

        // 4. High-Definition Animated Depth Planes
        val horizontalRows = 11
        val maxDepthAlpha = if (isDarkMode) 0.42f else 0.36f
        for (r in 0 until horizontalRows) {
            val progress = ((r.toFloat() + phase) / horizontalRows.toFloat()).coerceIn(0f, 1f)
            val depthCurve = progress * progress
            val y = horizonY + depthCurve * (h - horizonY)
            val alpha = (depthCurve * maxDepthAlpha).coerceIn(0.06f, maxDepthAlpha)
            drawLine(
                color = accentColor.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.5f + depthCurve * 2.2f
            )
        }

        // 5. Render Chosen 3D Visual Style Centerpiece in Upper Spatial Field
        val center3D = Offset(vanishingX, h * 0.22f)
        val baseScale = (w.coerceAtMost(h) * 0.12f).coerceIn(42f, 92f)
        val strokeAlpha = if (isDarkMode) 0.56f else 0.42f

        when (visualStyleId) {
            "CYBER_SYNTHWAVE" -> {
                // Retro-futuristic Synthwave Horizon Sun + Scanline Bars + Twin Laser Beams
                val sunRadius = baseScale * 1.25f
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SolarAmber.copy(alpha = strokeAlpha * 1.3f),
                            NeonPink.copy(alpha = strokeAlpha * 1.2f),
                            accentColor.copy(alpha = strokeAlpha * 0.5f)
                        ),
                        startY = center3D.y - sunRadius,
                        endY = center3D.y + sunRadius
                    ),
                    radius = sunRadius,
                    center = center3D
                )
                for (band in 1..5) {
                    val bandY = center3D.y + (band / 6f) * sunRadius * 0.9f
                    drawLine(
                        color = baseBgColor,
                        start = Offset(center3D.x - sunRadius, bandY),
                        end = Offset(center3D.x + sunRadius, bandY),
                        strokeWidth = (band * 1.6f)
                    )
                }
                drawCircle(
                    color = HoloCyan.copy(alpha = strokeAlpha),
                    radius = sunRadius * 1.12f,
                    center = center3D,
                    style = Stroke(width = 2f)
                )
            }

            "GALAXY_VORTEX" -> {
                // Swirling 4-Arm Cosmic Nebula Spiral Vortex
                val armCount = 4
                val pointsPerArm = 18
                for (arm in 0 until armCount) {
                    val armOffset = (arm * Math.PI / 2.0).toFloat()
                    var prevPt: Offset? = null
                    for (p in 1..pointsPerArm) {
                        val t = p.toFloat() / pointsPerArm.toFloat()
                        val spiralAngle = angle6D * 1.5f + armOffset + t * 3.8f
                        val r = t * baseScale * 1.45f
                        val pt = Offset(
                            x = center3D.x + cos(spiralAngle) * r,
                            y = center3D.y + sin(spiralAngle) * r * 0.68f
                        )
                        prevPt?.let { start ->
                            drawLine(
                                color = if (arm % 2 == 0) accentColor.copy(alpha = strokeAlpha) else HoloCyan.copy(alpha = strokeAlpha),
                                start = start,
                                end = pt,
                                strokeWidth = 2.2f * (1f - t * 0.4f),
                                cap = StrokeCap.Round
                            )
                        }
                        if (p % 4 == 0) {
                            drawCircle(
                                color = NeonPink.copy(alpha = strokeAlpha * 1.2f),
                                radius = 3f,
                                center = pt
                            )
                        }
                        prevPt = pt
                    }
                }
            }

            "CRYSTAL_PRISM" -> {
                // Rotating Sacred-Geometry Octahedron & Diamond Prism
                val topApex = Offset(center3D.x, center3D.y - baseScale * 1.35f)
                val bottomApex = Offset(center3D.x, center3D.y + baseScale * 1.35f)
                val ringPts = (0 until 6).map { i ->
                    val a = angle6D + (i * Math.PI / 3.0).toFloat()
                    Offset(
                        x = center3D.x + cos(a) * baseScale * 1.2f,
                        y = center3D.y + sin(a) * baseScale * 0.48f
                    )
                }
                ringPts.forEachIndexed { idx, pt ->
                    val nextPt = ringPts[(idx + 1) % ringPts.size]
                    drawLine(
                        color = HoloCyan.copy(alpha = strokeAlpha * 1.2f),
                        start = topApex,
                        end = pt,
                        strokeWidth = 1.8f
                    )
                    drawLine(
                        color = accentColor.copy(alpha = strokeAlpha * 1.2f),
                        start = bottomApex,
                        end = pt,
                        strokeWidth = 1.8f
                    )
                    drawLine(
                        color = CyberEmerald.copy(alpha = strokeAlpha),
                        start = pt,
                        end = nextPt,
                        strokeWidth = 1.6f
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = strokeAlpha),
                        radius = 3f,
                        center = pt
                    )
                }
            }

            "AURORA_WAVE" -> {
                // Flowing Multi-Ribbon Bioluminescent Aurora Waves
                val waveCount = 4
                val segments = 24
                for (wave in 0 until waveCount) {
                    val path = Path()
                    val baseWaveY = h * (0.12f + wave * 0.045f)
                    for (s in 0..segments) {
                        val ratio = s.toFloat() / segments.toFloat()
                        val x = ratio * w
                        val y = baseWaveY + sin(ratio * 6.28f + angle6D + wave * 0.9f) * (baseScale * 0.42f)
                        if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    val waveColor = when (wave % 3) {
                        0 -> CyberEmerald
                        1 -> HoloCyan
                        else -> accentColor
                    }
                    drawPath(
                        path = path,
                        color = waveColor.copy(alpha = strokeAlpha * 1.15f),
                        style = Stroke(width = 3.2f - wave * 0.4f, cap = StrokeCap.Round)
                    )
                }
            }

            "MATRIX_CYBERNETIC" -> {
                // Concentric 3D Gyroscopic Orbital Rings & Quantum Streams
                for (ring in 1..3) {
                    val rx = baseScale * (0.55f + ring * 0.34f)
                    val ry = rx * (0.35f + 0.2f * sin(angle6D + ring))
                    drawOval(
                        color = if (ring % 2 == 0) HoloCyan.copy(alpha = strokeAlpha * 1.1f) else accentColor.copy(alpha = strokeAlpha * 1.1f),
                        topLeft = Offset(center3D.x - rx, center3D.y - ry),
                        size = Size(rx * 2f, ry * 2f),
                        style = Stroke(width = 2.2f)
                    )
                }
                for (col in -4..4) {
                    val streamX = center3D.x + col * (baseScale * 0.48f)
                    val streamTop = ((phase + kotlin.math.abs(col) * 0.13f) % 1f) * horizonY * 0.6f
                    drawLine(
                        color = CyberEmerald.copy(alpha = strokeAlpha),
                        start = Offset(streamX, streamTop),
                        end = Offset(streamX, streamTop + baseScale * 0.65f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
            }

            else -> {
                // Default: HYPERCUBE_6D Rotating 6D Hypercube (Tesseract Inner + Outer Cube + Bridges)
                val innerScale = baseScale * (0.52f + 0.12f * sin(angle6D * 2f))

                fun projectVertex(vx: Float, vy: Float, vz: Float, scale: Float, rotOffset: Float): Offset {
                    val a = angle6D + rotOffset
                    val rx = vx * cos(a) - vz * sin(a)
                    val rz = vx * sin(a) + vz * cos(a)
                    val ry = vy * cos(a * 0.7f) - rz * sin(a * 0.7f)
                    val depthFactor = 1f / (2.6f - rz * 0.45f)
                    return Offset(
                        x = center3D.x + rx * scale * depthFactor * 2.1f,
                        y = center3D.y + ry * scale * depthFactor * 2.1f
                    )
                }

                val unitVertices = listOf(
                    Triple(-1f, -1f, -1f),
                    Triple(1f, -1f, -1f),
                    Triple(1f, 1f, -1f),
                    Triple(-1f, 1f, -1f),
                    Triple(-1f, -1f, 1f),
                    Triple(1f, -1f, 1f),
                    Triple(1f, 1f, 1f),
                    Triple(-1f, 1f, 1f)
                )
                val edges = listOf(
                    0 to 1, 1 to 2, 2 to 3, 3 to 0,
                    4 to 5, 5 to 6, 6 to 7, 7 to 4,
                    0 to 4, 1 to 5, 2 to 6, 3 to 7
                )

                val outerPts = unitVertices.map { (vx, vy, vz) ->
                    projectVertex(vx, vy, vz, baseScale, 0f)
                }
                val innerPts = unitVertices.map { (vx, vy, vz) ->
                    projectVertex(vx, vy, vz, innerScale, 0.85f)
                }

                edges.forEach { (s, e) ->
                    drawLine(
                        color = HoloCyan.copy(alpha = strokeAlpha),
                        start = outerPts[s],
                        end = outerPts[e],
                        strokeWidth = 1.6f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = NeonPink.copy(alpha = strokeAlpha * 0.9f),
                        start = innerPts[s],
                        end = innerPts[e],
                        strokeWidth = 1.3f,
                        cap = StrokeCap.Round
                    )
                }
                for (idx in 0 until 8) {
                    drawLine(
                        color = accentColor.copy(alpha = strokeAlpha * 0.75f),
                        start = outerPts[idx],
                        end = innerPts[idx],
                        strokeWidth = 1.1f
                    )
                    drawCircle(
                        color = HoloCyan.copy(alpha = strokeAlpha * 1.2f),
                        radius = 2.6f,
                        center = outerPts[idx]
                    )
                }
            }
        }

        // 6. Crisp HD Multi-Plane Parallax Nodes
        val orbPositions = listOf(
            Triple(0.14f, 0.15f, 18f),
            Triple(0.86f, 0.18f, 24f),
            Triple(0.20f, 0.70f, 16f),
            Triple(0.80f, 0.66f, 22f)
        )
        val orbAlpha = if (isDarkMode) 0.30f else 0.20f
        orbPositions.forEachIndexed { idx, (rx, ry, radius) ->
            val bobOffset = sin((phase * 6.28318f) + idx * 1.4f) * 12f
            val center = Offset(w * rx, h * ry + bobOffset)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = orbAlpha),
                        HoloCyan.copy(alpha = orbAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.4f
                ),
                radius = radius * 2.4f,
                center = center
            )
        }

        // 7. Horizon Specular Laser Line
        val horizonPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(w, horizonY)
        }
        drawPath(
            path = horizonPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    HoloCyan.copy(alpha = if (isDarkMode) 0.55f else 0.40f),
                    accentColor.copy(alpha = if (isDarkMode) 0.70f else 0.50f),
                    HoloCyan.copy(alpha = if (isDarkMode) 0.55f else 0.40f),
                    Color.Transparent
                )
            ),
            style = Stroke(width = 2f)
        )
    }
}
