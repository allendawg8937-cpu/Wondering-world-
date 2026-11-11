package com.example.ui.components

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

/**
 * Financial Literacy Tracker component that allows users to input their monthly savings
 * and monthly expenses, visualizing the data with a simple, high-clarity summary card
 * alongside itemized budget entries synced with Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialLiteracyTrackerCard(
    budgetState: UiState<List<BudgetEntry>>,
    onAddBudgetItem: (title: String, category: String, amountDollars: Double) -> Unit,
    onDeleteBudgetItem: (entryId: String) -> Unit = {},
    onShareSummaryToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    userRole: String = "veteran",
    userAge: Long = 26L,
    equalCoinsBalance: Long = 250L,
    modifier: Modifier = Modifier
) {
    val entries = (budgetState as? UiState.Success)?.data.orEmpty()

    // Calculate logged savings ("Save", "Goal") and logged expenses ("Spend", "Give") from entries
    val loggedSavingsDollars = remember(entries) {
        entries.filter { it.category.equals("Save", ignoreCase = true) || it.category.equals("Goal", ignoreCase = true) }
            .sumOf { it.amountCents / 100.0 }
    }
    val loggedExpensesDollars = remember(entries) {
        entries.filter { it.category.equals("Spend", ignoreCase = true) || it.category.equals("Give", ignoreCase = true) }
            .sumOf { it.amountCents / 100.0 }
    }

    // Direct Monthly Savings & Monthly Expenses inputs for instant summary visualization
    var monthlySavingsInput by remember { mutableStateOf("1250") }
    var monthlyExpensesInput by remember { mutableStateOf("820") }
    var customEntryTitle by remember { mutableStateOf("") }
    var customEntryAmount by remember { mutableStateOf("") }
    var customEntryType by remember { mutableStateOf("Save") } // "Save" or "Spend"
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Effective totals combine direct monthly inputs with any newly logged items or allow direct override
    val parsedSavingsInput = monthlySavingsInput.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val parsedExpensesInput = monthlyExpensesInput.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0

    val totalMonthlySavings = parsedSavingsInput
    val totalMonthlyExpenses = parsedExpensesInput
    val netMonthlyBalance = totalMonthlySavings - totalMonthlyExpenses
    val totalMonthlyVolume = (totalMonthlySavings + totalMonthlyExpenses).coerceAtLeast(1.0)
    val savingsRatio = (totalMonthlySavings / totalMonthlyVolume).toFloat().coerceIn(0f, 1f)
    val expensesRatio = (1f - savingsRatio).coerceIn(0f, 1f)
    val savingsRatePercent = (savingsRatio * 100f).toInt()

    val animatedSavingsRatio by animateFloatAsState(
        targetValue = savingsRatio,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "monthly_savings_ratio_anim"
    )

    val literacyTip = remember(savingsRatePercent, netMonthlyBalance) {
        when {
            savingsRatePercent >= 50 -> "🏆 Elite Saver (50%+): Your monthly savings exceed your expenses—accelerating long-term freedom!"
            savingsRatePercent >= 20 -> "✅ Healthy 50/30/20 Rule (20%+): You are hitting the gold-standard financial literacy savings benchmark."
            netMonthlyBalance >= 0.0 -> "🌱 Positive Monthly Cash Flow: Try boosting monthly savings by $50–$100 to reach the 20% buffer target."
            else -> "⚠️ Monthly Expenses Exceed Savings: Review recurring expenses below to restore a positive monthly surplus."
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(CyberEmerald, HoloCyan, SolarAmber, NeonPink)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("financial_literacy_tracker_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Row
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(CyberEmerald, HoloCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Financial Literacy Tracker",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Financial Literacy Tracker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Input monthly savings & expenses • Instant visual summary card",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "$savingsRatePercent% Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 2. SIMPLE VISUAL SUMMARY CARD (Visualizing Monthly Savings, Monthly Expenses & Net Cash Flow)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.2.dp,
                    color = if (netMonthlyBalance >= 0) CyberEmerald.copy(alpha = 0.65f) else RosePulse.copy(alpha = 0.65f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("financial_literacy_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 MONTHLY SUMMARY OVERVIEW",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (netMonthlyBalance >= 0) "SURPLUS ACTIVE" else "DEFICIT ALERT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (netMonthlyBalance >= 0) CyberEmerald else RosePulse,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // 3 Metric Boxes: Monthly Savings | Monthly Expenses | Net Monthly Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Monthly Savings Metric
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Savings",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "$%,.0f", totalMonthlySavings),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.testTag("summary_monthly_savings_value")
                                )
                                Text(
                                    text = "$savingsRatePercent% of flow",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Monthly Expenses Metric
                        Surface(
                            color = RosePulse.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.55f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = RosePulse,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Expenses",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RosePulse,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "$%,.0f", totalMonthlyExpenses),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.testTag("summary_monthly_expenses_value")
                                )
                                Text(
                                    text = "${100 - savingsRatePercent}% of flow",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Net Balance Metric
                        val netColor = if (netMonthlyBalance >= 0) HoloCyan else SolarAmber
                        Surface(
                            color = netColor.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, netColor.copy(alpha = 0.55f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = netColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Net Flow",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = netColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                val prefix = if (netMonthlyBalance >= 0) "+" else "-"
                                Text(
                                    text = "$prefix$${String.format(Locale.US, "%,.0f", kotlin.math.abs(netMonthlyBalance))}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = netColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.testTag("summary_net_balance_value")
                                )
                                Text(
                                    text = if (netMonthlyBalance >= 0) "Monthly net" else "Over budget",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Visual Split Bar: Savings (Emerald) vs Expenses (RosePulse)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(50))
                                .background(RosePulse.copy(alpha = 0.75f))
                        ) {
                            if (animatedSavingsRatio > 0.01f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(animatedSavingsRatio.coerceIn(0.05f, 1f))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(CyberEmerald, HoloCyan)
                                            )
                                        )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🟢 Savings ($savingsRatePercent%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "🔴 Expenses (${(expensesRatio * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = RosePulse,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Financial Literacy Coaching Tip
                    Text(
                        text = literacyTip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 2B. Financial Tip of the Day (Persona-Aware Daily Snippet)
            FinancialTipOfTheDayCard(
                userRole = userRole,
                userAge = userAge,
                onApplyTipSavingsAction = { title, amountDollars ->
                    onAddBudgetItem(title, "Save", amountDollars)
                    statusMessage = "✅ Applied Financial Tip of the Day: +$${String.format(Locale.US, "%,.0f", amountDollars)} to $title!"
                },
                onShareTipToWall = onShareSummaryToWall
            )

            // 2C. Educational Digital Asset & Market Literacy Simulator (Compliant Paper Buy/Sell Sandbox)
            DigitalAssetMarketSimulatorCard(
                equalCoinsBalance = equalCoinsBalance,
                onLogSimulatedBudgetPractice = { title, category, amountDollars ->
                    onAddBudgetItem(title, category, amountDollars)
                },
                onShareSimulationToWall = onShareSummaryToWall
            )

            // 3. Direct Monthly Savings & Monthly Expenses Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = monthlySavingsInput,
                    onValueChange = { value ->
                        monthlySavingsInput = value.filter { it.isDigit() || it == '.' }.take(9)
                        statusMessage = null
                    },
                    label = { Text("Monthly Savings ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("monthly_savings_input")
                )

                OutlinedTextField(
                    value = monthlyExpensesInput,
                    onValueChange = { value ->
                        monthlyExpensesInput = value.filter { it.isDigit() || it == '.' }.take(9)
                        statusMessage = null
                    },
                    label = { Text("Monthly Expenses ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("monthly_expenses_input")
                )
            }

            // Quick Monthly Scenario Presets + Sync to Cloud Button
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "🎓 Student ($450 / $310)" to ("450" to "310"),
                    "🛡️ Veteran ($1,600 / $1,050)" to ("1600" to "1050"),
                    "💼 Family ($2,400 / $1,750)" to ("2400" to "1750"),
                    "🚀 Sync Logged Vault ($${loggedSavingsDollars.toInt()} / $${loggedExpensesDollars.toInt()})" to (
                        loggedSavingsDollars.toInt().coerceAtLeast(100).toString() to
                            loggedExpensesDollars.toInt().coerceAtLeast(50).toString()
                        )
                ).forEach { (label, pair) ->
                    FilterChip(
                        selected = monthlySavingsInput == pair.first && monthlyExpensesInput == pair.second,
                        onClick = {
                            monthlySavingsInput = pair.first
                            monthlyExpensesInput = pair.second
                            statusMessage = "Updated monthly summary card to $label"
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (parsedSavingsInput > 0) {
                            onAddBudgetItem("Monthly Savings Target", "Save", parsedSavingsInput)
                        }
                        if (parsedExpensesInput > 0) {
                            onAddBudgetItem("Monthly Living Expenses", "Spend", parsedExpensesInput)
                        }
                        statusMessage = "Synced $$monthlySavingsInput savings & $$monthlyExpensesInput expenses to your Cloud Vault!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_monthly_summary_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Monthly Totals",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        onShareSummaryToWall(
                            "📊 My Monthly Financial Literacy Summary",
                            "Monthly Savings: $${String.format(Locale.US, "%,.0f", totalMonthlySavings)} • Monthly Expenses: $${String.format(Locale.US, "%,.0f", totalMonthlyExpenses)} • Savings Rate: $savingsRatePercent% (#WereEqual Financial Literacy)"
                        )
                        statusMessage = "Shared your Financial Literacy Summary Card to the Community Wall!"
                    },
                    border = BorderStroke(1.dp, HoloCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("share_financial_summary_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Summary",
                        tint = HoloCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.labelMedium,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

            // 4. Add Individual Monthly Saving or Expense Item
            Text(
                text = "➕ Log Individual Monthly Saving or Expense Item",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = customEntryType == "Save",
                    onClick = { customEntryType = "Save" },
                    label = { Text("🟢 Monthly Saving", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("chip_entry_type_save")
                )
                FilterChip(
                    selected = customEntryType == "Spend",
                    onClick = { customEntryType = "Spend" },
                    label = { Text("🔴 Monthly Expense", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("chip_entry_type_spend")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customEntryTitle,
                    onValueChange = { customEntryTitle = it.take(60) },
                    label = {
                        Text(
                            if (customEntryType == "Save") "Saving Item (e.g. Emergency Fund)" else "Expense Item (e.g. Rent / Groceries)"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("custom_budget_item_title_input")
                )

                OutlinedTextField(
                    value = customEntryAmount,
                    onValueChange = { customEntryAmount = it.filter { ch -> ch.isDigit() || ch == '.' }.take(8) },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("custom_budget_item_amount_input")
                )

                Button(
                    onClick = {
                        val amount = customEntryAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0.0) {
                            val defaultLabel = if (customEntryType == "Save") "Monthly Savings Deposit" else "Monthly Expense"
                            val finalTitle = customEntryTitle.trim().ifBlank { defaultLabel }
                            onAddBudgetItem(finalTitle, customEntryType, amount)
                            if (customEntryType == "Save") {
                                monthlySavingsInput = (parsedSavingsInput + amount).toInt().toString()
                            } else {
                                monthlyExpensesInput = (parsedExpensesInput + amount).toInt().toString()
                            }
                            customEntryTitle = ""
                            customEntryAmount = ""
                            statusMessage = "Added $finalTitle ($$amount) to Monthly ${if (customEntryType == "Save") "Savings" else "Expenses"}!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (customEntryType == "Save") CyberEmerald else RosePulse,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_custom_budget_item_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Add Item",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (statusMessage != null) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = statusMessage.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Recent Logged Items Preview
            if (entries.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Recent Logged Savings & Expenses (${entries.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    entries.take(4).forEach { entry ->
                        val isSaving = entry.category.equals("Save", ignoreCase = true) ||
                            entry.category.equals("Goal", ignoreCase = true)
                        val displayTitle = entry.title.substringBefore("|")
                        val itemDollars = entry.amountCents / 100.0
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
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
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSaving) CyberEmerald else RosePulse)
                                    )
                                    Text(
                                        text = displayTitle,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${if (isSaving) "+" else "-"}$${String.format(Locale.US, "%,.0f", itemDollars)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSaving) CyberEmerald else RosePulse,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    IconButton(
                                        onClick = { onDeleteBudgetItem(entry.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Entry",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Dynamic 'Spending History' Bar Chart Dashboard (Currency Expenditures Over Time)
            SpendingHistoryBarChartDashboardCard(
                budgetEntries = entries,
                currentMonthlyExpenses = totalMonthlyExpenses,
                currentMonthlySavings = totalMonthlySavings,
                onLogQuickExpenditure = { title, amt ->
                    onAddBudgetItem(title, "Spend", amt)
                }
            )
        }
    }
}
