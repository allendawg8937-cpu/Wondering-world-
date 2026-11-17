package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.ResourceDirectoryItem
import com.example.data.model.WallPost
import com.example.data.model.normalizePersonaStageId
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import com.example.ui.theme.parseThemeHex

enum class CommunityResourceCategory(val label: String) {
    ALL("All"),
    GRANTS_AID("Grants & Aid"),
    PEER_CIRCLES("Peer Circles"),
    LEARNING("Learning & STEM"),
    CAREER_BIZ("Career & Biz"),
    COMMUNITY_POSTS("Community Posts")
}

data class StageCommunityResource(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: CommunityResourceCategory,
    val stageIds: List<String>,
    val badge: String,
    val providerLabel: String,
    val url: String?,
    val accentHex: String,
    val isFromCloudPost: Boolean = false,
    val matchScore: Int = 96
)

private val BUILTIN_STAGE_COMMUNITY_PROGRAMS: List<StageCommunityResource> = listOf(
    StageCommunityResource(
        id = "prog_kid_nasa_stem",
        title = "NASA Space Place & Cosmic STEM Club",
        subtitle = "Hands-on planetary experiments, block coding missions, and supervised young-explorer quizzes.",
        category = CommunityResourceCategory.LEARNING,
        stageIds = listOf("kid"),
        badge = "KID STEM • SAFE SPACE",
        providerLabel = "NASA Youth STEM Circle",
        url = "https://spaceplace.nasa.gov/",
        accentHex = "#10B981",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_kid_scratch_coders",
        title = "Block Coding Creative Guild (Ages 5–12)",
        subtitle = "Build interactive 3D stories, math puzzle games, and weekly science doodles with moderated family mentors.",
        category = CommunityResourceCategory.PEER_CIRCLES,
        stageIds = listOf("kid"),
        badge = "COPPA PEER CLUB",
        providerLabel = "Scratch & Open Library",
        url = "https://scratch.mit.edu/",
        accentHex = "#06B6D4",
        matchScore = 97
    ),
    StageCommunityResource(
        id = "prog_teen_hackathons",
        title = "Teen Hackathon & Digital Creator Circle",
        subtitle = "Weekly Kotlin, UI design, and beatmaking jams plus verified community service hours tracking.",
        category = CommunityResourceCategory.PEER_CIRCLES,
        stageIds = listOf("teen"),
        badge = "YOUTH CREATOR LAB",
        providerLabel = "Teen Builder Collective",
        url = "https://mlh.io/",
        accentHex = "#EC4899",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_teen_scholarships",
        title = "Early High School Scholarship & Portfolio Finder",
        subtitle = "Curated $500–$5,000 youth awards, AP STEM prep guides, and paid summer internship directories.",
        category = CommunityResourceCategory.GRANTS_AID,
        stageIds = listOf("teen"),
        badge = "SCHOLARSHIPS & PREP",
        providerLabel = "BigFuture & Youth Aid",
        url = "https://bigfuture.collegeboard.org/",
        accentHex = "#A855F7",
        matchScore = 97
    ),
    StageCommunityResource(
        id = "prog_student_campus_hall",
        title = "Campus STEM & Exam Prep Study Circle",
        subtitle = "Peer study sprints, AI flashcard swaps, and undergraduate research mentorship matching.",
        category = CommunityResourceCategory.LEARNING,
        stageIds = listOf("student"),
        badge = "HIGHER ED PEERS",
        providerLabel = "Academic Excellence Hub",
        url = "https://openstax.org/",
        accentHex = "#6366F1",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_student_fafsa_grant",
        title = "FAFSA, Pell Grant & Paid Pathways Desk",
        subtitle = "Step-by-step aid filing workshops and paid federal/tech student internship cohorts.",
        category = CommunityResourceCategory.GRANTS_AID,
        stageIds = listOf("student"),
        badge = "STUDENT AID & JOBS",
        providerLabel = "Federal Student Aid",
        url = "https://studentaid.gov/",
        accentHex = "#00E5FF",
        matchScore = 98
    ),
    StageCommunityResource(
        id = "prog_adult_stability_211",
        title = "211 Local Housing, Healthcare & Bill Relief Network",
        subtitle = "Connect immediately with verified sliding-scale clinics, utility support, and household stability coaches.",
        category = CommunityResourceCategory.GRANTS_AID,
        stageIds = listOf("adult"),
        badge = "211 STABILITY RELIEF",
        providerLabel = "United Way 211 Hub",
        url = "https://www.211.org/",
        accentHex = "#06B6D4",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_adult_career_circle",
        title = "Career Pivot & Emergency Savings Circle",
        subtitle = "Peer resume reviews, 3-month emergency reserve accountability challenges, and skilled trade transitions.",
        category = CommunityResourceCategory.CAREER_BIZ,
        stageIds = listOf("adult"),
        badge = "CAREER & SAVINGS",
        providerLabel = "Community Finance Circle",
        url = "https://www.careeronestop.org/",
        accentHex = "#10B981",
        matchScore = 97
    ),
    StageCommunityResource(
        id = "prog_biz_sba_sbdc",
        title = "SBA & SBDC Free Founder Legal & Capital Clinic",
        subtitle = "Zero-cost 1-on-1 business planning, LLC/EIN checklists, and SBA 7(a) micro-grant readiness audits.",
        category = CommunityResourceCategory.CAREER_BIZ,
        stageIds = listOf("business"),
        badge = "SBA FOUNDER SUITE",
        providerLabel = "Small Business Admin",
        url = "https://www.sba.gov/",
        accentHex = "#F59E0B",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_biz_mastermind",
        title = "SCORE Mentorship & Local Commerce Cooperative",
        subtitle = "Pair with seasoned operators for quarterly cashflow reviews and B2B storefront partnerships.",
        category = CommunityResourceCategory.PEER_CIRCLES,
        stageIds = listOf("business"),
        badge = "FOUNDER PEER CIRCLE",
        providerLabel = "SCORE Business Mentors",
        url = "https://www.score.org/",
        accentHex = "#EC4899",
        matchScore = 97
    ),
    StageCommunityResource(
        id = "prog_vet_988_va_care",
        title = "VA PACT Act Health & 988 Veterans Support",
        subtitle = "Locate VA clinics, file PACT Act benefits screenings, or reach confidential 24/7 peer crisis counselors.",
        category = CommunityResourceCategory.GRANTS_AID,
        stageIds = listOf("veteran"),
        badge = "24/7 VA & 988 PRESS 1",
        providerLabel = "U.S. Dept of Veterans Affairs",
        url = "https://www.va.gov/find-locations/",
        accentHex = "#8B5CF6",
        matchScore = 99
    ),
    StageCommunityResource(
        id = "prog_vet_skillbridge",
        title = "DAV / VFW Peer Network & GI Bill Transition Hub",
        subtitle = "Post-9/11 GI Bill housing stipend tools, DoD SkillBridge fellowships, and veteran founder mentors.",
        category = CommunityResourceCategory.PEER_CIRCLES,
        stageIds = listOf("veteran"),
        badge = "VETERAN PEER MENTORS",
        providerLabel = "DAV & Veteran Chapter",
        url = "https://www.va.gov/education/about-gi-bill-benefits/",
        accentHex = "#10B981",
        matchScore = 98
    )
)

fun mapCuratedDirectoryToStageCommunityResource(
    item: ResourceDirectoryItem,
    stage: DashboardLifeStage
): StageCommunityResource {
    val category = when {
        item.tags.any { it in listOf("fafsa", "grants", "housing", "food", "bills", "211", "crisis", "988", "va", "healthcare") } ->
            CommunityResourceCategory.GRANTS_AID
        item.tags.any { it in listOf("business", "jobs", "career", "entrepreneur", "trades", "resume", "internship") } ->
            CommunityResourceCategory.CAREER_BIZ
        else -> CommunityResourceCategory.LEARNING
    }
    return StageCommunityResource(
        id = "dir_${item.id}",
        title = item.title,
        subtitle = item.subtitle,
        category = category,
        stageIds = item.roles.map { normalizePersonaStageId(it) }.distinct(),
        badge = item.badge,
        providerLabel = "Curated Verified Directory",
        url = item.url,
        accentHex = stage.accentHex,
        isFromCloudPost = false,
        matchScore = 94
    )
}

fun buildTailoredCommunityResourcesForStage(
    stage: DashboardLifeStage,
    curatedDirectory: List<ResourceDirectoryItem>,
    remoteAndLocalPosts: List<WallPost>
): List<StageCommunityResource> {
    val stagePrograms = BUILTIN_STAGE_COMMUNITY_PROGRAMS.filter { stage.stageId in it.stageIds }

    val matchingPosts = remoteAndLocalPosts
        .filter { post ->
            val normalizedPostRole = normalizePersonaStageId(post.authorRole)
            val textBlob = "${post.title} ${post.content} ${post.category}".lowercase()
            normalizedPostRole == stage.stageId ||
                textBlob.contains(stage.label.lowercase()) ||
                textBlob.contains(stage.stageId) ||
                stage.suggestedTopics.any { topic -> textBlob.contains(topic.lowercase().substringBefore(" ")) }
        }
        .map { post ->
            StageCommunityResource(
                id = "post_${post.id}",
                title = post.title.ifBlank { "${stage.label} Community Update" },
                subtitle = post.content.ifBlank { "Shared by ${post.authorName} for the ${stage.label} stage community." },
                category = CommunityResourceCategory.COMMUNITY_POSTS,
                stageIds = listOf(stage.stageId),
                badge = "${post.category.uppercase()} • ${post.likesCount} Likes",
                providerLabel = "${post.authorName.ifBlank { "Community Member" }} • Live Post",
                url = null,
                accentHex = post.accentHex.ifBlank { stage.accentHex },
                isFromCloudPost = true,
                matchScore = if (normalizePersonaStageId(post.authorRole) == stage.stageId) 98 else 90
            )
        }

    val mappedCurated = curatedDirectory
        .map { mapCuratedDirectoryToStageCommunityResource(it, stage) }
        .filter { stage.stageId in it.stageIds || it.stageIds.isEmpty() }

    return (stagePrograms + matchingPosts + mappedCurated)
        .distinctBy { it.id }
        .sortedWith(compareByDescending<StageCommunityResource> { it.matchScore }.thenBy { it.title })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaCommunityHubView(
    activeRoleOrStageId: String,
    curatedResources: List<ResourceDirectoryItem> = emptyList(),
    wallPostsState: UiState<List<WallPost>> = UiState.Success(emptyList()),
    onFetchStagePosts: suspend (String) -> Result<List<WallPost>> = { Result.success(emptyList()) },
    onSelectStage: (DashboardLifeStage) -> Unit = {},
    onOpenUrl: ((String) -> Unit)? = null,
    onShareResourceToWall: (title: String, body: String) -> Unit = { _, _ -> },
    onRecordSignal: (String) -> Unit = {},
    savedResourceIds: Set<String> = emptySet(),
    onToggleSaveResource: ((StageCommunityResource) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val resolvedExternalStage = remember(activeRoleOrStageId) {
        DashboardLifeStage.fromRoleOrStageId(activeRoleOrStageId)
    }

    var selectedCategory by rememberSaveable { mutableStateOf(CommunityResourceCategory.ALL) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var fetchState by remember { mutableStateOf<UiState<List<StageCommunityResource>>>(UiState.Loading) }
    val localSavedResourceIds = remember { mutableStateMapOf<String, Boolean>() }
    var statusBanner by rememberSaveable { mutableStateOf<String?>(null) }

    val activePosts = remember(wallPostsState) {
        (wallPostsState as? UiState.Success)?.data ?: emptyList()
    }

    val stageAccent by animateColorAsState(
        targetValue = parseThemeHex(resolvedExternalStage.accentHex),
        animationSpec = tween(durationMillis = 260),
        label = "community_hub_stage_accent"
    )

    // Dynamically fetch & rank resources whenever the active persona stage or refresh trigger changes
    LaunchedEffect(resolvedExternalStage, activePosts, curatedResources, refreshTrigger) {
        fetchState = UiState.Loading
        val remoteResult = onFetchStagePosts(resolvedExternalStage.stageId)
        if (remoteResult.isFailure) {
            val errMsg = remoteResult.exceptionOrNull()?.message
                ?: "Unable to fetch live community resources right now."
            fetchState = UiState.Error(errMsg)
        } else {
            val mergedPosts = (remoteResult.getOrNull().orEmpty() + activePosts).distinctBy { it.id }
            val tailored = buildTailoredCommunityResourcesForStage(
                stage = resolvedExternalStage,
                curatedDirectory = curatedResources,
                remoteAndLocalPosts = mergedPosts
            )
            fetchState = UiState.Success(tailored)
        }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        ),
        border = BorderStroke(1.2.dp, stageAccent.copy(alpha = 0.55f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("persona_community_hub_view")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = stageAccent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, stageAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_hub_stage_header_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${resolvedExternalStage.emoji} ${resolvedExternalStage.label} Community Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.testTag("community_hub_stage_header")
                        )
                        Text(
                            text = "${resolvedExternalStage.guardrailBadge} • ${resolvedExternalStage.headline}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = {
                            refreshTrigger++
                            onRecordSignal("Refreshed ${resolvedExternalStage.label} community resources")
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("community_hub_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh tailored resources",
                            tint = stageAccent
                        )
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth >= 460.dp) 3 else 2
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DashboardLifeStage.entries.chunked(cols).forEach { rowStages ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowStages.forEach { stage ->
                                val selected = stage == resolvedExternalStage
                                val chipColor = parseThemeHex(stage.accentHex)
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        onSelectStage(stage)
                                        onRecordSignal("Switched Community Hub to ${stage.label}")
                                    },
                                    label = {
                                        Text(
                                            text = "${stage.emoji} ${stage.label}",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = chipColor.copy(alpha = 0.22f),
                                        selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .semantics {
                                            contentDescription = "Show community resources for ${stage.label}"
                                        }
                                        .testTag("community_hub_stage_chip_${stage.stageId}")
                                )
                            }
                            repeat(cols - rowStages.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search resources"
                    )
                },
                label = { Text("Search ${resolvedExternalStage.label} grants, peers & guides") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_hub_search_input")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CommunityResourceCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.label, maxLines = 1) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("community_hub_category_chip_${category.name.lowercase()}")
                    )
                }
            }

            if (statusBanner != null) {
                Text(
                    text = statusBanner!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.testTag("community_hub_status_banner")
                )
            }

            AnimatedContent(
                targetState = fetchState,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(140)) },
                label = "community_hub_fetch_state"
            ) { currentState ->
                when (currentState) {
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color = stageAccent,
                                modifier = Modifier.testTag("community_hub_loading_indicator")
                            )
                            Text(
                                text = "Fetching resources tailored for ${resolvedExternalStage.label}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is UiState.Error -> {
                        Surface(
                            color = RosePulse.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("community_hub_error_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Button(
                                    onClick = { refreshTrigger++ },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("community_hub_retry_button")
                                ) {
                                    Text("Retry Fetch")
                                }
                            }
                        }
                    }

                    is UiState.Success -> {
                        val filtered = remember(currentState.data, selectedCategory, searchQuery) {
                            val q = searchQuery.trim()
                            currentState.data.filter { item ->
                                val matchesCategory = selectedCategory == CommunityResourceCategory.ALL ||
                                    item.category == selectedCategory
                                val matchesQuery = q.isEmpty() ||
                                    item.title.contains(q, ignoreCase = true) ||
                                    item.subtitle.contains(q, ignoreCase = true) ||
                                    item.providerLabel.contains(q, ignoreCase = true) ||
                                    item.badge.contains(q, ignoreCase = true)
                                matchesCategory && matchesQuery
                            }
                        }

                        if (filtered.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("community_hub_empty_state")
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = "No matching resources",
                                        tint = stageAccent
                                    )
                                    Text(
                                        text = "No ${resolvedExternalStage.label} resources match that filter.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    OutlinedButton(
                                        onClick = {
                                            selectedCategory = CommunityResourceCategory.ALL
                                            searchQuery = ""
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Text("Reset Filters")
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "${filtered.size} resources matched to your ${resolvedExternalStage.label} stage",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = stageAccent,
                                    fontWeight = FontWeight.Bold
                                )

                                filtered.forEach { resource ->
                                    val itemAccent = parseThemeHex(resource.accentHex)
                                    val sanitizedId = resource.id.replace(Regex("[^A-Za-z0-9_-]"), "_").take(100)
                                    val isSaved = if (onToggleSaveResource != null) {
                                        resource.id in savedResourceIds || sanitizedId in savedResourceIds
                                    } else {
                                        localSavedResourceIds[resource.id] == true || resource.id in savedResourceIds
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
                                        border = BorderStroke(1.dp, itemAccent.copy(alpha = 0.45f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("community_hub_resource_card_${resource.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = itemAccent.copy(alpha = 0.18f)
                                                ) {
                                                    Text(
                                                        text = resource.badge,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = itemAccent,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    if (resource.isFromCloudPost) {
                                                        Icon(
                                                            imageVector = Icons.Default.CloudDone,
                                                            contentDescription = "Synced live post",
                                                            tint = CyberEmerald,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = "${resource.matchScore}% Match",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = HoloCyan,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }
                                            }

                                            Text(
                                                text = resource.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = resource.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = resource.providerLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = itemAccent
                                            )

                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        val nextSaved = !isSaved
                                                        if (onToggleSaveResource != null) {
                                                            onToggleSaveResource(resource)
                                                        } else {
                                                            localSavedResourceIds[resource.id] = nextSaved
                                                        }
                                                        if (nextSaved) {
                                                            statusBanner = "Saved '${resource.title}' for ${resolvedExternalStage.label}."
                                                            onRecordSignal("Saved ${resolvedExternalStage.label} resource: ${resource.title}")
                                                        } else {
                                                            statusBanner = "Removed '${resource.title}' from saved resources."
                                                        }
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    modifier = Modifier
                                                        .minimumInteractiveComponentSize()
                                                        .testTag("bookmark_resource_btn_${resource.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                        contentDescription = "Bookmark resource",
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(if (isSaved) "Saved" else "Save", style = MaterialTheme.typography.labelSmall)
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        onShareResourceToWall(
                                                            "${resolvedExternalStage.emoji} ${resource.title}",
                                                            "${resource.subtitle} (Tailored for ${resolvedExternalStage.label} Community)"
                                                        )
                                                        statusBanner = "Shared '${resource.title}' to Community Wall!"
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    modifier = Modifier
                                                        .minimumInteractiveComponentSize()
                                                        .testTag("share_resource_btn_${resource.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Share,
                                                        contentDescription = "Share resource",
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Share", style = MaterialTheme.typography.labelSmall)
                                                }

                                                if (!resource.url.isNullOrBlank()) {
                                                    Button(
                                                        onClick = {
                                                            onRecordSignal("Opened ${resolvedExternalStage.label} resource: ${resource.title}")
                                                            if (onOpenUrl != null) {
                                                                onOpenUrl(resource.url)
                                                            } else {
                                                                uriHandler.openUri(resource.url)
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = itemAccent,
                                                            contentColor = Color(0xFF06101B)
                                                        ),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                        modifier = Modifier
                                                            .minimumInteractiveComponentSize()
                                                            .testTag("open_resource_btn_${resource.id}")
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                            contentDescription = "Open external resource",
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Open", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
        }
    }
}
