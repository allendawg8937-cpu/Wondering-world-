package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

data class ModerationQueueItem(
    val id: String,
    val targetId: String,
    val targetType: String, // "Post", "Comment", or "Profile"
    val contentPreview: String,
    val authorLabel: String,
    val reason: String,
    val reporterNotes: String,
    val status: String, // "PENDING", "APPROVED", "REMOVED"
    val moderatorResolution: String = ""
)

val MODERATOR_GUIDELINES = listOf(
    Triple(
        "Guideline 1 • Dignity & Equality First",
        "Every review must uphold 'We Are All Equal'",
        "Immediately remove any post, comment, or profile containing hate speech, racism, discrimination, or harassment against any age group, veteran, student, or community member."
    ),
    Triple(
        "Guideline 2 • Youth & Minor Protection (13–17)",
        "Strict age-appropriate boundaries",
        "Flagged content involving or targeting Kids/Teens with mature themes, bullying, or unsafe external links must be removed immediately and escalated."
    ),
    Triple(
        "Guideline 3 • Privacy & Anti-Scam Shield",
        "Zero tolerance for doxxing or financial scams",
        "Remove any post or comment attempting to harvest phone numbers, emails, or external payments."
    ),
    Triple(
        "Guideline 4 • Fair Context & Constructive Discourse",
        "Protect helpful community speech",
        "If a flagged post or comment shares legitimate VA, FAFSA, 211, or educational resources in good faith, mark it 'Approved & Cleared'."
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentModerationSystemCard(
    onSubmitFlagToBackend: (targetId: String, targetType: String, reason: String, detail: String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Review Queue (Admin/Community), 1 = Flag Post/Comment/Profile, 2 = Moderator Guidelines
    var queueFilter by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "RESOLVED"
    var statusBanner by remember { mutableStateOf<String?>(null) }

    // Flag Form State
    var selectedTargetType by remember { mutableStateOf("Post") } // "Post", "Comment", "Profile"
    var targetIdentifierInput by remember { mutableStateOf("") }
    var selectedFlagReason by remember { mutableStateOf("Spam or Scam Link") }
    var flagDetailsInput by remember { mutableStateOf("") }

    val queueItems = remember {
        mutableStateListOf(
            ModerationQueueItem(
                id = "mod_flag_101",
                targetId = "post_ext_promo_92",
                targetType = "Post",
                contentPreview = "\"Guaranteed 10x crypto return—DM your phone number now!\"",
                authorLabel = "@unknown_promo_bot",
                reason = "Spam or Scam Link",
                reporterNotes = "Soliciting private phone numbers & promoting an unverified scheme.",
                status = "PENDING"
            ),
            ModerationQueueItem(
                id = "mod_flag_102",
                targetId = "comment_thread_44",
                targetType = "Comment",
                contentPreview = "\"Your 3D art project doesn't belong in the Teen Creator showcase.\"",
                authorLabel = "@critic_account_8",
                reason = "Bullying or Harassment",
                reporterNotes = "Unconstructive hostility on a student's first 3D studio post.",
                status = "PENDING"
            ),
            ModerationQueueItem(
                id = "mod_flag_103",
                targetId = "profile_check_19",
                targetType = "Profile",
                contentPreview = "Profile Bio: Sharing verified regional 211 Food Bank & VA Clinic hours.",
                authorLabel = "Marcus Vance (Veteran Guide)",
                reason = "Misinformation Check",
                reporterNotes = "Community requested verification of listed clinic phone hours.",
                status = "APPROVED",
                moderatorResolution = "Verified official VA.gov & 211.org hours — Cleared by Moderation Queue."
            )
        )
    }

    val pendingCount = queueItems.count { it.status == "PENDING" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("content_moderation_system_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.94f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(RosePulse, SolarAmber, CyberEmerald, HoloCyan)
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
                            .background(RosePulse.copy(alpha = 0.22f))
                            .border(1.5.dp, RosePulse, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Content Moderation System",
                            tint = RosePulse,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "CONTENT MODERATION & SAFETY QUEUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = RosePulse,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Flag Posts, Comments & Profiles • Review Queue",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (pendingCount > 0) SolarAmber.copy(alpha = 0.22f) else CyberEmerald.copy(alpha = 0.22f),
                    border = BorderStroke(1.dp, if (pendingCount > 0) SolarAmber else CyberEmerald)
                ) {
                    Text(
                        text = "$pendingCount Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pendingCount > 0) SolarAmber else CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Status Banner
            AnimatedVisibility(visible = !statusBanner.isNullOrBlank()) {
                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberEmerald.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("moderation_status_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusBanner.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3 Mode Tabs: Review Queue | Flag Content | Moderator Guidelines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(0, "⚖️ Review Queue ($pendingCount)", "mod_tab_queue"),
                    Triple(1, "🚩 Flag Content", "mod_tab_flag"),
                    Triple(2, "📜 Mod Guidelines", "mod_tab_guidelines")
                ).forEach { (idx, label, tag) ->
                    val selected = activeTab == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) RosePulse.copy(alpha = 0.26f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) RosePulse else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeTab = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            when (activeTab) {
                0 -> {
                    // ADMIN & COMMUNITY REVIEW QUEUE
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Admin & Community-Driven Review Queue:",
                                style = MaterialTheme.typography.labelMedium,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("ALL" to "All", "PENDING" to "Pending", "RESOLVED" to "Resolved").forEach { (k, label) ->
                                    FilterChip(
                                        selected = queueFilter == k,
                                        onClick = { queueFilter = k },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }

                        val filteredQueue = queueItems.filter { item ->
                            when (queueFilter) {
                                "PENDING" -> item.status == "PENDING"
                                "RESOLVED" -> item.status != "PENDING"
                                else -> true
                            }
                        }

                        filteredQueue.forEach { item ->
                            val statusColor = when (item.status) {
                                "APPROVED" -> CyberEmerald
                                "REMOVED" -> RosePulse
                                else -> SolarAmber
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.88f),
                                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mod_queue_item_${item.id}")
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = HoloCyan.copy(alpha = 0.22f)
                                            ) {
                                                Text(
                                                    text = item.targetType.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = HoloCyan,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "Reason: ${item.reason}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = statusColor.copy(alpha = 0.20f)
                                        ) {
                                            Text(
                                                text = item.status,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = statusColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = item.contentPreview,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFF8FAFC),
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = "Author/Target: ${item.authorLabel} • Note: ${item.reporterNotes}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )

                                    if (item.moderatorResolution.isNotBlank()) {
                                        Text(
                                            text = "⚖️ Decision: ${item.moderatorResolution}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (item.status == "PENDING") {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    val idx = queueItems.indexOfFirst { it.id == item.id }
                                                    if (idx >= 0) {
                                                        queueItems[idx] = item.copy(
                                                            status = "APPROVED",
                                                            moderatorResolution = "Approved & Kept (Complies with Equality Guidelines)"
                                                        )
                                                        statusBanner = "✅ Approved ${item.targetType} (${item.targetId}) and cleared flag."
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CyberEmerald,
                                                    contentColor = Color(0xFF052E16)
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                                    .testTag("mod_approve_${item.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Approve & Keep",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    val idx = queueItems.indexOfFirst { it.id == item.id }
                                                    if (idx >= 0) {
                                                        queueItems[idx] = item.copy(
                                                            status = "REMOVED",
                                                            moderatorResolution = "Removed & Author Warned per Code of Conduct"
                                                        )
                                                        statusBanner = "🛡️ Removed flagged ${item.targetType} (${item.targetId}) from public feed."
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = RosePulse,
                                                    contentColor = Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                                    .testTag("mod_remove_${item.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteForever,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Remove Content",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // FLAG A POST, COMMENT, OR PROFILE FORM
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🚩 Submit a Content Flag (Post, Comment, or Profile):",
                            style = MaterialTheme.typography.labelMedium,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Post", "Comment", "Profile").forEach { type ->
                                val selected = selectedTargetType == type
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) RosePulse.copy(alpha = 0.28f) else Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, if (selected) RosePulse else Color.White.copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTargetType = type }
                                        .testTag("select_flag_type_${type.lowercase()}")
                                ) {
                                    Text(
                                        text = "Flag $type",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = targetIdentifierInput,
                            onValueChange = { targetIdentifierInput = it },
                            label = { Text("$selectedTargetType Title, Comment Snippet, or @Username") },
                            placeholder = { Text("e.g., @user_handle or post title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("flag_target_identifier_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = RosePulse,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                            )
                        )

                        Text(
                            text = "Select Violation Category:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Spam or Scam Link",
                                "Bullying or Harassment",
                                "Equality Charter Violation",
                                "Inappropriate / 18+ Policy",
                                "Misinformation"
                            ).forEach { reason ->
                                FilterChip(
                                    selected = selectedFlagReason == reason,
                                    onClick = { selectedFlagReason = reason },
                                    label = { Text(reason, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = flagDetailsInput,
                            onValueChange = { flagDetailsInput = it },
                            label = { Text("Context / Notes for Moderators") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("flag_details_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = RosePulse,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                            )
                        )

                        Button(
                            onClick = {
                                val cleanTarget = targetIdentifierInput.trim().ifBlank { "Flagged $selectedTargetType" }
                                val cleanNotes = flagDetailsInput.trim().ifBlank { "Reported via Community Safety Hub" }
                                val newItem = ModerationQueueItem(
                                    id = "mod_flag_${System.currentTimeMillis()}",
                                    targetId = cleanTarget.lowercase().replace(' ', '_').take(24),
                                    targetType = selectedTargetType,
                                    contentPreview = "\"$cleanTarget\"",
                                    authorLabel = cleanTarget,
                                    reason = selectedFlagReason,
                                    reporterNotes = cleanNotes,
                                    status = "PENDING"
                                )
                                queueItems.add(0, newItem)
                                onSubmitFlagToBackend(newItem.targetId, selectedTargetType.lowercase(), selectedFlagReason, cleanNotes)
                                targetIdentifierInput = ""
                                flagDetailsInput = ""
                                statusBanner = "🚩 Flagged $selectedTargetType ('$cleanTarget') added to Review Queue & synced!"
                                activeTab = 0
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RosePulse,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_flag_to_queue_button")
                        ) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit $selectedTargetType Flag to Review Queue",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                2 -> {
                    // CLEAR MODERATOR GUIDELINES
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📜 Official Wondering World Moderator Review Guidelines:",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        MODERATOR_GUIDELINES.forEach { (badge, title, detail) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = detail,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp
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
