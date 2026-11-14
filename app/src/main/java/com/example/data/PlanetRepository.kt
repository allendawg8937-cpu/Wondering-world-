package com.example.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlanetRepository(
    private val context: Context,
    private val database: PlanetDatabase = PlanetDatabase.getInstance(context)
) {
    private val userProfileDao: UserProfileDao = database.userProfileDao()
    private val planetDao: PlanetDao = database.planetDao()

    val primaryUserProfileFlow: Flow<UserProfileEntity?> =
        userProfileDao.observeUserProfile("primary_user")

    val allUserProfilesFlow: Flow<List<UserProfileEntity>> =
        userProfileDao.getAllUserProfiles()

    val financialEntriesFlow: Flow<List<FinancialTrackerEntryEntity>> =
        planetDao.observeFinancialEntries()

    suspend fun ensureSeeded(
        displayName: String = "Jordan Vance",
        persona: StagesOfLifePersona = StagesOfLifePersona.VETERAN,
        isDarkMode: Boolean = true
    ): UserProfileEntity = withContext(Dispatchers.IO) {
        val existing = userProfileDao.getUserProfileOnce("primary_user")
        val resolvedProfile = if (existing == null) {
            val seed = UserProfileEntity(
                profileId = "primary_user",
                displayName = displayName.ifBlank { "Jordan Vance" },
                handle = "@${displayName.lowercase(Locale.US).replace(" ", "_").ifBlank { "jordan_vance" }}",
                roleBadge = "Creation Planet & Wondering World Pioneer",
                bio = "We are all equal. Building community, creativity, and stability across every stage of life.",
                statusText = "Online • Creation Planet + Wondering World Unified Hub",
                avatarColorIndex = persona.ordinal,
                personaName = persona.name,
                isDarkMode = isDarkMode,
                useDynamicColor = false,
                updatedAtMillis = System.currentTimeMillis()
            )
            userProfileDao.upsertUserProfile(seed)
            seed
        } else {
            existing
        }

        val existingFinance = planetDao.getAllFinancialEntriesOnce()
        if (existingFinance.isEmpty()) {
            val defaultEntries = listOf(
                FinancialTrackerEntryEntity(
                    entryId = "fin_emergency_vault",
                    title = "Emergency Stability & Housing Reserve",
                    category = "Savings Goal",
                    entryType = "SAVINGS_GOAL",
                    amountCents = 425000L,
                    targetCents = 600000L,
                    personaStage = StagesOfLifePersona.ADULT.name,
                    notes = "3-month essential living & 211 stability cushion"
                ),
                FinancialTrackerEntryEntity(
                    entryId = "fin_fafsa_grant_stipend",
                    title = "STEM Scholarship & Pell Grant Allocation",
                    category = "Education & Tuition",
                    entryType = "INCOME",
                    amountCents = 365000L,
                    targetCents = 500000L,
                    personaStage = StagesOfLifePersona.STUDENT.name,
                    notes = "Semester tuition & textbook allocation"
                ),
                FinancialTrackerEntryEntity(
                    entryId = "fin_auto_market_fund",
                    title = "Reliable Commuter EV / Hybrid Down Payment",
                    category = "Auto & Mobility",
                    entryType = "LOAN_ESTIMATE",
                    amountCents = 280000L,
                    targetCents = 450000L,
                    personaStage = StagesOfLifePersona.BUSINESS.name,
                    notes = "Auto Loan & Monthly Payment Estimator reserve"
                ),
                FinancialTrackerEntryEntity(
                    entryId = "fin_veteran_gi_housing",
                    title = "VA GI Bill & PACT Act Monthly Benefit",
                    category = "Veteran Benefits",
                    entryType = "INCOME",
                    amountCents = 245000L,
                    targetCents = 245000L,
                    personaStage = StagesOfLifePersona.VETERAN.name,
                    notes = "Tax-free housing & transition stipend"
                )
            )
            planetDao.upsertFinancialEntries(defaultEntries)
        }

        resolvedProfile
    }

    suspend fun upsertUserProfile(entity: UserProfileEntity) = withContext(Dispatchers.IO) {
        userProfileDao.upsertUserProfile(entity.copy(updatedAtMillis = System.currentTimeMillis()))
    }

    suspend fun syncIdentityAndPersona(
        displayName: String,
        bio: String,
        persona: StagesOfLifePersona,
        isDarkMode: Boolean,
        useDynamicColor: Boolean
    ): UserProfileEntity = withContext(Dispatchers.IO) {
        val current = userProfileDao.getUserProfileOnce("primary_user") ?: ensureSeeded(displayName, persona, isDarkMode)
        val updated = current.copy(
            displayName = displayName.ifBlank { current.displayName },
            handle = "@${displayName.lowercase(Locale.US).replace(" ", "_").ifBlank { "jordan_vance" }}",
            bio = bio.ifBlank { current.bio },
            personaName = persona.name,
            avatarColorIndex = persona.ordinal,
            isDarkMode = isDarkMode,
            useDynamicColor = useDynamicColor,
            updatedAtMillis = System.currentTimeMillis()
        )
        userProfileDao.upsertUserProfile(updated)
        updated
    }

    suspend fun updatePreferences(settings: PlanetAccessibilitySettings): UserProfileEntity =
        withContext(Dispatchers.IO) {
            val current = userProfileDao.getUserProfileOnce("primary_user") ?: ensureSeeded()
            userProfileDao.updateProfilePreferences(
                profileId = current.profileId,
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
                updatedAtMillis = System.currentTimeMillis()
            )
            userProfileDao.getUserProfileOnce("primary_user") ?: current
        }

    suspend fun addFinancialTrackerEntry(
        title: String,
        category: String,
        entryType: String,
        amountCents: Long,
        targetCents: Long,
        personaStage: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val entry = FinancialTrackerEntryEntity(
            entryId = "fin_${System.currentTimeMillis()}",
            title = title.trim().ifBlank { "Financial Goal Entry" },
            category = category.trim().ifBlank { "General" },
            entryType = entryType.trim().ifBlank { "SAVINGS_GOAL" },
            amountCents = amountCents,
            targetCents = targetCents,
            personaStage = personaStage,
            notes = notes.trim()
        )
        planetDao.upsertFinancialEntry(entry)
    }

    /**
     * Retrieves all user profile data (`user_profiles`) and financial tracker entries
     * (`financial_tracker_entries`) from the Room database and converts them into a
     * structured, formatted JSON string for local backup.
     */
    suspend fun retrieveAndExportProfileAndFinancialJson(): String = withContext(Dispatchers.IO) {
        ensureSeeded()
        val profiles = userProfileDao.getAllUserProfilesOnce()
        val financialEntries = planetDao.getAllFinancialEntriesOnce()

        val rootJson = JSONObject()
        rootJson.put("schemaVersion", 2)
        rootJson.put("platform", "Wondering World + Creation Planet Unified Platform")
        rootJson.put("databaseName", "creation_planet_wondering_world.db")
        rootJson.put(
            "exportedAtIso",
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
        )
        rootJson.put("exportedAtMillis", System.currentTimeMillis())

        val profilesArray = JSONArray()
        profiles.forEach { profile ->
            val profileObj = JSONObject().apply {
                put("profileId", profile.profileId)
                put("displayName", profile.displayName)
                put("handle", profile.handle)
                put("roleBadge", profile.roleBadge)
                put("bio", profile.bio)
                put("statusText", profile.statusText)
                put("avatarColorIndex", profile.avatarColorIndex)
                put("personaName", profile.personaName)
                put(
                    "preferences",
                    JSONObject().apply {
                        put("isDarkMode", profile.isDarkMode)
                        put("useDynamicColor", profile.useDynamicColor)
                        put("highContrast", profile.highContrast)
                        put("largeText", profile.largeText)
                        put("dyslexiaSpacing", profile.dyslexiaSpacing)
                        put("screenReaderHints", profile.screenReaderHints)
                        put("offlineModeManual", profile.offlineModeManual)
                        put("preferredLanguage", profile.preferredLanguage)
                        put("notificationsEnabled", profile.notificationsEnabled)
                        put("autoSyncWhenOnline", profile.autoSyncWhenOnline)
                    }
                )
                put("updatedAtMillis", profile.updatedAtMillis)
            }
            profilesArray.put(profileObj)
        }
        rootJson.put("userProfiles", profilesArray)

        val financeArray = JSONArray()
        var totalAmountCents = 0L
        var totalTargetCents = 0L
        financialEntries.forEach { entry ->
            totalAmountCents += entry.amountCents
            totalTargetCents += entry.targetCents
            val entryObj = JSONObject().apply {
                put("entryId", entry.entryId)
                put("title", entry.title)
                put("category", entry.category)
                put("entryType", entry.entryType)
                put("amountCents", entry.amountCents)
                put("amountFormatted", String.format(Locale.US, "$%.2f", entry.amountCents / 100.0))
                put("targetCents", entry.targetCents)
                put("targetFormatted", String.format(Locale.US, "$%.2f", entry.targetCents / 100.0))
                put("personaStage", entry.personaStage)
                put("notes", entry.notes)
                put("createdAtMillis", entry.createdAtMillis)
            }
            financeArray.put(entryObj)
        }
        rootJson.put("financialTrackerEntries", financeArray)
        rootJson.put(
            "financialSummary",
            JSONObject().apply {
                put("entryCount", financialEntries.size)
                put("totalSavedOrLoggedCents", totalAmountCents)
                put(
                    "totalSavedOrLoggedFormatted",
                    String.format(Locale.US, "$%.2f", totalAmountCents / 100.0)
                )
                put("totalTargetCents", totalTargetCents)
                put(
                    "totalTargetFormatted",
                    String.format(Locale.US, "$%.2f", totalTargetCents / 100.0)
                )
            }
        )

        rootJson.toString(2)
    }

    /**
     * Saves the structured JSON string to local device storage (Downloads via MediaStore on API 29+
     * and app files directory) and triggers an Android system download/save/share intent so the
     * user can save the JSON backup locally.
     */
    fun triggerSystemJsonDownloadIntent(
        jsonString: String,
        fileName: String = "wondering_world_planet_room_backup_${System.currentTimeMillis()}.json"
    ): String {
        var savedLocationLabel = "App Backup Storage"

        // 1. Write to system Downloads collection on Android 10+ (zero-permission MediaStore.Downloads)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runCatching {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { output ->
                        output.write(jsonString.toByteArray(Charsets.UTF_8))
                    }
                    savedLocationLabel = "System Downloads/$fileName"
                }
            }
        }

        // 2. Also write a local backup file inside context.filesDir
        runCatching {
            val backupDir = File(context.filesDir, "room_json_backups").apply { mkdirs() }
            val localFile = File(backupDir, fileName)
            localFile.writeText(jsonString, Charsets.UTF_8)
            if (savedLocationLabel == "App Backup Storage") {
                savedLocationLabel = localFile.absolutePath
            }
        }

        // 3. Trigger Android System Share / Save Intent so the user can save or export the JSON immediately
        runCatching {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, fileName)
                putExtra(Intent.EXTRA_SUBJECT, "Room Database JSON Backup ($fileName)")
                putExtra(Intent.EXTRA_TEXT, jsonString)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(sendIntent, "Save or Export Room JSON Backup ($fileName)").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }

        return savedLocationLabel
    }
}
