package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class StagesOfLifePersona(
    val displayName: String,
    val subtitle: String,
    val ageRangeLabel: String,
    val badgeEmoji: String,
    val accentHex: String,
    val roleKey: String
) {
    KID(
        displayName = "Kid",
        subtitle = "Curious Explorer",
        ageRangeLabel = "Ages 6-12",
        badgeEmoji = "🚀",
        accentHex = "#00E5FF",
        roleKey = "kid"
    ),
    TEEN(
        displayName = "Teen",
        subtitle = "Creative Navigator",
        ageRangeLabel = "Ages 13-17",
        badgeEmoji = "🎵",
        accentHex = "#EC4899",
        roleKey = "teen"
    ),
    STUDENT(
        displayName = "Student",
        subtitle = "Higher Ed & Scholar",
        ageRangeLabel = "College & Trade",
        badgeEmoji = "🎓",
        accentHex = "#A855F7",
        roleKey = "student"
    ),
    ADULT(
        displayName = "Adult",
        subtitle = "Life, Wellness & Family",
        ageRangeLabel = "Ages 18+",
        badgeEmoji = "🏔️",
        accentHex = "#10B981",
        roleKey = "adult"
    ),
    BUSINESS(
        displayName = "Business",
        subtitle = "Founder & Enterprise",
        ageRangeLabel = "Entrepreneurs",
        badgeEmoji = "💼",
        accentHex = "#F59E0B",
        roleKey = "business"
    ),
    VETERAN(
        displayName = "Veteran",
        subtitle = "Service & Honor",
        ageRangeLabel = "Military & Res.",
        badgeEmoji = "🛡️",
        accentHex = "#FF6D00",
        roleKey = "veteran"
    );

    companion object {
        fun fromRoleOrName(value: String?): StagesOfLifePersona {
            if (value.isNullOrBlank()) return VETERAN
            val clean = value.trim()
            return entries.firstOrNull {
                it.name.equals(clean, ignoreCase = true) ||
                    it.roleKey.equals(clean, ignoreCase = true) ||
                    clean.contains(it.roleKey, ignoreCase = true)
            } ?: VETERAN
        }
    }
}

data class PlanetUserProfileState(
    val profileId: String = "primary_user",
    val displayName: String = "Jordan Vance",
    val handle: String = "@jordan_vance",
    val roleBadge: String = "Creation Planet & Wondering World Pioneer",
    val bio: String = "We are all equal. Building community, creativity, and stability across every stage of life.",
    val statusText: String = "Online • Creation Planet & Wondering World Unified Hub",
    val avatarColorIndex: Int = 0,
    val persona: StagesOfLifePersona = StagesOfLifePersona.VETERAN
)

data class PlanetAccessibilitySettings(
    val isDarkMode: Boolean = true,
    val useDynamicColor: Boolean = false,
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val dyslexiaSpacing: Boolean = false,
    val screenReaderHints: Boolean = true,
    val offlineModeManual: Boolean = false,
    val preferredLanguage: String = "English (US)",
    val notificationsEnabled: Boolean = true,
    val autoSyncWhenOnline: Boolean = true
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val profileId: String = "primary_user",
    val displayName: String = "Jordan Vance",
    val handle: String = "@jordan_vance",
    val roleBadge: String = "Creation Planet & Wondering World Pioneer",
    val bio: String = "We are all equal. Building community, creativity, and stability across every stage of life.",
    val statusText: String = "Online • Creation Planet & Wondering World Unified Hub",
    val avatarColorIndex: Int = 0,
    val personaName: String = StagesOfLifePersona.VETERAN.name,
    val isDarkMode: Boolean = true,
    val useDynamicColor: Boolean = false,
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val dyslexiaSpacing: Boolean = false,
    val screenReaderHints: Boolean = true,
    val offlineModeManual: Boolean = false,
    val preferredLanguage: String = "English (US)",
    val notificationsEnabled: Boolean = true,
    val autoSyncWhenOnline: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "financial_tracker_entries")
data class FinancialTrackerEntryEntity(
    @PrimaryKey
    val entryId: String,
    val title: String,
    val category: String,
    val entryType: String, // "INCOME", "EXPENSE", "SAVINGS_GOAL", "LOAN_ESTIMATE"
    val amountCents: Long,
    val targetCents: Long = 0L,
    val personaStage: String = StagesOfLifePersona.ADULT.name,
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

fun UserProfileEntity.toUserProfileState(): PlanetUserProfileState {
    return PlanetUserProfileState(
        profileId = profileId,
        displayName = displayName,
        handle = handle,
        roleBadge = roleBadge,
        bio = bio,
        statusText = statusText,
        avatarColorIndex = avatarColorIndex,
        persona = StagesOfLifePersona.fromRoleOrName(personaName)
    )
}

fun UserProfileEntity.toAccessibilitySettings(): PlanetAccessibilitySettings {
    return PlanetAccessibilitySettings(
        isDarkMode = isDarkMode,
        useDynamicColor = useDynamicColor,
        highContrast = highContrast,
        largeText = largeText,
        dyslexiaSpacing = dyslexiaSpacing,
        screenReaderHints = screenReaderHints,
        offlineModeManual = offlineModeManual,
        preferredLanguage = preferredLanguage,
        notificationsEnabled = notificationsEnabled,
        autoSyncWhenOnline = autoSyncWhenOnline
    )
}

fun buildUserProfileEntity(
    profile: PlanetUserProfileState,
    settings: PlanetAccessibilitySettings,
    updatedAtMillis: Long = System.currentTimeMillis()
): UserProfileEntity {
    return UserProfileEntity(
        profileId = profile.profileId,
        displayName = profile.displayName,
        handle = profile.handle,
        roleBadge = profile.roleBadge,
        bio = profile.bio,
        statusText = profile.statusText,
        avatarColorIndex = profile.avatarColorIndex,
        personaName = profile.persona.name,
        isDarkMode = settings.isDarkMode,
        useDynamicColor = settings.useDynamicColor,
        highContrast = settings.highContrast,
        largeText = settings.largeText,
        dyslexiaSpacing = settings.dyslexiaSpacing,
        screenReaderHints = settings.screenReaderHints,
        offlineModeManual = settings.offlineModeManual,
        preferredLanguage = settings.preferredLanguage,
        notificationsEnabled = settings.notificationsEnabled,
        autoSyncWhenOnline = settings.autoSyncWhenOnline,
        updatedAtMillis = updatedAtMillis
    )
}
