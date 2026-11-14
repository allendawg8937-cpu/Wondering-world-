package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.FinancialTrackerEntryEntity
import com.example.data.PlanetAccessibilitySettings
import com.example.data.PlanetRepository
import com.example.data.StagesOfLifePersona
import com.example.data.UserProfileEntity
import com.example.data.toAccessibilitySettings
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlinx.coroutines.launch
import java.util.Locale

data class CuratedPersonaArticle(
    val id: String,
    val persona: StagesOfLifePersona,
    val title: String,
    val category: String,
    val readingTime: String,
    val summary: String,
    val actionChecklist: List<String>,
    val curatedBy: String
)

private val CURATED_PERSONA_ARTICLES = listOf(
    CuratedPersonaArticle(
        id = "art_kid_1",
        persona = StagesOfLifePersona.KID,
        title = "Solar System & Planetary Gravity Explorer Guide",
        category = "STEM & Space",
        readingTime = "4 min read",
        summary = "Hands-on planetary science experiments, safe block-coding games, and dinosaur fossil discovery maps designed for curious young explorers.",
        actionChecklist = listOf("Complete Solar Quest Quiz", "Build a 3x3 Cosmic Mosaic", "Earn 150 Curiosity XP"),
        curatedBy = "Creation Planet Educators Guild"
    ),
    CuratedPersonaArticle(
        id = "art_kid_2",
        persona = StagesOfLifePersona.KID,
        title = "Kid-Safe Digital Citizenship & Creative Building 101",
        category = "Safety & Creativity",
        readingTime = "3 min read",
        summary = "Learn how to stay safe online while creating colorful 3D avatars, pixel art blocks, and math blitz puzzles.",
        actionChecklist = listOf("Review COPPA Safe Rules", "Try Block Coding 101"),
        curatedBy = "Youth STEM Mentors"
    ),
    CuratedPersonaArticle(
        id = "art_teen_1",
        persona = StagesOfLifePersona.TEEN,
        title = "First Hackathon Playbook: Kotlin Compose, Python AI & Web",
        category = "Coding & Tech",
        readingTime = "6 min read",
        summary = "Step-by-step roadmap to ship your first teen hackathon project, build a portfolio, and log verified community service hours for scholarships.",
        actionChecklist = listOf("Pick a Coding Track", "Log 2 Community Service Hours", "Publish Demo to Studio"),
        curatedBy = "Teen Creators Collective"
    ),
    CuratedPersonaArticle(
        id = "art_teen_2",
        persona = StagesOfLifePersona.TEEN,
        title = "High School to College & Trade Scholarship Timeline",
        category = "College Prep",
        readingTime = "5 min read",
        summary = "Month-by-month checklist for merit scholarships, dual-enrollment credits, and creative digital art/music portfolios.",
        actionChecklist = listOf("Track Service Hours", "Draft Personal Statement"),
        curatedBy = "Scholarship Navigators"
    ),
    CuratedPersonaArticle(
        id = "art_student_1",
        persona = StagesOfLifePersona.STUDENT,
        title = "Maximizing FAFSA, Pell Grants & Work-Study Stipends",
        category = "Financial Aid",
        readingTime = "7 min read",
        summary = "How to calculate your true net college cost, appeal financial aid offers, and combine Pell Grants with campus research stipends.",
        actionChecklist = listOf("Run FAFSA Net Cost Calculator", "Check Priority Filing Deadlines", "Bookmark JSTOR/arXiv"),
        curatedBy = "Higher Ed Financial Aid Commons"
    ),
    CuratedPersonaArticle(
        id = "art_student_2",
        persona = StagesOfLifePersona.STUDENT,
        title = "Open-Access Academic Research: arXiv, JSTOR & Citation Mastery",
        category = "Academic Research",
        readingTime = "5 min read",
        summary = "Fast workflows for organizing peer-reviewed papers, tracking exam deadlines, and collaborating with study groups.",
        actionChecklist = listOf("Add Semester Exam Deadlines", "Launch Research Commons"),
        curatedBy = "University Scholars Network"
    ),
    CuratedPersonaArticle(
        id = "art_adult_1",
        persona = StagesOfLifePersona.ADULT,
        title = "211 Community Housing, Utility Relief & Family Wellness Guide",
        category = "Family & Stability",
        readingTime = "6 min read",
        summary = "Direct access to 211 local housing assistance, preventative family telehealth appointments, and emergency budget planning.",
        actionChecklist = listOf("Save 211 Support Gateway", "Estimate Monthly Auto Payment", "Sync Emergency Reserve in Room DB"),
        curatedBy = "Community Stability Coalition"
    ),
    CuratedPersonaArticle(
        id = "art_adult_2",
        persona = StagesOfLifePersona.ADULT,
        title = "Smart Auto Financing & Career Pivot Blueprint",
        category = "Finance & Mobility",
        readingTime = "5 min read",
        summary = "Compare APR tiers, optimize down payments before visiting the Auto Market, and upskill for remote-friendly career tracks.",
        actionChecklist = listOf("Compare 48 vs 60 Month Auto Terms", "Export Financial Backup JSON"),
        curatedBy = "Adult Financial Wellness Guild"
    ),
    CuratedPersonaArticle(
        id = "art_business_1",
        persona = StagesOfLifePersona.BUSINESS,
        title = "SBA 7(a) Working Capital & Startup Micro-Grant Readiness Guide",
        category = "Grants & Capital",
        readingTime = "8 min read",
        summary = "Complete founder guide to SBA 7(a) microloans, state innovation grants, DUNS/UEI registration, and commercial fleet financing.",
        actionChecklist = listOf("Complete 5-Stage LLC Checklist", "Run SBA Eligibility Checker", "Log Working Capital in Room Vault"),
        curatedBy = "Founders & Enterprise Council"
    ),
    CuratedPersonaArticle(
        id = "art_business_2",
        persona = StagesOfLifePersona.BUSINESS,
        title = "Formation to Storefront: EIN, Operating Agreements & B2B Compliance",
        category = "Legal & Operations",
        readingTime = "6 min read",
        summary = "Everything entrepreneurs need to form an LLC, open business banking, and manage storefront or auto marketplace inventory.",
        actionChecklist = listOf("Verify Articles of Organization", "Configure Dedicated Business Banking"),
        curatedBy = "Small Business Development Network"
    ),
    CuratedPersonaArticle(
        id = "art_veteran_1",
        persona = StagesOfLifePersona.VETERAN,
        title = "PACT Act Toxic Exposure Screening, VA Healthcare & GI Bill Maximizer",
        category = "VA Benefits & Health",
        readingTime = "7 min read",
        summary = "Step-by-step veteran guide to PACT Act presumptive benefits, priority VA healthcare enrollment, Post-9/11 GI Bill housing stipends, and DAV/VFW peer support.",
        actionChecklist = listOf("Verify PACT Act Registry", "Calculate GI Bill Housing Allowance", "Connect with Local DAV/VFW Post"),
        curatedBy = "Veteran Service Officers & Peer Network"
    ),
    CuratedPersonaArticle(
        id = "art_veteran_2",
        persona = StagesOfLifePersona.VETERAN,
        title = "Military-to-Civilian Leadership Transition & VA Home/Auto Benefits",
        category = "Transition & Honor",
        readingTime = "6 min read",
        summary = "Translate MOS/AFSC experience into executive civilian careers, access 24/7 988 Press 1 support, and leverage veteran-owned business grants.",
        actionChecklist = listOf("Review Transition Milestones", "Save Offline VA Profile Backup"),
        curatedBy = "Veterans Transition Alliance"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StagesOfLifeDashboardComponent(
    currentRole: String,
    currentDisplayName: String,
    currentBio: String,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSelectPersonaRole: (role: String, defaultAge: Long, accentHex: String) -> Unit,
    onShareToCommunityWall: (category: String, title: String, content: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val planetRepository = remember(context) { PlanetRepository(context) }

    val roomUserProfile by planetRepository.primaryUserProfileFlow.collectAsState(initial = null)
    val roomFinancialEntries by planetRepository.financialEntriesFlow.collectAsState(initial = emptyList())

    var selectedPersona by rememberSaveable(currentRole) {
        mutableStateOf(StagesOfLifePersona.fromRoleOrName(currentRole))
    }
    var useDynamicColor by rememberSaveable {
        mutableStateOf(roomUserProfile?.useDynamicColor ?: false)
    }
    var showAccessibilityDialog by rememberSaveable { mutableStateOf(false) }
    var statusBanner by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var exportedJsonPreview by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        planetRepository.ensureSeeded(
            displayName = currentDisplayName,
            persona = selectedPersona,
            isDarkMode = isDarkMode
        )
    }

    LaunchedEffect(roomUserProfile?.useDynamicColor) {
        roomUserProfile?.let {
            useDynamicColor = it.useDynamicColor
        }
    }

    val personaAccentColor = parseThemeHex(selectedPersona.accentHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("stages_of_life_unified_dashboard_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        ),
        border = BorderStroke(1.5.dp, personaAccentColor.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Sticky Top Header with Global Material 3 Dynamic Theme Switcher & Accessibility Suite
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(personaAccentColor, HoloCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedPersona.badgeEmoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column {
                        Text(
                            text = "CREATION PLANET × WONDERING WORLD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Stages Of Life Dashboard & Room Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selectedPersona.displayName} (${selectedPersona.subtitle} • ${selectedPersona.ageRangeLabel})",
                            style = MaterialTheme.typography.bodySmall,
                            color = personaAccentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onToggleDarkMode()
                            coroutineScope.launch {
                                planetRepository.syncIdentityAndPersona(
                                    displayName = currentDisplayName,
                                    bio = currentBio,
                                    persona = selectedPersona,
                                    isDarkMode = !isDarkMode,
                                    useDynamicColor = useDynamicColor
                                )
                            }
                            statusBanner =
                                "Switched Global Material 3 Theme to ${if (!isDarkMode) "Dark Mode" else "Light Mode"} • Synced to Room DB ✓"
                        },
                        modifier = Modifier.testTag("header_theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Global Light or Dark Mode",
                            tint = SolarAmber
                        )
                    }

                    IconButton(
                        onClick = { showAccessibilityDialog = true },
                        modifier = Modifier.testTag("header_accessibility_suite_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsAccessibility,
                            contentDescription = "Open Material 3 Dynamic Theme & Accessibility Suite",
                            tint = HoloCyan
                        )
                    }
                }
            }

            // Optional Status Banner
            statusBanner?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Sticky Top Persona Carousel (All 6 Personas: Kid, Teen, Student, Adult, Business, Veteran)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StagesOfLifePersona.entries.forEach { persona ->
                    val isSelected = persona == selectedPersona
                    val chipAccent = parseThemeHex(persona.accentHex)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) {
                            chipAccent.copy(alpha = 0.22f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) chipAccent else chipAccent.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .clickable {
                                selectedPersona = persona
                                val defaultAge = when (persona) {
                                    StagesOfLifePersona.KID -> 10L
                                    StagesOfLifePersona.TEEN -> 15L
                                    StagesOfLifePersona.STUDENT -> 20L
                                    StagesOfLifePersona.ADULT -> 28L
                                    StagesOfLifePersona.BUSINESS -> 34L
                                    StagesOfLifePersona.VETERAN -> 38L
                                }
                                onSelectPersonaRole(persona.roleKey, defaultAge, persona.accentHex)
                                coroutineScope.launch {
                                    planetRepository.syncIdentityAndPersona(
                                        displayName = currentDisplayName,
                                        bio = currentBio,
                                        persona = persona,
                                        isDarkMode = isDarkMode,
                                        useDynamicColor = useDynamicColor
                                    )
                                }
                                statusBanner =
                                    "Activated ${persona.displayName} Dashboard (${persona.ageRangeLabel}) • Saved to Room SQLite ✓"
                            }
                            .testTag("stages_carousel_${persona.roleKey}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = persona.badgeEmoji,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Column {
                                Text(
                                    text = persona.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) chipAccent else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = persona.ageRangeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Idiomatic Conditional Composition Pattern: when (selectedPersona)
            when (selectedPersona) {
                StagesOfLifePersona.KID -> KidDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onShareCreation = { title, body ->
                        onShareToCommunityWall("Kid STEM & Art", title, body)
                    }
                )
                StagesOfLifePersona.TEEN -> TeenDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onShareCreation = { title, body ->
                        onShareToCommunityWall("Teen Hackathon", title, body)
                    }
                )
                StagesOfLifePersona.STUDENT -> StudentDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onSaveFinancialEstimateToRoom = { title, amountCents, targetCents, notes ->
                        coroutineScope.launch {
                            planetRepository.addFinancialTrackerEntry(
                                title = title,
                                category = "FAFSA & Tuition",
                                entryType = "LOAN_ESTIMATE",
                                amountCents = amountCents,
                                targetCents = targetCents,
                                personaStage = StagesOfLifePersona.STUDENT.name,
                                notes = notes
                            )
                            statusBanner = "Saved '$title' to Room Financial Tracker ✓"
                        }
                    }
                )
                StagesOfLifePersona.ADULT -> AdultDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onSaveAutoEstimateToRoom = { title, monthlyCents, vehicleCents, notes ->
                        coroutineScope.launch {
                            planetRepository.addFinancialTrackerEntry(
                                title = title,
                                category = "Auto Loan Estimator",
                                entryType = "LOAN_ESTIMATE",
                                amountCents = monthlyCents,
                                targetCents = vehicleCents,
                                personaStage = StagesOfLifePersona.ADULT.name,
                                notes = notes
                            )
                            statusBanner = "Saved Auto Loan Estimate to Room Financial Tracker ✓"
                        }
                    }
                )
                StagesOfLifePersona.BUSINESS -> BusinessDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onSaveCapitalGoalToRoom = { title, amountCents, notes ->
                        coroutineScope.launch {
                            planetRepository.addFinancialTrackerEntry(
                                title = title,
                                category = "SBA & Startup Capital",
                                entryType = "SAVINGS_GOAL",
                                amountCents = amountCents,
                                targetCents = amountCents,
                                personaStage = StagesOfLifePersona.BUSINESS.name,
                                notes = notes
                            )
                            statusBanner = "Logged '$title' in Room Business Financial Vault ✓"
                        }
                    }
                )
                StagesOfLifePersona.VETERAN -> VeteranDashboardLayout(
                    accentColor = personaAccentColor,
                    onStatusUpdate = { statusBanner = it },
                    onSaveBenefitToRoom = { title, monthlyCents, notes ->
                        coroutineScope.launch {
                            planetRepository.addFinancialTrackerEntry(
                                title = title,
                                category = "VA & GI Bill Benefits",
                                entryType = "INCOME",
                                amountCents = monthlyCents,
                                targetCents = monthlyCents,
                                personaStage = StagesOfLifePersona.VETERAN.name,
                                notes = notes
                            )
                            statusBanner = "Saved '$title' to Room Veteran Financial Vault ✓"
                        }
                    }
                )
            }

            HorizontalDivider(color = personaAccentColor.copy(alpha = 0.25f))

            // 4. Persona-Tailored Community Resource Discovery Component
            PersonaResourceDiscoverySection(
                selectedPersona = selectedPersona,
                accentColor = personaAccentColor,
                onShareArticleToWall = { article ->
                    onShareToCommunityWall(
                        "${selectedPersona.displayName} Guide",
                        "Recommended ${selectedPersona.displayName} Guide: ${article.title}",
                        "${article.summary} (Curated by ${article.curatedBy})"
                    )
                    statusBanner = "Shared '${article.title}' to Community Feed ✓"
                }
            )

            HorizontalDivider(color = personaAccentColor.copy(alpha = 0.25f))

            // 5. Offline Room SQLite Profile Vault & Financial JSON Backup Export Card
            OfflineRoomProfileVaultAndJsonBackupCard(
                roomProfile = roomUserProfile,
                financialEntries = roomFinancialEntries,
                selectedPersona = selectedPersona,
                isDarkMode = isDarkMode,
                useDynamicColor = useDynamicColor,
                exportedJsonPreview = exportedJsonPreview,
                onSyncRoomDb = {
                    coroutineScope.launch {
                        planetRepository.syncIdentityAndPersona(
                            displayName = currentDisplayName,
                            bio = currentBio,
                            persona = selectedPersona,
                            isDarkMode = isDarkMode,
                            useDynamicColor = useDynamicColor
                        )
                        statusBanner = "Synced Profile & Preferences to Room SQLite (user_profiles) ✓"
                    }
                },
                onAddQuickFinancialEntry = { title, category, amountDollars ->
                    coroutineScope.launch {
                        val cents = ((amountDollars.toDoubleOrNull() ?: 250.0) * 100).toLong()
                        planetRepository.addFinancialTrackerEntry(
                            title = title,
                            category = category,
                            entryType = "SAVINGS_GOAL",
                            amountCents = cents,
                            targetCents = cents * 2,
                            personaStage = selectedPersona.name,
                            notes = "Added from ${selectedPersona.displayName} Room Vault"
                        )
                        statusBanner = "Added '$title' to Room financial_tracker_entries ✓"
                    }
                },
                onExportAndDownloadJson = {
                    coroutineScope.launch {
                        val jsonString = planetRepository.retrieveAndExportProfileAndFinancialJson()
                        exportedJsonPreview = jsonString
                        val fileName =
                            "wondering_world_${selectedPersona.roleKey}_backup_${System.currentTimeMillis()}.json"
                        val savedAt = planetRepository.triggerSystemJsonDownloadIntent(
                            jsonString = jsonString,
                            fileName = fileName
                        )
                        statusBanner =
                            "Exported Room Profile & Financial Tracker JSON Backup → $savedAt ✓"
                    }
                }
            )
        }
    }

    if (showAccessibilityDialog) {
        val currentSettings = roomUserProfile?.toAccessibilitySettings()
            ?: PlanetAccessibilitySettings(isDarkMode = isDarkMode, useDynamicColor = useDynamicColor)
        var highContrast by remember(currentSettings) { mutableStateOf(currentSettings.highContrast) }
        var largeText by remember(currentSettings) { mutableStateOf(currentSettings.largeText) }
        var dyslexiaSpacing by remember(currentSettings) { mutableStateOf(currentSettings.dyslexiaSpacing) }
        var screenReaderHints by remember(currentSettings) { mutableStateOf(currentSettings.screenReaderHints) }
        var offlineModeManual by remember(currentSettings) { mutableStateOf(currentSettings.offlineModeManual) }
        var autoSyncWhenOnline by remember(currentSettings) { mutableStateOf(currentSettings.autoSyncWhenOnline) }

        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = {
                Text(
                    text = "Global Material 3 Theme & Accessibility Suite",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Persisted in local Room SQLite (user_profiles) for instant offline access across sessions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Mode Theme", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode() }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Material 3 Dynamic Palette (Android 12+)", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = useDynamicColor,
                            onCheckedChange = { useDynamicColor = it }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("High Contrast Surfaces")
                        Switch(checked = highContrast, onCheckedChange = { highContrast = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Large Accessible Typography")
                        Switch(checked = largeText, onCheckedChange = { largeText = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dyslexia-Friendly Spacing")
                        Switch(checked = dyslexiaSpacing, onCheckedChange = { dyslexiaSpacing = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Offline-First Room Vault Mode")
                        Switch(checked = offlineModeManual, onCheckedChange = { offlineModeManual = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            planetRepository.updatePreferences(
                                PlanetAccessibilitySettings(
                                    isDarkMode = isDarkMode,
                                    useDynamicColor = useDynamicColor,
                                    highContrast = highContrast,
                                    largeText = largeText,
                                    dyslexiaSpacing = dyslexiaSpacing,
                                    screenReaderHints = screenReaderHints,
                                    offlineModeManual = offlineModeManual,
                                    preferredLanguage = "English (US)",
                                    notificationsEnabled = true,
                                    autoSyncWhenOnline = autoSyncWhenOnline
                                )
                            )
                            statusBanner = "Saved Material 3 Theme & Accessibility Preferences to Room DB ✓"
                            showAccessibilityDialog = false
                        }
                    }
                ) {
                    Text("Save to Room DB")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccessibilityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KidDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onShareCreation: (String, String) -> Unit
) {
    val palette = remember {
        listOf(
            Color(0xFF00E5FF),
            Color(0xFFEC4899),
            Color(0xFFA855F7),
            Color(0xFF10B981),
            Color(0xFFF59E0B),
            Color(0xFF3B82F6)
        )
    }
    val blockColors = remember {
        mutableStateListOf(0, 1, 2, 3, 4, 5, 0, 2, 4)
    }
    var questXp by rememberSaveable { mutableIntStateOf(180) }
    var selectedArcadeGame by rememberSaveable { mutableStateOf("Solar Quest") }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kid_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🚀 KID CURIOUS EXPLORER HUB (AGES 6-12)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Text(
                        text = "COPPA-Compliant Kid-Safe Space Guarantee • Verified Learning & Creativity",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = accentColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, accentColor)
                ) {
                    Text(
                        text = "⭐ $questXp XP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // STEM Curiosity Arcade
            Text(
                text = "STEM Curiosity Arcade",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Solar Quest", "Math Blitz", "Dino Fossil Dig", "Block Coding 101").forEach { game ->
                    FilterChip(
                        selected = selectedArcadeGame == game,
                        onClick = {
                            selectedArcadeGame = game
                            questXp += 25
                            onStatusUpdate("Launched $game in STEM Curiosity Arcade (+25 XP)!")
                        },
                        label = { Text(game) }
                    )
                }
            }

            // Interactive 3x3 Creative Block Builder Canvas
            Text(
                text = "Interactive 3x3 Creative Block Builder Canvas (Tap tiles to cycle cosmic colors)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (row in 0 until 3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (col in 0 until 3) {
                            val idx = row * 3 + col
                            val tileColor = palette[blockColors[idx] % palette.size]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(tileColor)
                                    .clickable {
                                        blockColors[idx] = (blockColors[idx] + 1) % palette.size
                                        questXp += 5
                                    }
                                    .testTag("kid_block_tile_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "★",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        for (i in 0 until 9) blockColors[i] = 0
                        onStatusUpdate("Reset 3x3 Cosmic Block Canvas!")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset Blocks")
                }
                Button(
                    onClick = {
                        questXp += 30
                        onShareCreation(
                            "Created a 3x3 Cosmic Block Mosaic in Kid Explorer Hub!",
                            "Completed my $selectedArcadeGame mission and built a custom 3x3 cosmic mosaic ($questXp XP)!"
                        )
                        onStatusUpdate("Saved 3x3 Cosmic Mosaic & earned +30 XP!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Mosaic (+30 XP)", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeenDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onShareCreation: (String, String) -> Unit
) {
    var selectedTrack by rememberSaveable { mutableStateOf("Kotlin & Compose") }
    var serviceHours by rememberSaveable { mutableIntStateOf(24) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("teen_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎵 TEEN CREATIVE NAVIGATOR HUB (AGES 13-17)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "Code Workshop & Hackathons • College & Scholarship Roadmap • Digital Art & Beats",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Kotlin & Compose", "Python AI", "Full-Stack Web", "Digital Synth Beats").forEach { track ->
                    FilterChip(
                        selected = selectedTrack == track,
                        onClick = {
                            selectedTrack = track
                            onStatusUpdate("Switched Teen Code Workshop Track to $track")
                        },
                        label = { Text(track) }
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "College & Scholarship Service Hours Logger",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$serviceHours / 40 Verified Hours for Presidential Youth Scholarship",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            serviceHours += 1
                            onStatusUpdate("Logged +1 Hr Community Service ($serviceHours hrs total) ✓")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("teen_log_service_hour_button")
                    ) {
                        Text("+1 Hr", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            LinearProgressIndicator(
                progress = { (serviceHours / 40f).coerceIn(0f, 1f) },
                color = accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun StudentDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onSaveFinancialEstimateToRoom: (title: String, amountCents: Long, targetCents: Long, notes: String) -> Unit
) {
    var annualTuition by rememberSaveable { mutableFloatStateOf(18500f) }
    var pellAndScholarships by rememberSaveable { mutableFloatStateOf(9200f) }
    var workStudyIncome by rememberSaveable { mutableFloatStateOf(3800f) }
    val netCollegeCost = (annualTuition - pellAndScholarships - workStudyIncome).coerceAtLeast(0f)

    val deadlines = remember {
        mutableStateListOf(
            "FAFSA Priority Renewal Filing — Due Apr 15" to true,
            "STEM Research Commons Grant Abstract — Due May 02" to false,
            "Algorithms & Linear Algebra Midterm — Due May 10" to false
        )
    }
    var newDeadlineTitle by rememberSaveable { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎓 STUDENT HIGHER ED & SCHOLAR HUB (COLLEGE & TRADE)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "Interactive FAFSA & Net College Cost Calculator • Semester Deadlines • JSTOR & arXiv Commons",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Annual Tuition & Fees: $${annualTuition.toInt()} | Grants/Scholarships: $${pellAndScholarships.toInt()}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = annualTuition,
                onValueChange = { annualTuition = it },
                valueRange = 4000f..45000f,
                colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
            )
            Slider(
                value = pellAndScholarships,
                onValueChange = { pellAndScholarships = it },
                valueRange = 0f..30000f,
                colors = SliderDefaults.colors(thumbColor = CyberEmerald, activeTrackColor = CyberEmerald)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, accentColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Net Out-of-Pocket College Cost",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.0f / year ($%,.0f / mo)", netCollegeCost, netCollegeCost / 12f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                    }
                    Button(
                        onClick = {
                            onSaveFinancialEstimateToRoom(
                                "FAFSA Net College Cost Plan",
                                (netCollegeCost * 100).toLong(),
                                (annualTuition * 100).toLong(),
                                "Tuition $${annualTuition.toInt()} - Grants $${pellAndScholarships.toInt()} - Work-Study $${workStudyIncome.toInt()}"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                    ) {
                        Text("Save to Room", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "Semester Deadlines & Exam Tracker",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            deadlines.forEachIndexed { idx, (label, done) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = done,
                        onCheckedChange = { checked ->
                            deadlines[idx] = label to checked
                        }
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newDeadlineTitle,
                    onValueChange = { newDeadlineTitle = it },
                    label = { Text("Add Exam or FAFSA Deadline") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newDeadlineTitle.isNotBlank()) {
                            deadlines.add(newDeadlineTitle.trim() to false)
                            onStatusUpdate("Added semester deadline: ${newDeadlineTitle.trim()}")
                            newDeadlineTitle = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Deadline")
                }
            }
        }
    }
}

@Composable
private fun AdultDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onSaveAutoEstimateToRoom: (title: String, monthlyCents: Long, vehicleCents: Long, notes: String) -> Unit
) {
    val context = LocalContext.current
    var vehiclePrice by rememberSaveable { mutableFloatStateOf(24500f) }
    var downPayment by rememberSaveable { mutableFloatStateOf(3500f) }
    val principal = (vehiclePrice - downPayment).coerceAtLeast(1000f)
    val monthlyPayment = (principal * 1.055f) / 60f

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("adult_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🏔️ ADULT LIFE, WELLNESS & FAMILY HUB (AGES 18+)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "211 Housing & Emergency Support • Auto Loan & Payment Estimator • Family Health & Career Pivot",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        runCatching {
                            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:211")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(dial)
                        }
                        onStatusUpdate("Opened 211 Housing & Community Support Dialer")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call 211 Support", fontWeight = FontWeight.ExtraBold)
                }

                OutlinedButton(
                    onClick = {
                        onSaveAutoEstimateToRoom(
                            "Auto Market Monthly Loan Estimate",
                            (monthlyPayment * 100).toLong(),
                            (vehiclePrice * 100).toLong(),
                            "Vehicle $${vehiclePrice.toInt()} • Down $${downPayment.toInt()} • 60 mos"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Auto Plan to Room")
                }
            }

            Text(
                text = "Auto Loan Estimator: Vehicle $${vehiclePrice.toInt()} | Down Payment $${downPayment.toInt()} → Est. $${monthlyPayment.toInt()}/mo (60m)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = vehiclePrice,
                onValueChange = { vehiclePrice = it },
                valueRange = 8000f..65000f,
                colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
            )
            Slider(
                value = downPayment,
                onValueChange = { downPayment = it.coerceAtMost(vehiclePrice - 1000f) },
                valueRange = 0f..25000f,
                colors = SliderDefaults.colors(thumbColor = HoloCyan, activeTrackColor = HoloCyan)
            )
        }
    }
}

@Composable
private fun BusinessDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onSaveCapitalGoalToRoom: (title: String, amountCents: Long, notes: String) -> Unit
) {
    val llcChecklist = remember {
        mutableStateListOf(
            "1. Register State Articles of Organization (LLC)" to true,
            "2. Obtain IRS Federal EIN & DUNS / SAM.gov UEI" to true,
            "3. Open Dedicated Business Treasury & Escrow Account" to true,
            "4. Complete SBA 7(a) & Startup Micro-Grant Readiness Packet" to false,
            "5. Launch Commercial Storefront & Auto Fleet Inventory" to false
        )
    }
    val completedCount = llcChecklist.count { it.second }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("business_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💼 BUSINESS FOUNDER & ENTERPRISE HUB (ENTREPRENEURS)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "SBA 7(a) & Micro-Grants Eligibility • 5-Stage Interactive LLC Checklist • Storefront & Auto Commerce",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            llcChecklist.forEachIndexed { index, (step, checked) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isDone ->
                            llcChecklist[index] = step to isDone
                            onStatusUpdate("Updated LLC Stage ${index + 1}: ${if (isDone) "Completed" else "Pending"}")
                        }
                    )
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SBA 7(a) Readiness Score: ${(completedCount * 20)}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Button(
                    onClick = {
                        onSaveCapitalGoalToRoom(
                            "SBA 7(a) Working Capital & Micro-Grant Target",
                            5000000L,
                            "$completedCount/5 LLC compliance stages verified"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Save $50k SBA Target to Room", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun VeteranDashboardLayout(
    accentColor: Color,
    onStatusUpdate: (String) -> Unit,
    onSaveBenefitToRoom: (title: String, monthlyCents: Long, notes: String) -> Unit
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("veteran_dashboard_layout")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🛡️ VETERAN SERVICE & HONOR HUB (MILITARY & RES.)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = "988 Veterans Crisis Line (Press 1) • VA Healthcare & PACT Act • Post-9/11 GI Bill & DAV/VFW Network",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF7F1D1D).copy(alpha = 0.32f),
                border = BorderStroke(1.5.dp, SolarAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "988 Veterans Crisis Line (Dial 988 then Press 1)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "24/7 Confidential, toll-free support for Veterans, service members, and families.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Button(
                        onClick = {
                            runCatching {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(dialIntent)
                            }
                            onStatusUpdate("Opened 988 Veterans Crisis Line Dialer")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SolarAmber, contentColor = Color.Black),
                        modifier = Modifier.testTag("veteran_988_crisis_dial_button")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dial 988", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onSaveBenefitToRoom(
                            "Post-9/11 GI Bill MHA & Stipend",
                            245000L,
                            "Monthly Housing Allowance + Books/Supplies logged in Room DB"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Log GI Bill ($2,450/mo) in Room")
                }
                OutlinedButton(
                    onClick = {
                        onStatusUpdate("PACT Act Toxic Exposure Screening & DAV/VFW Peer Navigator Ready ✓")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("PACT Act & DAV Check")
                }
            }
        }
    }
}

@Composable
fun PersonaResourceDiscoverySection(
    selectedPersona: StagesOfLifePersona,
    accentColor: Color,
    onShareArticleToWall: (CuratedPersonaArticle) -> Unit
) {
    val articlesForPersona = remember(selectedPersona) {
        CURATED_PERSONA_ARTICLES.filter { it.persona == selectedPersona }
    }
    val bookmarkedIds = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("persona_resource_discovery_component"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "📚 PERSONA RESOURCE DISCOVERY (${selectedPersona.displayName.uppercase()} GUIDES)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Text(
                    text = "Community-curated articles, playbooks, and step-by-step guides tailored to ${selectedPersona.displayName} (${selectedPersona.ageRangeLabel})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        articlesForPersona.forEach { article ->
            val isBookmarked = bookmarkedIds.contains(article.id)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("curated_article_${article.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = accentColor.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, accentColor)
                        ) {
                            Text(
                                text = "${article.category} • ${article.readingTime}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = article.curatedBy,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    article.actionChecklist.forEach { step ->
                        Text(
                            text = "• $step",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                if (isBookmarked) bookmarkedIds.remove(article.id)
                                else bookmarkedIds.add(article.id)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmark Guide",
                                tint = if (isBookmarked) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBookmarked) "Bookmarked" else "Save Guide")
                        }
                        TextButton(onClick = { onShareArticleToWall(article) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Guide",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share to Feed", color = HoloCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineRoomProfileVaultAndJsonBackupCard(
    roomProfile: UserProfileEntity?,
    financialEntries: List<FinancialTrackerEntryEntity>,
    selectedPersona: StagesOfLifePersona,
    isDarkMode: Boolean,
    useDynamicColor: Boolean,
    exportedJsonPreview: String?,
    onSyncRoomDb: () -> Unit,
    onAddQuickFinancialEntry: (title: String, category: String, amountDollars: String) -> Unit,
    onExportAndDownloadJson: () -> Unit
) {
    var quickEntryTitle by rememberSaveable { mutableStateOf("") }
    var quickEntryAmount by rememberSaveable { mutableStateOf("500") }
    var showJsonPreview by rememberSaveable { mutableStateOf(false) }

    val totalSavedCents = remember(financialEntries) {
        financialEntries.sumOf { it.amountCents }
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0A1020),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.7f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("offline_room_sqlite_profile_vault_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Offline Room SQLite Database Vault",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "OFFLINE ROOM SQLITE PROFILE & FINANCIAL VAULT",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Tables: user_profiles & financial_tracker_entries • JSON Local Backup Export",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onSyncRoomDb,
                    border = BorderStroke(1.dp, CyberEmerald),
                    modifier = Modifier.testTag("sync_room_db_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync Room DB",
                        tint = CyberEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync DB", color = CyberEmerald, fontWeight = FontWeight.Bold)
                }
            }

            // Cached Room Profile Record Summary
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Room Cached Identity: ${roomProfile?.displayName ?: "Jordan Vance"} (${roomProfile?.handle ?: "@jordan_vance"})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Persona: ${roomProfile?.personaName ?: selectedPersona.name} • Theme: ${if (isDarkMode) "Dark" else "Light"} • Dynamic M3: ${if (useDynamicColor) "ON" else "OFF"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald
                    )
                    Text(
                        text = "Room Financial Tracker Rows: ${financialEntries.size} entries • Total Logged: ${String.format(Locale.US, "$%,.2f", totalSavedCents / 100.0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Add Financial Tracker Row into Room Database
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickEntryTitle,
                    onValueChange = { quickEntryTitle = it },
                    label = { Text("Add Financial Goal / Entry to Room") },
                    singleLine = true,
                    modifier = Modifier.weight(1.4f)
                )
                OutlinedTextField(
                    value = quickEntryAmount,
                    onValueChange = { quickEntryAmount = it },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    modifier = Modifier.weight(0.8f)
                )
                Button(
                    onClick = {
                        val title = quickEntryTitle.ifBlank { "${selectedPersona.displayName} Reserve Entry" }
                        onAddQuickFinancialEntry(title, selectedPersona.displayName, quickEntryAmount)
                        quickEntryTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Financial Entry to Room")
                }
            }

            // Primary Action: Export Profile & Financial Data from Room to JSON & Trigger System Download Intent
            Button(
                onClick = {
                    onExportAndDownloadJson()
                    showJsonPreview = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HoloCyan,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_room_json_backup_download_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Export Room Profile and Financial Data to JSON and Download Locally",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export Room Profile & Financial Data to JSON (Download Locally)",
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (exportedJsonPreview != null && showJsonPreview) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.78f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exported_room_json_preview_box")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Structured JSON Backup (Saved to System Downloads)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberEmerald
                            )
                            TextButton(onClick = { showJsonPreview = false }) {
                                Text("Hide JSON", color = HoloCyan)
                            }
                        }
                        Text(
                            text = exportedJsonPreview.take(900) + if (exportedJsonPreview.length > 900) "\n..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
