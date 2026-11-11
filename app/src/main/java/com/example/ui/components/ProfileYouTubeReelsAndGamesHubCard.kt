package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.cos
import kotlin.math.sin

data class ProfileYouTubeVideoPreset(
    val id: String,
    val title: String,
    val channelName: String,
    val category: String,
    val videoId: String,
    val watchUrl: String,
    val durationSeconds: Float = 180f,
    val accent: Color
)

data class YouTubeShortReelItem(
    val id: String,
    val videoId: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorRole: String,
    val category: String,
    val title: String,
    val caption: String,
    val musicTrack: String,
    val visualEffect: String,
    val durationSeconds: Float,
    val likesCount: Int,
    val commentsCount: Int,
    val accentColor: Color
)

data class KidTeenGamePlatformItem(
    val id: String,
    val name: String,
    val badge: String,
    val ageRating: String,
    val description: String,
    val url: String,
    val emoji: String,
    val accent: Color
)

val PROFILE_YOUTUBE_PRESETS = listOf(
    ProfileYouTubeVideoPreset(
        id = "yt_lofi_study",
        title = "Lofi Hip Hop Radio — Beats to Study & Relax To",
        channelName = "Lofi Girl • Profile Stream",
        category = "Music & Study",
        videoId = "jfKfPfyJRdk",
        watchUrl = "https://www.youtube.com/watch?v=jfKfPfyJRdk",
        durationSeconds = 240f,
        accent = HoloCyan
    ),
    ProfileYouTubeVideoPreset(
        id = "yt_nasa_earth",
        title = "NASA Live 4K — Earth & Space Station View",
        channelName = "NASA Official",
        category = "STEM & Space",
        videoId = "xRPjKQtRXR8",
        watchUrl = "https://www.youtube.com/watch?v=xRPjKQtRXR8",
        durationSeconds = 210f,
        accent = CyberEmerald
    ),
    ProfileYouTubeVideoPreset(
        id = "yt_pink_floyd",
        title = "Comfortably Numb & Classic Rock Synth Visuals",
        channelName = "YouTube Music Stage",
        category = "Profile Anthem",
        videoId = "_FrOQC-zEog",
        watchUrl = "https://www.youtube.com/watch?v=_FrOQC-zEog",
        durationSeconds = 380f,
        accent = NeonPink
    ),
    ProfileYouTubeVideoPreset(
        id = "yt_sports_highlights",
        title = "Top 10 Basketball, Football & Soccer Plays of the Week",
        channelName = "YouTube Sports Hub",
        category = "Live Sports",
        videoId = "5qap5aO4i9A",
        watchUrl = "https://www.youtube.com/watch?v=5qap5aO4i9A",
        durationSeconds = 195f,
        accent = SolarAmber
    ),
    ProfileYouTubeVideoPreset(
        id = "yt_gaming_creators",
        title = "Teen & Kid Gaming Builds — Minecraft, Roblox & Coding",
        channelName = "YouTube Gaming Creators",
        category = "Teen & Kid Gaming",
        videoId = "MmB9b5njVbA",
        watchUrl = "https://www.youtube.com/watch?v=MmB9b5njVbA",
        durationSeconds = 165f,
        accent = RosePulse
    )
)

val DEFAULT_YOUTUBE_SHORTS_REELS_FEED = listOf(
    YouTubeShortReelItem(
        id = "short_1",
        videoId = "jfKfPfyJRdk",
        creatorName = "Jordan Vance",
        creatorHandle = "@jordan_6d",
        creatorRole = "VETERAN CREATOR",
        category = "6D Music & Synth",
        title = "My 6D Profile Anthem & Cyberpunk Neon Short",
        caption = "Welcome to my WereEqual Profile! Continuous YouTube Shorts & Reels feed powered by the Android YouTube Player API. #WereEqual #Shorts",
        musicTrack = "Comfortably Numb — 6D Synthwave Remix",
        visualEffect = "NEON_SYNTHWAVE",
        durationSeconds = 15f,
        likesCount = 342,
        commentsCount = 48,
        accentColor = HoloCyan
    ),
    YouTubeShortReelItem(
        id = "short_2",
        videoId = "MmB9b5njVbA",
        creatorName = "Maya Lin",
        creatorHandle = "@maya_builds",
        creatorRole = "TEEN CREATOR • 17",
        category = "Teen & Kid Gaming",
        title = "How I Built a 3D Roblox & Minecraft Shader in 20 Seconds",
        caption = "Swipe up for more Shorts! Teen & Kid builders can launch all 12 game platforms right from their profile. #Roblox #Minecraft #STEM",
        musicTrack = "Lofi Coding Beats — 120 BPM",
        visualEffect = "CYBER_MATRIX",
        durationSeconds = 20f,
        likesCount = 589,
        commentsCount = 74,
        accentColor = NeonPink
    ),
    YouTubeShortReelItem(
        id = "short_3",
        videoId = "xRPjKQtRXR8",
        creatorName = "NASA & STEM Hub",
        creatorHandle = "@nasa_stem_youth",
        creatorRole = "SPACE & SCIENCE",
        category = "STEM & Space",
        title = "Orbital Sunrise from the International Space Station",
        caption = "Watch Earth glow at 17,500 mph! Use the AI Study Tutor on WereEqual to generate astronomy flashcards. #NASA #SpaceShorts",
        musicTrack = "Cosmic Horizon — Ambient Synth",
        visualEffect = "AURORA_WAVE",
        durationSeconds = 18f,
        likesCount = 912,
        commentsCount = 115,
        accentColor = CyberEmerald
    ),
    YouTubeShortReelItem(
        id = "short_4",
        videoId = "5qap5aO4i9A",
        creatorName = "Marcus Sterling",
        creatorHandle = "@marcus_mentor",
        creatorRole = "CAREER MENTOR",
        category = "Career & Study Hacks",
        title = "3 FAFSA & Paid Tech Internship Milestones Every Student Needs",
        caption = "Check your Firestore Career Portal Vertical Timeline to mark your milestones Completed and earn XP! #CareerPortal #FAFSA",
        musicTrack = "Solar Momentum — LoFi Drive",
        visualEffect = "SOLAR_FLARE",
        durationSeconds = 25f,
        likesCount = 476,
        commentsCount = 63,
        accentColor = SolarAmber
    ),
    YouTubeShortReelItem(
        id = "short_5",
        videoId = "_FrOQC-zEog",
        creatorName = "Leo & Zara",
        creatorHandle = "@leo_zara_arcade",
        creatorRole = "KID GAMERS • 14",
        category = "Arcade & Challenges",
        title = "Top 5 Free YouTube Playables & Poki Games Challenge!",
        caption = "We tested every game platform on our Teen & Kid Profile! Comment your high score below. #YouTubePlayables #GamingShorts",
        musicTrack = "Hyper Arcade Chiptune 6D",
        visualEffect = "PRISM_PULSE",
        durationSeconds = 16f,
        likesCount = 730,
        commentsCount = 92,
        accentColor = RosePulse
    ),
    YouTubeShortReelItem(
        id = "short_6",
        videoId = "jfKfPfyJRdk",
        creatorName = "Elena Rostova",
        creatorHandle = "@elena_finance",
        creatorRole = "FINANCE COACH",
        category = "Financial Literacy",
        title = "How to Reach Your First $1,000 Emergency Savings Goal",
        caption = "Protect your Financial Literacy Tracker with Biometric Unlock and log your weekly savings milestones! #FinanceShorts #WereEqual",
        musicTrack = "Emerald Focus — Chill Hop",
        visualEffect = "CYBER_MATRIX",
        durationSeconds = 22f,
        likesCount = 518,
        commentsCount = 59,
        accentColor = CyberEmerald
    )
)

/**
 * Extracts a clean 11-character YouTube Video ID from any YouTube URL
 * (watch?v=, youtu.be/, youtube.com/shorts/, youtube.com/embed/, youtube.com/live/)
 * or raw 11-character video ID string.
 */
fun extractYouTubeVideoId(rawInput: String, fallbackVideoId: String = "jfKfPfyJRdk"): String {
    val trimmed = rawInput.trim()
    if (trimmed.isEmpty()) return fallbackVideoId

    val idRegex = Regex("^[a-zA-Z0-9_-]{11}$")
    if (idRegex.matches(trimmed)) {
        return trimmed
    }

    val urlPatterns = listOf(
        Regex("(?:v=|/v/|youtu\\.be/|/embed/|/shorts/|/live/)([a-zA-Z0-9_-]{11})")
    )
    for (pattern in urlPatterns) {
        val match = pattern.find(trimmed)
        if (match != null && match.groupValues.size > 1) {
            return match.groupValues[1]
        }
    }

    val lower = trimmed.lowercase(Locale.ROOT)
    val matchedPreset = PROFILE_YOUTUBE_PRESETS.firstOrNull {
        it.title.lowercase(Locale.ROOT).contains(lower) ||
            it.category.lowercase(Locale.ROOT).contains(lower) ||
            it.channelName.lowercase(Locale.ROOT).contains(lower)
    }
    return matchedPreset?.videoId ?: fallbackVideoId
}

private fun formatSecondsToMmSs(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format(Locale.US, "%d:%02d", mins, secs)
}

/**
 * Crash-Safe implementation of the official Android YouTube Player API (`YouTubePlayer` interface)
 * that dispatches `YouTubePlayerListener` / `AbstractYouTubePlayerListener` callbacks
 * (`onReady`, `onStateChange`, `onCurrentSecond`, `onVideoDuration`, `onPlaybackRateChange`)
 * and optionally bridges to a hardware/software-guarded `YouTubePlayerView` without ever
 * risking a Chromium renderer crash (`aw_browser_terminator.cc`) in the Android emulator.
 */
class CrashSafeYouTubePlayerController(
    initialVideoId: String,
    initialDuration: Float = 180f
) : YouTubePlayer {
    private val listeners = CopyOnWriteArraySet<YouTubePlayerListener>()
    var attachedNativePlayer: YouTubePlayer? = null

    var currentVideoId: String = initialVideoId
        private set
    var currentState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.VIDEO_CUED
        private set
    var currentSecond: Float = 0f
        private set
    var videoDuration: Float = initialDuration.coerceAtLeast(10f)
        private set
    var playbackRate: PlayerConstants.PlaybackRate = PlayerConstants.PlaybackRate.RATE_1
        private set
    var isMuted: Boolean = false
        private set
    var volumePercent: Int = 100
        private set

    fun notifyReady() {
        listeners.forEach { it.onReady(this) }
        listeners.forEach { it.onVideoId(this, currentVideoId) }
        listeners.forEach { it.onVideoDuration(this, videoDuration) }
    }

    fun updateDuration(durationSeconds: Float) {
        videoDuration = durationSeconds.coerceAtLeast(5f)
        listeners.forEach { it.onVideoDuration(this, videoDuration) }
    }

    fun tickProgress(deltaSeconds: Float) {
        if (currentState != PlayerConstants.PlayerState.PLAYING) return
        val speedMultiplier = when (playbackRate) {
            PlayerConstants.PlaybackRate.RATE_0_25 -> 0.25f
            PlayerConstants.PlaybackRate.RATE_0_5 -> 0.5f
            PlayerConstants.PlaybackRate.RATE_1_5 -> 1.5f
            PlayerConstants.PlaybackRate.RATE_2 -> 2.0f
            else -> 1.0f
        }
        val nextSec = currentSecond + (deltaSeconds * speedMultiplier)
        if (nextSec >= videoDuration) {
            currentSecond = videoDuration
            listeners.forEach { it.onCurrentSecond(this, currentSecond) }
            currentState = PlayerConstants.PlayerState.ENDED
            listeners.forEach { it.onStateChange(this, PlayerConstants.PlayerState.ENDED) }
        } else {
            currentSecond = nextSec
            listeners.forEach { it.onCurrentSecond(this, currentSecond) }
        }
    }

    override fun loadVideo(videoId: String, startSeconds: Float) {
        currentVideoId = videoId
        currentSecond = startSeconds.coerceAtLeast(0f)
        currentState = PlayerConstants.PlayerState.PLAYING
        listeners.forEach { it.onVideoId(this, videoId) }
        listeners.forEach { it.onCurrentSecond(this, currentSecond) }
        listeners.forEach { it.onStateChange(this, PlayerConstants.PlayerState.PLAYING) }
        try {
            attachedNativePlayer?.loadVideo(videoId, startSeconds)
        } catch (_: Exception) {
        }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        currentVideoId = videoId
        currentSecond = startSeconds.coerceAtLeast(0f)
        currentState = PlayerConstants.PlayerState.VIDEO_CUED
        listeners.forEach { it.onVideoId(this, videoId) }
        listeners.forEach { it.onCurrentSecond(this, currentSecond) }
        listeners.forEach { it.onStateChange(this, PlayerConstants.PlayerState.VIDEO_CUED) }
        try {
            attachedNativePlayer?.cueVideo(videoId, startSeconds)
        } catch (_: Exception) {
        }
    }

    override fun play() {
        if (currentState == PlayerConstants.PlayerState.ENDED) {
            currentSecond = 0f
        }
        currentState = PlayerConstants.PlayerState.PLAYING
        listeners.forEach { it.onStateChange(this, PlayerConstants.PlayerState.PLAYING) }
        try {
            attachedNativePlayer?.play()
        } catch (_: Exception) {
        }
    }

    override fun pause() {
        currentState = PlayerConstants.PlayerState.PAUSED
        listeners.forEach { it.onStateChange(this, PlayerConstants.PlayerState.PAUSED) }
        try {
            attachedNativePlayer?.pause()
        } catch (_: Exception) {
        }
    }

    override fun mute() {
        isMuted = true
        try {
            attachedNativePlayer?.mute()
        } catch (_: Exception) {
        }
    }

    override fun unMute() {
        isMuted = false
        try {
            attachedNativePlayer?.unMute()
        } catch (_: Exception) {
        }
    }

    override fun setVolume(volumePercent: Int) {
        this.volumePercent = volumePercent.coerceIn(0, 100)
        try {
            attachedNativePlayer?.setVolume(this.volumePercent)
        } catch (_: Exception) {
        }
    }

    override fun seekTo(time: Float) {
        currentSecond = time.coerceIn(0f, videoDuration)
        listeners.forEach { it.onCurrentSecond(this, currentSecond) }
        try {
            attachedNativePlayer?.seekTo(currentSecond)
        } catch (_: Exception) {
        }
    }

    override fun setPlaybackRate(playbackRate: PlayerConstants.PlaybackRate) {
        this.playbackRate = playbackRate
        listeners.forEach { it.onPlaybackRateChange(this, playbackRate) }
        try {
            attachedNativePlayer?.setPlaybackRate(playbackRate)
        } catch (_: Exception) {
        }
    }

    override fun nextVideo() {
        try {
            attachedNativePlayer?.nextVideo()
        } catch (_: Exception) {
        }
    }

    override fun previousVideo() {
        try {
            attachedNativePlayer?.previousVideo()
        } catch (_: Exception) {
        }
    }

    override fun playVideoAt(index: Int) {
        try {
            attachedNativePlayer?.playVideoAt(index)
        } catch (_: Exception) {
        }
    }

    override fun setLoop(loop: Boolean) {
        try {
            attachedNativePlayer?.setLoop(loop)
        } catch (_: Exception) {
        }
    }

    override fun setShuffle(shuffle: Boolean) {
        try {
            attachedNativePlayer?.setShuffle(shuffle)
        } catch (_: Exception) {
        }
    }

    override fun toggleFullscreen() {
        try {
            attachedNativePlayer?.toggleFullscreen()
        } catch (_: Exception) {
        }
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean {
        return listeners.add(listener)
    }

    override fun removeListener(listener: YouTubePlayerListener): Boolean {
        return listeners.remove(listener)
    }
}

/**
 * Recursively locates any internal WebView inside a `YouTubePlayerView` and configures:
 * 1) `View.LAYER_TYPE_SOFTWARE` so the emulator's Chromium GPU renderer never crashes on 3D surfaces.
 * 2) A `WebViewClient` with `onRenderProcessGone` returning `true` so Android's `aw_browser_terminator.cc`
 *    never terminates the host application if the sandboxed WebView renderer exits.
 */
private fun hardenYouTubePlayerViewWebView(
    view: View,
    onRenderProcessCrashed: () -> Unit
) {
    if (view is WebView) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val existingClient = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                view.webViewClient
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
        view.webViewClient = object : WebViewClient() {
            override fun onRenderProcessGone(
                webView: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                try {
                    webView?.destroy()
                } catch (_: Exception) {
                }
                onRenderProcessCrashed()
                // Returning true is mandatory in Android to prevent aw_browser_terminator.cc from killing the app
                return true
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                existingClient?.onPageFinished(webView, url)
            }
        }
    } else if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            hardenYouTubePlayerViewWebView(view.getChildAt(i), onRenderProcessCrashed)
        }
    }
}

val ALL_TEEN_AND_KID_GAME_PLATFORMS = listOf(
    KidTeenGamePlatformItem(
        id = "game_roblox",
        name = "Roblox",
        badge = "TEEN & KID FAVORITE",
        ageRating = "Kids & Teens (E10+)",
        description = "Create 3D worlds, play obbies, and hang out with friends across millions of community games.",
        url = "https://www.roblox.com/",
        emoji = "🧱",
        accent = RosePulse
    ),
    KidTeenGamePlatformItem(
        id = "game_minecraft",
        name = "Minecraft",
        badge = "3D SANDBOX & STEM",
        ageRating = "All Ages (Everyone)",
        description = "Build creative 3D worlds, Redstone engineering circuits, and multiplayer survival adventures.",
        url = "https://www.minecraft.net/",
        emoji = "⛏️",
        accent = CyberEmerald
    ),
    KidTeenGamePlatformItem(
        id = "game_youtube_playables",
        name = "YouTube Playables",
        badge = "INSTANT YOUTUBE GAMES",
        ageRating = "Kids & Teens",
        description = "Play dozens of instant arcade, puzzle, racing, and trivia games directly inside YouTube.",
        url = "https://www.youtube.com/playables",
        emoji = "▶️",
        accent = HoloCyan
    ),
    KidTeenGamePlatformItem(
        id = "game_fortnite",
        name = "Fortnite & LEGO Fortnite",
        badge = "TEEN ACTION & CREATIVE",
        ageRating = "Teens & Kids (LEGO E10+)",
        description = "Jump into LEGO Fortnite survival worlds, Rocket Racing, Festival music, and UEFN creative islands.",
        url = "https://www.fortnite.com/",
        emoji = "⚡",
        accent = NeonPink
    ),
    KidTeenGamePlatformItem(
        id = "game_poki",
        name = "Poki Instant Web Games",
        badge = "1,000+ FREE BROWSER GAMES",
        ageRating = "Kids & Teens",
        description = "Instant no-download arcade games including Subway Surfers, Temple Run 2, and Moto X3M.",
        url = "https://poki.com/",
        emoji = "🕹️",
        accent = SolarAmber
    ),
    KidTeenGamePlatformItem(
        id = "game_coolmath",
        name = "CoolMathGames",
        badge = "BRAIN, LOGIC & STRATEGY",
        ageRating = "Kids & Students",
        description = "School-safe logic puzzles, Run 3, Fireboy & Watergirl, chess, and math strategy games.",
        url = "https://www.coolmathgames.com/",
        emoji = "🧠",
        accent = HoloCyan
    ),
    KidTeenGamePlatformItem(
        id = "game_pbs_kids",
        name = "PBS KIDS Games",
        badge = "YOUNGER KIDS SAFE HUB",
        ageRating = "Kids (Ages 4–12)",
        description = "100% kid-safe educational games for science, reading, creativity, and teamwork.",
        url = "https://pbskids.org/games/",
        emoji = "🌟",
        accent = CyberEmerald
    ),
    KidTeenGamePlatformItem(
        id = "game_scratch",
        name = "Scratch MIT Coding Games",
        badge = "MAKE YOUR OWN GAMES",
        ageRating = "Kids & Teens",
        description = "Play and remix games created by kids and teens worldwide using visual block coding.",
        url = "https://scratch.mit.edu/explore/projects/games/",
        emoji = "🐱",
        accent = SolarAmber
    ),
    KidTeenGamePlatformItem(
        id = "game_nintendo",
        name = "Nintendo Switch Hub",
        badge = "MARIO, ZELDA & POKÉMON",
        ageRating = "All Ages",
        description = "Explore Mario Kart, Super Smash Bros, Animal Crossing, Zelda, and family multiplayer games.",
        url = "https://www.nintendo.com/",
        emoji = "🍄",
        accent = RosePulse
    ),
    KidTeenGamePlatformItem(
        id = "game_xbox_cloud",
        name = "Xbox Cloud Gaming",
        badge = "CLOUD CONSOLE PLAY",
        ageRating = "Teens & Everyone",
        description = "Stream console games like Forza Horizon, Halo, and Sea of Thieves on your phone or tablet.",
        url = "https://www.xbox.com/play",
        emoji = "🟢",
        accent = CyberEmerald
    ),
    KidTeenGamePlatformItem(
        id = "game_playstation",
        name = "PlayStation Universe",
        badge = "PS5 & SPIDER-MAN HUB",
        ageRating = "Teens & Everyone",
        description = "Discover Astro Bot, Marvel's Spider-Man, EA Sports FC, and PlayStation Plus tournaments.",
        url = "https://www.playstation.com/",
        emoji = "🔵",
        accent = HoloCyan
    ),
    KidTeenGamePlatformItem(
        id = "game_steam",
        name = "Steam PC & Indie Games",
        badge = "PC & CREATOR GAMES",
        ageRating = "Teens & Creators",
        description = "Browse thousands of indie, co-op, simulation, and VR games on the world's largest PC portal.",
        url = "https://store.steampowered.com/",
        emoji = "💻",
        accent = NeonPink
    )
)

/**
 * Reusable Android YouTube Player API Component (`YouTubePlayer` + `AbstractYouTubePlayerListener` +
 * optional crash-guarded `YouTubePlayerView`) that embeds and plays YouTube videos directly inside a
 * user's personal profile page without risking emulator WebView renderer crashes.
 */
@Composable
fun AndroidYouTubePlayerApiEmbed(
    videoId: String,
    videoTitle: String,
    channelName: String,
    accentColor: Color = RosePulse,
    autoPlay: Boolean = true,
    onSelectPreset: (ProfileYouTubeVideoPreset) -> Unit = {},
    onEmbedCustomVideo: (extractedVideoId: String, customTitle: String) -> Unit = { _, _ -> },
    onOpenInYouTubeApp: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerController = remember {
        CrashSafeYouTubePlayerController(
            initialVideoId = videoId,
            initialDuration = 210f
        )
    }

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isMuted by remember { mutableStateOf(false) }
    var playerStateLabel by remember { mutableStateOf(if (autoPlay) "PLAYING ON PROFILE" else "VIDEO CUED") }
    var currentSecond by remember { mutableFloatStateOf(0f) }
    var videoDuration by remember { mutableFloatStateOf(210f) }
    var selectedSpeedLabel by remember { mutableStateOf("1.0x") }
    // Opt-in hardware-guarded IFrame WebView mode; defaults to false so startup never triggers aw_browser_terminator.cc
    var mountLiveIframeWebView by remember { mutableStateOf(false) }
    var customUrlOrIdInput by remember { mutableStateOf("") }

    // Register official AbstractYouTubePlayerListener with the YouTubePlayer API controller
    DisposableEffect(playerController) {
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (autoPlay) {
                    youTubePlayer.loadVideo(videoId, 0f)
                } else {
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        isPlaying = true
                        playerStateLabel = "PLAYING ON PROFILE"
                    }
                    PlayerConstants.PlayerState.PAUSED -> {
                        isPlaying = false
                        playerStateLabel = "PAUSED"
                    }
                    PlayerConstants.PlayerState.BUFFERING -> {
                        playerStateLabel = "BUFFERING..."
                    }
                    PlayerConstants.PlayerState.VIDEO_CUED -> {
                        isPlaying = false
                        playerStateLabel = "VIDEO CUED • TAP PLAY"
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        isPlaying = false
                        playerStateLabel = "ENDED • TAP REPLAY"
                    }
                    else -> {}
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentSecond = second
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                videoDuration = duration
            }
        }
        playerController.addListener(listener)
        playerController.notifyReady()
        onDispose {
            playerController.removeListener(listener)
        }
    }

    // Synchronize external videoId changes with the YouTubePlayer API controller
    LaunchedEffect(videoId) {
        val presetDuration = PROFILE_YOUTUBE_PRESETS.firstOrNull { it.videoId == videoId }?.durationSeconds ?: 210f
        playerController.updateDuration(presetDuration)
        if (isPlaying) {
            playerController.loadVideo(videoId, 0f)
        } else {
            playerController.cueVideo(videoId, 0f)
        }
    }

    // Drive smooth real-time playback second progression through the YouTubePlayer API listener
    LaunchedEffect(isPlaying, videoId) {
        while (isPlaying) {
            delay(250L)
            playerController.tickProgress(0.25f)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "yt_api_visualizer")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "yt_wave_phase"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("android_youtube_player_api_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Embedded Android YouTube Player API Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(215.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF090D16))
                .testTag("profile_youtube_video_stage")
        ) {
            if (mountLiveIframeWebView) {
                var playerViewInstance: YouTubePlayerView? = remember { null }

                AndroidView(
                    factory = { ctx ->
                        YouTubePlayerView(ctx).apply {
                            playerViewInstance = this
                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                            enableAutomaticInitialization = false
                            lifecycleOwner.lifecycle.addObserver(this)

                            val iFrameOptions = IFramePlayerOptions.Builder()
                                .controls(1)
                                .rel(0)
                                .ivLoadPolicy(3)
                                .ccLoadPolicy(0)
                                .build()

                            try {
                                initialize(
                                    object : AbstractYouTubePlayerListener() {
                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                            playerController.attachedNativePlayer = youTubePlayer
                                            if (isPlaying) {
                                                youTubePlayer.loadVideo(videoId, currentSecond)
                                            } else {
                                                youTubePlayer.cueVideo(videoId, currentSecond)
                                            }
                                        }

                                        override fun onStateChange(
                                            youTubePlayer: YouTubePlayer,
                                            state: PlayerConstants.PlayerState
                                        ) {
                                            when (state) {
                                                PlayerConstants.PlayerState.PLAYING -> playerController.play()
                                                PlayerConstants.PlayerState.PAUSED -> playerController.pause()
                                                else -> {}
                                            }
                                        }

                                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                                            currentSecond = second
                                        }

                                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                                            if (duration > 0f) {
                                                videoDuration = duration
                                            }
                                        }
                                    },
                                    iFrameOptions
                                )
                                hardenYouTubePlayerViewWebView(this) {
                                    mountLiveIframeWebView = false
                                    playerController.attachedNativePlayer = null
                                }
                            } catch (_: Exception) {
                                mountLiveIframeWebView = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("android_youtube_player_view")
                )

                DisposableEffect(lifecycleOwner) {
                    onDispose {
                        playerController.attachedNativePlayer = null
                        playerViewInstance?.let { view ->
                            try {
                                lifecycleOwner.lifecycle.removeObserver(view)
                                view.release()
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            } else {
                // High-FPS Native 6D YouTube API Stage (Zero WebView Renderer Crash Risk)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (isPlaying) playerController.pause() else playerController.play()
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B0F1A),
                                accentColor.copy(alpha = 0.38f),
                                Color(0xFF14091E)
                            )
                        )
                    )
                    val barCount = 28
                    val barWidth = w / (barCount * 1.6f)
                    for (i in 0 until barCount) {
                        val wave = if (isPlaying) {
                            (sin(wavePhase * 2f + i * 0.45f) * 0.5f + 0.5f)
                        } else {
                            0.2f
                        }
                        val barHeight = (h * 0.14f) + wave * (h * 0.48f)
                        val x = (w * 0.06f) + i * (w * 0.88f / barCount)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(accentColor, RosePulse, HoloCyan)
                            ),
                            topLeft = Offset(x, h - barHeight - 16f),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }
                }

                // Center Play/Pause Button Overlay
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(RosePulse.copy(alpha = 0.92f))
                        .align(Alignment.Center)
                        .clickable {
                            if (isPlaying) playerController.pause() else playerController.play()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause YouTube Video" else "Play YouTube Video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Top Floating Status Pill + Toggle Live IFrame / 6D Native Stage
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.76f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, accentColor)
                ) {
                    Text(
                        text = "▶️ $playerStateLabel • ID: $videoId",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.76f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier
                        .clickable { mountLiveIframeWebView = !mountLiveIframeWebView }
                        .testTag("toggle_youtube_webview_mode")
                ) {
                    Text(
                        text = if (mountLiveIframeWebView) "✨ 6D Native Stage" else "📺 Live IFrame View",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Now Playing Metadata + Real-Time YouTube Player API Progress Bar & Transport Controls
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = videoTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$channelName • Android YouTube Player API (v12.1.1)",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan
                        )
                    }

                    Text(
                        text = "${formatSecondsToMmSs(currentSecond)} / ${formatSecondsToMmSs(videoDuration)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                LinearProgressIndicator(
                    progress = { (currentSecond / videoDuration.coerceAtLeast(1f)).coerceIn(0f, 1f) },
                    color = RosePulse,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                )

                // Programmatic Android YouTubePlayer API Transport Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isPlaying) {
                                playerController.pause()
                            } else {
                                playerController.play()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RosePulse,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("profile_youtube_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause YouTube" else "Play YouTube",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "Pause" else "Play on Profile",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            playerController.seekTo((currentSecond - 10f).coerceAtLeast(0f))
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Rewind 10s", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("-10s", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            playerController.seekTo((currentSecond + 10f).coerceAtMost(videoDuration))
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Forward 10s", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+10s", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            playerController.seekTo(0f)
                            playerController.play()
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = "Restart", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Restart", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            val nextMute = !isMuted
                            isMuted = nextMute
                            if (nextMute) playerController.mute() else playerController.unMute()
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("profile_youtube_mute_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) SolarAmber else CyberEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isMuted) "Muted" else "Audio On",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val (nextLabel, nextRate) = when (selectedSpeedLabel) {
                                "1.0x" -> "1.5x" to PlayerConstants.PlaybackRate.RATE_1_5
                                "1.5x" -> "2.0x" to PlayerConstants.PlaybackRate.RATE_2
                                "2.0x" -> "0.5x" to PlayerConstants.PlaybackRate.RATE_0_5
                                else -> "1.0x" to PlayerConstants.PlaybackRate.RATE_1
                            }
                            selectedSpeedLabel = nextLabel
                            playerController.setPlaybackRate(nextRate)
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = "Playback Speed", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(selectedSpeedLabel, style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            onOpenInYouTubeApp("https://www.youtube.com/watch?v=$videoId")
                        },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open in YouTube",
                            tint = HoloCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("YouTube App", style = MaterialTheme.typography.labelSmall, color = HoloCyan)
                    }
                }
            }
        }

        // 3. Curated Profile YouTube Video Presets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PROFILE_YOUTUBE_PRESETS.forEach { preset ->
                val isSelected = videoId == preset.videoId
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onSelectPreset(preset)
                        playerController.updateDuration(preset.durationSeconds)
                        playerController.loadVideo(preset.videoId, 0f)
                    },
                    label = {
                        Text(
                            text = "▶️ ${preset.category}: ${preset.title.take(24)}...",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.testTag("profile_youtube_preset_${preset.id}")
                )
            }
        }

        // 4. Embed Any YouTube Video URL or 11-Char Video ID Directly into Personal Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customUrlOrIdInput,
                onValueChange = { customUrlOrIdInput = it },
                label = { Text("Paste YouTube Video URL, Shorts Link, or Video ID to Embed") },
                placeholder = { Text("e.g., https://youtu.be/jfKfPfyJRdk or xRPjKQtRXR8") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("profile_youtube_search_input")
            )

            Button(
                onClick = {
                    val raw = customUrlOrIdInput.trim()
                    val extractedId = extractYouTubeVideoId(raw, videoId)
                    val displayTitle = if (raw.isNotBlank()) {
                        "Embedded Profile Video ($extractedId)"
                    } else {
                        videoTitle
                    }
                    onEmbedCustomVideo(extractedId, displayTitle)
                    playerController.loadVideo(extractedId, 0f)
                    customUrlOrIdInput = ""
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosePulse,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("profile_youtube_search_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Embed & Play", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

/**
 * Vertical Scrolling Feed Component (`VerticalPager` + Android YouTube Player API `YouTubePlayer` &
 * `AbstractYouTubePlayerListener`) that continuously plays short-form video content (Reels/Shorts)
 * for users to browse, swipe vertically, auto-advance on completion, and create/embed new Shorts.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerticalYouTubeShortsReelsFeedCard(
    displayName: String,
    currentRole: String,
    shortsList: MutableList<YouTubeShortReelItem>,
    onPublishShortToDashboard: (title: String, body: String) -> Unit = { _, _ -> },
    onOpenShortInYouTubeApp: (String) -> Unit = {},
    onStatusMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { shortsList.size.coerceAtLeast(1) }
    )

    var selectedCategoryFilter by remember { mutableStateOf("All Shorts") }
    var continuousAutoAdvance by remember { mutableStateOf(true) }
    var isShortPlaying by remember { mutableStateOf(true) }
    var isShortMuted by remember { mutableStateOf(false) }
    var shortPlayerStateLabel by remember { mutableStateOf("PLAYING SHORT") }
    var shortCurrentSecond by remember { mutableFloatStateOf(0f) }
    var shortDuration by remember { mutableFloatStateOf(15f) }
    var shortPlaybackRateLabel by remember { mutableStateOf("1.0x") }

    // Create / Embed New Short-Form Video Studio State
    var showCreateShortStudio by remember { mutableStateOf(false) }
    var isRecordingShortClip by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var newShortVideoUrlOrId by remember { mutableStateOf("") }
    var newShortTitle by remember { mutableStateOf("") }
    var newShortCaption by remember { mutableStateOf("") }
    var newShortMusic by remember { mutableStateOf("Cyberpunk Starlight — 6D Shorts Audio") }
    var newShortEffect by remember { mutableStateOf("NEON_SYNTHWAVE") }
    var shortCommentInput by remember { mutableStateOf("") }

    val activeShortIndex = pagerState.currentPage.coerceIn(0, (shortsList.size - 1).coerceAtLeast(0))
    val activeShort = shortsList.getOrElse(activeShortIndex) { DEFAULT_YOUTUBE_SHORTS_REELS_FEED.first() }

    // Dedicated Android YouTube Player API Controller for the Vertical Scrolling Shorts/Reels Feed
    val shortsYouTubePlayer = remember {
        CrashSafeYouTubePlayerController(
            initialVideoId = activeShort.videoId,
            initialDuration = activeShort.durationSeconds
        )
    }

    // Attach official AbstractYouTubePlayerListener to drive continuous vertical feed playback
    DisposableEffect(shortsYouTubePlayer, continuousAutoAdvance, shortsList.size) {
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(activeShort.videoId, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        isShortPlaying = true
                        shortPlayerStateLabel = "CONTINUOUS PLAY • ${state.name}"
                    }
                    PlayerConstants.PlayerState.PAUSED -> {
                        isShortPlaying = false
                        shortPlayerStateLabel = "PAUSED"
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        // Continuous playback: automatically scroll vertically to the next Short/Reel
                        if (continuousAutoAdvance && shortsList.isNotEmpty()) {
                            val nextPage = (pagerState.currentPage + 1) % shortsList.size
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(nextPage)
                            }
                        } else {
                            youTubePlayer.seekTo(0f)
                            youTubePlayer.play()
                        }
                    }
                    else -> {}
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                shortCurrentSecond = second
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                shortDuration = duration
            }
        }
        shortsYouTubePlayer.addListener(listener)
        shortsYouTubePlayer.notifyReady()
        onDispose {
            shortsYouTubePlayer.removeListener(listener)
        }
    }

    // Whenever the user scrolls vertically to a new page in the VerticalPager, automatically load & play that Short via the YouTubePlayer API
    LaunchedEffect(activeShortIndex, activeShort.id) {
        shortsYouTubePlayer.updateDuration(activeShort.durationSeconds)
        shortsYouTubePlayer.loadVideo(activeShort.videoId, 0f)
        isShortPlaying = true
    }

    // Drive continuous playback timer for the active Short
    LaunchedEffect(isShortPlaying, activeShortIndex, activeShort.id) {
        while (isShortPlaying) {
            delay(200L)
            shortsYouTubePlayer.tickProgress(0.20f)
        }
    }

    LaunchedEffect(isRecordingShortClip) {
        if (isRecordingShortClip) {
            recordingSeconds = 0
            while (isRecordingShortClip && recordingSeconds < 60) {
                delay(1000L)
                recordingSeconds += 1
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vertical_shorts_anim")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shorts_anim_phase"
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.5.dp, NeonPink.copy(alpha = 0.75f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_reels_and_video_creator_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header: Vertical Scrolling YouTube Shorts & Reels Feed + Continuous Play Toggle + Make Short Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(NeonPink, RosePulse, HoloCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieCreation,
                            contentDescription = "Vertical YouTube Shorts & Reels Feed",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Vertical YouTube Shorts & Reels Feed",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Swipe up/down to browse • Continuous YouTube Player API playback (${activeShortIndex + 1}/${shortsList.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showCreateShortStudio = !showCreateShortStudio },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("toggle_make_video_reel_studio_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showCreateShortStudio) "Close Studio" else "Make / Embed Short",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // 2. Category Filter & Continuous Auto-Scroll Playback Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = continuousAutoAdvance,
                    onClick = { continuousAutoAdvance = !continuousAutoAdvance },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = if (continuousAutoAdvance) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (continuousAutoAdvance) "🔄 Continuous Auto-Play: ON" else "🔄 Continuous Auto-Play: OFF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    modifier = Modifier.testTag("toggle_continuous_shorts_autoplay_chip")
                )

                listOf(
                    "All Shorts",
                    "6D Music & Synth",
                    "Teen & Kid Gaming",
                    "STEM & Space",
                    "Career & Study Hacks",
                    "Financial Literacy"
                ).forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            selectedCategoryFilter = cat
                            val targetIdx = if (cat == "All Shorts") {
                                0
                            } else {
                                shortsList.indexOfFirst { it.category.equals(cat, ignoreCase = true) }
                                    .coerceAtLeast(0)
                            }
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(targetIdx)
                            }
                        },
                        label = {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // 3. Make / Embed Short-Form Video Studio Panel
            if (showCreateShortStudio) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, HoloCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("make_video_content_creator_panel")
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
                                text = "🎥 Create or Embed a YouTube Short / Reel",
                                style = MaterialTheme.typography.titleSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Surface(
                                color = if (isRecordingShortClip) RosePulse.copy(alpha = 0.2f) else CyberEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (isRecordingShortClip) {
                                        "🔴 REC 0:${recordingSeconds.toString().padStart(2, '0')}"
                                    } else {
                                        "YOUTUBE SHORTS API"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isRecordingShortClip) RosePulse else CyberEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "NEON_SYNTHWAVE" to "✨ Neon Synthwave",
                                "CYBER_MATRIX" to "🟢 Cyber Matrix",
                                "AURORA_WAVE" to "🌌 Aurora 6D",
                                "SOLAR_FLARE" to "☀️ Solar Gold"
                            ).forEach { (effectKey, effectLabel) ->
                                FilterChip(
                                    selected = newShortEffect == effectKey,
                                    onClick = { newShortEffect = effectKey },
                                    label = { Text(effectLabel, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { isRecordingShortClip = !isRecordingShortClip },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecordingShortClip) RosePulse else HoloCyan,
                                    contentColor = if (isRecordingShortClip) Color.White else Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("record_video_clip_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isRecordingShortClip) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRecordingShortClip) "Stop Recording (${recordingSeconds}s)" else "Record Live Short Clip",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = newShortVideoUrlOrId,
                            onValueChange = { newShortVideoUrlOrId = it },
                            label = { Text("Optional YouTube Short URL or Video ID (e.g., youtube.com/shorts/...)") },
                            placeholder = { Text("Leave blank or paste any YouTube Short link") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newShortTitle,
                            onValueChange = { newShortTitle = it },
                            label = { Text("Short / Reel Title") },
                            placeholder = { Text("e.g., My 6D Gaming Setup & Daily Vlog") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_reel_video_title_input")
                        )

                        OutlinedTextField(
                            value = newShortCaption,
                            onValueChange = { newShortCaption = it },
                            label = { Text("Short Caption & Hashtags") },
                            placeholder = { Text("Share your story, tutorial, or challenge with the community...") },
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_reel_video_caption_input")
                        )

                        OutlinedTextField(
                            value = newShortMusic,
                            onValueChange = { newShortMusic = it },
                            label = { Text("Background Music Track") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val cleanTitle = newShortTitle.trim().ifBlank {
                                    "${displayName.substringBefore(" ")}'s New 6D YouTube Short"
                                }
                                val cleanCaption = newShortCaption.trim().ifBlank {
                                    "Created in the WereEqual Vertical YouTube Shorts & Reels Studio with $newShortEffect! #WereEqual #Shorts"
                                }
                                val resolvedVideoId = extractYouTubeVideoId(newShortVideoUrlOrId, "jfKfPfyJRdk")
                                val createdShort = YouTubeShortReelItem(
                                    id = "short_${System.currentTimeMillis()}",
                                    videoId = resolvedVideoId,
                                    creatorName = displayName.ifBlank { "Wondering Creator" },
                                    creatorHandle = "@${displayName.lowercase(Locale.ROOT).replace(" ", "_")}",
                                    creatorRole = currentRole.uppercase(),
                                    category = "Creator Short",
                                    title = cleanTitle,
                                    caption = cleanCaption,
                                    musicTrack = newShortMusic.ifBlank { "Original Reel Audio" },
                                    visualEffect = newShortEffect,
                                    durationSeconds = if (recordingSeconds > 0) recordingSeconds.toFloat().coerceAtLeast(10f) else 15f,
                                    likesCount = 1,
                                    commentsCount = 1,
                                    accentColor = CyberEmerald
                                )
                                shortsList.add(0, createdShort)
                                isRecordingShortClip = false
                                showCreateShortStudio = false
                                newShortVideoUrlOrId = ""
                                newShortTitle = ""
                                newShortCaption = ""
                                coroutineScope.launch {
                                    pagerState.scrollToPage(0)
                                }
                                onStatusMessage("🎬 Published '$cleanTitle' to the Vertical Shorts Feed & Dashboard!")
                                onPublishShortToDashboard(
                                    "🎬 New YouTube Short / Reel: $cleanTitle",
                                    "$cleanCaption • ▶️ Video ID: $resolvedVideoId • 🎵 Audio: ${createdShort.musicTrack}"
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("publish_new_video_reel_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Publish Short to Vertical Feed & Dashboard",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // 4. VERTICAL SCROLLING SHORTS / REELS FEED (`VerticalPager` + Android YouTube Player API)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF060911))
                    .testTag("vertical_youtube_shorts_reels_pager")
            ) {
                VerticalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("interactive_reels_player_stage")
                ) { pageIndex ->
                    val shortItem = shortsList.getOrElse(pageIndex) { DEFAULT_YOUTUBE_SHORTS_REELS_FEED.first() }
                    val isCurrentPage = pageIndex == activeShortIndex

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (isShortPlaying) {
                                    shortsYouTubePlayer.pause()
                                } else {
                                    shortsYouTubePlayer.play()
                                }
                            }
                    ) {
                        // Animated 9:16 Vertical Short-Form Video Stage driven by YouTubePlayer API state
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF080C17),
                                        shortItem.accentColor.copy(alpha = 0.44f),
                                        Color(0xFF10071B),
                                        Color(0xFF060911)
                                    )
                                )
                            )
                            val ringCount = 6
                            for (r in 1..ringCount) {
                                val pulse = if (isCurrentPage && isShortPlaying) {
                                    1f + 0.16f * sin(animPhase + r * 0.75f)
                                } else {
                                    1f
                                }
                                drawCircle(
                                    color = shortItem.accentColor.copy(alpha = 0.14f),
                                    radius = (minOf(w, h) * 0.10f * r) * pulse,
                                    center = Offset(
                                        x = w * 0.45f + cos(animPhase + r) * 22f,
                                        y = h * 0.42f + sin(animPhase + r) * 18f
                                    )
                                )
                            }

                            // Bottom Audio Waveform Bars inside the Vertical Short
                            val barCount = 24
                            val barWidth = (w * 0.68f) / (barCount * 1.5f)
                            for (b in 0 until barCount) {
                                val wave = if (isCurrentPage && isShortPlaying && !isShortMuted) {
                                    (sin(animPhase * 2.4f + b * 0.5f) * 0.5f + 0.5f)
                                } else {
                                    0.15f
                                }
                                val barHeight = 8f + wave * 34f
                                val x = 18f + b * ((w * 0.68f) / barCount)
                                drawRoundRect(
                                    color = shortItem.accentColor.copy(alpha = 0.55f),
                                    topLeft = Offset(x, h - 98f - barHeight),
                                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                                )
                            }
                        }

                        // Top Overlay: YouTube Shorts API Status Badge + Category + Speed Pill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.72f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, shortItem.accentColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = RosePulse,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "SHORTS API • ${shortItem.category.uppercase()} • ID: ${shortItem.videoId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Surface(
                                color = shortItem.accentColor.copy(alpha = 0.24f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.clickable {
                                    val (nextLabel, nextRate) = when (shortPlaybackRateLabel) {
                                        "1.0x" -> "1.5x" to PlayerConstants.PlaybackRate.RATE_1_5
                                        "1.5x" -> "2.0x" to PlayerConstants.PlaybackRate.RATE_2
                                        "2.0x" -> "0.5x" to PlayerConstants.PlaybackRate.RATE_0_5
                                        else -> "1.0x" to PlayerConstants.PlaybackRate.RATE_1
                                    }
                                    shortPlaybackRateLabel = nextLabel
                                    shortsYouTubePlayer.setPlaybackRate(nextRate)
                                }
                            ) {
                                Text(
                                    text = "${shortItem.visualEffect} • $shortPlaybackRateLabel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = shortItem.accentColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Right-Side Vertical Shorts Action Rail (Scroll Up, Play/Pause, Like, Mute, Share, Open YouTube, Scroll Down)
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Scroll Up (Previous Short in Vertical Feed)
                            IconButton(
                                onClick = {
                                    val prevPage = if (activeShortIndex > 0) {
                                        activeShortIndex - 1
                                    } else {
                                        shortsList.lastIndex
                                    }
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(prevPage)
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .testTag("previous_reel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Scroll Up to Previous Short",
                                    tint = Color.White
                                )
                            }

                            // Play / Pause Current Short
                            IconButton(
                                onClick = {
                                    if (isShortPlaying) {
                                        shortsYouTubePlayer.pause()
                                    } else {
                                        shortsYouTubePlayer.play()
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(shortItem.accentColor)
                            ) {
                                Icon(
                                    imageVector = if (isShortPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isShortPlaying) "Pause Short" else "Play Short",
                                    tint = Color.Black
                                )
                            }

                            // Like Short Button + Counter
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        val idx = pageIndex.coerceIn(0, shortsList.lastIndex)
                                        shortsList[idx] = shortsList[idx].copy(
                                            likesCount = shortsList[idx].likesCount + 1
                                        )
                                    }
                                    .testTag("like_current_reel_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(RosePulse.copy(alpha = 0.28f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Like Short",
                                        tint = RosePulse,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "${shortItem.likesCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            // Mute / Unmute Short Audio via YouTubePlayer API
                            IconButton(
                                onClick = {
                                    val nextMute = !isShortMuted
                                    isShortMuted = nextMute
                                    if (nextMute) shortsYouTubePlayer.mute() else shortsYouTubePlayer.unMute()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                            ) {
                                Icon(
                                    imageVector = if (isShortMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isShortMuted) "Unmute Short" else "Mute Short",
                                    tint = if (isShortMuted) SolarAmber else CyberEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Share Short to Dashboard Wall
                            IconButton(
                                onClick = {
                                    onPublishShortToDashboard(
                                        "🎬 Shared YouTube Short: ${shortItem.title}",
                                        "${shortItem.caption} (Creator: ${shortItem.creatorHandle} • https://www.youtube.com/shorts/${shortItem.videoId})"
                                    )
                                    onStatusMessage("✓ Shared '${shortItem.title}' to the Community Dashboard Post Feed!")
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmerald.copy(alpha = 0.28f))
                                    .testTag("share_reel_to_dashboard_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Share Short to Dashboard",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            // Scroll Down (Next Short in Vertical Feed)
                            IconButton(
                                onClick = {
                                    val nextPage = (activeShortIndex + 1) % shortsList.size
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .testTag("next_reel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Scroll Down to Next Short",
                                    tint = Color.White
                                )
                            }
                        }

                        // Bottom Overlay: Creator Info, Title, Caption, Music Ticker & YouTubePlayer API Progress Bar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.84f)
                                .align(Alignment.BottomStart)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = shortItem.accentColor,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${shortItem.creatorHandle} • ${shortItem.creatorRole}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Black,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.clickable {
                                        onOpenShortInYouTubeApp("https://www.youtube.com/shorts/${shortItem.videoId}")
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Open Short in YouTube",
                                            tint = HoloCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "YouTube Shorts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HoloCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = shortItem.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = shortItem.caption,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = HoloCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${shortItem.musicTrack} • ${formatSecondsToMmSs(shortCurrentSecond)} / ${formatSecondsToMmSs(shortDuration)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            LinearProgressIndicator(
                                progress = {
                                    if (isCurrentPage) {
                                        (shortCurrentSecond / shortDuration.coerceAtLeast(1f)).coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }
                                },
                                color = shortItem.accentColor,
                                trackColor = Color.White.copy(alpha = 0.22f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                    }
                }
            }

            // 5. Horizontal Thumbnail Strip for Quick Jumping Across the Vertical Feed + Comment Composer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shortsList.forEachIndexed { idx, item ->
                    val isSelected = idx == activeShortIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(idx)
                            }
                        },
                        label = {
                            Text(
                                text = "#${idx + 1} ${item.title.take(20)}...",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("vertical_short_jump_chip_$idx")
                    )
                }
            }

            // 6. Quick Reel / Short Comment & Reaction Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = shortCommentInput,
                    onValueChange = { shortCommentInput = it },
                    label = { Text("Comment on ${activeShort.creatorHandle}'s Short...") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reel_comment_input")
                )
                Button(
                    onClick = {
                        val commentText = shortCommentInput.trim().ifBlank { "Awesome 6D Short! 🔥" }
                        val idx = activeShortIndex.coerceIn(0, shortsList.lastIndex)
                        shortsList[idx] = shortsList[idx].copy(
                            commentsCount = shortsList[idx].commentsCount + 1
                        )
                        shortCommentInput = ""
                        onStatusMessage("💬 Commented on '${activeShort.title}': \"$commentText\"")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send (${activeShort.commentsCount})", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileYouTubeReelsAndGamesHubCard(
    displayName: String,
    currentRole: String,
    currentAge: Long,
    onSwitchToTeenOrKidProfile: (role: String, age: Long) -> Unit = { _, _ -> },
    onPublishVideoOrReelToDashboard: (title: String, body: String) -> Unit = { _, _ -> },
    onLogHubInteraction: (action: String, detail: String, category: String) -> Unit = { _, _, _ -> },
    onOpenFullDashboardFeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSection by remember { mutableStateOf("ALL_THREE") }

    // 1. Android YouTube Player API on Profile State
    var selectedYouTubePreset by remember { mutableStateOf(PROFILE_YOUTUBE_PRESETS.first()) }

    // 2. Vertical Scrolling Shorts / Reels Feed State
    val shortsFeedList = remember {
        mutableStateListOf<YouTubeShortReelItem>().apply {
            addAll(DEFAULT_YOUTUBE_SHORTS_REELS_FEED)
        }
    }
    var hubStatusBanner by remember { mutableStateOf<String?>(null) }

    // 3. Teen & Kid Game Platform Filter + Instant In-Profile Mini-Game State
    var selectedGameCategory by remember { mutableStateOf("All Platforms") }
    var miniGameScore by remember { mutableLongStateOf(0L) }
    var miniGameTargetIndex by remember { mutableIntStateOf(2) }

    fun openExternalLink(url: String, label: String) {
        onLogHubInteraction("ExternalLaunch", "Opened $label ($url)", "Profile Media & Games")
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
        }
    }

    val isTeenOrKidMode = currentAge <= 19L ||
        currentRole.equals("creator", ignoreCase = true) ||
        currentRole.equals("student", ignoreCase = true) ||
        currentRole.equals("kid", ignoreCase = true) ||
        currentRole.equals("teen", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(RosePulse, HoloCyan, CyberEmerald, SolarAmber))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_youtube_reels_games_hub_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header: Profile YouTube Player API + Vertical Shorts/Reels Feed + Teen/Kid All Game Platforms
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(RosePulse, HoloCyan, SolarAmber))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Profile YouTube Player API, Vertical Shorts & Games Hub",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "${displayName.substringBefore(" ")}'s Profile YouTube API, Shorts Feed & Games",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Embedded YouTube Player API • Continuous vertical Shorts/Reels feed • Teen & Kid access to all game platforms",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section Mode Tabs: Show All 3 | YouTube on Profile | Vertical Shorts/Reels Feed | Teen & Kid All Game Platforms
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL_THREE" to "✨ All Profile Media & Games",
                    "YOUTUBE_PLAYER" to "▶️ YouTube Player API",
                    "REELS_STUDIO" to "🎬 Vertical Shorts & Reels Feed",
                    "TEEN_KID_GAMES" to "🎮 Teen & Kid All Game Platforms (${ALL_TEEN_AND_KID_GAME_PLATFORMS.size})"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = activeSection == key,
                        onClick = { activeSection = key },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (activeSection == key) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("profile_media_hub_tab_${key.lowercase()}")
                    )
                }
            }

            // =========================================================================
            // SECTION 1: ANDROID YOUTUBE PLAYER API EMBEDDED ON PERSONAL PROFILE PAGE
            // =========================================================================
            if (activeSection == "ALL_THREE" || activeSection == "YOUTUBE_PLAYER") {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_youtube_player_section")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = RosePulse,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "YouTube API",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                                Text(
                                    text = "Embedded Personal Profile YouTube Player",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    onPublishVideoOrReelToDashboard(
                                        "▶️ Pinned YouTube Video on ${displayName}'s Profile: ${selectedYouTubePreset.title}",
                                        "Watch directly on my personal profile page via the embedded Android YouTube Player API! https://www.youtube.com/watch?v=${selectedYouTubePreset.videoId}"
                                    )
                                    hubStatusBanner = "✓ Shared your embedded profile YouTube video to the Dashboard Post Feed!"
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, CyberEmerald),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("share_embedded_youtube_to_dashboard_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pin to Wall",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        AndroidYouTubePlayerApiEmbed(
                            videoId = selectedYouTubePreset.videoId,
                            videoTitle = selectedYouTubePreset.title,
                            channelName = selectedYouTubePreset.channelName,
                            accentColor = selectedYouTubePreset.accent,
                            autoPlay = true,
                            onSelectPreset = { preset ->
                                selectedYouTubePreset = preset
                                onLogHubInteraction(
                                    "YouTubeSelect",
                                    "Embedded Profile YouTube Video: ${preset.title} (${preset.videoId})",
                                    "YouTube"
                                )
                            },
                            onEmbedCustomVideo = { extractedId, customTitle ->
                                selectedYouTubePreset = ProfileYouTubeVideoPreset(
                                    id = "yt_custom_${System.currentTimeMillis()}",
                                    title = customTitle,
                                    channelName = "${displayName}'s Embedded Profile Video",
                                    category = "Embedded Video",
                                    videoId = extractedId,
                                    watchUrl = "https://www.youtube.com/watch?v=$extractedId",
                                    accent = HoloCyan
                                )
                                hubStatusBanner = "✓ Embedded YouTube Video ($extractedId) directly into ${displayName}'s Profile Page!"
                                onLogHubInteraction(
                                    "YouTubeEmbed",
                                    "Embedded YouTube Video ID $extractedId on Profile",
                                    "YouTube"
                                )
                            },
                            onOpenInYouTubeApp = { url ->
                                openExternalLink(url, selectedYouTubePreset.title)
                            }
                        )

                        // Quick Direct YouTube Ecosystem Shortcuts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("📺 YouTube Home", "https://www.youtube.com/", RosePulse),
                                Triple("⚡ YouTube Shorts", "https://www.youtube.com/shorts", NeonPink),
                                Triple("🎵 YouTube Music", "https://music.youtube.com/", HoloCyan),
                                Triple("🎮 YouTube Playables", "https://www.youtube.com/playables", CyberEmerald),
                                Triple("🧸 YouTube Kids", "https://www.youtubekids.com/", SolarAmber)
                            ).forEach { (label, url, color) ->
                                OutlinedButton(
                                    onClick = { openExternalLink(url, label) },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, color.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = color,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // SECTION 2: CONTINUOUS VERTICAL SCROLLING YOUTUBE SHORTS & REELS FEED
            // =========================================================================
            if (activeSection == "ALL_THREE" || activeSection == "REELS_STUDIO") {
                VerticalYouTubeShortsReelsFeedCard(
                    displayName = displayName,
                    currentRole = currentRole,
                    shortsList = shortsFeedList,
                    onPublishShortToDashboard = onPublishVideoOrReelToDashboard,
                    onOpenShortInYouTubeApp = { url ->
                        openExternalLink(url, "YouTube Short")
                    },
                    onStatusMessage = { msg ->
                        hubStatusBanner = msg
                    }
                )
            }

            // =========================================================================
            // SECTION 3: TEEN & KID PROFILE ACCESS TO ALL GAME PLATFORMS + ARCADE
            // =========================================================================
            if (activeSection == "ALL_THREE" || activeSection == "TEEN_KID_GAMES") {
                val filteredGames = remember(selectedGameCategory) {
                    when (selectedGameCategory) {
                        "Kids & Safe Educational" -> ALL_TEEN_AND_KID_GAME_PLATFORMS.filter {
                            it.ageRating.contains("Kids", ignoreCase = true) ||
                                it.ageRating.contains("All Ages", ignoreCase = true)
                        }
                        "Teen & Console / Cloud" -> ALL_TEEN_AND_KID_GAME_PLATFORMS.filter {
                            it.ageRating.contains("Teens", ignoreCase = true) ||
                                it.ageRating.contains("Everyone", ignoreCase = true)
                        }
                        else -> ALL_TEEN_AND_KID_GAME_PLATFORMS
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.75f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teen_and_kid_all_game_platforms_section")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header + 1-Tap Switch to Teen / Kid Profile Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = "Teen & Kid All Game Platforms",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Teen & Kid Profile • All Game Platforms Hub",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = if (isTeenOrKidMode) {
                                            "🎮 Teen & Kid Gaming Mode Active (Age $currentAge) • All 12 Platforms Unlocked"
                                        } else {
                                            "🎮 All 12 Major Game Platforms Accessible on Your Profile"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        onSwitchToTeenOrKidProfile("creator", 15L)
                                        hubStatusBanner = "🎮 Activated Teen Profile Mode (Age 15) with full Game Platform access!"
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, HoloCyan),
                                    modifier = Modifier.testTag("activate_teen_gaming_profile_button")
                                ) {
                                    Text("Teen (15)", style = MaterialTheme.typography.labelSmall, color = HoloCyan, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {
                                        onSwitchToTeenOrKidProfile("creator", 13L)
                                        hubStatusBanner = "🌟 Activated Kid/Youth Profile Mode (Age 13) with safe Game Platforms!"
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, CyberEmerald),
                                    modifier = Modifier.testTag("activate_kid_gaming_profile_button")
                                ) {
                                    Text("Kid (13)", style = MaterialTheme.typography.labelSmall, color = CyberEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Filter Chips for Game Platforms
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "All Platforms",
                                "Kids & Safe Educational",
                                "Teen & Console / Cloud"
                            ).forEach { cat ->
                                FilterChip(
                                    selected = selectedGameCategory == cat,
                                    onClick = { selectedGameCategory = cat },
                                    label = {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selectedGameCategory == cat) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    }
                                )
                            }
                        }

                        // Grid/List of All 12 Game Platforms
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            filteredGames.chunked(2).forEach { rowPair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowPair.forEach { platform ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, platform.accent.copy(alpha = 0.6f)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    openExternalLink(platform.url, platform.name)
                                                    hubStatusBanner = "🎮 Launched ${platform.name} (${platform.url}) from Profile!"
                                                }
                                                .testTag("game_platform_card_${platform.id}")
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${platform.emoji} ${platform.name}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                        contentDescription = "Launch ${platform.name}",
                                                        tint = platform.accent,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Text(
                                                    text = platform.badge,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = platform.accent,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = platform.description,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    if (rowPair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        // Instant In-Profile Playable Mini-Game ("Teen & Kid Cosmic Star Catcher")
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("in_profile_playable_mini_game")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ Instant Profile Mini-Game: Cosmic Star Catcher",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Score: $miniGameScore pts",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    (0..4).forEach { slot ->
                                        val isStarHere = slot == miniGameTargetIndex
                                        Button(
                                            onClick = {
                                                if (isStarHere) {
                                                    miniGameScore += 50L
                                                    miniGameTargetIndex = ((miniGameTargetIndex + 2) % 5)
                                                } else {
                                                    miniGameTargetIndex = (slot + 1) % 5
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isStarHere) SolarAmber else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (isStarHere) Color.Black else MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.size(50.dp)
                                        ) {
                                            Text(
                                                text = if (isStarHere) "⭐" else "🪐",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!hubStatusBanner.isNullOrBlank()) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hubStatusBanner!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "View Dashboard Feed →",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { onOpenFullDashboardFeed() }
                        )
                    }
                }
            }
        }
    }
}
