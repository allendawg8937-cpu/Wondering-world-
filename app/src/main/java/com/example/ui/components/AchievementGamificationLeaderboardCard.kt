package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

data class GamificationAchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Profile Setup", "Resource Sharing", "Helpful Engagement", "3D Customization"
    val badgeEmoji: String,
    val xpPoints: Int,
    val eqcBonus: Long
)

data class LeaderboardMemberEntry(
    val rank: Int,
    val displayName: String,
    val personaRole: String,
    val avatarEmoji: String,
    val xpPoints: Int,
    val badgesCount: Int,
    val topBadgeEmoji: String,
    val isCurrentUser: Boolean = false
)

val GAMIFICATION_ACHIEVEMENTS = listOf(
    GamificationAchievementItem(
        id = "ach_profile_architect",
        title = "Identity & Persona Pioneer",
        description = "Created your profile, selected a primary persona & accepted the Equality Pledge",
        category = "Profile Creation",
        badgeEmoji = "🪪",
        xpPoints = 250,
        eqcBonus = 30L
    ),
    GamificationAchievementItem(
        id = "ach_resource_beacon",
        title = "Community Resource Beacon",
        description = "Posted or shared verified VA, FAFSA, 211, or STEM resources on the Community Wall",
        category = "Posting Resources",
        badgeEmoji = "📚",
        xpPoints = 350,
        eqcBonus = 40L
    ),
    GamificationAchievementItem(
        id = "ach_helpful_mentor",
        title = "Helpful Content Champion",
        description = "Engaged with helpful community posts, liked resources & tipped fellow peers",
        category = "Helpful Engagement",
        badgeEmoji = "🤝",
        xpPoints = 300,
        eqcBonus = 35L
    ),
    GamificationAchievementItem(
        id = "ach_3d_theme_master",
        title = "6D Theme & Studio Customizer",
        description = "Customized your 3D visual style, background transparency, and profile jukebox",
        category = "Profile Customization",
        badgeEmoji = "🎨",
        xpPoints = 275,
        eqcBonus = 30L
    ),
    GamificationAchievementItem(
        id = "ach_financial_navigator",
        title = "Financial Literacy Navigator",
        description = "Tracked savings milestones & reviewed the Spending History Bar Chart",
        category = "Financial Literacy",
        badgeEmoji = "💎",
        xpPoints = 325,
        eqcBonus = 45L
    ),
    GamificationAchievementItem(
        id = "ach_equality_guardian",
        title = "We Are All Equal Guardian",
        description = "Supported safe, inclusive community moderation and cross-generational connection",
        category = "Community Safety",
        badgeEmoji = "🕊️",
        xpPoints = 400,
        eqcBonus = 50L
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementGamificationLeaderboardCard(
    userDisplayName: String,
    userRole: String,
    userAvatarEmoji: String,
    accentColor: Color,
    onEquipBadgeToProfile: (String) -> Unit = {},
    onAwardEqualCoinsBonus: (reason: String) -> Unit = {},
    onShareAchievementToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Profile Achievements & Badges, 1 = Community Leaderboard
    var leaderboardPersonaFilter by remember { mutableStateOf("ALL") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val unlockedAchievementIds = remember {
        mutableStateListOf(
            "ach_profile_architect",
            "ach_3d_theme_master",
            "ach_helpful_mentor"
        )
    }

    val earnedXpTotal = remember(unlockedAchievementIds.size) {
        650 + GAMIFICATION_ACHIEVEMENTS
            .filter { unlockedAchievementIds.contains(it.id) }
            .sumOf { it.xpPoints }
    }
    val currentLevel = (earnedXpTotal / 350).coerceAtLeast(1)
    val nextLevelXp = (currentLevel + 1) * 350
    val levelProgress = ((earnedXpTotal % 350) / 350f).coerceIn(0.12f, 1f)

    val leaderboardEntries = remember(userDisplayName, userRole, userAvatarEmoji, earnedXpTotal, unlockedAchievementIds.size) {
        val raw = listOf(
            LeaderboardMemberEntry(
                rank = 1,
                displayName = "Maya Lin",
                personaRole = "creator",
                avatarEmoji = "🎓",
                xpPoints = 2180,
                badgesCount = 6,
                topBadgeEmoji = "🕊️"
            ),
            LeaderboardMemberEntry(
                rank = 2,
                displayName = "Marcus Vance",
                personaRole = "veteran",
                avatarEmoji = "🛡️",
                xpPoints = 1940,
                badgesCount = 5,
                topBadgeEmoji = "📚"
            ),
            LeaderboardMemberEntry(
                rank = 3,
                displayName = userDisplayName.ifBlank { "Jordan Vance" },
                personaRole = userRole.ifBlank { "veteran" },
                avatarEmoji = userAvatarEmoji.ifBlank { "🌟" },
                xpPoints = earnedXpTotal,
                badgesCount = unlockedAchievementIds.size,
                topBadgeEmoji = "🎨",
                isCurrentUser = true
            ),
            LeaderboardMemberEntry(
                rank = 4,
                displayName = "Elena Rostova",
                personaRole = "business",
                avatarEmoji = "💼",
                xpPoints = 1490,
                badgesCount = 4,
                topBadgeEmoji = "💎"
            ),
            LeaderboardMemberEntry(
                rank = 5,
                displayName = "Devon Brooks",
                personaRole = "teen",
                avatarEmoji = "⚡",
                xpPoints = 1360,
                badgesCount = 4,
                topBadgeEmoji = "🎨"
            ),
            LeaderboardMemberEntry(
                rank = 6,
                displayName = "Leo Chen",
                personaRole = "kid",
                avatarEmoji = "🚀",
                xpPoints = 1210,
                badgesCount = 3,
                topBadgeEmoji = "🪪"
            ),
            LeaderboardMemberEntry(
                rank = 7,
                displayName = "Sarah Jenkins",
                personaRole = "adult",
                avatarEmoji = "🌿",
                xpPoints = 1150,
                badgesCount = 3,
                topBadgeEmoji = "🤝"
            )
        ).sortedByDescending { it.xpPoints }

        raw.mapIndexed { idx, item -> item.copy(rank = idx + 1) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("achievement_gamification_leaderboard_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1120).copy(alpha = 0.92f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(SolarAmber, accentColor, CyberEmerald, HoloCyan)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Header with Level & XP Pill
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(SolarAmber, NeonPink)))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Achievements & Leaderboards",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ACHIEVEMENTS, BADGES & LEADERBOARD",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Level $currentLevel • $earnedXpTotal XP • ${unlockedAchievementIds.size}/${GAMIFICATION_ACHIEVEMENTS.size} Badges",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CyberEmerald.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, CyberEmerald)
                ) {
                    Text(
                        text = "Rank #${leaderboardEntries.firstOrNull { it.isCurrentUser }?.rank ?: 3}",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // XP Level Progress Bar + Showcase of Unlocked Profile Badges
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.80f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Displayed Profile Achievement Showcase:",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$earnedXpTotal / $nextLevelXp XP to Level ${currentLevel + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        color = SolarAmber,
                        trackColor = Color(0xFF0F172A),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GAMIFICATION_ACHIEVEMENTS.filter { unlockedAchievementIds.contains(it.id) }.forEach { badge ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SolarAmber.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable {
                                        onEquipBadgeToProfile(badge.badgeEmoji)
                                        statusMessage = "✨ Equipped ${badge.badgeEmoji} '${badge.title}' on your 3D Profile Avatar!"
                                    }
                            ) {
                                Text(
                                    text = "${badge.badgeEmoji} ${badge.title}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !statusMessage.isNullOrBlank()) {
                if (!statusMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberEmerald.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = statusMessage.orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 2 Mode Tabs: Achievements & Badges | Community Leaderboards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(0, "🏅 Achievements & Badges", "gamification_tab_badges"),
                    Triple(1, "🏆 Community Leaderboard", "gamification_tab_leaderboard")
                ).forEach { (idx, label, tag) ->
                    val selected = activeTab == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) accentColor.copy(alpha = 0.28f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) accentColor else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeTab = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (activeTab == 0) {
                // ACHIEVEMENTS & BADGES LIST
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GAMIFICATION_ACHIEVEMENTS.forEach { ach ->
                        val isUnlocked = unlockedAchievementIds.contains(ach.id)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isUnlocked) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                            border = BorderStroke(
                                1.dp,
                                if (isUnlocked) CyberEmerald.copy(alpha = 0.65f) else SolarAmber.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = ach.badgeEmoji, fontSize = 24.sp)
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = ach.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = HoloCyan.copy(alpha = 0.22f)
                                            ) {
                                                Text(
                                                    text = "+${ach.xpPoints} XP",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = HoloCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${ach.category} • ${ach.description}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (!isUnlocked) {
                                            unlockedAchievementIds.add(ach.id)
                                            onEquipBadgeToProfile(ach.badgeEmoji)
                                            onAwardEqualCoinsBonus("Unlocked Achievement: ${ach.title}")
                                            statusMessage = "🏆 Unlocked '${ach.title}' (+${ach.xpPoints} XP & +25 EQC) and displayed on profile!"
                                        } else {
                                            onShareAchievementToWall(
                                                "🏅 Earned the ${ach.badgeEmoji} ${ach.title} Badge!",
                                                "Just unlocked the '${ach.title}' achievement (+${ach.xpPoints} XP) on Wondering World! (${ach.description})"
                                            )
                                            statusMessage = "📣 Shared '${ach.title}' badge to the Community Wall!"
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isUnlocked) CyberEmerald else SolarAmber,
                                        contentColor = Color(0xFF0F172A)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("achievement_action_${ach.id}")
                                ) {
                                    Text(
                                        text = if (isUnlocked) "Share Badge" else "Unlock +${ach.xpPoints} XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // COMMUNITY LEADERBOARDS
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "🌍 All Personas",
                            "veteran" to "🛡️ Veterans",
                            "creator" to "🎓 Students",
                            "business" to "💼 Business",
                            "teen" to "🎒 Teens & Kids",
                            "adult" to "🌟 Adults"
                        ).forEach { (roleKey, label) ->
                            FilterChip(
                                selected = leaderboardPersonaFilter == roleKey,
                                onClick = { leaderboardPersonaFilter = roleKey },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    val filteredLeaderboard = leaderboardEntries.filter { entry ->
                        when (leaderboardPersonaFilter) {
                            "ALL" -> true
                            "teen" -> entry.personaRole == "teen" || entry.personaRole == "kid"
                            else -> entry.personaRole.equals(leaderboardPersonaFilter, ignoreCase = true)
                        }
                    }

                    filteredLeaderboard.forEach { member ->
                        val medal = when (member.rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#${member.rank}"
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (member.isCurrentUser) accentColor.copy(alpha = 0.25f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                            border = BorderStroke(
                                width = if (member.isCurrentUser) 1.5.dp else 1.dp,
                                color = if (member.isCurrentUser) SolarAmber else Color.White.copy(alpha = 0.14f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("leaderboard_row_${member.rank}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = medal,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(text = member.avatarEmoji, fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = if (member.isCurrentUser) "${member.displayName} (You)" else member.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Persona: ${member.personaRole.uppercase()} • ${member.badgesCount} Badges (${member.topBadgeEmoji})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CyberEmerald.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "${member.xpPoints} XP",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
