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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.WallPost
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import kotlin.math.abs

/**
 * Combined Top Header Component displayed at the top of every page in the app:
 * merges the Universal Platform Search Bar + Dark Mode Switch + Night Care + Solid Color Changes
 * + Background Transparency controls into one compact, unified surface.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnifiedSearchAndThemeHeaderBar(
    // Dark Mode & Theme Colors
    isDarkMode: Boolean,
    followSystemTheme: Boolean,
    isLateNightEyeComfort: Boolean,
    nightDimmerAlpha: Float,
    useDynamicColor: Boolean,
    currentThemeHex: String = "#2563EB",
    onSelectThemeHex: (String) -> Unit = {},
    backgroundTransparency: Float = 0.0f,
    onTransparencyChange: (Float) -> Unit = {},
    onSetDarkMode: (Boolean) -> Unit,
    onSetFollowSystemTheme: (Boolean) -> Unit,
    onSetLateNightEyeComfort: (Boolean) -> Unit,
    onSetNightDimmerAlpha: (Float) -> Unit,
    onSetUseDynamicColor: (Boolean) -> Unit,
    // Universal Search
    currentUserDisplayName: String,
    currentUserRole: String,
    wallPosts: List<WallPost>,
    onNavigateToTab: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val systemDark = isSystemInDarkTheme()

    var isThemeExpanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var selectedScope by remember { mutableStateOf("All") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchStatusMessage by remember { mutableStateOf<String?>(null) }

    val solidColors = remember {
        EXPANDED_THEME_COLOR_OPTIONS.filter { it.category == "Solid Colors" }
    }
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
            searchStatusMessage = "✓ Opened $engineName search for \"$cleanQuery\""
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, cleanQuery)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                searchStatusMessage = "✓ Launched system web search for \"$cleanQuery\""
            } catch (_: Exception) {
                searchStatusMessage = "✓ Search ready for \"$cleanQuery\" across Stages Of Life"
            }
        }
    }

    val headerAlpha = (0.96f * (1f - backgroundTransparency.coerceIn(0f, 1f))).coerceIn(0.0f, 0.96f)

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = headerAlpha),
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("unified_search_and_theme_header_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Universal Search Input + Inline Dark Mode Switch + Night Care + Theme Options Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_platform_search_bar_container"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        isSearchExpanded = it.isNotBlank() || isSearchExpanded
                    },
                    placeholder = {
                        Text(
                            text = "Search pages, users, posts or web...",
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
                                        searchStatusMessage = null
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
                                    .padding(end = 4.dp)
                                    .clickable { isSearchExpanded = !isSearchExpanded }
                                    .testTag("universal_search_toggle_results_button")
                            ) {
                                Text(
                                    text = if (isSearchExpanded) "Hide" else "${dynamicResults.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            isSearchExpanded = true
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("universal_search_input_field")
                )

                // Dark / Light Mode Quick Switch
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.testTag("system_wide_dark_mode_header_bar")
                ) {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            onSetFollowSystemTheme(false)
                            onSetDarkMode(enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = HoloCyan,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = SolarAmber
                        ),
                        modifier = Modifier.testTag("system_wide_dark_mode_switch")
                    )
                }

                // Expand Theme / Color / Transparency Controls
                Surface(
                    color = if (isThemeExpanded) HoloCyan.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        if (isThemeExpanded) HoloCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { isThemeExpanded = !isThemeExpanded }
                        .testTag("header_dark_mode_expand_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Expand Theme & Color Options",
                            tint = HoloCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Row 2: Compact Combined Dark Mode Pill + Night Care + 1-Tap Solid Color Swatches Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isDarkMode) HoloCyan.copy(alpha = 0.18f) else SolarAmber.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, if (isDarkMode) HoloCyan else SolarAmber),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            onSetFollowSystemTheme(false)
                            onSetDarkMode(!isDarkMode)
                        }
                        .testTag("header_mode_quick_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = if (isDarkMode) "Dark Mode Active" else "Light Mode Active",
                            tint = if (isDarkMode) HoloCyan else SolarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isDarkMode) "Dark" else "Light",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDarkMode) HoloCyan else SolarAmber
                        )
                    }
                }

                Surface(
                    color = if (isLateNightEyeComfort) SolarAmber.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            val next = !isLateNightEyeComfort
                            onSetLateNightEyeComfort(next)
                            if (next && !isDarkMode) {
                                onSetFollowSystemTheme(false)
                                onSetDarkMode(true)
                            }
                        }
                        .testTag("header_late_night_comfort_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightlightRound,
                            contentDescription = "Late-Night Eye Comfort",
                            tint = if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isLateNightEyeComfort) "Night ON" else "Night",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isLateNightEyeComfort) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                solidColors.forEach { option ->
                    val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                    val isLightSwatch = option.hex.equals("#FFFFFF", ignoreCase = true) ||
                        option.hex.equals("#EAB308", ignoreCase = true) ||
                        option.hex.equals("#9CA3AF", ignoreCase = true)
                    Surface(
                        color = option.color,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = if (isSelected) 2.2.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                onSelectThemeHex(option.hex)
                                if (option.hex.equals("#FFFFFF", ignoreCase = true)) {
                                    onSetFollowSystemTheme(false)
                                    onSetDarkMode(false)
                                } else if (option.hex.equals("#111827", ignoreCase = true)) {
                                    onSetFollowSystemTheme(false)
                                    onSetDarkMode(true)
                                }
                            }
                            .testTag("header_solid_color_${option.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isLightSwatch) Color.Black else Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Text(
                                text = option.name.removePrefix("Solid "),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLightSwatch) Color.Black else Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Expandable Theme, Color, Transparency & Night Dimmer Drawer
            AnimatedVisibility(visible = isThemeExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Backgrounds:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        listOf(
                            "100% Clear" to 1.0f,
                            "85% Glass" to 0.85f,
                            "50% Half" to 0.50f,
                            "0% Solid" to 0.0f
                        ).forEach { (label, value) ->
                            val isSelected = abs(backgroundTransparency - value) < 0.08f
                            Surface(
                                color = if (isSelected) CyberEmerald.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    width = if (isSelected) 1.6.dp else 1.dp,
                                    color = if (isSelected) CyberEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .clickable { onTransparencyChange(value) }
                                    .testTag("header_bg_transparency_${(value * 100).toInt()}")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) CyberEmerald else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = isDarkMode && !followSystemTheme,
                            onClick = {
                                onSetFollowSystemTheme(false)
                                onSetDarkMode(true)
                            },
                            label = { Text("🌙 Dark Mode", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_dark_chip")
                        )
                        FilterChip(
                            selected = !isDarkMode && !followSystemTheme,
                            onClick = {
                                onSetFollowSystemTheme(false)
                                onSetDarkMode(false)
                                onSetLateNightEyeComfort(false)
                            },
                            label = { Text("☀️ Light Mode", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_light_chip")
                        )
                        FilterChip(
                            selected = followSystemTheme,
                            onClick = {
                                val nextFollow = !followSystemTheme
                                onSetFollowSystemTheme(nextFollow)
                                if (nextFollow) {
                                    onSetDarkMode(systemDark)
                                }
                            },
                            label = { Text("📱 Follow OS", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_system_chip")
                        )
                        FilterChip(
                            selected = useDynamicColor,
                            onClick = { onSetUseDynamicColor(!useDynamicColor) },
                            label = { Text("🎨 M3 Dynamic Color", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("header_mode_dynamic_color_chip")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Warm Dimmer (${(nightDimmerAlpha * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = nightDimmerAlpha,
                            onValueChange = {
                                onSetNightDimmerAlpha(it)
                                if (it > 0.05f && !isLateNightEyeComfort) {
                                    onSetLateNightEyeComfort(true)
                                }
                            },
                            valueRange = 0f..0.55f,
                            colors = SliderDefaults.colors(
                                thumbColor = SolarAmber,
                                activeTrackColor = SolarAmber
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("header_night_dimmer_slider")
                        )
                    }
                }
            }

            // Expandable Search Results & Scope Drawer
            AnimatedVisibility(
                visible = isSearchExpanded || query.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

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
                            Text("Google Web", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
                            Text("DuckDuckGo", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    searchStatusMessage?.let { msg ->
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
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
                                            isSearchExpanded = false
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
