package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserProfile
import com.example.ui.AiFlashcardItem
import com.example.ui.AiTextUiState
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiToolsAndSchoolResearchScreen(
    profile: UserProfile,
    learnedSignals: List<String>,
    aiStudyState: AiTextUiState,
    aiFlashcardsState: UiState<List<AiFlashcardItem>>,
    aiAdaptivePlanState: AiTextUiState,
    onAskAiStudyTutor: (topic: String, subject: String, studyMode: String, gradeLevel: String) -> Unit,
    onGenerateStudyFlashcards: (topic: String, gradeLevel: String) -> Unit,
    onGenerateAdaptiveLearningPlan: () -> Unit,
    onRecordLearnedSignal: (String) -> Unit,
    onShareStudyGuideToWall: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var studyTopic by remember {
        mutableStateOf("How does photosynthesis work in plants and why is chlorophyll green?")
    }
    var selectedSubject by remember { mutableStateOf("Science & Space") }
    var selectedStudyMode by remember { mutableStateOf("Step-by-Step Tutor") }
    var selectedGradeLevel by remember(profile.age) {
        mutableStateOf(
            when {
                profile.age <= 12L -> "Elementary (Ages 8–11)"
                profile.age <= 15L -> "Middle School (Ages 12–14)"
                profile.age <= 18L -> "High School (Ages 15–17)"
                else -> "College & Lifelong Learner (18+)"
            }
        )
    }
    var flashcardTopic by remember { mutableStateOf("Solar System Planets & Gravity") }
    var flippedCardIndex by remember { mutableIntStateOf(-1) }

    val subjects = listOf(
        "Math & Fractions",
        "Science & Space",
        "Reading & Essays",
        "History & Civics",
        "Coding & STEM",
        "Vocabulary & Languages"
    )

    val studyModes = listOf(
        "Step-by-Step Tutor",
        "Research Outline",
        "3-Question Practice Quiz",
        "Memory Tricks & Summary"
    )

    val gradeLevels = listOf(
        "Elementary (Ages 8–11)",
        "Middle School (Ages 12–14)",
        "High School (Ages 15–17)",
        "College & Lifelong Learner (18+)"
    )

    val quickHomeworkPrompts = listOf(
        "Explain how to add fractions with different denominators step by step",
        "Help me outline a 5-paragraph school research paper on renewable energy",
        "How does the water cycle work? Give me a fun memory trick!",
        "Explain the 3 branches of government for a civics quiz",
        "Teach me how loops and variables work in coding with a game example"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_tools_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Kid-Friendly AI Study Tutor & Schoolwork Research Coach
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("ai_study_tutor_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HoloCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "AI Study Tutor",
                            tint = HoloCyan
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Kids Study Tutor & Schoolwork Research Hub",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Powered by Firebase AI Logic (Gemini 3.8 Flash) • Kid-safe step-by-step homework help, research outlines & quizzes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Grade / Age Level Selector
                Text(
                    text = "1. Select Student Age / Grade Level:",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberEmerald
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gradeLevels.forEach { level ->
                        FilterChip(
                            selected = selectedGradeLevel == level,
                            onClick = { selectedGradeLevel = level },
                            label = { Text(level, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Subject Selector
                Text(
                    text = "2. Choose School Subject:",
                    style = MaterialTheme.typography.labelLarge,
                    color = HoloCyan
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjects.forEach { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = {
                                selectedSubject = subj
                                onRecordLearnedSignal("Studied $subj")
                            },
                            label = { Text(subj, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Study Mode Selector
                Text(
                    text = "3. Choose AI Study Tool Mode:",
                    style = MaterialTheme.typography.labelLarge,
                    color = SolarAmber
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    studyModes.forEach { mode ->
                        FilterChip(
                            selected = selectedStudyMode == mode,
                            onClick = { selectedStudyMode = mode },
                            label = { Text(mode, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Quick Homework Question Presets
                Text(
                    text = "Quick Schoolwork & Research Topics (Tap to Load):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickHomeworkPrompts.forEach { preset ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                studyTopic = preset
                            }
                        ) {
                            Text(
                                text = "💡 ${preset.take(42)}…",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = studyTopic,
                    onValueChange = { studyTopic = it },
                    label = { Text("Enter homework question, research topic, or math problem") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_homework_prompt_input")
                )

                Button(
                    onClick = {
                        if (studyTopic.isNotBlank()) {
                            onAskAiStudyTutor(
                                studyTopic,
                                selectedSubject,
                                selectedStudyMode,
                                selectedGradeLevel
                            )
                        }
                    },
                    enabled = aiStudyState !is AiTextUiState.Loading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ai_homework_ask_button")
                ) {
                    if (aiStudyState is AiTextUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Tutor Thinking…", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Help Me Study & Research with AI", fontWeight = FontWeight.Bold)
                    }
                }

                // Streaming / Result Display Box
                when (aiStudyState) {
                    is AiTextUiState.Idle -> {}
                    is AiTextUiState.Loading -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = HoloCyan,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Generating kid-friendly $selectedStudyMode guide for $selectedSubject…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    is AiTextUiState.Streaming -> {
                        StudyResponseCard(
                            title = "$selectedStudyMode • $selectedSubject (Streaming Live…)",
                            body = aiStudyState.text,
                            onPinToWall = null
                        )
                    }
                    is AiTextUiState.Success -> {
                        StudyResponseCard(
                            title = "$selectedStudyMode • $selectedSubject",
                            body = aiStudyState.text,
                            onPinToWall = {
                                onShareStudyGuideToWall(
                                    "Study Guide: ${studyTopic.take(55)}",
                                    aiStudyState.text.take(900)
                                )
                            }
                        )
                    }
                    is AiTextUiState.Error -> {
                        Surface(
                            color = SolarAmber.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, SolarAmber),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_study_error_banner")
                        ) {
                            Text(
                                text = aiStudyState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SolarAmber,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Interactive AI Study Flashcards Maker (Structured Schema Output)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.55f))
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
                        imageVector = Icons.Default.Quiz,
                        contentDescription = "AI Study Flashcards",
                        tint = NeonPink
                    )
                    Column {
                        Text(
                            text = "AI Study Flashcard Deck Generator",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Creates structured flip-cards with questions, answers, and memory hints to help kids ace tests.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = flashcardTopic,
                        onValueChange = { flashcardTopic = it },
                        label = { Text("Flashcard Topic (e.g., Multiplication, U.S. History, Cells)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_flashcard_topic_input")
                    )
                    Button(
                        onClick = {
                            flippedCardIndex = -1
                            onGenerateStudyFlashcards(flashcardTopic, selectedGradeLevel)
                        },
                        enabled = aiFlashcardsState !is UiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPink,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("generate_ai_flashcards_button")
                    ) {
                        Text("Create Cards", fontWeight = FontWeight.Bold)
                    }
                }

                when (aiFlashcardsState) {
                    is UiState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeonPink,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Generating structured JSON study flashcards…",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = aiFlashcardsState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SolarAmber
                        )
                    }
                    is UiState.Success -> {
                        aiFlashcardsState.data.forEachIndexed { index, card ->
                            val isFlipped = flippedCardIndex == index
                            Surface(
                                color = if (isFlipped) CyberEmerald.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isFlipped) CyberEmerald else NeonPink.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        flippedCardIndex = if (isFlipped) -1 else index
                                    }
                                    .testTag("study_flashcard_$index")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "FLASHCARD #${index + 1} • ${if (isFlipped) "ANSWER REVEALED" else "TAP TO FLIP"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isFlipped) CyberEmerald else NeonPink
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = SolarAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isFlipped) "A: ${card.answer}" else "Q: ${card.question}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Memory Trick: ${card.memoryHint}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SolarAmber
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. "Platform Learns From Users" — Adaptive AI Learning & Personalization Engine Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
            modifier = Modifier.testTag("adaptive_learning_engine_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CyberEmerald.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Adaptive Learning Engine",
                            tint = CyberEmerald
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Adaptive AI Engine: Platform Learns From You",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Wondering World learns your favorite subjects, age group (${profile.age}), and interests to tailor study tools & resources.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Learned User Signals & Interests:",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberEmerald
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val combinedSignals = (learnedSignals + profile.interests).distinct().take(10)
                    combinedSignals.forEach { signal ->
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "🧠 $signal",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onGenerateAdaptiveLearningPlan,
                    enabled = aiAdaptivePlanState !is AiTextUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_adaptive_study_plan_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Generate Personalized Study & Resource Plan from My Activity",
                        fontWeight = FontWeight.Bold
                    )
                }

                when (aiAdaptivePlanState) {
                    is AiTextUiState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CyberEmerald,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Synthesizing personalized plan from your learned signals…",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is AiTextUiState.Streaming -> {
                        StudyResponseCard(
                            title = "Personalized AI Learning Plan (Streaming…)",
                            body = aiAdaptivePlanState.text,
                            onPinToWall = null
                        )
                    }
                    is AiTextUiState.Success -> {
                        StudyResponseCard(
                            title = "Personalized AI Learning & Discovery Plan",
                            body = aiAdaptivePlanState.text,
                            onPinToWall = {
                                onShareStudyGuideToWall(
                                    "My Adaptive Wondering World Learning Plan",
                                    aiAdaptivePlanState.text.take(900)
                                )
                            }
                        )
                    }
                    is AiTextUiState.Error -> {
                        Text(
                            text = aiAdaptivePlanState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SolarAmber
                        )
                    }
                    is AiTextUiState.Idle -> {}
                }
            }
        }
    }
}

@Composable
private fun StudyResponseCard(
    title: String,
    body: String,
    onPinToWall: (() -> Unit)?
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_study_response_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = HoloCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = HoloCyan
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (onPinToWall != null) {
                OutlinedButton(
                    onClick = onPinToWall,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberEmerald)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pin Study Guide to My 3D Wall", color = CyberEmerald)
                }
            }
        }
    }
}
