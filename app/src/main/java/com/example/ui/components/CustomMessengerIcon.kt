package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink

/**
 * Custom-drawn adaptive Messenger & Social Pulse emblem.
 * Combines a layered diagonal holographic chat bubble, neural affinity signal arc,
 * and live unread indicator dot.
 */
@Composable
fun CustomMessengerIcon(
    isSelected: Boolean = true,
    unreadCount: Int = 0,
    accentColor: Color = HoloCyan,
    iconSize: Dp = 26.dp,
    tagName: String = "custom_messenger_icon",
    contentDesc: String = "WereEqual Adaptive Messenger",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "messenger_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "messenger_signal_pulse"
    )
    val inactiveStroke = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .size(iconSize)
            .semantics {
                contentDescription = if (unreadCount > 0) {
                    "$contentDesc ($unreadCount new updates)"
                } else {
                    contentDesc
                }
            }
            .testTag(tagName),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(iconSize)) {
            val w = size.width
            val h = size.height

            // Outer ambient halo when selected
            if (isSelected) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.28f),
                            NeonPink.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.48f),
                        radius = w * 0.56f * pulseScale
                    ),
                    radius = w * 0.56f * pulseScale,
                    center = Offset(w * 0.5f, h * 0.48f)
                )
            }

            // Speech bubble body + lower-left tail
            val bubbleLeft = w * 0.10f
            val bubbleTop = h * 0.12f
            val bubbleRight = w * 0.88f
            val bubbleBottom = h * 0.74f
            val tailPath = Path().apply {
                moveTo(w * 0.24f, bubbleBottom - h * 0.04f)
                lineTo(w * 0.14f, h * 0.90f)
                lineTo(w * 0.42f, bubbleBottom - h * 0.01f)
                close()
            }

            val fillBrush = if (isSelected) {
                Brush.linearGradient(
                    colors = listOf(accentColor, CyberEmerald, NeonPink),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        inactiveStroke.copy(alpha = 0.20f),
                        accentColor.copy(alpha = 0.16f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            }

            drawPath(path = tailPath, brush = fillBrush)
            drawRoundRect(
                brush = fillBrush,
                topLeft = Offset(bubbleLeft, bubbleTop),
                size = Size(bubbleRight - bubbleLeft, bubbleBottom - bubbleTop),
                cornerRadius = CornerRadius(w * 0.24f, h * 0.24f)
            )

            // Crisp border outline when unselected for immediate recognition
            if (!isSelected) {
                drawRoundRect(
                    color = inactiveStroke.copy(alpha = 0.82f),
                    topLeft = Offset(bubbleLeft, bubbleTop),
                    size = Size(bubbleRight - bubbleLeft, bubbleBottom - bubbleTop),
                    cornerRadius = CornerRadius(w * 0.24f, h * 0.24f),
                    style = Stroke(width = (w * 0.065f).coerceAtLeast(1.4f), join = StrokeJoin.Round)
                )
            }

            // Inner Adaptive 3-Node Signal Wave (represents real-time user-learning pulse)
            val node1 = Offset(w * 0.30f, h * 0.46f)
            val node2 = Offset(w * 0.49f, h * 0.35f)
            val node3 = Offset(w * 0.68f, h * 0.48f)
            val waveColor = if (isSelected) Color(0xFF06101B) else accentColor
            val wavePath = Path().apply {
                moveTo(node1.x, node1.y)
                lineTo(node2.x, node2.y)
                lineTo(node3.x, node3.y)
            }
            drawPath(
                path = wavePath,
                color = waveColor,
                style = Stroke(
                    width = (w * 0.085f).coerceAtLeast(1.8f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            val nodeRadius = (w * 0.062f).coerceAtLeast(1.5f)
            drawCircle(color = waveColor, radius = nodeRadius, center = node1)
            drawCircle(color = waveColor, radius = nodeRadius * 1.15f, center = node2)
            drawCircle(color = waveColor, radius = nodeRadius, center = node3)

            // Unread / Active notification pip in top-right corner
            if (unreadCount > 0) {
                val badgeCenter = Offset(w * 0.85f, h * 0.18f)
                val badgeRadius = (w * 0.14f).coerceAtLeast(3.2f)
                drawCircle(
                    color = Color(0xFF0A0F1A),
                    radius = badgeRadius * 1.25f,
                    center = badgeCenter
                )
                drawCircle(
                    color = NeonPink,
                    radius = badgeRadius,
                    center = badgeCenter
                )
            }
        }
    }
}
