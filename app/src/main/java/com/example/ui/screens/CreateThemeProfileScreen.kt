package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.AiImageUiState
import com.example.ui.AiTextUiState
import com.example.ui.SignUpFormState
import com.example.ui.components.AVATAR_DESCRIPTIVE_TAG_GROUPS
import com.example.ui.components.AdultProfileDatingHubCard
import com.example.ui.components.BackgroundSongPreset
import com.example.ui.components.CreativePhotoStudioCard
import com.example.ui.components.GoogleMapsSearchYouTubeCodingThemeWidget
import com.example.ui.components.ProfileAdaptiveLearningCard
import com.example.ui.components.ProfileBackgroundMusicJukeboxCard
import com.example.ui.components.ProfileCodeOfConductAgreementCard
import com.example.ui.components.ProfileSportsSelectorCard
import com.example.ui.components.ProfileYouTubeReelsAndGamesHubCard
import com.example.ui.components.THREE_D_AVATAR_PRESETS
import com.example.ui.components.THREE_D_THEME_VISUAL_STYLES
import com.example.ui.components.ThreeDAvatarPreset
import com.example.ui.components.ThreeDProfileThemePresetsToggleCard
import com.example.ui.components.ThreeDThemeStyleSelectorMenuCard
import com.example.ui.components.ThreeDimensionalBackground
import com.example.ui.components.build3DAvatarPromptFromTags
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.findSecondaryAccentForHex
import com.example.ui.theme.parseThemeHex
import com.example.R

val ALL_MUSIC_PLATFORMS = listOf(
    "Spotify",
    "Apple Music",
    "YouTube Music",
    "SoundCloud",
    "Amazon Music",
    "Tidal",
    "Pandora",
    "Deezer",
    "Bandcamp",
    "Audiomack",
    "iHeartRadio"
)

fun buildMusicPlatformSearchUrl(platform: String, trackQuery: String): String {
    val encoded = Uri.encode(trackQuery.ifBlank { "Comfortably Numb Pink Floyd" })
    return when (platform) {
        "Apple Music" -> "https://music.apple.com/us/search?term=$encoded"
        "YouTube Music" -> "https://music.youtube.com/search?q=$encoded"
        "SoundCloud" -> "https://soundcloud.com/search?q=$encoded"
        "Amazon Music" -> "https://music.amazon.com/search/$encoded"
        "Tidal" -> "https://listen.tidal.com/search?q=$encoded"
        "Pandora" -> "https://www.pandora.com/search/$encoded/all"
        "Deezer" -> "https://www.deezer.com/search/$encoded"
        "Bandcamp" -> "https://bandcamp.com/search?q=$encoded"
        "Audiomack" -> "https://audiomack.com/search?q=$encoded"
        "iHeartRadio" -> "https://www.iheart.com/search/?q=$encoded"
        else -> "https://open.spotify.com/search/$encoded"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateThemeProfileScreen(
    formState: SignUpFormState,
    customProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int? = null,
    selectedAvatarTags: Set<String> = emptySet(),
    avatarEmojiBadge: String,
    aiImageState: AiImageUiState,
    isPlayingMusic: Boolean,
    isDarkMode: Boolean = true,
    onSetDarkMode: (Boolean) -> Unit = {},
    selectedSportsIds: Set<String> = emptySet(),
    learnedInterests: Set<String> = emptySet(),
    learnedLifestyle: Set<String> = emptySet(),
    learnedSchedule: Set<String> = emptySet(),
    learnedResearch: Set<String> = emptySet(),
    learnedSignals: List<String> = emptyList(),
    aiAdaptivePlanState: AiTextUiState = AiTextUiState.Idle,
    onDisplayNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onRoleSelect: (String) -> Unit,
    onThemeColorSelect: (String) -> Unit,
    onThemeVisualStyleSelect: (String) -> Unit = {},
    onMusicChange: (String, String) -> Unit,
    onSelectBackgroundSongPreset: (BackgroundSongPreset) -> Unit = {},
    onToggleMusic: () -> Unit,
    onToggleSport: (String) -> Unit = {},
    onToggleLearnedInterest: (String) -> Unit = {},
    onToggleLearnedLifestyle: (String) -> Unit = {},
    onToggleLearnedSchedule: (String) -> Unit = {},
    onToggleLearnedResearch: (String) -> Unit = {},
    onAddCustomLearningSignal: (String, String) -> Unit = { _, _ -> },
    onGenerateAdaptiveLearningPlan: () -> Unit = {},
    onUpdateProfileBitmap: (Bitmap) -> Unit,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit = {},
    onToggleAvatarTag: (String) -> Unit = {},
    onUpdateAvatarBadge: (String) -> Unit,
    onGenerateAiProfilePhoto: (String) -> Unit,
    onPledgeChange: (Boolean) -> Unit = {},
    onSaveProfile: () -> Unit,
    onPublishPostToDashboard: (title: String, body: String) -> Unit = { _, _ -> },
    onOpenDashboardFeed: () -> Unit = {},
    backgroundTransparency: Float = 0.45f,
    onTransparencyChange: (Float) -> Unit = {},
    onOpenMaintenancePage: () -> Unit = {},
    spotifyConnected: Boolean = false,
    appleMusicConnected: Boolean = false,
    onConnectSpotify: () -> Unit = {},
    onConnectAppleMusic: () -> Unit = {},
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
    var selected3DRoomStyle by remember { mutableStateOf("Cyber Grid 3D") }
    var aiPhotoPrompt by remember {
        mutableStateOf("Futuristic 3D neon hologram portrait avatar, vibrant cyberpunk lighting, digital art")
    }
    var saveFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Zero-Permission Android Photo Picker per Google Play Policy
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                    if (decoded != null) {
                        onUpdateProfileBitmap(decoded)
                        saveFeedbackMessage = "Loaded your selected photo into your 3D Profile!"
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    // Camera Snapshot Launcher
    val cameraSnapshotLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onUpdateProfileBitmap(bitmap)
            saveFeedbackMessage = "Captured your new profile photo!"
        }
    }

    val themeSwatches = EXPANDED_THEME_COLOR_OPTIONS.map { it.hex to it.name }

    val roomStyles = listOf(
        "Cyber Grid 3D",
        "Aurora Nebula",
        "Solar Rings",
        "MySpace Retro"
    )

    val avatarBadges = listOf("🚀", "🎵", "🌟", "🛡️", "🎓", "🎨", "⚡", "🌍")
    val activeAccent = parseThemeHex(formState.themeColorHex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("create_theme_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 00-DarkMode. System-Wide Material 3 Dark Mode & Solid Color Toggle
        com.example.ui.components.SystemWideDarkModeControlCard(
            isDarkMode = isDarkMode,
            onSetDarkMode = onSetDarkMode,
            backgroundTransparency = backgroundTransparency,
            onTransparencyChange = onTransparencyChange,
            currentThemeHex = formState.themeColorHex,
            onSelectThemeColor = onThemeColorSelect
        )

        // 0A. Background Transparency Bar + Real 360° Color Wheel & Maintenance Page Shortcut
        BackgroundTransparencyControlBarCard(
            backgroundTransparency = backgroundTransparency,
            onTransparencyChange = onTransparencyChange,
            currentThemeHex = formState.themeColorHex,
            onSelectColorHex = onThemeColorSelect,
            onOpenMaintenancePage = onOpenMaintenancePage
        )

        // 0A-Persona. Persona-Based Profile System (Veteran, Business, Kid, Teen, Adult, Student)
        com.example.ui.components.PersonaProfileSystemCard(
            currentRole = formState.role,
            currentAgeText = formState.ageText,
            currentVisualStyleId = formState.themeVisualStyle,
            currentThemeHex = formState.themeColorHex,
            selectedInterests = formState.selectedInterests + learnedInterests,
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
            onSharePersonaActionToWall = onPublishPostToDashboard
        )

        // 0A-3DAvatarCustomizer. Interactive Compose 3D Avatar Identity Customizer (Accessories & Multi-Zone Colors)
        com.example.ui.components.ThreeDAvatarCustomizerCard(
            userDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            currentThemeHex = formState.themeColorHex,
            currentAvatarEmoji = avatarEmojiBadge,
            onApplyPrimaryThemeHex = onThemeColorSelect,
            onApplyAvatarBadgeEmoji = onUpdateAvatarBadge,
            onApplyCustomizedAvatarBitmap = onUpdateProfileBitmap,
            onToggleAvatarAccessoryTag = onToggleAvatarTag,
            onShareAvatarToWall = onPublishPostToDashboard
        )

        // 0A-Currency. Virtual Currency System (EqualCoins / EQC - Earn, Spend on Profile Themes/Items & Tip Peers)
        com.example.ui.components.VirtualCurrencySystemCard(
            balance = virtualCurrencyBalance,
            dailyBonusClaimed = dailyBonusClaimed,
            streakDays = loginStreakDays,
            claimedBadgeIds = claimedBadgeIds,
            unlockedItemIds = unlockedShopItemIds,
            transactions = currencyTransactions,
            statusBanner = currencyStatusBanner,
            accentColor = activeAccent,
            onClaimDailyLoginBonus = onClaimDailyLoginBonus,
            onRewardHelpfulPostBonus = onRewardHelpfulPostBonus,
            onClaimBadgeQuest = onClaimBadgeQuest,
            onPurchaseOrApplyShopItem = onPurchaseOrApplyShopItem,
            onTipCommunityUser = onTipCommunityUser
        )

        // 0A-Achievements. Achievement & Gamification System + Community Leaderboards
        com.example.ui.components.AchievementGamificationLeaderboardCard(
            userDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            userRole = formState.role,
            userAvatarEmoji = avatarEmojiBadge,
            accentColor = activeAccent,
            onEquipBadgeToProfile = onUpdateAvatarBadge,
            onAwardEqualCoinsBonus = { onRewardHelpfulPostBonus() },
            onShareAchievementToWall = onPublishPostToDashboard
        )

        // 0A-ImageVoiceStudio. Create & Edit Images (Nano Banana 2.1) + Live Voice Conversations
        com.example.ui.components.AiImageAndVoiceConversationStudioCard(
            currentProfileBitmap = customProfileBitmap,
            aiProfileImageState = aiImageState,
            onGenerateNewImageFromPrompt = onGenerateAiProfilePhoto,
            onApplyGeneratedBitmapToProfile = onUpdateProfileBitmap,
            onShareCreationToWall = onPublishPostToDashboard
        )

        // 00. 3D Profile Theme Presets Quick Toggle Card in Profile Settings
        ThreeDProfileThemePresetsToggleCard(
            currentVisualStyleId = formState.themeVisualStyle,
            currentThemeHex = formState.themeColorHex,
            isDarkMode = isDarkMode,
            backgroundTransparency = backgroundTransparency,
            onSelectVisualStyle = onThemeVisualStyleSelect,
            onSelectThemeColor = onThemeColorSelect,
            onTransparencyChange = onTransparencyChange,
            onSetDarkMode = onSetDarkMode,
            onPersistThemeToFirestore = onSaveProfile
        )

        // 00B. Platform URL & Deep Link Builder Card for Profile
        com.example.ui.components.PlatformUrlGeneratorCard(
            userDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            currentVisualStyleId = formState.themeVisualStyle,
            currentThemeHex = formState.themeColorHex,
            onPublishUrlToCommunityWall = onPublishPostToDashboard
        )

        // 00C. Daily Horoscopes & 6D Cosmic Zodiac Hub
        com.example.ui.components.DailyHoroscopesCosmicCard(
            onApplyCosmicTheme = { visualStyleId, colorHex, zodiacEmoji ->
                onThemeVisualStyleSelect(visualStyleId)
                onThemeColorSelect(colorHex)
                onUpdateAvatarBadge(zodiacEmoji)
            },
            onShareHoroscopeToWall = onPublishPostToDashboard
        )

        // 00D. Financial Tip of the Day (Persona-Based Daily Literacy Snippet)
        com.example.ui.components.FinancialTipOfTheDayCard(
            userRole = formState.role,
            userAge = formState.ageText.toLongOrNull() ?: 26L,
            onShareTipToWall = onPublishPostToDashboard
        )

        // 00E. Compliant Educational Digital Asset & Market Literacy Simulator
        com.example.ui.components.DigitalAssetMarketSimulatorCard(
            equalCoinsBalance = virtualCurrencyBalance,
            onShareSimulationToWall = onPublishPostToDashboard
        )

        // 00F. WereEqual Donation & Creator Support Page
        com.example.ui.components.WereEqualDonationAndCreatorSupportCard(
            donorDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            onTipEqualCoinsToCreatorOrCharity = { recipientName, eqcAmount, note ->
                onTipCommunityUser(recipientName, eqcAmount, note)
            },
            onEquipSupporterBadge = onUpdateAvatarBadge,
            onShareDonationToWall = onPublishPostToDashboard
        )

        // 0. 3D Profile Theme Visual Style, 32-Color Selection & Create Your Own Custom Profile Theme Studio
        ThreeDThemeStyleSelectorMenuCard(
            currentVisualStyleId = formState.themeVisualStyle,
            currentThemeHex = formState.themeColorHex,
            isDarkMode = isDarkMode,
            onSelectVisualStyle = onThemeVisualStyleSelect,
            onSelectThemeColor = onThemeColorSelect,
            onPersistThemeToFirestore = onSaveProfile,
            userDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            backgroundTransparency = backgroundTransparency,
            onTransparencyChange = onTransparencyChange,
            onSetDarkMode = onSetDarkMode,
            onShareCustomThemeToWall = onPublishPostToDashboard
        )

        // 0.1. Multi-Tool Widget: Google Maps, Google Search, YouTube & Coding Theme Creation
        GoogleMapsSearchYouTubeCodingThemeWidget(
            currentLocation = formState.location,
            currentThemeHex = formState.themeColorHex,
            currentVisualStyleId = formState.themeVisualStyle,
            backgroundTransparency = backgroundTransparency,
            isDarkMode = isDarkMode,
            onApplyCodedThemeColor = onThemeColorSelect,
            onApplyCodedVisualStyle = onThemeVisualStyleSelect,
            onApplyCodedTransparency = onTransparencyChange,
            onSetDarkMode = onSetDarkMode,
            onShareToWall = onPublishPostToDashboard
        )

        // 0.2. Adult Profile Dating & Matchmaking Hub (Strictly 18+ Verified)
        AdultProfileDatingHubCard(
            userDisplayName = formState.displayName.ifBlank { "Jordan Vance" },
            userAge = formState.ageText.toLongOrNull() ?: 26L,
            userRole = formState.role,
            userLocation = formState.location,
            onSwitchToAdultProfile = {
                onAgeChange("26")
                onRoleSelect("adult")
            },
            onShareMatchMilestoneToWall = onPublishPostToDashboard
        )

        // 0B. Creative Photo Studio (Camera Integration + Persona Filters & Overlays + Social Posts)
        CreativePhotoStudioCard(
            displayName = formState.displayName.ifBlank { "Wondering Explorer" },
            currentProfileBitmap = customProfileBitmap,
            selected3DAvatarResId = selected3DAvatarResId,
            userPersona = formState.role,
            onApplyFilteredBitmapToProfile = { filteredBmp ->
                onUpdateProfileBitmap(filteredBmp)
                saveFeedbackMessage = "Applied filtered camera photo to your 3D Profile!"
            },
            onPublishPhotoToSocialPost = { title, caption, _ ->
                onPublishPostToDashboard(title, caption)
                saveFeedbackMessage = "Published Studio Camera Photo '$title' to Community Social Feed!"
            }
        )

        // 0C. Profile YouTube Player, Watch & Make Reels Video Studio & Teen/Kid All-Platform Games Hub
        ProfileYouTubeReelsAndGamesHubCard(
            displayName = formState.displayName.ifBlank { "Jordan Vance" },
            currentRole = formState.role,
            currentAge = formState.ageText.toLongOrNull() ?: 16L,
            onSwitchToTeenOrKidProfile = { role, age ->
                onRoleSelect(role)
                onAgeChange(age.toString())
                saveFeedbackMessage = "Switched to ${role.uppercase()} (Age $age) with all Teen & Kid Game Platforms unlocked!"
            },
            onPublishVideoOrReelToDashboard = { title, body ->
                onPublishPostToDashboard(title, body)
                saveFeedbackMessage = "Published '$title' to your Community Dashboard Post Feed!"
            },
            onOpenFullDashboardFeed = onOpenDashboardFeed
        )

        // 1. Live 3D Theme Profile Preview Card (Dynamically Updated by Chosen Palette & 3D Style)
        val activeSecondary = findSecondaryAccentForHex(formState.themeColorHex)
        val active3DStyleTitle = THREE_D_THEME_VISUAL_STYLES.firstOrNull {
            it.id == formState.themeVisualStyle
        }?.title ?: "WereEqual Classic Theme"

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(listOf(activeAccent, activeSecondary, CyberEmerald))
            ),
            modifier = Modifier.testTag("live_3d_theme_profile_preview_card")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 3D Room Atmosphere Banner with Live ThreeDimensionalBackground Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(114.dp)
                ) {
                    ThreeDimensionalBackground(
                        accentHex = formState.themeColorHex,
                        visualStyleId = formState.themeVisualStyle,
                        isDarkMode = isDarkMode,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.62f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "LIVE 3D PROFILE • ${active3DStyleTitle.uppercase()} • ${formState.themeColorHex.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        listOf(activeAccent, activeSecondary, CyberEmerald).forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(swatch)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
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
                        // Profile Photo / 3D Avatar Portrait Box
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(activeAccent.copy(alpha = 0.22f))
                                .border(2.5.dp, activeAccent, RoundedCornerShape(22.dp))
                                .testTag("theme_profile_avatar_preview"),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                customProfileBitmap != null -> {
                                    Image(
                                        bitmap = customProfileBitmap.asImageBitmap(),
                                        contentDescription = "Custom Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                selected3DAvatarResId != null -> {
                                    Image(
                                        painter = painterResource(id = selected3DAvatarResId),
                                        contentDescription = "Selected 3D Avatar Portrait",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = avatarEmojiBadge,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                            text = formState.displayName.take(2).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = activeAccent,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = avatarEmojiBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$avatarEmojiBadge ${formState.displayName.ifBlank { "Wondering Explorer" }}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "${formState.role.uppercase()} • AGE ${formState.ageText.ifBlank { "26" }} • ${formState.location.ifBlank { "Austin, TX" }}",
                                style = MaterialTheme.typography.labelLarge,
                                color = HoloCyan
                            )
                            Text(
                                text = formState.bio.ifBlank { "We are all equal." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Embedded Music Anthem Preview
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = CyberEmerald
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = formState.musicTrackTitle.ifBlank { "Comfortably Numb - Pink Floyd" },
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Anthem on ${formState.musicPlatform}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberEmerald
                                )
                            }
                            IconButton(onClick = onToggleMusic) {
                                Icon(
                                    imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause Profile Anthem",
                                    tint = CyberEmerald
                                )
                            }
                            IconButton(
                                onClick = {
                                    try {
                                        val url = buildMusicPlatformSearchUrl(
                                            formState.musicPlatform,
                                            formState.musicTrackTitle
                                        )
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (_: Exception) {
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open in ${formState.musicPlatform}",
                                    tint = HoloCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1C. Personality-Based AI Avatar Synthesis (Requested Feature)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("personality_avatar_synthesis_card")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ai_avatar_synthesis_banner),
                        contentDescription = "AI Synthesis Lab",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 20.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "GEMINI AI SYNTHESIS LAB",
                            style = MaterialTheme.typography.labelSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Personality-Based 3D Avatar Synthesis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Mix your unique personality traits to synthesize a high-fidelity 3D avatar that reflects who you are. Powered by Gemini Nano Banana 2.1.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Trait Selection Section
                    val traitGroup = AVATAR_DESCRIPTIVE_TAG_GROUPS.find { it.first == "Personality Traits" }
                    if (traitGroup != null) {
                        Text(
                            text = "1. Select Your Core Traits:",
                            style = MaterialTheme.typography.labelLarge,
                            color = HoloCyan
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            traitGroup.second.forEach { trait ->
                                val isSelected = selectedAvatarTags.contains(trait)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onToggleAvatarTag(trait) },
                                    label = { Text(trait) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Optional: Style Mixers
                    Text(
                        text = "2. Blend 3D Style & Lighting:",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonPink
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AVATAR_DESCRIPTIVE_TAG_GROUPS.find { it.first == "3D Render & Lighting" }?.second?.forEach { tag ->
                            val isSelected = selectedAvatarTags.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onToggleAvatarTag(tag) },
                                label = { Text(tag) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val finalPrompt = build3DAvatarPromptFromTags(selectedAvatarTags)
                            onGenerateAiProfilePhoto(finalPrompt)
                        },
                        enabled = aiImageState !is AiImageUiState.Loading && selectedAvatarTags.isNotEmpty(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(54.dp).testTag("synthesize_personality_avatar_button")
                    ) {
                        if (aiImageState is AiImageUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Synthesizing 3D Personality Avatar...", fontWeight = FontWeight.Black)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesize Custom 3D Avatar", fontWeight = FontWeight.Black)
                        }
                    }

                    if (aiImageState is AiImageUiState.Success) {
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "✓ Synthesis Complete: Your personality-based 3D avatar has been rendered and applied!",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberEmerald,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. 3D-Style AI Avatar Portrait Gallery & Descriptive Tag Studio
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, NeonPink.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("three_d_avatar_selector_card")
        ) {
            val tagBuiltPrompt = build3DAvatarPromptFromTags(selectedAvatarTags)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "3D Avatar Portraits",
                        tint = NeonPink
                    )
                    Column {
                        Text(
                            text = "Select 3D-Style Avatar Portrait & Descriptive Tags",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Choose an AI-generated 3D character portrait below or mix descriptive tags to synthesize a custom 3D avatar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Interactive 3D Base Character Model Selection Studio for Stages Of Life Personas
                com.example.ui.components.Persona3DAvatarSelectionCard(
                    currentPersonaRole = formState.role,
                    selected3DAvatarResId = selected3DAvatarResId,
                    onSelect3DAvatarPreset = onSelect3DAvatarPreset,
                    onSelectPersonaStage = { newStage ->
                        onRoleSelect(newStage)
                    },
                    onEquipBadgeEmoji = onUpdateAvatarBadge,
                    currentVisualThemeId = formState.themeVisualStyle,
                    onSelectVisualTheme = { themeId, primaryHex ->
                        onThemeVisualStyleSelect(themeId)
                        onThemeColorSelect(primaryHex)
                    },
                    onSaveAvatarToFirestore = onSaveProfile
                )

                // 2x2 Grid of AI-Generated 3D-Style Avatar Portraits
                THREE_D_AVATAR_PRESETS.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPresets.forEach { preset ->
                            val isSelected = customProfileBitmap == null && selected3DAvatarResId == preset.drawableResId
                            Surface(
                                color = if (isSelected) preset.accentColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) preset.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onSelect3DAvatarPreset(preset)
                                        aiPhotoPrompt = build3DAvatarPromptFromTags(preset.tags.toSet())
                                        saveFeedbackMessage = "Selected '${preset.title}' 3D Avatar Portrait!"
                                    }
                                    .testTag("select_3d_avatar_${preset.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(126.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = preset.drawableResId),
                                            contentDescription = preset.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        if (isSelected) {
                                            Surface(
                                                color = preset.accentColor,
                                                shape = CircleShape,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(6.dp)
                                                    .size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected 3D Avatar",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.65f),
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = "${preset.badgeEmoji} 3D AI PORTRAIT",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = preset.tags.take(2).joinToString(" • "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = preset.accentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Descriptive Tag Builder for AI Avatar Portraits
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Descriptive Tag Builder (Tap Tags to Customize 3D Avatar):",
                            style = MaterialTheme.typography.titleMedium,
                            color = HoloCyan
                        )

                        AVATAR_DESCRIPTIVE_TAG_GROUPS.forEach { (groupTitle, tags) ->
                            Text(
                                text = "$groupTitle:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                tags.forEach { tag ->
                                    val isTagActive = selectedAvatarTags.contains(tag)
                                    FilterChip(
                                        selected = isTagActive,
                                        onClick = {
                                            onToggleAvatarTag(tag)
                                            val updatedTags = if (isTagActive && selectedAvatarTags.size > 1) {
                                                selectedAvatarTags - tag
                                            } else {
                                                selectedAvatarTags + tag
                                            }
                                            aiPhotoPrompt = build3DAvatarPromptFromTags(updatedTags)
                                        },
                                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.testTag(
                                            "avatar_tag_${tag.lowercase().replace(" ", "_")}"
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Active Tag Prompt: $tagBuiltPrompt",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald
                        )

                        Button(
                            onClick = {
                                aiPhotoPrompt = tagBuiltPrompt
                                onGenerateAiProfilePhoto(tagBuiltPrompt)
                            },
                            enabled = aiImageState !is AiImageUiState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_tag_based_3d_avatar_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Unique 3D Avatar from Selected Tags", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2B. Create Your Photo on the Profile Page (Photo Picker + Camera + Custom AI Prompt + Badges)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Create Profile Photo",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "Upload Photo, Camera Snapshot & Custom AI Prompt",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Pick from gallery, snap a camera portrait, or write a custom prompt for Gemini Nano Banana 2.1.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Gallery Picker & Camera Snapshot Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pick_profile_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select Photo", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { cameraSnapshotLauncher.launch(null) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("capture_camera_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Snap Camera", color = CyberEmerald, fontWeight = FontWeight.Bold)
                    }
                }

                // Avatar Emoji Badge Selector
                Text(
                    text = "Choose 3D Avatar Crest & Flair:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    avatarBadges.forEach { emoji ->
                        val isSelected = avatarEmojiBadge == emoji
                        Surface(
                            color = if (isSelected) activeAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) activeAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onUpdateAvatarBadge(emoji) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }

                }
            }
        }

        // 3. 3D Theme Palette & Room Style Customizer
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "3D Theme Customizer",
                        tint = activeAccent
                    )
                    Text(
                        text = "Customize Your 3D Theme Palette & Dimension Room",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    themeSwatches.forEach { (hex, name) ->
                        val color = parseThemeHex(hex)
                        val isSelected = formState.themeColorHex.equals(hex, ignoreCase = true)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onThemeColorSelect(hex) }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = name,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // Light Mode vs Dark Mode Selector
                Text(
                    text = "Global Light / Dark Display Mode:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isDarkMode,
                        onClick = { onSetDarkMode(true) },
                        label = { Text("Dark Space Mode") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Dark Mode",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("profile_dark_mode_chip")
                    )
                    FilterChip(
                        selected = !isDarkMode,
                        onClick = { onSetDarkMode(false) },
                        label = { Text("Light Daylight Mode") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = "Light Mode",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("profile_light_mode_chip")
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    roomStyles.forEach { room ->
                        FilterChip(
                            selected = selected3DRoomStyle == room,
                            onClick = { selected3DRoomStyle = room },
                            label = { Text(room) }
                        )
                    }
                }
            }
        }

        // 4A. Background Song Jukebox & Real Background Audio Player for Profile
        ProfileBackgroundMusicJukeboxCard(
            currentTrackTitle = formState.musicTrackTitle,
            currentPlatform = formState.musicPlatform,
            isPlayingMusic = isPlayingMusic,
            onSelectSongPreset = onSelectBackgroundSongPreset,
            onCustomSongAndPlatformChange = onMusicChange,
            onTogglePlayMusic = onToggleMusic,
            spotifyConnected = spotifyConnected,
            appleMusicConnected = appleMusicConnected,
            onConnectSpotify = onConnectSpotify,
            onConnectAppleMusic = onConnectAppleMusic,
            onDisconnectSpotify = onDisconnectSpotify,
            onDisconnectAppleMusic = onDisconnectAppleMusic
        )

        // 4B. All Music Platform Selection for Profile Page
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "All Music Platforms",
                        tint = CyberEmerald
                    )
                    Column {
                        Text(
                            text = "All Music Platforms for Your Profile Page",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Connect any streaming platform to play and share your MySpace-style profile anthem.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = formState.musicTrackTitle,
                    onValueChange = { onMusicChange(it, formState.musicPlatform) },
                    label = { Text("Profile Song Title & Artist") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_profile_music_track_input")
                )

                Text(
                    text = "Select Music Platform (${ALL_MUSIC_PLATFORMS.size} Supported):",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberEmerald
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ALL_MUSIC_PLATFORMS.forEach { platform ->
                        val isSelected = formState.musicPlatform.equals(platform, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onMusicChange(formState.musicTrackTitle, platform) },
                            label = { Text(platform) },
                            modifier = Modifier.testTag(
                                "music_platform_chip_${platform.lowercase().replace(" ", "_")}"
                            )
                        )
                    }
                }
            }
        }

        // 4C. Sports of Your Choosing on Your Profile Page
        ProfileSportsSelectorCard(
            selectedSportsIds = selectedSportsIds,
            onToggleSport = onToggleSport
        )

        // 4D. Adaptive Profile Learning Engine (Interests, Lifestyle, Daily Schedule & Research)
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

        // 5. Mandatory Simple Code of Conduct Agreement when Creating/Saving Profile Page
        ProfileCodeOfConductAgreementCard(
            accepted = formState.pledgeAccepted,
            onAcceptedChange = onPledgeChange,
            checkboxTestTag = "theme_profile_conduct_checkbox",
            cardTestTag = "theme_profile_code_of_conduct_card"
        )

        // 6. Profile Identity & Save Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Profile Identity, Age & Bio Details",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = formState.displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formState.ageText,
                        onValueChange = onAgeChange,
                        label = { Text("Age") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.35f)
                    )
                    OutlinedTextField(
                        value = formState.location,
                        onValueChange = onLocationChange,
                        label = { Text("City / ZIP") },
                        singleLine = true,
                        modifier = Modifier.weight(0.65f)
                    )
                }

                OutlinedTextField(
                    value = formState.bio,
                    onValueChange = onBioChange,
                    label = { Text("Personal Bio & Life Motto") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "veteran" to "Veteran",
                        "creator" to "Student / Creator",
                        "business" to "Business / Worker",
                        "adult" to "Community Adult"
                    ).forEach { (roleKey, label) ->
                        FilterChip(
                            selected = formState.role == roleKey,
                            onClick = { onRoleSelect(roleKey) },
                            label = { Text(label) }
                        )
                    }
                }

                if (!saveFeedbackMessage.isNullOrBlank()) {
                    Text(
                        text = saveFeedbackMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (formState.pledgeAccepted) CyberEmerald else SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (!formState.pledgeAccepted) {
                            saveFeedbackMessage = "You must agree to the Simple Code of Conduct above before creating or saving your profile page!"
                            return@Button
                        }
                        onSaveProfile()
                        saveFeedbackMessage = "Agreed to Code of Conduct & saved your 3D Theme Profile, Filtered Photo & Music!"
                    },
                    enabled = formState.pledgeAccepted,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (formState.pledgeAccepted) activeAccent else SolarAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_theme_profile_button")
                ) {
                    Text(
                        text = if (formState.pledgeAccepted) {
                            "Agree to Code of Conduct & Save 3D Profile Page"
                        } else {
                            "Check Code of Conduct Box Above to Save Profile"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
            }
        }
    }
}
