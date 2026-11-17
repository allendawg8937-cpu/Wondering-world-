package com.example.dating

import java.util.Calendar
import java.util.Locale

/**
 * Third-party age assurance providers supported by the isolated 18+ Sanctuary.
 */
enum class AgeVerificationProvider(
    val id: String,
    val displayName: String,
    val summary: String,
    val portalUrl: String,
    val tokenPrefix: String
) {
    GOOGLE_PLAY_AGE_SIGNALS(
        id = "google_play_age",
        displayName = "Google Play Age Signals",
        summary = "On-device Play Integrity & privacy-preserving 18+ signal check",
        portalUrl = "https://support.google.com/googleplay/answer/10070489",
        tokenPrefix = "GPAS-18-"
    ),
    YOTI_DIGITAL_AGE(
        id = "yoti_age",
        displayName = "Yoti Age Estimation",
        summary = "Privacy-first biometric age estimation & encrypted reusable token",
        portalUrl = "https://www.yoti.com/business/age-verification/",
        tokenPrefix = "YOTI-18-"
    ),
    PERSONA_IDENTITY(
        id = "persona_id",
        displayName = "Persona 18+ Assurance",
        summary = "Independent third-party 18+ identity verification handoff",
        portalUrl = "https://withpersona.com/",
        tokenPrefix = "PRSN-18-"
    )
}

sealed interface DatingVerificationStatus {
    data object Locked : DatingVerificationStatus
    data class DeniedUnderage(val computedAgeYears: Int, val reason: String) : DatingVerificationStatus
    data class PendingProviderHandshake(val provider: AgeVerificationProvider, val computedAgeYears: Int) : DatingVerificationStatus
    data class Verified18Plus(
        val verifiedAgeYears: Int,
        val provider: AgeVerificationProvider,
        val assuranceToken: String,
        val isolatedAlias: String
    ) : DatingVerificationStatus
}

data class IsolatedDatingCandidate(
    val id: String,
    val alias: String,
    val age: Int,
    val cityRegion: String,
    val headline: String,
    val promptAnswer: String,
    val sharedValues: List<String>,
    val accentHex: String
)

data class IsolatedDatingMessage(
    val id: String,
    val recipientAlias: String,
    val text: String,
    val isMine: Boolean
)

data class IsolatedDatingUiState(
    val birthYearInput: String = "",
    val birthMonthInput: String = "",
    val birthDayInput: String = "",
    val isolatedAliasInput: String = "",
    val selectedProvider: AgeVerificationProvider = AgeVerificationProvider.GOOGLE_PLAY_AGE_SIGNALS,
    val providerReferenceCode: String = "",
    val hasAcceptedConductAndPrivacyPledge: Boolean = false,
    val verificationStatus: DatingVerificationStatus = DatingVerificationStatus.Locked,
    val activeSection: String = "DISCOVER", // DISCOVER, MATCHES, SAFETY_GATE
    val candidateIndex: Int = 0,
    val likedCandidateIds: Set<String> = emptySet(),
    val passedCandidateIds: Set<String> = emptySet(),
    val blockedCandidateIds: Set<String> = emptySet(),
    val isolatedChats: List<IsolatedDatingMessage> = emptyList(),
    val statusNotice: String? = null
)

object AgeGateCalculator {
    fun calculateAgeYears(
        year: Int,
        month: Int,
        day: Int,
        referenceCalendar: Calendar = Calendar.getInstance()
    ): Int? {
        if (year !in 1900..referenceCalendar.get(Calendar.YEAR)) return null
        if (month !in 1..12) return null
        val checkCal = (referenceCalendar.clone() as Calendar).apply {
            isLenient = false
            clear()
            set(year, month - 1, day)
        }
        return try {
            val resolvedYear = checkCal.get(Calendar.YEAR)
            val resolvedMonth = checkCal.get(Calendar.MONTH) + 1
            val resolvedDay = checkCal.get(Calendar.DAY_OF_MONTH)
            if (resolvedYear != year || resolvedMonth != month || resolvedDay != day) {
                return null
            }
            val nowYear = referenceCalendar.get(Calendar.YEAR)
            val nowMonth = referenceCalendar.get(Calendar.MONTH) + 1
            val nowDay = referenceCalendar.get(Calendar.DAY_OF_MONTH)
            var age = nowYear - year
            if (nowMonth < month || (nowMonth == month && nowDay < day)) {
                age -= 1
            }
            if (age < 0 || age > 120) null else age
        } catch (_: Exception) {
            null
        }
    }

    fun isValidAssuranceToken(token: String, provider: AgeVerificationProvider): Boolean {
        val clean = token.trim().uppercase(Locale.US)
        return clean.length >= 10 &&
            (clean.startsWith(provider.tokenPrefix) || clean.matches(Regex("^[A-Z0-9_-]{10,48}$")))
    }
}

val ISOLATED_DATING_POOL: List<IsolatedDatingCandidate> = listOf(
    IsolatedDatingCandidate(
        id = "iso_claire_27",
        alias = "Claire S.",
        age = 27,
        cityRegion = "Pacific Northwest",
        headline = "Architecture illustrator & weekend botanical garden explorer",
        promptAnswer = "Ideal Saturday starts at a quiet pour-over spot and ends at a sunset ferry ride.",
        sharedValues = listOf("Kindness", "Creative Arts", "Long Walks"),
        accentHex = "#FB7185"
    ),
    IsolatedDatingCandidate(
        id = "iso_julian_31",
        alias = "Julian M.",
        age = 31,
        cityRegion = "Mountain West",
        headline = "Acoustic guitarist, volunteer trail steward & cookbook collector",
        promptAnswer = "Looking for real conversation, mutual respect, and someone who laughs easily.",
        sharedValues = listOf("Live Music", "Outdoor Trails", "Slow Mornings"),
        accentHex = "#C084FC"
    ),
    IsolatedDatingCandidate(
        id = "iso_nadia_29",
        alias = "Nadia K.",
        age = 29,
        cityRegion = "Great Lakes",
        headline = "Library program coordinator & vinyl jazz enthusiast",
        promptAnswer = "Tell me the book or album that changed how you see the world.",
        sharedValues = listOf("Independent Cinema", "Books & Coffee", "Community"),
        accentHex = "#38BDF8"
    ),
    IsolatedDatingCandidate(
        id = "iso_omar_33",
        alias = "Omar T.",
        age = 33,
        cityRegion = "Southwest",
        headline = "Ceramicist, museum docent & homemade pasta perfectionist",
        promptAnswer = "Best first meetup is browsing a farmers market and comparing favorite playlists.",
        sharedValues = listOf("Artisan Cooking", "Museums", "Thoughtful Dates"),
        accentHex = "#34D399"
    )
)
