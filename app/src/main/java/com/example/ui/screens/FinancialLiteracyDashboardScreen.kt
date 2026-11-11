package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PieChart
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BudgetEntry
import com.example.data.model.UserProfile
import com.example.ui.UiState
import com.example.ui.components.FinancialBiometricProtectionGate
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class CategorySliceData(
    val category: String,
    val label: String,
    val amountDollars: Double,
    val color: Color,
    val tip: String
)

data class MonthlyTrajectoryPoint(
    val periodLabel: String,
    val savedDollars: Double,
    val spentDollars: Double,
    val cumulativeSavings: Double
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialLiteracyDashboardScreen(
    profile: UserProfile,
    budgetState: UiState<List<BudgetEntry>>,
    onAddBudgetItem: (String, String, Double) -> Unit,
    onDeleteBudgetItem: (String) -> Unit,
    onUpdateSavingsGoal: (Double) -> Unit,
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

    var selectedCategoryKey by remember { mutableStateOf("Save") }
    var selectedBarIndex by remember { mutableIntStateOf(5) }
    var entryTitle by remember { mutableStateOf("") }
    var entryAmountText by remember { mutableStateOf("75") }
    var entryCategory by remember { mutableStateOf("Save") }
    var customGoalText by remember {
        mutableStateOf(String.format(Locale.US, "%.0f", (profile.savingsGoalCents / 100.0).coerceAtLeast(1000.0)))
    }

    val entries = (budgetState as? UiState.Success)?.data.orEmpty()
    val balanceDollars = profile.savingsBalanceCents / 100.0
    val goalDollars = (profile.savingsGoalCents / 100.0).coerceAtLeast(100.0)
    val goalProgress = (balanceDollars / goalDollars).toFloat().coerceIn(0f, 1f)

    // Aggregate logged entries by category, with sensible baseline values so charts are always rich
    val rawSave = entries.filter { it.category == "Save" }.sumOf { it.amountCents } / 100.0
    val rawSpend = entries.filter { it.category == "Spend" }.sumOf { it.amountCents } / 100.0
    val rawGive = entries.filter { it.category == "Give" }.sumOf { it.amountCents } / 100.0
    val rawGoal = entries.filter { it.category == "Goal" }.sumOf { it.amountCents } / 100.0

    val saveTotal = if (rawSave > 0.0) rawSave else 850.0
    val spendTotal = if (rawSpend > 0.0) rawSpend else 620.0
    val giveTotal = if (rawGive > 0.0) rawGive else 140.0
    val goalTotal = if (rawGoal > 0.0) rawGoal else 490.0
    val grandTotal = (saveTotal + spendTotal + giveTotal + goalTotal).coerceAtLeast(1.0)

    val slices = listOf(
        CategorySliceData(
            category = "Save",
            label = "Emergency & Core Savings",
            amountDollars = saveTotal,
            color = CyberEmerald,
            tip = "Aim to allocate at least 20% of income toward liquid emergency savings."
        ),
        CategorySliceData(
            category = "Goal",
            label = "Goal Milestones (FAFSA / Biz / Housing)",
            amountDollars = goalTotal,
            color = HoloCyan,
            tip = "Dedicated milestone buckets accelerate long-term career and education stability."
        ),
        CategorySliceData(
            category = "Spend",
            label = "Essential Living & Monthly Bills",
            amountDollars = spendTotal,
            color = RosePulse,
            tip = "Keep essential spending below 50% and discretionary wants under 30%."
        ),
        CategorySliceData(
            category = "Give",
            label = "Community & Mutual Aid Giving",
            amountDollars = giveTotal,
            color = NeonPink,
            tip = "Planned giving strengthens community resilience without straining your core budget."
        )
    )

    val activeSlice = slices.find { it.category == selectedCategoryKey } ?: slices.first()
    val activePercentage = (activeSlice.amountDollars / grandTotal) * 100.0

    // 6-Period Trajectory Data for Composed Bar + Area Chart
    val trajectoryPoints = remember(balanceDollars, saveTotal, spendTotal, goalTotal) {
        val stepSave = (saveTotal + goalTotal) / 3.0
        val stepSpend = spendTotal / 3.0
        listOf(
            MonthlyTrajectoryPoint("M1", stepSave * 0.65, stepSpend * 0.80, (balanceDollars * 0.35).coerceAtLeast(200.0)),
            MonthlyTrajectoryPoint("M2", stepSave * 0.78, stepSpend * 0.75, (balanceDollars * 0.48).coerceAtLeast(450.0)),
            MonthlyTrajectoryPoint("M3", stepSave * 0.85, stepSpend * 0.90, (balanceDollars * 0.62).coerceAtLeast(750.0)),
            MonthlyTrajectoryPoint("M4", stepSave * 0.92, stepSpend * 0.70, (balanceDollars * 0.76).coerceAtLeast(1100.0)),
            MonthlyTrajectoryPoint("M5", stepSave * 1.05, stepSpend * 0.82, (balanceDollars * 0.89).coerceAtLeast(1600.0)),
            MonthlyTrajectoryPoint("Now", saveTotal + goalTotal, spendTotal, balanceDollars.coerceAtLeast(saveTotal + goalTotal))
        )
    }

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "recharts_canvas_anim"
    )

    FinancialBiometricProtectionGate(
        vaultTitle = "Financial Literacy & Analytics Tracker",
        vaultSubtitle = "Biometric authentication (Fingerprint or Face Unlock) is required to view your sensitive savings balance, spending breakdown, and Firestore budget entries.",
        isUnlocked = effectiveUnlocked,
        onUnlockSuccess = handleUnlock,
        onLockVault = handleLock,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("financial_literacy_dashboard"),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Top KPI Summary Banner
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SolarAmber.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Financial Literacy Analytics",
                            tint = SolarAmber
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Financial Literacy & Visual Analytics",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Interactive Recharts-style spending breakdown & savings goal trajectory",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 3 KPI Metric Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricTile(
                        label = "Saved Balance",
                        value = String.format(Locale.US, "$%,.0f", balanceDollars),
                        subtext = String.format(Locale.US, "%.0f%% of Goal", goalProgress * 100f),
                        accent = CyberEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricTile(
                        label = "Savings Target",
                        value = String.format(Locale.US, "$%,.0f", goalDollars),
                        subtext = "Active Milestone",
                        accent = HoloCyan,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricTile(
                        label = "Logged Outflow",
                        value = String.format(Locale.US, "$%,.0f", spendTotal + giveTotal),
                        subtext = "Spend + Give",
                        accent = RosePulse,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Overall Savings Goal Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Primary Savings Goal Progress",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = String.format(
                                Locale.US,
                                "$%,.2f / $%,.2f",
                                balanceDollars,
                                goalDollars
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = CyberEmerald
                        )
                    }
                    LinearProgressIndicator(
                        progress = { goalProgress * animProgress },
                        color = CyberEmerald,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        }

        // 1B. Animated Milestone Progress Bars (25%, 50%, 75%, 100%)
        com.example.ui.components.AnimatedSavingsMilestoneBars(
            currentBalanceDollars = balanceDollars,
            goalTargetDollars = goalDollars
        )

        // 2. Recharts-Style Interactive Spending & Allocation Donut Chart
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f))
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
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Spending Categories Chart",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "Spending & Allocation Categories (Donut Chart)",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Tap any slice or category pill to inspect its share and financial literacy tip.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Donut Chart Canvas + Center Callout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                        .testTag("recharts_spending_donut_chart"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(185.dp)
                            .pointerInput(slices) {
                                detectTapGestures { tapOffset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = tapOffset.x - center.x
                                    val dy = tapOffset.y - center.y
                                    var angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    angleDeg = (angleDeg + 90f + 360f) % 360f
                                    var runningAngle = 0f
                                    for (slice in slices) {
                                        val sweep = ((slice.amountDollars / grandTotal) * 360.0).toFloat()
                                        if (angleDeg >= runningAngle && angleDeg <= runningAngle + sweep) {
                                            selectedCategoryKey = slice.category
                                            break
                                        }
                                        runningAngle += sweep
                                    }
                                }
                            }
                    ) {
                        val diameter = size.minDimension * 0.82f
                        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                        val arcSize = Size(diameter, diameter)

                        var startAngle = -90f
                        slices.forEach { slice ->
                            val sweepAngle = ((slice.amountDollars / grandTotal) * 360.0).toFloat() * animProgress
                            val isSelected = slice.category == selectedCategoryKey
                            val strokeW = if (isSelected) 40f else 28f

                            drawArc(
                                color = slice.color.copy(alpha = if (isSelected) 1f else 0.72f),
                                startAngle = startAngle + 2f,
                                sweepAngle = (sweepAngle - 4f).coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeW, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Center Active Slice Callout
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = activeSlice.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = activeSlice.color
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.0f", activeSlice.amountDollars),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%%", activePercentage),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Recharts-style Tooltip Callout Banner for Selected Category
                Surface(
                    color = activeSlice.color.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, activeSlice.color.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeSlice.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = activeSlice.color
                            )
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "$%,.2f (%.1f%%)",
                                    activeSlice.amountDollars,
                                    activePercentage
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Coach Tip: ${activeSlice.tip}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Interactive Legend / Category Selector Grid
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEach { slice ->
                        val isSelected = slice.category == selectedCategoryKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryKey = slice.category },
                            label = {
                                Text(
                                    String.format(
                                        Locale.US,
                                        "%s: $%,.0f",
                                        slice.category,
                                        slice.amountDollars
                                    )
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(slice.color)
                                )
                            },
                            modifier = Modifier.testTag("chart_slice_chip_${slice.category.lowercase(Locale.ROOT)}")
                        )
                    }
                }
            }
        }

        // 3. Recharts-Style Composed Bar + Area Trajectory Chart (Savings Goals vs Spending)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f))
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
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Savings Trajectory Chart",
                        tint = CyberEmerald
                    )
                    Column {
                        Text(
                            text = "Savings Trajectory vs. Monthly Spending",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Recharts ComposedChart: Green bars = Saved, Coral bars = Spent, Cyan curve = Cumulative Savings.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val selectedPoint = trajectoryPoints.getOrElse(selectedBarIndex) { trajectoryPoints.last() }
                val chartGridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                val chartHighlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

                // Tooltip header for active period
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Period: ${selectedPoint.periodLabel}",
                            style = MaterialTheme.typography.titleMedium,
                            color = HoloCyan
                        )
                        Text(
                            text = String.format(Locale.US, "Saved: $%,.0f", selectedPoint.savedDollars),
                            style = MaterialTheme.typography.labelLarge,
                            color = CyberEmerald
                        )
                        Text(
                            text = String.format(Locale.US, "Spent: $%,.0f", selectedPoint.spentDollars),
                            style = MaterialTheme.typography.labelLarge,
                            color = RosePulse
                        )
                    }
                }

                // Bar + Area Chart Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 16.dp)
                        .testTag("recharts_savings_bar_area_chart")
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(trajectoryPoints) {
                                detectTapGestures { tapOffset ->
                                    val count = trajectoryPoints.size
                                    if (count > 0 && size.width > 0) {
                                        val index = ((tapOffset.x / size.width) * count)
                                            .toInt()
                                            .coerceIn(0, count - 1)
                                        selectedBarIndex = index
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val maxVal = maxOf(
                            goalDollars,
                            trajectoryPoints.maxOf { maxOf(it.savedDollars, it.spentDollars, it.cumulativeSavings) }
                        ).coerceAtLeast(500.0)

                        // Cartesian grid lines
                        val gridSteps = 4
                        for (i in 0..gridSteps) {
                            val y = h * (i.toFloat() / gridSteps.toFloat())
                            drawLine(
                                color = chartGridColor,
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1f
                            )
                        }

                        // Dashed Savings Goal Target ReferenceLine
                        val goalRatio = (goalDollars / maxVal).toFloat().coerceIn(0f, 1f)
                        val goalY = h - (goalRatio * h * 0.88f)
                        drawLine(
                            color = SolarAmber.copy(alpha = 0.85f),
                            start = Offset(0f, goalY),
                            end = Offset(w, goalY),
                            strokeWidth = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )

                        val slotWidth = w / trajectoryPoints.size.toFloat()
                        val barWidth = (slotWidth * 0.26f).coerceAtLeast(8f)

                        val linePath = Path()
                        val areaPath = Path()

                        trajectoryPoints.forEachIndexed { idx, pt ->
                            val centerX = slotWidth * (idx + 0.5f)
                            val isSelected = idx == selectedBarIndex

                            if (isSelected) {
                                drawRoundRect(
                                    color = chartHighlightColor,
                                    topLeft = Offset(slotWidth * idx + 4f, 0f),
                                    size = Size(slotWidth - 8f, h),
                                    cornerRadius = CornerRadius(10f, 10f)
                                )
                            }

                            // Saved Bar
                            val saveH = ((pt.savedDollars / maxVal).toFloat() * h * 0.85f * animProgress).coerceAtLeast(6f)
                            drawRoundRect(
                                color = CyberEmerald,
                                topLeft = Offset(centerX - barWidth - 2f, h - saveH),
                                size = Size(barWidth, saveH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            // Spent Bar
                            val spendH = ((pt.spentDollars / maxVal).toFloat() * h * 0.85f * animProgress).coerceAtLeast(6f)
                            drawRoundRect(
                                color = RosePulse,
                                topLeft = Offset(centerX + 2f, h - spendH),
                                size = Size(barWidth, spendH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            // Cumulative Savings Curve Point
                            val cumY = h - ((pt.cumulativeSavings / maxVal).toFloat() * h * 0.88f * animProgress)
                            if (idx == 0) {
                                linePath.moveTo(centerX, cumY)
                                areaPath.moveTo(centerX, h)
                                areaPath.lineTo(centerX, cumY)
                            } else {
                                linePath.lineTo(centerX, cumY)
                                areaPath.lineTo(centerX, cumY)
                            }
                            if (idx == trajectoryPoints.lastIndex) {
                                areaPath.lineTo(centerX, h)
                                areaPath.close()
                            }
                        }

                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    HoloCyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        drawPath(
                            path = linePath,
                            color = HoloCyan,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                    }
                }

                // Period Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    trajectoryPoints.forEachIndexed { idx, pt ->
                        FilterChip(
                            selected = selectedBarIndex == idx,
                            onClick = { selectedBarIndex = idx },
                            label = { Text(pt.periodLabel, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }

        // 4. Savings Goal Customizer & Milestones Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.45f))
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
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Savings Goals",
                        tint = SolarAmber
                    )
                    Column {
                        Text(
                            text = "Savings Goal Milestones & Target Setter",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Set your personal savings target or choose a recommended stability milestone.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1000.0 to "$1k Emergency",
                        5000.0 to "$5k Career/FAFSA",
                        10000.0 to "$10k Biz Seed"
                    ).forEach { (presetVal, label) ->
                        FilterChip(
                            selected = kotlin.math.abs(goalDollars - presetVal) < 1.0,
                            onClick = {
                                customGoalText = String.format(Locale.US, "%.0f", presetVal)
                                onUpdateSavingsGoal(presetVal)
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customGoalText,
                        onValueChange = { customGoalText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Custom Savings Target ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_savings_goal_input")
                    )
                    Button(
                        onClick = {
                            val target = customGoalText.toDoubleOrNull()?.coerceIn(100.0, 1000000.0) ?: 5000.0
                            onUpdateSavingsGoal(target)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("update_savings_goal_button")
                    ) {
                        Text("Set Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Log New Transaction & Live Ledger
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
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
                        imageVector = Icons.Default.AddChart,
                        contentDescription = "Log Budget Entry",
                        tint = SolarAmber
                    )
                    Text(
                        text = "Log Spending or Savings Activity",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Save", "Spend", "Give", "Goal").forEach { bucket ->
                        FilterChip(
                            selected = entryCategory == bucket,
                            onClick = { entryCategory = bucket },
                            label = { Text(bucket) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = entryTitle,
                        onValueChange = { entryTitle = it },
                        label = { Text("Entry Label") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.62f)
                            .testTag("budget_title_input")
                    )
                    OutlinedTextField(
                        value = entryAmountText,
                        onValueChange = { entryAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.38f)
                            .testTag("budget_amount_input")
                    )
                }

                Button(
                    onClick = {
                        val amount = entryAmountText.toDoubleOrNull() ?: 25.0
                        onAddBudgetItem(
                            entryTitle.ifBlank { "$entryCategory Jar Entry" },
                            entryCategory,
                            amount
                        )
                        selectedCategoryKey = entryCategory
                        entryTitle = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_budget_entry_button")
                ) {
                    Text("Log to Stability Jar & Update Charts", fontWeight = FontWeight.Bold)
                }

                if (entries.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        entries.take(6).forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = entry.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (entry.category in listOf("Save", "Goal")) CyberEmerald else SolarAmber
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", entry.amountCents / 100.0),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (entry.category in listOf("Save", "Goal")) CyberEmerald else RosePulse
                                    )
                                    IconButton(
                                        onClick = { onDeleteBudgetItem(entry.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove entry",
                                            modifier = Modifier.size(16.dp)
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

@Composable
private fun KpiMetricTile(
    label: String,
    value: String,
    subtext: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}
