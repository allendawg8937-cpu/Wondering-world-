package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.RosePulse

data class ReportingReasonOption(
    val label: String,
    val description: String
)

val STANDARD_REPORTING_REASONS = listOf(
    ReportingReasonOption(
        label = "Harassment",
        description = "Bullying, targeted hostility, intimidation, or personal attacks"
    ),
    ReportingReasonOption(
        label = "Spam",
        description = "Unsolicited promotions, scam links, or repetitive bot activity"
    ),
    ReportingReasonOption(
        label = "Inappropriate Content",
        description = "Offensive media, mature/18+ violations, or unsafe material"
    ),
    ReportingReasonOption(
        label = "Equality Charter Violation",
        description = "Hate speech or discrimination against any life stage or group"
    ),
    ReportingReasonOption(
        label = "Other",
        description = "Another platform safety concern requiring administrator review"
    )
)

@Composable
fun ReportContentDialog(
    targetId: String,
    targetType: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    val initialType = remember(targetType) {
        when {
            targetType.contains("profile", ignoreCase = true) || targetType.contains("user", ignoreCase = true) -> "Profile"
            targetType.contains("comment", ignoreCase = true) -> "Comment"
            else -> "Post"
        }
    }
    var selectedTargetType by remember(initialType) { mutableStateOf(initialType) }
    var selectedReason by remember { mutableStateOf("Harassment") }
    var detail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "Report $selectedTargetType",
                tint = RosePulse,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Flag $selectedTargetType for Admin Review",
                    fontWeight = FontWeight.ExtraBold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = HoloCyan.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "Target $selectedTargetType: $targetId",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.testTag("report_content_dialog")
            ) {
                // Target Type Selector: Post, Comment, or Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Post", "Comment", "Profile").forEach { typeOption ->
                        FilterChip(
                            selected = selectedTargetType == typeOption,
                            onClick = { selectedTargetType = typeOption },
                            label = {
                                Text(
                                    text = typeOption,
                                    fontWeight = if (selectedTargetType == typeOption) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("report_dialog_type_${typeOption.lowercase()}")
                        )
                    }
                }

                Text(
                    text = "Select a category for flagging this ${selectedTargetType.lowercase()} for review by administrators:",
                    style = MaterialTheme.typography.bodySmall
                )

                STANDARD_REPORTING_REASONS.forEach { option ->
                    val isSelected = selectedReason == option.label
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) {
                            RosePulse.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) RosePulse else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = option.label }
                            .testTag("report_reason_option_${option.label.lowercase().replace(' ', '_')}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedReason = option.label }
                            )
                            Column(modifier = Modifier.padding(start = 6.dp)) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    label = { Text("Additional Notes for Administrators (Optional)") },
                    placeholder = { Text("Describe the harassment, spam, or policy violation...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_dialog_details_input"),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanDetail = detail.trim().ifBlank {
                        "Flagged $selectedTargetType ($targetId) for administrator review: $selectedReason"
                    }
                    ModerationReportQueueManager.submitReport(
                        targetId = targetId,
                        targetType = selectedTargetType,
                        contentPreview = "Flagged $selectedTargetType ($targetId)",
                        authorLabel = targetId,
                        reason = selectedReason,
                        reporterNotes = cleanDetail
                    )
                    onSubmit(targetId, selectedTargetType.lowercase(), selectedReason, cleanDetail)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosePulse,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("report_dialog_submit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Flag for Admin Review", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("report_dialog_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
