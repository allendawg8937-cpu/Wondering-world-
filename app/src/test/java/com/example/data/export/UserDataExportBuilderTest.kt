package com.example.data.export

import com.example.data.model.BudgetEntry
import com.example.data.model.CareerMilestone
import com.example.data.model.Profile3DConfig
import com.example.data.model.UserProfile
import com.example.data.model.buildDefaultPersonaStageData
import com.example.ui.SignUpFormState
import com.example.ui.components.VirtualCurrencyTransaction
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserDataExportBuilderTest {

    @Test
    fun buildJsonString_aggregatesFinancialGoalsAndUserPreferencesAccurately() {
        val payload = UserDataExportPayload(
            profile = UserProfile(
                userId = "user_123",
                displayName = "Jordan Vance",
                email = "jordan@example.com",
                role = "student",
                age = 21L,
                location = "Austin, TX",
                bio = "Building skills & stability",
                themeColorHex = "#06B6D4",
                themeVisualStyle = "AURORA_GLASS",
                isDarkMode = true,
                savingsGoalCents = 200000L, // $2,000.00
                pledgeAccepted = true
            ),
            signUpForm = SignUpFormState(
                displayName = "Jordan Vance",
                role = "student",
                themeColorHex = "#06B6D4",
                themeVisualStyle = "AURORA_GLASS",
                selectedInterests = setOf("STEM Grants", "Apprenticeships")
            ),
            userEmail = "jordan@example.com",
            userPhone = "555-0199",
            avatarEmojiBadge = "🎓",
            isDarkMode = false,
            backgroundTransparency = 0.35f,
            currentVisualStyleId = "AURORA_GLASS",
            selectedSportsIds = setOf("basketball", "track"),
            learnedInterests = setOf("Kotlin", "FAFSA"),
            learnedLifestyle = setOf("Morning Study"),
            learnedSchedule = setOf("Weekdays"),
            learnedResearch = setOf("Solar Tech"),
            learnedSignals = listOf("Searched FAFSA", "Saved Internship"),
            budgetEntries = listOf(
                BudgetEntry(id = "b1", title = "Emergency Deposit", category = "Save", amountCents = 75000L),
                BudgetEntry(id = "b2", title = "Lab Stipend", category = "Income", amountCents = 25000L),
                BudgetEntry(id = "b3", title = "Textbooks", category = "Spend", amountCents = 12000L),
                BudgetEntry(id = "b4", title = "Community Fund", category = "Give", amountCents = 3000L)
            ),
            careerMilestones = listOf(
                CareerMilestone(
                    id = "m1",
                    title = "Submit FAFSA",
                    stage = "Foundation",
                    status = "Completed",
                    xpReward = 150L
                )
            ),
            virtualCurrencyBalance = 420L,
            dailyBonusClaimed = true,
            loginStreakDays = 7,
            currencyTransactions = listOf(
                VirtualCurrencyTransaction(
                    id = "tx1",
                    title = "Daily Bonus",
                    amount = 50L,
                    isEarned = true,
                    category = "Daily Login",
                    timestampLabel = "Today"
                )
            ),
            personaStageDataMap = mapOf(
                "student" to buildDefaultPersonaStageData("student", "user_123")
            ),
            profile3DConfigs = listOf(
                Profile3DConfig(id = "active", userId = "user_123", configName = "Cyber Scholar")
            )
        )

        val jsonText = UserDataExportBuilder.buildJsonString(payload, exportedAtMillis = 1794558000000L)
        val parsed = JSONObject(jsonText)

        assertTrue(parsed.has("exportMetadata"))
        assertTrue(parsed.has("financialGoalData"))
        assertTrue(parsed.has("userPreferences"))

        val fin = parsed.getJSONObject("financialGoalData")
        assertEquals(200000L, fin.getLong("overallSavingsGoalCents"))
        assertEquals("$2000.00", fin.getString("overallSavingsGoalUsd"))
        assertEquals(100000L, fin.getLong("totalSavedCents"))
        assertEquals(12000L, fin.getLong("totalSpentCents"))
        assertEquals(3000L, fin.getLong("totalGivenCents"))
        assertEquals(85000L, fin.getLong("netBalanceCents"))
        assertEquals(50.0, fin.getDouble("goalProgressPercent"), 0.1)
        assertEquals(420L, fin.getLong("equalCoinsBalance"))
        assertEquals(4, fin.getJSONArray("budgetEntries").length())
        assertEquals(1, fin.getJSONArray("careerMilestones").length())

        val prefs = parsed.getJSONObject("userPreferences")
        val profileJson = prefs.getJSONObject("profile")
        assertEquals("Jordan Vance", profileJson.getString("displayName"))
        assertEquals("jordan@example.com", profileJson.getString("email"))
        assertEquals("student", profileJson.getString("lifeStageRole"))

        val appearance = prefs.getJSONObject("appearance")
        assertEquals(false, appearance.getBoolean("isDarkMode"))
        assertEquals(35, appearance.getInt("backgroundTransparencyPercent"))
        assertEquals("#06B6D4", appearance.getString("themeColorHex"))
        assertEquals("AURORA_GLASS", appearance.getString("themeVisualStyle"))

        val stages = prefs.getJSONObject("personaStagePreferences")
        assertTrue(stages.has("student"))
        assertEquals(1, prefs.getJSONArray("profile3DConfigs").length())
    }

    @Test
    fun defaultExportFileName_sanitizesDisplayNameAndAppendsJsonExtension() {
        val fileName = UserDataExportBuilder.defaultExportFileName(
            displayName = "Jordan @ Vance!!",
            timestampMillis = 1794558000000L
        )
        assertTrue(fileName.startsWith("stages_of_life_data_jordan_vance_"))
        assertTrue(fileName.endsWith(".json"))
    }
}
