package com.example.ui.components

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.AiFlashcardItem
import com.example.ui.AiTextUiState
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AdaptiveAiBotCard"
private const val AI_LEARNING_PREFS = "stages_of_life_ai_learning_prefs"
private const val KEY_LEARNED_TOPICS_CSV = "learned_research_topics_csv"

data class AiBotChatMessage(
    val id: String,
    val sender: String, // "USER" or "AI_BOT"
    val modeLabel: String,
    val text: String,
    val learnedTakeaway: String? = null
)

data class SpecializedAiToolSpec(
    val id: String,
    val title: String,
    val subtitle: String,
    val promptPlaceholder: String,
    val systemInstruction: String,
    val accentColor: Color,
    val badgeEmoji: String
)

private val SPECIALIZED_AI_TOOLS = listOf(
    SpecializedAiToolSpec(
        id = "deep_research_bot",
        title = "AI Deep Research Bot",
        subtitle = "Multi-angle research synthesis calibrated to your Stage of Life & learned profile",
        promptPlaceholder = "Enter any topic, question, science concept, financial strategy, or history subject...",
        systemInstruction = "Provide a structured Deep Research Synthesis with: 1) Executive Summary tailored to the user's life stage, 2) Key Facts & Evidence, 3) Step-by-Step Action Plan, and 4) Recommended Public Library / Academic Search Keywords.",
        accentColor = HoloCyan,
        badgeEmoji = "🔬"
    ),
    SpecializedAiToolSpec(
        id = "summarizer_notes",
        title = "AI Summarizer & Key Takeaways",
        subtitle = "Condense articles, study notes, policies, or long documents into clear bullet points",
        promptPlaceholder = "Paste an article, chapter notes, policy text, or complex concept to summarize...",
        systemInstruction = "Summarize the user's input into: 1) Core Thesis in 1 sentence, 2) 5 High-Impact Bullet Takeaways, 3) Key Vocabulary / Definitions, and 4) Next Actionable Step.",
        accentColor = CyberEmerald,
        badgeEmoji = "📝"
    ),
    SpecializedAiToolSpec(
        id = "career_grant_mentor",
        title = "AI Career, Resume & Grant Mentor",
        subtitle = "Build resume bullets, scholarship essays, SBA pitches, or Veteran transition plans",
        promptPlaceholder = "Describe your target job, scholarship, business idea, or military-to-civilian skill...",
        systemInstruction = "Act as an expert Career, Scholarship & Grant Coach. Craft high-impact resume bullet points, interview talking points, and a 30-day milestone roadmap tailored to the user's persona.",
        accentColor = SolarAmber,
        badgeEmoji = "💼"
    ),
    SpecializedAiToolSpec(
        id = "code_math_solver",
        title = "AI STEM, Coding & Formula Lab",
        subtitle = "Explain math equations, debug code snippets, or design science projects step by step",
        promptPlaceholder = "Enter a math problem, coding question, or STEM project idea...",
        systemInstruction = "Explain the STEM, math, or coding problem step-by-step with clear examples, intuition, and a practice challenge.",
        accentColor = NeonPink,
        badgeEmoji = "💻"
    ),
    SpecializedAiToolSpec(
        id = "life_stage_decision_matrix",
        title = "AI Pros/Cons & Decision Matrix",
        subtitle = "Compare colleges, budgets, career offers, or business choices with weighted criteria",
        promptPlaceholder = "What two or more options are you comparing? (e.g., Community College vs University)...",
        systemInstruction = "Build a clear Decision Matrix comparing the options across Cost, Long-Term Growth, Time Commitment, and Alignment with the user's learned interests and schedule.",
        accentColor = HoloCyan,
        badgeEmoji = "⚖️"
    ),
    SpecializedAiToolSpec(
        id = "flashcard_quiz_maker",
        title = "AI Flashcards & Adaptive Blueprint",
        subtitle = "Generate structured study flashcards or a 4-part Adaptive Daily Blueprint",
        promptPlaceholder = "Enter a study subject for 3 AI Flashcards (e.g., Cellular Respiration, US Constitution)...",
        systemInstruction = "Create an interactive study guide and quiz questions with memory mnemonics tailored to the user's grade and stage of life.",
        accentColor = CyberEmerald,
        badgeEmoji = "🎓"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdaptiveUserLearningAiBotAndToolsCard(
    userDisplayName: String,
    userRole: String,
    userAge: Long,
    userLocation: String,
    learnedSignals: List<String>,
    learnedInterests: Set<String>,
    learnedLifestyle: Set<String>,
    learnedSchedule: Set<String>,
    learnedResearch: Set<String>,
    aiStudyState: AiTextUiState,
    aiFlashcardsState: UiState<List<AiFlashcardItem>>,
    aiAdaptivePlanState: AiTextUiState,
    onRecordLearnedSignal: (String) -> Unit,
    onAddCustomLearningSignal: (category: String, value: String) -> Unit,
    onToggleLearnedInterest: (String) -> Unit,
    onToggleLearnedResearch: (String) -> Unit,
    onAskAiStudyTutor: (topic: String, subject: String, studyMode: String, gradeLevel: String) -> Unit,
    onGenerateStudyFlashcards: (topic: String, gradeLevel: String) -> Unit,
    onGenerateAdaptiveLearningPlan: () -> Unit,
    onShareAiResearchToWall: (title: String, body: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember(context) {
        context.getSharedPreferences(AI_LEARNING_PREFS, Context.MODE_PRIVATE)
    }

    // Lazy initialization of Firebase AI Logic Gemini 3.8 Flash model per firebase-ai-logic-android skill
    val researchBotModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    var persistedTopicsCsv by rememberSaveable {
        mutableStateOf(
            prefs.getString(
                KEY_LEARNED_TOPICS_CSV,
                "STEM & Space,Financial Literacy,Career Growth,Community Wellness"
            ) ?: "STEM & Space,Financial Literacy,Career Growth,Community Wellness"
        )
    }

    val localLearnedTopics = remember(persistedTopicsCsv) {
        persistedTopicsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct()
    }

    fun appendLearnedTopicLocally(newTopic: String) {
        val clean = newTopic.trim().take(42)
        if (clean.isEmpty()) return
        val updated = (listOf(clean) + localLearnedTopics).distinct().take(12)
        val csv = updated.joinToString(",")
        persistedTopicsCsv = csv
        prefs.edit().putString(KEY_LEARNED_TOPICS_CSV, csv).apply()
        onRecordLearnedSignal("Researched: $clean")
    }

    var activeSectionTab by rememberSaveable { mutableStateOf("AI_RESEARCH_BOT") } // AI_RESEARCH_BOT, AI_TOOLS_SUITE, USER_LEARNING_MEMORY
    var selectedToolId by rememberSaveable { mutableStateOf(SPECIALIZED_AI_TOOLS.first().id) }
    var selectedResearchDepth by rememberSaveable { mutableStateOf("Deep Synthesis") }
    var userPromptInput by rememberSaveable { mutableStateOf("") }
    var customMemorySignalInput by rememberSaveable { mutableStateOf("") }
    var selectedMemoryCategory by rememberSaveable { mutableStateOf("Research") }

    var botGenerationState by remember { mutableStateOf<AiTextUiState>(AiTextUiState.Idle) }
    val chatHistory = remember {
        mutableStateListOf(
            AiBotChatMessage(
                id = "welcome_msg",
                sender = "AI_BOT",
                modeLabel = "Adaptive Persona Memory Active",
                text = "Hello $userDisplayName! I am your Stages Of Life AI Research Bot & Tool Assistant. I continuously learn from your ${userRole.uppercase()} persona, interests (${learnedInterests.take(3).joinToString(", ")}), schedule, and research history to tailor every answer. What would you like to research or build today?",
                learnedTakeaway = "Synced ${learnedSignals.size + localLearnedTopics.size} active user-learning signals"
            )
        )
    }

    val currentTool = remember(selectedToolId) {
        SPECIALIZED_AI_TOOLS.firstOrNull { it.id == selectedToolId } ?: SPECIALIZED_AI_TOOLS.first()
    }

    fun executeAiResearchOrTool(queryText: String, tool: SpecializedAiToolSpec, depthMode: String) {
        val cleanQuery = queryText.trim()
        if (cleanQuery.isEmpty()) return

        // 1. Teach the platform from this user query immediately
        appendLearnedTopicLocally(cleanQuery)
        onAddCustomLearningSignal("Research", cleanQuery.take(36))

        chatHistory.add(
            AiBotChatMessage(
                id = "user_${System.currentTimeMillis()}",
                sender = "USER",
                modeLabel = "${tool.title} • $depthMode",
                text = cleanQuery,
                learnedTakeaway = "Platform learned new interest signal: \"${cleanQuery.take(32)}\""
            )
        )

        // Also trigger structured flashcards if the Flashcard tool is selected
        if (tool.id == "flashcard_quiz_maker") {
            onGenerateStudyFlashcards(cleanQuery, "$userRole (Age $userAge)")
        }

        val fullAdaptivePrompt = """
            You are the Stages Of Life Adaptive AI Research Bot & AI Power Tool Engine ("We are all equal").
            The platform actively learns from the user to personalize research and tools.
            
            [LEARNED USER PROFILE CONTEXT]
            - Name: $userDisplayName
            - Stage of Life Persona: ${userRole.uppercase()} (Age $userAge)
            - Location: $userLocation
            - Learned Interests: ${learnedInterests.joinToString(", ")}
            - Learned Lifestyle: ${learnedLifestyle.joinToString(", ")}
            - Learned Daily Schedule: ${learnedSchedule.joinToString(", ")}
            - Learned Research History: ${(learnedResearch + localLearnedTopics).distinct().take(8).joinToString(", ")}
            - Recent Platform Activity Signals: ${learnedSignals.take(6).joinToString(" • ")}
            
            [ACTIVE AI TOOL & DEPTH]
            - Selected Tool: ${tool.title}
            - Research Depth / Format: $depthMode
            - Tool Directive: ${tool.systemInstruction}
            
            [USER REQUEST / RESEARCH TOPIC]
            $cleanQuery
            
            Provide a thorough, well-structured, accurate response tailored specifically to $userDisplayName's ${userRole.uppercase()} life stage and learned interests. End with 1 sentence titled "What the Platform Learned & Next Suggested Research:" recommending a follow-up topic.
        """.trimIndent()

        coroutineScope.launch {
            botGenerationState = AiTextUiState.Loading
            val globalOpId = GlobalLoadingTracker.beginRequest(
                title = "Fetching ${tool.title} Response...",
                subtitle = "Streaming adaptive Gemini AI research for '$cleanQuery'",
                category = "Gemini AI API"
            )
            val accumulated = StringBuilder()
            try {
                withTimeout(60_000L) {
                    researchBotModel.generateContentStream(fullAdaptivePrompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulated.append(chunkText)
                        if (accumulated.length >= 24) {
                            GlobalLoadingTracker.endRequest(globalOpId)
                        }
                        botGenerationState = AiTextUiState.Streaming(accumulated.toString())
                    }
                }
                val finalAnswer = accumulated.toString().trim()
                if (finalAnswer.isNotEmpty()) {
                    botGenerationState = AiTextUiState.Success(finalAnswer)
                    chatHistory.add(
                        AiBotChatMessage(
                            id = "ai_${System.currentTimeMillis()}",
                            sender = "AI_BOT",
                            modeLabel = "${tool.badgeEmoji} ${tool.title} ($depthMode)",
                            text = finalAnswer,
                            learnedTakeaway = "Adapted response to ${userRole.uppercase()} profile & saved topic to memory"
                        )
                    )
                } else {
                    botGenerationState = AiTextUiState.Error("Model returned an empty response. Please try again.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Research Bot timed out", e)
                botGenerationState = AiTextUiState.Error("AI Research Bot timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Research Bot generation failed", e)
                botGenerationState = AiTextUiState.Error(
                    e.localizedMessage ?: "AI Research generation failed."
                )
            } finally {
                GlobalLoadingTracker.endRequest(globalOpId)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("adaptive_user_learning_ai_bot_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        ),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header
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
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(HoloCyan, NeonPink, CyberEmerald)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "User-Learning AI Research Bot",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ADAPTIVE AI RESEARCH BOT & AI TOOLS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Platform learns from your interests, persona & research in real time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Learned Signals Count",
                            tint = CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${learnedSignals.size + localLearnedTopics.size} Learned Signals",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Live "What the Platform Has Learned About You" Adaptive Telemetry Strip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧠 Live User-Learning Memory (${userRole.uppercase()} • Age $userAge)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Adapts AI Prompts Automatically",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (localLearnedTopics + learnedSignals).distinct().take(10).forEach { signal ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = HoloCyan.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f)),
                                modifier = Modifier.clickable {
                                    userPromptInput = signal
                                }
                            ) {
                                Text(
                                    text = "⚡ $signal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3-Mode Workspace Selector: 1) AI Research Bot, 2) 6 AI Power Tools, 3) Train User-Learning Engine
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "AI_RESEARCH_BOT" to "🤖 AI Research Bot",
                    "AI_TOOLS_SUITE" to "🧰 6 AI Power Tools",
                    "USER_LEARNING_MEMORY" to "🧠 Platform Learning Memory"
                ).forEach { (tabId, tabTitle) ->
                    val selected = activeSectionTab == tabId
                    FilterChip(
                        selected = selected,
                        onClick = { activeSectionTab = tabId },
                        label = {
                            Text(
                                text = tabTitle,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoloCyan.copy(alpha = 0.24f),
                            selectedLabelColor = HoloCyan
                        ),
                        modifier = Modifier.testTag("ai_hub_mode_tab_${tabId.lowercase()}")
                    )
                }
            }

            // SECTION 2: 6 Specialized AI Power Tools Grid (visible when AI_TOOLS_SUITE is active or as quick selector)
            if (activeSectionTab == "AI_TOOLS_SUITE") {
                Text(
                    text = "Select an AI Power Tool (Calibrated to your Learned Profile)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                SPECIALIZED_AI_TOOLS.chunked(2).forEach { rowTools ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowTools.forEach { tool ->
                            val isSelected = tool.id == selectedToolId
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) {
                                    tool.accentColor.copy(alpha = 0.20f)
                                } else {
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                },
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) tool.accentColor else tool.accentColor.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedToolId = tool.id
                                        onRecordLearnedSignal("Opened Tool: ${tool.title}")
                                    }
                                    .testTag("select_ai_power_tool_${tool.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = tool.badgeEmoji,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = tool.title,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = tool.accentColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = tool.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick One-Tap Adaptive Daily Blueprint Generator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onGenerateAdaptiveLearningPlan() },
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("generate_adaptive_blueprint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Generate Adaptive Daily Blueprint",
                            tint = CyberEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Synthesize 4-Part Daily Blueprint",
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // SECTION 3: Train & Inspect User-Learning Memory Engine
            if (activeSectionTab == "USER_LEARNING_MEMORY") {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Teach the Platform New Interests, Schedule or Research Goals",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Research", "Interests", "Lifestyle", "Schedule").forEach { cat ->
                                FilterChip(
                                    selected = selectedMemoryCategory == cat,
                                    onClick = { selectedMemoryCategory = cat },
                                    label = { Text("Category: $cat") }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customMemorySignalInput,
                                onValueChange = { customMemorySignalInput = it },
                                label = {
                                    Text("Add $selectedMemoryCategory signal (e.g., Renewable Energy, Evening Study)...")
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("teach_platform_signal_input")
                            )
                            Button(
                                onClick = {
                                    val clean = customMemorySignalInput.trim()
                                    if (clean.isNotEmpty()) {
                                        onAddCustomLearningSignal(selectedMemoryCategory, clean)
                                        appendLearnedTopicLocally(clean)
                                        customMemorySignalInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("teach_platform_signal_submit")
                            ) {
                                Text("Teach AI", fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Text(
                            text = "Tap any topic below to toggle or launch instant AI research:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val allInterestsAndResearch = (
                                learnedInterests +
                                    learnedResearch +
                                    localLearnedTopics +
                                    listOf(
                                        "AI & Robotics Research",
                                        "FAFSA & Scholarships",
                                        "VA Healthcare & GI Bill",
                                        "Small Business Grants",
                                        "3D Graphics & Coding",
                                        "Personal Finance & Budgeting"
                                    )
                                ).distinct()

                            allInterestsAndResearch.forEach { topic ->
                                val isSaved = learnedInterests.contains(topic) ||
                                    learnedResearch.contains(topic) ||
                                    localLearnedTopics.contains(topic)
                                FilterChip(
                                    selected = isSaved,
                                    onClick = {
                                        onToggleLearnedResearch(topic)
                                        appendLearnedTopicLocally(topic)
                                        userPromptInput = topic
                                    },
                                    label = { Text(topic) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberEmerald.copy(alpha = 0.20f),
                                        selectedLabelColor = CyberEmerald
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // AI Research Bot & Active Tool Prompt Controls (Always Accessible)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, currentTool.accentColor.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentTool.badgeEmoji} Active Mode: ${currentTool.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = currentTool.accentColor
                        )
                        Text(
                            text = "Gemini 3.8 Flash",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Research Depth Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Deep Synthesis",
                            "Explain for ${userRole.replaceFirstChar { it.uppercase() }}",
                            "Step-by-Step Guide",
                            "Pros & Cons Matrix",
                            "Quick 5-Bullet Summary"
                        ).forEach { depth ->
                            val selected = selectedResearchDepth == depth
                            FilterChip(
                                selected = selected,
                                onClick = { selectedResearchDepth = depth },
                                label = { Text(depth) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentTool.accentColor.copy(alpha = 0.22f),
                                    selectedLabelColor = currentTool.accentColor
                                )
                            )
                        }
                    }

                    // Persona-Aware Suggested Research Starters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val starterPrompts = remember(userRole) {
                            when {
                                userRole.contains("kid", true) -> listOf(
                                    "How do rockets escape Earth's gravity?",
                                    "Fun 20-minute science experiment at home",
                                    "How do computers turn 1s and 0s into 3D games?"
                                )
                                userRole.contains("teen", true) -> listOf(
                                    "How to build a coding & digital art portfolio in high school",
                                    "Beginner guide to saving from a first summer job",
                                    "How AI music production and sound synthesis work"
                                )
                                userRole.contains("student", true) -> listOf(
                                    "Compare federal student aid, Pell Grants, and local scholarships",
                                    "Active recall & spaced repetition study schedule for exams",
                                    "Top entry-level STEM & healthcare internships"
                                )
                                userRole.contains("business", true) -> listOf(
                                    "SBA micro-grants & SBDC free advisory checklist for founders",
                                    "How to validate a local service business with zero debt",
                                    "Cash-flow forecasting template for small businesses"
                                )
                                userRole.contains("veteran", true) -> listOf(
                                    "Maximizing Post-9/11 GI Bill + DoD SkillBridge transition roadmap",
                                    "Translating military leadership into tech & operations resumes",
                                    "VA home loan & veteran entrepreneurship grant resources"
                                )
                                else -> listOf(
                                    "3-bucket automated household emergency fund & debt strategy",
                                    "High-growth career pivot certifications under 6 months",
                                    "Weekly meal-prep & family budget optimization blueprint"
                                )
                            }
                        }

                        starterPrompts.forEach { starter ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier.clickable {
                                    userPromptInput = starter
                                    executeAiResearchOrTool(starter, currentTool, selectedResearchDepth)
                                }
                            ) {
                                Text(
                                    text = "💡 $starter",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Prompt Input & Ask AI Bot Button
                    OutlinedTextField(
                        value = userPromptInput,
                        onValueChange = { userPromptInput = it },
                        label = { Text(currentTool.promptPlaceholder) },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_research_bot_prompt_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                executeAiResearchOrTool(
                                    queryText = userPromptInput,
                                    tool = currentTool,
                                    depthMode = selectedResearchDepth
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTool.accentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_research_bot_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Ask AI Research Bot",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Run ${currentTool.title}",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val clean = userPromptInput.trim().ifBlank {
                                    localLearnedTopics.firstOrNull() ?: "STEM & Financial Literacy"
                                }
                                onAskAiStudyTutor(
                                    clean,
                                    currentTool.title,
                                    selectedResearchDepth,
                                    "$userRole (Age $userAge)"
                                )
                            },
                            border = BorderStroke(1.dp, SolarAmber),
                            modifier = Modifier.testTag("ai_study_tutor_sync_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Study Tutor Mode",
                                tint = SolarAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tutor", color = SolarAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Streaming / Loading Indicator or Error State
            when (val state = botGenerationState) {
                is AiTextUiState.Loading -> {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = HoloCyan.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = HoloCyan,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "AI Research Bot is synthesizing with your learned profile...",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = HoloCyan
                                )
                                Text(
                                    text = "Calibrating for ${userRole.uppercase()} • ${learnedInterests.take(2).joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                is AiTextUiState.Streaming -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF08101E),
                        border = BorderStroke(1.5.dp, HoloCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = CyberEmerald,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Streaming Live Gemini Research Synthesis...",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyberEmerald
                                )
                            }
                            Text(
                                text = state.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
                is AiTextUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                else -> {}
            }

            // Recent AI Research Bot Conversation & Tool Outputs
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                chatHistory.takeLast(4).reversed().forEach { msg ->
                    val isBot = msg.sender == "AI_BOT"
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isBot) {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                        } else {
                            HoloCyan.copy(alpha = 0.14f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isBot) CyberEmerald.copy(alpha = 0.5f) else HoloCyan.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBot) "🤖 ${msg.modeLabel}" else "🧑 You • ${msg.modeLabel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isBot) CyberEmerald else HoloCyan
                                )

                                if (isBot && msg.id != "welcome_msg") {
                                    OutlinedButton(
                                        onClick = {
                                            onShareAiResearchToWall(
                                                "AI Research Brief: ${msg.modeLabel}",
                                                msg.text.take(650)
                                            )
                                        },
                                        border = BorderStroke(1.dp, HoloCyan)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share AI Research to Community Hub",
                                            tint = HoloCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Share to Hub",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HoloCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            msg.learnedTakeaway?.let { takeaway ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Learned Signal Recorded",
                                        tint = SolarAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = takeaway,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Display AI Adaptive Plan or AI Flashcards if generated via ViewModel
            if (aiAdaptivePlanState is AiTextUiState.Success) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CyberEmerald.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, CyberEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✨ Personalized 4-Part Adaptive Learning Blueprint",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                        Text(
                            text = aiAdaptivePlanState.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
