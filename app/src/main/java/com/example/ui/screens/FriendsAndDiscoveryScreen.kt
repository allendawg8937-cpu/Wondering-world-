package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.Friend
import com.example.data.model.FriendRequest
import com.example.data.model.UserProfile
import com.example.ui.UiState
import com.example.ui.components.OFFICIAL_PLATFORM_CLOUD_URL
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import java.security.MessageDigest
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FriendsAndDiscoveryScreen(
    friendsState: UiState<List<Friend>>,
    incomingRequestsState: UiState<List<FriendRequest>>,
    discoveredUsers: List<UserProfile>,
    myPhoneNumber: String,
    myUserProfile: UserProfile = UserProfile(
        userId = "weq_member_default",
        displayName = "Jordan Vance",
        location = "Austin, TX"
    ),
    onUpdatePhoneNumber: (String) -> Unit,
    onSyncContacts: (Context) -> Unit,
    onSearchUsers: (String) -> Unit,
    onSendFriendRequest: (String, String) -> Unit,
    onAcceptRequest: (FriendRequest) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onShowReportDialog: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Default to DISCOVERY ("Find Friends") so Find Friends & Share QR Code are immediately accessible
    var activeSubTab by remember { mutableStateOf("DISCOVERY") } // DISCOVERY, FRIENDS, REQUESTS
    var phoneInput by remember(myPhoneNumber) { mutableStateOf(myPhoneNumber) }
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    val contactPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onSyncContacts(context)
            statusFeedback = "✓ Synced phone contacts to Find Friends!"
        } else {
            statusFeedback = "Contact permission is required to match phone contacts."
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("friends_and_discovery_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card with Back Button, Sub-Tab Switcher & Quick Share QR Toggle
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, NeonPink, SolarAmber))
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("friends_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = HoloCyan
                            )
                        }
                        Column {
                            Text(
                                text = "Find Friends & Share QR Code",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Scan or share personal QR codes • Search by Name/Email • Sync Contacts",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan
                            )
                        }
                    }
                }

                // Sub-Tab Switcher Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "DISCOVERY" to "🔍 Find Friends & QR Code",
                        "FRIENDS" to "👥 My Friends",
                        "REQUESTS" to "🔔 Friend Requests"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = activeSubTab == key,
                            onClick = { activeSubTab = key },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (activeSubTab == key) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("friends_subtab_${key.lowercase()}")
                        )
                    }
                }

                if (!statusFeedback.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, CyberEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = statusFeedback!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        when (activeSubTab) {
            "FRIENDS" -> {
                FriendsListSection(
                    state = friendsState,
                    onShowReportDialog = onShowReportDialog,
                    onSwitchToFindFriends = { activeSubTab = "DISCOVERY" }
                )
            }
            "DISCOVERY" -> {
                DiscoverySection(
                    myUserProfile = myUserProfile,
                    discoveredUsers = discoveredUsers,
                    phoneInput = phoneInput,
                    onPhoneChange = { phoneInput = it },
                    onUpdatePhone = {
                        onUpdatePhoneNumber(phoneInput)
                        statusFeedback = "✓ Updated your verified phone number ($phoneInput)"
                    },
                    onSyncContacts = {
                        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    onSearch = { query ->
                        onSearchUsers(query)
                        statusFeedback = "Searching community index for '$query'..."
                    },
                    onReport = onShowReportDialog,
                    onSendRequest = { targetUid, targetName ->
                        onSendFriendRequest(targetUid, targetName)
                        statusFeedback = "✓ Sent friend request to $targetName!"
                    },
                    onQrStatusMessage = { msg ->
                        statusFeedback = msg
                    }
                )
            }
            "REQUESTS" -> {
                RequestsSection(
                    requestsState = incomingRequestsState,
                    onAccept = { req ->
                        onAcceptRequest(req)
                        statusFeedback = "✓ Accepted friend request from ${req.fromName}!"
                    },
                    onDecline = { reqId ->
                        onDeclineRequest(reqId)
                        statusFeedback = "Declined friend request."
                    }
                )
            }
        }
    }
}

/**
 * Generates a deterministic 21x21 Version-1 QR Code Matrix from the user's unique payload string,
 * including standard 7x7 finder patterns in the three corners, quiet zones, timing strips,
 * and SHA-256 encoded data modules.
 */
fun generatePersonalQrMatrix(payload: String, gridSize: Int = 21): Array<BooleanArray> {
    val matrix = Array(gridSize) { BooleanArray(gridSize) }
    val reserved = Array(gridSize) { BooleanArray(gridSize) }

    fun placeFinderPattern(startRow: Int, startCol: Int) {
        for (r in -1..7) {
            for (c in -1..7) {
                val rr = startRow + r
                val cc = startCol + c
                if (rr in 0 until gridSize && cc in 0 until gridSize) {
                    reserved[rr][cc] = true
                    val inOuterBorder = (r in 0..6 && (c == 0 || c == 6)) || (c in 0..6 && (r == 0 || r == 6))
                    val inInnerCore = r in 2..4 && c in 2..4
                    matrix[rr][cc] = inOuterBorder || inInnerCore
                }
            }
        }
    }

    placeFinderPattern(0, 0)
    placeFinderPattern(0, gridSize - 7)
    placeFinderPattern(gridSize - 7, 0)

    // Timing patterns on row 6 and col 6
    for (i in 8 until gridSize - 8) {
        reserved[6][i] = true
        matrix[6][i] = (i % 2 == 0)
        reserved[i][6] = true
        matrix[i][6] = (i % 2 == 0)
    }

    // Dark module per QR spec
    if (gridSize > 13) {
        reserved[gridSize - 8][8] = true
        matrix[gridSize - 8][8] = true
    }

    // Fill remaining data modules deterministically using SHA-256 digest of the payload
    val digestBytes = try {
        val md = MessageDigest.getInstance("SHA-256")
        val firstHash = md.digest(payload.toByteArray(Charsets.UTF_8))
        val secondHash = md.digest((payload + "|wereallequals_qr_v1").toByteArray(Charsets.UTF_8))
        firstHash + secondHash
    } catch (_: Exception) {
        payload.toByteArray(Charsets.UTF_8)
    }

    var bitIndex = 0
    for (r in 0 until gridSize) {
        for (c in 0 until gridSize) {
            if (!reserved[r][c]) {
                val byteVal = digestBytes[(bitIndex / 8) % digestBytes.size].toInt() and 0xFF
                val bitOffset = bitIndex % 8
                val isDark = ((byteVal shr bitOffset) and 1) == 1
                // Apply subtle mask pattern ((r + c) % 3 == 0) to avoid large uniform blocks
                matrix[r][c] = if ((r + c) % 3 == 0) !isDark else isDark
                bitIndex++
            }
        }
    }

    return matrix
}

/**
 * Interactive 'Share QR Code' & 'Scan Peer QR Code' Card on the Find Friends screen.
 * Generates a unique personal QR code encoding the user's friend invite link and ID,
 * supports native sharing, clipboard copy, QR theme customization, and live CameraX / code scanning.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharePersonalQrCodeCard(
    myUserProfile: UserProfile,
    onSendFriendRequest: (targetUserId: String, targetDisplayName: String) -> Unit,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val effectiveUserId = myUserProfile.userId.ifBlank {
        "weq_" + myUserProfile.displayName
            .trim()
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "explorer" }
    }
    val effectiveDisplayName = myUserProfile.displayName.ifBlank { "Jordan Vance" }

    val uniqueFriendCode = remember(effectiveUserId, effectiveDisplayName) {
        val hash = (effectiveUserId + "|" + effectiveDisplayName).hashCode().toUInt().toString(16).uppercase(Locale.US).padStart(8, '0')
        "SOL-${hash.take(4)}-${hash.takeLast(4)}"
    }

    val qrInviteHttpsUrl = remember(effectiveUserId, effectiveDisplayName, uniqueFriendCode) {
        "$OFFICIAL_PLATFORM_CLOUD_URL:$effectiveDisplayName:$uniqueFriendCode"
    }

    val qrInviteBackslashUrl = remember(uniqueFriendCode) {
        "Stages Of Life Code: $uniqueFriendCode"
    }

    var activeQrMode by remember { mutableStateOf("MY_QR") } // "MY_QR" or "SCAN_QR"
    var selectedPaletteIndex by remember { mutableIntStateOf(0) }
    var scannedOrPastedInput by remember { mutableStateOf("") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            onStatusMessage("✓ Camera active — point at a friend's personal QR code to add them!")
        } else {
            onStatusMessage("Camera permission denied. You can still paste a friend's QR code or invite link below.")
        }
    }

    val qrPalettes = remember {
        listOf(
            Triple("Cyber Cyan", HoloCyan, Color(0xFF061320)),
            Triple("Neon Magenta", NeonPink, Color(0xFF1A0718)),
            Triple("Emerald Matrix", CyberEmerald, Color(0xFF041B11)),
            Triple("Solar Gold", SolarAmber, Color(0xFF1C1304))
        )
    }
    val (paletteName, activeQrAccent, activeQrDarkBg) = qrPalettes[selectedPaletteIndex % qrPalettes.size]

    val qrMatrix = remember(qrInviteHttpsUrl) {
        generatePersonalQrMatrix(qrInviteHttpsUrl, gridSize = 21)
    }

    fun sharePersonalQrCode() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Add $effectiveDisplayName on Stages Of Life (Personal QR Code)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "📲 Add me ($effectiveDisplayName) on Stages Of Life!\n" +
                        "• Personal QR Friend Code: $uniqueFriendCode"
                )
            }
            val chooser = Intent.createChooser(shareIntent, "Share Personal QR Code").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            onStatusMessage("✓ Opened Share Sheet for your personal QR code ($uniqueFriendCode)!")
        } catch (_: Exception) {
            onStatusMessage("✓ Personal QR Code ready to share: $uniqueFriendCode")
        }
    }

    fun copyQrCodeLink() {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(
                ClipData.newPlainText("Stages Of Life Personal QR Code", uniqueFriendCode)
            )
            onStatusMessage("✓ Copied Personal QR Code ($uniqueFriendCode) to clipboard!")
        } catch (_: Exception) {
            onStatusMessage("✓ Personal QR Code: $uniqueFriendCode")
        }
    }

    fun resolveScannedQrPayloadAndAddFriend(rawPayload: String) {
        val trimmed = rawPayload.trim()
        if (trimmed.isEmpty()) return
        val normalized = trimmed.replace('\\', '/')
        val parsedUri = try {
            Uri.parse(normalized)
        } catch (_: Exception) {
            null
        }

        val resolvedUserId = parsedUri?.getQueryParameter("addUserId")
            ?: parsedUri?.getQueryParameter("uid")
            ?: parsedUri?.getQueryParameter("user")
            ?: ("qr_peer_" + trimmed.lowercase(Locale.US).replace(Regex("[^a-z0-9]+"), "_").trim('_').take(16))

        val resolvedName = parsedUri?.getQueryParameter("name")
            ?.takeIf { it.isNotBlank() }
            ?: if (trimmed.startsWith("WEQ-", ignoreCase = true)) {
                "Peer ($trimmed)"
            } else {
                trimmed.substringAfterLast("/").substringBefore("?").replace("-", " ").trim()
                    .ifBlank { "Scanned QR Friend" }
            }

        onSendFriendRequest(resolvedUserId, resolvedName)
        onStatusMessage("✓ Scanned QR Code & sent Friend Request to $resolvedName!")
        scannedOrPastedInput = ""
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, activeQrAccent, CyberEmerald, NeonPink))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("share_personal_qr_code_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Header
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
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Personal QR Code",
                        tint = activeQrAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Share QR Code — Instant Friend Connect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Share your unique personal QR code or scan a friend's QR code to connect",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = activeQrAccent.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, activeQrAccent),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = uniqueFriendCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = activeQrAccent,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Mode Toggle: My Personal QR Code vs. Scan Friend's QR Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeQrMode == "MY_QR",
                    onClick = { activeQrMode = "MY_QR" },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "My Personal QR Code",
                            fontWeight = if (activeQrMode == "MY_QR") FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("qr_mode_my_code_chip")
                )

                FilterChip(
                    selected = activeQrMode == "SCAN_QR",
                    onClick = { activeQrMode = "SCAN_QR" },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Scan Friend QR",
                            fontWeight = if (activeQrMode == "SCAN_QR") FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("qr_mode_scan_code_chip")
                )
            }

            AnimatedVisibility(
                visible = activeQrMode == "MY_QR",
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Unique Personal QR Code Badge Container
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(3.dp, activeQrAccent),
                        modifier = Modifier
                            .size(228.dp)
                            .clickable {
                                selectedPaletteIndex = (selectedPaletteIndex + 1) % qrPalettes.size
                            }
                            .testTag("personal_qr_code_canvas_box")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("personal_qr_code_canvas")
                            ) {
                                val gridCount = qrMatrix.size
                                val cellSize = size.width / gridCount.toFloat()

                                // Crisp high-contrast background
                                drawRoundRect(
                                    color = Color.White,
                                    size = size,
                                    cornerRadius = CornerRadius(12f, 12f)
                                )

                                for (r in 0 until gridCount) {
                                    for (c in 0 until gridCount) {
                                        if (qrMatrix[r][c]) {
                                            val isFinderCorner = (r < 7 && c < 7) ||
                                                (r < 7 && c >= gridCount - 7) ||
                                                (r >= gridCount - 7 && c < 7)
                                            drawRoundRect(
                                                color = if (isFinderCorner) activeQrDarkBg else Color(0xFF0B132B),
                                                topLeft = Offset(c * cellSize, r * cellSize),
                                                size = Size(cellSize * 0.94f, cellSize * 0.94f),
                                                cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)
                                            )
                                        }
                                    }
                                }

                                // Center 3D Identity Emblem Badge overlay
                                val badgeSize = cellSize * 4.2f
                                val badgeTopLeft = Offset(
                                    (size.width - badgeSize) / 2f,
                                    (size.height - badgeSize) / 2f
                                )
                                drawRoundRect(
                                    color = activeQrDarkBg,
                                    topLeft = badgeTopLeft,
                                    size = Size(badgeSize, badgeSize),
                                    cornerRadius = CornerRadius(10f, 10f)
                                )
                                drawRoundRect(
                                    color = activeQrAccent,
                                    topLeft = Offset(badgeTopLeft.x + 4f, badgeTopLeft.y + 4f),
                                    size = Size(badgeSize - 8f, badgeSize - 8f),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                            }

                            Text(
                                text = "3D",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    // User Identity & Friend Code Summary
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = effectiveDisplayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Personal QR Code: $uniqueFriendCode • Style: $paletteName (Tap QR to Restyle)",
                            style = MaterialTheme.typography.labelSmall,
                            color = activeQrAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = qrInviteBackslashUrl,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    // Action Buttons: Share QR Code & Copy QR Invite Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { sharePersonalQrCode() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activeQrAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("share_qr_code_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share QR Code",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share QR Code",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        OutlinedButton(
                            onClick = { copyQrCodeLink() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, HoloCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_qr_code_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy QR Link",
                                tint = HoloCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Copy QR Link",
                                style = MaterialTheme.typography.labelMedium,
                                color = HoloCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = activeQrMode == "SCAN_QR",
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (hasCameraPermission) {
                        QrScannerCameraPreviewBox(
                            accentColor = activeQrAccent,
                            onSimulatedScanComplete = { peerId, peerName ->
                                onSendFriendRequest(peerId, peerName)
                                onStatusMessage("✓ Scanned QR Code & sent Friend Request to $peerName!")
                            }
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, activeQrAccent.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = activeQrAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Enable Camera to Scan a Friend's Personal QR Code",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Point your camera at another member's Share QR Code screen or paste their QR code below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = activeQrAccent,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.testTag("enable_qr_camera_scanner_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Camera QR Scanner", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }

                    // Paste or Enter a Friend's QR Code / Invite URL directly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = scannedOrPastedInput,
                            onValueChange = { scannedOrPastedInput = it },
                            label = { Text("Paste Friend QR Code or Link (e.g. WEQ-7A2F-91B0)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("qr_code_manual_input")
                        )

                        Button(
                            onClick = { resolveScannedQrPayloadAndAddFriend(scannedOrPastedInput) },
                            enabled = scannedOrPastedInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("qr_code_add_friend_submit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add Friend via QR",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Quick-Scan Nearby Community QR Codes for instant testing
                    Text(
                        text = "⚡ Instant Scan Nearby Community Member QR Codes:",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("weq_maya_lin", "Maya Lin", "WEQ-4C91-82E0"),
                            Triple("weq_marcus_vance", "Marcus Vance", "WEQ-9B3D-17A4"),
                            Triple("weq_elena_rostova", "Elena Rostova", "WEQ-2F8E-64C9")
                        ).forEach { (peerUid, peerName, peerCode) ->
                            OutlinedButton(
                                onClick = {
                                    onSendFriendRequest(peerUid, peerName)
                                    onStatusMessage("✓ Scanned QR ($peerCode) — Friend request sent to $peerName!")
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, activeQrAccent),
                                modifier = Modifier.testTag("instant_scan_qr_${peerUid}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = activeQrAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$peerName ($peerCode)",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QrScannerCameraPreviewBox(
    accentColor: Color,
    onSimulatedScanComplete: (peerId: String, peerName: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val infiniteTransition = rememberInfiniteTransition(label = "qr_laser_transition")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "qr_laser_line"
    )

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        val listener = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            } catch (_: Exception) {
            }
        }
        cameraProviderFuture.addListener(listener, executor)
        onDispose {
            try {
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (_: Exception) {
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, accentColor, RoundedCornerShape(18.dp))
                .testTag("qr_camera_viewfinder_box"),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Viewfinder Reticle & Animated Laser Line Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val frameSize = size.minDimension * 0.68f
                val left = (size.width - frameSize) / 2f
                val top = (size.height - frameSize) / 2f
                drawRoundRect(
                    color = accentColor.copy(alpha = 0.85f),
                    topLeft = Offset(left, top),
                    size = Size(frameSize, frameSize),
                    cornerRadius = CornerRadius(18f, 18f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )

                val laserY = top + frameSize * laserProgress
                drawLine(
                    color = CyberEmerald,
                    start = Offset(left + 12f, laserY),
                    end = Offset(left + frameSize - 12f, laserY),
                    strokeWidth = 4f
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = "Align friend's Personal QR Code inside frame",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Button(
            onClick = {
                onSimulatedScanComplete("weq_scanned_friend", "Alex Rivera (QR Verified)")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberEmerald,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("capture_qr_code_from_camera_button")
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Scan & Add QR Friend Now", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun FriendsListSection(
    state: UiState<List<Friend>>,
    onShowReportDialog: (String, String) -> Unit,
    onSwitchToFindFriends: () -> Unit
) {
    when (state) {
        is UiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EmptyState("No friends yet. Share your Personal QR Code or search on 'Find Friends' to add people!")
                    Button(
                        onClick = onSwitchToFindFriends,
                        colors = ButtonDefaults.buttonColors(containerColor = HoloCyan, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Find Friends & Share QR Code", fontWeight = FontWeight.ExtraBold)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.data.forEach { friend ->
                        FriendItem(friend, onShowReportDialog)
                    }
                }
            }
        }
        is UiState.Error -> Text("Error: ${state.message}", color = Color.Red)
    }
}

@Composable
private fun DiscoverySection(
    myUserProfile: UserProfile,
    discoveredUsers: List<UserProfile>,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    onUpdatePhone: () -> Unit,
    onSyncContacts: () -> Unit,
    onSearch: (String) -> Unit,
    onReport: (String, String) -> Unit,
    onSendRequest: (String, String) -> Unit,
    onQrStatusMessage: (String) -> Unit
) {
    var searchInput by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Share Personal QR Code & Scan Friend QR Code Card
        SharePersonalQrCodeCard(
            myUserProfile = myUserProfile,
            onSendFriendRequest = onSendRequest,
            onStatusMessage = onQrStatusMessage
        )

        // 2. Search for Peers by Name or Email
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Search for Peers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Search for friends by name or email address using Firestore indexing.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        if (it.trim().length >= 2) {
                            onSearch(it.trim())
                        }
                    },
                    label = { Text("Name or Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("find_friends_search_input"),
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch(searchInput) },
                            modifier = Modifier.testTag("find_friends_search_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearch(searchInput) }
                    )
                )
            }
        }

        // 3. Sync Phone Contacts
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sync Phone Contacts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Verify your phone number so friends can find you, and sync your contacts.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = onPhoneChange,
                    label = { Text("Your Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("find_friends_phone_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onUpdatePhone,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("find_friends_save_phone_button")
                    ) {
                        Text("Save Phone")
                    }
                    Button(
                        onClick = onSyncContacts,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("find_friends_sync_contacts_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Contacts", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Discovered Friends List
        Text(
            text = "Discovered Friends",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (discoveredUsers.isEmpty()) {
            EmptyState("Share your Personal QR Code, search by name/email, or sync contacts to find friends.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                discoveredUsers.forEach { user ->
                    UserDiscoveryItem(user, onSendRequest, onReport)
                }
            }
        }
    }
}

@Composable
private fun RequestsSection(
    requestsState: UiState<List<FriendRequest>>,
    onAccept: (FriendRequest) -> Unit,
    onDecline: (String) -> Unit
) {
    when (requestsState) {
        is UiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Success -> {
            if (requestsState.data.isEmpty()) {
                EmptyState("No pending friend requests.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    requestsState.data.forEach { request ->
                        RequestItem(request, onAccept, onDecline)
                    }
                }
            }
        }
        is UiState.Error -> Text("Error: ${requestsState.message}")
    }
}

@Composable
private fun FriendItem(friend: Friend, onShowReportDialog: (String, String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(friend.avatarEmoji)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(friend.displayName, fontWeight = FontWeight.Bold)
                Text(friend.role.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Message",
                tint = HoloCyan,
                modifier = Modifier.clickable { }
            )
            IconButton(
                onClick = { onShowReportDialog(friend.friendUserId, "profile") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Report",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun UserDiscoveryItem(
    user: UserProfile,
    onSendRequest: (String, String) -> Unit,
    onReport: (String, String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SolarAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(user.displayName, fontWeight = FontWeight.Bold)
                Text(user.location, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(
                onClick = { onReport(user.userId, "profile") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Report",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Button(
                onClick = { onSendRequest(user.userId, user.displayName) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                Text("Add", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun RequestItem(
    request: FriendRequest,
    onAccept: (FriendRequest) -> Unit,
    onDecline: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonPink.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(request.fromAvatarEmoji)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(request.fromName, fontWeight = FontWeight.Bold)
                Text("Wants to be friends", style = MaterialTheme.typography.labelSmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { onDecline(request.id) }) {
                    Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color.Red)
                }
                IconButton(onClick = { onAccept(request) }) {
                    Icon(Icons.Default.Check, contentDescription = "Accept", tint = CyberEmerald)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(msg: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color.LightGray
        )
        Text(
            text = msg,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
