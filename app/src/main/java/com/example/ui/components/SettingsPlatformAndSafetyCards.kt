package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BlockedUser
import com.example.data.model.CommunityConnectionCard
import com.example.data.model.Friend
import com.example.data.model.UserProfile
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

private val LIFE_STAGE_PLATFORM_CHOICES = listOf(
    Triple("kid", "🧒 Kid Platform", 11L to "#F59E0B"),
    Triple("teen", "🛹 Teen Platform", 16L to "#00E5FF"),
    Triple("student", "🎓 Student Platform", 20L to "#8B5CF6"),
    Triple("adult", "🌟 Adult Platform", 28L to "#10B981"),
    Triple("business", "💼 Business Platform", 34L to "#EC4899"),
    Triple("veteran", "🎖️ Veteran Platform", 36L to "#6366F1")
)

private val SEARCH_ENGINE_OPTIONS = listOf("Google", "DuckDuckGo", "Bing")
private val LAYOUT_DENSITY_OPTIONS = listOf("Compact", "Balanced", "Expanded")
private val MUSIC_PLATFORM_CHOICES = listOf("Spotify", "Apple Music", "YouTube Music", "SoundCloud", "Pandora")
private val WALLET_TIER_OPTIONS = listOf(
    "Personal Digital Wallet",
    "Creator Vault",
    "Business Reserve",
    "Veteran Benefits Wallet"
)

private val SETTINGS_CONDUCT_ARTICLES = listOf(
    "Rule 1 • No Bullying" to "Zero tolerance for bullying, intimidation, or harassment of any community member.",
    "Rule 2 • We Are All Equal (Zero Racism)" to "No racist profiles, hate speech, or discrimination against any person or group.",
    "Rule 3 • Strict 18+ Protection" to "No sexual misconduct. Mature 18+ lounges are strictly age-gated; minors (13–17) stay protected.",
    "Rule 4 • No Selling Personal Info" to "Stages Of Life never sells your data, and members may never harvest or trade private info.",
    "Rule 5 • Fair Moderation & Safety" to "Violating the Code of Conduct results in immediate content removal and account termination."
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPlatformSwitcherCard(
    currentRole: String,
    currentVisualStyleId: String,
    currentSearchEngine: String,
    currentMusicPlatform: String,
    currentLayoutMode: String,
    notificationsEnabled: Boolean,
    showMatureLoungeEnabled: Boolean,
    onSelectLifeStagePlatform: (stageId: String, defaultAge: Long, accentHex: String) -> Unit,
    onSelectVisualStyle: (String) -> Unit,
    onSelectSearchEngine: (String) -> Unit,
    onSelectMusicPlatform: (String) -> Unit,
    onSelectLayoutMode: (String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleMatureLounge: (Boolean) -> Unit,
    onSavePlatformChanges: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_platform_switcher_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(22.dp))
                Column {
                    Text(
                        text = "Platform Options & Experience Switcher",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Change your life-stage platform, 3D visual engine, search engine, music platform & layout",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "1. Switch Life-Stage Platform:",
                style = MaterialTheme.typography.labelMedium,
                color = HoloCyan,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LIFE_STAGE_PLATFORM_CHOICES.forEach { (stageId, label, meta) ->
                    val selected = currentRole.equals(stageId, ignoreCase = true)
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectLifeStagePlatform(stageId, meta.first, meta.second) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_platform_stage_$stageId")
                    )
                }
            }

            Text(
                text = "2. Change 3D Visual Engine:",
                style = MaterialTheme.typography.labelMedium,
                color = NeonPink,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                THREE_D_THEME_VISUAL_STYLES.forEach { style ->
                    FilterChip(
                        selected = currentVisualStyleId == style.id,
                        onClick = { onSelectVisualStyle(style.id) },
                        label = { Text("◈ ${style.title}", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_platform_visual_${style.id.lowercase()}")
                    )
                }
            }

            Text(
                text = "3. Default Search & Music Platforms:",
                style = MaterialTheme.typography.labelMedium,
                color = CyberEmerald,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SEARCH_ENGINE_OPTIONS.forEach { engine ->
                    FilterChip(
                        selected = currentSearchEngine.equals(engine, ignoreCase = true),
                        onClick = { onSelectSearchEngine(engine) },
                        label = { Text("🔍 $engine", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_search_engine_${engine.lowercase()}")
                    )
                }
                MUSIC_PLATFORM_CHOICES.forEach { music ->
                    FilterChip(
                        selected = currentMusicPlatform.equals(music, ignoreCase = true),
                        onClick = { onSelectMusicPlatform(music) },
                        label = { Text("🎵 $music", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_music_platform_${music.lowercase().replace(" ", "_")}")
                    )
                }
            }

            Text(
                text = "4. Platform Density & Comfort:",
                style = MaterialTheme.typography.labelMedium,
                color = SolarAmber,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LAYOUT_DENSITY_OPTIONS.forEach { mode ->
                    FilterChip(
                        selected = currentLayoutMode.equals(mode, ignoreCase = true),
                        onClick = { onSelectLayoutMode(mode) },
                        label = { Text("📐 $mode", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_layout_mode_${mode.lowercase()}")
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(18.dp))
                    Text("Platform Notifications & Alerts", style = MaterialTheme.typography.labelLarge)
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    modifier = Modifier.testTag("settings_notifications_switch")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                    Text("Show 18+ Stability Lounge Tab", style = MaterialTheme.typography.labelLarge)
                }
                Switch(
                    checked = showMatureLoungeEnabled,
                    onCheckedChange = onToggleMatureLounge,
                    modifier = Modifier.testTag("settings_mature_lounge_switch")
                )
            }

            Button(
                onClick = onSavePlatformChanges,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HoloCyan, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_save_platform_options_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Platform Settings to Profile", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPhoneWalletSetupCard(
    profile: UserProfile,
    virtualCurrencyBalance: Long,
    selectedWalletTier: String,
    isVaultUnlocked: Boolean,
    onSelectWalletTier: (String) -> Unit,
    onUpdateSavingsGoal: (Double) -> Unit,
    onQuickWalletDeposit: (String, String, Double) -> Unit,
    onUnlockVault: () -> Unit,
    onLockVault: () -> Unit,
    onOpenFullWalletTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBalanceDollars = profile.savingsBalanceCents / 100.0
    val currentGoalDollars = (profile.savingsGoalCents / 100.0).coerceAtLeast(100.0)
    var goalInputText by remember(profile.savingsGoalCents) {
        mutableStateOf(String.format(Locale.US, "%.0f", currentGoalDollars))
    }
    var walletNote by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.75f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_wallet_setup_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Setup",
                        tint = CyberEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Digital Wallet Setup & Security",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Configure your wallet tier, savings goal, quick deposits & vault lock",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                OutlinedButton(
                    onClick = onOpenFullWalletTab,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("settings_open_full_wallet_btn")
                ) {
                    Text("Open Wallet", style = MaterialTheme.typography.labelSmall, color = CyberEmerald, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(13.dp))
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CyberEmerald.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Wallet Balance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${String.format(Locale.US, "%,.2f", currentBalanceDollars)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = CyberEmerald
                        )
                    }
                    Column {
                        Text("Savings Goal", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${String.format(Locale.US, "%,.0f", currentGoalDollars)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = HoloCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EqualCoins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$virtualCurrencyBalance EQC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SolarAmber
                        )
                    }
                }
            }

            Text(
                text = "Select Wallet Account Setup Tier:",
                style = MaterialTheme.typography.labelMedium,
                color = CyberEmerald,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WALLET_TIER_OPTIONS.forEach { tier ->
                    FilterChip(
                        selected = selectedWalletTier == tier,
                        onClick = { onSelectWalletTier(tier) },
                        label = { Text(tier, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("settings_wallet_tier_${tier.lowercase().replace(" ", "_")}")
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = goalInputText,
                    onValueChange = { goalInputText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Target Savings Goal ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("settings_wallet_goal_input")
                )
                Button(
                    onClick = {
                        val parsed = goalInputText.toDoubleOrNull()
                        if (parsed != null && parsed >= 10.0) {
                            onUpdateSavingsGoal(parsed)
                            walletNote = "✓ Updated Wallet Savings Goal to $${String.format(Locale.US, "%,.0f", parsed)}"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("settings_wallet_save_goal_btn")
                ) {
                    Text("Set Goal", fontWeight = FontWeight.ExtraBold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onQuickWalletDeposit("Settings Quick Vault Deposit", "Save", 25.0)
                        walletNote = "✓ Added +$25.00 to your Digital Wallet!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+$25 Quick Setup", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = {
                        if (isVaultUnlocked) onLockVault() else onUnlockVault()
                        walletNote = if (isVaultUnlocked) "🔒 Financial Vault locked." else "🔓 Financial Vault unlocked."
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("settings_wallet_vault_lock_btn")
                ) {
                    Icon(
                        imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isVaultUnlocked) "Lock Vault" else "Unlock Vault",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!walletNote.isNullOrBlank()) {
                Text(
                    text = walletNote ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SettingsCodeOfConductCard(
    pledgeAccepted: Boolean,
    onPledgeChange: (Boolean) -> Unit,
    onOpenFullCodeOfConductTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, SolarAmber.copy(alpha = 0.75f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_code_of_conduct_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = "Code of Conduct", tint = SolarAmber, modifier = Modifier.size(22.dp))
                    Column {
                        Text(
                            text = "Platform Code of Conduct",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Community Equality Charter & Safety Rules",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber
                        )
                    }
                }
                OutlinedButton(
                    onClick = onOpenFullCodeOfConductTab,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Full Page", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp))
                }
            }

            SETTINGS_CONDUCT_ARTICLES.forEach { (title, body) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                        Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (pledgeAccepted) "Equality Pledge Accepted" else "Accept Equality Pledge",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = pledgeAccepted,
                    onCheckedChange = onPledgeChange,
                    modifier = Modifier.testTag("settings_pledge_switch")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsBlockUsersCard(
    blockedUsers: List<BlockedUser>,
    friends: List<Friend>,
    communityConnections: List<CommunityConnectionCard>,
    onBlockUser: (targetUserId: String, targetDisplayName: String, reason: String) -> Unit,
    onUnblockUser: (blockedUserId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameOrIdInput by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("Boundary Preference") }
    val reasons = listOf("Boundary Preference", "Harassment", "Spam", "Inappropriate")

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, RosePulse.copy(alpha = 0.75f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_block_users_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Block, contentDescription = "Block Users", tint = RosePulse, modifier = Modifier.size(22.dp))
                Column {
                    Text(
                        text = "Blocking Users & Privacy Shield (${blockedUsers.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Blocked members cannot appear in your Social Wall, Discovery, or friend requests",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = nameOrIdInput,
                onValueChange = { nameOrIdInput = it },
                label = { Text("Enter User Name or User ID to Block") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_block_user_input")
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                reasons.forEach { reason ->
                    FilterChip(
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason },
                        label = { Text(reason, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Button(
                onClick = {
                    val trimmed = nameOrIdInput.trim()
                    if (trimmed.isNotEmpty()) {
                        val slugId = trimmed.lowercase().replace(Regex("[^a-z0-9_-]"), "_").take(50)
                        onBlockUser(slugId.ifBlank { "blocked_member" }, trimmed, selectedReason)
                        nameOrIdInput = ""
                    }
                },
                enabled = nameOrIdInput.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RosePulse, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_block_user_submit_btn")
            ) {
                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Block User", fontWeight = FontWeight.ExtraBold)
            }

            val quickSuggestions = remember(friends, communityConnections, blockedUsers) {
                val blockedNames = blockedUsers.map { it.blockedDisplayName.lowercase() }.toSet()
                val friendTargets = friends.map { it.friendUserId to it.displayName }
                val communityTargets = communityConnections.map { it.id to it.name }
                (friendTargets + communityTargets)
                    .distinctBy { it.first }
                    .filterNot { (_, name) -> name.lowercase() in blockedNames }
            }

            if (quickSuggestions.isNotEmpty()) {
                Text(
                    text = "Quick Block from Friends & Community:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickSuggestions.forEach { (id, name) ->
                        OutlinedButton(
                            onClick = { onBlockUser(id, name, selectedReason) },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("quick_block_member_$id")
                        ) {
                            Text("🚫 Block $name", style = MaterialTheme.typography.labelSmall, color = RosePulse)
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            if (blockedUsers.isEmpty()) {
                Text(
                    text = "You haven't blocked any users. Blocked accounts appear here for 1-tap unblocking.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    blockedUsers.forEach { entry ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RosePulse.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("blocked_user_row_${entry.blockedUserId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.blockedDisplayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Reason: ${entry.reason} • ID: ${entry.blockedUserId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onUnblockUser(entry.blockedUserId.ifBlank { entry.id }) },
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("unblock_user_btn_${entry.blockedUserId}")
                                ) {
                                    Text("Unblock", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDeleteAccountCard(
    userDisplayName: String,
    isDeleting: Boolean,
    onJumpToDownloadData: () -> Unit,
    onConfirmDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var deleteConfirmText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.85f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_delete_account_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Delete Account Warning",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Delete Account & Cloud Data",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFEF4444)
                    )
                    Text(
                        text = "Permanently remove profile, wallet goals, posts, stage data, and Firebase sign-in",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Account holder: $userDisplayName\n" +
                    "• Deletes your user profile, posts, budget goals, milestones, 3D avatar builds, stage progress, friends & blocklist.\n" +
                    "• Permanently deletes your Firebase authentication login. This cannot be undone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onJumpToDownloadData,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CyberEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_download_before_delete_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Download My Data (.json) Before Deleting", color = CyberEmerald, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showConfirmDialog = true },
                enabled = !isDeleting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_delete_account_button")
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deleting Account...", fontWeight = FontWeight.ExtraBold)
                } else {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete Account Permanently", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                deleteConfirmText = ""
            },
            title = {
                Text(
                    text = "Confirm Permanent Account Deletion",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "This will permanently delete your Stages Of Life account and all associated cloud records. Type DELETE below to confirm.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = deleteConfirmText,
                        onValueChange = { deleteConfirmText = it },
                        label = { Text("Type DELETE to confirm") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_delete_account_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        deleteConfirmText = ""
                        onConfirmDeleteAccount()
                    },
                    enabled = deleteConfirmText.trim().equals("DELETE", ignoreCase = true),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_delete_account_submit_btn")
                ) {
                    Text("Delete Forever", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        deleteConfirmText = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
