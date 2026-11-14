package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.dp
import com.example.data.export.UserDataExportBuilder
import com.example.data.export.UserDataExportPayload
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

/**
 * Profile Settings 'Download Data' card that aggregates financial goal data
 * (savings goals, budget entries, career milestones, EqualCoins ledger) and
 * user preferences (theme, appearance, interests, life-stage progress, 3D avatar configs)
 * into a downloadable JSON file using Android's zero-permission Storage Access Framework.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDownloadDataCard(
    exportPayload: UserDataExportPayload,
    onStatusMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showJsonPreview by rememberSaveable { mutableStateOf(false) }
    var statusBanner by rememberSaveable { mutableStateOf<String?>(null) }

    val aggregatedJson = remember(exportPayload) {
        UserDataExportBuilder.buildJsonString(exportPayload)
    }
    val suggestedFileName = remember(exportPayload.profile.displayName) {
        UserDataExportBuilder.defaultExportFileName(
            exportPayload.profile.displayName.ifBlank { exportPayload.signUpForm.displayName }
        )
    }
    val jsonSizeKb = remember(aggregatedJson) {
        val bytes = aggregatedJson.toByteArray(StandardCharsets.UTF_8).size
        String.format(java.util.Locale.US, "%.1f KB", bytes / 1024.0)
    }

    // Zero-Permission Storage Access Framework JSON File Saver
    val createJsonDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { targetUri: Uri? ->
        if (targetUri == null) {
            statusBanner = "Download cancelled — tap 'Download JSON File' whenever you're ready."
            return@rememberLauncherForActivityResult
        }
        coroutineScope.launch {
            val writeResult = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                        outputStream.write(aggregatedJson.toByteArray(StandardCharsets.UTF_8))
                        outputStream.flush()
                    } ?: error("Unable to open destination file stream")
                }
            }
            writeResult.fold(
                onSuccess = {
                    val msg = "✓ Downloaded $suggestedFileName ($jsonSizeKb) successfully!"
                    statusBanner = msg
                    onStatusMessage(msg)
                },
                onFailure = { err ->
                    val msg = "Could not save JSON file: ${err.localizedMessage ?: "Unknown error"}"
                    statusBanner = msg
                    onStatusMessage(msg)
                }
            )
        }
    }

    fun copyJsonToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Stages Of Life User Data JSON", aggregatedJson))
        val msg = "✓ Copied aggregated JSON ($jsonSizeKb) to clipboard!"
        statusBanner = msg
        onStatusMessage(msg)
    }

    fun shareJsonExport() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, suggestedFileName)
            putExtra(Intent.EXTRA_TEXT, aggregatedJson)
        }
        val chooser = Intent.createChooser(sendIntent, "Export User Data (JSON)").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(chooser) }
        val msg = "✓ Opened share sheet with your JSON export."
        statusBanner = msg
        onStatusMessage(msg)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_download_data_card")
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(HoloCyan, CyberEmerald))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataObject,
                            contentDescription = "Download Data JSON",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Download Data (JSON Export)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Aggregates financial goals & user preferences • $jsonSizeKb",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald)
                ) {
                    Text(
                        text = ".JSON",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberEmerald,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // What's Included Summary Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val prefCount = exportPayload.learnedInterests.size +
                    exportPayload.selectedSportsIds.size +
                    exportPayload.signUpForm.selectedInterests.size
                listOf(
                    "💰 Goal: $${exportPayload.profile.savingsGoalCents / 100}",
                    "📒 ${exportPayload.budgetEntries.size} Budget Items",
                    "🪙 ${exportPayload.virtualCurrencyBalance} EQC",
                    "🎛️ ${exportPayload.personaStageDataMap.size.coerceAtLeast(1)} Life Stages",
                    "⚙️ $prefCount Preferences",
                    "🧊 ${exportPayload.profile3DConfigs.size} 3D Looks"
                ).forEach { badgeText ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Primary Action: Download JSON File + Secondary Actions: Preview, Copy, Share
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { createJsonDocumentLauncher.launch(suggestedFileName) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("download_data_json_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download JSON File",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Download JSON File",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = { showJsonPreview = !showJsonPreview },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("preview_export_json_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Preview JSON",
                        tint = HoloCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showJsonPreview) "Hide JSON" else "Preview JSON",
                        style = MaterialTheme.typography.labelMedium,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { copyJsonToClipboard() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SolarAmber),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("copy_export_json_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy JSON",
                        tint = SolarAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { shareJsonExport() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("share_export_json_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share JSON",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = showJsonPreview) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0A101E),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_json_preview_box")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = aggregatedJson,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            if (!statusBanner.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("download_data_status_banner")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = statusBanner!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
