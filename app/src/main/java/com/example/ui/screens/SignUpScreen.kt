package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.SignUpFormState
import com.example.ui.auth.AuthManager
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignUpScreen(
    formState: SignUpFormState,
    isAuthenticated: Boolean,
    isDarkMode: Boolean = true,
    onSetDarkMode: (Boolean) -> Unit = {},
    onDisplayNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onRoleSelect: (String) -> Unit,
    onThemeColorSelect: (String) -> Unit,
    onMusicChange: (String, String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onPledgeChange: (Boolean) -> Unit,
    onSaveAuthenticatedProfile: () -> Unit,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    onBack: () -> Unit,
    isEmbeddedInLoginScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!isEmbeddedInLoginScreen) {
        BackHandler {
            onBack()
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var isAuthenticating by remember { mutableStateOf(false) }

    val availableInterests = listOf(
        "Healthcare & Wellness",
        "VA & Veteran Support",
        "FAFSA & College Aid",
        "211 Housing & Food",
        "Music & 3D Art",
        "STEM & Coding",
        "Business & Career Grants",
        "Financial Stability"
    )

    val themePalette = listOf(
        "#6366F1" to "Indigo Pulse",
        "#EC4899" to "Neon Magenta",
        "#10B981" to "Cyber Emerald",
        "#F59E0B" to "Solar Gold",
        "#06B6D4" to "Holo Cyan"
    )

    val musicPlatforms = listOf("Spotify", "SoundCloud", "YouTube Music")

    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }

    val ageGroupOptions = listOf(
        "14" to "🧒 Kids & Youth (13–15)",
        "16" to "🎒 Teens (16–17)",
        "20" to "🎓 Young Adults (18–24)",
        "29" to "🌟 Adults (25–54)",
        "35" to "🛡️ Veterans & Leaders (28+)"
    )

    val containerModifier = if (isEmbeddedInLoginScreen) {
        modifier
            .fillMaxWidth()
            .testTag("embedded_account_creation_section")
    } else {
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    }

    Column(
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("signup_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.switch_to_login),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.signup_header_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Choose your age group, Business or Content Account type, and login with Email, Phone Number, or Name.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Persona Selection & Age-Verified Profile Customization System
            com.example.ui.components.PersonaSelectionSystemCard(
                currentRole = formState.role,
                currentAge = formState.ageText.toLongOrNull() ?: 26L,
                onApplyPersona = { role, age, defaultColorHex, _ ->
                    onRoleSelect(role)
                    onAgeChange(age.toString())
                    onThemeColorSelect(defaultColorHex)
                }
            )

            // Step 1: Account Login Credentials (Name, Email, Phone Number) & Age Group Selection
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Account Login (Name, Email & Phone Number) + Age Group",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Every user can log in using their Name, Email Address, and/or Phone Number.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = formState.displayName,
                        onValueChange = onDisplayNameChange,
                        label = { Text("Full Name / Display Handle") },
                        placeholder = { Text("e.g., Jordan Vance") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("you@email.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("signup_email_input")
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("(555) 234-5678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("signup_phone_input")
                        )
                    }

                    Text(
                        text = "Choose Your Age Group:",
                        style = MaterialTheme.typography.labelLarge,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ageGroupOptions.forEach { (ageVal, label) ->
                            val selected = formState.ageText == ageVal
                            FilterChip(
                                selected = selected,
                                onClick = { onAgeChange(ageVal) },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("signup_age_group_$ageVal")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.ageText,
                            onValueChange = onAgeChange,
                            label = { Text("Exact Age") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(0.38f)
                                .testTag("signup_age_input")
                        )
                        OutlinedTextField(
                            value = formState.location,
                            onValueChange = onLocationChange,
                            label = { Text("City / State") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.62f)
                                .testTag("signup_location_input")
                        )
                    }

                    OutlinedTextField(
                        value = formState.bio,
                        onValueChange = onBioChange,
                        label = { Text("Personal, Content Creator, or Business Bio") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_bio_input")
                    )
                }
            }

            // Step 2: Account Type Selection (Student Account, Business Account, Content Account, Personal, Veteran)
            var isStudentSubMode by remember { mutableStateOf(true) }
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. Choose Account Type: Student, Business, Content, Personal, or Veteran",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select a Student Account for all school resources & links to apply for school, jobs & careers; Business Account for Marketplace Ads; or Content Account for 3D media.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Highlighted Student Account Full-Width Card
                    PersonaOptionCard(
                        roleKey = "student",
                        label = "🎓 Student Account (School Resources + Apply for School, Jobs & Careers)",
                        subtitle = "Common App school applications, FAFSA financial aid, Khan Academy, OpenLibrary & student job/career portals",
                        icon = Icons.Default.School,
                        selected = formState.role == "creator" && isStudentSubMode,
                        onClick = {
                            isStudentSubMode = true
                            onRoleSelect("creator")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PersonaOptionCard(
                            roleKey = "business",
                            label = "💼 Business Account",
                            subtitle = "Marketplace Ads, Digital Wallet reserve & SBA tools",
                            icon = Icons.Default.BusinessCenter,
                            selected = formState.role == "business",
                            onClick = {
                                isStudentSubMode = false
                                onRoleSelect("business")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PersonaOptionCard(
                            roleKey = "creator",
                            label = "🎬 Content Account",
                            subtitle = "Creator Studio, 3D Art, Music & Marketplace drops",
                            icon = Icons.Default.Palette,
                            selected = formState.role == "creator" && !isStudentSubMode,
                            onClick = {
                                isStudentSubMode = false
                                onRoleSelect("creator")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PersonaOptionCard(
                            roleKey = "adult",
                            label = "🌟 Personal / Adult Account",
                            subtitle = "Full 6D hub, Digital Wallet & community lounge",
                            icon = Icons.Default.Person,
                            selected = formState.role == "adult",
                            onClick = {
                                isStudentSubMode = false
                                onRoleSelect("adult")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PersonaOptionCard(
                            roleKey = "veteran",
                            label = "🛡️ Veteran Account",
                            subtitle = "VA clinics, GI Bill, peer support & Digital Wallet",
                            icon = Icons.Default.MilitaryTech,
                            selected = formState.role == "veteran",
                            onClick = {
                                isStudentSubMode = false
                                onRoleSelect("veteran")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Interactive Persona-Based Profile System (Veteran, Business, Kid, Teen, Adult, Student)
                    com.example.ui.components.PersonaProfileSystemCard(
                        currentRole = formState.role,
                        currentAgeText = formState.ageText,
                        currentVisualStyleId = formState.themeVisualStyle,
                        currentThemeHex = formState.themeColorHex,
                        selectedInterests = formState.selectedInterests,
                        onSelectPersona = { roleKey, defaultAge, defaultHex, _ ->
                            isStudentSubMode = (roleKey == "creator")
                            onRoleSelect(roleKey)
                            onAgeChange(defaultAge)
                            onThemeColorSelect(defaultHex)
                        },
                        onApplyPersonaTheme = { _, colorHex, _ ->
                            onThemeColorSelect(colorHex)
                        },
                        onPinPersonaInterest = { interest ->
                            if (!formState.selectedInterests.contains(interest)) {
                                onToggleInterest(interest)
                            }
                        }
                    )

                    // Direct School, Job & Career Application Links right inside Account Setup
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🎓 Student School Resources & Direct Links to Apply for School, Jobs & Careers:",
                                style = MaterialTheme.typography.labelLarge,
                                color = HoloCyan,
                                fontWeight = FontWeight.ExtraBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "🎓 Apply for School (Common App)" to "https://www.commonapp.org/",
                                    "💰 Apply for FAFSA Aid" to "https://studentaid.gov/",
                                    "📚 Free School Courses (Khan Academy)" to "https://www.khanacademy.org/",
                                    "📖 Free Textbooks (OpenLibrary)" to "https://openlibrary.org/",
                                    "💼 Apply for Careers (CareerOneStop)" to "https://www.careeronestop.org/",
                                    "🏛️ Student Jobs (USAJOBS)" to "https://www.usajobs.gov/Help/working-in-government/unique-hiring-paths/students/",
                                    "🎓 Student Internships (Handshake)" to "https://joinhandshake.com/",
                                    "🚀 Apply for Jobs (Indeed)" to "https://www.indeed.com/"
                                ).forEach { (label, url) ->
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = android.content.Intent(
                                                    android.content.Intent.ACTION_VIEW,
                                                    android.net.Uri.parse(url)
                                                )
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                            }
                                        },
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f))
                                    ) {
                                        Text(
                                            text = label,
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
            }

            // Step 3: Interest Discovery Quiz
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. Discovery Quiz: What Interests You?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Choose topics to curate your profile dashboard and resource recommendations:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableInterests.forEach { interest ->
                            val isSelected = formState.selectedInterests.contains(interest)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onToggleInterest(interest) },
                                label = { Text(interest) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            // Step 4: MySpace 3D Theme & 32-Color Customizer
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "3D Theme Color",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "4. WereEqual Theme Selection, Basic Colors & Soundtrack",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.example.ui.theme.EXPANDED_THEME_COLOR_OPTIONS.forEach { option ->
                            val isSelected = formState.themeColorHex.equals(option.hex, ignoreCase = true)
                            Surface(
                                color = if (isSelected) option.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) option.color else option.color.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.clickable { onThemeColorSelect(option.hex) }
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

                    // Material 3 Light & Dark Mode Toggle inside 3D Theme Setup
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_m3_mode_toggle_row")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (isDarkMode) HoloCyan else SolarAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isDarkMode) "M3 Dark Mode (3D Nebula)" else "M3 Light Mode (3D Studio)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Switch Material 3 color scheme for your 3D profile",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onSetDarkMode(it) },
                                modifier = Modifier.testTag("signup_dark_mode_switch")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = formState.musicTrackTitle,
                        onValueChange = { onMusicChange(it, formState.musicPlatform) },
                        label = { Text("Profile Anthem (Song & Artist)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ALL_MUSIC_PLATFORMS.forEach { platform ->
                            val selected = formState.musicPlatform == platform
                            FilterChip(
                                selected = selected,
                                onClick = { onMusicChange(formState.musicTrackTitle, platform) },
                                label = { Text(platform) }
                            )
                        }
                    }
                }
            }

            // Step 5: Mandatory Wondering World Code of Conduct Agreement
            com.example.ui.components.ProfileCodeOfConductAgreementCard(
                accepted = formState.pledgeAccepted,
                onAcceptedChange = onPledgeChange,
                checkboxTestTag = "signup_pledge_checkbox",
                cardTestTag = "signup_code_of_conduct_card"
            )

            if (!formState.statusMessage.isNullOrBlank()) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formState.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Primary Submit CTA: Create / Save Profile Page (Requires Code of Conduct Agreement)
            Button(
                onClick = {
                    if (!formState.pledgeAccepted) {
                        onAuthError("Please check 'I Agree to the Wondering World Code of Conduct' above to create your profile page.")
                        return@Button
                    }
                    onSaveAuthenticatedProfile()
                },
                enabled = formState.pledgeAccepted && !isAuthenticating && !formState.isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberEmerald,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("complete_signup_button")
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formState.pledgeAccepted) {
                            "Agree to Code of Conduct & Save Profile Page"
                        } else {
                            "Check Code of Conduct Box Above to Create Profile"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Optional Cloud Google Sign-In Sync Button
            if (!isAuthenticated) {
                OutlinedButton(
                    onClick = {
                        if (!formState.pledgeAccepted) {
                            onAuthError("Please agree to the Code of Conduct first.")
                            return@OutlinedButton
                        }
                        isAuthenticating = true
                        AuthManager.onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isAuthenticating = false
                                onAuthSuccess()
                            },
                            onAuthError = { msg ->
                                isAuthenticating = false
                                onAuthError(msg)
                            },
                            scope = scope,
                            onAuthCancelled = {
                                isAuthenticating = false
                            }
                        )
                    },
                    enabled = formState.pledgeAccepted && !isAuthenticating,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_cloud_sync_signup_button")
                ) {
                    Text(
                        text = stringResource(R.string.complete_signup_google),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("navigate_to_login_button")
            ) {
                Text(
                    text = "Return to Main Dashboard",
                    style = MaterialTheme.typography.labelLarge,
                    color = HoloCyan
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PersonaOptionCard(
    roleKey: String,
    label: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("persona_option_$roleKey"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
