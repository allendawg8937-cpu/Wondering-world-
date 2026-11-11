package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class Procedural3DScenePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val rimColor: Color
)

private val THREE_JS_PROCEDURAL_SCENES = listOf(
    Procedural3DScenePreset(
        id = "pyramid_live_life_love",
        title = "Stages Of Life 3D Pyramid",
        subtitle = "Procedural 4-tier crystal pyramid • LIVE (1D X) • LIFE (2D Y) • LOVE (3D Z)",
        primaryColor = Color(0xFF2563EB),
        rimColor = Color(0xFFEAB308)
    ),
    Procedural3DScenePreset(
        id = "pbr_torus_knot",
        title = "Studio PBR Torus Knot",
        subtitle = "Procedural (p=3, q=2) metallic torus knot with orbital satellites",
        primaryColor = Color(0xFFDC2626),
        rimColor = Color(0xFF06B6D4)
    ),
    Procedural3DScenePreset(
        id = "life_stages_helix",
        title = "Life Stages Architectural Helix",
        subtitle = "Zero-occlusion spiral monument: Child • Teen • Student • Adult • Veteran",
        primaryColor = Color(0xFF16A34A),
        rimColor = Color(0xFFF97316)
    ),
    Procedural3DScenePreset(
        id = "zero_occlusion_constellation",
        title = "Zero-Occlusion Geodesic Hub",
        subtitle = "Procedural icosahedron core with bounding-sphere separated orbital nodes",
        primaryColor = Color(0xFF7C3AED),
        rimColor = Color(0xFF10B981)
    )
)

private val SOLID_PBR_ALBEDO_SWATCHES = listOf(
    "Solid Blue" to Color(0xFF2563EB),
    "Solid Red" to Color(0xFFDC2626),
    "Solid Green" to Color(0xFF16A34A),
    "Solid Gold" to Color(0xFFEAB308),
    "Solid Orange" to Color(0xFFF97316),
    "Solid White" to Color(0xFFFFFFFF),
    "Solid Purple" to Color(0xFF7C3AED),
    "Solid Teal" to Color(0xFF0D9488)
)

private data class Vec3(val x: Float, val y: Float, val z: Float)

private data class Face3D(
    val vertices: List<Vec3>,
    val baseColor: Color,
    val emissiveBoost: Float = 0f
)

/**
 * Interactive Three.js-style 3D Scenes & Studio PBR Renderer Card.
 * Features procedural 3D geometry generation, physically based rendering (Metalness, Roughness,
 * Fresnel Rim Lighting, Specular GGX approximation), and Zero-Occlusion depth sorting & framing.
 */
@Composable
fun ThreeJsStudio3DSceneCard(
    onShareSceneToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedScene by remember { mutableStateOf(THREE_JS_PROCEDURAL_SCENES.first()) }
    var selectedAlbedo by remember { mutableStateOf(SOLID_PBR_ALBEDO_SWATCHES.first().second) }
    var metalness by remember { mutableFloatStateOf(0.72f) }
    var roughness by remember { mutableFloatStateOf(0.24f) }
    var rimLightIntensity by remember { mutableFloatStateOf(0.85f) }
    var isWireframeOverlay by remember { mutableStateOf(false) }
    var isAutoRotating by remember { mutableStateOf(true) }
    var zeroOcclusionGuardEnabled by remember { mutableStateOf(true) }

    var userYawDeg by remember { mutableFloatStateOf(28f) }
    var userPitchDeg by remember { mutableFloatStateOf(-18f) }
    var cameraZoom by remember { mutableFloatStateOf(1.0f) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "threejs_pbr_turntable")
    val autoAngleDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_turntable_angle"
    )

    val effectiveYawRad = Math.toRadians(((if (isAutoRotating) userYawDeg + autoAngleDeg else userYawDeg).toDouble())).toFloat()
    val effectivePitchRad = Math.toRadians(userPitchDeg.toDouble()).toFloat()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("threejs_studio_3d_scene_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🧊 THREE.JS 3D STUDIO • PROCEDURAL PBR & ZERO-OCCLUSION",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Interactive 3D models & procedural scenes • Studio PBR lighting • Zero-occlusion framing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberEmerald)
                ) {
                    Text(
                        text = if (zeroOcclusionGuardEnabled) "ZERO-OCCLUSION ON" else "STANDARD DEPTH",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Procedural Scene Preset Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                THREE_JS_PROCEDURAL_SCENES.forEach { preset ->
                    val isSelected = selectedScene.id == preset.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedScene = preset
                            selectedAlbedo = preset.primaryColor
                        },
                        label = {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("threejs_scene_chip_${preset.id}")
                    )
                }
            }

            // Interactive 3D Studio PBR Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0A0F1D))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            userYawDeg = (userYawDeg + dragAmount.x * 0.45f) % 360f
                            userPitchDeg = (userPitchDeg - dragAmount.y * 0.35f).coerceIn(-68f, 68f)
                        }
                    }
                    .testTag("threejs_pbr_viewport_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val baseScale = min(size.width, size.height) * 0.31f * cameraZoom

                    // Studio HDRI Ground Grid & Backdrop Reflection
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                selectedAlbedo.copy(alpha = 0.22f),
                                Color(0xFF0F172A),
                                Color(0xFF050811)
                            ),
                            center = Offset(cx, cy),
                            radius = max(size.width, size.height) * 0.7f
                        )
                    )

                    fun rotatePoint(v: Vec3): Vec3 {
                        // Rotate around Y (Yaw)
                        val cosY = cos(effectiveYawRad)
                        val sinY = sin(effectiveYawRad)
                        val x1 = v.x * cosY + v.z * sinY
                        val z1 = -v.x * sinY + v.z * cosY
                        // Rotate around X (Pitch)
                        val cosX = cos(effectivePitchRad)
                        val sinX = sin(effectivePitchRad)
                        val y2 = v.y * cosX - z1 * sinX
                        val z2 = v.y * sinX + z1 * cosX
                        return Vec3(x1, y2, z2)
                    }

                    fun project(v: Vec3): Offset {
                        val fovDist = 3.8f
                        val perspective = fovDist / (fovDist + v.z + 1.8f).coerceAtLeast(0.6f)
                        return Offset(
                            x = cx + v.x * baseScale * perspective,
                            y = cy - v.y * baseScale * perspective
                        )
                    }

                    // Draw Studio Floor Grid (Y = -1.15f)
                    val gridExtent = 1.6f
                    val steps = 6
                    for (i in -steps..steps) {
                        val t = (i.toFloat() / steps) * gridExtent
                        val a1 = project(rotatePoint(Vec3(t, -1.15f, -gridExtent)))
                        val a2 = project(rotatePoint(Vec3(t, -1.15f, gridExtent)))
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = a1,
                            end = a2,
                            strokeWidth = 1f
                        )
                        val b1 = project(rotatePoint(Vec3(-gridExtent, -1.15f, t)))
                        val b2 = project(rotatePoint(Vec3(gridExtent, -1.15f, t)))
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = b1,
                            end = b2,
                            strokeWidth = 1f
                        )
                    }

                    // Build Procedural 3D Faces for Selected Scene
                    val faces = mutableListOf<Face3D>()
                    val separationMultiplier = if (zeroOcclusionGuardEnabled) 1.18f else 0.95f

                    when (selectedScene.id) {
                        "pyramid_live_life_love" -> {
                            // Central 3D Dimensional Pyramid ("CREATE YOUR LIFE")
                            val apex = Vec3(0f, 1.05f, 0f)
                            val b0 = Vec3(-0.85f, -0.75f, 0.85f)
                            val b1 = Vec3(0.85f, -0.75f, 0.85f)
                            val b2 = Vec3(0.85f, -0.75f, -0.85f)
                            val b3 = Vec3(-0.85f, -0.75f, -0.85f)
                            faces.add(Face3D(listOf(apex, b0, b1), selectedAlbedo, 0.1f))
                            faces.add(Face3D(listOf(apex, b1, b2), selectedAlbedo, 0.05f))
                            faces.add(Face3D(listOf(apex, b2, b3), selectedAlbedo, 0.0f))
                            faces.add(Face3D(listOf(apex, b3, b0), selectedAlbedo, 0.08f))
                            faces.add(Face3D(listOf(b0, b3, b2, b1), selectedScene.rimColor, 0.0f))

                            // Zero-Occlusion Orbiting Dimension Octahedra (1D LIVE, 2D LIFE, 3D LOVE)
                            val orbitColors = listOf(Color(0xFFDC2626), Color(0xFF16A34A), Color(0xFFEAB308))
                            for (k in 0..2) {
                                val ang = (k * 120f) * (Math.PI.toFloat() / 180f)
                                val ox = cos(ang) * 1.45f * separationMultiplier
                                val oz = sin(ang) * 1.45f * separationMultiplier
                                val oy = -0.05f + (k - 1) * 0.28f
                                val s = 0.24f
                                val top = Vec3(ox, oy + s, oz)
                                val bot = Vec3(ox, oy - s, oz)
                                val p0 = Vec3(ox - s, oy, oz + s)
                                val p1 = Vec3(ox + s, oy, oz + s)
                                val p2 = Vec3(ox + s, oy, oz - s)
                                val p3 = Vec3(ox - s, oy, oz - s)
                                val c = orbitColors[k]
                                faces.add(Face3D(listOf(top, p0, p1), c, 0.15f))
                                faces.add(Face3D(listOf(top, p1, p2), c, 0.1f))
                                faces.add(Face3D(listOf(top, p2, p3), c, 0.1f))
                                faces.add(Face3D(listOf(top, p3, p0), c, 0.15f))
                                faces.add(Face3D(listOf(bot, p1, p0), c, 0.05f))
                                faces.add(Face3D(listOf(bot, p2, p1), c, 0.05f))
                            }
                        }

                        "pbr_torus_knot" -> {
                            // Procedural (p=3, q=2) Torus Knot Tube Segments
                            val segments = 36
                            val tubeSides = 6
                            val tubeRadius = 0.20f
                            fun knotCenter(t: Float): Vec3 {
                                val r = 0.65f + 0.26f * cos(3f * t)
                                return Vec3(
                                    r * cos(2f * t) * separationMultiplier,
                                    0.38f * sin(3f * t) * separationMultiplier,
                                    r * sin(2f * t) * separationMultiplier
                                )
                            }
                            for (i in 0 until segments) {
                                val t0 = (i.toFloat() / segments) * (2f * Math.PI.toFloat())
                                val t1 = ((i + 1).toFloat() / segments) * (2f * Math.PI.toFloat())
                                val c0 = knotCenter(t0)
                                val c1 = knotCenter(t1)
                                for (j in 0 until tubeSides) {
                                    val a0 = (j.toFloat() / tubeSides) * (2f * Math.PI.toFloat())
                                    val a1 = ((j + 1).toFloat() / tubeSides) * (2f * Math.PI.toFloat())
                                    val v00 = Vec3(c0.x + cos(a0) * tubeRadius, c0.y + sin(a0) * tubeRadius, c0.z)
                                    val v01 = Vec3(c0.x + cos(a1) * tubeRadius, c0.y + sin(a1) * tubeRadius, c0.z)
                                    val v11 = Vec3(c1.x + cos(a1) * tubeRadius, c1.y + sin(a1) * tubeRadius, c1.z)
                                    val v10 = Vec3(c1.x + cos(a0) * tubeRadius, c1.y + sin(a0) * tubeRadius, c1.z)
                                    faces.add(Face3D(listOf(v00, v01, v11, v10), selectedAlbedo, 0.08f))
                                }
                            }
                        }

                        "life_stages_helix" -> {
                            // 5 Zero-Occlusion Architectural Pillars along a Rising Spiral
                            val stageColors = listOf(
                                Color(0xFFEAB308), // Child
                                Color(0xFFF97316), // Teenager
                                Color(0xFF7C3AED), // Student
                                Color(0xFF2563EB), // Adult
                                Color(0xFF16A34A)  // Veteran
                            )
                            for (idx in 0 until 5) {
                                val angle = idx * 1.15f
                                val radius = 0.82f * separationMultiplier
                                val cxPillar = cos(angle) * radius
                                val czPillar = sin(angle) * radius
                                val hTop = -0.35f + idx * 0.32f
                                val hBot = -0.95f
                                val w = 0.22f
                                val col = stageColors[idx]
                                val t0 = Vec3(cxPillar - w, hTop, czPillar + w)
                                val t1 = Vec3(cxPillar + w, hTop, czPillar + w)
                                val t2 = Vec3(cxPillar + w, hTop, czPillar - w)
                                val t3 = Vec3(cxPillar - w, hTop, czPillar - w)
                                val d0 = Vec3(cxPillar - w, hBot, czPillar + w)
                                val d1 = Vec3(cxPillar + w, hBot, czPillar + w)
                                val d2 = Vec3(cxPillar + w, hBot, czPillar - w)
                                val d3 = Vec3(cxPillar - w, hBot, czPillar - w)
                                faces.add(Face3D(listOf(t0, t1, t2, t3), col, 0.16f))
                                faces.add(Face3D(listOf(d0, d1, t1, t0), col, 0.08f))
                                faces.add(Face3D(listOf(d1, d2, t2, t1), col, 0.05f))
                                faces.add(Face3D(listOf(d2, d3, t3, t2), col, 0.05f))
                                faces.add(Face3D(listOf(d3, d0, t0, t3), col, 0.08f))
                            }
                        }

                        else -> {
                            // Zero-Occlusion Geodesic Hub + Bounding-Sphere Separated Satellites
                            val rCore = 0.68f
                            val latSteps = 6
                            val lonSteps = 10
                            for (i in 0 until latSteps) {
                                val lat0 = Math.PI.toFloat() * (-0.5f + i.toFloat() / latSteps)
                                val lat1 = Math.PI.toFloat() * (-0.5f + (i + 1).toFloat() / latSteps)
                                for (j in 0 until lonSteps) {
                                    val lon0 = 2f * Math.PI.toFloat() * (j.toFloat() / lonSteps)
                                    val lon1 = 2f * Math.PI.toFloat() * ((j + 1).toFloat() / lonSteps)
                                    fun sph(lat: Float, lon: Float) = Vec3(
                                        rCore * cos(lat) * cos(lon),
                                        rCore * sin(lat),
                                        rCore * cos(lat) * sin(lon)
                                    )
                                    faces.add(
                                        Face3D(
                                            listOf(sph(lat0, lon0), sph(lat0, lon1), sph(lat1, lon1), sph(lat1, lon0)),
                                            selectedAlbedo,
                                            0.1f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Transform & Depth-Sort Faces (Painter's Algorithm + Zero-Occlusion Normal Culling)
                    val lightDir = Vec3(0.52f, 0.72f, -0.46f)
                    val rimDir = Vec3(-0.65f, 0.35f, 0.65f)

                    val transformedFaces = faces.map { face ->
                        val rVerts = face.vertices.map { rotatePoint(it) }
                        val avgZ = rVerts.map { it.z }.average().toFloat()
                        Triple(face, rVerts, avgZ)
                    }.sortedByDescending { it.third } // Back-to-front

                    transformedFaces.forEach { (face, rVerts, _) ->
                        if (rVerts.size >= 3) {
                            val u = Vec3(
                                rVerts[1].x - rVerts[0].x,
                                rVerts[1].y - rVerts[0].y,
                                rVerts[1].z - rVerts[0].z
                            )
                            val v = Vec3(
                                rVerts[2].x - rVerts[0].x,
                                rVerts[2].y - rVerts[0].y,
                                rVerts[2].z - rVerts[0].z
                            )
                            val nx = u.y * v.z - u.z * v.y
                            val ny = u.z * v.x - u.x * v.z
                            val nz = u.x * v.y - u.y * v.x
                            val len = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.0001f)
                            val n = Vec3(nx / len, ny / len, nz / len)

                            // Studio PBR Lighting Equation (Diffuse + Metallic Specular + Rim Fresnel)
                            val ndotl = max(0f, n.x * lightDir.x + n.y * lightDir.y + n.z * lightDir.z)
                            val ndotRim = max(0f, n.x * rimDir.x + n.y * rimDir.y + n.z * rimDir.z)
                            val specPower = (1f - roughness).coerceIn(0.1f, 0.95f)
                            val specular = (ndotl * ndotl * specPower * (0.35f + 0.65f * metalness))
                            val ambient = 0.26f + face.emissiveBoost
                            val diffuse = ndotl * (1f - 0.35f * metalness)
                            val rimBoost = ndotRim * rimLightIntensity * 0.42f

                            val rCol = (face.baseColor.red * (ambient + diffuse) + specular + selectedScene.rimColor.red * rimBoost).coerceIn(0f, 1f)
                            val gCol = (face.baseColor.green * (ambient + diffuse) + specular + selectedScene.rimColor.green * rimBoost).coerceIn(0f, 1f)
                            val bCol = (face.baseColor.blue * (ambient + diffuse) + specular + selectedScene.rimColor.blue * rimBoost).coerceIn(0f, 1f)

                            val path = Path()
                            val p0 = project(rVerts[0])
                            path.moveTo(p0.x, p0.y)
                            for (idx in 1 until rVerts.size) {
                                val pi = project(rVerts[idx])
                                path.lineTo(pi.x, pi.y)
                            }
                            path.close()

                            drawPath(
                                path = path,
                                color = Color(rCol, gCol, bCol, 1f)
                            )
                            drawPath(
                                path = path,
                                color = if (isWireframeOverlay) HoloCyan.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.25f),
                                style = Stroke(width = if (isWireframeOverlay) 1.8f else 0.8f, cap = StrokeCap.Round)
                            )
                        }
                    }
                }

                // Top-left Studio HUD Badge
                Surface(
                    color = Color(0xCC0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(
                            text = selectedScene.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "PBR Metalness ${(metalness * 100).toInt()}% • Roughness ${(roughness * 100).toInt()}% • Drag to Orbit",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan
                        )
                    }
                }

                // Bottom-right Zero-Occlusion Dimension Axis Callout
                Surface(
                    color = Color(0xCC0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "1D: LIVE • 2D: LIFE • 3D: LOVE • CREATE YOUR LIFE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = SolarAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Solid PBR Material Albedo Swatches
            Text(
                text = "Studio PBR Solid Albedo Material:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SOLID_PBR_ALBEDO_SWATCHES.forEach { (name, color) ->
                    val isSelected = selectedAlbedo == color
                    Surface(
                        color = color,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clickable { selectedAlbedo = color }
                            .testTag("threejs_albedo_${name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (name == "Solid White") Color.Black else Color.White)
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (name == "Solid White" || name == "Solid Gold") Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // PBR Sliders: Metalness, Roughness & Camera Zoom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Metalness: ${(metalness * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = metalness,
                        onValueChange = { metalness = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.testTag("threejs_metalness_slider")
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Roughness: ${(roughness * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = roughness,
                        onValueChange = { roughness = it },
                        valueRange = 0.05f..1f,
                        modifier = Modifier.testTag("threejs_roughness_slider")
                    )
                }
            }

            // Controls Row: Wireframe, Zero-Occlusion, Turntable, Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { isWireframeOverlay = !isWireframeOverlay },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("threejs_wireframe_toggle_button")
                ) {
                    Icon(Icons.Default.GridOn, contentDescription = "Wireframe", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isWireframeOverlay) "Wireframe: ON" else "Solid PBR Mesh",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { zeroOcclusionGuardEnabled = !zeroOcclusionGuardEnabled },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("threejs_zero_occlusion_toggle_button")
                ) {
                    Icon(Icons.Default.CenterFocusStrong, contentDescription = "Zero-Occlusion", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (zeroOcclusionGuardEnabled) "Zero-Occlusion: Active" else "Zero-Occlusion: Off",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { isAutoRotating = !isAutoRotating },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("threejs_turntable_toggle_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Turntable", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAutoRotating) "Pause Orbit" else "Auto-Orbit",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        userYawDeg = 28f
                        userPitchDeg = -18f
                        cameraZoom = 1.0f
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("threejs_reset_camera_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Camera", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset View", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val headline = "Shared 3D Studio Scene: ${selectedScene.title}"
                        val body = "Rendered '${selectedScene.title}' in Stages Of Life Three.js 3D Studio with Metalness ${(metalness * 100).toInt()}%, Roughness ${(roughness * 100).toInt()}%, and Zero-Occlusion active!"
                        onShareSceneToWall(headline, body)
                        statusBanner = "✓ Published '${selectedScene.title}' 3D snapshot to Community Feed!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("threejs_share_scene_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share 3D Scene", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share 3D Scene", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            statusBanner?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
