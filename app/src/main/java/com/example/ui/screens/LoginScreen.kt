package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.AiImageUiState
import com.example.ui.AiTextUiState
import com.example.ui.SignUpFormState
import com.example.ui.auth.AuthManager
import com.example.ui.components.BackgroundSongPreset
import com.example.ui.components.EveryLoginSuggestionsCard
import com.example.ui.components.OFFICIAL_PLATFORM_CLOUD_URL
import com.example.ui.components.ProfileBackgroundMusicJukeboxCard
import com.example.ui.components.ProfileSportsSelectorCard
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.WereAllEquals3DHeroShowcaseBanner
import com.example.ui.components.WereAllEquals3DMultiDimensionalIcon
import com.example.ui.components.THREE_D_AVATAR_PRESETS
import com.example.ui.components.ThreeDAvatarPreset
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

private enum class LoginPortalSection(val label: String, val icon: ImageVector) {
    BRIEFING("⚡ Smart Briefing", Icons.Default.AutoAwesome),
    AVATAR_THEME("🎨 6D Avatar & Theme", Icons.Default.Palette),
    MUSIC_SPORTS("🎵 Music & Sports", Icons.Default.EmojiEvents),
    ALL("🌐 Full 6D View", Icons.Default.MusicNote)
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    authBannerMessage: String?,
    isDarkMode: Boolean = true,
    onSetDarkMode: (Boolean) -> Unit = {},
    selected3DAvatarResId: Int? = R.drawable.img_avatar_cyber_astronaut,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit = {},
    currentThemeHex: String = "#6366F1",
    onThemeColorSelect: (String) -> Unit = {},
    currentVisualStyleId: String = "HYPERCUBE_6D",
    onSelectVisualStyle: (String) -> Unit = {},
    onAdvanceThemeAndColor: () -> Unit = {},
    displayName: String = "Jordan Vance",
    everyLoginSuggestions: List<String> = emptyList(),
    onRefreshLoginSuggestions: () -> Unit = {},
    selectedSportsIds: Set<String> = emptySet(),
    onToggleSport: (String) -> Unit = {},
    currentTrackTitle: String = "Comfortably Numb - Pink Floyd",
    currentMusicPlatform: String = "Spotify",
    isPlayingMusic: Boolean = false,
    onSelectSongPreset: (BackgroundSongPreset) -> Unit = {},
    onMusicChange: (String, String) -> Unit = { _, _ -> },
    onToggleMusic: () -> Unit = {},
    onQuickLoginWithIdentity: (identifier: String, accountRole: String, age: Long) -> Unit = { _, _, _ -> },
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onEnterDashboard: (preserveSelectedTheme: Boolean) -> Unit = {},
    // Same-page Create an Account & Full Profile Creation parameters
    activePortalLink: String = "BOTH",
    onSelectPortalLink: (String) -> Unit = {},
    signUpFormState: SignUpFormState = SignUpFormState(),
    isAuthenticated: Boolean = false,
    customProfileBitmap: Bitmap? = null,
    selectedAvatarTags: Set<String> = emptySet(),
    avatarEmojiBadge: String = "🚀",
    aiProfileImageState: AiImageUiState = AiImageUiState.Idle,
    learnedInterests: Set<String> = emptySet(),
    learnedLifestyle: Set<String> = emptySet(),
    learnedSchedule: Set<String> = emptySet(),
    learnedResearch: Set<String> = emptySet(),
    learnedSignals: List<String> = emptyList(),
    aiAdaptivePlanState: AiTextUiState = AiTextUiState.Idle,
    backgroundTransparency: Float = 0.82f,
    onTransparencyChange: (Float) -> Unit = {},
    onDisplayNameChange: (String) -> Unit = {},
    onAgeChange: (String) -> Unit = {},
    onLocationChange: (String) -> Unit = {},
    onBioChange: (String) -> Unit = {},
    onRoleSelect: (String) -> Unit = {},
    onToggleInterest: (String) -> Unit = {},
    onPledgeChange: (Boolean) -> Unit = {},
    onSaveAuthenticatedProfile: () -> Unit = {},
    onUpdateProfileBitmap: (Bitmap) -> Unit = {},
    onToggleAvatarTag: (String) -> Unit = {},
    onUpdateAvatarBadge: (String) -> Unit = {},
    onGenerateAiProfilePhoto: (String) -> Unit = {},
    onToggleLearnedInterest: (String) -> Unit = {},
    onToggleLearnedLifestyle: (String) -> Unit = {},
    onToggleLearnedSchedule: (String) -> Unit = {},
    onToggleLearnedResearch: (String) -> Unit = {},
    onAddCustomLearningSignal: (String, String) -> Unit = { _, _ -> },
    onGenerateAdaptiveLearningPlan: () -> Unit = {},
    onPublishPostToDashboard: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var isSigningIn by remember { mutableStateOf(false) }
    var activeSection by remember { mutableStateOf(LoginPortalSection.ALL) }
    var dualLinkFeedback by remember { mutableStateOf<String?>(null) }

    var loginNameOrEmailOrPhone by remember { mutableStateOf(displayName) }
    var loginEmailOrNumber by remember { mutableStateOf("") }
    var selectedAccountType by remember { mutableStateOf("business") }
    var selectedLoginAge by remember { mutableStateOf(26L) }
    var loginColorCategory by remember { mutableStateOf("ALL") }
    var autoShiftThemeOnLogin by remember { mutableStateOf(false) }

    val activeColorName = com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS.firstOrNull {
        it.hex.equals(currentThemeHex, ignoreCase = true)
    }?.name ?: currentThemeHex

    val activeStyleTitle = com.example.ui.components.THREE_D_THEME_VISUAL_STYLES.firstOrNull {
        it.id == currentVisualStyleId
    }?.title ?: "6D Hypercube"

    val filteredLoginColors = remember(loginColorCategory) {
        if (loginColorCategory == "ALL") {
            com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS
        } else {
            com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS.filter { it.category == loginColorCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Clean Top Identity + FHD 1080p • HD 6D Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WereAllEquals3DMultiDimensionalIcon(
                        iconSize = 48.dp,
                        showRibbonText = true
                    )
                    Column {
                        Text(
                            text = "STAGES OF LIFE • ${stringResource(R.string.app_slogan).uppercase()}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "\"LIVE • LIFE • LOVE\" • Theme: $activeStyleTitle • $activeColorName",
                            style = MaterialTheme.typography.labelSmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                SixDimensionalHdBadge(label = "LOGIN")
            }

            // 1B. Interactive 3D / Multi-Dimensional "WEREALLEQUALS" Emblem Showcase
            WereAllEquals3DHeroShowcaseBanner()

            // 1C. Unified Dual-Link Hub: Link 1 (Log In Page) & Link 2 (Create an Account & Full Creation Studio) on the Same Page
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = 1.6.dp,
                    brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, NeonPink, SolarAmber))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_and_creation_dual_links_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🔗 Log In & Create an Account on the Same Page (Two Direct Platform Links)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Both Log In and Create an Account (with all 3D Profile & Account Creation features) are on this page. Tap either link below:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Section 1: Log In Portal Box
                    Surface(
                        color = if (activePortalLink == "LOGIN" || activePortalLink == "BOTH") {
                            HoloCyan.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, HoloCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectPortalLink("LOGIN")
                                dualLinkFeedback = "✓ Switched to Stages Of Life Log In Portal"
                            }
                            .testTag("dual_link_login_portal_button")
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
                                    text = "1. Stages Of Life Log In Portal",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Sign in with Name, Email, Phone Number, or Biometric ID",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "Select",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Section 2: Create an Account & Full Creation Studio Box
                    Surface(
                        color = if (activePortalLink == "CREATE_ACCOUNT" || activePortalLink == "BOTH") {
                            CyberEmerald.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, CyberEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectPortalLink("CREATE_ACCOUNT")
                                dualLinkFeedback = "✓ Switched to Stages Of Life Create Account Studio"
                            }
                            .testTag("dual_link_create_account_portal_button")
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
                                    text = "2. Stages Of Life Create an Account & Profile Studio",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Choose Kid, Teen, Student, Adult, Business, or Veteran Persona",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "Select",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // View Filter Chips: Show Both on Same Page vs Link 1 Only vs Link 2 Only
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activePortalLink == "BOTH",
                            onClick = { onSelectPortalLink("BOTH") },
                            label = {
                                Text(
                                    text = "⚡ Show Both on Same Page (Log In + All Creation Features)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            },
                            modifier = Modifier.testTag("portal_mode_both_same_page_chip")
                        )
                        FilterChip(
                            selected = activePortalLink == "LOGIN",
                            onClick = { onSelectPortalLink("LOGIN") },
                            label = {
                                Text(
                                    text = "🔗 Link 1: Log In Only",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.testTag("portal_mode_login_only_chip")
                        )
                        FilterChip(
                            selected = activePortalLink == "CREATE_ACCOUNT",
                            onClick = { onSelectPortalLink("CREATE_ACCOUNT") },
                            label = {
                                Text(
                                    text = "🔗 Link 2: Create Account & Profile Only",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.testTag("portal_mode_create_only_chip")
                        )
                    }

                    if (!dualLinkFeedback.isNullOrBlank()) {
                        Text(
                            text = dualLinkFeedback!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 1D. Official Platform Download & Shareable URL Hub (APK / Cloud Install / https:\\ & https://)
            com.example.ui.components.PlatformUrlGeneratorCard(
                userDisplayName = displayName.ifBlank { "Jordan Vance" },
                currentVisualStyleId = currentVisualStyleId,
                currentThemeHex = currentThemeHex,
                onPublishUrlToCommunityWall = onPublishPostToDashboard
            )

            // Optional status / error banner
            if (!authBannerMessage.isNullOrBlank()) {
                Surface(
                    color = SolarAmber.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = authBannerMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SolarAmber,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // 2. Universal Login Card (Shown when activePortalLink is "BOTH" or "LOGIN")
            if (activePortalLink == "BOTH" || activePortalLink == "LOGIN") {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(HoloCyan, MaterialTheme.colorScheme.primary, NeonPink, CyberEmerald)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .sixDimensionalCardEffect(tiltFactor = 1.4f)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top FHD 6D Visual Banner with Live 3D Theme Overlay & Floating Avatar Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp)
                    ) {
                        com.example.ui.components.ThreeDimensionalBackground(
                            accentHex = currentThemeHex,
                            visualStyleId = currentVisualStyleId,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                        )

                        // Quick Top-Right Shuffle Theme & Color Pill
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clickable { onAdvanceThemeAndColor() }
                                .testTag("login_shuffle_theme_color_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Change Theme & Color",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "🎲 Shift Theme & Color",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, HoloCyan, NeonPink)),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = selected3DAvatarResId ?: R.drawable.img_avatar_cyber_astronaut
                                    ),
                                    contentDescription = "6D Avatar Portrait",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.login_welcome_title),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Login with Email, Phone Number, or Name & Pick Your Theme!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Universal Email / Phone Number / Name Login Form + Theme & Color Picker on Login
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = loginNameOrEmailOrPhone,
                            onValueChange = { loginNameOrEmailOrPhone = it },
                            label = { Text("Your Name, Email, or Phone Number") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_input")
                        )

                        androidx.compose.material3.OutlinedTextField(
                            value = loginEmailOrNumber,
                            onValueChange = { loginEmailOrNumber = it },
                            label = { Text("Email Address or Phone Number (Optional)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_or_phone_input")
                        )

                        // Quick Account Type & Age Group Row on Login (Student, Business, Content, Personal, Veteran)
                        Text(
                            text = "1. Select Account Type (Student, Business, Content, Personal, Veteran) & Age Group:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "student" to "🎓 Student Account",
                                "business" to "💼 Business Account",
                                "creator" to "🎬 Content Account",
                                "adult" to "🌟 Personal Account",
                                "veteran" to "🛡️ Veteran Account"
                            ).forEach { (roleKey, label) ->
                                FilterChip(
                                    selected = selectedAccountType == roleKey,
                                    onClick = {
                                        selectedAccountType = roleKey
                                        if (roleKey == "student" && selectedLoginAge > 24L) {
                                            selectedLoginAge = 18L
                                        }
                                    },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("login_account_type_$roleKey")
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                14L to "Youth (13–15)",
                                16L to "Teen (16–17)",
                                21L to "Young Adult (18–24)",
                                32L to "Adult (25–54)",
                                28L to "Veteran / 28+"
                            ).forEach { (ageVal, ageLabel) ->
                                FilterChip(
                                    selected = selectedLoginAge == ageVal,
                                    onClick = { selectedLoginAge = ageVal },
                                    label = { Text(ageLabel, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // NEW: Choose Your 3D Theme & 32-Color Selection Directly When Logging In!
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_inline_theme_color_selector")
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "2. WereEqual Theme Selection & Basic Colors (When Logging In)",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Active: $activeStyleTitle • $activeColorName ($currentThemeHex)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = if (isDarkMode) HoloCyan.copy(alpha = 0.2f) else SolarAmber.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, if (isDarkMode) HoloCyan else SolarAmber),
                                            modifier = Modifier.clickable { onSetDarkMode(!isDarkMode) }
                                        ) {
                                            Text(
                                                text = if (isDarkMode) "🌙 Dark" else "☀️ Light",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isDarkMode) HoloCyan else SolarAmber,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                // 3D Visual Theme Style Selector Strip on Login
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    com.example.ui.components.THREE_D_THEME_VISUAL_STYLES.forEach { styleOpt ->
                                        val isStyleSelected = styleOpt.id == currentVisualStyleId
                                        FilterChip(
                                            selected = isStyleSelected,
                                            onClick = {
                                                onSelectVisualStyle(styleOpt.id)
                                                autoShiftThemeOnLogin = false
                                            },
                                            label = {
                                                Text(
                                                    text = "◈ ${styleOpt.title}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isStyleSelected) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            modifier = Modifier.testTag("login_style_chip_${styleOpt.id.lowercase()}")
                                        )
                                    }
                                }

                                // 32-Color Category Filter Chips on Login
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "ALL" to "🌈 All 32 Colors",
                                        "Basic Colors" to "🎨 Basic Colors (12)",
                                        "Neon & Cyber" to "⚡ Neon & Cyber",
                                        "Cosmic & Royal" to "🌌 Cosmic & Royal",
                                        "Ocean & Mint" to "🌊 Ocean & Mint",
                                        "Solar & Warm" to "☀️ Solar & Warm",
                                        "Luxury & Gold" to "👑 Luxury & Gold"
                                    ).forEach { (catKey, catLabel) ->
                                        FilterChip(
                                            selected = loginColorCategory == catKey,
                                            onClick = { loginColorCategory = catKey },
                                            label = { Text(catLabel, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }

                                // Scrollable 32-Color Named Swatches on Login
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    filteredLoginColors.forEach { option ->
                                        val isSelected = currentThemeHex.equals(option.hex, ignoreCase = true)
                                        Surface(
                                            color = if (isSelected) option.color.copy(alpha = 0.28f) else MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) option.color else option.color.copy(alpha = 0.55f)
                                            ),
                                            modifier = Modifier
                                                .clickable {
                                                    onThemeColorSelect(option.hex)
                                                    autoShiftThemeOnLogin = false
                                                }
                                                .testTag("login_color_swatch_${option.hex.removePrefix("#").lowercase()}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
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

                                // Auto-Shift vs Keep Chosen Login Theme Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (autoShiftThemeOnLogin) {
                                            "🎲 Mode: Auto-Shift Theme & Color on Login & Every Page Change"
                                        } else {
                                            "✓ Mode: Login with My Selected Theme ($activeColorName) & Shift on Page Change"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = autoShiftThemeOnLogin,
                                        onClick = {
                                            autoShiftThemeOnLogin = !autoShiftThemeOnLogin
                                            if (autoShiftThemeOnLogin) {
                                                onAdvanceThemeAndColor()
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = if (autoShiftThemeOnLogin) "Auto-Shift ON" else "Keep Login Pick",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Primary Button: Login with Email / Phone Number / Name + Chosen Theme
                        Button(
                            onClick = {
                                val combinedName = when {
                                    loginNameOrEmailOrPhone.isNotBlank() -> loginNameOrEmailOrPhone.trim()
                                    loginEmailOrNumber.isNotBlank() -> loginEmailOrNumber.trim()
                                    else -> displayName.ifBlank { "Jordan Vance" }
                                }
                                val mappedRole = if (selectedAccountType == "student") "creator" else selectedAccountType
                                onQuickLoginWithIdentity(combinedName, mappedRole, selectedLoginAge)
                                onEnterDashboard(!autoShiftThemeOnLogin)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("enter_6d_hub_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = "Login with Email, Number or Name",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login with $activeColorName Theme • Email / Number / Name",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Prominent Switch / Scroll to Link 2: Create an Account on Same Page Button
                        Button(
                            onClick = {
                                onSelectPortalLink("BOTH")
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("navigate_to_signup_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = stringResource(R.string.switch_to_signup),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔗 Link 2 Below: Create an Account & Full Profile Studio (Same Page)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // 3. Biometric Scan Login Card (Fingerprint & Face Recognition)
                        var bioStatusMessage by remember { mutableStateOf<String?>(null) }
                        var isBioScanning by remember { mutableStateOf(false) }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = NeonPink.copy(alpha = 0.08f),
                            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(NeonPink, HoloCyan, CyberEmerald))),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bio_scan_login_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(NeonPink.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Fingerprint,
                                                contentDescription = "Bio Scan",
                                                tint = NeonPink,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "⚡ Quick Bio Scan Login",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "Fingerprint • Face ID • Biometric Security",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NeonPink
                                            )
                                        }
                                    }

                                    Surface(
                                        color = CyberEmerald.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, CyberEmerald)
                                    ) {
                                        Text(
                                            text = "BIO READY",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                if (!bioStatusMessage.isNullOrBlank()) {
                                    Surface(
                                        color = HoloCyan.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = bioStatusMessage ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HoloCyan,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Hardware BiometricPrompt Launch Button
                                    Button(
                                        onClick = {
                                            isBioScanning = true
                                            val fragmentActivity = context as? FragmentActivity
                                            if (fragmentActivity != null) {
                                                val executor = ContextCompat.getMainExecutor(context)
                                                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                                    .setTitle("WereEqual Bio Scan")
                                                    .setSubtitle("Authenticate using your fingerprint or face recognition")
                                                    .setNegativeButtonText("Cancel")
                                                    .build()

                                                val prompt = BiometricPrompt(
                                                    fragmentActivity,
                                                    executor,
                                                    object : BiometricPrompt.AuthenticationCallback() {
                                                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                                            isBioScanning = false
                                                            bioStatusMessage = "✓ Biometric identity confirmed!"
                                                            val combinedName = when {
                                                                loginNameOrEmailOrPhone.isNotBlank() -> loginNameOrEmailOrPhone.trim()
                                                                loginEmailOrNumber.isNotBlank() -> loginEmailOrNumber.trim()
                                                                else -> displayName.ifBlank { "Jordan Vance" }
                                                            }
                                                            val mappedRole = if (selectedAccountType == "student") "creator" else selectedAccountType
                                                            onQuickLoginWithIdentity(combinedName, mappedRole, selectedLoginAge)
                                                            onEnterDashboard(!autoShiftThemeOnLogin)
                                                        }

                                                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                                            isBioScanning = false
                                                            bioStatusMessage = "Bio Scan info: $errString (Fast-Pass Available)"
                                                        }

                                                        override fun onAuthenticationFailed() {
                                                            isBioScanning = false
                                                            bioStatusMessage = "Biometric not recognized. Please retry or use Fast-Pass."
                                                        }
                                                    }
                                                )
                                                prompt.authenticate(promptInfo)
                                            } else {
                                                // Fallback if not inside FragmentActivity
                                                isBioScanning = false
                                                val combinedName = when {
                                                    loginNameOrEmailOrPhone.isNotBlank() -> loginNameOrEmailOrPhone.trim()
                                                    loginEmailOrNumber.isNotBlank() -> loginEmailOrNumber.trim()
                                                    else -> displayName.ifBlank { "Jordan Vance" }
                                                }
                                                val mappedRole = if (selectedAccountType == "student") "creator" else selectedAccountType
                                                onQuickLoginWithIdentity(combinedName, mappedRole, selectedLoginAge)
                                                onEnterDashboard(!autoShiftThemeOnLogin)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonPink,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("biometric_prompt_scan_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Scan Bio ID",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    // Instant 1-Tap Bio Scan Fast-Pass (works seamlessly on emulator and hardware)
                                    Button(
                                        onClick = {
                                            bioStatusMessage = "✓ Verified via 1-Tap Bio Fast-Pass"
                                            val combinedName = when {
                                                loginNameOrEmailOrPhone.isNotBlank() -> loginNameOrEmailOrPhone.trim()
                                                loginEmailOrNumber.isNotBlank() -> loginEmailOrNumber.trim()
                                                else -> displayName.ifBlank { "Jordan Vance" }
                                            }
                                            val mappedRole = if (selectedAccountType == "student") "creator" else selectedAccountType
                                            onQuickLoginWithIdentity(combinedName, mappedRole, selectedLoginAge)
                                            onEnterDashboard(!autoShiftThemeOnLogin)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HoloCyan,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("bio_instant_unlock_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "⚡ Instant Unlock",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        // Google Sign-In & External Auth Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isSigningIn = true
                                    AuthManager.onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onAuthSuccess = {
                                            isSigningIn = false
                                            onAuthSuccess()
                                        },
                                        onAuthError = { msg ->
                                            isSigningIn = false
                                            onAuthError(msg)
                                        },
                                        scope = scope,
                                        onAuthCancelled = {
                                            isSigningIn = false
                                        }
                                    )
                                },
                                enabled = !isSigningIn,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("google_login_button")
                            ) {
                                if (isSigningIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Google Sign-In",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            } // End of Universal Login Card (activePortalLink == "BOTH" || activePortalLink == "LOGIN")

            // =========================================================================
            // LINK 2 SECTION ON THE SAME PAGE: CREATE AN ACCOUNT & ALL PROFILE CREATION FEATURES & SELECTIONS
            // =========================================================================
            if (activePortalLink == "BOTH" || activePortalLink == "CREATE_ACCOUNT") {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.8.dp,
                        brush = Brush.horizontalGradient(listOf(CyberEmerald, HoloCyan, SolarAmber, NeonPink))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("same_page_create_account_and_profile_section")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔗 LINK 2: CREATE AN ACCOUNT & FULL 3D PROFILE CREATION STUDIO",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Stages Of Life • All Account Setup, Age Groups, Student/Business/Content Selections, 3D Themes, Camera Studio & Music",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Part A: Full SignUpScreen Account Creation Form & Selections (Embedded on Same Page)
                        SignUpScreen(
                            formState = signUpFormState,
                            isAuthenticated = isAuthenticated,
                            isDarkMode = isDarkMode,
                            onSetDarkMode = onSetDarkMode,
                            onDisplayNameChange = onDisplayNameChange,
                            onAgeChange = onAgeChange,
                            onLocationChange = onLocationChange,
                            onBioChange = onBioChange,
                            onRoleSelect = onRoleSelect,
                            onThemeColorSelect = onThemeColorSelect,
                            onMusicChange = onMusicChange,
                            onToggleInterest = onToggleInterest,
                            onPledgeChange = onPledgeChange,
                            onSaveAuthenticatedProfile = {
                                onSaveAuthenticatedProfile()
                                onEnterDashboard(true)
                            },
                            onAuthSuccess = onAuthSuccess,
                            onAuthError = onAuthError,
                            onBack = { onEnterDashboard(true) },
                            isEmbeddedInLoginScreen = true
                        )

                        // Part B: Full CreateThemeProfileScreen Creation Features & Selections (3D Theme Presets, Camera Photo Studio, 3D Avatar Builder, Music Jukebox, Sports & Adaptive AI)
                        CreateThemeProfileScreen(
                            formState = signUpFormState,
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
                            onThemeVisualStyleSelect = onSelectVisualStyle,
                            onMusicChange = onMusicChange,
                            onSelectBackgroundSongPreset = onSelectSongPreset,
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
                            onSaveProfile = {
                                onSaveAuthenticatedProfile()
                                onEnterDashboard(true)
                            },
                            onPublishPostToDashboard = onPublishPostToDashboard,
                            onOpenDashboardFeed = { onEnterDashboard(true) },
                            backgroundTransparency = backgroundTransparency,
                            onTransparencyChange = onTransparencyChange
                        )
                    }
                }
            }

            // 3. Clean 6D Section Organizer Strip (Lets User Filter or View All cleanly)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LoginPortalSection.entries.forEach { section ->
                    FilterChip(
                        selected = activeSection == section,
                        onClick = { activeSection = section },
                        leadingIcon = {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null,
                                tint = if (activeSection == section) HoloCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = section.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (activeSection == section) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Section A: Every-Login Smart Suggestions
            if (activeSection == LoginPortalSection.BRIEFING || activeSection == LoginPortalSection.ALL) {
                EveryLoginSuggestionsCard(
                    displayName = displayName.substringBefore(" ").ifBlank { "Explorer" },
                    suggestions = everyLoginSuggestions,
                    onRefreshSuggestions = onRefreshLoginSuggestions,
                    onOpenNewsAndSportsTab = onRefreshLoginSuggestions
                )
            }

            // Section B: Full 3D Theme Visual Style Studio + 32-Color Picker + 6D Avatar Selector on Login
            if (activeSection == LoginPortalSection.AVATAR_THEME || activeSection == LoginPortalSection.ALL) {
                com.example.ui.components.ThreeDThemeStyleSelectorMenuCard(
                    currentVisualStyleId = currentVisualStyleId,
                    currentThemeHex = currentThemeHex,
                    isDarkMode = isDarkMode,
                    onSelectVisualStyle = { styleId ->
                        onSelectVisualStyle(styleId)
                        autoShiftThemeOnLogin = false
                    },
                    onSelectThemeColor = { hex ->
                        onThemeColorSelect(hex)
                        autoShiftThemeOnLogin = false
                    }
                )

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)),
                    modifier = Modifier.testTag("login_3d_profile_m3_theme_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "6D Profile Material 3 Mode & 3D Avatar",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isDarkMode) {
                                        "Active: M3 DarkColorScheme ($activeColorName)"
                                    } else {
                                        "Active: M3 LightColorScheme ($activeColorName)"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onSetDarkMode(it) },
                                modifier = Modifier.testTag("login_3d_profile_dark_mode_switch")
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
                                label = { Text("☀️ Light 6D Mode") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("login_3d_profile_light_chip")
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
                                label = { Text("🌙 Dark 6D Mode") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("login_3d_profile_dark_chip")
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            THREE_D_AVATAR_PRESETS.forEach { preset ->
                                val isSelected = selected3DAvatarResId == preset.drawableResId
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelect3DAvatarPreset(preset) }
                                        .testTag("login_3d_avatar_${preset.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = preset.drawableResId),
                                            contentDescription = preset.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                        )
                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section C: Background Song Jukebox & Chosen Sports on Login
            if (activeSection == LoginPortalSection.MUSIC_SPORTS || activeSection == LoginPortalSection.ALL) {
                ProfileBackgroundMusicJukeboxCard(
                    currentTrackTitle = currentTrackTitle,
                    currentPlatform = currentMusicPlatform,
                    isPlayingMusic = isPlayingMusic,
                    onSelectSongPreset = onSelectSongPreset,
                    onCustomSongAndPlatformChange = onMusicChange,
                    onTogglePlayMusic = onToggleMusic
                )

                ProfileSportsSelectorCard(
                    selectedSportsIds = selectedSportsIds,
                    onToggleSport = onToggleSport
                )
            }

            // 4 Modular 6D Pillars Grid (Compact & Eye-Popping)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePillarCard(
                    icon = Icons.Default.MusicNote,
                    title = "6D Themes & Music",
                    subtitle = "Holographic 6D space & background song jukebox.",
                    accent = NeonPink,
                    modifier = Modifier.weight(1f)
                )
                FeaturePillarCard(
                    icon = Icons.Default.Brush,
                    title = "Studio & Sports",
                    subtitle = "Digital brush canvas, arcade & live chosen sports.",
                    accent = HoloCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePillarCard(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Lifeline & News",
                    subtitle = "Daily login news wire, VA clinics, 211 & FAFSA.",
                    accent = CyberEmerald,
                    modifier = Modifier.weight(1f)
                )
                FeaturePillarCard(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Finance & AI Mind",
                    subtitle = "Savings milestones & adaptive user learning.",
                    accent = SolarAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FeaturePillarCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
