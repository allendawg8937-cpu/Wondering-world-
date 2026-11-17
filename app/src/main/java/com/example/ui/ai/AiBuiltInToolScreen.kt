package com.example.ui.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiBuiltInToolScreen(
    userDisplayName: String,
    userRole: String,
    onShareResultToWall: (title: String, body: String) -> Unit,
    modifier: Modifier = Modifier,
    toolViewModel: AiBuiltInToolViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedMode by toolViewModel.selectedMode.collectAsStateWithLifecycle()
    val uiState by toolViewModel.uiState.collectAsStateWithLifecycle()
    val history by toolViewModel.history.collectAsStateWithLifecycle()

    var promptText by rememberSaveable { mutableStateOf("") }
    var actionFeedback by rememberSaveable { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.65f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_built_in_tool_root")
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(HoloCyan, CyberEmerald))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Built-In AI Tool",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Stages Of Life • Built-In AI Tool",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Gemini 3.8 Flash • Streaming & Structured Action Plans for ${userRole.ifBlank { "Adult" }.uppercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tool Capability Selector Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BuiltInAiToolMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            toolViewModel.selectMode(mode)
                            actionFeedback = null
                        },
                        label = {
                            Text(
                                text = "${mode.label} (${mode.shortTag})",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                            selectedLabelColor = HoloCyan
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("ai_tool_mode_${mode.id}")
                    )
                }
            }

            // Active Tool Description + One-Tap Sample Fill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedMode.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = { promptText = selectedMode.samplePrompt },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("ai_tool_sample_prompt_btn")
                    ) {
                        Text("Use Example Prompt", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Prompt Input Field
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("Ask or paste content for ${selectedMode.label}") },
                placeholder = { Text(selectedMode.promptHint) },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_tool_prompt_input")
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        actionFeedback = null
                        toolViewModel.runTool(
                            inputPrompt = promptText,
                            userRole = userRole,
                            userDisplayName = userDisplayName,
                            mode = selectedMode
                        )
                    },
                    enabled = uiState !is AiBuiltInToolUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("ai_tool_run_button")
                ) {
                    if (uiState is AiBuiltInToolUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Running...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Run AI Tool",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Run ${selectedMode.label}", fontWeight = FontWeight.ExtraBold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        promptText = ""
                        actionFeedback = null
                        toolViewModel.resetState()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("ai_tool_clear_button")
                ) {
                    Text("Clear")
                }
            }

            // Output Area
            when (val state = uiState) {
                AiBuiltInToolUiState.Idle -> {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tool_idle_card")
                    ) {
                        Text(
                            text = "Select any AI tool above, enter a prompt or tap 'Use Example Prompt', and press Run to stream real Gemini 3.8 Flash output or a structured JSON Action Plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                AiBuiltInToolUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .testTag("ai_tool_loading_indicator")
                    ) {
                        CircularProgressIndicator(color = HoloCyan, modifier = Modifier.size(22.dp))
                        Text(
                            text = "Connecting to Firebase AI Logic (Gemini 3.8 Flash)...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HoloCyan
                        )
                    }
                }

                is AiBuiltInToolUiState.Streaming -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tool_streaming_output")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Streaming live output...",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.partialText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is AiBuiltInToolUiState.Success -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.2.dp, CyberEmerald.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tool_success_output")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val plan = state.structuredPlan
                            if (plan != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = plan.goalTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CyberEmerald,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = SolarAmber.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, SolarAmber)
                                    ) {
                                        Text(
                                            text = "${plan.priorityLevel} • ${plan.estimatedMinutes}m",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SolarAmber,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = plan.summary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                HorizontalDivider()
                                plan.steps.forEachIndexed { idx, step ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Step ${idx + 1}",
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "${idx + 1}. $step",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = state.outputText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        clipboard?.setPrimaryClip(
                                            ClipData.newPlainText("Stages Of Life AI Output", state.outputText)
                                        )
                                        actionFeedback = "Copied AI output to clipboard."
                                    },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("ai_tool_copy_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Output", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy")
                                }

                                OutlinedButton(
                                    onClick = {
                                        onShareResultToWall(
                                            "AI Tool: ${selectedMode.label}",
                                            state.outputText
                                        )
                                        actionFeedback = "Shared to Community Wall!"
                                    },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("ai_tool_share_button")
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share to Wall", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share to Wall")
                                }
                            }
                        }
                    }
                }

                is AiBuiltInToolUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RosePulse.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, RosePulse),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tool_error_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "AI Tool Error",
                                tint = RosePulse
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (actionFeedback != null) {
                Text(
                    text = actionFeedback!!,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            if (history.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Recent AI Tool Runs (${history.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPink,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    history.forEach { entry ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${entry.modeLabel} • ${entry.prompt.take(48)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = entry.preview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
