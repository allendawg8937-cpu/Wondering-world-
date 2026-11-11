package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.data.model.WallPost
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

data class CommunityLifeStageSharedItem(
    val id: String,
    val title: String,
    val content: String,
    val authorName: String,
    val lifeStagePersona: String,
    val categoryLabel: String,
    val baseUpvotes: Long,
    val accentHex: String,
    val isFromFirestore: Boolean,
    val firestoreWallPost: WallPost? = null
)

private val CURATED_FIRESTORE_SEED_TIPS = listOf(
    CommunityLifeStageSharedItem(
        id = "seed_student_fafsa_checklist",
        title = "Student Aid & Scholarship Stack Strategy",
        content = "Submit your federal and state financial aid applications in the first 2 weeks of opening, then stack local community foundation grants under $2,000 where applicant pools are 70% smaller.",
        authorName = "Maya Lin • University Mentor",
        lifeStagePersona = "Student",
        categoryLabel = "Resource Guide",
        baseUpvotes = 48L,
        accentHex = "#A855F7",
        isFromFirestore = false
    ),
    CommunityLifeStageSharedItem(
        id = "seed_veteran_benefits_bridge",
        title = "Veteran GI Bill + SkillBridge Transition Roadmap",
        content = "Use DoD SkillBridge during your final 180 days of service to intern with civilian tech or logistics employers at full military pay before activating Post-9/11 GI Bill housing stipends.",
        authorName = "Marcus Vance • USMC Veteran",
        lifeStagePersona = "Veteran",
        categoryLabel = "Resource Guide",
        baseUpvotes = 64L,
        accentHex = "#FF6D00",
        isFromFirestore = false
    ),
    CommunityLifeStageSharedItem(
        id = "seed_adult_emergency_bucket",
        title = "3-Bucket Automated Household Cashflow Tip",
        content = "Split direct deposits automatically: 55% fixed essentials, 25% high-yield emergency reserve until 4 months of expenses are covered, and 20% debt snowballs & skill investments.",
        authorName = "Elena Rostova • Financial Coach",
        lifeStagePersona = "Adult",
        categoryLabel = "Life Stage Tip",
        baseUpvotes = 53L,
        accentHex = "#10B981",
        isFromFirestore = false
    ),
    CommunityLifeStageSharedItem(
        id = "seed_business_sba_microgrant",
        title = "Small Business SBDC Free Legal & Pitch Audit",
        content = "Before paying for incorporation or pitch decks, book a free 1-on-1 advisor session at your local Small Business Development Center (SBDC) for non-dilutive grant eligibility checks.",
        authorName = "Devon Brooks • Founder Hub",
        lifeStagePersona = "Business",
        categoryLabel = "Resource Guide",
        baseUpvotes = 41L,
        accentHex = "#F59E0B",
        isFromFirestore = false
    ),
    CommunityLifeStageSharedItem(
        id = "seed_teen_first_portfolio",
        title = "Teen Creative & STEM Portfolio Starter Guide",
        content = "Document 3 weekend projects (coding, digital art, or community volunteering) with before/after screenshots. Local summer internships value proof-of-curiosity over prior job titles.",
        authorName = "Liam Ortiz • Teen Creator",
        lifeStagePersona = "Teen",
        categoryLabel = "Life Stage Tip",
        baseUpvotes = 37L,
        accentHex = "#EC4899",
        isFromFirestore = false
    ),
    CommunityLifeStageSharedItem(
        id = "seed_kid_curiosity_quest",
        title = "Kid Explorer 20-Minute Daily Reading & Sketch Habit",
        content = "Pair 15 minutes of illustrated science or adventure reading with a 5-minute doodle summary in your Stages Of Life journal to build memory and storytelling superpowers!",
        authorName = "Coach Sunny • Youth Educator",
        lifeStagePersona = "Kid",
        categoryLabel = "Life Stage Tip",
        baseUpvotes = 32L,
        accentHex = "#00E5FF",
        isFromFirestore = false
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityLifeStageHubCard(
    postsState: UiState<List<WallPost>>,
    currentUserDisplayName: String,
    currentPersonaRole: String,
    onCreateFirestoreTipOrResource: (category: String, title: String, content: String) -> Unit,
    onUpvoteFirestorePost: (WallPost) -> Unit,
    onRewardHelpfulVote: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stageFilters = remember {
        listOf("All Stages", "Kid", "Teen", "Student", "Adult", "Business", "Veteran")
    }
    var selectedStageFilter by rememberSaveable { mutableStateOf("All Stages") }
    var selectedTypeFilter by rememberSaveable { mutableStateOf("All") } // All, Resource Guide, Life Stage Tip
    var sortByMostUpvoted by rememberSaveable { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showShareComposer by rememberSaveable { mutableStateOf(false) }

    // Composer fields for sharing directly to Firestore
    var newTipTitle by rememberSaveable { mutableStateOf("") }
    var newTipContent by rememberSaveable { mutableStateOf("") }
    var newTipStage by rememberSaveable {
        mutableStateOf(
            when {
                currentPersonaRole.contains("kid", ignoreCase = true) -> "Kid"
                currentPersonaRole.contains("teen", ignoreCase = true) -> "Teen"
                currentPersonaRole.contains("student", ignoreCase = true) -> "Student"
                currentPersonaRole.contains("business", ignoreCase = true) -> "Business"
                currentPersonaRole.contains("veteran", ignoreCase = true) -> "Veteran"
                else -> "Adult"
            }
        )
    }
    var newTipCategory by rememberSaveable { mutableStateOf("Resource Guide") }
    var statusFeedbackBanner by rememberSaveable { mutableStateOf<String?>(null) }

    // Track local upvote deltas and upvoted state so users get immediate responsive upvote feedback
    // in addition to real Firestore persistence via onUpvoteFirestorePost(post)
    val localUpvoteBonusMap = remember { mutableStateMapOf<String, Long>() }
    val upvotedByMeSet = remember { mutableStateMapOf<String, Boolean>() }

    // Extract live Firestore posts from postsState
    val firestorePosts: List<WallPost> = when (postsState) {
        is UiState.Success -> postsState.data
        else -> emptyList()
    }

    val combinedItems = remember(firestorePosts, localUpvoteBonusMap.toMap()) {
        val mappedFirestoreItems = firestorePosts.map { post ->
            val inferredStage = inferLifeStageFromPost(post)
            val categoryDisplay = if (post.category.equals("Resource Guide", ignoreCase = true)) {
                "Resource Guide"
            } else {
                "Life Stage Tip"
            }
            CommunityLifeStageSharedItem(
                id = "fs_${post.id}",
                title = post.title.ifBlank { "Shared ${inferredStage} Tip" },
                content = post.content,
                authorName = "${post.authorName.ifBlank { currentUserDisplayName }} • Firestore Live",
                lifeStagePersona = inferredStage,
                categoryLabel = categoryDisplay,
                baseUpvotes = post.likesCount,
                accentHex = post.accentHex.ifBlank { "#00E5FF" },
                isFromFirestore = true,
                firestoreWallPost = post
            )
        }
        mappedFirestoreItems + CURATED_FIRESTORE_SEED_TIPS
    }

    val filteredAndSortedItems = remember(
        combinedItems,
        selectedStageFilter,
        selectedTypeFilter,
        sortByMostUpvoted,
        searchQuery,
        localUpvoteBonusMap.toMap()
    ) {
        combinedItems
            .filter { item ->
                val matchesStage = selectedStageFilter == "All Stages" ||
                    item.lifeStagePersona.equals(selectedStageFilter, ignoreCase = true)
                val matchesType = selectedTypeFilter == "All" ||
                    item.categoryLabel.equals(selectedTypeFilter, ignoreCase = true)
                val q = searchQuery.trim()
                val matchesQuery = q.isEmpty() ||
                    item.title.contains(q, ignoreCase = true) ||
                    item.content.contains(q, ignoreCase = true) ||
                    item.authorName.contains(q, ignoreCase = true) ||
                    item.lifeStagePersona.contains(q, ignoreCase = true)
                matchesStage && matchesType && matchesQuery
            }
            .let { list ->
                if (sortByMostUpvoted) {
                    list.sortedByDescending { item ->
                        item.baseUpvotes + (localUpvoteBonusMap[item.id] ?: 0L)
                    }
                } else {
                    list
                }
            }
    }

    val totalCommunityUpvotes = combinedItems.sumOf { item ->
        item.baseUpvotes + (localUpvoteBonusMap[item.id] ?: 0L)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("community_life_stage_hub_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
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
                                    listOf(HoloCyan, CyberEmerald)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "Community Hub Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "STAGES OF LIFE • COMMUNITY HUB",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Firestore-Synced Life Stage Tips, Guides & Helpful Upvotes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (postsState is UiState.Loading) Icons.Default.CloudSync else Icons.Default.CloudDone,
                            contentDescription = "Firestore Sync Status",
                            tint = CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${firestorePosts.size} Cloud • $totalCommunityUpvotes ▲",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Action & Search Row
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_hub_search_input"),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Community Hub",
                        tint = HoloCyan
                    )
                },
                label = {
                    Text("Search shared life stage tips, resources, or personas...")
                }
            )

            // Life Stage Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stageFilters.forEach { stage ->
                    val isSelected = selectedStageFilter == stage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStageFilter = stage },
                        label = {
                            Text(
                                text = stage,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                            selectedLabelColor = HoloCyan
                        ),
                        modifier = Modifier.testTag("community_hub_stage_filter_${stage.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // Content Type & Sort Controls + Share Button
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Resource Guide", "Life Stage Tip").forEach { typeLabel ->
                    val selected = selectedTypeFilter == typeLabel
                    FilterChip(
                        selected = selected,
                        onClick = { selectedTypeFilter = typeLabel },
                        label = { Text(typeLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SolarAmber.copy(alpha = 0.22f),
                            selectedLabelColor = SolarAmber
                        )
                    )
                }

                FilterChip(
                    selected = sortByMostUpvoted,
                    onClick = { sortByMostUpvoted = !sortByMostUpvoted },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Sort by Most Upvoted",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(if (sortByMostUpvoted) "Sorted: Most Upvoted ▲" else "Sorted: Latest Sync")
                    }
                )

                Button(
                    onClick = { showShareComposer = !showShareComposer },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("community_hub_toggle_composer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Share Tip or Resource",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showShareComposer) "Close Form" else "Share Tip / Resource",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Optional Composer to Publish Shared Tip or Resource Directly to Firestore
            if (showShareComposer) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("community_hub_composer_panel")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Publish Life Stage Tip or Resource to Firestore",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald
                        )

                        // Target Stage Selection
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Kid", "Teen", "Student", "Adult", "Business", "Veteran").forEach { stageOption ->
                                FilterChip(
                                    selected = newTipStage == stageOption,
                                    onClick = { newTipStage = stageOption },
                                    label = { Text(stageOption) }
                                )
                            }
                        }

                        // Category Selection (Maps cleanly to Firestore security rules: 'Resource Guide' or 'Milestone')
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = newTipCategory == "Resource Guide",
                                onClick = { newTipCategory = "Resource Guide" },
                                label = { Text("Resource Guide") }
                            )
                            FilterChip(
                                selected = newTipCategory == "Milestone",
                                onClick = { newTipCategory = "Milestone" },
                                label = { Text("Life Stage Tip") }
                            )
                        }

                        OutlinedTextField(
                            value = newTipTitle,
                            onValueChange = { newTipTitle = it },
                            label = { Text("Tip or Resource Title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("community_hub_new_title_input")
                        )

                        OutlinedTextField(
                            value = newTipContent,
                            onValueChange = { newTipContent = it },
                            label = { Text("Actionable advice, steps, or resource details...") },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("community_hub_new_content_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val cleanTitle = newTipTitle.trim()
                                    val cleanContent = newTipContent.trim()
                                    if (cleanTitle.isNotEmpty() && cleanContent.isNotEmpty()) {
                                        val taggedTitle = "[$newTipStage] $cleanTitle"
                                        onCreateFirestoreTipOrResource(
                                            newTipCategory,
                                            taggedTitle,
                                            cleanContent
                                        )
                                        newTipTitle = ""
                                        newTipContent = ""
                                        showShareComposer = false
                                        statusFeedbackBanner = "Published \"$taggedTitle\" to Firestore Community Hub!"
                                    } else {
                                        statusFeedbackBanner = "Please enter both a title and helpful details before publishing."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloCyan,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("community_hub_submit_post_button")
                            ) {
                                Text("Publish to Firestore", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            statusFeedbackBanner?.let { message ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Status",
                            tint = CyberEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Shared Tips & Resources Feed List
            if (filteredAndSortedItems.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No matching life stage tips or resources found.",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try selecting 'All Stages' or share the first tip for this filter!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredAndSortedItems.forEach { item ->
                        val itemAccent = parseThemeHex(item.accentHex)
                        val extraVotes = localUpvoteBonusMap[item.id] ?: 0L
                        val effectiveUpvotes = item.baseUpvotes + extraVotes
                        val hasUpvoted = upvotedByMeSet[item.id] == true

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            border = BorderStroke(
                                width = if (hasUpvoted) 1.5.dp else 1.dp,
                                color = if (hasUpvoted) CyberEmerald else itemAccent.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("community_hub_item_${item.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = itemAccent.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, itemAccent)
                                        ) {
                                            Text(
                                                text = item.lifeStagePersona.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = itemAccent,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                        ) {
                                            Text(
                                                text = item.categoryLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        if (item.isFromFirestore) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = CyberEmerald.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = "☁ Firestore",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CyberEmerald,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (effectiveUpvotes >= 45L) {
                                        Text(
                                            text = "🔥 Top Helpful",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SolarAmber
                                        )
                                    }
                                }

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = item.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Shared by ${item.authorName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    OutlinedButton(
                                        onClick = {
                                            // Persist to Firestore if backed by a WallPost document
                                            item.firestoreWallPost?.let { wallPost ->
                                                onUpvoteFirestorePost(wallPost)
                                            }
                                            localUpvoteBonusMap[item.id] = (localUpvoteBonusMap[item.id] ?: 0L) + 1L
                                            upvotedByMeSet[item.id] = true
                                            onRewardHelpfulVote()
                                            statusFeedbackBanner = "Upvoted \"${item.title}\" (+1 Helpful Vote synced)"
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (hasUpvoted) CyberEmerald else HoloCyan
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (hasUpvoted) {
                                                CyberEmerald.copy(alpha = 0.18f)
                                            } else {
                                                HoloCyan.copy(alpha = 0.10f)
                                            }
                                        ),
                                        modifier = Modifier.testTag("community_hub_upvote_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = "Upvote Helpful Content",
                                            tint = if (hasUpvoted) CyberEmerald else HoloCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "▲ Helpful ($effectiveUpvotes)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (hasUpvoted) CyberEmerald else HoloCyan
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
}

private fun inferLifeStageFromPost(post: WallPost): String {
    val combined = "${post.title} ${post.authorRole} ${post.content}"
    return when {
        combined.contains("kid", ignoreCase = true) -> "Kid"
        combined.contains("teen", ignoreCase = true) -> "Teen"
        combined.contains("student", ignoreCase = true) -> "Student"
        combined.contains("veteran", ignoreCase = true) -> "Veteran"
        combined.contains("business", ignoreCase = true) -> "Business"
        else -> "Adult"
    }
}
