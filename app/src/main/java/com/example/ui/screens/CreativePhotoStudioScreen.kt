package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.RenderEffect
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.ui.studio.CreativePhotoGalleryStorage
import com.example.ui.studio.LocalStudioGalleryView
import com.example.ui.studio.LocalStudioPhoto
import com.example.ui.studio.LocalStudioPhotoInspectorDialog
import com.example.ui.studio.PERSONA_STYLIZED_FILTERS
import com.example.ui.studio.PersonaStylizedFilter
import com.example.ui.studio.StudioFilterFrameStyle
import com.example.ui.studio.StudioPersonaStage
import com.example.ui.studio.decodeAndOrientImageProxy
import com.example.ui.studio.filtersForPersona
import com.example.ui.studio.orderedFiltersForPersona
import com.example.ui.studio.renderPersonaStylizedBitmap
import com.example.ui.studio.scaleBitmapSafely
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreativePhotoStudioScreen(
    displayName: String,
    currentRole: String,
    currentProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int?,
    onSwitchPersonaStage: (role: String, age: Long) -> Unit,
    onApplyToProfileAvatar: (Bitmap) -> Unit,
    onShareCreationToFeed: (category: String, title: String, content: String) -> Unit,
    onLogStudioInteraction: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val syncedPersonaStage = remember(currentRole) {
        StudioPersonaStage.fromRole(currentRole)
    }
    var activePersona by remember(syncedPersonaStage) {
        mutableStateOf(syncedPersonaStage)
    }

    // Studio top-level mode: 0 = Camera & Edit Studio, 1 = Local Gallery
    var studioMode by rememberSaveable { mutableIntStateOf(0) }
    var showAllPersonasFilters by rememberSaveable { mutableStateOf(false) }

    val personaRecommendedFilters = remember(activePersona) {
        filtersForPersona(activePersona)
    }
    val allOrderedFilters = remember(activePersona) {
        orderedFiltersForPersona(activePersona)
    }
    val visibleFilters = if (showAllPersonasFilters) allOrderedFilters else personaRecommendedFilters

    var selectedFilter by remember(activePersona) {
        mutableStateOf(filtersForPersona(activePersona).first())
    }
    var selectedFrameStyle by remember(selectedFilter) {
        mutableStateOf(selectedFilter.defaultFrameStyle)
    }
    var filterIntensity by remember { mutableFloatStateOf(0.92f) }
    var brightnessOffset by remember { mutableFloatStateOf(0f) }
    var contrastBoost by remember(selectedFilter) {
        mutableFloatStateOf(selectedFilter.defaultContrastBoost)
    }

    // Captured or imported photo bitmap; null means live camera viewfinder is active
    var capturedSourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoTitleInput by rememberSaveable(displayName, activePersona) {
        val firstName = displayName.substringBefore(" ").ifBlank { "Studio" }
        mutableStateOf("$firstName • ${activePersona.displayName} Shot")
    }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showShutterFlash by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    // CameraX state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var isCameraHardwareBound by remember { mutableStateOf(false) }
    var activePreviewView by remember { mutableStateOf<PreviewView?>(null) }
    var activeImageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Persistent Local Gallery state
    val galleryPhotos = remember { mutableStateListOf<LocalStudioPhoto>() }
    var inspectedPhoto by remember { mutableStateOf<LocalStudioPhoto?>(null) }

    LaunchedEffect(Unit) {
        val loaded = CreativePhotoGalleryStorage.loadSavedPhotos(context)
        galleryPhotos.clear()
        galleryPhotos.addAll(loaded)
    }

    LaunchedEffect(showShutterFlash) {
        if (showShutterFlash) {
            delay(160L)
            showShutterFlash = false
        }
    }

    val renderedPreviewBitmap = remember(
        capturedSourceBitmap,
        selectedFilter,
        filterIntensity,
        brightnessOffset,
        contrastBoost,
        selectedFrameStyle
    ) {
        capturedSourceBitmap?.let { src ->
            renderPersonaStylizedBitmap(
                source = src,
                filter = selectedFilter,
                intensity = filterIntensity,
                brightnessOffset = brightnessOffset,
                contrastBoost = contrastBoost,
                frameStyle = selectedFrameStyle
            )
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        statusMessage = if (granted) {
            "Camera ready! Applying ${activePersona.displayName} filters live."
        } else {
            "Camera permission denied. You can still import photos or use your avatar."
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val decoded = withContext(Dispatchers.IO) {
                    try {
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
                        raw?.let { scaleBitmapSafely(it, 1280) }
                    } catch (_: Exception) {
                        null
                    }
                }
                if (decoded != null) {
                    capturedSourceBitmap = decoded
                    studioMode = 0
                    statusMessage = "Loaded photo! Customize your ${activePersona.displayName} filter & save."
                } else {
                    statusMessage = "Could not load selected image."
                }
            }
        }
    }

    val systemCameraFallbackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            capturedSourceBitmap = scaleBitmapSafely(bmp, 1280)
            statusMessage = "Photo captured! Customize your ${selectedFilter.name} look and save."
        }
    }

    fun takeStudioPhoto() {
        if (isCapturing) return
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        showShutterFlash = true
        val imageCapture = activeImageCapture
        if (imageCapture != null && isCameraHardwareBound) {
            isCapturing = true
            imageCapture.flashMode = flashMode
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val isFront = lensFacing == CameraSelector.LENS_FACING_FRONT
                        scope.launch {
                            val bmp = withContext(Dispatchers.Default) {
                                try {
                                    decodeAndOrientImageProxy(image, mirrorHorizontal = isFront)
                                } finally {
                                    image.close()
                                }
                            }
                            isCapturing = false
                            if (bmp != null) {
                                capturedSourceBitmap = bmp
                                statusMessage = "Captured! Applying ${selectedFilter.name} (${activePersona.displayName})."
                            } else {
                                activePreviewView?.bitmap?.let { previewBmp ->
                                    capturedSourceBitmap = scaleBitmapSafely(previewBmp, 1280)
                                    statusMessage = "Captured camera frame!"
                                }
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        isCapturing = false
                        val previewBmp = activePreviewView?.bitmap
                        if (previewBmp != null) {
                            capturedSourceBitmap = scaleBitmapSafely(previewBmp, 1280)
                            statusMessage = "Captured live camera preview frame!"
                        } else {
                            try {
                                systemCameraFallbackLauncher.launch(null)
                            } catch (_: Exception) {
                                statusMessage = "Camera capture unavailable on this device."
                            }
                        }
                    }
                }
            )
        } else {
            val previewBmp = activePreviewView?.bitmap
            if (previewBmp != null) {
                capturedSourceBitmap = scaleBitmapSafely(previewBmp, 1280)
                statusMessage = "Captured live viewfinder frame!"
            } else {
                try {
                    systemCameraFallbackLauncher.launch(null)
                } catch (_: Exception) {
                    statusMessage = "No hardware camera found — import a photo or use your profile avatar."
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("creative_photo_studio_screen")
    ) {
        // Top Studio Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, activePersona.accentColor.copy(alpha = 0.55f)),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("creative_studio_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.creative_studio_back_desc)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.creative_studio_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${activePersona.badgeEmoji} ${activePersona.displayName} • ${activePersona.styleHeadline}",
                                style = MaterialTheme.typography.labelSmall,
                                color = activePersona.accentColor,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Mode Switcher Pills: Camera vs Local Gallery
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = studioMode == 0,
                            onClick = { studioMode = 0 },
                            leadingIcon = {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text("Studio", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = activePersona.accentColor.copy(alpha = 0.22f),
                                selectedLabelColor = activePersona.accentColor
                            ),
                            modifier = Modifier.testTag("studio_mode_camera_tab")
                        )
                        FilterChip(
                            selected = studioMode == 1,
                            onClick = { studioMode = 1 },
                            leadingIcon = {
                                Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text("Gallery (${galleryPhotos.size})", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                                selectedLabelColor = CyberEmerald
                            ),
                            modifier = Modifier.testTag("studio_mode_gallery_tab")
                        )
                    }
                }

                // Persona Stage Quick-Switch Bar (tailors filters immediately to selected persona)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Persona:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    StudioPersonaStage.entries.forEach { stage ->
                        val isSelected = activePersona == stage
                        Surface(
                            color = if (isSelected) stage.accentColor.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (isSelected) 1.6.dp else 1.dp,
                                color = if (isSelected) stage.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    activePersona = stage
                                    val defaultAge = when (stage) {
                                        StudioPersonaStage.KID -> 10L
                                        StudioPersonaStage.TEEN -> 15L
                                        StudioPersonaStage.STUDENT -> 20L
                                        StudioPersonaStage.ADULT -> 28L
                                        StudioPersonaStage.BUSINESS -> 34L
                                        StudioPersonaStage.VETERAN -> 38L
                                    }
                                    onSwitchPersonaStage(stage.id, defaultAge)
                                    statusMessage = "Switched to ${stage.displayName} Studio Filters"
                                }
                                .testTag("studio_persona_chip_${stage.id}")
                        ) {
                            Text(
                                text = "${stage.badgeEmoji} ${stage.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) stage.accentColor else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        if (studioMode == 1) {
            LocalStudioGalleryView(
                photos = galleryPhotos,
                activePersona = activePersona,
                onOpenCamera = { studioMode = 0 },
                onInspectPhoto = { photo -> inspectedPhoto = photo },
                modifier = Modifier.weight(1f)
            )
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val isWideLayout = maxWidth >= 720.dp

                val viewfinderPane: @Composable (Modifier) -> Unit = { paneModifier ->
                    StudioViewfinderPane(
                        capturedBitmap = renderedPreviewBitmap,
                        isCameraPermissionGranted = hasCameraPermission,
                        lensFacing = lensFacing,
                        flashMode = flashMode,
                        selectedFilter = selectedFilter,
                        filterIntensity = filterIntensity,
                        brightnessOffset = brightnessOffset,
                        contrastBoost = contrastBoost,
                        selectedFrameStyle = selectedFrameStyle,
                        showShutterFlash = showShutterFlash,
                        isCapturing = isCapturing,
                        latestGalleryPhoto = galleryPhotos.firstOrNull(),
                        onGrantCameraPermission = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onSwitchLens = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        onCycleFlashMode = {
                            flashMode = when (flashMode) {
                                ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                else -> ImageCapture.FLASH_MODE_AUTO
                            }
                        },
                        onCapturePhoto = { takeStudioPhoto() },
                        onRetakePhoto = {
                            capturedSourceBitmap = null
                            statusMessage = "Camera viewfinder active."
                        },
                        onPickPhoto = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onUseAvatarFallback = {
                            val avatarBmp = currentProfileBitmap?.let { scaleBitmapSafely(it, 1280) }
                                ?: BitmapFactory.decodeResource(
                                    context.resources,
                                    selected3DAvatarResId ?: R.drawable.img_avatar_cyber_astronaut
                                )?.let { scaleBitmapSafely(it, 1280) }
                            if (avatarBmp != null) {
                                capturedSourceBitmap = avatarBmp
                                statusMessage = "Loaded profile avatar for ${activePersona.displayName} styling!"
                            }
                        },
                        onOpenGallery = { studioMode = 1 },
                        onPreviewViewReady = { activePreviewView = it },
                        onImageCaptureReady = { activeImageCapture = it },
                        onCameraBoundChanged = { isCameraHardwareBound = it },
                        modifier = paneModifier
                    )
                }

                val controlsPane: @Composable (Modifier) -> Unit = { paneModifier ->
                    StudioControlsAndFiltersPane(
                        activePersona = activePersona,
                        visibleFilters = visibleFilters,
                        selectedFilter = selectedFilter,
                        showAllPersonasFilters = showAllPersonasFilters,
                        onToggleShowAllFilters = { showAllPersonasFilters = it },
                        onSelectFilter = { filter ->
                            selectedFilter = filter
                            statusMessage = "Filter: ${filter.name} (${filter.personaStage.displayName})"
                        },
                        selectedFrameStyle = selectedFrameStyle,
                        onSelectFrameStyle = { selectedFrameStyle = it },
                        filterIntensity = filterIntensity,
                        onIntensityChange = { filterIntensity = it },
                        brightnessOffset = brightnessOffset,
                        onBrightnessChange = { brightnessOffset = it },
                        contrastBoost = contrastBoost,
                        onContrastChange = { contrastBoost = it },
                        photoTitle = photoTitleInput,
                        onPhotoTitleChange = { photoTitleInput = it },
                        hasCapturedPhoto = renderedPreviewBitmap != null,
                        statusMessage = statusMessage,
                        onSaveToLocalGallery = {
                            val finalBmp = renderedPreviewBitmap ?: return@StudioControlsAndFiltersPane
                            scope.launch {
                                val saved = CreativePhotoGalleryStorage.savePhotoToLocalGallery(
                                    context = context,
                                    bitmap = finalBmp,
                                    title = photoTitleInput,
                                    persona = activePersona,
                                    filter = selectedFilter,
                                    frameStyle = selectedFrameStyle
                                )
                                if (saved != null) {
                                    galleryPhotos.add(0, saved)
                                    onLogStudioInteraction(
                                        "Saved '${saved.title}' (${selectedFilter.name}) to Local Photo Studio Gallery"
                                    )
                                    statusMessage = "Saved '${saved.title}' to Local Gallery!"
                                } else {
                                    statusMessage = "Failed to save photo to Local Gallery."
                                }
                            }
                        },
                        onExportToDevicePhotos = {
                            val finalBmp = renderedPreviewBitmap ?: return@StudioControlsAndFiltersPane
                            scope.launch {
                                val uri = CreativePhotoGalleryStorage.exportToDeviceMediaStore(
                                    context = context,
                                    bitmap = finalBmp,
                                    title = photoTitleInput
                                )
                                statusMessage = if (uri != null) {
                                    "Saved copy to Device Photos (Pictures/StagesOfLifeStudio)!"
                                } else {
                                    "Saved in-app Local Gallery (Device MediaStore requires Android 10+)."
                                }
                            }
                        },
                        onSetAsProfileAvatar = {
                            val finalBmp = renderedPreviewBitmap ?: return@StudioControlsAndFiltersPane
                            onApplyToProfileAvatar(finalBmp)
                            onLogStudioInteraction("Updated Profile Avatar with '${selectedFilter.name}'")
                            statusMessage = "Updated your Profile Avatar!"
                        },
                        onShareToCommunityWall = {
                            val cleanTitle = photoTitleInput.trim().ifBlank {
                                "${activePersona.displayName} Studio Photo"
                            }
                            onShareCreationToFeed(
                                "Studio Art",
                                cleanTitle,
                                "Captured in Creative Photo Studio with ${activePersona.displayName} filter '${selectedFilter.name}' (${selectedFrameStyle.label})."
                            )
                            statusMessage = "Shared '$cleanTitle' to the Community Wall!"
                        },
                        onResetAdjustments = {
                            filterIntensity = 0.92f
                            brightnessOffset = 0f
                            contrastBoost = selectedFilter.defaultContrastBoost
                            selectedFrameStyle = selectedFilter.defaultFrameStyle
                            statusMessage = "Reset adjustments to ${selectedFilter.name} defaults."
                        },
                        modifier = paneModifier
                    )
                }

                if (isWideLayout) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        viewfinderPane(
                            Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                        )
                        controlsPane(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        viewfinderPane(Modifier.fillMaxWidth())
                        controlsPane(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }

    val activeInspected = inspectedPhoto
    if (activeInspected != null) {
        LocalStudioPhotoInspectorDialog(
            photo = activeInspected,
            onDismiss = { inspectedPhoto = null },
            onRemixInStudio = { bmp, stage ->
                activePersona = stage
                capturedSourceBitmap = scaleBitmapSafely(bmp, 1280)
                studioMode = 0
                statusMessage = "Loaded '${activeInspected.title}' back into Studio Editor."
            },
            onApplyToProfileAvatar = { bmp ->
                onApplyToProfileAvatar(bmp)
                onLogStudioInteraction("Equipped '${activeInspected.title}' from Local Gallery")
            },
            onDeletePhoto = { target ->
                scope.launch {
                    CreativePhotoGalleryStorage.deletePhoto(context, target)
                    galleryPhotos.removeAll { it.id == target.id }
                    statusMessage = "Deleted '${target.title}' from Local Gallery."
                }
            },
            onStatusMessage = { msg -> statusMessage = msg }
        )
    }
}

@Composable
private fun StudioViewfinderPane(
    capturedBitmap: Bitmap?,
    isCameraPermissionGranted: Boolean,
    lensFacing: Int,
    flashMode: Int,
    selectedFilter: PersonaStylizedFilter,
    filterIntensity: Float,
    brightnessOffset: Float,
    contrastBoost: Float,
    selectedFrameStyle: StudioFilterFrameStyle,
    showShutterFlash: Boolean,
    isCapturing: Boolean,
    latestGalleryPhoto: LocalStudioPhoto?,
    onGrantCameraPermission: () -> Unit,
    onSwitchLens: () -> Unit,
    onCycleFlashMode: () -> Unit,
    onCapturePhoto: () -> Unit,
    onRetakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
    onUseAvatarFallback: () -> Unit,
    onOpenGallery: () -> Unit,
    onPreviewViewReady: (PreviewView) -> Unit,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    onCameraBoundChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(previewView) {
        onPreviewViewReady(previewView)
    }

    // Apply live hardware RenderEffect ColorMatrix on Android 12+ (API 31+)
    LaunchedEffect(previewView, selectedFilter, filterIntensity, brightnessOffset, contrastBoost) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val matrix = selectedFilter.buildAdjustedMatrix(
                intensity = filterIntensity,
                brightnessOffset = brightnessOffset,
                contrastBoost = contrastBoost
            )
            previewView.setRenderEffect(
                RenderEffect.createColorFilterEffect(
                    ColorMatrixColorFilter(ColorMatrix(matrix))
                )
            )
        }
    }

    DisposableEffect(isCameraPermissionGranted, lensFacing, lifecycleOwner, capturedBitmap == null) {
        var provider: ProcessCameraProvider? = null
        if (isCameraPermissionGranted && capturedBitmap == null) {
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .setFlashMode(flashMode)
                    .build()
                try {
                    val camProvider = future.get()
                    provider = camProvider
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()
                    camProvider.unbindAll()
                    camProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                    onImageCaptureReady(imageCapture)
                    onCameraBoundChanged(true)
                } catch (_: Exception) {
                    try {
                        val camProvider = future.get()
                        provider = camProvider
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        camProvider.unbindAll()
                        camProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                        onImageCaptureReady(imageCapture)
                        onCameraBoundChanged(true)
                    } catch (_: Exception) {
                        onImageCaptureReady(null)
                        onCameraBoundChanged(false)
                    }
                }
            }, ContextCompat.getMainExecutor(context))
        } else {
            onImageCaptureReady(null)
            onCameraBoundChanged(false)
        }

        onDispose {
            try {
                provider?.unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    val flashAlpha by animateFloatAsState(
        targetValue = if (showShutterFlash) 0.78f else 0f,
        animationSpec = tween(durationMillis = 120),
        label = "studio_shutter_flash"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Main Viewfinder / Captured Photo Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0A0F1C))
                .border(2.dp, selectedFilter.accentColor.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                .testTag("creative_studio_viewfinder"),
            contentAlignment = Alignment.Center
        ) {
            if (capturedBitmap != null) {
                Image(
                    bitmap = capturedBitmap.asImageBitmap(),
                    contentDescription = "Filtered Studio Photo Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (isCameraPermissionGranted) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("creative_studio_camerax_preview")
                )

                // Live Frame Overlay on top of CameraX feed
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        drawRect(color = selectedFilter.accentColor.copy(alpha = 0.10f * filterIntensity))
                    }
                    when (selectedFrameStyle) {
                        StudioFilterFrameStyle.NEON_HUD -> {
                            val pad = size.minDimension * 0.06f
                            val len = size.minDimension * 0.12f
                            val stroke = 3.dp.toPx()
                            val c = selectedFilter.accentColor
                            drawLine(c, Offset(pad, pad), Offset(pad + len, pad), stroke, StrokeCap.Round)
                            drawLine(c, Offset(pad, pad), Offset(pad, pad + len), stroke, StrokeCap.Round)
                            drawLine(c, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), stroke, StrokeCap.Round)
                            drawLine(c, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), stroke, StrokeCap.Round)
                            drawLine(c, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), stroke, StrokeCap.Round)
                            drawLine(c, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), stroke, StrokeCap.Round)
                            drawLine(c, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), stroke, StrokeCap.Round)
                            drawLine(c, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), stroke, StrokeCap.Round)
                        }
                        StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE -> {
                            val inset = size.minDimension * 0.045f
                            drawRect(
                                color = Color(0xFFEAB308).copy(alpha = 0.8f),
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        StudioFilterFrameStyle.PERSONA_SIGNATURE -> {
                            val inset = 8.dp.toPx()
                            drawRoundRect(
                                color = selectedFilter.accentColor.copy(alpha = 0.8f),
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        StudioFilterFrameStyle.CINEMA_SPROCKET -> {
                            val barH = size.height * 0.055f
                            drawRect(Color.Black.copy(alpha = 0.78f), Offset.Zero, Size(size.width, barH))
                            drawRect(Color.Black.copy(alpha = 0.78f), Offset(0f, size.height - barH), Size(size.width, barH))
                        }
                        StudioFilterFrameStyle.CLEAN,
                        StudioFilterFrameStyle.VIGNETTE_FOCUS -> Unit
                    }
                }
            } else {
                // Camera Permission Onboarding Overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, top = 44.dp, bottom = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = selectedFilter.accentColor.copy(alpha = 0.18f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, selectedFilter.accentColor),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Access",
                                tint = selectedFilter.accentColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Text(
                        text = "Enable Camera for Live ${selectedFilter.personaStage.displayName} Filters",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Take portraits with real-time ${selectedFilter.name} color grading or pick a photo from your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                    Button(
                        onClick = onGrantCameraPermission,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedFilter.accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics(mergeDescendants = true) {}
                            .testTag("creative_studio_grant_camera_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Allow Camera Access", fontWeight = FontWeight.ExtraBold)
                    }
                    OutlinedButton(
                        onClick = onUseAvatarFallback,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("creative_studio_use_avatar_btn")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Style My Profile Avatar", color = HoloCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Top HUD Overlay Controls
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, selectedFilter.accentColor)
                ) {
                    Text(
                        text = "${selectedFilter.personaStage.badgeEmoji} ${selectedFilter.name} • ${selectedFilter.badge}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                if (capturedBitmap == null && isCameraPermissionGranted) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.70f),
                            shape = CircleShape,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .clickable { onCycleFlashMode() }
                                .testTag("creative_studio_flash_btn")
                        ) {
                            Icon(
                                imageVector = when (flashMode) {
                                    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                    ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                    else -> Icons.Default.FlashAuto
                                },
                                contentDescription = "Toggle Camera Flash Mode",
                                tint = if (flashMode == ImageCapture.FLASH_MODE_ON) SolarAmber else Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.70f),
                            shape = CircleShape,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .clickable { onSwitchLens() }
                                .testTag("creative_studio_switch_lens_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Front and Back Camera",
                                tint = HoloCyan,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // Shutter Flash Effect
            if (flashAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = flashAlpha))
                )
            }
        }

        // Camera Shutter / Pick / Gallery Dock
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPickPhoto,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("creative_studio_pick_photo_btn")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import", color = HoloCyan, fontWeight = FontWeight.Bold)
                }

                if (capturedBitmap == null) {
                    // Big Camera Shutter Button
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(3.dp, selectedFilter.accentColor, CircleShape)
                            .padding(5.dp)
                            .clip(CircleShape)
                            .background(if (isCapturing) Color.Gray else Color.White)
                            .clickable(enabled = !isCapturing) { onCapturePhoto() }
                            .testTag("creative_studio_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Take Photo",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onRetakePhoto,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("creative_studio_retake_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Photo", fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Quick Gallery Thumbnail Shortcut
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.5.dp, CyberEmerald, RoundedCornerShape(12.dp))
                        .clickable { onOpenGallery() }
                        .testTag("creative_studio_quick_gallery_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    val thumb = latestGalleryPhoto?.bitmap
                    if (thumb != null) {
                        Image(
                            bitmap = thumb.asImageBitmap(),
                            contentDescription = "Open Local Gallery",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "Open Local Gallery",
                            tint = CyberEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioControlsAndFiltersPane(
    activePersona: StudioPersonaStage,
    visibleFilters: List<PersonaStylizedFilter>,
    selectedFilter: PersonaStylizedFilter,
    showAllPersonasFilters: Boolean,
    onToggleShowAllFilters: (Boolean) -> Unit,
    onSelectFilter: (PersonaStylizedFilter) -> Unit,
    selectedFrameStyle: StudioFilterFrameStyle,
    onSelectFrameStyle: (StudioFilterFrameStyle) -> Unit,
    filterIntensity: Float,
    onIntensityChange: (Float) -> Unit,
    brightnessOffset: Float,
    onBrightnessChange: (Float) -> Unit,
    contrastBoost: Float,
    onContrastChange: (Float) -> Unit,
    photoTitle: String,
    onPhotoTitleChange: (String) -> Unit,
    hasCapturedPhoto: Boolean,
    statusMessage: String?,
    onSaveToLocalGallery: () -> Unit,
    onExportToDevicePhotos: () -> Unit,
    onSetAsProfileAvatar: () -> Unit,
    onShareToCommunityWall: () -> Unit,
    onResetAdjustments: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, activePersona.accentColor.copy(alpha = 0.45f)),
        modifier = modifier.testTag("creative_studio_controls_pane")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Persona Filter Header + Toggle (Persona Picks vs All 24)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${activePersona.badgeEmoji} ${activePersona.displayName} Stylized Filters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = selectedFilter.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FilterChip(
                    selected = showAllPersonasFilters,
                    onClick = { onToggleShowAllFilters(!showAllPersonasFilters) },
                    label = {
                        Text(
                            text = if (showAllPersonasFilters) "All (${PERSONA_STYLIZED_FILTERS.size})" else "For ${activePersona.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.testTag("creative_studio_toggle_all_filters")
                )
            }

            // Stylized Filter Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .testTag("creative_studio_filters_carousel"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                visibleFilters.forEach { filter ->
                    val isSelected = selectedFilter.id == filter.id
                    Surface(
                        color = if (isSelected) filter.accentColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) filter.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectFilter(filter) }
                            .testTag("creative_studio_filter_${filter.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(filter.accentColor)
                                )
                                Text(
                                    text = filter.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) filter.accentColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${filter.personaStage.badgeEmoji} ${filter.personaStage.displayName} • ${filter.badge}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Frame Style Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Studio Frame & Border",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioFilterFrameStyle.entries.forEach { frame ->
                        FilterChip(
                            selected = selectedFrameStyle == frame,
                            onClick = { onSelectFrameStyle(frame) },
                            label = { Text(frame.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("creative_studio_frame_${frame.id}")
                        )
                    }
                }
            }

            // Tone & Filter Strength Sliders
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StudioSliderRow(
                    label = "Filter ${(filterIntensity * 100).toInt()}%",
                    value = filterIntensity,
                    valueRange = 0f..1f,
                    accent = selectedFilter.accentColor,
                    onValueChange = onIntensityChange,
                    tag = "creative_studio_intensity_slider"
                )
                StudioSliderRow(
                    label = "Light ${brightnessOffset.toInt()}",
                    value = brightnessOffset,
                    valueRange = -40f..40f,
                    accent = HoloCyan,
                    onValueChange = onBrightnessChange,
                    tag = "creative_studio_brightness_slider"
                )
                StudioSliderRow(
                    label = "Contrast +${(contrastBoost * 100).toInt()}%",
                    value = contrastBoost,
                    valueRange = 0f..0.45f,
                    accent = NeonPink,
                    onValueChange = onContrastChange,
                    tag = "creative_studio_contrast_slider"
                )
            }

            // Save & Publish Controls
            OutlinedTextField(
                value = photoTitle,
                onValueChange = onPhotoTitleChange,
                label = { Text("Photo Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creative_studio_title_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onResetAdjustments,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(50.dp)
                        .testTag("creative_studio_reset_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                }

                Button(
                    onClick = onSaveToLocalGallery,
                    enabled = hasCapturedPhoto,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("creative_studio_save_gallery_btn")
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save to Local Gallery", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            AnimatedVisibility(
                visible = hasCapturedPhoto,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSetAsProfileAvatar,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("creative_studio_set_avatar_btn")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Avatar", color = HoloCyan, maxLines = 1)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        OutlinedButton(
                            onClick = onExportToDevicePhotos,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SolarAmber),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("creative_studio_export_photos_btn")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Photos", color = SolarAmber, maxLines = 1)
                        }
                    }

                    OutlinedButton(
                        onClick = onShareToCommunityWall,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonPink),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("creative_studio_share_wall_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Feed", color = NeonPink, maxLines = 1)
                    }
                }
            }

            if (!statusMessage.isNullOrBlank()) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creative_studio_status_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                            text = statusMessage,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    accent: Color,
    onValueChange: (Float) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(90.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent
            ),
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .testTag(tag)
        )
    }
}
