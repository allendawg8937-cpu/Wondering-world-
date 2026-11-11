package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import kotlin.math.cos
import kotlin.math.sin

enum class StudioCanvasEffectType(
    val id: String,
    val title: String,
    val subtitle: String,
    val accent: Color
) {
    CYBER_HUD_GRID(
        id = "cyber_hud",
        title = "6D Cyber HUD",
        subtitle = "Perspective Grid & Reticle",
        accent = NeonPink
    ),
    SYNTHWAVE_SCANLINES(
        id = "synth_scanlines",
        title = "Synth CRT Sun",
        subtitle = "Scanlines & Solar Horizon",
        accent = RosePulse
    ),
    HOLO_PARTICLES(
        id = "holo_particles",
        title = "Holo Starlight",
        subtitle = "Bokeh Orbs & Prism Links",
        accent = HoloCyan
    ),
    PRISM_RAYS(
        id = "prism_rays",
        title = "Crystal Prism",
        subtitle = "Spectral Light Beams",
        accent = CyberEmerald
    ),
    FILM_LIGHT_LEAK(
        id = "film_leak",
        title = "Cinema Leak",
        subtitle = "Warm Sprocket & Amber Glow",
        accent = SolarAmber
    ),
    NOIR_SPOTLIGHT(
        id = "noir_spotlight",
        title = "Noir Spotlight",
        subtitle = "Dramatic Radial Vignette",
        accent = Color(0xFFE2E8F0)
    ),
    VETERAN_TACTICAL_HUD(
        id = "veteran_tactical",
        title = "Tactical Crosshair",
        subtitle = "Mil-Spec Crosshair & Olive Camo",
        accent = Color(0xFF15803D)
    ),
    BUSINESS_EXECUTIVE_GOLD(
        id = "business_executive",
        title = "Executive Pinstripe",
        subtitle = "Navy Horizon & Golden Geometric Crest",
        accent = Color(0xFFEAB308)
    ),
    STUDENT_BLUEPRINT_GRID(
        id = "student_blueprint",
        title = "Blueprint Academy",
        subtitle = "Cyber Grid & Graduation Constellation",
        accent = Color(0xFF7C3AED)
    ),
    TEEN_NEON_POP(
        id = "teen_neon_pop",
        title = "Arcade Glitch Pop",
        subtitle = "Chromatic Waves & Cyber Starburst",
        accent = Color(0xFFF97316)
    ),
    KIDS_RAINBOW_SPARKLE(
        id = "kids_rainbow_sparkle",
        title = "Toon Sparkle Wonder",
        subtitle = "Pastel Floating Stars & Rainbow Halos",
        accent = Color(0xFFFACC15)
    ),
    NONE(
        id = "clean_lens",
        title = "Clean Lens",
        subtitle = "Color Matrix Only",
        accent = HoloCyan
    )
}

data class PhotoStudioFilterPreset(
    val id: String,
    val name: String,
    val badge: String,
    val accent: Color,
    val matrixValues: FloatArray,
    val defaultCanvasEffect: StudioCanvasEffectType = StudioCanvasEffectType.CYBER_HUD_GRID
)

val PHOTO_STUDIO_FILTERS = listOf(
    PhotoStudioFilterPreset(
        id = "original",
        name = "Original HD",
        badge = "NATURAL",
        accent = HoloCyan,
        defaultCanvasEffect = StudioCanvasEffectType.HOLO_PARTICLES,
        matrixValues = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "cyber_neon",
        name = "Cyber Neon",
        badge = "6D GLOW",
        accent = NeonPink,
        defaultCanvasEffect = StudioCanvasEffectType.CYBER_HUD_GRID,
        matrixValues = floatArrayOf(
            1.25f, 0f, 0.15f, 0f, 12f,
            0f, 0.92f, 0.12f, 0f, -6f,
            0.18f, 0f, 1.35f, 0f, 24f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "synthwave",
        name = "Synthwave",
        badge = "RETRO 3D",
        accent = RosePulse,
        defaultCanvasEffect = StudioCanvasEffectType.SYNTHWAVE_SCANLINES,
        matrixValues = floatArrayOf(
            1.30f, 0.08f, 0f, 0f, 18f,
            0f, 0.85f, 0.05f, 0f, -10f,
            0.10f, 0.10f, 1.25f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "golden_hour",
        name = "Golden Hour",
        badge = "SOLAR",
        accent = SolarAmber,
        defaultCanvasEffect = StudioCanvasEffectType.FILM_LIGHT_LEAK,
        matrixValues = floatArrayOf(
            1.22f, 0.10f, 0f, 0f, 20f,
            0.05f, 1.08f, 0f, 0f, 10f,
            0f, 0f, 0.82f, 0f, -12f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "aqua_matrix",
        name = "Aqua Matrix",
        badge = "HOLO CYAN",
        accent = HoloCyan,
        defaultCanvasEffect = StudioCanvasEffectType.HOLO_PARTICLES,
        matrixValues = floatArrayOf(
            0.80f, 0f, 0.10f, 0f, -8f,
            0.05f, 1.22f, 0.12f, 0f, 14f,
            0.10f, 0.15f, 1.32f, 0f, 22f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "emerald_glow",
        name = "Emerald Glow",
        badge = "BIO MINT",
        accent = CyberEmerald,
        defaultCanvasEffect = StudioCanvasEffectType.PRISM_RAYS,
        matrixValues = floatArrayOf(
            0.85f, 0.10f, 0f, 0f, 0f,
            0.10f, 1.32f, 0.08f, 0f, 16f,
            0f, 0.08f, 0.95f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "vintage_warm",
        name = "Vintage Film",
        badge = "CLASSIC",
        accent = SolarAmber,
        defaultCanvasEffect = StudioCanvasEffectType.FILM_LIGHT_LEAK,
        matrixValues = floatArrayOf(
            0.393f + 0.5f, 0.769f * 0.5f, 0.189f * 0.5f, 0f, 10f,
            0.349f * 0.5f, 0.686f * 0.5f + 0.5f, 0.168f * 0.5f, 0f, 6f,
            0.272f * 0.5f, 0.534f * 0.5f, 0.131f * 0.5f + 0.4f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "noir_bw",
        name = "Noir Contrast",
        badge = "MONO HD",
        accent = Color(0xFFE2E8F0),
        defaultCanvasEffect = StudioCanvasEffectType.NOIR_SPOTLIGHT,
        matrixValues = floatArrayOf(
            0.38f, 0.52f, 0.18f, 0f, 8f,
            0.38f, 0.52f, 0.18f, 0f, 8f,
            0.38f, 0.52f, 0.18f, 0f, 8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "veteran_olive_hdr",
        name = "Veteran Camo HDR",
        badge = "VETERAN",
        accent = Color(0xFF15803D),
        defaultCanvasEffect = StudioCanvasEffectType.VETERAN_TACTICAL_HUD,
        matrixValues = floatArrayOf(
            0.88f, 0.18f, 0.05f, 0f, 4f,
            0.12f, 1.25f, 0.08f, 0f, 10f,
            0.05f, 0.15f, 0.78f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "business_executive_slate",
        name = "Executive Navy",
        badge = "BUSINESS",
        accent = Color(0xFF1E3A8A),
        defaultCanvasEffect = StudioCanvasEffectType.BUSINESS_EXECUTIVE_GOLD,
        matrixValues = floatArrayOf(
            0.92f, 0.05f, 0.15f, 0f, -4f,
            0.04f, 1.02f, 0.10f, 0f, 2f,
            0.15f, 0.18f, 1.35f, 0f, 16f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "student_academe_indigo",
        name = "Academia Indigo",
        badge = "STUDENT",
        accent = Color(0xFF7C3AED),
        defaultCanvasEffect = StudioCanvasEffectType.STUDENT_BLUEPRINT_GRID,
        matrixValues = floatArrayOf(
            1.15f, 0.05f, 0.22f, 0f, 14f,
            0.05f, 0.95f, 0.12f, 0f, -2f,
            0.20f, 0.10f, 1.38f, 0f, 24f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "teen_hyper_arcade",
        name = "Arcade Pop Tangerine",
        badge = "TEEN",
        accent = Color(0xFFF97316),
        defaultCanvasEffect = StudioCanvasEffectType.TEEN_NEON_POP,
        matrixValues = floatArrayOf(
            1.38f, 0.10f, 0.02f, 0f, 22f,
            0.08f, 1.10f, 0.05f, 0f, 12f,
            0.02f, 0.08f, 0.85f, 0f, -10f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    PhotoStudioFilterPreset(
        id = "kids_vibrant_cartoon",
        name = "Toon Sunshine",
        badge = "KID",
        accent = Color(0xFFEAB308),
        defaultCanvasEffect = StudioCanvasEffectType.KIDS_RAINBOW_SPARKLE,
        matrixValues = floatArrayOf(
            1.28f, 0.15f, 0.02f, 0f, 24f,
            0.05f, 1.25f, 0.02f, 0f, 22f,
            0.02f, 0.05f, 1.15f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

/**
 * Renders the selected ColorMatrix filter, brightness adjustment, custom procedural Canvas effect,
 * and optional 3D neon border into a new Android Bitmap that can be saved directly as the user's profile photo.
 */
fun applyStudioFilterToBitmap(
    source: Bitmap,
    filter: PhotoStudioFilterPreset,
    brightnessOffset: Float,
    addNeonBorder: Boolean,
    canvasEffect: StudioCanvasEffectType = filter.defaultCanvasEffect,
    effectIntensity: Float = 0.85f
): Bitmap {
    val width = source.width.coerceAtLeast(1)
    val height = source.height.coerceAtLeast(1)
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val adjustedValues = filter.matrixValues.copyOf()
    adjustedValues[4] += brightnessOffset
    adjustedValues[9] += brightnessOffset
    adjustedValues[14] += brightnessOffset

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(ColorMatrix(adjustedValues))
    }
    canvas.drawBitmap(source, 0f, 0f, paint)

    // Render custom procedural Canvas effect onto the output Bitmap
    drawProceduralEffectOnAndroidCanvas(
        canvas = canvas,
        width = width.toFloat(),
        height = height.toFloat(),
        effect = canvasEffect,
        intensity = effectIntensity.coerceIn(0f, 1f)
    )

    if (addNeonBorder) {
        val strokeWidth = (width.coerceAtMost(height) * 0.045f).coerceAtLeast(6f)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            shader = LinearGradient(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#06B6D4"),
                    android.graphics.Color.parseColor("#EC4899"),
                    android.graphics.Color.parseColor("#10B981")
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        val half = strokeWidth / 2f
        canvas.drawRoundRect(
            half,
            half,
            width - half,
            height - half,
            width * 0.12f,
            height * 0.12f,
            borderPaint
        )
    }

    return output
}

/**
 * Draws the selected custom Canvas filter effect onto an Android Bitmap Canvas when saving/capturing frames.
 */
private fun drawProceduralEffectOnAndroidCanvas(
    canvas: Canvas,
    width: Float,
    height: Float,
    effect: StudioCanvasEffectType,
    intensity: Float
) {
    if (effect == StudioCanvasEffectType.NONE || intensity <= 0.02f) return
    val alphaScale = (intensity * 255).toInt().coerceIn(0, 255)

    when (effect) {
        StudioCanvasEffectType.CYBER_HUD_GRID -> {
            val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.006f).coerceAtLeast(2f)
                color = android.graphics.Color.argb((alphaScale * 0.55f).toInt(), 6, 182, 212)
            }
            val horizonY = height * 0.68f
            // Perspective floor lines
            for (i in -4..4) {
                val startX = width * 0.5f + (i * width * 0.06f)
                val endX = width * 0.5f + (i * width * 0.24f)
                canvas.drawLine(startX, horizonY, endX, height, gridPaint)
            }
            for (step in 1..4) {
                val y = horizonY + (height - horizonY) * (step / 4f)
                canvas.drawLine(0f, y, width, y, gridPaint)
            }
            // Targeting Reticle & Corner HUD Brackets
            val hudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.009f).coerceAtLeast(3f)
                color = android.graphics.Color.argb((alphaScale * 0.85f).toInt(), 236, 72, 153)
            }
            val cx = width * 0.5f
            val cy = height * 0.44f
            val radius = width * 0.18f
            canvas.drawCircle(cx, cy, radius, hudPaint)
            val bracket = width * 0.09f
            val pad = width * 0.08f
            // Top-left bracket
            canvas.drawLine(pad, pad, pad + bracket, pad, hudPaint)
            canvas.drawLine(pad, pad, pad, pad + bracket, hudPaint)
            // Top-right bracket
            canvas.drawLine(width - pad, pad, width - pad - bracket, pad, hudPaint)
            canvas.drawLine(width - pad, pad, width - pad, pad + bracket, hudPaint)
            // Bottom-left bracket
            canvas.drawLine(pad, height - pad, pad + bracket, height - pad, hudPaint)
            canvas.drawLine(pad, height - pad, pad, height - pad - bracket, hudPaint)
            // Bottom-right bracket
            canvas.drawLine(width - pad, height - pad, width - pad - bracket, height - pad, hudPaint)
            canvas.drawLine(width - pad, height - pad, width - pad, height - pad - bracket, hudPaint)
        }

        StudioCanvasEffectType.SYNTHWAVE_SCANLINES -> {
            // Retro solar glow at top center
            val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    width * 0.5f,
                    height * 0.28f,
                    width * 0.35f,
                    intArrayOf(
                        android.graphics.Color.argb((alphaScale * 0.55f).toInt(), 244, 63, 94),
                        android.graphics.Color.argb((alphaScale * 0.35f).toInt(), 245, 158, 11),
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(width * 0.5f, height * 0.28f, width * 0.35f, sunPaint)

            // CRT Horizontal Scanlines
            val scanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (height * 0.005f).coerceAtLeast(1.5f)
                color = android.graphics.Color.argb((alphaScale * 0.32f).toInt(), 236, 72, 153)
            }
            val step = (height / 28f).coerceAtLeast(6f)
            var y = 0f
            while (y < height) {
                canvas.drawLine(0f, y, width, y, scanPaint)
                y += step
            }
        }

        StudioCanvasEffectType.HOLO_PARTICLES -> {
            val orbPositions = listOf(
                Triple(0.20f, 0.22f, 0.07f),
                Triple(0.82f, 0.25f, 0.09f),
                Triple(0.18f, 0.75f, 0.08f),
                Triple(0.78f, 0.72f, 0.06f),
                Triple(0.50f, 0.16f, 0.05f),
                Triple(0.34f, 0.52f, 0.04f),
                Triple(0.68f, 0.46f, 0.05f)
            )
            val linkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.004f).coerceAtLeast(1.5f)
                color = android.graphics.Color.argb((alphaScale * 0.42f).toInt(), 6, 182, 212)
                pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 0f)
            }
            for (i in 0 until orbPositions.size - 1) {
                val a = orbPositions[i]
                val b = orbPositions[i + 1]
                canvas.drawLine(a.first * width, a.second * height, b.first * width, b.second * height, linkPaint)
            }
            orbPositions.forEachIndexed { idx, (rx, ry, rr) ->
                val cx = rx * width
                val cy = ry * height
                val r = (rr * width).coerceAtLeast(8f)
                val orbColor = if (idx % 2 == 0) {
                    android.graphics.Color.argb((alphaScale * 0.65f).toInt(), 6, 182, 212)
                } else {
                    android.graphics.Color.argb((alphaScale * 0.65f).toInt(), 236, 72, 153)
                }
                val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = RadialGradient(
                        cx,
                        cy,
                        r,
                        intArrayOf(orbColor, android.graphics.Color.TRANSPARENT),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawCircle(cx, cy, r, orbPaint)
            }
        }

        StudioCanvasEffectType.PRISM_RAYS -> {
            val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                shader = LinearGradient(
                    0f,
                    0f,
                    width,
                    height,
                    intArrayOf(
                        android.graphics.Color.argb((alphaScale * 0.42f).toInt(), 6, 182, 212),
                        android.graphics.Color.argb((alphaScale * 0.35f).toInt(), 236, 72, 153),
                        android.graphics.Color.argb((alphaScale * 0.42f).toInt(), 16, 185, 129)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            val path1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(width * 0.38f, 0f)
                lineTo(width, height * 0.65f)
                lineTo(width, height)
                lineTo(width * 0.62f, height)
                lineTo(0f, height * 0.35f)
                close()
            }
            canvas.drawPath(path1, rayPaint)

            // Geometric diamond facet frame
            val facetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.007f).coerceAtLeast(2.5f)
                color = android.graphics.Color.argb((alphaScale * 0.80f).toInt(), 16, 185, 129)
            }
            val diamond = Path().apply {
                moveTo(width * 0.5f, height * 0.08f)
                lineTo(width * 0.92f, height * 0.5f)
                lineTo(width * 0.5f, height * 0.92f)
                lineTo(width * 0.08f, height * 0.5f)
                close()
            }
            canvas.drawPath(diamond, facetPaint)
        }

        StudioCanvasEffectType.FILM_LIGHT_LEAK -> {
            // Left warm amber/coral cinema light leak
            val leakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f,
                    0f,
                    width * 0.45f,
                    height,
                    intArrayOf(
                        android.graphics.Color.argb((alphaScale * 0.72f).toInt(), 245, 158, 11),
                        android.graphics.Color.argb((alphaScale * 0.48f).toInt(), 244, 63, 94),
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width * 0.48f, height, leakPaint)

            // Film sprocket holes on top and bottom edges
            val sprocketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb((alphaScale * 0.70f).toInt(), 254, 243, 199)
            }
            val holeW = width * 0.06f
            val holeH = height * 0.035f
            for (i in 0..6) {
                val left = width * 0.08f + i * (width * 0.13f)
                canvas.drawRoundRect(
                    RectF(left, height * 0.03f, left + holeW, height * 0.03f + holeH),
                    4f,
                    4f,
                    sprocketPaint
                )
                canvas.drawRoundRect(
                    RectF(left, height * 0.93f, left + holeW, height * 0.93f + holeH),
                    4f,
                    4f,
                    sprocketPaint
                )
            }
        }

        StudioCanvasEffectType.NOIR_SPOTLIGHT -> {
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    width * 0.5f,
                    height * 0.45f,
                    width * 0.62f,
                    intArrayOf(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.argb((alphaScale * 0.55f).toInt(), 15, 23, 42),
                        android.graphics.Color.argb((alphaScale * 0.88f).toInt(), 2, 6, 23)
                    ),
                    floatArrayOf(0.35f, 0.72f, 1.0f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width, height, vignettePaint)
        }

        StudioCanvasEffectType.VETERAN_TACTICAL_HUD -> {
            // Mil-spec olive border & tactical targeting crosshair
            val tacPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.008f).coerceAtLeast(3f)
                color = android.graphics.Color.argb((alphaScale * 0.85f).toInt(), 21, 128, 61)
            }
            val cx = width * 0.5f
            val cy = height * 0.48f
            val r = width * 0.22f
            canvas.drawCircle(cx, cy, r, tacPaint)
            // Crosshairs
            canvas.drawLine(cx - r * 1.35f, cy, cx - r * 0.45f, cy, tacPaint)
            canvas.drawLine(cx + r * 0.45f, cy, cx + r * 1.35f, cy, tacPaint)
            canvas.drawLine(cx, cy - r * 1.35f, cx, cy - r * 0.45f, tacPaint)
            canvas.drawLine(cx, cy + r * 0.45f, cx, cy + r * 1.35f, tacPaint)
            // Stencil corner marks
            val pad = width * 0.06f
            val len = width * 0.12f
            canvas.drawLine(pad, pad, pad + len, pad, tacPaint)
            canvas.drawLine(pad, pad, pad, pad + len, tacPaint)
            canvas.drawLine(width - pad, pad, width - pad - len, pad, tacPaint)
            canvas.drawLine(width - pad, pad, width - pad, pad + len, tacPaint)
            canvas.drawLine(pad, height - pad, pad + len, height - pad, tacPaint)
            canvas.drawLine(pad, height - pad, pad, height - pad - len, tacPaint)
            canvas.drawLine(width - pad, height - pad, width - pad - len, height - pad, tacPaint)
            canvas.drawLine(width - pad, height - pad, width - pad, height - pad - len, tacPaint)
        }

        StudioCanvasEffectType.BUSINESS_EXECUTIVE_GOLD -> {
            // Elegant gold corner laurels and subtle pinstripe header
            val goldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.007f).coerceAtLeast(2.5f)
                color = android.graphics.Color.argb((alphaScale * 0.82f).toInt(), 234, 179, 8)
            }
            val navyWash = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, 0f, height * 0.28f,
                    intArrayOf(
                        android.graphics.Color.argb((alphaScale * 0.40f).toInt(), 30, 58, 138),
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width, height * 0.28f, navyWash)
            // Golden diamond crest
            val crestPath = Path().apply {
                moveTo(width * 0.5f, height * 0.05f)
                lineTo(width * 0.58f, height * 0.11f)
                lineTo(width * 0.5f, height * 0.17f)
                lineTo(width * 0.42f, height * 0.11f)
                close()
            }
            canvas.drawPath(crestPath, goldPaint)
            // Refined luxury inset border
            val inset = width * 0.045f
            canvas.drawRect(inset, inset, width - inset, height - inset, goldPaint)
        }

        StudioCanvasEffectType.STUDENT_BLUEPRINT_GRID -> {
            val blueprintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.005f).coerceAtLeast(1.8f)
                color = android.graphics.Color.argb((alphaScale * 0.65f).toInt(), 124, 58, 237)
            }
            // Fine blueprint grid lines
            val step = width * 0.12f
            var gx = step
            while (gx < width) {
                canvas.drawLine(gx, 0f, gx, height, blueprintPaint)
                gx += step
            }
            var gy = step
            while (gy < height) {
                canvas.drawLine(0f, gy, width, gy, blueprintPaint)
                gy += step
            }
            // Constellation node circles
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb((alphaScale * 0.88f).toInt(), 196, 181, 253)
            }
            canvas.drawCircle(width * 0.24f, height * 0.24f, 6f, dotPaint)
            canvas.drawCircle(width * 0.76f, height * 0.24f, 6f, dotPaint)
            canvas.drawCircle(width * 0.5f, height * 0.82f, 7f, dotPaint)
        }

        StudioCanvasEffectType.TEEN_NEON_POP -> {
            val popPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.012f).coerceAtLeast(4f)
                color = android.graphics.Color.argb((alphaScale * 0.85f).toInt(), 249, 115, 22)
            }
            // Dynamic zigzag chromatic lightning
            val zig = Path().apply {
                moveTo(width * 0.06f, height * 0.15f)
                lineTo(width * 0.22f, height * 0.12f)
                lineTo(width * 0.16f, height * 0.28f)
                lineTo(width * 0.32f, height * 0.25f)
            }
            canvas.drawPath(zig, popPaint)
            // Neon starburst at bottom-right
            val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb((alphaScale * 0.80f).toInt(), 236, 72, 153)
            }
            canvas.drawCircle(width * 0.88f, height * 0.82f, width * 0.07f, starPaint)
        }

        StudioCanvasEffectType.KIDS_RAINBOW_SPARKLE -> {
            // Rainbow gradient arch across top
            val rainbowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (width * 0.022f).coerceAtLeast(8f)
                shader = LinearGradient(
                    0f, 0f, width, 0f,
                    intArrayOf(
                        android.graphics.Color.RED,
                        android.graphics.Color.YELLOW,
                        android.graphics.Color.GREEN,
                        android.graphics.Color.CYAN,
                        android.graphics.Color.MAGENTA
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawArc(
                RectF(-width * 0.1f, -height * 0.2f, width * 1.1f, height * 0.45f),
                0f, 180f, false, rainbowPaint
            )
            // Floating stars/bubbles
            val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = android.graphics.Color.argb((alphaScale * 0.85f).toInt(), 250, 204, 21)
            }
            canvas.drawCircle(width * 0.18f, height * 0.78f, width * 0.05f, starPaint)
            canvas.drawCircle(width * 0.82f, height * 0.74f, width * 0.06f, starPaint)
            canvas.drawCircle(width * 0.86f, height * 0.22f, width * 0.04f, starPaint)
        }

        StudioCanvasEffectType.NONE -> Unit
    }
}

/**
 * Real-time Compose DrawScope custom Canvas filter overlay drawn over the live CameraX PreviewView
 * and studio viewfinder. Uses DrawScope `size.width` and `size.height` dynamically.
 */
private fun DrawScope.drawLiveStudioCanvasFilterOverlay(
    effect: StudioCanvasEffectType,
    intensity: Float,
    animPhase: Float
) {
    if (effect == StudioCanvasEffectType.NONE || intensity <= 0.02f) return
    val w = size.width
    val h = size.height
    val alpha = intensity.coerceIn(0.1f, 1f)

    when (effect) {
        StudioCanvasEffectType.CYBER_HUD_GRID -> {
            val horizonY = h * 0.66f
            // Animated perspective floor grid
            for (i in -5..5) {
                val startX = w * 0.5f + (i * w * 0.055f)
                val endX = w * 0.5f + (i * w * 0.25f)
                drawLine(
                    color = HoloCyan.copy(alpha = 0.55f * alpha),
                    start = Offset(startX, horizonY),
                    end = Offset(endX, h),
                    strokeWidth = 2.dp.toPx()
                )
            }
            for (step in 0..4) {
                val frac = ((step / 5f) + (animPhase * 0.2f)) % 1f
                val y = horizonY + (h - horizonY) * frac
                drawLine(
                    color = NeonPink.copy(alpha = (0.25f + 0.45f * frac) * alpha),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.8.dp.toPx()
                )
            }
            // Animated laser scan bar across the camera surface
            val scanY = h * ((animPhase * 0.85f) % 1f)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, HoloCyan.copy(alpha = 0.85f * alpha), NeonPink.copy(alpha = 0.85f * alpha), Color.Transparent)
                ),
                start = Offset(0f, scanY),
                end = Offset(w, scanY),
                strokeWidth = 3.dp.toPx()
            )
            // Center targeting reticle
            val center = Offset(w * 0.5f, h * 0.44f)
            val reticleRadius = w * (0.16f + 0.015f * sin(animPhase * 6.28f))
            drawCircle(
                color = NeonPink.copy(alpha = 0.78f * alpha),
                radius = reticleRadius,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), animPhase * 40f)
                )
            )
            // Corner brackets
            val pad = w * 0.07f
            val len = w * 0.10f
            val stroke = 2.8.dp.toPx()
            val bracketColor = HoloCyan.copy(alpha = 0.9f * alpha)
            // TL
            drawLine(bracketColor, Offset(pad, pad), Offset(pad + len, pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + len), stroke, StrokeCap.Round)
            // TR
            drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad - len, pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + len), stroke, StrokeCap.Round)
            // BL
            drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + len, h - pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, h - pad), Offset(pad, h - pad - len), stroke, StrokeCap.Round)
            // BR
            drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad - len, h - pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - len), stroke, StrokeCap.Round)
        }

        StudioCanvasEffectType.SYNTHWAVE_SCANLINES -> {
            // Synthwave sunset orb glow
            val sunCenter = Offset(w * 0.5f, h * 0.30f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RosePulse.copy(alpha = 0.50f * alpha),
                        SolarAmber.copy(alpha = 0.32f * alpha),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = w * 0.38f
                ),
                radius = w * 0.38f,
                center = sunCenter
            )
            // Moving CRT scanlines
            val lineSpacing = (h / 32f).coerceAtLeast(6f)
            val offsetPhase = (animPhase * lineSpacing) % lineSpacing
            var y = offsetPhase
            while (y < h) {
                drawLine(
                    color = NeonPink.copy(alpha = 0.28f * alpha),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.5.dp.toPx()
                )
                y += lineSpacing
            }
            // Chromatic neon frame glow
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(RosePulse.copy(alpha = 0.7f * alpha), HoloCyan.copy(alpha = 0.7f * alpha))
                ),
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(w - 8.dp.toPx(), h - 8.dp.toPx()),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        StudioCanvasEffectType.HOLO_PARTICLES -> {
            val baseNodes = listOf(
                Triple(0.20f, 0.24f, 0.075f),
                Triple(0.80f, 0.22f, 0.085f),
                Triple(0.18f, 0.74f, 0.070f),
                Triple(0.78f, 0.72f, 0.065f),
                Triple(0.50f, 0.18f, 0.055f),
                Triple(0.32f, 0.52f, 0.048f),
                Triple(0.68f, 0.48f, 0.055f)
            )
            val animatedOffsets = baseNodes.mapIndexed { idx, (bx, by, br) ->
                val angle = (animPhase * 6.283f) + (idx * 1.1f)
                val ox = (bx + 0.03f * cos(angle)) * w
                val oy = (by + 0.03f * sin(angle)) * h
                Pair(Offset(ox, oy), br * w)
            }
            for (i in 0 until animatedOffsets.size - 1) {
                drawLine(
                    color = HoloCyan.copy(alpha = 0.40f * alpha),
                    start = animatedOffsets[i].first,
                    end = animatedOffsets[i + 1].first,
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            }
            animatedOffsets.forEachIndexed { idx, (center, radius) ->
                val orbColor = if (idx % 2 == 0) HoloCyan else NeonPink
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f * alpha),
                            orbColor.copy(alpha = 0.58f * alpha),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
        }

        StudioCanvasEffectType.PRISM_RAYS -> {
            val shift = (animPhase - 0.5f) * w * 0.12f
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HoloCyan.copy(alpha = 0.50f * alpha),
                        NeonPink.copy(alpha = 0.45f * alpha),
                        CyberEmerald.copy(alpha = 0.50f * alpha)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                start = Offset(w * 0.15f + shift, 0f),
                end = Offset(w * 0.85f - shift, h),
                strokeWidth = (w * 0.18f).coerceAtLeast(24f)
            )
            // Diamond prism outline
            val diamondPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.07f)
                lineTo(w * 0.93f, h * 0.5f)
                lineTo(w * 0.5f, h * 0.93f)
                lineTo(w * 0.07f, h * 0.5f)
                close()
            }
            drawPath(
                path = diamondPath,
                brush = Brush.linearGradient(listOf(HoloCyan, NeonPink, CyberEmerald)),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        StudioCanvasEffectType.FILM_LIGHT_LEAK -> {
            val pulse = 0.85f + 0.15f * sin(animPhase * 6.283f)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SolarAmber.copy(alpha = 0.62f * alpha * pulse),
                        RosePulse.copy(alpha = 0.36f * alpha * pulse),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = w * 0.52f
                ),
                size = Size(w * 0.55f, h)
            )
            // Top & bottom cinema sprocket markers
            val holeW = w * 0.06f
            val holeH = h * 0.035f
            for (i in 0..6) {
                val x = w * 0.07f + i * (w * 0.13f)
                drawRoundRect(
                    color = Color(0xFFFEF3C7).copy(alpha = 0.72f * alpha),
                    topLeft = Offset(x, h * 0.03f),
                    size = Size(holeW, holeH),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    color = Color(0xFFFEF3C7).copy(alpha = 0.72f * alpha),
                    topLeft = Offset(x, h * 0.93f),
                    size = Size(holeW, holeH),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        StudioCanvasEffectType.NOIR_SPOTLIGHT -> {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.55f * alpha),
                        Color.Black.copy(alpha = 0.88f * alpha)
                    ),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.62f
                ),
                size = Size(w, h)
            )
        }

        StudioCanvasEffectType.VETERAN_TACTICAL_HUD -> {
            val cx = w * 0.5f
            val cy = h * 0.48f
            val r = w * 0.22f
            val pulse = 1f + 0.03f * sin(animPhase * 6.283f)
            val strokeW = 2.5.dp.toPx()
            val tacColor = Color(0xFF15803D).copy(alpha = 0.85f * alpha)
            drawCircle(color = tacColor, radius = r * pulse, center = Offset(cx, cy), style = Stroke(width = strokeW))
            drawLine(tacColor, Offset(cx - r * 1.35f, cy), Offset(cx - r * 0.45f, cy), strokeW)
            drawLine(tacColor, Offset(cx + r * 0.45f, cy), Offset(cx + r * 1.35f, cy), strokeW)
            drawLine(tacColor, Offset(cx, cy - r * 1.35f), Offset(cx, cy - r * 0.45f), strokeW)
            drawLine(tacColor, Offset(cx, cy + r * 0.45f), Offset(cx, cy + r * 1.35f), strokeW)
            val pad = w * 0.06f
            val len = w * 0.12f
            drawLine(tacColor, Offset(pad, pad), Offset(pad + len, pad), strokeW)
            drawLine(tacColor, Offset(pad, pad), Offset(pad, pad + len), strokeW)
            drawLine(tacColor, Offset(w - pad, pad), Offset(w - pad - len, pad), strokeW)
            drawLine(tacColor, Offset(w - pad, pad), Offset(w - pad, pad + len), strokeW)
            drawLine(tacColor, Offset(pad, h - pad), Offset(pad + len, h - pad), strokeW)
            drawLine(tacColor, Offset(pad, h - pad), Offset(pad, h - pad - len), strokeW)
            drawLine(tacColor, Offset(w - pad, h - pad), Offset(w - pad - len, h - pad), strokeW)
            drawLine(tacColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - len), strokeW)
        }

        StudioCanvasEffectType.BUSINESS_EXECUTIVE_GOLD -> {
            val goldColor = Color(0xFFEAB308).copy(alpha = 0.82f * alpha)
            val inset = w * 0.045f
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1E3A8A).copy(alpha = 0.40f * alpha), Color.Transparent),
                    startY = 0f,
                    endY = h * 0.32f
                ),
                size = Size(w, h * 0.32f)
            )
            drawRect(
                color = goldColor,
                topLeft = Offset(inset, inset),
                size = Size(w - inset * 2f, h - inset * 2f),
                style = Stroke(width = 2.dp.toPx())
            )
            val diamond = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.05f)
                lineTo(w * 0.58f, h * 0.11f)
                lineTo(w * 0.5f, h * 0.17f)
                lineTo(w * 0.42f, h * 0.11f)
                close()
            }
            drawPath(diamond, color = goldColor, style = Stroke(width = 2.dp.toPx()))
        }

        StudioCanvasEffectType.STUDENT_BLUEPRINT_GRID -> {
            val gridColor = Color(0xFF7C3AED).copy(alpha = 0.50f * alpha)
            val step = w * 0.12f
            var gx = step
            while (gx < w) {
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1.dp.toPx())
                gx += step
            }
            var gy = step
            while (gy < h) {
                drawLine(gridColor, Offset(0f, gy), Offset(w, gy), strokeWidth = 1.dp.toPx())
                gy += step
            }
            drawCircle(Color(0xFFC4B5FD).copy(alpha = 0.85f * alpha), radius = 5.dp.toPx(), center = Offset(w * 0.24f, h * 0.24f))
            drawCircle(Color(0xFFC4B5FD).copy(alpha = 0.85f * alpha), radius = 5.dp.toPx(), center = Offset(w * 0.76f, h * 0.24f))
            drawCircle(Color(0xFFC4B5FD).copy(alpha = 0.85f * alpha), radius = 6.dp.toPx(), center = Offset(w * 0.5f, h * 0.82f))
        }

        StudioCanvasEffectType.TEEN_NEON_POP -> {
            val popColor = Color(0xFFF97316).copy(alpha = 0.85f * alpha)
            val zig = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.06f, h * 0.15f)
                lineTo(w * 0.22f, h * 0.12f)
                lineTo(w * 0.16f, h * 0.28f)
                lineTo(w * 0.32f, h * 0.25f)
            }
            drawPath(zig, color = popColor, style = Stroke(width = 3.dp.toPx()))
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFEC4899).copy(alpha = 0.80f * alpha), Color.Transparent)),
                radius = w * 0.12f,
                center = Offset(w * 0.86f, h * 0.82f)
            )
        }

        StudioCanvasEffectType.KIDS_RAINBOW_SPARKLE -> {
            drawArc(
                brush = Brush.horizontalGradient(
                    listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Magenta)
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(-w * 0.1f, -h * 0.2f),
                size = Size(w * 1.2f, h * 0.5f),
                style = Stroke(width = 6.dp.toPx())
            )
            val starColor = Color(0xFFFACC15).copy(alpha = 0.85f * alpha)
            drawCircle(starColor, radius = w * 0.05f, center = Offset(w * 0.18f, h * 0.78f))
            drawCircle(starColor, radius = w * 0.06f, center = Offset(w * 0.82f, h * 0.74f))
            drawCircle(starColor, radius = w * 0.04f, center = Offset(w * 0.86f, h * 0.22f))
        }

        StudioCanvasEffectType.NONE -> Unit
    }
}

/**
 * Generates a vibrant studio portrait bitmap when testing in an emulator without a webcam
 * or when cycling studio portraits.
 */
private fun createSimulatedCameraPortrait(
    context: android.content.Context,
    portraitIndex: Int,
    displayName: String
): Bitmap {
    val presetDrawables = listOf(
        R.drawable.img_avatar_cyber_astronaut,
        R.drawable.img_avatar_neon_musician,
        R.drawable.img_avatar_veteran_guardian,
        R.drawable.img_avatar_stem_scholar
    )
    val resId = presetDrawables[portraitIndex % presetDrawables.size]
    val decoded = BitmapFactory.decodeResource(context.resources, resId)
    if (decoded != null) {
        return decoded
    }
    val bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            200f,
            180f,
            240f,
            intArrayOf(
                android.graphics.Color.parseColor("#6366F1"),
                android.graphics.Color.parseColor("#06B6D4"),
                android.graphics.Color.parseColor("#0F172A")
            ),
            null,
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, 400f, 400f, bgPaint)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 96f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    canvas.drawText(displayName.take(2).uppercase(), 200f, 230f, textPaint)
    return bmp
}

/**
 * Real-Time CameraX Preview Surface composable with live front/back lens switching and fallback
 * animated studio feed when running in an environment without camera hardware.
 */
@Composable
private fun RealTimeCameraPreviewSurface(
    isCameraPermissionGranted: Boolean,
    lensFacing: Int,
    fallbackBitmap: Bitmap?,
    selectedFilter: PhotoStudioFilterPreset,
    brightnessOffset: Float,
    selectedCanvasEffect: StudioCanvasEffectType,
    canvasEffectIntensity: Float,
    addNeonFrame: Boolean,
    onPreviewViewReady: (PreviewView) -> Unit,
    onImageCaptureReady: (ImageCapture?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraBoundSuccessfully by remember { mutableStateOf(false) }

    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(previewView) {
        onPreviewViewReady(previewView)
    }

    DisposableEffect(isCameraPermissionGranted, lensFacing, lifecycleOwner) {
        var cameraProvider: ProcessCameraProvider? = null
        if (isCameraPermissionGranted) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val executor = ContextCompat.getMainExecutor(context)
            cameraProviderFuture.addListener({
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                try {
                    val provider = cameraProviderFuture.get()
                    cameraProvider = provider
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                    onImageCaptureReady(imageCapture)
                    cameraBoundSuccessfully = true
                } catch (_: Exception) {
                    // Fallback to default available camera if requested lens isn't present
                    try {
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                        onImageCaptureReady(imageCapture)
                        cameraBoundSuccessfully = true
                    } catch (_: Exception) {
                        onImageCaptureReady(null)
                        cameraBoundSuccessfully = false
                    }
                }
            }, executor)
        } else {
            onImageCaptureReady(null)
            cameraBoundSuccessfully = false
        }

        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "studio_canvas_filter_anim")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "canvas_effect_phase"
    )

    // Compose ColorFilter matching the active ColorMatrix + brightness slider
    val composeColorFilter = remember(selectedFilter, brightnessOffset) {
        val values = selectedFilter.matrixValues.copyOf()
        values[4] += brightnessOffset
        values[9] += brightnessOffset
        values[14] += brightnessOffset
        ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(values))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background( Color(0xFF090D16))
            .border(
                width = if (addNeonFrame) 2.5.dp else 1.5.dp,
                brush = if (addNeonFrame) {
                    Brush.linearGradient(listOf(HoloCyan, NeonPink, CyberEmerald))
                } else {
                    Brush.linearGradient(listOf(selectedFilter.accent, selectedFilter.accent))
                },
                shape = RoundedCornerShape(22.dp)
            )
    ) {
        // Base animated or fallback portrait layer (visible before permission or on headless emulators)
        if (fallbackBitmap != null) {
            Image(
                bitmap = fallbackBitmap.asImageBitmap(),
                contentDescription = "Studio Base Feed",
                contentScale = ContentScale.Crop,
                colorFilter = composeColorFilter,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Hardware CameraX Real-Time PreviewView Surface when permission is granted
        if (isCameraPermissionGranted) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("studio_realtime_camerax_surface")
            )
        }

        // Real-Time Custom Compose Canvas Filter Effect Overlay on top of the camera surface
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("studio_custom_canvas_filter_overlay")
        ) {
            // Subtle tint wash matching the active ColorMatrix accent on live hardware camera feed
            if (isCameraPermissionGranted && cameraBoundSuccessfully && selectedFilter.id != "original") {
                drawRect(
                    color = selectedFilter.accent.copy(alpha = 0.14f),
                    size = size
                )
            }
            drawLiveStudioCanvasFilterOverlay(
                effect = selectedCanvasEffect,
                intensity = canvasEffectIntensity,
                animPhase = animPhase
            )
        }

        // Top-left Live Surface status pill
        Surface(
            color = Color.Black.copy(alpha = 0.72f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, if (isCameraPermissionGranted) CyberEmerald else SolarAmber),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isCameraPermissionGranted) CyberEmerald else SolarAmber)
                )
                Text(
                    text = if (isCameraPermissionGranted) "LIVE CAM + CANVAS FX" else "SIMULATED LIVE FX",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Bottom Filter + Canvas Effect badge
        Surface(
            color = Color.Black.copy(alpha = 0.72f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(6.dp)
        ) {
            Text(
                text = "${selectedFilter.name} • ${selectedCanvasEffect.title}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun CreativePhotoStudioCard(
    displayName: String,
    currentProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int?,
    userPersona: String = "Adult",
    onApplyFilteredBitmapToProfile: (Bitmap) -> Unit,
    onLogStudioInteraction: (String) -> Unit = {},
    onPublishPhotoToSocialPost: (title: String, caption: String, category: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Determine initial persona-tailored filter preset
    val initialPersonaFilter = remember(userPersona) {
        val lower = userPersona.lowercase()
        when {
            lower.contains("veteran") -> PHOTO_STUDIO_FILTERS.firstOrNull { it.id == "veteran_olive_hdr" } ?: PHOTO_STUDIO_FILTERS.first()
            lower.contains("business") -> PHOTO_STUDIO_FILTERS.firstOrNull { it.id == "business_executive_slate" } ?: PHOTO_STUDIO_FILTERS.first()
            lower.contains("student") -> PHOTO_STUDIO_FILTERS.firstOrNull { it.id == "student_academe_indigo" } ?: PHOTO_STUDIO_FILTERS.first()
            lower.contains("teen") -> PHOTO_STUDIO_FILTERS.firstOrNull { it.id == "teen_hyper_arcade" } ?: PHOTO_STUDIO_FILTERS.first()
            lower.contains("child") || lower.contains("kid") -> PHOTO_STUDIO_FILTERS.firstOrNull { it.id == "kids_vibrant_cartoon" } ?: PHOTO_STUDIO_FILTERS.first()
            else -> PHOTO_STUDIO_FILTERS.first()
        }
    }

    // Initial base bitmap in the studio (either user's current photo or default 3D avatar resource)
    var studioSourceBitmap by remember(currentProfileBitmap, selected3DAvatarResId) {
        val initial = currentProfileBitmap ?: run {
            val resId = selected3DAvatarResId ?: R.drawable.img_avatar_cyber_astronaut
            BitmapFactory.decodeResource(context.resources, resId)
        }
        mutableStateOf(initial)
    }

    var selectedFilter by remember(initialPersonaFilter) { mutableStateOf(initialPersonaFilter) }
    var selectedCanvasEffect by remember(initialPersonaFilter) { mutableStateOf(initialPersonaFilter.defaultCanvasEffect) }
    var canvasEffectIntensity by remember { mutableFloatStateOf(0.85f) }
    var brightnessOffset by remember { mutableFloatStateOf(0f) }
    var addNeonFrame by remember { mutableStateOf(true) }
    var simulatedShotCounter by remember { mutableIntStateOf(0) }
    var studioStatusMessage by remember { mutableStateOf<String?>(null) }

    // Social post composer state inside the Creative Photo Studio
    var socialPostTitle by remember(displayName) {
        mutableStateOf("${displayName.substringBefore(" ").ifBlank { "My" }}'s 3D Camera Studio Snapshot")
    }
    var socialPostCaption by remember {
        mutableStateOf("Captured a new 3D photo in the Creative Photo Studio with live camera filters! #WereEqual #3DStudio")
    }
    var socialPostCategory by remember { mutableStateOf("Studio Art") }
    val capturedStudioRoll = remember { mutableStateListOf<Bitmap>() }

    // Real-time CameraX surface + ImageCapture state
    var isLiveCameraSurfaceEnabled by remember { mutableStateOf(true) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var activePreviewView by remember { mutableStateOf<PreviewView?>(null) }
    var activeImageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Live filtered preview bitmap (including both ColorMatrix and Custom Canvas Effect)
    val previewFilteredBitmap = remember(
        studioSourceBitmap,
        selectedFilter,
        brightnessOffset,
        addNeonFrame,
        selectedCanvasEffect,
        canvasEffectIntensity
    ) {
        studioSourceBitmap?.let { src ->
            applyStudioFilterToBitmap(
                source = src,
                filter = selectedFilter,
                brightnessOffset = brightnessOffset,
                addNeonBorder = addNeonFrame,
                canvasEffect = selectedCanvasEffect,
                effectIntensity = canvasEffectIntensity
            )
        }
    }

    fun processAndStoreCapturedBitmap(rawBitmap: Bitmap, sourceLabel: String) {
        studioSourceBitmap = rawBitmap
        val filtered = applyStudioFilterToBitmap(
            source = rawBitmap,
            filter = selectedFilter,
            brightnessOffset = brightnessOffset,
            addNeonBorder = addNeonFrame,
            canvasEffect = selectedCanvasEffect,
            effectIntensity = canvasEffectIntensity
        )
        if (capturedStudioRoll.size >= 6) {
            capturedStudioRoll.removeAt(capturedStudioRoll.lastIndex)
        }
        capturedStudioRoll.add(0, filtered)
        onApplyFilteredBitmapToProfile(filtered)
        onLogStudioInteraction("$sourceLabel with '${selectedFilter.name}' + '${selectedCanvasEffect.title}' canvas effect")
        studioStatusMessage = "$sourceLabel! Applied '${selectedFilter.name}' + '${selectedCanvasEffect.title}' to Profile & ready for Social Post."
    }

    // 1. Device Camera Launcher (TakePicturePreview fallback / full-res snap)
    val cameraTakePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap: Bitmap? ->
        val rawBitmap = capturedBitmap
            ?: activePreviewView?.bitmap
            ?: createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
        processAndStoreCapturedBitmap(rawBitmap, "Captured camera photo")
    }

    // 2. Runtime CAMERA Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (isGranted) {
            isLiveCameraSurfaceEnabled = true
            studioStatusMessage = "Camera API active! Snap photos for your 3D profile theme or social posts."
        } else {
            val fallback = createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
            processAndStoreCapturedBitmap(fallback, "Simulated Studio Camera Shot")
            studioStatusMessage = "Using Simulated Live Surface — Camera FX & Social Post publisher remain active!"
        }
    }

    // 3. Zero-Permission Photo Picker Launcher
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                    if (decoded != null) {
                        processAndStoreCapturedBitmap(decoded, "Imported gallery photo")
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun captureFromLiveSurfaceOrCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        hasCameraPermission = granted

        if (granted) {
            val imageCapture = activeImageCapture
            val liveBitmap = activePreviewView?.bitmap
            if (isLiveCameraSurfaceEnabled && liveBitmap != null) {
                processAndStoreCapturedBitmap(liveBitmap, "Captured live CameraX frame")
            } else if (imageCapture != null) {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bmp = try {
                                image.toBitmap()
                            } catch (_: Exception) {
                                null
                            } finally {
                                image.close()
                            }
                            val resolved = bmp
                                ?: activePreviewView?.bitmap
                                ?: createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
                            processAndStoreCapturedBitmap(resolved, "Captured CameraX hardware photo")
                        }

                        override fun onError(exception: ImageCaptureException) {
                            try {
                                cameraTakePictureLauncher.launch(null)
                            } catch (_: Exception) {
                                val fallback = createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
                                processAndStoreCapturedBitmap(fallback, "Captured studio portrait")
                            }
                        }
                    }
                )
            } else {
                try {
                    cameraTakePictureLauncher.launch(null)
                } catch (_: Exception) {
                    val fallback = createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
                    processAndStoreCapturedBitmap(fallback, "Captured studio portrait")
                }
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, NeonPink, CyberEmerald))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("creative_photo_studio_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Studio Header
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
                    Surface(
                        color = NeonPink.copy(alpha = 0.18f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, NeonPink)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Creative Photo Studio",
                            tint = NeonPink,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "📸 Live Camera & Canvas FX Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Real-time camera preview surface with custom procedural Canvas filter effects.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                SixDimensionalHdBadge(label = selectedFilter.badge)
            }

            // Live Camera Surface Mode Bar (Toggle Live Preview Surface, Switch Front/Back Lens, Enable Camera)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = isLiveCameraSurfaceEnabled,
                        onClick = { isLiveCameraSurfaceEnabled = !isLiveCameraSurfaceEnabled },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isLiveCameraSurfaceEnabled) "Live Camera Surface: ON" else "Static Viewfinder",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.testTag("studio_toggle_live_surface_chip")
                    )

                    if (!hasCameraPermission) {
                        FilterChip(
                            selected = false,
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            label = {
                                Text(
                                    text = "Grant Camera",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.testTag("studio_grant_camera_permission_chip")
                        )
                    }
                }

                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                        studioStatusMessage = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            "Switched to Front Selfie Camera"
                        } else {
                            "Switched to Rear World Camera"
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("studio_switch_camera_lens_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Front/Back Camera Lens",
                        tint = HoloCyan
                    )
                }
            }

            // Live Camera & Filter Viewfinder Preview Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLiveCameraSurfaceEnabled) {
                    RealTimeCameraPreviewSurface(
                        isCameraPermissionGranted = hasCameraPermission,
                        lensFacing = lensFacing,
                        fallbackBitmap = studioSourceBitmap,
                        selectedFilter = selectedFilter,
                        brightnessOffset = brightnessOffset,
                        selectedCanvasEffect = selectedCanvasEffect,
                        canvasEffectIntensity = canvasEffectIntensity,
                        addNeonFrame = addNeonFrame,
                        onPreviewViewReady = { activePreviewView = it },
                        onImageCaptureReady = { activeImageCapture = it },
                        modifier = Modifier
                            .size(164.dp)
                            .testTag("photo_studio_viewfinder")
                    )
                } else {
                    // Static Filtered Portrait Viewfinder
                    Box(
                        modifier = Modifier
                            .size(164.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, selectedFilter.accent, RoundedCornerShape(22.dp))
                            .testTag("photo_studio_viewfinder"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewFilteredBitmap != null) {
                            Image(
                                bitmap = previewFilteredBitmap.asImageBitmap(),
                                contentDescription = "Filtered Profile Photo Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = selectedFilter.accent,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.68f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "${selectedFilter.name} • ${selectedCanvasEffect.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 3 Action Buttons (Capture Live Frame / Camera, Instant Studio Shot, Gallery)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { captureFromLiveSurfaceOrCamera() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPink,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_open_device_camera_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("1. Snap Live Frame", fontWeight = FontWeight.ExtraBold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = {
                            val shot = createSimulatedCameraPortrait(context, simulatedShotCounter++, displayName)
                            studioSourceBitmap = shot
                            val filtered = applyStudioFilterToBitmap(
                                source = shot,
                                filter = selectedFilter,
                                brightnessOffset = brightnessOffset,
                                addNeonBorder = addNeonFrame,
                                canvasEffect = selectedCanvasEffect,
                                effectIntensity = canvasEffectIntensity
                            )
                            onApplyFilteredBitmapToProfile(filtered)
                            onLogStudioInteraction("Captured Instant 3D Studio Shot with '${selectedFilter.name}' & '${selectedCanvasEffect.title}'")
                            studioStatusMessage = "Instant Studio Shot captured & applied!"
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_instant_portrait_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Instant Studio Shot", color = HoloCyan, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_pick_gallery_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick from Gallery", color = CyberEmerald, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }

            // Step 2A: Custom Real-Time Canvas Filter Effects Strip
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            imageVector = Icons.Default.BlurOn,
                            contentDescription = null,
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "2. Live Custom Canvas Filter Overlay:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "FX ${(canvasEffectIntensity * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioCanvasEffectType.entries.forEach { effect ->
                        val isSelected = selectedCanvasEffect == effect
                        Surface(
                            color = if (isSelected) effect.accent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) effect.accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clickable {
                                    selectedCanvasEffect = effect
                                    studioSourceBitmap?.let { src ->
                                        val updated = applyStudioFilterToBitmap(
                                            source = src,
                                            filter = selectedFilter,
                                            brightnessOffset = brightnessOffset,
                                            addNeonBorder = addNeonFrame,
                                            canvasEffect = effect,
                                            effectIntensity = canvasEffectIntensity
                                        )
                                        onApplyFilteredBitmapToProfile(updated)
                                    }
                                    studioStatusMessage = "Active Canvas FX: ${effect.title} (${effect.subtitle})"
                                }
                                .testTag("canvas_effect_${effect.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(effect.accent)
                                    )
                                    Text(
                                        text = effect.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSelected) effect.accent else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = effect.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Canvas Effect Intensity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Canvas FX:",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = canvasEffectIntensity,
                        onValueChange = { canvasEffectIntensity = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPink,
                            activeTrackColor = NeonPink
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .testTag("studio_canvas_effect_intensity_slider")
                    )
                }
            }

            // Step 2B: 8 One-Tap ColorMatrix Photo Filters Strip
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            imageVector = Icons.Default.FilterVintage,
                            contentDescription = null,
                            tint = HoloCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "3. Color Grade Filter (8 Studio Styles):",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilterChip(
                        selected = addNeonFrame,
                        onClick = { addNeonFrame = !addNeonFrame },
                        label = { Text("3D Neon Frame", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("studio_toggle_neon_frame")
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PHOTO_STUDIO_FILTERS.forEach { filter ->
                        val isSelected = selectedFilter.id == filter.id
                        Surface(
                            color = if (isSelected) filter.accent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) filter.accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clickable {
                                    selectedFilter = filter
                                    selectedCanvasEffect = filter.defaultCanvasEffect
                                    previewFilteredBitmap?.let { src ->
                                        val updated = applyStudioFilterToBitmap(
                                            source = studioSourceBitmap ?: src,
                                            filter = filter,
                                            brightnessOffset = brightnessOffset,
                                            addNeonBorder = addNeonFrame,
                                            canvasEffect = filter.defaultCanvasEffect,
                                            effectIntensity = canvasEffectIntensity
                                        )
                                        onApplyFilteredBitmapToProfile(updated)
                                    }
                                    studioStatusMessage = "Applied '${filter.name}' + '${filter.defaultCanvasEffect.title}' FX!"
                                }
                                .testTag("photo_filter_${filter.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(filter.accent)
                                )
                                Text(
                                    text = filter.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) filter.accent else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Brightness / Glow Slider + Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Glow: ${brightnessOffset.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoloCyan,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = brightnessOffset,
                    onValueChange = { brightnessOffset = it },
                    valueRange = -35f..45f,
                    colors = SliderDefaults.colors(
                        thumbColor = HoloCyan,
                        activeTrackColor = HoloCyan
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .testTag("studio_brightness_slider")
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable {
                            selectedFilter = PHOTO_STUDIO_FILTERS.first()
                            selectedCanvasEffect = StudioCanvasEffectType.CYBER_HUD_GRID
                            canvasEffectIntensity = 0.85f
                            brightnessOffset = 0f
                            studioStatusMessage = "Reset filter & Canvas FX to default."
                        }
                        .testTag("studio_reset_filter_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Filter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Captured Studio Camera Roll (Recent Snaps)
            if (capturedStudioRoll.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Recent Studio Camera Captures (${capturedStudioRoll.size}) — Tap to Select:",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        capturedStudioRoll.forEachIndexed { idx, rollBmp ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, HoloCyan, RoundedCornerShape(12.dp))
                                    .clickable {
                                        studioSourceBitmap = rollBmp
                                        onApplyFilteredBitmapToProfile(rollBmp)
                                        studioStatusMessage = "Selected Camera Capture #${idx + 1} for Profile Theme & Social Post!"
                                    }
                                    .testTag("studio_roll_item_$idx")
                            ) {
                                Image(
                                    bitmap = rollBmp.asImageBitmap(),
                                    contentDescription = "Captured Studio Photo ${idx + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Step 4: Use Camera Photo for 3D Profile Theme & Social Posts
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, HoloCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_social_post_and_profile_publisher")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "4. Use Studio Photo for Profile Theme & Social Posts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, CyberEmerald)
                        ) {
                            Text(
                                text = "CAMERA API READY",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = socialPostTitle,
                        onValueChange = { socialPostTitle = it },
                        label = { Text("Social Post Headline") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_social_post_title_input")
                    )

                    OutlinedTextField(
                        value = socialPostCaption,
                        onValueChange = { socialPostCaption = it },
                        label = { Text("Social Post Caption & Filter Notes") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_social_post_caption_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Post Type:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        listOf("Studio Art", "Update", "Milestone").forEach { cat ->
                            FilterChip(
                                selected = socialPostCategory == cat,
                                onClick = { socialPostCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("studio_post_category_${cat.lowercase().replace(" ", "_")}")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val liveSurfaceBmp = activePreviewView?.bitmap
                                val baseBmp = if (isLiveCameraSurfaceEnabled && liveSurfaceBmp != null) {
                                    studioSourceBitmap = liveSurfaceBmp
                                    liveSurfaceBmp
                                } else {
                                    studioSourceBitmap
                                }
                                if (baseBmp != null) {
                                    val finalBmp = applyStudioFilterToBitmap(
                                        source = baseBmp,
                                        filter = selectedFilter,
                                        brightnessOffset = brightnessOffset,
                                        addNeonBorder = addNeonFrame,
                                        canvasEffect = selectedCanvasEffect,
                                        effectIntensity = canvasEffectIntensity
                                    )
                                    onApplyFilteredBitmapToProfile(finalBmp)
                                    onLogStudioInteraction("Saved '${selectedFilter.name}' + '${selectedCanvasEffect.title}' photo to Profile Theme")
                                    studioStatusMessage = "Applied '${selectedFilter.name}' + '${selectedCanvasEffect.title}' to your 3D Profile Theme!"
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("studio_apply_to_profile_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Set Profile Theme Photo",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                val liveSurfaceBmp = activePreviewView?.bitmap
                                val baseBmp = if (isLiveCameraSurfaceEnabled && liveSurfaceBmp != null) {
                                    studioSourceBitmap = liveSurfaceBmp
                                    liveSurfaceBmp
                                } else {
                                    studioSourceBitmap
                                }
                                if (baseBmp != null) {
                                    val finalBmp = applyStudioFilterToBitmap(
                                        source = baseBmp,
                                        filter = selectedFilter,
                                        brightnessOffset = brightnessOffset,
                                        addNeonBorder = addNeonFrame,
                                        canvasEffect = selectedCanvasEffect,
                                        effectIntensity = canvasEffectIntensity
                                    )
                                    onApplyFilteredBitmapToProfile(finalBmp)
                                }
                                val cleanTitle = socialPostTitle.trim().ifBlank {
                                    "📸 New 3D Studio Photo (${selectedFilter.name})"
                                }
                                val cleanCaption = buildString {
                                    append(socialPostCaption.trim().ifBlank { "Captured in the Creative Photo Studio!" })
                                    append(" • Filter: ${selectedFilter.name} • Canvas FX: ${selectedCanvasEffect.title}")
                                }
                                onPublishPhotoToSocialPost(cleanTitle, cleanCaption, socialPostCategory)
                                onLogStudioInteraction("Published studio photo social post: '$cleanTitle'")
                                studioStatusMessage = "Published '$cleanTitle' to the Community Social Feed & updated Profile Theme!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("studio_publish_social_post_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Post to Social Feed",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (!studioStatusMessage.isNullOrBlank()) {
                Text(
                    text = "✓ $studioStatusMessage",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
