package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.AppNavScreen
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

data class AgeGroupPreset(
    val id: String,
    val label: String,
    val age: Long,
    val role: String,
    val description: String
)

val AGE_GROUP_PRESETS = listOf(
    AgeGroupPreset(
        id = "kids_youth",
        label = "🎓 Student / Youth (13–15)",
        age = 14L,
        role = "creator",
        description = "Student Account: School Resources, Homework AI, ZIP Library & 6D Arcade (18+ Locked)"
    ),
    AgeGroupPreset(
        id = "teens_highschool",
        label = "🎓 High School & College Student (16–20)",
        age = 17L,
        role = "creator",
        description = "Student Account: Apply for School (Common App), FAFSA Aid & Student Internships"
    ),
    AgeGroupPreset(
        id = "young_adults",
        label = "🌟 Young Adults & Careers (18–24)",
        age = 21L,
        role = "adult",
        description = "College/Career Job Portals, Digital Wallet, Marketplace & 18+ Lounge"
    ),
    AgeGroupPreset(
        id = "adults_business",
        label = "💼 Business Account & Adults (25–54)",
        age = 32L,
        role = "business",
        description = "Business Account: Marketplace Ads, Digital Wallet Reserve & SBA Mentorship"
    ),
    AgeGroupPreset(
        id = "veterans_leaders",
        label = "🛡️ Veterans & Leaders (28+)",
        age = 28L,
        role = "veteran",
        description = "Veteran Account: VA Clinic Locator, GI Bill Aid, Housing Lifeline & Peer Support"
    )
)

data class PhoneSystemSettingOption(
    val id: String,
    val label: String,
    val action: String
)

val PHONE_SYSTEM_SETTINGS_OPTIONS = listOf(
    PhoneSystemSettingOption("main_settings", "Phone System Settings", Settings.ACTION_SETTINGS),
    PhoneSystemSettingOption("location_gps", "GPS & Location Settings", Settings.ACTION_LOCATION_SOURCE_SETTINGS),
    PhoneSystemSettingOption("wifi_network", "Wi-Fi & Internet Settings", Settings.ACTION_WIFI_SETTINGS),
    PhoneSystemSettingOption("display", "Display & Brightness", Settings.ACTION_DISPLAY_SETTINGS),
    PhoneSystemSettingOption("sound", "Sound & Vibration", Settings.ACTION_SOUND_SETTINGS),
    PhoneSystemSettingOption("security", "Security & Biometric Login", Settings.ACTION_SECURITY_SETTINGS),
    PhoneSystemSettingOption("accessibility", "Accessibility Settings", Settings.ACTION_ACCESSIBILITY_SETTINGS),
    PhoneSystemSettingOption("app_details", "App Permissions & Info", Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
)

fun launchPhoneSystemSetting(context: Context, action: String) {
    try {
        val intent = if (action == Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
            Intent(action).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
        }
    }
}

val PAGE_COLOR_SWATCHES = EXPANDED_THEME_COLOR_OPTIONS.map { it.hex to it.name }

private data class PullDownPageItem(
    val tab: DashboardTab,
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val tag: String
)

private val ALL_PULL_DOWN_PAGES = listOf(
    PullDownPageItem(
        tab = DashboardTab.HUB_3D,
        stepNumber = 1,
        title = "1. Clean Home & Feed",
        subtitle = "Maps, Search, YouTube, Coding Theme & Social Wall",
        icon = Icons.Default.Public,
        accent = HoloCyan,
        tag = "pull_nav_dashboard_feed"
    ),
    PullDownPageItem(
        tab = DashboardTab.THEME_PROFILE,
        stepNumber = 2,
        title = "2. Profile, Theme & Adult Dating",
        subtitle = "Custom Themes, Coding Studio & 18+ Dating Hub",
        icon = Icons.Default.Palette,
        accent = NeonPink,
        tag = "pull_nav_theme_profile"
    ),
    PullDownPageItem(
        tab = DashboardTab.FINANCE_GOALS,
        stepNumber = 3,
        title = "3. Financial Literacy & Wallet",
        subtitle = "Monthly Savings/Expenses Summary & Goals",
        icon = Icons.Default.Savings,
        accent = CyberEmerald,
        tag = "pull_nav_finance_goals"
    ),
    PullDownPageItem(
        tab = DashboardTab.RESOURCES,
        stepNumber = 4,
        title = "4. School, Jobs & Aid",
        subtitle = "Apply for School, Careers, 211 & VA Support",
        icon = Icons.Default.School,
        accent = CyberEmerald,
        tag = "pull_nav_resource_hub"
    ),
    PullDownPageItem(
        tab = DashboardTab.STUDIO_ARCADE,
        stepNumber = 5,
        title = "5. YouTube, Reels & Arcade",
        subtitle = "Embedded YouTube, Short Reels & Games",
        icon = Icons.Default.Brush,
        accent = HoloCyan,
        tag = "pull_nav_studio_arcade"
    ),
    PullDownPageItem(
        tab = DashboardTab.MESSAGING,
        stepNumber = 6,
        title = "6. Messaging & Friends",
        subtitle = "Direct Messages & Match Icebreakers",
        icon = Icons.Default.Settings,
        accent = CyberEmerald,
        tag = "pull_nav_messaging"
    ),
    PullDownPageItem(
        tab = DashboardTab.FRIENDS,
        stepNumber = 7,
        title = "7. Friends & Discovery",
        subtitle = "Find Friends & Manage Requests",
        icon = Icons.Default.PersonAdd,
        accent = NeonPink,
        tag = "pull_nav_friends"
    ),
    PullDownPageItem(
        tab = DashboardTab.VIDEO_CHAT,
        stepNumber = 8,
        title = "8. Video Chat & Date Calls",
        subtitle = "Live Video Rooms & Group Calling",
        icon = Icons.Default.Videocam,
        accent = NeonPink,
        tag = "pull_nav_video_chat"
    ),
    PullDownPageItem(
        tab = DashboardTab.DELIVERY_DRIVING,
        stepNumber = 8,
        title = "8. Rideshare & Delivery",
        subtitle = "Uber, Lyft, DoorDash & Instacart",
        icon = Icons.Default.DirectionsCar,
        accent = Color(0xFFFF8200),
        tag = "pull_nav_delivery_driving"
    ),
    PullDownPageItem(
        tab = DashboardTab.NEWS_SPORTS,
        stepNumber = 9,
        title = "9. News & Sports",
        subtitle = "Live Scoreboards & Daily Headlines",
        icon = Icons.Default.Newspaper,
        accent = SolarAmber,
        tag = "pull_nav_news_sports"
    ),
    PullDownPageItem(
        tab = DashboardTab.AI_TOOLS,
        stepNumber = 10,
        title = "10. AI Study Tutor",
        subtitle = "Homework Coach & Study Flashcards",
        icon = Icons.Default.AutoAwesome,
        accent = SolarAmber,
        tag = "pull_nav_ai_tools"
    ),
    PullDownPageItem(
        tab = DashboardTab.STABILITY_LOUNGE,
        stepNumber = 11,
        title = "11. Market, Growth & 18+ Lounge",
        subtitle = "Marketplace, Charts & Adult Connections",
        icon = Icons.Default.AccountBalanceWallet,
        accent = SolarAmber,
        tag = "pull_nav_finance_hub"
    ),
    PullDownPageItem(
        tab = DashboardTab.CODE_OF_CONDUCT,
        stepNumber = 12,
        title = "12. Code of Conduct",
        subtitle = "Equality Rules & Community Ideas",
        icon = Icons.Default.Gavel,
        accent = RosePulse,
        tag = "pull_nav_code_of_conduct"
    ),
    PullDownPageItem(
        tab = DashboardTab.MAINTENANCE_SETTINGS,
        stepNumber = 13,
        title = "13. Maintenance & Settings",
        subtitle = "Color Wheel, Transparency, Contact Us & Download APK",
        icon = Icons.Default.Settings,
        accent = HoloCyan,
        tag = "pull_nav_maintenance_settings"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PullDownNavigationBar(
    effectiveScreen: AppNavScreen,
    activeTab: DashboardTab,
    currentAge: Long,
    currentRole: String,
    currentThemeHex: String,
    currentVisualStyleId: String = "HYPERCUBE_6D",
    isDarkMode: Boolean,
    backgroundTransparency: Float = 0.45f,
    onTransparencyChange: (Float) -> Unit = {},
    currentLocationName: String = "Austin, TX",
    onLocationUpdated: (String) -> Unit = {},
    learnedSummary: String,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToDashboardTab: (DashboardTab) -> Unit,
    onSelectAgeGroup: (AgeGroupPreset) -> Unit,
    onSelectPageColor: (String) -> Unit,
    onSelectVisualStyle: (String) -> Unit = {},
    onToggleDarkMode: () -> Unit,
    onRebootPlatform: () -> Unit = {},
    isNetworkLoading: Boolean = false,
    onTriggerNetworkSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isNavPulledDown by remember { mutableStateOf(false) }
    var isFhdMode by remember { mutableStateOf(true) }

    val orderedTabs = remember { DashboardTab.entries }
    val currentTabIndex = orderedTabs.indexOf(activeTab).coerceAtLeast(0)

    val currentAgeGroup = remember(currentAge, currentRole) {
        when {
            currentAge < 16L -> AGE_GROUP_PRESETS[0]
            currentAge < 18L -> AGE_GROUP_PRESETS[1]
            currentRole == "veteran" -> AGE_GROUP_PRESETS[4]
            currentRole == "business" -> AGE_GROUP_PRESETS[3]
            else -> AGE_GROUP_PRESETS[2]
        }
    }

    val activeColorName = remember(currentThemeHex) {
        EXPANDED_THEME_COLOR_OPTIONS.firstOrNull {
            it.hex.equals(currentThemeHex, ignoreCase = true)
        }?.name ?: currentThemeHex
    }

    val activeSpaceTitle = remember(effectiveScreen, activeTab) {
        when (effectiveScreen) {
            AppNavScreen.LOGIN -> "Login Portal"
            AppNavScreen.SIGN_UP -> "Create Account"
            AppNavScreen.MAIN_DASHBOARD -> when (activeTab) {
                DashboardTab.HUB_3D -> "Home, Wallet & Market"
                DashboardTab.DELIVERY_DRIVING -> "Rideshare, Delivery & Driving"
                DashboardTab.MESSAGING -> "Messaging & Chat"
                DashboardTab.FRIENDS -> "Friends & Discovery"
                DashboardTab.VIDEO_CHAT -> "Video Chat & Live Calling"
                DashboardTab.RESOURCES -> "School, Jobs & Aid"
                DashboardTab.THEME_PROFILE -> "Stages Of Life Theme Selection"
                DashboardTab.NEWS_SPORTS -> "News & Sports"
                DashboardTab.AI_TOOLS -> "AI Study Tutor"
                DashboardTab.STUDIO_ARCADE -> "YouTube & Arcade"
                DashboardTab.FINANCE_GOALS -> "Digital Wallet & Goals"
                DashboardTab.STABILITY_LOUNGE -> "Market & Growth"
                DashboardTab.CODE_OF_CONDUCT -> "Code of Conduct"
                DashboardTab.MAINTENANCE_SETTINGS -> "Maintenance & Settings"
            }
            AppNavScreen.FRIENDS_DISCOVERY -> "Find Friends"
        }
    }

    // STRICT SINGLE PULL-DOWN BAR AT TOP OF PLATFORM (Transparent Glass so 3D Theme Shows Clearly)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    HoloCyan,
                    NeonPink
                )
            )
        ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 8f) {
                        isNavPulledDown = true
                    } else if (dragAmount < -8f) {
                        isNavPulledDown = false
                    }
                }
            }
            .testTag("pull_down_navigation_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // SINGLE COMPACT TOP BAR ROW (Swipe Down or Tap to Open Full Menu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Single Pull-Down Trigger Pill (Shows 3D WEREALLEQUALS Icon + Current Page + Active Color Dot)
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable { isNavPulledDown = !isNavPulledDown }
                        .testTag("pull_down_nav_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WereAllEquals3DMultiDimensionalIcon(
                            iconSize = 26.dp,
                            showRibbonText = false,
                            onClick = onNavigateToLogin
                        )
                        Icon(
                            imageVector = if (isNavPulledDown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Pull Down Navigation Bar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isNavPulledDown) {
                                "▲ STAGES OF LIFE • Close Pull-Down Bar"
                            } else {
                                "▼ STAGES OF LIFE • $activeSpaceTitle • $activeColorName"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 2. Compact Controls on the Right of the Single Bar: Network Sync, Prev/Next Page, FHD/HD, Dark/Light
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Global Network Sync Trigger Chip (Displays mini CircularProgressIndicator or triggers cloud sync)
                    GlobalNetworkSyncHeaderChip(
                        isLoading = isNetworkLoading,
                        onTriggerSync = onTriggerNetworkSync
                    )

                    // Prev Page Button
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable {
                                val prevIndex = (currentTabIndex - 1 + orderedTabs.size) % orderedTabs.size
                                onNavigateToDashboardTab(orderedTabs[prevIndex])
                            }
                            .testTag("pull_bar_prev_page_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Next Page Button
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable {
                                val nextIndex = (currentTabIndex + 1) % orderedTabs.size
                                onNavigateToDashboardTab(orderedTabs[nextIndex])
                            }
                            .testTag("pull_bar_next_page_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Page",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // FHD 1080p / HD 720p Toggle Pill
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable { isFhdMode = !isFhdMode }
                            .testTag("fhd_hd_quality_toggle_button")
                    ) {
                        Text(
                            text = if (isFhdMode) "FHD" else "HD",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }

                    // System-Wide Material 3 Dark / Light Mode Toggle Pill (Persists to Firestore)
                    Surface(
                        color = if (isDarkMode) HoloCyan.copy(alpha = 0.18f) else SolarAmber.copy(alpha = 0.22f),
                        border = BorderStroke(
                            1.2.dp,
                            if (isDarkMode) HoloCyan.copy(alpha = 0.75f) else SolarAmber.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable { onToggleDarkMode() }
                            .testTag("theme_mode_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = if (isDarkMode) "Dark Theme Active" else "Light Theme Active",
                                tint = if (isDarkMode) HoloCyan else SolarAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isDarkMode) "DARK" else "LIGHT",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkMode) HoloCyan else SolarAmber,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // EVERY-PAGE LIVE TIME, DATE, WEATHER & LOCATION PERMISSION BAR
            EveryPageTimeDateWeatherLocationBar(
                locationName = currentLocationName,
                onLocationUpdated = onLocationUpdated
            )

            // SINGLE UNIFIED PULL-DOWN DRAWER (Opens when user pulls down or taps the bar)
            AnimatedVisibility(
                visible = isNavPulledDown,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(MaterialTheme.colorScheme.primary, HoloCyan, NeonPink, CyberEmerald)
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 460.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                        .testTag("pull_down_nav_menu")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // System-Wide Material 3 Dark Theme Toggle Card (Synced to Firestore)
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.2.dp,
                                if (isDarkMode) HoloCyan.copy(alpha = 0.65f) else SolarAmber.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("system_wide_dark_theme_toggle_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isDarkMode) HoloCyan else SolarAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isDarkMode) {
                                                "System-Wide Material 3 Dark Theme"
                                            } else {
                                                "System-Wide Material 3 Light Theme"
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Updates M3 ColorScheme & persists to Firestore",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { onToggleDarkMode() },
                                    modifier = Modifier.testTag("system_wide_dark_theme_switch")
                                )
                            }
                        }

                        // Global Background Transparency Bar + Open Maintenance & Color Wheel Page
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, HoloCyan.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pulldown_background_transparency_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔮 Background Transparency (${(backgroundTransparency * 100).toInt()}%)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = HoloCyan,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Surface(
                                        color = SolarAmber.copy(alpha = 0.22f),
                                        border = BorderStroke(1.dp, SolarAmber),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier
                                            .clickable {
                                                onNavigateToDashboardTab(DashboardTab.MAINTENANCE_SETTINGS)
                                                isNavPulledDown = false
                                            }
                                            .testTag("pulldown_open_maintenance_button")
                                    ) {
                                        Text(
                                            text = "🎨 Color Wheel & Maintenance",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SolarAmber,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Slider(
                                    value = backgroundTransparency,
                                    onValueChange = onTransparencyChange,
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = HoloCyan,
                                        activeTrackColor = HoloCyan
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Top Account & Auth Row (Home Hub | Login Portal | Create Account)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PullDownDestinationChip(
                                icon = Icons.Default.Public,
                                label = "Home Hub",
                                accent = CyberEmerald,
                                selected = effectiveScreen == AppNavScreen.MAIN_DASHBOARD,
                                tag = "nav_state_dashboard",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onNavigateToDashboardTab(activeTab)
                                    isNavPulledDown = false
                                }
                            )
                            PullDownDestinationChip(
                                icon = Icons.AutoMirrored.Filled.Login,
                                label = "Login",
                                accent = HoloCyan,
                                selected = effectiveScreen == AppNavScreen.LOGIN,
                                tag = "pull_nav_login",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onNavigateToLogin()
                                    isNavPulledDown = false
                                }
                            )
                            PullDownDestinationChip(
                                icon = Icons.Default.PersonAdd,
                                label = "Create Account",
                                accent = NeonPink,
                                selected = effectiveScreen == AppNavScreen.SIGN_UP,
                                tag = "pull_nav_create_account",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onNavigateToSignUp()
                                    isNavPulledDown = false
                                }
                            )
                        }

                        // 2-Column Grid of All 9 Platform Pages (Each Page Change Auto-Shifts Theme & Color!)
                        Text(
                            text = "🧭 Stages Of Life Navigation (Theme & Color Shift Every Page Change):",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )

                        ALL_PULL_DOWN_PAGES.chunked(2).forEach { rowPages ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPages.forEach { page ->
                                    val isSelected =
                                        effectiveScreen == AppNavScreen.MAIN_DASHBOARD && activeTab == page.tab
                                    Surface(
                                        color = if (isSelected) {
                                            page.accent.copy(alpha = 0.24f)
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        },
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) page.accent else page.accent.copy(alpha = 0.45f)
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onNavigateToDashboardTab(page.tab)
                                                isNavPulledDown = false
                                            }
                                            .testTag(page.tag)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = page.icon,
                                                contentDescription = null,
                                                tint = page.accent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = page.title,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = page.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowPages.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        // 32-Color Selection Strip + WereEqual Theme Selection Inside the Single Pull-Down Drawer
                        Text(
                            text = "🎨 Stages Of Life Theme Selection & Solid Colors ($activeColorName):",
                            style = MaterialTheme.typography.labelLarge,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .testTag("every_page_color_picker_bar"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EXPANDED_THEME_COLOR_OPTIONS.forEach { option ->
                                val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                                Surface(
                                    color = if (isSelected) option.color.copy(alpha = 0.28f) else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) option.color else option.color.copy(alpha = 0.55f)
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelectPageColor(option.hex) }
                                        .testTag("page_color_swatch_${option.name.substringBefore(" ").lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(option.color)
                                        )
                                        Text(
                                            text = option.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            THREE_D_THEME_VISUAL_STYLES.forEach { styleOption ->
                                val isStyleSelected = styleOption.id == currentVisualStyleId
                                FilterChip(
                                    selected = isStyleSelected,
                                    onClick = { onSelectVisualStyle(styleOption.id) },
                                    label = {
                                        Text(
                                            text = "◈ ${styleOption.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isStyleSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    },
                                    modifier = Modifier.testTag("pull_nav_3d_style_${styleOption.id.lowercase()}")
                                )
                            }
                        }

                        // Student / Business / Content Account & Age Group Presets Inside Single Pull-Down Drawer
                        Text(
                            text = "🎓 Account Type & Age Group (Student, Business, Content, Veteran):",
                            style = MaterialTheme.typography.labelLarge,
                            color = CyberEmerald,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AGE_GROUP_PRESETS.forEach { preset ->
                                val isSelected = currentAgeGroup.id == preset.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onSelectAgeGroup(preset)
                                        isNavPulledDown = false
                                    },
                                    label = {
                                        Text(
                                            text = preset.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    },
                                    modifier = Modifier.testTag("age_preset_${preset.id}")
                                )
                            }
                        }

                        // Bottom Row: Phone System Settings & Reboot Platform
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = SolarAmber.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .clickable {
                                        launchPhoneSystemSetting(context, Settings.ACTION_SETTINGS)
                                        isNavPulledDown = false
                                    }
                                    .testTag("system_settings_pulldown_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Phone Settings",
                                        tint = SolarAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Phone OS Settings",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                color = NeonPink.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, NeonPink),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .clickable {
                                        onRebootPlatform()
                                        isNavPulledDown = false
                                    }
                                    .testTag("reboot_platform_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Shuffle Theme & Reboot",
                                        tint = NeonPink,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "🎲 Shift Theme & Refresh",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPink,
                                        fontWeight = FontWeight.Bold
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

@Composable
private fun PullDownDestinationChip(
    icon: ImageVector,
    label: String,
    accent: Color,
    selected: Boolean,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) accent.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) accent else accent.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
