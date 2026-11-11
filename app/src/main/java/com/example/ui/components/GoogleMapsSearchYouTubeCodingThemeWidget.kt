package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import java.util.Locale

enum class MultiWidgetMode(val label: String, val badge: String) {
    ALL_WIDGETS("⚡ All 4 Widgets", "MAPS • SEARCH • YOUTUBE • CODE THEME"),
    GOOGLE_MAPS("🗺️ Google Maps", "LIVE MAPS & DIRECTIONS"),
    GOOGLE_SEARCH("🔍 Google Search", "WEB & SCHOLAR SEARCH"),
    YOUTUBE_STUDIO("▶️ YouTube", "VIDEO & CHANNEL LAUNCHER"),
    CODING_THEME("💻 Coding Theme", "LIVE CODE THEME CREATOR")
}

private data class YouTubeQuickVideoPreset(
    val title: String,
    val channel: String,
    val videoId: String,
    val category: String
)

private val YOUTUBE_QUICK_PRESETS = listOf(
    YouTubeQuickVideoPreset(
        title = "Jetpack Compose & Kotlin 3D UI Coding",
        channel = "Android Developers",
        videoId = "cDabx3SjuOY",
        category = "Coding & Tech"
    ),
    YouTubeQuickVideoPreset(
        title = "Pink Floyd - Comfortably Numb (Pulse Live)",
        channel = "Pink Floyd Official",
        videoId = "_FrOQC-zEog",
        category = "6D Profile Anthem"
    ),
    YouTubeQuickVideoPreset(
        title = "50/30/20 Budget & Financial Literacy Guide",
        channel = "Financial Education Hub",
        videoId = "HQzoZfc3GwQ",
        category = "Financial Literacy"
    ),
    YouTubeQuickVideoPreset(
        title = "6D Holographic Shaders & Creative Coding",
        channel = "The Coding Train",
        videoId = "f4s1h2YETNY",
        category = "Coding Theme Art"
    )
)

/**
 * Interactive 4-in-1 Widget Hub connecting:
 * 1. Google Maps (Destination search, quick pins, directions & live radar route preview)
 * 2. Google Search (Instant Google Web, Scholar & News search bar with smart topic chips)
 * 3. YouTube (Video search, curated coding/music/finance videos & 1-tap YouTube launch)
 * 4. Coding Theme Creation (Live Kotlin/CSS/JSON code theme editor that compiles and applies
 *    custom-coded colors, 3D visual shaders, and glass transparency across the platform).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoogleMapsSearchYouTubeCodingThemeWidget(
    currentLocation: String,
    currentThemeHex: String,
    currentVisualStyleId: String,
    backgroundTransparency: Float,
    isDarkMode: Boolean,
    onApplyCodedThemeColor: (String) -> Unit,
    onApplyCodedVisualStyle: (String) -> Unit,
    onApplyCodedTransparency: (Float) -> Unit,
    onSetDarkMode: (Boolean) -> Unit = {},
    onShareToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeMode by remember { mutableStateOf(MultiWidgetMode.ALL_WIDGETS) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    // 1. Google Maps state
    var mapsQuery by remember(currentLocation) {
        mutableStateOf(currentLocation.ifBlank { "820 N Summit Blvd Apt 205" })
    }

    // 2. Google Search state
    var googleSearchQuery by remember {
        mutableStateOf("Kotlin Jetpack Compose 3D theme design & STEM scholarships")
    }

    // 3. YouTube state
    var youtubeQuery by remember {
        mutableStateOf("Creative coding 3D shaders & music")
    }
    var selectedYouTubePreset by remember { mutableStateOf(YOUTUBE_QUICK_PRESETS.first()) }

    // 4. Coding Theme Creation state
    var codedThemeName by remember { mutableStateOf("CyberEquinox_6D") }
    var codedHexInput by remember(currentThemeHex) { mutableStateOf(currentThemeHex) }
    var codedShaderStyle by remember(currentVisualStyleId) { mutableStateOf(currentVisualStyleId) }
    var codedCodeLanguage by remember { mutableStateOf("Kotlin Compose") }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("maps_search_youtube_coding_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(HoloCyan, NeonPink, CyberEmerald)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Multi-Tool Widget Hub",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Maps • Google Search • YouTube • Coding Theme Widget",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = activeMode.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Mode Selector Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MultiWidgetMode.entries.forEach { mode ->
                    FilterChip(
                        selected = activeMode == mode,
                        onClick = { activeMode = mode },
                        label = {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (activeMode == mode) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("widget_tab_${mode.name.lowercase(Locale.US)}")
                    )
                }
            }

            // =========================================================================
            // WIDGET 1: GOOGLE MAPS WIDGET
            // =========================================================================
            if (activeMode == MultiWidgetMode.ALL_WIDGETS || activeMode == MultiWidgetMode.GOOGLE_MAPS) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_maps_widget_section")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Google Maps",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "🗺️ Google Maps Live Navigator Widget",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "GPS Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Interactive Radar Map Preview Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF081420))
                                .clickable {
                                    openExternalUri(
                                        context,
                                        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(mapsQuery)}"
                                    )
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val step = 24.dp.toPx()
                                var x = 0f
                                while (x < size.width) {
                                    drawLine(
                                        color = CyberEmerald.copy(alpha = 0.14f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1f
                                    )
                                    x += step
                                }
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(
                                        color = CyberEmerald.copy(alpha = 0.14f),
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = 1f
                                    )
                                    y += step
                                }
                                // Route line
                                val startPt = Offset(size.width * 0.16f, size.height * 0.70f)
                                val midPt = Offset(size.width * 0.50f, size.height * 0.32f)
                                val endPt = Offset(size.width * 0.84f, size.height * 0.45f)
                                drawLine(
                                    color = HoloCyan,
                                    start = startPt,
                                    end = midPt,
                                    strokeWidth = 3.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                )
                                drawLine(
                                    color = CyberEmerald,
                                    start = midPt,
                                    end = endPt,
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(color = HoloCyan, radius = 6.dp.toPx(), center = startPt)
                                drawCircle(color = NeonPink, radius = 7.dp.toPx(), center = endPt)
                                drawCircle(
                                    color = NeonPink.copy(alpha = 0.35f),
                                    radius = 14.dp.toPx(),
                                    center = endPt,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Surface(
                                    color = Color(0xCC06101E),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "📍 Target: $mapsQuery",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = CyberEmerald,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Tap Map",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Black,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = mapsQuery,
                            onValueChange = { mapsQuery = it },
                            label = { Text("Google Maps Destination, City or Address") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_maps_widget_input")
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "📍 My Location" to currentLocation.ifBlank { "Austin, TX" },
                                "🏢 Creator HQ (820 N Summit)" to "820 N Summit Blvd Apt 205",
                                "📚 Public Library" to "Public Library near $currentLocation",
                                "🏥 VA Medical Clinic" to "VA Medical Center near $currentLocation",
                                "🎓 Colleges & Schools" to "Community College & University near $currentLocation"
                            ).forEach { (chipLabel, targetQuery) ->
                                FilterChip(
                                    selected = mapsQuery == targetQuery,
                                    onClick = { mapsQuery = targetQuery },
                                    label = { Text(chipLabel, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val encoded = Uri.encode(mapsQuery.ifBlank { "Austin, TX" })
                                    openExternalUri(
                                        context,
                                        "https://www.google.com/maps/search/?api=1&query=$encoded"
                                    )
                                    statusBanner = "Opened Google Maps for '$mapsQuery'"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("google_maps_open_button")
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Google Maps", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val encoded = Uri.encode(mapsQuery.ifBlank { "820 N Summit Blvd Apt 205" })
                                    openExternalUri(
                                        context,
                                        "https://www.google.com/maps/dir/?api=1&destination=$encoded"
                                    )
                                    statusBanner = "Started Google Maps directions to '$mapsQuery'"
                                },
                                border = BorderStroke(1.dp, HoloCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("google_maps_directions_button")
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", style = MaterialTheme.typography.labelMedium, color = HoloCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // WIDGET 2: GOOGLE SEARCH WIDGET
            // =========================================================================
            if (activeMode == MultiWidgetMode.ALL_WIDGETS || activeMode == MultiWidgetMode.GOOGLE_SEARCH) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_search_widget_section")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Google Search",
                                tint = HoloCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "🔍 Google Search & Discovery Widget",
                                style = MaterialTheme.typography.titleSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        OutlinedTextField(
                            value = googleSearchQuery,
                            onValueChange = { googleSearchQuery = it },
                            label = { Text("Search Google Web, Scholar, News or Coding Docs") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_search_widget_input")
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "💻 Kotlin & Compose Coding",
                                "🎓 FAFSA & College Grants",
                                "💼 CareerOneStop Apprenticeships",
                                "💰 50/30/20 Financial Literacy",
                                "🛡️ VA Healthcare & GI Bill"
                            ).forEach { topic ->
                                val cleanTopic = topic.substringAfter(" ")
                                FilterChip(
                                    selected = googleSearchQuery == cleanTopic,
                                    onClick = { googleSearchQuery = cleanTopic },
                                    label = { Text(topic, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val encoded = Uri.encode(googleSearchQuery.ifBlank { "WereEqual Community" })
                                    openExternalUri(context, "https://www.google.com/search?q=$encoded")
                                    statusBanner = "Launched Google Search for '$googleSearchQuery'"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("google_search_launch_button")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Google Search", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val encoded = Uri.encode(googleSearchQuery.ifBlank { "STEM Education" })
                                    openExternalUri(context, "https://scholar.google.com/scholar?q=$encoded")
                                    statusBanner = "Opened Google Scholar for '$googleSearchQuery'"
                                },
                                border = BorderStroke(1.dp, SolarAmber),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("google_scholar_launch_button")
                            ) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scholar", style = MaterialTheme.typography.labelMedium, color = SolarAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // WIDGET 3: YOUTUBE WIDGET
            // =========================================================================
            if (activeMode == MultiWidgetMode.ALL_WIDGETS || activeMode == MultiWidgetMode.YOUTUBE_STUDIO) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("youtube_widget_section")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = "YouTube Widget",
                                    tint = RosePulse,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "▶️ YouTube Video & Channel Widget",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = RosePulse,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = selectedYouTubePreset.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Featured YouTube Video Card
                        Surface(
                            color = Color(0xFF120B1D),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openExternalUri(
                                        context,
                                        "https://www.youtube.com/watch?v=${selectedYouTubePreset.videoId}"
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(RosePulse),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play on YouTube",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedYouTubePreset.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${selectedYouTubePreset.channel} • ID: ${selectedYouTubePreset.videoId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HoloCyan
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Watch on YouTube",
                                    tint = RosePulse,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            YOUTUBE_QUICK_PRESETS.forEach { preset ->
                                FilterChip(
                                    selected = selectedYouTubePreset.videoId == preset.videoId,
                                    onClick = {
                                        selectedYouTubePreset = preset
                                        youtubeQuery = preset.title
                                    },
                                    label = {
                                        Text(
                                            text = preset.title.take(26),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = youtubeQuery,
                            onValueChange = { youtubeQuery = it },
                            label = { Text("Search YouTube Videos, Tutorials, Music or Shorts") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("youtube_widget_search_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val encoded = Uri.encode(youtubeQuery.ifBlank { selectedYouTubePreset.title })
                                    openExternalUri(
                                        context,
                                        "https://www.youtube.com/results?search_query=$encoded"
                                    )
                                    statusBanner = "Opened YouTube search for '$youtubeQuery'"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RosePulse,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("youtube_widget_search_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search YouTube", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    onShareToWall(
                                        "▶️ Shared from YouTube Widget: ${selectedYouTubePreset.title}",
                                        "Watching '${selectedYouTubePreset.title}' by ${selectedYouTubePreset.channel} (https://www.youtube.com/watch?v=${selectedYouTubePreset.videoId}) on WereEqual!"
                                    )
                                    statusBanner = "Pinned '${selectedYouTubePreset.title}' to the Community Wall!"
                                },
                                border = BorderStroke(1.dp, HoloCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("youtube_widget_share_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pin Video", style = MaterialTheme.typography.labelMedium, color = HoloCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // WIDGET 4: CODING THEME CREATION WIDGET
            // =========================================================================
            if (activeMode == MultiWidgetMode.ALL_WIDGETS || activeMode == MultiWidgetMode.CODING_THEME) {
                val parsedPreviewColor = remember(codedHexInput) {
                    parseThemeHex(codedHexInput)
                }
                val transparencyPercent = (backgroundTransparency * 100).toInt()

                val generatedCodeSnippet = remember(
                    codedCodeLanguage,
                    codedThemeName,
                    codedHexInput,
                    codedShaderStyle,
                    transparencyPercent,
                    isDarkMode
                ) {
                    when (codedCodeLanguage) {
                        "CSS / WebGL" -> """
                            /* WereEqual 6D Coded Theme: $codedThemeName */
                            :root {
                              --theme-primary: $codedHexInput;
                              --shader-engine: "$codedShaderStyle";
                              --glass-transparency: $transparencyPercent%;
                              --color-mode: "${if (isDarkMode) "dark" else "light"}";
                            }
                        """.trimIndent()
                        "JSON Schema" -> """
                            {
                              "themeName": "$codedThemeName",
                              "primaryColorHex": "$codedHexInput",
                              "shader3DEngine": "$codedShaderStyle",
                              "glassTransparency": ${(backgroundTransparency * 100).toInt()}
                            }
                        """.trimIndent()
                        else -> """
                            // WereEqual Live Compose Theme Compiler
                            WonderingWorldTheme(
                                themeName = "$codedThemeName",
                                accentHex = "$codedHexInput",
                                visualStyle = "$codedShaderStyle",
                                backgroundTransparency = ${String.format(Locale.US, "%.2ff", backgroundTransparency)},
                                isDarkMode = $isDarkMode
                            )
                        """.trimIndent()
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("coding_theme_creation_widget_section")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Coding Theme Creation",
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "💻 Coding Theme Creation Studio Widget",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = NeonPink,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(parsedPreviewColor)
                                )
                                Text(
                                    text = codedHexInput.uppercase(Locale.US),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = parsedPreviewColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Language Mode Selector (Kotlin Compose | CSS / WebGL | JSON Schema)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Kotlin Compose", "CSS / WebGL", "JSON Schema").forEach { lang ->
                                FilterChip(
                                    selected = codedCodeLanguage == lang,
                                    onClick = { codedCodeLanguage = lang },
                                    label = { Text(lang, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // Live Code Editor & Syntax Preview Box
                        Surface(
                            color = Color(0xFF070B16),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, parsedPreviewColor.copy(alpha = 0.75f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("coding_theme_code_preview")
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
                                        Icon(
                                            imageVector = Icons.Default.Terminal,
                                            contentDescription = null,
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "LIVE THEME COMPILER • $codedCodeLanguage",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Text(
                                        text = "READY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = HoloCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = generatedCodeSnippet,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = HoloCyan
                                )
                            }
                        }

                        // Theme Name & Hex Code Inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = codedThemeName,
                                onValueChange = { codedThemeName = it.take(32) },
                                label = { Text("Theme Class Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("coding_theme_name_input")
                            )

                            OutlinedTextField(
                                value = codedHexInput,
                                onValueChange = { raw ->
                                    val cleaned = raw.trim().uppercase(Locale.US)
                                    codedHexInput = if (cleaned.startsWith("#")) cleaned.take(7) else "#${cleaned.take(6)}"
                                },
                                label = { Text("Hex (#RRGGBB)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("coding_theme_hex_input")
                            )
                        }

                        // Quick Coded Color Tokens
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "#00F5D4" to "Cyan Neon",
                                "#6366F1" to "Indigo Pulse",
                                "#FF2A85" to "Cyber Pink",
                                "#10B981" to "Matrix Emerald",
                                "#F59E0B" to "Solar Gold",
                                "#8B5CF6" to "Quantum Violet"
                            ).forEach { (hex, label) ->
                                FilterChip(
                                    selected = codedHexInput.equals(hex, ignoreCase = true),
                                    onClick = {
                                        codedHexInput = hex
                                        onApplyCodedThemeColor(hex)
                                    },
                                    label = { Text("$label ($hex)", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // 3D Shader Style Tokens
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "HYPERCUBE_6D" to "🧊 Hypercube 6D",
                                "CYBER_SYNTHWAVE" to "🌆 Synthwave Grid",
                                "GALAXY_VORTEX" to "🌌 Galaxy Vortex",
                                "CRYSTAL_PRISM" to "💎 Crystal Prism",
                                "AURORA_WAVE" to "🌈 Aurora Wave",
                                "MATRIX_CYBERNETIC" to "💻 Matrix Code"
                            ).forEach { (styleId, label) ->
                                FilterChip(
                                    selected = codedShaderStyle == styleId,
                                    onClick = {
                                        codedShaderStyle = styleId
                                        onApplyCodedVisualStyle(styleId)
                                    },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // Coded Glass Transparency Slider
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Glass Alpha Parameter: $transparencyPercent% Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isDarkMode) "Dark Shader" else "Light Shader",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    modifier = Modifier.clickable { onSetDarkMode(!isDarkMode) }
                                )
                            }
                            Slider(
                                value = backgroundTransparency,
                                onValueChange = onApplyCodedTransparency,
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonPink,
                                    activeTrackColor = HoloCyan
                                ),
                                modifier = Modifier.testTag("coding_theme_transparency_slider")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val validHex = if (codedHexInput.length == 7 && codedHexInput.startsWith("#")) {
                                        codedHexInput
                                    } else {
                                        "#00F5D4"
                                    }
                                    onApplyCodedThemeColor(validHex)
                                    onApplyCodedVisualStyle(codedShaderStyle)
                                    statusBanner = "Compiled & applied theme '$codedThemeName' ($validHex • $codedShaderStyle) across the platform!"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonPink,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("compile_apply_coding_theme_button")
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compile & Apply Theme", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    onShareToWall(
                                        "💻 Coded Custom 6D Theme: $codedThemeName",
                                        "Created '$codedThemeName' in the Coding Theme Studio (Hex: $codedHexInput • Shader: $codedShaderStyle • Transparency: $transparencyPercent%)"
                                    )
                                    statusBanner = "Shared coded theme '$codedThemeName' to the Community Wall!"
                                },
                                border = BorderStroke(1.dp, CyberEmerald),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("share_coding_theme_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share Code", style = MaterialTheme.typography.labelMedium, color = CyberEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (statusBanner != null) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(12.dp),
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
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun openExternalUri(context: Context, uriString: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
