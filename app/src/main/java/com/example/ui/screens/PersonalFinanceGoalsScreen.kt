package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.components.FinancialBiometricProtectionGate
import com.example.ui.components.FinancialLiteracyTrackerCard
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.VioletNebula
import java.util.Locale

data class SavingsMilestoneStage(
    val id: String,
    val stageNumber: Int,
    val title: String,
    val subtitle: String,
    val fractionOfOverallGoal: Double,
    val quickBoostDollars: Double,
    val accent: Color
)

data class PersonalFinanceGoalItem(
    val id: String,
    val entryId: String?,
    val title: String,
    val categoryBadge: String,
    val savedDollars: Double,
    val targetDollars: Double,
    val accentColor: Color
)

private val GOAL_CATEGORY_PRESETS = listOf(
    "Emergency Fund" to CyberEmerald,
    "Education & FAFSA" to HoloCyan,
    "Housing & Stability" to SolarAmber,
    "Business & Career" to NeonPink,
    "Creator & STEM Tech" to VioletNebula
)

/**
 * Encodes a personal finance goal's category and target dollar amount inside the Firestore-compliant
 * `BudgetEntry.title` (<= 80 chars) so `users/{userId}/budget_entries` stores both the deposit
 * (`amountCents`) and the target milestone metadata cleanly.
 */
fun formatFirestoreGoalTitle(
    goalName: String,
    categoryLabel: String,
    targetDollars: Double
): String {
    val cleanName = goalName.replace("|", "-").trim().ifBlank { "Savings Milestone" }.take(38)
    val cleanCat = categoryLabel.replace("|", "-").trim().ifBlank { "Emergency Fund" }.take(18)
    val targetInt = targetDollars.toLong().coerceIn(50L, 1000000L)
    return "$cleanName|$cleanCat|$targetInt".take(80)
}

private fun parseFirestoreGoalEntry(entry: BudgetEntry, index: Int): PersonalFinanceGoalItem {
    val parts = entry.title.split("|")
    val savedDollars = (entry.amountCents / 100.0).coerceAtLeast(1.0)
    return if (parts.size >= 3) {
        val name = parts[0].trim().ifBlank { "Personal Goal" }
        val cat = parts[1].trim().ifBlank { "Savings Goal" }
        val target = parts[2].trim().toDoubleOrNull()?.coerceAtLeast(savedDollars) ?: (savedDollars * 2.5)
        val color = GOAL_CATEGORY_PRESETS.firstOrNull {
            it.first.equals(cat, ignoreCase = true)
        }?.second ?: when (index % 4) {
            0 -> CyberEmerald
            1 -> HoloCyan
            2 -> SolarAmber
            else -> NeonPink
        }
        PersonalFinanceGoalItem(
            id = "goal_${entry.id}",
            entryId = entry.id,
            title = name,
            categoryBadge = cat,
            savedDollars = savedDollars,
            targetDollars = target,
            accentColor = color
        )
    } else {
        val defaultTarget = maxOf(savedDollars * 2.0, 500.0)
        PersonalFinanceGoalItem(
            id = "goal_${entry.id}",
            entryId = entry.id,
            title = entry.title,
            categoryBadge = if (entry.category == "Goal") "Milestone Goal" else "Core Savings",
            savedDollars = savedDollars,
            targetDollars = defaultTarget,
            accentColor = if (entry.category == "Goal") HoloCyan else CyberEmerald
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalFinanceGoalsScreen(
    profile: UserProfile,
    budgetState: UiState<List<BudgetEntry>>,
    onAddGoalEntryToFirestore: (formattedTitle: String, category: String, depositDollars: Double) -> Unit,
    onDeleteGoalEntryFromFirestore: (entryId: String) -> Unit,
    onUpdateOverallSavingsGoalInFirestore: (targetDollars: Double) -> Unit,
    onShareMilestoneToWall: (title: String, content: String) -> Unit,
    isBiometricUnlocked: Boolean? = null,
    onBiometricUnlockSuccess: (() -> Unit)? = null,
    onBiometricLockVault: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var localUnlocked by remember { mutableStateOf(false) }
    val effectiveUnlocked = isBiometricUnlocked ?: localUnlocked
    val handleUnlock: () -> Unit = {
        localUnlocked = true
        onBiometricUnlockSuccess?.invoke()
    }
    val handleLock: () -> Unit = {
        localUnlocked = false
        onBiometricLockVault?.invoke()
    }

    var goalNameInput by remember { mutableStateOf("") }
    var goalTargetInput by remember { mutableStateOf("1200") }
    var goalDepositInput by remember { mutableStateOf("150") }
    var selectedCategoryLabel by remember { mutableStateOf(GOAL_CATEGORY_PRESETS.first().first) }
    var overallGoalInput by remember(profile.savingsGoalCents) {
        mutableStateOf(
            String.format(
                Locale.US,
                "%.0f",
                (profile.savingsGoalCents / 100.0).coerceAtLeast(1000.0)
            )
        )
    }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val allEntries = (budgetState as? UiState.Success)?.data.orEmpty()
    val balanceDollars = (profile.savingsBalanceCents / 100.0).coerceAtLeast(0.0)
    val overallGoalDollars = (profile.savingsGoalCents / 100.0).coerceAtLeast(100.0)
    val overallProgressFraction = (balanceDollars / overallGoalDollars).toFloat().coerceIn(0f, 1f)

    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallProgressFraction,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "overall_firestore_goal_progress"
    )

    // 4 Visual Milestone Stages derived from the user's Firestore savingsGoalCents & savingsBalanceCents
    val milestoneStages = remember(overallGoalDollars) {
        listOf(
            SavingsMilestoneStage(
                id = "stage_25",
                stageNumber = 1,
                title = "25% Starter Emergency Cushion",
                subtitle = "Covers unexpected urgent bills, car repairs, or textbook needs",
                fractionOfOverallGoal = 0.25,
                quickBoostDollars = 50.0,
                accent = CyberEmerald
            ),
            SavingsMilestoneStage(
                id = "stage_50",
                stageNumber = 2,
                title = "50% Halfway Stability Shield",
                subtitle = "Builds a resilient 1-month living & housing buffer",
                fractionOfOverallGoal = 0.50,
                quickBoostDollars = 100.0,
                accent = HoloCyan
            ),
            SavingsMilestoneStage(
                id = "stage_75",
                stageNumber = 3,
                title = "75% Career, School & Housing Accelerator",
                subtitle = "Supports FAFSA tuition, business equipment, or move-in deposits",
                fractionOfOverallGoal = 0.75,
                quickBoostDollars = 250.0,
                accent = SolarAmber
            ),
            SavingsMilestoneStage(
                id = "stage_100",
                stageNumber = 4,
                title = "100% Master Financial Goal Unlocked",
                subtitle = "Full personal finance target achieved & synced to your profile",
                fractionOfOverallGoal = 1.00,
                quickBoostDollars = 500.0,
                accent = NeonPink
            )
        )
    }

    // Build personal finance goals list from Firestore `budget_entries` (Goal + Save entries),
    // merged with curated starter goals so the screen is immediately rich and interactive.
    val firestoreGoalItems = remember(allEntries, balanceDollars) {
        val goalDocs = allEntries
            .filter { it.category == "Goal" || it.category == "Save" }
            .mapIndexed { idx, entry -> parseFirestoreGoalEntry(entry, idx) }

        val defaultStarterGoals = listOf(
            PersonalFinanceGoalItem(
                id = "starter_emergency",
                entryId = null,
                title = "Emergency Rainy-Day Reserve",
                categoryBadge = "Emergency Fund",
                savedDollars = (balanceDollars * 0.45).coerceIn(150.0, 1500.0),
                targetDollars = 1500.0,
                accentColor = CyberEmerald
            ),
            PersonalFinanceGoalItem(
                id = "starter_education",
                entryId = null,
                title = "College, FAFSA & STEM Laptop Fund",
                categoryBadge = "Education & FAFSA",
                savedDollars = (balanceDollars * 0.30).coerceIn(100.0, 1200.0),
                targetDollars = 1200.0,
                accentColor = HoloCyan
            ),
            PersonalFinanceGoalItem(
                id = "starter_housing",
                entryId = null,
                title = "Housing & Independence Milestone",
                categoryBadge = "Housing & Stability",
                savedDollars = (balanceDollars * 0.25).coerceIn(120.0, 2000.0),
                targetDollars = 2000.0,
                accentColor = SolarAmber
            )
        )
        goalDocs + defaultStarterGoals
    }

    FinancialBiometricProtectionGate(
        vaultTitle = "Personal Finance Goals & Savings Vault",
        vaultSubtitle = "Authenticate with Fingerprint or Face Unlock to view and manage your Firestore-synced savings balances, milestones, and financial tracking entries.",
        isUnlocked = effectiveUnlocked,
        onUnlockSuccess = handleUnlock,
        onLockVault = handleLock,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("personal_finance_goals_screen"),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 0. Monthly Financial Literacy Tracker (Monthly Savings & Expenses Input + Summary Card)
            FinancialLiteracyTrackerCard(
                budgetState = budgetState,
                onAddBudgetItem = onAddGoalEntryToFirestore,
                onDeleteBudgetItem = onDeleteGoalEntryFromFirestore,
                onShareSummaryToWall = onShareMilestoneToWall
            )

            // 1. Master Firestore-Synced Personal Finance Goal Hero Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.65f)),
                modifier = Modifier.testTag("firestore_master_goal_card")
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberEmerald.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = "Personal Finance Goals",
                                tint = CyberEmerald,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Personal Finance Goals & Savings Milestones",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Live Cloud Firestore Sync • Track savings milestones & visual progress bars",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = CyberEmerald.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Firestore Synced",
                                tint = CyberEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "FIRESTORE LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Overall Master Progress Bar Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Cloud Savings Balance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", balanceDollars),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Master Target Milestone",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", overallGoalDollars),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Multi-Gradient Visual Progress Bar with 25% / 50% / 75% / 100% Milestone Markers
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("master_savings_progress_bar")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedOverallProgress.coerceIn(0.03f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(CyberEmerald, HoloCyan, NeonPink)
                                        )
                                    )
                            )
                        }

                        // 25% / 50% / 75% / 100% Checkpoint Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                "0%" to 0f,
                                "25%" to 0.25f,
                                "50%" to 0.50f,
                                "75%" to 0.75f,
                                "100%" to 1.0f
                            ).forEach { (label, threshold) ->
                                val reached = overallProgressFraction >= threshold
                                Text(
                                    text = if (reached && threshold > 0f) "✓ $label" else label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (reached) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        // Quick Master Target Updater
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = overallGoalInput,
                                onValueChange = { overallGoalInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Master Savings Goal ($)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("master_goal_target_input")
                            )
                            Button(
                                onClick = {
                                    val newTarget = overallGoalInput.toDoubleOrNull()?.coerceIn(100.0, 1000000.0) ?: 5000.0
                                    onUpdateOverallSavingsGoalInFirestore(newTarget)
                                    statusBanner = "Updated Master Savings Goal to ${String.format(Locale.US, "$%,.0f", newTarget)} in Firestore!"
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloCyan,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("save_master_goal_button")
                            ) {
                                Text("Sync Target", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Visual Progress Bars for Tiered Savings Milestones (25%, 50%, 75%, 100%)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.55f)),
            modifier = Modifier.testTag("savings_milestones_progress_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Savings Milestones",
                        tint = SolarAmber
                    )
                    Column {
                        Text(
                            text = "Savings Milestone Checkpoints & Progress Bars",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Each milestone unlocks automatically as your Firestore savings balance grows.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                milestoneStages.forEach { stage ->
                    val stageTargetDollars = (overallGoalDollars * stage.fractionOfOverallGoal).coerceAtLeast(25.0)
                    val stageProgress = (balanceDollars / stageTargetDollars).toFloat().coerceIn(0f, 1f)
                    val isUnlocked = balanceDollars >= stageTargetDollars
                    val animatedStageProgress by animateFloatAsState(
                        targetValue = stageProgress,
                        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                        label = "stage_progress_${stage.id}"
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = if (isUnlocked) 1.5.dp else 1.dp,
                            color = if (isUnlocked) stage.accent else stage.accent.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("milestone_stage_card_${stage.stageNumber}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(stage.accent.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isUnlocked) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Unlocked",
                                                tint = stage.accent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "#${stage.stageNumber}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = stage.accent,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = stage.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stage.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    color = stage.accent.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (isUnlocked) {
                                            "UNLOCKED ✓"
                                        } else {
                                            String.format(Locale.US, "%.0f%%", stageProgress * 100f)
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = stage.accent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Visual Progress Bar for this Milestone
                            LinearProgressIndicator(
                                progress = { animatedStageProgress },
                                color = stage.accent,
                                trackColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .testTag("milestone_progress_bar_${stage.stageNumber}")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "$%,.0f / $%,.0f target",
                                        balanceDollars.coerceAtMost(stageTargetDollars),
                                        stageTargetDollars
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            val formatted = formatFirestoreGoalTitle(
                                                stage.title,
                                                "Milestone Boost",
                                                stageTargetDollars
                                            )
                                            onAddGoalEntryToFirestore(
                                                formatted,
                                                "Goal",
                                                stage.quickBoostDollars
                                            )
                                            statusBanner = "Deposited +$${stage.quickBoostDollars.toInt()} toward ${stage.title} in Firestore!"
                                        },
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, stage.accent),
                                        modifier = Modifier.testTag("milestone_boost_button_${stage.stageNumber}")
                                    ) {
                                        Text(
                                            text = "+$${stage.quickBoostDollars.toInt()} Boost",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = stage.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (isUnlocked) {
                                        OutlinedButton(
                                            onClick = {
                                                onShareMilestoneToWall(
                                                    "Unlocked Savings Milestone: ${stage.title}!",
                                                    "Reached ${String.format(Locale.US, "$%,.0f", stageTargetDollars)} in my Wondering World Personal Finance Goal Tracker! \"We are all equal\""
                                                )
                                            },
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = "Share Milestone",
                                                tint = HoloCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Create New Personal Finance Goal Card (Persists to Firestore `budget_entries`)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.55f)),
            modifier = Modifier.testTag("create_personal_finance_goal_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddTask,
                        contentDescription = "Create Finance Goal",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "Create New Personal Finance Goal",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Add a custom savings goal with a target amount and initial deposit synced to Firestore.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Category Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GOAL_CATEGORY_PRESETS.forEach { (catName, _) ->
                        FilterChip(
                            selected = selectedCategoryLabel == catName,
                            onClick = { selectedCategoryLabel = catName },
                            label = { Text(catName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = goalNameInput,
                    onValueChange = { goalNameInput = it },
                    label = { Text("Goal Name (e.g., Summer STEM Camp, Car Down Payment)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("finance_goal_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = goalTargetInput,
                        onValueChange = { goalTargetInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Target Goal ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.5f)
                            .testTag("finance_goal_target_input")
                    )
                    OutlinedTextField(
                        value = goalDepositInput,
                        onValueChange = { goalDepositInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Initial Deposit ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.5f)
                            .testTag("finance_goal_deposit_input")
                    )
                }

                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = statusBanner!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberEmerald,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        val target = goalTargetInput.toDoubleOrNull()?.coerceIn(50.0, 1000000.0) ?: 1000.0
                        val deposit = goalDepositInput.toDoubleOrNull()?.coerceIn(5.0, 1000000.0) ?: 100.0
                        val cleanName = goalNameInput.ifBlank { "$selectedCategoryLabel Goal" }
                        val formattedTitle = formatFirestoreGoalTitle(
                            goalName = cleanName,
                            categoryLabel = selectedCategoryLabel,
                            targetDollars = target
                        )
                        onAddGoalEntryToFirestore(formattedTitle, "Goal", deposit)
                        goalNameInput = ""
                        statusBanner = "Saved '$cleanName' ($${deposit.toInt()} / $${target.toInt()}) to Cloud Firestore!"
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("create_finance_goal_button")
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Goal & Deposit to Cloud Firestore", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Individual Personal Finance Goal Cards with Visual Progress Bars
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("individual_finance_goals_list_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Active Personal Finance Goals",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "Active Personal Finance Goals (${firestoreGoalItems.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Tap +$25 or +$100 on any goal to log a new Firestore contribution and advance its progress bar.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                firestoreGoalItems.forEachIndexed { idx, goal ->
                    val progress = (goal.savedDollars / goal.targetDollars.coerceAtLeast(1.0))
                        .toFloat()
                        .coerceIn(0f, 1f)
                    val animGoalProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        label = "individual_goal_progress_$idx"
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, goal.accentColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("personal_goal_item_$idx")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = goal.accentColor.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = goal.categoryBadge.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = goal.accentColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.US, "%.0f%%", progress * 100f),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = goal.accentColor,
                                        fontWeight = FontWeight.Black
                                    )
                                    if (goal.entryId != null) {
                                        IconButton(
                                            onClick = { onDeleteGoalEntryFromFirestore(goal.entryId) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete goal entry",
                                                tint = RosePulse,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Visual Progress Bar
                            LinearProgressIndicator(
                                progress = { animGoalProgress },
                                color = goal.accentColor,
                                trackColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .testTag("personal_goal_progress_bar_$idx")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Saved: $%,.0f / Target: $%,.0f",
                                        goal.savedDollars,
                                        goal.targetDollars
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = goal.accentColor.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, goal.accentColor.copy(alpha = 0.55f)),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier
                                            .clickable {
                                                val formatted = formatFirestoreGoalTitle(
                                                    goal.title,
                                                    goal.categoryBadge,
                                                    goal.targetDollars
                                                )
                                                onAddGoalEntryToFirestore(formatted, "Goal", 25.0)
                                                statusBanner = "Added +$25 to '${goal.title}' in Firestore!"
                                            }
                                            .testTag("goal_add_25_button_$idx")
                                    ) {
                                        Text(
                                            text = "+$25",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = goal.accentColor,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        color = CyberEmerald.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier
                                            .clickable {
                                                val formatted = formatFirestoreGoalTitle(
                                                    goal.title,
                                                    goal.categoryBadge,
                                                    goal.targetDollars
                                                )
                                                onAddGoalEntryToFirestore(formatted, "Goal", 100.0)
                                                statusBanner = "Added +$100 to '${goal.title}' in Firestore!"
                                            }
                                            .testTag("goal_add_100_button_$idx")
                                    ) {
                                        Text(
                                            text = "+$100",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
    }
}
