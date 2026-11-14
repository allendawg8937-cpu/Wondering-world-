package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class VideoReelComment(
    val id: String,
    val reelId: String,
    val authorName: String,
    val personaBadge: String,
    val text: String,
    val timestampLabel: String,
    val likes: Int = 1
)

data class VideoReelItem(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val personaCategory: String,
    val description: String,
    val youtubeVideoId: String,
    val videoUrl: String,
    val durationLabel: String,
    val accentHex: String,
    val likesCount: Int,
    val viewsLabel: String
)

private val INITIAL_VIDEO_REELS = listOf(
    VideoReelItem(
        id = "reel_nasa_earth_6d",
        title = "Earth from Orbit: 4K ISS Live Horizon & Planetary STEM",
        creatorName = "Creation Planet STEM Lab",
        creatorHandle = "@planet_stem",
        personaCategory = "Kid & Student STEM",
        description = "Watch the curvature of Earth from low-Earth orbit while exploring atmospheric physics and orbital mechanics.",
        youtubeVideoId = "86YLFOog4GM",
        videoUrl = "https://www.youtube.com/embed/86YLFOog4GM?autoplay=1&playsinline=1",
        durationLabel = "01:45",
        accentHex = "#00E5FF",
        likesCount = 428,
        viewsLabel = "14.2K views"
    ),
    VideoReelItem(
        id = "reel_kotlin_compose_3d",
        title = "Building 6D Holographic Interfaces with Jetpack Compose & WebGL",
        creatorName = "Teen Hackathon Guild",
        creatorHandle = "@teen_coders",
        personaCategory = "Teen & Coding",
        description = "Live walkthrough of custom Canvas shaders, 3D avatar turntables, and reactive Material 3 architecture.",
        youtubeVideoId = "cDabx3SjuOY",
        videoUrl = "https://www.youtube.com/embed/cDabx3SjuOY?autoplay=1&playsinline=1",
        durationLabel = "02:15",
        accentHex = "#EC4899",
        likesCount = 319,
        viewsLabel = "9.8K views"
    ),
    VideoReelItem(
        id = "reel_veteran_community_honor",
        title = "Veterans & Community Builders: PACT Act, GI Bill & Peer Leadership",
        creatorName = "Jordan Vance • Veteran Hub",
        creatorHandle = "@jordan_vance",
        personaCategory = "Veteran & Adult",
        description = "How veterans and community leaders connect across Stages Of Life for healthcare, housing stability, and entrepreneurship.",
        youtubeVideoId = "5qap5aO4i9A",
        videoUrl = "https://www.youtube.com/embed/5qap5aO4i9A?autoplay=1&playsinline=1",
        durationLabel = "03:10",
        accentHex = "#FF6D00",
        likesCount = 512,
        viewsLabel = "19.4K views"
    ),
    VideoReelItem(
        id = "reel_founder_sba_pitch",
        title = "From Zero to LLC & First SBA 7(a) Micro-Grant in 30 Days",
        creatorName = "Apex Founders Studio",
        creatorHandle = "@apex_founders",
        personaCategory = "Business & Finance",
        description = "Practical checklist for registering your LLC, opening business treasury accounts, and qualifying for startup grants.",
        youtubeVideoId = "jfKfPfyJRdk",
        videoUrl = "https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=1&playsinline=1",
        durationLabel = "02:30",
        accentHex = "#F59E0B",
        likesCount = 276,
        viewsLabel = "8.1K views"
    )
)

private val INITIAL_REEL_COMMENTS = listOf(
    VideoReelComment(
        id = "rc_1",
        reelId = "reel_nasa_earth_6d",
        authorName = "Maya Lin",
        personaBadge = "🎓 Student",
        text = "The orbital sunrise view is incredible! Using this for our astronomy study group.",
        timestampLabel = "2m ago",
        likes = 19
    ),
    VideoReelComment(
        id = "rc_2",
        reelId = "reel_nasa_earth_6d",
        authorName = "Leo Vance",
        personaBadge = "🚀 Kid Explorer",
        text = "I just finished the Solar Quest quiz right after watching this reel!",
        timestampLabel = "5m ago",
        likes = 14
    ),
    VideoReelComment(
        id = "rc_3",
        reelId = "reel_kotlin_compose_3d",
        authorName = "Devon Brooks",
        personaBadge = "🎵 Teen Creator",
        text = "The 3D avatar turntable + visual theme switcher works so smoothly!",
        timestampLabel = "4m ago",
        likes = 23
    ),
    VideoReelComment(
        id = "rc_4",
        reelId = "reel_veteran_community_honor",
        authorName = "Sgt. Marcus Hale",
        personaBadge = "🛡️ Veteran",
        text = "Thank you for putting the 988 Press 1 banner and GI Bill calculator right up front.",
        timestampLabel = "1m ago",
        likes = 41
    ),
    VideoReelComment(
        id = "rc_5",
        reelId = "reel_founder_sba_pitch",
        authorName = "Elena Rostova",
        personaBadge = "💼 Business",
        text = "Checked off step 4 of the LLC checklist today—exporting my Room financial backup next!",
        timestampLabel = "6m ago",
        likes = 17
    )
)

object PublishedVideoReelsStore {
    val reels = mutableStateListOf<VideoReelItem>().apply { addAll(INITIAL_VIDEO_REELS) }
    val comments = mutableStateListOf<VideoReelComment>().apply { addAll(INITIAL_REEL_COMMENTS) }

    fun publishAiGeneratedReel(
        title: String,
        description: String,
        category: String,
        creatorName: String,
        accentHex: String
    ) {
        val newReelId = "reel_ai_${System.currentTimeMillis()}"
        val item = VideoReelItem(
            id = newReelId,
            title = title,
            creatorName = creatorName.ifBlank { "Jordan Vance" },
            creatorHandle = "@${creatorName.lowercase().replace(" ", "_").ifBlank { "jordan_vance" }}",
            personaCategory = category,
            description = description,
            youtubeVideoId = "86YLFOog4GM",
            videoUrl = "https://www.youtube.com/embed/86YLFOog4GM?autoplay=1&playsinline=1",
            durationLabel = "00:20 • AI Clip",
            accentHex = accentHex,
            likesCount = 1,
            viewsLabel = "AI Generated Clip"
        )
        reels.add(0, item)
        comments.add(
            0,
            VideoReelComment(
                id = "rc_ai_${System.currentTimeMillis()}",
                reelId = newReelId,
                authorName = creatorName.ifBlank { "Jordan Vance" },
                personaBadge = "🎬 AI Video Creator",
                text = "Generated this short video clip from text using the AI Text-to-Video Studio! Drop your thoughts below.",
                timestampLabel = "Just now",
                likes = 5
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoReelsCommentsHubCard(
    currentUserDisplayName: String,
    currentPersonaRole: String,
    onShareReelOrCommentToWall: (category: String, title: String, content: String) -> Unit = { _, _, _ -> },
    onReportReelOrComment: (targetType: String, targetId: String, targetTitle: String, authorName: String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reels = PublishedVideoReelsStore.reels
    val allComments = PublishedVideoReelsStore.comments

    var currentReelIndex by rememberSaveable { mutableIntStateOf(0) }
    var isPlaying by rememberSaveable { mutableStateOf(true) }
    var useEmbeddedWebPlayer by rememberSaveable { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0.18f) }
    var newCommentText by rememberSaveable { mutableStateOf("") }
    var showAddReelDialog by rememberSaveable { mutableStateOf(false) }
    var statusBanner by rememberSaveable { mutableStateOf<String?>(null) }

    val activeReel = reels.getOrElse(currentReelIndex.coerceIn(0, (reels.size - 1).coerceAtLeast(0))) {
        INITIAL_VIDEO_REELS.first()
    }
    val reelAccent = parseThemeHex(activeReel.accentHex)

    val commentsForActiveReel = allComments.filter { it.reelId == activeReel.id }

    LaunchedEffect(isPlaying, activeReel.id) {
        if (isPlaying) {
            while (true) {
                delay(400L)
                playbackProgress = if (playbackProgress >= 1f) 0f else playbackProgress + 0.015f
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("video_reels_comments_hub_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        ),
        border = BorderStroke(1.5.dp, reelAccent.copy(alpha = 0.75f))
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(reelAccent, NeonPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Video Reels & Live Comments Studio",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "6D VIDEO REELS & LIVE COMMUNITY COMMENTS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = reelAccent
                        )
                        Text(
                            text = "Watch Stages Of Life video reels, react in real time, and post or reply to comments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showAddReelDialog = true },
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier.testTag("add_custom_video_reel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Post Video Reel",
                        tint = HoloCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Reel", color = HoloCyan, fontWeight = FontWeight.Bold)
                }
            }

            statusBanner?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberEmerald.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyberEmerald,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            // Reel Selector Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reels.forEachIndexed { index, reel ->
                    val selected = index == currentReelIndex
                    val chipColor = parseThemeHex(reel.accentHex)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            currentReelIndex = index
                            playbackProgress = 0.05f
                            isPlaying = true
                        },
                        label = {
                            Text(
                                text = "🎬 ${reel.personaCategory}: ${reel.title.take(26)}...",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.22f),
                            selectedLabelColor = chipColor
                        ),
                        modifier = Modifier.testTag("select_video_reel_chip_$index")
                    )
                }
            }

            // Video Reel Viewport (Visualizer + Optional Embedded YouTube WebView Stream)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF060A14),
                border = BorderStroke(1.5.dp, reelAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_reel_player_viewport")
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeReel.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${activeReel.creatorName} (${activeReel.creatorHandle}) • ${activeReel.viewsLabel} • ${activeReel.durationLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = reelAccent
                            )
                        }

                        FilterChip(
                            selected = useEmbeddedWebPlayer,
                            onClick = { useEmbeddedWebPlayer = !useEmbeddedWebPlayer },
                            label = {
                                Text(if (useEmbeddedWebPlayer) "Live Stream ON" else "6D Reel Stage")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                                selectedLabelColor = HoloCyan
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        if (useEmbeddedWebPlayer) {
                            AndroidView(
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        settings.mediaPlaybackRequiresUserGesture = false
                                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                                        webChromeClient = WebChromeClient()
                                        webViewClient = WebViewClient()
                                        loadUrl(activeReel.videoUrl)
                                    }
                                },
                                update = { webView ->
                                    if (webView.url != activeReel.videoUrl) {
                                        webView.loadUrl(activeReel.videoUrl)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            VideoReelAnimatedStageCanvas(
                                isPlaying = isPlaying,
                                accentColor = reelAccent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Overlay Badge Top-Left
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.72f),
                            border = BorderStroke(1.dp, reelAccent),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (isPlaying) "● PLAYING REEL • ${activeReel.personaCategory}" else "❚❚ PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = reelAccent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Overlay Comment Count & Likes Bottom-Right
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "❤️ ${activeReel.likesCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "💬 ${commentsForActiveReel.size} Comments",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = activeReel.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.86f)
                    )

                    LinearProgressIndicator(
                        progress = { playbackProgress.coerceIn(0f, 1f) },
                        color = reelAccent,
                        trackColor = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    // Player Transport & Social Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentReelIndex =
                                        if (currentReelIndex - 1 < 0) reels.lastIndex else currentReelIndex - 1
                                    playbackProgress = 0f
                                },
                                modifier = Modifier.testTag("video_reel_prev_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous Video Reel",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier.testTag("video_reel_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause Video Reel" else "Play Video Reel",
                                    tint = reelAccent
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentReelIndex = (currentReelIndex + 1) % reels.size
                                    playbackProgress = 0f
                                },
                                modifier = Modifier.testTag("video_reel_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next Video Reel",
                                    tint = Color.White
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    val updated = activeReel.copy(likesCount = activeReel.likesCount + 1)
                                    reels[currentReelIndex] = updated
                                    statusBanner = "Liked '${activeReel.title}' (+1 ❤️)"
                                },
                                modifier = Modifier.testTag("video_reel_like_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like Video Reel",
                                    tint = reelAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${activeReel.likesCount}", color = reelAccent, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = {
                                    runCatching {
                                        val watchUrl = "https://www.youtube.com/watch?v=${activeReel.youtubeVideoId}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open Full Video",
                                    tint = HoloCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Watch Full", color = HoloCyan)
                            }

                            IconButton(
                                onClick = {
                                    onReportReelOrComment(
                                        "POST",
                                        activeReel.id,
                                        "Video Reel: ${activeReel.title}",
                                        activeReel.creatorName
                                    )
                                    statusBanner = "Opened Content Report Dialog for '${activeReel.title}'"
                                },
                                modifier = Modifier.testTag("report_active_video_reel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Report Video Reel",
                                    tint = SolarAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Live Comments Section for Active Video Reel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Video Reel Comments",
                        tint = reelAccent
                    )
                    Text(
                        text = "Live Reel Comments (${commentsForActiveReel.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Quick Emoji Reaction Bar
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("🔥", "❤️", "👏", "🚀", "💡").forEach { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, reelAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                val quickComment = VideoReelComment(
                                    id = "rc_${System.currentTimeMillis()}",
                                    reelId = activeReel.id,
                                    authorName = currentUserDisplayName.ifBlank { "Jordan Vance" },
                                    personaBadge = "✨ ${currentPersonaRole.replaceFirstChar { it.uppercase() }}",
                                    text = "$emoji Loved this part of '${activeReel.title}'!",
                                    timestampLabel = "Just now",
                                    likes = 1
                                )
                                allComments.add(0, quickComment)
                                statusBanner = "Posted $emoji reaction comment on '${activeReel.title}' ✓"
                            }
                        ) {
                            Text(
                                text = emoji,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Comment Composer Input + Post Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    label = { Text("Add a comment on this video reel...") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("video_reel_comment_input")
                )
                Button(
                    onClick = {
                        val trimmed = newCommentText.trim()
                        if (trimmed.isNotEmpty()) {
                            val comment = VideoReelComment(
                                id = "rc_${System.currentTimeMillis()}",
                                reelId = activeReel.id,
                                authorName = currentUserDisplayName.ifBlank { "Jordan Vance" },
                                personaBadge = "🌟 ${currentPersonaRole.replaceFirstChar { it.uppercase() }}",
                                text = trimmed,
                                timestampLabel = "Just now",
                                likes = 1
                            )
                            allComments.add(0, comment)
                            onShareReelOrCommentToWall(
                                "Video Reel Comment",
                                "Commented on Reel: ${activeReel.title}",
                                trimmed
                            )
                            newCommentText = ""
                            statusBanner = "Posted comment on '${activeReel.title}' & synced to Community Feed ✓"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = reelAccent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("video_reel_post_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post Reel Comment",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comment", fontWeight = FontWeight.ExtraBold)
                }
            }

            // List of Comments for the Active Video Reel
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                commentsForActiveReel.forEach { comment ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, reelAccent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_reel_comment_item_${comment.id}")
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = reelAccent.copy(alpha = 0.18f)
                                    ) {
                                        Text(
                                            text = comment.personaBadge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = reelAccent,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = comment.timestampLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = comment.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        val idx = allComments.indexOfFirst { it.id == comment.id }
                                        if (idx >= 0) {
                                            allComments[idx] = comment.copy(likes = comment.likes + 1)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Like Comment",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${comment.likes}", color = CyberEmerald)
                                }

                                TextButton(
                                    onClick = {
                                        newCommentText = "@${comment.authorName} "
                                    }
                                ) {
                                    Text("Reply", color = HoloCyan)
                                }

                                TextButton(
                                    onClick = {
                                        onReportReelOrComment(
                                            "COMMENT",
                                            comment.id,
                                            comment.text.take(60),
                                            comment.authorName
                                        )
                                        statusBanner = "Flagged comment by ${comment.authorName} for admin review"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Report Comment",
                                        tint = SolarAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Report", color = SolarAmber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddReelDialog) {
        var newReelTitle by remember { mutableStateOf("") }
        var newReelDescription by remember { mutableStateOf("") }
        var newReelYoutubeId by remember { mutableStateOf("86YLFOog4GM") }

        AlertDialog(
            onDismissRequest = { showAddReelDialog = false },
            title = {
                Text("Post a New Video Reel", fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newReelTitle,
                        onValueChange = { newReelTitle = it },
                        label = { Text("Reel Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newReelDescription,
                        onValueChange = { newReelDescription = it },
                        label = { Text("Description / Caption") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newReelYoutubeId,
                        onValueChange = { newReelYoutubeId = it },
                        label = { Text("YouTube Video ID or URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanId = newReelYoutubeId.trim()
                            .substringAfterLast("v=", newReelYoutubeId.trim())
                            .substringAfterLast("/", newReelYoutubeId.trim())
                            .ifBlank { "86YLFOog4GM" }
                        val created = VideoReelItem(
                            id = "reel_${System.currentTimeMillis()}",
                            title = newReelTitle.ifBlank { "Community Spotlight Video Reel" },
                            creatorName = currentUserDisplayName.ifBlank { "Jordan Vance" },
                            creatorHandle = "@${currentUserDisplayName.lowercase().replace(" ", "_")}",
                            personaCategory = currentPersonaRole.replaceFirstChar { it.uppercase() },
                            description = newReelDescription.ifBlank { "Shared a new Stages Of Life community video reel!" },
                            youtubeVideoId = cleanId,
                            videoUrl = "https://www.youtube.com/embed/$cleanId?autoplay=1&playsinline=1",
                            durationLabel = "01:30",
                            accentHex = "#00E5FF",
                            likesCount = 1,
                            viewsLabel = "Just posted"
                        )
                        reels.add(0, created)
                        currentReelIndex = 0
                        onShareReelOrCommentToWall(
                            "Video Reel",
                            "Posted Video Reel: ${created.title}",
                            created.description
                        )
                        statusBanner = "Published Video Reel '${created.title}' ✓"
                        showAddReelDialog = false
                    }
                ) {
                    Text("Publish Reel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun VideoReelAnimatedStageCanvas(
    isPlaying: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_reel_stage")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283185f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reel_wave_phase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF050814),
                    accentColor.copy(alpha = 0.22f),
                    Color(0xFF050814)
                )
            )
        )

        val activePhase = if (isPlaying) phase else 0.8f
        for (ring in 1..4) {
            val radius = (h * 0.11f * ring) + sin(activePhase + ring) * 6f
            drawCircle(
                color = accentColor.copy(alpha = (0.36f / ring).coerceIn(0.06f, 0.35f)),
                radius = radius,
                center = Offset(cx, cy)
            )
        }

        // Animated audio/video equalizer bars across bottom of reel stage
        val barCount = 24
        val barWidth = (w * 0.72f) / barCount
        val startX = w * 0.14f
        for (i in 0 until barCount) {
            val wave = kotlin.math.abs(sin(activePhase * 2f + i * 0.45f))
            val barHeight = (h * 0.08f) + wave * (h * 0.26f)
            val bx = startX + i * barWidth
            drawLine(
                color = if (i % 2 == 0) accentColor else HoloCyan,
                start = Offset(bx, h * 0.82f),
                end = Offset(bx, h * 0.82f - barHeight),
                strokeWidth = barWidth * 0.55f
            )
        }
    }
}
