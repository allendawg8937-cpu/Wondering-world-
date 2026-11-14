package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.PersonaStageData
import com.example.data.model.buildDefaultPersonaStageData
import com.example.data.model.normalizePersonaStageId
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlin.math.roundToInt

data class StageActionTask(
    val id: String,
    val title: String,
    val xpReward: Int
)

data class StageQuickDestination(
    val label: String,
    val targetTab: DashboardTab
)

enum class DashboardLifeStage(
    val stageId: String,
    val label: String,
    val defaultAge: Long,
    val emoji: String,
    val accentHex: String,
    val guardrailBadge: String,
    val headline: String,
    val summary: String,
    val stagePillars: List<String>,
    val suggestedTopics: List<String>,
    val checklistTasks: List<StageActionTask>,
    val quickDestinations: List<StageQuickDestination>
) {
    KID(
        stageId = "kid",
        label = "Kid",
        defaultAge = 10L,
        emoji = "🚀",
        accentHex = "#10B981",
        guardrailBadge = "COPPA Kid-Safe Space",
        headline = "STEM Curiosity & Cosmic Explorer Hub",
        summary = "Playful science missions, math blitz puzzles, and moderated discovery badges designed for young learners.",
        stagePillars = listOf("Space & STEM Quests", "Block Coding Canvas", "Safe Reading Badges"),
        suggestedTopics = listOf("Solar Quest & Space", "Math Blitz Puzzles", "Block Coding 101", "Dinosaur Fossils", "Animal Habitats"),
        checklistTasks = listOf(
            StageActionTask("kid_task_1", "Build 3D Solar System Planet Model", 30),
            StageActionTask("kid_task_2", "Complete 5 Math Blitz Puzzles", 25),
            StageActionTask("kid_task_3", "Explore Block Coding 101 Lesson", 35),
            StageActionTask("kid_task_4", "Unlock Friendly Explorer Star", 20)
        ),
        quickDestinations = listOf(
            StageQuickDestination("AI Study Tutor", DashboardTab.AI_TOOLS),
            StageQuickDestination("Studio & Arcade", DashboardTab.STUDIO_ARCADE),
            StageQuickDestination("Open Library", DashboardTab.RESOURCES)
        )
    ),
    TEEN(
        stageId = "teen",
        label = "Teen",
        defaultAge = 15L,
        emoji = "⚡",
        accentHex = "#EC4899",
        guardrailBadge = "Youth Creator Guardrails",
        headline = "Creator Lab, Hackathons & Volunteer Impact",
        summary = "Track community service hours, build real Kotlin & Python projects, and map out early college scholarships.",
        stagePillars = listOf("Code & AI Workshops", "Service Hours Tracker", "Scholarship Prep"),
        suggestedTopics = listOf("Kotlin & Compose", "Python AI Club", "Scholarship Roadmap", "Music & Beatmaking", "Creator Reels"),
        checklistTasks = listOf(
            StageActionTask("teen_task_1", "Ship Mini Compose UI or Synth Track", 35),
            StageActionTask("teen_task_2", "Log 2 Hours Library Volunteer Shift", 30),
            StageActionTask("teen_task_3", "Save 3 Youth Scholarship Deadlines", 25),
            StageActionTask("teen_task_4", "Publish Positive Skill Reel", 25)
        ),
        quickDestinations = listOf(
            StageQuickDestination("Reels & Photo", DashboardTab.STUDIO_ARCADE),
            StageQuickDestination("AI Coach", DashboardTab.AI_TOOLS),
            StageQuickDestination("News & Sports", DashboardTab.NEWS_SPORTS)
        )
    ),
    STUDENT(
        stageId = "student",
        label = "Student",
        defaultAge = 20L,
        emoji = "🎓",
        accentHex = "#6366F1",
        guardrailBadge = "Higher Ed & Trade Track",
        headline = "FAFSA Aid, Exam Prep & Paid Internships",
        summary = "Organize grant deadlines, AI flashcard study blocks, and paid internship or apprenticeship applications.",
        stagePillars = listOf("FAFSA & Pell Tracker", "AI Flashcard Sprints", "CareerOneStop & Internships"),
        suggestedTopics = listOf("FAFSA & Pell Grants", "Academic Research", "Internship Prep", "Campus Study Groups", "Apprenticeships"),
        checklistTasks = listOf(
            StageActionTask("student_task_1", "Verify FAFSA & State Grant Profile", 40),
            StageActionTask("student_task_2", "Complete 3 AI Flashcard Quizzes", 30),
            StageActionTask("student_task_3", "Apply to 2 Paid Internships", 40),
            StageActionTask("student_task_4", "Update Semester Resume Draft", 25)
        ),
        quickDestinations = listOf(
            StageQuickDestination("School & Jobs", DashboardTab.RESOURCES),
            StageQuickDestination("AI Study", DashboardTab.AI_TOOLS),
            StageQuickDestination("Savings Goals", DashboardTab.FINANCE_GOALS)
        )
    ),
    ADULT(
        stageId = "adult",
        label = "Adult",
        defaultAge = 28L,
        emoji = "🏡",
        accentHex = "#06B6D4",
        guardrailBadge = "Community Stability Suite",
        headline = "Financial Resilience, 211 Support & Career Balance",
        summary = "Manage emergency fund checkpoints, access verified 211 housing/health directories, and track weekly wellness.",
        stagePillars = listOf("Emergency Savings Reserve", "211 Housing & Health", "Work-Life Check-Ins"),
        suggestedTopics = listOf("211 Housing & Relief", "Smart Auto Finance", "Career Pivot", "Family Health", "Emergency Reserve"),
        checklistTasks = listOf(
            StageActionTask("adult_task_1", "Review 3-Month Emergency Reserve", 35),
            StageActionTask("adult_task_2", "Check Local 211 & Clinic Locator", 30),
            StageActionTask("adult_task_3", "Log Weekly Family Wellness Goal", 25),
            StageActionTask("adult_task_4", "Explore Delivery / Career Side Gig", 30)
        ),
        quickDestinations = listOf(
            StageQuickDestination("Wallet & Vault", DashboardTab.FINANCE_GOALS),
            StageQuickDestination("211 & Clinics", DashboardTab.RESOURCES),
            StageQuickDestination("Rideshare Hub", DashboardTab.DELIVERY_DRIVING)
        )
    ),
    BUSINESS(
        stageId = "business",
        label = "Business",
        defaultAge = 34L,
        emoji = "💼",
        accentHex = "#F59E0B",
        guardrailBadge = "Founder & Enterprise Suite",
        headline = "SBA Capital Readiness, LLC Compliance & Storefront",
        summary = "Track LLC/EIN milestones, prepare SBA 7(a) micro-grant packets, and grow local community commerce.",
        stagePillars = listOf("SBA 7(a) Micro-Grants", "LLC & EIN Checklist", "Community Storefront"),
        suggestedTopics = listOf("SBA 7(a) Capital", "LLC Formation & EIN", "B2B Storefront", "Startup Micro-Grants", "Fleet Logistics"),
        checklistTasks = listOf(
            StageActionTask("biz_task_1", "Audit LLC Articles & Federal EIN", 40),
            StageActionTask("biz_task_2", "Update Q4 Operating Cashflow Sheet", 35),
            StageActionTask("biz_task_3", "Prepare SBA Microloan Pitch Packet", 45),
            StageActionTask("biz_task_4", "List Service in Marketplace Hub", 30)
        ),
        quickDestinations = listOf(
            StageQuickDestination("Market & Growth", DashboardTab.STABILITY_LOUNGE),
            StageQuickDestination("SBA & Grants", DashboardTab.RESOURCES),
            StageQuickDestination("Treasury Vault", DashboardTab.FINANCE_GOALS)
        )
    ),
    VETERAN(
        stageId = "veteran",
        label = "Veteran",
        defaultAge = 38L,
        emoji = "🎖️",
        accentHex = "#8B5CF6",
        guardrailBadge = "24/7 988 Veterans Support",
        headline = "VA PACT Act Health, GI Bill & DAV Peer Network",
        summary = "Coordinate VA medical center visits, confirm Post-9/11 GI Bill housing stipends, and connect with peer mentors.",
        stagePillars = listOf("988 Press 1 Lifeline", "VA & PACT Act Care", "GI Bill & VFW Peers"),
        suggestedTopics = listOf("PACT Act & VA Health", "Post-9/11 GI Bill", "DAV / VFW Peer Posts", "Veteran Entrepreneurship", "VA Housing Stability"),
        checklistTasks = listOf(
            StageActionTask("vet_task_1", "Complete VA PACT Act Health Screen", 45),
            StageActionTask("vet_task_2", "Verify Post-9/11 GI Bill Housing", 40),
            StageActionTask("vet_task_3", "Connect with DAV / VFW Peer Mentor", 35),
            StageActionTask("vet_task_4", "Pin Nearest VA Clinic & 988 Press 1", 25)
        ),
        quickDestinations = listOf(
            StageQuickDestination("VA & 988 Aid", DashboardTab.RESOURCES),
            StageQuickDestination("Peer Network", DashboardTab.FRIENDS),
            StageQuickDestination("Finance Goals", DashboardTab.FINANCE_GOALS)
        )
    );

    companion object {
        fun fromRoleOrStageId(value: String?): DashboardLifeStage {
            val normalized = normalizePersonaStageId(value.orEmpty())
            return entries.firstOrNull { it.stageId == normalized } ?: ADULT
        }
    }
}

/**
 * Explicit State-Based Persona View Machine for toggling between Kid, Teen, Student,
 * Adult, Business, and Veteran dashboard views.
 */
sealed interface PersonaViewState {
    val stage: DashboardLifeStage
    val stateTitle: String
    val stateHighlights: List<Pair<String, String>>
    val primaryActionLabel: String
    val primaryActionTab: DashboardTab

    data class KidView(override val stage: DashboardLifeStage = DashboardLifeStage.KID) : PersonaViewState {
        override val stateTitle = "Cosmic STEM Lab & Curiosity Board"
        override val stateHighlights = listOf(
            "Safety Mode" to "COPPA Filter ON",
            "Learning Focus" to "Space, Math & Blocks",
            "Daily Streak" to "Explorer Rank ★"
        )
        override val primaryActionLabel = "Launch STEM & Block Studio"
        override val primaryActionTab = DashboardTab.STUDIO_ARCADE
    }

    data class TeenView(override val stage: DashboardLifeStage = DashboardLifeStage.TEEN) : PersonaViewState {
        override val stateTitle = "Youth Creator Studio & Hackathon Track"
        override val stateHighlights = listOf(
            "Creator Lab" to "Reels + Audio Synth",
            "Impact Track" to "Volunteer Service Log",
            "Future Ready" to "Scholarships & Code"
        )
        override val primaryActionLabel = "Open Reels & Synth Studio"
        override val primaryActionTab = DashboardTab.STUDIO_ARCADE
    }

    data class StudentView(override val stage: DashboardLifeStage = DashboardLifeStage.STUDENT) : PersonaViewState {
        override val stateTitle = "FAFSA Aid Radar & Exam Study Cockpit"
        override val stateHighlights = listOf(
            "Aid Status" to "Pell & Grants Synced",
            "Study Mode" to "AI Flashcards Ready",
            "Career Path" to "Paid Internships"
        )
        override val primaryActionLabel = "Open AI Study & Flashcards"
        override val primaryActionTab = DashboardTab.AI_TOOLS
    }

    data class AdultView(override val stage: DashboardLifeStage = DashboardLifeStage.ADULT) : PersonaViewState {
        override val stateTitle = "Home Resilience, 211 Relief & Savings"
        override val stateHighlights = listOf(
            "Reserve Goal" to "3-Mo Safety Net",
            "Local Support" to "211 Housing & Health",
            "Earnings" to "Career & Gig Hubs"
        )
        override val primaryActionLabel = "Open Wallet & Savings Vault"
        override val primaryActionTab = DashboardTab.FINANCE_GOALS
    }

    data class BusinessView(override val stage: DashboardLifeStage = DashboardLifeStage.BUSINESS) : PersonaViewState {
        override val stateTitle = "Founder Capital, LLC & Storefront Ops"
        override val stateHighlights = listOf(
            "Capital Prep" to "SBA 7(a) Readiness",
            "Compliance" to "LLC / EIN Tracker",
            "Commerce" to "Market & Fleet Hub"
        )
        override val primaryActionLabel = "Open Business Market Hub"
        override val primaryActionTab = DashboardTab.STABILITY_LOUNGE
    }

    data class VeteranView(override val stage: DashboardLifeStage = DashboardLifeStage.VETERAN) : PersonaViewState {
        override val stateTitle = "VA PACT Act, GI Bill & 988 Support Desk"
        override val stateHighlights = listOf(
            "Crisis Support" to "988 Press 1 (24/7)",
            "Education" to "Post-9/11 GI Bill",
            "Peer Network" to "DAV / VFW Mentors"
        )
        override val primaryActionLabel = "Open VA & 988 Resource Hub"
        override val primaryActionTab = DashboardTab.RESOURCES
    }

    companion object {
        fun fromStage(stage: DashboardLifeStage): PersonaViewState = when (stage) {
            DashboardLifeStage.KID -> KidView(stage)
            DashboardLifeStage.TEEN -> TeenView(stage)
            DashboardLifeStage.STUDENT -> StudentView(stage)
            DashboardLifeStage.ADULT -> AdultView(stage)
            DashboardLifeStage.BUSINESS -> BusinessView(stage)
            DashboardLifeStage.VETERAN -> VeteranView(stage)
        }
    }
}

/**
 * `PersonaDashboardComponent`:
 * Interactive state-based persona selection component in the main dashboard that allows users
 * to toggle between Kid, Teen, Student, Adult, Business, and Veteran views with distinct
 * stage layouts, metrics, checklists, and Cloud Firestore persistence under
 * `users/{userId}/persona_stage_data/{stageId}`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaDashboardComponent(
    currentRole: String,
    currentDisplayName: String,
    personaStageDataMap: Map<String, PersonaStageData>,
    isSaving: Boolean,
    statusBanner: String?,
    isAuthenticatedForCloud: Boolean,
    onSelectLifeStage: (stageId: String, defaultAge: Long, accentHex: String) -> Unit,
    onSavePersonaStageData: (PersonaStageData) -> Unit,
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareToWall: (category: String, title: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State-based active persona synchronized with ViewModel role + instant optimistic selection
    var selectedStageState by rememberSaveable(currentRole) {
        mutableStateOf(DashboardLifeStage.fromRoleOrStageId(currentRole))
    }
    LaunchedEffect(currentRole) {
        val synced = DashboardLifeStage.fromRoleOrStageId(currentRole)
        if (synced != selectedStageState) {
            selectedStageState = synced
        }
    }

    val activeStage = selectedStageState
    val activeViewState = remember(activeStage) { PersonaViewState.fromStage(activeStage) }
    val stageColor = parseThemeHex(activeStage.accentHex)

    val stageData = personaStageDataMap[activeStage.stageId]
        ?: remember(activeStage.stageId) { buildDefaultPersonaStageData(activeStage.stageId) }

    var draftGoal by remember(activeStage.stageId) { mutableStateOf(stageData.focusGoal) }
    var draftNote by remember(activeStage.stageId) { mutableStateOf(stageData.customNote) }
    var activeSection by rememberSaveable { mutableStateOf("OVERVIEW") }

    // Keep text fields synchronized when switching stages or when fresh Firestore data loads
    LaunchedEffect(activeStage.stageId, stageData.focusGoal, stageData.customNote, stageData.updatedAt) {
        draftGoal = stageData.focusGoal
        draftNote = stageData.customNote
    }

    val metricProgress = remember(stageData.primaryMetricValue, stageData.primaryMetricTarget) {
        val target = stageData.primaryMetricTarget.coerceAtLeast(1.0)
        (stageData.primaryMetricValue / target).toFloat().coerceIn(0f, 1f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("persona_dashboard_component_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        border = BorderStroke(1.5.dp, stageColor.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header & Cloud Sync Status Pill
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(stageColor, HoloCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeStage.emoji,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column {
                        Text(
                            text = "PERSONA DASHBOARD",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = stageColor
                        )
                        Text(
                            text = "${activeStage.label} Stage • $currentDisplayName",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isAuthenticatedForCloud) CyberEmerald.copy(alpha = 0.16f) else SolarAmber.copy(alpha = 0.16f),
                    border = BorderStroke(
                        1.dp,
                        if (isAuthenticatedForCloud) CyberEmerald else SolarAmber
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAuthenticatedForCloud) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                            contentDescription = if (isAuthenticatedForCloud) "Firestore Sync Active" else "Local Stage Mode",
                            tint = if (isAuthenticatedForCloud) CyberEmerald else SolarAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isAuthenticatedForCloud) "Cloud Synced" else "Local Draft",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isAuthenticatedForCloud) CyberEmerald else SolarAmber
                        )
                    }
                }
            }

            // 2. Interactive 6-Stage State Switcher Grid (All 6 Persona Views visible at once)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Toggle Persona View (Kid, Teen, Student, Adult, Business, Veteran):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("persona_stage_selector_row")
                ) {
                    val columns = if (maxWidth >= 560.dp) 6 else 3
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DashboardLifeStage.entries.chunked(columns).forEach { rowStages ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowStages.forEach { stage ->
                                    val isSelected = stage == activeStage
                                    val pillColor = parseThemeHex(stage.accentHex)
                                    val savedXp = personaStageDataMap[stage.stageId]?.stageScoreOrXp
                                        ?: buildDefaultPersonaStageData(stage.stageId).stageScoreOrXp

                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) {
                                            pillColor.copy(alpha = 0.24f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                        },
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) {
                                                pillColor
                                            } else {
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                            }
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                selectedStageState = stage
                                                onSelectLifeStage(stage.stageId, stage.defaultAge, stage.accentHex)
                                            }
                                            .testTag("persona_stage_pill_${stage.stageId}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = stage.emoji,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = stage.label,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    color = if (isSelected) pillColor else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "$savedXp XP",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            }

            // 3. State-Based Persona View Content (AnimatedContent switching between 6 distinct PersonaViewStates)
            AnimatedContent(
                targetState = activeViewState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "persona_stage_dynamic_content"
            ) { viewState ->
                val stage = viewState.stage
                val accent = parseThemeHex(stage.accentHex)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = accent.copy(alpha = 0.11f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("persona_stage_hero_${stage.stageId}")
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
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = accent.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = stage.guardrailBadge,
                                        tint = accent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = stage.guardrailBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = accent
                                    )
                                }
                            }
                            Text(
                                text = "🏆 ${stageData.stageScoreOrXp} Stage XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = accent
                            )
                        }

                        Text(
                            text = stage.headline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stage.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            stage.stagePillars.forEach { pillar ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "✦ $pillar",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        // Distinct State-Tailored Persona View Panel
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("persona_view_module_${stage.stageId}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = viewState.stateTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accent
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    viewState.stateHighlights.forEach { (metricTitle, metricValue) ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = accent.copy(alpha = 0.11f),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text(
                                                    text = metricTitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = metricValue,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                                Button(
                                    onClick = { onNavigateToTab(viewState.primaryActionTab) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accent,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("persona_primary_action_${stage.stageId}")
                                ) {
                                    Text(
                                        text = viewState.primaryActionLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = viewState.primaryActionLabel,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Stage-tailored 1-tap navigation buttons
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            stage.quickDestinations.forEach { dest ->
                                OutlinedButton(
                                    onClick = { onNavigateToTab(dest.targetTab) },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, accent),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("persona_quick_nav_${dest.targetTab.name.lowercase()}")
                                ) {
                                    Text(
                                        text = dest.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = accent
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Open ${dest.label}",
                                        tint = accent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Live Stage Metric Progress Tracker (Step -1 / +1 & Save to Firestore)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)),
                modifier = Modifier.fillMaxWidth()
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stageData.primaryMetricLabel,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${stageData.primaryMetricValue.roundToInt()} / ${stageData.primaryMetricTarget.roundToInt()} (${(metricProgress * 100).roundToInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = stageColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val nextVal = (stageData.primaryMetricValue - 1.0).coerceAtLeast(0.0)
                                    onSavePersonaStageData(
                                        stageData.copy(
                                            stageId = activeStage.stageId,
                                            stageLabel = activeStage.label,
                                            focusGoal = draftGoal,
                                            customNote = draftNote,
                                            primaryMetricValue = nextVal
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("persona_metric_decrement_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease stage metric"
                                )
                            }
                            IconButton(
                                onClick = {
                                    val stepAmount = if (activeStage == DashboardLifeStage.BUSINESS) 5.0 else 1.0
                                    val nextVal = (stageData.primaryMetricValue + stepAmount)
                                        .coerceAtMost(stageData.primaryMetricTarget.coerceAtLeast(1.0))
                                    onSavePersonaStageData(
                                        stageData.copy(
                                            stageId = activeStage.stageId,
                                            stageLabel = activeStage.label,
                                            focusGoal = draftGoal,
                                            customNote = draftNote,
                                            primaryMetricValue = nextVal,
                                            stageScoreOrXp = (stageData.stageScoreOrXp + 15L).coerceAtMost(1000000L)
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(CircleShape)
                                    .background(stageColor.copy(alpha = 0.22f))
                                    .testTag("persona_metric_increment_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase stage metric",
                                    tint = stageColor
                                )
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { metricProgress },
                        color = stageColor,
                        trackColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }

            // Compact Section Link Buttons inside Persona Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "OVERVIEW" to "⚡ Stage Hub",
                    "CHECKLIST" to "✅ Checklist (${stageData.completedTaskIds.size}/${activeStage.checklistTasks.size})",
                    "GOAL_NOTE" to "📝 Goal & Note"
                ).forEach { (secKey, secLabel) ->
                    val selected = activeSection == secKey
                    FilterChip(
                        selected = selected,
                        onClick = { activeSection = secKey },
                        label = {
                            Text(
                                text = secLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = stageColor.copy(alpha = 0.22f),
                            selectedLabelColor = stageColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("persona_dash_section_${secKey.lowercase()}")
                    )
                }
            }

            if (activeSection == "OVERVIEW") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                    modifier = Modifier.fillMaxWidth()
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
                                text = "Current Goal: ${draftGoal.ifBlank { "Set your ${activeStage.label} goal" }}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Topics: ${stageData.pinnedTopics.take(3).joinToString(" • ").ifBlank { "None pinned" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        OutlinedButton(
                            onClick = { activeSection = "GOAL_NOTE" },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, stageColor.copy(alpha = 0.7f)),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text("Edit", style = MaterialTheme.typography.labelSmall, color = stageColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (activeSection == "CHECKLIST") {
                // 5. Interactive Stage Action Checklist (persists completedTaskIds & XP)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${activeStage.label} Action Checklist (${stageData.completedTaskIds.size}/${activeStage.checklistTasks.size} done)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    activeStage.checklistTasks.forEach { task ->
                        val isDone = stageData.completedTaskIds.contains(task.id)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDone) stageColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                            border = BorderStroke(
                                1.dp,
                                if (isDone) stageColor.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    val updatedTasks = if (isDone) {
                                        stageData.completedTaskIds.filterNot { it == task.id }
                                    } else {
                                        (stageData.completedTaskIds + task.id).distinct().take(20)
                                    }
                                    val xpDelta = if (isDone) -task.xpReward.toLong() else task.xpReward.toLong()
                                    onSavePersonaStageData(
                                        stageData.copy(
                                            stageId = activeStage.stageId,
                                            stageLabel = activeStage.label,
                                            focusGoal = draftGoal,
                                            customNote = draftNote,
                                            completedTaskIds = updatedTasks,
                                            stageScoreOrXp = (stageData.stageScoreOrXp + xpDelta).coerceIn(0L, 1000000L)
                                        )
                                    )
                                }
                                .testTag("persona_checklist_item_${task.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (isDone) "Completed ${task.title}" else "Mark ${task.title} complete",
                                        tint = if (isDone) stageColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Medium,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }
                                Text(
                                    text = "+${task.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = stageColor
                                )
                            }
                        }
                    }
                }
            }

            if (activeSection == "GOAL_NOTE") {
                // 6. Pinned Feed Topics for This Life Stage
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Pinned ${activeStage.label} Topics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeStage.suggestedTopics.forEach { topic ->
                            val pinned = stageData.pinnedTopics.contains(topic)
                            FilterChip(
                                selected = pinned,
                                onClick = {
                                    val nextTopics = if (pinned) {
                                        stageData.pinnedTopics.filterNot { it == topic }
                                    } else {
                                        (stageData.pinnedTopics + topic).distinct().take(12)
                                    }
                                    onSavePersonaStageData(
                                        stageData.copy(
                                            stageId = activeStage.stageId,
                                            stageLabel = activeStage.label,
                                            focusGoal = draftGoal,
                                            customNote = draftNote,
                                            pinnedTopics = nextTopics
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        text = topic,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = stageColor.copy(alpha = 0.22f),
                                    selectedLabelColor = stageColor
                                ),
                                modifier = Modifier.testTag("persona_topic_chip_${topic.lowercase().replace(Regex("[^a-z0-9]+"), "_")}")
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                // 7. Stage-Specific Focus Goal & Journal Note
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = draftGoal,
                        onValueChange = { draftGoal = it.take(140) },
                        label = { Text("${activeStage.label} Focus Goal") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Focus Goal",
                                tint = stageColor
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("persona_focus_goal_input")
                    )

                    OutlinedTextField(
                        value = draftNote,
                        onValueChange = { draftNote = it.take(400) },
                        label = { Text("${activeStage.label} Journal & Notes") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Stage Note",
                                tint = stageColor
                            )
                        },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("persona_custom_note_input")
                    )
                }
            }

            AnimatedVisibility(visible = !statusBanner.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = stageColor.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, stageColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusBanner.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            // 8. Primary Save & Share Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onSavePersonaStageData(
                            stageData.copy(
                                stageId = activeStage.stageId,
                                stageLabel = activeStage.label,
                                focusGoal = draftGoal.trim().take(140),
                                customNote = draftNote.trim().take(400),
                                stageScoreOrXp = (stageData.stageScoreOrXp + 10L).coerceAtMost(1000000L)
                            )
                        )
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = stageColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("save_persona_stage_data_button")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Save Stage Data",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAuthenticatedForCloud) "Save ${activeStage.label} to Firestore" else "Save ${activeStage.label} Draft",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        onShareToWall(
                            "Milestone",
                            "${activeStage.emoji} ${activeStage.label} Stage Update",
                            "Focused on: $draftGoal • ${stageData.completedTaskIds.size}/${activeStage.checklistTasks.size} stage goals done (${stageData.stageScoreOrXp} XP)!"
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, stageColor),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("share_persona_stage_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share stage progress to feed",
                        tint = stageColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
