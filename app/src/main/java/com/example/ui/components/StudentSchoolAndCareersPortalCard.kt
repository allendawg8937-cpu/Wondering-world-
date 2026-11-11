package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CareerMilestone
import com.example.data.model.buildDefaultStudentCareerMilestones
import com.example.ui.UiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

data class SchoolOrCareerPortalLink(
    val id: String,
    val title: String,
    val category: String,
    val badge: String,
    val subtitle: String,
    val url: String,
    val ctaLabel: String,
    val accent: Color
)

val SCHOOL_AND_CAREER_APPLICATION_LINKS = listOf(
    // 1. Apply for School, College & Financial Aid
    SchoolOrCareerPortalLink(
        id = "apply_common_app",
        title = "Common App — Apply to College & University",
        category = "Apply for School",
        badge = "🎓 APPLY FOR SCHOOL",
        subtitle = "Official portal to apply to over 1,000 colleges, universities, and scholarship programs.",
        url = "https://www.commonapp.org/",
        ctaLabel = "Apply for School",
        accent = HoloCyan
    ),
    SchoolOrCareerPortalLink(
        id = "apply_fafsa",
        title = "FAFSA — Federal Student Aid, Grants & Work-Study",
        category = "Apply for School",
        badge = "💰 SCHOOL AID & GRANTS",
        subtitle = "Apply for Pell Grants, federal student aid, work-study jobs, and college scholarships.",
        url = "https://studentaid.gov/",
        ctaLabel = "Apply for FAFSA Aid",
        accent = CyberEmerald
    ),
    SchoolOrCareerPortalLink(
        id = "apply_bigfuture",
        title = "College Board BigFuture — School & Scholarship Finder",
        category = "Apply for School",
        badge = "🏫 SCHOOL & MAJORS",
        subtitle = "Search trade schools, community colleges, 4-year universities, and monthly scholarships.",
        url = "https://bigfuture.collegeboard.org/",
        ctaLabel = "Explore Schools",
        accent = NeonPink
    ),

    // 2. All School Study & Research Resources
    SchoolOrCareerPortalLink(
        id = "school_khan_academy",
        title = "Khan Academy — Free Math, Science, SAT & Coding",
        category = "School Resources",
        badge = "📚 FREE SCHOOL COURSES",
        subtitle = "100% free K-12 and college courses in math, biology, chemistry, history, and SAT prep.",
        url = "https://www.khanacademy.org/",
        ctaLabel = "Open School Courses",
        accent = HoloCyan
    ),
    SchoolOrCareerPortalLink(
        id = "school_open_library",
        title = "OpenLibrary — Free Digital Textbooks & Study Books",
        category = "School Resources",
        badge = "📖 DIGITAL LIBRARY",
        subtitle = "Borrow millions of free school textbooks, literature classics, and STEM study guides.",
        url = "https://openlibrary.org/",
        ctaLabel = "Open Library Books",
        accent = CyberEmerald
    ),
    SchoolOrCareerPortalLink(
        id = "school_google_scholar",
        title = "Google Scholar — Academic Papers & Research",
        category = "School Resources",
        badge = "🔬 SCHOOL RESEARCH",
        subtitle = "Search peer-reviewed science articles, history sources, and university research papers.",
        url = "https://scholar.google.com/",
        ctaLabel = "Search Scholar",
        accent = SolarAmber
    ),

    // 3. Apply for Jobs, Student Internships & Careers
    SchoolOrCareerPortalLink(
        id = "career_careeronestop",
        title = "CareerOneStop — U.S. Dept. of Labor Job & Career Portal",
        category = "Apply for Jobs & Careers",
        badge = "💼 APPLY FOR CAREERS",
        subtitle = "Official career explorer, resume builder, local training finder, and live job board.",
        url = "https://www.careeronestop.org/",
        ctaLabel = "Apply for Careers",
        accent = SolarAmber
    ),
    SchoolOrCareerPortalLink(
        id = "career_usajobs_students",
        title = "USAJOBS Pathways — Paid Student Internships & Grad Jobs",
        category = "Apply for Jobs & Careers",
        badge = "🏛️ STUDENT & GRAD JOBS",
        subtitle = "Apply for official federal internships, high school/college student jobs, and entry careers.",
        url = "https://www.usajobs.gov/Help/working-in-government/unique-hiring-paths/students/",
        ctaLabel = "Apply on USAJOBS",
        accent = HoloCyan
    ),
    SchoolOrCareerPortalLink(
        id = "career_handshake",
        title = "Handshake — Student Part-Time Jobs & Internships",
        category = "Apply for Jobs & Careers",
        badge = "🎓 STUDENT JOB BOARD",
        subtitle = "Top career network for students to apply for internships, campus jobs, and first careers.",
        url = "https://joinhandshake.com/",
        ctaLabel = "Apply on Handshake",
        accent = NeonPink
    ),
    SchoolOrCareerPortalLink(
        id = "career_apprenticeship",
        title = "Apprenticeship.gov — Earn While You Learn Career Trades",
        category = "Apply for Jobs & Careers",
        badge = "🛠️ PAID APPRENTICESHIPS",
        subtitle = "Apply for paid tech, healthcare, engineering, and skilled-trade apprenticeships nationwide.",
        url = "https://www.apprenticeship.gov/",
        ctaLabel = "Apply for Apprenticeship",
        accent = CyberEmerald
    ),
    SchoolOrCareerPortalLink(
        id = "career_indeed",
        title = "Indeed — Apply for Local Part-Time, Full-Time & Career Jobs",
        category = "Apply for Jobs & Careers",
        badge = "🚀 DIRECT JOB APPLY",
        subtitle = "Search and apply directly to local student jobs, remote roles, and career openings.",
        url = "https://www.indeed.com/",
        ctaLabel = "Search & Apply Jobs",
        accent = SolarAmber
    ),
    SchoolOrCareerPortalLink(
        id = "career_linkedin",
        title = "LinkedIn Jobs — Entry-Level & Professional Career Network",
        category = "Apply for Jobs & Careers",
        badge = "🌐 CAREER NETWORK",
        subtitle = "Build your student/professional profile and apply with 1 click to entry-level careers.",
        url = "https://www.linkedin.com/jobs/",
        ctaLabel = "Open LinkedIn Jobs",
        accent = HoloCyan
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentSchoolAndCareersPortalCard(
    currentRole: String,
    currentAge: Long,
    onSelectStudentAccount: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onOpenAiStudyTutor: () -> Unit = {},
    onShareApplicationGoalToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    careerMilestonesState: UiState<List<CareerMilestone>> = UiState.Success(buildDefaultStudentCareerMilestones()),
    onAddCareerMilestone: (
        title: String,
        stage: String,
        description: String,
        targetTerm: String,
        xpReward: Long,
        portalUrl: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateCareerMilestoneStatus: (milestone: CareerMilestone, newStatus: String) -> Unit = { _, _ -> },
    onDeleteCareerMilestone: (milestoneId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var goalTargetTitle by remember { mutableStateOf("") }
    var goalTargetType by remember { mutableStateOf("School / College Application") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val savedApplications = remember {
        mutableStateListOf(
            "🎓 FAFSA & State University STEM Application — In Progress",
            "💼 Summer Tech & Community Internship Application — Ready to Submit"
        )
    }

    val isStudentAccountActive = currentRole.equals("creator", ignoreCase = true) || currentRole.equals("student", ignoreCase = true)

    val filteredLinks = remember(selectedCategory, searchQuery) {
        val q = searchQuery.trim().lowercase(java.util.Locale.ROOT)
        SCHOOL_AND_CAREER_APPLICATION_LINKS.filter { item ->
            val matchesCategory = (selectedCategory == "All") || (item.category == selectedCategory)
            val matchesSearch = q.isEmpty() ||
                item.title.lowercase(java.util.Locale.ROOT).contains(q) ||
                item.subtitle.lowercase(java.util.Locale.ROOT).contains(q) ||
                item.category.lowercase(java.util.Locale.ROOT).contains(q) ||
                item.badge.lowercase(java.util.Locale.ROOT).contains(q) ||
                item.url.lowercase(java.util.Locale.ROOT).contains(q)
            matchesCategory && matchesSearch
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink))
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.15f)
            .testTag("student_school_and_careers_portal_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header + 1-Tap Switch to Student Account Badge
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(HoloCyan, CyberEmerald, SolarAmber))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Student School & Career Portal",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Student Account • School, Jobs & Careers Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "All school resources + direct links to apply for school, college, internships & careers",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Student Account Activation Banner
            Surface(
                color = if (isStudentAccountActive) {
                    CyberEmerald.copy(alpha = 0.16f)
                } else {
                    HoloCyan.copy(alpha = 0.14f)
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    if (isStudentAccountActive) CyberEmerald else HoloCyan
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isStudentAccountActive) {
                                "🎓 Student Account Mode Active (Age $currentAge)"
                            } else {
                                "🎓 Want Full Student & School Resources?"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isStudentAccountActive) CyberEmerald else HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Unlocks FAFSA aid, Common App school links, Khan Academy, OpenLibrary & student job portals.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSelectStudentAccount()
                            statusMessage = "✓ Activated 🎓 Student Account Mode with all school & career resources!"
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStudentAccountActive) CyberEmerald else HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("activate_student_account_button")
                    ) {
                        Icon(
                            imageVector = if (isStudentAccountActive) Icons.Default.CheckCircle else Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isStudentAccountActive) "Student Active" else "Select Student Account",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // 3. Direct Quick-Apply Action Row (1-Tap Apply for School, Apply for Jobs, Open AI Study Tutor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenExternalUrl("https://www.commonapp.org/") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("quick_link_apply_school")
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("🎓 Apply for School (Common App)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { onOpenExternalUrl("https://studentaid.gov/") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("quick_link_apply_fafsa")
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("💰 Apply for FAFSA School Aid", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { onOpenExternalUrl("https://www.careeronestop.org/") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("quick_link_apply_jobs_careers")
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("💼 Apply for Jobs & Careers", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }

                OutlinedButton(
                    onClick = onOpenAiStudyTutor,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, NeonPink),
                    modifier = Modifier.testTag("quick_link_ai_study_tutor")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("🤖 Open AI Study Tutor", style = MaterialTheme.typography.labelSmall, color = NeonPink, fontWeight = FontWeight.Bold)
                }
            }

            // 3B. Firestore-Backed Student Career & Academic Milestone Vertical Timeline
            StudentCareerVerticalTimelineSection(
                careerMilestonesState = careerMilestonesState,
                onAddCareerMilestone = onAddCareerMilestone,
                onUpdateCareerMilestoneStatus = onUpdateCareerMilestoneStatus,
                onDeleteCareerMilestone = onDeleteCareerMilestone,
                onOpenExternalUrl = onOpenExternalUrl,
                onShareMilestoneToWall = onShareApplicationGoalToWall
            )

            // 4. Search Bar for School & Career Portals
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Career & School Portals (e.g. FAFSA, Common App, Internships)...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Portals",
                        tint = HoloCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_search_bar_input")
            )

            // 5. Filter Category Tabs: All | Apply for School | School Resources | Apply for Jobs & Careers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All" to "🌐 All School & Career Links (${SCHOOL_AND_CAREER_APPLICATION_LINKS.size})",
                    "Apply for School" to "🎓 Apply for School & Aid",
                    "School Resources" to "📚 School Study Resources",
                    "Apply for Jobs & Careers" to "💼 Apply for Jobs & Careers"
                ).forEach { (catKey, catLabel) ->
                    FilterChip(
                        selected = selectedCategory == catKey,
                        onClick = { selectedCategory = catKey },
                        label = {
                            Text(
                                text = catLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedCategory == catKey) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("student_portal_filter_${catKey.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // 5. Cards Grid of Official School, Study & Job/Career Application Portals
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredLinks.forEach { item ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, item.accent.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_portal_link_${item.id}")
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
                                Surface(
                                    color = item.accent.copy(alpha = 0.20f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = item.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = item.accent,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.url,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = item.accent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        onOpenExternalUrl(item.url)
                                        statusMessage = "Opened ${item.title} (${item.url})"
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = item.accent,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("open_portal_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item.ctaLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Interactive School, Job & Career Application Goal Tracker
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BusinessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Track Your School, Job & Career Applications",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "School / College Application",
                            "FAFSA / Scholarship Grant",
                            "Student Internship / Part-Time Job",
                            "Full-Time Career Application"
                        ).forEach { typeLabel ->
                            FilterChip(
                                selected = goalTargetType == typeLabel,
                                onClick = { goalTargetType = typeLabel },
                                label = { Text(typeLabel, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = goalTargetTitle,
                        onValueChange = { goalTargetTitle = it },
                        label = { Text("School Name, Major, or Job / Career Title") },
                        placeholder = { Text("e.g., State University Computer Science or Jr. Web Developer") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_application_goal_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clean = goalTargetTitle.trim().ifBlank { "Community College & Career Pathway" }
                                val entry = "🎯 [$goalTargetType] $clean — Submitted / Active"
                                savedApplications.add(0, entry)
                                goalTargetTitle = ""
                                statusMessage = "Saved '$clean' to your Student & Career Application Tracker!"
                                onShareApplicationGoalToWall(
                                    "Applied for $goalTargetType: $clean",
                                    "Tracking my school and career milestone in the Wondering World Student & Career Portal!"
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_school_career_application_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save & Share Application Goal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    savedApplications.take(4).forEach { appItem ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmerald)
                            )
                            Text(
                                text = appItem,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (!statusMessage.isNullOrBlank()) {
                        Text(
                            text = statusMessage!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudentCareerVerticalTimelineSection(
    careerMilestonesState: UiState<List<CareerMilestone>>,
    onAddCareerMilestone: (
        title: String,
        stage: String,
        description: String,
        targetTerm: String,
        xpReward: Long,
        portalUrl: String
    ) -> Unit,
    onUpdateCareerMilestoneStatus: (milestone: CareerMilestone, newStatus: String) -> Unit,
    onDeleteCareerMilestone: (milestoneId: String) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onShareMilestoneToWall: (headline: String, body: String) -> Unit
) {
    var selectedStageFilter by remember { mutableStateOf("All") }
    var showAddForm by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newStage by remember { mutableStateOf("Academic") }
    var newTerm by remember { mutableStateOf("Spring Semester") }
    var newDescription by remember { mutableStateOf("") }
    var newPortalUrl by remember { mutableStateOf("https://www.careeronestop.org/") }
    var timelineFeedback by remember { mutableStateOf<String?>(null) }

    val localFallbackMilestones = remember {
        mutableStateListOf<CareerMilestone>().apply {
            addAll(buildDefaultStudentCareerMilestones())
        }
    }

    val rawMilestones = when (careerMilestonesState) {
        is UiState.Success -> if (careerMilestonesState.data.isNotEmpty()) {
            careerMilestonesState.data
        } else {
            localFallbackMilestones
        }
        else -> localFallbackMilestones
    }.sortedBy { it.stepOrder }

    val filteredMilestones = remember(rawMilestones, selectedStageFilter) {
        if (selectedStageFilter == "All") {
            rawMilestones
        } else {
            rawMilestones.filter { it.stage.equals(selectedStageFilter, ignoreCase = true) }
        }
    }

    val completedCount = rawMilestones.count { it.status.equals("Completed", ignoreCase = true) }
    val inProgressCount = rawMilestones.count { it.status.equals("In Progress", ignoreCase = true) }
    val totalCount = rawMilestones.size.coerceAtLeast(1)
    val earnedXp = rawMilestones
        .filter { it.status.equals("Completed", ignoreCase = true) }
        .sumOf { it.xpReward }
    val totalPossibleXp = rawMilestones.sumOf { it.xpReward }
    val completionFraction = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, SolarAmber, NeonPink))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("career_portal_vertical_timeline")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Timeline Header + Add Objective Toggle
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(CyberEmerald, HoloCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Student Career Milestone Timeline",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Student Career & Academic Milestone Timeline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Synced with Cloud Firestore • Track step-by-step student career objectives",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showAddForm = !showAddForm },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier.testTag("toggle_add_career_milestone_button")
                ) {
                    Icon(
                        imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        tint = HoloCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showAddForm) "Close" else "Add Objective",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Progress Bar & XP Summary Card
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
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
                            text = "Milestone Completion: $completedCount / ${rawMilestones.size} Completed ($inProgressCount In Progress)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = SolarAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "⚡ $earnedXp / $totalPossibleXp XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { completionFraction },
                        color = CyberEmerald,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .testTag("career_timeline_progress_bar")
                    )
                }
            }

            // Stage Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "All" to "🗺️ All Stages (${rawMilestones.size})",
                    "Foundation" to "🧭 Foundation",
                    "Academic" to "📚 Academic",
                    "Application" to "🎓 Application",
                    "Internship" to "💼 Internship",
                    "Career Launch" to "🚀 Career Launch"
                ).forEach { (stageKey, label) ->
                    FilterChip(
                        selected = selectedStageFilter == stageKey,
                        onClick = { selectedStageFilter = stageKey },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedStageFilter == stageKey) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("timeline_stage_filter_${stageKey.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // Optional Add New Firestore Career Milestone Objective Form
            if (showAddForm) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_career_milestone_form")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Add New Student Milestone Objective to Firestore",
                            style = MaterialTheme.typography.titleSmall,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Foundation", "Academic", "Application", "Internship", "Career Launch").forEach { st ->
                                FilterChip(
                                    selected = newStage == st,
                                    onClick = { newStage = st },
                                    label = { Text(st, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Milestone Objective Title") },
                            placeholder = { Text("e.g., Complete Python Data Science Certificate") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_career_milestone_title_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newTerm,
                                onValueChange = { newTerm = it },
                                label = { Text("Target Term / Grade") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_career_milestone_term_input")
                            )
                            OutlinedTextField(
                                value = newPortalUrl,
                                onValueChange = { newPortalUrl = it },
                                label = { Text("Official Portal Link") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("Objective Action Steps & Description") },
                            placeholder = { Text("Describe what you will complete to achieve this career objective...") },
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_career_milestone_desc_input")
                        )

                        Button(
                            onClick = {
                                val cleanTitle = newTitle.trim().ifBlank { "Complete $newStage Portfolio Objective" }
                                val cleanDesc = newDescription.trim().ifBlank {
                                    "Actionable student career milestone tracked in the WereEqual Career Portal."
                                }
                                onAddCareerMilestone(
                                    cleanTitle,
                                    newStage,
                                    cleanDesc,
                                    newTerm.ifBlank { "This Semester" },
                                    250L,
                                    newPortalUrl.ifBlank { "https://www.careeronestop.org/" }
                                )
                                localFallbackMilestones.add(
                                    CareerMilestone(
                                        id = "local_ms_${System.currentTimeMillis()}",
                                        userId = "local_user",
                                        title = cleanTitle,
                                        stage = newStage,
                                        description = cleanDesc,
                                        targetTerm = newTerm.ifBlank { "This Semester" },
                                        status = "In Progress",
                                        xpReward = 250L,
                                        stepOrder = (localFallbackMilestones.size + 1).toLong(),
                                        portalUrl = newPortalUrl.ifBlank { "https://www.careeronestop.org/" }
                                    )
                                )
                                newTitle = ""
                                newDescription = ""
                                showAddForm = false
                                timelineFeedback = "✓ Added '$cleanTitle' to your Firestore Career Timeline!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_career_milestone_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save Milestone Objective to Firestore (+250 XP)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            if (careerMilestonesState is UiState.Loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = HoloCyan)
                    Text(
                        text = "Fetching student milestone objectives from Cloud Firestore...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Vertical Timeline Nodes & Connector Rail
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                filteredMilestones.forEachIndexed { index, milestone ->
                    val isLast = index == filteredMilestones.lastIndex
                    val isCompleted = milestone.status.equals("Completed", ignoreCase = true)
                    val isInProgress = milestone.status.equals("In Progress", ignoreCase = true)

                    val nodeColor = when {
                        isCompleted -> CyberEmerald
                        isInProgress -> HoloCyan
                        else -> SolarAmber
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .testTag("career_timeline_node_${milestone.id}"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left Vertical Timeline Rail & Node Indicator
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                            ) {
                                val centerX = size.width / 2f
                                val startY = if (index == 0) 22.dp.toPx() else 0f
                                val endY = if (isLast) 22.dp.toPx() else size.height
                                if (endY > startY) {
                                    drawLine(
                                        brush = Brush.verticalGradient(
                                            colors = if (isCompleted) {
                                                listOf(CyberEmerald, HoloCyan)
                                            } else {
                                                listOf(nodeColor.copy(alpha = 0.75f), nodeColor.copy(alpha = 0.28f))
                                            }
                                        ),
                                        start = Offset(centerX, startY),
                                        end = Offset(centerX, endY),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        pathEffect = if (isCompleted || isInProgress) {
                                            null
                                        } else {
                                            PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                        }
                                    )
                                }
                            }

                            // Circular Timeline Step Node
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(nodeColor.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(nodeColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isCompleted -> Icons.Default.CheckCircle
                                            isInProgress -> Icons.Default.Schedule
                                            else -> Icons.Default.RadioButtonUnchecked
                                        },
                                        contentDescription = milestone.status,
                                        tint = Color.Black,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        // Right Milestone Objective Card
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isInProgress) 1.5.dp else 1.dp,
                                color = nodeColor.copy(alpha = if (isInProgress) 0.85f else 0.5f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = if (isLast) 4.dp else 12.dp)
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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = nodeColor.copy(alpha = 0.18f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "STEP ${milestone.stepOrder} • ${milestone.stage.uppercase()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = nodeColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Surface(
                                            color = SolarAmber.copy(alpha = 0.18f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "+${milestone.xpReward} XP",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SolarAmber,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = milestone.targetTerm,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (milestone.id.startsWith("local_ms_") || rawMilestones.size > 3) {
                                            IconButton(
                                                onClick = {
                                                    onDeleteCareerMilestone(milestone.id)
                                                    localFallbackMilestones.removeAll { it.id == milestone.id }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete Milestone",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = milestone.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = milestone.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Interactive Status Progression & Portal Launch Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf("Upcoming", "In Progress", "Completed").forEach { statusOption ->
                                        val isCurrentStatus = milestone.status.equals(statusOption, ignoreCase = true)
                                        FilterChip(
                                            selected = isCurrentStatus,
                                            onClick = {
                                                onUpdateCareerMilestoneStatus(milestone, statusOption)
                                                val idx = localFallbackMilestones.indexOfFirst { it.id == milestone.id }
                                                if (idx >= 0) {
                                                    localFallbackMilestones[idx] = localFallbackMilestones[idx].copy(status = statusOption)
                                                }
                                                timelineFeedback = "✓ Updated '${milestone.title}' to $statusOption in Firestore!"
                                                if (statusOption == "Completed") {
                                                    onShareMilestoneToWall(
                                                        "Completed Career Milestone: ${milestone.title} (+${milestone.xpReward} XP)",
                                                        "Achieved my ${milestone.stage} objective (${milestone.targetTerm}) on the WereEqual Student Career Timeline!"
                                                    )
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = when (statusOption) {
                                                        "Completed" -> "✅ Completed"
                                                        "In Progress" -> "⏳ In Progress"
                                                        else -> "📌 Upcoming"
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isCurrentStatus) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            modifier = Modifier.testTag(
                                                "milestone_status_${milestone.id}_${statusOption.lowercase().replace(" ", "_")}"
                                            )
                                        )
                                    }

                                    if (milestone.portalUrl.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = { onOpenExternalUrl(milestone.portalUrl) },
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, nodeColor),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                contentDescription = null,
                                                tint = nodeColor,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Open Portal",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = nodeColor,
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

            if (!timelineFeedback.isNullOrBlank()) {
                Text(
                    text = timelineFeedback!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
