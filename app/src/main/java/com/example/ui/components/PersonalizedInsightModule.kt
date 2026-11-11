package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.PersonalizedDailyInsight
import com.example.data.model.UserInteraction
import com.example.ui.DashboardTab
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizedInsightModuleCard(
    displayName: String,
    interactionsState: UiState<List<UserInteraction>>,
    insightsState: UiState<List<PersonalizedDailyInsight>>,
    onGenerateGeminiInsights: () -> Unit,
    onLogInteraction: (String, String, String) -> Unit,
    onDeleteInteraction: (String) -> Unit,
    onExecuteInsightAction: (PersonalizedDailyInsight, DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInteractionHistory by remember { mutableStateOf(true) }
    var customDetailText by remember { mutableStateOf("") }
    var selectedActionType by remember { mutableStateOf("StudySession") }

    val interactions = (interactionsState as? UiState.Success)?.data.orEmpty()
    val insights = (insightsState as? UiState.Success)?.data.orEmpty()
    val isGenerating = insightsState is UiState.Loading

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, NeonPink, CyberEmerald, SolarAmber)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.2f)
            .testTag("personalized_insight_module_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Personalized Insight Title + Firestore + Gemini Badges
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
                                Brush.linearGradient(listOf(HoloCyan, NeonPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Personalized Insight Module",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Personalized Insight Module",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                color = CyberEmerald.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${interactions.size} Logged",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Firestore Interaction Store + Gemini Daily Engagement Engine for ${displayName.ifBlank { "You" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan
                        )
                    }
                }
            }

            // Primary Action: Generate Daily Engagement-Driving Suggestions via Gemini API
            Button(
                onClick = onGenerateGeminiInsights,
                enabled = !isGenerating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_gemini_insights_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini Analyzing ${interactions.size} Firestore Interactions…",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✨ Generate Daily Gemini Engagement Insights",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (insightsState is UiState.Error) {
                Surface(
                    color = SolarAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = insightsState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = SolarAmber,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Daily Engagement-Driving Suggestion Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("personalized_insights_list")
            ) {
                insights.forEachIndexed { index, insight ->
                    val cardAccent = when (index % 4) {
                        0 -> HoloCyan
                        1 -> NeonPink
                        2 -> CyberEmerald
                        else -> SolarAmber
                    }
                    val targetTab = resolveDashboardTab(insight.targetTabName)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.2.dp, cardAccent.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("personalized_insight_item_${insight.id}")
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
                                Surface(
                                    color = cardAccent.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "◈ ${insight.category.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cardAccent,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = SolarAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${insight.impactScore}% Engagement Match",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = insight.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = insight.suggestion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onExecuteInsightAction(insight, targetTab)
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, cardAccent),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("insight_action_button_${insight.id}")
                                ) {
                                    Text(
                                        text = insight.actionLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cardAccent,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = cardAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick 1-Tap Firestore Interaction Logger Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log User Interaction to Firestore:",
                    style = MaterialTheme.typography.labelLarge,
                    color = HoloCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (showInteractionHistory) "Hide Log (${interactions.size})" else "Show Log (${interactions.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showInteractionHistory = !showInteractionHistory }
                        .testTag("toggle_interaction_history_button")
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("MusicPlay", "Played 6D Synth Anthem in Jukebox", "Music"),
                    Triple("SportSelect", "Checked Live Chosen Sports Scoreboard", "Sports"),
                    Triple("StudySession", "Completed AI STEM & Flashcard Session", "Learning"),
                    Triple("GoalLog", "Updated Personal Finance Savings Milestone", "Finance")
                ).forEach { (type, detail, cat) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable { onLogInteraction(type, detail, cat) }
                            .testTag("quick_log_interaction_${type.lowercase()}")
                    ) {
                        Text(
                            text = "+ $cat",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Collapsible Firestore User Interaction Stream + Custom Logger
            AnimatedVisibility(visible = showInteractionHistory) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "StudySession" to "Study",
                            "MusicPlay" to "Music",
                            "SportSelect" to "Sports",
                            "GoalLog" to "Finance",
                            "FeatureVisit" to "Explore"
                        ).forEach { (typeKey, label) ->
                            FilterChip(
                                selected = selectedActionType == typeKey,
                                onClick = { selectedActionType = typeKey },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customDetailText,
                            onValueChange = { customDetailText = it },
                            label = { Text("Record interaction (e.g., Researched FAFSA Grants)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_custom_interaction_input")
                        )
                        Button(
                            onClick = {
                                val trimmed = customDetailText.trim()
                                if (trimmed.isNotEmpty()) {
                                    onLogInteraction(selectedActionType, trimmed, selectedActionType)
                                    customDetailText = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("log_custom_interaction_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Log Interaction")
                        }
                    }

                    // Recent Interactions Stored in Firestore
                    interactions.take(4).forEach { interaction ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Column {
                                        Text(
                                            text = interaction.detail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${interaction.actionType} • ${interaction.category}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (interaction.id.isNotBlank()) {
                                    IconButton(
                                        onClick = { onDeleteInteraction(interaction.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Interaction",
                                            tint = RosePulse,
                                            modifier = Modifier.size(15.dp)
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

private fun resolveDashboardTab(tabName: String): DashboardTab {
    val normalized = tabName.uppercase()
    return when {
        normalized.contains("NEWS") || normalized.contains("SPORT") -> DashboardTab.NEWS_SPORTS
        normalized.contains("THEME") || normalized.contains("AVATAR") || normalized.contains("MUSIC") -> DashboardTab.THEME_PROFILE
        normalized.contains("RESOURCE") || normalized.contains("AID") || normalized.contains("LIBRARY") -> DashboardTab.RESOURCES
        normalized.contains("AI") || normalized.contains("STUDY") -> DashboardTab.AI_TOOLS
        normalized.contains("STUDIO") || normalized.contains("ARCADE") || normalized.contains("GAME") -> DashboardTab.STUDIO_ARCADE
        normalized.contains("GOAL") || normalized.contains("FINANCE_GOALS") -> DashboardTab.FINANCE_GOALS
        normalized.contains("FINANCE") || normalized.contains("LOUNGE") -> DashboardTab.STABILITY_LOUNGE
        normalized.contains("IDEA") || normalized.contains("CONDUCT") -> DashboardTab.CODE_OF_CONDUCT
        else -> DashboardTab.HUB_3D
    }
}
