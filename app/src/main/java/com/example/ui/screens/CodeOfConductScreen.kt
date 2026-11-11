package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AiTextUiState
import com.example.ui.UserIdeaProposal
import com.example.ui.components.PHONE_SYSTEM_SETTINGS_OPTIONS
import com.example.ui.components.launchPhoneSystemSetting
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CodeOfConductScreen(
    userIdeas: List<UserIdeaProposal>,
    aiIdeaFeedbackState: AiTextUiState,
    onSubmitUserIdea: (title: String, category: String, description: String) -> Unit,
    onUpvoteUserIdea: (String) -> Unit,
    onExpandIdeaWithAi: (title: String, category: String, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPhoneSettingsExpanded by remember { mutableStateOf(true) }
    var ideaTitle by remember { mutableStateOf("") }
    var ideaDescription by remember { mutableStateOf("") }
    var selectedIdeaCategory by remember { mutableStateOf("Kids Study & AI Tools") }
    var ideaToast by remember { mutableStateOf<String?>(null) }

    val ideaCategories = listOf(
        "Kids Study & AI Tools",
        "3D Theme & Profile",
        "ZIP Library & GPS Hub",
        "YouTube, Music & Games",
        "Community & Safety"
    )

    val conductArticles = listOf(
        Triple(
            "Rule 1 • No Bullying",
            "Zero Tolerance for Bullying or Harassment",
            "No bullying, cyberbullying, intimidation, or mistreating anyone on Wondering World."
        ),
        Triple(
            "Rule 2 • No Racist Profiles or Disrespectful Content",
            "We Are All Equal — Zero Racism or Hate",
            "No race-hate profiles and no posting bad content that is racist or disrespectful toward any person or group."
        ),
        Triple(
            "Rule 3 • No Sexual Misconduct & Strict 18+ Rule",
            "18 Years or Older Required for Adult Spaces/Content",
            "No sexual misconduct of any kind. Mature or adult/nude imagery is strictly prohibited unless verified 18 years or older in age-restricted spaces; minors (13–17) are 100% protected."
        ),
        Triple(
            "Rule 4 • No Selling Your Information",
            "100% Data Privacy & Zero Information Selling",
            "Wondering World never sells your personal information, and no user may sell, harvest, or trade anyone's private information."
        ),
        Triple(
            "Rule 5 • Automatic Account Deletion",
            "Immediate & Permanent Account Deletion for Violations",
            "If you violate any rule in this Code of Conduct, your account is automatically and permanently deleted immediately."
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("code_of_conduct_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 0. Content Moderation System (Flag Posts, Comments & Profiles + Review Queue + Moderator Guidelines)
        com.example.ui.components.ContentModerationSystemCard()

        // 1. User Ideas & Platform Improvement Co-Design Hub ("page to make improvement with users Ideas")
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("user_ideas_improvement_card")
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
                            .clip(CircleShape)
                            .background(CyberEmerald.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "User Ideas & Improvements",
                            tint = CyberEmerald
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "User Ideas & Platform Improvement Hub",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Share your ideas to improve Wondering World! Vote on community proposals and co-design features with AI.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ideaCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedIdeaCategory == cat,
                            onClick = { selectedIdeaCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = ideaTitle,
                    onValueChange = { ideaTitle = it },
                    label = { Text("Your Improvement Idea Title (e.g., Multiplayer Math Space Race)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_idea_title_input")
                )

                OutlinedTextField(
                    value = ideaDescription,
                    onValueChange = { ideaDescription = it },
                    label = { Text("Describe how your idea helps kids, students, veterans, or creators…") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_idea_description_input")
                )

                if (!ideaToast.isNullOrBlank()) {
                    Text(
                        text = ideaToast!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberEmerald
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val finalTitle = ideaTitle.ifBlank { "New $selectedIdeaCategory Enhancement" }
                            val finalDesc = ideaDescription.ifBlank {
                                "Community proposal to expand $selectedIdeaCategory for all users."
                            }
                            onSubmitUserIdea(finalTitle, selectedIdeaCategory, finalDesc)
                            ideaTitle = ""
                            ideaDescription = ""
                            ideaToast = "Published your improvement idea to the Community Board & 3D Wall!"
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_user_idea_button")
                    ) {
                        Text("Submit Idea", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val finalTitle = ideaTitle.ifBlank { "Interactive 3D Study & Resource Room" }
                            val finalDesc = ideaDescription.ifBlank {
                                "Let users collaborate on school research and customize 3D study themes."
                            }
                            onExpandIdeaWithAi(finalTitle, selectedIdeaCategory, finalDesc)
                        },
                        enabled = aiIdeaFeedbackState !is AiTextUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_expand_user_idea_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HoloCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Blueprint", color = HoloCyan, fontWeight = FontWeight.Bold)
                    }
                }

                // AI Co-Designer Output
                when (aiIdeaFeedbackState) {
                    is AiTextUiState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = HoloCyan,
                                strokeWidth = 2.dp
                            )
                            Text("AI Co-Designer is expanding your improvement idea…")
                        }
                    }
                    is AiTextUiState.Streaming -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = aiIdeaFeedbackState.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    is AiTextUiState.Success -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "AI Feature Blueprint for Your Idea:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HoloCyan
                                )
                                Text(
                                    text = aiIdeaFeedbackState.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    is AiTextUiState.Error -> {
                        Text(
                            text = aiIdeaFeedbackState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SolarAmber
                        )
                    }
                    is AiTextUiState.Idle -> {}
                }

                // Live Community Ideas Board
                Text(
                    text = "Community Improvement Ideas Board (${userIdeas.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    color = HoloCyan
                )
                userIdeas.forEach { idea ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = NeonPink.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${idea.category.uppercase()} • ${idea.statusBadge}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPink,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onUpvoteUserIdea(idea.id) },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Upvote Idea",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${idea.votes} Votes")
                                }
                            }
                            Text(
                                text = idea.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = idea.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Proposed by ${idea.authorName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan
                            )
                        }
                    }
                }
            }
        }

        // 2. Pull-Down Bar to Select Phone System Settings (Explicitly Requested on Code of Conduct Page)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = SolarAmber.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPhoneSettingsExpanded = !isPhoneSettingsExpanded }
                        .testTag("conduct_phone_settings_pulldown")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Phone System Settings Pull-Down",
                                tint = SolarAmber
                            )
                            Column {
                                Text(
                                    text = "Pull-Down Bar: Select Phone System Settings",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SolarAmber,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap to pull down and open Android GPS, Wi-Fi, Security, Sound, Display, or App Settings",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isPhoneSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = SolarAmber
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isPhoneSettingsExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PHONE_SYSTEM_SETTINGS_OPTIONS.forEach { option ->
                            Button(
                                onClick = { launchPhoneSystemSetting(context, option.action) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = SolarAmber
                                ),
                                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("conduct_setting_${option.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(option.label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // 3. Wondering World Code of Conduct & Equality Charter
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.6f))
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
                            .background(RosePulse.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Code of Conduct",
                            tint = RosePulse
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wondering World Code of Conduct",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "\"We are all equal\" • Official Community Rules, Youth Protection & Dignity Charter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HoloCyan
                        )
                    }
                }

                conductArticles.forEach { (articleNum, title, body) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = articleNum,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberEmerald
                                )
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
