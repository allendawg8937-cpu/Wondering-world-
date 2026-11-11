package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReportContentDialog(
    targetId: String,
    targetType: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Spam") }
    var detail by remember { mutableStateOf("") }
    val reasons = listOf("Spam", "Harassment", "Inappropriate", "Violence", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Content", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Why are you reporting this $targetType?", style = MaterialTheme.typography.bodyMedium)
                
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(reason, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    label = { Text("Additional Details (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(targetId, targetType, selectedReason, detail)
                    onDismiss()
                }
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
