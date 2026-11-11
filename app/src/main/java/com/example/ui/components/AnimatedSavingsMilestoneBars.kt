package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class SavingsMilestoneTier(
    val percentage: Int,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color
)

val SAVINGS_MILESTONES = listOf(
    SavingsMilestoneTier(
        percentage = 25,
        title = "Starter Safety Buffer",
        subtitle = "Covers small emergencies & builds initial saving habit",
        badge = "🌱 25% STARTER",
        accentColor = HoloCyan
    ),
    SavingsMilestoneTier(
        percentage = 50,
        title = "Momentum Powerhouse",
        subtitle = "Halfway mark! Compound confidence & automated savings",
        badge = "⚡ 50% MOMENTUM",
        accentColor = CyberEmerald
    ),
    SavingsMilestoneTier(
        percentage = 75,
        title = "Growth Accelerator",
        subtitle = "Within striking distance of your major life goal",
        badge = "🔥 75% ACCELERATOR",
        accentColor = SolarAmber
    ),
    SavingsMilestoneTier(
        percentage = 100,
        title = "Savings Goal Champion",
        subtitle = "Full target achieved! True financial stability unlocked",
        badge = "🏆 100% CHAMPION",
        accentColor = NeonPink
    )
)

@Composable
fun AnimatedSavingsMilestoneBars(
    currentBalanceDollars: Double,
    goalTargetDollars: Double,
    onShareMilestoneToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val safeGoal = goalTargetDollars.coerceAtLeast(100.0)
    val overallRatio = (currentBalanceDollars / safeGoal).toFloat()

    val infiniteTransition = rememberInfiniteTransition(label = "milestone_sparkle_anim")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("animated_savings_milestone_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(SolarAmber, CyberEmerald))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Savings Goal Milestone Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Animated milestone tracks & achievement unlocks at 25%, 50%, 75% & 100%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = CyberEmerald.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f%% Done", (overallRatio * 100f).coerceAtMost(100f)),
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Milestone Rows
            SAVINGS_MILESTONES.forEach { milestone ->
                val milestoneTargetDollars = safeGoal * (milestone.percentage / 100.0)
                val isReached = currentBalanceDollars >= milestoneTargetDollars
                val tierProgress = ((currentBalanceDollars / milestoneTargetDollars).toFloat()).coerceIn(0f, 1f)

                val animatedProgress by animateFloatAsState(
                    targetValue = tierProgress,
                    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                    label = "milestone_anim_${milestone.percentage}"
                )

                Surface(
                    color = if (isReached) {
                        milestone.accentColor.copy(alpha = 0.14f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isReached) 1.5.dp else 1.dp,
                        color = if (isReached) {
                            milestone.accentColor.copy(alpha = shimmerAlpha)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isReached) milestone.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isReached) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isReached) Color.Black else Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "${milestone.percentage}% • ${milestone.title}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isReached) milestone.accentColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = milestone.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.US, "$%,.0f", milestoneTargetDollars),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = milestone.accentColor
                                )
                                Text(
                                    text = if (isReached) "UNLOCKED" else String.format(Locale.US, "$%,.0f left", (milestoneTargetDollars - currentBalanceDollars).coerceAtLeast(0.0)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isReached) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isReached) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Animated Progress Bar
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            color = milestone.accentColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }
                }
            }

            // Highest reached milestone celebration card
            val highestReached = SAVINGS_MILESTONES.lastOrNull { currentBalanceDollars >= safeGoal * (it.percentage / 100.0) }
            if (highestReached != null) {
                Surface(
                    color = highestReached.accentColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, highestReached.accentColor.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = highestReached.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Milestone Unlocked: ${highestReached.badge}!",
                                style = MaterialTheme.typography.labelMedium,
                                color = highestReached.accentColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Button(
                            onClick = {
                                onShareMilestoneToWall(
                                    "🎉 Reached Savings Milestone: ${highestReached.title}!",
                                    "I reached ${highestReached.percentage}% ($${String.format(Locale.US, "%,.0f", currentBalanceDollars)}) toward my savings goal of $${String.format(Locale.US, "%,.0f", safeGoal)} on WereEqual!"
                                )
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = highestReached.accentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Share to Wall",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
