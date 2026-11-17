package com.example.ui.studio

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import kotlinx.coroutines.launch

@Composable
fun LocalStudioGalleryView(
    photos: List<LocalStudioPhoto>,
    activePersona: StudioPersonaStage,
    onOpenCamera: () -> Unit,
    onInspectPhoto: (LocalStudioPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterByActivePersona by remember { mutableStateOf(false) }

    val displayedPhotos = remember(photos, filterByActivePersona, activePersona) {
        if (filterByActivePersona) {
            photos.filter { it.personaId.equals(activePersona.id, ignoreCase = true) }
        } else {
            photos
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Local Photo Studio Gallery",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${photos.size} saved ${if (photos.size == 1) "photo" else "photos"} stored privately on device",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = if (filterByActivePersona) activePersona.accentColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50),
                border = BorderStroke(
                    1.dp,
                    if (filterByActivePersona) activePersona.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(50))
                    .clickable { filterByActivePersona = !filterByActivePersona }
                    .testTag("studio_gallery_persona_filter_toggle")
            ) {
                Text(
                    text = if (filterByActivePersona) {
                        "${activePersona.badgeEmoji} ${activePersona.displayName} Only"
                    } else {
                        "All Personas"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (filterByActivePersona) activePersona.accentColor else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        if (displayedPhotos.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, activePersona.accentColor.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creative_studio_empty_gallery")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = activePersona.accentColor.copy(alpha = 0.16f),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = "Empty Local Gallery",
                                tint = activePersona.accentColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Text(
                        text = if (filterByActivePersona) {
                            "No ${activePersona.displayName} photos saved yet"
                        } else {
                            "Your Local Studio Gallery is empty"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Snap a photo with your camera, apply ${activePersona.displayName} stylized filters, and tap Save to Gallery.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onOpenCamera,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activePersona.accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .semantics(mergeDescendants = true) {}
                            .testTag("creative_studio_empty_open_camera_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Studio Camera", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val columns = if (maxWidth >= 680.dp) 3 else 2
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    displayedPhotos.chunked(columns).forEach { rowPhotos ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowPhotos.forEach { photo ->
                                val stage = StudioPersonaStage.fromRole(photo.personaId)
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.2.dp, stage.accentColor.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { onInspectPhoto(photo) }
                                        .testTag("creative_studio_gallery_item_${photo.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            photo.bitmap?.let { bmp ->
                                                Image(
                                                    bitmap = bmp.asImageBitmap(),
                                                    contentDescription = photo.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.72f),
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = "${stage.badgeEmoji} ${photo.personaLabel}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = photo.title,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${photo.filterName} • ${photo.frameLabel}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = stage.accentColor,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = photo.createdAtLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            repeat(columns - rowPhotos.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalStudioPhotoInspectorDialog(
    photo: LocalStudioPhoto,
    onDismiss: () -> Unit,
    onRemixInStudio: (Bitmap, StudioPersonaStage) -> Unit,
    onApplyToProfileAvatar: (Bitmap) -> Unit,
    onDeletePhoto: (LocalStudioPhoto) -> Unit,
    onStatusMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val stage = StudioPersonaStage.fromRole(photo.personaId)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, stage.accentColor),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("creative_studio_gallery_inspector")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${stage.badgeEmoji} ${photo.personaLabel} • ${photo.filterName} • ${photo.createdAtLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = stage.accentColor
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("creative_studio_inspector_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Photo Preview"
                        )
                    }
                }

                photo.bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photo.bitmap?.let { bmp ->
                                onRemixInStudio(bmp, stage)
                            }
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("creative_studio_inspector_remix")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Remix", maxLines = 1)
                    }

                    Button(
                        onClick = {
                            photo.bitmap?.let { bmp ->
                                onApplyToProfileAvatar(bmp)
                                onStatusMessage("Applied '${photo.title}' as your Profile Avatar!")
                            }
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("creative_studio_inspector_avatar")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Set Avatar", fontWeight = FontWeight.ExtraBold, maxLines = 1)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareUri = CreativePhotoGalleryStorage.getShareableUri(context, photo)
                            if (shareUri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, shareUri)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${photo.title} — Captured in Stages Of Life Studio (${photo.filterName})"
                                    )
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Studio Photo"))
                            } else {
                                onStatusMessage("Could not prepare share URI for this photo.")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("creative_studio_inspector_share")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", maxLines = 1)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        OutlinedButton(
                            onClick = {
                                val bmp = photo.bitmap ?: return@OutlinedButton
                                scope.launch {
                                    val uri = CreativePhotoGalleryStorage.exportToDeviceMediaStore(
                                        context = context,
                                        bitmap = bmp,
                                        title = photo.title
                                    )
                                    onStatusMessage(
                                        if (uri != null) {
                                            "Exported '${photo.title}' to Device Photos (Pictures/StagesOfLifeStudio)."
                                        } else {
                                            "Could not export to Device Photos."
                                        }
                                    )
                                    onDismiss()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CyberEmerald),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("creative_studio_inspector_export")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Device Photos", color = CyberEmerald, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    IconButton(
                        onClick = {
                            onDeletePhoto(photo)
                            onDismiss()
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("creative_studio_inspector_delete")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Photo from Gallery",
                            tint = RosePulse
                        )
                    }
                }
            }
        }
    }
}
