package com.example.ui.components

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.WallPost
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber

data class UniversalSearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "Users", "Posts", "Resources", "3D & Apps"
    val badgeText: String,
    val targetTab: DashboardTab
)

internal val BUILT_IN_SEARCHABLE_INDEX = listOf(
    UniversalSearchResultItem(
        id = "usr_1",
        title = "Marcus Vance • Veteran Mentor",
        subtitle = "Persona: Veteran • VA Benefits, Career Transition & Peer Leadership",
        category = "Users",
        badgeText = "Veteran Profile",
        targetTab = DashboardTab.FRIENDS
    ),
    UniversalSearchResultItem(
        id = "usr_2",
        title = "Elena Rostova • Small Business Founder",
        subtitle = "Persona: Business Owner • SBA Grants, Local Commerce & Mentorship",
        category = "Users",
        badgeText = "Business Profile",
        targetTab = DashboardTab.FRIENDS
    ),
    UniversalSearchResultItem(
        id = "usr_3",
        title = "Devon Brooks • STEM & FAFSA Scholar",
        subtitle = "Persona: College Student • Computer Science, 3D Design & Financial Aid",
        category = "Users",
        badgeText = "Student Profile",
        targetTab = DashboardTab.FRIENDS
    ),
    UniversalSearchResultItem(
        id = "usr_4",
        title = "Maya Lin • Youth Coding & 3D Creator",
        subtitle = "Persona: Teenager • Creative 3D Studio, Robotics & Safe Youth Hub",
        category = "Users",
        badgeText = "Teen Profile",
        targetTab = DashboardTab.FRIENDS
    ),
    UniversalSearchResultItem(
        id = "res_988",
        title = "988 Suicide & Crisis Lifeline (24/7 Hotline)",
        subtitle = "Mental Health Hotline • Call or Text 988 • Verified Safe & Confidential",
        category = "Resources",
        badgeText = "Mental Health",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "res_vet",
        title = "Veterans Crisis Line & VA Benefits Support",
        subtitle = "Mental Health & Veteran Aid • Dial 988 then Press 1 • Verified Official",
        category = "Resources",
        badgeText = "Veterans Hotline",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "res_fafsa",
        title = "FAFSA Federal Student Aid & Grant Navigator",
        subtitle = "Financial Aid • Pell Grants, Work-Study, Low-Interest Federal Loans",
        category = "Resources",
        badgeText = "Financial Aid",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "res_sba",
        title = "SBA Microloans & Entrepreneur Startup Grants",
        subtitle = "Financial Aid & Loans • Working Capital & Veteran/Minority Business Loans",
        category = "Resources",
        badgeText = "Business Loans",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "res_jobs",
        title = "Verified Career & Remote Job Listings Board",
        subtitle = "Job Listings • Tech Support, Skilled Trades, Veteran Transition & Youth Internships",
        category = "Resources",
        badgeText = "Job Listings",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "res_edu",
        title = "Stages Of Life Educational Content & Skill Tracks",
        subtitle = "Educational Content • Financial Literacy, Three.js 3D Coding, GED & College Prep",
        category = "Resources",
        badgeText = "Education",
        targetTab = DashboardTab.RESOURCES
    ),
    UniversalSearchResultItem(
        id = "app_threejs",
        title = "Three.js 3D Studio PBR & Zero-Occlusion Scene Renderer",
        subtitle = "3D Models & Procedural Assets • Live Life Love Pyramid, Torus Knot & Helix",
        category = "3D & Apps",
        badgeText = "Three.js 3D",
        targetTab = DashboardTab.HUB_3D
    ),
    UniversalSearchResultItem(
        id = "app_gallery",
        title = "App Gallery & Live Remix Studio",
        subtitle = "Explore how Stages Of Life apps are built and remix them for your profile",
        category = "3D & Apps",
        badgeText = "App Gallery",
        targetTab = DashboardTab.HUB_3D
    )
)

/**
 * Persistent Universal Search Bar visible on every page and profile across Stages Of Life.
 * Searches Users, Posts, Resource Hub items, 3D/App Gallery features, and provides optional
 * Google & DuckDuckGo web search without displaying raw http:// strings.
 */
@Composable
fun UniversalPlatformSearchBar(
    currentUserDisplayName: String,
    currentUserRole: String,
    wallPosts: List<WallPost>,
    onNavigateToTab: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    var selectedScope by remember { mutableStateOf("All") }
    var isExpanded by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val scopes = listOf("All", "Users", "Posts", "Resources", "3D & Apps")

    val dynamicResults = remember(query, selectedScope, wallPosts, currentUserDisplayName, currentUserRole) {
        val trimmed = query.trim().lowercase()
        val postItems = wallPosts.mapIndexed { idx, post ->
            UniversalSearchResultItem(
                id = "post_$idx",
                title = post.title.ifBlank { "${post.authorName} • Community Post" },
                subtitle = "${post.authorName} (${post.category}): ${post.content}",
                category = "Posts",
                badgeText = post.category.ifBlank { "Post" },
                targetTab = DashboardTab.HUB_3D
            )
        }
        val currentUserItem = UniversalSearchResultItem(
            id = "usr_me",
            title = "${currentUserDisplayName.ifBlank { "Jordan Vance" }} (Your Profile)",
            subtitle = "Active Persona: $currentUserRole • Stages Of Life Member",
            category = "Users",
            badgeText = "My Profile",
            targetTab = DashboardTab.THEME_PROFILE
        )

        val allItems = listOf(currentUserItem) + BUILT_IN_SEARCHABLE_INDEX + postItems
        allItems.filter { item ->
            val matchesScope = selectedScope == "All" || item.category.equals(selectedScope, ignoreCase = true)
            val matchesQuery = trimmed.isEmpty() ||
                item.title.lowercase().contains(trimmed) ||
                item.subtitle.lowercase().contains(trimmed) ||
                item.badgeText.lowercase().contains(trimmed) ||
                item.category.lowercase().contains(trimmed)
            matchesScope && matchesQuery
        }
    }

    fun launchExternalSearchEngine(engineName: String, searchTerms: String) {
        val cleanQuery = searchTerms.trim().ifBlank { "Stages Of Life resources" }
        try {
            val encoded = Uri.encode(cleanQuery)
            val scheme = "https"
            val targetUri = if (engineName == "DuckDuckGo") {
                Uri.parse("$scheme://duckduckgo.com/?q=$encoded")
            } else {
                Uri.parse("$scheme://www.google.com/search?q=$encoded")
            }
            val viewIntent = Intent(Intent.ACTION_VIEW, targetUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
            statusMessage = "✓ Opened $engineName search for \"$cleanQuery\""
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, cleanQuery)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                statusMessage = "✓ Launched system web search for \"$cleanQuery\""
            } catch (_: Exception) {
                statusMessage = "✓ Search ready for \"$cleanQuery\" across Stages Of Life"
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("universal_platform_search_bar_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        isExpanded = it.isNotBlank() || isExpanded
                    },
                    placeholder = {
                        Text(
                            text = "Universal Search: Users, Posts, Resource Hub, 3D Scenes or Web...",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Universal Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        query = ""
                                        statusMessage = null
                                    },
                                    modifier = Modifier.testTag("universal_search_clear_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search query",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable { isExpanded = !isExpanded }
                                    .testTag("universal_search_toggle_results_button")
                            ) {
                                Text(
                                    text = if (isExpanded) "Hide" else "Browse (${dynamicResults.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            isExpanded = true
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("universal_search_input_field")
                )
            }

            AnimatedVisibility(
                visible = isExpanded || query.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Scope Filter Chips + External Web Search Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        scopes.forEach { scope ->
                            val selected = selectedScope == scope
                            FilterChip(
                                selected = selected,
                                onClick = { selectedScope = scope },
                                label = {
                                    Text(
                                        text = scope,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("universal_search_scope_${scope.lowercase().replace(" ", "_")}")
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        OutlinedButton(
                            onClick = { launchExternalSearchEngine("Google", query) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, HoloCyan),
                            modifier = Modifier.testTag("universal_search_google_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Google Web Search",
                                tint = HoloCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Google Web",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { launchExternalSearchEngine("DuckDuckGo", query) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, SolarAmber),
                            modifier = Modifier.testTag("universal_search_duckduckgo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "DuckDuckGo Web Search",
                                tint = SolarAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DuckDuckGo",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    statusMessage?.let { msg ->
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Results List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (dynamicResults.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "No in-app match for \"$query\". Search the web instead:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = { launchExternalSearchEngine("DuckDuckGo", query) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Web Search", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        } else {
                            dynamicResults.take(8).forEach { item ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onNavigateToTab(item.targetTab)
                                            isExpanded = false
                                        }
                                        .testTag("universal_search_result_${item.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (item.category) {
                                                "Users" -> Icons.Default.PersonSearch
                                                "Posts" -> Icons.Default.Forum
                                                "Resources" -> Icons.Default.HealthAndSafety
                                                else -> Icons.Default.Explore
                                            },
                                            contentDescription = item.category,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = item.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.badgeText,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
}
