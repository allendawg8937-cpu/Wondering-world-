package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

data class AdultDatingProfileCandidate(
    val id: String,
    val name: String,
    val age: Int,
    val roleLabel: String,
    val location: String,
    val compatibilityPercent: Int,
    val favoriteSong: String,
    val firstDateIdea: String,
    val bio: String,
    val interests: List<String>,
    val accentHex: String,
    val avatarEmoji: String
)

private val ADULT_DATING_CANDIDATES = listOf(
    AdultDatingProfileCandidate(
        id = "date_maya",
        name = "Maya Lin",
        age = 27,
        roleLabel = "Creative Technologist & 3D Artist",
        location = "Seattle, WA",
        compatibilityPercent = 97,
        favoriteSong = "Midnight City - M83",
        firstDateIdea = "Coffee roastery tour & interactive digital art gallery",
        bio = "Looking for a genuine, kind partner who loves live synth music, weekend road trips, and building creative projects together.",
        interests = listOf("Music & 3D Art", "Coffee & Galleries", "Coding & Design"),
        accentHex = "#EC4899",
        avatarEmoji = "🎨"
    ),
    AdultDatingProfileCandidate(
        id = "date_marcus",
        name = "Marcus Sterling",
        age = 31,
        roleLabel = "USAF Veteran & Aerospace Mentor",
        location = "Denver, CO",
        compatibilityPercent = 95,
        favoriteSong = "Comfortably Numb - Pink Floyd",
        firstDateIdea = "Sunset overlook walk & acoustic live music cafe",
        bio = "Values honesty, equality, and good conversation. Big fan of outdoor trails, classic rock, and supporting local community events.",
        interests = listOf("Outdoor Trails", "Live Music", "Community Leadership"),
        accentHex = "#06B6D4",
        avatarEmoji = "🛡️"
    ),
    AdultDatingProfileCandidate(
        id = "date_elena",
        name = "Elena Rostova",
        age = 29,
        roleLabel = "Community Founder & Book Cafe Host",
        location = "Chicago, IL",
        compatibilityPercent = 94,
        favoriteSong = "Golden Hour - JVKE",
        firstDateIdea = "Local bookstore browse & artisan pizza night",
        bio = "Warm-hearted optimist who loves indie bookstores, cooking new recipes, financial wellness goals, and meaningful connections.",
        interests = listOf("Bookstores & Coffee", "Cooking", "Financial Stability"),
        accentHex = "#10B981",
        avatarEmoji = "📚"
    ),
    AdultDatingProfileCandidate(
        id = "date_devon",
        name = "Devon Brooks",
        age = 28,
        roleLabel = "Indie Musician & Sound Engineer",
        location = "Austin, TX",
        compatibilityPercent = 92,
        favoriteSong = "Starlight - Muse",
        firstDateIdea = "Vinyl record shop & rooftop tacos",
        bio = "Always chasing great melodies and authentic conversations. Let's share playlists and explore local food spots.",
        interests = listOf("Vinyl & Concerts", "Foodie Spots", "6D Audio Studio"),
        accentHex = "#F59E0B",
        avatarEmoji = "🎸"
    )
)

/**
 * Clean, simple Adult Profile Dating & Matchmaking Hub (strictly 18+ gated).
 * Allows adult users to browse compatible matches, send respectful icebreakers,
 * plan first dates via Google Maps, or launch a video date call.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdultProfileDatingHubCard(
    userDisplayName: String,
    userAge: Long,
    userRole: String,
    userLocation: String,
    onSwitchToAdultProfile: () -> Unit = {},
    onStartVideoDateCall: () -> Unit = {},
    onOpenMessagingWithMatch: () -> Unit = {},
    onShareMatchMilestoneToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAdultProfile = userAge >= 18L

    var activeDatingTab by remember { mutableStateOf("DISCOVER") } // DISCOVER, MATCHES, PREFERENCES
    var candidateIndex by remember { mutableIntStateOf(0) }
    val matchedProfiles = remember {
        mutableStateListOf(ADULT_DATING_CANDIDATES[0])
    }
    var datingIntentStatus by remember { mutableStateOf("Meaningful Relationship") }
    var firstDatePreference by remember { mutableStateOf("Coffee & Music") }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    val currentCandidate = ADULT_DATING_CANDIDATES[candidateIndex % ADULT_DATING_CANDIDATES.size]
    val candidateColor = parseThemeHex(currentCandidate.accentHex)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(NeonPink, RosePulse, HoloCyan, SolarAmber)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("adult_profile_dating_hub_card")
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(NeonPink, RosePulse))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Adult Profile Dating",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "WereEqual Adult Dating & Match Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Adult Profile (18+) • Shared music, values & first-date planner",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = RosePulse.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, RosePulse),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isAdultProfile) "18+ VERIFIED" else "18+ LOCKED",
                        style = MaterialTheme.typography.labelSmall,
                        color = RosePulse,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (!isAdultProfile) {
                // Clean 18+ Protection Gate for Youth/Student Profiles
                Surface(
                    color = RosePulse.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, RosePulse.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "18+ Only",
                                tint = RosePulse,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Adult Profile Required (Current Age: $userAge). WereEqual Dating is exclusively for verified Adult Profiles (18+).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Button(
                            onClick = onSwitchToAdultProfile,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("switch_to_adult_profile_for_dating_button")
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch to Adult Profile (Age 26) to Unlock Dating", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            } else {
                // Simple 3-Button Tab Bar: Discover | My Matches | Preferences
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "DISCOVER" to "💘 Discover",
                        "MATCHES" to "💬 Matches (${matchedProfiles.size})",
                        "PREFERENCES" to "⚙️ Dating Prefs"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = activeDatingTab == key,
                            onClick = { activeDatingTab = key },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (activeDatingTab == key) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("dating_tab_${key.lowercase()}")
                        )
                    }
                }

                when (activeDatingTab) {
                    "DISCOVER" -> {
                        // Clean, Single-Card Match Discovery
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.5.dp, candidateColor.copy(alpha = 0.75f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dating_candidate_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(candidateColor.copy(alpha = 0.22f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = currentCandidate.avatarEmoji,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "${currentCandidate.name}, ${currentCandidate.age}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "${currentCandidate.roleLabel} • 📍 ${currentCandidate.location}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = candidateColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Surface(
                                        color = CyberEmerald.copy(alpha = 0.18f),
                                        border = BorderStroke(1.dp, CyberEmerald),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "${currentCandidate.compatibilityPercent}% Match",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = currentCandidate.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Shared Music Anthem & First Date Idea
                                Surface(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = HoloCyan,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "Profile Song: ${currentCandidate.favoriteSong}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = HoloCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = null,
                                                tint = SolarAmber,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "Ideal First Date: ${currentCandidate.firstDateIdea}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SolarAmber,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    currentCandidate.interests.forEach { tag ->
                                        Surface(
                                            color = candidateColor.copy(alpha = 0.16f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = candidateColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                // Simple 3 Action Buttons: Pass | Super Like | Match & Connect
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            candidateIndex = (candidateIndex + 1) % ADULT_DATING_CANDIDATES.size
                                            feedbackBanner = null
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("dating_pass_button")
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Next", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Next", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            if (matchedProfiles.none { it.id == currentCandidate.id }) {
                                                matchedProfiles.add(0, currentCandidate)
                                            }
                                            feedbackBanner = "💖 Matched with ${currentCandidate.name}! Added to your Matches tab."
                                            candidateIndex = (candidateIndex + 1) % ADULT_DATING_CANDIDATES.size
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonPink,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .testTag("dating_like_match_button")
                                    ) {
                                        Icon(Icons.Default.Favorite, contentDescription = "Match", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Match & Connect", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }

                    "MATCHES" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            matchedProfiles.forEach { match ->
                                val matchColor = parseThemeHex(match.accentHex)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, matchColor.copy(alpha = 0.6f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                                Text(match.avatarEmoji, style = MaterialTheme.typography.titleMedium)
                                                Column {
                                                    Text(
                                                        text = "${match.name}, ${match.age} • ${match.compatibilityPercent}% Match",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                    Text(
                                                        text = "First Date: ${match.firstDateIdea}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    feedbackBanner = "Sent icebreaker to ${match.name}: 'Hey ${match.name.substringBefore(" ")}, would love to check out ${match.firstDateIdea}!'"
                                                    onOpenMessagingWithMatch()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = HoloCyan,
                                                    contentColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("💬 Message", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    onStartVideoDateCall()
                                                },
                                                border = BorderStroke(1.dp, NeonPink),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Videocam, contentDescription = null, tint = NeonPink, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Video Date", style = MaterialTheme.typography.labelSmall, color = NeonPink, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        val encoded = Uri.encode("${match.firstDateIdea} near ${match.location}")
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")
                                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        )
                                                    } catch (_: Exception) {
                                                    }
                                                },
                                                border = BorderStroke(1.dp, CyberEmerald),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.Map, contentDescription = "Map Date Spot", tint = CyberEmerald, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "PREFERENCES" -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Dating Goal for $userDisplayName (Age $userAge)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "Meaningful Relationship",
                                        "Coffee & Conversation",
                                        "Concert & Music Partner",
                                        "Creative Companion"
                                    ).forEach { option ->
                                        FilterChip(
                                            selected = datingIntentStatus == option,
                                            onClick = {
                                                datingIntentStatus = option
                                                feedbackBanner = "Updated dating preference to '$option'"
                                            },
                                            label = { Text(option, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }

                                Text(
                                    text = "Favorite First-Date Vibe",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "Coffee & Music",
                                        "Art Gallery Walk",
                                        "Bookstore & Pizza",
                                        "Outdoor Sunset Walk"
                                    ).forEach { vibe ->
                                        FilterChip(
                                            selected = firstDatePreference == vibe,
                                            onClick = {
                                                firstDatePreference = vibe
                                                feedbackBanner = "Saved favorite first-date vibe: '$vibe'"
                                            },
                                            label = { Text(vibe, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (feedbackBanner != null) {
                Surface(
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
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
                            text = feedbackBanner.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
