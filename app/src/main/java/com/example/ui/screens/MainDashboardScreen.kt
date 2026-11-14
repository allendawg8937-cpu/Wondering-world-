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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.data.model.BlockedUser
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
import com.example.ui.components.AdaptiveUserLearningAiBotAndToolsCard
import com.example.ui.components.AdultProfileDatingHubCard
import com.example.ui.components.AiImageAndVoiceConversationStudioCard
import com.example.ui.components.AppGalleryAndApiKeyQuotaCard
import com.example.ui.components.AuthGuardedAppShellRouterCard
import com.example.ui.components.BackgroundSongPreset
import com.example.ui.components.CommunityLifeStageHubCard
import com.example.ui.components.CommunityMarketplaceCard
import com.example.ui.components.ContentModerationSystemCard
import com.example.ui.components.AchievementGamificationLeaderboardCard
import com.example.ui.components.CreativePhotoStudioCard
import com.example.ui.components.CustomProfileThemeCreatorSection
import com.example.ui.components.DailyHoroscopesCosmicCard
import com.example.ui.components.DashboardTopWeatherSearchHeader
import com.example.ui.components.DigitalAssetMarketSimulatorCard
import com.example.ui.components.DigitalWalletCard
import com.example.ui.components.EveryLoginSuggestionsCard
import com.example.ui.components.FinancialLiteracyTrackerCard
import com.example.ui.components.FinancialTipOfTheDayCard
import com.example.ui.components.Persona3DAvatarSelectionCard
import com.example.ui.components.PersonaProfileSystemCard
import com.example.ui.components.PersonaSelectionComponent
import com.example.ui.components.PersonalGrowthRechartsDashboard
import com.example.ui.components.PersonalizedInsightModuleCard
import com.example.ui.components.PhotoStudioComponent
import com.example.ui.components.PlatformReleaseAndDownloadHubCard
import com.example.data.model.Profile3DConfig
import com.example.ui.components.Profile3DCustomizerComponent
import com.example.ui.components.ProfileAdaptiveLearningCard
import com.example.ui.components.ProfileBackgroundMusicJukeboxCard
import com.example.ui.components.ProfileSportsSelectorCard
import com.example.ui.components.ProfileYouTubeReelsAndGamesHubCard
import com.example.data.export.UserDataExportPayload
import com.example.data.model.PersonaStageData
import com.example.data.model.buildDefaultPersonaStageData
import com.example.data.model.normalizePersonaStageId
import com.example.ui.components.PersonaDashboardComponent
import com.example.ui.components.ProfileDownloadDataCard
import com.example.ui.components.PublishedVideoReelsStore
import com.example.ui.components.ResourceHubCard
import com.example.ui.components.SettingsBlockUsersCard
import com.example.ui.components.SettingsCodeOfConductCard
import com.example.ui.components.SettingsDeleteAccountCard
import com.example.ui.components.SettingsPhoneWalletSetupCard
import com.example.ui.components.SettingsPlatformSwitcherCard
import com.example.ui.components.StudentSchoolAndCareersPortalCard
import com.example.ui.components.SystemWideDarkModeControlCard
import com.example.ui.components.THREE_D_AVATAR_PRESETS
import com.example.ui.components.THREE_D_THEME_VISUAL_STYLES
import com.example.ui.components.TextToVideoGeneratorStudioCard
import com.example.ui.components.ThreeDAvatarCustomizerCard
import com.example.ui.components.ThreeDAvatarPreset
import com.example.ui.components.ThreeDimensionalBackground
import com.example.ui.components.ThreeJsStudio3DSceneCard
import com.example.ui.components.VideoReelsComponent
import com.example.ui.components.VirtualCurrencySystemCard
import com.example.ui.components.WereAllEquals3DHeroShowcaseBanner
import com.example.ui.components.WereEqualDonationAndCreatorSupportCard
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
    profile3DConfigs: List<Profile3DConfig> = emptyList(),
    isProfile3DSaving: Boolean = false,
    profile3DStatusBanner: String? = null,
    isAuthenticatedForCloud: Boolean = false,
    onSaveProfile3DConfig: (Profile3DConfig, String) -> Unit = { _, _ -> },
    onDeleteProfile3DConfig: (String) -> Unit = {},
    personaStageDataMap: Map<String, PersonaStageData> = emptyMap(),
    isPersonaStageSaving: Boolean = false,
    personaStageStatusBanner: String? = null,
    onSavePersonaStageData: (PersonaStageData) -> Unit = {},
    preferredSearchEngine: String = "Google",
    onSelectSearchEngine: (String) -> Unit = {},
    platformLayoutMode: String = "Balanced",
    onSelectPlatformLayoutMode: (String) -> Unit = {},
    platformNotificationsEnabled: Boolean = true,
    onTogglePlatformNotifications: (Boolean) -> Unit = {},
    showMatureLoungeEnabled: Boolean = true,
    onToggleMatureLounge: (Boolean) -> Unit = {},
    selectedWalletTier: String = "Personal Digital Wallet",
    onSelectWalletTier: (String) -> Unit = {},
    blockedUsers: List<BlockedUser> = emptyList(),
    onBlockUser: (targetUserId: String, targetDisplayName: String, reason: String) -> Unit = { _, _, _ -> },
    onUnblockUser: (String) -> Unit = {},
    settingsStatusBanner: String? = null,
    onClearSettingsStatusBanner: () -> Unit = {},
    isDeletingAccount: Boolean = false,
    onDeleteAccountPermanently: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var universalSearchQuery by remember { mutableStateOf("") }
    var showAllPagesSheet by rememberSaveable { mutableStateOf(false) }
    var showInstallPublishSheet by rememberSaveable { mutableStateOf(false) }

    val blockedIdsSet = remember(blockedUsers) {
        blockedUsers.map { it.blockedUserId.lowercase() }.toSet()
    }
    val blockedNamesSet = remember(blockedUsers) {
        blockedUsers.map { it.blockedDisplayName.trim().lowercase() }.toSet()
    }
    val visibleDiscoveredUsers = remember(discoveredUsers, blockedIdsSet, blockedNamesSet) {
        discoveredUsers.filterNot { user ->
            user.userId.lowercase() in blockedIdsSet ||
                user.displayName.trim().lowercase() in blockedNamesSet
        }
    }
    val visibleCommunityConnections = remember(communityConnections, blockedIdsSet, blockedNamesSet) {
        communityConnections.filterNot { conn ->
            conn.id.lowercase() in blockedIdsSet ||
                conn.name.trim().lowercase() in blockedNamesSet
        }
    }

    // In-App Search WebView State
    var searchUrlToOpen by remember { mutableStateOf<String?>(null) }
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val utilitySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    val currentBudgetEntries = (budgetState as? UiState.Success)?.data ?: emptyList()
    val currentCareerMilestones = (careerMilestonesState as? UiState.Success)?.data ?: emptyList()
    val exportPayload = remember(
        profile,
        signUpForm,
        userAccountEmail,
        userAccountPhone,
        avatarEmojiBadge,
        isDarkMode,
        backgroundTransparency,
        currentVisualStyleId,
        selectedSportsIds,
        learnedInterests,
        learnedLifestyle,
        learnedSchedule,
        learnedResearch,
        learnedSignals,
        currentBudgetEntries,
        currentCareerMilestones,
        virtualCurrencyBalance,
        dailyBonusClaimed,
        loginStreakDays,
        currencyTransactions,
        personaStageDataMap,
        profile3DConfigs
    ) {
        UserDataExportPayload(
            profile = profile,
            signUpForm = signUpForm,
            userEmail = userAccountEmail,
            userPhone = userAccountPhone,
            avatarEmojiBadge = avatarEmojiBadge,
            isDarkMode = isDarkMode,
            backgroundTransparency = backgroundTransparency,
            currentVisualStyleId = currentVisualStyleId,
            selectedSportsIds = selectedSportsIds,
            learnedInterests = learnedInterests,
            learnedLifestyle = learnedLifestyle,
            learnedSchedule = learnedSchedule,
            learnedResearch = learnedResearch,
            learnedSignals = learnedSignals,
            budgetEntries = currentBudgetEntries,
            careerMilestones = currentCareerMilestones,
            virtualCurrencyBalance = virtualCurrencyBalance,
            dailyBonusClaimed = dailyBonusClaimed,
            loginStreakDays = loginStreakDays,
            currencyTransactions = currencyTransactions,
            personaStageDataMap = personaStageDataMap,
            profile3DConfigs = profile3DConfigs
        )
    }

    fun launchWebUrl(url: String) {
        searchUrlToOpen = url
    }

    fun executeUniversalSearch(overrideQuery: String? = null) {
        val q = (overrideQuery ?: universalSearchQuery).trim()
        if (q.isEmpty()) return
        onRecordLearnedSignal("Searched $preferredSearchEngine: ${q.take(24)}")
        val encoded = Uri.encode(q)
        val url = when (preferredSearchEngine) {
            "DuckDuckGo" -> "https://duckduckgo.com/?q=$encoded"
            "Bing" -> "https://www.bing.com/search?q=$encoded"
            else -> "https://www.google.com/search?q=$encoded"
        }
        launchWebUrl(url)
    }

    // DashboardTab destinations (respecting 18+ lounge visibility preference)
    val pagerTabs = remember(showMatureLoungeEnabled) {
        buildList {
            add(DashboardTab.HUB_3D)               // 1. Home
            add(DashboardTab.STUDIO_ARCADE)        // 2. Reels, Photo & Studio
            add(DashboardTab.THEME_PROFILE)        // 3. Profile & 3D
            add(DashboardTab.FRIENDS)              // 4. Friends
            add(DashboardTab.MESSAGING)            // 5. Messages
            add(DashboardTab.VIDEO_CHAT)           // 6. Video Calls
            add(DashboardTab.FINANCE_GOALS)        // 7. Wallet & Finance
            add(DashboardTab.RESOURCES)            // 8. School, Jobs & Aid
            add(DashboardTab.AI_TOOLS)             // 9. AI Tools & Tutor
            add(DashboardTab.NEWS_SPORTS)          // 10. News & Sports
            add(DashboardTab.DELIVERY_DRIVING)     // 11. Rideshare & Delivery
            if (showMatureLoungeEnabled) {
                add(DashboardTab.STABILITY_LOUNGE) // 12. Market, Growth & 18+
            }
            add(DashboardTab.CODE_OF_CONDUCT)      // 13. Safety & Community
            add(DashboardTab.MAINTENANCE_SETTINGS) // 14. Settings & Platform Hub
        }
    }

    val pagerState = rememberPagerState(
        initialPage = pagerTabs.indexOf(activeTab).coerceAtLeast(0),
        pageCount = { pagerTabs.size }
    )

    val currentActiveTab by rememberUpdatedState(activeTab)

    // Sync activeTab -> pager with smooth horizontal slide animation
    LaunchedEffect(activeTab, pagerTabs) {
        val targetPage = pagerTabs.indexOf(activeTab)
        if (targetPage >= 0 && targetPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(
                page = targetPage,
                animationSpec = tween(
                    durationMillis = 320,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    // Sync settled pager swipe -> activeTab (ignores intermediate animated pages)
    LaunchedEffect(pagerState, pagerTabs) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val tab = pagerTabs.getOrNull(page) ?: return@collect
            if (tab != currentActiveTab && !pagerState.isScrollInProgress) {
                onSelectTab(tab)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Clean 5-Tab Material 3 Bottom Bar with dedicated 1-tap Settings tab
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == DashboardTab.HUB_3D ||
                        activeTab == DashboardTab.THEME_PROFILE ||
                        activeTab == DashboardTab.NEWS_SPORTS ||
                        activeTab == DashboardTab.RESOURCES ||
                        activeTab == DashboardTab.CODE_OF_CONDUCT,
                    onClick = { onSelectTab(DashboardTab.HUB_3D) },
                    icon = { Icon(Icons.Default.Public, contentDescription = "Home") },
                    label = { Text("Home", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_hub_3d")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.STUDIO_ARCADE || activeTab == DashboardTab.AI_TOOLS,
                    onClick = { onSelectTab(DashboardTab.STUDIO_ARCADE) },
                    icon = { Icon(Icons.Default.Brush, contentDescription = "Reels & Studio") },
                    label = { Text("Studio", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_studio_arcade")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.FRIENDS ||
                        activeTab == DashboardTab.MESSAGING ||
                        activeTab == DashboardTab.VIDEO_CHAT,
                    onClick = { onSelectTab(DashboardTab.FRIENDS) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Friends & Chat") },
                    label = { Text("Social", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_friends")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.FINANCE_GOALS ||
                        activeTab == DashboardTab.DELIVERY_DRIVING ||
                        activeTab == DashboardTab.STABILITY_LOUNGE,
                    onClick = { onSelectTab(DashboardTab.FINANCE_GOALS) },
                    icon = { Icon(Icons.Default.Savings, contentDescription = "Wallet & Goals") },
                    label = { Text("Wallet", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    modifier = Modifier.testTag("tab_finance_goals")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.MAINTENANCE_SETTINGS,
                    onClick = { onSelectTab(DashboardTab.MAINTENANCE_SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
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
            // 1. Compact Top Dashboard Bar (Search toggle, Install/Publish, Live Time/Weather, Settings, Profile, Sign Out)
            DashboardTopWeatherSearchHeader(
                locationName = profile.location,
                onLocationUpdated = onLocationChange,
                universalSearchQuery = universalSearchQuery,
                onSearchQueryChange = { universalSearchQuery = it },
                selectedSearchEngine = preferredSearchEngine,
                onSelectSearchEngine = onSelectSearchEngine,
                onExecuteSearch = { executeUniversalSearch(it) },
                onNavigateToEditProfile = onNavigateToEditProfile,
                onOpenPhoneSystemSettings = {
                    onSelectTab(DashboardTab.MAINTENANCE_SETTINGS)
                },
                onSignOut = onSignOut,
                onNavigateToFriendsDiscovery = {
                    onSelectTab(DashboardTab.FRIENDS)
                },
                onOpenInstallPublishHub = {
                    showInstallPublishSheet = true
                },
                discoveredUsers = visibleDiscoveredUsers,
                communityConnections = visibleCommunityConnections,
                onSearchCommunityMembers = onSearchUsers,
                onSendFriendRequest = onSendFriendRequest,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // 1A. Clean Page Navigator Strip + 1-Tap "All Pages" Grid Drawer
            DashboardQuickSwipeTabStrip(
                pagerTabs = pagerTabs,
                currentPage = pagerState.currentPage,
                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                activeAccentHex = signUpForm.themeColorHex,
                onSelectTab = onSelectTab,
                onOpenAllPagesMenu = { showAllPagesSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )

            val coroutineScope = rememberCoroutineScope()
            val accentColor = parseThemeHex(signUpForm.themeColorHex)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("dashboard_horizontal_pager"),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val tab = pagerTabs[pageIndex]
                val pageScrollState = rememberScrollState()
                val rawPageOffset = (
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                ).coerceIn(-1f, 1f)
                val absOffset = rawPageOffset.absoluteValue
                val slideScale = 0.94f + (1f - absOffset) * 0.06f
                val slideAlpha = 0.58f + (1f - absOffset) * 0.42f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = -rawPageOffset * size.width * 0.12f
                            scaleX = slideScale
                            scaleY = slideScale
                            alpha = slideAlpha
                            rotationY = -rawPageOffset * 4.5f
                            cameraDistance = 14f * density
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(pageScrollState)
                            .padding(start = 16.dp, end = 24.dp, top = 4.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val sectionSpacing = when (platformLayoutMode) {
                            "Compact" -> 8.dp
                            "Expanded" -> 18.dp
                            else -> 12.dp
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp),
                            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
                        ) {
                        // Clean Page Header Bar on Secondary Pages for Instant Orientation & 1-Tap Return Home
                        if (tab != DashboardTab.HUB_3D) {
                            val pageMeta = when (tab) {
                                DashboardTab.STUDIO_ARCADE -> "🎬 Reels, Photo Studio & Arcade" to "Watch & make video reels, AI photo filters & games"
                                DashboardTab.THEME_PROFILE -> "🎨 Profile & 3D Themes" to "3D avatars, custom themes & profile customization"
                                DashboardTab.FRIENDS -> "👥 Friends & Discovery" to "Find members, manage requests & sync contacts"
                                DashboardTab.MESSAGING -> "💬 Messages & Chat" to "Direct conversations & icebreakers"
                                DashboardTab.VIDEO_CHAT -> "📹 Video Chat & Calls" to "Live video rooms & face-to-face calls"
                                DashboardTab.FINANCE_GOALS -> "💰 Wallet & Finance" to "Digital wallet, EqualCoins & savings goals"
                                DashboardTab.RESOURCES -> "🎓 School, Jobs & Aid" to "Education portals, career tracks, 211 & VA aid"
                                DashboardTab.AI_TOOLS -> "🤖 AI Tools & Tutor" to "Study coach, flashcards, voice & AI studio"
                                DashboardTab.NEWS_SPORTS -> "📰 News, Sports & Horoscopes" to "Daily headlines, live scores & zodiac insights"
                                DashboardTab.DELIVERY_DRIVING -> "🚗 Rideshare & Delivery" to "Uber, Lyft, DoorDash & food ordering portals"
                                DashboardTab.STABILITY_LOUNGE -> "🛍️ Market, Growth & 18+" to "Marketplace, growth charts, donations & 18+ lounge"
                                DashboardTab.CODE_OF_CONDUCT -> "🛡️ Safety & Community" to "Code of conduct, moderation & community ideas"
                                DashboardTab.MAINTENANCE_SETTINGS -> "⚙️ Settings & Downloads" to "App colors, dark mode, account & APK export"
                                DashboardTab.HUB_3D -> "🏠 Home" to ""
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, parseThemeHex(signUpForm.themeColorHex).copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("active_page_header_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pageMeta.first,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = pageMeta.second,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { onSelectTab(DashboardTab.HUB_3D) },
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.testTag("back_to_home_button_${tab.name.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back to Home",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Home", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }

                        when (tab) {
                            DashboardTab.HUB_3D -> {
                                var homeSection by rememberSaveable { mutableStateOf("STAGE") }

                                // Compact Single-Row Welcome & Direct Install/Pages Bar
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("home_compact_welcome_bar")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${profile.displayName} • ${profile.role.uppercase()} Stage",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Tap links below to jump to any page or view",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { showAllPagesSheet = true },
                                                shape = RoundedCornerShape(50),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                border = BorderStroke(1.dp, HoloCyan),
                                                modifier = Modifier
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("home_open_all_pages_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Apps,
                                                    contentDescription = "All Pages",
                                                    tint = HoloCyan,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pages", style = MaterialTheme.typography.labelSmall, color = HoloCyan, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { showInstallPublishSheet = true },
                                                shape = RoundedCornerShape(50),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = CyberEmerald,
                                                    contentColor = Color.Black
                                                ),
                                                modifier = Modifier
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("home_quick_install_publish_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Download,
                                                    contentDescription = "Install & Publish",
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Install", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }
                                    }
                                }

                                // Clean Single-Focus Home Link Bar (renders 1 view at a time for minimal scrolling)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "STAGE" to "🎛️ My Stage",
                                        "FEED" to "🌐 Social Wall",
                                        "PAGES" to "🔗 All Links",
                                        "SETUP" to "🧭 3-Step Setup",
                                        "INSTALL" to "📲 Publish & Install"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = homeSection == key,
                                            onClick = { homeSection = key },
                                            label = {
                                                Text(
                                                    text = label,
                                                    fontWeight = if (homeSection == key) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            modifier = Modifier.testTag("home_section_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (homeSection == "STAGE") {
                                    PersonaDashboardComponent(
                                        currentRole = profile.role,
                                        currentDisplayName = profile.displayName,
                                        personaStageDataMap = personaStageDataMap,
                                        isSaving = isPersonaStageSaving,
                                        statusBanner = personaStageStatusBanner,
                                        isAuthenticatedForCloud = isAuthenticatedForCloud,
                                        onSelectLifeStage = { stageId, defaultAge, accentHex ->
                                            onSwitchPersonaQuick(stageId, defaultAge)
                                            onThemeColorSelect(accentHex)
                                        },
                                        onSavePersonaStageData = onSavePersonaStageData,
                                        onNavigateToTab = onSelectTab,
                                        onShareToWall = onCreatePost
                                    )
                                }

                                if (homeSection == "PAGES") {
                                    WereAllEquals3DHeroShowcaseBanner()

                                    Card(
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("home_explore_pages_directory")
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "🔗 Direct Page Links (Tap Any Button)",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                listOf(
                                                    DashboardTab.STUDIO_ARCADE to "🎬 Reels & Studio",
                                                    DashboardTab.THEME_PROFILE to "🎨 Profile & 3D",
                                                    DashboardTab.FRIENDS to "👥 Friends",
                                                    DashboardTab.MESSAGING to "💬 Messages",
                                                    DashboardTab.VIDEO_CHAT to "📹 Video Calls",
                                                    DashboardTab.FINANCE_GOALS to "💰 Wallet",
                                                    DashboardTab.RESOURCES to "🎓 Resources",
                                                    DashboardTab.AI_TOOLS to "🤖 AI Tools",
                                                    DashboardTab.NEWS_SPORTS to "📰 News & Sports",
                                                    DashboardTab.DELIVERY_DRIVING to "🚗 Delivery",
                                                    DashboardTab.STABILITY_LOUNGE to "🛍️ Market & 18+",
                                                    DashboardTab.CODE_OF_CONDUCT to "🛡️ Safety",
                                                    DashboardTab.MAINTENANCE_SETTINGS to "⚙️ Settings & APK"
                                                ).forEach { (destTab, destLabel) ->
                                                    OutlinedButton(
                                                        onClick = { onSelectTab(destTab) },
                                                        shape = RoundedCornerShape(50),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
                                                        modifier = Modifier
                                                            .minimumInteractiveComponentSize()
                                                            .testTag("home_dir_link_${destTab.name.lowercase()}")
                                                    ) {
                                                        Text(
                                                            text = destLabel,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                            contentDescription = "Open $destLabel",
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (homeSection == "SETUP") {
                                    PersonaSelectionComponent(
                                        currentRole = profile.role,
                                        currentDisplayName = profile.displayName,
                                        currentBio = profile.bio,
                                        isDarkMode = isDarkMode,
                                        onApplyCustomizedExperience = { roleKey, defaultAge, accentHex, visualStyleId, selectedTopics ->
                                            onSwitchPersonaQuick(roleKey, defaultAge)
                                            onThemeColorSelect(accentHex)
                                            onThemeVisualStyleSelect(visualStyleId)
                                            selectedTopics.forEach { onToggleLearnedInterest(it) }
                                            val normalizedId = normalizePersonaStageId(roleKey)
                                            val existingStage = personaStageDataMap[normalizedId]
                                                ?: buildDefaultPersonaStageData(normalizedId)
                                            onSavePersonaStageData(
                                                existingStage.copy(
                                                    stageId = normalizedId,
                                                    pinnedTopics = selectedTopics.distinct().take(12)
                                                )
                                            )
                                            homeSection = "STAGE"
                                        },
                                        onSharePersonaChoiceToFeed = onCreatePost
                                    )
                                }

                                if (homeSection == "INSTALL") {
                                    PlatformReleaseAndDownloadHubCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        onTriggerFullCloudSync = onSaveProfileFromForm
                                    )
                                    DownloadAndInstallAppCenterCard()
                                }

                                if (homeSection == "FEED") {
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
                                }
                            }

                            DashboardTab.STUDIO_ARCADE -> {
                                var studioSection by rememberSaveable { mutableStateOf("REELS") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "REELS" to "🎬 Video Reels",
                                        "TEXT_TO_VIDEO" to "🎥 Text-to-Video",
                                        "PHOTO" to "📸 Photo Studio",
                                        "ARCADE" to "🎮 Arcade & Draw",
                                        "ALL" to "✨ Show All"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = studioSection == key,
                                            onClick = { studioSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("studio_filter_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (studioSection == "ALL" || studioSection == "REELS") {
                                    VideoReelsComponent(
                                        currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentPersonaRole = profile.role,
                                        onShareReelOrCommentToWall = { category, title, content ->
                                            onCreatePost(category, title, content)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        },
                                        onReportReelOrComment = { targetType, targetId, targetTitle, authorName ->
                                            onReportContent(targetId, targetType, "Reported Reel ($targetTitle)", "Author: $authorName")
                                        }
                                    )
                                }

                                if (studioSection == "ALL" || studioSection == "TEXT_TO_VIDEO") {
                                    TextToVideoGeneratorStudioCard(
                                        currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentPersonaRole = profile.role,
                                        onPublishGeneratedClipToReels = { title, description, category, accentHex ->
                                            PublishedVideoReelsStore.publishAiGeneratedReel(
                                                title = title,
                                                description = description,
                                                category = category,
                                                creatorName = profile.displayName.ifBlank { "Jordan Vance" },
                                                accentHex = accentHex
                                            )
                                            studioSection = "REELS"
                                        },
                                        onShareClipToCommunityWall = { category, title, content ->
                                            onCreatePost(category, title, content)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        }
                                    )
                                }

                                if (studioSection == "ALL" || studioSection == "PHOTO") {
                                    PhotoStudioComponent(
                                        displayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentProfileBitmap = customProfileBitmap,
                                        selected3DAvatarResId = selected3DAvatarResId,
                                        onApplyToProfile = onUpdateProfileBitmap,
                                        onShareCreationToFeed = { category, title, content ->
                                            onCreatePost(category, title, content)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        }
                                    )

                                    CreativePhotoStudioCard(
                                        displayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentProfileBitmap = customProfileBitmap,
                                        selected3DAvatarResId = selected3DAvatarResId,
                                        userPersona = profile.role,
                                        onApplyFilteredBitmapToProfile = onUpdateProfileBitmap,
                                        onLogStudioInteraction = { detail ->
                                            onLogInteraction("PhotoStudio", detail, "Studio")
                                        },
                                        onPublishPhotoToSocialPost = { title, caption, category ->
                                            onCreatePost(category, title, caption)
                                        }
                                    )
                                }

                                if (studioSection == "ALL" || studioSection == "ARCADE") {
                                    ProfileYouTubeReelsAndGamesHubCard(
                                        displayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentRole = profile.role,
                                        currentAge = profile.age,
                                        onSwitchToTeenOrKidProfile = onSwitchPersonaQuick,
                                        onPublishVideoOrReelToDashboard = { title, body ->
                                            onCreatePost("Studio Art", title, body)
                                        },
                                        onLogHubInteraction = onLogInteraction,
                                        onOpenFullDashboardFeed = { onSelectTab(DashboardTab.HUB_3D) }
                                    )

                                    StudioAndArcadeTab(
                                        highScore = profile.arcadeHighScore,
                                        onShareStudioCreation = { title, body ->
                                            onCreatePost("Studio Art", title, body)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        },
                                        onNewHighScore = onNewArcadeHighScore
                                    )
                                }
                            }

                            DashboardTab.THEME_PROFILE -> {
                                var profileSection by rememberSaveable { mutableStateOf("3D_CUSTOMIZER") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "3D_CUSTOMIZER" to "🧊 3D Customizer",
                                        "AVATARS" to "🎭 Avatars & Personas",
                                        "EDITOR" to "📝 Full Profile & Coins",
                                        "DATA_EXPORT" to "📥 Download Data",
                                        "STUDIO_3D" to "🌌 3D Scene"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = profileSection == key,
                                            onClick = { profileSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("profile_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (profileSection == "DATA_EXPORT") {
                                    ProfileDownloadDataCard(exportPayload = exportPayload)
                                }

                                val active3DConfig = profile3DConfigs.firstOrNull { it.id == "active" }
                                    ?: profile3DConfigs.firstOrNull()
                                    ?: Profile3DConfig()

                                if (profileSection == "3D_CUSTOMIZER") {
                                    Profile3DCustomizerComponent(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        activeConfig = active3DConfig,
                                        savedConfigs = profile3DConfigs,
                                        isAuthenticated = isAuthenticatedForCloud,
                                        isSavingToCloud = isProfile3DSaving,
                                        cloudStatusMessage = profile3DStatusBanner,
                                        onSaveConfigToFirestore = onSaveProfile3DConfig,
                                        onDeleteSavedConfig = onDeleteProfile3DConfig,
                                        onApplyPrimaryThemeHex = onThemeColorSelect,
                                        onApplyAvatarBadgeEmoji = onUpdateAvatarBadge,
                                        onApplyCustomizedAvatarBitmap = onUpdateProfileBitmap,
                                        onShareLookToWall = { headline, body ->
                                            onCreatePost("Studio Art", headline, body)
                                        }
                                    )
                                }

                                if (profileSection == "AVATARS") {
                                    Persona3DAvatarSelectionCard(
                                        currentPersonaRole = profile.role,
                                        selected3DAvatarResId = selected3DAvatarResId,
                                        onSelect3DAvatarPreset = onSelect3DAvatarPreset,
                                        onSelectPersonaStage = { stage -> onRoleSelect(stage.lowercase()) },
                                        onEquipBadgeEmoji = onUpdateAvatarBadge,
                                        onShareCharacterToWall = { title, body ->
                                            onCreatePost("Update", title, body)
                                        },
                                        currentVisualThemeId = currentVisualStyleId,
                                        onSelectVisualTheme = { styleId, primaryHex ->
                                            onThemeVisualStyleSelect(styleId)
                                            onThemeColorSelect(primaryHex)
                                        },
                                        onSaveAvatarToFirestore = onSaveProfileFromForm
                                    )

                                    PersonaProfileSystemCard(
                                        currentRole = profile.role,
                                        currentAgeText = signUpForm.ageText.ifBlank { profile.age.toString() },
                                        currentVisualStyleId = currentVisualStyleId,
                                        currentThemeHex = signUpForm.themeColorHex,
                                        selectedInterests = learnedInterests,
                                        onSelectPersona = { roleKey, defaultAge, defaultHex, defaultStyleId ->
                                            onSwitchPersonaQuick(roleKey, defaultAge.toLongOrNull() ?: 26L)
                                            onThemeColorSelect(defaultHex)
                                            onThemeVisualStyleSelect(defaultStyleId)
                                        },
                                        onApplyPersonaTheme = { visualStyleId, colorHex, badgeEmoji ->
                                            onThemeVisualStyleSelect(visualStyleId)
                                            onThemeColorSelect(colorHex)
                                            onUpdateAvatarBadge(badgeEmoji)
                                        },
                                        onPinPersonaInterest = onToggleLearnedInterest,
                                        onSharePersonaActionToWall = { title, content ->
                                            onCreatePost("Milestone", title, content)
                                        }
                                    )
                                }

                                if (profileSection == "EDITOR") {
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

                                if (profileSection == "STUDIO_3D") {
                                    ThreeJsStudio3DSceneCard(
                                        onShareSceneToWall = { title, body ->
                                            onCreatePost("Studio Art", title, body)
                                        }
                                    )
                                }
                            }

                            DashboardTab.FRIENDS -> {
                                FriendsAndDiscoveryScreen(
                                    friendsState = friendsState,
                                    incomingRequestsState = incomingRequestsState,
                                    discoveredUsers = visibleDiscoveredUsers,
                                    myPhoneNumber = profile.phoneNumber,
                                    myUserProfile = profile,
                                    onUpdatePhoneNumber = onUpdatePhoneNumber,
                                    onSyncContacts = onSyncContacts,
                                    onSearchUsers = onSearchUsers,
                                    onSendFriendRequest = onSendFriendRequest,
                                    onAcceptRequest = onAcceptRequest,
                                    onDeclineRequest = onDeclineRequest,
                                    onShowReportDialog = onShowReportDialog,
                                    blockedUsers = blockedUsers,
                                    onBlockUser = onBlockUser,
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
                                var financeSection by rememberSaveable { mutableStateOf("WALLET") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "WALLET" to "💳 Wallet & Coins",
                                        "VAULT" to "🏦 Savings Vault",
                                        "LITERACY" to "📊 Literacy & Tips"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = financeSection == key,
                                            onClick = { financeSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("finance_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (financeSection == "WALLET") {
                                    DigitalWalletCard(
                                        profile = profile,
                                        budgetState = budgetState,
                                        onAddWalletTransaction = onAddBudgetItem
                                    )

                                    VirtualCurrencySystemCard(
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
                                }

                                if (financeSection == "VAULT") {
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
                                }

                                if (financeSection == "LITERACY") {
                                    FinancialLiteracyTrackerCard(
                                        budgetState = budgetState,
                                        onAddBudgetItem = onAddBudgetItem,
                                        onDeleteBudgetItem = onDeleteBudgetItem,
                                        onShareSummaryToWall = { headline, body ->
                                            onCreatePost("Milestone", headline, body)
                                        },
                                        userRole = signUpForm.role,
                                        userAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                        equalCoinsBalance = virtualCurrencyBalance
                                    )

                                    FinancialTipOfTheDayCard(
                                        userRole = signUpForm.role,
                                        userAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                        onApplyTipSavingsAction = { title, amountDollars ->
                                            onAddBudgetItem(title, "Save", amountDollars)
                                        },
                                        onShareTipToWall = { headline, body ->
                                            onCreatePost("Milestone", headline, body)
                                        }
                                    )
                                }
                            }

                            DashboardTab.RESOURCES -> {
                                var resourceSection by rememberSaveable { mutableStateOf("FINDER") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "FINDER" to "🧭 Aid & 211 Finder",
                                        "CAREERS" to "🎓 School & Careers",
                                        "GUIDES" to "📚 Community Guides"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = resourceSection == key,
                                            onClick = { resourceSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("resources_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (resourceSection == "FINDER") {
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
                                }

                                if (resourceSection == "GUIDES") {
                                    ResourceHubCard(
                                        currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        onShareResourceToWall = { title, body ->
                                            onCreatePost("Resource Guide", title, body)
                                        }
                                    )
                                }

                                if (resourceSection == "CAREERS") {
                                    StudentSchoolAndCareersPortalCard(
                                        currentRole = profile.role,
                                        currentAge = profile.age,
                                        onSelectStudentAccount = { onSwitchPersonaQuick("student", 19L) },
                                        onOpenExternalUrl = { launchWebUrl(it) },
                                        onOpenAiStudyTutor = { onSelectTab(DashboardTab.AI_TOOLS) },
                                        onShareApplicationGoalToWall = { headline, body ->
                                            onCreatePost("Resource Guide", headline, body)
                                        },
                                        careerMilestonesState = careerMilestonesState,
                                        onAddCareerMilestone = onAddCareerMilestone,
                                        onUpdateCareerMilestoneStatus = onUpdateCareerMilestoneStatus,
                                        onDeleteCareerMilestone = onDeleteCareerMilestone
                                    )
                                }
                            }

                            DashboardTab.AI_TOOLS -> {
                                var aiSection by rememberSaveable { mutableStateOf("STUDY") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "STUDY" to "📚 AI Study Coach",
                                        "ADAPTIVE" to "🧠 Adaptive Mind",
                                        "VISION" to "🎨 AI Image & Voice"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = aiSection == key,
                                            onClick = { aiSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("ai_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (aiSection == "STUDY") {
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
                                }

                                if (aiSection == "ADAPTIVE") {
                                    AdaptiveUserLearningAiBotAndToolsCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        userRole = signUpForm.role.ifBlank { profile.role },
                                        userAge = signUpForm.ageText.toLongOrNull() ?: profile.age,
                                        userLocation = profile.location.ifBlank { signUpForm.location },
                                        learnedSignals = learnedSignals,
                                        learnedInterests = learnedInterests,
                                        learnedLifestyle = learnedLifestyle,
                                        learnedSchedule = learnedSchedule,
                                        learnedResearch = learnedResearch,
                                        aiStudyState = aiStudyState,
                                        aiFlashcardsState = aiFlashcardsState,
                                        aiAdaptivePlanState = aiAdaptivePlanState,
                                        onRecordLearnedSignal = onRecordLearnedSignal,
                                        onAddCustomLearningSignal = onAddCustomLearningSignal,
                                        onToggleLearnedInterest = onToggleLearnedInterest,
                                        onToggleLearnedResearch = onToggleLearnedResearch,
                                        onAskAiStudyTutor = onAskAiStudyTutor,
                                        onGenerateStudyFlashcards = onGenerateStudyFlashcards,
                                        onGenerateAdaptiveLearningPlan = onGenerateAdaptiveLearningPlan,
                                        onShareAiResearchToWall = { headline, body ->
                                            onCreatePost("Resource Guide", headline, body)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        }
                                    )
                                }

                                if (aiSection == "VISION") {
                                    AiImageAndVoiceConversationStudioCard(
                                        currentProfileBitmap = customProfileBitmap,
                                        aiProfileImageState = aiProfileImageState,
                                        onGenerateNewImageFromPrompt = onGenerateAiProfilePhoto,
                                        onApplyGeneratedBitmapToProfile = onUpdateProfileBitmap,
                                        onShareCreationToWall = { title, body ->
                                            onCreatePost("Studio Art", title, body)
                                        }
                                    )
                                }
                            }

                            DashboardTab.NEWS_SPORTS -> {
                                var newsSection by rememberSaveable { mutableStateOf("HUB") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "HUB" to "📰 News & Sports",
                                        "SUGGESTIONS" to "✨ Daily Suggestions",
                                        "HOROSCOPES" to "♈ Horoscopes"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = newsSection == key,
                                            onClick = { newsSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("news_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (newsSection == "SUGGESTIONS") {
                                    EveryLoginSuggestionsCard(
                                        displayName = profile.displayName.substringBefore(" "),
                                        suggestions = everyLoginSuggestions,
                                        onRefreshSuggestions = onRefreshLoginSuggestions,
                                        onOpenNewsAndSportsTab = { onSelectTab(DashboardTab.NEWS_SPORTS) }
                                    )
                                }

                                if (newsSection == "HUB") {
                                    RealNewsAndSportsHubCard(
                                        onShareToWall = { headline, body ->
                                            onCreatePost("News & Sports Update", headline, body)
                                            onSelectTab(DashboardTab.HUB_3D)
                                        }
                                    )
                                }

                                if (newsSection == "HOROSCOPES") {
                                    DailyHoroscopesCosmicCard(
                                        onApplyCosmicTheme = { visualStyleId, colorHex, zodiacEmoji ->
                                            onThemeVisualStyleSelect(visualStyleId)
                                            onThemeColorSelect(colorHex)
                                            onUpdateAvatarBadge(zodiacEmoji)
                                        },
                                        onShareHoroscopeToWall = { headline, body ->
                                            onCreatePost("Daily Affirmation", headline, body)
                                        }
                                    )
                                }
                            }

                            DashboardTab.DELIVERY_DRIVING -> {
                                DeliveryAndOrderingPortalScreen(
                                    profile = profile,
                                    onAddDeliveryExpenseToBudget = onAddBudgetItem,
                                    onLogDeliveryInteraction = onLogInteraction,
                                    onShareMealOrderToWall = { headline, body ->
                                        onCreatePost("Update", headline, body)
                                        onSelectTab(DashboardTab.HUB_3D)
                                    }
                                )
                            }

                            DashboardTab.STABILITY_LOUNGE -> {
                                var loungeSection by rememberSaveable { mutableStateOf("GROWTH") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "GROWTH" to "📈 Market & Growth",
                                        "DATING" to "💖 18+ Connections",
                                        "SIMULATOR" to "📊 Asset Simulator",
                                        "DONATE" to "🤝 Donations"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = loungeSection == key,
                                            onClick = { loungeSection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("lounge_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (loungeSection == "GROWTH") {
                                    StabilityAndLoungeTab(
                                        profile = profile,
                                        budgetState = budgetState,
                                        interactionsState = userInteractionsState,
                                        postsState = postsState,
                                        connections = visibleCommunityConnections,
                                        onLogInteraction = onLogInteraction,
                                        onAddBudgetItem = onAddBudgetItem,
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
                                        }
                                    )
                                }

                                if (loungeSection == "DATING") {
                                    AdultProfileDatingHubCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        userAge = profile.age,
                                        userRole = profile.role,
                                        userLocation = profile.location,
                                        onSwitchToAdultProfile = { onSwitchPersonaQuick("adult", 25L) },
                                        onStartVideoDateCall = { onSelectTab(DashboardTab.VIDEO_CHAT) },
                                        onOpenMessagingWithMatch = { onSelectTab(DashboardTab.MESSAGING) },
                                        onShareMatchMilestoneToWall = { headline, body ->
                                            onCreatePost("Milestone", headline, body)
                                        }
                                    )
                                }

                                if (loungeSection == "SIMULATOR") {
                                    DigitalAssetMarketSimulatorCard(
                                        equalCoinsBalance = virtualCurrencyBalance,
                                        onLogSimulatedBudgetPractice = { title, category, amountDollars ->
                                            onAddBudgetItem(title, category, amountDollars)
                                        },
                                        onShareSimulationToWall = { headline, body ->
                                            onCreatePost("Milestone", headline, body)
                                        }
                                    )
                                }

                                if (loungeSection == "DONATE") {
                                    WereEqualDonationAndCreatorSupportCard(
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
                                }
                            }

                            DashboardTab.CODE_OF_CONDUCT -> {
                                var safetySection by rememberSaveable { mutableStateOf("RULES") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "RULES" to "🛡️ Rules & Ideas",
                                        "MODERATION" to "🚩 Moderation",
                                        "COMMUNITY" to "🤝 Life-Stage Hub",
                                        "LEADERBOARD" to "🏆 Leaderboard"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = safetySection == key,
                                            onClick = { safetySection = key },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("safety_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                if (safetySection == "RULES") {
                                    CodeOfConductScreen(
                                        userIdeas = userIdeas,
                                        aiIdeaFeedbackState = aiIdeaFeedbackState,
                                        onSubmitUserIdea = onSubmitUserIdea,
                                        onUpvoteUserIdea = onUpvoteUserIdea,
                                        onExpandIdeaWithAi = onExpandIdeaWithAi
                                    )
                                }

                                if (safetySection == "MODERATION") {
                                    ContentModerationSystemCard(
                                        onSubmitFlagToBackend = onReportContent
                                    )
                                }

                                if (safetySection == "COMMUNITY") {
                                    CommunityLifeStageHubCard(
                                        postsState = postsState,
                                        currentUserDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        currentPersonaRole = profile.role,
                                        onCreateFirestoreTipOrResource = { category, title, content ->
                                            onCreatePost(category, title, content)
                                        },
                                        onUpvoteFirestorePost = onLikePost,
                                        onRewardHelpfulVote = onRewardHelpfulPostBonus,
                                        onReportContent = onReportContent
                                    )
                                }

                                if (safetySection == "LEADERBOARD") {
                                    AchievementGamificationLeaderboardCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        userRole = profile.role,
                                        userAvatarEmoji = avatarEmojiBadge,
                                        accentColor = parseThemeHex(signUpForm.themeColorHex),
                                        onEquipBadgeToProfile = onUpdateAvatarBadge,
                                        onAwardEqualCoinsBonus = { onRewardHelpfulPostBonus() },
                                        onShareAchievementToWall = { title, body ->
                                            onCreatePost("Milestone", title, body)
                                        }
                                    )
                                }
                            }

                            DashboardTab.MAINTENANCE_SETTINGS -> {
                                var settingsSection by rememberSaveable { mutableStateOf("ALL") }

                                if (!settingsStatusBanner.isNullOrBlank()) {
                                    Surface(
                                        color = CyberEmerald.copy(alpha = 0.18f),
                                        border = BorderStroke(1.2.dp, CyberEmerald),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("settings_status_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = CyberEmerald,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = settingsStatusBanner,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            IconButton(
                                                onClick = onClearSettingsStatusBanner,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Dismiss status",
                                                    modifier = Modifier.size(16.dp)
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
                                    listOf(
                                        "ALL" to "🎛️ All Settings",
                                        "PLATFORM" to "🌐 Platform",
                                        "COLORS" to "🎨 Colors & Theme",
                                        "PHONE_WALLET" to "📱 Phone & Wallet",
                                        "CONDUCT" to "🛡️ Code of Conduct",
                                        "BLOCKING" to "🚫 Blocking (${blockedUsers.size})",
                                        "ABOUT_CREATOR" to "ℹ️ About & Creator",
                                        "DELETE_ACCOUNT" to "⚠️ Delete Account"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = settingsSection == key,
                                            onClick = { settingsSection = key },
                                            label = {
                                                Text(
                                                    text = label,
                                                    fontWeight = if (settingsSection == key) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            modifier = Modifier.testTag("settings_link_${key.lowercase()}")
                                        )
                                    }
                                }

                                // 1. Platform Options & Switcher
                                if (settingsSection == "ALL" || settingsSection == "PLATFORM") {
                                    SettingsPlatformSwitcherCard(
                                        currentRole = profile.role,
                                        currentVisualStyleId = currentVisualStyleId,
                                        currentSearchEngine = preferredSearchEngine,
                                        currentMusicPlatform = signUpForm.musicPlatform,
                                        currentLayoutMode = platformLayoutMode,
                                        notificationsEnabled = platformNotificationsEnabled,
                                        showMatureLoungeEnabled = showMatureLoungeEnabled,
                                        onSelectLifeStagePlatform = { stageId, defaultAge, accentHex ->
                                            onSwitchPersonaQuick(stageId, defaultAge)
                                            onThemeColorSelect(accentHex)
                                        },
                                        onSelectVisualStyle = onThemeVisualStyleSelect,
                                        onSelectSearchEngine = onSelectSearchEngine,
                                        onSelectMusicPlatform = { musicPlatform ->
                                            onMusicChange(signUpForm.musicTrackTitle, musicPlatform)
                                        },
                                        onSelectLayoutMode = onSelectPlatformLayoutMode,
                                        onToggleNotifications = onTogglePlatformNotifications,
                                        onToggleMatureLounge = onToggleMatureLounge,
                                        onSavePlatformChanges = onSaveProfileFromForm
                                    )
                                }

                                // 2. All Colors, 360° Color Wheel, Transparency & Dark/Light Mode
                                if (settingsSection == "ALL" || settingsSection == "COLORS") {
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
                                        onNavigateToCodeOfConduct = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) },
                                        exportPayload = null,
                                        forcedSectionLink = MaintenanceSectionLink.SETTING_CUSTOMIZATION,
                                        showHeaderAndPullUp = false
                                    )

                                    SystemWideDarkModeControlCard(
                                        isDarkMode = isDarkMode,
                                        onSetDarkMode = onSetDarkMode,
                                        backgroundTransparency = backgroundTransparency,
                                        onTransparencyChange = onTransparencyChange,
                                        currentThemeHex = signUpForm.themeColorHex,
                                        onSelectThemeColor = onThemeColorSelect
                                    )
                                }

                                // 3. Phone & OS Sync + Digital Wallet Setup
                                if (settingsSection == "ALL" || settingsSection == "PHONE_WALLET") {
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
                                        onNavigateToCodeOfConduct = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) },
                                        exportPayload = null,
                                        forcedSectionLink = MaintenanceSectionLink.PHONE_EMAIL_SETTINGS,
                                        showHeaderAndPullUp = false
                                    )

                                    SettingsPhoneWalletSetupCard(
                                        profile = profile,
                                        virtualCurrencyBalance = virtualCurrencyBalance,
                                        selectedWalletTier = selectedWalletTier,
                                        isVaultUnlocked = isFinancialVaultUnlocked,
                                        onSelectWalletTier = onSelectWalletTier,
                                        onUpdateSavingsGoal = onUpdateSavingsGoal,
                                        onQuickWalletDeposit = onAddBudgetItem,
                                        onUnlockVault = onUnlockFinancialVault,
                                        onLockVault = onLockFinancialVault,
                                        onOpenFullWalletTab = { onSelectTab(DashboardTab.FINANCE_GOALS) }
                                    )
                                }

                                // 4. Platform Code of Conduct + Download Data (JSON)
                                if (settingsSection == "ALL" || settingsSection == "CONDUCT") {
                                    SettingsCodeOfConductCard(
                                        pledgeAccepted = signUpForm.pledgeAccepted,
                                        onPledgeChange = { accepted ->
                                            onPledgeChange(accepted)
                                            onSaveProfileFromForm()
                                        },
                                        onOpenFullCodeOfConductTab = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) }
                                    )

                                    ProfileDownloadDataCard(exportPayload = exportPayload)
                                }

                                // 5. Blocking Users
                                if (settingsSection == "ALL" || settingsSection == "BLOCKING") {
                                    val friendsList = (friendsState as? UiState.Success)?.data ?: emptyList()
                                    SettingsBlockUsersCard(
                                        blockedUsers = blockedUsers,
                                        friends = friendsList,
                                        communityConnections = visibleCommunityConnections,
                                        onBlockUser = onBlockUser,
                                        onUnblockUser = onUnblockUser
                                    )
                                }

                                // 6. About the Platform + Creator of the Platform Contact + Releases
                                if (settingsSection == "ALL" || settingsSection == "ABOUT_CREATOR") {
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
                                        onNavigateToCodeOfConduct = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) },
                                        exportPayload = null,
                                        forcedSectionLink = MaintenanceSectionLink.ABOUT_PLATFORM,
                                        showHeaderAndPullUp = false
                                    )

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
                                        onNavigateToCodeOfConduct = { onSelectTab(DashboardTab.CODE_OF_CONDUCT) },
                                        exportPayload = null,
                                        forcedSectionLink = MaintenanceSectionLink.CONTACT_US_CREATOR,
                                        showHeaderAndPullUp = false
                                    )

                                    PlatformReleaseAndDownloadHubCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        onTriggerFullCloudSync = onSaveProfileFromForm
                                    )

                                    DownloadAndInstallAppCenterCard()
                                }

                                // 7. Delete Account
                                if (settingsSection == "ALL" || settingsSection == "DELETE_ACCOUNT") {
                                    SettingsDeleteAccountCard(
                                        userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                                        isDeleting = isDeletingAccount,
                                        onJumpToDownloadData = { settingsSection = "CONDUCT" },
                                        onConfirmDeleteAccount = onDeleteAccountPermanently
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Visible Interactive Vertical Scrollbar on every page
                DashboardVerticalScrollbar(
                    scrollState = pageScrollState,
                    accentColor = accentColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(end = 4.dp, top = 6.dp, bottom = 6.dp)
                        .testTag("dashboard_vertical_scrollbar_${tab.name.lowercase()}")
                )

                // Quick Jump-to-Top Link Button when scrolled down
                androidx.compose.animation.AnimatedVisibility(
                    visible = pageScrollState.value > 220,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = accentColor.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                coroutineScope.launch {
                                    pageScrollState.animateScrollTo(0)
                                }
                            }
                            .testTag("dashboard_scroll_to_top_btn_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Scroll back to top",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Top",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
                }
            }
        }
    }

    // All-Pages Quick Navigator Bottom Sheet (1-tap jump to any of the 14 platform pages)
    if (showAllPagesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAllPagesSheet = false },
            sheetState = utilitySheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "All Platform Pages",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Tap any page below to open it right away",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            showAllPagesSheet = false
                            showInstallPublishSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("all_pages_sheet_install_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publish / APK", fontWeight = FontWeight.ExtraBold)
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pagerTabs.forEach { destTab ->
                        val isCurrent = destTab == activeTab
                        val label = when (destTab) {
                            DashboardTab.HUB_3D -> "🏠 Home"
                            DashboardTab.STUDIO_ARCADE -> "🎬 Reels & Studio"
                            DashboardTab.THEME_PROFILE -> "🎨 Profile & 3D"
                            DashboardTab.FRIENDS -> "👥 Friends"
                            DashboardTab.MESSAGING -> "💬 Messages"
                            DashboardTab.VIDEO_CHAT -> "📹 Video Calls"
                            DashboardTab.FINANCE_GOALS -> "💰 Wallet"
                            DashboardTab.RESOURCES -> "🎓 School & Aid"
                            DashboardTab.AI_TOOLS -> "🤖 AI Tools"
                            DashboardTab.NEWS_SPORTS -> "📰 News & Sports"
                            DashboardTab.DELIVERY_DRIVING -> "🚗 Delivery"
                            DashboardTab.STABILITY_LOUNGE -> "🛍️ Market & 18+"
                            DashboardTab.CODE_OF_CONDUCT -> "🛡️ Safety"
                            DashboardTab.MAINTENANCE_SETTINGS -> "⚙️ Settings & APK"
                        }
                        OutlinedButton(
                            onClick = {
                                showAllPagesSheet = false
                                onSelectTab(destTab)
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) HoloCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("all_pages_sheet_link_${destTab.name.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isCurrent) HoloCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dedicated Publish, Download & Install Modal Sheet (accessible in 1 tap from any page)
    if (showInstallPublishSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInstallPublishSheet = false },
            sheetState = utilitySheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlatformReleaseAndDownloadHubCard(
                    userDisplayName = profile.displayName.ifBlank { "Jordan Vance" },
                    onTriggerFullCloudSync = onSaveProfileFromForm
                )
                DownloadAndInstallAppCenterCard()
                Spacer(modifier = Modifier.height(16.dp))
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
    var hubSubView by rememberSaveable { mutableStateOf("DASHBOARD_POST_FEED") }
    var showInlineComposer by rememberSaveable { mutableStateOf(false) }
    var showAllWallPosts by rememberSaveable { mutableStateOf(false) }
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

    // Compact Home Feed & Link Buttons (eliminates endless vertical scroll)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "DASHBOARD_POST_FEED" to "🌐 Social Feed",
            "AI_INSIGHTS" to "✨ AI Insights",
            "STORIES" to "📖 Stories",
            "MY_PROFILE_PAGE" to "👤 Quick 3D Profile",
            "MUSIC_SPORTS" to "🎵 Music & Sports",
            "AI_MIND" to "🤖 Learning Mind"
        ).forEach { (key, label) ->
            FilterChip(
                selected = hubSubView == key,
                onClick = { hubSubView = key },
                label = { Text(label, fontWeight = if (hubSubView == key) FontWeight.ExtraBold else FontWeight.Medium) },
                modifier = Modifier.testTag("home_feed_subview_${key.lowercase()}")
            )
        }
    }

    if (hubSubView == "AI_INSIGHTS") {
        DailyAffirmationCard(
            userDisplayName = profile.displayName,
            onPinAffirmationToWall = { headline, body ->
                momentumXpBonus += 35
                onCreatePost("Daily Affirmation", headline, "$body • #WereEqual #AllEqual")
                hubSubView = "DASHBOARD_POST_FEED"
            }
        )

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

    // 1. Facebook-Life Style Daily Stories & Highlights Reel (shown via Stories link button)
    if (hubSubView == "STORIES") {
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
    }

    // 2. 3D Profile Card with Material 3 Light/Dark Mode Toggle, Photo Creation Shortcut & All-Platform Music Player
    if (hubSubView == "MY_PROFILE_PAGE") {
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
    }

    // 2B. Every-Login Personalized Suggestions (shown in Quick Profile or Social Feed)
    if (hubSubView == "MY_PROFILE_PAGE") {
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

    // 3. Compact Status Composer Toggle Link Button & 4. Wall Posts Stream
    if (hubSubView == "DASHBOARD_POST_FEED") {
        OutlinedButton(
            onClick = { showInlineComposer = !showInlineComposer },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .testTag("toggle_inline_composer_button")
        ) {
            Icon(
                imageVector = if (showInlineComposer) Icons.Default.KeyboardArrowUp else Icons.Default.EmojiEmotions,
                contentDescription = if (showInlineComposer) "Collapse composer" else "Write a community post",
                tint = SolarAmber,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (showInlineComposer) "Hide Post Composer" else "✍️ Share a Status Update or Life Event...",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (showInlineComposer) {
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
                    text = "What's on your mind, ${profile.displayName.substringBefore(" ")}?",
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
                label = { Text("Headline or Life Event") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_title_input")
            )

            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                label = { Text("Share life updates or helpful guides…") },
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
                        showInlineComposer = false
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
                Text("Publish to Community Wall", fontWeight = FontWeight.Bold)
            }
        }
    }
        }

    // 4. Wall Posts Stream with Facebook-Life Multi-Reactions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Community Wall Feed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "${(postsState as? UiState.Success)?.data?.size ?: 0} posts",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

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
                        text = "No posts yet. Tap 'Share a Status Update' above!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                val visiblePosts = if (showAllWallPosts) postsState.data else postsState.data.take(3)
                visiblePosts.forEach { post ->
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
                if (postsState.data.size > 3) {
                    OutlinedButton(
                        onClick = { showAllWallPosts = !showAllWallPosts },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("show_all_wall_posts_button")
                    ) {
                        Text(
                            text = if (showAllWallPosts) "Show Fewer Posts" else "View All ${postsState.data.size} Community Posts",
                            fontWeight = FontWeight.Bold
                        )
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
    onShareMilestoneToWall: (String, String) -> Unit,
    onShareConnectionGreeting: (CommunityConnectionCard) -> Unit
) {
    val isAdult = profile.age >= 18L

    // Community Marketplace & Business Listings
    CommunityMarketplaceCard(
        userDisplayName = profile.displayName,
        userRole = profile.role,
        onPayWithDigitalWallet = onAddBudgetItem,
        onShareListingToWall = onShareMilestoneToWall
    )

    // Personal Growth Analytics Dashboard (Activity, Mood Trends & Financial Literacy Over Time)
    PersonalGrowthRechartsDashboard(
        profile = profile,
        interactionsState = interactionsState,
        budgetState = budgetState,
        postsState = postsState,
        onLogInteraction = onLogInteraction,
        onAddBudgetItem = onAddBudgetItem,
        onShareGrowthMilestoneToWall = onShareMilestoneToWall
    )

    // Equal-Match Community & Connections Lounge (Strict 18+ Gate)
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
    onOpenAllPagesMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeAccent = parseThemeHex(activeAccentHex)
    val listState = rememberLazyListState()
    LaunchedEffect(currentPage) {
        if (currentPage in pagerTabs.indices) {
            listState.animateScrollToItem(currentPage)
        }
    }

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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = HoloCyan.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onOpenAllPagesMenu() }
                        .testTag("quick_strip_all_pages_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "All Pages Menu",
                            tint = HoloCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Pages",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                LazyRow(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                itemsIndexed(pagerTabs) { index, tab ->
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
                        DashboardTab.HUB_3D -> "🏠 Home"
                        DashboardTab.STUDIO_ARCADE -> "🎬 Reels & Studio"
                        DashboardTab.THEME_PROFILE -> "🎨 Profile"
                        DashboardTab.FRIENDS -> "👥 Friends"
                        DashboardTab.MESSAGING -> "💬 Messages"
                        DashboardTab.VIDEO_CHAT -> "📹 Video"
                        DashboardTab.FINANCE_GOALS -> "💰 Wallet"
                        DashboardTab.RESOURCES -> "🎓 Resources"
                        DashboardTab.AI_TOOLS -> "🤖 AI Tools"
                        DashboardTab.NEWS_SPORTS -> "📰 News & Sports"
                        DashboardTab.DELIVERY_DRIVING -> "🚗 Delivery"
                        DashboardTab.STABILITY_LOUNGE -> "🛍️ Market & 18+"
                        DashboardTab.CODE_OF_CONDUCT -> "🛡️ Safety"
                        DashboardTab.MAINTENANCE_SETTINGS -> "⚙️ Settings"
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

/**
 * Interactive Vertical Scrollbar overlay rendered along the right edge of every dashboard page.
 * Supports proportional thumb sizing, drag-to-scroll, and tap-to-jump without hardcoded screen sizes.
 */
@Composable
fun DashboardVerticalScrollbar(
    scrollState: ScrollState,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    if (scrollState.maxValue <= 0) return

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.width(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val trackHeightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)
        val viewportRatio = (trackHeightPx / (trackHeightPx + scrollState.maxValue.toFloat())).coerceIn(0.12f, 0.72f)
        val minThumbPx = with(density) { 40.dp.toPx() }
        val thumbHeightPx = (trackHeightPx * viewportRatio).coerceAtLeast(minThumbPx).coerceAtMost(trackHeightPx)
        val scrollableTrackPx = (trackHeightPx - thumbHeightPx).coerceAtLeast(1f)
        val fraction = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
        val thumbOffsetPx = fraction * scrollableTrackPx
        val thumbHeightDp = with(density) { thumbHeightPx.toDp() }

        var dragFractionOffset by remember { mutableFloatStateOf(0f) }

        // Background track rail + drag/tap interaction surface
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(14.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f))
                .pointerInput(scrollState, scrollableTrackPx) {
                    detectTapGestures { offset ->
                        val targetFrac = ((offset.y - thumbHeightPx / 2f) / scrollableTrackPx).coerceIn(0f, 1f)
                        val targetScroll = (targetFrac * scrollState.maxValue).roundToInt()
                        coroutineScope.launch { scrollState.animateScrollTo(targetScroll) }
                    }
                }
                .pointerInput(scrollState, scrollableTrackPx) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            dragFractionOffset = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f) * scrollableTrackPx
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            dragFractionOffset = (dragFractionOffset + dragAmount).coerceIn(0f, scrollableTrackPx)
                            val targetScroll = ((dragFractionOffset / scrollableTrackPx) * scrollState.maxValue).roundToInt()
                            coroutineScope.launch { scrollState.scrollTo(targetScroll) }
                        }
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, thumbOffsetPx.roundToInt()) }
                    .width(8.dp)
                    .height(thumbHeightDp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(HoloCyan, accentColor)
                        )
                    )
            )
        }
    }
}



