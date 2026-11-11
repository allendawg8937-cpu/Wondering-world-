package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class SimulatedMarketAsset(
    val id: String,
    val ticker: String,
    val name: String,
    val emoji: String,
    val unitPriceUsd: Double,
    val change24hPercent: Double,
    val educationalNote: String,
    val accentHex: Color
)

data class SimulatedTradeOrder(
    val id: String,
    val side: String, // "BUY" or "SELL"
    val ticker: String,
    val units: Double,
    val totalVirtualUsd: Double,
    val timestampLabel: String
)

private val SIMULATED_MARKET_ASSETS = listOf(
    SimulatedMarketAsset(
        id = "eqc_index",
        ticker = "EQC-IDX",
        name = "EqualCoins Community Index (Paper)",
        emoji = "🪙",
        unitPriceUsd = 25.40,
        change24hPercent = 4.2,
        educationalNote = "Simulates a diversified digital index benchmark for practicing dollar-cost averaging (DCA).",
        accentHex = CyberEmerald
    ),
    SimulatedMarketAsset(
        id = "btc_sim",
        ticker = "BTC-SIM",
        name = "Digital Store-of-Value Benchmark (Sim)",
        emoji = "⚡",
        unitPriceUsd = 68450.00,
        change24hPercent = 2.8,
        educationalNote = "Educational paper tracker illustrating scarcity, volatility cycles, and position sizing.",
        accentHex = SolarAmber
    ),
    SimulatedMarketAsset(
        id = "eth_sim",
        ticker = "ETH-SIM",
        name = "Smart Contract Compute Index (Sim)",
        emoji = "💎",
        unitPriceUsd = 3420.00,
        change24hPercent = -1.4,
        educationalNote = "Demonstrates how network utility and gas fees affect programmable digital asset valuations.",
        accentHex = HoloCyan
    ),
    SimulatedMarketAsset(
        id = "sol_sim",
        ticker = "SOL-SIM",
        name = "High-Throughput Network Benchmark (Sim)",
        emoji = "🚀",
        unitPriceUsd = 178.50,
        change24hPercent = 6.5,
        educationalNote = "Highlights high-beta market swings and why stop-loss / risk caps matter in portfolio literacy.",
        accentHex = NeonPink
    )
)

/**
 * Google Play Creator Program-Compliant Alternative:
 * Educational Digital Asset & Market Literacy Portfolio Simulator.
 * Lets users practice simulated paper-portfolio buy & sell orders using virtual USD / EqualCoins
 * without connecting to real cryptocurrency wallets, blockchains, or token exchanges.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DigitalAssetMarketSimulatorCard(
    equalCoinsBalance: Long = 250L,
    onLogSimulatedBudgetPractice: (title: String, category: String, amountDollars: Double) -> Unit = { _, _, _ -> },
    onShareSimulationToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var virtualCashBalance by remember { mutableDoubleStateOf(10000.00) }
    val holdingsMap = remember {
        mutableStateMapOf(
            "EQC-IDX" to 12.0,
            "BTC-SIM" to 0.05,
            "ETH-SIM" to 0.40,
            "SOL-SIM" to 4.0
        )
    }
    var selectedAssetId by remember { mutableStateOf(SIMULATED_MARKET_ASSETS.first().id) }
    var tradeSide by remember { mutableStateOf("BUY") } // "BUY" or "SELL"
    var usdOrderAmountInput by remember { mutableStateOf("250") }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    val tradeHistory = remember {
        mutableStateListOf(
            SimulatedTradeOrder(
                id = "sim_1",
                side = "BUY",
                ticker = "EQC-IDX",
                units = 10.0,
                totalVirtualUsd = 254.0,
                timestampLabel = "Today • Paper Practice"
            ),
            SimulatedTradeOrder(
                id = "sim_2",
                side = "BUY",
                ticker = "BTC-SIM",
                units = 0.05,
                totalVirtualUsd = 3422.50,
                timestampLabel = "Yesterday • Paper Practice"
            )
        )
    }

    val selectedAsset = remember(selectedAssetId) {
        SIMULATED_MARKET_ASSETS.find { it.id == selectedAssetId } ?: SIMULATED_MARKET_ASSETS.first()
    }

    val totalHoldingsValueUsd = SIMULATED_MARKET_ASSETS.sumOf { asset ->
        val units = holdingsMap[asset.ticker] ?: 0.0
        units * asset.unitPriceUsd
    }
    val totalPortfolioValueUsd = virtualCashBalance + totalHoldingsValueUsd
    val parsedOrderUsd = usdOrderAmountInput.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val estimatedUnits = if (selectedAsset.unitPriceUsd > 0) parsedOrderUsd / selectedAsset.unitPriceUsd else 0.0

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(SolarAmber, CyberEmerald, HoloCyan, NeonPink)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("digital_asset_market_simulator_card")
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(SolarAmber, CyberEmerald))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Digital Asset Paper Simulator",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Digital Asset Portfolio Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Paper Buy & Sell Sandbox • Creator Program Compliant",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = SolarAmber.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = SolarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "PAPER SANDBOX",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Compliance & Education Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = HoloCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Practice simulated buy & sell orders with virtual paper funds and EqualCoins ($equalCoinsBalance EQC) to build market literacy risk-free.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Portfolio Summary Metrics
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VIRTUAL PORTFOLIO VALUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.2f", totalPortfolioValueUsd),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Paper Cash: ${String.format(Locale.US, "$%,.2f", virtualCashBalance)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simulated Assets: ${String.format(Locale.US, "$%,.2f", totalHoldingsValueUsd)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Asset Selector Chips
            Text(
                text = "1. Select Simulated Benchmark Asset",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = HoloCyan
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SIMULATED_MARKET_ASSETS.forEach { asset ->
                    val isSelected = asset.id == selectedAsset.id
                    val isPositive = asset.change24hPercent >= 0
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAssetId = asset.id },
                        label = {
                            Text(
                                text = "${asset.emoji} ${asset.ticker} • $${String.format(Locale.US, "%,.2f", asset.unitPriceUsd)} (${if (isPositive) "+" else ""}${asset.change24hPercent}%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = asset.accentHex.copy(alpha = 0.22f),
                            selectedLabelColor = asset.accentHex
                        ),
                        modifier = Modifier.testTag("sim_asset_chip_${asset.id}")
                    )
                }
            }

            // Selected Asset Detail & Holdings Card
            val currentUnits = holdingsMap[selectedAsset.ticker] ?: 0.0
            val currentHoldingUsd = currentUnits * selectedAsset.unitPriceUsd
            Surface(
                color = selectedAsset.accentHex.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, selectedAsset.accentHex.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedAsset.emoji} ${selectedAsset.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedAsset.change24hPercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (selectedAsset.change24hPercent >= 0) CyberEmerald else RosePulse,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${if (selectedAsset.change24hPercent >= 0) "+" else ""}${selectedAsset.change24hPercent}% (24h Sim)",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedAsset.change24hPercent >= 0) CyberEmerald else RosePulse,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Text(
                        text = selectedAsset.educationalNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Simulated Holding: ${String.format(Locale.US, "%.4f", currentUnits)} ${selectedAsset.ticker}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = selectedAsset.accentHex
                        )
                        Text(
                            text = "Value: ${String.format(Locale.US, "$%,.2f", currentHoldingUsd)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Buy / Sell Mode Toggle & Order Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = tradeSide == "BUY",
                    onClick = { tradeSide = "BUY" },
                    label = {
                        Text(
                            text = "🟢 Practice BUY",
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = CyberEmerald
                    ),
                    modifier = Modifier.testTag("sim_trade_side_buy")
                )
                FilterChip(
                    selected = tradeSide == "SELL",
                    onClick = { tradeSide = "SELL" },
                    label = {
                        Text(
                            text = "🟠 Practice SELL",
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SolarAmber.copy(alpha = 0.22f),
                        selectedLabelColor = SolarAmber
                    ),
                    modifier = Modifier.testTag("sim_trade_side_sell")
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Est: ${String.format(Locale.US, "%.4f", estimatedUnits)} ${selectedAsset.ticker}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = usdOrderAmountInput,
                    onValueChange = { usdOrderAmountInput = it },
                    label = { Text("Virtual Order Amount ($ USD)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sim_order_amount_input")
                )

                Button(
                    onClick = {
                        if (parsedOrderUsd <= 0.0) {
                            feedbackBanner = "Enter a valid virtual USD amount above $0."
                            return@Button
                        }
                        if (tradeSide == "BUY") {
                            if (parsedOrderUsd > virtualCashBalance) {
                                feedbackBanner = "Insufficient virtual paper cash ($${String.format(Locale.US, "%,.2f", virtualCashBalance)} available)."
                            } else {
                                virtualCashBalance -= parsedOrderUsd
                                val existingUnits = holdingsMap[selectedAsset.ticker] ?: 0.0
                                holdingsMap[selectedAsset.ticker] = existingUnits + estimatedUnits
                                tradeHistory.add(
                                    0,
                                    SimulatedTradeOrder(
                                        id = "sim_${System.currentTimeMillis()}",
                                        side = "BUY",
                                        ticker = selectedAsset.ticker,
                                        units = estimatedUnits,
                                        totalVirtualUsd = parsedOrderUsd,
                                        timestampLabel = "Just now • Paper Buy"
                                    )
                                )
                                onLogSimulatedBudgetPractice(
                                    "Paper Buy ${selectedAsset.ticker} (Sim)",
                                    "Save",
                                    parsedOrderUsd
                                )
                                feedbackBanner = "✅ Simulated BUY executed: +${String.format(Locale.US, "%.4f", estimatedUnits)} ${selectedAsset.ticker} ($${String.format(Locale.US, "%,.2f", parsedOrderUsd)} paper cash)."
                            }
                        } else {
                            val existingUnits = holdingsMap[selectedAsset.ticker] ?: 0.0
                            if (estimatedUnits > existingUnits + 1e-6) {
                                feedbackBanner = "You only hold ${String.format(Locale.US, "%.4f", existingUnits)} ${selectedAsset.ticker} in your paper portfolio."
                            } else {
                                holdingsMap[selectedAsset.ticker] = (existingUnits - estimatedUnits).coerceAtLeast(0.0)
                                virtualCashBalance += parsedOrderUsd
                                tradeHistory.add(
                                    0,
                                    SimulatedTradeOrder(
                                        id = "sim_${System.currentTimeMillis()}",
                                        side = "SELL",
                                        ticker = selectedAsset.ticker,
                                        units = estimatedUnits,
                                        totalVirtualUsd = parsedOrderUsd,
                                        timestampLabel = "Just now • Paper Sell"
                                    )
                                )
                                feedbackBanner = "✅ Simulated SELL executed: -${String.format(Locale.US, "%.4f", estimatedUnits)} ${selectedAsset.ticker} (+$${String.format(Locale.US, "%,.2f", parsedOrderUsd)} paper cash)."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tradeSide == "BUY") CyberEmerald else SolarAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("sim_execute_order_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (tradeSide == "BUY") "Sim Buy" else "Sim Sell",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Quick Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("50", "100", "250", "500", "1000").forEach { preset ->
                    OutlinedButton(
                        onClick = { usdOrderAmountInput = preset },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sim_preset_$preset")
                    ) {
                        Text(
                            text = "$$preset",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            feedbackBanner?.let { banner ->
                Surface(
                    color = CyberEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = banner,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Recent Paper Order Ledger & Share Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = HoloCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Recent Paper Practice Orders (${tradeHistory.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = HoloCyan
                    )
                }

                OutlinedButton(
                    onClick = {
                        val headline = "📊 Digital Asset Paper Portfolio: ${String.format(Locale.US, "$%,.2f", totalPortfolioValueUsd)}"
                        val body = "Practicing risk-free digital asset allocation in the WereEqual Market Literacy Sandbox! Current virtual portfolio value: ${String.format(Locale.US, "$%,.2f", totalPortfolioValueUsd)} across ${holdingsMap.count { it.value > 0 }} benchmark indices."
                        onShareSimulationToWall(headline, body)
                        feedbackBanner = "Shared your paper portfolio progress to the Community Wall!"
                    },
                    modifier = Modifier.testTag("sim_share_portfolio_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            tradeHistory.take(3).forEach { order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (order.side == "BUY") CyberEmerald else SolarAmber)
                        )
                        Text(
                            text = "${order.side} ${String.format(Locale.US, "%.4f", order.units)} ${order.ticker}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${String.format(Locale.US, "$%,.2f", order.totalVirtualUsd)} • ${order.timestampLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
