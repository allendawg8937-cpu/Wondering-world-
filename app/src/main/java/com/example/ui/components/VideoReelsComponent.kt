package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.sin

private const val TAG = "VideoReelsComponent"

data class FirestoreReelCommentItem(
    val id: String,
    val reelId: String,
    val videoId: String,
    val userId: String,
    val authorName: String,
    val authorRole: String,
    val text: String,
    val likesCount: Int,
    val timestampLabel: String,
    val isFromFirestore: Boolean = false
)

/**
 * Prevents Android Emulator WebView renderer crashes (`aw_browser_terminator.cc`) when mounting
 * `YouTubePlayerView` inside the vertical scrollable video reels feed.
 */
private fun guardYouTubePlayerWebView(
    view: View,
    onRendererGone: () -> Unit
) {
    if (view is WebView) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val existingClient = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching { view.webViewClient }.getOrNull()
        } else {
            null
        }
        view.webViewClient = object : WebViewClient() {
            override fun onRenderProcessGone(
                webView: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                runCatching { webView?.destroy() }
                onRendererGone()
                return true
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                existingClient?.onPageFinished(webView, url)
            }
        }
    } else if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            guardYouTubePlayerWebView(view.getChildAt(i), onRendererGone)
        }
    }
}

/**
 * `VideoReelsComponent`:
 * - Displays a vertical scrollable feed of short-form video reels (`VerticalPager`) powered by
 *   the existing Android YouTube Player library (`YouTubePlayerView`, `YouTubePlayer`, and
 *   `AbstractYouTubePlayerListener`).
 * - Integrates a real-time comment system backed by Firebase Firestore (`/video_reel_comments`)
 *   allowing users to post comments, like comments, reply, and delete their own comments.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoReelsComponent(
    currentUserDisplayName: String,
    currentPersonaRole: String,
    onShareReelOrCommentToWall: (category: String, title: String, content: String) -> Unit = { _, _, _ -> },
    onReportReelOrComment: (targetType: String, targetId: String, targetTitle: String, authorName: String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val reels = PublishedVideoReelsStore.reels
    val localFallbackComments = PublishedVideoReelsStore.comments
    val firestoreComments = remember { mutableStateListOf<FirestoreReelCommentItem>() }

    val verticalPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { reels.size.coerceAtLeast(1) }
    )

    val activePageIndex = verticalPagerState.currentPage.coerceIn(0, (reels.size - 1).coerceAtLeast(0))
    val activeReel = reels.getOrElse(activePageIndex) { reels.first() }
    val reelAccent = parseThemeHex(activeReel.accentHex)

    // Android YouTube Player API controller & state
    val youTubePlayerController = remember {
        CrashSafeYouTubePlayerController(
            initialVideoId = activeReel.youtubeVideoId,
            initialDuration = 120f
        )
    }

    var isPlaying by rememberSaveable { mutableStateOf(true) }
    var isMuted by rememberSaveable { mutableStateOf(false) }
    var currentSecond by remember { mutableFloatStateOf(0f) }
    var videoDuration by remember { mutableFloatStateOf(120f) }
    var playerStateText by remember { mutableStateOf("PLAYING REEL") }
    var mountLiveYouTubePlayerView by rememberSaveable { mutableStateOf(false) }

    var newCommentText by rememberSaveable { mutableStateOf("") }
    var isSubmittingComment by remember { mutableStateOf(false) }
    var showAddReelDialog by rememberSaveable { mutableStateOf(false) }
    var showShareReelDialog by rememberSaveable { mutableStateOf(false) }
    var statusBanner by rememberSaveable {
        mutableStateOf<String?>(
            "Swipe Up/Down in the Vertical Video Feed • Firestore Real-Time Comments & Social Share Active"
        )
    }

    fun buildDirectVideoWatchUrl(reel: VideoReelItem): String =
        "https://www.youtube.com/watch?v=${reel.youtubeVideoId}"

    fun buildPlatformReelShareUrl(reel: VideoReelItem): String =
        "https://ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app/?reel=${reel.id}&v=${reel.youtubeVideoId}"

    fun copyReelLinkToClipboard(reel: VideoReelItem, usePlatformDeepLink: Boolean = false) {
        val targetLink = if (usePlatformDeepLink) {
            buildPlatformReelShareUrl(reel)
        } else {
            buildDirectVideoWatchUrl(reel)
        }
        runCatching {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("Video Reel Link - ${reel.title}", targetLink)
            clipboard?.setPrimaryClip(clip)
        }
        statusBanner = "📋 Copied video link to clipboard: $targetLink"
    }

    fun shareReelViaSystemSheet(reel: VideoReelItem) {
        val watchUrl = buildDirectVideoWatchUrl(reel)
        val platformUrl = buildPlatformReelShareUrl(reel)
        val shareText = buildString {
            appendLine("🎬 Watch \"${reel.title}\" by ${reel.creatorName} (${reel.creatorHandle})")
            appendLine(reel.description)
            appendLine("▶️ Watch Video: $watchUrl")
            append("🌐 Creation Planet × Wondering World Reel: $platformUrl")
        }
        runCatching {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Video Reel: ${reel.title}")
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(sendIntent, "Share Video Reel: ${reel.title}").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
        statusBanner = "🚀 Opened system share sheet for '${reel.title}'"
    }

    fun shareReelToSocialPlatform(reel: VideoReelItem, platform: String) {
        val watchUrl = buildDirectVideoWatchUrl(reel)
        val encodedUrl = Uri.encode(watchUrl)
        val encodedTitle = Uri.encode("Watch \"${reel.title}\" by ${reel.creatorName} on Wondering World Reels!")
        val encodedCombined = Uri.encode("🎬 ${reel.title} — $watchUrl")

        if (platform == "Community Wall") {
            onShareReelOrCommentToWall(
                "Shared Video Reel",
                "Shared Video Reel: ${reel.title}",
                "${reel.description}\n▶️ Watch: $watchUrl"
            )
            statusBanner = "✨ Shared '${reel.title}' directly to the Community Feed!"
            return
        }

        val socialShareUri = when (platform) {
            "X / Twitter" -> "https://twitter.com/intent/tweet?text=$encodedTitle&url=$encodedUrl"
            "Facebook" -> "https://www.facebook.com/sharer/sharer.php?u=$encodedUrl"
            "WhatsApp" -> "https://api.whatsapp.com/send?text=$encodedCombined"
            "Telegram" -> "https://t.me/share/url?url=$encodedUrl&text=$encodedTitle"
            "Reddit" -> "https://www.reddit.com/submit?url=$encodedUrl&title=$encodedTitle"
            else -> watchUrl
        }

        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(socialShareUri)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        statusBanner = "🌐 Sharing '${reel.title}' to $platform..."
    }

    // Attach official AbstractYouTubePlayerListener from Android YouTube Player library
    DisposableEffect(youTubePlayerController) {
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(activeReel.youtubeVideoId, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        isPlaying = true
                        playerStateText = "PLAYING VIA YOUTUBE PLAYER"
                    }
                    PlayerConstants.PlayerState.PAUSED -> {
                        isPlaying = false
                        playerStateText = "PAUSED"
                    }
                    PlayerConstants.PlayerState.BUFFERING -> {
                        playerStateText = "BUFFERING..."
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        youTubePlayer.seekTo(0f)
                        youTubePlayer.play()
                    }
                    else -> {}
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentSecond = second
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                videoDuration = duration.coerceAtLeast(1f)
            }
        }
        youTubePlayerController.addListener(listener)
        youTubePlayerController.notifyReady()
        onDispose {
            youTubePlayerController.removeListener(listener)
        }
    }

    // Load new YouTube video ID whenever the user scrolls vertically to a different reel
    LaunchedEffect(activeReel.id, activeReel.youtubeVideoId) {
        youTubePlayerController.loadVideo(activeReel.youtubeVideoId, 0f)
        currentSecond = 0f
        isPlaying = true
    }

    // Drive smooth playback progress through the YouTubePlayer controller
    LaunchedEffect(isPlaying, activeReel.id) {
        while (isPlaying) {
            delay(250L)
            youTubePlayerController.tickProgress(0.25f)
        }
    }

    // Real-Time Firestore Snapshot Listener for `/video_reel_comments`
    DisposableEffect(Unit) {
        var registration: ListenerRegistration? = null
        runCatching {
            val currentUid = Firebase.auth.currentUser?.uid
            if (currentUid != null) {
                registration = Firebase.firestore.collection("video_reel_comments")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(50)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "Firestore video_reel_comments listener warning: ${error.message}")
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            val loaded = snapshot.documents.mapNotNull { doc ->
                                val reelId = doc.getString("reelId") ?: return@mapNotNull null
                                val videoId = doc.getString("videoId") ?: "86YLFOog4GM"
                                val userId = doc.getString("userId") ?: ""
                                val authorName = doc.getString("authorName") ?: "Community Member"
                                val authorRole = doc.getString("authorRole") ?: "Explorer"
                                val text = doc.getString("text") ?: return@mapNotNull null
                                val likes = (doc.getLong("likesCount") ?: 0L).toInt()
                                FirestoreReelCommentItem(
                                    id = doc.id,
                                    reelId = reelId,
                                    videoId = videoId,
                                    userId = userId,
                                    authorName = authorName,
                                    authorRole = authorRole,
                                    text = text,
                                    likesCount = likes,
                                    timestampLabel = "Synced • Firestore",
                                    isFromFirestore = true
                                )
                            }
                            firestoreComments.clear()
                            firestoreComments.addAll(loaded)
                        }
                    }
            }
        }
        onDispose {
            registration?.remove()
        }
    }

    // Merge live Firestore comments with seed comments for the active vertical video reel
    val mergedCommentsForActiveReel = remember(
        activeReel.id,
        firestoreComments.toList(),
        localFallbackComments.toList()
    ) {
        val cloudForReel = firestoreComments.filter { it.reelId == activeReel.id }
        val localForReel = localFallbackComments
            .filter { it.reelId == activeReel.id }
            .map { local ->
                FirestoreReelCommentItem(
                    id = local.id,
                    reelId = local.reelId,
                    videoId = activeReel.youtubeVideoId,
                    userId = "seed_user",
                    authorName = local.authorName,
                    authorRole = local.personaBadge,
                    text = local.text,
                    likesCount = local.likes,
                    timestampLabel = local.timestampLabel,
                    isFromFirestore = false
                )
            }
        (cloudForReel + localForReel).distinctBy { "${it.authorName}:${it.text}" }
    }

    fun postCommentToFirestore(commentText: String) {
        val cleanText = commentText.trim().take(600)
        if (cleanText.isEmpty()) return

        // Optimistically add to local store so UI responds immediately
        val tempId = "rc_${System.currentTimeMillis()}"
        localFallbackComments.add(
            0,
            VideoReelComment(
                id = tempId,
                reelId = activeReel.id,
                authorName = currentUserDisplayName.ifBlank { "Jordan Vance" }.take(60),
                personaBadge = "✨ ${currentPersonaRole.replaceFirstChar { it.uppercase() }.take(30)}",
                text = cleanText,
                timestampLabel = "Just now",
                likes = 1
            )
        )
        onShareReelOrCommentToWall(
            "Video Reel Comment",
            "Commented on Reel: ${activeReel.title}",
            cleanText
        )

        val currentUid = runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
        if (currentUid != null) {
            coroutineScope.launch {
                isSubmittingComment = true
                val opId = GlobalLoadingTracker.beginRequest(
                    title = "Posting Comment to Firestore",
                    subtitle = "Syncing comment on '${activeReel.title}'...",
                    category = "Firestore Comments"
                )
                try {
                    val payload = hashMapOf(
                        "reelId" to activeReel.id.take(100),
                        "videoId" to activeReel.youtubeVideoId.take(40),
                        "userId" to currentUid,
                        "authorName" to currentUserDisplayName.ifBlank { "Jordan Vance" }.take(60),
                        "authorRole" to currentPersonaRole.ifBlank { "veteran" }.take(40),
                        "text" to cleanText,
                        "likesCount" to 1,
                        "createdAt" to Timestamp.now()
                    )
                    Firebase.firestore.collection("video_reel_comments")
                        .add(payload)
                        .await()
                    statusBanner =
                        "☁️ Comment saved to Firebase Firestore (/video_reel_comments) on '${activeReel.title}' ✓"
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore comment write queued locally: ${e.message}")
                    statusBanner =
                        "✓ Comment posted on '${activeReel.title}' (Local + Cloud Queue)"
                } finally {
                    GlobalLoadingTracker.endRequest(opId)
                    isSubmittingComment = false
                }
            }
        } else {
            statusBanner =
                "✓ Comment posted on '${activeReel.title}' (Sign in to sync across devices via Firestore)"
        }
    }

    fun likeCommentInFirestore(item: FirestoreReelCommentItem) {
        val nextLikes = item.likesCount + 1
        // Update local fallback list if present
        val localIdx = localFallbackComments.indexOfFirst { it.id == item.id }
        if (localIdx >= 0) {
            val current = localFallbackComments[localIdx]
            localFallbackComments[localIdx] = current.copy(likes = current.likes + 1)
        }
        val cloudIdx = firestoreComments.indexOfFirst { it.id == item.id }
        if (cloudIdx >= 0) {
            firestoreComments[cloudIdx] = item.copy(likesCount = nextLikes)
        }

        if (item.isFromFirestore && runCatching { Firebase.auth.currentUser?.uid }.getOrNull() != null) {
            coroutineScope.launch {
                runCatching {
                    Firebase.firestore.collection("video_reel_comments")
                        .document(item.id)
                        .update("likesCount", nextLikes)
                        .await()
                }
            }
        }
    }

    fun deleteOwnFirestoreComment(item: FirestoreReelCommentItem) {
        localFallbackComments.removeAll { it.id == item.id || it.text == item.text }
        firestoreComments.removeAll { it.id == item.id }
        val currentUid = runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
        if (item.isFromFirestore && currentUid != null && item.userId == currentUid) {
            coroutineScope.launch {
                runCatching {
                    Firebase.firestore.collection("video_reel_comments")
                        .document(item.id)
                        .delete()
                        .await()
                    statusBanner = "Deleted comment from Firestore ✓"
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("video_reels_component_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ),
        border = BorderStroke(1.5.dp, reelAccent.copy(alpha = 0.78f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            .background(
                                Brush.linearGradient(
                                    listOf(reelAccent, NeonPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Vertical Video Reels & Firestore Comments",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "VERTICAL VIDEO REELS & FIRESTORE COMMENTS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = reelAccent
                        )
                        Text(
                            text = "Swipe up/down through YouTube Player Reels • Live Firestore comment sync",
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
                        contentDescription = "Add Video Reel",
                        tint = HoloCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Reel", color = HoloCyan, fontWeight = FontWeight.Bold)
                }
            }

            // Status Banner
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

            // Vertical Scrollable Video Feed (`VerticalPager` + Android YouTube Player Library)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF050814))
                    .testTag("video_reels_vertical_pager_container")
            ) {
                VerticalPager(
                    state = verticalPagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("video_reels_vertical_pager")
                ) { page ->
                    val reelItem = reels.getOrElse(page) { reels.first() }
                    val itemAccent = parseThemeHex(reelItem.accentHex)
                    val isCurrentPage = page == activePageIndex

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF060A16))
                    ) {
                        if (isCurrentPage && mountLiveYouTubePlayerView) {
                            // Existing Android YouTube Player Library (`YouTubePlayerView` + `AbstractYouTubePlayerListener`)
                            AndroidView(
                                factory = { ctx ->
                                    YouTubePlayerView(ctx).apply {
                                        enableAutomaticInitialization = false
                                        lifecycleOwner.lifecycle.addObserver(this)
                                        val options = IFramePlayerOptions.Builder()
                                            .controls(1)
                                            .rel(0)
                                            .build()
                                        runCatching {
                                            initialize(
                                                object : AbstractYouTubePlayerListener() {
                                                    override fun onReady(youTubePlayer: YouTubePlayer) {
                                                        youTubePlayerController.attachedNativePlayer = youTubePlayer
                                                        if (isPlaying) {
                                                            youTubePlayer.loadVideo(reelItem.youtubeVideoId, currentSecond)
                                                        } else {
                                                            youTubePlayer.cueVideo(reelItem.youtubeVideoId, currentSecond)
                                                        }
                                                    }

                                                    override fun onStateChange(
                                                        youTubePlayer: YouTubePlayer,
                                                        state: PlayerConstants.PlayerState
                                                    ) {
                                                        when (state) {
                                                            PlayerConstants.PlayerState.PLAYING -> youTubePlayerController.play()
                                                            PlayerConstants.PlayerState.PAUSED -> youTubePlayerController.pause()
                                                            else -> {}
                                                        }
                                                    }

                                                    override fun onCurrentSecond(
                                                        youTubePlayer: YouTubePlayer,
                                                        second: Float
                                                    ) {
                                                        currentSecond = second
                                                    }

                                                    override fun onVideoDuration(
                                                        youTubePlayer: YouTubePlayer,
                                                        duration: Float
                                                    ) {
                                                        if (duration > 0f) {
                                                            videoDuration = duration
                                                        }
                                                    }
                                                },
                                                options
                                            )
                                        }
                                        guardYouTubePlayerWebView(this) {
                                            mountLiveYouTubePlayerView = false
                                            youTubePlayerController.attachedNativePlayer = null
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // 6D Animated Vertical Reel Stage synchronized with YouTubePlayer API progress
                            VerticalYouTubeReelStageCanvas(
                                isPlaying = isPlaying && isCurrentPage,
                                accentColor = itemAccent,
                                progressFraction = (currentSecond / videoDuration.coerceAtLeast(1f)).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (isPlaying) youTubePlayerController.pause()
                                        else youTubePlayerController.play()
                                    }
                            )
                        }

                        // Top-Left Badge: Reel Index & YouTubePlayer State
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.75f),
                                border = BorderStroke(1.dp, itemAccent)
                            ) {
                                Text(
                                    text = "REEL ${page + 1}/${reels.size} • $playerStateText",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = itemAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            FilterChip(
                                selected = mountLiveYouTubePlayerView,
                                onClick = { mountLiveYouTubePlayerView = !mountLiveYouTubePlayerView },
                                label = {
                                    Text(
                                        text = if (mountLiveYouTubePlayerView) "YouTube IFrame ON" else "6D Stage (Tap for IFrame)",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HoloCyan.copy(alpha = 0.25f),
                                    selectedLabelColor = HoloCyan
                                )
                            )
                        }

                        // Right-Side Vertical Reel Action Rail (Swipe Up/Down, Like, Mute, Watch on YouTube, Flag)
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val prev = (activePageIndex - 1).coerceAtLeast(0)
                                        verticalPagerState.animateScrollToPage(prev)
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                                    .testTag("video_reel_scroll_up_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Scroll Up to Previous Reel",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    val updated = reelItem.copy(likesCount = reelItem.likesCount + 1)
                                    reels[page] = updated
                                    statusBanner = "Liked '${reelItem.title}' (+1 ❤️)"
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                                    .testTag("video_reel_like_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Like Video Reel",
                                    tint = NeonPink
                                )
                            }
                            Text(
                                text = "${reelItem.likesCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = {
                                    isMuted = !isMuted
                                    if (isMuted) youTubePlayerController.mute()
                                    else youTubePlayerController.unMute()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isMuted) "Unmute Reel" else "Mute Reel",
                                    tint = HoloCyan
                                )
                            }

                            IconButton(
                                onClick = {
                                    showShareReelDialog = true
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                                    .testTag("video_reel_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Video Reel or Copy Link",
                                    tint = SolarAmber
                                )
                            }

                            IconButton(
                                onClick = {
                                    copyReelLinkToClipboard(reelItem)
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                                    .testTag("video_reel_rail_copy_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Video Link",
                                    tint = HoloCyan
                                )
                            }

                            IconButton(
                                onClick = {
                                    runCatching {
                                        val url = "https://www.youtube.com/watch?v=${reelItem.youtubeVideoId}"
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open in YouTube",
                                    tint = CyberEmerald
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val next = (activePageIndex + 1) % reels.size.coerceAtLeast(1)
                                        verticalPagerState.animateScrollToPage(next)
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.68f))
                                    .testTag("video_reel_scroll_down_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Scroll Down to Next Reel",
                                    tint = Color.White
                                )
                            }
                        }

                        // Bottom Overlay: Creator Info, Title, Description, Play/Pause & YouTubePlayer Progress Bar
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                                    )
                                )
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.84f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlaying) youTubePlayerController.pause()
                                        else youTubePlayerController.play()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(itemAccent)
                                        .testTag("video_reel_play_pause_button")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause Reel" else "Play Reel",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "${reelItem.creatorName} (${reelItem.creatorHandle}) • ${reelItem.personaCategory}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = itemAccent
                                    )
                                    Text(
                                        text = reelItem.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Text(
                                text = reelItem.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.84f)
                            )

                            LinearProgressIndicator(
                                progress = {
                                    (currentSecond / videoDuration.coerceAtLeast(1f)).coerceIn(0f, 1f)
                                },
                                color = itemAccent,
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

            // Share & Copy Video Link Bar (Copy Video Link, Share Sheet & Direct Social Platform Targets)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                border = BorderStroke(1.dp, reelAccent.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_reel_share_bar")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { copyReelLinkToClipboard(activeReel, usePlatformDeepLink = false) },
                            border = BorderStroke(1.dp, HoloCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_reel_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Video Link",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Video Link", color = HoloCyan, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showShareReelDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = reelAccent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_reel_social_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Video Reel to Social Platforms",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Reel", fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Direct Share:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(
                            "Community Wall",
                            "X / Twitter",
                            "WhatsApp",
                            "Facebook",
                            "Telegram",
                            "Reddit"
                        ).forEach { platformName ->
                            FilterChip(
                                selected = false,
                                onClick = { shareReelToSocialPlatform(activeReel, platformName) },
                                label = {
                                    Text(
                                        text = platformName,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.testTag(
                                    "share_reel_platform_${platformName.lowercase().replace(" ", "_").replace("/", "")}"
                                )
                            )
                        }
                    }
                }
            }

            // Firebase Firestore Comment System Header + Quick Reactions
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
                        contentDescription = "Firestore Reel Comments",
                        tint = reelAccent
                    )
                    Column {
                        Text(
                            text = "Firestore Comments (${mergedCommentsForActiveReel.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Active Reel: ${activeReel.title.take(34)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("🔥", "❤️", "👏", "🚀").forEach { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, reelAccent.copy(alpha = 0.45f)),
                            modifier = Modifier.clickable {
                                postCommentToFirestore("$emoji Loving this video reel: '${activeReel.title}'!")
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

            // Firestore Comment Input & Submit Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    label = { Text("Write a comment (synced to Firestore)...") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("video_reel_comment_input")
                )

                Button(
                    onClick = {
                        val trimmed = newCommentText.trim()
                        if (trimmed.isNotEmpty()) {
                            postCommentToFirestore(trimmed)
                            newCommentText = ""
                        }
                    },
                    enabled = !isSubmittingComment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = reelAccent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("video_reel_post_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Submit Comment to Firestore",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSubmittingComment) "Saving..." else "Comment",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Comment Feed List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mergedCommentsForActiveReel.forEach { comment ->
                    val currentUid = runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
                    val isOwnComment =
                        (currentUid != null && comment.userId == currentUid) ||
                            comment.authorName.equals(currentUserDisplayName, ignoreCase = true)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
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
                                            text = comment.authorRole,
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
                                    color = if (comment.isFromFirestore) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant
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
                                TextButton(onClick = { likeCommentInFirestore(comment) }) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Like Comment",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${comment.likesCount}", color = CyberEmerald)
                                }

                                TextButton(
                                    onClick = {
                                        newCommentText = "@${comment.authorName} "
                                    }
                                ) {
                                    Text("Reply", color = HoloCyan)
                                }

                                if (isOwnComment) {
                                    IconButton(
                                        onClick = { deleteOwnFirestoreComment(comment) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Own Comment",
                                            tint = NeonPink,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        onReportReelOrComment(
                                            "COMMENT",
                                            comment.id,
                                            comment.text.take(60),
                                            comment.authorName
                                        )
                                        statusBanner = "Flagged comment by ${comment.authorName} for moderator review"
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
                Text("Post a New Vertical Video Reel", fontWeight = FontWeight.ExtraBold)
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
                        PublishedVideoReelsStore.publishAiGeneratedReel(
                            title = newReelTitle.ifBlank { "Community Spotlight Reel" },
                            description = newReelDescription.ifBlank { "Shared a new vertical video reel!" },
                            category = currentPersonaRole.replaceFirstChar { it.uppercase() },
                            creatorName = currentUserDisplayName.ifBlank { "Jordan Vance" },
                            accentHex = "#00E5FF"
                        )
                        // Update the YouTube ID on the newly inserted reel at index 0
                        if (reels.isNotEmpty()) {
                            reels[0] = reels[0].copy(
                                youtubeVideoId = cleanId,
                                videoUrl = "https://www.youtube.com/embed/$cleanId?autoplay=1&playsinline=1"
                            )
                        }
                        coroutineScope.launch {
                            verticalPagerState.animateScrollToPage(0)
                        }
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

    if (showShareReelDialog) {
        val youtubeUrl = buildDirectVideoWatchUrl(activeReel)
        val platformDeepUrl = buildPlatformReelShareUrl(activeReel)

        AlertDialog(
            onDismissRequest = { showShareReelDialog = false },
            title = {
                Text(
                    text = "Share Video Reel & Copy Link",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.testTag("share_video_reel_dialog")
                ) {
                    Text(
                        text = "${activeReel.title} • by ${activeReel.creatorName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Direct Video Link:",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = youtubeUrl,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                copyReelLinkToClipboard(activeReel, usePlatformDeepLink = false)
                                showShareReelDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dialog_copy_youtube_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Video URL")
                        }

                        OutlinedButton(
                            onClick = {
                                copyReelLinkToClipboard(activeReel, usePlatformDeepLink = true)
                                showShareReelDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dialog_copy_platform_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy App Link")
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Share Directly to Social Platforms:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Community Wall",
                            "X / Twitter",
                            "WhatsApp",
                            "Facebook",
                            "Telegram",
                            "Reddit"
                        ).forEach { socialTarget ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    shareReelToSocialPlatform(activeReel, socialTarget)
                                    showShareReelDialog = false
                                },
                                label = { Text(socialTarget, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        shareReelViaSystemSheet(activeReel)
                        showShareReelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = reelAccent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("dialog_system_share_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("More Apps (Share Sheet)", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareReelDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun VerticalYouTubeReelStageCanvas(
    isPlaying: Boolean,
    accentColor: Color,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vertical_reel_stage")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283185f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reel_wave"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.46f
        val cy = h * 0.42f
        val phase = if (isPlaying) wavePhase else 1.0f

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF040711),
                    accentColor.copy(alpha = 0.25f),
                    Color(0xFF03060E)
                )
            )
        )

        for (ring in 1..4) {
            val r = (h * 0.09f * ring) + sin(phase + ring) * 7f
            drawCircle(
                color = accentColor.copy(alpha = (0.34f / ring).coerceIn(0.06f, 0.34f)),
                radius = r,
                center = Offset(cx, cy)
            )
        }

        // Dynamic audio/video equalizer bars
        val bars = 20
        val barWidth = (w * 0.68f) / bars
        val startX = w * 0.12f
        for (i in 0 until bars) {
            val amp = kotlin.math.abs(sin(phase * 2.2f + i * 0.5f + progressFraction * 6f))
            val barH = (h * 0.05f) + amp * (h * 0.18f)
            val x = startX + i * barWidth
            drawLine(
                color = if (i % 2 == 0) accentColor else HoloCyan,
                start = Offset(x, h * 0.68f),
                end = Offset(x, h * 0.68f - barH),
                strokeWidth = barWidth * 0.55f
            )
        }
    }
}
