package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber

data class ConductRuleItem(
    val number: String,
    val title: String,
    val summary: String
)

val PROFILE_CODE_OF_CONDUCT_RULES = listOf(
    ConductRuleItem(
        number = "1",
        title = "No Bullying",
        summary = "Zero tolerance for bullying, cyberbullying, intimidation, or mistreating anyone on the platform."
    ),
    ConductRuleItem(
        number = "2",
        title = "No Racist Profiles or Disrespectful Content",
        summary = "No race-hate profiles, no posting bad content that is racist or disrespectful. We are all equal."
    ),
    ConductRuleItem(
        number = "3",
        title = "No Sexual Misconduct & Strict 18+ Age Rule",
        summary = "Zero sexual misconduct. Mature/adult photos or 18+ lounge spaces are strictly prohibited unless you are verified 18 years or older."
    ),
    ConductRuleItem(
        number = "4",
        title = "No Selling Your Information",
        summary = "We never sell your personal information, and users may never harvest, trade, or sell anyone's private data."
    ),
    ConductRuleItem(
        number = "5",
        title = "Automatic Account Deletion",
        summary = "If you do any of this, your account is automatically and permanently deleted immediately."
    )
)

@Composable
fun ProfileCodeOfConductAgreementCard(
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
    checkboxTestTag: String = "signup_pledge_checkbox",
    cardTestTag: String = "profile_code_of_conduct_card",
    modifier: Modifier = Modifier
) {
    val accentColor = if (accepted) CyberEmerald else SolarAmber

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = accentColor.copy(alpha = 0.85f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(cardTestTag)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (accepted) Icons.Default.CheckCircle else Icons.Default.Gavel,
                        contentDescription = "Code of Conduct",
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Simple Code of Conduct (Required)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "You must agree to these rules to create or save a profile page",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = accentColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, accentColor),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (accepted) "✓ AGREED" else "MUST AGREE",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 5 Simple Code of Conduct Rules
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PROFILE_CODE_OF_CONDUCT_RULES.forEach { rule ->
                        val isAutoDeleteRule = rule.number == "5"
                        val ruleAccent = if (isAutoDeleteRule) RosePulse else HoloCyan
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = ruleAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, ruleAccent.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = rule.number,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ruleAccent,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rule.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isAutoDeleteRule) RosePulse else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = rule.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Automatic Account Deletion Enforcement Banner
            Surface(
                color = RosePulse.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = RosePulse,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "STRICT ENFORCEMENT: No bullying • No racist profiles • No sexual misconduct • No nudity/18+ content under 18 • No racist/disrespectful posts • No selling information. Violating any rule results in AUTOMATIC ACCOUNT DELETION.",
                        style = MaterialTheme.typography.labelSmall,
                        color = RosePulse,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Checkbox Row (Mandatory Agreement)
            Surface(
                color = accentColor.copy(alpha = 0.12f),
                border = BorderStroke(1.5.dp, accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAcceptedChange(!accepted) }
                    .testTag("code_of_conduct_agreement_row")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = onAcceptedChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = CyberEmerald,
                            checkmarkColor = Color.Black,
                            uncheckedColor = SolarAmber
                        ),
                        modifier = Modifier.testTag(checkboxTestTag)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "I Agree to the Simple Code of Conduct (Required to Create Profile)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (accepted) CyberEmerald else SolarAmber
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (accepted) {
                                "Agreed: No bullying, no racist profiles/posts, no sexual misconduct, 18+ rule respected, no selling info, or automatic account deletion."
                            } else {
                                "Tap to check this box and agree to the Code of Conduct before creating your profile page."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
