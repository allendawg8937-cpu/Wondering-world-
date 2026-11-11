package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.NetworkOperationInfo
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Global Loading Overlay component using a Material 3 [CircularProgressIndicator]
 * to provide visual feedback during background network, cloud database, and AI operations.
 *
 * Supports both a full-screen glassmorphic overlay and a minimized floating pill mode so
 * users get immediate visual feedback while retaining control over long-running tasks.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalLoadingOverlay(
    activeOperations: List<NetworkOperationInfo>,
    isOverlayMinimized: Boolean,
    onMinimizeToggle: (Boolean) -> Unit,
    onDismissAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = activeOperations.isNotEmpty()
    val primaryOperation = activeOperations.firstOrNull()

    // Live elapsed timer for the currently active network operation
    var elapsedMillis by remember(primaryOperation?.id) { mutableLongStateOf(0L) }
    LaunchedEffect(isLoading, primaryOperation?.id) {
        if (!isLoading || primaryOperation == null) {
            elapsedMillis = 0L
            return@LaunchedEffect
        }
        val start = primaryOperation.startedAtMillis
        while (true) {
            elapsedMillis = (System.currentTimeMillis() - start).coerceAtLeast(0L)
            delay(100L)
        }
    }

    // Reset minimized state once all active network operations complete
    LaunchedEffect(isLoading) {
        if (!isLoading && isOverlayMinimized) {
            onMinimizeToggle(false)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "global_loading_transition")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // 1. Full-Screen Global Loading Overlay Modal
    AnimatedVisibility(
        visible = isLoading && !isOverlayMinimized,
        enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.94f),
        exit = fadeOut(tween(180)) + scaleOut(tween(180), targetScale = 0.96f),
        modifier = modifier
    ) {
        val currentOp = primaryOperation ?: NetworkOperationInfo(
            id = "default_net_op",
            title = "Synchronizing with WereEqual Cloud...",
            subtitle = "Performing background network request",
            category = "Cloud Sync"
        )
        val elapsedSec = String.format(Locale.US, "%.1fs", elapsedMillis / 1000f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xCC070B19),
                            Color(0xE6040711)
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Intercepts background taps while modal is active */ }
                )
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = "Global network loading overlay: ${currentOp.title}"
                }
                .testTag("global_loading_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 12.dp,
                shadowElevation = 24.dp,
                border = BorderStroke(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            HoloCyan,
                            MaterialTheme.colorScheme.primary,
                            NeonPink,
                            CyberEmerald
                        )
                    )
                ),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .testTag("global_loading_overlay_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Row: Category Badge & Elapsed Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = HoloCyan.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.75f)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(CyberEmerald)
                                )
                                Text(
                                    text = currentOp.category.uppercase(Locale.US),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "⏱ $elapsedSec",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Centerpiece: CircularProgressIndicator with 6D Orbital Ring & Center Status
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(108.dp)
                    ) {
                        // Outer animated 6D orbital glow ring
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 3.dp.toPx()
                            val radius = (size.minDimension / 2f) * pulseScale.coerceAtMost(1.0f)
                            drawCircle(
                                color = HoloCyan.copy(alpha = 0.14f),
                                radius = radius,
                                style = Stroke(width = strokeWidth * 2.2f)
                            )
                            rotate(degrees = orbitAngle) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            Color.Transparent,
                                            HoloCyan.copy(alpha = 0.85f),
                                            NeonPink.copy(alpha = 0.85f),
                                            CyberEmerald.copy(alpha = 0.85f),
                                            Color.Transparent
                                        )
                                    ),
                                    startAngle = 0f,
                                    sweepAngle = 220f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        // Primary Material 3 CircularProgressIndicator
                        val progressValue = currentOp.progress
                        if (progressValue != null) {
                            CircularProgressIndicator(
                                progress = { progressValue.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .size(76.dp)
                                    .testTag("global_circular_progress_indicator"),
                                color = HoloCyan,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                strokeWidth = 6.dp,
                                strokeCap = StrokeCap.Round
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(76.dp)
                                    .testTag("global_circular_progress_indicator"),
                                color = HoloCyan,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                strokeWidth = 6.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }

                        // Inner Icon or Determinate Percentage
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Background Network Activity",
                                tint = CyberEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                            if (progressValue != null) {
                                Text(
                                    text = "${(progressValue * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Active Operation Headline & Subtitle
                    Text(
                        text = currentOp.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("global_loading_title")
                    )

                    Text(
                        text = currentOp.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("global_loading_subtitle")
                    )

                    // Optional linear progress bar when determinate step progress is provided
                    if (currentOp.progress != null) {
                        LinearProgressIndicator(
                            progress = { currentOp.progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = CyberEmerald,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Queue indicator when multiple background network tasks are active simultaneously
                    if (activeOperations.size > 1) {
                        Surface(
                            color = NeonPink.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Active Network Queue (${activeOperations.size} tasks):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPink,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    activeOperations.take(4).forEach { op ->
                                        Text(
                                            text = "• ${op.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons: Run in Background (Minimize) & Dismiss
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onMinimizeToggle(true) },
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.75f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("global_loading_minimize_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Minimize,
                                contentDescription = "Minimize to Background",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Background",
                                style = MaterialTheme.typography.labelMedium,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = onDismissAll,
                            modifier = Modifier.testTag("global_loading_dismiss_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Overlay",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Dismiss",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // 2. Minimized Floating Pill (when user clicks "Background" during a long operation)
    AnimatedVisibility(
        visible = isLoading && isOverlayMinimized,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(180))
    ) {
        val currentOp = primaryOperation ?: return@AnimatedVisibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                border = BorderStroke(1.5.dp, HoloCyan),
                shape = RoundedCornerShape(50),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .clickable { onMinimizeToggle(false) }
                    .testTag("global_loading_minimized_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .testTag("minimized_circular_progress_indicator"),
                        color = HoloCyan,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        strokeWidth = 2.5.dp
                    )
                    Column {
                        Text(
                            text = currentOp.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Tap to expand overlay",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact header status & manual trigger chip for the top navigation bar so users can see
 * live network status or trigger a background cloud synchronization on any screen.
 */
@Composable
fun GlobalNetworkSyncHeaderChip(
    isLoading: Boolean,
    onTriggerSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isLoading) HoloCyan.copy(alpha = 0.22f) else CyberEmerald.copy(alpha = 0.16f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isLoading) HoloCyan else CyberEmerald.copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .clickable { onTriggerSync() }
            .testTag("global_network_sync_chip")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(12.dp)
                        .testTag("header_mini_circular_progress"),
                    color = HoloCyan,
                    trackColor = HoloCyan.copy(alpha = 0.25f),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Syncing...",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoloCyan,
                    fontWeight = FontWeight.ExtraBold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync Cloud Network",
                    tint = CyberEmerald,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Sync",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
