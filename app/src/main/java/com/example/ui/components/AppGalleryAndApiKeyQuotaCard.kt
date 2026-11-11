package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber

data class AppGalleryTemplate(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val howItsBuilt: List<String>,
    val defaultAccentHex: String,
    val defaultStyleId: String
)

private val APP_GALLERY_TEMPLATES = listOf(
    AppGalleryTemplate(
        id = "pyramid_3d_visualizer",
        title = "3D Pyramid 'Create Your Life' Studio",
        category = "Three.js 3D & PBR",
        summary = "Interactive 3D dimensional pyramid with LIVE • LIFE • LOVE orbital geometry and studio PBR shading.",
        howItsBuilt = listOf(
            "Renderer: Procedural 3D Mesh + Studio PBR (Metalness/Roughness + Fresnel Rim Light)",
            "Zero-Occlusion: Painter's depth sorting + bounding-sphere orbital separation",
            "Theme Integration: Solid Color Albedo Swatches + 100% Transparent Background support"
        ),
        defaultAccentHex = "#2563EB",
        defaultStyleId = "HYPERCUBE_6D"
    ),
    AppGalleryTemplate(
        id = "resource_hub_verifier",
        title = "Community Resource Hub & Admin Verifier",
        category = "Civic & Wellness",
        summary = "Categorized 988 Mental Health Hotlines, FAFSA/Loans, Job Listings & Admin Safety Verification workflow.",
        howItsBuilt = listOf(
            "State Layer: Reactive verified/pending resource queue with real-time search filtering",
            "Safety Gate: 3-point Administrator Accuracy & Safety Verification checklist",
            "Telephony Integration: 1-tap ACTION_DIAL hotline launcher for 988 & 211"
        ),
        defaultAccentHex = "#16A34A",
        defaultStyleId = "AURORA_WAVE"
    ),
    AppGalleryTemplate(
        id = "persona_stage_engine",
        title = "Persona & Age-Verified Profile Engine",
        category = "Identity & Safety",
        summary = "Dynamic persona switcher for Veteran, Business Owner, Adult, Student, Teenager & Child profiles.",
        howItsBuilt = listOf(
            "Age Gate: Birth-year calculator + 18+ verification check for adult social lounges",
            "Youth Protection: Automatic Child/Teen safe mode locking 18+ sections & enabling 3D Arcade",
            "Adaptive Theming: Maps each persona to its signature Solid Color & tailored feed"
        ),
        defaultAccentHex = "#7C3AED",
        defaultStyleId = "GALAXY_VORTEX"
    ),
    AppGalleryTemplate(
        id = "youth_3d_arcade_lab",
        title = "Youth 3D Arcade & Creative Avatar Remix",
        category = "3D Games & Avatar",
        summary = "Safe, colorful 3D avatar customizer and interactive mini-games for kids, teens, and creators.",
        howItsBuilt = listOf(
            "3D Transform: Compose graphicsLayer pitch/yaw/roll + custom Canvasaccessories",
            "Bitmap Baker: Renders custom 3D avatar identity directly to profile badge",
            "Color Engine: 18 solid colors + transparent glass backdrop toggle"
        ),
        defaultAccentHex = "#F97316",
        defaultStyleId = "CYBER_SYNTHWAVE"
    )
)

/**
 * Combined card for:
 * 1. Explore the App Gallery (Find an app, inspect how it's built, and remix it to make it your own)
 * 2. Use Your API Key & Free Quota Auto-Switch Manager (Switch to custom API key when free quota runs out,
 *    and automatically switch back to free quota when it resets).
 */
@Composable
fun AppGalleryAndApiKeyQuotaCard(
    onApplyRemixedAppTheme: (colorHex: String, styleId: String, makeTransparent: Boolean) -> Unit,
    onShareRemixToWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTemplate by remember { mutableStateOf(APP_GALLERY_TEMPLATES.first()) }
    var showHowItsBuilt by remember { mutableStateOf(true) }
    var remixTitleInput by remember { mutableStateOf("My ${APP_GALLERY_TEMPLATES.first().title} Remix") }
    var remixColorHex by remember { mutableStateOf(APP_GALLERY_TEMPLATES.first().defaultAccentHex) }
    var remixTransparentBg by remember { mutableStateOf(false) }
    val remixedAppsLog = remember { mutableStateListOf<String>() }
    var galleryStatus by remember { mutableStateOf<String?>(null) }

    // API Key & Quota Auto-Switch State
    var userApiKeyInput by remember { mutableStateOf("") }
    var maskApiKey by remember { mutableStateOf(true) }
    var autoSwitchToUserKey by remember { mutableStateOf(true) }
    var autoReturnToFreeQuotaOnReset by remember { mutableStateOf(true) }
    var freeQuotaRemaining by remember { mutableIntStateOf(85) } // out of 100
    var quotaStatusBanner by remember { mutableStateOf<String?>(null) }

    val isUsingCustomKeyNow = freeQuotaRemaining == 0 && autoSwitchToUserKey && userApiKeyInput.isNotBlank()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_gallery_and_api_key_quota_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // PART 1: EXPLORE THE APP GALLERY & REMIX STUDIO
            Text(
                text = "🚀 EXPLORE THE APP GALLERY • INSPECT & REMIX",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Kickstart your project by exploring the Stages Of Life App Gallery. Find an app you like, see how it's built, and remix it to make it your own.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                APP_GALLERY_TEMPLATES.forEach { tpl ->
                    val isSelected = selectedTemplate.id == tpl.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTemplate = tpl
                            remixTitleInput = "My ${tpl.title} Remix"
                            remixColorHex = tpl.defaultAccentHex
                        },
                        label = {
                            Text(
                                text = tpl.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("app_gallery_template_${tpl.id}")
                    )
                }
            }

            // Selected App Gallery Card Details + How It's Built + Remix Controls
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                ),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f))
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedTemplate.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedTemplate.category} • ${selectedTemplate.summary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { showHowItsBuilt = !showHowItsBuilt },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("app_gallery_how_its_built_button")
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "How It's Built", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHowItsBuilt) "Hide Architecture" else "See How It's Built",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AnimatedVisibility(visible = showHowItsBuilt) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🔧 How It's Built (Architecture & Stack):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = HoloCyan
                            )
                            selectedTemplate.howItsBuilt.forEach { step ->
                                Text(
                                    text = "• $step",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Remix Studio Inputs
                    OutlinedTextField(
                        value = remixTitleInput,
                        onValueChange = { remixTitleInput = it },
                        label = { Text("Remix Project Title (Make It Your Own)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_gallery_remix_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Remix with 100% Transparent Background",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = remixTransparentBg,
                                onCheckedChange = { remixTransparentBg = it },
                                modifier = Modifier.testTag("app_gallery_remix_transparent_switch")
                            )
                        }

                        Button(
                            onClick = {
                                val cleanTitle = remixTitleInput.trim().ifBlank { "My ${selectedTemplate.title} Remix" }
                                remixedAppsLog.add(0, "$cleanTitle (${selectedTemplate.category})")
                                onApplyRemixedAppTheme(
                                    remixColorHex,
                                    selectedTemplate.defaultStyleId,
                                    remixTransparentBg
                                )
                                onShareRemixToWall(
                                    "Remixed App: $cleanTitle",
                                    "Remixed '${selectedTemplate.title}' from the Stages Of Life App Gallery with custom solid color $remixColorHex and ${if (remixTransparentBg) "100% Transparent Background" else "Solid Backdrop"}!"
                                )
                                galleryStatus = "✓ Remixed '$cleanTitle' & applied your custom theme to Stages Of Life!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("app_gallery_remix_launch_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Remix App", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remix & Apply Now",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    galleryStatus?.let { msg ->
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))

            // PART 2: USE YOUR API KEY & FREE QUOTA AUTO-SWITCH MANAGER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔑 USE YOUR API KEY • SMART QUOTA AUTO-SWITCH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Switch to your API key whenever you run out of quota, and we'll automatically switch back to your free quota when it resets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (isUsingCustomKeyNow) SolarAmber.copy(alpha = 0.22f) else CyberEmerald.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isUsingCustomKeyNow) SolarAmber else CyberEmerald)
                ) {
                    Text(
                        text = if (isUsingCustomKeyNow) "ACTIVE: YOUR API KEY" else "ACTIVE: FREE QUOTA ($freeQuotaRemaining%)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = if (isUsingCustomKeyNow) SolarAmber else CyberEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { freeQuotaRemaining / 100f },
                color = if (freeQuotaRemaining > 20) CyberEmerald else SolarAmber,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = userApiKeyInput,
                onValueChange = { userApiKeyInput = it },
                label = { Text("Your Personal Fallback API Key (Used Only When Free Quota Hits 0%)") },
                placeholder = { Text("Enter your API key for zero-downtime fallback...") },
                singleLine = true,
                visualTransformation = if (maskApiKey) PasswordVisualTransformation() else VisualTransformation.None,
                leadingIcon = {
                    Icon(Icons.Default.Key, contentDescription = "API Key", tint = SolarAmber)
                },
                trailingIcon = {
                    Text(
                        text = if (maskApiKey) "Show" else "Hide",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = HoloCyan,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { maskApiKey = !maskApiKey }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_fallback_api_key_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Switch to My API Key When Free Quota Runs Out",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automatically Return to Free Quota When It Resets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoSwitchToUserKey && autoReturnToFreeQuotaOnReset,
                    onCheckedChange = {
                        autoSwitchToUserKey = it
                        autoReturnToFreeQuotaOnReset = it
                    },
                    modifier = Modifier.testTag("api_key_auto_switch_toggle")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (userApiKeyInput.isBlank()) {
                            userApiKeyInput = "SOL_USER_KEY_LIVE_FALLBACK"
                        }
                        freeQuotaRemaining = 0
                        quotaStatusBanner = "⚡ Free quota reached 0% — Automatically switched to your API Key with zero interruption!"
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_quota_exhausted_button")
                ) {
                    Icon(Icons.Default.SyncAlt, contentDescription = "Test Fallback", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Quota Fallback", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        freeQuotaRemaining = 100
                        quotaStatusBanner = "✓ Free quota reset to 100% — Automatically switched back to your Free Quota!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_quota_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Free Quota", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Free Quota Reset", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            quotaStatusBanner?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
