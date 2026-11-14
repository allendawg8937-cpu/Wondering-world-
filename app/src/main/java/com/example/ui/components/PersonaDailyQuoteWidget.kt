package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.normalizePersonaStageId
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PersonaMotivationalQuote(
    val id: String,
    val personaId: String,
    val quote: String,
    val author: String,
    val encouragementTip: String,
    val focusBadge: String
)

val PERSONA_MOTIVATIONAL_QUOTES: List<PersonaMotivationalQuote> = listOf(
    // KID (5)
    PersonaMotivationalQuote(
        id = "kid_q1",
        personaId = "kid",
        quote = "You are braver than you believe, stronger than you seem, and smarter than you think.",
        author = "A.A. Milne",
        encouragementTip = "Try one new puzzle, drawing, or science question today—curiosity is your superpower!",
        focusBadge = "Curiosity Spark"
    ),
    PersonaMotivationalQuote(
        id = "kid_q2",
        personaId = "kid",
        quote = "Every mistake is proof that you are trying and learning something brand new.",
        author = "Cosmic Explorer Club",
        encouragementTip = "Celebrate effort over perfection—each try levels up your brain!",
        focusBadge = "Growth Explorer"
    ),
    PersonaMotivationalQuote(
        id = "kid_q3",
        personaId = "kid",
        quote = "A single kind word can brighten someone's whole day like sunshine after rain.",
        author = "Stages Of Life Kids Circle",
        encouragementTip = "Share one encouraging high-five or thank-you with a classmate or family member.",
        focusBadge = "Kindness Quest"
    ),
    PersonaMotivationalQuote(
        id = "kid_q4",
        personaId = "kid",
        quote = "The more that you read, the more things you will know. The more that you learn, the more places you'll go.",
        author = "Dr. Seuss",
        encouragementTip = "Read 15 minutes in a favorite science, space, or story book today.",
        focusBadge = "Reading Adventure"
    ),
    PersonaMotivationalQuote(
        id = "kid_q5",
        personaId = "kid",
        quote = "Small coins saved steadily build giant dreams tomorrow.",
        author = "Kid Savings Explorers",
        encouragementTip = "Check your savings jar and imagine what big project you want to build next.",
        focusBadge = "Starter Saver"
    ),

    // TEEN (5)
    PersonaMotivationalQuote(
        id = "teen_q1",
        personaId = "teen",
        quote = "Don't wait for inspiration to strike—create your own momentum by building one small thing today.",
        author = "Teen Creator Collective",
        encouragementTip = "Spend 20 focused minutes on a music track, sketch, or coding experiment.",
        focusBadge = "Creator Momentum"
    ),
    PersonaMotivationalQuote(
        id = "teen_q2",
        personaId = "teen",
        quote = "Your voice, your creativity, and your perspective are needed in rooms you haven't even entered yet.",
        author = "Youth Leadership Forum",
        encouragementTip = "Speak up thoughtfully in class, club practice, or a team discussion today.",
        focusBadge = "Authentic Voice"
    ),
    PersonaMotivationalQuote(
        id = "teen_q3",
        personaId = "teen",
        quote = "Small daily discipline beats once-a-month bursts of motivation every single time.",
        author = "Hackathon Mentors",
        encouragementTip = "Knock out your top homework or practice block before scrolling feeds tonight.",
        focusBadge = "Daily Focus"
    ),
    PersonaMotivationalQuote(
        id = "teen_q4",
        personaId = "teen",
        quote = "Be the friend who lifts others higher—real confidence never needs to put anyone else down.",
        author = "Stages Of Life Youth Council",
        encouragementTip = "Send an uplifting shout-out to a friend working hard toward a goal.",
        focusBadge = "Positive Impact"
    ),
    PersonaMotivationalQuote(
        id = "teen_q5",
        personaId = "teen",
        quote = "The future belongs to those who stay curious when things feel challenging.",
        author = "Eleanor Roosevelt (Inspired)",
        encouragementTip = "Bookmark one scholarship, workshop, or skill path to explore this week.",
        focusBadge = "Future Ready"
    ),

    // STUDENT (5)
    PersonaMotivationalQuote(
        id = "student_q1",
        personaId = "student",
        quote = "Success is the sum of small efforts, repeated day in and day out.",
        author = "Robert Collier",
        encouragementTip = "Finish one 25-minute flashcard sprint and review tomorrow's hardest lecture notes.",
        focusBadge = "Study Mastery"
    ),
    PersonaMotivationalQuote(
        id = "student_q2",
        personaId = "student",
        quote = "Education is the passport to the future, for tomorrow belongs to those who prepare for it today.",
        author = "Malcolm X",
        encouragementTip = "Check one FAFSA, grant, or paid internship deadline on your semester calendar.",
        focusBadge = "Academic Horizon"
    ),
    PersonaMotivationalQuote(
        id = "student_q3",
        personaId = "student",
        quote = "Asking questions isn't a sign of falling behind—it's how scholars turn confusion into clarity.",
        author = "Campus Study Mentors",
        encouragementTip = "Message a study buddy or ask one question during office hours today.",
        focusBadge = "Curious Scholar"
    ),
    PersonaMotivationalQuote(
        id = "student_q4",
        personaId = "student",
        quote = "Protect your sleep and your focus; an energized mind learns twice as fast in half the time.",
        author = "Academic Wellness Lab",
        encouragementTip = "Schedule a clean cutoff time tonight so tomorrow's study block feels sharp.",
        focusBadge = "Scholar Balance"
    ),
    PersonaMotivationalQuote(
        id = "student_q5",
        personaId = "student",
        quote = "Every lab, paper, and project you finish adds brick-by-brick confidence to your career foundation.",
        author = "CareerOneStop Advisors",
        encouragementTip = "Add one recent class project or skill win to your student portfolio.",
        focusBadge = "Career Launchpad"
    ),

    // ADULT (5)
    PersonaMotivationalQuote(
        id = "adult_q1",
        personaId = "adult",
        quote = "Balance is not something you find; it is something you design intentionally each morning.",
        author = "Jana Kingsford",
        encouragementTip = "Protect 20 minutes today for movement, reflection, or unhurried family time.",
        focusBadge = "Intentional Living"
    ),
    PersonaMotivationalQuote(
        id = "adult_q2",
        personaId = "adult",
        quote = "Peace of mind is built one steady habit at a time—automated savings, honest rest, and clear boundaries.",
        author = "Household Resilience Circle",
        encouragementTip = "Do a 2-minute check on your emergency reserve or weekly priority list.",
        focusBadge = "Steady Stability"
    ),
    PersonaMotivationalQuote(
        id = "adult_q3",
        personaId = "adult",
        quote = "You don't have to carry every responsibility alone; strong communities thrive when neighbors share support.",
        author = "Stages Of Life Community",
        encouragementTip = "Check in with a trusted friend or explore local 211 community resources if needed.",
        focusBadge = "Connected Strength"
    ),
    PersonaMotivationalQuote(
        id = "adult_q4",
        personaId = "adult",
        quote = "Progress in mid-career isn't about rushing—it's about aligning your energy with work that matters.",
        author = "Career Pivot Network",
        encouragementTip = "Write down one skill you want to sharpen or one milestone for this month.",
        focusBadge = "Purposeful Growth"
    ),
    PersonaMotivationalQuote(
        id = "adult_q5",
        personaId = "adult",
        quote = "Consistency outperforms intensity; showing up calmly for yourself today compounds for years.",
        author = "Wellness & Finance Coaches",
        encouragementTip = "Celebrate one small win you already accomplished this week.",
        focusBadge = "Daily Resilience"
    ),

    // BUSINESS (5)
    PersonaMotivationalQuote(
        id = "business_q1",
        personaId = "business",
        quote = "Vision without execution is just a daydream; consistent customer care turns ideas into lasting enterprises.",
        author = "Founders Roundtable",
        encouragementTip = "Follow up personally with one loyal customer, partner, or prospective lead today.",
        focusBadge = "Founder Execution"
    ),
    PersonaMotivationalQuote(
        id = "business_q2",
        personaId = "business",
        quote = "Protect your cash flow, clarify your metrics, and build systems that serve your team even on busy days.",
        author = "SBA Growth Mentors",
        encouragementTip = "Review your operating runway and flag the single highest-ROI task on your board.",
        focusBadge = "Capital Discipline"
    ),
    PersonaMotivationalQuote(
        id = "business_q3",
        personaId = "business",
        quote = "Great leaders don't create followers—they build environments where teammates and communities win together.",
        author = "Tom Peters (Inspired)",
        encouragementTip = "Recognize one teammate or local vendor who helped move your business forward.",
        focusBadge = "Servant Leadership"
    ),
    PersonaMotivationalQuote(
        id = "business_q4",
        personaId = "business",
        quote = "Every obstacle in operations is feedback pointing toward a stronger product or smarter workflow.",
        author = "Enterprise Builders",
        encouragementTip = "Simplify one recurring bottleneck or update a storefront listing today.",
        focusBadge = "Adaptive Strategy"
    ),
    PersonaMotivationalQuote(
        id = "business_q5",
        personaId = "business",
        quote = "Reputation is built in drops and measured in trust—deliver more clarity and reliability than expected.",
        author = "Main Street Innovators",
        encouragementTip = "Publish a concise progress update or client milestone for your community.",
        focusBadge = "Trust Builder"
    ),

    // VETERAN (5)
    PersonaMotivationalQuote(
        id = "veteran_q1",
        personaId = "veteran",
        quote = "Your service forged discipline, adaptability, and teamwork—strengths that remain an anchor for every chapter ahead.",
        author = "Veterans Transition Network",
        encouragementTip = "Connect with a fellow veteran peer or check one earned VA/GI Bill benefit today.",
        focusBadge = "Service Legacy"
    ),
    PersonaMotivationalQuote(
        id = "veteran_q2",
        personaId = "veteran",
        quote = "True strength includes knowing when to stand shoulder-to-shoulder with peers who understand your path.",
        author = "DAV & VFW Peer Mentors",
        encouragementTip = "Reach out to a battle buddy or mentor—and remember 988 Press 1 is always here 24/7.",
        focusBadge = "Camaraderie"
    ),
    PersonaMotivationalQuote(
        id = "veteran_q3",
        personaId = "veteran",
        quote = "Transitioning missions doesn't end your purpose; it opens new ways to lead, mentor, and build at home.",
        author = "Veteran Founders Guild",
        encouragementTip = "Log one career milestone, mentorship conversation, or personal wellness goal.",
        focusBadge = "Next Mission"
    ),
    PersonaMotivationalQuote(
        id = "veteran_q4",
        personaId = "veteran",
        quote = "Steadiness in storm and calm alike comes from taking care of body, mind, and brotherhood.",
        author = "Whole Health VA Circle",
        encouragementTip = "Schedule time outdoors or verify your upcoming wellness check-in.",
        focusBadge = "Whole Health"
    ),
    PersonaMotivationalQuote(
        id = "veteran_q5",
        personaId = "veteran",
        quote = "One conversation sharing what you've learned can light the path for a younger generation finding its footing.",
        author = "Stages Of Life Veterans Council",
        encouragementTip = "Share one practical piece of encouragement on the Community Wall today.",
        focusBadge = "Peer Mentorship"
    )
)

fun quotesForPersona(personaId: String): List<PersonaMotivationalQuote> {
    val normalized = normalizePersonaStageId(personaId)
    val matches = PERSONA_MOTIVATIONAL_QUOTES.filter { it.personaId == normalized }
    return matches.ifEmpty { PERSONA_MOTIVATIONAL_QUOTES.filter { it.personaId == "adult" } }
}

fun selectDailyPersonaQuote(
    personaId: String,
    dayOfYear: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR),
    offset: Int = 0
): PersonaMotivationalQuote {
    val pool = quotesForPersona(personaId)
    val rawIndex = (dayOfYear + offset) % pool.size
    val safeIndex = if (rawIndex < 0) rawIndex + pool.size else rawIndex
    return pool[safeIndex]
}

/**
 * Renders a crisp square PNG card featuring the current quote, author, daily encouragement,
 * and active persona branding ("Stages Of Life • <Persona> Mode").
 */
fun renderPersonaQuoteCardBitmap(
    quote: PersonaMotivationalQuote,
    activeStage: DashboardLifeStage,
    userDisplayName: String,
    widthPx: Int = 1080,
    heightPx: Int = 1080
): Bitmap {
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val stageArgb = parseThemeHex(activeStage.accentHex).toArgb()
    val cyanArgb = android.graphics.Color.parseColor("#00E5FF")
    val emeraldArgb = android.graphics.Color.parseColor("#10B981")
    val darkTop = android.graphics.Color.parseColor("#090E1A")
    val darkBottom = android.graphics.Color.parseColor("#131C31")

    // Cosmic gradient backdrop
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f,
            0f,
            widthPx.toFloat(),
            heightPx.toFloat(),
            intArrayOf(darkTop, darkBottom, stageArgb),
            floatArrayOf(0f, 0.72f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), bgPaint)

    // Subtle persona glow accents
    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = stageArgb
        alpha = 46
    }
    canvas.drawCircle(widthPx * 0.85f, heightPx * 0.16f, 220f, glowPaint)
    glowPaint.color = cyanArgb
    glowPaint.alpha = 34
    canvas.drawCircle(widthPx * 0.14f, heightPx * 0.84f, 180f, glowPaint)

    // Framed inner glass card
    val cardRect = RectF(56f, 56f, widthPx - 56f, heightPx - 56f)
    val cardFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(226, 15, 23, 42)
    }
    canvas.drawRoundRect(cardRect, 42f, 42f, cardFill)

    val cardBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        shader = LinearGradient(
            cardRect.left,
            cardRect.top,
            cardRect.right,
            cardRect.bottom,
            intArrayOf(stageArgb, cyanArgb, emeraldArgb),
            null,
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRoundRect(cardRect, 42f, 42f, cardBorder)

    // Top persona branding pill
    val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = stageArgb
        alpha = 52
    }
    val badgeRect = RectF(108f, 108f, widthPx - 108f, 180f)
    canvas.drawRoundRect(badgeRect, 36f, 36f, badgePaint)

    val badgeStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
        color = stageArgb
    }
    canvas.drawRoundRect(badgeRect, 36f, 36f, badgeStroke)

    val badgeTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val badgeLabel = "${activeStage.emoji}  ${activeStage.label.uppercase(Locale.US)} MODE • ${quote.focusBadge.uppercase(Locale.US)}"
    canvas.drawText(badgeLabel, 140f, 155f, badgeTextPaint)

    // Personalized header subtitle
    val cleanUser = userDisplayName.trim().ifBlank { activeStage.label }.take(36)
    val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cyanArgb
        textSize = 28f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("Daily Motivation • Curated for $cleanUser", 112f, 240f, subtitlePaint)

    // Decorative quote glyph
    val glyphPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = stageArgb
        alpha = 180
        textSize = 110f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }
    canvas.drawText("“", 104f, 355f, glyphPaint)

    // Main quote StaticLayout
    val quoteTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 46f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
    }
    val contentWidth = widthPx - 224
    val quoteLayout = StaticLayout.Builder
        .obtain("\"${quote.quote}\"", 0, quote.quote.length + 2, quoteTextPaint, contentWidth)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(10f, 1.08f)
        .build()

    canvas.save()
    canvas.translate(112f, 372f)
    quoteLayout.draw(canvas)
    canvas.restore()

    // Author attribution
    val authorY = (372f + quoteLayout.height + 54f).coerceAtMost(710f)
    val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = stageArgb
        textSize = 34f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("— ${quote.author}", 112f, authorY, authorPaint)

    // Encouragement highlight box
    val tipBoxTop = (authorY + 42f).coerceAtLeast(695f).coerceAtMost(770f)
    val tipBoxRect = RectF(108f, tipBoxTop, widthPx - 108f, 920f)
    val tipFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(42, 16, 185, 129)
    }
    canvas.drawRoundRect(tipBoxRect, 24f, 24f, tipFill)

    val tipBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = emeraldArgb
    }
    canvas.drawRoundRect(tipBoxRect, 24f, 24f, tipBorder)

    val tipPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#D1FAE5")
        textSize = 27f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val tipText = "Daily Boost: ${quote.encouragementTip}"
    val tipLayout = StaticLayout.Builder
        .obtain(tipText, 0, tipText.length, tipPaint, widthPx - 264)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(6f, 1.05f)
        .build()

    canvas.save()
    canvas.translate(132f, tipBoxTop + 26f)
    tipLayout.draw(canvas)
    canvas.restore()

    // Footer branding strip
    val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#94A3B8")
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("STAGES OF LIFE • WE ARE ALL EQUAL • wereallequals.world", 112f, heightPx - 92f, footerPaint)

    return bitmap
}

/**
 * Exports the current motivational quote card with persona branding as a PNG into cacheDir
 * and launches the Android share sheet without requiring broad storage permissions.
 */
fun exportAndSharePersonaQuoteImage(
    context: Context,
    quote: PersonaMotivationalQuote,
    activeStage: DashboardLifeStage,
    userDisplayName: String
): Result<Uri> = runCatching {
    val exportDir = File(context.cacheDir, "quote_cards").apply { mkdirs() }
    val imageFile = File(exportDir, "stages_of_life_${activeStage.stageId}_${quote.id}.png")
    val bitmap = renderPersonaQuoteCardBitmap(
        quote = quote,
        activeStage = activeStage,
        userDisplayName = userDisplayName
    )
    try {
        FileOutputStream(imageFile).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    } finally {
        bitmap.recycle()
    }

    val contentUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(
            Intent.EXTRA_SUBJECT,
            "${activeStage.emoji} ${activeStage.label} Daily Motivation — Stages Of Life"
        )
        putExtra(
            Intent.EXTRA_TEXT,
            "\"${quote.quote}\" — ${quote.author}\n\n${activeStage.emoji} ${activeStage.label} Mode • Stages Of Life"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent = Intent.createChooser(
        shareIntent,
        "Share ${activeStage.label} Quote Image"
    ).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    context.startActivity(chooserIntent)
    contentUri
}

/**
 * Daily Motivational Quote Widget that dynamically updates its quote, author, accent styling,
 * and personalized encouragement prompt whenever the user's selected persona changes.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonaDailyQuoteWidget(
    currentRole: String,
    userDisplayName: String = "Explorer",
    onShareQuoteToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    onExportQuoteImage: ((PersonaMotivationalQuote, DashboardLifeStage) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeStage = remember(currentRole) {
        DashboardLifeStage.fromRoleOrStageId(currentRole)
    }
    val stageColor = parseThemeHex(activeStage.accentHex)
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val todayFormatted = remember {
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    }

    val perPersonaOffsets = remember { mutableStateMapOf<String, Int>() }
    val favoriteQuoteIds = remember { mutableStateListOf<String>() }
    var statusBanner by remember(activeStage.stageId) { mutableStateOf<String?>(null) }

    val currentOffset = perPersonaOffsets[activeStage.stageId] ?: 0
    val pool = remember(activeStage.stageId) { quotesForPersona(activeStage.stageId) }
    val activeQuote = remember(activeStage.stageId, dayOfYear, currentOffset) {
        selectDailyPersonaQuote(
            personaId = activeStage.stageId,
            dayOfYear = dayOfYear,
            offset = currentOffset
        )
    }
    val isFavorited = favoriteQuoteIds.contains(activeQuote.id)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(stageColor, HoloCyan, CyberEmerald)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.05f)
            .testTag("persona_daily_quote_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Persona Stage Tag + Favorite Toggle
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
                                Brush.linearGradient(listOf(stageColor, HoloCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Daily Motivational Quote",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Daily Motivation",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = stageColor.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, stageColor),
                                modifier = Modifier
                                    .semantics(mergeDescendants = true) {}
                                    .testTag("persona_quote_stage_badge")
                            ) {
                                Text(
                                    text = "${activeStage.emoji} ${activeStage.label} Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = stageColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "$todayFormatted • Tailored for ${userDisplayName.substringBefore(" ").ifBlank { activeStage.label }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (isFavorited) {
                            favoriteQuoteIds.remove(activeQuote.id)
                            statusBanner = "Removed from saved quotes"
                        } else {
                            favoriteQuoteIds.add(activeQuote.id)
                            statusBanner = "Saved ${activeStage.label} quote (${favoriteQuoteIds.size} saved)"
                        }
                    },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("persona_quote_favorite_button")
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isFavorited) "Remove saved quote" else "Save motivational quote",
                        tint = if (isFavorited) SolarAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Animated Quote Body updating automatically when Persona or Quote changes
            AnimatedContent(
                targetState = activeQuote,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 6 })
                        .togetherWith(fadeOut(tween(160)) + slideOutVertically(tween(160)) { -it / 6 })
                },
                label = "persona_daily_quote_transition"
            ) { quoteItem ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = stageColor.copy(alpha = 0.11f),
                    border = BorderStroke(1.dp, stageColor.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
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
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            ) {
                                Text(
                                    text = quoteItem.focusBadge.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = stageColor,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = if (currentOffset == 0) "Today's Pick" else "Pick #${(pool.indexOf(quoteItem) + 1)} of ${pool.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "\"${quoteItem.quote}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 22.sp,
                            modifier = Modifier.testTag("persona_quote_text")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(stageColor)
                            )
                            Text(
                                text = "— ${quoteItem.author}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = stageColor,
                                modifier = Modifier.testTag("persona_quote_author")
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Personalized Encouragement",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = quoteItem.encouragementTip,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("persona_quote_encouragement_text")
                                )
                            }
                        }
                    }
                }
            }

            if (!statusBanner.isNullOrBlank()) {
                Text(
                    text = statusBanner!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("persona_quote_status_banner")
                )
            }

            // Action Controls: Navigation + Wall Share + Branded PNG Image Export
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            perPersonaOffsets[activeStage.stageId] = currentOffset - 1
                            statusBanner = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, stageColor.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("persona_quote_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Quote",
                            tint = stageColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            perPersonaOffsets[activeStage.stageId] = currentOffset + 1
                            statusBanner = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = stageColor,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("persona_quote_next_button")
                    ) {
                        Text(
                            text = "Next Quote",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next ${activeStage.label} Quote",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (currentOffset != 0) {
                        OutlinedButton(
                            onClick = {
                                perPersonaOffsets[activeStage.stageId] = 0
                                statusBanner = "Restored Today's ${activeStage.label} Quote"
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("persona_quote_today_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Today's Pick",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (onExportQuoteImage != null) {
                                onExportQuoteImage(activeQuote, activeStage)
                                statusBanner = "Exported ${activeStage.label} branded quote image!"
                            } else {
                                val result = exportAndSharePersonaQuoteImage(
                                    context = context,
                                    quote = activeQuote,
                                    activeStage = activeStage,
                                    userDisplayName = userDisplayName
                                )
                                statusBanner = if (result.isSuccess) {
                                    "Shared ${activeStage.label} branded quote image!"
                                } else {
                                    "Could not share quote image: ${result.exceptionOrNull()?.localizedMessage ?: "unknown error"}"
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        border = BorderStroke(1.2.dp, stageColor),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("persona_quote_export_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Export and share branded quote image",
                            tint = stageColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Image",
                            style = MaterialTheme.typography.labelMedium,
                            color = stageColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onShareQuoteToWall(
                                "${activeStage.emoji} Daily ${activeStage.label} Motivation",
                                "\"${activeQuote.quote}\" — ${activeQuote.author}\n${activeQuote.encouragementTip}"
                            )
                            statusBanner = "Shared quote to Community Wall!"
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        border = BorderStroke(1.dp, HoloCyan),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("persona_quote_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share quote to community wall",
                            tint = HoloCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Post to Wall",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoloCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
