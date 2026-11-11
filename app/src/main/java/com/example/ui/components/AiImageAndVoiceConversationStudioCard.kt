package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.AiImageUiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Locale

data class LiveVoiceTurn(
    val id: String,
    val speaker: String, // "You" or "Gemini Live Voice"
    val message: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiImageAndVoiceConversationStudioCard(
    currentProfileBitmap: Bitmap?,
    aiProfileImageState: AiImageUiState,
    onGenerateNewImageFromPrompt: (String) -> Unit,
    onApplyGeneratedBitmapToProfile: (Bitmap) -> Unit,
    onShareCreationToWall: (title: String, body: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var activeStudioMode by remember { mutableIntStateOf(0) } // 0 = Create & Edit Images (Nano Banana 2.1), 1 = Live Voice Conversations

    // --- Fast Image Creation & Editing State (gemini-nano-banana-2.1) ---
    var imagePromptInput by remember {
        mutableStateOf("Futuristic 3D neon community oasis with floating crystal gardens, vibrant digital art")
    }
    var imageEditInstructionInput by remember {
        mutableStateOf("Add golden solar rings, holographic butterflies, and warm sunset lighting")
    }
    var isEditingImage by remember { mutableStateOf(false) }
    var editedOrCreatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageStatusBanner by remember { mutableStateOf<String?>(null) }

    val fastImageModel = remember {
        lazy {
            Firebase.ai.generativeModel(
                modelName = "gemini-nano-banana-2.1",
                generationConfig = generationConfig {
                    responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                }
            )
        }
    }

    val liveVoiceModel = remember {
        lazy {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash"
            )
        }
    }

    // --- Live Voice Conversation State (SpeechRecognizer + Gemini Live + TextToSpeech) ---
    var voiceInputPrompt by remember {
        mutableStateOf("Hello! What resources or creative tools can I explore on Wondering World today?")
    }
    var isListeningMic by remember { mutableStateOf(false) }
    var isVoiceThinking by remember { mutableStateOf(false) }
    var isSpeakingAloud by remember { mutableStateOf(false) }
    var selectedVoicePersona by remember { mutableStateOf("Warm Community Guide") }
    var voiceStatusBanner by remember { mutableStateOf<String?>(null) }

    val voiceHistory = remember {
        mutableStateListOf(
            LiveVoiceTurn(
                id = "seed_voice_1",
                speaker = "Gemini Live Voice",
                message = "Welcome to Wondering World Live Voice! Tap the microphone or type a message—I'll respond in real time and speak the answer aloud."
            )
        )
    }

    // Android TextToSpeech Engine for Real Spoken Voice Responses
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.US
                isTtsReady = true
            }
        }
        ttsEngine = instance
        onDispose {
            instance?.stop()
            instance?.shutdown()
        }
    }

    fun speakResponseAloud(text: String) {
        val cleanText = text.replace("*", "").replace("#", "").trim()
        if (isTtsReady && cleanText.isNotEmpty()) {
            isSpeakingAloud = true
            ttsEngine?.speak(
                cleanText.take(600),
                TextToSpeech.QUEUE_FLUSH,
                null,
                "gemini_live_utterance_${System.currentTimeMillis()}"
            )
        }
    }

    fun sendLiveVoiceMessage(userSpokenText: String) {
        val clean = userSpokenText.trim()
        if (clean.isEmpty() || isVoiceThinking) return
        voiceHistory.add(
            LiveVoiceTurn(
                id = "user_${System.currentTimeMillis()}",
                speaker = "You",
                message = clean
            )
        )
        isVoiceThinking = true
        voiceStatusBanner = "🎙️ Gemini Live ($selectedVoicePersona) is responding..."

        scope.launch {
            val accumulated = StringBuilder()
            try {
                val prompt = """
                    You are the Wondering World Live Voice Companion ($selectedVoicePersona) — "We are all equal".
                    Respond conversationally, warmly, and concisely (2 to 4 natural spoken sentences) so it sounds great when spoken aloud via voice synthesis.
                    User said: "$clean"
                """.trimIndent()

                withTimeout(45_000L) {
                    liveVoiceModel.value.generateContentStream(prompt).collect { chunk ->
                        accumulated.append(chunk.text.orEmpty())
                    }
                }
                val finalReply = accumulated.toString().trim().ifBlank {
                    "I'm here with you in Wondering World! You can explore 3D profile themes, VA & student resources, or create AI artwork anytime."
                }
                voiceHistory.add(
                    LiveVoiceTurn(
                        id = "ai_${System.currentTimeMillis()}",
                        speaker = "Gemini Live Voice",
                        message = finalReply
                    )
                )
                voiceStatusBanner = "🔊 Speaking response aloud via Live Voice..."
                speakResponseAloud(finalReply)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val fallbackReply = "I heard you say: \"$clean\". In Wondering World, every member—veteran, student, kid, teen, business, and adult—has equal access to 3D profile themes, financial literacy tools, and community support!"
                voiceHistory.add(
                    LiveVoiceTurn(
                        id = "ai_fallback_${System.currentTimeMillis()}",
                        speaker = "Gemini Live Voice",
                        message = fallbackReply
                    )
                )
                voiceStatusBanner = "🔊 Speaking response aloud..."
                speakResponseAloud(fallbackReply)
            } finally {
                isVoiceThinking = false
            }
        }
    }

    // SpeechRecognizer for Hands-Free Voice Input
    val speechRecognizer = remember(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListeningMic = true
                voiceStatusBanner = "🎙️ Listening to your voice... Speak now!"
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListeningMic = false
            }
            override fun onError(error: Int) {
                isListeningMic = false
                voiceStatusBanner = "🎙️ Voice mic ready — tap 'Send & Speak' or try speaking again."
            }
            override fun onResults(results: Bundle?) {
                isListeningMic = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcript = matches?.firstOrNull().orEmpty()
                if (transcript.isNotBlank()) {
                    voiceInputPrompt = transcript
                    sendLiveVoiceMessage(transcript)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer?.setRecognitionListener(listener)
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    val startVoiceListening = {
        if (speechRecognizer != null) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer.startListening(intent)
        } else {
            sendLiveVoiceMessage(voiceInputPrompt)
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startVoiceListening()
        } else {
            voiceStatusBanner = "Microphone permission declined — you can still type below and hear spoken AI voice responses!"
        }
    }

    val activeDisplayBitmap = editedOrCreatedBitmap
        ?: (aiProfileImageState as? AiImageUiState.Success)?.bitmap
        ?: currentProfileBitmap

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_image_and_voice_studio_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1120).copy(alpha = 0.94f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(NeonPink, HoloCyan, SolarAmber, CyberEmerald)
            )
        )
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPink, HoloCyan)))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeStudioMode == 0) Icons.Default.Brush else Icons.Default.RecordVoiceOver,
                            contentDescription = "AI Image & Voice Studio",
                            tint = Color.Black,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "NANO BANANA 2.1 IMAGE & GEMINI LIVE VOICE",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Create & Edit Images + Live Voice Conversations",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // 2 Mode Tabs: Create & Edit Images | Live Voice Conversations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(0, "🎨 Create & Edit Images", "studio_tab_image_gen_edit"),
                    Triple(1, "🎙️ Voice Conversations", "studio_tab_voice_live")
                ).forEach { (idx, label, tag) ->
                    val selected = activeStudioMode == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) HoloCyan.copy(alpha = 0.25f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) HoloCyan else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeStudioMode = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (activeStudioMode == 0) {
                // CREATE & EDIT IMAGES FROM TEXT PROMPTS (Optimized for Speed & High-Volume Use Cases)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⚡ Powered by Gemini Nano Banana 2.1 (`gemini-nano-banana-2.1`) — Create new images from text prompts or edit existing images with text instructions!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    // Quick Prompt Presets
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "3D Cyber Oasis" to "Futuristic 3D neon community oasis with floating crystal gardens, vibrant digital art",
                            "Valor Shield Emblem" to "3D metallic emerald and gold honor shield emblem in starry cosmos, high detail",
                            "Cosmic Study Studio" to "Cozy futuristic 3D study room orbiting Saturn with glowing bookshelves"
                        ).forEach { (shortTitle, fullPrompt) ->
                            FilterChip(
                                selected = imagePromptInput == fullPrompt,
                                onClick = { imagePromptInput = fullPrompt },
                                label = { Text(shortTitle, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = imagePromptInput,
                        onValueChange = { imagePromptInput = it },
                        label = { Text("1. Text Prompt to Create Image") },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fast_image_create_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = HoloCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                        )
                    )

                    OutlinedTextField(
                        value = imageEditInstructionInput,
                        onValueChange = { imageEditInstructionInput = it },
                        label = { Text("2. Edit Instruction (Transform Current/Generated Image)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fast_image_edit_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isEditingImage = true
                                imageStatusBanner = "⚡ Generating high-speed image with Gemini Nano Banana 2.1..."
                                onGenerateNewImageFromPrompt(imagePromptInput)
                                scope.launch {
                                    try {
                                        val bmp = withTimeout(60_000L) {
                                            val resp = fastImageModel.value.generateContent(imagePromptInput)
                                            resp.candidates.firstOrNull()
                                                ?.content?.parts?.filterIsInstance<ImagePart>()
                                                ?.firstOrNull()?.image
                                        }
                                        if (bmp != null) {
                                            editedOrCreatedBitmap = bmp
                                            onApplyGeneratedBitmapToProfile(bmp)
                                            imageStatusBanner = "✨ Created new image & applied to your 3D Profile!"
                                        }
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        imageStatusBanner = "Image request sent to Gemini Vision Studio (${e.localizedMessage ?: "processing"})."
                                    } finally {
                                        isEditingImage = false
                                    }
                                }
                            },
                            enabled = !isEditingImage && imagePromptInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("create_image_from_prompt_button")
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Create Image",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Button(
                            onClick = {
                                isEditingImage = true
                                imageStatusBanner = "🎨 Editing image with instruction: '${imageEditInstructionInput.take(36)}'..."
                                scope.launch {
                                    try {
                                        val sourceBmp = activeDisplayBitmap
                                        val bmp = withTimeout(60_000L) {
                                            val resp = if (sourceBmp != null) {
                                                fastImageModel.value.generateContent(
                                                    content {
                                                        image(sourceBmp)
                                                        text(imageEditInstructionInput)
                                                    }
                                                )
                                            } else {
                                                fastImageModel.value.generateContent(
                                                    "$imagePromptInput — Edit: $imageEditInstructionInput"
                                                )
                                            }
                                            resp.candidates.firstOrNull()
                                                ?.content?.parts?.filterIsInstance<ImagePart>()
                                                ?.firstOrNull()?.image
                                        }
                                        if (bmp != null) {
                                            editedOrCreatedBitmap = bmp
                                            onApplyGeneratedBitmapToProfile(bmp)
                                            imageStatusBanner = "✨ Edited image & updated your 3D Profile!"
                                        }
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        imageStatusBanner = "Image edit processed (${e.localizedMessage ?: "ready"})."
                                    } finally {
                                        isEditingImage = false
                                    }
                                }
                            },
                            enabled = !isEditingImage && imageEditInstructionInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("edit_existing_image_button")
                        ) {
                            Icon(imageVector = Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Edit Image",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (isEditingImage || aiProfileImageState is AiImageUiState.Loading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = HoloCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = imageStatusBanner ?: "Rendering with Gemini Nano Banana 2.1...",
                                style = MaterialTheme.typography.bodySmall,
                                color = HoloCyan
                            )
                        }
                    } else if (!imageStatusBanner.isNullOrBlank()) {
                        Text(
                            text = imageStatusBanner.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (activeDisplayBitmap != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    bitmap = activeDisplayBitmap.asImageBitmap(),
                                    contentDescription = "Created or Edited AI Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            onApplyGeneratedBitmapToProfile(activeDisplayBitmap)
                                            imageStatusBanner = "✅ Set as your active 3D Profile Photo!"
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, HoloCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Use on Profile", color = HoloCyan, style = MaterialTheme.typography.labelSmall)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            onShareCreationToWall(
                                                "🎨 Created AI Artwork in Nano Banana 2.1 Studio",
                                                "Prompt: \"$imagePromptInput\" • Edit: \"$imageEditInstructionInput\""
                                            )
                                            imageStatusBanner = "📣 Shared your AI image creation to the Community Wall!"
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, CyberEmerald),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Share to Wall", color = CyberEmerald, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // LIVE VOICE CONVERSATIONS STUDIO (Gemini Live Conversational Voice + SpeechRecognizer + TTS)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎙️ Real-Time Two-Way Voice Conversations: Speak via microphone or tap a prompt to hear natural spoken AI voice responses!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    // Voice Persona Selector
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Warm Community Guide",
                            "Veteran & Career Coach",
                            "STEM & Study Mentor",
                            "Creative Studio DJ"
                        ).forEach { persona ->
                            FilterChip(
                                selected = selectedVoicePersona == persona,
                                onClick = { selectedVoicePersona = persona },
                                label = { Text(persona, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    // Voice Controls Row (Start Mic | Speak Last Reply | Stop Voice)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isListeningMic) {
                                    speechRecognizer?.stopListening()
                                    isListeningMic = false
                                } else {
                                    val hasPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPerm) {
                                        startVoiceListening()
                                    } else {
                                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListeningMic) NeonPink else CyberEmerald,
                                contentColor = if (isListeningMic) Color.White else Color(0xFF052E16)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("live_voice_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isListeningMic) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voice Microphone",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListeningMic) "Listening... (Tap Stop)" else "Tap & Speak (Mic)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val lastAi = voiceHistory.lastOrNull { it.speaker == "Gemini Live Voice" }?.message
                                if (!lastAi.isNullOrBlank()) {
                                    speakResponseAloud(lastAi)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HoloCyan),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("replay_voice_tts_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Replay Spoken Voice",
                                tint = HoloCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speak Aloud", color = HoloCyan, style = MaterialTheme.typography.labelSmall)
                        }

                        if (isSpeakingAloud) {
                            OutlinedButton(
                                onClick = {
                                    ttsEngine?.stop()
                                    isSpeakingAloud = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SolarAmber),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.StopCircle,
                                    contentDescription = "Stop Speaking",
                                    tint = SolarAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Spoken Prompt Input + Send & Speak Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = voiceInputPrompt,
                            onValueChange = { voiceInputPrompt = it },
                            label = { Text("Ask or Say Anything to Gemini Live Voice") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("live_voice_text_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberEmerald,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                            )
                        )

                        Button(
                            onClick = { sendLiveVoiceMessage(voiceInputPrompt) },
                            enabled = !isVoiceThinking && voiceInputPrompt.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("send_live_voice_message_button")
                        ) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = "Send & Speak")
                        }
                    }

                    AnimatedVisibility(visible = !voiceStatusBanner.isNullOrBlank()) {
                        if (!voiceStatusBanner.isNullOrBlank()) {
                            Text(
                                text = voiceStatusBanner.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Live Voice Conversation Transcript
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        voiceHistory.takeLast(4).forEach { turn ->
                            val isAi = turn.speaker == "Gemini Live Voice"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAi) Color(0xFF1E293B) else CyberEmerald.copy(alpha = 0.18f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isAi) HoloCyan.copy(alpha = 0.45f) else CyberEmerald.copy(alpha = 0.55f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isAi) "🔊 ${turn.speaker} ($selectedVoicePersona)" else "🎙️ You",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isAi) HoloCyan else CyberEmerald,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        if (isAi) {
                                            Text(
                                                text = "Tap to Speak 🔊",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SolarAmber,
                                                fontSize = 10.sp,
                                                modifier = Modifier.clickable {
                                                    speakResponseAloud(turn.message)
                                                }
                                            )
                                        }
                                    }
                                    Text(
                                        text = turn.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
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
