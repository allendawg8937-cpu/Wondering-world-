package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex

data class ZodiacHoroscopeReading(
    val id: String,
    val signName: String,
    val symbolEmoji: String,
    val dateRange: String,
    val element: String,
    val dailyForecast: String,
    val financeAndCareerVibe: String,
    val communityCompatibility: String,
    val luckyNumber: Int,
    val luckyColorName: String,
    val luckyColorHex: String,
    val cosmic3DStyleId: String
)

val ZODIAC_HOROSCOPES_CATALOG: List<ZodiacHoroscopeReading> = listOf(
    ZodiacHoroscopeReading(
        id = "aries",
        signName = "Aries",
        symbolEmoji = "♈",
        dateRange = "Mar 21 – Apr 19",
        element = "Fire 🔥",
        dailyForecast = "Bold creative energy surrounds your projects today! Stepping up to mentor or share a resource in the community sparks an unexpected breakthrough.",
        financeAndCareerVibe = "Great day to launch a new savings milestone or pitch a fresh idea.",
        communityCompatibility = "Leo ♌ & Sagittarius ♐",
        luckyNumber = 7,
        luckyColorName = "Crimson Flare",
        luckyColorHex = "#EF4444",
        cosmic3DStyleId = "SOLAR_FLARE"
    ),
    ZodiacHoroscopeReading(
        id = "taurus",
        signName = "Taurus",
        symbolEmoji = "♉",
        dateRange = "Apr 20 – May 20",
        element = "Earth 🌿",
        dailyForecast = "Steady persistence pays off today. Focus on grounding your daily routine, enjoying your favorite background music, and strengthening long-term stability.",
        financeAndCareerVibe = "High clarity for budgeting, building your emergency reserve, and practical goals.",
        communityCompatibility = "Virgo ♍ & Capricorn ♑",
        luckyNumber = 6,
        luckyColorName = "Cyber Emerald",
        luckyColorHex = "#10B981",
        cosmic3DStyleId = "AURORA"
    ),
    ZodiacHoroscopeReading(
        id = "gemini",
        signName = "Gemini",
        symbolEmoji = "♊",
        dateRange = "May 21 – Jun 20",
        element = "Air 🌬️",
        dailyForecast = "Your curiosity and communication shine! Dive into a new study topic, connect with a new friend via QR code, or collaborate on creative media.",
        financeAndCareerVibe = "Learning a new digital or STEM skill opens doors to future income streams.",
        communityCompatibility = "Libra ♎ & Aquarius ♒",
        luckyNumber = 5,
        luckyColorName = "Holo Cyan",
        luckyColorHex = "#06B6D4",
        cosmic3DStyleId = "CYBER_GRID"
    ),
    ZodiacHoroscopeReading(
        id = "cancer",
        signName = "Cancer",
        symbolEmoji = "♋",
        dateRange = "Jun 21 – Jul 22",
        element = "Water 🌊",
        dailyForecast = "Empathy and community support are your superpowers today. Reaching out to check in on a fellow veteran, student, or neighbor brings mutual joy.",
        financeAndCareerVibe = "Focus on home stability, family savings goals, and protective buffers.",
        communityCompatibility = "Scorpio ♏ & Pisces ♓",
        luckyNumber = 2,
        luckyColorName = "Moonlit Teal",
        luckyColorHex = "#14B8A6",
        cosmic3DStyleId = "AURORA"
    ),
    ZodiacHoroscopeReading(
        id = "leo",
        signName = "Leo",
        symbolEmoji = "♌",
        dateRange = "Jul 23 – Aug 22",
        element = "Fire ☀️",
        dailyForecast = "Warmth and leadership flow naturally today! Showcase your 3D profile theme, celebrate a peer's milestone, and inspire everyone around you.",
        financeAndCareerVibe = "Confidence in career interviews and creative showcases brings recognition.",
        communityCompatibility = "Aries ♈ & Libra ♎",
        luckyNumber = 1,
        luckyColorName = "Solar Gold",
        luckyColorHex = "#F59E0B",
        cosmic3DStyleId = "SOLAR_FLARE"
    ),
    ZodiacHoroscopeReading(
        id = "virgo",
        signName = "Virgo",
        symbolEmoji = "♍",
        dateRange = "Aug 23 – Sep 22",
        element = "Earth 🌾",
        dailyForecast = "Precision and thoughtful planning make today extra productive. Organizing your study flashcards or career roadmap creates instant peace of mind.",
        financeAndCareerVibe = "Ideal day to review your Spending History Bar Chart and optimize savings.",
        communityCompatibility = "Taurus ♉ & Cancer ♋",
        luckyNumber = 14,
        luckyColorName = "Sage Mint",
        luckyColorHex = "#34D399",
        cosmic3DStyleId = "CRYSTAL_PRISM"
    ),
    ZodiacHoroscopeReading(
        id = "libra",
        signName = "Libra",
        symbolEmoji = "♎",
        dateRange = "Sep 23 – Oct 22",
        element = "Air ⚖️",
        dailyForecast = "Harmony, fairness, and artistic expression take center stage—embodying 'We Are All Equal'. Great day for partnerships and customizing your 3D aesthetic.",
        financeAndCareerVibe = "Balanced partnerships and fair collaborations bring shared prosperity.",
        communityCompatibility = "Gemini ♊ & Leo ♌",
        luckyNumber = 9,
        luckyColorName = "Rose Quartz",
        luckyColorHex = "#EC4899",
        cosmic3DStyleId = "CRYSTAL_PRISM"
    ),
    ZodiacHoroscopeReading(
        id = "scorpio",
        signName = "Scorpio",
        symbolEmoji = "♏",
        dateRange = "Oct 23 – Nov 21",
        element = "Water 🌌",
        dailyForecast = "Deep focus and transformative determination empower you today. Dive beneath the surface on a research goal or creative project for remarkable results.",
        financeAndCareerVibe = "Strategic long-term planning and compounding investments favor you.",
        communityCompatibility = "Cancer ♋ & Pisces ♓",
        luckyNumber = 8,
        luckyColorName = "Royal Violet",
        luckyColorHex = "#8B5CF6",
        cosmic3DStyleId = "HYPERCUBE_6D"
    ),
    ZodiacHoroscopeReading(
        id = "sagittarius",
        signName = "Sagittarius",
        symbolEmoji = "♐",
        dateRange = "Nov 22 – Dec 21",
        element = "Fire 🏹",
        dailyForecast = "Optimism and big-picture vision light up your path! Explore new educational resources, connect across cultures, and aim for an ambitious milestone.",
        financeAndCareerVibe = "Scholarships, fellowships, and entrepreneurial ideas gain momentum.",
        communityCompatibility = "Aries ♈ & Aquarius ♒",
        luckyNumber = 3,
        luckyColorName = "Indigo Horizon",
        luckyColorHex = "#6366F1",
        cosmic3DStyleId = "HYPERCUBE_6D"
    ),
    ZodiacHoroscopeReading(
        id = "capricorn",
        signName = "Capricorn",
        symbolEmoji = "♑",
        dateRange = "Dec 22 – Jan 19",
        element = "Earth 🏔️",
        dailyForecast = "Disciplined momentum carries you closer to a major goal today. Every step you log on your career or financial timeline builds lasting legacy.",
        financeAndCareerVibe = "Strongest alignment for career milestones, certifications, and savings goals.",
        communityCompatibility = "Taurus ♉ & Virgo ♍",
        luckyNumber = 10,
        luckyColorName = "Cobalt Steel",
        luckyColorHex = "#3B82F6",
        cosmic3DStyleId = "CYBER_GRID"
    ),
    ZodiacHoroscopeReading(
        id = "aquarius",
        signName = "Aquarius",
        symbolEmoji = "♒",
        dateRange = "Jan 20 – Feb 18",
        element = "Air ⚡",
        dailyForecast = "Innovative ideas and humanitarian spirit shine brightly! Your unique perspective helps solve community challenges and inspires technological creativity.",
        financeAndCareerVibe = "Tech innovation, AI tools, and community-driven projects thrive today.",
        communityCompatibility = "Gemini ♊ & Sagittarius ♐",
        luckyNumber = 11,
        luckyColorName = "Electric Sky",
        luckyColorHex = "#38BDF8",
        cosmic3DStyleId = "HYPERCUBE_6D"
    ),
    ZodiacHoroscopeReading(
        id = "pisces",
        signName = "Pisces",
        symbolEmoji = "♓",
        dateRange = "Feb 19 – Mar 20",
        element = "Water 🌠",
        dailyForecast = "Imagination, music, and heartfelt kindness guide your day. Queue your favorite soundtrack in the 6D Jukebox and turn inspiration into art.",
        financeAndCareerVibe = "Intuitive creative work and generous peer support bring rewarding returns.",
        communityCompatibility = "Cancer ♋ & Scorpio ♏",
        luckyNumber = 12,
        luckyColorName = "Nebula Orchid",
        luckyColorHex = "#A855F7",
        cosmic3DStyleId = "AURORA"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyHoroscopesCosmicCard(
    onApplyCosmicTheme: (visualStyleId: String, colorHex: String, zodiacEmoji: String) -> Unit = { _, _, _ -> },
    onShareHoroscopeToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedZodiac by remember { mutableStateOf(ZODIAC_HOROSCOPES_CATALOG[7]) } // Default Scorpio (Nov 10)
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val signAccent = parseThemeHex(selectedZodiac.luckyColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_horoscopes_cosmic_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1024).copy(alpha = 0.94f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(signAccent, SolarAmber, HoloCyan, NeonPink)
            )
        )
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(signAccent.copy(alpha = 0.25f))
                            .border(1.5.dp, signAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedZodiac.symbolEmoji, fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "✨ DAILY HOROSCOPES & 6D COSMIC ZODIAC HUB",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "${selectedZodiac.symbolEmoji} ${selectedZodiac.signName} (${selectedZodiac.dateRange})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Element: ${selectedZodiac.element} • Match: ${selectedZodiac.communityCompatibility}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // All 12 Zodiac Sign Selector Pills
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ZODIAC_HOROSCOPES_CATALOG.forEach { sign ->
                    val isSelected = selectedZodiac.id == sign.id
                    val pillColor = parseThemeHex(sign.luckyColorHex)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) pillColor.copy(alpha = 0.30f) else Color(0xFF1E293B).copy(alpha = 0.75f),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) pillColor else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedZodiac = sign
                                statusBanner = null
                            }
                            .testTag("zodiac_sign_chip_${sign.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = sign.symbolEmoji, fontSize = 14.sp)
                            Text(
                                text = sign.signName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Daily Horoscope Reading Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.88f),
                border = BorderStroke(1.dp, signAccent.copy(alpha = 0.65f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("horoscope_daily_reading_box")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔮 Today's Cosmic Forecast for ${selectedZodiac.signName}:",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = selectedZodiac.dailyForecast,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "💫 Finance & Career Vibe: ${selectedZodiac.financeAndCareerVibe}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎨 Lucky Color: ${selectedZodiac.luckyColorName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = signAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "🔢 Lucky #: ${selectedZodiac.luckyNumber} • 🤝 Best Match: ${selectedZodiac.communityCompatibility}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Action Row: Apply Zodiac 3D Theme + Share Horoscope to Community Wall
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onApplyCosmicTheme(
                            selectedZodiac.cosmic3DStyleId,
                            selectedZodiac.luckyColorHex,
                            selectedZodiac.symbolEmoji
                        )
                        statusBanner = "✨ Applied ${selectedZodiac.symbolEmoji} ${selectedZodiac.signName}'s ${selectedZodiac.luckyColorName} (${selectedZodiac.cosmic3DStyleId}) 3D Theme!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = signAccent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("apply_zodiac_theme_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply ${selectedZodiac.signName} 3D Theme",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        onShareHoroscopeToWall(
                            "🔮 ${selectedZodiac.symbolEmoji} ${selectedZodiac.signName} Daily Horoscope (${selectedZodiac.dateRange})",
                            "${selectedZodiac.dailyForecast}\n• Finance & Career Vibe: ${selectedZodiac.financeAndCareerVibe}\n• Lucky Color: ${selectedZodiac.luckyColorName} • Lucky #: ${selectedZodiac.luckyNumber}"
                        )
                        statusBanner = "📣 Shared your ${selectedZodiac.signName} Daily Horoscope to the Community Wall!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SolarAmber),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("share_horoscope_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Horoscope",
                        tint = SolarAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share Horoscope",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            AnimatedVisibility(visible = !statusBanner.isNullOrBlank()) {
                if (!statusBanner.isNullOrBlank()) {
                    Text(
                        text = statusBanner.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
