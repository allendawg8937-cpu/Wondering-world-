package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.sp
import com.example.ui.theme.parseThemeHex

data class PersonaThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val visualStyleId: String,
    val colorHex: String,
    val badgeEmoji: String
)

data class PersonaResourceCategory(
    val title: String,
    val description: String,
    val tagInterest: String,
    val url: String,
    val badgeLabel: String
)

data class PersonaSpecializedFeature(
    val id: String,
    val title: String,
    val subtitle: String,
    val actionButtonLabel: String,
    val interactiveResultText: String,
    val wallShareTitle: String,
    val wallShareContent: String
)

data class PersonaBlueprint(
    val roleKey: String,
    val displayTitle: String,
    val shortLabel: String,
    val emoji: String,
    val defaultAge: String,
    val tagline: String,
    val primaryColorHex: String,
    val defaultVisualStyleId: String,
    val unlockedThemes: List<PersonaThemePreset>,
    val resourceCategories: List<PersonaResourceCategory>,
    val specializedFeatures: List<PersonaSpecializedFeature>
)

val PERSONA_BLUEPRINTS: List<PersonaBlueprint> = listOf(
    PersonaBlueprint(
        roleKey = "veteran",
        displayTitle = "Veteran Persona",
        shortLabel = "Veteran",
        emoji = "🛡️",
        defaultAge = "34",
        tagline = "Unlocks Honor Shield 3D Themes, VA & GI Bill Resource Categories, and Service-to-Civilian Mission Tools.",
        primaryColorHex = "#10B981",
        defaultVisualStyleId = "SOLAR_FLARE",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "vet_honor_shield",
                title = "Valor & Emerald 3D Shield",
                subtitle = "Solar Flare 3D rings with tactical emerald & gold accents",
                visualStyleId = "SOLAR_FLARE",
                colorHex = "#10B981",
                badgeEmoji = "🛡️"
            ),
            PersonaThemePreset(
                id = "vet_starlight_command",
                title = "Command Hypercube 6D",
                subtitle = "Deep navy & amber gold 6D Hypercube command center",
                visualStyleId = "HYPERCUBE_6D",
                colorHex = "#F59E0B",
                badgeEmoji = "🎖️"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "VA Healthcare & Mental Wellness",
                description = "24/7 VA medical center locator, telehealth & veteran crisis support",
                tagInterest = "VA & Veteran Support",
                url = "https://www.va.gov/health-care/",
                badgeLabel = "VA Verified"
            ),
            PersonaResourceCategory(
                title = "GI Bill, Tuition & SkillBridge",
                description = "Post-9/11 GI Bill housing calculator & DoD SkillBridge fellowships",
                tagInterest = "Business & Career Grants",
                url = "https://www.va.gov/education/about-gi-bill-benefits/",
                badgeLabel = "Education"
            ),
            PersonaResourceCategory(
                title = "Veteran-Owned Small Business Grants",
                description = "SBA VetCert, Boots to Business & zero-fee veteran capital programs",
                tagInterest = "Financial Stability",
                url = "https://www.sba.gov/business-guide/grow-your-business/veteran-owned-businesses",
                badgeLabel = "SBA VetCert"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "vet_mos_translator",
                title = "Military-to-Civilian Career Translator",
                subtitle = "Maps your military service leadership & technical skills to high-growth civilian roles",
                actionButtonLabel = "Run Career Bridge Check",
                interactiveResultText = "✅ Veteran Career Bridge Active: Matched Leadership, Operations & Technical Systems to Project Management, Cybersecurity & Federal Pathways (+25 EQC Bonus Ready).",
                wallShareTitle = "🛡️ Activated My Veteran Career & GI Bill Blueprint",
                wallShareContent = "Just unlocked the Veteran Honor Shield 3D theme and mapped my service skills to new civilian career & GI Bill milestones! We are all equal."
            ),
            PersonaSpecializedFeature(
                id = "vet_peer_beacon",
                title = "Veteran Comradeship Check-In Beacon",
                subtitle = "Broadcasts a supportive check-in signal to fellow veterans in your region",
                actionButtonLabel = "Ping Comradeship Beacon",
                interactiveResultText = "🟢 Comradeship Beacon Live: Connected with 14 Veterans in the Peer Lounge for coffee chats, VA claim tips, and mutual support.",
                wallShareTitle = "🎖️ Veteran Peer Check-In Beacon Active",
                wallShareContent = "Checking in with all fellow veterans on Wondering World! Drop a reply if you want to connect on VA resources, career transitions, or camaraderie."
            )
        )
    ),
    PersonaBlueprint(
        roleKey = "business",
        displayTitle = "Business Persona",
        shortLabel = "Business",
        emoji = "💼",
        defaultAge = "31",
        tagline = "Unlocks Executive Prism 3D Themes, SBA & Capital Grant Categories, and Founder Pitch & Marketplace Tools.",
        primaryColorHex = "#F59E0B",
        defaultVisualStyleId = "CRYSTAL_PRISM",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "biz_executive_prism",
                title = "Executive Gold Crystal Prism",
                subtitle = "Refractive 6D Crystal Prism with Solar Gold & Onyx finish",
                visualStyleId = "CRYSTAL_PRISM",
                colorHex = "#F59E0B",
                badgeEmoji = "💼"
            ),
            PersonaThemePreset(
                id = "biz_fintech_grid",
                title = "FinTech Cyber Grid 3D",
                subtitle = "High-contrast Holo Cyan & Emerald venture studio grid",
                visualStyleId = "CYBER_GRID",
                colorHex = "#06B6D4",
                badgeEmoji = "📈"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "SBA Small Business Grants & Loans",
                description = "Federal & state small business funding, micro-grants & 7(a) capital",
                tagInterest = "Business & Career Grants",
                url = "https://www.sba.gov/funding-programs",
                badgeLabel = "Capital"
            ),
            PersonaResourceCategory(
                title = "SCORE Free Executive Mentorship",
                description = "1-on-1 startup mentoring, business plan templates & financial modeling",
                tagInterest = "Financial Stability",
                url = "https://www.score.org/",
                badgeLabel = "Mentorship"
            ),
            PersonaResourceCategory(
                title = "Minority & Community Enterprise Hub",
                description = "MBDA grants, local chamber networking & ethical marketplace certification",
                tagInterest = "STEM & Coding",
                url = "https://www.mbda.gov/",
                badgeLabel = "Growth"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "biz_pitch_builder",
                title = "60-Second Founder Pitch & Grant Matcher",
                subtitle = "Structures your value proposition and matches you with community grants",
                actionButtonLabel = "Generate Founder Pitch",
                interactiveResultText = "🚀 Pitch Blueprint Ready: Highlighted Community Impact, Revenue Sustainability & 3 Matching SBA/Local Micro-Grant Programs.",
                wallShareTitle = "💼 Shared Our Community Business & Creator Spotlight",
                wallShareContent = "Excited to connect with fellow founders, creators, and partners on Wondering World! Let's collaborate and build sustainable community ventures."
            ),
            PersonaSpecializedFeature(
                id = "biz_partner_beacon",
                title = "B2B & Creator Collaboration Spotlight",
                subtitle = "Opens your profile to partnerships, local sponsorships, and skill-swaps",
                actionButtonLabel = "Activate Partner Badge",
                interactiveResultText = "🤝 B2B Collaboration Badge Active: Your profile is now highlighted in the Business & Creator Directory for ethical partnerships.",
                wallShareTitle = "📈 Open for Community Business & Creator Collaborations",
                wallShareContent = "Activated my Business Persona & Executive Gold 3D Theme! Reach out if you're looking for mentorship, partnerships, or project collaborations."
            )
        )
    ),
    PersonaBlueprint(
        roleKey = "kid",
        displayTitle = "Kid Explorer Persona",
        shortLabel = "Kid",
        emoji = "🧒",
        defaultAge = "13",
        tagline = "Unlocks Starlight Wonder 3D Themes, Kid-Safe STEM & Art Categories, and Curiosity Quest Interactions.",
        primaryColorHex = "#06B6D4",
        defaultVisualStyleId = "AURORA",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "kid_starlight_aurora",
                title = "Starlight Wonder Aurora 3D",
                subtitle = "Playful sky-cyan & rainbow aurora waves for young explorers",
                visualStyleId = "AURORA",
                colorHex = "#06B6D4",
                badgeEmoji = "🚀"
            ),
            PersonaThemePreset(
                id = "kid_cosmic_playground",
                title = "Cosmic Discovery Orbit 3D",
                subtitle = "Bright solar rings and friendly space exploration theme",
                visualStyleId = "SOLAR_FLARE",
                colorHex = "#EC4899",
                badgeEmoji = "🌟"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "Khan Academy Kids & Youth STEM",
                description = "Interactive math, science, space exploration & reading adventures",
                tagInterest = "STEM & Coding",
                url = "https://www.khanacademy.org/",
                badgeLabel = "Kid-Safe STEM"
            ),
            PersonaResourceCategory(
                title = "Scratch Creative Coding & Animation",
                description = "Build your own kid-friendly games, animations & interactive stories",
                tagInterest = "Music & 3D Art",
                url = "https://scratch.mit.edu/",
                badgeLabel = "Creative Coding"
            ),
            PersonaResourceCategory(
                title = "NASA Space Place for Young Explorers",
                description = "Explore planets, rockets, telescopes & Earth science games",
                tagInterest = "STEM & Coding",
                url = "https://spaceplace.nasa.gov/",
                badgeLabel = "NASA Kids"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "kid_wonder_quest",
                title = "Daily STEM & Space Wonder Quest",
                subtitle = "Fun interactive science & kindness challenge designed for young minds",
                actionButtonLabel = "Launch Wonder Quest",
                interactiveResultText = "🌟 Wonder Quest Complete! Today's Discovery: Light from the Sun takes 8 minutes and 20 seconds to reach Earth! Earned Explorer Star Badge.",
                wallShareTitle = "🚀 Completed Today's STEM Space Wonder Quest!",
                wallShareContent = "Just unlocked the Starlight Wonder 3D theme and completed my Daily STEM & Space Wonder Quest on Wondering World!"
            ),
            PersonaSpecializedFeature(
                id = "kid_story_spark",
                title = "Super-Creator Story & Drawing Spark",
                subtitle = "Generates a fun, family-friendly creative art & story prompt",
                actionButtonLabel = "Spark Creative Idea",
                interactiveResultText = "🎨 Creative Prompt: Design a friendly 3D robot helper that plants trees on Mars and plays your favorite song!",
                wallShareTitle = "🎨 Working on a New 3D Robot Helper Art Project!",
                wallShareContent = "Using the Kid Explorer Persona studio to design a friendly tree-planting Mars robot!"
            )
        )
    ),
    PersonaBlueprint(
        roleKey = "teen",
        displayTitle = "Teen Innovator Persona",
        shortLabel = "Teen",
        emoji = "🎒",
        defaultAge = "16",
        tagline = "Unlocks Neon Cyber Pulse 3D Themes, Scholarships & First-Job Categories, and Teen Study & Music Jam Features.",
        primaryColorHex = "#EC4899",
        defaultVisualStyleId = "CYBER_GRID",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "teen_neon_cyber",
                title = "Neon Cyber Pulse 3D Grid",
                subtitle = "Electric magenta & cyan 3D synthwave grid for teen creators",
                visualStyleId = "CYBER_GRID",
                colorHex = "#EC4899",
                badgeEmoji = "⚡"
            ),
            PersonaThemePreset(
                id = "teen_hologram_studio",
                title = "Hologram Beats 6D Cube",
                subtitle = "Vibrant violet & neon indigo 6D Hypercube for music & gaming",
                visualStyleId = "HYPERCUBE_6D",
                colorHex = "#8B5CF6",
                badgeEmoji = "🎧"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "Teen Scholarships, SAT/ACT & College Prep",
                description = "Free SAT/AP prep, college admissions guides & youth scholarships",
                tagInterest = "FAFSA & College Aid",
                url = "https://www.khanacademy.org/test-prep/digital-sat",
                badgeLabel = "College Prep"
            ),
            PersonaResourceCategory(
                title = "Youth Summer Jobs & Paid Internships",
                description = "First-job resume templates, youth apprenticeships & coding camps",
                tagInterest = "STEM & Coding",
                url = "https://www.careeronestop.org/GetMyFuture/index.aspx",
                badgeLabel = "First Job"
            ),
            PersonaResourceCategory(
                title = "Teen Digital Art, Music & Game Design",
                description = "3D modeling, beat-making, and open-source coding clubs",
                tagInterest = "Music & 3D Art",
                url = "https://openlibrary.org/",
                badgeLabel = "Creator Lab"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "teen_scholarship_radar",
                title = "Teen Scholarship & First-Job Radar",
                subtitle = "Curates actionable high school scholarships, summer internships & coding grants",
                actionButtonLabel = "Scan Teen Opportunities",
                interactiveResultText = "🎓 Teen Opportunity Radar: Found 4 High School STEM & Creative Arts Scholarships + Youth Summer Apprenticeship Tracks!",
                wallShareTitle = "⚡ Exploring Teen STEM & Creative Scholarships!",
                wallShareContent = "Switched to the Teen Innovator Persona with the Neon Cyber Pulse 3D theme! Checking out summer coding & scholarship tracks."
            ),
            PersonaSpecializedFeature(
                id = "teen_music_code_jam",
                title = "Teen 3D Music & Coding Collab Jam",
                subtitle = "Pairs your background soundtrack with a 25-minute Pomodoro study/coding sprint",
                actionButtonLabel = "Start 25-Min Jam Sprint",
                interactiveResultText = "🎧 25-Minute Music & Coding Jam Active: Synced with your 6D Background Jukebox & AI Flashcard Tutor!",
                wallShareTitle = "🎧 Kicking Off a 25-Min Teen Coding & Music Study Sprint!",
                wallShareContent = "Queueing up my 6D profile soundtrack and running a 25-minute coding & study sprint. Join in!"
            )
        )
    ),
    PersonaBlueprint(
        roleKey = "adult",
        displayTitle = "Adult Community Persona",
        shortLabel = "Adult",
        emoji = "🌟",
        defaultAge = "28",
        tagline = "Unlocks Harmony Aurora 3D Themes, Career & Family Finance Categories, and Stability & Civic Networking Tools.",
        primaryColorHex = "#6366F1",
        defaultVisualStyleId = "HYPERCUBE_6D",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "adult_indigo_hypercube",
                title = "Indigo Harmony 6D Hypercube",
                subtitle = "Balanced royal indigo & teal 6D Hypercube workspace",
                visualStyleId = "HYPERCUBE_6D",
                colorHex = "#6366F1",
                badgeEmoji = "🌟"
            ),
            PersonaThemePreset(
                id = "adult_aurora_sanctuary",
                title = "Aurora Wellness Sanctuary",
                subtitle = "Calming northern lights aurora for work-life balance & focus",
                visualStyleId = "AURORA",
                colorHex = "#14B8A6",
                badgeEmoji = "🌿"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "Career Advancement & Remote Job Hub",
                description = "Professional certifications, salary benchmarks & career pivot guides",
                tagInterest = "Business & Career Grants",
                url = "https://www.careeronestop.org/",
                badgeLabel = "Career Growth"
            ),
            PersonaResourceCategory(
                title = "Personal Finance, Housing & 211 Support",
                description = "Emergency savings blueprints, first-time homebuyer grants & 211 directory",
                tagInterest = "Financial Stability",
                url = "https://www.211.org/",
                badgeLabel = "Stability"
            ),
            PersonaResourceCategory(
                title = "Holistic Healthcare & Community Wellness",
                description = "Preventive health, mindfulness & local neighborhood mutual aid",
                tagInterest = "Healthcare & Wellness",
                url = "https://findahealthcenter.hrsa.gov/",
                badgeLabel = "Wellness"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "adult_stability_planner",
                title = "50/30/20 Financial & Career Stability Sync",
                subtitle = "Aligns your monthly savings goal with career certification milestones",
                actionButtonLabel = "Run Stability Sync",
                interactiveResultText = "💎 Stability Sync Complete: Optimized your Emergency Savings Vault target and pinned 3 high-ROI career certifications.",
                wallShareTitle = "🌟 Updated My Personal Finance & Career Growth Blueprint",
                wallShareContent = "Using the Adult Community Persona tools to sync my savings milestones and career growth goals! Remember: We are all equal."
            ),
            PersonaSpecializedFeature(
                id = "adult_civic_matcher",
                title = "Local Community & Mentor Circle Matcher",
                subtitle = "Connects you with peers sharing your career, wellness, and creative interests",
                actionButtonLabel = "Match With Peers",
                interactiveResultText = "🤝 Matched with 9 Community Peers in Healthcare, Financial Literacy, and 3D Creative Arts!",
                wallShareTitle = "🤝 Connecting With Peers in the Adult Community Circle",
                wallShareContent = "Always happy to share career tips, financial literacy resources, and creative projects with everyone in Wondering World!"
            )
        )
    ),
    PersonaBlueprint(
        roleKey = "creator",
        displayTitle = "Student & Creator Persona",
        shortLabel = "Student",
        emoji = "🎓",
        defaultAge = "20",
        tagline = "Unlocks Campus Crystal 3D Themes, FAFSA & University Resource Categories, and AI Study & Portfolio Tools.",
        primaryColorHex = "#8B5CF6",
        defaultVisualStyleId = "CRYSTAL_PRISM",
        unlockedThemes = listOf(
            PersonaThemePreset(
                id = "student_crystal_scholar",
                title = "Campus Crystal Prism 3D",
                subtitle = "Academic violet & cyan prism for students and digital creators",
                visualStyleId = "CRYSTAL_PRISM",
                colorHex = "#8B5CF6",
                badgeEmoji = "🎓"
            ),
            PersonaThemePreset(
                id = "student_studio_nebula",
                title = "Creator Studio Aurora",
                subtitle = "Vibrant magenta & indigo aurora for portfolios and music",
                visualStyleId = "AURORA",
                colorHex = "#EC4899",
                badgeEmoji = "🎨"
            )
        ),
        resourceCategories = listOf(
            PersonaResourceCategory(
                title = "FAFSA Federal Aid & Pell Grants",
                description = "Direct StudentAid.gov portal for grants, scholarships & work-study",
                tagInterest = "FAFSA & College Aid",
                url = "https://studentaid.gov/",
                badgeLabel = "FAFSA Aid"
            ),
            PersonaResourceCategory(
                title = "Common App & OpenLibrary Textbooks",
                description = "College applications and 100% free open-access academic textbooks",
                tagInterest = "STEM & Coding",
                url = "https://openlibrary.org/",
                badgeLabel = "Textbooks"
            ),
            PersonaResourceCategory(
                title = "Handshake Student Internships",
                description = "Paid college internships, research fellowships & entry-level careers",
                tagInterest = "Business & Career Grants",
                url = "https://joinhandshake.com/",
                badgeLabel = "Internships"
            )
        ),
        specializedFeatures = listOf(
            PersonaSpecializedFeature(
                id = "student_aid_tracker",
                title = "FAFSA, Scholarship & Internship Fast-Track",
                subtitle = "Organizes your semester grant deadlines and internship applications",
                actionButtonLabel = "Activate Semester Checklist",
                interactiveResultText = "📚 Semester Fast-Track Ready: FAFSA Pell Grant Check + 3 Paid STEM/Creator Internships pinned to your Career Roadmap!",
                wallShareTitle = "🎓 Activated My Student Aid & Internship Fast-Track!",
                wallShareContent = "Unlocked the Campus Crystal Prism 3D theme and organized my FAFSA & internship milestones for the semester!"
            ),
            PersonaSpecializedFeature(
                id = "student_portfolio_showcase",
                title = "3D Academic & Creator Portfolio Beacon",
                subtitle = "Highlights your research, art, and coding projects to mentors & recruiters",
                actionButtonLabel = "Highlight Portfolio",
                interactiveResultText = "✨ Portfolio Beacon Live: Your 3D theme, research interests, and creative projects are featured on your public card.",
                wallShareTitle = "🎨 Showcasing My Student & Creator Portfolio on Wondering World",
                wallShareContent = "Just updated my 3D Creator Portfolio! Open to study groups, research collaborations, and creative projects."
            )
        )
    )
)

fun findPersonaBlueprint(roleKey: String, ageText: String = "26"): PersonaBlueprint {
    val cleanRole = roleKey.lowercase().trim()
    val age = ageText.toIntOrNull() ?: 26
    return when {
        cleanRole == "kid" || (cleanRole == "adult" && age <= 15) ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "kid" }
        cleanRole == "teen" || (cleanRole == "adult" && age in 16..17) ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "teen" }
        cleanRole == "veteran" ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "veteran" }
        cleanRole == "business" ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "business" }
        cleanRole == "creator" || cleanRole == "student" ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "creator" }
        else ->
            PERSONA_BLUEPRINTS.first { it.roleKey == "adult" }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaProfileSystemCard(
    currentRole: String,
    currentAgeText: String,
    currentVisualStyleId: String,
    currentThemeHex: String,
    selectedInterests: Set<String>,
    onSelectPersona: (roleKey: String, defaultAge: String, defaultHex: String, defaultStyleId: String) -> Unit,
    onApplyPersonaTheme: (visualStyleId: String, colorHex: String, badgeEmoji: String) -> Unit,
    onPinPersonaInterest: (String) -> Unit,
    onSharePersonaActionToWall: (title: String, content: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeBlueprint = remember(currentRole, currentAgeText) {
        findPersonaBlueprint(currentRole, currentAgeText)
    }
    val personaAccent = parseThemeHex(activeBlueprint.primaryColorHex)
    var activeSubTab by remember { mutableIntStateOf(0) } // 0 = Unlocked Themes, 1 = Resource Categories, 2 = Specialized Features
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persona_profile_system_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.92f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(
                    personaAccent,
                    Color(0xFF38BDF8),
                    Color(0xFF10B981)
                )
            )
        )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(personaAccent.copy(alpha = 0.25f))
                            .border(1.5.dp, personaAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = activeBlueprint.emoji, fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = "PERSONA-BASED PROFILE SYSTEM",
                            style = MaterialTheme.typography.labelSmall,
                            color = personaAccent,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Active: ${activeBlueprint.emoji} ${activeBlueprint.displayTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = activeBlueprint.tagline,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // Primary Persona Selection Bar (Veteran, Business, Kid, Teen, Adult, Student)
            Text(
                text = "Select Primary Persona (Unlocks Exclusive Themes, Resources & Tools):",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PERSONA_BLUEPRINTS.forEach { blueprint ->
                    val isSelected = activeBlueprint.roleKey == blueprint.roleKey
                    val chipAccent = parseThemeHex(blueprint.primaryColorHex)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) chipAccent.copy(alpha = 0.28f) else Color(0xFF1E293B).copy(alpha = 0.75f),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) chipAccent else Color.White.copy(alpha = 0.18f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectPersona(
                                    blueprint.roleKey,
                                    blueprint.defaultAge,
                                    blueprint.primaryColorHex,
                                    blueprint.defaultVisualStyleId
                                )
                                statusFeedback = "${blueprint.emoji} Activated ${blueprint.displayTitle}! Unlocked ${blueprint.unlockedThemes.size} themes, ${blueprint.resourceCategories.size} resource tracks & specialized features."
                            }
                            .testTag("select_persona_${blueprint.roleKey}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = blueprint.emoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = blueprint.shortLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isSelected) "Active ✓" else "Age ${blueprint.defaultAge}+",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Status Feedback Banner
            AnimatedVisibility(visible = !statusFeedback.isNullOrBlank()) {
                if (!statusFeedback.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = personaAccent.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, personaAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("persona_system_feedback_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = statusFeedback.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3 Sub-Tabs for the Selected Persona:
            // 0 = Unlocked Persona Themes | 1 = Relevant Resource Categories | 2 = Specialized Persona Features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val subTabs = listOf(
                    Triple(0, "🎨 Persona Themes", "persona_tab_themes"),
                    Triple(1, "📚 Resource Tracks", "persona_tab_resources"),
                    Triple(2, "⚡ Special Tools", "persona_tab_features")
                )
                subTabs.forEach { (idx, label, tag) ->
                    val selected = activeSubTab == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) personaAccent.copy(alpha = 0.28f) else Color(0xFF1E293B).copy(alpha = 0.65f),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) personaAccent else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeSubTab = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            when (activeSubTab) {
                0 -> {
                    // UNLOCKED PERSONA THEMES
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎨 Exclusive 3D Profile Themes Unlocked for ${activeBlueprint.displayTitle}:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold
                        )
                        activeBlueprint.unlockedThemes.forEach { themePreset ->
                            val isEquipped = currentVisualStyleId == themePreset.visualStyleId &&
                                currentThemeHex.equals(themePreset.colorHex, ignoreCase = true)
                            val swatchColor = parseThemeHex(themePreset.colorHex)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isEquipped) swatchColor.copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isEquipped) swatchColor else Color.White.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(swatchColor)
                                                .border(1.5.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = themePreset.badgeEmoji, fontSize = 18.sp)
                                        }
                                        Column {
                                            Text(
                                                text = themePreset.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${themePreset.subtitle} (${themePreset.visualStyleId})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            onApplyPersonaTheme(
                                                themePreset.visualStyleId,
                                                themePreset.colorHex,
                                                themePreset.badgeEmoji
                                            )
                                            statusFeedback = "🎨 Applied ${themePreset.title} (${themePreset.visualStyleId}) to your profile!"
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEquipped) Color(0xFF059669) else swatchColor,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("apply_persona_theme_${themePreset.id}")
                                    ) {
                                        Text(
                                            text = if (isEquipped) "✓ Active" else "Apply Theme",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // RELEVANT RESOURCE CATEGORIES FOR THIS PERSONA
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📚 Curated Resource Categories Unlocked for ${activeBlueprint.displayTitle}:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.Bold
                        )
                        activeBlueprint.resourceCategories.forEachIndexed { index, cat ->
                            val isPinned = selectedInterests.contains(cat.tagInterest)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isPinned) Color(0xFF10B981) else personaAccent.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
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
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = cat.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = personaAccent.copy(alpha = 0.25f)
                                                ) {
                                                    Text(
                                                        text = cat.badgeLabel,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = cat.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                onPinPersonaInterest(cat.tagInterest)
                                                statusFeedback = "📌 Pinned '${cat.tagInterest}' to your ${activeBlueprint.shortLabel} Profile!"
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, if (isPinned) Color(0xFF10B981) else personaAccent),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("pin_persona_resource_$index")
                                        ) {
                                            Text(
                                                text = if (isPinned) "✓ Pinned: ${cat.tagInterest}" else "Pin Category",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isPinned) Color(0xFF6EE7B7) else Color.White,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cat.url))
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = personaAccent,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .height(38.dp)
                                                .testTag("open_persona_resource_$index")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                contentDescription = "Open Resource",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Open Portal",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // SPECIALIZED INTERACTION FEATURES FOR THIS PERSONA
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚡ Specialized Interaction Features for ${activeBlueprint.displayTitle}:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF93C5FD),
                            fontWeight = FontWeight.Bold
                        )
                        activeBlueprint.specializedFeatures.forEach { feature ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, personaAccent.copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = feature.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = feature.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                statusFeedback = feature.interactiveResultText
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = personaAccent,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("run_persona_feature_${feature.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stars,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = feature.actionButtonLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                onSharePersonaActionToWall(
                                                    feature.wallShareTitle,
                                                    feature.wallShareContent
                                                )
                                                statusFeedback = "📣 Shared '${feature.wallShareTitle}' to the Community Wall!"
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, Color(0xFF34D399)),
                                            modifier = Modifier
                                                .height(40.dp)
                                                .testTag("share_persona_feature_${feature.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share to Wall",
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Share",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF6EE7B7),
                                                fontWeight = FontWeight.Bold
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
    }
}
