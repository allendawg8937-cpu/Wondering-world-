package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

enum class UserBadgeStyle {
    COMPACT,
    FULL,
    PILL
}

data class AccountBadgeConfig(
    val title: String,
    val shortLabel: String,
    val icon: ImageVector,
    val emoji: String,
    val gradientColors: List<Color>,
    val accentColor: Color,
    val description: String
)

fun getAccountBadgeConfig(role: String): AccountBadgeConfig {
    return when (role.lowercase()) {
        "student" -> AccountBadgeConfig(
            title = "Verified Student Account",
            shortLabel = "Student Account",
            icon = Icons.Default.School,
            emoji = "🎓",
            gradientColors = listOf(HoloCyan, CyberEmerald),
            accentColor = HoloCyan,
            description = "Unlocked FAFSA, Common App & Career Resources"
        )
        "business" -> AccountBadgeConfig(
            title = "Verified Business Account",
            shortLabel = "Business Account",
            icon = Icons.Default.BusinessCenter,
            emoji = "💼",
            gradientColors = listOf(SolarAmber, CyberEmerald),
            accentColor = SolarAmber,
            description = "Marketplace Ads, Digital Wallet & SBA Resources"
        )
        "veteran" -> AccountBadgeConfig(
            title = "Verified Veteran Leader",
            shortLabel = "Veteran Leader",
            icon = Icons.Default.Shield,
            emoji = "🛡️",
            gradientColors = listOf(HoloCyan, Color(0xFF3B82F6)),
            accentColor = HoloCyan,
            description = "VA Support, GI Bill & Community Leadership"
        )
        "creator" -> AccountBadgeConfig(
            title = "Verified Content Creator",
            shortLabel = "Content Creator",
            icon = Icons.Default.Movie,
            emoji = "🎬",
            gradientColors = listOf(NeonPink, Color(0xFF8B5CF6)),
            accentColor = NeonPink,
            description = "3D Studio Art, Music Engine & Community Media"
        )
        else -> AccountBadgeConfig(
            title = "Verified Stages Of Life Member",
            shortLabel = "Community Member",
            icon = Icons.Default.Verified,
            emoji = "🌟",
            gradientColors = listOf(HoloCyan, SolarAmber),
            accentColor = HoloCyan,
            description = "\"Create Your Life • Live Life Love\" community member"
        )
    }
}

@Composable
fun UserAccountBadge(
    role: String,
    modifier: Modifier = Modifier,
    style: UserBadgeStyle = UserBadgeStyle.FULL
) {
    val config = getAccountBadgeConfig(role)

    when (style) {
        UserBadgeStyle.FULL -> {
            Surface(
                color = config.accentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(config.gradientColors)
                ),
                modifier = modifier.testTag("user_account_badge_full_${role.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(config.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = config.icon,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = config.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = config.accentColor
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Badge",
                                tint = CyberEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = config.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        UserBadgeStyle.PILL -> {
            Surface(
                color = config.accentColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, config.accentColor.copy(alpha = 0.7f)),
                modifier = modifier.testTag("user_account_badge_pill_${role.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = config.emoji, fontSize = 12.sp)
                    Text(
                        text = config.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = config.accentColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = CyberEmerald,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }

        UserBadgeStyle.COMPACT -> {
            Surface(
                color = config.accentColor.copy(alpha = 0.20f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, config.accentColor),
                modifier = modifier.testTag("user_account_badge_compact_${role.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = config.emoji, fontSize = 11.sp)
                    Text(
                        text = config.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = config.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
