package com.example.ui.components

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntry
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class SpendingHistoryBarPoint(
    val periodLabel: String,
    val usdSpent: Double,
    val eqcCoinsSpent: Long,
    val savingsAdded: Double,
    val topCategory: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpendingHistoryBarChartDashboardCard(
    budgetEntries: List<BudgetEntry>,
    currentMonthlyExpenses: Double,
    currentMonthlySavings: Double,
    onLogQuickExpenditure: (title: String, amountDollars: Double) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var timeRangeMode by remember { mutableStateOf("6M_TREND") } // "6M_TREND", "WEEKLY", "CATEGORIES"
    var currencyMetricMode by remember { mutableStateOf("BOTH") } // "BOTH", "USD_ONLY", "EQC_ONLY"
    var selectedBarIndex by remember { mutableIntStateOf(5) }

    val loggedSpendItems = remember(budgetEntries) {
        budgetEntries.filter {
            it.category.equals("Spend", ignoreCase = true) ||
                it.category.equals("Give", ignoreCase = true)
        }
    }
    val extraLoggedSpend = remember(loggedSpendItems) {
        loggedSpendItems.sumOf { it.amountCents / 100.0 }
    }

    val customAddedPoints = remember { mutableStateListOf<Pair<String, Double>>() }
    val customAddedTotal = customAddedPoints.sumOf { it.second }

    val activePoints: List<SpendingHistoryBarPoint> = remember(
        timeRangeMode,
        currentMonthlyExpenses,
        currentMonthlySavings,
        extraLoggedSpend,
        customAddedTotal
    ) {
        val currentSpend = (currentMonthlyExpenses + extraLoggedSpend * 0.25 + customAddedTotal).coerceAtLeast(120.0)
        val currentSave = currentMonthlySavings.coerceAtLeast(150.0)
        when (timeRangeMode) {
            "WEEKLY" -> listOf(
                SpendingHistoryBarPoint("Wk 1", (currentSpend * 0.22), 45L, currentSave * 0.25, "Groceries & Dining"),
                SpendingHistoryBarPoint("Wk 2", (currentSpend * 0.28), 75L, currentSave * 0.22, "Housing & Utilities"),
                SpendingHistoryBarPoint("Wk 3", (currentSpend * 0.21), 60L, currentSave * 0.27, "3D Profile Themes"),
                SpendingHistoryBarPoint("Wk 4", (currentSpend * 0.29), 95L, currentSave * 0.26, "Education & Peer Tips")
            )
            "CATEGORIES" -> listOf(
                SpendingHistoryBarPoint("Housing", currentSpend * 0.36, 40L, currentSave * 0.30, "Essentials & Utilities"),
                SpendingHistoryBarPoint("Food", currentSpend * 0.24, 55L, currentSave * 0.20, "Groceries & Delivery"),
                SpendingHistoryBarPoint("Study/Tech", currentSpend * 0.18, 80L, currentSave * 0.25, "Books, STEM & AI"),
                SpendingHistoryBarPoint("3D Themes", currentSpend * 0.12, 110L, currentSave * 0.15, "Virtual Items & Flair"),
                SpendingHistoryBarPoint("Peer Tips", currentSpend * 0.10, 85L, currentSave * 0.10, "Community Rewards")
            )
            else -> listOf(
                SpendingHistoryBarPoint("Jun", (currentSpend * 0.84), 110L, currentSave * 0.78, "Housing & Books"),
                SpendingHistoryBarPoint("Jul", (currentSpend * 0.92), 145L, currentSave * 0.82, "Summer Travel & Food"),
                SpendingHistoryBarPoint("Aug", (currentSpend * 0.79), 95L, currentSave * 0.90, "School & Tech Setup"),
                SpendingHistoryBarPoint("Sep", (currentSpend * 0.88), 160L, currentSave * 0.86, "3D Themes & Supplies"),
                SpendingHistoryBarPoint("Oct", (currentSpend * 0.76), 130L, currentSave * 0.95, "Emergency Buffer"),
                SpendingHistoryBarPoint("Nov", currentSpend, 185L + (customAddedTotal.toLong() / 2L), currentSave, "Current Month Active")
            )
        }
    }

    val safeSelectedIdx = selectedBarIndex.coerceIn(0, (activePoints.size - 1).coerceAtLeast(0))
    val selectedPoint = activePoints.getOrNull(safeSelectedIdx) ?: activePoints.last()

    val maxUsdValue = remember(activePoints) {
        (activePoints.maxOfOrNull { maxOf(it.usdSpent, it.savingsAdded) } ?: 1000.0).coerceAtLeast(200.0) * 1.15
    }
    val maxEqcValue = remember(activePoints) {
        (activePoints.maxOfOrNull { it.eqcCoinsSpent } ?: 200L).coerceAtLeast(100L) * 1.2f
    }

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "spending_history_bar_chart_anim"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0B1120).copy(alpha = 0.92f),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, RosePulse, SolarAmber, CyberEmerald))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_history_bar_chart_dashboard")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(RosePulse, SolarAmber))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Spending History Bar Chart",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "RECHARTS SPENDING HISTORY DASHBOARD",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Dynamic Currency Expenditures Over Time",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = RosePulse.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, RosePulse)
                ) {
                    Text(
                        text = String.format(Locale.US, "$%,.0f / mo", selectedPoint.usdSpent),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFECDD3),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // View Mode Filter Chips (6-Month Trend | Weekly | By Category)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("6M_TREND", "📅 6-Month History", "chart_mode_6m"),
                    Triple("WEEKLY", "📊 Weekly", "chart_mode_weekly"),
                    Triple("CATEGORIES", "🏷️ By Category", "chart_mode_categories")
                ).forEach { (key, label, tag) ->
                    val selected = timeRangeMode == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) HoloCyan.copy(alpha = 0.25f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            1.dp,
                            if (selected) HoloCyan else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                timeRangeMode = key
                                selectedBarIndex = 0
                            }
                            .testTag(tag)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) Color.White else Color(0xFFCBD5E1),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            // Series Toggle Legend (USD Expenditures | EqualCoins Spent | Savings Benchmark)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("BOTH", "All Series (USD + EQC + Savings)", HoloCyan),
                    Triple("USD_ONLY", "💵 USD Spending ($)", RosePulse),
                    Triple("EQC_ONLY", "🪙 EqualCoins Spent (EQC)", SolarAmber)
                ).forEach { (mode, label, color) ->
                    val active = currencyMetricMode == mode
                    FilterChip(
                        selected = active,
                        onClick = { currencyMetricMode = mode },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.25f),
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            // Interactive Bar Chart Canvas Area
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spending_history_bar_chart_canvas_container")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Y-Axis Top Reference Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format(Locale.US, "Max Scale: $%,.0f USD", maxUsdValue),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Tap any bar group to inspect details",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🪙 ${maxEqcValue.toInt()} EQC",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontSize = 10.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .pointerInput(activePoints) {
                                detectTapGestures { offset ->
                                    if (activePoints.isNotEmpty() && size.width > 0) {
                                        val slotWidth = size.width.toFloat() / activePoints.size
                                        val tappedIdx = (offset.x / slotWidth).toInt()
                                            .coerceIn(0, activePoints.size - 1)
                                        selectedBarIndex = tappedIdx
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                            // Horizontal Cartesian Grid Lines (25%, 50%, 75%, 100%)
                            for (i in 1..4) {
                                val y = h - (h * (i / 4f))
                                drawLine(
                                    color = Color.White.copy(alpha = 0.10f),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = dashEffect
                                )
                            }

                            val count = activePoints.size.coerceAtLeast(1)
                            val groupWidth = w / count

                            activePoints.forEachIndexed { idx, point ->
                                val groupLeft = idx * groupWidth
                                val isSelected = idx == safeSelectedIdx

                                // Highlight backdrop for active bar group
                                if (isSelected) {
                                    drawRoundRect(
                                        color = Color.White.copy(alpha = 0.08f),
                                        topLeft = Offset(groupLeft + 4.dp.toPx(), 0f),
                                        size = Size(groupWidth - 8.dp.toPx(), h),
                                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                    )
                                }

                                val usdRatio = ((point.usdSpent / maxUsdValue).toFloat().coerceIn(0.06f, 1f)) * animProgress
                                val eqcRatio = ((point.eqcCoinsSpent.toFloat() / maxEqcValue).coerceIn(0.06f, 1f)) * animProgress
                                val saveRatio = ((point.savingsAdded / maxUsdValue).toFloat().coerceIn(0.06f, 1f)) * animProgress

                                when (currencyMetricMode) {
                                    "USD_ONLY" -> {
                                        val barW = (groupWidth * 0.48f).coerceAtLeast(10f)
                                        val barX = groupLeft + (groupWidth - barW) / 2f
                                        val barH = h * usdRatio
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(RosePulse, Color(0xFFBE123C))
                                            ),
                                            topLeft = Offset(barX, h - barH),
                                            size = Size(barW, barH),
                                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                        )
                                    }
                                    "EQC_ONLY" -> {
                                        val barW = (groupWidth * 0.48f).coerceAtLeast(10f)
                                        val barX = groupLeft + (groupWidth - barW) / 2f
                                        val barH = h * eqcRatio
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(SolarAmber, Color(0xFFB45309))
                                            ),
                                            topLeft = Offset(barX, h - barH),
                                            size = Size(barW, barH),
                                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                        )
                                    }
                                    else -> {
                                        val singleBarW = (groupWidth * 0.22f).coerceAtLeast(6f)
                                        val spacing = 3.dp.toPx()
                                        val totalBarsW = singleBarW * 3 + spacing * 2
                                        val startX = groupLeft + (groupWidth - totalBarsW) / 2f

                                        // Bar 1: USD Expenditure (RosePulse)
                                        val usdH = h * usdRatio
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(RosePulse, Color(0xFFBE123C))
                                            ),
                                            topLeft = Offset(startX, h - usdH),
                                            size = Size(singleBarW, usdH),
                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                        )

                                        // Bar 2: EqualCoins Expenditure (SolarAmber)
                                        val eqcH = h * eqcRatio
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(SolarAmber, Color(0xFFD97706))
                                            ),
                                            topLeft = Offset(startX + singleBarW + spacing, h - eqcH),
                                            size = Size(singleBarW, eqcH),
                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                        )

                                        // Bar 3: Monthly Savings Benchmark (CyberEmerald)
                                        val saveH = h * saveRatio
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                listOf(CyberEmerald, Color(0xFF047857))
                                            ),
                                            topLeft = Offset(startX + (singleBarW + spacing) * 2, h - saveH),
                                            size = Size(singleBarW, saveH),
                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // X-Axis Period Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        activePoints.forEachIndexed { idx, pt ->
                            val isSelected = idx == safeSelectedIdx
                            Text(
                                text = pt.periodLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) HoloCyan else Color(0xFFCBD5E1),
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedBarIndex = idx }
                            )
                        }
                    }

                    // Color Legend Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendDot(color = RosePulse, label = "USD Spent ($)")
                        Spacer(modifier = Modifier.width(12.dp))
                        LegendDot(color = SolarAmber, label = "EqualCoins Spent (EQC)")
                        Spacer(modifier = Modifier.width(12.dp))
                        LegendDot(color = CyberEmerald, label = "Savings ($)")
                    }
                }
            }

            // Recharts-Style Interactive Tooltip Card for the Selected Bar Group
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.90f),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spending_history_selected_tooltip")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📌 ${selectedPoint.periodLabel} • ${selectedPoint.topCategory}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = String.format(
                                Locale.US,
                                "USD Spent: $%,.0f • EqualCoins Spent: 🪙 %d EQC • Saved: $%,.0f",
                                selectedPoint.usdSpent,
                                selectedPoint.eqcCoinsSpent,
                                selectedPoint.savingsAdded
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick 1-Tap Log Expenditure to See Real-Time Bar Chart Update
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Log Spend:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Bold
                )
                listOf(
                    "📚 Study & Tech ($45)" to 45.0,
                    "🍎 Groceries ($65)" to 65.0,
                    "🎨 Theme Boost ($25)" to 25.0
                ).forEach { (label, amt) ->
                    Button(
                        onClick = {
                            customAddedPoints.add(label to amt)
                            selectedBarIndex = (activePoints.size - 1).coerceAtLeast(0)
                            onLogQuickExpenditure(label, amt)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFCBD5E1),
            fontSize = 10.sp
        )
    }
}
