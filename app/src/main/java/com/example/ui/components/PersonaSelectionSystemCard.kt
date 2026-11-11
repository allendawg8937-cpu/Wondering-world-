package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

data class StagesOfLifePersonaProfile(
    val id: String,
    val roleLabel: String,
    val emoji: String,
    val defaultAge: Long,
    val minAgeRequirement: Int,
    val requiresAdultAgeVerification: Boolean,
    val defaultThemeHex: String,
    val defaultThemeName: String,
    val defaultVisualStyleId: String,
    val unlockedSections: List<Pair<String, DashboardTab>>,
    val restrictedSectionsNote: String,
    val tailoredFeedHighlights: List<String>,
    val icon: ImageVector
)

val STAGES_OF_LIFE_PERSONAS = listOf(
    StagesOfLifePersonaProfile(
        id = "veteran",
        roleLabel = "Veteran",
        emoji = "🛡️",
        defaultAge = 34L,
        minAgeRequirement = 18,
        requiresAdultAgeVerification = true,
        defaultThemeHex = "#15803D",
        defaultThemeName = "Forest Green",
        defaultVisualStyleId = "AURORA_WAVE",
        unlockedSections = listOf(
            "VA Benefits & 988 Crisis Line" to DashboardTab.RESOURCES,
            "Veteran Career & Logistics Hub" to DashboardTab.FINANCE_GOALS,
            "18+ Adult Connections Lounge" to DashboardTab.FRIENDS,
            "3D Studio & Avatar Forge" to DashboardTab.HUB_3D
        ),
        restrictedSectionsNote = "Full 18+ Verified Access • Priority Veteran Support Enabled",
        tailoredFeedHighlights = listOf(
            "🛡️ VA Disability & GI Bill Housing Allowance Updates",
            "🤝 Veteran Peer Mentorship & Transition Careers Meetup",
            "🏦 Zero-Down VA Home Loan & Financial Stability Guide"
        ),
        icon = Icons.Default.MilitaryTech
    ),
    StagesOfLifePersonaProfile(
        id = "business_owner",
        roleLabel = "Business Owner",
        emoji = "💼",
        defaultAge = 32L,
        minAgeRequirement = 18,
        requiresAdultAgeVerification = true,
        defaultThemeHex = "#1E3A8A",
        defaultThemeName = "Solid Navy",
        defaultVisualStyleId = "HYPERCUBE_6D",
        unlockedSections = listOf(
            "SBA Microloans & Grants Hub" to DashboardTab.RESOURCES,
            "Executive Budget & Cashflow" to DashboardTab.FINANCE_GOALS,
            "B2B Founder & Adult Network (18+)" to DashboardTab.FRIENDS,
            "Delivery & Commerce Portal" to DashboardTab.DELIVERY_DRIVING
        ),
        restrictedSectionsNote = "Full 18+ Verified Access • Business & Commerce Suite Active",
        tailoredFeedHighlights = listOf(
            "💼 SBA Microloan & Local Small Business Grant Deadlines",
            "📈 Cashflow Forecasting & Tax Deduction Checklist for Founders",
            "🤝 Local Entrepreneur & Creator B2B Networking Showcase"
        ),
        icon = Icons.Default.BusinessCenter
    ),
    StagesOfLifePersonaProfile(
        id = "adult",
        roleLabel = "Adult",
        emoji = "🌟",
        defaultAge = 28L,
        minAgeRequirement = 18,
        requiresAdultAgeVerification = true,
        defaultThemeHex = "#2563EB",
        defaultThemeName = "Solid Blue",
        defaultVisualStyleId = "HYPERCUBE_6D",
        unlockedSections = listOf(
            "18+ Adult Social & Connections Hub" to DashboardTab.FRIENDS,
            "Personal Finance & Loan Navigator" to DashboardTab.FINANCE_GOALS,
            "Resource Hub & Career Board" to DashboardTab.RESOURCES,
            "Live Video & Community Wall" to DashboardTab.VIDEO_CHAT
        ),
        restrictedSectionsNote = "18+ Age Verification Required for Adult Social Connections",
        tailoredFeedHighlights = listOf(
            "🌟 18+ Adult Community Connections & Local Events Mixer",
            "🏡 High-Yield Savings, Credit Building & Career Growth",
            "🧘 Late-Night Wellness, Horoscopes & 3D Studio Showcase"
        ),
        icon = Icons.Default.Person
    ),
    StagesOfLifePersonaProfile(
        id = "college_student",
        roleLabel = "College Student",
        emoji = "🎓",
        defaultAge = 20L,
        minAgeRequirement = 17,
        requiresAdultAgeVerification = true,
        defaultThemeHex = "#7C3AED",
        defaultThemeName = "Solid Purple",
        defaultVisualStyleId = "GALAXY_VORTEX",
        unlockedSections = listOf(
            "FAFSA, Pell Grants & Student Loans" to DashboardTab.RESOURCES,
            "AI Study Flashcards & Career Track" to DashboardTab.AI_TOOLS,
            "Campus & 18+ Young Adult Network" to DashboardTab.FRIENDS,
            "Student Budget & Textbook Saver" to DashboardTab.FINANCE_GOALS
        ),
        restrictedSectionsNote = "FAFSA & Campus Mode Active • 18+ Verification Gate Enabled",
        tailoredFeedHighlights = listOf(
            "🎓 Priority FAFSA & Pell Grant Application Walkthrough",
            "💻 Paid Summer Tech Internships & Campus Work-Study",
            "🧠 AI Study Flashcards & 3D Three.js Portfolio Builder"
        ),
        icon = Icons.Default.School
    ),
    StagesOfLifePersonaProfile(
        id = "teenager",
        roleLabel = "Teenager",
        emoji = "🎒",
        defaultAge = 15L,
        minAgeRequirement = 13,
        requiresAdultAgeVerification = false,
        defaultThemeHex = "#F97316",
        defaultThemeName = "Solid Orange",
        defaultVisualStyleId = "CYBER_SYNTHWAVE",
        unlockedSections = listOf(
            "3D Youth Arcade & Game Studio" to DashboardTab.STUDIO_ARCADE,
            "High School Prep & Coding Lab" to DashboardTab.AI_TOOLS,
            "Youth Safe Resource & Crisis Hub" to DashboardTab.RESOURCES,
            "3D Avatar & Theme Customizer" to DashboardTab.THEME_PROFILE
        ),
        restrictedSectionsNote = "🔒 Youth Protection Active: 18+ Adult Connections & Financial Contracts Locked",
        tailoredFeedHighlights = listOf(
            "🎮 3D Youth Arcade High Scores & Creative Avatar Showcase",
            "🚀 Teen Coding, Three.js 3D Design & Robotics Challenges",
            "🎒 High School Study Tips, First Part-Time Job & Scholarship Prep"
        ),
        icon = Icons.Default.SportsEsports
    ),
    StagesOfLifePersonaProfile(
        id = "child",
        roleLabel = "Child",
        emoji = "🧸",
        defaultAge = 10L,
        minAgeRequirement = 6,
        requiresAdultAgeVerification = false,
        defaultThemeHex = "#EAB308",
        defaultThemeName = "Solid Yellow",
        defaultVisualStyleId = "CRYSTAL_PRISM",
        unlockedSections = listOf(
            "Kids 3D Arcade & Puzzle World" to DashboardTab.STUDIO_ARCADE,
            "3D Avatar & Color Coloring Studio" to DashboardTab.THEME_PROFILE,
            "Safe Science, Space & Art Feed" to DashboardTab.HUB_3D
        ),
        restrictedSectionsNote = "🛡️ Strict Child Safe Mode: 18+ Adult Sections, Public DMs & Unverified Links Locked",
        tailoredFeedHighlights = listOf(
            "🧸 Kids 3D Arcade: Build & Color Your Own 3D Pyramid & Avatar!",
            "🦖 Amazing Space, Dinosaurs & Science Discovery Facts",
            "🎨 Safe Creative Art & Music Jukebox for Kids"
        ),
        icon = Icons.Default.ChildCare
    )
)

/**
 * Persona Selection System for Profile Creation & Dashboard Customization.
 * Customizes default solid color theme, available app sections (e.g., 18+ adult connections
 * vs. kids/youth arcade), tailored feed content, and enforces age verification checks.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaSelectionSystemCard(
    currentRole: String,
    currentAge: Long,
    onApplyPersona: (role: String, age: Long, defaultColorHex: String, visualStyleId: String) -> Unit,
    onNavigateToSection: (DashboardTab) -> Unit = {},
    onPublishPersonaPost: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val initialPersona = remember(currentRole, currentAge) {
        STAGES_OF_LIFE_PERSONAS.find {
            it.roleLabel.equals(currentRole, ignoreCase = true)
        } ?: when {
            currentAge < 13L -> STAGES_OF_LIFE_PERSONAS.last()
            currentAge < 18L -> STAGES_OF_LIFE_PERSONAS[4]
            else -> STAGES_OF_LIFE_PERSONAS[2]
        }
    }

    var selectedPersona by remember { mutableStateOf(initialPersona) }
    var birthYearInput by remember { mutableStateOf((2026 - currentAge.toInt()).toString()) }
    var adultAgeConfirmedCheckbox by remember { mutableStateOf(currentAge >= 18L) }
    var isAdultAgeVerified by remember { mutableStateOf(currentAge >= 18L) }
    var verificationMessage by remember { mutableStateOf<String?>(null) }

    val calculatedAgeFromYear = remember(birthYearInput) {
        val yr = birthYearInput.trim().toIntOrNull()
        if (yr != null && yr in 1920..2025) (2026 - yr).toLong() else currentAge
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("persona_selection_system_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎭 STAGES OF LIFE PERSONA & AGE-VERIFIED CUSTOMIZER",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select Veteran, Business Owner, Adult, Student, Teenager, or Child to tailor your theme, app sections & feed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (selectedPersona.requiresAdultAgeVerification && isAdultAgeVerified) {
                        CyberEmerald.copy(alpha = 0.18f)
                    } else {
                        SolarAmber.copy(alpha = 0.20f)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (!selectedPersona.requiresAdultAgeVerification) {
                            "YOUTH SAFE MODE"
                        } else if (isAdultAgeVerified) {
                            "18+ VERIFIED"
                        } else {
                            "VERIFY 18+ AGE"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = if (!selectedPersona.requiresAdultAgeVerification || !isAdultAgeVerified) SolarAmber else CyberEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Persona Cards Grid / Scroll Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                STAGES_OF_LIFE_PERSONAS.forEach { persona ->
                    val isSelected = selectedPersona.id == persona.id
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            }
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .width(172.dp)
                            .clickable {
                                selectedPersona = persona
                                birthYearInput = (2026 - persona.defaultAge.toInt()).toString()
                                if (!persona.requiresAdultAgeVerification) {
                                    isAdultAgeVerified = false
                                    adultAgeConfirmedCheckbox = false
                                    onApplyPersona(
                                        persona.roleLabel,
                                        persona.defaultAge,
                                        persona.defaultThemeHex,
                                        persona.defaultVisualStyleId
                                    )
                                    verificationMessage = "✓ Activated ${persona.emoji} ${persona.roleLabel} Persona with ${persona.defaultThemeName} theme & Youth-Safe Arcade sections!"
                                } else if (isAdultAgeVerified && calculatedAgeFromYear >= 18L) {
                                    onApplyPersona(
                                        persona.roleLabel,
                                        persona.defaultAge,
                                        persona.defaultThemeHex,
                                        persona.defaultVisualStyleId
                                    )
                                    verificationMessage = "✓ Activated 18+ Verified ${persona.emoji} ${persona.roleLabel} Persona with ${persona.defaultThemeName} theme!"
                                } else {
                                    verificationMessage = "Please complete the 18+ Age Verification check below to unlock ${persona.roleLabel} adult sections."
                                }
                            }
                            .testTag("persona_card_${persona.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${persona.emoji} ${persona.roleLabel}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected Persona",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Default Theme: ${persona.defaultThemeName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (persona.requiresAdultAgeVerification) "18+ Age-Verified Tier" else "Youth & Arcade Tier",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (persona.requiresAdultAgeVerification) CyberEmerald else SolarAmber
                            )
                        }
                    }
                }
            }

            // Age Verification Check Panel for 18+ Personas (Adult Connections, Veteran, Business Owner)
            AnimatedVisibility(visible = selectedPersona.requiresAdultAgeVerification) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isAdultAgeVerified) CyberEmerald else SolarAmber
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isAdultAgeVerified) Icons.Default.VerifiedUser else Icons.Default.Lock,
                                contentDescription = "Age Verification Gate",
                                tint = if (isAdultAgeVerified) CyberEmerald else SolarAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "18+ Age Verification Gate (${selectedPersona.roleLabel} & Adult Social Sections)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = birthYearInput,
                                onValueChange = { birthYearInput = it.filter { ch -> ch.isDigit() }.take(4) },
                                label = { Text("Birth Year (YYYY)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("persona_birth_year_input")
                            )

                            Button(
                                onClick = {
                                    if (calculatedAgeFromYear >= 18L && adultAgeConfirmedCheckbox) {
                                        isAdultAgeVerified = true
                                        onApplyPersona(
                                            selectedPersona.roleLabel,
                                            calculatedAgeFromYear,
                                            selectedPersona.defaultThemeHex,
                                            selectedPersona.defaultVisualStyleId
                                        )
                                        verificationMessage = "✓ 18+ Age Verified (Age $calculatedAgeFromYear)! Unlocked ${selectedPersona.roleLabel} sections & ${selectedPersona.defaultThemeName} theme."
                                    } else {
                                        isAdultAgeVerified = false
                                        verificationMessage = "⚠️ Must be 18 or older (and check confirmation box) for Adult Social & Business sections. Switching to Youth-Safe mode if under 18."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("persona_verify_age_button")
                            ) {
                                Text(
                                    text = "Verify & Apply",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = adultAgeConfirmedCheckbox,
                                onCheckedChange = { adultAgeConfirmedCheckbox = it },
                                modifier = Modifier.testTag("persona_age_confirm_checkbox")
                            )
                            Text(
                                text = "I confirm I am at least 18 years old and eligible for age-restricted adult social & financial sections.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            verificationMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (msg.startsWith("⚠️")) RosePulse else CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Active Persona Unlocked App Sections & Tailored Feed Preview
            Text(
                text = "${selectedPersona.emoji} ${selectedPersona.roleLabel} • Unlocked App Sections (${selectedPersona.restrictedSectionsNote}):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedPersona.unlockedSections.forEach { (sectionTitle, targetTab) ->
                    val isLockedByAge = selectedPersona.requiresAdultAgeVerification && !isAdultAgeVerified
                    OutlinedButton(
                        onClick = {
                            if (isLockedByAge) {
                                verificationMessage = "⚠️ Complete the 18+ Age Verification check above to open '$sectionTitle'."
                            } else {
                                onNavigateToSection(targetTab)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isLockedByAge) SolarAmber else HoloCyan)
                    ) {
                        if (isLockedByAge) {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = SolarAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = sectionTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isLockedByAge) SolarAmber else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Tailored Feed Content for Selected Persona
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📰 Tailored Feed Content for ${selectedPersona.roleLabel}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Theme: ${selectedPersona.defaultThemeName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald
                        )
                    }

                    selectedPersona.tailoredFeedHighlights.forEach { highlight ->
                        Text(
                            text = "• $highlight",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = {
                            onApplyPersona(
                                selectedPersona.roleLabel,
                                if (selectedPersona.requiresAdultAgeVerification) calculatedAgeFromYear.coerceAtLeast(18L) else selectedPersona.defaultAge,
                                selectedPersona.defaultThemeHex,
                                selectedPersona.defaultVisualStyleId
                            )
                            onPublishPersonaPost(
                                "${selectedPersona.emoji} ${selectedPersona.roleLabel} Stage Update",
                                "Customized my Stages Of Life profile for the ${selectedPersona.roleLabel} persona with ${selectedPersona.defaultThemeName} theme! Highlight: ${selectedPersona.tailoredFeedHighlights.first()}"
                            )
                            verificationMessage = "✓ Applied ${selectedPersona.roleLabel} persona theme & posted tailored update to feed!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("persona_apply_and_post_button")
                    ) {
                        Text(
                            text = "Apply ${selectedPersona.roleLabel} Theme & Tailored Feed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
