package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.data.model.UserProfile
import com.example.ui.components.CustomMessengerIcon
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.UserAccountBadge
import com.example.ui.components.UserBadgeStyle
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WereEqualChatThread(
    val id: String,
    val participantName: String,
    val participantRole: String,
    val avatarEmoji: String,
    val topicBadge: String,
    val accentHex: String,
    val isOnline: Boolean = true,
    val tags: List<String> = emptyList()
)

data class WereEqualChatMessage(
    val id: String,
    val threadId: String,
    val senderName: String,
    val senderRole: String,
    val text: String,
    val timestampLabel: String,
    val formattedDateTime: String = "10:05 AM • Nov 9, 2026",
    val isFromCurrentUser: Boolean,
    val deliveryStatus: String = "Delivered",
    val reactionsCount: Int = 0
)

data class WereEqualFriendRequest(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val avatarEmoji: String,
    val mutualFriends: Int,
    val note: String,
    val timeAgo: String,
    val status: String = "PENDING" // "PENDING", "ACCEPTED", "DECLINED"
)

data class AdaptiveSocialPost(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val avatarEmoji: String,
    val circleTag: String,
    val headline: String,
    val body: String,
    val tags: List<String>,
    val accentHex: String,
    val likesCount: Int,
    val commentsCount: Int,
    val timeAgo: String,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val latestReply: String? = null
)

/**
 * Computes how well a thread or social post fits the current user based on
 * their life-stage role, declared interests, learned interests/research/lifestyle,
 * and live in-session topic affinity weights.
 */
fun computeAdaptiveAffinityPercent(
    itemTags: List<String>,
    itemRole: String,
    profile: UserProfile,
    learnedInterests: Set<String>,
    learnedResearch: Set<String>,
    learnedLifestyle: Set<String>,
    topicAffinities: Map<String, Int>
): Int {
    var score = 70
    if (itemRole.contains(profile.role, ignoreCase = true)) {
        score += 12
    }
    val userKeywords = (
        profile.interests.map { it.lowercase() } +
            learnedInterests.map { it.lowercase() } +
            learnedResearch.map { it.lowercase() } +
            learnedLifestyle.map { it.lowercase() } +
            listOf(profile.role.lowercase())
        ).distinct()

    itemTags.forEach { rawTag ->
        val tagLower = rawTag.lowercase()
        if (userKeywords.any { kw -> tagLower.contains(kw) || kw.contains(tagLower) }) {
            score += 8
        }
        score += (topicAffinities[rawTag] ?: 0) * 4
    }
    return score.coerceIn(68, 99)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WereEqualMessagingScreen(
    profile: UserProfile,
    learnedSignals: List<String> = emptyList(),
    learnedInterests: Set<String> = emptySet(),
    learnedLifestyle: Set<String> = emptySet(),
    learnedResearch: Set<String> = emptySet(),
    onRecordLearnedSignal: (String) -> Unit = {},
    onAddCustomLearningSignal: (String, String) -> Unit = { _, _ -> },
    onToggleLearnedInterest: (String) -> Unit = {},
    onOpenFriendsDirectory: () -> Unit = {},
    onShareMessageToWall: (String, String) -> Unit = { _, _ -> },
    onLogMessagingInteraction: (String) -> Unit = {},
    onStartVideoCall: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeMessagingTab by remember { mutableStateOf("MESSAGES") } // "MESSAGES", "FOR_YOU", "FRIEND_REQUESTS"
    var selectedFeedTopicFilter by remember { mutableStateOf("ALL") }

    // Live per-topic engagement counter that learns from user actions as they go
    val topicAffinities = remember {
        mutableStateMapOf(
            "3D Art" to 2,
            "Career" to 2,
            "STEM" to 1,
            "Wellness" to 1,
            "Business" to 1,
            "Veteran" to 1
        )
    }

    fun recordTopicEngagement(topic: String, actionDetail: String) {
        topicAffinities[topic] = (topicAffinities[topic] ?: 0) + 1
        onRecordLearnedSignal("Messenger learned $topic: $actionDetail")
        onAddCustomLearningSignal("interest", topic)
    }

    val friendRequests = remember {
        mutableStateListOf(
            WereEqualFriendRequest(
                id = "req_1",
                senderName = "Kai Chen",
                senderRole = "student",
                avatarEmoji = "🚀",
                mutualFriends = 3,
                note = "Hey! Saw your project in the Student & STEM study group. Would love to connect and share study notes!",
                timeAgo = "15m ago",
                status = "PENDING"
            ),
            WereEqualFriendRequest(
                id = "req_2",
                senderName = "Sofia Morales",
                senderRole = "business",
                avatarEmoji = "💼",
                mutualFriends = 5,
                note = "Hi! Let's connect on local community business partnerships and WereEqual Marketplace listings.",
                timeAgo = "1h ago",
                status = "PENDING"
            ),
            WereEqualFriendRequest(
                id = "req_3",
                senderName = "Derrick 'Doc' Walker",
                senderRole = "veteran",
                avatarEmoji = "🛡️",
                mutualFriends = 8,
                note = "Greetings from Austin Veterans chapter. Adding you for the weekend mentorship roundtable.",
                timeAgo = "3h ago",
                status = "PENDING"
            )
        )
    }

    var sendFriendNameInput by remember { mutableStateOf("") }
    var sendFriendRoleInput by remember { mutableStateOf("student") }
    var sendFriendNoteInput by remember { mutableStateOf("") }

    val threads = remember {
        mutableStateListOf(
            WereEqualChatThread(
                id = "thread_student_hub",
                participantName = "Maya Lin & Student Study Pod",
                participantRole = "STUDENT & STEM MENTOR",
                avatarEmoji = "🎓",
                topicBadge = "STEM & Career Apps",
                accentHex = "#2563EB",
                isOnline = true,
                tags = listOf("STEM", "Career", "Student")
            ),
            WereEqualChatThread(
                id = "thread_veteran_support",
                participantName = "Marcus Sterling",
                participantRole = "VETERAN MENTOR",
                avatarEmoji = "🛡️",
                topicBadge = "VA & Tech Careers",
                accentHex = "#06B6D4",
                isOnline = true,
                tags = listOf("Veteran", "Career", "Wellness")
            ),
            WereEqualChatThread(
                id = "thread_business_market",
                participantName = "Elena Rostova",
                participantRole = "BUSINESS & MARKETPLACE",
                avatarEmoji = "💼",
                topicBadge = "Marketplace & Wallet",
                accentHex = "#10B981",
                isOnline = true,
                tags = listOf("Business", "Finance", "Career")
            ),
            WereEqualChatThread(
                id = "thread_creator_studio",
                participantName = "Alex Rivera",
                participantRole = "CONTENT CREATOR",
                avatarEmoji = "🎬",
                topicBadge = "3D Art & Music Collab",
                accentHex = "#EC4899",
                isOnline = false,
                tags = listOf("3D Art", "Creator", "Music")
            )
        )
    }

    val socialFeedPosts = remember {
        mutableStateListOf(
            AdaptiveSocialPost(
                id = "feed_post_1",
                authorName = "Maya Lin",
                authorRole = "student",
                avatarEmoji = "🎓",
                circleTag = "STEM & Career Lab",
                headline = "Built a 3D Portfolio & Paid Internship Checklist for Fall",
                body = "Combining interactive 3D avatars with real budgeting milestones helped me land two engineering interviews this week. Happy to review resumes here!",
                tags = listOf("STEM", "Career", "3D Art"),
                accentHex = "#2563EB",
                likesCount = 24,
                commentsCount = 5,
                timeAgo = "12m ago"
            ),
            AdaptiveSocialPost(
                id = "feed_post_2",
                authorName = "Marcus Sterling",
                authorRole = "veteran",
                avatarEmoji = "🛡️",
                circleTag = "Veteran Transition & Wellness",
                headline = "Evening Peer Check-In + GI Bill Cloud Certification Path",
                body = "Friendly reminder: 15 minutes of breathwork plus steady daily study beats burnout every time. Drop your current goal below so our circle can back you up.",
                tags = listOf("Veteran", "Wellness", "Career"),
                accentHex = "#06B6D4",
                likesCount = 31,
                commentsCount = 8,
                timeAgo = "38m ago"
            ),
            AdaptiveSocialPost(
                id = "feed_post_3",
                authorName = "Elena Rostova",
                authorRole = "business",
                avatarEmoji = "💼",
                circleTag = "Founders & Community Economy",
                headline = "Local Creator Marketplace & Micro-Savings Challenge",
                body = "Set up a small weekly reserve target in Wallet & Goals alongside one creative product listing—consistency compounds fast.",
                tags = listOf("Business", "Finance"),
                accentHex = "#10B981",
                likesCount = 19,
                commentsCount = 4,
                timeAgo = "1h ago"
            ),
            AdaptiveSocialPost(
                id = "feed_post_4",
                authorName = "Alex Rivera",
                authorRole = "creator",
                avatarEmoji = "🎬",
                circleTag = "3D Holographic Studio",
                headline = "New Turntable Shaders & Dance Poses Live in Studio!",
                body = "Experimenting with Cyber Neon rims and orb drones in the 3D customizer. Share your favorite stage theme color combo!",
                tags = listOf("3D Art", "Creator", "Music"),
                accentHex = "#EC4899",
                likesCount = 42,
                commentsCount = 11,
                timeAgo = "2h ago"
            )
        )
    }

    var newSocialPostDraft by remember { mutableStateOf("") }
    var newSocialPostTopic by remember { mutableStateOf("Career") }
    var replyDraftByPostId by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Rank threads & social posts dynamically from user profile + learned signals
    val rankedThreads = threads.sortedByDescending { thread ->
        computeAdaptiveAffinityPercent(
            itemTags = thread.tags,
            itemRole = thread.participantRole,
            profile = profile,
            learnedInterests = learnedInterests,
            learnedResearch = learnedResearch,
            learnedLifestyle = learnedLifestyle,
            topicAffinities = topicAffinities
        )
    }

    var selectedThreadId by remember { mutableStateOf("thread_student_hub") }
    var messageDraft by remember { mutableStateOf("") }
    var newContactName by remember { mutableStateOf("") }
    var newContactRole by remember { mutableStateOf("STUDENT") }
    var showNewChatCreator by remember { mutableStateOf(false) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val messages = remember {
        mutableStateListOf(
            WereEqualChatMessage(
                id = "msg_1",
                threadId = "thread_student_hub",
                senderName = "Maya Lin",
                senderRole = "STUDENT",
                text = "Welcome to WereEqual Messenger! Have you checked out the Common App and FAFSA links in the Student portal yet?",
                timestampLabel = "10:02 AM",
                formattedDateTime = "10:02 AM • Nov 9, 2026",
                isFromCurrentUser = false,
                deliveryStatus = "Received",
                reactionsCount = 4
            ),
            WereEqualChatMessage(
                id = "msg_2",
                threadId = "thread_student_hub",
                senderName = profile.displayName,
                senderRole = profile.role.uppercase(),
                text = "Yes! I'm reviewing the FAFSA scholarship guide and USAJOBS student internship applications right now.",
                timestampLabel = "10:05 AM",
                formattedDateTime = "10:05 AM • Nov 9, 2026",
                isFromCurrentUser = true,
                deliveryStatus = "Delivered",
                reactionsCount = 3
            ),
            WereEqualChatMessage(
                id = "msg_3",
                threadId = "thread_veteran_support",
                senderName = "Marcus Sterling",
                senderRole = "VETERAN",
                text = "Hey ${profile.displayName.substringBefore(" ")}, we're hosting a Veteran-to-Tech career mentorship Q&A tonight. Let me know if you want the resume checklist!",
                timestampLabel = "9:40 AM",
                formattedDateTime = "9:40 AM • Nov 9, 2026",
                isFromCurrentUser = false,
                deliveryStatus = "Received",
                reactionsCount = 5
            ),
            WereEqualChatMessage(
                id = "msg_4",
                threadId = "thread_business_market",
                senderName = "Elena Rostova",
                senderRole = "BUSINESS",
                text = "Your Business & Content Marketplace booth looks great! We can settle community collaborations directly via the WereEqual Digital Wallet.",
                timestampLabel = "Yesterday",
                formattedDateTime = "4:15 PM • Nov 8, 2026",
                isFromCurrentUser = false,
                deliveryStatus = "Received",
                reactionsCount = 2
            ),
            WereEqualChatMessage(
                id = "msg_5",
                threadId = "thread_creator_studio",
                senderName = "Alex Rivera",
                senderRole = "CREATOR",
                text = "Love the new WereEqual Theme Selection and 3D poses! Want to collab on a synthwave showcase?",
                timestampLabel = "Yesterday",
                formattedDateTime = "2:30 PM • Nov 8, 2026",
                isFromCurrentUser = false,
                deliveryStatus = "Received",
                reactionsCount = 6
            )
        )
    }

    val activeThread = threads.firstOrNull { it.id == selectedThreadId } ?: threads.first()
    val activeThreadMessages = messages.filter { it.threadId == activeThread.id }
    val activeAccent = parseThemeHex(activeThread.accentHex)
    val activeAffinityPct = computeAdaptiveAffinityPercent(
        itemTags = activeThread.tags,
        itemRole = activeThread.participantRole,
        profile = profile,
        learnedInterests = learnedInterests,
        learnedResearch = learnedResearch,
        learnedLifestyle = learnedLifestyle,
        topicAffinities = topicAffinities
    )

    val topLearnedTopics = topicAffinities.entries
        .sortedByDescending { it.value }
        .take(3)
        .map { it.key }
    val learningScore = (64 + learnedSignals.size * 3 + learnedInterests.size * 4 + topicAffinities.values.sum())
        .coerceIn(65, 100)

    // Smart replies adapt dynamically to active thread + user's learned interests
    val quickReplies = buildList {
        val focusTopic = topLearnedTopics.firstOrNull() ?: "Community"
        add("✨ Loving this $focusTopic thread—count me in!")
        if (activeThread.tags.contains("STEM")) {
            add("🎓 Let's share STEM notes & FAFSA / Internship links!")
        }
        if (activeThread.tags.contains("Veteran") || profile.role.equals("veteran", ignoreCase = true)) {
            add("🛡️ Joining the Veteran mentorship & wellness check-in!")
        }
        if (activeThread.tags.contains("3D Art") || learnedInterests.any { it.contains("3D", ignoreCase = true) }) {
            add("🌀 Just updated my 3D Avatar pose—want to collab?")
        }
        add("🤝 We are all equal — let's build together!")
    }.distinct().take(4)

    fun sendChatMessage(content: String) {
        val clean = content.trim()
        if (clean.isEmpty()) return
        val nowFormatted = SimpleDateFormat("h:mm a • MMM d, yyyy", Locale.getDefault()).format(Date())
        messages.add(
            WereEqualChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                threadId = activeThread.id,
                senderName = profile.displayName,
                senderRole = profile.role.uppercase(),
                text = clean,
                timestampLabel = "Just now",
                formattedDateTime = nowFormatted,
                isFromCurrentUser = true,
                deliveryStatus = "Delivered",
                reactionsCount = 0
            )
        )
        val primaryTag = activeThread.tags.firstOrNull() ?: "Community"
        recordTopicEngagement(primaryTag, "Sent message to ${activeThread.participantName}")
        messageDraft = ""
        statusBanner = "✓ Sent to ${activeThread.participantName} • Learned affinity for $primaryTag"
        onLogMessagingInteraction("Messaged ${activeThread.participantName}: ${clean.take(32)}")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wereequal_messaging_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Custom Adaptive Messenger Hub Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            ),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.primary, HoloCyan, CyberEmerald, NeonPink)
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .sixDimensionalCardEffect(tiltFactor = 1.15f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, HoloCyan.copy(alpha = 0.65f)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CustomMessengerIcon(
                                    isSelected = true,
                                    unreadCount = friendRequests.count { it.status == "PENDING" } + threads.size,
                                    accentColor = activeAccent,
                                    iconSize = 32.dp,
                                    tagName = "custom_messenger_icon",
                                    contentDesc = "WereEqual Custom Adaptive Messenger Emblem"
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "WereEqual Messenger",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                SixDimensionalHdBadge(label = "LEARNS YOU")
                            }
                            Text(
                                text = "Adapts to ${profile.displayName} (${profile.role.uppercase()}) in real time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showNewChatCreator = !showNewChatCreator },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, HoloCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("toggle_new_message_thread_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "New Chat",
                            tint = HoloCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showNewChatCreator) "Close" else "New Chat",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Adaptive User Learning Strip (Shows what platform learned as you interact)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adaptive_learning_summary_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Adaptive Learning Engine",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Personal Fit Model: $learningScore% Tuned",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "${learnedSignals.size + topicAffinities.values.sum()} signals",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("learned_signals_count_label")
                            )
                        }

                        LinearProgressIndicator(
                            progress = { learningScore / 100f },
                            color = CyberEmerald,
                            trackColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Text(
                            text = "Tap topics to teach your feed & circles (${topLearnedTopics.joinToString(" • ")}):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("3D Art", "STEM", "Career", "Wellness", "Business", "Veteran", "Music").forEach { topic ->
                                val picked = learnedInterests.any { it.equals(topic, ignoreCase = true) } ||
                                    (topicAffinities[topic] ?: 0) >= 2
                                FilterChip(
                                    selected = picked,
                                    onClick = {
                                        onToggleLearnedInterest(topic)
                                        recordTopicEngagement(topic, "Toggled interest chip")
                                        statusBanner = "🧠 Learned you like $topic — ranking feed & chats!"
                                    },
                                    label = {
                                        Text(
                                            text = "$topic (${topicAffinities[topic] ?: 0})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (picked) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    },
                                    modifier = Modifier.testTag("learn_topic_chip_${topic.lowercase().replace(" ", "_")}")
                                )
                            }
                        }
                    }
                }

                if (showNewChatCreator) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Start a New WereEqual Conversation",
                                style = MaterialTheme.typography.labelLarge,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = newContactName,
                                onValueChange = { newContactName = it },
                                label = { Text("Recipient or Study/Career Group Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_chat_recipient_input")
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("STUDENT", "BUSINESS", "CREATOR", "VETERAN").forEach { roleTag ->
                                    FilterChip(
                                        selected = newContactRole == roleTag,
                                        onClick = { newContactRole = roleTag },
                                        label = { Text(roleTag, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    if (newContactName.isNotBlank()) {
                                        val newId = "thread_${System.currentTimeMillis()}"
                                        threads.add(
                                            0,
                                            WereEqualChatThread(
                                                id = newId,
                                                participantName = newContactName.trim(),
                                                participantRole = newContactRole,
                                                avatarEmoji = when (newContactRole) {
                                                    "STUDENT" -> "🎓"
                                                    "BUSINESS" -> "💼"
                                                    "VETERAN" -> "🛡️"
                                                    else -> "🎬"
                                                },
                                                topicBadge = "Direct Message",
                                                accentHex = "#10B981",
                                                isOnline = true,
                                                tags = listOf(newContactRole, "Career")
                                            )
                                        )
                                        messages.add(
                                            WereEqualChatMessage(
                                                id = "msg_welcome_$newId",
                                                threadId = newId,
                                                senderName = newContactName.trim(),
                                                senderRole = newContactRole,
                                                text = "Connected on WereEqual Messaging! \"We are all equal.\"",
                                                timestampLabel = "Just now",
                                                isFromCurrentUser = false,
                                                reactionsCount = 1
                                            )
                                        )
                                        selectedThreadId = newId
                                        recordTopicEngagement("Career", "Started chat with ${newContactName.trim()}")
                                        newContactName = ""
                                        showNewChatCreator = false
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .testTag("create_chat_thread_submit_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Conversation", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                // Best-Fit Ranked Conversation Selector Strip
                Text(
                    text = "Conversations Ranked for You (${rankedThreads.size} Active):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rankedThreads.forEach { thread ->
                        val isSelected = thread.id == activeThread.id
                        val threadColor = parseThemeHex(thread.accentHex)
                        val fitPct = computeAdaptiveAffinityPercent(
                            itemTags = thread.tags,
                            itemRole = thread.participantRole,
                            profile = profile,
                            learnedInterests = learnedInterests,
                            learnedResearch = learnedResearch,
                            learnedLifestyle = learnedLifestyle,
                            topicAffinities = topicAffinities
                        )
                        Surface(
                            color = if (isSelected) {
                                threadColor.copy(alpha = 0.22f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) threadColor else threadColor.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .width(222.dp)
                                .clickable {
                                    selectedThreadId = thread.id
                                    thread.tags.firstOrNull()?.let { firstTag ->
                                        recordTopicEngagement(firstTag, "Opened chat with ${thread.participantName}")
                                    }
                                }
                                .testTag("chat_thread_card_${thread.id}")
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
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = thread.avatarEmoji,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Surface(
                                            color = CyberEmerald.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "$fitPct% Fit",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CyberEmerald,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(if (thread.isOnline) CyberEmerald else SolarAmber)
                                    )
                                }
                                Text(
                                    text = thread.participantName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${thread.topicBadge} • ${thread.participantRole}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3-Mode Sub-Tab Switcher: Direct Chats | For You Adaptive Social Feed | Friends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val pendingCount = friendRequests.count { it.status == "PENDING" }
            FilterChip(
                selected = activeMessagingTab == "MESSAGES",
                onClick = { activeMessagingTab = "MESSAGES" },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                },
                label = {
                    Text("Chats (${threads.size})", maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_switch_direct_messages")
            )
            FilterChip(
                selected = activeMessagingTab == "FOR_YOU",
                onClick = { activeMessagingTab = "FOR_YOU" },
                leadingIcon = {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                },
                label = {
                    Text("For You (${socialFeedPosts.size})", maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_switch_for_you_feed")
            )
            FilterChip(
                selected = activeMessagingTab == "FRIEND_REQUESTS",
                onClick = { activeMessagingTab = "FRIEND_REQUESTS" },
                leadingIcon = {
                    Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                },
                label = {
                    Text("Friends ($pendingCount)", maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_switch_friend_requests")
            )
        }

        if (!statusBanner.isNullOrBlank()) {
            Surface(
                color = CyberEmerald.copy(alpha = 0.16f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("messenger_status_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = statusBanner.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        when (activeMessagingTab) {
            "MESSAGES" -> {
                // Active Conversation Thread & Adaptive Composer Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, activeAccent.copy(alpha = 0.65f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                        .clip(CircleShape)
                                        .background(activeAccent.copy(alpha = 0.22f))
                                        .border(1.5.dp, activeAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeThread.avatarEmoji,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Column {
                                    Text(
                                        text = activeThread.participantName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "$activeAffinityPct% Match • ${activeThread.participantRole} • ${if (activeThread.isOnline) "🟢 Online" else "🕒 Away"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = activeAccent
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onStartVideoCall(activeThread.participantName) },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonPink,
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("start_video_call_from_active_chat_button")
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Video", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val latestMsg = activeThreadMessages.lastOrNull()?.text
                                            ?: "Great conversation in WereEqual Messenger!"
                                        onShareMessageToWall(
                                            "Chat Highlight with ${activeThread.participantName}",
                                            latestMsg
                                        )
                                        statusBanner = "✓ Shared conversation highlight to your Social Wall!"
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.7f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text(
                                        text = "Share",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = activeAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Messages List
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            activeThreadMessages.forEach { msg ->
                                val bubbleColor = if (msg.isFromCurrentUser) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                                val borderColor = if (msg.isFromCurrentUser) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    activeAccent.copy(alpha = 0.5f)
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (msg.isFromCurrentUser) Alignment.End else Alignment.Start
                                ) {
                                    Surface(
                                        color = bubbleColor,
                                        shape = RoundedCornerShape(
                                            topStart = 18.dp,
                                            topEnd = 18.dp,
                                            bottomStart = if (msg.isFromCurrentUser) 18.dp else 4.dp,
                                            bottomEnd = if (msg.isFromCurrentUser) 4.dp else 18.dp
                                        ),
                                        border = BorderStroke(1.dp, borderColor),
                                        modifier = Modifier.fillMaxWidth(0.92f)
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
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.weight(1f, fill = false)
                                                ) {
                                                    Text(
                                                        text = msg.senderName,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (msg.isFromCurrentUser) MaterialTheme.colorScheme.primary else activeAccent,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                    UserAccountBadge(
                                                        role = msg.senderRole,
                                                        style = UserBadgeStyle.PILL
                                                    )
                                                }
                                                Text(
                                                    text = msg.timestampLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Text(
                                                text = msg.text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (msg.isFromCurrentUser) Icons.Default.DoneAll else Icons.Default.CheckCircle,
                                                        contentDescription = if (msg.isFromCurrentUser) "Sent & Delivered" else "Received",
                                                        tint = if (msg.isFromCurrentUser) CyberEmerald else HoloCyan,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = if (msg.isFromCurrentUser) {
                                                            "Sent • ${msg.formattedDateTime}"
                                                        } else {
                                                            "Received • ${msg.formattedDateTime}"
                                                        },
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (msg.isFromCurrentUser) CyberEmerald else HoloCyan,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }

                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                                    shape = RoundedCornerShape(50),
                                                    border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.4f)),
                                                    modifier = Modifier.clickable {
                                                        val idx = messages.indexOf(msg)
                                                        if (idx >= 0) {
                                                            messages[idx] = msg.copy(reactionsCount = msg.reactionsCount + 1)
                                                            activeThread.tags.firstOrNull()?.let { tag ->
                                                                recordTopicEngagement(tag, "Liked message from ${msg.senderName}")
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ThumbUp,
                                                            contentDescription = "Like Message",
                                                            tint = HoloCyan,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Text(
                                                            text = "${msg.reactionsCount}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Adaptive Quick Replies
                        Text(
                            text = "Adaptive Quick Replies (Personalized from your activity):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickReplies.forEach { replyText ->
                                FilterChip(
                                    selected = false,
                                    onClick = { sendChatMessage(replyText) },
                                    label = {
                                        Text(
                                            text = replyText,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        // Message Input & Send Button Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = messageDraft,
                                onValueChange = { messageDraft = it },
                                label = { Text("Write a message to ${activeThread.participantName.substringBefore(" ")}…") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("messaging_input_field")
                            )

                            Button(
                                onClick = { sendChatMessage(messageDraft) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .height(54.dp)
                                    .testTag("messaging_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Message",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Send", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            "FOR_YOU" -> {
                // Adaptive Social Media Feed that learns from each user as they go
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adaptive_for_you_feed_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "✨ Relatable Social Circles Ranked For ${profile.displayName.substringBefore(" ")}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                        Text(
                            text = "Every like, comment, and post teaches WereEqual what matters most to your journey.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick Composer for Sharing a Relatable Update
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newSocialPostDraft,
                                    onValueChange = { newSocialPostDraft = it },
                                    label = { Text("Share a milestone, win, or question with your circles…") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("adaptive_feed_post_input")
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Career", "3D Art", "STEM", "Wellness", "Business", "Veteran").forEach { topic ->
                                        FilterChip(
                                            selected = newSocialPostTopic == topic,
                                            onClick = { newSocialPostTopic = topic },
                                            label = { Text(topic, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        val clean = newSocialPostDraft.trim()
                                        if (clean.isNotEmpty()) {
                                            socialFeedPosts.add(
                                                0,
                                                AdaptiveSocialPost(
                                                    id = "feed_${System.currentTimeMillis()}",
                                                    authorName = profile.displayName,
                                                    authorRole = profile.role,
                                                    avatarEmoji = "🌟",
                                                    circleTag = "$newSocialPostTopic Circle",
                                                    headline = "${profile.displayName} • $newSocialPostTopic Update",
                                                    body = clean,
                                                    tags = listOf(newSocialPostTopic, profile.role),
                                                    accentHex = profile.themeColorHex,
                                                    likesCount = 1,
                                                    commentsCount = 0,
                                                    timeAgo = "Just now",
                                                    isLikedByMe = true
                                                )
                                            )
                                            recordTopicEngagement(newSocialPostTopic, "Published social update")
                                            onLogMessagingInteraction("Shared For You post ($newSocialPostTopic)")
                                            statusBanner = "✓ Posted to For You Feed • Platform learned your interest in $newSocialPostTopic"
                                            newSocialPostDraft = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberEmerald,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("adaptive_feed_post_submit")
                                ) {
                                    Text("Share to Adaptive Feed", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        // Topic Filter Pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL", "Career", "3D Art", "STEM", "Wellness", "Business", "Veteran").forEach { filter ->
                                FilterChip(
                                    selected = selectedFeedTopicFilter == filter,
                                    onClick = { selectedFeedTopicFilter = filter },
                                    label = { Text(if (filter == "ALL") "All Ranked" else filter) }
                                )
                            }
                        }

                        val rankedPosts = socialFeedPosts
                            .filter { post ->
                                selectedFeedTopicFilter == "ALL" ||
                                    post.tags.any { it.equals(selectedFeedTopicFilter, ignoreCase = true) }
                            }
                            .sortedByDescending { post ->
                                computeAdaptiveAffinityPercent(
                                    itemTags = post.tags,
                                    itemRole = post.authorRole,
                                    profile = profile,
                                    learnedInterests = learnedInterests,
                                    learnedResearch = learnedResearch,
                                    learnedLifestyle = learnedLifestyle,
                                    topicAffinities = topicAffinities
                                )
                            }

                        rankedPosts.forEach { post ->
                            val postAccent = parseThemeHex(post.accentHex)
                            val postFit = computeAdaptiveAffinityPercent(
                                itemTags = post.tags,
                                itemRole = post.authorRole,
                                profile = profile,
                                learnedInterests = learnedInterests,
                                learnedResearch = learnedResearch,
                                learnedLifestyle = learnedLifestyle,
                                topicAffinities = topicAffinities
                            )
                            val replyDraft = replyDraftByPostId[post.id].orEmpty()

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                ),
                                border = BorderStroke(1.2.dp, postAccent.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("adaptive_feed_card_${post.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
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
                                            Text(post.avatarEmoji, style = MaterialTheme.typography.titleLarge)
                                            Column {
                                                Text(
                                                    text = "${post.authorName} • ${post.circleTag}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${post.timeAgo} • Relates via ${post.tags.joinToString(", ")}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Surface(
                                            color = CyberEmerald.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "$postFit% Fit",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CyberEmerald,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = post.headline,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = postAccent
                                    )
                                    Text(
                                        text = post.body,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!post.latestReply.isNullOrBlank()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "💬 Your reply: ${post.latestReply}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = HoloCyan,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                val idx = socialFeedPosts.indexOfFirst { it.id == post.id }
                                                if (idx >= 0) {
                                                    val nextLiked = !post.isLikedByMe
                                                    socialFeedPosts[idx] = post.copy(
                                                        isLikedByMe = nextLiked,
                                                        likesCount = post.likesCount + if (nextLiked) 1 else -1
                                                    )
                                                    if (nextLiked) {
                                                        recordTopicEngagement(
                                                            post.tags.firstOrNull() ?: "Community",
                                                            "Related to ${post.authorName}'s post"
                                                        )
                                                        statusBanner = "🧠 Boosted ${post.tags.firstOrNull() ?: "Community"} posts in your feed!"
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(50),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("relate_post_btn_${post.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ThumbUp,
                                                contentDescription = "Relate",
                                                tint = if (post.isLikedByMe) CyberEmerald else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Relate (${post.likesCount})", style = MaterialTheme.typography.labelSmall)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val idx = socialFeedPosts.indexOfFirst { it.id == post.id }
                                                if (idx >= 0) {
                                                    socialFeedPosts[idx] = post.copy(isSavedByMe = !post.isSavedByMe)
                                                    if (!post.isSavedByMe) {
                                                        recordTopicEngagement(
                                                            post.tags.firstOrNull() ?: "Community",
                                                            "Saved ${post.headline}"
                                                        )
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(50),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(
                                                imageVector = if (post.isSavedByMe) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Save Post",
                                                tint = if (post.isSavedByMe) SolarAmber else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (post.isSavedByMe) "Saved" else "Save", style = MaterialTheme.typography.labelSmall)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val match = threads.firstOrNull {
                                                    it.participantName.contains(post.authorName, ignoreCase = true)
                                                }
                                                if (match != null) {
                                                    selectedThreadId = match.id
                                                    activeMessagingTab = "MESSAGES"
                                                } else {
                                                    val newId = "thread_${post.id}"
                                                    threads.add(
                                                        0,
                                                        WereEqualChatThread(
                                                            id = newId,
                                                            participantName = post.authorName,
                                                            participantRole = post.authorRole.uppercase(),
                                                            avatarEmoji = post.avatarEmoji,
                                                            topicBadge = post.circleTag,
                                                            accentHex = post.accentHex,
                                                            isOnline = true,
                                                            tags = post.tags
                                                        )
                                                    )
                                                    selectedThreadId = newId
                                                    activeMessagingTab = "MESSAGES"
                                                }
                                            },
                                            shape = RoundedCornerShape(50),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Text("Message", style = MaterialTheme.typography.labelSmall, color = HoloCyan)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = replyDraft,
                                            onValueChange = { updated ->
                                                replyDraftByPostId = replyDraftByPostId + (post.id to updated)
                                            },
                                            label = { Text("Add supportive comment…") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                val cleanReply = replyDraft.trim()
                                                if (cleanReply.isNotEmpty()) {
                                                    val idx = socialFeedPosts.indexOfFirst { it.id == post.id }
                                                    if (idx >= 0) {
                                                        socialFeedPosts[idx] = post.copy(
                                                            commentsCount = post.commentsCount + 1,
                                                            latestReply = cleanReply
                                                        )
                                                        replyDraftByPostId = replyDraftByPostId - post.id
                                                        recordTopicEngagement(
                                                            post.tags.firstOrNull() ?: "Community",
                                                            "Commented on ${post.authorName}'s post"
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Text("Reply", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Friend Requests & Network Hub View
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_requests_management_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🤝 Community Connections & Friends",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HoloCyan
                                )
                                Text(
                                    text = "Discover members matched by life stage & shared interests.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = onOpenFriendsDirectory,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, CyberEmerald),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("open_full_friends_directory_btn")
                            ) {
                                Text("Find Friends", style = MaterialTheme.typography.labelSmall, color = CyberEmerald, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = sendFriendNameInput,
                            onValueChange = { sendFriendNameInput = it },
                            label = { Text("Friend's Full Name, Username, or Student/Biz ID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("send_friend_name_input")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "student" to "🎓 Student",
                                "business" to "💼 Business",
                                "veteran" to "🛡️ Veteran",
                                "creator" to "🎬 Creator",
                                "adult" to "🌟 Member"
                            ).forEach { (rKey, rLabel) ->
                                FilterChip(
                                    selected = sendFriendRoleInput == rKey,
                                    onClick = { sendFriendRoleInput = rKey },
                                    label = { Text(rLabel, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = sendFriendNoteInput,
                            onValueChange = { sendFriendNoteInput = it },
                            label = { Text("Personal Connection Note") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("send_friend_note_input")
                        )

                        Button(
                            onClick = {
                                val cleanName = sendFriendNameInput.trim()
                                if (cleanName.isNotEmpty()) {
                                    statusBanner = "✓ Friend request sent to $cleanName!"
                                    recordTopicEngagement(sendFriendRoleInput, "Sent friend request to $cleanName")
                                    onLogMessagingInteraction("Sent friend request to $cleanName ($sendFriendRoleInput)")
                                    sendFriendNameInput = ""
                                    sendFriendNoteInput = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("submit_send_friend_request_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Friend Request", fontWeight = FontWeight.ExtraBold)
                        }

                        val pendingList = friendRequests.filter { it.status == "PENDING" }
                        Text(
                            text = "Pending Friend Requests (${pendingList.size}):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (pendingList.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "All caught up! No pending friend requests.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        } else {
                            pendingList.forEach { req ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("pending_friend_request_${req.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                                Text(req.avatarEmoji, style = MaterialTheme.typography.titleLarge)
                                                Column {
                                                    Text(
                                                        text = req.senderName,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "🤝 ${req.mutualFriends} mutual friends • ${req.timeAgo}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            UserAccountBadge(
                                                role = req.senderRole,
                                                style = UserBadgeStyle.COMPACT
                                            )
                                        }

                                        Text(
                                            text = "\"${req.note}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    val idx = friendRequests.indexOfFirst { it.id == req.id }
                                                    if (idx >= 0) {
                                                        friendRequests[idx] = req.copy(status = "ACCEPTED")
                                                    }
                                                    threads.add(
                                                        WereEqualChatThread(
                                                            id = "thread_${req.id}",
                                                            participantName = req.senderName,
                                                            participantRole = req.senderRole.uppercase(),
                                                            avatarEmoji = req.avatarEmoji,
                                                            topicBadge = "New Friend",
                                                            accentHex = "#10B981",
                                                            isOnline = true,
                                                            tags = listOf(req.senderRole, "Community")
                                                        )
                                                    )
                                                    recordTopicEngagement(req.senderRole, "Connected with ${req.senderName}")
                                                    statusBanner = "✓ Accepted ${req.senderName}! Added to Messenger."
                                                    onLogMessagingInteraction("Accepted friend request from ${req.senderName}")
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CyberEmerald,
                                                    contentColor = Color.Black
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("accept_friend_${req.id}")
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Accept", fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    val idx = friendRequests.indexOfFirst { it.id == req.id }
                                                    if (idx >= 0) {
                                                        friendRequests[idx] = req.copy(status = "DECLINED")
                                                    }
                                                    statusBanner = "Declined friend request from ${req.senderName}."
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("decline_friend_${req.id}")
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Decline", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "🌟 Your WereEqual Friends & Circles:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        rankedThreads.forEach { friend ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("friend_directory_item_${friend.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
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
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(friend.avatarEmoji, style = MaterialTheme.typography.titleMedium)
                                        }
                                        Column {
                                            Text(
                                                text = friend.participantName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${friend.participantRole} • ${if (friend.isOnline) "🟢 Online" else "🕒 Away"}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedThreadId = friend.id
                                                activeMessagingTab = "MESSAGES"
                                            },
                                            shape = RoundedCornerShape(50),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Text("Chat", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { onStartVideoCall(friend.participantName) },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = NeonPink,
                                                contentColor = Color.Black
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Text("Video", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
}
