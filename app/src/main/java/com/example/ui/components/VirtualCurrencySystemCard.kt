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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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

data class VirtualCurrencyTransaction(
    val id: String,
    val title: String,
    val amount: Long,
    val isEarned: Boolean,
    val category: String,
    val timestampLabel: String
)

data class CurrencyEarnBadgeQuest(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val rewardCoins: Long
)

data class CurrencySpendItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val costCoins: Long,
    val itemType: String, // "THEME" or "VIRTUAL_ITEM"
    val themeStyleId: String? = null,
    val themeColorHex: String? = null,
    val badgeEmoji: String? = null
)

val CURRENCY_EARN_BADGE_QUESTS = listOf(
    CurrencyEarnBadgeQuest(
        id = "welcome_pioneer",
        title = "First Welcome Pioneer Badge",
        subtitle = "Joined Wondering World & pledged equality for all members",
        emoji = "🌍",
        rewardCoins = 30L
    ),
    CurrencyEarnBadgeQuest(
        id = "helpful_mentor",
        title = "Helpful Community Mentor Badge",
        subtitle = "Shared resources, encouragement, or helpful posts on the wall",
        emoji = "🤝",
        rewardCoins = 50L
    ),
    CurrencyEarnBadgeQuest(
        id = "theme_architect_3d",
        title = "3D Theme Pioneer Badge",
        subtitle = "Customized your 6D Hypercube, Aurora, or Cyber Grid profile style",
        emoji = "🎨",
        rewardCoins = 40L
    ),
    CurrencyEarnBadgeQuest(
        id = "studio_creator",
        title = "Creative Studio & Jukebox Badge",
        subtitle = "Explored 6D background music & creative expression tools",
        emoji = "🎵",
        rewardCoins = 45L
    ),
    CurrencyEarnBadgeQuest(
        id = "equality_ambassador",
        title = "Equality Ambassador Badge",
        subtitle = "Supported fellow veterans, students, seniors, and peers",
        emoji = "🕊️",
        rewardCoins = 75L
    )
)

val CURRENCY_SPEND_SHOP_ITEMS = listOf(
    CurrencySpendItem(
        id = "theme_aurora_gold",
        title = "Solar Flare 3D Profile Theme",
        subtitle = "Unlocks & applies the radiant Solar Flare 3D engine + Gold accent",
        emoji = "☀️",
        costCoins = 60L,
        itemType = "THEME",
        themeStyleId = "SOLAR_FLARE",
        themeColorHex = "#F59E0B"
    ),
    CurrencySpendItem(
        id = "theme_aurora_emerald",
        title = "Aurora Nebula 3D Profile Theme",
        subtitle = "Unlocks & applies the shimmering Aurora 3D engine + Emerald glow",
        emoji = "🌌",
        costCoins = 75L,
        itemType = "THEME",
        themeStyleId = "AURORA",
        themeColorHex = "#10B981"
    ),
    CurrencySpendItem(
        id = "theme_cyber_grid_neon",
        title = "Cyber Grid 3D Horizon Theme",
        subtitle = "Unlocks & applies the Cyber Grid 3D engine + Neon Cyan palette",
        emoji = "⚡",
        costCoins = 80L,
        itemType = "THEME",
        themeStyleId = "CYBER_GRID",
        themeColorHex = "#06B6D4"
    ),
    CurrencySpendItem(
        id = "theme_crystal_prism_royal",
        title = "Crystal Prism 6D Royal Theme",
        subtitle = "Unlocks & applies the Crystal Prism 3D engine + Royal Violet",
        emoji = "💎",
        costCoins = 95L,
        itemType = "THEME",
        themeStyleId = "CRYSTAL_PRISM",
        themeColorHex = "#8B5CF6"
    ),
    CurrencySpendItem(
        id = "item_golden_crown",
        title = "Golden Crown Avatar Flair",
        subtitle = "Equips the 👑 Golden Crown badge on your 3D profile avatar",
        emoji = "👑",
        costCoins = 45L,
        itemType = "VIRTUAL_ITEM",
        badgeEmoji = "👑"
    ),
    CurrencySpendItem(
        id = "item_hyperspace_rocket",
        title = "Hyperspace Boost Flair",
        subtitle = "Equips the 🚀 Hyperspace Rocket badge & boosts profile spotlight",
        emoji = "🚀",
        costCoins = 50L,
        itemType = "VIRTUAL_ITEM",
        badgeEmoji = "🚀"
    ),
    CurrencySpendItem(
        id = "item_diamond_equality_crest",
        title = "Diamond Equality Crest",
        subtitle = "Equips the 💠 Diamond Equality Crest on your community profile",
        emoji = "💠",
        costCoins = 65L,
        itemType = "VIRTUAL_ITEM",
        badgeEmoji = "💠"
    ),
    CurrencySpendItem(
        id = "item_neon_jukebox_halo",
        title = "Neon Jukebox Halo Item",
        subtitle = "Equips the 🎧 Neon Jukebox Halo for music lovers",
        emoji = "🎧",
        costCoins = 40L,
        itemType = "VIRTUAL_ITEM",
        badgeEmoji = "🎧"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VirtualCurrencySystemCard(
    balance: Long,
    dailyBonusClaimed: Boolean,
    streakDays: Int,
    claimedBadgeIds: Set<String>,
    unlockedItemIds: Set<String>,
    transactions: List<VirtualCurrencyTransaction>,
    statusBanner: String?,
    accentColor: Color,
    onClaimDailyLoginBonus: () -> Unit,
    onRewardHelpfulPostBonus: () -> Unit,
    onClaimBadgeQuest: (CurrencyEarnBadgeQuest) -> Unit,
    onPurchaseOrApplyShopItem: (CurrencySpendItem) -> Unit,
    onTipCommunityUser: (recipientName: String, amount: Long, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Earn, 1 = Spend Shop, 2 = Tip Peers & Ledger
    var selectedRecipient by remember { mutableStateOf("Maya Lin (Student Mentor)") }
    var customRecipient by remember { mutableStateOf("") }
    var selectedTipAmount by remember { mutableLongStateOf(20L) }
    var tipNote by remember { mutableStateOf("Thank you for your helpful community post! 🌟") }

    val presetRecipients = remember {
        listOf(
            "Maya Lin (Student Mentor)",
            "Marcus Vance (Veteran Guide)",
            "Elena Rostova (Senior Historian)",
            "Devon Brooks (Creative Studio)"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("virtual_currency_system_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1022).copy(alpha = 0.90f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color(0xFFF59E0B),
                    accentColor,
                    Color(0xFF10B981)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Header with Live EqualCoins Balance
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
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🪙",
                            fontSize = 22.sp
                        )
                    }
                    Column {
                        Text(
                            text = "EQUALCOINS (EQC) TREASURY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Virtual Currency & Rewards Hub",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Starts with 250 EQC • Earn from logins, helpful posts & badges • Spend on themes, items & tipping",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Live Balance Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.20f),
                    border = BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                    modifier = Modifier.testTag("virtual_currency_balance_badge")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🪙 $balance EQC",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "🔥 $streakDays-Day Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Status / Reward Banner
            AnimatedVisibility(visible = !statusBanner.isNullOrBlank()) {
                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, Color(0xFF34D399)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("virtual_currency_status_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Color(0xFF6EE7B7),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusBanner,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFECFDF5),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Action Bar: Daily Login Bonus + Reward Helpful Post
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClaimDailyLoginBonus,
                    enabled = !dailyBonusClaimed,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("claim_daily_login_bonus_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dailyBonusClaimed) Color(0xFF1E293B) else Color(0xFFF59E0B),
                        contentColor = if (dailyBonusClaimed) Color(0xFF94A3B8) else Color(0xFF0F172A),
                        disabledContainerColor = Color(0xFF1E293B),
                        disabledContentColor = Color(0xFF6EE7B7)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (dailyBonusClaimed) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                        contentDescription = "Daily Login Bonus",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (dailyBonusClaimed) "Daily +50 EQC Claimed" else "Daily Login (+50 EQC)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onRewardHelpfulPostBonus,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reward_helpful_post_bonus_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color(0xFF052E16)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Reward Helpful Post",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Helpful Post (+25 EQC)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3 Mode Tabs: Earn | Spend Shop | Tip Peers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Triple(0, "🪙 Earn & Badges", "currency_tab_earn"),
                    Triple(1, "🛍️ Spend Shop", "currency_tab_spend"),
                    Triple(2, "🤝 Tip Users", "currency_tab_tip")
                )
                tabs.forEach { (idx, label, tag) ->
                    val selected = activeTab == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) accentColor.copy(alpha = 0.28f) else Color(0xFF1E293B).copy(alpha = 0.65f),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) accentColor else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeTab = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            when (activeTab) {
                0 -> {
                    // EARN TAB: Badges, Daily Login, Helpful Posts
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🏅 Earn EqualCoins via Achievement Badges & Community Contributions",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tip: You also automatically earn +25 EQC every time you publish a helpful post on the Community Wall and +10 EQC when endorsing posts!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )

                        CURRENCY_EARN_BADGE_QUESTS.forEach { badge ->
                            val isClaimed = claimedBadgeIds.contains(badge.id)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isClaimed) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFF1E293B).copy(alpha = 0.80f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isClaimed) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFFF59E0B).copy(alpha = 0.45f)
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
                                        Text(text = badge.emoji, fontSize = 24.sp)
                                        Column {
                                            Text(
                                                text = badge.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = badge.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onClaimBadgeQuest(badge) },
                                        enabled = !isClaimed,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isClaimed) Color(0xFF065F46) else Color(0xFFF59E0B),
                                            contentColor = if (isClaimed) Color(0xFFA7F3D0) else Color(0xFF0F172A),
                                            disabledContainerColor = Color(0xFF065F46).copy(alpha = 0.6f),
                                            disabledContentColor = Color(0xFFA7F3D0)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("claim_badge_${badge.id}")
                                    ) {
                                        Text(
                                            text = if (isClaimed) "✓ Earned" else "Claim +${badge.rewardCoins} EQC",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // SPEND SHOP TAB: 3D Profile Themes & Virtual Items
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎨 Spend EqualCoins on 3D Profile Themes & Virtual Items",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF93C5FD),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unlocking a 3D Profile Theme or Virtual Item immediately equips it on your profile!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )

                        CURRENCY_SPEND_SHOP_ITEMS.forEach { item ->
                            val isOwned = unlockedItemIds.contains(item.id)
                            val canAfford = balance >= item.costCoins
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isOwned) Color(0xFF1E1B4B).copy(alpha = 0.75f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isOwned) Color(0xFF818CF8) else accentColor.copy(alpha = 0.4f)
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
                                        Text(text = item.emoji, fontSize = 24.sp)
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (item.itemType == "THEME") Color(0xFF6366F1).copy(alpha = 0.25f) else Color(0xFFEC4899).copy(alpha = 0.25f)
                                                ) {
                                                    Text(
                                                        text = if (item.itemType == "THEME") "3D Theme" else "Virtual Item",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = item.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onPurchaseOrApplyShopItem(item) },
                                        enabled = isOwned || canAfford,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isOwned) Color(0xFF4F46E5) else Color(0xFFF59E0B),
                                            contentColor = if (isOwned) Color.White else Color(0xFF0F172A),
                                            disabledContainerColor = Color(0xFF334155),
                                            disabledContentColor = Color(0xFF94A3B8)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("shop_item_button_${item.id}")
                                    ) {
                                        Text(
                                            text = if (isOwned) "Equip Again" else "Buy ${item.costCoins} EQC",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TIP COMMUNITY MEMBERS & ACTIVITY LEDGER TAB
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🤝 Tip Other Users to Reward Helpful Advice & Kindness",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Select a Community Member or Enter Handle:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetRecipients.forEach { member ->
                                val selected = customRecipient.isBlank() && selectedRecipient == member
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        customRecipient = ""
                                        selectedRecipient = member
                                    },
                                    label = {
                                        Text(
                                            text = member,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentColor.copy(alpha = 0.30f),
                                        selectedLabelColor = Color.White,
                                        labelColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = customRecipient,
                            onValueChange = { customRecipient = it },
                            label = { Text("Or enter custom member name / @handle") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tip_custom_recipient_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedLabelColor = accentColor,
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        // Tip Amount Chips
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tip Amount:",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            listOf(10L, 20L, 50L, 100L).forEach { amt ->
                                val isSelected = selectedTipAmount == amt
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF1E293B),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFFDE68A) else Color.White.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTipAmount = amt }
                                        .testTag("select_tip_amount_$amt")
                                ) {
                                    Text(
                                        text = "🪙 $amt EQC",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tipNote,
                            onValueChange = { tipNote = it },
                            label = { Text("Encouragement Note (Included with Tip)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tip_note_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedLabelColor = accentColor,
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        val targetRecipient = customRecipient.trim().ifBlank { selectedRecipient }
                        Button(
                            onClick = {
                                onTipCommunityUser(targetRecipient, selectedTipAmount, tipNote)
                            },
                            enabled = balance >= selectedTipAmount && targetRecipient.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_currency_tip_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color(0xFF052E16)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = "Send Tip",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send 🪙 $selectedTipAmount EQC Tip to $targetRecipient",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Recent EqualCoins Ledger Summary
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
            Text(
                text = "📜 Recent EqualCoins Activity Ledger",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Bold
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                transactions.take(4).forEach { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${tx.category} • ${tx.timestampLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = if (tx.isEarned) "+${tx.amount} EQC" else "-${tx.amount} EQC",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (tx.isEarned) Color(0xFF34D399) else Color(0xFFFBBF24),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
