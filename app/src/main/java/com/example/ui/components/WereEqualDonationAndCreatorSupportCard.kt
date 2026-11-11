package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class CharityOrganizationPreset(
    val id: String,
    val name: String,
    val category: String,
    val emoji: String,
    val missionSummary: String,
    val officialDonateUrl: String
)

data class DonationPledgeRecord(
    val id: String,
    val recipientName: String,
    val recipientType: String, // "Charity of Choice" or "Platform Creator"
    val amountDollars: Double,
    val eqcBonusAwarded: Long,
    val dedicationNote: String
)

val VERIFIED_CHARITY_PRESETS: List<CharityOrganizationPreset> = listOf(
    CharityOrganizationPreset(
        id = "dav_veterans",
        name = "Disabled American Veterans (DAV)",
        category = "Veterans & Families",
        emoji = "🛡️",
        missionSummary = "Empowers veterans to lead high-quality lives with respect and dignity.",
        officialDonateUrl = "https://www.dav.org/"
    ),
    CharityOrganizationPreset(
        id = "st_jude_kids",
        name = "St. Jude Children's Research Hospital",
        category = "Kids & Healthcare",
        emoji = "🏥",
        missionSummary = "Families never receive a bill from St. Jude for treatment, travel, housing or food.",
        officialDonateUrl = "https://www.stjude.org/"
    ),
    CharityOrganizationPreset(
        id = "feeding_america",
        name = "Feeding America Community Food Banks",
        category = "Food & Stability",
        emoji = "🍎",
        missionSummary = "Nationwide network of 200+ food banks feeding families and seniors.",
        officialDonateUrl = "https://www.feedingamerica.org/"
    ),
    CharityOrganizationPreset(
        id = "united_way_211",
        name = "United Way 211 Community Fund",
        category = "Housing & Emergency Aid",
        emoji = "🤝",
        missionSummary = "Supports local 211 emergency housing, utility assistance, and crisis support.",
        officialDonateUrl = "https://www.unitedway.org/"
    ),
    CharityOrganizationPreset(
        id = "khan_academy_edu",
        name = "Khan Academy Free World-Class Education",
        category = "Youth & STEM Education",
        emoji = "🎓",
        missionSummary = "Provides free, world-class education for anyone, anywhere.",
        officialDonateUrl = "https://www.khanacademy.org/donate"
    ),
    CharityOrganizationPreset(
        id = "doctors_without_borders",
        name = "Doctors Without Borders (MSF)",
        category = "Global Humanitarian",
        emoji = "🌍",
        missionSummary = "Delivers emergency medical aid to people affected by conflict and disasters.",
        officialDonateUrl = "https://www.doctorswithoutborders.org/"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WereEqualDonationAndCreatorSupportCard(
    donorDisplayName: String = "Jordan Vance",
    onRecordGivingInBudget: (title: String, amountDollars: Double) -> Unit = { _, _ -> },
    onTipEqualCoinsToCreatorOrCharity: (recipientName: String, eqcAmount: Long, note: String) -> Unit = { _, _, _ -> },
    onEquipSupporterBadge: (String) -> Unit = {},
    onShareDonationToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeDonationTab by remember { mutableIntStateOf(0) } // 0 = Donate to a Charity of Your Choice, 1 = Support the Platform Creator
    var selectedCharity by remember { mutableStateOf(VERIFIED_CHARITY_PRESETS.first()) }
    var customCharityName by remember { mutableStateOf("") }
    var customCharityUrl by remember { mutableStateOf("") }
    var selectedAmountDollars by remember { mutableDoubleStateOf(25.0) }
    var customAmountText by remember { mutableStateOf("") }
    var dedicationNote by remember {
        mutableStateOf("We are all equal — supporting our community & keeping WereEqual thriving! 💖")
    }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    val creatorSupportHttpsUrl = "https://ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app/donate-creator"
    val creatorSupportBackslashUrl = "https:\\\\ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app\\donate-creator"

    val pledgeHistory = remember {
        mutableStateListOf(
            DonationPledgeRecord(
                id = "seed_don_1",
                recipientName = "WereEqual Platform Creator Fund",
                recipientType = "Platform Creator",
                amountDollars = 25.0,
                eqcBonusAwarded = 50L,
                dedicationNote = "Thank you for building the 6D WereEqual community platform!"
            ),
            DonationPledgeRecord(
                id = "seed_don_2",
                recipientName = "Disabled American Veterans (DAV)",
                recipientType = "Charity of Choice",
                amountDollars = 20.0,
                eqcBonusAwarded = 40L,
                dedicationNote = "Supporting our fellow veterans and their families."
            )
        )
    }

    val effectiveAmount = customAmountText.toDoubleOrNull()?.coerceAtLeast(1.0) ?: selectedAmountDollars

    fun copyToClipboard(label: String, text: String) {
        try {
            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            cb?.setPrimaryClip(ClipData.newPlainText(label, text))
            feedbackBanner = "✓ Copied $label: $text"
        } catch (_: Exception) {
            feedbackBanner = "✓ Ready: $text"
        }
    }

    fun openExternalUrl(url: String) {
        try {
            val clean = if (url.startsWith("http", ignoreCase = true)) url else "https://$url"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clean)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wereequal_donation_and_creator_support_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1120).copy(alpha = 0.94f)
        ),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.linearGradient(
                listOf(RosePulse, SolarAmber, CyberEmerald, HoloCyan)
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
                            .background(Brush.linearGradient(listOf(RosePulse, SolarAmber)))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "WereEqual Donation Page",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "💖 STAGES OF LIFE DONATION & CREATOR SUPPORT PAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Donate to Any Charity or Help the Creator Keep Creating",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "\"Create Your Life • Live Life Love\" • Support a charity of your choice or fuel new 6D features",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // 2 Primary Mode Tabs:
            // Tab 0: 🌍 Donate to a Charity of Your Choice
            // Tab 1: 🚀 Help the Platform Creator Keep Creating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(0, "🌍 Charity of Your Choice", "donation_tab_charity"),
                    Triple(1, "🚀 Support Platform Creator", "donation_tab_creator")
                ).forEach { (idx, label, tag) ->
                    val selected = activeDonationTab == idx
                    val tabColor = if (idx == 0) CyberEmerald else RosePulse
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) tabColor.copy(alpha = 0.28f) else Color(0xFF1E293B),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) tabColor else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeDonationTab = idx }
                            .testTag(tag)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Amount Selector Chips ($5, $10, $25, $50, $100) + Custom Amount Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Contribution / Support Amount:",
                    style = MaterialTheme.typography.labelMedium,
                    color = SolarAmber,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5.0, 10.0, 25.0, 50.0, 100.0).forEach { amt ->
                        val isSelected = customAmountText.isBlank() && selectedAmountDollars == amt
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SolarAmber else Color(0xFF1E293B),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    customAmountText = ""
                                    selectedAmountDollars = amt
                                }
                                .testTag("donation_amount_chip_${amt.toInt()}")
                        ) {
                            Text(
                                text = "$${amt.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { customAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Or Custom Amount ($)") },
                        placeholder = { Text("e.g., 35") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.42f)
                            .testTag("donation_custom_amount_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SolarAmber,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                        )
                    )

                    OutlinedTextField(
                        value = dedicationNote,
                        onValueChange = { dedicationNote = it },
                        label = { Text("Dedication / Encouragement Note") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.58f)
                            .testTag("donation_dedication_note_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberEmerald,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                        )
                    )
                }
            }

            if (activeDonationTab == 0) {
                // TAB 0: DONATE TO A CHARITY OF YOUR CHOICE
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Choose a Verified Charity OR Enter Any Charity of Your Choice:",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VERIFIED_CHARITY_PRESETS.forEach { charity ->
                            val isSelected = customCharityName.isBlank() && selectedCharity.id == charity.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    customCharityName = ""
                                    selectedCharity = charity
                                },
                                label = {
                                    Text(
                                        text = "${charity.emoji} ${charity.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmerald.copy(alpha = 0.28f),
                                    selectedLabelColor = Color.White,
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("select_charity_${charity.id}")
                            )
                        }
                    }

                    // Custom Charity of Your Choice Inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customCharityName,
                            onValueChange = { customCharityName = it },
                            label = { Text("Or Type Any Charity Name of Your Choice") },
                            placeholder = { Text("e.g., Local Animal Shelter or Food Pantry") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_charity_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberEmerald,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                            )
                        )
                        OutlinedTextField(
                            value = customCharityUrl,
                            onValueChange = { customCharityUrl = it },
                            label = { Text("Charity Website (Optional)") },
                            placeholder = { Text("https://...") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.85f)
                                .testTag("custom_charity_url_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = HoloCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                            )
                        )
                    }

                    val targetCharityName = customCharityName.trim().ifBlank { selectedCharity.name }
                    val targetCharityUrl = customCharityUrl.trim().ifBlank { selectedCharity.officialDonateUrl }

                    // Active Charity Highlight Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🌍 Selected Charity: $targetCharityName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (customCharityName.isBlank()) {
                                    "${selectedCharity.category} • ${selectedCharity.missionSummary}"
                                } else {
                                    "Custom User-Selected Charity • 100% Direct Community Giving"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Official Portal: $targetCharityUrl",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = HoloCyan
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                pledgeHistory.add(
                                    0,
                                    DonationPledgeRecord(
                                        id = "don_${System.currentTimeMillis()}",
                                        recipientName = targetCharityName,
                                        recipientType = "Charity of Choice",
                                        amountDollars = effectiveAmount,
                                        eqcBonusAwarded = 35L,
                                        dedicationNote = dedicationNote
                                    )
                                )
                                onRecordGivingInBudget("Charity Donation: $targetCharityName", effectiveAmount)
                                onEquipSupporterBadge("🕊️")
                                feedbackBanner = "🎉 Recorded $${effectiveAmount.toInt()} donation to '$targetCharityName' & unlocked the 🕊️ Charity Humanitarian Badge!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color(0xFF052E16)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("donate_to_charity_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Donate $${effectiveAmount.toInt()} to Charity",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { openExternalUrl(targetCharityUrl) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HoloCyan),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("open_charity_portal_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Charity Portal",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Portal", color = HoloCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            } else {
                // TAB 1: HELP THE CREATOR OF THIS PLATFORM TO KEEP CREATING
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RosePulse.copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, RosePulse.copy(alpha = 0.75f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creator_support_mission_box")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = SolarAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "🚀 Help the Creator of Stages Of Life Keep Creating!",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "Your direct support helps the independent creator of Stages Of Life keep building new 3D themes, AI study tools, veteran/student resource directories, and keeping the platform free and accessible for everyone.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF8FAFC),
                                lineHeight = 17.sp
                            )
                            Text(
                                text = "• Creator Contact: allengrogan33@gmail.com • 509-552-3217",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = SolarAmber,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Creator Support Tier Presets
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "☕ $5 Creator Coffee" to 5.0,
                            "⚡ $15 6D Server Boost" to 15.0,
                            "🌟 $25 Studio Patron" to 25.0,
                            "👑 $50 Equality Visionary" to 50.0
                        ).forEach { (tierLabel, tierAmt) ->
                            FilterChip(
                                selected = customAmountText.isBlank() && selectedAmountDollars == tierAmt,
                                onClick = {
                                    customAmountText = ""
                                    selectedAmountDollars = tierAmt
                                },
                                label = { Text(tierLabel, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                pledgeHistory.add(
                                    0,
                                    DonationPledgeRecord(
                                        id = "creator_${System.currentTimeMillis()}",
                                        recipientName = "WereEqual Platform Creator Fund",
                                        recipientType = "Platform Creator",
                                        amountDollars = effectiveAmount,
                                        eqcBonusAwarded = 50L,
                                        dedicationNote = dedicationNote
                                    )
                                )
                                onRecordGivingInBudget("Creator Support: WereEqual Platform", effectiveAmount)
                                onEquipSupporterBadge("💖")
                                feedbackBanner = "🚀 Thank you! Your $${effectiveAmount.toInt()} Creator Support pledge is logged & you unlocked the 💖 Creator Patron Badge!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RosePulse,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("support_platform_creator_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Support Creator ($${effectiveAmount.toInt()})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                onTipEqualCoinsToCreatorOrCharity(
                                    "WereEqual Platform Creator",
                                    25L,
                                    dedicationNote
                                )
                                onEquipSupporterBadge("👑")
                                feedbackBanner = "🪙 Sent 25 EqualCoins (EQC) Creator Tip to help keep creating!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SolarAmber,
                                contentColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("tip_creator_eqc_button")
                        ) {
                            Text(
                                text = "🪙 Tip 25 EQC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                copyToClipboard("Creator Support URL", creatorSupportHttpsUrl)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, HoloCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_creator_support_url_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = HoloCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Creator Link", color = HoloCyan, style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                onShareDonationToWall(
                                    "💖 Supporting the Creator of WereEqual & Community Charities!",
                                    "I just supported the WereEqual Platform Creator ($${effectiveAmount.toInt()}) to help keep creating new 6D features! Support or donate here: $creatorSupportHttpsUrl"
                                )
                                feedbackBanner = "📣 Shared your Creator Support shout-out to the Community Wall!"
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CyberEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_creator_support_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Support", color = CyberEmerald, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !feedbackBanner.isNullOrBlank()) {
                if (!feedbackBanner.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberEmerald.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, CyberEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = feedbackBanner.orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Recent Community & Creator Support Wall
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
            Text(
                text = "🌟 Recent Charity & Creator Support Pledges",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pledgeHistory.take(3).forEach { pledge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${pledge.recipientName} (${pledge.recipientType})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "\"${pledge.dedicationNote}\"",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = String.format(Locale.US, "$%,.0f", pledge.amountDollars),
                            style = MaterialTheme.typography.labelMedium,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
