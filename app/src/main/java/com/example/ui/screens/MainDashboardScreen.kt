package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.ui.components.DailyAffirmationCard
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.example.ui.components.RealNewsAndSportsHubCard
import com.example.ui.components.UserAccountBadge
import com.example.ui.screens.DeliveryAndOrderingPortalScreen
import com.example.ui.screens.WereEqualVideoChatScreen
import com.example.ui.screens.FriendsAndDiscoveryScreen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.BudgetEntry
import com.example.data.model.CareerMilestone
import com.example.data.model.CommunityConnectionCard
import com.example.data.model.Friend
import com.example.data.model.FriendRequest
import com.example.data.model.PersonalizedDailyInsight
import com.example.data.model.ResourceDirectoryItem
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import com.example.data.model.WallPost
import com.example.data.model.buildDefaultStudentCareerMilestones
import com.example.ui.AiFlashcardItem
import com.example.ui.AiImageUiState
import com.example.ui.AiTextUiState
import com.example.ui.DashboardTab
import com.example.ui.SignUpFormState
import com.example.ui.UiState
import com.example.ui.UserIdeaProposal
import androidx.compose.material.icons.filled.Newspaper
import com.example.ui.components.AdultProfileDatingHubCard
import com.example.ui.components.BackgroundSongPreset
import com.example.ui.components.CommunityMarketplaceCard
import com.example.ui.components.CreativePhotoStudioCard
import com.example.ui.components.CustomProfileThemeCreatorSection
import com.example.ui.components.DashboardTopWeatherSearchHeader
import com.example.ui.components.DigitalWalletCard
import com.example.ui.components.EveryLoginSuggestionsCard
import com.example.ui.components.FinancialLiteracyTrackerCard
import com.example.ui.components.GoogleMapsSearchYouTubeCodingThemeWidget
import com.example.ui.components.NewsAndSportsHubScreen
import com.example.ui.components.PersonalGrowthRechartsDashboard
import com.example.ui.components.PersonalizedInsightModuleCard
import com.example.ui.components.ProfileAdaptiveLearningCard
import com.example.ui.components.ProfileBackgroundMusicJukeboxCard
import com.example.ui.components.ProfileSportsSelectorCard
import com.example.ui.components.ProfileYouTubeReelsAndGamesHubCard
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.StudentSchoolAndCareersPortalCard
import com.example.ui.components.THREE_D_AVATAR_PRESETS
import com.example.ui.components.THREE_D_THEME_VISUAL_STYLES
import com.example.ui.components.ThreeDAvatarPreset
import com.example.ui.components.ThreeDThemeStyleSelectorMenuCard
import com.example.ui.components.ThreeDimensionalBackground
import com.example.ui.components.WereAllEquals3DHeroShowcaseBanner
import com.example.ui.components.launchPhoneSystemSetting
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CURATED_3D_COLOR_PALETTES
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.findSecondaryAccentForHex
import com.example.ui.theme.parseThemeHex
import com.example.ui.components.EmbeddedSearchWebView
import com.example.ui.components.ReportContentDialog
import androidx.compose.material3.rememberModalBottomSheetState

private data class FacebookStoryItem(
    val title: String,
    val author: String,
    val emoji: String,
    val accent: Color
)
@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    activeTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit,
    profileState: UiState<UserProfile?>,
    signUpForm: SignUpFormState,
    postsState: UiState<List<WallPost>>,
    budgetState: UiState<List<BudgetEntry>>,
    curatedResources: List<ResourceDirectoryItem>,
    communityConnections: List<CommunityConnectionCard>,
    customProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int? = null,
    selectedAvatarTags: Set<String> = emptySet(),
    avatarEmojiBadge: String,
    learnedSignals: List<String>,
    everyLoginSuggestions: List<String> = emptyList(),
    selectedSportsIds: Set<String> = emptySet(),
    learnedInterests: Set<String> = emptySet(),
    learnedLifestyle: Set<String> = emptySet(),
    learnedSchedule: Set<String> = emptySet(),
    learnedResearch: Set<String> = emptySet(),
    userIdeas: List<UserIdeaProposal>,
    aiStudyState: AiTextUiState,
    aiFlashcardsState: UiState<List<AiFlashcardItem>>,
    aiAdaptivePlanState: AiTextUiState,
    aiIdeaFeedbackState: AiTextUiState,
    aiProfileImageState: AiImageUiState,
    isPlayingMusic: Boolean,
    isDarkMode: Boolean = true,
    onSetDarkMode: (Boolean) -> Unit = {},
    onToggleMusic: () -> Unit,
    onSelectBackgroundSongPreset: (BackgroundSongPreset) -> Unit = {},
    onToggleSport: (String) -> Unit = {},
    onToggleLearnedInterest: (String) -> Unit = {},
    onToggleLearnedLifestyle: (String) -> Unit = {},
    onToggleLearnedSchedule: (String) -> Unit = {},
    onToggleLearnedResearch: (String) -> Unit = {},
    onAddCustomLearningSignal: (String, String) -> Unit = { _, _ -> },
    onRefreshLoginSuggestions: () -> Unit = {},
    onSwitchPersonaQuick: (String, Long) -> Unit,
    onThemeColorSelect: (String) -> Unit,
    currentVisualStyleId: String = "HYPERCUBE_6D",
    onThemeVisualStyleSelect: (String) -> Unit = {},
    onDisplayNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onRoleSelect: (String) -> Unit,
    onMusicChange: (String, String) -> Unit,
    onUpdateProfileBitmap: (Bitmap) -> Unit,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit = {},
    onToggleAvatarTag: (String) -> Unit = {},
    onUpdateAvatarBadge: (String) -> Unit,
    onGenerateAiProfilePhoto: (String) -> Unit,
    onPledgeChange: (Boolean) -> Unit = {},
    onSaveProfileFromForm: () -> Unit,
    onCreatePost: (String, String, String) -> Unit,
    onLikePost: (WallPost) -> Unit,
    onDeletePost: (String) -> Unit,
    onAddBudgetItem: (String, String, Double) -> Unit,
    onDeleteBudgetItem: (String) -> Unit,
    onUpdateSavingsGoal: (Double) -> Unit = {},
    onNewArcadeHighScore: (Long) -> Unit,
    onAskAiStudyTutor: (String, String, String, String) -> Unit,
    onGenerateStudyFlashcards: (String, String) -> Unit,
    onGenerateAdaptiveLearningPlan: () -> Unit,
    onRecordLearnedSignal: (String) -> Unit,
    onSubmitUserIdea: (String, String, String) -> Unit,
    onUpvoteUserIdea: (String) -> Unit,
    onExpandIdeaWithAi: (String, String, String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onSignOut: () -> Unit,
    userInteractionsState: UiState<List<UserInteraction>> = UiState.Success(emptyList()),
    personalizedInsightsState: UiState<List<PersonalizedDailyInsight>> = UiState.Success(emptyList()),
    onGenerateGeminiInsights: () -> Unit = {},
    onLogInteraction: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteInteraction: (String) -> Unit = {},
    onAdvanceThemeAndColor: () -> Unit = {},
    isFinancialVaultUnlocked: Boolean = false,
    onUnlockFinancialVault: () -> Unit = {},
    onLockFinancialVault: () -> Unit = {},
    careerMilestonesState: UiState<List<CareerMilestone>> = UiState.Success(buildDefaultStudentCareerMilestones()),
    onAddCareerMilestone: (String, String, String, String, Long, String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateCareerMilestoneStatus: (CareerMilestone, String) -> Unit = { _, _ -> },
    onDeleteCareerMilestone: (String) -> Unit = {},
    backgroundTransparency: Float = 0.45f,
    onTransparencyChange: (Float) -> Unit = {},
    userAccountEmail: String = "allengrogan33@gmail.com",
    userAccountPhone: String = "509-552-3217",
    onUpdateUserEmail: (String) -> Unit = {},
    onUpdateUserPhone: (String) -> Unit = {},
    isResourceHubUnlocked: Boolean = false,
    onUnlockResourceHub: () -> Unit = {},
    onLockResourceHub: () -> Unit = {},
    onNavigateToFriendsDiscovery: () -> Unit = {},
    friendsState: UiState<List<Friend>> = UiState.Success(emptyList()),
    incomingRequestsState: UiState<List<FriendRequest>> = UiState.Success(emptyList()),
    discoveredUsers: List<UserProfile> = emptyList(),
    onUpdatePhoneNumber: (String) -> Unit = {},
    onSyncContacts: (android.content.Context) -> Unit = {},
    onSearchUsers: (String) -> Unit = {},
    onSendFriendRequest: (String, String) -> Unit = { _, _ -> },
    onAcceptRequest: (FriendRequest) -> Unit = { _ -> },
    onDeclineRequest: (String) -> Unit = { _ -> },
    onReportContent: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onShowReportDialog: (String, String) -> Unit = { _, _ -> },
    onConnectSpotify: (String) -> Unit = {},
    onConnectAppleMusic: (String) -> Unit = {},
    onDisconnectSpotify: () -> Unit = {},
    onDisconnectAppleMusic: () -> Unit = {},
    virtualCurrencyBalance: Long = 250L,
    dailyBonusClaimed: Boolean = false,
    loginStreakDays: Int = 3,
    claimedBadgeIds: Set<String> = setOf("welcome_pioneer"),
    unlockedShopItemIds: Set<String> = emptySet(),
    currencyTransactions: List<com.example.ui.components.VirtualCurrencyTransaction> = emptyList(),
    currencyStatusBanner: String? = null,
    onClaimDailyLoginBonus: () -> Unit = {},
    onRewardHelpfulPostBonus: () -> Unit = {},
    onClaimBadgeQuest: (com.example.ui.components.CurrencyEarnBadgeQuest) -> Unit = {},
    onPurchaseOrApplyShopItem: (com.example.ui.components.CurrencySpendItem) -> Unit = {},
    onTipCommunityUser: (String, Long, String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var universalSearchQuery by remember { mutableStateOf("") }
    var selectedSearchEngine by remember { mutableStateOf("Google") }

    // In-App Search WebView State
    var searchUrlToOpen by remember { mutableStateOf<String?>(null) }
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Music connection state
    var musicServiceToConnect by remember { mutableStateOf<String?>(null) }

    val profile = (profileState as? UiState.Success)?.data ?: UserProfile(
        displayName = signUpForm.displayName.ifBlank { "Jordan Vance" },
        role = signUpForm.role,
        age = signUpForm.ageText.toLongOrNull() ?: 26L,
        location = signUpForm.location.ifBlank { "Austin, TX" },
        bio = signUpForm.bio.ifBlank { "We are all equal." },
        themeColorHex = signUpForm.themeColorHex,
        musicTrackTitle = signUpForm.musicTrackTitle,
        musicPlatform = signUpForm.musicPlatform
    )

    fun launchWebUrl(url: String) {
        searchUrlToOpen = url
    }

    fun executeUniversalSearch(overrideQuery: String? = null) {
        val q = (overrideQuery ?: universalSearchQuery).trim()
        if (q.isEmpty()) return
        onRecordLearnedSignal("Searched $selectedSearchEngine: ${q.take(24)}")
        val encoded = Uri.encode(q)
        val url = when (selectedSearchEngine) {
            "DuckDuckGo" -> "https://duckduckgo.com/?q=$encoded"
            "Bing" -> "https://www.bing.com/search?q=$encoded"
            else -> "https://www.google.com/search?q=$encoded"
        }
        launchWebUrl(url)
    }

    // Consolidated 6-Tab Navigation (Matching Bottom Nav Items)
    val pagerTabs = remember {
        listOf(
            DashboardTab.HUB_3D,           // 1. Dashboard
            DashboardTab.THEME_PROFILE,     // 2. Profile
            DashboardTab.FRIENDS,           // 3. Friends (New)
            DashboardTab.MESSAGING,         // 4. Messages (+ Studio/Arcade)
            DashboardTab.VIDEO_CHAT,        // 5. Video
            DashboardTab.FINANCE_GOALS,     // 6. Wallet (+ Stability/Delivery)
            DashboardTab.MAINTENANCE_SETTINGS // 7. Settings (+ Resources/Explore)
        )
    }

    val pagerState = rememberPagerState(
        initialPage = pagerTabs.indexOf(activeTab).coerceAtLeast(0),
        pageCount = { pagerTabs.size }
    )

    // Sync activeTab -> pager with smooth cubic-bezier horizontal slide animation
    LaunchedEffect(activeTab) {
        val targetPage = pagerTabs.indexOf(activeTab)
        if (targetPage >= 0 && targetPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(
                page = targetPage,
                animationSpec = tween(
                    durationMillis = 360,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    // Sync pager -> activeTab
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = pagerTabs[page]
            if (tab != activeTab) {
                onSelectTab(tab)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == DashboardTab.HUB_3D,
                    onClick = { onSelectTab(DashboardTab.HUB_3D) },
                    icon = { Icon(Icons.Default.Public, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_hub_3d")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.THEME_PROFILE,
                    onClick = { onSelectTab(DashboardTab.THEME_PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "My Profile") },
                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_theme_profile")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.FRIENDS,
                    onClick = { onSelectTab(DashboardTab.FRIENDS) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Friends") },
                    label = { Text("Friends", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_friends")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.MESSAGING || activeTab == DashboardTab.STUDIO_ARCADE,
                    onClick = { onSelectTab(DashboardTab.MESSAGING) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages") },
                    label = { Text("Messages", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_messaging")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.VIDEO_CHAT,
                    onClick = { onSelectTab(DashboardTab.VIDEO_CHAT) },
                    icon = { Icon(Icons.Default.Videocam, contentDescription = "Video Chat") },
                    label = { Text("Video", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_video_chat")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.FINANCE_GOALS || activeTab == DashboardTab.STABILITY_LOUNGE || activeTab == DashboardTab.DELIVERY_DRIVING,
                    onClick = { onSelectTab(DashboardTab.FINANCE_GOALS) },
                    icon = { Icon(Icons.Default.Savings, contentDescription = "Wallet & Goals") },
                    label = { Text("Wallet", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_finance_goals")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.MAINTENANCE_SETTINGS || activeTab == DashboardTab.RESOURCES || activeTab == DashboardTab.AI_TOOLS || activeTab == DashboardTab.CODE_OF_CONDUCT,
                    onClick = { onSelectTab(DashboardTab.MAINTENANCE_SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Maintenance & Settings") },
                    label = { Text("Settings", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_maintenance_settings")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Dashboard Bar (Fixed at top — includes Firestore-Indexed Community Member Lookup)
            DashboardTopWeatherSearchHeader(
                locationName = profile.location,
                onLocationUpdated = onLocationChange,
                universalSearchQuery = universalSearchQuery,
                onSearchQueryChange = { universalSearchQuery = it },
                selectedSearchEngine = selectedSearchEngine,
                onSelectSearchEngine = { selectedSearchEngine = it },
                onExecuteSearch = { executeUniversalSearch(it) },
                onNavigateToEditProfile = onNavigateToEditProfile,
                onOpenPhoneSystemSettings = {
                    onSelectTab(DashboardTab.MAINTENANCE_SETTINGS)
                },
                onSignOut = onSignOut,
                onNavigateToFriendsDiscovery = onNavigateToFriendsDiscovery,
                discoveredUsers = discoveredUsers,
                communityConnections = communityConnections,
                onSearchCommunityMembers = onSearchUsers,
                onSendFriendRequest = onSendFriendRequest,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 1A. Quick-Swipe Horizontal Tab Strip with Animated Slide Indicator (Dashboard, Profile, Community & More)
            DashboardQuickSwipeTabStrip(
                pagerTabs = pagerTabs,
                currentPage = pagerState.currentPage,
                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                activeAccentHex = signUpForm.themeColorHex,
                onSelectTab = onSelectTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("dashboard_horizontal_pager"),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val tab = pagerTabs[pageIndex]
                val rawPageOffset = (
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                ).coerceIn(-1f, 1f)
                val absOffset = rawPageOffset.absoluteValue
                val slideScale = 0.94f + (1f - absOffset) * 0.06f
                val slideAlpha = 0.58f + (1f - absOffset) * 0.42f

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Subtle horizontal slide parallax, scale depth, alpha fade, and 3D Y-axis tilt
                            translationX = -rawPageOffset * size.width * 0.12f
                            scaleX = slideScale
                            scaleY = slideScale
                            alpha = slideAlpha
                            rotationY = -rawPageOffset * 4.5f
                            cameraDistance = 14f * density
                        }
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1B. Show 3D Emblem Showcase & Platform https:\\ / https:// URL Builder on Home Hub
                        if (tab == DashboardTab.HUB_3D) {
                            WereAllEquals3DHeroShowcaseBanner()

                            // Persona-Based Profile System (Veteran, Business, Kid, Teen, Adult, Student)
                            com.example.ui.components.PersonaProfileSystemCard(
                                currentRole = signUpForm.role,
                                currentAgeText = signUpForm.ageText,
                                currentVisualStyleId = currentVisualStyleId,
                                currentThemeHex = signUpForm.themeColorHex,
                                selectedInterests = signUpForm.selectedInterests + learnedInterests,
                                onSelectPersona = { roleKey, defaultAge, defaultHex, defaultStyleId ->
                                    onRoleSelect(roleKey)
                                    onAgeChange(defaultAge)
                                    onThemeColorSelect(defaultHex)
                                    onThemeVisualStyleSelect(defaultStyleId)
                                },
                                onApplyPersonaTheme = { visualStyleId, colorHex, badgeEmoji ->
                                    onThemeVisualStyleSelect(visualStyleId)
                                    onThemeColorSelect(colorHex)
                                    onUpdateAvatarBadge(badgeEmoji)
                                },
                                onPinPersonaInterest = { interest ->
                                    onToggleLearnedInterest(interest)
                                },
                                onSharePersonaActionToWall = { title, content ->
                                    onCreatePost("Update", title, content)
                                }
                            )

                            // Compose 3D Avatar Identity Customizer (Accessories, Perspective Orbit & Multi-Zone Colors)
                            com.example.ui.components.ThreeDAvatarCustomizerCard(
                                userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                currentThemeHex = signUpForm.themeColorHex,
                                currentAvatarEmoji = avatarEmojiBadge,
                                onApplyPrimaryThemeHex = onThemeColorSelect,
                                onApplyAvatarBadgeEmoji = onUpdateAvatarBadge,
                                onApplyCustomizedAvatarBitmap = onUpdateProfileBitmap,
                                onToggleAvatarAccessoryTag = onToggleAvatarTag,
                                onShareAvatarToWall = { headline, body ->
                                    onCreatePost("Studio Art", headline, body)
                                }
                            )

                            // Virtual Currency System (EqualCoins / EQC - Earn, Spend & Tip Users)
                            com.example.ui.components.VirtualCurrencySystemCard(
                                balance = virtualCurrencyBalance,
                                dailyBonusClaimed = dailyBonusClaimed,
                                streakDays = loginStreakDays,
                                claimedBadgeIds = claimedBadgeIds,
                                unlockedItemIds = unlockedShopItemIds,
                                transactions = currencyTransactions,
                                statusBanner = currencyStatusBanner,
                                accentColor = parseThemeHex(signUpForm.themeColorHex),
                                onClaimDailyLoginBonus = onClaimDailyLoginBonus,
                                onRewardHelpfulPostBonus = onRewardHelpfulPostBonus,
                                onClaimBadgeQuest = onClaimBadgeQuest,
                                onPurchaseOrApplyShopItem = onPurchaseOrApplyShopItem,
                                onTipCommunityUser = onTipCommunityUser
                            )

                            // Achievement & Gamification System + Community Leaderboards
                            com.example.ui.components.AchievementGamificationLeaderboardCard(
                                userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                userRole = signUpForm.role,
                                userAvatarEmoji = avatarEmojiBadge,
                                accentColor = parseThemeHex(signUpForm.themeColorHex),
                                onEquipBadgeToProfile = onUpdateAvatarBadge,
                                onAwardEqualCoinsBonus = { onRewardHelpfulPostBonus() },
                                onShareAchievementToWall = { title, body ->
                                    onCreatePost("Milestone", title, body)
                                }
                            )

                            // Fast AI Image Creation & Editing (Nano Banana 2.1) + Live Voice Conversations
                            com.example.ui.components.AiImageAndVoiceConversationStudioCard(
                                currentProfileBitmap = customProfileBitmap,
                                aiProfileImageState = aiProfileImageState,
                                onGenerateNewImageFromPrompt = onGenerateAiProfilePhoto,
                                onApplyGeneratedBitmapToProfile = onUpdateProfileBitmap,
                                onShareCreationToWall = { title, body ->
                                    onCreatePost("Studio Art", title, body)
                                }
                            )

                            // Content Moderation System (Flag Posts, Comments & Profiles + Review Queue)
                            com.example.ui.components.ContentModerationSystemCard(
                                onSubmitFlagToBackend = onReportContent
                            )

                            // Centralized Dynamic Persona Dashboard (Kid, Teen, Student, Adult, Business, Veteran)
                            com.example.ui.components.CentralizedPersonaDynamicDashboardCard(
                                currentPersonaRole = signUpForm.role.ifBlank { profile.role },
                                userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                onSwitchPersona = { newRole ->
                                    onSwitchPersonaQuick(newRole, if (newRole.contains("kid", true)) 10L else if (newRole.contains("teen", true)) 15L else if (newRole.contains("student", true)) 20L else 28L)
                                },
                                onNavigateToTab = onSelectTab,
                                onShareMilestoneToWall = { title, body ->
                                    onCreatePost("Milestone", title, body)
                                }
                            )

                            com.example.ui.components.PlatformUrlGeneratorCard(
                                userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                currentVisualStyleId = currentVisualStyleId,
                                currentThemeHex = signUpForm.themeColorHex,
                                onPublishUrlToCommunityWall = { title, body ->
                                    onCreatePost("Platform Link", title, body)
                                }
                            )

                            // Financial Tip of the Day (Persona-Based Daily Literacy Snippet)
                            com.example.ui.components.FinancialTipOfTheDayCard(
                                userRole = signUpForm.role,
                                userAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                onApplyTipSavingsAction = { title, amountDollars ->
                                    onAddBudgetItem(title, "Save", amountDollars)
                                },
                                onShareTipToWall = { headline, body ->
                                    onCreatePost("Milestone", headline, body)
                                }
                            )

                            // Daily Horoscopes & 6D Cosmic Zodiac Hub
                            com.example.ui.components.DailyHoroscopesCosmicCard(
                                onApplyCosmicTheme = { visualStyleId, colorHex, zodiacEmoji ->
                                    onThemeVisualStyleSelect(visualStyleId)
                                    onThemeColorSelect(colorHex)
                                    onUpdateAvatarBadge(zodiacEmoji)
                                },
                                onShareHoroscopeToWall = { headline, body ->
                                    onCreatePost("Update", headline, body)
                                }
                            )

                            // WereEqual Donation & Creator Support Page (Charity of Choice or Support Platform Creator)
                            com.example.ui.components.WereEqualDonationAndCreatorSupportCard(
                                donorDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                onRecordGivingInBudget = { title, amountDollars ->
                                    onAddBudgetItem(title, "Give", amountDollars)
                                },
                                onTipEqualCoinsToCreatorOrCharity = { recipientName, eqcAmount, note ->
                                    onTipCommunityUser(recipientName, eqcAmount, note)
                                },
                                onEquipSupporterBadge = onUpdateAvatarBadge,
                                onShareDonationToWall = { headline, body ->
                                    onCreatePost("Milestone", headline, body)
                                }
                            )

                            // Three.js 3D Studio PBR & Procedural Zero-Occlusion Scene Renderer
                            com.example.ui.components.ThreeJsStudio3DSceneCard(
                                onShareSceneToWall = { headline, body ->
                                    onCreatePost("3D Studio", headline, body)
                                }
                            )

                            // Stages Of Life Persona Selection & Age-Verified Profile Customizer
                            com.example.ui.components.PersonaSelectionSystemCard(
                                currentRole = signUpForm.role,
                                currentAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                onApplyPersona = { role, age, defaultColorHex, styleId ->
                                    onSwitchPersonaQuick(role, age)
                                    onThemeColorSelect(defaultColorHex)
                                    onThemeVisualStyleSelect(styleId)
                                },
                                onNavigateToSection = { targetTab ->
                                    onSelectTab(targetTab)
                                },
                                onPublishPersonaPost = { headline, body ->
                                    onCreatePost("Persona", headline, body)
                                }
                            )

                            // 3D Avatar Base Character Selection Studio for Stages Of Life Personas (Local State Persisted)
                            com.example.ui.components.Persona3DAvatarSelectionCard(
                                currentPersonaRole = signUpForm.role.ifBlank { profile.role },
                                selected3DAvatarResId = selected3DAvatarResId,
                                onSelect3DAvatarPreset = onSelect3DAvatarPreset,
                                onSelectPersonaStage = { newStage ->
                                    val defaultAge = when (newStage.lowercase()) {
                                        "kid" -> 10L
                                        "teen" -> 15L
                                        "student" -> 20L
                                        "business" -> 32L
                                        "veteran" -> 38L
                                        else -> 28L
                                    }
                                    onSwitchPersonaQuick(newStage, defaultAge)
                                },
                                onEquipBadgeEmoji = onUpdateAvatarBadge,
                                onShareCharacterToWall = { headline, body ->
                                    onCreatePost("Studio Art", headline, body)
                                }
                            )

                            // Stages Of Life Resource Hub (Mental Health Hotlines, FAFSA/Loans, Jobs, Education + Admin Verification)
                            com.example.ui.components.ResourceHubCard(
                                currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                onShareResourceToWall = { headline, body ->
                                    onCreatePost("Resource", headline, body)
                                }
                            )

                            // Stages Of Life Community Hub (Firestore Shared Life Stage Tips & Resources + Helpful Upvote Mechanism)
                            com.example.ui.components.CommunityLifeStageHubCard(
                                postsState = postsState,
                                currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                currentPersonaRole = signUpForm.role.ifBlank { profile.role },
                                onCreateFirestoreTipOrResource = { category, title, content ->
                                    onCreatePost(category, title, content)
                                },
                                onUpvoteFirestorePost = { wallPost ->
                                    onLikePost(wallPost)
                                },
                                onRewardHelpfulVote = {
                                    onRewardHelpfulPostBonus()
                                }
                            )

                            // Explore the App Gallery & Remix Studio + Personal API Key Quota Auto-Switch
                            com.example.ui.components.AppGalleryAndApiKeyQuotaCard(
                                onApplyRemixedAppTheme = { colorHex, styleId, makeTransparent ->
                                    onThemeColorSelect(colorHex)
                                    onThemeVisualStyleSelect(styleId)
                                    if (makeTransparent) {
                                        onTransparencyChange(1.0f)
                                    }
                                },
                                onShareRemixToWall = { headline, body ->
                                    onCreatePost("Remix", headline, body)
                                }
                            )

                            // Compliant Educational Digital Asset & Market Literacy Simulator
                            com.example.ui.components.DigitalAssetMarketSimulatorCard(
                                equalCoinsBalance = virtualCurrencyBalance,
                                onLogSimulatedBudgetPractice = { title, category, amountDollars ->
                                    onAddBudgetItem(title, category, amountDollars)
                                },
                                onShareSimulationToWall = { headline, body ->
                                    onCreatePost("Milestone", headline, body)
                                }
                            )

                            // System-Wide Material 3 Dark Mode & Solid Color Card
                            com.example.ui.components.SystemWideDarkModeControlCard(
                                isDarkMode = isDarkMode,
                                onSetDarkMode = onSetDarkMode,
                                backgroundTransparency = backgroundTransparency,
                                onTransparencyChange = onTransparencyChange,
                                currentThemeHex = signUpForm.themeColorHex,
                                onSelectThemeColor = onThemeColorSelect
                            )

                            BackgroundTransparencyControlBarCard(
                                backgroundTransparency = backgroundTransparency,
                                onTransparencyChange = onTransparencyChange,
                                currentThemeHex = signUpForm.themeColorHex,
                                onSelectColorHex = onThemeColorSelect,
                                onOpenMaintenancePage = {
                                    onSelectTab(DashboardTab.MAINTENANCE_SETTINGS)
                                }
                            )
                        }

                        // Active Page Content (Consolidated into 6 primary sections)
                        when (tab) {
                            DashboardTab.HUB_3D -> {
                                ThreeDProfileAndFeedTab(
                                    profile = profile,
                                    customProfileBitmap = customProfileBitmap,
                                    selected3DAvatarResId = selected3DAvatarResId,
                                    avatarEmojiBadge = avatarEmojiBadge,
                                    postsState = postsState,
                                    isPlayingMusic = isPlayingMusic,
                                    isDarkMode = isDarkMode,
                                    everyLoginSuggestions = everyLoginSuggestions,
                                    selectedSportsIds = selectedSportsIds,
                                    learnedInterests = learnedInterests,
                                    learnedLifestyle = learnedLifestyle,
                                    learnedSchedule = learnedSchedule,
                                    learnedResearch = learnedResearch,
                                    learnedSignals = learnedSignals,
                                    aiAdaptivePlanState = aiAdaptivePlanState,
                                    onSetDarkMode = onSetDarkMode,
                                    onToggleMusic = onToggleMusic,
                                    onSelectBackgroundSongPreset = onSelectBackgroundSongPreset,
                                    onToggleSport = onToggleSport,
                                    onToggleLearnedInterest = onToggleLearnedInterest,
                                    onToggleLearnedLifestyle = onToggleLearnedLifestyle,
                                    onToggleLearnedSchedule = onToggleLearnedSchedule,
                                    onToggleLearnedResearch = onToggleLearnedResearch,
                                    onAddCustomLearningSignal = onAddCustomLearningSignal,
                                    onGenerateAdaptiveLearningPlan = onGenerateAdaptiveLearningPlan,
                                    onRefreshLoginSuggestions = onRefreshLoginSuggestions,
                                    onOpenNewsAndSportsTab = { onSelectTab(DashboardTab.NEWS_SPORTS) },
                                    onOpenMusicExternal = {
                                        val url = buildMusicPlatformSearchUrl(
                                            profile.musicPlatform,
                                            profile.musicTrackTitle
                                        )
                                        launchWebUrl(url)
                                    },
                                    onSwitchPersonaQuick = onSwitchPersonaQuick,
                                    onSelect3DAvatarPreset = onSelect3DAvatarPreset,
                                    onThemeColorSelect = onThemeColorSelect,
                                    currentVisualStyleId = currentVisualStyleId,
                                    onThemeVisualStyleSelect = onThemeVisualStyleSelect,
                                    onPersistThemeToFirestore = onSaveProfileFromForm,
                                    onMusicChange = onMusicChange,
                                    onNavigateToThemeAndPhotoCreator = {
                                        onSelectTab(DashboardTab.THEME_PROFILE)
                                    },
                                    onCreatePost = onCreatePost,
                                    onLikePost = onLikePost,
                                    onDeletePost = onDeletePost,
                                    userInteractionsState = userInteractionsState,
                                    personalizedInsightsState = personalizedInsightsState,
                                    budgetState = budgetState,
                                    onGenerateGeminiInsights = onGenerateGeminiInsights,
                                    onLogInteraction = onLogInteraction,
                                    onDeleteInteraction = onDeleteInteraction,
                                    onAddBudgetItem = onAddBudgetItem,
                                    onUpdateProfileBitmap = onUpdateProfileBitmap,
                                    onSelectDashboardTab = onSelectTab,
                                    onAdvanceThemeAndColor = onAdvanceThemeAndColor,
                                    careerMilestonesState = careerMilestonesState,
                                    onAddCareerMilestone = onAddCareerMilestone,
                                    onUpdateCareerMilestoneStatus = onUpdateCareerMilestoneStatus,
                                    onDeleteCareerMilestone = onDeleteCareerMilestone,
                                    backgroundTransparency = backgroundTransparency,
                                    onTransparencyChange = onTransparencyChange,
                                    onExecuteUniversalSearch = { executeUniversalSearch(it) },
                                    onReportContent = onShowReportDialog,
                                    onConnectSpotify = { musicServiceToConnect = "Spotify" },
                                    onConnectAppleMusic = { musicServiceToConnect = "Apple Music" },
                                    onDisconnectSpotify = onDisconnectSpotify,
                                    onDisconnectAppleMusic = onDisconnectAppleMusic,
                                    onTipPostAuthor = { post ->
                                        onTipCommunityUser(
                                            post.authorName,
                                            15L,
                                            "Thank you for sharing '${post.title.take(28)}'!"
                                        )
                                    }
                                )

                                // Insert News & Sports into Home Hub for simplicity
                                RealNewsAndSportsHubCard(
                                    onShareToWall = { headline, body ->
                                        onCreatePost("News & Sports Update", headline, body)
                                    }
                                )
                            }

                            DashboardTab.THEME_PROFILE -> {
                                CreateThemeProfileScreen(
                                    formState = signUpForm,
                                    customProfileBitmap = customProfileBitmap,
                                    selected3DAvatarResId = selected3DAvatarResId,
                                    selectedAvatarTags = selectedAvatarTags,
                                    avatarEmojiBadge = avatarEmojiBadge,
                                    aiImageState = aiProfileImageState,
                                    isPlayingMusic = isPlayingMusic,
                                    isDarkMode = isDarkMode,
                                    onSetDarkMode = onSetDarkMode,
                                    selectedSportsIds = selectedSportsIds,
                                    learnedInterests = learnedInterests,
                                    learnedLifestyle = learnedLifestyle,
                                    learnedSchedule = learnedSchedule,
                                    learnedResearch = learnedResearch,
                                    learnedSignals = learnedSignals,
                                    aiAdaptivePlanState = aiAdaptivePlanState,
                                    onDisplayNameChange = onDisplayNameChange,
                                    onAgeChange = onAgeChange,
                                    onLocationChange = onLocationChange,
                                    onBioChange = onBioChange,
                                    onRoleSelect = onRoleSelect,
                                    onThemeColorSelect = onThemeColorSelect,
                                    onThemeVisualStyleSelect = onThemeVisualStyleSelect,
                                    onMusicChange = onMusicChange,
                                    onSelectBackgroundSongPreset = onSelectBackgroundSongPreset,
                                    onToggleMusic = onToggleMusic,
                                    onToggleSport = onToggleSport,
                                    onToggleLearnedInterest = onToggleLearnedInterest,
                                    onToggleLearnedLifestyle = onToggleLearnedLifestyle,
                                    onToggleLearnedSchedule = onToggleLearnedSchedule,
                                    onToggleLearnedResearch = onToggleLearnedResearch,
                                    onAddCustomLearningSignal = onAddCustomLearningSignal,
                                    onGenerateAdaptiveLearningPlan = onGenerateAdaptiveLearningPlan,
                                    onUpdateProfileBitmap = onUpdateProfileBitmap,
                                    onSelect3DAvatarPreset = onSelect3DAvatarPreset,
                                    onToggleAvatarTag = onToggleAvatarTag,
                                    onUpdateAvatarBadge = onUpdateAvatarBadge,
                                    onGenerateAiProfilePhoto = onGenerateAiProfilePhoto,
                                    onPledgeChange = onPledgeChange,
                                    onSaveProfile = onSaveProfileFromForm,
                                    onPublishPostToDashboard = { title, body ->
                                        onCreatePost("Update", title, body)
                                    },
                                    onOpenDashboardFeed = {
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    backgroundTransparency = backgroundTransparency,
                                    onTransparencyChange = onTransparencyChange,
                                    onOpenMaintenancePage = {
                                        onSelectTab(DashboardTab.MAINTENANCE_SETTINGS)
                                    },
                                    virtualCurrencyBalance = virtualCurrencyBalance,
                                    dailyBonusClaimed = dailyBonusClaimed,
                                    loginStreakDays = loginStreakDays,
                                    claimedBadgeIds = claimedBadgeIds,
                                    unlockedShopItemIds = unlockedShopItemIds,
                                    currencyTransactions = currencyTransactions,
                                    currencyStatusBanner = currencyStatusBanner,
                                    onClaimDailyLoginBonus = onClaimDailyLoginBonus,
                                    onRewardHelpfulPostBonus = onRewardHelpfulPostBonus,
                                    onClaimBadgeQuest = onClaimBadgeQuest,
                                    onPurchaseOrApplyShopItem = onPurchaseOrApplyShopItem,
                                    onTipCommunityUser = onTipCommunityUser
                                )
                            }

                            DashboardTab.FRIENDS -> {
                                FriendsAndDiscoveryScreen(
                                    friendsState = friendsState,
                                    incomingRequestsState = incomingRequestsState,
                                    discoveredUsers = discoveredUsers,
                                    myPhoneNumber = profile.phoneNumber,
                                    myUserProfile = profile,
                                    onUpdatePhoneNumber = onUpdatePhoneNumber,
                                    onSyncContacts = onSyncContacts,
                                    onSearchUsers = onSearchUsers,
                                    onSendFriendRequest = onSendFriendRequest,
                                    onAcceptRequest = onAcceptRequest,
                                    onDeclineRequest = onDeclineRequest,
                                    onShowReportDialog = onShowReportDialog,
                                    onBack = { onSelectTab(DashboardTab.HUB_3D) }
                                )
                            }

                            DashboardTab.MESSAGING -> {
                                WereEqualMessagingScreen(
                                    profile = profile,
                                    onShareMessageToWall = { headline, body ->
                                        onCreatePost("Milestone", headline, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    onLogMessagingInteraction = { detail ->
                                        onLogInteraction("Messaging", detail, "Community")
                                    },
                                    onStartVideoCall = {
                                        onSelectTab(DashboardTab.VIDEO_CHAT)
                                    }
                                )

                                // Add Studio & Arcade to Community tab for simplicity
                                StudioAndArcadeTab(
                                    highScore = profile.arcadeHighScore,
                                    onShareStudioCreation = { title, body ->
                                        onCreatePost("Studio Art", title, body)
                                    },
                                    onNewHighScore = onNewArcadeHighScore
                                )
                            }

                            DashboardTab.VIDEO_CHAT -> {
                                WereEqualVideoChatScreen(
                                    profile = profile,
                                    onLogInteraction = { action, detail, category ->
                                        onLogInteraction(action, detail, category)
                                    },
                                    onShareCallHighlightToWall = { headline, body ->
                                        onCreatePost("Video Chat Milestone", headline, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    }
                                )
                            }

                            DashboardTab.FINANCE_GOALS -> {
                                com.example.ui.components.VirtualCurrencySystemCard(
                                    balance = virtualCurrencyBalance,
                                    dailyBonusClaimed = dailyBonusClaimed,
                                    streakDays = loginStreakDays,
                                    claimedBadgeIds = claimedBadgeIds,
                                    unlockedItemIds = unlockedShopItemIds,
                                    transactions = currencyTransactions,
                                    statusBanner = currencyStatusBanner,
                                    accentColor = parseThemeHex(signUpForm.themeColorHex),
                                    onClaimDailyLoginBonus = onClaimDailyLoginBonus,
                                    onRewardHelpfulPostBonus = onRewardHelpfulPostBonus,
                                    onClaimBadgeQuest = onClaimBadgeQuest,
                                    onPurchaseOrApplyShopItem = onPurchaseOrApplyShopItem,
                                    onTipCommunityUser = onTipCommunityUser
                                )

                                com.example.ui.components.FinancialTipOfTheDayCard(
                                    userRole = signUpForm.role,
                                    userAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                    onApplyTipSavingsAction = { title, amountDollars ->
                                        onAddBudgetItem(title, "Save", amountDollars)
                                    },
                                    onShareTipToWall = { headline, body ->
                                        onCreatePost("Milestone", headline, body)
                                    }
                                )

                                com.example.ui.components.DigitalAssetMarketSimulatorCard(
                                    equalCoinsBalance = virtualCurrencyBalance,
                                    onLogSimulatedBudgetPractice = { title, category, amountDollars ->
                                        onAddBudgetItem(title, category, amountDollars)
                                    },
                                    onShareSimulationToWall = { headline, body ->
                                        onCreatePost("Milestone", headline, body)
                                    }
                                )

                                com.example.ui.components.WereEqualDonationAndCreatorSupportCard(
                                    donorDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                    onRecordGivingInBudget = { title, amountDollars ->
                                        onAddBudgetItem(title, "Give", amountDollars)
                                    },
                                    onTipEqualCoinsToCreatorOrCharity = { recipientName, eqcAmount, note ->
                                        onTipCommunityUser(recipientName, eqcAmount, note)
                                    },
                                    onEquipSupporterBadge = onUpdateAvatarBadge,
                                    onShareDonationToWall = { headline, body ->
                                        onCreatePost("Milestone", headline, body)
                                    }
                                )

                                PersonalFinanceGoalsScreen(
                                    profile = profile,
                                    budgetState = budgetState,
                                    onAddGoalEntryToFirestore = onAddBudgetItem,
                                    onDeleteGoalEntryFromFirestore = onDeleteBudgetItem,
                                    onUpdateOverallSavingsGoalInFirestore = onUpdateSavingsGoal,
                                    onShareMilestoneToWall = { title, content ->
                                        onCreatePost("Milestone", title, content)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    isBiometricUnlocked = isFinancialVaultUnlocked,
                                    onBiometricUnlockSuccess = onUnlockFinancialVault,
                                    onBiometricLockVault = onLockFinancialVault
                                )

                                // Add Delivery Portal to Wallet tab
                                DeliveryAndOrderingPortalScreen(
                                    profile = profile,
                                    onAddDeliveryExpenseToBudget = onAddBudgetItem,
                                    onLogDeliveryInteraction = onLogInteraction,
                                    onShareMealOrderToWall = { headline, body ->
                                        onCreatePost("Update", headline, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    }
                                )

                                // Stability Lounge context
                                StabilityAndLoungeTab(
                                    profile = profile,
                                    budgetState = budgetState,
                                    interactionsState = userInteractionsState,
                                    postsState = postsState,
                                    connections = communityConnections,
                                    onLogInteraction = onLogInteraction,
                                    onAddBudgetItem = onAddBudgetItem,
                                    onDeleteBudgetItem = onDeleteBudgetItem,
                                    onUpdateSavingsGoal = onUpdateSavingsGoal,
                                    onShareMilestoneToWall = { title, content ->
                                        onCreatePost("Milestone", title, content)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    onShareConnectionGreeting = { conn ->
                                        onCreatePost(
                                            "Milestone",
                                            "Connected with ${conn.name} in WereEqual Lounge!",
                                            "Bonding over ${conn.sharedInterests.joinToString(", ")} • \"We are all equal\""
                                        )
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    isFinancialVaultUnlocked = isFinancialVaultUnlocked,
                                    onUnlockFinancialVault = onUnlockFinancialVault,
                                    onLockFinancialVault = onLockFinancialVault
                                )
                            }

                            DashboardTab.MAINTENANCE_SETTINGS -> {
                                MaintenanceAndCustomizationScreen(
                                    backgroundTransparency = backgroundTransparency,
                                    onTransparencyChange = onTransparencyChange,
                                    currentThemeHex = signUpForm.themeColorHex,
                                    onThemeColorSelect = onThemeColorSelect,
                                    currentVisualStyleId = currentVisualStyleId,
                                    onThemeVisualStyleSelect = onThemeVisualStyleSelect,
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { onSetDarkMode(!isDarkMode) },
                                    userDisplayName = profile.displayName,
                                    userEmail = userAccountEmail,
                                    userPhone = userAccountPhone,
                                    currentLocationName = profile.location,
                                    onUpdateUserLocation = onLocationChange,
                                    onUpdateUserEmail = onUpdateUserEmail,
                                    onUpdateUserPhone = onUpdateUserPhone,
                                    onSaveSettingsToCloud = onSaveProfileFromForm,
                                    onNavigateToDashboard = { onSelectTab(DashboardTab.HUB_3D) },
                                    onNavigateToProfile = { onSelectTab(DashboardTab.THEME_PROFILE) },
                                    onNavigateToCodeOfConduct = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) }
                                )

                                // Add Resources Hub to Settings tab
                                FindingResourceHubScreen(
                                    profile = profile,
                                    resources = curatedResources,
                                    searchFilter = universalSearchQuery,
                                    onOpenResourceUrl = { launchWebUrl(it) },
                                    onShareResourceToWall = { item ->
                                        onCreatePost(
                                            "Resource Guide",
                                            item.title,
                                            "${item.subtitle} — Official portal: ${item.url}"
                                        )
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    onUpdateUserLocationFromGps = onLocationChange,
                                    onRecordLearnedSignal = onRecordLearnedSignal,
                                    onActivateStudentAccount = onSwitchPersonaQuick,
                                    onShareCustomMilestone = { headline, body ->
                                        onCreatePost("Resource Guide", headline, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    },
                                    careerMilestonesState = careerMilestonesState,
                                    onAddCareerMilestone = onAddCareerMilestone,
                                    onUpdateCareerMilestoneStatus = onUpdateCareerMilestoneStatus,
                                    onDeleteCareerMilestone = onDeleteCareerMilestone,
                                    isBiometricUnlocked = isResourceHubUnlocked,
                                    onUnlockBiometric = onUnlockResourceHub,
                                    onLockBiometric = onLockResourceHub
                                )

                                // Add AI Tools to Settings tab
                                AiToolsAndSchoolResearchScreen(
                                    profile = profile,
                                    learnedSignals = learnedSignals,
                                    aiStudyState = aiStudyState,
                                    aiFlashcardsState = aiFlashcardsState,
                                    aiAdaptivePlanState = aiAdaptivePlanState,
                                    onAskAiStudyTutor = onAskAiStudyTutor,
                                    onGenerateStudyFlashcards = onGenerateStudyFlashcards,
                                    onGenerateAdaptiveLearningPlan = onGenerateAdaptiveLearningPlan,
                                    onRecordLearnedSignal = onRecordLearnedSignal,
                                    onShareStudyGuideToWall = { title, body ->
                                        onCreatePost("Resource Guide", title, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    }
                                )

                                // Code of Conduct Ideas board
                                CodeOfConductScreen(
                                    userIdeas = userIdeas,
                                    aiIdeaFeedbackState = aiIdeaFeedbackState,
                                    onSubmitUserIdea = onSubmitUserIdea,
                                    onUpvoteUserIdea = onUpvoteUserIdea,
                                    onExpandIdeaWithAi = onExpandIdeaWithAi
                                )
                            }
                            else -> {}
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // In-App Search WebView Bottom Sheet
    if (searchUrlToOpen != null) {
        EmbeddedSearchWebView(
            url = searchUrlToOpen!!,
            onDismiss = { searchUrlToOpen = null },
            sheetState = searchSheetState
        )
    }

    if (musicServiceToConnect != null) {
        com.example.ui.components.MusicAccountConnectDialog(
            serviceName = musicServiceToConnect!!,
            onDismiss = { musicServiceToConnect = null },
            onConnectSuccess = { id ->
                if (musicServiceToConnect == "Spotify") onConnectSpotify(id)
                else onConnectAppleMusic(id)
                musicServiceToConnect = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThreeDProfileAndFeedTab(
    profile: UserProfile,
    customProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int?,
    avatarEmojiBadge: String,
    postsState: UiState<List<WallPost>>,
    isPlayingMusic: Boolean,
    isDarkMode: Boolean,
    everyLoginSuggestions: List<String>,
    selectedSportsIds: Set<String>,
    learnedInterests: Set<String>,
    learnedLifestyle: Set<String>,
    learnedSchedule: Set<String>,
    learnedResearch: Set<String>,
    learnedSignals: List<String>,
    aiAdaptivePlanState: AiTextUiState,
    onSetDarkMode: (Boolean) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectBackgroundSongPreset: (BackgroundSongPreset) -> Unit,
    onToggleSport: (String) -> Unit,
    onToggleLearnedInterest: (String) -> Unit,
    onToggleLearnedLifestyle: (String) -> Unit,
    onToggleLearnedSchedule: (String) -> Unit,
    onToggleLearnedResearch: (String) -> Unit,
    onAddCustomLearningSignal: (String, String) -> Unit,
    onGenerateAdaptiveLearningPlan: () -> Unit,
    onRefreshLoginSuggestions: () -> Unit,
    onOpenNewsAndSportsTab: () -> Unit,
    onOpenMusicExternal: () -> Unit,
    onSwitchPersonaQuick: (String, Long) -> Unit,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit,
    onThemeColorSelect: (String) -> Unit,
    currentVisualStyleId: String = "HYPERCUBE_6D",
    onThemeVisualStyleSelect: (String) -> Unit = {},
    onPersistThemeToFirestore: () -> Unit = {},
    onMusicChange: (String, String) -> Unit,
    onNavigateToThemeAndPhotoCreator: () -> Unit,
    onCreatePost: (String, String, String) -> Unit,
    onLikePost: (WallPost) -> Unit,
    onDeletePost: (String) -> Unit,
    userInteractionsState: UiState<List<UserInteraction>> = UiState.Success(emptyList()),
    personalizedInsightsState: UiState<List<PersonalizedDailyInsight>> = UiState.Success(emptyList()),
    budgetState: UiState<List<BudgetEntry>> = UiState.Success(emptyList()),
    onGenerateGeminiInsights: () -> Unit = {},
    onLogInteraction: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteInteraction: (String) -> Unit = {},
    onAddBudgetItem: (String, String, Double) -> Unit = { _, _, _ -> },
    onUpdateProfileBitmap: (Bitmap) -> Unit = {},
    onSelectDashboardTab: (DashboardTab) -> Unit = {},
    onAdvanceThemeAndColor: () -> Unit = {},
    careerMilestonesState: UiState<List<CareerMilestone>> = UiState.Success(buildDefaultStudentCareerMilestones()),
    onAddCareerMilestone: (String, String, String, String, Long, String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateCareerMilestoneStatus: (CareerMilestone, String) -> Unit = { _, _ -> },
    onDeleteCareerMilestone: (String) -> Unit = {},
    backgroundTransparency: Float = 0.82f,
    onTransparencyChange: (Float) -> Unit = {},
    onExecuteUniversalSearch: (String) -> Unit = {},
    onReportContent: (String, String) -> Unit = { _, _ -> },
    onConnectSpotify: () -> Unit = {},
    onConnectAppleMusic: () -> Unit = {},
    onDisconnectSpotify: () -> Unit = {},
    onDisconnectAppleMusic: () -> Unit = {},
    onTipPostAuthor: (WallPost) -> Unit = {}
) {
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    var postCategory by remember { mutableStateOf("Update") }
    var selectedFeeling by remember { mutableStateOf("🌟 Feeling Inspired") }
    var hubSubView by remember { mutableStateOf("ALL_6D") }
    var momentumXpBonus by remember { mutableStateOf(0) }

    val totalMomentumScore = 600 +
        (learnedInterests.size + learnedLifestyle.size + learnedSchedule.size + learnedResearch.size) * 25 +
        selectedSportsIds.size * 40 +
        (if (isPlayingMusic) 85 else 0) +
        momentumXpBonus

    val stories = remember {
        mutableStateListOf(
            FacebookStoryItem("Create Story", "You", "➕", HoloCyan),
            FacebookStoryItem("6D Synth Art", "Maya Lin", "🎨", NeonPink),
            FacebookStoryItem("VA Clinic Tips", "Marcus S.", "🛡️", CyberEmerald),
            FacebookStoryItem("Savings Goal!", "Elena R.", "💰", SolarAmber),
            FacebookStoryItem("STEM Library", "Youth Hub", "📚", HoloCyan)
        )
    }

    val feelings = listOf(
        "🌟 Feeling Inspired",
        "🎵 Listening to ${profile.musicPlatform}",
        "📚 Studying with AI",
        "🎉 Celebrating Life Event",
        "🤝 Building Community"
    )

    // 0. 6D Non-Stop Momentum & Clean Section Organizer Bar
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, NeonPink, CyberEmerald, SolarAmber))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.2f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SixDimensionalHdBadge(label = "6D NON-STOP")
                        Text(
                            text = "⚡ $totalMomentumScore XP Momentum",
                            style = MaterialTheme.typography.labelLarge,
                            color = CyberEmerald,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${selectedSportsIds.size} Sports Pinned • ${learnedInterests.size + learnedResearch.size} AI Signals • ${if (isPlayingMusic) "🎵 Playing Live" else "🎵 Jukebox Ready"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        momentumXpBonus += 50
                        onRefreshLoginSuggestions()
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Boost 6D", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Clean, Simple 3-Button Primary Switcher: Home | My Profile & Dating | Post Feed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { hubSubView = "ALL_6D" },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hubSubView == "ALL_6D") SolarAmber else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (hubSubView == "ALL_6D") Color.Black else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("switch_to_clean_home_button")
                ) {
                    Text("🏠 Clean Home", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { hubSubView = "MY_PROFILE_PAGE" },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hubSubView == "MY_PROFILE_PAGE") HoloCyan else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (hubSubView == "MY_PROFILE_PAGE") Color.Black else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("switch_to_my_profile_page_button")
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("👤 Profile & Dating", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { hubSubView = "DASHBOARD_POST_FEED" },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hubSubView == "DASHBOARD_POST_FEED") CyberEmerald else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (hubSubView == "DASHBOARD_POST_FEED") Color.Black else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("switch_to_dashboard_post_feed_button")
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🌐 Social Feed", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Simple, Organized Section Directory (Everything in Its Place)
            Text(
                text = "Quick Section Jump — Everything in Its Place:",
                style = MaterialTheme.typography.labelSmall,
                color = HoloCyan,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL_6D" to "🏠 Overview",
                    "MY_PROFILE_PAGE" to "👤 Profile & Theme",
                    "ADULT_DATING" to "💘 Adult Dating (18+)",
                    "WIDGET_HUB" to "🗺️ Maps • Search • YouTube • Code",
                    "WALLET_MARKET" to "💰 Finance & Summary",
                    "YOUTUBE_REELS_GAMES" to "▶️ YouTube & Reels",
                    "STUDENT_CAREERS" to "🎓 School & Careers",
                    "PHOTO_STUDIO" to "📸 Camera Studio"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = hubSubView == key,
                        onClick = {
                            hubSubView = key
                            onAdvanceThemeAndColor()
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (hubSubView == key) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }
    }

    // 0. Randomized Community Daily Affirmation Widget (Displayed on Dashboard, Post Feed & Profile views)
    if (hubSubView == "ALL_6D" || hubSubView == "DASHBOARD_POST_FEED" || hubSubView == "MY_PROFILE_PAGE") {
        DailyAffirmationCard(
            userDisplayName = profile.displayName,
            onPinAffirmationToWall = { headline, body ->
                momentumXpBonus += 35
                onCreatePost("Daily Affirmation", headline, "$body • #WereEqual #AllEqual")
            }
        )
    }

    // 0.001. Prominent Google Search Widget Card (as requested)
    if (hubSubView == "ALL_6D" || hubSubView == "WIDGET_HUB") {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .sixDimensionalCardEffect(tiltFactor = 1.1f)
                .testTag("google_search_widget_card")
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
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // Using a simple "G" text for Google if icon is unavailable, or a search icon
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleLarge,
                            color = HoloCyan,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Instant Google Search",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                var query by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search for community aid, libraries...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotBlank()) {
                                onExecuteUniversalSearch(query)
                                momentumXpBonus += 15
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (query.isNotBlank()) {
                                    onExecuteUniversalSearch(query)
                                    momentumXpBonus += 15
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = HoloCyan)
                        }
                    }
                )
                
                Text(
                    text = "Perform instant web searches without leaving WereEqual.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // 0.005. 4-in-1 Quick Widget: Google Maps, Google Search, YouTube & Coding Theme Creation
    if (
        hubSubView == "ALL_6D" ||
        hubSubView == "WIDGET_HUB" ||
        hubSubView == "MY_PROFILE_PAGE" ||
        hubSubView == "THEME_3D_STYLE"
    ) {
        GoogleMapsSearchYouTubeCodingThemeWidget(
            currentLocation = profile.location,
            currentThemeHex = profile.themeColorHex,
            currentVisualStyleId = currentVisualStyleId,
            backgroundTransparency = backgroundTransparency,
            isDarkMode = isDarkMode,
            onApplyCodedThemeColor = { hex ->
                momentumXpBonus += 25
                onThemeColorSelect(hex)
            },
            onApplyCodedVisualStyle = { styleId ->
                momentumXpBonus += 25
                onThemeVisualStyleSelect(styleId)
            },
            onApplyCodedTransparency = onTransparencyChange,
            onSetDarkMode = onSetDarkMode,
            onShareToWall = { headline, body ->
                momentumXpBonus += 35
                onCreatePost("Update", headline, body)
            }
        )
    }

    // 0.008. Adult Profile Dating & Matchmaking Hub (On Adult Profile, Dating Filter & Overview)
    if (
        hubSubView == "ALL_6D" ||
        hubSubView == "MY_PROFILE_PAGE" ||
        hubSubView == "ADULT_DATING"
    ) {
        AdultProfileDatingHubCard(
            userDisplayName = profile.displayName,
            userAge = profile.age,
            userRole = profile.role,
            userLocation = profile.location,
            onSwitchToAdultProfile = {
                momentumXpBonus += 35
                onSwitchPersonaQuick("adult", 26L)
            },
            onStartVideoDateCall = {
                onSelectDashboardTab(DashboardTab.VIDEO_CHAT)
            },
            onOpenMessagingWithMatch = {
                onSelectDashboardTab(DashboardTab.MESSAGING)
            },
            onShareMatchMilestoneToWall = { headline, body ->
                momentumXpBonus += 40
                onCreatePost("Milestone", headline, body)
            }
        )
    }

    // 0.009. Monthly Financial Literacy Tracker (Monthly Savings & Expenses Input + Visual Summary Card)
    if (hubSubView == "ALL_6D" || hubSubView == "WALLET_MARKET") {
        FinancialLiteracyTrackerCard(
            budgetState = budgetState,
            onAddBudgetItem = { title, category, amountDollars ->
                momentumXpBonus += 35
                onAddBudgetItem(title, category, amountDollars)
            },
            onShareSummaryToWall = { headline, body ->
                momentumXpBonus += 40
                onCreatePost("Milestone", headline, body)
            },
            userRole = profile.role,
            userAge = profile.age
        )
    }

    // 0.01. Download & Install WereEqual App Center (APK / AAB / Pin 3D WEREALLEQUALS Icon to Home Screen & Share)
    if (hubSubView == "ALL_6D") {
        DownloadAndInstallAppCenterCard()
    }

    // 0.0. WereEqual Theme Selection, Basic Color Studio & Create Your Own Custom Profile Theme (Accessible on Home, Profile Page & Theme sub-view)
    if (hubSubView == "ALL_6D" || hubSubView == "MY_PROFILE_PAGE" || hubSubView == "THEME_3D_STYLE") {
        ThreeDThemeStyleSelectorMenuCard(
            currentVisualStyleId = currentVisualStyleId,
            currentThemeHex = profile.themeColorHex,
            isDarkMode = isDarkMode,
            onSelectVisualStyle = { styleId ->
                momentumXpBonus += 30
                onThemeVisualStyleSelect(styleId)
            },
            onSelectThemeColor = { hex ->
                momentumXpBonus += 20
                onThemeColorSelect(hex)
            },
            onPersistThemeToFirestore = onPersistThemeToFirestore,
            userDisplayName = profile.displayName,
            backgroundTransparency = backgroundTransparency,
            onTransparencyChange = onTransparencyChange,
            onSetDarkMode = onSetDarkMode,
            onShareCustomThemeToWall = { headline, body ->
                momentumXpBonus += 45
                onCreatePost("Studio Art", headline, body)
            }
        )
    }

    // 0.05. Student Account & School, Jobs & Careers Application Portal (with Firestore Vertical Timeline)
    if (hubSubView == "ALL_6D" || hubSubView == "STUDENT_CAREERS" || hubSubView == "MY_PROFILE_PAGE") {
        val localCtx = LocalContext.current
        StudentSchoolAndCareersPortalCard(
            currentRole = profile.role,
            currentAge = profile.age,
            onSelectStudentAccount = {
                momentumXpBonus += 40
                onSwitchPersonaQuick("creator", 18L)
            },
            onOpenExternalUrl = { url ->
                try {
                    localCtx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) {
                }
            },
            onOpenAiStudyTutor = {
                onSelectDashboardTab(DashboardTab.AI_TOOLS)
            },
            onShareApplicationGoalToWall = { headline, body ->
                momentumXpBonus += 35
                onCreatePost("Resource Guide", headline, body)
            },
            careerMilestonesState = careerMilestonesState,
            onAddCareerMilestone = onAddCareerMilestone,
            onUpdateCareerMilestoneStatus = onUpdateCareerMilestoneStatus,
            onDeleteCareerMilestone = onDeleteCareerMilestone
        )
    }

    // 0.08. Profile YouTube Player, Watch & Make Video Reels Studio & Teen/Kid All-Platform Gaming Hub
    if (
        hubSubView == "ALL_6D" ||
        hubSubView == "MY_PROFILE_PAGE" ||
        hubSubView == "DASHBOARD_POST_FEED" ||
        hubSubView == "YOUTUBE_REELS_GAMES"
    ) {
        ProfileYouTubeReelsAndGamesHubCard(
            displayName = profile.displayName,
            currentRole = profile.role,
            currentAge = profile.age,
            onSwitchToTeenOrKidProfile = { role, age ->
                momentumXpBonus += 45
                onSwitchPersonaQuick(role, age)
            },
            onPublishVideoOrReelToDashboard = { title, body ->
                momentumXpBonus += 50
                onCreatePost("Studio Art", title, body)
            },
            onLogHubInteraction = { action, detail, category ->
                momentumXpBonus += 20
                onLogInteraction(action, detail, category)
            },
            onOpenFullDashboardFeed = {
                hubSubView = "DASHBOARD_POST_FEED"
            }
        )
    }

    // 0.1. WereEqual Digital Wallet & Vault
    if (hubSubView == "ALL_6D" || hubSubView == "WALLET_MARKET") {
        DigitalWalletCard(
            profile = profile,
            budgetState = budgetState,
            onAddWalletTransaction = { title, category, amountDollars ->
                momentumXpBonus += 35
                onAddBudgetItem(title, category, amountDollars)
                onLogInteraction("FinanceUpdate", "$category: $title ($${amountDollars.toInt()})", "Financial")
            },
            onOpenFullFinanceScreen = {
                onSelectDashboardTab(DashboardTab.FINANCE_GOALS)
            }
        )

        // 0.2. WereEqual Marketplace & Business / Content Ads Board
        CommunityMarketplaceCard(
            userDisplayName = profile.displayName,
            userRole = profile.role,
            onPayWithDigitalWallet = { title, category, amountDollars ->
                momentumXpBonus += 40
                onAddBudgetItem(title, category, amountDollars)
                onLogInteraction("FeatureVisit", "Marketplace: $title", "Community")
            },
            onShareListingToWall = { headline, body ->
                momentumXpBonus += 30
                onCreatePost("Marketplace Ad", headline, body)
            }
        )
    }

    // 0.3. Creative Photo Studio (Device Camera Capture + Persona Color Grades & Overlays + Profile Photo Updater + Social Post Publisher)
    if (hubSubView == "ALL_6D" || hubSubView == "PHOTO_STUDIO") {
        CreativePhotoStudioCard(
            displayName = profile.displayName,
            currentProfileBitmap = customProfileBitmap,
            selected3DAvatarResId = selected3DAvatarResId,
            userPersona = profile.role.ifBlank { "Adult" },
            onApplyFilteredBitmapToProfile = { bmp ->
                momentumXpBonus += 35
                onUpdateProfileBitmap(bmp)
            },
            onPublishPhotoToSocialPost = { title, caption, category ->
                momentumXpBonus += 50
                onCreatePost(category, title, caption)
                onLogInteraction("FeatureVisit", "Published Studio Photo Post: $title", "Photo Studio")
            },
            onLogStudioInteraction = { detail ->
                onLogInteraction("FeatureVisit", detail, "Photo Studio")
            }
        )
    }

    // 0.5. Personalized Insight Module: Firestore User Interactions + Gemini Daily Engagement-Driving Suggestions
    if (hubSubView == "ALL_6D") {
        PersonalizedInsightModuleCard(
            displayName = profile.displayName.substringBefore(" "),
            interactionsState = userInteractionsState,
            insightsState = personalizedInsightsState,
            onGenerateGeminiInsights = {
                momentumXpBonus += 45
                onGenerateGeminiInsights()
            },
            onLogInteraction = { actionType, detail, category ->
                momentumXpBonus += 25
                onLogInteraction(actionType, detail, category)
            },
            onDeleteInteraction = onDeleteInteraction,
            onExecuteInsightAction = { insight, targetTab ->
                momentumXpBonus += 35
                onLogInteraction("InsightAction", "Opened: ${insight.title}", insight.category)
                onSelectDashboardTab(targetTab)
            }
        )
    }

    // 0.6. Recharts Personal Growth Dashboard (Activity, Mood Trends & Financial Literacy Over Time)
    if (hubSubView == "GROWTH_RECHARTS") {
        PersonalGrowthRechartsDashboard(
            profile = profile,
            interactionsState = userInteractionsState,
            budgetState = budgetState,
            postsState = postsState,
            onLogInteraction = { actionType, detail, category ->
                momentumXpBonus += 30
                onLogInteraction(actionType, detail, category)
            },
            onAddBudgetItem = onAddBudgetItem,
            onShareGrowthMilestoneToWall = { title, content ->
                onCreatePost("Milestone", title, content)
            }
        )
    }

    // 1. Facebook-Life Style Daily Stories & Highlights Reel
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
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
                Text(
                    text = "Facebook Life Stories & 3D Community Moments",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tap + to Add Story",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoloCyan,
                    modifier = Modifier.clickable {
                        stories.add(
                            1,
                            FacebookStoryItem(
                                title = selectedFeeling.substringAfter(" "),
                                author = profile.displayName.take(10),
                                emoji = avatarEmojiBadge,
                                accent = CyberEmerald
                            )
                        )
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                stories.forEachIndexed { idx, story ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, story.accent.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .width(108.dp)
                            .height(126.dp)
                            .clickable {
                                if (idx == 0) {
                                    onCreatePost(
                                        "Milestone",
                                        "${profile.displayName}'s Daily Story • $selectedFeeling",
                                        "Sharing a positive Facebook Life moment from ${profile.location}! \"We are all equal\""
                                    )
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(story.accent.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = story.emoji, style = MaterialTheme.typography.titleMedium)
                            }
                            Column {
                                Text(
                                    text = story.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = story.author,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = story.accent,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 2. 3D Profile Card with Material 3 Light/Dark Mode Toggle, Photo Creation Shortcut & All-Platform Music Player
    val profileAccent = parseThemeHex(profile.themeColorHex)
    val profileSecondary = findSecondaryAccentForHex(profile.themeColorHex)
    val activeVisualStyleTitle = THREE_D_THEME_VISUAL_STYLES.firstOrNull {
        it.id == currentVisualStyleId
    }?.title ?: "WereEqual Classic Theme"

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(profileAccent, profileSecondary, CyberEmerald))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.0f)
            .testTag("three_d_profile_interface_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 3D Banner Header with Live ThreeDimensionalBackground + Quick Light/Dark Mode Toggle Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                ThreeDimensionalBackground(
                    accentHex = profile.themeColorHex,
                    visualStyleId = currentVisualStyleId,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (isDarkMode) HoloCyan.copy(alpha = 0.7f) else SolarAmber.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .clickable { onSetDarkMode(!isDarkMode) }
                        .testTag("hub_3d_profile_header_mode_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = if (isDarkMode) "Dark Mode Active" else "Light Mode Active",
                            tint = if (isDarkMode) HoloCyan else SolarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isDarkMode) "M3 Dark 3D" else "M3 Light 3D",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.58f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, profileAccent.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "${activeVisualStyleTitle.uppercase()} • ${profile.themeColorHex.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
                            .clickable { onNavigateToThemeAndPhotoCreator() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (customProfileBitmap != null) {
                            Image(
                                bitmap = customProfileBitmap.asImageBitmap(),
                                contentDescription = "User Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (selected3DAvatarResId != null) {
                            Image(
                                painter = painterResource(id = selected3DAvatarResId),
                                contentDescription = "Selected 3D Avatar Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = avatarEmojiBadge, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = profile.displayName.take(2).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${profile.role.uppercase()} • AGE ${profile.age} • ${profile.location}",
                            style = MaterialTheme.typography.labelLarge,
                            color = HoloCyan
                        )
                        if (profile.bio.isNotBlank()) {
                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Quick 3D-Style Avatar Portrait Switcher on 3D Hub
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Quick 3D Avatar Portrait Switcher:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        THREE_D_AVATAR_PRESETS.forEach { preset ->
                            val isSelected = customProfileBitmap == null && selected3DAvatarResId == preset.drawableResId
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) HoloCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .clickable { onSelect3DAvatarPreset(preset) }
                                    .testTag("hub_quick_3d_avatar_${preset.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = preset.drawableResId),
                                        contentDescription = preset.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) HoloCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Shortcut Button to Create Profile Photo & 3D Theme + Inline Create Your Own Custom Profile Theme Studio
                var showInlineProfileThemeCreator by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showInlineProfileThemeCreator = !showInlineProfileThemeCreator },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_card_create_own_theme_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showInlineProfileThemeCreator) "Hide Theme Creator" else "✨ Create My Own Theme",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToThemeAndPhotoCreator,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_create_photo_profile_button")
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Full Profile Studio",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showInlineProfileThemeCreator) {
                    CustomProfileThemeCreatorSection(
                        userDisplayName = profile.displayName,
                        currentVisualStyleId = currentVisualStyleId,
                        currentThemeHex = profile.themeColorHex,
                        isDarkMode = isDarkMode,
                        backgroundTransparency = backgroundTransparency,
                        isExpanded = true,
                        onToggleExpanded = { showInlineProfileThemeCreator = false },
                        onSelectVisualStyle = { styleId ->
                            momentumXpBonus += 30
                            onThemeVisualStyleSelect(styleId)
                        },
                        onSelectThemeColor = { hex ->
                            momentumXpBonus += 25
                            onThemeColorSelect(hex)
                        },
                        onTransparencyChange = onTransparencyChange,
                        onSetDarkMode = onSetDarkMode,
                        onPersistThemeToFirestore = onPersistThemeToFirestore,
                        onShareCustomThemeToWall = { headline, body ->
                            momentumXpBonus += 45
                            onCreatePost("Studio Art", headline, body)
                        }
                    )
                }

                // Quick Persona & Age Gate Switcher
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Instant Persona & Age Group Switcher:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = profile.role == "veteran",
                            onClick = { onSwitchPersonaQuick("veteran", 28L) },
                            label = { Text("Veteran (28)") },
                            modifier = Modifier.testTag("quick_persona_veteran")
                        )
                        FilterChip(
                            selected = profile.role == "creator",
                            onClick = { onSwitchPersonaQuick("creator", 16L) },
                            label = { Text("Student (16)") },
                            modifier = Modifier.testTag("quick_persona_creator")
                        )
                        FilterChip(
                            selected = profile.role == "business",
                            onClick = { onSwitchPersonaQuick("business", 35L) },
                            label = { Text("Biz (35)") },
                            modifier = Modifier.testTag("quick_persona_business")
                        )
                    }
                }

                // Quick 3D Color Palette Presets on Profile Card (Syncs Palette + 3D Geometry to Firestore)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "3D Color Palettes (Tap Palette to Sync to Firestore & Profile):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CURATED_3D_COLOR_PALETTES.forEach { palette ->
                            val isActive = profile.themeColorHex.equals(palette.primaryHex, ignoreCase = true) &&
                                currentVisualStyleId == palette.visualStyleId
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isActive) {
                                    parseThemeHex(palette.primaryHex).copy(alpha = 0.22f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                border = BorderStroke(
                                    width = if (isActive) 2.dp else 1.dp,
                                    brush = Brush.horizontalGradient(palette.swatchColors)
                                ),
                                modifier = Modifier
                                    .clickable {
                                        onThemeColorSelect(palette.primaryHex)
                                        onThemeVisualStyleSelect(palette.visualStyleId)
                                    }
                                    .testTag("profile_card_palette_${palette.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        palette.swatchColors.take(3).forEach { c ->
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(c)
                                            )
                                        }
                                    }
                                    Text(
                                        text = palette.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isActive) parseThemeHex(palette.primaryHex) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Expanded 32-Color MySpace 3D Theme Swatches
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "MySpace 3D Theme Swatches (32 Colors — Tap to Sync):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EXPANDED_THEME_COLOR_OPTIONS.forEach { option ->
                            val isSelected = profile.themeColorHex.equals(option.hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(option.color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onThemeColorSelect(option.hex) }
                            )
                        }
                    }
                }

                // Material 3 Color Scheme Light / Dark Mode Switcher for 3D Profile Interface
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hub_3d_profile_m3_mode_section")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = if (isDarkMode) "Dark Mode" else "Light Mode",
                                    tint = if (isDarkMode) HoloCyan else SolarAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Material 3 3D Profile Color Scheme",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isDarkMode) {
                                            "DarkColorScheme • Deep Space Nebula Surfaces"
                                        } else {
                                            "LightColorScheme • Crisp Daylight 3D Surfaces"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onSetDarkMode(it) },
                                modifier = Modifier.testTag("hub_3d_profile_dark_mode_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !isDarkMode,
                                onClick = { onSetDarkMode(false) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = SolarAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text("☀️ Light Mode") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hub_3d_profile_light_mode_chip")
                            )
                            FilterChip(
                                selected = isDarkMode,
                                onClick = { onSetDarkMode(true) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = HoloCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text("🌙 Dark Mode") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hub_3d_profile_dark_mode_chip")
                            )
                        }

                        // Live Material 3 ColorScheme Semantic Token Preview Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary),
                                Triple("PrimaryContainer", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer),
                                Triple("SecondaryContainer", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer),
                                Triple("TertiaryContainer", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer),
                                Triple("SurfaceVariant", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                            ).forEach { (tokenName, containerColor, contentColor) ->
                                Surface(
                                    color = containerColor,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = tokenName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = contentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Universal Profile Music Player Widget + All Music Platform Selector
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberEmerald.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Profile Music",
                                    tint = CyberEmerald
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.musicTrackTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isPlayingMusic) "Now Playing • ${profile.musicPlatform}" else "Connected via ${profile.musicPlatform}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPlayingMusic) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = onToggleMusic,
                                modifier = Modifier.testTag("music_play_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlayingMusic) "Pause Music" else "Play Music",
                                    tint = CyberEmerald
                                )
                            }
                            IconButton(onClick = onOpenMusicExternal) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open in Music App",
                                    tint = HoloCyan
                                )
                            }
                        }

                        // Horizontal Scrollable Selector of All 11 Music Platforms
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ALL_MUSIC_PLATFORMS.forEach { platform ->
                                FilterChip(
                                    selected = profile.musicPlatform.equals(platform, ignoreCase = true),
                                    onClick = { onMusicChange(profile.musicTrackTitle, platform) },
                                    label = { Text(platform, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 2B. Every-Login Personalized Suggestions & Quick Link to Daily News & Sports Hub
    if (hubSubView == "WALL_BRIEFING") {
        EveryLoginSuggestionsCard(
            displayName = profile.displayName.substringBefore(" "),
            suggestions = everyLoginSuggestions,
            onRefreshSuggestions = onRefreshLoginSuggestions,
            onOpenNewsAndSportsTab = onOpenNewsAndSportsTab
        )
    }

    // 2C. Profile Background Song Jukebox & Real Ambient Synthesizer Player
    if (hubSubView == "MUSIC_SPORTS") {
        ProfileBackgroundMusicJukeboxCard(
            currentTrackTitle = profile.musicTrackTitle,
            currentPlatform = profile.musicPlatform,
            isPlayingMusic = isPlayingMusic,
            spotifyConnected = profile.spotifyConnected,
            appleMusicConnected = profile.appleMusicConnected,
            onSelectSongPreset = onSelectBackgroundSongPreset,
            onCustomSongAndPlatformChange = onMusicChange,
            onTogglePlayMusic = onToggleMusic,
            onConnectSpotify = onConnectSpotify,
            onConnectAppleMusic = onConnectAppleMusic,
            onDisconnectSpotify = onDisconnectSpotify,
            onDisconnectAppleMusic = onDisconnectAppleMusic
        )

        // 2D. Profile Chosen Sports Hub (Each Profile Page has Sports of Their Choosing)
        ProfileSportsSelectorCard(
            selectedSportsIds = selectedSportsIds,
            onToggleSport = onToggleSport
        )
    }

    // 2E. Platform Adaptive Learning Engine (Interests, Lifestyle, Daily Schedule & Research)
    if (hubSubView == "AI_MIND") {
        ProfileAdaptiveLearningCard(
            learnedInterests = learnedInterests,
            learnedLifestyle = learnedLifestyle,
            learnedSchedule = learnedSchedule,
            learnedResearch = learnedResearch,
            recentActivitySignals = learnedSignals,
            aiAdaptivePlanState = aiAdaptivePlanState,
            onToggleInterest = onToggleLearnedInterest,
            onToggleLifestyle = onToggleLearnedLifestyle,
            onToggleSchedule = onToggleLearnedSchedule,
            onToggleResearch = onToggleLearnedResearch,
            onAddCustomLearningItem = onAddCustomLearningSignal,
            onGeneratePersonalizedBlueprint = onGenerateAdaptiveLearningPlan
        )
    }

    // 3. Facebook-Life Vibe Status, Life Event & Resource Post Composer
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEmotions,
                    contentDescription = null,
                    tint = SolarAmber
                )
                Text(
                    text = "What's on your mind, ${profile.displayName.substringBefore(" ")}? (Facebook Life Vibe)",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Feeling / Activity Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                feelings.forEach { feeling ->
                    FilterChip(
                        selected = selectedFeeling == feeling,
                        onClick = { selectedFeeling = feeling },
                        label = { Text(feeling, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Update" to "Status Update",
                    "Milestone" to "🎉 Life Event",
                    "Resource Guide" to "📚 Resource Guide"
                ).forEach { (catKey, catLabel) ->
                    FilterChip(
                        selected = postCategory == catKey,
                        onClick = { postCategory = catKey },
                        label = { Text(catLabel) }
                    )
                }
            }

            OutlinedTextField(
                value = postTitle,
                onValueChange = { postTitle = it },
                label = { Text("Headline or Life Event (e.g., Graduated STEM Class / FAFSA Tip)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_title_input")
            )

            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                label = { Text("Share life updates, school achievements, or helpful guides…") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_content_input")
            )

            Button(
                onClick = {
                    if (postTitle.isNotBlank() || postContent.isNotBlank()) {
                        val headline = postTitle.ifBlank { "${profile.displayName} • $selectedFeeling" }
                        val body = if (postContent.isNotBlank()) {
                            "$selectedFeeling — $postContent"
                        } else {
                            "$selectedFeeling — $postTitle"
                        }
                        onCreatePost(postCategory, headline, body)
                        postTitle = ""
                        postContent = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("publish_wall_post_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publish to Stages Of Life Community Wall", fontWeight = FontWeight.Bold)
            }
        }
    }

    // 4. Wall Posts Stream with Facebook-Life Multi-Reactions
    Text(
        text = "Your 3D Facebook-Life Wall & Community Feed",
        style = MaterialTheme.typography.titleLarge
    )

    when (postsState) {
        is UiState.Loading -> {
            Text(
                text = "Loading your 3D wall…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        is UiState.Error -> {
            Text(
                text = postsState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = SolarAmber
            )
        }
        is UiState.Success -> {
            if (postsState.data.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No posts yet. Share a life event, status update, or resource guide above!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                postsState.data.forEach { post ->
                    val cardAccent = parseThemeHex(post.accentHex)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, cardAccent.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = cardAccent.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${post.category.uppercase()} • ${post.authorRole.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cardAccent,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onReportContent(post.id, "post") },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Gavel,
                                            contentDescription = "Report post",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeletePost(post.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete post",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "By ${post.authorName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { onReportContent(post.id, "post") },
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.75f)),
                                        modifier = Modifier.testTag("flag_post_button_${post.id}")
                                    ) {
                                        Text(
                                            text = "🚩 Flag",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RosePulse,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { onTipPostAuthor(post) },
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.85f)),
                                        modifier = Modifier.testTag("tip_post_author_button_${post.id}")
                                    ) {
                                        Text(
                                            text = "🪙 Tip 15 EQC",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SolarAmber,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { onLikePost(post) },
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = "Like & Celebrate",
                                            tint = HoloCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = RosePulse,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Celebration,
                                            contentDescription = null,
                                            tint = SolarAmber,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${post.likesCount} Reactions")
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StabilityAndLoungeTab(
    profile: UserProfile,
    budgetState: UiState<List<BudgetEntry>>,
    interactionsState: UiState<List<UserInteraction>> = UiState.Success(emptyList()),
    postsState: UiState<List<WallPost>> = UiState.Success(emptyList()),
    connections: List<CommunityConnectionCard>,
    onLogInteraction: (String, String, String) -> Unit = { _, _, _ -> },
    onAddBudgetItem: (String, String, Double) -> Unit,
    onDeleteBudgetItem: (String) -> Unit,
    onUpdateSavingsGoal: (Double) -> Unit,
    onShareMilestoneToWall: (String, String) -> Unit,
    onShareConnectionGreeting: (CommunityConnectionCard) -> Unit,
    isFinancialVaultUnlocked: Boolean = false,
    onUnlockFinancialVault: () -> Unit = {},
    onLockFinancialVault: () -> Unit = {}
) {
    val isAdult = profile.age >= 18L

    // 0.0. Wondering World Digital Wallet & Community Marketplace / Business Ads
    DigitalWalletCard(
        profile = profile,
        budgetState = budgetState,
        onAddWalletTransaction = onAddBudgetItem
    )

    CommunityMarketplaceCard(
        userDisplayName = profile.displayName,
        userRole = profile.role,
        onPayWithDigitalWallet = onAddBudgetItem,
        onShareListingToWall = onShareMilestoneToWall
    )

    // 0. Personal Growth Recharts Dashboard (Activity, Mood Trends & Financial Literacy Over Time)
    PersonalGrowthRechartsDashboard(
        profile = profile,
        interactionsState = interactionsState,
        budgetState = budgetState,
        postsState = postsState,
        onLogInteraction = onLogInteraction,
        onAddBudgetItem = onAddBudgetItem,
        onShareGrowthMilestoneToWall = onShareMilestoneToWall
    )

    // 0.5. Firestore-Synced Personal Finance Goals & Visual Savings Milestone Progress Bars Screen
    PersonalFinanceGoalsScreen(
        profile = profile,
        budgetState = budgetState,
        onAddGoalEntryToFirestore = onAddBudgetItem,
        onDeleteGoalEntryFromFirestore = onDeleteBudgetItem,
        onUpdateOverallSavingsGoalInFirestore = onUpdateSavingsGoal,
        onShareMilestoneToWall = onShareMilestoneToWall,
        isBiometricUnlocked = isFinancialVaultUnlocked,
        onBiometricUnlockSuccess = onUnlockFinancialVault,
        onBiometricLockVault = onLockFinancialVault
    )

    // 1. Recharts-Style Financial Literacy Dashboard Screen
    FinancialLiteracyDashboardScreen(
        profile = profile,
        budgetState = budgetState,
        onAddBudgetItem = onAddBudgetItem,
        onDeleteBudgetItem = onDeleteBudgetItem,
        onUpdateSavingsGoal = onUpdateSavingsGoal,
        isBiometricUnlocked = isFinancialVaultUnlocked,
        onBiometricUnlockSuccess = onUnlockFinancialVault,
        onBiometricLockVault = onLockFinancialVault
    )

    // 2. Wondering World Equal-Match Community & Connections Lounge (Strict 18+ Gate)
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.55f))
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Connections Lounge",
                        tint = RosePulse
                    )
                    Column {
                        Text(
                            text = "Stages Of Life Connections Lounge",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Equal-match social & community discovery",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    color = RosePulse.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "18+ ONLY",
                        style = MaterialTheme.typography.labelSmall,
                        color = RosePulse,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (isAdult) {
                Text(
                    text = "Verified Adult Community Matches (Age ${profile.age} Verified):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                connections.forEach { conn ->
                    val connColor = parseThemeHex(conn.themeHex)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, connColor.copy(alpha = 0.45f))
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
                                Column {
                                    Text(conn.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "${conn.role} • ${conn.location}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = connColor
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onShareConnectionGreeting(conn) },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = connColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Say Hello")
                                }
                            }
                            Text(
                                text = conn.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                conn.sharedInterests.forEach { tag ->
                                    Surface(
                                        color = connColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = connColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Surface(
                    color = RosePulse.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked for under 18",
                            tint = RosePulse,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Section Strictly Locked: Your profile age is set to ${profile.age}. Users under 18 cannot access or appear in the adult Connections Lounge.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Horizontal Swipe Tab Strip with real-time fractional slide progress bar
 * and animated pill transitions between Dashboard, Profile, Community & Friends, and other tabs.
 */
@Composable
fun DashboardQuickSwipeTabStrip(
    pagerTabs: List<DashboardTab>,
    currentPage: Int,
    currentPageOffsetFraction: Float,
    activeAccentHex: String,
    onSelectTab: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = parseThemeHex(activeAccentHex)
    val continuousPosition = (currentPage + currentPageOffsetFraction).coerceIn(
        0f,
        (pagerTabs.size - 1).coerceAtLeast(1).toFloat()
    )
    val progressFraction by animateFloatAsState(
        targetValue = (continuousPosition + 1f) / pagerTabs.size.toFloat().coerceAtLeast(1f),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "swipe_progress_fraction"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan.copy(alpha = 0.7f), activeAccent, NeonPink.copy(alpha = 0.7f))
            )
        ),
        modifier = modifier.testTag("dashboard_quick_swipe_tab_strip")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pagerTabs.forEachIndexed { index, tab ->
                    val distance = (continuousPosition - index).absoluteValue.coerceIn(0f, 1f)
                    val isSelected = index == currentPage
                    val pillScale by animateFloatAsState(
                        targetValue = 0.95f + (1f - distance) * 0.05f,
                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                        label = "tab_pill_scale_$index"
                    )
                    val pillBgColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            activeAccent.copy(alpha = 0.24f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        },
                        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
                        label = "tab_pill_bg_$index"
                    )
                    val pillBorderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            activeAccent
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        },
                        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
                        label = "tab_pill_border_$index"
                    )

                    val tabLabel = when (tab) {
                        DashboardTab.HUB_3D -> "🌐 Dashboard"
                        DashboardTab.THEME_PROFILE -> "🎨 Profile"
                        DashboardTab.FRIENDS -> "👥 Community"
                        DashboardTab.MESSAGING -> "💬 Messages"
                        DashboardTab.VIDEO_CHAT -> "📹 Video"
                        DashboardTab.FINANCE_GOALS -> "💰 Wallet"
                        DashboardTab.MAINTENANCE_SETTINGS -> "⚙️ Settings"
                        else -> tab.name
                    }

                    Surface(
                        color = pillBgColor,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(if (isSelected) 1.4.dp else 1.dp, pillBorderColor),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = pillScale
                                scaleY = pillScale
                            }
                            .clickable { onSelectTab(tab) }
                            .testTag("quick_swipe_tab_pill_${tab.name.lowercase()}")
                    ) {
                        Text(
                            text = tabLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Fluid Animated Horizontal Slide Progress Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0.08f, 1f))
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(HoloCyan, activeAccent, CyberEmerald, NeonPink)
                            )
                        )
                )
            }
        }
    }
}

