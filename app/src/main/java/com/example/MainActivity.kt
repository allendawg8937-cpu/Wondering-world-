package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.WonderingWorldRepository
import com.example.ui.AppNavScreen
import com.example.ui.DashboardTab
import com.example.ui.UiState
import com.example.ui.WonderingWorldViewModel
import com.example.ui.auth.AuthManager
import com.example.ui.auth.configureAppCheck
import com.example.ui.components.BackgroundMusicAudioEngine
import com.example.ui.components.GlobalLoadingOverlay
import com.example.ui.components.PullDownNavigationBar
import com.example.ui.components.ThreeDimensionalBackground
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.FriendsAndDiscoveryScreen
import com.example.ui.theme.WonderingWorldTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

import android.content.Intent
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : FragmentActivity() {
    private var appIconLaunchSignal by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        window.setBackgroundDrawableResource(android.R.color.transparent)
        enableEdgeToEdge()
        setContent {
            WonderingWorldRoot(appIconLaunchSignal = appIconLaunchSignal)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Whenever the user taps the app launcher icon or shortcut, pop up the Login & Create Account page
        appIconLaunchSignal++
    }
}

@Composable
fun WonderingWorldRoot(appIconLaunchSignal: Int = 0) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var currentUser by remember {
        mutableStateOf(
            try {
                Firebase.auth.currentUser
            } catch (_: Exception) {
                null
            }
        )
    }

    val viewModel: WonderingWorldViewModel = viewModel(
        key = currentUser?.uid ?: "guest_session",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                WonderingWorldViewModel(WonderingWorldRepository(db))
            }
        }
    )

    DisposableEffect(Unit) {
        val auth = try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            viewModel.onAuthSessionChanged(
                uid = firebaseAuth.currentUser?.uid,
                defaultDisplayName = firebaseAuth.currentUser?.displayName
            )
        }
        auth?.addAuthStateListener(listener)
        onDispose {
            auth?.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                viewModel.onAuthSessionChanged(
                    uid = currentUser?.uid,
                    defaultDisplayName = currentUser?.displayName
                )
            },
            onUnauthenticated = {},
            scope = scope
        )
    }

    val navScreen by viewModel.navScreen.collectAsStateWithLifecycle()
    val authPortalActiveLink by viewModel.authPortalActiveLink.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    // Pop up the Login & Create Account page every time the app icon is hit
    LaunchedEffect(appIconLaunchSignal) {
        viewModel.navigateToLogin()
    }
    var reportTargetInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
    val signUpForm by viewModel.signUpForm.collectAsStateWithLifecycle()
    val authBannerMessage by viewModel.authBannerMessage.collectAsStateWithLifecycle()
    val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val wallPostsState by viewModel.wallPostsState.collectAsStateWithLifecycle()
    val budgetEntriesState by viewModel.budgetEntriesState.collectAsStateWithLifecycle()
    val isPlayingMusic by viewModel.isPlayingMusic.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val backgroundTransparency by viewModel.backgroundTransparency.collectAsStateWithLifecycle()
    val userAccountEmail by viewModel.userAccountEmail.collectAsStateWithLifecycle()
    val userAccountPhone by viewModel.userAccountPhone.collectAsStateWithLifecycle()
    val isFinancialVaultUnlocked by viewModel.isFinancialVaultUnlocked.collectAsStateWithLifecycle()
    val isResourceHubUnlocked by viewModel.isResourceHubUnlocked.collectAsStateWithLifecycle()
    val pageThemeColorHex by viewModel.pageThemeColorHex.collectAsStateWithLifecycle()
    val themeVisualStyle by viewModel.themeVisualStyle.collectAsStateWithLifecycle()
    val customProfileBitmap by viewModel.customProfileBitmap.collectAsStateWithLifecycle()
    val selected3DAvatarResId by viewModel.selected3DAvatarResId.collectAsStateWithLifecycle()
    val selectedAvatarTags by viewModel.selectedAvatarTags.collectAsStateWithLifecycle()
    val avatarEmojiBadge by viewModel.avatarEmojiBadge.collectAsStateWithLifecycle()
    val learnedSignals by viewModel.learnedSignals.collectAsStateWithLifecycle()
    val learnedInterests by viewModel.learnedInterests.collectAsStateWithLifecycle()
    val learnedLifestyle by viewModel.learnedLifestyle.collectAsStateWithLifecycle()
    val learnedSchedule by viewModel.learnedSchedule.collectAsStateWithLifecycle()
    val learnedResearch by viewModel.learnedResearch.collectAsStateWithLifecycle()
    val selectedSports by viewModel.selectedSports.collectAsStateWithLifecycle()
    val everyLoginSuggestions by viewModel.everyLoginSuggestions.collectAsStateWithLifecycle()
    val userIdeas by viewModel.userIdeas.collectAsStateWithLifecycle()
    val aiStudyState by viewModel.aiStudyState.collectAsStateWithLifecycle()
    val aiFlashcardsState by viewModel.aiFlashcardsState.collectAsStateWithLifecycle()
    val aiAdaptivePlanState by viewModel.aiAdaptivePlanState.collectAsStateWithLifecycle()
    val aiIdeaFeedbackState by viewModel.aiIdeaFeedbackState.collectAsStateWithLifecycle()
    val aiProfileImageState by viewModel.aiProfileImageState.collectAsStateWithLifecycle()
    val userInteractionsState by viewModel.userInteractionsState.collectAsStateWithLifecycle()
    val personalizedInsightsState by viewModel.personalizedInsightsState.collectAsStateWithLifecycle()
    val careerMilestonesState by viewModel.careerMilestonesState.collectAsStateWithLifecycle()
    val activeNetworkOperations by viewModel.activeNetworkOperations.collectAsStateWithLifecycle()
    val isLoadingOverlayMinimized by viewModel.isLoadingOverlayMinimized.collectAsStateWithLifecycle()
    val virtualCurrencyBalance by viewModel.virtualCurrencyBalance.collectAsStateWithLifecycle()
    val dailyLoginBonusClaimed by viewModel.dailyLoginBonusClaimed.collectAsStateWithLifecycle()
    val loginStreakDays by viewModel.loginStreakDays.collectAsStateWithLifecycle()
    val claimedBadgeIds by viewModel.claimedBadgeIds.collectAsStateWithLifecycle()
    val unlockedShopItemIds by viewModel.unlockedShopItemIds.collectAsStateWithLifecycle()
    val currencyTransactions by viewModel.currencyTransactions.collectAsStateWithLifecycle()
    val currencyStatusBanner by viewModel.currencyStatusBanner.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.notificationEvents.collect { fromName: String ->
            com.example.ui.components.NotificationHelper.showFriendRequestNotification(context, fromName)
        }
    }

    val loadedProfile = (userProfileState as? UiState.Success)?.data
    val currentAge = loadedProfile?.age ?: signUpForm.ageText.toLongOrNull() ?: 26L
    val currentRole = loadedProfile?.role ?: signUpForm.role
    val currentTrackTitle = loadedProfile?.musicTrackTitle ?: signUpForm.musicTrackTitle

    // Real background audio synthesizer engine for profile music playback
    BackgroundMusicAudioEngine(
        isPlaying = isPlayingMusic,
        currentTrackTitle = currentTrackTitle
    )

    val effectiveScreen = navScreen
    var followSystemTheme by remember { mutableStateOf(false) }
    var isLateNightEyeComfort by remember { mutableStateOf(false) }
    var nightDimmerAlpha by remember { mutableStateOf(0.0f) }
    var useDynamicColor by remember { mutableStateOf(false) }

    WonderingWorldTheme(
        accentHex = pageThemeColorHex,
        isDarkMode = isDarkMode,
        backgroundTransparency = backgroundTransparency,
        useDynamicColor = useDynamicColor,
        isLateNightEyeComfort = isLateNightEyeComfort
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThreeDimensionalBackground(
                accentHex = pageThemeColorHex,
                visualStyleId = themeVisualStyle,
                isDarkMode = isDarkMode,
                backgroundTransparency = backgroundTransparency
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Global Pull-Down Navigation Bar + Every-Page Time/Date/Weather/Location Permission + Age Group Selector + Phone System Settings
                PullDownNavigationBar(
                    effectiveScreen = effectiveScreen,
                    activeTab = activeTab,
                    currentAge = currentAge,
                    currentRole = currentRole,
                    currentThemeHex = pageThemeColorHex,
                    currentVisualStyleId = themeVisualStyle,
                    isDarkMode = isDarkMode,
                    backgroundTransparency = backgroundTransparency,
                    onTransparencyChange = viewModel::updateBackgroundTransparency,
                    currentLocationName = loadedProfile?.location ?: signUpForm.location,
                    onLocationUpdated = viewModel::updateSignUpLocation,
                    learnedSummary = learnedSignals.take(3).joinToString(" • "),
                    onNavigateToLogin = { viewModel.navigateToLogin() },
                    onNavigateToSignUp = { viewModel.navigateToSignUp() },
                    onNavigateToDashboardTab = { targetTab ->
                        viewModel.selectDashboardTab(targetTab)
                        viewModel.navigateToMainDashboard()
                    },
                    onSelectAgeGroup = { preset ->
                        viewModel.switchPersonaQuick(preset.role, preset.age)
                    },
                    onSelectPageColor = { hex ->
                        viewModel.selectPageThemeColor(hex)
                    },
                    onSelectVisualStyle = { styleId ->
                        viewModel.selectThemeVisualStyle(styleId)
                    },
                    onToggleDarkMode = {
                        viewModel.toggleDarkMode()
                    },
                    onRebootPlatform = {
                        viewModel.rebootPlatform()
                    },
                    isNetworkLoading = activeNetworkOperations.isNotEmpty(),
                    onTriggerNetworkSync = {
                        viewModel.syncAllCloudDataAndNetworkRefresh()
                    }
                )

                // System-Wide Material 3 Dark Mode, Late-Night Eye Comfort, Solid Colors & All-Backgrounds Transparency Bar
                com.example.ui.components.SystemWideDarkModeHeaderBar(
                    isDarkMode = isDarkMode,
                    followSystemTheme = followSystemTheme,
                    isLateNightEyeComfort = isLateNightEyeComfort,
                    nightDimmerAlpha = nightDimmerAlpha,
                    useDynamicColor = useDynamicColor,
                    currentThemeHex = pageThemeColorHex,
                    onSelectThemeHex = viewModel::selectPageThemeColor,
                    backgroundTransparency = backgroundTransparency,
                    onTransparencyChange = viewModel::updateBackgroundTransparency,
                    onSetDarkMode = viewModel::setDarkMode,
                    onSetFollowSystemTheme = { followSystemTheme = it },
                    onSetLateNightEyeComfort = { isLateNightEyeComfort = it },
                    onSetNightDimmerAlpha = { nightDimmerAlpha = it },
                    onSetUseDynamicColor = { useDynamicColor = it }
                )

                // Universal Search Bar visible on every page and profile
                val currentWallPosts = (wallPostsState as? UiState.Success)?.data ?: emptyList()
                com.example.ui.components.UniversalPlatformSearchBar(
                    currentUserDisplayName = loadedProfile?.displayName ?: signUpForm.displayName,
                    currentUserRole = currentRole,
                    wallPosts = currentWallPosts,
                    onNavigateToTab = { targetTab ->
                        viewModel.selectDashboardTab(targetTab)
                        viewModel.navigateToMainDashboard()
                    }
                )

                AnimatedContent(
                    targetState = effectiveScreen,
                    label = "app_navigation_horizontal_slide",
                    transitionSpec = {
                        val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                        (slideInHorizontally(
                            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> (fullWidth * 0.18f * direction).toInt() }
                        ) + fadeIn(animationSpec = tween(durationMillis = 280)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                                    targetOffsetX = { fullWidth -> (-fullWidth * 0.18f * direction).toInt() }
                                ) + fadeOut(animationSpec = tween(durationMillis = 220))
                            )
                    },
                    modifier = Modifier.weight(1f)
                ) { screen ->
                    when (screen) {
                        AppNavScreen.LOGIN, AppNavScreen.SIGN_UP -> {
                            LoginScreen(
                                authBannerMessage = authBannerMessage,
                                isDarkMode = isDarkMode,
                                onSetDarkMode = viewModel::setDarkMode,
                                selected3DAvatarResId = selected3DAvatarResId,
                                onSelect3DAvatarPreset = viewModel::select3DAvatarPreset,
                                currentThemeHex = pageThemeColorHex,
                                onThemeColorSelect = viewModel::selectPageThemeColor,
                                currentVisualStyleId = themeVisualStyle,
                                onSelectVisualStyle = viewModel::selectThemeVisualStyle,
                                onAdvanceThemeAndColor = viewModel::advancePageThemeColor,
                                displayName = signUpForm.displayName.ifBlank { "Jordan Vance" },
                                everyLoginSuggestions = everyLoginSuggestions,
                                onRefreshLoginSuggestions = viewModel::refreshEveryLoginSuggestions,
                                selectedSportsIds = selectedSports,
                                onToggleSport = viewModel::toggleFavoriteSport,
                                currentTrackTitle = signUpForm.musicTrackTitle,
                                currentMusicPlatform = signUpForm.musicPlatform,
                                isPlayingMusic = isPlayingMusic,
                                onSelectSongPreset = viewModel::selectBackgroundSongPreset,
                                onMusicChange = viewModel::updateSignUpMusic,
                                onToggleMusic = viewModel::toggleMusicPlayback,
                                onAuthSuccess = {
                                    currentUser = Firebase.auth.currentUser
                                    viewModel.onAuthSessionChanged(
                                        uid = currentUser?.uid,
                                        defaultDisplayName = currentUser?.displayName
                                    )
                                    viewModel.saveProfileFromSignUpForm {
                                        viewModel.navigateToMainDashboard()
                                    }
                                },
                                onAuthError = { msg ->
                                    viewModel.setAuthBannerMessage(msg)
                                },
                                onNavigateToSignUp = {
                                    viewModel.navigateToSignUp()
                                },
                                onEnterDashboard = { preserveSelectedTheme ->
                                    viewModel.navigateToMainDashboard(preserveSelectedTheme)
                                },
                                onQuickLoginWithIdentity = { identifier, role, age ->
                                    viewModel.updateSignUpDisplayName(identifier)
                                    viewModel.switchPersonaQuick(role, age)
                                    viewModel.updatePledgeAccepted(true)
                                    viewModel.saveProfileFromSignUpForm {}
                                },
                                activePortalLink = authPortalActiveLink,
                                onSelectPortalLink = viewModel::selectAuthPortalLink,
                                signUpFormState = signUpForm,
                                isAuthenticated = currentUser != null,
                                customProfileBitmap = customProfileBitmap,
                                selectedAvatarTags = selectedAvatarTags,
                                avatarEmojiBadge = avatarEmojiBadge,
                                aiProfileImageState = aiProfileImageState,
                                learnedInterests = learnedInterests,
                                learnedLifestyle = learnedLifestyle,
                                learnedSchedule = learnedSchedule,
                                learnedResearch = learnedResearch,
                                learnedSignals = learnedSignals,
                                aiAdaptivePlanState = aiAdaptivePlanState,
                                backgroundTransparency = backgroundTransparency,
                                onTransparencyChange = viewModel::updateBackgroundTransparency,
                                onDisplayNameChange = viewModel::updateSignUpDisplayName,
                                onAgeChange = viewModel::updateSignUpAge,
                                onLocationChange = viewModel::updateSignUpLocation,
                                onBioChange = viewModel::updateSignUpBio,
                                onRoleSelect = viewModel::updateSignUpRole,
                                onToggleInterest = viewModel::toggleSignUpInterest,
                                onPledgeChange = viewModel::updatePledgeAccepted,
                                onSaveAuthenticatedProfile = {
                                    viewModel.saveProfileFromSignUpForm {
                                        viewModel.navigateToMainDashboard()
                                    }
                                },
                                onUpdateProfileBitmap = viewModel::updateCustomProfileBitmap,
                                onToggleAvatarTag = viewModel::toggleAvatarDescriptiveTag,
                                onUpdateAvatarBadge = viewModel::updateAvatarEmojiBadge,
                                onGenerateAiProfilePhoto = viewModel::generateAiProfilePhoto,
                                onToggleLearnedInterest = viewModel::toggleLearnedInterest,
                                onToggleLearnedLifestyle = viewModel::toggleLearnedLifestyle,
                                onToggleLearnedSchedule = viewModel::toggleLearnedSchedule,
                                onToggleLearnedResearch = viewModel::toggleLearnedResearch,
                                onAddCustomLearningSignal = viewModel::addCustomLearningSignal,
                                onGenerateAdaptiveLearningPlan = viewModel::generateAdaptiveLearningPlan,
                                onPublishPostToDashboard = { title, body ->
                                    viewModel.createPost("Update", title, body)
                                }
                            )
                        }

                        AppNavScreen.MAIN_DASHBOARD -> {
                            val friendsState by viewModel.friendsState.collectAsStateWithLifecycle()
                            val requestsState by viewModel.incomingRequestsState.collectAsStateWithLifecycle()
                            val discoveredUsers by viewModel.discoveredUsers.collectAsStateWithLifecycle()

                            MainDashboardScreen(
                                activeTab = activeTab,
                                onSelectTab = viewModel::selectDashboardTab,
                                profileState = userProfileState,
                                signUpForm = signUpForm,
                                postsState = wallPostsState,
                                budgetState = budgetEntriesState,
                                curatedResources = viewModel.curatedResources,
                                communityConnections = viewModel.communityConnections,
                                customProfileBitmap = customProfileBitmap,
                                selected3DAvatarResId = selected3DAvatarResId,
                                selectedAvatarTags = selectedAvatarTags,
                                avatarEmojiBadge = avatarEmojiBadge,
                                learnedSignals = learnedSignals,
                                everyLoginSuggestions = everyLoginSuggestions,
                                selectedSportsIds = selectedSports,
                                learnedInterests = learnedInterests,
                                learnedLifestyle = learnedLifestyle,
                                learnedSchedule = learnedSchedule,
                                learnedResearch = learnedResearch,
                                userIdeas = userIdeas,
                                aiStudyState = aiStudyState,
                                aiFlashcardsState = aiFlashcardsState,
                                aiAdaptivePlanState = aiAdaptivePlanState,
                                aiIdeaFeedbackState = aiIdeaFeedbackState,
                                aiProfileImageState = aiProfileImageState,
                                isPlayingMusic = isPlayingMusic,
                                isDarkMode = isDarkMode,
                                onSetDarkMode = viewModel::setDarkMode,
                                onToggleMusic = viewModel::toggleMusicPlayback,
                                onSelectBackgroundSongPreset = viewModel::selectBackgroundSongPreset,
                                onToggleSport = viewModel::toggleFavoriteSport,
                                onToggleLearnedInterest = viewModel::toggleLearnedInterest,
                                onToggleLearnedLifestyle = viewModel::toggleLearnedLifestyle,
                                onToggleLearnedSchedule = viewModel::toggleLearnedSchedule,
                                onToggleLearnedResearch = viewModel::toggleLearnedResearch,
                                onAddCustomLearningSignal = viewModel::addCustomLearningSignal,
                                onRefreshLoginSuggestions = viewModel::refreshEveryLoginSuggestions,
                                onSwitchPersonaQuick = viewModel::switchPersonaQuick,
                                onThemeColorSelect = viewModel::selectPageThemeColor,
                                currentVisualStyleId = themeVisualStyle,
                                onThemeVisualStyleSelect = viewModel::selectThemeVisualStyle,
                                onDisplayNameChange = viewModel::updateSignUpDisplayName,
                                onAgeChange = viewModel::updateSignUpAge,
                                onLocationChange = viewModel::updateSignUpLocation,
                                onBioChange = viewModel::updateSignUpBio,
                                onRoleSelect = viewModel::updateSignUpRole,
                                onMusicChange = viewModel::updateSignUpMusic,
                                onUpdateProfileBitmap = viewModel::updateCustomProfileBitmap,
                                onSelect3DAvatarPreset = viewModel::select3DAvatarPreset,
                                onToggleAvatarTag = viewModel::toggleAvatarDescriptiveTag,
                                onUpdateAvatarBadge = viewModel::updateAvatarEmojiBadge,
                                onGenerateAiProfilePhoto = viewModel::generateAiProfilePhoto,
                                onPledgeChange = viewModel::updatePledgeAccepted,
                                onSaveProfileFromForm = {
                                    viewModel.saveProfileFromSignUpForm {}
                                },
                                onCreatePost = viewModel::createPost,
                                onLikePost = viewModel::likePost,
                                onDeletePost = viewModel::deletePost,
                                onAddBudgetItem = viewModel::addBudgetItem,
                                onDeleteBudgetItem = viewModel::deleteBudgetItem,
                                onUpdateSavingsGoal = viewModel::updateSavingsGoal,
                                onNewArcadeHighScore = viewModel::recordArcadeScore,
                                onAskAiStudyTutor = viewModel::askAiStudyTutor,
                                onGenerateStudyFlashcards = viewModel::generateStudyFlashcards,
                                onGenerateAdaptiveLearningPlan = viewModel::generateAdaptiveLearningPlan,
                                onRecordLearnedSignal = viewModel::recordLearnedSignal,
                                onSubmitUserIdea = viewModel::submitUserIdea,
                                onUpvoteUserIdea = viewModel::upvoteUserIdea,
                                onExpandIdeaWithAi = viewModel::expandUserIdeaWithAi,
                                onNavigateToEditProfile = {
                                    viewModel.navigateToSignUp()
                                },
                                onSignOut = {
                                    AuthManager.signOut(
                                        credentialManager = credentialManager,
                                        scope = scope,
                                        onSignOutComplete = {
                                            currentUser = null
                                            viewModel.onAuthSessionChanged(null, null)
                                            viewModel.navigateToLogin()
                                        }
                                    )
                                },
                                userInteractionsState = userInteractionsState,
                                personalizedInsightsState = personalizedInsightsState,
                                onGenerateGeminiInsights = viewModel::generateDailyPersonalizedInsightsWithGemini,
                                onLogInteraction = viewModel::logUserInteraction,
                                onDeleteInteraction = viewModel::deleteUserInteraction,
                                onAdvanceThemeAndColor = viewModel::advancePageThemeColor,
                                isFinancialVaultUnlocked = isFinancialVaultUnlocked,
                                onUnlockFinancialVault = viewModel::unlockFinancialVault,
                                onLockFinancialVault = viewModel::lockFinancialVault,
                                careerMilestonesState = careerMilestonesState,
                                onAddCareerMilestone = viewModel::addCareerMilestoneObjective,
                                onUpdateCareerMilestoneStatus = viewModel::updateCareerMilestoneObjectiveStatus,
                                onDeleteCareerMilestone = viewModel::deleteCareerMilestoneObjective,
                                backgroundTransparency = backgroundTransparency,
                                onTransparencyChange = viewModel::updateBackgroundTransparency,
                                userAccountEmail = userAccountEmail,
                                userAccountPhone = userAccountPhone,
                                onUpdateUserEmail = viewModel::updateUserAccountEmail,
                                onUpdateUserPhone = viewModel::updateUserAccountPhone,
                                friendsState = friendsState,
                                incomingRequestsState = requestsState,
                                discoveredUsers = discoveredUsers,
                                onUpdatePhoneNumber = viewModel::updatePhoneNumber,
                                onSyncContacts = viewModel::syncContacts,
                                onSearchUsers = viewModel::searchUsers,
                                onSendFriendRequest = viewModel::sendFriendRequest,
                                onAcceptRequest = viewModel::acceptFriendRequest,
                                onDeclineRequest = viewModel::declineFriendRequest,
                                onReportContent = viewModel::reportContent,
                                onShowReportDialog = { id, type -> reportTargetInfo = id to type },
                                onConnectSpotify = viewModel::connectSpotify,
                                onConnectAppleMusic = viewModel::connectAppleMusic,
                                onDisconnectSpotify = viewModel::disconnectSpotify,
                                onDisconnectAppleMusic = viewModel::disconnectAppleMusic,
                                isResourceHubUnlocked = isResourceHubUnlocked,
                                onUnlockResourceHub = viewModel::unlockResourceHub,
                                onLockResourceHub = viewModel::lockResourceHub,
                                onNavigateToFriendsDiscovery = viewModel::navigateToFriendsDiscovery,
                                virtualCurrencyBalance = virtualCurrencyBalance,
                                dailyBonusClaimed = dailyLoginBonusClaimed,
                                loginStreakDays = loginStreakDays,
                                claimedBadgeIds = claimedBadgeIds,
                                unlockedShopItemIds = unlockedShopItemIds,
                                currencyTransactions = currencyTransactions,
                                currencyStatusBanner = currencyStatusBanner,
                                onClaimDailyLoginBonus = viewModel::claimDailyLoginCurrencyBonus,
                                onRewardHelpfulPostBonus = { viewModel.rewardHelpfulPostBonus() },
                                onClaimBadgeQuest = viewModel::claimCurrencyBadgeQuest,
                                onPurchaseOrApplyShopItem = viewModel::purchaseOrApplyShopItem,
                                onTipCommunityUser = viewModel::tipCommunityMember
                            )
                        }

                        AppNavScreen.FRIENDS_DISCOVERY -> {
                            val friendsState by viewModel.friendsState.collectAsStateWithLifecycle()
                            val requestsState by viewModel.incomingRequestsState.collectAsStateWithLifecycle()
                            val discoveredUsers by viewModel.discoveredUsers.collectAsStateWithLifecycle()
                            val profile = (userProfileState as? UiState.Success)?.data

                            FriendsAndDiscoveryScreen(
                                friendsState = friendsState,
                                incomingRequestsState = requestsState,
                                discoveredUsers = discoveredUsers,
                                myPhoneNumber = profile?.phoneNumber ?: "",
                                myUserProfile = profile ?: com.example.data.model.UserProfile(
                                    displayName = signUpForm.displayName.ifBlank { "Jordan Vance" },
                                    location = signUpForm.location.ifBlank { "Austin, TX" }
                                ),
                                onUpdatePhoneNumber = viewModel::updatePhoneNumber,
                                onSyncContacts = viewModel::syncContacts,
                                onSearchUsers = viewModel::searchUsers,
                                onSendFriendRequest = viewModel::sendFriendRequest,
                                onAcceptRequest = viewModel::acceptFriendRequest,
                                onDeclineRequest = viewModel::declineFriendRequest,
                                onShowReportDialog = { id, type -> reportTargetInfo = id to type },
                                onBack = { viewModel.navigateToMainDashboard() }
                            )
                        }
                    }
                }
            }

            // Global Loading Overlay with CircularProgressIndicator for background network, Firestore & AI operations
            GlobalLoadingOverlay(
                activeOperations = activeNetworkOperations,
                isOverlayMinimized = isLoadingOverlayMinimized,
                onMinimizeToggle = viewModel::setLoadingOverlayMinimized,
                onDismissAll = viewModel::dismissAllNetworkLoadingIndicators
            )

            // Global Reporting Dialog
            if (reportTargetInfo != null) {
                com.example.ui.components.ReportContentDialog(
                    targetId = reportTargetInfo!!.first,
                    targetType = reportTargetInfo!!.second,
                    onDismiss = { reportTargetInfo = null },
                    onSubmit = viewModel::reportContent
                )
            }

            // Late-Night Eye Comfort Non-Blocking Warm Dimmer Filter
            if (isDarkMode && isLateNightEyeComfort && nightDimmerAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Color(0xFF140C02)
                                .copy(alpha = nightDimmerAlpha.coerceIn(0f, 0.55f))
                        )
                )
            }
        }
    }
}
