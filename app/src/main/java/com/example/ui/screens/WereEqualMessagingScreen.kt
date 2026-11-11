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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import com.example.ui.components.UserAccountBadge
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

data class WereEqualChatThread(
    val id: String,
    val participantName: String,
    val participantRole: String,
    val avatarEmoji: String,
    val topicBadge: String,
    val accentHex: String,
    val isOnline: Boolean = true
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WereEqualMessagingScreen(
    profile: UserProfile,
    onShareMessageToWall: (String, String) -> Unit = { _, _ -> },
    onLogMessagingInteraction: (String) -> Unit = {},
    onStartVideoCall: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeMessagingTab by remember { mutableStateOf("MESSAGES") } // "MESSAGES" vs "FRIEND_REQUESTS"

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
                topicBadge = "School & Career Apps",
                accentHex = "#2563EB",
                isOnline = true
            ),
            WereEqualChatThread(
                id = "thread_veteran_support",
                participantName = "Marcus Sterling",
                participantRole = "VETERAN MENTOR",
                avatarEmoji = "🛡️",
                topicBadge = "VA & GI Bill Careers",
                accentHex = "#06B6D4",
                isOnline = true
            ),
            WereEqualChatThread(
                id = "thread_business_market",
                participantName = "Elena Rostova",
                participantRole = "BUSINESS & MARKETPLACE",
                avatarEmoji = "💼",
                topicBadge = "Marketplace & Wallet",
                accentHex = "#10B981",
                isOnline = true
            ),
            WereEqualChatThread(
                id = "thread_creator_studio",
                participantName = "Alex Rivera",
                participantRole = "CONTENT CREATOR",
                avatarEmoji = "🎬",
                topicBadge = "3D Art & Music Collab",
                accentHex = "#EC4899",
                isOnline = false
            )
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
                text = "Welcome to WereEqual Messaging! Have you checked out the Common App and FAFSA links in the Student Account portal yet?",
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
                text = "Love the new WereEqual Theme Selection and Basic Colors! Want to collab on a 3D synthwave soundtrack?",
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

    val quickReplies = listOf(
        "🎓 Here is the Common App & FAFSA link!",
        "💼 Let's connect on a Career / Internship opportunity!",
        "🏪 Interested in your WereEqual Marketplace listing!",
        "🎨 Loving the new WereEqual Theme Selection!",
        "🤝 We are all equal — let's collaborate!"
    )

    fun sendChatMessage(content: String) {
        val clean = content.trim()
        if (clean.isEmpty()) return
        val nowFormatted = java.text.SimpleDateFormat("h:mm a • MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
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
        messageDraft = ""
        statusBanner = "✓ Sent message to ${activeThread.participantName} ($nowFormatted)"
        onLogMessagingInteraction("Messaged ${activeThread.participantName}: ${clean.take(32)}")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wereequal_messaging_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. WereEqual Messaging Hub Header Card
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
                    .padding(18.dp),
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
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(MaterialTheme.colorScheme.primary, HoloCyan, CyberEmerald)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "WereEqual Messaging",
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
                                    text = "WereEqual Messaging Hub",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                SixDimensionalHdBadge(label = "LIVE CHAT")
                            }
                            Text(
                                text = "Direct Messages, Student Study Pods, Career Networking & Business Chat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showNewChatCreator = !showNewChatCreator },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier.testTag("toggle_new_message_thread_button")
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
                                modifier = Modifier.fillMaxWidth(),
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
                                                isOnline = true
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
                                    .testTag("create_chat_thread_submit_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Conversation", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                // Horizontal Conversation / Channel Selector Cards
                Text(
                    text = "Active Conversations (${threads.size} Channels):",
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
                    threads.forEach { thread ->
                        val isSelected = thread.id == activeThread.id
                        val threadColor = parseThemeHex(thread.accentHex)
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
                                .width(205.dp)
                                .clickable { selectedThreadId = thread.id }
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
                                            color = threadColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = thread.topicBadge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = threadColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                                    text = thread.participantRole,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Switcher: Direct Messages vs Friends & Requests
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val pendingCount = friendRequests.count { it.status == "PENDING" }
            FilterChip(
                selected = activeMessagingTab == "MESSAGES",
                onClick = { activeMessagingTab = "MESSAGES" },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                label = {
                    Text("💬 Direct Messages (${threads.size})", fontWeight = FontWeight.Bold)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_switch_direct_messages")
            )
            FilterChip(
                selected = activeMessagingTab == "FRIEND_REQUESTS",
                onClick = { activeMessagingTab = "FRIEND_REQUESTS" },
                leadingIcon = {
                    Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                label = {
                    Text("👥 Friend Requests ($pendingCount)", fontWeight = FontWeight.Bold)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_switch_friend_requests")
            )
        }

        if (activeMessagingTab == "MESSAGES") {
            // 2. Active Conversation Thread & Message Composer Card
            Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, activeAccent.copy(alpha = 0.65f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Thread Header
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
                                text = "${activeThread.participantRole} • ${if (activeThread.isOnline) "🟢 Online Now" else "🕒 Away"}",
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
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("start_video_call_from_active_chat_button")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video Call", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Video", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val latestMsg = activeThreadMessages.lastOrNull()?.text
                                    ?: "Great conversation in WereEqual Messaging!"
                                onShareMessageToWall(
                                    "Chat Highlight with ${activeThread.participantName}",
                                    latestMsg
                                )
                                statusBanner = "✓ Shared conversation highlight to your WereEqual Wall!"
                            },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.7f)),
                            modifier = Modifier.height(36.dp)
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

                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
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

                // Messages List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activeThreadMessages.forEachIndexed { idx, msg ->
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
                                            com.example.ui.components.UserAccountBadge(
                                                role = msg.senderRole,
                                                style = com.example.ui.components.UserBadgeStyle.PILL
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

                                    // Detailed Timestamp & Sent/Received Indicator Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (msg.isFromCurrentUser) {
                                                Icon(
                                                    imageVector = Icons.Default.DoneAll,
                                                    contentDescription = "Sent & Delivered",
                                                    tint = CyberEmerald,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "Sent • ${msg.formattedDateTime}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CyberEmerald,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Received",
                                                    tint = HoloCyan,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "Received • ${msg.formattedDateTime}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = HoloCyan,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.4f)),
                                            modifier = Modifier.clickable {
                                                messages[messages.indexOf(msg)] =
                                                    msg.copy(reactionsCount = msg.reactionsCount + 1)
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

                // Quick-Reply & School/Job Attachment Chips
                Text(
                    text = "Quick Replies & School / Career Attachments:",
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
    } else {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Send Friend Request Form
                    Text(
                        text = "🤝 Send a WereEqual Friend Request",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = HoloCyan
                    )
                    Text(
                        text = "Connect with students, business partners, veterans, and creators across the platform.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                        label = { Text("Personal Connection Note (e.g. Study Partner, Marketplace Collab)") },
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
                            .height(46.dp)
                            .testTag("submit_send_friend_request_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Friend Request", fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pending Incoming Friend Requests
                    val pendingList = friendRequests.filter { it.status == "PENDING" }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pending Friend Requests (${pendingList.size}):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (pendingList.isNotEmpty()) {
                            Surface(
                                color = SolarAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "${pendingList.size} ACTION NEEDED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SolarAmber,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

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
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
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
                                            style = com.example.ui.components.UserBadgeStyle.COMPACT
                                        )
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "\"${req.note}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }

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
                                                // Automatically add a chat thread with this friend
                                                threads.add(
                                                    WereEqualChatThread(
                                                        id = "thread_${req.id}",
                                                        participantName = req.senderName,
                                                        participantRole = req.senderRole.uppercase(),
                                                        avatarEmoji = req.avatarEmoji,
                                                        topicBadge = "New Friend",
                                                        accentHex = "#10B981",
                                                        isOnline = true
                                                    )
                                                )
                                                statusBanner = "✓ Accepted friend request from ${req.senderName}! You can now chat and video call."
                                                onLogMessagingInteraction("Accepted friend request from ${req.senderName}")
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = CyberEmerald,
                                                contentColor = Color.Black
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
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
                                                .height(40.dp)
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Friends Directory with 1-Tap Message & Video Call
                    Text(
                        text = "🌟 Your WereEqual Friends & Network:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    threads.forEach { friend ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Chat", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            onStartVideoCall(friend.participantName)
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonPink,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
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
