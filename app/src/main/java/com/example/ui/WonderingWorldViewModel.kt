package com.example.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.model.BlockedUser
import com.example.data.model.BudgetEntry
import com.example.data.model.CareerMilestone
import com.example.ui.auth.AuthManager
import com.example.data.model.CommunityConnectionCard
import com.example.data.model.PersonalizedDailyInsight
import com.example.data.model.PersonaStageData
import com.example.data.model.Profile3DConfig
import com.example.data.model.ResourceDirectoryItem
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import com.example.data.model.WallPost
import com.example.data.model.buildDefaultPersonaStageData
import com.example.data.model.buildDefaultStudentCareerMilestones
import com.example.data.model.normalizePersonaStageId
import com.example.data.repository.WonderingWorldRepository
import com.example.ui.components.BackgroundSongPreset
import com.example.ui.components.CurrencyEarnBadgeQuest
import com.example.ui.components.CurrencySpendItem
import com.example.ui.components.SPORTS_CATALOG
import com.example.ui.components.ThreeDAvatarPreset
import com.example.ui.components.VirtualCurrencyTransaction
import com.example.ui.components.buildEveryLoginSuggestions
import com.example.ui.components.findBestMatching3DAvatarPreset
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import android.provider.ContactsContract
import android.content.Context
import com.example.ui.components.NotificationHelper
import com.example.data.model.Friend
import com.example.data.model.FriendRequest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

enum class AppNavScreen {
    LOGIN,
    SIGN_UP,
    MAIN_DASHBOARD,
    FRIENDS_DISCOVERY
}

enum class DashboardTab {
    HUB_3D,
    DELIVERY_DRIVING,
    MESSAGING,
    VIDEO_CHAT,
    NEWS_SPORTS,
    THEME_PROFILE,
    RESOURCES,
    AI_TOOLS,
    STUDIO_ARCADE,
    FINANCE_GOALS,
    STABILITY_LOUNGE,
    CODE_OF_CONDUCT,
    MAINTENANCE_SETTINGS,
    FRIENDS
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiTextUiState {
    data object Idle : AiTextUiState
    data object Loading : AiTextUiState
    data class Streaming(val text: String) : AiTextUiState
    data class Success(val text: String) : AiTextUiState
    data class Error(val message: String) : AiTextUiState
}

sealed interface AiImageUiState {
    data object Idle : AiImageUiState
    data object Loading : AiImageUiState
    data class Success(val bitmap: Bitmap) : AiImageUiState
    data class Error(val message: String) : AiImageUiState
}

data class AiFlashcardItem(
    val question: String,
    val answer: String,
    val memoryHint: String
)

data class UserIdeaProposal(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val authorName: String,
    val votes: Int,
    val statusBadge: String
)

data class NetworkOperationInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String = "Cloud Sync",
    val progress: Float? = null,
    val startedAtMillis: Long = System.currentTimeMillis()
)

data class SignUpFormState(
    val displayName: String = "Jordan Vance",
    val ageText: String = "26",
    val location: String = "Austin, TX",
    val bio: String = "We are all equal. Building community, creativity, and stability.",
    val role: String = "veteran",
    val themeColorHex: String = "#6366F1",
    val themeVisualStyle: String = "HYPERCUBE_6D",
    val musicTrackTitle: String = "Comfortably Numb - Pink Floyd",
    val musicPlatform: String = "Spotify",
    val selectedInterests: Set<String> = setOf("Healthcare & Wellness", "Music & 3D Art", "Financial Stability"),
    val pledgeAccepted: Boolean = true,
    val isSaving: Boolean = false,
    val statusMessage: String? = null
)

class WonderingWorldViewModel(
    private val repository: WonderingWorldRepository
) : ViewModel() {

    private val _navScreen = MutableStateFlow(AppNavScreen.LOGIN)
    val navScreen: StateFlow<AppNavScreen> = _navScreen.asStateFlow()

    private val _authPortalActiveLink = MutableStateFlow("BOTH") // "BOTH", "LOGIN", or "CREATE_ACCOUNT"
    val authPortalActiveLink: StateFlow<String> = _authPortalActiveLink.asStateFlow()

    private val _activeTab = MutableStateFlow(DashboardTab.HUB_3D)
    val activeTab: StateFlow<DashboardTab> = _activeTab.asStateFlow()

    private val _authenticatedUid = MutableStateFlow<String?>(null)
    val authenticatedUid: StateFlow<String?> = _authenticatedUid.asStateFlow()

    private val _signUpForm = MutableStateFlow(SignUpFormState())
    val signUpForm: StateFlow<SignUpFormState> = _signUpForm.asStateFlow()

    private val _authBannerMessage = MutableStateFlow<String?>(null)
    val authBannerMessage: StateFlow<String?> = _authBannerMessage.asStateFlow()

    private val _isPlayingMusic = MutableStateFlow(false)
    val isPlayingMusic: StateFlow<Boolean> = _isPlayingMusic.asStateFlow()

    // Global Light / Dark Mode Theme State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Global Background Transparency State (0f = 100% Solid Color .. 1f = 100% Transparent All Backgrounds)
    private val _backgroundTransparency = MutableStateFlow(0.0f)
    val backgroundTransparency: StateFlow<Float> = _backgroundTransparency.asStateFlow()

    // User Phone & Email Settings State (Synced with Phone OS Settings on Maintenance Page)
    private val _userAccountEmail = MutableStateFlow("allengrogan33@gmail.com")
    val userAccountEmail: StateFlow<String> = _userAccountEmail.asStateFlow()

    private val _userAccountPhone = MutableStateFlow("509-552-3217")
    val userAccountPhone: StateFlow<String> = _userAccountPhone.asStateFlow()

    // Virtual Currency System State (EqualCoins / EQC - Starts with 250 EQC starter balance)
    private val _virtualCurrencyBalance = MutableStateFlow(250L)
    val virtualCurrencyBalance: StateFlow<Long> = _virtualCurrencyBalance.asStateFlow()

    private val _dailyLoginBonusClaimed = MutableStateFlow(false)
    val dailyLoginBonusClaimed: StateFlow<Boolean> = _dailyLoginBonusClaimed.asStateFlow()

    private val _loginStreakDays = MutableStateFlow(3)
    val loginStreakDays: StateFlow<Int> = _loginStreakDays.asStateFlow()

    private val _claimedBadgeIds = MutableStateFlow(setOf("welcome_pioneer"))
    val claimedBadgeIds: StateFlow<Set<String>> = _claimedBadgeIds.asStateFlow()

    private val _unlockedShopItemIds = MutableStateFlow(emptySet<String>())
    val unlockedShopItemIds: StateFlow<Set<String>> = _unlockedShopItemIds.asStateFlow()

    private val _currencyStatusBanner = MutableStateFlow<String?>(
        "🪙 Welcome Starter Balance: 250 EqualCoins (EQC) ready to earn, spend on 3D themes/items, or tip peers!"
    )
    val currencyStatusBanner: StateFlow<String?> = _currencyStatusBanner.asStateFlow()

    private val _currencyTransactions = MutableStateFlow(
        listOf(
            VirtualCurrencyTransaction(
                id = "tx_seed_welcome",
                title = "Starter Welcome Treasury Grant",
                amount = 220L,
                isEarned = true,
                category = "Starter Balance",
                timestampLabel = "Account Init"
            ),
            VirtualCurrencyTransaction(
                id = "tx_seed_badge",
                title = "First Welcome Pioneer Badge (🌍)",
                amount = 30L,
                isEarned = true,
                category = "Badge Reward",
                timestampLabel = "Unlocked"
            )
        )
    )
    val currencyTransactions: StateFlow<List<VirtualCurrencyTransaction>> = _currencyTransactions.asStateFlow()

    fun claimDailyLoginCurrencyBonus() {
        if (_dailyLoginBonusClaimed.value) {
            _currencyStatusBanner.value = "✓ You already claimed today's +50 EQC Daily Login Bonus!"
            return
        }
        val bonus = 50L
        _dailyLoginBonusClaimed.value = true
        _loginStreakDays.update { it + 1 }
        addCurrencyTransaction(
            title = "Daily Login Bonus (${_loginStreakDays.value}-Day Streak 🔥)",
            amount = bonus,
            isEarned = true,
            category = "Daily Login"
        )
        _currencyStatusBanner.value = "🎉 Claimed +$bonus EQC Daily Login Bonus! (${_loginStreakDays.value}-Day Streak)"
        logUserInteraction("CurrencyEarn", "Claimed +$bonus EQC Daily Login Bonus", "EqualCoins")
    }

    fun rewardHelpfulPostBonus(reason: String = "Shared & endorsed helpful community resource post") {
        val reward = 25L
        addCurrencyTransaction(
            title = "Helpful Post Reward: ${reason.take(44)}",
            amount = reward,
            isEarned = true,
            category = "Helpful Post"
        )
        _currencyStatusBanner.value = "🌟 Earned +$reward EQC for contributing a helpful community post!"
        logUserInteraction("CurrencyEarn", "Earned +$reward EQC: $reason", "EqualCoins")
    }

    fun claimCurrencyBadgeQuest(badge: CurrencyEarnBadgeQuest) {
        if (_claimedBadgeIds.value.contains(badge.id)) {
            _currencyStatusBanner.value = "✓ Badge '${badge.title}' already claimed!"
            return
        }
        _claimedBadgeIds.update { it + badge.id }
        addCurrencyTransaction(
            title = "Badge Unlocked: ${badge.emoji} ${badge.title}",
            amount = badge.rewardCoins,
            isEarned = true,
            category = "Achievement Badge"
        )
        _currencyStatusBanner.value = "🏅 Unlocked ${badge.emoji} ${badge.title} (+${badge.rewardCoins} EQC)!"
        logUserInteraction("BadgeUnlock", "Claimed ${badge.title} (+${badge.rewardCoins} EQC)", "EqualCoins")
    }

    fun purchaseOrApplyShopItem(item: CurrencySpendItem) {
        val alreadyOwned = _unlockedShopItemIds.value.contains(item.id)
        if (!alreadyOwned) {
            if (_virtualCurrencyBalance.value < item.costCoins) {
                _currencyStatusBanner.value = "⚠️ Need ${item.costCoins} EQC to unlock ${item.title} (Current: ${_virtualCurrencyBalance.value} EQC)"
                return
            }
            _unlockedShopItemIds.update { it + item.id }
            addCurrencyTransaction(
                title = "Unlocked ${item.emoji} ${item.title}",
                amount = item.costCoins,
                isEarned = false,
                category = if (item.itemType == "THEME") "3D Theme Shop" else "Virtual Item"
            )
        }
        if (item.itemType == "THEME") {
            item.themeStyleId?.let { selectThemeVisualStyle(it) }
            item.themeColorHex?.let { selectPageThemeColor(it) }
            _currencyStatusBanner.value = if (alreadyOwned) {
                "🎨 Applied owned 3D Profile Theme: ${item.title}!"
            } else {
                "🎨 Purchased & applied ${item.title} (-${item.costCoins} EQC)!"
            }
        } else {
            item.badgeEmoji?.let { updateAvatarEmojiBadge(it) }
            _currencyStatusBanner.value = if (alreadyOwned) {
                "✨ Equipped owned Virtual Item: ${item.emoji} ${item.title}!"
            } else {
                "✨ Purchased & equipped ${item.emoji} ${item.title} (-${item.costCoins} EQC)!"
            }
        }
        logUserInteraction("CurrencySpend", "Equipped ${item.title}", "EqualCoins")
    }

    fun tipCommunityMember(recipientName: String, amount: Long, note: String) {
        val cleanRecipient = recipientName.trim().ifBlank { "Community Member" }
        val validAmount = amount.coerceAtLeast(5L)
        if (_virtualCurrencyBalance.value < validAmount) {
            _currencyStatusBanner.value = "⚠️ Insufficient EQC balance to send $validAmount EQC tip."
            return
        }
        addCurrencyTransaction(
            title = "Tipped $cleanRecipient: \"${note.take(36)}\"",
            amount = validAmount,
            isEarned = false,
            category = "Peer Tip"
        )
        _currencyStatusBanner.value = "🤝 Sent 🪙 $validAmount EQC tip to $cleanRecipient!"
        logUserInteraction("CurrencyTip", "Tipped $validAmount EQC to $cleanRecipient", "EqualCoins")
    }

    private fun addCurrencyTransaction(
        title: String,
        amount: Long,
        isEarned: Boolean,
        category: String
    ) {
        val delta = if (isEarned) amount else -amount
        val newBalance = (_virtualCurrencyBalance.value + delta).coerceAtLeast(0L)
        _virtualCurrencyBalance.value = newBalance
        val tx = VirtualCurrencyTransaction(
            id = "tx_${System.currentTimeMillis()}_${(100..999).random()}",
            title = title,
            amount = amount,
            isEarned = isEarned,
            category = category,
            timestampLabel = "Just now"
        )
        _currencyTransactions.update { (listOf(tx) + it).take(20) }
        _localProfile.update {
            it.copy(
                virtualCurrencyBalance = newBalance,
                earnedBadges = _claimedBadgeIds.value.toList(),
                ownedVirtualItems = _unlockedShopItemIds.value.toList()
            )
        }
        val uid = _authenticatedUid.value
        if (uid != null) {
            viewModelScope.launch {
                repository.saveUserProfile(_localProfile.value.copy(userId = uid))
            }
        }
    }

    // Global Network Loading Overlay State (Tracks all active background Firestore, Auth & Gemini AI operations)
    private val _activeNetworkOperations = MutableStateFlow<List<NetworkOperationInfo>>(emptyList())
    val activeNetworkOperations: StateFlow<List<NetworkOperationInfo>> = _activeNetworkOperations.asStateFlow()

    private val _isLoadingOverlayMinimized = MutableStateFlow(false)
    val isLoadingOverlayMinimized: StateFlow<Boolean> = _isLoadingOverlayMinimized.asStateFlow()

    fun setLoadingOverlayMinimized(minimized: Boolean) {
        _isLoadingOverlayMinimized.value = minimized
    }

    fun dismissAllNetworkLoadingIndicators() {
        _activeNetworkOperations.value = emptyList()
        _isLoadingOverlayMinimized.value = false
    }

    fun startNetworkOperation(
        title: String,
        subtitle: String,
        category: String = "Cloud Sync",
        progress: Float? = null
    ): String {
        val opId = "net_op_${System.currentTimeMillis()}_${(100..999).random()}"
        val op = NetworkOperationInfo(
            id = opId,
            title = title,
            subtitle = subtitle,
            category = category,
            progress = progress
        )
        _activeNetworkOperations.update { current -> current + op }
        return opId
    }

    fun updateNetworkOperationProgress(opId: String, subtitle: String, progress: Float?) {
        _activeNetworkOperations.update { current ->
            current.map { item ->
                if (item.id == opId) {
                    item.copy(subtitle = subtitle, progress = progress)
                } else {
                    item
                }
            }
        }
    }

    fun finishNetworkOperation(opId: String) {
        _activeNetworkOperations.update { current ->
            current.filterNot { it.id == opId }
        }
    }

    private suspend fun <T> withGlobalNetworkLoading(
        title: String,
        subtitle: String,
        category: String = "Cloud Sync",
        minDisplayMillis: Long = 520L,
        block: suspend (opId: String) -> T
    ): T {
        val startMs = System.currentTimeMillis()
        val opId = startNetworkOperation(title = title, subtitle = subtitle, category = category)
        return try {
            val result = block(opId)
            val elapsed = System.currentTimeMillis() - startMs
            if (elapsed < minDisplayMillis) {
                delay(minDisplayMillis - elapsed)
            }
            result
        } finally {
            finishNetworkOperation(opId)
        }
    }

    fun syncAllCloudDataAndNetworkRefresh() {
        viewModelScope.launch {
            withGlobalNetworkLoading(
                title = "Synchronizing WereEqual Cloud Hub...",
                subtitle = "Step 1/3: Verifying user profile & 6D theme preferences",
                category = "Cloud Network Sync",
                minDisplayMillis = 900L
            ) { opId ->
                updateNetworkOperationProgress(
                    opId = opId,
                    subtitle = "Step 1/3: Syncing profile & 6D theme preferences",
                    progress = 0.33f
                )
                val uid = _authenticatedUid.value
                if (uid != null) {
                    val existing = repository.getUserProfile(uid).getOrNull()
                    if (existing != null) {
                        syncFormFromProfile(existing)
                        _localProfile.value = existing
                    } else {
                        repository.saveUserProfile(_localProfile.value.copy(userId = uid))
                    }
                } else {
                    delay(280L)
                }

                updateNetworkOperationProgress(
                    opId = opId,
                    subtitle = "Step 2/3: Refreshing Social Wall, Savings Vault & Career Milestones",
                    progress = 0.68f
                )
                if (uid != null) {
                    repository.seedDefaultStudentCareerMilestonesIfEmpty(uid)
                } else {
                    delay(280L)
                }

                updateNetworkOperationProgress(
                    opId = opId,
                    subtitle = "Step 3/3: Updating personalized daily insights & live signals",
                    progress = 0.95f
                )
                refreshEveryLoginSuggestions()
                delay(220L)
            }
        }
    }

    fun updateBackgroundTransparency(transparency: Float) {
        _backgroundTransparency.value = transparency.coerceIn(0f, 1f)
    }

    fun updateUserAccountEmail(email: String) {
        _userAccountEmail.value = email
    }

    fun updateUserAccountPhone(phone: String) {
        _userAccountPhone.value = phone
    }

    // Biometric Protection State for Financial Literacy Tracker & Sensitive Financial Data
    private val _isFinancialVaultUnlocked = MutableStateFlow(false)
    val isFinancialVaultUnlocked: StateFlow<Boolean> = _isFinancialVaultUnlocked.asStateFlow()

    fun unlockFinancialVault() {
        if (!_isFinancialVaultUnlocked.value) {
            _isFinancialVaultUnlocked.value = true
            logUserInteraction("FeatureVisit", "Biometric Fingerprint/Face Unlock verified for Financial Literacy Tracker", "Finance")
            recordLearnedSignal("Biometric Unlocked Financial Vault")
        }
    }

    fun lockFinancialVault() {
        _isFinancialVaultUnlocked.value = false
    }

    // Biometric Protection State for Sensitive Community Resource Hub
    private val _isResourceHubUnlocked = MutableStateFlow(false)
    val isResourceHubUnlocked: StateFlow<Boolean> = _isResourceHubUnlocked.asStateFlow()

    fun unlockResourceHub() {
        if (!_isResourceHubUnlocked.value) {
            _isResourceHubUnlocked.value = true
            logUserInteraction("FeatureVisit", "Biometric Fingerprint/Face Unlock verified for Sensitive Resource Hub", "Community")
            recordLearnedSignal("Biometric Unlocked Resource Hub")
        }
    }

    fun lockResourceHub() {
        _isResourceHubUnlocked.value = false
    }

    // Dynamic 32-Color Page Theme Cycle & Basic Theme Visual Style Cycle (Changes Every Login & Page Change)
    private val pageThemeCycle = EXPANDED_THEME_COLOR_OPTIONS.map { it.hex }
    private val visualStyleCycle = listOf(
        "HYPERCUBE_6D",
        "CYBER_SYNTHWAVE",
        "GALAXY_VORTEX",
        "CRYSTAL_PRISM",
        "AURORA_WAVE",
        "MATRIX_CYBERNETIC"
    )
    private var themeCycleIndex = 0
    private var visualStyleCycleIndex = 0
    private val _pageThemeColorHex = MutableStateFlow("#2563EB")
    val pageThemeColorHex: StateFlow<String> = _pageThemeColorHex.asStateFlow()

    private val _themeVisualStyle = MutableStateFlow("HYPERCUBE_6D")
    val themeVisualStyle: StateFlow<String> = _themeVisualStyle.asStateFlow()

    // Profile Photo, 3D Avatar Portrait & Descriptive Tag State
    private val _customProfileBitmap = MutableStateFlow<Bitmap?>(null)
    val customProfileBitmap: StateFlow<Bitmap?> = _customProfileBitmap.asStateFlow()

    private val _selected3DAvatarResId = MutableStateFlow<Int?>(R.drawable.img_avatar_cyber_astronaut)
    val selected3DAvatarResId: StateFlow<Int?> = _selected3DAvatarResId.asStateFlow()

    private val _selectedAvatarTags = MutableStateFlow(
        setOf("Cosmic Explorer", "Pixar 3D Render", "Holo Visor", "Deep Space Nebula")
    )
    val selectedAvatarTags: StateFlow<Set<String>> = _selectedAvatarTags.asStateFlow()

    private val _avatarEmojiBadge = MutableStateFlow("🚀")
    val avatarEmojiBadge: StateFlow<String> = _avatarEmojiBadge.asStateFlow()

    // Adaptive Learning Signals ("platform learns from users": Interests, Lifestyle, Schedule & Research)
    private val _learnedSignals = MutableStateFlow(
        listOf(
            "STEM & Space Exploration",
            "Community Equality",
            "Financial Literacy Goals",
            "Public Library Research"
        )
    )
    val learnedSignals: StateFlow<List<String>> = _learnedSignals.asStateFlow()

    private val _learnedInterests = MutableStateFlow(
        setOf("Music & 3D Art", "STEM & Coding", "Financial Stability")
    )
    val learnedInterests: StateFlow<Set<String>> = _learnedInterests.asStateFlow()

    private val _learnedLifestyle = MutableStateFlow(
        setOf("Active Creator & Builder", "Veteran Community Leader")
    )
    val learnedLifestyle: StateFlow<Set<String>> = _learnedLifestyle.asStateFlow()

    private val _learnedSchedule = MutableStateFlow(
        setOf("Morning Study & Prep (8–11 AM)", "Evening 3D Studio & Music (6–9 PM)")
    )
    val learnedSchedule: StateFlow<Set<String>> = _learnedSchedule.asStateFlow()

    private val _learnedResearch = MutableStateFlow(
        setOf("AI & Robotics Research", "Personal Finance & Savings", "VA Healthcare & GI Bill")
    )
    val learnedResearch: StateFlow<Set<String>> = _learnedResearch.asStateFlow()

    private val _selectedSports = MutableStateFlow(
        setOf("basketball", "football", "soccer")
    )
    val selectedSports: StateFlow<Set<String>> = _selectedSports.asStateFlow()

    private val _everyLoginSuggestions = MutableStateFlow(
        buildEveryLoginSuggestions(
            displayName = "Jordan Vance",
            role = "veteran",
            interests = setOf("Music & 3D Art", "STEM & Coding", "Financial Stability"),
            lifestyle = setOf("Active Creator & Builder", "Veteran Community Leader"),
            schedule = setOf("Morning Study & Prep (8–11 AM)", "Evening 3D Studio & Music (6–9 PM)"),
            research = setOf("AI & Robotics Research", "Personal Finance & Savings"),
            sports = setOf("basketball", "football", "soccer"),
            musicTrack = "Comfortably Numb - Pink Floyd"
        )
    )
    val everyLoginSuggestions: StateFlow<List<String>> = _everyLoginSuggestions.asStateFlow()

    // User Improvement Ideas Board ("page to make improvement with users Ideas")
    private val _userIdeas = MutableStateFlow(
        listOf(
            UserIdeaProposal(
                id = "idea_1",
                title = "Interactive ZIP Code Homework Study Pods",
                category = "Kids Study & AI Tools",
                description = "Let students in the same ZIP code form safe, moderated study groups at their local public library.",
                authorName = "Maya Lin",
                votes = 24,
                statusBadge = "TOP VOTED"
            ),
            UserIdeaProposal(
                id = "idea_2",
                title = "Veteran-to-Student STEM Mentorship Badges",
                category = "3D Theme & Profile",
                description = "Add 3D holographic profile badges for veterans and adults who mentor students in coding and financial literacy.",
                authorName = "Marcus Sterling",
                votes = 19,
                statusBadge = "COMMUNITY PICK"
            ),
            UserIdeaProposal(
                id = "idea_3",
                title = "Multi-Platform Playlist Sync on 3D Walls",
                category = "YouTube, Music & Games",
                description = "Allow switching between Spotify, Apple Music, YouTube Music, and SoundCloud anthems per mood.",
                authorName = "Elena Rostova",
                votes = 15,
                statusBadge = "LIVE NOW"
            )
        )
    )
    val userIdeas: StateFlow<List<UserIdeaProposal>> = _userIdeas.asStateFlow()

    // Firebase AI Logic Models (Initialized Lazily per firebase-ai-logic-android)
    private val textModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val flashcardSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "cards" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "question" to Schema.string(description = "Clear study question for the student"),
                            "answer" to Schema.string(description = "Concise, accurate answer"),
                            "memoryHint" to Schema.string(description = "Fun mnemonic or memory trick")
                        )
                    ),
                    description = "List of 3 study flashcards"
                )
            )
        )
    }

    private val flashcardModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = flashcardSchema
            }
        )
    }

    private val personalizedInsightSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "insights" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "title" to Schema.string(description = "Action-oriented daily suggestion headline"),
                            "suggestion" to Schema.string(description = "Personalized 1-2 sentence recommendation referencing user interactions"),
                            "category" to Schema.string(description = "Category such as Music, Sports, AI Study, Finance, or Community"),
                            "actionLabel" to Schema.string(description = "Short CTA button label under 22 chars"),
                            "targetTabName" to Schema.string(description = "Target tab key: HUB_3D, NEWS_SPORTS, THEME_PROFILE, RESOURCES, AI_TOOLS, STUDIO_ARCADE, FINANCE_GOALS, or STABILITY_LOUNGE"),
                            "impactScore" to Schema.integer(description = "Engagement match score from 88 to 99")
                        )
                    ),
                    description = "List of 3 personalized daily engagement-driving suggestions"
                )
            )
        )
    }

    private val personalizedInsightModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = personalizedInsightSchema
            }
        )
    }

    private val imageGenModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    private val _aiStudyState = MutableStateFlow<AiTextUiState>(AiTextUiState.Idle)
    val aiStudyState: StateFlow<AiTextUiState> = _aiStudyState.asStateFlow()

    private val _aiFlashcardsState = MutableStateFlow<UiState<List<AiFlashcardItem>>>(
        UiState.Success(
            listOf(
                AiFlashcardItem(
                    question = "What process do plants use to turn sunlight into food?",
                    answer = "Photosynthesis — using sunlight, water, and carbon dioxide to create glucose and oxygen.",
                    memoryHint = "Photo = Light, Synthesis = Putting together!"
                ),
                AiFlashcardItem(
                    question = "How do you add two fractions with different denominators?",
                    answer = "Find a common denominator first, convert the numerators, then add the top numbers.",
                    memoryHint = "Same bottom name before you play the adding game!"
                ),
                AiFlashcardItem(
                    question = "What are the three branches of the U.S. Federal Government?",
                    answer = "Legislative (Congress), Executive (President), and Judicial (Supreme Court).",
                    memoryHint = "L-E-J: Lawmakers, Enforcers, Judges!"
                )
            )
        )
    )
    val aiFlashcardsState: StateFlow<UiState<List<AiFlashcardItem>>> = _aiFlashcardsState.asStateFlow()

    private val _aiAdaptivePlanState = MutableStateFlow<AiTextUiState>(AiTextUiState.Idle)
    val aiAdaptivePlanState: StateFlow<AiTextUiState> = _aiAdaptivePlanState.asStateFlow()

    private val _aiIdeaFeedbackState = MutableStateFlow<AiTextUiState>(AiTextUiState.Idle)
    val aiIdeaFeedbackState: StateFlow<AiTextUiState> = _aiIdeaFeedbackState.asStateFlow()

    private val _aiProfileImageState = MutableStateFlow<AiImageUiState>(AiImageUiState.Idle)
    val aiProfileImageState: StateFlow<AiImageUiState> = _aiProfileImageState.asStateFlow()

    val curatedResources: List<ResourceDirectoryItem> = listOf(
        ResourceDirectoryItem(
            id = "res_commonapp",
            title = "Common App — Apply for College & Universities",
            subtitle = "Official portal to apply to over 1,000 colleges and universities with fee waivers and counselor guides.",
            badge = "APPLY FOR SCHOOL • STUDENT",
            url = "https://www.commonapp.org/",
            roles = listOf("student", "creator", "adult", "veteran"),
            tags = listOf("school", "college", "apply", "student", "admissions", "education")
        ),
        ResourceDirectoryItem(
            id = "res_fafsa",
            title = "FAFSA & Federal Student Financial Aid Application",
            subtitle = "Apply for Pell Grants, scholarships, work-study jobs, and college or trade school financial aid.",
            badge = "SCHOOL AID & GRANTS",
            url = "https://studentaid.gov/",
            roles = listOf("student", "creator", "adult", "veteran"),
            tags = listOf("fafsa", "college", "school", "grants", "education", "student", "apply")
        ),
        ResourceDirectoryItem(
            id = "res_careeronestop",
            title = "CareerOneStop — Apply for Jobs, Careers & Training",
            subtitle = "U.S. Dept. of Labor portal for job applications, career assessments, resume builders, and local job fairs.",
            badge = "APPLY FOR JOBS & CAREERS",
            url = "https://www.careeronestop.org/",
            roles = listOf("student", "creator", "adult", "business", "veteran"),
            tags = listOf("jobs", "career", "apply", "student", "business", "employment", "resume")
        ),
        ResourceDirectoryItem(
            id = "res_usajobs_pathways",
            title = "USAJOBS Pathways — Student Internships & Recent Grad Careers",
            subtitle = "Official federal government paid internships, student jobs, and entry-level career pathways.",
            badge = "STUDENT JOBS & CAREERS",
            url = "https://www.usajobs.gov/Help/working-in-government/unique-hiring-paths/students/",
            roles = listOf("student", "creator", "adult", "veteran"),
            tags = listOf("jobs", "career", "student", "internship", "apply", "government")
        ),
        ResourceDirectoryItem(
            id = "res_apprenticeship",
            title = "Apprenticeship.gov — Earn While You Learn Career Programs",
            subtitle = "Apply for paid apprenticeships in tech, healthcare, engineering, and skilled trades nationwide.",
            badge = "PAID CAREER TRAINING",
            url = "https://www.apprenticeship.gov/",
            roles = listOf("student", "creator", "adult", "business", "veteran"),
            tags = listOf("jobs", "career", "school", "student", "apply", "trades")
        ),
        ResourceDirectoryItem(
            id = "res_openlibrary",
            title = "Open Digital Library, Khan Academy & STEM Learning Hub",
            subtitle = "Free access to millions of school textbooks, AP/SAT study prep, coding courses, and research templates.",
            badge = "SCHOOL & LIBRARY RESOURCES",
            url = "https://openlibrary.org/",
            roles = listOf("student", "creator", "adult", "business", "veteran"),
            tags = listOf("library", "books", "coding", "school", "study", "student", "events")
        ),
        ResourceDirectoryItem(
            id = "res_988",
            title = "988 Suicide & Crisis Lifeline",
            subtitle = "24/7 confidential, free call or text mental health support for everyone.",
            badge = "EMERGENCY • ALL AGES",
            url = "https://988lifeline.org/",
            roles = listOf("student", "veteran", "business", "creator", "adult"),
            tags = listOf("mental health", "crisis", "988", "counseling", "emergency")
        ),
        ResourceDirectoryItem(
            id = "res_va_locations",
            title = "VA Medical Centers & Clinic Locator",
            subtitle = "Find nearby veteran healthcare providers, doctor appointments, and support.",
            badge = "VETERAN HEALTHCARE",
            url = "https://www.va.gov/find-locations/",
            roles = listOf("veteran"),
            tags = listOf("veteran", "doctors", "appointments", "va", "healthcare")
        ),
        ResourceDirectoryItem(
            id = "res_va_housing",
            title = "Veterans Housing & GI Bill Education Assistance",
            subtitle = "Support for housing stability, disability resources, and GI Bill school & career benefits.",
            badge = "VETERAN SUPPORT",
            url = "https://www.va.gov/housing-assistance/",
            roles = listOf("veteran"),
            tags = listOf("veteran", "housing", "homelessness", "gi bill", "benefits", "school")
        ),
        ResourceDirectoryItem(
            id = "res_211",
            title = "211 Community Housing, Food & Utility Hub",
            subtitle = "Connect with local shelter beds, food banks, rent assistance, and utility relief.",
            badge = "HOUSING & ESSENTIAL AID",
            url = "https://www.211.org/",
            roles = listOf("student", "veteran", "business", "creator", "adult"),
            tags = listOf("homelessness", "housing", "food", "bills", "211", "shelter")
        ),
        ResourceDirectoryItem(
            id = "res_sba",
            title = "Small Business Mentorship & Entrepreneurship Guide",
            subtitle = "Free business planning templates, local chamber events, and career workshops.",
            badge = "BUSINESS & CAREERS",
            url = "https://www.sba.gov/",
            roles = listOf("business", "adult", "veteran", "student"),
            tags = listOf("business", "jobs", "career", "entrepreneur", "grants")
        ),
        ResourceDirectoryItem(
            id = "res_healthcare",
            title = "Community Health Center & Doctor Finder",
            subtitle = "Locate sliding-scale clinics, primary care doctors, and wellness checkups.",
            badge = "MEDICAL & DOCTORS",
            url = "https://findahealthcenter.hrsa.gov/",
            roles = listOf("student", "veteran", "business", "creator", "adult"),
            tags = listOf("doctors", "appointments", "medical", "health", "clinic")
        )
    )

    val communityConnections: List<CommunityConnectionCard> = listOf(
        CommunityConnectionCard(
            id = "conn_1",
            name = "Maya Lin",
            role = "Creative Technologist • 27",
            location = "Seattle, WA",
            bio = "Building 3D web art, indie synth music, and community coding workshops.",
            sharedInterests = listOf("Music & 3D Art", "STEM & Coding"),
            themeHex = "#EC4899"
        ),
        CommunityConnectionCard(
            id = "conn_2",
            name = "Marcus Sterling",
            role = "USAF Veteran & Mentor • 32",
            location = "Denver, CO",
            bio = "Helping fellow veterans transition into tech careers and outdoor wellness groups.",
            sharedInterests = listOf("Healthcare & Wellness", "Career & Education"),
            themeHex = "#06B6D4"
        ),
        CommunityConnectionCard(
            id = "conn_3",
            name = "Elena Rostova",
            role = "Social Impact Founder • 29",
            location = "Chicago, IL",
            bio = "Organizing neighborhood book drives, financial literacy circles, and local art nights.",
            sharedInterests = listOf("Financial Stability", "Community Support"),
            themeHex = "#10B981"
        )
    )

    private val _localWallPosts = MutableStateFlow(
        listOf(
            WallPost(
                id = "seed_post_1",
                userId = "local_user",
                authorName = "Jordan Vance",
                authorRole = "veteran",
                category = "Resource Guide",
                title = "Welcome to My WereEquals Hub",
                content = "Just customized my WereEquals basic theme & color, connected Spotify, and explored Student School & Career applications! Remember: We are all equal.",
                likesCount = 12L,
                accentHex = "#2563EB",
                createdAt = Timestamp.now()
            ),
            WallPost(
                id = "seed_post_2",
                userId = "local_user",
                authorName = "Maya Lin",
                authorRole = "creator",
                category = "Studio Art",
                title = "New 6D Holographic Synth & STEM Study Guide",
                content = "Shared flashcards and background synth chords for morning study sessions!",
                likesCount = 8L,
                accentHex = "#EC4899",
                createdAt = Timestamp.now()
            )
        )
    )

    private val _localBudgetEntries = MutableStateFlow(
        listOf(
            BudgetEntry(
                id = "seed_budget_1",
                userId = "local_user",
                title = "Emergency Savings Buffer",
                category = "Save",
                amountCents = 125000L,
                createdAt = Timestamp.now()
            ),
            BudgetEntry(
                id = "seed_budget_2",
                userId = "local_user",
                title = "STEM & Career Certification Goal",
                category = "Goal",
                amountCents = 75000L,
                createdAt = Timestamp.now()
            )
        )
    )

    private val _localInteractions = MutableStateFlow(
        listOf(
            UserInteraction(
                id = "seed_int_1",
                userId = "local_user",
                actionType = "MusicPlay",
                detail = "Queued 'Comfortably Numb - Pink Floyd' in 6D Background Jukebox",
                category = "Music",
                createdAt = Timestamp.now()
            ),
            UserInteraction(
                id = "seed_int_2",
                userId = "local_user",
                actionType = "SportSelect",
                detail = "Pinned Basketball, Football & Soccer live scoreboards to profile",
                category = "Sports",
                createdAt = Timestamp.now()
            ),
            UserInteraction(
                id = "seed_int_3",
                userId = "local_user",
                actionType = "StudySession",
                detail = "Explored AI Robotics & VA Healthcare Grants in Resource Hub",
                category = "Learning",
                createdAt = Timestamp.now()
            ),
            UserInteraction(
                id = "seed_int_4",
                userId = "local_user",
                actionType = "GoalLog",
                detail = "Tracked 49% milestone toward $5,000 Personal Finance Goal",
                category = "Finance",
                createdAt = Timestamp.now()
            )
        )
    )

    private val _localCareerMilestones = MutableStateFlow(
        buildDefaultStudentCareerMilestones("local_user")
    )

    private val _localProfile3DConfigs = MutableStateFlow(
        listOf(
            Profile3DConfig(
                id = "active",
                userId = "local_user",
                configName = "Cosmic Vanguard",
                baseModelId = "cyber_astronaut",
                baseModelName = "Cyber Astronaut",
                primaryColorHex = "#00E5FF",
                secondaryColorHex = "#FF2E93",
                auraColorHex = "#00E676",
                headgearId = "head_crown",
                eyewearId = "eye_visor",
                auraId = "aura_drones",
                rigId = "rig_pauldrons",
                metallicFinish = 0.72,
                scaleFactor = 1.0,
                autoRotate = true,
                turntableYawDeg = 14.0,
                isActive = true
            )
        )
    )
    private val _profile3DSaving = MutableStateFlow(false)
    val profile3DSaving: StateFlow<Boolean> = _profile3DSaving.asStateFlow()

    private val _profile3DStatusBanner = MutableStateFlow<String?>(null)
    val profile3DStatusBanner: StateFlow<String?> = _profile3DStatusBanner.asStateFlow()

    private val _localPersonaStageMap = MutableStateFlow(
        listOf("kid", "teen", "student", "adult", "business", "veteran")
            .associateWith { stageKey -> buildDefaultPersonaStageData(stageKey) }
    )
    private val _personaStageSaving = MutableStateFlow(false)
    val personaStageSaving: StateFlow<Boolean> = _personaStageSaving.asStateFlow()

    private val _personaStageStatusBanner = MutableStateFlow<String?>(null)
    val personaStageStatusBanner: StateFlow<String?> = _personaStageStatusBanner.asStateFlow()

    private val _personalizedInsightsState = MutableStateFlow<UiState<List<PersonalizedDailyInsight>>>(
        UiState.Success(
            listOf(
                PersonalizedDailyInsight(
                    id = "insight_1",
                    title = "Morning AI Study & Library Sprint",
                    suggestion = "Based on your logged StudySession interactions and 8–11 AM schedule, launch a 3-card AI Flashcard sprint on AI & Robotics.",
                    category = "AI Study",
                    actionLabel = "Launch AI Tutor",
                    targetTabName = "AI_TOOLS",
                    impactScore = 98
                ),
                PersonalizedDailyInsight(
                    id = "insight_2",
                    title = "Live Basketball & Football Scoreboard Check",
                    suggestion = "Your Firestore interaction log shows high sports engagement—check today's 4th Quarter & RedZone updates in your News & Sports Hub.",
                    category = "Chosen Sports",
                    actionLabel = "Open Sports Hub",
                    targetTabName = "NEWS_SPORTS",
                    impactScore = 95
                ),
                PersonalizedDailyInsight(
                    id = "insight_3",
                    title = "Unlock Your 50% Savings Milestone Badge",
                    suggestion = "You are just $50 away from crossing the 50% halfway mark on your $5,000 savings goal. Log a contribution today!",
                    category = "Finance Goals",
                    actionLabel = "Track Goal",
                    targetTabName = "FINANCE_GOALS",
                    impactScore = 94
                )
            )
        )
    )
    val personalizedInsightsState: StateFlow<UiState<List<PersonalizedDailyInsight>>> =
        _personalizedInsightsState.asStateFlow()

    private val _localProfile = MutableStateFlow(
        UserProfile(
            userId = "local_user",
            displayName = "Jordan Vance",
            role = "veteran",
            age = 26L,
            location = "Austin, TX",
            bio = "We are all equal. Building community, creativity, and stability.",
            themeColorHex = "#6366F1",
            themeVisualStyle = "HYPERCUBE_6D",
            musicTrackTitle = "Comfortably Numb - Pink Floyd",
            musicPlatform = "Spotify",
            savingsBalanceCents = 245075L,
            savingsGoalCents = 500000L,
            arcadeHighScore = 1250L,
            pledgeAccepted = true,
            interests = listOf("Healthcare & Wellness", "Music & 3D Art", "Financial Stability")
        )
    )

    private val _pendingPersonaRoleOverride = MutableStateFlow<Pair<String, Long>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfileState: StateFlow<UiState<UserProfile?>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(null)
            } else {
                repository.observeUserProfile(uid)
                    .map { profile ->
                        if (profile != null) {
                            val pending = _pendingPersonaRoleOverride.value
                            if (pending == null || profile.role == pending.first) {
                                if (pending != null && profile.role == pending.first) {
                                    _pendingPersonaRoleOverride.value = null
                                }
                                syncFormFromProfile(profile)
                                _localProfile.value = profile
                            } else {
                                val merged = profile.copy(role = pending.first, age = pending.second)
                                syncFormFromProfile(merged)
                                _localProfile.value = merged
                            }
                        }
                        profile
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val rootError = firestoreError ?: error
                        Log.w(TAG, "Error observing user profile", rootError)
                        emit(null)
                    }
            }
        },
        _localProfile,
        _pendingPersonaRoleOverride
    ) { cloudProfile, localFallback, pendingRole ->
        val baseProfile = cloudProfile ?: localFallback
        val resolvedProfile = if (pendingRole != null) {
            baseProfile.copy(role = pendingRole.first, age = pendingRole.second)
        } else {
            baseProfile
        }
        UiState.Success(resolvedProfile)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(_localProfile.value)
    )

    private val _preferredSearchEngine = MutableStateFlow("Google")
    val preferredSearchEngine: StateFlow<String> = _preferredSearchEngine.asStateFlow()

    private val _platformLayoutMode = MutableStateFlow("Balanced")
    val platformLayoutMode: StateFlow<String> = _platformLayoutMode.asStateFlow()

    private val _platformNotificationsEnabled = MutableStateFlow(true)
    val platformNotificationsEnabled: StateFlow<Boolean> = _platformNotificationsEnabled.asStateFlow()

    private val _showMatureLoungeEnabled = MutableStateFlow(true)
    val showMatureLoungeEnabled: StateFlow<Boolean> = _showMatureLoungeEnabled.asStateFlow()

    private val _selectedWalletTier = MutableStateFlow("Personal Digital Wallet")
    val selectedWalletTier: StateFlow<String> = _selectedWalletTier.asStateFlow()

    private val _localBlockedUsers = MutableStateFlow<List<BlockedUser>>(emptyList())
    private val _settingsStatusBanner = MutableStateFlow<String?>(null)
    val settingsStatusBanner: StateFlow<String?> = _settingsStatusBanner.asStateFlow()

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val blockedUsersState: StateFlow<List<BlockedUser>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeBlockedUsers(uid).catch { emit(emptyList()) }
            }
        },
        _localBlockedUsers
    ) { cloudBlocked, localBlocked ->
        (cloudBlocked + localBlocked)
            .distinctBy { it.blockedUserId.ifBlank { it.blockedDisplayName.lowercase() } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val wallPostsState: StateFlow<UiState<List<WallPost>>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeWallPosts(uid).catch { emit(emptyList()) }
            }
        },
        _localWallPosts,
        blockedUsersState
    ) { cloudPosts, localPosts, blockedList ->
        val blockedIds = blockedList.map { it.blockedUserId.lowercase() }.toSet()
        val blockedNames = blockedList.map { it.blockedDisplayName.lowercase() }.toSet()
        val merged = (cloudPosts + localPosts)
            .distinctBy { it.title + it.content }
            .filterNot { post ->
                post.userId.lowercase() in blockedIds ||
                    post.authorName.trim().lowercase() in blockedNames
            }
        UiState.Success(merged)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(_localWallPosts.value)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val budgetEntriesState: StateFlow<UiState<List<BudgetEntry>>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeBudgetEntries(uid).catch { emit(emptyList()) }
            }
        },
        _localBudgetEntries
    ) { cloudEntries, localEntries ->
        val merged = (cloudEntries + localEntries).distinctBy { it.title + it.amountCents }
        UiState.Success(merged)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(_localBudgetEntries.value)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val userInteractionsState: StateFlow<UiState<List<UserInteraction>>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeUserInteractions(uid).catch { emit(emptyList()) }
            }
        },
        _localInteractions
    ) { cloudInteractions, localInteractions ->
        val merged = (cloudInteractions + localInteractions).distinctBy { it.actionType + it.detail }
        UiState.Success(merged)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(_localInteractions.value)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val careerMilestonesState: StateFlow<UiState<List<CareerMilestone>>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeCareerMilestones(uid).catch { emit(emptyList()) }
            }
        },
        _localCareerMilestones
    ) { cloudMilestones, localMilestones ->
        val merged = if (cloudMilestones.isNotEmpty()) {
            cloudMilestones.sortedBy { it.stepOrder }
        } else {
            localMilestones.sortedBy { it.stepOrder }
        }
        UiState.Success(merged)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(_localCareerMilestones.value)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val profile3DConfigsState: StateFlow<List<Profile3DConfig>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeProfile3DConfigs(uid)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.w(TAG, "Error observing 3D profile configs", firestoreError ?: error)
                        emit(emptyList())
                    }
            }
        },
        _localProfile3DConfigs
    ) { cloudConfigs, localConfigs ->
        if (cloudConfigs.isNotEmpty()) cloudConfigs else localConfigs
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = _localProfile3DConfigs.value
    )

    private val _pendingPersonaStages = MutableStateFlow<Map<String, PersonaStageData>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val personaStageDataMapState: StateFlow<Map<String, PersonaStageData>> = combine(
        _authenticatedUid.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                repository.observeAllPersonaStageData(uid)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.w(TAG, "Error observing persona stage data", firestoreError ?: error)
                        emit(emptyList())
                    }
            }
        },
        _localPersonaStageMap,
        _pendingPersonaStages
    ) { cloudStages, localMap, pendingMap ->
        val defaultMap = listOf("kid", "teen", "student", "adult", "business", "veteran")
            .associateWith { stageKey -> buildDefaultPersonaStageData(stageKey) }
        val cloudMap = cloudStages.associateBy {
            normalizePersonaStageId(it.stageId.ifBlank { it.id })
        }
        defaultMap + localMap + cloudMap + pendingMap
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = _localPersonaStageMap.value
    )

    fun advancePageThemeColor() {
        themeCycleIndex = (themeCycleIndex + 1) % pageThemeCycle.size
        visualStyleCycleIndex = (visualStyleCycleIndex + 1) % visualStyleCycle.size
        val nextHex = pageThemeCycle[themeCycleIndex]
        val nextStyle = visualStyleCycle[visualStyleCycleIndex]
        _pageThemeColorHex.value = nextHex
        _themeVisualStyle.value = nextStyle
        _signUpForm.update {
            it.copy(
                themeColorHex = nextHex,
                themeVisualStyle = nextStyle
            )
        }
        _localProfile.update {
            it.copy(
                themeColorHex = nextHex,
                themeVisualStyle = nextStyle
            )
        }
    }

    fun selectPageThemeColor(hex: String) {
        _pageThemeColorHex.value = hex
        val idx = pageThemeCycle.indexOfFirst { it.equals(hex, ignoreCase = true) }
        if (idx >= 0) themeCycleIndex = idx
        updateSignUpThemeColor(hex)
        logUserInteraction("ThemeChange", "Selected Theme Color $hex", "3D Theme")
    }

    fun selectThemeVisualStyle(styleId: String) {
        _themeVisualStyle.value = styleId
        val idx = visualStyleCycle.indexOf(styleId)
        if (idx >= 0) visualStyleCycleIndex = idx
        _signUpForm.update { it.copy(themeVisualStyle = styleId, statusMessage = null) }
        _localProfile.update { it.copy(themeVisualStyle = styleId) }
        logUserInteraction("ThemeChange", "Selected 3D Visual Style: $styleId", "3D Theme")
        persistThemeSelectionToFirestore()
    }

    fun persistThemeSelectionToFirestore() {
        val form = _signUpForm.value
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: _localProfile.value
        val parsedAge = form.ageText.toLongOrNull()?.coerceIn(13L, 120L) ?: 26L
        val profileToSave = currentProfile.copy(
            userId = _authenticatedUid.value ?: currentProfile.userId,
            displayName = form.displayName.ifBlank { "Wondering Explorer" }.take(60),
            role = form.role,
            age = parsedAge,
            location = form.location.ifBlank { "Austin, TX" }.take(80),
            bio = form.bio.ifBlank { "We are all equal." }.take(300),
            themeColorHex = _pageThemeColorHex.value,
            themeVisualStyle = _themeVisualStyle.value,
            isDarkMode = _isDarkMode.value,
            musicTrackTitle = form.musicTrackTitle.take(120),
            musicPlatform = form.musicPlatform.take(40),
            pledgeAccepted = form.pledgeAccepted,
            interests = form.selectedInterests.toList().take(10)
        )
        _localProfile.value = profileToSave
        if (_authenticatedUid.value != null) {
            viewModelScope.launch {
                repository.saveUserProfile(profileToSave)
            }
        }
    }

    fun toggleDarkMode() {
        setDarkMode(!_isDarkMode.value)
    }

    fun setDarkMode(dark: Boolean) {
        if (_isDarkMode.value != dark) {
            _isDarkMode.value = dark
            _localProfile.update { it.copy(isDarkMode = dark) }
            val modeName = if (dark) "Dark Mode" else "Light Mode"
            logUserInteraction("ThemeChange", "Switched System Theme to $modeName", "3D Theme")
            recordLearnedSignal("Switched Theme to $modeName")
            persistThemeSelectionToFirestore()
        }
    }

    fun selectAuthPortalLink(linkKey: String) {
        _authPortalActiveLink.value = linkKey
    }

    fun navigateToLogin() {
        _authBannerMessage.value = null
        _authPortalActiveLink.value = "BOTH"
        advancePageThemeColor()
        refreshEveryLoginSuggestions()
        _navScreen.value = AppNavScreen.LOGIN
    }

    fun navigateToSignUp() {
        _authBannerMessage.value = null
        _authPortalActiveLink.value = "CREATE_ACCOUNT"
        advancePageThemeColor()
        _navScreen.value = AppNavScreen.LOGIN
    }

    fun navigateToMainDashboard(preserveSelectedTheme: Boolean = false) {
        _authBannerMessage.value = null
        if (preserveSelectedTheme) {
            persistThemeSelectionToFirestore()
        }
        refreshEveryLoginSuggestions()
        _navScreen.value = AppNavScreen.MAIN_DASHBOARD
    }

    fun selectDashboardTab(tab: DashboardTab) {
        if (_activeTab.value == tab) return
        _activeTab.value = tab
        logUserInteraction(
            actionType = "FeatureVisit",
            detail = "Opened ${tab.name.replace('_', ' ')} in 6D Hub",
            category = "Navigation"
        )
    }

    fun setAuthBannerMessage(message: String?) {
        _authBannerMessage.value = message
    }

    fun logUserInteraction(actionType: String, detail: String, category: String) {
        val cleanDetail = detail.trim().take(200)
        if (cleanDetail.isEmpty()) return
        val cleanCategory = category.trim().ifEmpty { "General" }.take(50)
        val localItem = UserInteraction(
            id = "local_int_${System.currentTimeMillis()}",
            userId = _authenticatedUid.value ?: "local_user",
            actionType = actionType,
            detail = cleanDetail,
            category = cleanCategory,
            createdAt = Timestamp.now()
        )
        _localInteractions.update { current ->
            (listOf(localItem) + current).distinctBy { it.actionType + it.detail }.take(25)
        }
        recordLearnedSignal("$cleanCategory: ${cleanDetail.take(32)}")
        if (_authenticatedUid.value != null) {
            viewModelScope.launch {
                repository.logUserInteraction(actionType, cleanDetail, cleanCategory)
            }
        }
    }

    fun deleteUserInteraction(interactionId: String) {
        _localInteractions.update { list -> list.filterNot { it.id == interactionId } }
        if (_authenticatedUid.value != null && !interactionId.startsWith("local_int_") && !interactionId.startsWith("seed_int_")) {
            viewModelScope.launch {
                repository.deleteUserInteraction(interactionId)
            }
        }
    }

    fun generateDailyPersonalizedInsightsWithGemini() {
        val form = _signUpForm.value
        val interactions = (userInteractionsState.value as? UiState.Success)?.data.orEmpty()
        val interactionSummary = interactions.take(8).joinToString("; ") {
            "[${it.actionType}/${it.category}] ${it.detail}"
        }
        val sportsSummary = _selectedSports.value.joinToString(", ")
        val interestsSummary = (_learnedInterests.value + _learnedResearch.value).joinToString(", ")

        val prompt = """
            You are the Wondering World Personalized Insight Engine powered by Gemini.
            Analyze this user's Firestore interaction history and profile to generate 3 daily, engagement-driving suggestions for their home screen:
            - User: ${form.displayName}, Persona: ${form.role}, Age: ${form.ageText}, Location: ${form.location}
            - Active Background Song: ${form.musicTrackTitle} (${form.musicPlatform})
            - Chosen Sports: $sportsSummary
            - Learned Interests & Research: $interestsSummary
            - Recent Firestore Interactions Log: $interactionSummary
            
            Return 3 highly engaging, specific daily suggestions with title, suggestion, category, actionLabel, targetTabName (one of HUB_3D, NEWS_SPORTS, THEME_PROFILE, RESOURCES, AI_TOOLS, STUDIO_ARCADE, FINANCE_GOALS, STABILITY_LOUNGE), and impactScore (88..99).
        """.trimIndent()

        viewModelScope.launch {
            _personalizedInsightsState.value = UiState.Loading
            withGlobalNetworkLoading(
                title = "Generating Personalized Daily Insights...",
                subtitle = "Analyzing your recent interactions & goals with Gemini AI",
                category = "Gemini AI Network"
            ) {
                try {
                    val response = withTimeout(45_000L) {
                        personalizedInsightModel.generateContent(prompt)
                    }
                    val jsonText = response.text ?: throw IllegalStateException("Empty Gemini JSON response")
                    val root = JSONObject(jsonText)
                    val arr = root.getJSONArray("insights")
                    val parsed = (0 until arr.length()).map { idx ->
                        val item = arr.getJSONObject(idx)
                        PersonalizedDailyInsight(
                            id = "gemini_insight_${System.currentTimeMillis()}_$idx",
                            title = item.optString("title", "6D Daily Momentum Pick"),
                            suggestion = item.optString("suggestion", "Explore your personalized 6D hub tools today."),
                            category = item.optString("category", "6D Insight"),
                            actionLabel = item.optString("actionLabel", "Open Now").take(22),
                            targetTabName = item.optString("targetTabName", "HUB_3D"),
                            impactScore = item.optInt("impactScore", 96).coerceIn(85, 99)
                        )
                    }
                    if (parsed.isNotEmpty()) {
                        _personalizedInsightsState.value = UiState.Success(parsed)
                    } else {
                        _personalizedInsightsState.value = UiState.Success(buildFallbackPersonalizedInsights())
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "Gemini Personalized Insight fallback activated", e)
                    _personalizedInsightsState.value = UiState.Success(buildFallbackPersonalizedInsights())
                }
            }
        }
    }

    private fun buildFallbackPersonalizedInsights(): List<PersonalizedDailyInsight> {
        val form = _signUpForm.value
        val topInteraction = _localInteractions.value.firstOrNull()?.detail ?: "6D Hub Exploration"
        val topSport = SPORTS_CATALOG.firstOrNull { _selectedSports.value.contains(it.id) }?.name ?: "Basketball"
        val topResearch = _learnedResearch.value.firstOrNull() ?: "AI & Robotics Research"
        return listOf(
            PersonalizedDailyInsight(
                id = "insight_dyn_1_${System.currentTimeMillis()}",
                title = "Continue After: ${topInteraction.take(34)}",
                suggestion = "Gemini noticed your recent activity ('$topInteraction')—jump into AI Study Tutor to generate interactive flashcards on $topResearch.",
                category = "AI Research",
                actionLabel = "Open AI Study",
                targetTabName = "AI_TOOLS",
                impactScore = 98
            ),
            PersonalizedDailyInsight(
                id = "insight_dyn_2_${System.currentTimeMillis()}",
                title = "$topSport Live Scoreboard & Daily Wire",
                suggestion = "Catch the latest $topSport scores and community headlines tailored for ${form.displayName} in your Daily News & Sports Hub.",
                category = "Sports & News",
                actionLabel = "View Scoreboard",
                targetTabName = "NEWS_SPORTS",
                impactScore = 96
            ),
            PersonalizedDailyInsight(
                id = "insight_dyn_3_${System.currentTimeMillis()}",
                title = "6D Soundtrack & Savings Milestone Boost",
                suggestion = "Queue '${form.musicTrackTitle}' in the background while logging your next Personal Finance Goal contribution.",
                category = "Finance & Music",
                actionLabel = "Track Goals",
                targetTabName = "FINANCE_GOALS",
                impactScore = 94
            )
        )
    }

    fun toggleMusicPlayback() {
        _isPlayingMusic.update { !it }
        val currentTrack = _signUpForm.value.musicTrackTitle
        if (_isPlayingMusic.value) {
            logUserInteraction("MusicPlay", "Playing BG Song: ${currentTrack.take(40)}", "Music")
        }
        refreshEveryLoginSuggestions()
    }

    fun selectBackgroundSongPreset(preset: BackgroundSongPreset) {
        updateSignUpMusic(preset.title, preset.defaultPlatform)
        _isPlayingMusic.value = true
        logUserInteraction("MusicPlay", "Selected BG Song: ${preset.title.take(40)}", "Music")
        refreshEveryLoginSuggestions()
    }

    fun toggleFavoriteSport(sportId: String) {
        _selectedSports.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(sportId) && updated.size > 1) updated.remove(sportId) else updated.add(sportId)
            updated
        }
        val sportName = SPORTS_CATALOG.firstOrNull { it.id == sportId }?.name ?: sportId
        logUserInteraction("SportSelect", "Pinned Sport: ${sportName.take(40)}", "Sports")
        refreshEveryLoginSuggestions()
    }

    fun toggleLearnedInterest(item: String) {
        _learnedInterests.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(item) && updated.size > 1) updated.remove(item) else updated.add(item)
            updated
        }
        recordLearnedSignal("Interest: $item")
        refreshEveryLoginSuggestions()
    }

    fun toggleLearnedLifestyle(item: String) {
        _learnedLifestyle.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(item) && updated.size > 1) updated.remove(item) else updated.add(item)
            updated
        }
        recordLearnedSignal("Lifestyle: $item")
        refreshEveryLoginSuggestions()
    }

    fun toggleLearnedSchedule(item: String) {
        _learnedSchedule.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(item) && updated.size > 1) updated.remove(item) else updated.add(item)
            updated
        }
        recordLearnedSignal("Schedule: $item")
        refreshEveryLoginSuggestions()
    }

    fun toggleLearnedResearch(item: String) {
        _learnedResearch.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(item) && updated.size > 1) updated.remove(item) else updated.add(item)
            updated
        }
        recordLearnedSignal("Research: $item")
        refreshEveryLoginSuggestions()
    }

    fun addCustomLearningSignal(category: String, value: String) {
        val clean = value.trim().take(42)
        if (clean.isEmpty()) return
        when (category) {
            "Interest" -> _learnedInterests.update { it + clean }
            "Lifestyle" -> _learnedLifestyle.update { it + clean }
            "Schedule" -> _learnedSchedule.update { it + clean }
            else -> _learnedResearch.update { it + clean }
        }
        recordLearnedSignal("$category: $clean")
        refreshEveryLoginSuggestions()
    }

    fun refreshEveryLoginSuggestions() {
        val form = _signUpForm.value
        val rotatedInterests = _learnedInterests.value.shuffled().toSet()
        val rotatedLifestyle = _learnedLifestyle.value.shuffled().toSet()
        val rotatedSchedule = _learnedSchedule.value.shuffled().toSet()
        val rotatedResearch = _learnedResearch.value.shuffled().toSet()
        val rotatedSports = _selectedSports.value.shuffled().toSet()
        _everyLoginSuggestions.value = buildEveryLoginSuggestions(
            displayName = form.displayName,
            role = form.role,
            interests = rotatedInterests,
            lifestyle = rotatedLifestyle,
            schedule = rotatedSchedule,
            research = rotatedResearch,
            sports = rotatedSports,
            musicTrack = form.musicTrackTitle
        )
    }

    fun rebootPlatform() {
        _authBannerMessage.value = null
        _aiStudyState.value = AiTextUiState.Idle
        _aiAdaptivePlanState.value = AiTextUiState.Idle
        _aiIdeaFeedbackState.value = AiTextUiState.Idle
        advancePageThemeColor()
        refreshEveryLoginSuggestions()
        recordLearnedSignal("Platform Rebooted & Synced")
        syncAllCloudDataAndNetworkRefresh()
    }

    fun updateCustomProfileBitmap(bitmap: Bitmap) {
        _customProfileBitmap.value = bitmap
        recordLearnedSignal("Created Custom Profile Photo")
    }

    fun select3DAvatarPreset(preset: ThreeDAvatarPreset) {
        _customProfileBitmap.value = null
        _selected3DAvatarResId.value = preset.drawableResId
        _selectedAvatarTags.value = preset.tags.toSet()
        _avatarEmojiBadge.value = preset.badgeEmoji
        recordLearnedSignal("Selected 3D Avatar: ${preset.title}")
    }

    fun toggleAvatarDescriptiveTag(tag: String) {
        _selectedAvatarTags.update { current ->
            val updated = current.toMutableSet()
            if (updated.contains(tag)) {
                if (updated.size > 1) updated.remove(tag)
            } else {
                updated.add(tag)
            }
            val bestPreset = findBestMatching3DAvatarPreset(updated)
            _selected3DAvatarResId.value = bestPreset.drawableResId
            _avatarEmojiBadge.value = bestPreset.badgeEmoji
            updated
        }
        recordLearnedSignal("3D Avatar Tag: $tag")
    }

    fun updateAvatarEmojiBadge(emoji: String) {
        _avatarEmojiBadge.value = emoji
    }

    fun recordLearnedSignal(signal: String) {
        val clean = signal.trim().take(48)
        if (clean.isEmpty()) return
        _learnedSignals.update { current ->
            (listOf(clean) + current).distinct().take(10)
        }
        val uid = _authenticatedUid.value ?: return
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        val mergedSignals = (
            listOf(clean) +
                _learnedInterests.value.take(2) +
                _learnedLifestyle.value.take(2) +
                _learnedSchedule.value.take(2) +
                _learnedResearch.value.take(2) +
                currentProfile.interests
            ).distinct().take(10)
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(interests = mergedSignals))
        }
    }

    fun onAuthSessionChanged(uid: String?, defaultDisplayName: String?) {
        val previousUid = _authenticatedUid.value
        _authenticatedUid.value = uid
        if (previousUid != uid) {
            _pendingPersonaRoleOverride.value = null
            _pendingPersonaStages.value = emptyMap()
        }
        if (uid != null) {
            if (!defaultDisplayName.isNullOrBlank() && _signUpForm.value.displayName == "Jordan Vance") {
                _signUpForm.update { it.copy(displayName = defaultDisplayName) }
            }
            if (previousUid != uid) {
                ensureProfileBootstrapped(uid, defaultDisplayName)
            }
            _navScreen.value = AppNavScreen.MAIN_DASHBOARD
        }
    }

    private fun ensureProfileBootstrapped(uid: String, defaultDisplayName: String?) {
        viewModelScope.launch {
            withGlobalNetworkLoading(
                title = "Bootstrapping Cloud Profile...",
                subtitle = "Syncing your WereEqual account & 6D workspace with Firestore",
                category = "Firestore Auth Sync"
            ) {
                val existing = repository.getUserProfile(uid).getOrNull()
                if (existing == null) {
                    val form = _signUpForm.value
                    val parsedAge = form.ageText.toLongOrNull()?.coerceIn(13L, 120L) ?: 26L
                    val newProfile = UserProfile(
                        userId = uid,
                        displayName = form.displayName.ifBlank { defaultDisplayName ?: "Wondering Explorer" },
                        role = form.role,
                        age = parsedAge,
                        location = form.location.ifBlank { "Austin, TX" },
                        bio = form.bio.ifBlank { "We are all equal." },
                        themeColorHex = form.themeColorHex,
                        themeVisualStyle = form.themeVisualStyle,
                        isDarkMode = _isDarkMode.value,
                        musicTrackTitle = form.musicTrackTitle,
                        musicPlatform = form.musicPlatform,
                        savingsBalanceCents = 245075L,
                        savingsGoalCents = 500000L,
                        arcadeHighScore = 0L,
                        pledgeAccepted = true,
                        interests = form.selectedInterests.toList().take(10)
                    )
                    repository.saveUserProfile(newProfile)
                    repository.createWallPost(
                        authorName = newProfile.displayName,
                        authorRole = newProfile.role,
                        category = "Resource Guide",
                        title = "Welcome to My 3D Wondering World Hub",
                        content = "Just customized my 3D profile theme, connected ${newProfile.musicPlatform}, and pinned my favorite community resources! Remember: We are all equal.",
                        accentHex = newProfile.themeColorHex
                    )
                    repository.addBudgetEntry(
                        title = "Emergency Savings Seed",
                        category = "Save",
                        amountCents = 50000L
                    )
                    repository.logUserInteraction(
                        actionType = "FeatureVisit",
                        detail = "Initialized 6D Profile & Personalized Insight Module",
                        category = "Onboarding"
                    )
                }
                repository.seedDefaultStudentCareerMilestonesIfEmpty(uid)
            }
        }
    }

    private fun syncFormFromProfile(profile: UserProfile) {
        _pageThemeColorHex.value = profile.themeColorHex
        _themeVisualStyle.value = profile.themeVisualStyle.ifBlank { "HYPERCUBE_6D" }
        _isDarkMode.value = profile.isDarkMode
        _signUpForm.update { current ->
            current.copy(
                displayName = profile.displayName,
                ageText = profile.age.toString(),
                location = profile.location,
                bio = profile.bio,
                role = profile.role,
                themeColorHex = profile.themeColorHex,
                themeVisualStyle = profile.themeVisualStyle.ifBlank { "HYPERCUBE_6D" },
                musicTrackTitle = profile.musicTrackTitle,
                musicPlatform = profile.musicPlatform,
                selectedInterests = profile.interests.toSet(),
                pledgeAccepted = profile.pledgeAccepted
            )
        }
    }

    fun updateSignUpDisplayName(value: String) {
        _signUpForm.update { it.copy(displayName = value, statusMessage = null) }
        if (value.isNotBlank()) {
            _localProfile.update { it.copy(displayName = value.take(60)) }
        }
    }

    fun updateSignUpAge(value: String) {
        val filtered = value.filter { it.isDigit() }.take(3)
        _signUpForm.update { it.copy(ageText = filtered, statusMessage = null) }
        val parsed = filtered.toLongOrNull()
        if (parsed != null) {
            _localProfile.update { it.copy(age = parsed.coerceIn(13L, 120L)) }
        }
    }

    fun updateSignUpLocation(value: String) {
        _signUpForm.update { it.copy(location = value, statusMessage = null) }
        if (value.isNotBlank()) {
            _localProfile.update { it.copy(location = value.take(80)) }
        }
        val uid = _authenticatedUid.value ?: return
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        if (value.isNotBlank() && value.length <= 80) {
            viewModelScope.launch {
                repository.saveUserProfile(currentProfile.copy(location = value))
            }
        }
    }

    fun updateSignUpBio(value: String) {
        _signUpForm.update { it.copy(bio = value, statusMessage = null) }
        if (value.isNotBlank()) {
            _localProfile.update { it.copy(bio = value.take(300)) }
        }
    }

    fun updateSignUpRole(role: String) {
        _signUpForm.update { it.copy(role = role, statusMessage = null) }
        _localProfile.update { it.copy(role = role) }
    }

    fun updateSignUpThemeColor(hex: String) {
        _pageThemeColorHex.value = hex
        _signUpForm.update { it.copy(themeColorHex = hex, statusMessage = null) }
        _localProfile.update { it.copy(themeColorHex = hex) }
        persistThemeSelectionToFirestore()
    }

    fun updateSignUpMusic(trackTitle: String, platform: String) {
        _signUpForm.update {
            it.copy(
                musicTrackTitle = trackTitle,
                musicPlatform = platform,
                statusMessage = null
            )
        }
        _localProfile.update {
            it.copy(
                musicTrackTitle = trackTitle.take(120),
                musicPlatform = platform.take(40)
            )
        }
        val uid = _authenticatedUid.value ?: return
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.saveUserProfile(
                currentProfile.copy(
                    musicTrackTitle = trackTitle.take(120),
                    musicPlatform = platform.take(40)
                )
            )
        }
    }

    fun toggleSignUpInterest(interest: String) {
        _signUpForm.update { current ->
            val updated = current.selectedInterests.toMutableSet()
            if (updated.contains(interest)) {
                updated.remove(interest)
            } else if (updated.size < 10) {
                updated.add(interest)
            }
            current.copy(selectedInterests = updated, statusMessage = null)
        }
    }

    fun updatePledgeAccepted(accepted: Boolean) {
        _signUpForm.update { it.copy(pledgeAccepted = accepted, statusMessage = null) }
    }

    fun saveProfileFromSignUpForm(onComplete: () -> Unit = {}) {
        val form = _signUpForm.value
        if (!form.pledgeAccepted) {
            _signUpForm.update {
                it.copy(statusMessage = "Please check 'I Agree to the Wondering World Code of Conduct' to create or save your profile page.")
            }
            return
        }
        val uid = _authenticatedUid.value
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: _localProfile.value
        val parsedAge = form.ageText.toLongOrNull()?.coerceIn(13L, 120L) ?: 26L
        val updatedProfile = UserProfile(
            userId = uid ?: currentProfile.userId,
            displayName = form.displayName.ifBlank { "Wondering Explorer" }.take(60),
            role = form.role,
            age = parsedAge,
            location = form.location.ifBlank { "Austin, TX" }.take(80),
            bio = form.bio.ifBlank { "We are all equal." }.take(300),
            themeColorHex = form.themeColorHex,
            themeVisualStyle = form.themeVisualStyle,
            isDarkMode = _isDarkMode.value,
            musicTrackTitle = form.musicTrackTitle.ifBlank { "Comfortably Numb - Pink Floyd" }.take(120),
            musicPlatform = form.musicPlatform.ifBlank { "Spotify" }.take(40),
            savingsBalanceCents = currentProfile.savingsBalanceCents,
            savingsGoalCents = currentProfile.savingsGoalCents,
            arcadeHighScore = currentProfile.arcadeHighScore,
            pledgeAccepted = true,
            interests = form.selectedInterests.map { it.take(50) }.take(10)
        )
        _localProfile.value = updatedProfile
        logUserInteraction(
            actionType = "FeatureVisit",
            detail = "Agreed to Code of Conduct & Saved Profile (${updatedProfile.displayName})",
            category = "Profile"
        )
        if (uid == null) {
            _signUpForm.update { it.copy(isSaving = true, statusMessage = null) }
            viewModelScope.launch {
                withGlobalNetworkLoading(
                    title = "Saving WereEqual Profile...",
                    subtitle = "Syncing ${updatedProfile.displayName}'s profile & theme settings",
                    category = "Profile Sync"
                ) {
                    delay(350L)
                }
                _signUpForm.update {
                    it.copy(
                        isSaving = false,
                        statusMessage = "Code of Conduct accepted & Profile saved!"
                    )
                }
                onComplete()
            }
            return
        }
        _signUpForm.update { it.copy(isSaving = true, statusMessage = null) }
        viewModelScope.launch {
            val result = withGlobalNetworkLoading(
                title = "Saving Profile to Cloud...",
                subtitle = "Persisting ${updatedProfile.displayName}'s profile to Firestore",
                category = "Firestore Database"
            ) {
                repository.saveUserProfile(updatedProfile)
            }
            _signUpForm.update {
                it.copy(
                    isSaving = false,
                    statusMessage = if (result.isSuccess) "Code of Conduct accepted & Profile saved!" else "Saved locally! (${result.exceptionOrNull()?.localizedMessage ?: "Cloud sync pending"})"
                )
            }
            onComplete()
        }
    }

    fun switchPersonaQuick(role: String, defaultAge: Long) {
        val coercedAge = defaultAge.coerceIn(13L, 120L)
        _pendingPersonaRoleOverride.value = role to coercedAge
        _signUpForm.update { it.copy(role = role, ageText = coercedAge.toString()) }
        _localProfile.update { it.copy(role = role, age = coercedAge) }
        recordLearnedSignal("Age Group ($coercedAge) • ${role.uppercase()}")
        val uid = _authenticatedUid.value ?: return
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: _localProfile.value
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(userId = uid, role = role, age = coercedAge))
        }
    }

    fun createPost(category: String, title: String, content: String) {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data
        val form = _signUpForm.value
        val validCategory = if (category in listOf("Update", "Resource Guide", "Studio Art", "Milestone")) {
            category
        } else {
            "Update"
        }
        val cleanTitle = title.ifBlank { "Community Update" }.take(100)
        val cleanContent = content.ifBlank { cleanTitle }.take(1000)
        val localPost = WallPost(
            id = "local_post_${System.currentTimeMillis()}",
            userId = _authenticatedUid.value ?: "local_user",
            authorName = (currentProfile?.displayName ?: form.displayName).ifBlank { "Explorer" }.take(60),
            authorRole = (currentProfile?.role ?: form.role).ifBlank { "adult" }.take(30),
            category = validCategory,
            title = cleanTitle,
            content = cleanContent,
            likesCount = 1L,
            accentHex = currentProfile?.themeColorHex ?: form.themeColorHex,
            createdAt = Timestamp.now()
        )
        _localWallPosts.update { listOf(localPost) + it }
        addCurrencyTransaction(
            title = "Published Helpful $validCategory Post: ${cleanTitle.take(28)}",
            amount = 25L,
            isEarned = true,
            category = "Helpful Post"
        )
        _currencyStatusBanner.value = "🌟 Earned +25 EQC for sharing '$cleanTitle' on the Community Wall!"
        logUserInteraction("FeatureVisit", "Posted $validCategory: ${cleanTitle.take(40)}", "Social Wall")
        viewModelScope.launch {
            withGlobalNetworkLoading(
                title = "Publishing to Community Wall...",
                subtitle = "Broadcasting '$cleanTitle' ($validCategory)",
                category = "Social Wall Sync"
            ) {
                if (_authenticatedUid.value != null) {
                    repository.createWallPost(
                        authorName = localPost.authorName,
                        authorRole = localPost.authorRole,
                        category = validCategory,
                        title = cleanTitle,
                        content = cleanContent,
                        accentHex = localPost.accentHex
                    )
                }
            }
        }
    }

    fun likePost(post: WallPost) {
        _localWallPosts.update { list ->
            list.map { if (it.id == post.id) it.copy(likesCount = it.likesCount + 1L) else it }
        }
        addCurrencyTransaction(
            title = "Rewarded Helpful Post by ${post.authorName}",
            amount = 10L,
            isEarned = true,
            category = "Post Appreciation"
        )
        _currencyStatusBanner.value = "👍 Endorsed ${post.authorName}'s helpful post (+10 EQC Community Bonus)!"
        if (_authenticatedUid.value != null && !post.id.startsWith("local_post_") && !post.id.startsWith("seed_post_")) {
            viewModelScope.launch {
                repository.incrementPostLikes(post)
            }
        }
    }

    fun deletePost(postId: String) {
        _localWallPosts.update { list -> list.filterNot { it.id == postId } }
        if (_authenticatedUid.value != null && !postId.startsWith("local_post_") && !postId.startsWith("seed_post_")) {
            viewModelScope.launch {
                repository.deleteWallPost(postId)
            }
        }
    }

    fun addBudgetEntryAndUpdateBalance(title: String, category: String, amountDollars: Double) {
        addBudgetItem(title, category, amountDollars)
    }

    fun addBudgetItem(title: String, category: String, amountDollars: Double) {
        val cents = (amountDollars * 100).toLong().coerceIn(100L, 100000000L)
        val currentProfile = (userProfileState.value as? UiState.Success)?.data
        val localEntry = BudgetEntry(
            id = "local_budget_${System.currentTimeMillis()}",
            userId = _authenticatedUid.value ?: "local_user",
            title = title.ifBlank { "Savings Contribution" }.take(80),
            category = category,
            amountCents = cents,
            createdAt = Timestamp.now()
        )
        _localBudgetEntries.update { listOf(localEntry) + it }
        logUserInteraction("GoalLog", "Logged $category ($${amountDollars.toInt()}): ${title.take(36)}", "Finance")
        viewModelScope.launch {
            withGlobalNetworkLoading(
                title = "Updating Financial Vault...",
                subtitle = "Recording $category entry: ${localEntry.title}",
                category = "Finance Vault Sync"
            ) {
                if (_authenticatedUid.value != null) {
                    val result = repository.addBudgetEntry(title.take(80), category, cents)
                    if (result.isSuccess && currentProfile != null) {
                        val delta = when (category) {
                            "Save", "Goal" -> cents
                            "Spend", "Give" -> -cents
                            else -> 0L
                        }
                        val newBalance = (currentProfile.savingsBalanceCents + delta).coerceAtLeast(0L)
                        repository.saveUserProfile(currentProfile.copy(savingsBalanceCents = newBalance))
                    }
                }
            }
        }
    }

    fun deleteBudgetItem(entryId: String) {
        _localBudgetEntries.update { list -> list.filterNot { it.id == entryId } }
        if (_authenticatedUid.value != null && !entryId.startsWith("local_budget_") && !entryId.startsWith("seed_budget_")) {
            viewModelScope.launch {
                repository.deleteBudgetEntry(entryId)
            }
        }
    }

    fun updateSavingsGoal(goalDollars: Double) {
        val goalCents = (goalDollars * 100).toLong().coerceIn(10000L, 1000000000L)
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: _localProfile.value
        val updated = currentProfile.copy(savingsGoalCents = goalCents)
        _localProfile.value = updated
        if (_authenticatedUid.value != null) {
            viewModelScope.launch {
                repository.saveUserProfile(updated)
            }
        }
    }

    fun recordArcadeScore(score: Long) {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: _localProfile.value
        if (score > currentProfile.arcadeHighScore) {
            val updated = currentProfile.copy(arcadeHighScore = score)
            _localProfile.value = updated
            if (_authenticatedUid.value != null) {
                viewModelScope.launch {
                    repository.saveUserProfile(updated)
                }
            }
        }
    }

    // --- Student Career Portal Vertical Timeline Milestone Actions (Firestore-Backed) ---

    fun addCareerMilestoneObjective(
        title: String,
        stage: String,
        description: String,
        targetTerm: String,
        xpReward: Long,
        portalUrl: String
    ) {
        val cleanTitle = title.trim().ifBlank { "Complete Career Portfolio Milestone" }.take(120)
        val cleanDesc = description.trim().ifBlank { "Actionable student milestone objective tracked in the Career Portal." }.take(400)
        val cleanStage = stage.ifBlank { "Academic" }
        val cleanTerm = targetTerm.trim().ifBlank { "This Semester" }.take(60)
        val cleanUrl = portalUrl.trim().ifBlank { "https://www.careeronestop.org/" }.take(300)
        val coercedXp = xpReward.coerceIn(10L, 5000L)
        val nextOrder = ((_localCareerMilestones.value.maxOfOrNull { it.stepOrder } ?: 0L) + 1L).coerceIn(1L, 100L)

        val localItem = CareerMilestone(
            id = "local_ms_${System.currentTimeMillis()}",
            userId = _authenticatedUid.value ?: "local_user",
            title = cleanTitle,
            stage = cleanStage,
            description = cleanDesc,
            targetTerm = cleanTerm,
            status = "In Progress",
            xpReward = coercedXp,
            stepOrder = nextOrder,
            portalUrl = cleanUrl,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        _localCareerMilestones.update { current ->
            (current + localItem).sortedBy { it.stepOrder }
        }
        logUserInteraction("GoalLog", "Added Career Milestone: ${cleanTitle.take(40)}", "Career Portal")
        recordLearnedSignal("Career Milestone: ${cleanTitle.take(28)}")

        viewModelScope.launch {
            withGlobalNetworkLoading(
                title = "Saving Career Milestone...",
                subtitle = "Adding '$cleanTitle' (+${coercedXp} XP) to your Career Portal",
                category = "Career Portal Sync"
            ) {
                if (_authenticatedUid.value != null) {
                    repository.addCareerMilestone(
                        title = cleanTitle,
                        stage = cleanStage,
                        description = cleanDesc,
                        targetTerm = cleanTerm,
                        status = "In Progress",
                        xpReward = coercedXp,
                        stepOrder = nextOrder,
                        portalUrl = cleanUrl
                    )
                }
            }
        }
    }

    fun updateCareerMilestoneObjectiveStatus(milestone: CareerMilestone, newStatus: String) {
        val validStatus = if (newStatus in listOf("Upcoming", "In Progress", "Completed")) newStatus else "In Progress"
        _localCareerMilestones.update { list ->
            list.map { item ->
                if (item.id == milestone.id || item.title == milestone.title) {
                    item.copy(status = validStatus, updatedAt = Timestamp.now())
                } else {
                    item
                }
            }
        }
        logUserInteraction(
            actionType = "GoalLog",
            detail = "Marked '${milestone.title.take(36)}' as $validStatus (+${milestone.xpReward} XP)",
            category = "Career Portal"
        )
        if (_authenticatedUid.value != null && !milestone.id.startsWith("local_ms_")) {
            viewModelScope.launch {
                repository.updateCareerMilestoneStatus(milestone.id, validStatus)
            }
        }
    }

    fun deleteCareerMilestoneObjective(milestoneId: String) {
        _localCareerMilestones.update { list -> list.filterNot { it.id == milestoneId } }
        if (_authenticatedUid.value != null && !milestoneId.startsWith("local_ms_")) {
            viewModelScope.launch {
                repository.deleteCareerMilestone(milestoneId)
            }
        }
    }

    // --- 3D Profile Customizer Firestore Persistence Actions ---

    fun saveProfile3DConfig(config: Profile3DConfig, docId: String = "active") {
        val uid = _authenticatedUid.value
        val targetDocId = docId.trim().ifEmpty { "active" }
        val normalized = config.copy(
            id = targetDocId,
            userId = uid ?: "local_user",
            isActive = targetDocId == "active"
        )
        _localProfile3DConfigs.update { current ->
            val withoutTarget = current.filterNot { it.id == targetDocId }
            listOf(normalized) + withoutTarget
        }
        if (targetDocId == "active") {
            _pageThemeColorHex.value = normalized.primaryColorHex
            _signUpForm.update { it.copy(themeColorHex = normalized.primaryColorHex) }
            _localProfile.update { it.copy(themeColorHex = normalized.primaryColorHex) }
        }
        logUserInteraction(
            actionType = "ThemeChange",
            detail = "Saved 3D Profile '${normalized.configName}' (${normalized.baseModelName})",
            category = "3D Profile"
        )
        recordLearnedSignal("3D Model: ${normalized.baseModelName}")

        if (uid == null) {
            _profile3DStatusBanner.value = "✓ Saved '${normalized.configName}' locally (Sign in with Google to sync to Cloud Firestore)"
            return
        }

        viewModelScope.launch {
            _profile3DSaving.value = true
            _profile3DStatusBanner.value = null
            val result = withGlobalNetworkLoading(
                title = "Saving 3D Profile Configuration...",
                subtitle = "Persisting ${normalized.baseModelName} (${normalized.configName}) to Firestore",
                category = "Firestore 3D Profile"
            ) {
                repository.saveProfile3DConfig(normalized)
            }
            _profile3DSaving.value = false
            _profile3DStatusBanner.value = if (result.isSuccess) {
                "☁️ Saved '${normalized.configName}' (${normalized.baseModelName}) to Cloud Firestore!"
            } else {
                "⚠️ Cloud save failed: ${result.exceptionOrNull()?.localizedMessage ?: "Permission or network error"}"
            }
        }
    }

    fun deleteProfile3DConfig(configId: String) {
        if (configId.isBlank() || configId == "active") return
        _localProfile3DConfigs.update { list -> list.filterNot { it.id == configId } }
        val uid = _authenticatedUid.value
        if (uid == null) {
            _profile3DStatusBanner.value = "Removed local 3D preset."
            return
        }
        viewModelScope.launch {
            val result = repository.deleteProfile3DConfig(configId)
            _profile3DStatusBanner.value = if (result.isSuccess) {
                "Deleted 3D preset from Cloud Firestore."
            } else {
                "⚠️ Could not delete preset: ${result.exceptionOrNull()?.localizedMessage ?: "Error"}"
            }
        }
    }

    // --- Persona Dashboard Stage-Specific Firestore Persistence Actions ---

    fun savePersonaStageData(data: PersonaStageData) {
        val uid = _authenticatedUid.value
        val normalizedStageId = normalizePersonaStageId(data.stageId.ifBlank { data.id })
        val stageLabel = when (normalizedStageId) {
            "kid" -> "Kid"
            "teen" -> "Teen"
            "student" -> "Student"
            "business" -> "Business"
            "veteran" -> "Veteran"
            else -> "Adult"
        }
        val normalized = data.copy(
            id = normalizedStageId,
            userId = uid ?: "local_user",
            stageId = normalizedStageId,
            stageLabel = stageLabel
        )
        _localPersonaStageMap.update { current ->
            current + (normalizedStageId to normalized)
        }
        _pendingPersonaStages.update { current ->
            current + (normalizedStageId to normalized)
        }
        logUserInteraction(
            actionType = "GoalLog",
            detail = "Updated $stageLabel Stage Dashboard (${normalized.stageScoreOrXp} XP)",
            category = "Persona Dashboard"
        )
        recordLearnedSignal("$stageLabel Stage: ${normalized.focusGoal.take(26)}")

        if (uid == null) {
            _personaStageStatusBanner.value = "✓ Saved $stageLabel stage draft locally (Sign in with Google to sync to Cloud Firestore)"
            return
        }

        viewModelScope.launch {
            _personaStageSaving.value = true
            _personaStageStatusBanner.value = null
            val result = repository.savePersonaStageData(normalized)
            _personaStageSaving.value = false
            if (result.isSuccess) {
                _pendingPersonaStages.update { current -> current - normalizedStageId }
                _personaStageStatusBanner.value = "☁️ Saved $stageLabel stage progress to Cloud Firestore!"
            } else {
                _personaStageStatusBanner.value = "✓ Updated locally • Cloud sync pending (${result.exceptionOrNull()?.localizedMessage ?: "Offline"})"
            }
        }
    }

    // User Ideas Board Actions
    fun submitUserIdea(title: String, category: String, description: String) {
        val author = (userProfileState.value as? UiState.Success)?.data?.displayName
            ?: _signUpForm.value.displayName
        val newIdea = UserIdeaProposal(
            id = "idea_${System.currentTimeMillis()}",
            title = title,
            category = category,
            description = description,
            authorName = author,
            votes = 1,
            statusBadge = "NEW USER IDEA"
        )
        _userIdeas.update { listOf(newIdea) + it }
        recordLearnedSignal("Proposed Idea: ${title.take(24)}")
        createPost(
            category = "Milestone",
            title = "User Improvement Idea: ${title.take(70)}",
            content = "[$category] $description"
        )
    }

    fun upvoteUserIdea(ideaId: String) {
        _userIdeas.update { list ->
            list.map { if (it.id == ideaId) it.copy(votes = it.votes + 1) else it }
        }
    }

    // --- Firebase AI Logic Operations (Gemini 3.8 Flash & Gemini Nano Banana 2.1) ---

    fun askAiStudyTutor(
        topic: String,
        subject: String,
        studyMode: String,
        gradeLevel: String
    ) {
        recordLearnedSignal("AI Study: $subject (${topic.take(22)})")
        val prompt = """
            You are the Wondering World Kid-Safe AI Study Tutor & Schoolwork Research Coach ("We are all equal").
            Student Grade/Age Level: $gradeLevel
            Subject: $subject
            Requested Study Mode: $studyMode
            Student Topic / Question: $topic
            
            Provide an encouraging, clear, age-appropriate $studyMode response:
            - Break concepts down into easy-to-follow steps
            - Include a memorable real-world example or memory trick
            - Suggest 2 keywords the student can look up at their local public library or OpenLibrary.
        """.trimIndent()

        viewModelScope.launch {
            _aiStudyState.value = AiTextUiState.Loading
            withGlobalNetworkLoading(
                title = "AI Study Tutor Thinking...",
                subtitle = "Streaming $studyMode guide for '$topic' ($subject)",
                category = "Gemini AI Tutor"
            ) {
                val accumulated = StringBuilder()
                try {
                    withTimeout(60_000L) {
                        textModel.generateContentStream(prompt).collect { chunk ->
                            val partText = chunk.text.orEmpty()
                            accumulated.append(partText)
                            _aiStudyState.value = AiTextUiState.Streaming(accumulated.toString())
                        }
                    }
                    if (accumulated.isNotBlank()) {
                        _aiStudyState.value = AiTextUiState.Success(accumulated.toString())
                    } else {
                        _aiStudyState.value = AiTextUiState.Error("Model returned an empty study response.")
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "AI Study Tutor timed out", e)
                    _aiStudyState.value = AiTextUiState.Error("Study Tutor timed out after 60 seconds. Please try again.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "AI Study Tutor generation failed", e)
                    _aiStudyState.value = AiTextUiState.Error(e.localizedMessage ?: "AI Study Tutor generation failed.")
                }
            }
        }
    }

    fun generateStudyFlashcards(topic: String, gradeLevel: String) {
        recordLearnedSignal("Flashcards: ${topic.take(25)}")
        val prompt = "Create 3 study flashcards with question, answer, and memoryHint about '$topic' tailored for $gradeLevel."
        viewModelScope.launch {
            _aiFlashcardsState.value = UiState.Loading
            withGlobalNetworkLoading(
                title = "Generating AI Study Flashcards...",
                subtitle = "Building 3 interactive flashcards on '$topic' ($gradeLevel)",
                category = "Gemini AI Flashcards"
            ) {
                try {
                    val response = withTimeout(60_000L) {
                        flashcardModel.generateContent(prompt)
                    }
                    val jsonString = response.text ?: throw IllegalStateException("Model returned empty JSON.")
                    val json = JSONObject(jsonString)
                    val cardsArray = json.getJSONArray("cards")
                    val parsedCards = (0 until cardsArray.length()).map { idx ->
                        val obj = cardsArray.getJSONObject(idx)
                        AiFlashcardItem(
                            question = obj.getString("question"),
                            answer = obj.getString("answer"),
                            memoryHint = obj.getString("memoryHint")
                        )
                    }
                    _aiFlashcardsState.value = UiState.Success(parsedCards)
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "Flashcard generation timed out", e)
                    _aiFlashcardsState.value = UiState.Error("Flashcard generation timed out after 60 seconds.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to generate flashcards JSON", e)
                    _aiFlashcardsState.value = UiState.Error(e.localizedMessage ?: "Failed to generate flashcards.")
                }
            }
        }
    }

    fun generateAdaptiveLearningPlan() {
        val form = _signUpForm.value
        val interestsText = (_learnedInterests.value + form.selectedInterests).distinct().joinToString(", ")
        val lifestyleText = _learnedLifestyle.value.joinToString(", ")
        val scheduleText = _learnedSchedule.value.joinToString(", ")
        val researchText = _learnedResearch.value.joinToString(", ")
        val recentSignals = _learnedSignals.value.joinToString(", ")
        val prompt = """
            You are the Wondering World Adaptive Profile Intelligence Engine ("The platform learns from each user").
            User Profile: ${form.displayName}, Age ${form.ageText}, Persona: ${form.role}, Location: ${form.location}.
            Background Profile Song: ${form.musicTrackTitle} (${form.musicPlatform}).
            1. Learned User Interests: $interestsText
            2. Learned Lifestyle & Rhythm: $lifestyleText
            3. Learned Daily Schedule: $scheduleText
            4. Learned Research Focus: $researchText
            5. Recent Platform Activity Signals: $recentSignals
            
            Create an inspiring, structured 4-part Personalized Daily Blueprint tailored to this user:
            - Part 1: Personalized Interest & Creative Soundtrack Match
            - Part 2: Lifestyle-Aligned Wellness & Community Connection Tip
            - Part 3: Optimized Daily Schedule Block Plan
            - Part 4: Next-Step Research & Resource Discovery Roadmap
        """.trimIndent()

        viewModelScope.launch {
            _aiAdaptivePlanState.value = AiTextUiState.Loading
            withGlobalNetworkLoading(
                title = "Building Adaptive Learning Plan...",
                subtitle = "Synthesizing personalized 4-part blueprint for ${form.displayName}",
                category = "Gemini Adaptive AI"
            ) {
                val accumulated = StringBuilder()
                try {
                    withTimeout(60_000L) {
                        textModel.generateContentStream(prompt).collect { chunk ->
                            accumulated.append(chunk.text.orEmpty())
                            _aiAdaptivePlanState.value = AiTextUiState.Streaming(accumulated.toString())
                        }
                    }
                    if (accumulated.isNotBlank()) {
                        _aiAdaptivePlanState.value = AiTextUiState.Success(accumulated.toString())
                    } else {
                        _aiAdaptivePlanState.value = AiTextUiState.Error("Model returned an empty adaptive plan.")
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "Adaptive learning plan timed out", e)
                    _aiAdaptivePlanState.value = AiTextUiState.Error("Adaptive plan timed out after 60 seconds.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Adaptive learning plan failed", e)
                    _aiAdaptivePlanState.value = AiTextUiState.Error(e.localizedMessage ?: "Failed to generate adaptive plan.")
                }
            }
        }
    }

    fun expandUserIdeaWithAi(title: String, category: String, description: String) {
        val prompt = """
            A Wondering World community member submitted this platform improvement idea:
            Title: $title
            Category: $category
            Description: $description
            
            Expand this user idea into an exciting 3-part Feature Blueprint (1. How It Works, 2. Why It Helps Kids/Veterans/Community, 3. Safety & Equality Design).
        """.trimIndent()

        viewModelScope.launch {
            _aiIdeaFeedbackState.value = AiTextUiState.Loading
            withGlobalNetworkLoading(
                title = "Expanding Community Idea with AI...",
                subtitle = "Creating 3-part feature blueprint for '$title'",
                category = "Gemini AI Blueprint"
            ) {
                val accumulated = StringBuilder()
                try {
                    withTimeout(60_000L) {
                        textModel.generateContentStream(prompt).collect { chunk ->
                            accumulated.append(chunk.text.orEmpty())
                            _aiIdeaFeedbackState.value = AiTextUiState.Streaming(accumulated.toString())
                        }
                    }
                    if (accumulated.isNotBlank()) {
                        _aiIdeaFeedbackState.value = AiTextUiState.Success(accumulated.toString())
                    } else {
                        _aiIdeaFeedbackState.value = AiTextUiState.Error("Model returned an empty blueprint.")
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "Idea blueprint timed out", e)
                    _aiIdeaFeedbackState.value = AiTextUiState.Error("AI Idea Blueprint timed out after 60 seconds.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Idea blueprint generation failed", e)
                    _aiIdeaFeedbackState.value = AiTextUiState.Error(e.localizedMessage ?: "Failed to expand idea.")
                }
            }
        }
    }

    fun generateAiProfilePhoto(promptText: String) {
        viewModelScope.launch {
            _aiProfileImageState.value = AiImageUiState.Loading
            withGlobalNetworkLoading(
                title = "Generating 3D AI Profile Artwork...",
                subtitle = "Rendering custom portrait for '$promptText'",
                category = "Gemini Vision Studio"
            ) {
                try {
                    val bitmap = withTimeout(60_000L) {
                        val response = imageGenModel.generateContent(promptText)
                        val imagePart = response.candidates.firstOrNull()
                            ?.content?.parts?.filterIsInstance<ImagePart>()
                            ?.firstOrNull()
                        imagePart?.image
                    }
                    if (bitmap != null) {
                        _customProfileBitmap.value = bitmap
                        _aiProfileImageState.value = AiImageUiState.Success(bitmap)
                        recordLearnedSignal("Generated AI 3D Profile Photo")
                    } else {
                        _aiProfileImageState.value = AiImageUiState.Error("No image was returned by the model.")
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "AI profile photo timed out", e)
                    _aiProfileImageState.value = AiImageUiState.Error("Image generation timed out after 60 seconds.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "AI profile photo generation failed", e)
                    _aiProfileImageState.value = AiImageUiState.Error(e.localizedMessage ?: "Image generation failed.")
                }
            }
        }
    }
    private val _friendsState = MutableStateFlow<UiState<List<Friend>>>(UiState.Loading)
    val friendsState: StateFlow<UiState<List<Friend>>> = _friendsState.asStateFlow()

    private val _incomingRequestsState = MutableStateFlow<UiState<List<FriendRequest>>>(UiState.Loading)
    val incomingRequestsState: StateFlow<UiState<List<FriendRequest>>> = _incomingRequestsState.asStateFlow()

    private val _discoveredUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val discoveredUsers: StateFlow<List<UserProfile>> = _discoveredUsers.asStateFlow()

    private val _notificationEvents = kotlinx.coroutines.flow.MutableSharedFlow<String>(extraBufferCapacity = 1)
    val notificationEvents = _notificationEvents.asSharedFlow()

    private var lastKnownRequestIds = setOf<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val friendsJob = _authenticatedUid.flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList<Friend>()) else repository.observeFriends(uid)
    }.onEach { friends: List<Friend> -> 
        _friendsState.value = UiState.Success(friends) 
    }.launchIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val incomingRequestsJob = _authenticatedUid.flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList<FriendRequest>()) else repository.observeIncomingFriendRequests(uid)
    }.onEach { requests: List<FriendRequest> ->
        val currentIds = requests.map { it.id }.toSet()
        val newIds = currentIds - lastKnownRequestIds
        if (newIds.isNotEmpty() && lastKnownRequestIds.isNotEmpty()) {
            // Trigger notifications for new requests
            requests.filter { it.id in newIds }.forEach { req ->
                _notificationEvents.tryEmit(req.fromName)
            }
        }
        lastKnownRequestIds = currentIds
        _incomingRequestsState.value = UiState.Success(requests)
    }.launchIn(viewModelScope)

    fun navigateToFriendsDiscovery() {
        _navScreen.value = AppNavScreen.FRIENDS_DISCOVERY
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _discoveredUsers.value = emptyList()
            return
        }
        viewModelScope.launch {
            withGlobalNetworkLoading("Searching users...", "Finding '$query' on WereEqual") {
                val result = repository.searchUsersByNameOrEmail(query)
                if (result.isSuccess) {
                    _discoveredUsers.value = result.getOrDefault(emptyList())
                }
            }
        }
    }

    fun syncContacts(context: Context) {
        viewModelScope.launch {
            withGlobalNetworkLoading("Syncing Contacts...", "Looking for friends on WereEqual") {
                val phones = mutableListOf<String>()
                try {
                    val cursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null
                    )
                    cursor?.use {
                        val phoneIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        while (it.moveToNext()) {
                            phones.add(it.getString(phoneIdx).replace(Regex("[^0-9+]"), ""))
                        }
                    }
                } catch (_: Exception) {}
                
                val found = mutableListOf<UserProfile>()
                phones.distinct().take(50).forEach { phone ->
                    repository.searchUsersByPhone(phone).getOrNull()?.let { found.add(it) }
                }
                _discoveredUsers.value = found.distinctBy { it.userId }
                recordLearnedSignal("Synced ${phones.size} contacts & found ${found.size} friends")
            }
        }
    }

    fun updatePhoneNumber(phone: String) {
        viewModelScope.launch {
            repository.updateUserPhoneNumber(phone)
            _signUpForm.update { it.copy(statusMessage = "Phone number updated for discovery!") }
        }
    }

    fun sendFriendRequest(toUserId: String, toName: String) {
        val form = _signUpForm.value
        val myName = (userProfileState.value as? UiState.Success)?.data?.displayName ?: form.displayName
        val myAvatar = avatarEmojiBadge.value
        viewModelScope.launch {
            repository.sendFriendRequest(toUserId, myName, myAvatar)
            recordLearnedSignal("Sent friend request to $toName")
        }
    }

    fun acceptFriendRequest(request: FriendRequest) {
        val form = _signUpForm.value
        val profile = (userProfileState.value as? UiState.Success)?.data
        val myName = profile?.displayName ?: form.displayName
        val myAvatar = avatarEmojiBadge.value
        val myRole = profile?.role ?: form.role
        viewModelScope.launch {
            repository.acceptFriendRequest(request, myName, myAvatar, myRole)
            recordLearnedSignal("Accepted friend request from ${request.fromName}")
        }
    }

    fun declineFriendRequest(requestId: String) {
        viewModelScope.launch {
            repository.declineFriendRequest(requestId)
        }
    }

    fun reportContent(targetId: String, targetType: String, reason: String, detail: String) {
        viewModelScope.launch {
            withGlobalNetworkLoading("Submitting Report...", "Our moderation team will review this shortly") {
                val result = repository.reportContent(targetId, targetType, reason, detail)
                if (result.isSuccess) {
                    _signUpForm.update { it.copy(statusMessage = "Thank you. Your report has been submitted.") }
                }
            }
        }
    }

    fun triggerFriendRequestNotification(context: Context, fromName: String) {
        NotificationHelper.showFriendRequestNotification(context, fromName)
    }

    fun connectSpotify(spotifyUserId: String) {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(
                spotifyConnected = true,
                spotifyUserId = spotifyUserId
            ))
            _signUpForm.update { it.copy(statusMessage = "Spotify account connected!") }
        }
    }

    fun disconnectSpotify() {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(
                spotifyConnected = false,
                spotifyUserId = ""
            ))
        }
    }

    fun connectAppleMusic(storefront: String) {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(
                appleMusicConnected = true,
                appleMusicStorefront = storefront
            ))
            _signUpForm.update { it.copy(statusMessage = "Apple Music connected!") }
        }
    }

    fun disconnectAppleMusic() {
        val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProfile.copy(
                appleMusicConnected = false,
                appleMusicStorefront = ""
            ))
        }
    }

    // --- Platform Preferences, User Blocking & Account Deletion ---

    fun updatePreferredSearchEngine(engine: String) {
        _preferredSearchEngine.value = engine
        _settingsStatusBanner.value = "✓ Search platform changed to $engine."
        recordLearnedSignal("Platform search changed to $engine")
    }

    fun updatePlatformLayoutMode(mode: String) {
        _platformLayoutMode.value = mode
        _settingsStatusBanner.value = "✓ Platform layout density set to $mode mode."
    }

    fun setPlatformNotificationsEnabled(enabled: Boolean) {
        _platformNotificationsEnabled.value = enabled
        _settingsStatusBanner.value = if (enabled) {
            "✓ Platform notifications & community alerts enabled."
        } else {
            "🔕 Platform notifications muted."
        }
    }

    fun setShowMatureLoungeEnabled(enabled: Boolean) {
        _showMatureLoungeEnabled.value = enabled
        _settingsStatusBanner.value = if (enabled) {
            "✓ 18+ Stability Lounge access enabled (age-verified profiles)."
        } else {
            "🛡️ 18+ Stability Lounge hidden on platform."
        }
    }

    fun selectWalletTier(tier: String) {
        _selectedWalletTier.value = tier
        _settingsStatusBanner.value = "💳 Wallet setup updated to $tier."
        logUserInteraction("FinanceLiteracy", "Selected wallet tier: $tier", "Wallet")
    }

    fun clearSettingsStatusBanner() {
        _settingsStatusBanner.value = null
    }

    fun blockCommunityUser(
        targetUserId: String,
        targetDisplayName: String,
        reason: String = "Boundary Preference"
    ) {
        val cleanName = targetDisplayName.trim().ifEmpty { "Community Member" }.take(60)
        val cleanId = targetUserId.trim().ifEmpty {
            cleanName.lowercase().replace(Regex("[^a-z0-9_-]"), "_").take(50)
        }
        val uid = _authenticatedUid.value
        if (cleanId == uid) {
            _settingsStatusBanner.value = "You cannot block your own account."
            return
        }

        val entry = BlockedUser(
            id = cleanId,
            userId = uid ?: "local_user",
            blockedUserId = cleanId,
            blockedDisplayName = cleanName,
            reason = reason.trim().ifEmpty { "Boundary Preference" }.take(100),
            createdAt = Timestamp.now()
        )
        _localBlockedUsers.update { existing ->
            (listOf(entry) + existing).distinctBy { it.blockedUserId }
        }
        _discoveredUsers.update { list ->
            list.filterNot { it.userId == cleanId || it.displayName.equals(cleanName, ignoreCase = true) }
        }
        _settingsStatusBanner.value = "🚫 Blocked $cleanName. Their posts & invites are now hidden."
        recordLearnedSignal("Blocked user $cleanName")

        if (uid != null) {
            viewModelScope.launch {
                repository.blockUser(cleanId, cleanName, entry.reason)
            }
        }
    }

    fun unblockCommunityUser(blockedUserId: String) {
        _localBlockedUsers.update { list ->
            list.filterNot { it.blockedUserId == blockedUserId || it.id == blockedUserId }
        }
        _settingsStatusBanner.value = "✓ User unblocked."
        if (_authenticatedUid.value != null) {
            viewModelScope.launch {
                repository.unblockUser(blockedUserId)
            }
        }
    }

    fun deleteUserAccountPermanently(
        credentialManager: CredentialManager,
        scope: kotlinx.coroutines.CoroutineScope
    ) {
        if (_isDeletingAccount.value) return
        viewModelScope.launch {
            _isDeletingAccount.value = true
            _settingsStatusBanner.value = "Deleting your cloud records and account..."
            val uid = _authenticatedUid.value
            if (uid != null) {
                val dataResult = repository.deleteUserAccountData()
                if (dataResult.isFailure) {
                    _isDeletingAccount.value = false
                    _settingsStatusBanner.value = "Could not delete cloud data: ${dataResult.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                    return@launch
                }
                AuthManager.deleteCurrentFirebaseAuthUser(
                    credentialManager = credentialManager,
                    scope = scope,
                    onDeleted = {
                        _isDeletingAccount.value = false
                        _localBlockedUsers.value = emptyList()
                        _localWallPosts.value = emptyList()
                        _localBudgetEntries.value = emptyList()
                        onAuthSessionChanged(null, null)
                        _authBannerMessage.value = "Your account and all personal data have been permanently deleted."
                        _navScreen.value = AppNavScreen.LOGIN
                    },
                    onRequiresRecentLogin = { message ->
                        _isDeletingAccount.value = false
                        _settingsStatusBanner.value = message
                    },
                    onError = { errMsg ->
                        _isDeletingAccount.value = false
                        _settingsStatusBanner.value = "Account deletion error: $errMsg"
                    }
                )
            } else {
                _isDeletingAccount.value = false
                _localBlockedUsers.value = emptyList()
                _authBannerMessage.value = "Local session and preferences cleared."
                _navScreen.value = AppNavScreen.LOGIN
            }
        }
    }

    private companion object {
        const val TAG = "WonderingWorldVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
