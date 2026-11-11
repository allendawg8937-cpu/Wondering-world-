package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush as ComposeBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class DrawnLine(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioAndArcadeTab(
    highScore: Long,
    onShareStudioCreation: (String, String) -> Unit,
    onNewHighScore: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lines = remember { mutableStateListOf<DrawnLine>() }
    var selectedBrushColor by remember { mutableStateOf(HoloCyan) }
    var selectedFilter by remember { mutableStateOf("Cyberpunk") }
    var selectedStickers by remember { mutableStateOf("✨ 🚀 🎵") }
    var captionText by remember { mutableStateOf("Equality in 3D Motion") }
    var studioToast by remember { mutableStateOf<String?>(null) }
    var youtubeSearchQuery by remember { mutableStateOf("STEM Science & 3D Game Creation Tutorials") }

    fun openExternalUrl(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
        }
    }

    val brushPalette = listOf(
        HoloCyan,
        NeonPink,
        CyberEmerald,
        SolarAmber,
        ElectricIndigo,
        Color.White
    )

    val filters = listOf("Cyberpunk", "Solar Warm", "Emerald Matrix", "Deep Space")

    // Profile Arcade State
    var isGameRunning by remember { mutableStateOf(false) }
    var currentScore by remember { mutableLongStateOf(0L) }
    var timeLeftSeconds by remember { mutableIntStateOf(15) }
    var starPosition by remember { mutableStateOf(Offset(0.5f, 0.5f)) }

    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            timeLeftSeconds = 15
            currentScore = 0L
            while (timeLeftSeconds > 0 && isGameRunning) {
                delay(1000L)
                timeLeftSeconds -= 1
                starPosition = Offset(
                    x = Random.nextFloat().coerceIn(0.15f, 0.85f),
                    y = Random.nextFloat().coerceIn(0.20f, 0.80f)
                )
            }
            isGameRunning = false
            if (currentScore > highScore) {
                onNewHighScore(currentScore)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 0. YouTube, Games & Creator Hub Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("youtube_games_creation_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RosePulse.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = "YouTube, Games & Creation",
                            tint = RosePulse
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YouTube, Playables Games & Video Creation Hub",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Explore YouTube educational videos, YouTube Playables games, YouTube Studio creator tools, and share video ideas to your 3D Wall.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = youtubeSearchQuery,
                    onValueChange = { youtubeSearchQuery = it },
                    label = { Text("Search YouTube Videos, Study Tutorials, or Games") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("youtube_search_input")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val q = Uri.encode(youtubeSearchQuery.ifBlank { "STEM Science for Kids" })
                            openExternalUrl("https://www.youtube.com/results?search_query=$q")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RosePulse,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("launch_youtube_search_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search YouTube", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { openExternalUrl("https://www.youtube.com/playables") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("launch_youtube_playables_button")
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("YouTube Playables Games", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { openExternalUrl("https://studio.youtube.com/") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier.testTag("launch_youtube_studio_button")
                    ) {
                        Icon(Icons.Default.VideoCall, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("YouTube Creator Studio", color = HoloCyan)
                    }

                    OutlinedButton(
                        onClick = { openExternalUrl("https://www.youtube.com/learning") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SolarAmber)
                    ) {
                        Text("YouTube Learning Hub", color = SolarAmber)
                    }
                }
            }
        }

        // 1. Creative Photo & Visual Studio Card
        Card(
            shape = RoundedCornerShape(22.dp),
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
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Creative Studio",
                        tint = HoloCyan
                    )
                    Column {
                        Text(
                            text = "Photoshop-Snap Creative Studio",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Draw on the 3D layered canvas, apply atmospheric filters, add stickers, and publish to your wall.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Interactive Layered Studio Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, HoloCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .testTag("creative_studio_canvas")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_studio_canvas),
                        contentDescription = "Studio background layer",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    val filterTint = when (selectedFilter) {
                        "Solar Warm" -> SolarAmber.copy(alpha = 0.35f)
                        "Emerald Matrix" -> CyberEmerald.copy(alpha = 0.35f)
                        "Deep Space" -> Color(0xAA070B19)
                        else -> ElectricIndigo.copy(alpha = 0.32f)
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedBrushColor) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val end = change.position
                                    val start = end - dragAmount
                                    lines.add(
                                        DrawnLine(
                                            start = start,
                                            end = end,
                                            color = selectedBrushColor,
                                            strokeWidth = 10f
                                        )
                                    )
                                }
                            }
                    ) {
                        drawRect(color = filterTint)
                        lines.forEach { line ->
                            drawLine(
                                color = line.color,
                                start = line.start,
                                end = line.end,
                                strokeWidth = line.strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Overlay Stickers & Caption
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                ComposeBrush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC070B19))
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = selectedStickers,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = captionText,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Brush Palette & Clear Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        brushPalette.forEach { color ->
                            val isSelected = selectedBrushColor == color
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedBrushColor = color }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { lines.clear() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear canvas",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear")
                    }
                }

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Visual Layer Caption") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!studioToast.isNullOrBlank()) {
                    Text(
                        text = studioToast!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberEmerald
                    )
                }

                Button(
                    onClick = {
                        onShareStudioCreation(
                            "Studio Art: $captionText",
                            "Created in Wondering World Studio with filter [$selectedFilter], stickers [$selectedStickers], and ${lines.size} custom brush strokes."
                        )
                        studioToast = "Published your Studio Art to your 3D Wall!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("publish_studio_art_button")
                ) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Studio Art to My 3D Wall", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Embedded Profile Arcade Card ("Star Catcher 3D")
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f))
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Profile Arcade",
                            tint = CyberEmerald
                        )
                        Column {
                            Text(
                                text = "Profile Arcade: Star Catcher 3D",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Safe interactive mini-game inside your profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = CyberEmerald.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SolarAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Best: ${maxOf(highScore, currentScore)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF050814))
                        .border(1.dp, CyberEmerald.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .testTag("profile_arcade_canvas"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGameRunning) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { tapOffset ->
                                        val starPx = Offset(
                                            x = starPosition.x * size.width,
                                            y = starPosition.y * size.height
                                        )
                                        if ((tapOffset - starPx).getDistance() <= 95f) {
                                            currentScore += 10L
                                            starPosition = Offset(
                                                x = Random.nextFloat().coerceIn(0.15f, 0.85f),
                                                y = Random.nextFloat().coerceIn(0.20f, 0.80f)
                                            )
                                        }
                                    }
                                }
                        ) {
                            val center = Offset(
                                x = starPosition.x * size.width,
                                y = starPosition.y * size.height
                            )
                            drawCircle(
                                color = SolarAmber.copy(alpha = 0.28f),
                                radius = 56f,
                                center = center
                            )
                            drawCircle(
                                color = SolarAmber,
                                radius = 28f,
                                center = center
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Score: $currentScore",
                                style = MaterialTheme.typography.titleMedium,
                                color = SolarAmber
                            )
                            Text(
                                text = "Time: ${timeLeftSeconds}s",
                                style = MaterialTheme.typography.titleMedium,
                                color = HoloCyan
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = if (currentScore > 0) "Round Complete! Score: $currentScore" else "Tap Glowing Stars to Earn Profile Points!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Button(
                                onClick = { isGameRunning = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("launch_arcade_game_button")
                            ) {
                                Text("Launch Safe Mini-Game", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
