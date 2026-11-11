package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

data class VerifiedResourceHubEntry(
    val id: String,
    val title: String,
    val category: String, // "Mental Health Hotlines", "Financial Aid (FAFSA & Loans)", "Job Listings", "Educational Content"
    val contactOrAccessInfo: String,
    val description: String,
    val submittedBy: String,
    val isVerified: Boolean,
    val safetyNote: String,
    val dialNumber: String? = null,
    val rating: Float = 4.8f,
    val ratingCount: Int = 12,
    val userRating: Int = 0
)

private val INITIAL_VERIFIED_RESOURCES = listOf(
    VerifiedResourceHubEntry(
        id = "mh_988",
        title = "988 Suicide & Crisis Lifeline (24/7 Free & Confidential)",
        category = "Mental Health Hotlines",
        contactOrAccessInfo = "Call or Text 988 (24/7 Nationwide)",
        description = "Immediate, free, confidential crisis counseling and emotional support 24 hours a day, 7 days a week.",
        submittedBy = "Stages Of Life Safety Team",
        isVerified = true,
        safetyNote = "Verified Official National Hotline • Safe for All Ages",
        dialNumber = "988"
    ),
    VerifiedResourceHubEntry(
        id = "mh_veterans",
        title = "Veterans Crisis Line & Military Peer Support",
        category = "Mental Health Hotlines",
        contactOrAccessInfo = "Dial 988 then Press 1 • Or Text 838255",
        description = "Dedicated 24/7 crisis responders and peer support specialists for Veterans, service members, and their families.",
        submittedBy = "Stages Of Life Veteran Council",
        isVerified = true,
        safetyNote = "Verified VA Official Hotline • Confidential",
        dialNumber = "988"
    ),
    VerifiedResourceHubEntry(
        id = "mh_crisistext",
        title = "Crisis Text Line (Youth, Teens & Adults)",
        category = "Mental Health Hotlines",
        contactOrAccessInfo = "Text HOME to 741741",
        description = "Free 24/7 text-based mental health support for anxiety, depression, bullying, and family stress.",
        submittedBy = "Stages Of Life Safety Team",
        isVerified = true,
        safetyNote = "Verified Crisis Support • Youth & Adult Safe",
        dialNumber = "741741"
    ),
    VerifiedResourceHubEntry(
        id = "fa_fafsa",
        title = "FAFSA (Free Application for Federal Student Aid) & Pell Grants",
        category = "Financial Aid (FAFSA & Loans)",
        contactOrAccessInfo = "Federal Student Aid Center • 1-800-433-3243",
        description = "Apply for federal Pell Grants, work-study programs, state scholarships, and low-interest federal student loans.",
        submittedBy = "Education & Aid Advisory",
        isVerified = true,
        safetyNote = "Verified Federal Financial Aid Resource • Never Pay to Apply for FAFSA",
        dialNumber = "18004333243"
    ),
    VerifiedResourceHubEntry(
        id = "fa_loans",
        title = "Federal Student Loan Repayment, SAVE Plan & Public Service Forgiveness",
        category = "Financial Aid (FAFSA & Loans)",
        contactOrAccessInfo = "Income-Driven Repayment & PSLF Support",
        description = "Compare income-driven loan repayment plans, deferment options, and Public Service Loan Forgiveness (PSLF) eligibility.",
        submittedBy = "Financial Literacy Team",
        isVerified = true,
        safetyNote = "Verified Official Loan Guidance • Zero Predatory Fees"
    ),
    VerifiedResourceHubEntry(
        id = "fa_sba",
        title = "SBA Microloans & Veteran / Small Business Startup Grants",
        category = "Financial Aid (FAFSA & Loans)",
        contactOrAccessInfo = "SBA Small Business Support • 1-800-827-5722",
        description = "Low-interest working capital microloans up to $50,000, entrepreneur training, and veteran business grants.",
        submittedBy = "Business Owner Guild",
        isVerified = true,
        safetyNote = "Verified SBA Resource • Official Government Program",
        dialNumber = "18008275722"
    ),
    VerifiedResourceHubEntry(
        id = "job_remote_tech",
        title = "Remote IT Helpdesk, Cloud Support & QA Analyst Openings",
        category = "Job Listings",
        contactOrAccessInfo = "Full-Time & Part-Time • $24–$38/hr • Remote / Hybrid",
        description = "Entry-level and mid-career remote technology support roles with paid certification training and equipment provided.",
        submittedBy = "Stages Of Life Career Board",
        isVerified = true,
        safetyNote = "Verified Employer Partner • No Upfront Training Fees"
    ),
    VerifiedResourceHubEntry(
        id = "job_veteran_ops",
        title = "Veteran Transition Logistics, Project Management & Skilled Trades",
        category = "Job Listings",
        contactOrAccessInfo = "Priority Military Skill Translation • $62k–$95k/yr",
        description = "Direct-hire leadership, aviation maintenance, supply chain, and cybersecurity pathways for transitioning Veterans.",
        submittedBy = "Veterans Career Network",
        isVerified = true,
        safetyNote = "Verified Veteran-Friendly Employer Network"
    ),
    VerifiedResourceHubEntry(
        id = "job_youth_intern",
        title = "Youth & College Paid Summer Apprenticeships (Ages 16–24)",
        category = "Job Listings",
        contactOrAccessInfo = "Paid Mentorship • Digital Media, Coding & Community Leadership",
        description = "Safe, supervised paid internships for high school teens and college students building real-world portfolios.",
        submittedBy = "Youth Opportunity Hub",
        isVerified = true,
        safetyNote = "Verified Youth-Safe Program • Parental Consent Supported"
    ),
    VerifiedResourceHubEntry(
        id = "edu_fin_lit",
        title = "Financial Literacy, Credit Building & Zero-Based Budgeting Course",
        category = "Educational Content",
        contactOrAccessInfo = "Self-Paced Interactive Modules • 6 Lessons",
        description = "Learn how APR, emergency funds, credit utilization, and compound savings work across every stage of life.",
        submittedBy = "Stages Of Life Academy",
        isVerified = true,
        safetyNote = "Verified Educational Curriculum • Free for All Members"
    ),
    VerifiedResourceHubEntry(
        id = "edu_3d_coding",
        title = "3D WebGL, Three.js & Android Compose Interactive Coding Lab",
        category = "Educational Content",
        contactOrAccessInfo = "Hands-On Project Track • Beginner to Advanced",
        description = "Build procedural 3D scenes, studio PBR shaders, and responsive Android interfaces step-by-step.",
        submittedBy = "Stages Of Life Academy",
        isVerified = true,
        safetyNote = "Verified STEM Curriculum • Safe for Youth & Adults"
    )
)

/**
 * Comprehensive 'Resource Hub' feature categorizing Mental Health Hotlines, Financial Aid (FAFSA, loans),
 * Job Listings, and Educational Content. Includes integrated search, user resource suggestions,
 * and an Administrator Accuracy & Safety Verification console.
 */
@Composable
fun ResourceHubCard(
    currentUserDisplayName: String,
    onShareResourceToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resources = remember {
        mutableStateListOf<VerifiedResourceHubEntry>().apply {
            addAll(INITIAL_VERIFIED_RESOURCES)
            // Include 1 sample user-suggested item in the pending queue so admins can test verification immediately
            add(
                VerifiedResourceHubEntry(
                    id = "pending_demo_1",
                    title = "211 Community Housing, Food & Utility Assistance Directory",
                    category = "Financial Aid (FAFSA & Loans)",
                    contactOrAccessInfo = "Dial 211 (24/7 Local Community Aid)",
                    description = "Connects families, students, and seniors with emergency utility relief, food banks, and housing grants.",
                    submittedBy = "Community Member Suggestion",
                    isVerified = false,
                    safetyNote = "Pending Admin Accuracy & Safety Review",
                    dialNumber = "211"
                )
            )
        }
    }

    val categories = listOf(
        "All",
        "Mental Health Hotlines",
        "Financial Aid (FAFSA & Loans)",
        "Job Listings",
        "Educational Content"
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showSuggestForm by remember { mutableStateOf(false) }
    var isAdminVerificationMode by remember { mutableStateOf(false) }
    var showOnlyVerified by remember { mutableStateOf(true) }

    // New Resource Suggestion Form State
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Mental Health Hotlines") }
    var newContactInfo by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    // Admin Accuracy & Safety Checklist State
    var adminCheckSourceAccuracy by remember { mutableStateOf(true) }
    var adminCheckCommunitySafety by remember { mutableStateOf(true) }
    var adminCheckZeroScamPolicy by remember { mutableStateOf(true) }

    var statusBanner by remember { mutableStateOf<String?>(null) }

    val filteredResources = remember(searchQuery, selectedCategory, showOnlyVerified, isAdminVerificationMode, resources.size, resources.toList()) {
        val q = searchQuery.trim().lowercase()
        resources.filter { item ->
            val matchesVerification = if (isAdminVerificationMode) true else (!showOnlyVerified || item.isVerified)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.contactOrAccessInfo.lowercase().contains(q) ||
                item.category.lowercase().contains(q)
            matchesVerification && matchesCategory && matchesQuery
        }
    }

    val pendingCount = resources.count { !it.isVerified }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("stages_of_life_resource_hub_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header + Admin Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🛟 STAGES OF LIFE RESOURCE HUB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Mental Health Hotlines • Financial Aid (FAFSA & Loans) • Job Listings • Education",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (isAdminVerificationMode) SolarAmber.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isAdminVerificationMode) SolarAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .clickable { isAdminVerificationMode = !isAdminVerificationMode }
                        .testTag("resource_hub_admin_mode_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Verification Mode",
                            tint = if (isAdminVerificationMode) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isAdminVerificationMode) "Admin Review ($pendingCount)" else "Admin Verify ($pendingCount)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isAdminVerificationMode) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Integrated Search Bar within the Resource Hub
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Resource Hub (988, FAFSA, Loans, Jobs, Coding...)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Resource Hub",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resource_hub_search_input")
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val selected = selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("resource_hub_category_${category.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // Action Bar: Suggest a New Resource + Verified Only Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSuggestForm = !showSuggestForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("resource_hub_suggest_button")
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Suggest Resource", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showSuggestForm) "Close Suggestion Form" else "Suggest New Resource",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Verified Only",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = showOnlyVerified,
                        onCheckedChange = { showOnlyVerified = it },
                        modifier = Modifier.testTag("resource_hub_verified_only_switch")
                    )
                }
            }

            // User Suggestion Form
            AnimatedVisibility(visible = showSuggestForm) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    ),
                    border = BorderStroke(1.dp, HoloCyan)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "➕ Suggest a Resource for Administrator Safety Verification",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "Submitted resources are placed in the Pending Verification Queue until an administrator verifies their accuracy and safety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Category Selector for Suggestion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.drop(1).forEach { cat ->
                                FilterChip(
                                    selected = newCategory == cat,
                                    onClick = { newCategory = cat },
                                    label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Resource Name / Program Title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("suggest_resource_title_input")
                        )

                        OutlinedTextField(
                            value = newContactInfo,
                            onValueChange = { newContactInfo = it },
                            label = { Text("Phone Hotline, Contact Info, or Eligibility Details") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("suggest_resource_contact_input")
                        )

                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("How does this resource help the community?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("suggest_resource_description_input")
                        )

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank() && newDescription.isNotBlank()) {
                                    val entry = VerifiedResourceHubEntry(
                                        id = "user_res_${System.currentTimeMillis()}",
                                        title = newTitle.trim(),
                                        category = newCategory,
                                        contactOrAccessInfo = newContactInfo.trim().ifBlank { "Community Resource Support" },
                                        description = newDescription.trim(),
                                        submittedBy = currentUserDisplayName.ifBlank { "Community Member" },
                                        isVerified = false,
                                        safetyNote = "Pending Administrator Accuracy & Safety Verification"
                                    )
                                    resources.add(0, entry)
                                    newTitle = ""
                                    newContactInfo = ""
                                    newDescription = ""
                                    showSuggestForm = false
                                    showOnlyVerified = false
                                    statusBanner = "✓ Submitted '${entry.title}' for Administrator Verification & Safety Review!"
                                } else {
                                    statusBanner = "Please enter a Resource Title and Description to submit."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_resource_suggestion_button")
                        ) {
                            Text(
                                text = "Submit Resource for Admin Verification",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Administrator Verification & Safety Panel
            AnimatedVisibility(visible = isAdminVerificationMode) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SolarAmber.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.2.dp, SolarAmber)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🛡️ ADMINISTRATOR ACCURACY & SAFETY VERIFICATION CONSOLE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = SolarAmber
                        )
                        Text(
                            text = "Confirm all 3 safety checks before approving user-suggested resources into the verified directory:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = adminCheckSourceAccuracy, onCheckedChange = { adminCheckSourceAccuracy = it })
                            Text("1. Verified Official Source & Accurate Contact Information", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = adminCheckCommunitySafety, onCheckedChange = { adminCheckCommunitySafety = it })
                            Text("2. Safe for Stages Of Life Community & Age-Appropriate", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = adminCheckZeroScamPolicy, onCheckedChange = { adminCheckZeroScamPolicy = it })
                            Text("3. Zero Predatory Fees or Unsolicited Financial Requests", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            statusBanner?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Resource Hub Items List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredResources.forEach { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (item.isVerified) CyberEmerald.copy(alpha = 0.55f) else SolarAmber.copy(alpha = 0.75f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("resource_hub_item_${item.id}")
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = when (item.category) {
                                            "Mental Health Hotlines" -> Icons.Default.HealthAndSafety
                                            "Job Listings" -> Icons.Default.WorkOutline
                                            else -> Icons.Default.School
                                        },
                                        contentDescription = item.category,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    color = if (item.isVerified) CyberEmerald.copy(alpha = 0.18f) else SolarAmber.copy(alpha = 0.20f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = if (item.isVerified) CyberEmerald else SolarAmber,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (item.isVerified) "VERIFIED SAFE" else "PENDING ADMIN",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = if (item.isVerified) CyberEmerald else SolarAmber
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${item.category} • ${item.contactOrAccessInfo}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = HoloCyan
                            )

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Safety Check: ${item.safetyNote} • Submitted by ${item.submittedBy}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.isVerified) CyberEmerald else SolarAmber
                            )

                            // Interactive Community Rating Row (Browse & Rate Helpful Links, Articles & Guides)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "Rating: %.1f★ (%d)".format(item.rating, item.ratingCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SolarAmber,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    for (star in 1..5) {
                                        val isFilled = star <= (if (item.userRating > 0) item.userRating else item.rating.toInt())
                                        Text(
                                            text = if (isFilled) "★" else "☆",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isFilled) SolarAmber else MaterialTheme.colorScheme.outline,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier
                                                .clickable {
                                                    val idx = resources.indexOfFirst { it.id == item.id }
                                                    if (idx >= 0) {
                                                        val newCount = if (item.userRating == 0) item.ratingCount + 1 else item.ratingCount
                                                        val newRating = if (item.userRating == 0) {
                                                            ((item.rating * item.ratingCount) + star) / newCount
                                                        } else {
                                                            ((item.rating * item.ratingCount) - item.userRating + star) / item.ratingCount
                                                        }
                                                        resources[idx] = item.copy(
                                                            userRating = star,
                                                            rating = newRating,
                                                            ratingCount = newCount
                                                        )
                                                        statusBanner = "✓ Rated '${item.title}' $star★! Thank you for helping the community."
                                                    }
                                                }
                                                .padding(horizontal = 2.dp)
                                        )
                                    }
                                }
                                if (item.userRating > 0) {
                                    Text(
                                        text = "Your rating: ${item.userRating}★",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Action Row: Dial Hotline (if phone number), Share to Community Wall, or Admin Approve/Reject
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item.dialNumber?.let { number ->
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                                context.startActivity(dialIntent)
                                            } catch (_: Exception) {
                                                statusBanner = "Hotline Number: $number"
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("resource_hub_dial_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Dial Hotline", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Dial $number", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        onShareResourceToWall(
                                            "Resource Hub: ${item.title}",
                                            "${item.category} — ${item.contactOrAccessInfo}. ${item.description}"
                                        )
                                        statusBanner = "✓ Shared '${item.title}' to Community Feed!"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("resource_hub_share_${item.id}")
                                ) {
                                    Text("Share Resource", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }

                                if (isAdminVerificationMode && !item.isVerified) {
                                    Button(
                                        onClick = {
                                            if (adminCheckSourceAccuracy && adminCheckCommunitySafety && adminCheckZeroScamPolicy) {
                                                val index = resources.indexOfFirst { it.id == item.id }
                                                if (index >= 0) {
                                                    resources[index] = item.copy(
                                                        isVerified = true,
                                                        safetyNote = "Verified Accurate & Safe by Administrator"
                                                    )
                                                    statusBanner = "✓ Administrator verified & approved '${item.title}'!"
                                                }
                                            } else {
                                                statusBanner = "Please complete all 3 Admin Accuracy & Safety checks above before approving."
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("resource_hub_admin_approve_${item.id}")
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Approve", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve & Verify", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            resources.removeAll { it.id == item.id }
                                            statusBanner = "Removed unverified suggestion '${item.title}'."
                                        },
                                        border = BorderStroke(1.dp, RosePulse),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("resource_hub_admin_reject_${item.id}")
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Reject", tint = RosePulse, modifier = Modifier.size(14.dp))
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
