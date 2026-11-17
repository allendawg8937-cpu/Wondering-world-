package com.example.dating

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val SanctuaryBg = Color(0xFF0E0914)
private val SanctuarySurface = Color(0xFF1A1224)
private val SanctuaryCardAlt = Color(0xFF231831)
private val SanctuaryRose = Color(0xFFFB7185)
private val SanctuaryGold = Color(0xFFFBBF24)
private val SanctuaryMint = Color(0xFF34D399)
private val SanctuaryText = Color(0xFFF8F4FC)
private val SanctuaryMuted = Color(0xFFC4B5D6)

private val IsolatedSanctuaryColorScheme = darkColorScheme(
    primary = SanctuaryRose,
    onPrimary = Color(0xFF1A070E),
    secondary = SanctuaryGold,
    tertiary = SanctuaryMint,
    background = SanctuaryBg,
    surface = SanctuarySurface,
    surfaceVariant = SanctuaryCardAlt,
    onBackground = SanctuaryText,
    onSurface = SanctuaryText,
    onSurfaceVariant = SanctuaryMuted,
    outline = Color(0xFF4C3966)
)

/**
 * Standalone, UI- and data-isolated 18+ Age-Gated Dating module.
 * Enforces its own color scheme, independent ViewModel/session state, strict DOB + third-party
 * assurance gate, and zero coupling to the main platform's UserProfile or Wall data.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IsolatedAgeGatedDatingScreen(
    onExitToMainPlatform: () -> Unit,
    datingViewModel: IsolatedDatingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by datingViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    BackHandler {
        onExitToMainPlatform()
    }

    MaterialTheme(colorScheme = IsolatedSanctuaryColorScheme) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = SanctuaryBg,
            modifier = modifier
                .fillMaxSize()
                .testTag("isolated_dating_root_screen")
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF190C24),
                                SanctuaryBg,
                                Color(0xFF0A0610)
                            )
                        )
                    )
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 620.dp)
                        .align(Alignment.TopCenter)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Isolated Chrome Top Header (completely separate from main platform nav)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SanctuarySurface,
                        border = BorderStroke(1.dp, SanctuaryRose.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = onExitToMainPlatform,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, SanctuaryMuted.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("dating_exit_to_platform_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Return to Main Platform",
                                    tint = SanctuaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Exit 18+",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SanctuaryText,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sanctuary 18+",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SanctuaryRose
                                )
                                Text(
                                    text = "Isolated Mode • Zero Main-Data Sync",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SanctuaryMuted
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = if (uiState.verificationStatus is DatingVerificationStatus.Verified18Plus) {
                                    SanctuaryMint.copy(alpha = 0.2f)
                                } else {
                                    SanctuaryRose.copy(alpha = 0.2f)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.verificationStatus is DatingVerificationStatus.Verified18Plus) {
                                            Icons.Default.VerifiedUser
                                        } else {
                                            Icons.Default.Lock
                                        },
                                        contentDescription = "Verification state",
                                        tint = if (uiState.verificationStatus is DatingVerificationStatus.Verified18Plus) {
                                            SanctuaryMint
                                        } else {
                                            SanctuaryRose
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (uiState.verificationStatus is DatingVerificationStatus.Verified18Plus) "18+" else "LOCKED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (uiState.verificationStatus is DatingVerificationStatus.Verified18Plus) {
                                            SanctuaryMint
                                        } else {
                                            SanctuaryRose
                                        },
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.statusNotice != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SanctuaryCardAlt,
                            border = BorderStroke(1.dp, SanctuaryGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dating_status_notice")
                        ) {
                            Text(
                                text = uiState.statusNotice!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = SanctuaryGold,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = uiState.verificationStatus,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "isolated_dating_gate_transition"
                    ) { status ->
                        when (status) {
                            is DatingVerificationStatus.Verified18Plus -> {
                                VerifiedSanctuaryContent(
                                    uiState = uiState,
                                    verifiedStatus = status,
                                    onSelectSection = datingViewModel::selectSection,
                                    onLike = datingViewModel::likeCandidate,
                                    onPass = datingViewModel::passCandidate,
                                    onBlock = datingViewModel::blockAndHideCandidate,
                                    onSendMessage = datingViewModel::sendIsolatedIcebreaker,
                                    onLockAndPurge = datingViewModel::revokeVerificationAndPurgeSession
                                )
                            }

                            else -> {
                                AgeVerificationGateCard(
                                    uiState = uiState,
                                    verificationStatus = status,
                                    onBirthMonthChange = datingViewModel::updateBirthMonth,
                                    onBirthDayChange = datingViewModel::updateBirthDay,
                                    onBirthYearChange = datingViewModel::updateBirthYear,
                                    onAliasChange = datingViewModel::updateIsolatedAlias,
                                    onSelectProvider = datingViewModel::selectVerificationProvider,
                                    onOpenExternalProvider = { url ->
                                        runCatching { uriHandler.openUri(url) }
                                    },
                                    onLaunchProviderCheck = {
                                        datingViewModel.launchProviderHandshake()
                                    },
                                    onReferenceCodeChange = datingViewModel::updateProviderReferenceCode,
                                    onPledgeToggle = datingViewModel::toggleConductAndPrivacyPledge,
                                    onSubmitUnlock = {
                                        datingViewModel.completeVerificationAndEnter()
                                    }
                                )
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
private fun AgeVerificationGateCard(
    uiState: IsolatedDatingUiState,
    verificationStatus: DatingVerificationStatus,
    onBirthMonthChange: (String) -> Unit,
    onBirthDayChange: (String) -> Unit,
    onBirthYearChange: (String) -> Unit,
    onAliasChange: (String) -> Unit,
    onSelectProvider: (AgeVerificationProvider) -> Unit,
    onOpenExternalProvider: (String) -> Unit,
    onLaunchProviderCheck: () -> Unit,
    onReferenceCodeChange: (String) -> Unit,
    onPledgeToggle: (Boolean) -> Unit,
    onSubmitUnlock: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SanctuaryRose,
        unfocusedBorderColor = Color(0xFF533E6E),
        focusedLabelColor = SanctuaryRose,
        unfocusedLabelColor = SanctuaryMuted,
        focusedTextColor = SanctuaryText,
        unfocusedTextColor = SanctuaryText
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SanctuarySurface),
        border = BorderStroke(1.2.dp, SanctuaryRose.copy(alpha = 0.65f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dating_age_gate_screen")
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
                Surface(shape = CircleShape, color = SanctuaryRose.copy(alpha = 0.18f)) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Strict 18+ Gate",
                        tint = SanctuaryRose,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Strict 18+ Age Verification Gate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SanctuaryText
                    )
                    Text(
                        text = "Third-party assurance + isolated sandbox (never reads main profile)",
                        style = MaterialTheme.typography.bodySmall,
                        color = SanctuaryMuted
                    )
                }
            }

            if (verificationStatus is DatingVerificationStatus.DeniedUnderage) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF450A1A),
                    border = BorderStroke(1.dp, SanctuaryRose),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dating_underage_blocked_banner")
                ) {
                    Text(
                        text = "⛔ ${verificationStatus.reason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFD4DC),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.isolatedAliasInput,
                onValueChange = onAliasChange,
                singleLine = true,
                label = { Text("Isolated Sanctuary Alias (separate from main account)") },
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dating_alias_input")
            )

            Text(
                text = "Step 1 • Date of Birth Check (Must be 18+)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SanctuaryGold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.birthMonthInput,
                    onValueChange = onBirthMonthChange,
                    singleLine = true,
                    label = { Text("MM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dating_dob_month_input")
                )
                OutlinedTextField(
                    value = uiState.birthDayInput,
                    onValueChange = onBirthDayChange,
                    singleLine = true,
                    label = { Text("DD") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dating_dob_day_input")
                )
                OutlinedTextField(
                    value = uiState.birthYearInput,
                    onValueChange = onBirthYearChange,
                    singleLine = true,
                    label = { Text("YYYY") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors,
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("dating_dob_year_input")
                )
            }

            HorizontalDivider(color = Color(0xFF3A2950))

            Text(
                text = "Step 2 • Third-Party Age Assurance Provider",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SanctuaryGold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AgeVerificationProvider.entries.forEach { provider ->
                    val selected = provider == uiState.selectedProvider
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectProvider(provider) },
                        label = {
                            Text(
                                text = provider.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SanctuaryRose.copy(alpha = 0.25f),
                            selectedLabelColor = SanctuaryText
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("dating_provider_chip_${provider.id}")
                    )
                }
            }

            Text(
                text = uiState.selectedProvider.summary,
                style = MaterialTheme.typography.bodySmall,
                color = SanctuaryMuted
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLaunchProviderCheck,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SanctuaryGold,
                        contentColor = Color(0xFF1C1003)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("dating_launch_provider_btn")
                ) {
                    Text(
                        text = "Run 18+ Assurance Check",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = { onOpenExternalProvider(uiState.selectedProvider.portalUrl) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("dating_open_provider_portal_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open third-party provider portal",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Provider Docs", style = MaterialTheme.typography.labelSmall)
                }
            }

            OutlinedTextField(
                value = uiState.providerReferenceCode,
                onValueChange = onReferenceCodeChange,
                singleLine = true,
                label = { Text("Assurance Token (${uiState.selectedProvider.tokenPrefix}...)") },
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dating_token_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = uiState.hasAcceptedConductAndPrivacyPledge,
                    onCheckedChange = onPledgeToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SanctuaryMint,
                        checkmarkColor = Color(0xFF042217)
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("dating_pledge_checkbox")
                )
                Text(
                    text = "I confirm I am 18+, accept respectful PG-13 community standards, and keep this session completely separate from main platform data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SanctuaryText
                )
            }

            Button(
                onClick = onSubmitUnlock,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SanctuaryRose,
                    contentColor = Color(0xFF1A070E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("dating_unlock_submit_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify 18+ & Enter Isolated Sanctuary",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VerifiedSanctuaryContent(
    uiState: IsolatedDatingUiState,
    verifiedStatus: DatingVerificationStatus.Verified18Plus,
    onSelectSection: (String) -> Unit,
    onLike: (IsolatedDatingCandidate) -> Unit,
    onPass: (IsolatedDatingCandidate) -> Unit,
    onBlock: (IsolatedDatingCandidate) -> Unit,
    onSendMessage: (String, String) -> Unit,
    onLockAndPurge: () -> Unit
) {
    val visiblePool = remember(uiState.blockedCandidateIds) {
        ISOLATED_DATING_POOL.filterNot { it.id in uiState.blockedCandidateIds }
    }
    var icebreakerText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dating_verified_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Verified Alias & Instant Lock/Wipe Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SanctuarySurface,
            border = BorderStroke(1.dp, SanctuaryMint.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${verifiedStatus.isolatedAlias} • Verified ${verifiedStatus.verifiedAgeYears}+",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SanctuaryMint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${verifiedStatus.provider.displayName} • Token ${verifiedStatus.assuranceToken.take(16)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SanctuaryMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onLockAndPurge,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, SanctuaryRose),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("dating_relock_purge_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock and purge 18+ session",
                        tint = SanctuaryRose,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lock & Wipe",
                        style = MaterialTheme.typography.labelSmall,
                        color = SanctuaryRose,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section Switcher inside Isolated Module
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "DISCOVER" to "Discover (${visiblePool.size})",
                "MATCHES" to "Waves (${uiState.likedCandidateIds.size})",
                "SAFETY_GATE" to "Isolation & Safety"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = uiState.activeSection == key,
                    onClick = { onSelectSection(key) },
                    label = { Text(label) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("dating_section_${key.lowercase()}")
                )
            }
        }

        when (uiState.activeSection) {
            "DISCOVER" -> {
                if (visiblePool.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SanctuarySurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No remaining isolated profiles right now. Tap Lock & Wipe to reset your session.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SanctuaryMuted,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                } else {
                    val current = visiblePool[uiState.candidateIndex % visiblePool.size]
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = SanctuarySurface),
                        border = BorderStroke(1.2.dp, SanctuaryRose.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dating_candidate_card_${current.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${current.alias}, ${current.age}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SanctuaryText
                                    )
                                    Text(
                                        text = "${current.cityRegion} • Verified 18+ Adult",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SanctuaryRose
                                    )
                                }
                                IconButton(
                                    onClick = { onBlock(current) },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("dating_block_btn_${current.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Block ${current.alias}",
                                        tint = SanctuaryMuted
                                    )
                                }
                            }

                            Text(
                                text = current.headline,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SanctuaryText
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SanctuaryCardAlt,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"${current.promptAnswer}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SanctuaryMuted,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                current.sharedValues.forEach { value ->
                                    Surface(
                                        shape = CircleShape,
                                        color = SanctuaryGold.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SanctuaryGold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onPass(current) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("dating_pass_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Pass profile",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pass")
                                }

                                Button(
                                    onClick = { onLike(current) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SanctuaryRose,
                                        contentColor = Color(0xFF1A070E)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("dating_like_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Send friendly wave",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send Wave", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }

            "MATCHES" -> {
                val likedProfiles = visiblePool.filter { it.id in uiState.likedCandidateIds }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SanctuarySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Isolated Waves & Private Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (likedProfiles.isEmpty()) {
                            Text(
                                text = "No waves sent yet in this isolated session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SanctuaryMuted
                            )
                        } else {
                            likedProfiles.forEach { candidate ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SanctuaryCardAlt,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${candidate.alias} (${candidate.age}) • ${candidate.cityRegion}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = icebreakerText,
                                                onValueChange = { icebreakerText = it },
                                                singleLine = true,
                                                label = { Text("Send kind icebreaker to ${candidate.alias}") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    onSendMessage(candidate.alias, icebreakerText)
                                                    icebreakerText = ""
                                                },
                                                modifier = Modifier.minimumInteractiveComponentSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = "Send isolated icebreaker",
                                                    tint = SanctuaryRose
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        uiState.isolatedChats.forEach { msg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SanctuaryRose.copy(alpha = 0.14f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "To ${msg.recipientAlias}: ${msg.text}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SanctuaryText,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SanctuarySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Strict Isolation & Age Assurance Audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryMint
                        )
                        Text(
                            text = "• Isolated Store: ${verifiedStatus.isolatedAlias}'s matches and messages stay in dedicated memory and never write to main platform Firestore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SanctuaryMuted
                        )
                        Text(
                            text = "• Provider Token: ${verifiedStatus.provider.displayName} (${verifiedStatus.assuranceToken})",
                            style = MaterialTheme.typography.bodySmall,
                            color = SanctuaryMuted
                        )
                        Text(
                            text = "• Content Ceiling: Respectful PG-13 introductions only; block or purge anytime.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SanctuaryMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = onLockAndPurge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SanctuaryRose,
                                contentColor = Color(0xFF1A070E)
                            ),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text("Lock Gate & Wipe Isolated Data", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
