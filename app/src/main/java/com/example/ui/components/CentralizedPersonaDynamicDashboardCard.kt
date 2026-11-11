package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

enum class StagesOfLifePersona(
    val id: String,
    val label: String,
    val emoji: String,
    val subtitle: String,
    val themeAccent: Color,
    val icon: ImageVector
) {
    KID(
        id = "kid",
        label = "Kid",
        emoji = "🧸",
        subtitle = "Ages 6-12 • Safe Play, Creative Art & 3D Arcade",
        themeAccent = Color(0xFFEAB308),
        icon = Icons.Default.ChildCare
    ),
    TEEN(
        id = "teen",
        label = "Teen",
        emoji = "🎒",
        subtitle = "Ages 13-17 • 3D Gaming, High School Prep & Coding Lab",
        themeAccent = Color(0xFFF97316),
        icon = Icons.Default.SportsEsports
    ),
    STUDENT(
        id = "student",
        label = "Student",
        emoji = "🎓",
        subtitle = "Ages 18-24 • FAFSA Aid, AI Study Hub & Campus Network",
        themeAccent = Color(0xFF7C3AED),
        icon = Icons.Default.School
    ),
    ADULT(
        id = "adult",
        label = "Adult",
        emoji = "🌟",
        subtitle = "Ages 25-54 • Career Growth, Home & Family Wellness",
        themeAccent = Color(0xFF2563EB),
        icon = Icons.Default.Person
    ),
    BUSINESS(
        id = "business",
        label = "Business",
        emoji = "💼",
        subtitle = "Founders & Owners • SBA Grants, B2B & Commerce",
        themeAccent = Color(0xFF1E3A8A),
        icon = Icons.Default.BusinessCenter
    ),
    VETERAN(
        id = "veteran",
        label = "Veteran",
        emoji = "🛡️",
        subtitle = "Military & Transition • VA Benefits, Leadership & 988 Aid",
        themeAccent = Color(0xFF15803D),
        icon = Icons.Default.MilitaryTech
    )
}

/**
 * Centralized dashboard UI component that dynamically adjusts its layout based on the user's
 * selected 'Stages Of Life' persona (Kid, Teen, Student, Adult, Business, or Veteran) using a
 * Conditional composition pattern (`when (currentPersona)`).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CentralizedPersonaDynamicDashboardCard(
    currentPersonaRole: String,
    userDisplayName: String,
    onSwitchPersona: (String) -> Unit,
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareMilestoneToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val activePersona = remember(currentPersonaRole) {
        val lower = currentPersonaRole.lowercase()
        when {
            lower.contains("kid") || lower.contains("child") -> StagesOfLifePersona.KID
            lower.contains("teen") -> StagesOfLifePersona.TEEN
            lower.contains("student") -> StagesOfLifePersona.STUDENT
            lower.contains("business") -> StagesOfLifePersona.BUSINESS
            lower.contains("veteran") -> StagesOfLifePersona.VETERAN
            else -> StagesOfLifePersona.ADULT
        }
    }

    var feedbackNotice by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, activePersona.themeAccent.copy(alpha = 0.85f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("centralized_persona_dynamic_dashboard_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Persona Switcher Selector + Dynamic Badge
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(activePersona.themeAccent.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activePersona.icon,
                            contentDescription = activePersona.label,
                            tint = activePersona.themeAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "STAGES OF LIFE: ${activePersona.emoji} ${activePersona.label.uppercase()} DASHBOARD",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = activePersona.themeAccent
                        )
                        Text(
                            text = "Conditional layout tuned for ${userDisplayName.ifBlank { "You" }} • ${activePersona.subtitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = activePersona.themeAccent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, activePersona.themeAccent)
                ) {
                    Text(
                        text = "DYNAMIC LAYOUT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = activePersona.themeAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick 1-Tap Persona Switch Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StagesOfLifePersona.entries.forEach { persona ->
                    val isSelected = activePersona == persona
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSwitchPersona(persona.label)
                            feedbackNotice = "✓ Switched dashboard layout to ${persona.emoji} ${persona.label} Mode!"
                        },
                        label = {
                            Text(
                                text = "${persona.emoji} ${persona.label}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("persona_tab_chip_${persona.id}")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // CONDITIONAL COMPOSITION PATTERN: Layout changes dynamically per persona
            AnimatedContent(
                targetState = activePersona,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "dynamic_persona_dashboard_layout"
            ) { persona ->
                when (persona) {
                    StagesOfLifePersona.KID -> {
                        KidDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                    StagesOfLifePersona.TEEN -> {
                        TeenDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                    StagesOfLifePersona.STUDENT -> {
                        StudentDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                    StagesOfLifePersona.ADULT -> {
                        AdultDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                    StagesOfLifePersona.BUSINESS -> {
                        BusinessDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                    StagesOfLifePersona.VETERAN -> {
                        VeteranDashboardComposition(
                            onNavigateToTab = onNavigateToTab,
                            onShareAction = onShareMilestoneToWall
                        )
                    }
                }
            }

            feedbackNotice?.let { notice ->
                Text(
                    text = notice,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Conditional Persona Compositions
// -------------------------------------------------------------

@Composable
private fun KidDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Kid Hero Banner (Playful, oversized colorful tiles)
        Surface(
            color = Color(0xFFFEF08A).copy(alpha = 0.35f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFFEAB308)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🧸", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kids 3D Arcade & Discovery Wonder",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "100% Kid Safe • Fun Math Puzzles, 3D Color Studio & Space Adventure Games!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Kid Fast Action Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.STUDIO_ARCADE) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308), contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Play 3D Games", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.THEME_PROFILE) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFFEAB308)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DesignServices, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Color Avatar", color = Color(0xFFEAB308), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TeenDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Teen Cyber Gaming & Tech Card
        Surface(
            color = Color(0xFFFFEDD5).copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFFF97316)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🎒", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Teen Creator Lab & 3D Web Studio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Three.js 3D Coding Challenges • Youth Arcades • First Part-Time Job Readiness",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.HUB_3D) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316), contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Three.js Studio", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.RESOURCES) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFFF97316)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Youth Guides", color = Color(0xFFF97316), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StudentDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color(0xFFEDE9FE).copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF7C3AED)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🎓", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Collegiate & Academic Excellence Hub",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "FAFSA Aid & Pell Grants • AI Study Flashcards • Zero-Based College Budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.RESOURCES) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("FAFSA & Loans", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.AI_TOOLS) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFF7C3AED)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Study Lab", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdultDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color(0xFFDBEAFE).copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF2563EB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🌟", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Adult Lifestyle, Career & Home Stability",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "18+ Community Connections • Smart Emergency Funds • Verified Career Boards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.FINANCE_GOALS) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finance Goals", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.FRIENDS) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFF2563EB)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Connections", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BusinessDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color(0xFF1E3A8A).copy(alpha = 0.15f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF1E3A8A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "💼", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Executive Enterprise & Startup Suite",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "SBA Microloans & Capital Grants • B2B Partnership Exchange • Commerce Deliveries",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.RESOURCES) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SBA Grants", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.DELIVERY_DRIVING) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFF1E3A8A)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Commerce Port", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VeteranDashboardComposition(
    onNavigateToTab: (DashboardTab) -> Unit,
    onShareAction: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color(0xFFDCFCE7).copy(alpha = 0.35f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF15803D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🛡️", style = MaterialTheme.typography.headlineMedium)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Veteran Command & Mission Transition Hub",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "VA Disability & GI Bill Navigator • 24/7 Veterans Crisis Line • Military Transition Careers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(DashboardTab.RESOURCES) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("VA & 988 Aid", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(DashboardTab.FINANCE_GOALS) },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, Color(0xFF15803D)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Transition Milestones", color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
            }
        }
    }
}
