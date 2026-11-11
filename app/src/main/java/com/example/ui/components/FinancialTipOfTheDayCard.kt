package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

data class PersonaFinancialTip(
    val id: String,
    val personaKey: String,
    val personaLabel: String,
    val personaEmoji: String,
    val tipHeadline: String,
    val dailySnippet: String,
    val takeawayRule: String,
    val suggestedActionTitle: String,
    val suggestedSaveDollars: Double,
    val accentColor: Color
)

val PERSONA_FINANCIAL_TIPS_CATALOG: List<PersonaFinancialTip> = listOf(
    // VETERAN PERSONA TIPS
    PersonaFinancialTip(
        id = "vet_tip_1",
        personaKey = "veteran",
        personaLabel = "Veteran",
        personaEmoji = "🛡️",
        tipHeadline = "Maximize Post-9/11 GI Bill Housing & Zero-Down VA Benefits",
        dailySnippet = "Any tax-free VA disability compensation or GI Bill Monthly Housing Allowance (MHA) can supercharge your emergency reserve if you automate even 15% of it into a high-yield savings vault on the 1st of each month.",
        takeawayRule = "Rule of Thumb: Automate 15% of tax-free benefits into your Emergency Fund before discretionary spending.",
        suggestedActionTitle = "Veteran Automated Reserve Boost",
        suggestedSaveDollars = 150.0,
        accentColor = CyberEmerald
    ),
    PersonaFinancialTip(
        id = "vet_tip_2",
        personaKey = "veteran",
        personaEmoji = "🛡️",
        personaLabel = "Veteran",
        tipHeadline = "Eliminate Funding Fees & Audit Military-to-Civilian Subscriptions",
        dailySnippet = "When transitioning or refinancing with a VA-backed loan, even a 10% service-connected rating waives the VA funding fee—saving thousands that can stay in your family stability fund.",
        takeawayRule = "Rule of Thumb: Review VA fee waivers & VSO benefit audits once every 6 months.",
        suggestedActionTitle = "VA Benefit Fee Savings Deposit",
        suggestedSaveDollars = 100.0,
        accentColor = CyberEmerald
    ),

    // BUSINESS PERSONA TIPS
    PersonaFinancialTip(
        id = "biz_tip_1",
        personaKey = "business",
        personaLabel = "Business",
        personaEmoji = "💼",
        tipHeadline = "Separate Operating Cash Flow from Your Founder Tax & Reserve Vault",
        dailySnippet = "Profitable small businesses and creators set aside 25%–30% of every client invoice into a dedicated Tax & Capital Reserve immediately upon receipt—preventing quarterly cash crunches.",
        takeawayRule = "Rule of Thumb: Split every incoming revenue payment 70% Operating / 20% Tax Reserve / 10% Growth.",
        suggestedActionTitle = "Founder 10% Profit Reserve Transfer",
        suggestedSaveDollars = 250.0,
        accentColor = SolarAmber
    ),
    PersonaFinancialTip(
        id = "biz_tip_2",
        personaKey = "business",
        personaLabel = "Business",
        personaEmoji = "💼",
        tipHeadline = "Track Customer Acquisition Cost (CAC) vs. SBA Micro-Grant Capital",
        dailySnippet = "Before taking on high-interest credit lines, check non-dilutive SBA grants, local chamber micro-grants, and community partnerships that fund equipment or marketing with zero debt.",
        takeawayRule = "Rule of Thumb: Prioritize non-dilutive grants and positive unit economics over credit card debt.",
        suggestedActionTitle = "Business Capital Runway Boost",
        suggestedSaveDollars = 200.0,
        accentColor = SolarAmber
    ),

    // KID PERSONA TIPS
    PersonaFinancialTip(
        id = "kid_tip_1",
        personaKey = "kid",
        personaLabel = "Kid",
        personaEmoji = "🧒",
        tipHeadline = "The 3-Jar Super Saver Trick: Save, Spend & Share!",
        dailySnippet = "Whenever you earn allowance or birthday money, split it into 3 jars: 50% for Fun Goals (like a new game or art kit), 40% for Future Savings, and 10% for Sharing Kindness!",
        takeawayRule = "Rule of Thumb: Always pay your Future Self Jar first before buying a treat!",
        suggestedActionTitle = "Kid 3-Jar Future Self Deposit",
        suggestedSaveDollars = 15.0,
        accentColor = HoloCyan
    ),
    PersonaFinancialTip(
        id = "kid_tip_2",
        personaKey = "kid",
        personaLabel = "Kid",
        personaEmoji = "🧒",
        tipHeadline = "The 24-Hour Cool-Down Power-Up Before Buying",
        dailySnippet = "See a cool toy or game item you want right now? Wait 24 hours! If you still really want it tomorrow, you know it's a smart pick—and if not, you just saved your coins!",
        takeawayRule = "Rule of Thumb: Wait 1 full day before spending on impulse items.",
        suggestedActionTitle = "Smart Shopper Saved Coins",
        suggestedSaveDollars = 10.0,
        accentColor = HoloCyan
    ),

    // TEEN PERSONA TIPS
    PersonaFinancialTip(
        id = "teen_tip_1",
        personaKey = "teen",
        personaLabel = "Teen",
        personaEmoji = "🎒",
        tipHeadline = "Pay Yourself First from Every Summer Job or Creator Gig",
        dailySnippet = "Got paid from a part-time job, tutoring, or digital art commission? Move 30% straight into your College, Car, or Tech Goal before spending on food deliveries or subscriptions.",
        takeawayRule = "Rule of Thumb: Save 30% of every teen paycheck while your fixed bills are low.",
        suggestedActionTitle = "Teen First-Job 30% Auto-Save",
        suggestedSaveDollars = 45.0,
        accentColor = NeonPink
    ),
    PersonaFinancialTip(
        id = "teen_tip_2",
        personaKey = "teen",
        personaLabel = "Teen",
        personaEmoji = "🎒",
        tipHeadline = "Watch Out for 'Micro-Spend' App & Delivery Leaks",
        dailySnippet = "Five $8 snack or gaming micro-transactions a week add up to $160 a month—over $1,900 a year! Swapping just half of those into your savings vault funds a new laptop.",
        takeawayRule = "Rule of Thumb: Track weekly micro-spends so small leaks don't sink big goals.",
        suggestedActionTitle = "Teen Micro-Spend Swap to Savings",
        suggestedSaveDollars = 35.0,
        accentColor = NeonPink
    ),

    // ADULT PERSONA TIPS
    PersonaFinancialTip(
        id = "adult_tip_1",
        personaKey = "adult",
        personaLabel = "Adult",
        personaEmoji = "🌟",
        tipHeadline = "Master the 50/30/20 Rule & Build a 3-Month Stability Buffer",
        dailySnippet = "Allocate 50% of take-home pay to Needs (housing, groceries, utilities), 30% to Wants, and 20% to Savings & Debt Paydown until your 3–6 month Emergency Buffer is fully funded.",
        takeawayRule = "Rule of Thumb: Treat your 20% monthly savings transfer like a non-negotiable utility bill.",
        suggestedActionTitle = "50/30/20 Monthly Buffer Deposit",
        suggestedSaveDollars = 125.0,
        accentColor = HoloCyan
    ),
    PersonaFinancialTip(
        id = "adult_tip_2",
        personaKey = "adult",
        personaLabel = "Adult",
        personaEmoji = "🌟",
        tipHeadline = "High-Interest Debt Snowball vs. Avalanche Strategy",
        dailySnippet = "Paying off highest-APR balances first (the Avalanche method) guarantees the highest mathematical return on your cash flow, freeing up monthly surplus for long-term investing.",
        takeawayRule = "Rule of Thumb: Redirect every paid-off bill's monthly payment straight into savings.",
        suggestedActionTitle = "Debt-Free Snowball Boost",
        suggestedSaveDollars = 100.0,
        accentColor = HoloCyan
    ),

    // STUDENT / CREATOR PERSONA TIPS
    PersonaFinancialTip(
        id = "student_tip_1",
        personaKey = "creator",
        personaLabel = "Student",
        personaEmoji = "🎓",
        tipHeadline = "Stack FAFSA Grants, OpenLibrary Textbooks & Student Discounts",
        dailySnippet = "Never pay full retail for college textbooks or software—use OpenLibrary.org, campus library reserves, and verified `.edu` discounts to save $600–$1,200 every semester.",
        takeawayRule = "Rule of Thumb: Always check free open-access textbooks & Pell Grant eligibility first.",
        suggestedActionTitle = "Semester Textbook & Grant Savings",
        suggestedSaveDollars = 75.0,
        accentColor = CyberEmerald
    ),
    PersonaFinancialTip(
        id = "student_tip_2",
        personaKey = "creator",
        personaLabel = "Student",
        personaEmoji = "🎓",
        tipHeadline = "Avoid Unsubsidized Loan Interest Creep During School",
        dailySnippet = "Even paying $25/month toward accruing interest while in school prevents interest capitalization at graduation—saving hundreds over the life of a student loan.",
        takeawayRule = "Rule of Thumb: Prefer grants, scholarships, and work-study over unsubsidized borrowing.",
        suggestedActionTitle = "Student Interest Shield Deposit",
        suggestedSaveDollars = 50.0,
        accentColor = CyberEmerald
    )
)

fun normalizePersonaKeyForFinancialTips(role: String, age: Long = 26L): String {
    val clean = role.lowercase().trim()
    return when {
        clean == "kid" || (clean == "adult" && age <= 15L) -> "kid"
        clean == "teen" || (clean == "adult" && age in 16L..17L) -> "teen"
        clean == "veteran" -> "veteran"
        clean == "business" -> "business"
        clean == "creator" || clean == "student" -> "creator"
        else -> "adult"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialTipOfTheDayCard(
    userRole: String,
    userAge: Long = 26L,
    onApplyTipSavingsAction: (title: String, amountDollars: Double) -> Unit = { _, _ -> },
    onShareTipToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val defaultPersonaKey = remember(userRole, userAge) {
        normalizePersonaKeyForFinancialTips(userRole, userAge)
    }
    var selectedPersonaKey by remember(defaultPersonaKey) { mutableStateOf(defaultPersonaKey) }
    var tipIndexOffset by remember { mutableIntStateOf(0) }
    var actionFeedback by remember { mutableStateOf<String?>(null) }

    val matchingTips = remember(selectedPersonaKey) {
        PERSONA_FINANCIAL_TIPS_CATALOG.filter { it.personaKey == selectedPersonaKey }
            .ifEmpty { PERSONA_FINANCIAL_TIPS_CATALOG }
    }
    val activeTip = matchingTips[tipIndexOffset % matchingTips.size]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("financial_tip_of_the_day_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B132B).copy(alpha = 0.94f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(activeTip.accentColor, SolarAmber, CyberEmerald, HoloCyan)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(activeTip.accentColor.copy(alpha = 0.22f))
                            .border(1.5.dp, activeTip.accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Financial Tip of the Day",
                            tint = SolarAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "💡 FINANCIAL TIP OF THE DAY • ${activeTip.personaEmoji} ${activeTip.personaLabel.uppercase()} PERSONA",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = activeTip.tipHeadline,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        tipIndexOffset = (tipIndexOffset + 1) % matchingTips.size
                        actionFeedback = null
                    },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, activeTip.accentColor),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("next_financial_tip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Next Financial Tip",
                        tint = activeTip.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Next Tip",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Persona Filter Chips (Auto-synced to user's selected persona, with 1-tap preview for all personas)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("veteran", "🛡️ Veteran", CyberEmerald),
                    Triple("business", "💼 Business", SolarAmber),
                    Triple("kid", "🧒 Kid", HoloCyan),
                    Triple("teen", "🎒 Teen", NeonPink),
                    Triple("adult", "🌟 Adult", HoloCyan),
                    Triple("creator", "🎓 Student", CyberEmerald)
                ).forEach { (key, label, color) ->
                    val selected = selectedPersonaKey == key
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedPersonaKey = key
                            tipIndexOffset = 0
                            actionFeedback = null
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.28f),
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("financial_tip_persona_chip_$key")
                    )
                }
            }

            // Daily Helpful Snippet Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.88f),
                border = BorderStroke(1.dp, activeTip.accentColor.copy(alpha = 0.55f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("financial_tip_snippet_box")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = activeTip.dailySnippet,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF8FAFC),
                        lineHeight = 18.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = activeTip.accentColor.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "📌 ${activeTip.takeawayRule}",
                            style = MaterialTheme.typography.labelSmall,
                            color = activeTip.accentColor,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Action Buttons: Apply Tip Savings Boost + Share Tip to Wall
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onApplyTipSavingsAction(activeTip.suggestedActionTitle, activeTip.suggestedSaveDollars)
                        actionFeedback = "✅ Logged '${activeTip.suggestedActionTitle}' (+$${activeTip.suggestedSaveDollars.toInt()}) to your Savings Vault!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = activeTip.accentColor,
                        contentColor = Color(0xFF0F172A)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("apply_financial_tip_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save +$${activeTip.suggestedSaveDollars.toInt()} (${activeTip.personaLabel})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        onShareTipToWall(
                            "💡 ${activeTip.personaEmoji} ${activeTip.personaLabel} Financial Tip: ${activeTip.tipHeadline}",
                            "${activeTip.dailySnippet}\n• ${activeTip.takeawayRule}"
                        )
                        actionFeedback = "📣 Shared today's ${activeTip.personaLabel} Financial Tip to the Community Wall!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SolarAmber),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("share_financial_tip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Financial Tip",
                        tint = SolarAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share Tip",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            AnimatedVisibility(visible = !actionFeedback.isNullOrBlank()) {
                if (!actionFeedback.isNullOrBlank()) {
                    Text(
                        text = actionFeedback.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
