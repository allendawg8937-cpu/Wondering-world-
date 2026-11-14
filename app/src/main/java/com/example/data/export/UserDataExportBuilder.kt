package com.example.data.export

import com.example.BuildConfig
import com.example.data.model.BudgetEntry
import com.example.data.model.CareerMilestone
import com.example.data.model.PersonaStageData
import com.example.data.model.Profile3DConfig
import com.example.data.model.UserProfile
import com.example.ui.SignUpFormState
import com.example.ui.components.VirtualCurrencyTransaction
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 * Aggregated snapshot of the user's Financial Goal Data and User Preferences
 * prepared for the 'Download Data' JSON exporter in Profile Settings.
 */
data class UserDataExportPayload(
    val profile: UserProfile,
    val signUpForm: SignUpFormState,
    val userEmail: String,
    val userPhone: String,
    val avatarEmojiBadge: String,
    val isDarkMode: Boolean,
    val backgroundTransparency: Float,
    val currentVisualStyleId: String,
    val selectedSportsIds: Set<String>,
    val learnedInterests: Set<String>,
    val learnedLifestyle: Set<String>,
    val learnedSchedule: Set<String>,
    val learnedResearch: Set<String>,
    val learnedSignals: List<String>,
    val budgetEntries: List<BudgetEntry>,
    val careerMilestones: List<CareerMilestone>,
    val virtualCurrencyBalance: Long,
    val dailyBonusClaimed: Boolean,
    val loginStreakDays: Int,
    val currencyTransactions: List<VirtualCurrencyTransaction>,
    val personaStageDataMap: Map<String, PersonaStageData>,
    val profile3DConfigs: List<Profile3DConfig>
)

object UserDataExportBuilder {

    fun defaultExportFileName(
        displayName: String,
        timestampMillis: Long = System.currentTimeMillis()
    ): String {
        val safeName = displayName
            .trim()
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "user" }
            .take(24)
        val dateStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(timestampMillis))
        return "stages_of_life_data_${safeName}_$dateStamp.json"
    }

    fun buildJsonString(
        payload: UserDataExportPayload,
        exportedAtMillis: Long = System.currentTimeMillis()
    ): String {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val totalSavedCents = payload.budgetEntries
            .filter { it.category.equals("Save", ignoreCase = true) || it.category.equals("Income", ignoreCase = true) }
            .sumOf { it.amountCents }
        val totalSpentCents = payload.budgetEntries
            .filter { it.category.equals("Spend", ignoreCase = true) || it.category.equals("Expense", ignoreCase = true) }
            .sumOf { it.amountCents }
        val totalGivenCents = payload.budgetEntries
            .filter { it.category.equals("Give", ignoreCase = true) }
            .sumOf { it.amountCents }
        val netBalanceCents = (totalSavedCents - totalSpentCents - totalGivenCents)
        val savingsGoalCents = payload.profile.savingsGoalCents.coerceAtLeast(100L)
        val effectiveSavedForGoal = if (payload.budgetEntries.isNotEmpty()) {
            totalSavedCents.coerceAtLeast(0L)
        } else {
            payload.profile.savingsBalanceCents.coerceAtLeast(0L)
        }
        val goalProgressPercent = ((effectiveSavedForGoal.toDouble() / savingsGoalCents.toDouble()) * 100.0)
            .coerceIn(0.0, 999.0)

        val root = JSONObject()

        // 1. Export Metadata
        val metadataJson = JSONObject().apply {
            put("schemaVersion", "1.0")
            put("platform", "Stages Of Life Android")
            put("applicationId", BuildConfig.APPLICATION_ID)
            put("appVersionName", BuildConfig.VERSION_NAME)
            put("appVersionCode", BuildConfig.VERSION_CODE)
            put("exportedAtIso", isoFormatter.format(Date(exportedAtMillis)))
            put("exportedByDisplayName", payload.profile.displayName.ifBlank { payload.signUpForm.displayName })
        }
        root.put("exportMetadata", metadataJson)

        // 2. Aggregated Financial Goal Data
        val budgetEntriesArray = JSONArray()
        payload.budgetEntries.forEach { entry ->
            budgetEntriesArray.put(
                JSONObject().apply {
                    put("id", entry.id)
                    put("title", entry.title)
                    put("category", entry.category)
                    put("amountCents", entry.amountCents)
                    put("amountUsd", formatCentsAsUsd(entry.amountCents))
                    val createdIso = entry.createdAt?.toDate()?.let { isoFormatter.format(it) } ?: ""
                    put("createdAtIso", createdIso)
                }
            )
        }

        val milestonesArray = JSONArray()
        payload.careerMilestones.forEach { m ->
            milestonesArray.put(
                JSONObject().apply {
                    put("id", m.id)
                    put("title", m.title)
                    put("stage", m.stage)
                    put("targetTerm", m.targetTerm)
                    put("status", m.status)
                    put("xpReward", m.xpReward)
                    put("stepOrder", m.stepOrder)
                    put("portalUrl", m.portalUrl)
                }
            )
        }

        val currencyTxArray = JSONArray()
        payload.currencyTransactions.forEach { tx ->
            currencyTxArray.put(
                JSONObject().apply {
                    put("id", tx.id)
                    put("title", tx.title)
                    put("category", tx.category)
                    put("amountEqc", tx.amount)
                    put("isEarned", tx.isEarned)
                    put("timestampLabel", tx.timestampLabel)
                }
            )
        }

        val financialGoalDataJson = JSONObject().apply {
            put("overallSavingsGoalCents", savingsGoalCents)
            put("overallSavingsGoalUsd", formatCentsAsUsd(savingsGoalCents))
            put("totalSavedCents", totalSavedCents)
            put("totalSavedUsd", formatCentsAsUsd(totalSavedCents))
            put("totalSpentCents", totalSpentCents)
            put("totalSpentUsd", formatCentsAsUsd(totalSpentCents))
            put("totalGivenCents", totalGivenCents)
            put("totalGivenUsd", formatCentsAsUsd(totalGivenCents))
            put("netBalanceCents", netBalanceCents)
            put("netBalanceUsd", formatCentsAsUsd(netBalanceCents))
            put("goalProgressPercent", (goalProgressPercent * 10.0).roundToInt() / 10.0)
            put("equalCoinsBalance", payload.virtualCurrencyBalance)
            put("dailyBonusClaimed", payload.dailyBonusClaimed)
            put("loginStreakDays", payload.loginStreakDays)
            put("budgetEntriesCount", payload.budgetEntries.size)
            put("budgetEntries", budgetEntriesArray)
            put("careerMilestonesCount", payload.careerMilestones.size)
            put("careerMilestones", milestonesArray)
            put("currencyTransactions", currencyTxArray)
        }
        root.put("financialGoalData", financialGoalDataJson)

        // 3. Aggregated User Preferences
        val profilePrefsJson = JSONObject().apply {
            put("userId", payload.profile.userId)
            put("displayName", payload.profile.displayName.ifBlank { payload.signUpForm.displayName })
            put("email", payload.userEmail.ifBlank { payload.profile.email })
            put("phoneNumber", payload.userPhone.ifBlank { payload.profile.phoneNumber })
            put("lifeStageRole", payload.profile.role.ifBlank { payload.signUpForm.role })
            put("age", payload.profile.age)
            put("location", payload.profile.location.ifBlank { payload.signUpForm.location })
            put("bio", payload.profile.bio.ifBlank { payload.signUpForm.bio })
            put("avatarEmojiBadge", payload.avatarEmojiBadge)
            put("pledgeAccepted", payload.profile.pledgeAccepted)
            put("musicPlatform", payload.profile.musicPlatform.ifBlank { payload.signUpForm.musicPlatform })
            put("musicTrackTitle", payload.profile.musicTrackTitle.ifBlank { payload.signUpForm.musicTrackTitle })
            put("spotifyConnected", payload.profile.spotifyConnected)
            put("appleMusicConnected", payload.profile.appleMusicConnected)
        }

        val appearancePrefsJson = JSONObject().apply {
            put("isDarkMode", payload.isDarkMode)
            put("backgroundTransparencyPercent", (payload.backgroundTransparency * 100f).roundToInt().coerceIn(0, 100))
            put("themeColorHex", payload.signUpForm.themeColorHex.ifBlank { payload.profile.themeColorHex })
            put("themeVisualStyle", payload.currentVisualStyleId.ifBlank { payload.profile.themeVisualStyle })
        }

        val interestsPrefsJson = JSONObject().apply {
            put("selectedInterests", JSONArray(payload.signUpForm.selectedInterests.sorted()))
            put("selectedSportsIds", JSONArray(payload.selectedSportsIds.sorted()))
            put("learnedInterests", JSONArray(payload.learnedInterests.sorted()))
            put("learnedLifestyle", JSONArray(payload.learnedLifestyle.sorted()))
            put("learnedSchedule", JSONArray(payload.learnedSchedule.sorted()))
            put("learnedResearch", JSONArray(payload.learnedResearch.sorted()))
            put("recentLearnedSignals", JSONArray(payload.learnedSignals.take(25)))
        }

        val stagesJson = JSONObject()
        payload.personaStageDataMap.toSortedMap().forEach { (stageId, data) ->
            stagesJson.put(
                stageId,
                JSONObject().apply {
                    put("stageId", data.stageId)
                    put("stageLabel", data.stageLabel)
                    put("focusGoal", data.focusGoal)
                    put("primaryMetricLabel", data.primaryMetricLabel)
                    put("primaryMetricValue", data.primaryMetricValue)
                    put("primaryMetricTarget", data.primaryMetricTarget)
                    put("stageScoreOrXp", data.stageScoreOrXp)
                    put("pinnedTopics", JSONArray(data.pinnedTopics))
                    put("completedTaskIds", JSONArray(data.completedTaskIds))
                    put("customNote", data.customNote)
                }
            )
        }

        val configs3DArray = JSONArray()
        payload.profile3DConfigs.forEach { cfg ->
            configs3DArray.put(
                JSONObject().apply {
                    put("id", cfg.id)
                    put("configName", cfg.configName)
                    put("baseModelId", cfg.baseModelId)
                    put("baseModelName", cfg.baseModelName)
                    put("primaryColorHex", cfg.primaryColorHex)
                    put("secondaryColorHex", cfg.secondaryColorHex)
                    put("auraColorHex", cfg.auraColorHex)
                    put("headgearId", cfg.headgearId)
                    put("eyewearId", cfg.eyewearId)
                    put("auraId", cfg.auraId)
                    put("rigId", cfg.rigId)
                    put("isActive", cfg.isActive)
                }
            )
        }

        val userPreferencesJson = JSONObject().apply {
            put("profile", profilePrefsJson)
            put("appearance", appearancePrefsJson)
            put("interestsAndLearning", interestsPrefsJson)
            put("personaStagePreferences", stagesJson)
            put("profile3DConfigs", configs3DArray)
        }
        root.put("userPreferences", userPreferencesJson)

        return root.toString(2)
    }

    private fun formatCentsAsUsd(cents: Long): String {
        val sign = if (cents < 0) "-" else ""
        val absCents = kotlin.math.abs(cents)
        val dollars = absCents / 100
        val rem = absCents % 100
        return "$sign$${dollars}.${rem.toString().padStart(2, '0')}"
    }
}
