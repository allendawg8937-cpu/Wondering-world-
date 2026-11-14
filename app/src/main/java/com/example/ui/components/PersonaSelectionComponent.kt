package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.PlanetRepository
import com.example.data.StagesOfLifePersona
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.parseThemeHex
import kotlinx.coroutines.launch

data class GuidedPersonaBlueprint(
    val persona: StagesOfLifePersona,
    val defaultAge: Long,
    val visualStyleId: String,
    val dashboardHighlights: List<String>,
    val feedTopics: List<String>,
    val curatedFeedPreview: String,
    val safetyBadge: String
)

private val GUIDED_PERSONA_BLUEPRINTS = listOf(
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.KID,
        defaultAge = 10L,
        visualStyleId = "GALAXY_VORTEX",
        dashboardHighlights = listOf("STEM Curiosity Arcade", "3x3 Block Builder Canvas", "Daily XP Quests"),
        feedTopics = listOf("Solar Quest & Space", "Math Blitz Puzzles", "Block Coding 101", "Dinosaur Fossils"),
        curatedFeedPreview = "Surfaces kid-safe STEM reels, cosmic learning games, and interactive block mosaics.",
        safetyBadge = "COPPA Kid-Safe Space"
    ),
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.TEEN,
        defaultAge = 15L,
        visualStyleId = "CYBER_SYNTHWAVE",
        dashboardHighlights = listOf("Code Workshop & Hackathons", "Service Hours Logger", "Digital Synth Studio"),
        feedTopics = listOf("Kotlin & Compose", "Python AI Club", "Scholarship Roadmap", "Music & Beatmaking"),
        curatedFeedPreview = "Curates teen coding hackathons, youth scholarship milestones, and creator video reels.",
        safetyBadge = "Youth Creator Guardrails"
    ),
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.STUDENT,
        defaultAge = 20L,
        visualStyleId = "HYPERCUBE_6D",
        dashboardHighlights = listOf("FAFSA Net Cost Calculator", "Semester Exam Tracker", "JSTOR & arXiv Commons"),
        feedTopics = listOf("FAFSA & Pell Grants", "Academic Research", "Internship Prep", "Campus Study Groups"),
        curatedFeedPreview = "Prioritizes scholarship calculators, academic research papers, and peer study discussions.",
        safetyBadge = "Higher Ed & Trade Track"
    ),
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.ADULT,
        defaultAge = 28L,
        visualStyleId = "CRYSTAL_PRISM",
        dashboardHighlights = listOf("211 Community Gateway", "Auto Loan Estimator", "Family Wellness & Career"),
        feedTopics = listOf("211 Housing & Relief", "Smart Auto Finance", "Career Pivot", "Family Health"),
        curatedFeedPreview = "Tailors housing stability resources, monthly auto loan planning, and local peer tips.",
        safetyBadge = "Verified Community Stability"
    ),
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.BUSINESS,
        defaultAge = 34L,
        visualStyleId = "AURORA_WAVE",
        dashboardHighlights = listOf("SBA 7(a) Micro-Grants", "5-Stage LLC Checklist", "Storefront & Fleet Manager"),
        feedTopics = listOf("SBA 7(a) Capital", "LLC Formation & EIN", "B2B Storefront", "Startup Micro-Grants"),
        curatedFeedPreview = "Highlights founder grant guides, LLC compliance steps, and entrepreneurship reels.",
        safetyBadge = "Founder & Enterprise Suite"
    ),
    GuidedPersonaBlueprint(
        persona = StagesOfLifePersona.VETERAN,
        defaultAge = 38L,
        visualStyleId = "MATRIX_CYBERNETIC",
        dashboardHighlights = listOf("988 Press 1 Crisis Line", "VA & PACT Act Screening", "GI Bill & DAV/VFW Network"),
        feedTopics = listOf("PACT Act & VA Health", "Post-9/11 GI Bill", "DAV / VFW Peer Posts", "Veteran Entrepreneurship"),
        curatedFeedPreview = "Surfaces priority VA healthcare guides, GI Bill housing tools, and veteran community reels.",
        safetyBadge = "24/7 988 Veterans Support"
    )
)

/**
 * `PersonaSelectionComponent`:
 * A guided 3-step experience that walks users through selecting their life stage
 * (Kid, Teen, Student, Adult, Business, Veteran), picking tailored feed topics, and
 * applying a customized dashboard layout, theme, and content feed synced to Room & Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaSelectionComponent(
    currentRole: String,
    currentDisplayName: String,
    currentBio: String,
    isDarkMode: Boolean,
    onApplyCustomizedExperience: (
        roleKey: String,
        defaultAge: Long,
        accentHex: String,
        visualStyleId: String,
        selectedFeedTopics: List<String>
    ) -> Unit,
    onSharePersonaChoiceToFeed: (category: String, title: String, content: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val planetRepository = remember(context) { PlanetRepository(context) }

    val initialPersona = remember(currentRole) { StagesOfLifePersona.fromRoleOrName(currentRole) }

    var currentStep by rememberSaveable { mutableIntStateOf(1) } // 1 = Life Stage, 2 = Feed Topics, 3 = Apply
    var selectedPersona by rememberSaveable(currentRole) { mutableStateOf(initialPersona) }
    var customFeedDensity by rememberSaveable { mutableStateOf("Balanced Feed + Tools") }
    var statusBanner by rememberSaveable { mutableStateOf<String?>(null) }

    val activeBlueprint = remember(selectedPersona) {
        GUIDED_PERSONA_BLUEPRINTS.firstOrNull { it.persona == selectedPersona }
            ?: GUIDED_PERSONA_BLUEPRINTS.last()
    }

    val chosenTopics = remember(selectedPersona) {
        mutableStateListOf<String>().apply {
            addAll(activeBlueprint.feedTopics.take(2))
        }
    }

    val accentColor = parseThemeHex(selectedPersona.accentHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("persona_selection_component_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.78f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Guided Wizard Header
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
                                    listOf(accentColor, HoloCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Guided Persona Selection Wizard",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "GUIDED LIFE STAGE PERSONA SETUP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                        Text(
                            text = "Step $currentStep of 3 • Tailor your dashboard, theme & content feed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = accentColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, accentColor)
                ) {
                    Text(
                        text = "${selectedPersona.badgeEmoji} ${selectedPersona.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 3-Step Interactive Progress Bar + Step Pills
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { (currentStep / 3f).coerceIn(0.33f, 1f) },
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1 to "1. Life Stage",
                        2 to "2. Feed Focus",
                        3 to "3. Review & Apply"
                    ).forEach { (stepNum, stepTitle) ->
                        val isStepActive = currentStep == stepNum
                        FilterChip(
                            selected = isStepActive,
                            onClick = { currentStep = stepNum },
                            label = {
                                Text(
                                    text = stepTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isStepActive) FontWeight.ExtraBold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor.copy(alpha = 0.22f),
                                selectedLabelColor = accentColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("persona_wizard_step_chip_$stepNum")
                        )
                    }
                }
            }

            statusBanner?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberEmerald.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Guided Step Content
            AnimatedContent(targetState = currentStep, label = "persona_wizard_step_content") { step ->
                when (step) {
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Select the life stage that best matches where you are right now:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            GUIDED_PERSONA_BLUEPRINTS.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { blueprint ->
                                        val isSelected = blueprint.persona == selectedPersona
                                        val tileAccent = parseThemeHex(blueprint.persona.accentHex)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) {
                                                tileAccent.copy(alpha = 0.22f)
                                            } else {
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                                            },
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) tileAccent else tileAccent.copy(alpha = 0.35f)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    selectedPersona = blueprint.persona
                                                }
                                                .testTag("persona_select_tile_${blueprint.persona.roleKey}")
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${blueprint.persona.badgeEmoji} ${blueprint.persona.displayName}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (isSelected) tileAccent else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Selected Persona",
                                                            tint = tileAccent,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = blueprint.persona.ageRangeLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = tileAccent
                                                )
                                                Text(
                                                    text = blueprint.persona.subtitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Choose which ${selectedPersona.displayName} topics and tools appear first in your feed:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                activeBlueprint.feedTopics.forEach { topic ->
                                    val isTopicSelected = chosenTopics.contains(topic)
                                    FilterChip(
                                        selected = isTopicSelected,
                                        onClick = {
                                            if (isTopicSelected && chosenTopics.size > 1) {
                                                chosenTopics.remove(topic)
                                            } else if (!isTopicSelected) {
                                                chosenTopics.add(topic)
                                            }
                                        },
                                        label = { Text(topic, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = accentColor.copy(alpha = 0.24f),
                                            selectedLabelColor = accentColor
                                        ),
                                        modifier = Modifier.testTag(
                                            "persona_topic_chip_${topic.lowercase().replace(" ", "_").replace("&", "")}"
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Dashboard Feed Mode:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "Balanced Feed + Tools",
                                    "Video Reels & Community First",
                                    "Focused Tools & Resources Only"
                                ).forEach { density ->
                                    FilterChip(
                                        selected = customFeedDensity == density,
                                        onClick = { customFeedDensity = density },
                                        label = { Text(density) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = HoloCyan.copy(alpha = 0.22f),
                                            selectedLabelColor = HoloCyan
                                        )
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("persona_wizard_review_summary")
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
                                    Text(
                                        text = "${selectedPersona.badgeEmoji} ${selectedPersona.displayName} Experience Ready",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = accentColor
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = CyberEmerald.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = activeBlueprint.safetyBadge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = activeBlueprint.curatedFeedPreview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Dashboard Modules: ${activeBlueprint.dashboardHighlights.joinToString(" • ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Pinned Feed Topics (${chosenTopics.size}): ${chosenTopics.joinToString(", ")} • Layout: $customFeedDensity",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Guided Footer Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStep > 1) currentStep -= 1
                    },
                    enabled = currentStep > 1,
                    modifier = Modifier.testTag("persona_wizard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Step",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back")
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { currentStep += 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("persona_wizard_next_button")
                    ) {
                        Text(
                            text = if (currentStep == 1) "Next: Customize Feed" else "Next: Review & Apply",
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Step",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                onSharePersonaChoiceToFeed(
                                    "Persona",
                                    "Customized my ${selectedPersona.displayName} Dashboard & Content Feed!",
                                    "Focused on ${chosenTopics.joinToString(", ")} with $customFeedDensity layout."
                                )
                                statusBanner = "Shared ${selectedPersona.displayName} setup to Community Feed ✓"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Share Persona Setup",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Setup")
                        }

                        Button(
                            onClick = {
                                onApplyCustomizedExperience(
                                    selectedPersona.roleKey,
                                    activeBlueprint.defaultAge,
                                    selectedPersona.accentHex,
                                    activeBlueprint.visualStyleId,
                                    chosenTopics.toList()
                                )
                                coroutineScope.launch {
                                    planetRepository.syncIdentityAndPersona(
                                        displayName = currentDisplayName,
                                        bio = currentBio,
                                        persona = selectedPersona,
                                        isDarkMode = isDarkMode,
                                        useDynamicColor = false
                                    )
                                }
                                statusBanner =
                                    "✨ Applied ${selectedPersona.displayName} (${selectedPersona.ageRangeLabel}) Dashboard & Content Feed ✓"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("persona_wizard_apply_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Apply Persona Dashboard & Feed",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Apply ${selectedPersona.displayName} Experience",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
