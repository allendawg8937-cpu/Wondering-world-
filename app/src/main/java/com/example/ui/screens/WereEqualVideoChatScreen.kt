package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.UserAccountBadge
import com.example.ui.components.UserBadgeStyle
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber

data class VideoChatParticipant(
    val id: String,
    val name: String,
    val role: String,
    val emoji: String,
    val avatarResId: Int,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isCameraOn: Boolean = true,
    val connectionQuality: String = "HD 1080p"
)

data class VideoChatRoomPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accent: Color,
    val activeMemberCount: Int
)

val VIDEO_CHAT_ROOMS = listOf(
    VideoChatRoomPreset(
        id = "room_study_pod",
        title = "🎓 Student Study Pod Video Lounge",
        subtitle = "Live homework help, FAFSA group Q&A & STEM coding jam",
        badge = "STUDENT POD",
        accent = HoloCyan,
        activeMemberCount = 4
    ),
    VideoChatRoomPreset(
        id = "room_veteran_mentorship",
        title = "🛡️ Veteran & Leader Video Round-Table",
        subtitle = "Career transitions, VA navigation & community peer support",
        badge = "VETERAN CIRCLE",
        accent = CyberEmerald,
        activeMemberCount = 3
    ),
    VideoChatRoomPreset(
        id = "room_marketplace_biz",
        title = "💼 Business & Marketplace Video Pitch",
        subtitle = "Local business networking, storefront showcases & deals",
        badge = "BUSINESS HUB",
        accent = SolarAmber,
        activeMemberCount = 5
    ),
    VideoChatRoomPreset(
        id = "room_creator_art",
        title = "🎬 Content Creator & 3D Art Studio Jam",
        subtitle = "Music synth jamming, 3D theme sharing & creative live feedback",
        badge = "CREATOR LAB",
        accent = NeonPink,
        activeMemberCount = 2
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WereEqualVideoChatScreen(
    profile: UserProfile,
    onLogInteraction: (actionType: String, detail: String, category: String) -> Unit = { _, _, _ -> },
    onShareCallHighlightToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isInCall by remember { mutableStateOf(true) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraEnabled by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var selectedRoomId by remember { mutableStateOf("room_study_pod") }
    var statusBanner by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            isCameraEnabled = true
            statusBanner = "✓ Camera access granted! Live video chat active."
        } else {
            statusBanner = "Camera access optional — you can still participate via audio & avatar!"
        }
    }

    val activeRoom = VIDEO_CHAT_ROOMS.firstOrNull { it.id == selectedRoomId } ?: VIDEO_CHAT_ROOMS.first()

    val participants = remember {
        mutableStateListOf(
            VideoChatParticipant(
                id = "part_maya",
                name = "Maya Lin",
                role = "student",
                emoji = "🎓",
                avatarResId = R.drawable.img_avatar_stem_scholar,
                isSpeaking = true,
                isMuted = false,
                isCameraOn = true,
                connectionQuality = "1080p HD"
            ),
            VideoChatParticipant(
                id = "part_marcus",
                name = "Marcus Sterling",
                role = "veteran",
                emoji = "🛡️",
                avatarResId = R.drawable.img_avatar_veteran_guardian,
                isSpeaking = false,
                isMuted = false,
                isCameraOn = true,
                connectionQuality = "1080p HD"
            ),
            VideoChatParticipant(
                id = "part_alex",
                name = "Alex Rivera",
                role = "creator",
                emoji = "🎬",
                avatarResId = R.drawable.img_avatar_neon_musician,
                isSpeaking = false,
                isMuted = true,
                isCameraOn = true,
                connectionQuality = "720p HD"
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_audio_wave")
    val pulseWave by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_wave"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wereequal_video_chat_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card with Live Status & Room Selection
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            ),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(HoloCyan, CyberEmerald, NeonPink, SolarAmber))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .sixDimensionalCardEffect(tiltFactor = 1.15f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(listOf(activeRoom.accent, HoloCyan, CyberEmerald))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Chat",
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "WereEqual Live Video Chat",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                SixDimensionalHdBadge(label = if (isInCall) "LIVE CALL" else "STANDBY")
                            }
                            Text(
                                text = "Face-to-face video rooms for study pods, mentorship & friend calls",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!hasCameraPermission) {
                        Button(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enable Camera", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Video Rooms Selector Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VIDEO_CHAT_ROOMS.forEach { room ->
                        FilterChip(
                            selected = selectedRoomId == room.id,
                            onClick = {
                                selectedRoomId = room.id
                                onLogInteraction("VideoRoomSwitch", "Joined ${room.title}", "VideoChat")
                                statusBanner = "✓ Switched to ${room.title}"
                            },
                            label = {
                                Text(
                                    text = "${room.badge} (${room.activeMemberCount})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selectedRoomId == room.id) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("video_room_chip_${room.id}")
                        )
                    }
                }

                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(15.dp))
                            Text(text = statusBanner!!, style = MaterialTheme.typography.labelSmall, color = CyberEmerald, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Video Call Viewfinder Grid (Active Video Call Screen)
        if (isInCall) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.5.dp, activeRoom.accent.copy(alpha = 0.8f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_video_viewfinder_grid")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Call Status Bar: Active Room & Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmerald)
                            )
                            Text(
                                text = activeRoom.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, activeRoom.accent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "🔒 Encrypted HD • 1080p 60fps",
                                style = MaterialTheme.typography.labelSmall,
                                color = activeRoom.accent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Video Tiles: 2x2 Grid (Current User + Other Participants)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tile 1: Current User's Video Feed (You)
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                width = if (!isMicMuted) 2.dp else 1.dp,
                                color = if (!isMicMuted) CyberEmerald else Color.Gray.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                                .testTag("user_video_tile")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isCameraEnabled) {
                                    Image(
                                        painter = painterResource(R.drawable.img_avatar_cyber_astronaut),
                                        contentDescription = "Your Video Feed",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.VideocamOff,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = "Camera Paused",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.LightGray
                                            )
                                        }
                                    }
                                }

                                // User Tag Overlay
                                Surface(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "You (${profile.displayName.substringBefore(" ")})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        if (isMicMuted) {
                                            Icon(
                                                imageVector = Icons.Default.MicOff,
                                                contentDescription = "Muted",
                                                tint = Color(0xFFFF3008),
                                                modifier = Modifier.size(11.dp)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(CyberEmerald)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Tile 2: Primary Speaking Participant (e.g. Maya Lin)
                        val mainPeer = participants.firstOrNull() ?: participants[0]
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                width = if (mainPeer.isSpeaking) 2.dp else 1.dp,
                                color = if (mainPeer.isSpeaking) HoloCyan else Color.Gray.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                                .testTag("peer_video_tile_${mainPeer.id}")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(mainPeer.avatarResId),
                                    contentDescription = mainPeer.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Surface(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = mainPeer.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        UserAccountBadge(role = mainPeer.role, style = UserBadgeStyle.COMPACT)
                                    }
                                }
                            }
                        }
                    }

                    // Secondary Participants Row (Horizontal Video Thumbnails)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        participants.drop(1).forEach { peer ->
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(95.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(peer.avatarResId),
                                        contentDescription = peer.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = peer.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. In-Call Interactive Control Bar (Mute, Video On/Off, Camera Flip, Speaker, End Call)
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mic Toggle
                            IconButton(
                                onClick = {
                                    isMicMuted = !isMicMuted
                                    statusBanner = if (isMicMuted) "Microphone muted" else "Microphone unmuted"
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isMicMuted) Color(0xFFFF3008).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f))
                                    .testTag("toggle_mic_button")
                            ) {
                                Icon(
                                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Toggle Mic",
                                    tint = if (isMicMuted) Color(0xFFFF3008) else Color.White
                                )
                            }

                            // Camera Toggle
                            IconButton(
                                onClick = {
                                    if (!hasCameraPermission) {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    } else {
                                        isCameraEnabled = !isCameraEnabled
                                        statusBanner = if (isCameraEnabled) "Camera enabled" else "Camera disabled"
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (!isCameraEnabled) Color(0xFFFF3008).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f))
                                    .testTag("toggle_camera_button")
                            ) {
                                Icon(
                                    imageVector = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                    contentDescription = "Toggle Video",
                                    tint = if (isCameraEnabled) Color.White else Color(0xFFFF3008)
                                )
                            }

                            // Camera Flip (Front / Rear)
                            IconButton(
                                onClick = {
                                    isFrontCamera = !isFrontCamera
                                    statusBanner = if (isFrontCamera) "Switched to Front Camera" else "Switched to Rear Camera"
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .testTag("flip_camera_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = "Flip Camera",
                                    tint = HoloCyan
                                )
                            }

                            // End Call Button (Red)
                            IconButton(
                                onClick = {
                                    isInCall = false
                                    onLogInteraction("VideoCallEnd", "Ended call in ${activeRoom.title}", "VideoChat")
                                    onShareCallHighlightToWall(
                                        "Connected via WereEqual Video Chat in ${activeRoom.title}",
                                        "Had a wonderful face-to-face video study & collaboration session with ${participants.joinToString { it.name }}!"
                                    )
                                    statusBanner = "Video call ended. Highlight shared to your Wall!"
                                },
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626))
                                    .testTag("end_video_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "End Call",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Standby / Rejoin Call Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(HoloCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = HoloCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Ready to Connect on Video?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Join ${activeRoom.title} with 1 tap to collaborate live with other community members.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            isInCall = true
                            statusBanner = "✓ Connected to ${activeRoom.title}!"
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("rejoin_video_call_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Join Video Room Now", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
