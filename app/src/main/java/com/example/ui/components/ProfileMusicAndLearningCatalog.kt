package com.example.ui.components

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.AiTextUiState
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

data class BackgroundSongPreset(
    val id: String,
    val title: String,
    val genreVibe: String,
    val defaultPlatform: String,
    val chordFrequencies: List<Double>,
    val accent: Color,
    val spotifyTrackId: String = "",
    val appleMusicId: String = ""
)

val BACKGROUND_SONG_PRESETS = listOf(
    BackgroundSongPreset(
        id = "comfortably_numb",
        title = "Comfortably Numb - Pink Floyd",
        genreVibe = "Dreamy 3D Space Rock & Ambient Synth",
        defaultPlatform = "Spotify",
        chordFrequencies = listOf(246.94, 293.66, 369.99, 493.88, 329.63, 440.00),
        accent = HoloCyan,
        spotifyTrackId = "76tGvMhZp9UqZfE9Yvl6h6",
        appleMusicId = "1440742903"
    ),
    BackgroundSongPreset(
        id = "cosmic_nebula",
        title = "Cosmic Nebula Synthwave - Wondering World",
        genreVibe = "Futuristic 3D Hologram Arpeggio",
        defaultPlatform = "YouTube Music",
        chordFrequencies = listOf(261.63, 329.63, 392.00, 493.88, 523.25, 392.00),
        accent = NeonPink,
        spotifyTrackId = "4uLU61CZdwh6909ub99s7u"
    ),
    BackgroundSongPreset(
        id = "lofi_library_study",
        title = "Lo-Fi Library Study & Research Beats",
        genreVibe = "Calm Focus Chords for Reading & Homework",
        defaultPlatform = "Spotify",
        chordFrequencies = listOf(220.00, 261.63, 329.63, 392.00, 440.00, 329.63),
        accent = CyberEmerald,
        spotifyTrackId = "5T8tKH69U26Vp9v68bp060"
    ),
    BackgroundSongPreset(
        id = "solar_horizon",
        title = "Solar Horizon Acoustic Groove",
        genreVibe = "Warm Golden Hour Uplifting Melody",
        defaultPlatform = "Apple Music",
        chordFrequencies = listOf(293.66, 369.99, 440.00, 587.33, 493.88, 440.00),
        accent = SolarAmber,
        appleMusicId = "1440742903"
    ),
    BackgroundSongPreset(
        id = "cyber_emerald_pulse",
        title = "Cyber Emerald Focus Pulse",
        genreVibe = "Upbeat STEM & Coding Synth Loop",
        defaultPlatform = "SoundCloud",
        chordFrequencies = listOf(329.63, 392.00, 493.88, 659.25, 587.33, 493.88),
        accent = CyberEmerald
    ),
    BackgroundSongPreset(
        id = "starlight_equality",
        title = "Starlight Equality Anthem",
        genreVibe = "We Are All Equal • Community Harmony Pad",
        defaultPlatform = "YouTube Music",
        chordFrequencies = listOf(261.63, 349.23, 440.00, 523.25, 392.00, 493.88),
        accent = HoloCyan
    )
)

data class SportOptionItem(
    val id: String,
    val name: String,
    val emoji: String,
    val liveHeadline: String,
    val liveScoreOrStatus: String,
    val hubUrl: String,
    val accent: Color
)

val SPORTS_CATALOG = listOf(
    SportOptionItem(
        id = "basketball",
        name = "Basketball (NBA / WNBA / NCAA)",
        emoji = "🏀",
        liveHeadline = "Primetime Conference Showdown & Youth Clinic Highlights",
        liveScoreOrStatus = "LIVE • Celtics 108 - Nuggets 104 (4Q)",
        hubUrl = "https://www.espn.com/nba/",
        accent = SolarAmber
    ),
    SportOptionItem(
        id = "football",
        name = "Football (NFL / College)",
        emoji = "🏈",
        liveHeadline = "Championship Drive Analysis & Community Stadium Night",
        liveScoreOrStatus = "FINAL • Texans 27 - Chiefs 24",
        hubUrl = "https://www.espn.com/nfl/",
        accent = CyberEmerald
    ),
    SportOptionItem(
        id = "soccer",
        name = "Soccer (MLS / Premier League / World Cup)",
        emoji = "⚽",
        liveHeadline = "Global Derby Goals & Youth Academy Spotlight",
        liveScoreOrStatus = "LIVE • Austin FC 2 - LAFC 1 (78')",
        hubUrl = "https://www.espn.com/soccer/",
        accent = HoloCyan
    ),
    SportOptionItem(
        id = "baseball",
        name = "Baseball (MLB / College)",
        emoji = "⚾",
        liveHeadline = "Extra-Inning Walk-Off & Diamond Pitching Breakdown",
        liveScoreOrStatus = "FINAL • Dodgers 6 - Yankees 4",
        hubUrl = "https://www.espn.com/mlb/",
        accent = NeonPink
    ),
    SportOptionItem(
        id = "tennis_pickleball",
        name = "Tennis & Pickleball",
        emoji = "🎾",
        liveHeadline = "Grand Slam Rally Highlights & Local Court Finder",
        liveScoreOrStatus = "SET 3 • Alcaraz 6-4, 4-6, 5-3",
        hubUrl = "https://www.espn.com/tennis/",
        accent = CyberEmerald
    ),
    SportOptionItem(
        id = "motorsports",
        name = "Motorsports (Formula 1 / NASCAR)",
        emoji = "🏎️",
        liveHeadline = "Circuit Qualifying Telemetry & Pit Strategy",
        liveScoreOrStatus = "POLE • P1 Lap 1:31.42",
        hubUrl = "https://www.espn.com/f1/",
        accent = RosePulse
    ),
    SportOptionItem(
        id = "track_olympics",
        name = "Track, Gymnastics & Olympic Sports",
        emoji = "🏅",
        liveHeadline = "World Record Sprint Splits & Student Athlete Stories",
        liveScoreOrStatus = "MEET • 100m Final 9.81s",
        hubUrl = "https://www.olympics.com/en/news",
        accent = SolarAmber
    ),
    SportOptionItem(
        id = "esports",
        name = "Esports & Competitive Gaming",
        emoji = "🎮",
        liveHeadline = "3D Arena Tournament Finals & Creator Streams",
        liveScoreOrStatus = "LIVE • Grand Finals Map 4",
        hubUrl = "https://www.youtube.com/gaming",
        accent = HoloCyan
    )
)

data class CuratedNewsArticle(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val source: String,
    val timeAgo: String,
    val url: String,
    val accent: Color
)

val CURATED_LOGIN_NEWS_ARTICLES = listOf(
    CuratedNewsArticle(
        id = "news_stem_ai",
        category = "Science & Tech",
        title = "Public Libraries & Schools Expand Free AI Study Labs Nationwide",
        summary = "New community grants bring interactive 3D STEM tutoring, robotics kits, and digital research tools to students and families in every ZIP code.",
        source = "AP Education & Tech",
        timeAgo = "18m ago",
        url = "https://apnews.com/hub/technology",
        accent = HoloCyan
    ),
    CuratedNewsArticle(
        id = "news_veterans",
        category = "Veterans & Community",
        title = "VA Launches Expanded Telehealth, GI Bill Career & Housing Fast-Track",
        summary = "Veterans and service members gain streamlined access to local clinic scheduling, tech entrepreneurship grants, and peer mentorship.",
        source = "VA Community Wire",
        timeAgo = "42m ago",
        url = "https://news.va.gov/",
        accent = CyberEmerald
    ),
    CuratedNewsArticle(
        id = "news_finance",
        category = "Finance & Economy",
        title = "Milestone Savings Strategies: How Families Are Building Emergency Buffers",
        summary = "Financial educators highlight visual 25%–100% milestone tracking and community credit union programs helping households reach stability.",
        source = "Consumer Finance Digest",
        timeAgo = "1h ago",
        url = "https://www.consumerfinance.gov/",
        accent = SolarAmber
    ),
    CuratedNewsArticle(
        id = "news_youth_grants",
        category = "Education & Youth",
        title = "FAFSA & Scholarship Deadlines: New Guides for First-Generation Students",
        summary = "Step-by-step college and trade school financial aid workshops open across local community centers this week.",
        source = "Student Aid Report",
        timeAgo = "2h ago",
        url = "https://studentaid.gov/",
        accent = NeonPink
    ),
    CuratedNewsArticle(
        id = "news_sports_community",
        category = "Sports Headlines",
        title = "Community Sports Leagues & Youth Athletics See Record Participation",
        summary = "From neighborhood basketball courts to collegiate stadiums, local athletes unite around teamwork, health, and equality.",
        source = "ESPN Community",
        timeAgo = "3h ago",
        url = "https://www.espn.com/",
        accent = RosePulse
    )
)

val PROFILE_INTEREST_OPTIONS = listOf(
    "Music & 3D Art",
    "STEM & Coding",
    "Space & Science",
    "Financial Stability",
    "Healthcare & Wellness",
    "Gaming & Creation"
)

val PROFILE_LIFESTYLE_OPTIONS = listOf(
    "Active Creator & Builder",
    "Early Bird Planner",
    "Night Owl Researcher",
    "Veteran Community Leader",
    "Student & Explorer",
    "Family & Youth Mentor"
)

val PROFILE_SCHEDULE_OPTIONS = listOf(
    "Morning Study & Prep (8–11 AM)",
    "Midday Career & Finance (11 AM–2 PM)",
    "Afternoon Library Research (2–5 PM)",
    "Evening 3D Studio & Music (6–9 PM)",
    "Weekend Community & Goals"
)

val PROFILE_RESEARCH_OPTIONS = listOf(
    "AI & Robotics Research",
    "FAFSA & College Grants",
    "VA Healthcare & GI Bill",
    "Personal Finance & Savings",
    "Public Library & History",
    "Sustainable Science"
)

/**
 * Generates personalized every-login suggestions based on everything the platform has learned
 * about the user (Interests, Sports, Lifestyle, Daily Schedule, Research, and Background Song).
 */
fun buildEveryLoginSuggestions(
    displayName: String,
    role: String,
    interests: Set<String>,
    lifestyle: Set<String>,
    schedule: Set<String>,
    research: Set<String>,
    sports: Set<String>,
    musicTrack: String
): List<String> {
    val primaryInterest = interests.firstOrNull() ?: "Music & 3D Art"
    val primaryLifestyle = lifestyle.firstOrNull() ?: "Active Creator & Builder"
    val primarySchedule = schedule.firstOrNull() ?: "Morning Study & Prep (8–11 AM)"
    val primaryResearch = research.firstOrNull() ?: "AI & Robotics Research"
    val primarySportId = sports.firstOrNull() ?: "basketball"
    val matchedSport = SPORTS_CATALOG.firstOrNull { it.id == primarySportId } ?: SPORTS_CATALOG.first()

    return listOf(
        "👋 Welcome back, ${displayName.ifBlank { "Explorer" }}! Based on your '$primaryLifestyle' lifestyle, your prime window today is $primarySchedule.",
        "🔬 Research Suggestion: Continue exploring '$primaryResearch' in the AI Study & ZIP Library Hub.",
        "${matchedSport.emoji} Sports Pick (${matchedSport.name}): ${matchedSport.liveScoreOrStatus} — ${matchedSport.liveHeadline}.",
        "🎵 Background Soundtrack Match: Queue up '$musicTrack' while working on $primaryInterest (${role.uppercase()} track)."
    )
}

/**
 * Zero-permission polyphonic ambient background music synthesizer.
 * Plays gentle harmonic chords and melodic notes in the background whenever [isPlaying] is true.
 */
@Composable
fun BackgroundMusicAudioEngine(
    isPlaying: Boolean,
    currentTrackTitle: String
) {
    LaunchedEffect(isPlaying, currentTrackTitle) {
        if (!isPlaying) return@LaunchedEffect

        val preset = BACKGROUND_SONG_PRESETS.firstOrNull {
            currentTrackTitle.contains(it.title.substringBefore(" -"), ignoreCase = true)
        } ?: BACKGROUND_SONG_PRESETS.first()

        withContext(Dispatchers.Default) {
            val sampleRate = 22050
            val noteDurationMs = 420
            val samplesPerNote = (sampleRate * noteDurationMs) / 1000
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(samplesPerNote * 2)

            var audioTrack: AudioTrack? = null
            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()
                val pcmBuffer = ShortArray(samplesPerNote)
                val notes = preset.chordFrequencies
                var stepIndex = 0

                while (isActive) {
                    val primaryFreq = notes[stepIndex % notes.size]
                    val fifthFreq = primaryFreq * 1.498
                    val subBassFreq = primaryFreq * 0.5

                    for (i in 0 until samplesPerNote) {
                        val progress = i.toDouble() / samplesPerNote
                        val envelope = when {
                            progress < 0.15 -> progress / 0.15
                            progress > 0.75 -> (1.0 - progress) / 0.25
                            else -> 1.0
                        }
                        val t = i.toDouble() / sampleRate
                        val wave = (
                            0.50 * sin(2.0 * PI * primaryFreq * t) +
                                0.30 * sin(2.0 * PI * fifthFreq * t) +
                                0.20 * sin(2.0 * PI * subBassFreq * t)
                            ) * envelope * 0.22
                        pcmBuffer[i] = (wave * Short.MAX_VALUE).toInt().coerceIn(
                            Short.MIN_VALUE.toInt(),
                            Short.MAX_VALUE.toInt()
                        ).toShort()
                    }
                    audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
                    stepIndex++
                }
            } catch (_: Exception) {
            } finally {
                try {
                    audioTrack?.stop()
                } catch (_: Exception) {
                }
                try {
                    audioTrack?.release()
                } catch (_: Exception) {
                }
            }
        }
    }
}

@Composable
fun EveryLoginSuggestionsCard(
    displayName: String,
    suggestions: List<String>,
    onRefreshSuggestions: () -> Unit,
    onOpenNewsAndSportsTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, SolarAmber.copy(alpha = 0.7f)),
        modifier = modifier.testTag("every_login_suggestions_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Every-Login Smart Suggestions",
                        tint = SolarAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Every-Login Smart Suggestions for ${displayName.ifBlank { "You" }}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Personalized from your learned interests, sports, lifestyle, schedule & research",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                OutlinedButton(
                    onClick = onRefreshSuggestions,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, HoloCyan),
                    modifier = Modifier.testTag("refresh_login_suggestions_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Login Suggestions",
                        tint = HoloCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Tips", style = MaterialTheme.typography.labelSmall, color = HoloCyan)
                }
            }

            suggestions.forEach { tip ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Button(
                onClick = onOpenNewsAndSportsTab,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HoloCyan,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_login_news_sports_button")
            ) {
                Icon(Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Full Daily News & My Chosen Sports Hub", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSportsSelectorCard(
    selectedSportsIds: Set<String>,
    onToggleSport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSports = SPORTS_CATALOG.filter { selectedSportsIds.contains(it.id) }
        .ifEmpty { listOf(SPORTS_CATALOG.first()) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, SolarAmber.copy(alpha = 0.65f)),
        modifier = modifier.testTag("profile_sports_selector_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Profile Sports Selector",
                    tint = SolarAmber,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Sports of Your Choosing on Your Profile Page",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pick your favorite sports below to pin live scores, headlines, and direct sports portals to your profile.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SPORTS_CATALOG.forEach { sport ->
                    val isSelected = selectedSportsIds.contains(sport.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggleSport(sport.id) },
                        label = {
                            Text(
                                text = "${sport.emoji} ${sport.name}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("profile_sport_chip_${sport.id}")
                    )
                }
            }

            Text(
                text = "Your Pinned Profile Sports Scoreboard (${activeSports.size} Active):",
                style = MaterialTheme.typography.labelLarge,
                color = SolarAmber
            )

            activeSports.forEach { sport ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, sport.accent.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "${sport.emoji} ${sport.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sport.liveScoreOrStatus,
                                style = MaterialTheme.typography.labelLarge,
                                color = sport.accent
                            )
                            Text(
                                text = sport.liveHeadline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sport.hubUrl)))
                                } catch (_: Exception) {
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, sport.accent),
                            modifier = Modifier.testTag("open_sport_portal_${sport.id}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open ${sport.name}",
                                tint = sport.accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live Hub", style = MaterialTheme.typography.labelSmall, color = sport.accent)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileBackgroundMusicJukeboxCard(
    currentTrackTitle: String,
    currentPlatform: String,
    isPlayingMusic: Boolean,
    spotifyConnected: Boolean = false,
    appleMusicConnected: Boolean = false,
    onSelectSongPreset: (BackgroundSongPreset) -> Unit,
    onCustomSongAndPlatformChange: (String, String) -> Unit,
    onTogglePlayMusic: () -> Unit,
    onConnectSpotify: () -> Unit = {},
    onConnectAppleMusic: () -> Unit = {},
    onDisconnectSpotify: () -> Unit = {},
    onDisconnectAppleMusic: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val matchedPreset = BACKGROUND_SONG_PRESETS.find { it.title == currentTrackTitle }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.65f)),
        modifier = modifier.testTag("profile_background_music_jukebox_card")
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Background Music Player",
                        tint = CyberEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Profile Background Music & Song Jukebox",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPlayingMusic) {
                                "🔊 Playing in Background: $currentTrackTitle ($currentPlatform)"
                            } else {
                                "Choose a song below & tap Play to listen in the background"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPlayingMusic) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = onTogglePlayMusic,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlayingMusic) NeonPink else CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("profile_background_music_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlayingMusic) "Pause Background Music" else "Play Background Music",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlayingMusic) "Pause" else "Play Song",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Connection Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Spotify", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    if (spotifyConnected) {
                        TextButton(onClick = onDisconnectSpotify, contentPadding = PaddingValues(0.dp)) {
                            Text("Connected • Disconnect", style = MaterialTheme.typography.labelSmall, color = CyberEmerald)
                        }
                    } else {
                        OutlinedButton(onClick = onConnectSpotify, shape = RoundedCornerShape(8.dp), modifier = Modifier.height(32.dp)) {
                            Text("Connect", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Apple Music", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    if (appleMusicConnected) {
                        TextButton(onClick = onDisconnectAppleMusic, contentPadding = PaddingValues(0.dp)) {
                            Text("Connected • Disconnect", style = MaterialTheme.typography.labelSmall, color = RosePulse)
                        }
                    } else {
                        OutlinedButton(onClick = onConnectAppleMusic, shape = RoundedCornerShape(8.dp), modifier = Modifier.height(32.dp)) {
                            Text("Connect", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            if (isPlayingMusic && matchedPreset != null) {
                if (currentPlatform == "Spotify" && spotifyConnected && matchedPreset.spotifyTrackId.isNotEmpty()) {
                    SpotifyEmbedPlayer(trackId = matchedPreset.spotifyTrackId)
                } else if (currentPlatform == "Apple Music" && appleMusicConnected && matchedPreset.appleMusicId.isNotEmpty()) {
                    AppleMusicEmbedPlayer(songId = matchedPreset.appleMusicId)
                }
            }

            Text(
                text = "Tap a Song to Play in the Background on Your Profile:",
                style = MaterialTheme.typography.labelLarge,
                color = HoloCyan
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BACKGROUND_SONG_PRESETS.forEach { song ->
                    val isSelected = currentTrackTitle.equals(song.title, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) {
                            song.accent.copy(alpha = 0.22f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) song.accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .width(215.dp)
                            .clickable { onSelectSongPreset(song) }
                            .testTag("select_bg_song_${song.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected && isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = song.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = song.genreVibe,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isSelected && isPlayingMusic) "🎵 Playing in Background" else "Tap to Play • ${song.defaultPlatform}",
                                style = MaterialTheme.typography.labelSmall,
                                color = song.accent
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = currentTrackTitle,
                onValueChange = { onCustomSongAndPlatformChange(it, currentPlatform) },
                label = { Text("Custom Background Song Title & Artist") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_bg_song_input")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAdaptiveLearningCard(
    learnedInterests: Set<String>,
    learnedLifestyle: Set<String>,
    learnedSchedule: Set<String>,
    learnedResearch: Set<String>,
    recentActivitySignals: List<String>,
    aiAdaptivePlanState: AiTextUiState,
    onToggleInterest: (String) -> Unit,
    onToggleLifestyle: (String) -> Unit,
    onToggleSchedule: (String) -> Unit,
    onToggleResearch: (String) -> Unit,
    onAddCustomLearningItem: (String, String) -> Unit,
    onGeneratePersonalizedBlueprint: () -> Unit,
    modifier: Modifier = Modifier
) {
    var customInputText by remember { mutableStateOf("") }
    var selectedCustomCategory by remember { mutableStateOf("Research") }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.65f)),
        modifier = modifier.testTag("profile_adaptive_learning_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Platform Adaptive Learning",
                    tint = HoloCyan,
                    modifier = Modifier.size(26.dp)
                )
                Column {
                    Text(
                        text = "Adaptive Profile Intelligence: Interests, Lifestyle, Schedule & Research",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Wondering World continuously learns from your interests, daily schedule, lifestyle, and research to personalize your hub.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1. Learned Interests
            Text(
                text = "1. Your Interests (Tap to train your profile):",
                style = MaterialTheme.typography.labelLarge,
                color = NeonPink
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (PROFILE_INTEREST_OPTIONS + learnedInterests).distinct().forEach { item ->
                    val selected = learnedInterests.contains(item)
                    FilterChip(
                        selected = selected,
                        onClick = { onToggleInterest(item) },
                        label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("learn_interest_${item.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // 2. Learned Lifestyle
            Text(
                text = "2. Your Lifestyle & Rhythm:",
                style = MaterialTheme.typography.labelLarge,
                color = CyberEmerald
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (PROFILE_LIFESTYLE_OPTIONS + learnedLifestyle).distinct().forEach { item ->
                    val selected = learnedLifestyle.contains(item)
                    FilterChip(
                        selected = selected,
                        onClick = { onToggleLifestyle(item) },
                        label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("learn_lifestyle_${item.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // 3. Learned Daily Schedule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SolarAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "3. Your Daily Schedule & Active Windows:",
                    style = MaterialTheme.typography.labelLarge,
                    color = SolarAmber
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (PROFILE_SCHEDULE_OPTIONS + learnedSchedule).distinct().forEach { item ->
                    val selected = learnedSchedule.contains(item)
                    FilterChip(
                        selected = selected,
                        onClick = { onToggleSchedule(item) },
                        label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("learn_schedule_${item.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // 4. Learned Research Focus
            Text(
                text = "4. Your Active Research Topics:",
                style = MaterialTheme.typography.labelLarge,
                color = HoloCyan
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (PROFILE_RESEARCH_OPTIONS + learnedResearch).distinct().forEach { item ->
                    val selected = learnedResearch.contains(item)
                    FilterChip(
                        selected = selected,
                        onClick = { onToggleResearch(item) },
                        label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("learn_research_${item.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // Add Custom Interest / Lifestyle / Schedule / Research Signal
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Teach Wondering World Something New About You:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Interest", "Lifestyle", "Schedule", "Research").forEach { cat ->
                            FilterChip(
                                selected = selectedCustomCategory == cat,
                                onClick = { selectedCustomCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customInputText,
                            onValueChange = { customInputText = it },
                            label = { Text("Add custom $selectedCustomCategory (e.g., Night Shift, Astrophysics)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_learning_signal_input")
                        )
                        Button(
                            onClick = {
                                val trimmed = customInputText.trim()
                                if (trimmed.isNotEmpty()) {
                                    onAddCustomLearningItem(selectedCustomCategory, trimmed)
                                    customInputText = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("add_custom_learning_signal_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Learned Item")
                        }
                    }
                }
            }

            if (recentActivitySignals.isNotEmpty()) {
                Text(
                    text = "Recent Signals Learned from Your Activity: ${recentActivitySignals.take(4).joinToString(" • ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald
                )
            }

            Button(
                onClick = onGeneratePersonalizedBlueprint,
                enabled = aiAdaptivePlanState !is AiTextUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_profile_learning_plan_button")
            ) {
                if (aiAdaptivePlanState is AiTextUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Analyzing Your Interests, Lifestyle, Schedule & Research…")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Synthesize My Schedule, Lifestyle & Research Plan",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (aiAdaptivePlanState) {
                is AiTextUiState.Streaming -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = aiAdaptivePlanState.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                is AiTextUiState.Success -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = aiAdaptivePlanState.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                is AiTextUiState.Error -> {
                    Text(
                        text = aiAdaptivePlanState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SolarAmber
                    )
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsAndSportsHubScreen(
    displayName: String,
    everyLoginSuggestions: List<String>,
    selectedSportsIds: Set<String>,
    learnedInterests: Set<String>,
    learnedLifestyle: Set<String>,
    learnedSchedule: Set<String>,
    learnedResearch: Set<String>,
    recentActivitySignals: List<String>,
    aiAdaptivePlanState: AiTextUiState,
    onRefreshLoginSuggestions: () -> Unit,
    onToggleSport: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onToggleLifestyle: (String) -> Unit,
    onToggleSchedule: (String) -> Unit,
    onToggleResearch: (String) -> Unit,
    onAddCustomLearningItem: (String, String) -> Unit,
    onGeneratePersonalizedBlueprint: () -> Unit,
    onShareNewsToWall: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedNewsCategory by remember { mutableStateOf("All News") }
    val categories = listOf(
        "All News",
        "Science & Tech",
        "Veterans & Community",
        "Finance & Economy",
        "Education & Youth",
        "Sports Headlines"
    )

    val filteredArticles = remember(selectedNewsCategory) {
        if (selectedNewsCategory == "All News") {
            CURATED_LOGIN_NEWS_ARTICLES
        } else {
            CURATED_LOGIN_NEWS_ARTICLES.filter { it.category == selectedNewsCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("news_and_sports_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Every-Login Personalized Suggestions Banner
        EveryLoginSuggestionsCard(
            displayName = displayName,
            suggestions = everyLoginSuggestions,
            onRefreshSuggestions = onRefreshLoginSuggestions,
            onOpenNewsAndSportsTab = onGeneratePersonalizedBlueprint
        )

        // 2. Daily News Briefing Page for Every User
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.65f)),
            modifier = Modifier.testTag("daily_news_briefing_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "Daily News Hub",
                        tint = HoloCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Column {
                        Text(
                            text = "Daily Login News Page & Community Wire",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Curated headlines tailored to your profile, research topics, and community.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedNewsCategory == cat,
                            onClick = { selectedNewsCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("news_category_chip_${cat.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                filteredArticles.forEach { article ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, article.accent.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("news_article_card_${article.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = article.accent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = article.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = article.accent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "${article.source} • ${article.timeAgo}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = article.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(article.url)))
                                        } catch (_: Exception) {
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = article.accent,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Read Full Story", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {
                                        onShareNewsToWall(article.title, "${article.summary} (${article.url})")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, article.accent),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = article.accent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share to 3D Wall", style = MaterialTheme.typography.labelLarge, color = article.accent)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Sports of Your Choosing & Live Scoreboard
        ProfileSportsSelectorCard(
            selectedSportsIds = selectedSportsIds,
            onToggleSport = onToggleSport
        )

        // 4. Adaptive Learning Engine (Interests, Lifestyle, Schedule & Research)
        ProfileAdaptiveLearningCard(
            learnedInterests = learnedInterests,
            learnedLifestyle = learnedLifestyle,
            learnedSchedule = learnedSchedule,
            learnedResearch = learnedResearch,
            recentActivitySignals = recentActivitySignals,
            aiAdaptivePlanState = aiAdaptivePlanState,
            onToggleInterest = onToggleInterest,
            onToggleLifestyle = onToggleLifestyle,
            onToggleSchedule = onToggleSchedule,
            onToggleResearch = onToggleResearch,
            onAddCustomLearningItem = onAddCustomLearningItem,
            onGeneratePersonalizedBlueprint = onGeneratePersonalizedBlueprint
        )
    }
}
