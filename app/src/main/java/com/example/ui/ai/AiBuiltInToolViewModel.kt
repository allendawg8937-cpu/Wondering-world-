package com.example.ui.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiBuiltInToolViewModel"

enum class BuiltInAiToolMode(
    val id: String,
    val label: String,
    val shortTag: String,
    val description: String,
    val promptHint: String,
    val samplePrompt: String,
    val systemDirective: String,
    val isStructuredPlan: Boolean = false
) {
    ACTION_PLAN(
        id = "action_plan",
        label = "Structured Plan",
        shortTag = "JSON Plan",
        description = "Builds a step-by-step action plan with priority and time estimate calibrated to your life stage.",
        promptHint = "Enter a goal or project (e.g., Launch a teen portfolio website, Prepare for a job interview)...",
        samplePrompt = "Build a 7-day study & savings habit routine balanced around school/work",
        systemDirective = "Return a practical, encouraging, step-by-step action plan tailored to the user's life stage and goals.",
        isStructuredPlan = true
    ),
    SUMMARIZE(
        id = "summarize",
        label = "Smart Summarizer",
        shortTag = "Stream",
        description = "Turns notes, articles, or policies into a concise 1-sentence thesis plus bullet takeaways.",
        promptHint = "Paste text, study notes, or a topic to summarize cleanly...",
        samplePrompt = "Summarize the key differences between a Roth IRA, 401(k), and high-yield emergency savings",
        systemDirective = "Condense the input into: 1) Core Thesis (1 sentence), 2) 4 Clear Bullet Takeaways, and 3) One Practical Next Step."
    ),
    REWRITE(
        id = "rewrite",
        label = "Rewrite & Polish",
        shortTag = "Tone",
        description = "Refines drafts, emails, bios, or resumes with clear, confident, respectful tone.",
        promptHint = "Paste your draft message, bio, scholarship note, or cover paragraph...",
        samplePrompt = "Polish a respectful outreach message to a local mentor asking for a 15-minute informational chat",
        systemDirective = "Rewrite the draft cleanly in a confident, warm, professional voice tailored to the user's stage of life, preserving key facts."
    ),
    TUTOR(
        id = "tutor",
        label = "Stage Explainer",
        shortTag = "Learn",
        description = "Explains complex STEM, civic, coding, or finance topics with intuitive examples and a quick check question.",
        promptHint = "Ask any how/why question in math, science, coding, civics, or life skills...",
        samplePrompt = "Explain compound interest and inflation simply with a concrete monthly example",
        systemDirective = "Explain clearly step by step using relatable analogies for the user's life stage, then finish with one quick check-for-understanding question."
    ),
    TRANSLATE_CLARIFY(
        id = "translate_clarify",
        label = "Simplify Jargon",
        shortTag = "Clarity",
        description = "Translates legal, financial, academic, or medical jargon into plain, everyday English.",
        promptHint = "Paste confusing terminology, fine print, or technical phrasing...",
        samplePrompt = "Explain what FAFSA Student Aid Index (SAI), subsidized loans, and grace periods mean in plain English",
        systemDirective = "Translate any jargon into plain, everyday language with clear definitions and what to watch out for."
    )
}

data class StructuredAiActionPlan(
    val goalTitle: String,
    val summary: String,
    val priorityLevel: String,
    val estimatedMinutes: Int,
    val steps: List<String>
)

sealed interface AiBuiltInToolUiState {
    data object Idle : AiBuiltInToolUiState
    data object Loading : AiBuiltInToolUiState
    data class Streaming(val partialText: String) : AiBuiltInToolUiState
    data class Success(
        val outputText: String,
        val structuredPlan: StructuredAiActionPlan? = null
    ) : AiBuiltInToolUiState
    data class Error(val message: String) : AiBuiltInToolUiState
}

data class AiToolHistoryItem(
    val id: String,
    val modeLabel: String,
    val prompt: String,
    val preview: String
)

class AiBuiltInToolViewModel : ViewModel() {

    private val streamingModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val structuredPlanSchema = Schema.obj(
        properties = mapOf(
            "goalTitle" to Schema.string(description = "Short actionable title for the user's goal"),
            "summary" to Schema.string(description = "One-to-two sentence tailored overview"),
            "priorityLevel" to Schema.enumeration(
                values = listOf("High", "Medium", "Foundational"),
                description = "Recommended priority level"
            ),
            "estimatedMinutes" to Schema.integer(description = "Estimated minutes to start the first milestone"),
            "steps" to Schema.array(
                items = Schema.string(),
                description = "3 to 5 clear sequential action steps"
            )
        )
    )

    private val structuredPlanModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = structuredPlanSchema
            }
        )
    }

    private val _selectedMode = MutableStateFlow(BuiltInAiToolMode.ACTION_PLAN)
    val selectedMode: StateFlow<BuiltInAiToolMode> = _selectedMode.asStateFlow()

    private val _uiState = MutableStateFlow<AiBuiltInToolUiState>(AiBuiltInToolUiState.Idle)
    val uiState: StateFlow<AiBuiltInToolUiState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<AiToolHistoryItem>>(emptyList())
    val history: StateFlow<List<AiToolHistoryItem>> = _history.asStateFlow()

    fun selectMode(mode: BuiltInAiToolMode) {
        _selectedMode.value = mode
    }

    fun resetState() {
        _uiState.value = AiBuiltInToolUiState.Idle
    }

    fun runTool(
        inputPrompt: String,
        userRole: String,
        userDisplayName: String,
        mode: BuiltInAiToolMode = _selectedMode.value
    ) {
        val cleanInput = inputPrompt.trim()
        if (cleanInput.isEmpty()) {
            _uiState.value = AiBuiltInToolUiState.Error("Enter a topic, question, or draft first.")
            return
        }

        _selectedMode.value = mode

        viewModelScope.launch {
            _uiState.value = AiBuiltInToolUiState.Loading
            val fullPrompt = buildString {
                appendLine("You are the built-in AI assistant inside the Stages Of Life platform.")
                appendLine("User Name: ${userDisplayName.ifBlank { "Explorer" }}")
                appendLine("Active Life Stage Persona: ${userRole.ifBlank { "adult" }.uppercase()}")
                appendLine("Tool Mode: ${mode.label}")
                appendLine("Instruction: ${mode.systemDirective}")
                appendLine("Content Policy: Keep output helpful, accurate, respectful, and PG-13 appropriate.")
                appendLine("User Input:")
                appendLine(cleanInput)
            }

            try {
                if (mode.isStructuredPlan) {
                    val response = withTimeout(60_000L) {
                        structuredPlanModel.generateContent(fullPrompt)
                    }
                    val jsonString = response.text
                        ?: throw IllegalStateException("Model returned an empty response.")
                    val plan = parseStructuredPlanJson(jsonString)
                    val formattedSummary = buildString {
                        appendLine("${plan.goalTitle} [${plan.priorityLevel} Priority • ~${plan.estimatedMinutes} min]")
                        appendLine(plan.summary)
                        plan.steps.forEachIndexed { idx, step ->
                            appendLine("${idx + 1}. $step")
                        }
                    }.trim()

                    recordHistory(mode.label, cleanInput, plan.goalTitle)
                    _uiState.value = AiBuiltInToolUiState.Success(
                        outputText = formattedSummary,
                        structuredPlan = plan
                    )
                } else {
                    val accumulated = StringBuilder()
                    withTimeout(60_000L) {
                        streamingModel.generateContentStream(fullPrompt).collect { chunk ->
                            val chunkText = chunk.text.orEmpty()
                            if (chunkText.isNotEmpty()) {
                                accumulated.append(chunkText)
                                _uiState.value = AiBuiltInToolUiState.Streaming(accumulated.toString())
                            }
                        }
                    }
                    val finalOutput = accumulated.toString().trim()
                    if (finalOutput.isNotEmpty()) {
                        recordHistory(mode.label, cleanInput, finalOutput.take(80))
                        _uiState.value = AiBuiltInToolUiState.Success(outputText = finalOutput)
                    } else {
                        _uiState.value = AiBuiltInToolUiState.Error("Model returned an empty response.")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Built-in AI tool timed out after 60s", e)
                _uiState.value = AiBuiltInToolUiState.Error(
                    "AI tool request timed out after 60 seconds. Please try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Built-in AI tool failed", e)
                _uiState.value = AiBuiltInToolUiState.Error(
                    e.localizedMessage ?: "AI tool generation failed. Check connection and try again."
                )
            }
        }
    }

    private fun recordHistory(modeLabel: String, prompt: String, preview: String) {
        val item = AiToolHistoryItem(
            id = "${System.currentTimeMillis()}",
            modeLabel = modeLabel,
            prompt = prompt,
            preview = preview
        )
        _history.value = (listOf(item) + _history.value).take(5)
    }

    companion object {
        fun parseStructuredPlanJson(rawJson: String): StructuredAiActionPlan {
            val json = JSONObject(rawJson)
            val stepsArray = json.optJSONArray("steps")
            val parsedSteps = if (stepsArray != null) {
                (0 until stepsArray.length()).mapNotNull { idx ->
                    stepsArray.optString(idx).takeIf { it.isNotBlank() }
                }
            } else {
                emptyList()
            }
            return StructuredAiActionPlan(
                goalTitle = json.optString("goalTitle").ifBlank { "Action Plan" },
                summary = json.optString("summary"),
                priorityLevel = json.optString("priorityLevel").ifBlank { "High" },
                estimatedMinutes = json.optInt("estimatedMinutes", 20).coerceAtLeast(5),
                steps = parsedSteps
            )
        }
    }
}
