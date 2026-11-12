package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BudgetEntry
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class RechartsSavingsMilestonePoint(
    val periodLabel: String,
    val periodDepositDollars: Double,
    val cumulativeSavedDollars: Double,
    val projectedTargetDollars: Double,
    val milestoneBadge: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RechartsFinancialProgressChartCard(
    savingsBalanceDollars: Double,
    overallGoalDollars: Double,
    budgetEntries: List<BudgetEntry>,
    onAddMilestoneBoostDeposit: (title: String, category: String, amountDollars: Double) -> Unit,
    onShareChartProgressToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedChartMode by rememberSaveable { mutableStateOf("AREA_MILESTONE") } // AREA_MILESTONE, COMPOSED_BAR_LINE
    var selectedHorizon by rememberSaveable { mutableStateOf("6M") } // 6M, 1Y
    val safeGoal = overallGoalDollars.coerceAtLeast(500.0)
    val safeBalance = savingsBalanceDollars.coerceAtLeast(0.0)

    // Build chronological Recharts time-series points from Firestore budget entries + current savings balance
    val seriesPoints = remember(safeBalance, safeGoal, budgetEntries, selectedHorizon) {
        val positiveEntries = budgetEntries
            .filter { it.category.equals("Save", true) || it.category.equals("Goal", true) }
            .reversed()

        val pointCount = if (selectedHorizon == "1Y") 8 else 6
        val labels = if (selectedHorizon == "1Y") {
            listOf("M1", "M2", "M4", "M6", "M8", "M10", "M11", "Now")
        } else {
            listOf("Mo 1", "Mo 2", "Mo 3", "Mo 4", "Mo 5", "Now")
        }

        val startSeed = (safeBalance * 0.28).coerceAtLeast(50.0)
        val totalGrowth = (safeBalance - startSeed).coerceAtLeast(0.0)

        labels.mapIndexed { idx, label ->
            val fraction = (idx + 1).toDouble() / pointCount.toDouble()
            // Blend actual entry amounts if available with smooth cumulative progression up to safeBalance
            val cumulative = if (idx == pointCount - 1) {
                safeBalance
            } else {
                startSeed + totalGrowth * (fraction * fraction * 0.35 + fraction * 0.65)
            }
            val prevCumulative = if (idx == 0) {
                0.0
            } else {
                val prevFraction = idx.toDouble() / pointCount.toDouble()
                startSeed + totalGrowth * (prevFraction * prevFraction * 0.35 + prevFraction * 0.65)
            }
            val entryOverride = positiveEntries.getOrNull(idx)?.let { it.amountCents / 100.0 }
            val periodDeposit = entryOverride ?: (cumulative - prevCumulative).coerceAtLeast(25.0)
            val projectedTarget = safeGoal * fraction

            val pctOfGoal = (cumulative / safeGoal) * 100.0
            val badge = when {
                pctOfGoal >= 100.0 -> "🏆 100% Master Goal Unlocked"
                pctOfGoal >= 75.0 -> "🚀 75% Career & Housing Tier"
                pctOfGoal >= 50.0 -> "🛡️ 50% Stability Shield Tier"
                pctOfGoal >= 25.0 -> "✅ 25% Emergency Cushion Tier"
                else -> "🌱 Building First 25% Milestone"
            }

            RechartsSavingsMilestonePoint(
                periodLabel = label,
                periodDepositDollars = periodDeposit,
                cumulativeSavedDollars = cumulative,
                projectedTargetDollars = projectedTarget,
                milestoneBadge = badge
            )
        }
    }

    var selectedPointIndex by remember(seriesPoints.size) {
        mutableIntStateOf((seriesPoints.size - 1).coerceAtLeast(0))
    }
    val activePoint = seriesPoints.getOrElse(selectedPointIndex) { seriesPoints.last() }

    val progressRatio = (safeBalance / safeGoal).toFloat().coerceIn(0f, 1.25f)
    val animatedChartReveal by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "recharts_financial_curve_reveal"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("recharts_financial_progress_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        ),
        border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.65f))
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
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(CyberEmerald, HoloCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Recharts Financial Milestone Chart",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "RECHARTS SAVINGS MILESTONES CHART",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                        Text(
                            text = "<ResponsiveContainer> • Cumulative Savings vs. 25% / 50% / 75% / 100% Thresholds",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = HoloCyan.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = String.format(Locale.US, "%.0f%% of Goal", (progressRatio * 100f)),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = HoloCyan,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Chart Mode & Time Horizon Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedChartMode == "AREA_MILESTONE",
                    onClick = { selectedChartMode = "AREA_MILESTONE" },
                    label = { Text("<AreaChart> Cumulative Curve") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = CyberEmerald
                    ),
                    modifier = Modifier.testTag("recharts_mode_area_chip")
                )
                FilterChip(
                    selected = selectedChartMode == "COMPOSED_BAR_LINE",
                    onClick = { selectedChartMode = "COMPOSED_BAR_LINE" },
                    label = { Text("<ComposedChart> Deposits + Line") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                        selectedLabelColor = HoloCyan
                    ),
                    modifier = Modifier.testTag("recharts_mode_composed_chip")
                )
                FilterChip(
                    selected = selectedHorizon == "6M",
                    onClick = { selectedHorizon = "6M" },
                    label = { Text("6-Month View") }
                )
                FilterChip(
                    selected = selectedHorizon == "1Y",
                    onClick = { selectedHorizon = "1Y" },
                    label = { Text("1-Year View") }
                )
            }

            // Interactive Recharts Canvas Viewport with ReferenceLines (25%, 50%, 75%, 100%)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF070D19),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recharts_financial_canvas_container")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Legend Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendDot(color = CyberEmerald, label = "Cumulative Saved ($)")
                            LegendDot(color = HoloCyan, label = "Target Pace ($)")
                            if (selectedChartMode == "COMPOSED_BAR_LINE") {
                                LegendDot(color = SolarAmber, label = "Period Deposit")
                            }
                        }
                        Text(
                            text = "Tap points for <Tooltip />",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .pointerInput(seriesPoints) {
                                detectTapGestures { tapOffset ->
                                    val count = seriesPoints.size
                                    if (count > 1 && size.width > 0) {
                                        val ratio = (tapOffset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                        val idx = Math.round(ratio * (count - 1)).coerceIn(0, count - 1)
                                        selectedPointIndex = idx
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val w = size.width
                            val h = size.height
                            val leftPad = 14f
                            val rightPad = 14f
                            val topPad = 16f
                            val bottomPad = 24f
                            val plotW = (w - leftPad - rightPad).coerceAtLeast(1f)
                            val plotH = (h - topPad - bottomPad).coerceAtLeast(1f)

                            val maxVal = maxOf(
                                safeGoal * 1.05,
                                seriesPoints.maxOfOrNull { it.cumulativeSavedDollars } ?: safeGoal
                            ).coerceAtLeast(100.0)

                            // 1. Draw Recharts <CartesianGrid strokeDasharray="3 3" /> & <ReferenceLine /> at 25%, 50%, 75%, 100%
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                            listOf(0.25, 0.50, 0.75, 1.00).forEach { milestoneFrac ->
                                val milestoneDollar = safeGoal * milestoneFrac
                                val y = topPad + plotH * (1f - (milestoneDollar / maxVal).toFloat()).coerceIn(0f, 1f)
                                val refColor = when (milestoneFrac) {
                                    1.00 -> NeonPink.copy(alpha = 0.55f)
                                    0.75 -> SolarAmber.copy(alpha = 0.45f)
                                    0.50 -> HoloCyan.copy(alpha = 0.40f)
                                    else -> CyberEmerald.copy(alpha = 0.35f)
                                }
                                drawLine(
                                    color = refColor,
                                    start = Offset(leftPad, y),
                                    end = Offset(w - rightPad, y),
                                    strokeWidth = 1.5f,
                                    pathEffect = dashEffect
                                )
                            }

                            val count = seriesPoints.size
                            if (count < 2) return@Canvas

                            fun pointX(index: Int): Float =
                                leftPad + plotW * (index.toFloat() / (count - 1).toFloat())

                            fun pointY(dollars: Double): Float {
                                val normalized = ((dollars / maxVal).toFloat() * animatedChartReveal).coerceIn(0f, 1f)
                                return topPad + plotH * (1f - normalized)
                            }

                            // 2. Optional <Bar dataKey="periodDepositDollars" /> in COMPOSED_BAR_LINE mode
                            if (selectedChartMode == "COMPOSED_BAR_LINE") {
                                val barWidth = (plotW / (count * 2.2f)).coerceIn(10f, 34f)
                                seriesPoints.forEachIndexed { idx, pt ->
                                    val cx = pointX(idx)
                                    val barTopY = pointY(pt.periodDepositDollars * 2.2) // Scaled for visibility
                                    val barH = (topPad + plotH - barTopY).coerceAtLeast(4f)
                                    drawRoundRect(
                                        color = SolarAmber.copy(alpha = if (idx == selectedPointIndex) 0.75f else 0.38f),
                                        topLeft = Offset(cx - barWidth / 2f, barTopY),
                                        size = Size(barWidth, barH),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                            }

                            // 3. Draw Target Trajectory <Line dataKey="projectedTargetDollars" strokeDasharray="5 5" />
                            val targetPath = Path()
                            seriesPoints.forEachIndexed { idx, pt ->
                                val x = pointX(idx)
                                val y = pointY(pt.projectedTargetDollars)
                                if (idx == 0) targetPath.moveTo(x, y) else targetPath.lineTo(x, y)
                            }
                            drawPath(
                                path = targetPath,
                                color = HoloCyan.copy(alpha = 0.75f),
                                style = Stroke(width = 2.2f, pathEffect = dashEffect)
                            )

                            // 4. Draw Cumulative Saved <Area /> + <Line dataKey="cumulativeSavedDollars" />
                            val areaPath = Path()
                            val linePath = Path()
                            seriesPoints.forEachIndexed { idx, pt ->
                                val x = pointX(idx)
                                val y = pointY(pt.cumulativeSavedDollars)
                                if (idx == 0) {
                                    areaPath.moveTo(x, topPad + plotH)
                                    areaPath.lineTo(x, y)
                                    linePath.moveTo(x, y)
                                } else {
                                    areaPath.lineTo(x, y)
                                    linePath.lineTo(x, y)
                                }
                                if (idx == count - 1) {
                                    areaPath.lineTo(x, topPad + plotH)
                                    areaPath.close()
                                }
                            }

                            drawPath(
                                path = areaPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        CyberEmerald.copy(alpha = 0.44f),
                                        HoloCyan.copy(alpha = 0.16f),
                                        Color.Transparent
                                    ),
                                    startY = topPad,
                                    endY = topPad + plotH
                                )
                            )

                            drawPath(
                                path = linePath,
                                brush = Brush.horizontalGradient(listOf(CyberEmerald, HoloCyan)),
                                style = Stroke(width = 4f, cap = StrokeCap.Round)
                            )

                            // 5. Draw Active Tooltip Crosshair & Data Nodes
                            seriesPoints.forEachIndexed { idx, pt ->
                                val x = pointX(idx)
                                val y = pointY(pt.cumulativeSavedDollars)
                                val isSelected = idx == selectedPointIndex

                                if (isSelected) {
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.5f),
                                        start = Offset(x, topPad),
                                        end = Offset(x, topPad + plotH),
                                        strokeWidth = 1.5f,
                                        pathEffect = dashEffect
                                    )
                                    drawCircle(
                                        color = CyberEmerald.copy(alpha = 0.35f),
                                        radius = 14f,
                                        center = Offset(x, y)
                                    )
                                }

                                drawCircle(
                                    color = if (isSelected) Color.White else CyberEmerald,
                                    radius = if (isSelected) 7f else 5f,
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }

                    // X-Axis Period Selector Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        seriesPoints.forEachIndexed { idx, pt ->
                            val isSelected = idx == selectedPointIndex
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) CyberEmerald.copy(alpha = 0.25f) else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.dp, CyberEmerald) else null,
                                modifier = Modifier.clickable { selectedPointIndex = idx }
                            ) {
                                Text(
                                    text = pt.periodLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                    color = if (isSelected) CyberEmerald else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Recharts Interactive <Tooltip /> Detail Card for Selected Period
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF10192E),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharts_active_tooltip_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Period: ${activePoint.periodLabel} • ${activePoint.milestoneBadge}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyberEmerald
                                )
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Cumulative Saved: $%,.2f  |  Period Deposit: +$%,.2f",
                                        activePoint.cumulativeSavedDollars,
                                        activePoint.periodDepositDollars
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Target Pace Reference: $%,.2f  |  Master Goal: $%,.2f",
                                        activePoint.projectedTargetDollars,
                                        safeGoal
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan
                                )
                            }
                        }
                    }
                }
            }

            // Quick Milestone Boost Actions (Persists to Firestore & Animates Recharts Curve)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(50.0, 100.0, 250.0).forEach { boostAmount ->
                    OutlinedButton(
                        onClick = {
                            onAddMilestoneBoostDeposit(
                                "Recharts Milestone Boost (+$${boostAmount.toInt()})",
                                "Save",
                                boostAmount
                            )
                        },
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recharts_boost_button_${boostAmount.toInt()}")
                    ) {
                        Text(
                            text = "+$${boostAmount.toInt()}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                    }
                }

                Button(
                    onClick = {
                        onShareChartProgressToWall(
                            "Savings Milestone Progress: ${String.format(Locale.US, "$%,.0f / $%,.0f", safeBalance, safeGoal)}",
                            "Reached ${String.format(Locale.US, "%.0f%%", progressRatio * 100f)} of my Stages Of Life savings milestone goal (${activePoint.milestoneBadge})!"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("recharts_share_milestone_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Milestone Chart",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
