package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class DailyAffirmation(
    val id: String,
    val quote: String,
    val author: String,
    val authorTitle: String,
    val category: String,
    val communityPrompt: String,
    val emoji: String,
    val accentColor: Color,
    val initialCommunityHearts: Int = 124
)

val CURATED_AFFIRMATIONS = listOf(
    DailyAffirmation(
        id = "aff_equal_1",
        quote = "We are all equal. Every human being has equal dignity, equal worth, and unlimited potential to build a brighter future together.",
        author = "WereEqual Community Manifesto",
        authorTitle = "Founding Principle",
        category = "Equal Rights & Unity",
        communityPrompt = "Reach out to one neighbor or peer today and remind them their voice matters.",
        emoji = "🤝",
        accentColor = HoloCyan,
        initialCommunityHearts = 248
    ),
    DailyAffirmation(
        id = "aff_community_2",
        quote = "A strong community is built when every generation—kids, teens, adults, and veterans—shares wisdom, kindness, and mutual respect.",
        author = "WereEqual Unity Circle",
        authorTitle = "Community Stewardship",
        category = "Community & Belonging",
        communityPrompt = "Share an encouraging comment or milestone on the Community Wall today.",
        emoji = "🌍",
        accentColor = CyberEmerald,
        initialCommunityHearts = 192
    ),
    DailyAffirmation(
        id = "aff_growth_3",
        quote = "Education is the most powerful weapon which you can use to change the world. Keep learning, keep asking questions, and share knowledge freely.",
        author = "Nelson Mandela",
        authorTitle = "Leader & Human Rights Advocate",
        category = "Education & STEM",
        communityPrompt = "Explore a new topic or career milestone today and celebrate curiosity.",
        emoji = "🎓",
        accentColor = CyberEmerald,
        initialCommunityHearts = 215
    ),
    DailyAffirmation(
        id = "aff_finance_4",
        quote = "Financial freedom begins with consistent daily choices. Every dollar saved today is an investment in your family's stability tomorrow.",
        author = "Personal Literacy Circle",
        authorTitle = "Wealth & Stability Guide",
        category = "Financial Resilience",
        communityPrompt = "Review one savings goal in your Wallet and celebrate your progress.",
        emoji = "💰",
        accentColor = SolarAmber,
        initialCommunityHearts = 164
    ),
    DailyAffirmation(
        id = "aff_courage_5",
        quote = "Courage doesn't always roar. Sometimes courage is the quiet voice at the end of the day saying, 'I will try again tomorrow.'",
        author = "Mary Anne Radmacher",
        authorTitle = "Author & Artist",
        category = "Veterans & Courage",
        communityPrompt = "Honor your resilience today—every step forward counts.",
        emoji = "🛡️",
        accentColor = NeonPink,
        initialCommunityHearts = 187
    ),
    DailyAffirmation(
        id = "aff_creative_6",
        quote = "Creativity is intelligence having fun. Bring your unique voice, your music, and your vision to the community without fear.",
        author = "Albert Einstein",
        authorTitle = "Physicist & Thinker",
        category = "Creative Vision",
        communityPrompt = "Express yourself through music, art, or a custom profile theme today.",
        emoji = "🎨",
        accentColor = HoloCyan,
        initialCommunityHearts = 176
    ),
    DailyAffirmation(
        id = "aff_unity_7",
        quote = "None of us, including me, ever do great things alone. But we can all do small things with great love, and together we can do something wonderful.",
        author = "Mother Teresa",
        authorTitle = "Humanitarian",
        category = "Community & Belonging",
        communityPrompt = "Small acts of kindness ripple across the entire WereEqual community.",
        emoji = "🌟",
        accentColor = CyberEmerald,
        initialCommunityHearts = 231
    ),
    DailyAffirmation(
        id = "aff_career_8",
        quote = "Your career is your marathon, not a sprint. Celebrate small milestones, build your skills, and lift others as you climb.",
        author = "Career Mentorship Network",
        authorTitle = "Professional Pathway",
        category = "Education & STEM",
        communityPrompt = "Check off or add one objective on your Student & Career Timeline.",
        emoji = "💼",
        accentColor = SolarAmber,
        initialCommunityHearts = 149
    ),
    DailyAffirmation(
        id = "aff_resilience_9",
        quote = "Out of difficulties grow miracles. Stand tall, support your neighbors, and know that you are stronger than any obstacle.",
        author = "Jean de La Bruyère",
        authorTitle = "Philosopher",
        category = "Veterans & Courage",
        communityPrompt = "You are never alone—our community stands beside you.",
        emoji = "⚡",
        accentColor = NeonPink,
        initialCommunityHearts = 178
    ),
    DailyAffirmation(
        id = "aff_equality_10",
        quote = "In diversity there is beauty and there is strength. When we welcome everyone to the table, the whole community thrives.",
        author = "Maya Angelou",
        authorTitle = "Poet & Civil Rights Champion",
        category = "Equal Rights & Unity",
        communityPrompt = "Celebrate what makes you unique while uplifting what unites us all.",
        emoji = "✨",
        accentColor = RosePulse,
        initialCommunityHearts = 260
    ),
    DailyAffirmation(
        id = "aff_kindness_11",
        quote = "Alone we can do so little; together we can do so much. Connection and empathy are the true superpowers of humanity.",
        author = "Helen Keller",
        authorTitle = "Author & Educator",
        category = "Community & Belonging",
        communityPrompt = "Send a supportive message or join a community conversation today.",
        emoji = "💫",
        accentColor = HoloCyan,
        initialCommunityHearts = 204
    ),
    DailyAffirmation(
        id = "aff_future_12",
        quote = "The best way to predict the future is to create it together—with fairness, open hearts, and unwavering hope.",
        author = "WereEqual Youth & Founders Council",
        authorTitle = "Platform Vision",
        category = "Creative Vision",
        communityPrompt = "Dream boldly today—your ideas shape our shared platform.",
        emoji = "🚀",
        accentColor = SolarAmber,
        initialCommunityHearts = 195
    )
)

/**
 * Randomized Community Daily Affirmation Widget for the Dashboard.
 * Picks a randomized affirmation on load, supports 1-tap random shuffle, optional auto-rotate,
 * category filtering, community heart reactions, custom user affirmation contributions,
 * and 1-tap pinning to the Community Wall.
 */
@Composable
fun DailyAffirmationCard(
    userDisplayName: String = "Community Member",
    onPinAffirmationToWall: (headline: String, body: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val affirmationsPool = remember {
        mutableStateListOf<DailyAffirmation>().apply {
            addAll(CURATED_AFFIRMATIONS)
        }
    }

    var selectedCategory by remember { mutableStateOf("All") }
    val filteredAffirmations = remember(selectedCategory, affirmationsPool.size) {
        if (selectedCategory == "All") {
            affirmationsPool.toList()
        } else {
            affirmationsPool.filter { it.category == selectedCategory }
        }
    }

    // Randomized initial index so each dashboard visit highlights a fresh inspiring affirmation
    var currentIndex by remember(selectedCategory, filteredAffirmations.size) {
        val randomStart = if (filteredAffirmations.size > 1) {
            Random.nextInt(filteredAffirmations.size)
        } else {
            0
        }
        mutableIntStateOf(randomStart)
    }

    var shuffleCount by remember { mutableIntStateOf(1) }
    var autoRotateEnabled by remember { mutableStateOf(false) }
    var showAddAffirmationForm by remember { mutableStateOf(false) }
    var customQuoteInput by remember { mutableStateOf("") }
    var customCategoryInput by remember { mutableStateOf("Community & Belonging") }

    val heartCounts = remember {
        mutableStateMapOf<String, Int>().apply {
            CURATED_AFFIRMATIONS.forEach { put(it.id, it.initialCommunityHearts) }
        }
    }
    val heartedByUser = remember { mutableStateListOf<String>() }
    val savedBookmarks = remember { mutableStateListOf<String>() }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    fun pickRandomAffirmationIndex(excludeIndex: Int, size: Int): Int {
        if (size <= 1) return 0
        var candidate = Random.nextInt(size)
        var attempts = 0
        while (candidate == excludeIndex && attempts < 6) {
            candidate = Random.nextInt(size)
            attempts++
        }
        if (candidate == excludeIndex) {
            candidate = (excludeIndex + 1) % size
        }
        return candidate
    }

    // Optional Auto-Rotate Randomizer timer
    LaunchedEffect(autoRotateEnabled, filteredAffirmations.size) {
        while (autoRotateEnabled && filteredAffirmations.size > 1) {
            delay(10000L)
            currentIndex = pickRandomAffirmationIndex(currentIndex, filteredAffirmations.size)
            shuffleCount++
        }
    }

    val safeIndex = currentIndex.coerceIn(0, (filteredAffirmations.size - 1).coerceAtLeast(0))
    val currentAffirmation = filteredAffirmations.getOrNull(safeIndex) ?: affirmationsPool.first()
    val isBookmarked = savedBookmarks.contains(currentAffirmation.id)
    val isHearted = heartedByUser.contains(currentAffirmation.id)
    val currentHearts = heartCounts[currentAffirmation.id] ?: currentAffirmation.initialCommunityHearts

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(
                listOf(currentAffirmation.accentColor, HoloCyan, CyberEmerald, SolarAmber)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.15f)
            .testTag("daily_affirmation_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header: Randomized Daily Community Affirmation Title, Live Badge & Bookmark
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
                                Brush.linearGradient(
                                    listOf(currentAffirmation.accentColor, HoloCyan, CyberEmerald)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Randomized Daily Affirmation Widget",
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
                                text = "Daily Community Affirmation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                color = HoloCyan.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, HoloCyan),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "🎲 RANDOMIZED • DAY $dayOfYear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "$todayFormatted • Uplifting the WereEqual Community",
                            style = MaterialTheme.typography.labelSmall,
                            color = currentAffirmation.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (isBookmarked) {
                            savedBookmarks.remove(currentAffirmation.id)
                            statusMessage = "Removed affirmation from your saved list"
                        } else {
                            savedBookmarks.add(currentAffirmation.id)
                            statusMessage = "✓ Saved affirmation (${savedBookmarks.size} bookmarked)!"
                        }
                    },
                    modifier = Modifier.testTag("bookmark_affirmation_button")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark Affirmation",
                        tint = if (isBookmarked) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Category Filter Chips + Auto-Rotate & Contribute Toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "All",
                    "Community & Belonging",
                    "Equal Rights & Unity",
                    "Education & STEM",
                    "Financial Resilience",
                    "Veterans & Courage",
                    "Creative Vision"
                ).forEach { catName ->
                    FilterChip(
                        selected = selectedCategory == catName,
                        onClick = {
                            selectedCategory = catName
                            statusMessage = null
                        },
                        label = {
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedCategory == catName) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("affirmation_cat_${catName.take(6).lowercase()}")
                    )
                }

                FilterChip(
                    selected = autoRotateEnabled,
                    onClick = { autoRotateEnabled = !autoRotateEnabled },
                    label = {
                        Text(
                            text = if (autoRotateEnabled) "⏱️ Auto-Random ON" else "⏱️ Auto-Random",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.testTag("affirmation_auto_rotate_chip")
                )
            }

            // 3. Randomized Affirmation Display Container with Animated Transition
            AnimatedContent(
                targetState = currentAffirmation,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "daily_affirmation_transition"
            ) { aff ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, aff.accentColor.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_affirmation_quote_surface")
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
                            Surface(
                                color = aff.accentColor.copy(alpha = 0.20f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = aff.emoji, fontSize = 12.sp)
                                    Text(
                                        text = aff.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = aff.accentColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Community Resonance Heart Counter Pill
                                Surface(
                                    color = if (isHearted) RosePulse.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isHearted) RosePulse else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier
                                        .clickable {
                                            if (isHearted) {
                                                heartedByUser.remove(aff.id)
                                                heartCounts[aff.id] = (currentHearts - 1).coerceAtLeast(1)
                                            } else {
                                                heartedByUser.add(aff.id)
                                                heartCounts[aff.id] = currentHearts + 1
                                                statusMessage = "❤️ You & ${currentHearts + 1} community members resonated with this affirmation!"
                                            }
                                        }
                                        .testTag("heart_affirmation_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Community Heart",
                                            tint = if (isHearted) RosePulse else NeonPink,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "$currentHearts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isHearted) RosePulse else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                Text(
                                    text = "#${safeIndex + 1} of ${filteredAffirmations.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "\"${aff.quote}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.testTag("daily_affirmation_quote_text")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(aff.accentColor)
                            )
                            Text(
                                text = "— ${aff.author}, ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = aff.accentColor
                            )
                            Text(
                                text = aff.authorTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Daily Community Action Reflection Prompt
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Community Action: ${aff.communityPrompt}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 4. Optional Inline Form: Add Your Own Community Affirmation to the Randomized Pool
            AnimatedVisibility(visible = showAddAffirmationForm) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, HoloCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_custom_affirmation_form")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✍️ Add a Community Affirmation to the Dashboard Pool",
                            style = MaterialTheme.typography.labelLarge,
                            color = HoloCyan,
                            fontWeight = FontWeight.ExtraBold
                        )

                        OutlinedTextField(
                            value = customQuoteInput,
                            onValueChange = { customQuoteInput = it },
                            label = { Text("Write an uplifting affirmation for the community...") },
                            placeholder = { Text("e.g., Every person in our community brings irreplaceable light and strength.") },
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_affirmation_quote_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val cleanQuote = customQuoteInput.trim().ifBlank {
                                        "Together in WereEqual, we lift each other up with kindness, hope, and equal respect."
                                    }
                                    val newAff = DailyAffirmation(
                                        id = "user_aff_${System.currentTimeMillis()}",
                                        quote = cleanQuote,
                                        author = userDisplayName.ifBlank { "Community Member" },
                                        authorTitle = "WereEqual Community Contributor",
                                        category = customCategoryInput,
                                        communityPrompt = "Contributed by $userDisplayName to inspire the community today!",
                                        emoji = "🌟",
                                        accentColor = HoloCyan,
                                        initialCommunityHearts = 1
                                    )
                                    affirmationsPool.add(0, newAff)
                                    heartCounts[newAff.id] = 1
                                    selectedCategory = "All"
                                    currentIndex = 0
                                    customQuoteInput = ""
                                    showAddAffirmationForm = false
                                    statusMessage = "✓ Added your affirmation to the Dashboard & selected it!"
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_custom_affirmation_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add to Widget Pool",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            OutlinedButton(
                                onClick = { showAddAffirmationForm = false },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            if (!statusMessage.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = statusMessage!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Primary Interactive Actions Row: Randomize / Shuffle, Pin to Community Wall & Add Your Own
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        currentIndex = pickRandomAffirmationIndex(safeIndex, filteredAffirmations.size)
                        shuffleCount++
                        statusMessage = "🎲 Randomized a fresh daily affirmation (#$shuffleCount)!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("shuffle_daily_affirmation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Randomize Daily Affirmation",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "🎲 Randomize",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Button(
                    onClick = {
                        onPinAffirmationToWall(
                            "🌟 Daily Community Affirmation: ${currentAffirmation.category}",
                            "\"${currentAffirmation.quote}\" — ${currentAffirmation.author} (${currentAffirmation.authorTitle})"
                        )
                        statusMessage = "✓ Shared affirmation to the WereEqual Community Wall!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentAffirmation.accentColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pin_affirmation_to_wall_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Pin to Wall",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = { showAddAffirmationForm = !showAddAffirmationForm },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.75f)),
                    modifier = Modifier.testTag("toggle_add_affirmation_form_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Community Affirmation",
                        tint = CyberEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
