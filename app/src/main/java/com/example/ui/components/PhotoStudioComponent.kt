package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PHOTO_STUDIO_LOG_TAG = "PhotoStudioComponent"
private const val PHOTO_STUDIO_MODEL_NAME = "gemini-nano-banana-2.1"

enum class StudioCreativeFilter(
    val id: String,
    val label: String,
    val description: String,
    val accent: Color,
    val targetMatrix: FloatArray
) {
    ORIGINAL(
        id = "original",
        label = "Original",
        description = "Natural tones",
        accent = HoloCyan,
        targetMatrix = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    CYBER_NEON(
        id = "cyber_neon",
        label = "Cyber Neon",
        description = "Electric magenta & cyan",
        accent = NeonPink,
        targetMatrix = floatArrayOf(
            1.24f, 0f, 0.16f, 0f, 10f,
            0f, 0.94f, 0.14f, 0f, -4f,
            0.16f, 0f, 1.34f, 0f, 22f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    GOLDEN_SUNSET(
        id = "golden_sunset",
        label = "Golden Hour",
        description = "Warm solar glow",
        accent = SolarAmber,
        targetMatrix = floatArrayOf(
            1.22f, 0.08f, 0f, 0f, 18f,
            0.04f, 1.08f, 0f, 0f, 8f,
            0f, 0f, 0.80f, 0f, -12f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    EMERALD_PRISM(
        id = "emerald_prism",
        label = "Emerald Prism",
        description = "Crisp mint clarity",
        accent = CyberEmerald,
        targetMatrix = floatArrayOf(
            0.86f, 0.08f, 0f, 0f, -2f,
            0.08f, 1.28f, 0.06f, 0f, 14f,
            0f, 0.08f, 0.98f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    NOIR_MONO(
        id = "noir_mono",
        label = "Noir Contrast",
        description = "Dramatic monochrome",
        accent = Color(0xFFE2E8F0),
        targetMatrix = floatArrayOf(
            0.36f, 0.56f, 0.16f, 0f, 6f,
            0.36f, 0.56f, 0.16f, 0f, 6f,
            0.36f, 0.56f, 0.16f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    RETRO_POLAROID(
        id = "retro_polaroid",
        label = "Retro Film",
        description = "Warm vintage sepia",
        accent = Color(0xFFF59E0B),
        targetMatrix = floatArrayOf(
            0.94f, 0.34f, 0.08f, 0f, 10f,
            0.16f, 0.88f, 0.08f, 0f, 4f,
            0.10f, 0.22f, 0.66f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    HOLO_FROST(
        id = "holo_frost",
        label = "Holo Frost",
        description = "Cool iridescent studio",
        accent = Color(0xFF38BDF8),
        targetMatrix = floatArrayOf(
            0.84f, 0.04f, 0.14f, 0f, 0f,
            0.06f, 1.16f, 0.16f, 0f, 10f,
            0.12f, 0.16f, 1.32f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

data class AiOverlayStylePreset(
    val id: String,
    val label: String,
    val promptInstruction: String,
    val accent: Color
)

private val AI_OVERLAY_PRESETS = listOf(
    AiOverlayStylePreset(
        id = "cosmic_aura",
        label = "Cosmic Hologram Aura",
        promptInstruction = "Overlay luminous holographic cyan and violet light rings, delicate floating stardust particles, and an ethereal studio rim-glow around the subject while preserving the original subject and photo composition.",
        accent = HoloCyan
    ),
    AiOverlayStylePreset(
        id = "neon_cyber_hud",
        label = "Neon Cyber HUD",
        promptInstruction = "Add sleek translucent futuristic neon magenta and cyan corner HUD brackets, subtle glowing light-streak accents, and soft synthwave studio atmosphere without obscuring the subject's face or focal area.",
        accent = NeonPink
    ),
    AiOverlayStylePreset(
        id = "golden_bokeh",
        label = "Golden Sunflare & Bokeh",
        promptInstruction = "Apply warm golden-hour sunflare light rays, creamy floating amber bokeh orbs, and subtle cinematic film glow over the photo while keeping natural details sharp.",
        accent = SolarAmber
    ),
    AiOverlayStylePreset(
        id = "prism_ribbons",
        label = "3D Prism Ribbons",
        promptInstruction = "Overlay delicate 3D translucent iridescent glass prism ribbons and sparkling emerald-cyan light refractions framing the subject cleanly.",
        accent = CyberEmerald
    ),
    AiOverlayStylePreset(
        id = "studio_spotlight",
        label = "Studio Spotlight",
        promptInstruction = "Add a dramatic editorial volumetric spotlight beam from above with soft floating dust motes and refined contrast framing around the edges.",
        accent = Color(0xFFE2E8F0)
    )
)

sealed interface PhotoStudioAiUiState {
    data object Idle : PhotoStudioAiUiState
    data class Generating(val statusText: String) : PhotoStudioAiUiState
    data class Success(val message: String) : PhotoStudioAiUiState
    data class Error(val errorMessage: String) : PhotoStudioAiUiState
}

data class PhotoGalleryCreation(
    val id: String,
    val title: String,
    val filePath: String,
    val bitmap: Bitmap,
    val filterName: String,
    val aiOverlayLabel: String?,
    val createdAtLabel: String
)

private object PhotoStudioGalleryStorage {
    private const val GALLERY_DIR_NAME = "photo_studio_gallery"
    private const val METADATA_FILE_NAME = "gallery_metadata.json"

    private fun galleryDir(context: Context): File {
        val dir = File(context.filesDir, GALLERY_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun loadCreations(context: Context): List<PhotoGalleryCreation> = withContext(Dispatchers.IO) {
        val dir = galleryDir(context)
        val metaFile = File(dir, METADATA_FILE_NAME)
        if (!metaFile.exists()) return@withContext emptyList()

        val loaded = mutableListOf<PhotoGalleryCreation>()
        try {
            val array = JSONArray(metaFile.readText())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id").takeIf { it.isNotBlank() } ?: continue
                val filePath = obj.optString("filePath").takeIf { it.isNotBlank() } ?: continue
                val imgFile = File(filePath)
                if (!imgFile.exists()) continue
                val bmp = BitmapFactory.decodeFile(imgFile.absolutePath) ?: continue
                loaded.add(
                    PhotoGalleryCreation(
                        id = id,
                        title = obj.optString("title", "Studio Creation"),
                        filePath = filePath,
                        bitmap = bmp,
                        filterName = obj.optString("filterName", "Original"),
                        aiOverlayLabel = obj.optString("aiOverlayLabel").takeIf { it.isNotBlank() },
                        createdAtLabel = obj.optString("createdAtLabel", "Saved")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(PHOTO_STUDIO_LOG_TAG, "Failed to load saved gallery creations", e)
        }
        loaded
    }

    suspend fun saveCreation(
        context: Context,
        title: String,
        bitmap: Bitmap,
        filterName: String,
        aiOverlayLabel: String?
    ): PhotoGalleryCreation? = withContext(Dispatchers.IO) {
        try {
            val dir = galleryDir(context)
            val id = "creation_${System.currentTimeMillis()}_${(100..999).random()}"
            val imageFile = File(dir, "$id.png")
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            val timestamp = SimpleDateFormat("MMM d, h:mm a", Locale.US).format(Date())
            val item = PhotoGalleryCreation(
                id = id,
                title = title.ifBlank { "Studio Creation" },
                filePath = imageFile.absolutePath,
                bitmap = bitmap,
                filterName = filterName,
                aiOverlayLabel = aiOverlayLabel?.takeIf { it.isNotBlank() },
                createdAtLabel = timestamp
            )
            persistMetadata(dir, listOf(item) + loadMetadataOnly(dir))
            item
        } catch (e: Exception) {
            Log.e(PHOTO_STUDIO_LOG_TAG, "Failed to save creation", e)
            null
        }
    }

    suspend fun deleteCreation(context: Context, creation: PhotoGalleryCreation) = withContext(Dispatchers.IO) {
        try {
            val dir = galleryDir(context)
            File(creation.filePath).delete()
            val remaining = loadMetadataOnly(dir).filterNot { it.id == creation.id }
            persistMetadata(dir, remaining)
        } catch (e: Exception) {
            Log.e(PHOTO_STUDIO_LOG_TAG, "Failed to delete creation", e)
        }
    }

    private fun loadMetadataOnly(dir: File): List<PhotoGalleryCreation> {
        val metaFile = File(dir, METADATA_FILE_NAME)
        if (!metaFile.exists()) return emptyList()
        val list = mutableListOf<PhotoGalleryCreation>()
        val dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        try {
            val array = JSONArray(metaFile.readText())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                list.add(
                    PhotoGalleryCreation(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        filePath = obj.optString("filePath"),
                        bitmap = dummyBitmap,
                        filterName = obj.optString("filterName"),
                        aiOverlayLabel = obj.optString("aiOverlayLabel").takeIf { it.isNotBlank() },
                        createdAtLabel = obj.optString("createdAtLabel")
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    private fun persistMetadata(dir: File, items: List<PhotoGalleryCreation>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("filePath", item.filePath)
                    put("filterName", item.filterName)
                    put("aiOverlayLabel", item.aiOverlayLabel.orEmpty())
                    put("createdAtLabel", item.createdAtLabel)
                }
            )
        }
        File(dir, METADATA_FILE_NAME).writeText(array.toString())
    }
}

private fun scaleBitmapForStudio(source: Bitmap, maxDimension: Int = 1024): Bitmap {
    val w = source.width.coerceAtLeast(1)
    val h = source.height.coerceAtLeast(1)
    if (w <= maxDimension && h <= maxDimension) {
        return if (source.config == Bitmap.Config.ARGB_8888) source else source.copy(Bitmap.Config.ARGB_8888, false)
    }
    val ratio = maxDimension.toFloat() / maxOf(w, h).toFloat()
    val targetW = (w * ratio).toInt().coerceAtLeast(1)
    val targetH = (h * ratio).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(source, targetW, targetH, true)
}

private fun decodePickedPhotoBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
        raw?.let { scaleBitmapForStudio(it, 1024) }
    } catch (e: Exception) {
        Log.e(PHOTO_STUDIO_LOG_TAG, "Failed to decode picked photo", e)
        null
    }
}

private fun renderCreativeFilterBitmap(
    source: Bitmap,
    filter: StudioCreativeFilter,
    intensity: Float,
    contrastBoost: Float
): Bitmap {
    val safeSource = scaleBitmapForStudio(source, 960)
    val t = intensity.coerceIn(0f, 1f)
    val identity = StudioCreativeFilter.ORIGINAL.targetMatrix
    val target = filter.targetMatrix
    val blended = FloatArray(20) { i ->
        identity[i] + (target[i] - identity[i]) * t
    }

    // Apply subtle contrast scaling without clipping shadows completely
    val contrastScale = 1f + contrastBoost.coerceIn(0f, 0.5f)
    val translate = (-0.5f * contrastScale + 0.5f) * 255f
    blended[0] *= contrastScale
    blended[6] *= contrastScale
    blended[12] *= contrastScale
    blended[4] += translate
    blended[9] += translate
    blended[14] += translate

    val out = Bitmap.createBitmap(safeSource.width, safeSource.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(out)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(ColorMatrix(blended))
    }
    canvas.drawBitmap(safeSource, 0f, 0f, paint)
    return out
}

private suspend fun exportBitmapToDevicePhotos(
    context: Context,
    bitmap: Bitmap,
    title: String
): Uri? = withContext(Dispatchers.IO) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return@withContext null
    }
    try {
        val safeName = title.replace(Regex("[^A-Za-z0-9_ -]"), "").trim()
            .ifBlank { "StagesOfLife_Photo" }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${safeName}_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/StagesOfLifeStudio")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext null
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } catch (e: Exception) {
        Log.e(PHOTO_STUDIO_LOG_TAG, "Failed to export to device MediaStore", e)
        null
    }
}

/**
 * PhotoStudioComponent lets users import or pick photos, apply live ColorMatrix creative filters,
 * generate AI-powered artistic overlays via Firebase AI Logic (gemini-nano-banana-2.1),
 * and save/manage creations inside a persistent local Gallery view.
 */
@Composable
fun PhotoStudioComponent(
    displayName: String,
    currentProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int?,
    onApplyToProfile: (Bitmap) -> Unit,
    onShareCreationToFeed: (category: String, title: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val aiImageEditModel = remember {
        lazy {
            Firebase.ai.generativeModel(
                modelName = PHOTO_STUDIO_MODEL_NAME,
                generationConfig = generationConfig {
                    responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                }
            )
        }
    }

    var sourceBitmap by remember(selected3DAvatarResId) {
        val initial = currentProfileBitmap?.let { scaleBitmapForStudio(it, 960) } ?: run {
            val resId = selected3DAvatarResId ?: R.drawable.img_avatar_cyber_astronaut
            BitmapFactory.decodeResource(context.resources, resId)?.let { scaleBitmapForStudio(it, 960) }
        }
        mutableStateOf(initial)
    }

    var selectedFilter by remember { mutableStateOf(StudioCreativeFilter.ORIGINAL) }
    var filterIntensity by remember { mutableFloatStateOf(0.85f) }
    var contrastBoost by remember { mutableFloatStateOf(0.06f) }

    var selectedOverlayPreset by remember { mutableStateOf(AI_OVERLAY_PRESETS.first()) }
    var customOverlayPrompt by remember { mutableStateOf(AI_OVERLAY_PRESETS.first().promptInstruction) }
    var aiOverlaidBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var appliedOverlayLabel by remember { mutableStateOf<String?>(null) }
    var aiUiState by remember { mutableStateOf<PhotoStudioAiUiState>(PhotoStudioAiUiState.Idle) }

    var activeStudioTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Filters, 1 = AI Overlays, 2 = Gallery
    var creationTitleInput by rememberSaveable(displayName) {
        val prefix = displayName.substringBefore(" ").ifBlank { "My" }
        mutableStateOf("$prefix's Studio Shot")
    }
    var galleryFeedback by remember { mutableStateOf<String?>(null) }

    val galleryCreations = remember { mutableStateListOf<PhotoGalleryCreation>() }
    var inspectedCreation by remember { mutableStateOf<PhotoGalleryCreation?>(null) }

    LaunchedEffect(Unit) {
        val saved = PhotoStudioGalleryStorage.loadCreations(context)
        galleryCreations.clear()
        galleryCreations.addAll(saved)
    }

    val filteredBitmap = remember(sourceBitmap, selectedFilter, filterIntensity, contrastBoost) {
        sourceBitmap?.let { src ->
            renderCreativeFilterBitmap(
                source = src,
                filter = selectedFilter,
                intensity = filterIntensity,
                contrastBoost = contrastBoost
            )
        }
    }

    val activePreviewBitmap = aiOverlaidBitmap ?: filteredBitmap

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val decoded = withContext(Dispatchers.IO) {
                    decodePickedPhotoBitmap(context, uri)
                }
                if (decoded != null) {
                    sourceBitmap = decoded
                    aiOverlaidBitmap = null
                    appliedOverlayLabel = null
                    aiUiState = PhotoStudioAiUiState.Idle
                    galleryFeedback = "Loaded selected photo into Photo Studio."
                } else {
                    aiUiState = PhotoStudioAiUiState.Error("Could not decode the selected image. Please try another photo.")
                }
            }
        }
    }

    fun triggerAiOverlayGeneration() {
        val baseBitmap = filteredBitmap
        val prompt = customOverlayPrompt.trim()
        if (baseBitmap == null) {
            aiUiState = PhotoStudioAiUiState.Error("Pick or select a photo first before generating an AI overlay.")
            return
        }
        if (prompt.isBlank()) {
            aiUiState = PhotoStudioAiUiState.Error("Describe the AI overlay you want to apply.")
            return
        }
        if (aiUiState is PhotoStudioAiUiState.Generating) return

        aiUiState = PhotoStudioAiUiState.Generating("Applying AI overlay with Gemini Nano Banana 2.1...")
        scope.launch {
            val reqId = GlobalLoadingTracker.beginRequest(
                title = "Photo Studio AI Overlay",
                subtitle = "Generating '${selectedOverlayPreset.label}' overlay...",
                category = "Firebase AI Logic"
            )
            try {
                val generatedBitmap = withTimeout(60_000L) {
                    val response = aiImageEditModel.value.generateContent(
                        content {
                            image(baseBitmap)
                            text(
                                "Edit the provided photo by applying this creative visual overlay while keeping the main subject recognizable: $prompt"
                            )
                        }
                    )
                    response.candidates.firstOrNull()
                        ?.content?.parts
                        ?.filterIsInstance<ImagePart>()
                        ?.firstOrNull()
                        ?.image
                }
                if (generatedBitmap != null) {
                    val scaled = scaleBitmapForStudio(generatedBitmap, 1024)
                    aiOverlaidBitmap = scaled
                    appliedOverlayLabel = selectedOverlayPreset.label
                    aiUiState = PhotoStudioAiUiState.Success("AI overlay applied! Save it to your Gallery or keep refining.")
                } else {
                    aiUiState = PhotoStudioAiUiState.Error(
                        "Gemini returned text without an edited image. Try refining your overlay prompt and retry."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                Log.e(PHOTO_STUDIO_LOG_TAG, "AI overlay generation timed out", e)
                aiUiState = PhotoStudioAiUiState.Error("AI overlay generation timed out after 60s. Please try again.")
            } catch (e: Exception) {
                Log.e(PHOTO_STUDIO_LOG_TAG, "AI overlay generation failed", e)
                aiUiState = PhotoStudioAiUiState.Error(
                    e.localizedMessage ?: "Failed to generate AI overlay. Check network and retry."
                )
            } finally {
                GlobalLoadingTracker.endRequest(reqId)
            }
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
            .testTag("photo_studio_component_card")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = HoloCyan.copy(alpha = 0.16f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, HoloCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Photo Studio",
                            tint = HoloCyan,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Photo Studio & AI Overlays",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Apply creative filters, generate Gemini AI overlays & save to your gallery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { activeStudioTab = 2 }
                        .testTag("photo_studio_gallery_count_badge")
                ) {
                    Text(
                        text = "${galleryCreations.size} Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Source Controls Row: Pick Photo | Use Profile Photo | Sample Starter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("photo_studio_pick_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Pick Photo",
                        tint = HoloCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pick Photo",
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        val profileOrAvatar = currentProfileBitmap?.let { scaleBitmapForStudio(it, 960) } ?: run {
                            val resId = selected3DAvatarResId ?: R.drawable.img_avatar_cyber_astronaut
                            BitmapFactory.decodeResource(context.resources, resId)?.let { scaleBitmapForStudio(it, 960) }
                        }
                        if (profileOrAvatar != null) {
                            sourceBitmap = profileOrAvatar
                            aiOverlaidBitmap = null
                            appliedOverlayLabel = null
                            aiUiState = PhotoStudioAiUiState.Idle
                            galleryFeedback = "Loaded your active profile photo."
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonPink),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("photo_studio_load_avatar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Use Profile Photo",
                        tint = NeonPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Use Avatar",
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Starter Portraits Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Starters:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                THREE_D_AVATAR_PRESETS.take(5).forEach { preset ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            BitmapFactory.decodeResource(context.resources, preset.drawableResId)?.let { bmp ->
                                sourceBitmap = scaleBitmapForStudio(bmp, 960)
                                aiOverlaidBitmap = null
                                appliedOverlayLabel = null
                                aiUiState = PhotoStudioAiUiState.Idle
                                galleryFeedback = "Loaded '${preset.title}' starter."
                            }
                        },
                        label = { Text("${preset.badgeEmoji} ${preset.title.substringBefore(" ")}") },
                        modifier = Modifier.testTag("photo_studio_starter_${preset.id}")
                    )
                }
            }

            // Studio Canvas Preview Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.5.dp,
                        color = if (aiOverlaidBitmap != null) NeonPink else selectedFilter.accent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .testTag("photo_studio_preview_canvas"),
                contentAlignment = Alignment.Center
            ) {
                if (activePreviewBitmap != null) {
                    Image(
                        bitmap = activePreviewBitmap.asImageBitmap(),
                        contentDescription = "Photo Studio Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Pick a photo or starter portrait to begin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Filter & Overlay Status Pill
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    val overlayText = appliedOverlayLabel?.let { " + AI: $it" }.orEmpty()
                    Text(
                        text = "${selectedFilter.label}$overlayText",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Undo AI Overlay chip when an AI overlay is active
                if (aiOverlaidBitmap != null) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, NeonPink),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clickable {
                                aiOverlaidBitmap = null
                                appliedOverlayLabel = null
                                aiUiState = PhotoStudioAiUiState.Idle
                            }
                            .testTag("photo_studio_undo_ai_overlay_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Undo,
                                contentDescription = "Undo AI Overlay",
                                tint = NeonPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Undo AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Loading overlay while Gemini Nano Banana 2.1 processes
                if (aiUiState is PhotoStudioAiUiState.Generating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = HoloCyan,
                                modifier = Modifier.size(38.dp)
                            )
                            Text(
                                text = (aiUiState as PhotoStudioAiUiState.Generating).statusText,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Visible AI State Feedback / Error Banner (no silent fallback)
            AnimatedVisibility(visible = aiUiState is PhotoStudioAiUiState.Error) {
                val errMsg = (aiUiState as? PhotoStudioAiUiState.Error)?.errorMessage.orEmpty()
                Surface(
                    color = RosePulse.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, RosePulse),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("photo_studio_ai_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "AI Error",
                            tint = RosePulse
                        )
                        Text(
                            text = errMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { triggerAiOverlayGeneration() },
                            modifier = Modifier.testTag("photo_studio_retry_ai_button")
                        ) {
                            Text("Retry", color = RosePulse, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            if (aiUiState is PhotoStudioAiUiState.Success) {
                Text(
                    text = (aiUiState as PhotoStudioAiUiState.Success).message,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            } else if (!galleryFeedback.isNullOrBlank()) {
                Text(
                    text = galleryFeedback.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = HoloCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Save Creation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = creationTitleInput,
                    onValueChange = { creationTitleInput = it },
                    label = { Text("Creation Title") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("photo_studio_title_input")
                )
                Button(
                    onClick = {
                        val toSave = activePreviewBitmap ?: return@Button
                        scope.launch {
                            val saved = PhotoStudioGalleryStorage.saveCreation(
                                context = context,
                                title = creationTitleInput,
                                bitmap = toSave,
                                filterName = selectedFilter.label,
                                aiOverlayLabel = appliedOverlayLabel
                            )
                            if (saved != null) {
                                galleryCreations.add(0, saved)
                                galleryFeedback = "Saved '${saved.title}' to Gallery!"
                                activeStudioTab = 2
                            } else {
                                galleryFeedback = "Could not save creation."
                            }
                        }
                    },
                    enabled = activePreviewBitmap != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("photo_studio_save_creation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Save to Gallery",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", fontWeight = FontWeight.ExtraBold)
                }
            }

            // 3 Studio Workspace Tabs: Filters | AI Overlays | Gallery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(0, "Filters", Icons.Default.FilterVintage),
                    Triple(1, "AI Overlays", Icons.Default.AutoAwesome),
                    Triple(2, "Gallery (${galleryCreations.size})", Icons.Default.Collections)
                ).forEach { (idx, label, icon) ->
                    val isSelected = activeStudioTab == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeStudioTab = idx },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoloCyan.copy(alpha = 0.20f),
                            selectedLabelColor = HoloCyan
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("photo_studio_tab_$idx")
                    )
                }
            }

            when (activeStudioTab) {
                0 -> {
                    // Creative Filters Tab
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StudioCreativeFilter.entries.forEach { filter ->
                                val isSelected = selectedFilter == filter
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
                                            aiOverlaidBitmap = null
                                            appliedOverlayLabel = null
                                        }
                                        .testTag("photo_studio_filter_${filter.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(filter.accent)
                                            )
                                            Text(
                                                text = filter.label,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isSelected) filter.accent else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = filter.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Filter Intensity Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Intensity ${(filterIntensity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(105.dp)
                            )
                            Slider(
                                value = filterIntensity,
                                onValueChange = {
                                    filterIntensity = it
                                    aiOverlaidBitmap = null
                                    appliedOverlayLabel = null
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = HoloCyan,
                                    activeTrackColor = HoloCyan
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("photo_studio_filter_intensity_slider")
                            )
                        }

                        // Contrast Boost Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Contrast +${(contrastBoost * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonPink,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(105.dp)
                            )
                            Slider(
                                value = contrastBoost,
                                onValueChange = {
                                    contrastBoost = it
                                    aiOverlaidBitmap = null
                                    appliedOverlayLabel = null
                                },
                                valueRange = 0f..0.45f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonPink,
                                    activeTrackColor = NeonPink
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("photo_studio_contrast_slider")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    selectedFilter = StudioCreativeFilter.ORIGINAL
                                    filterIntensity = 0.85f
                                    contrastBoost = 0f
                                    aiOverlaidBitmap = null
                                    appliedOverlayLabel = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("photo_studio_reset_filters_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Filters",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset")
                            }

                            Button(
                                onClick = {
                                    activePreviewBitmap?.let { bmp ->
                                        onApplyToProfile(bmp)
                                        galleryFeedback = "Updated your active Profile Avatar!"
                                    }
                                },
                                enabled = activePreviewBitmap != null,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloCyan,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(48.dp)
                                    .testTag("photo_studio_set_profile_button")
                            ) {
                                Text("Set as Profile Photo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                1 -> {
                    // AI-Generated Overlays Tab
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Select an AI Overlay Style or write your own:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AI_OVERLAY_PRESETS.forEach { preset ->
                                val isSelected = selectedOverlayPreset.id == preset.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedOverlayPreset = preset
                                        customOverlayPrompt = preset.promptInstruction
                                    },
                                    label = { Text(preset.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = preset.accent.copy(alpha = 0.22f),
                                        selectedLabelColor = preset.accent
                                    ),
                                    modifier = Modifier.testTag("photo_studio_overlay_preset_${preset.id}")
                                )
                            }
                        }

                        OutlinedTextField(
                            value = customOverlayPrompt,
                            onValueChange = { customOverlayPrompt = it },
                            label = { Text("AI Overlay Prompt") },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("photo_studio_ai_overlay_prompt_input")
                        )

                        Button(
                            onClick = { triggerAiOverlayGeneration() },
                            enabled = filteredBitmap != null && aiUiState !is PhotoStudioAiUiState.Generating,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("photo_studio_generate_ai_overlay_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Generate AI Overlay",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (aiUiState is PhotoStudioAiUiState.Generating) {
                                    "Generating AI Overlay..."
                                } else {
                                    "Generate AI Overlay"
                                },
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                2 -> {
                    // Creations Gallery View
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (galleryCreations.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("photo_studio_empty_gallery")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Collections,
                                        contentDescription = null,
                                        tint = HoloCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Your Photo Studio Gallery is empty",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Apply a creative filter or AI overlay above and tap Save to build your collection.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            galleryCreations.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { creation ->
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { inspectedCreation = creation }
                                                .testTag("photo_studio_gallery_item_${creation.id}")
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Image(
                                                    bitmap = creation.bitmap.asImageBitmap(),
                                                    contentDescription = creation.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                )
                                                Text(
                                                    text = creation.title,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val badgeText = listOfNotNull(
                                                    creation.filterName,
                                                    creation.aiOverlayLabel?.let { "AI: $it" }
                                                ).joinToString(" • ")
                                                Text(
                                                    text = badgeText,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = HoloCyan,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = creation.createdAtLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Gallery Creation Inspector Dialog
    val activeInspected = inspectedCreation
    if (activeInspected != null) {
        Dialog(onDismissRequest = { inspectedCreation = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, HoloCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("photo_studio_gallery_inspector_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeInspected.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = listOfNotNull(
                                    activeInspected.filterName,
                                    activeInspected.aiOverlayLabel?.let { "AI Overlay: $it" },
                                    activeInspected.createdAtLabel
                                ).joinToString(" • "),
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan
                            )
                        }
                        IconButton(
                            onClick = { inspectedCreation = null },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Preview"
                            )
                        }
                    }

                    Image(
                        bitmap = activeInspected.bitmap.asImageBitmap(),
                        contentDescription = activeInspected.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                sourceBitmap = scaleBitmapForStudio(activeInspected.bitmap, 960)
                                selectedFilter = StudioCreativeFilter.ORIGINAL
                                aiOverlaidBitmap = null
                                appliedOverlayLabel = null
                                creationTitleInput = "${activeInspected.title} Remix"
                                activeStudioTab = 0
                                inspectedCreation = null
                                galleryFeedback = "Loaded creation into Studio editor."
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("photo_studio_inspector_edit_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", maxLines = 1)
                        }

                        Button(
                            onClick = {
                                onApplyToProfile(activeInspected.bitmap)
                                galleryFeedback = "Equipped '${activeInspected.title}' as Profile Photo!"
                                inspectedCreation = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("photo_studio_inspector_avatar_button")
                        ) {
                            Text("Use as Avatar", fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val exportedUri = exportBitmapToDevicePhotos(
                                        context = context,
                                        bitmap = activeInspected.bitmap,
                                        title = activeInspected.title
                                    )
                                    if (exportedUri != null) {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "image/png"
                                            putExtra(Intent.EXTRA_STREAM, exportedUri)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Created in Photo Studio: ${activeInspected.title} (${activeInspected.filterName})"
                                            )
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(shareIntent, "Share Photo Creation")
                                        )
                                        galleryFeedback = "Saved to Photos & opened share sheet."
                                    } else {
                                        onShareCreationToFeed(
                                            "Studio Art",
                                            activeInspected.title,
                                            "Created in Photo Studio with ${activeInspected.filterName}" +
                                                (activeInspected.aiOverlayLabel?.let { " + AI Overlay: $it" } ?: "")
                                        )
                                        galleryFeedback = "Shared creation to Community Wall!"
                                    }
                                    inspectedCreation = null
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("photo_studio_inspector_share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", maxLines = 1)
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val exported = exportBitmapToDevicePhotos(
                                            context = context,
                                            bitmap = activeInspected.bitmap,
                                            title = activeInspected.title
                                        )
                                        galleryFeedback = if (exported != null) {
                                            "Saved '${activeInspected.title}' to Device Pictures/StagesOfLifeStudio."
                                        } else {
                                            "Could not export to Device Photos."
                                        }
                                        inspectedCreation = null
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("photo_studio_inspector_download_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export", maxLines = 1)
                            }
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    PhotoStudioGalleryStorage.deleteCreation(context, activeInspected)
                                    galleryCreations.removeAll { it.id == activeInspected.id }
                                    inspectedCreation = null
                                    galleryFeedback = "Deleted '${activeInspected.title}' from gallery."
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("photo_studio_inspector_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Creation",
                                tint = RosePulse
                            )
                        }
                    }
                }
            }
        }
    }
}
