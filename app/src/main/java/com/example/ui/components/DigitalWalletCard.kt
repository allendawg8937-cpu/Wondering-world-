package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.BudgetEntry
import com.example.data.model.UserProfile
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

private data class QuickWalletActionPreset(
    val label: String,
    val title: String,
    val category: String,
    val amountDollars: Double,
    val accent: Color
)

private val QUICK_WALLET_PRESETS = listOf(
    QuickWalletActionPreset("+$25 Creator Vault", "Content Creator Vault Deposit", "Save", 25.0, NeonPink),
    QuickWalletActionPreset("+$50 Savings Pass", "Digital Wallet Savings Deposit", "Save", 50.0, CyberEmerald),
    QuickWalletActionPreset("+$100 Business Reserve", "Business Account Growth Reserve", "Goal", 100.0, HoloCyan),
    QuickWalletActionPreset("-$15 Studio Gear", "Creator & Study Supplies", "Spend", 15.0, SolarAmber)
)

@Composable
fun DigitalWalletCard(
    profile: UserProfile,
    budgetState: UiState<List<BudgetEntry>>,
    onAddWalletTransaction: (title: String, category: String, amountDollars: Double) -> Unit,
    onOpenFullFinanceScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val entries = (budgetState as? UiState.Success)?.data.orEmpty()
    val savedFromEntriesCents = entries.sumOf { entry ->
        when (entry.category) {
            "Save", "Goal" -> entry.amountCents
            "Spend", "Give" -> -entry.amountCents
            else -> 0L
        }
    }
    val effectiveBalanceCents = (profile.savingsBalanceCents + savedFromEntriesCents).coerceAtLeast(0L)
    val balanceDollars = effectiveBalanceCents / 100.0
    val goalDollars = (profile.savingsGoalCents / 100.0).coerceAtLeast(100.0)
    val goalProgress = (balanceDollars / goalDollars).toFloat().coerceIn(0f, 1f)

    val accountTypeBadge = when (profile.role.lowercase()) {
        "business" -> "💼 BUSINESS ACCOUNT WALLET"
        "creator" -> "🎬 CONTENT ACCOUNT WALLET"
        "veteran" -> "🛡️ VETERAN & LEADER WALLET"
        else -> "🌟 PERSONAL DIGITAL WALLET"
    }

    var customTitle by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("50") }
    var selectedCategory by remember { mutableStateOf("Save") }
    var walletFeedback by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(CyberEmerald, HoloCyan, SolarAmber))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("digital_wallet_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
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
                        color = CyberEmerald.copy(alpha = 0.18f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, CyberEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "6D Digital Wallet",
                            tint = CyberEmerald,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "💳 Wondering World Digital Wallet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "FHD 6D Vault for Personal, Content & Business Accounts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                SixDimensionalHdBadge(label = "WALLET")
            }

            // Holographic 3D Digital Wallet Pass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E1B4B),
                                Color(0xFF064E3B)
                            )
                        )
                    )
                    .padding(16.dp)
                    .testTag("digital_wallet_pass_banner")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, CyberEmerald),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = accountTypeBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "6D PASS • ${profile.displayName.take(14).uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "AVAILABLE DIGITAL WALLET BALANCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format(Locale.US, "$%,.2f", balanceDollars),
                                style = MaterialTheme.typography.headlineLarge,
                                color = CyberEmerald,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "GOAL TARGET",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format(Locale.US, "$%,.0f", goalDollars),
                                style = MaterialTheme.typography.titleMedium,
                                color = SolarAmber,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { goalProgress },
                            color = CyberEmerald,
                            trackColor = Color.White.copy(alpha = 0.16f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${(goalProgress * 100).toInt()}% of Goal Reached",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Firestore Cloud Vault Synced",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // 1-Tap Quick Digital Wallet Actions
            Text(
                text = "1-Tap Digital Wallet Vault Actions:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QUICK_WALLET_PRESETS.forEach { preset ->
                    Surface(
                        color = preset.accent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, preset.accent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable {
                                onAddWalletTransaction(preset.title, preset.category, preset.amountDollars)
                                walletFeedback = "Logged ${preset.label} in your Digital Wallet!"
                            }
                            .testTag("wallet_quick_${preset.category.lowercase()}_${preset.amountDollars.toInt()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (preset.category == "Spend") Icons.Default.TrendingUp else Icons.Default.Savings,
                                contentDescription = null,
                                tint = preset.accent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = preset.accent,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Custom Digital Wallet Entry Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Save" to "💰 Deposit",
                    "Goal" to "🎯 Goal",
                    "Spend" to "🛍️ Spend",
                    "Give" to "🤝 Give"
                ).forEach { (catKey, label) ->
                    FilterChip(
                        selected = selectedCategory == catKey,
                        onClick = { selectedCategory = catKey },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    label = { Text("Wallet Note (e.g., Content Earnings)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.62f)
                        .testTag("digital_wallet_note_input")
                )
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it.filter { ch -> ch.isDigit() || ch == '.' }.take(7) },
                    label = { Text("Amount $") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(0.38f)
                        .testTag("digital_wallet_amount_input")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val amt = customAmount.toDoubleOrNull()?.coerceAtLeast(1.0) ?: 50.0
                        val note = customTitle.ifBlank {
                            when (selectedCategory) {
                                "Save" -> "Digital Wallet Vault Deposit"
                                "Goal" -> "Business / Creator Goal Allocation"
                                "Give" -> "Community Equal-Aid Contribution"
                                else -> "Digital Wallet Expense"
                            }
                        }
                        onAddWalletTransaction(note, selectedCategory, amt)
                        customTitle = ""
                        walletFeedback = "Added $$amt ($selectedCategory) to your Digital Wallet!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("digital_wallet_submit_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Digital Wallet", fontWeight = FontWeight.ExtraBold)
                }
            }

            if (!walletFeedback.isNullOrBlank()) {
                Text(
                    text = "✓ $walletFeedback",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            // Recent Digital Wallet Activity (Top 3)
            if (entries.isNotEmpty()) {
                Text(
                    text = "Recent Digital Wallet Activity:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                entries.take(3).forEach { entry ->
                    val isPositive = entry.category == "Save" || entry.category == "Goal"
                    val itemColor = if (isPositive) CyberEmerald else RosePulse
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = entry.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = entry.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = itemColor
                                )
                            }
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "%s$%,.2f",
                                    if (isPositive) "+" else "-",
                                    entry.amountCents / 100.0
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = itemColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
