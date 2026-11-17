package com.example.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.data.model.UserProfile
import com.example.dating.AgeGateCalculator
import com.example.dating.AgeVerificationProvider
import com.example.dating.DatingVerificationStatus
import com.example.dating.IsolatedDatingViewModel
import com.example.ui.UiState
import com.example.ui.navigation.PlatformModuleDestination
import com.example.ui.navigation.PlatformModuleNavHost
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlatformModuleNavHostAndDatingGateTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun ageGateCalculator_blocksUnderageAndAllowsAdults18Plus() {
        val refCal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 16)
        }
        assertEquals(17, AgeGateCalculator.calculateAgeYears(2008, 12, 1, refCal))
        assertEquals(18, AgeGateCalculator.calculateAgeYears(2008, 11, 16, refCal))
        assertEquals(30, AgeGateCalculator.calculateAgeYears(1996, 5, 10, refCal))
        assertEquals(null, AgeGateCalculator.calculateAgeYears(2000, 2, 31, refCal))
    }

    @Test
    fun isolatedDatingViewModel_blocksUnderageAndUnlocksWithThirdPartyAssuranceToken() {
        val refCal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 16)
        }
        val vm = IsolatedDatingViewModel()

        // Attempt underage DOB (2010 -> age 16)
        vm.updateBirthMonth("03")
        vm.updateBirthDay("15")
        vm.updateBirthYear("2010")
        assertFalse(vm.launchProviderHandshake(refCal))
        assertTrue(vm.uiState.value.verificationStatus is DatingVerificationStatus.DeniedUnderage)

        // Valid adult DOB (1995 -> age 31) + Yoti assurance + pledge
        vm.updateIsolatedAlias("Phoenix95")
        vm.updateBirthMonth("06")
        vm.updateBirthDay("20")
        vm.updateBirthYear("1995")
        vm.selectVerificationProvider(AgeVerificationProvider.YOTI_DIGITAL_AGE)
        assertTrue(vm.launchProviderHandshake(refCal))
        vm.toggleConductAndPrivacyPledge(true)
        assertTrue(vm.completeVerificationAndEnter(refCal))

        val verified = vm.uiState.value.verificationStatus
        assertTrue(verified is DatingVerificationStatus.Verified18Plus)
        assertEquals("Phoenix95", (verified as DatingVerificationStatus.Verified18Plus).isolatedAlias)

        // Purge & relock wipes isolated session cleanly
        vm.revokeVerificationAndPurgeSession()
        assertEquals(DatingVerificationStatus.Locked, vm.uiState.value.verificationStatus)
        assertEquals("", vm.uiState.value.isolatedAliasInput)
    }

    @Test
    fun platformModuleNavHost_navigatesAcrossDashboardPhotoStudioFinanceAndIsolatedDating() {
        composeRule.setContent {
            MaterialTheme {
                PlatformModuleNavHost(
                    userProfile = UserProfile(displayName = "Jordan Vance", role = "adult", age = 28L),
                    customProfileBitmap = null,
                    selected3DAvatarResId = null,
                    budgetState = UiState.Success(emptyList()),
                    virtualCurrencyBalance = 350L,
                    onApplyPhotoBitmap = {},
                    onAddBudgetItem = { _, _, _ -> },
                    onDeleteBudgetItem = {},
                    onShareToCommunityWall = { _, _, _ -> },
                    onLogModuleInteraction = { _, _, _ -> }
                ) { navigateToModule ->
                    androidx.compose.foundation.layout.Column {
                        Text("Dashboard Home", modifier = Modifier.testTag("test_dashboard_marker"))
                        Button(
                            onClick = { navigateToModule(PlatformModuleDestination.PhotoStudio) },
                            modifier = Modifier.testTag("goto_photo_btn")
                        ) { Text("Photo") }
                        Button(
                            onClick = { navigateToModule(PlatformModuleDestination.FinancialLiteracy) },
                            modifier = Modifier.testTag("goto_finance_btn")
                        ) { Text("Finance") }
                        Button(
                            onClick = { navigateToModule(PlatformModuleDestination.AgeGatedDatingSanctuary) },
                            modifier = Modifier.testTag("goto_dating_btn")
                        ) { Text("Dating 18+") }
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("test_dashboard_marker").assertIsDisplayed()

        // Navigate to Photo Studio module
        composeRule.onNodeWithTag("goto_photo_btn").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("dedicated_module_screen_${PlatformModuleDestination.PhotoStudio.route}")
            .assertIsDisplayed()

        // Switch to Financial Literacy module via module switcher chip
        composeRule.onNodeWithTag("module_nav_chip_${PlatformModuleDestination.FinancialLiteracy.route}")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("dedicated_module_screen_${PlatformModuleDestination.FinancialLiteracy.route}")
            .assertIsDisplayed()

        // Switch to Built-In AI Tool module via module switcher chip
        composeRule.onNodeWithTag("module_nav_chip_${PlatformModuleDestination.AiBuiltInTool.route}")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("dedicated_module_screen_${PlatformModuleDestination.AiBuiltInTool.route}")
            .assertIsDisplayed()
        composeRule.onNodeWithTag("ai_built_in_tool_root").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("ai_tool_sample_prompt_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Scroll back to module header before tapping the 18+ Sanctuary switcher chip
        composeRule.onNodeWithTag("module_nav_back_dashboard").performScrollTo()
        composeRule.onNodeWithTag("module_nav_chip_${PlatformModuleDestination.AgeGatedDatingSanctuary.route}")
            .performScrollTo()
            .performClick()
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("isolated_dating_root_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("dating_age_gate_screen").performScrollTo().assertIsDisplayed()

        // Test interactive 18+ verification flow in Compose UI
        composeRule.onNodeWithTag("dating_alias_input").performScrollTo().performTextInput("Sierra30")
        composeRule.onNodeWithTag("dating_dob_month_input").performScrollTo().performTextInput("04")
        composeRule.onNodeWithTag("dating_dob_day_input").performScrollTo().performTextInput("12")
        composeRule.onNodeWithTag("dating_dob_year_input").performScrollTo().performTextInput("1994")
        composeRule.onNodeWithTag("dating_launch_provider_btn").performScrollTo().performClick()
        composeRule.onNodeWithTag("dating_pledge_checkbox").performScrollTo().performClick()
        composeRule.onNodeWithTag("dating_unlock_submit_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("dating_verified_content").performScrollTo().assertIsDisplayed()

        // Exit isolated Sanctuary back to Main Dashboard
        composeRule.onNodeWithTag("dating_exit_to_platform_btn")
            .performScrollTo()
            .performClick()
        composeRule.mainClock.advanceTimeBy(600L)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("test_dashboard_marker").assertIsDisplayed()
    }

    @Test
    fun aiBuiltInToolViewModel_parsesStructuredPlanJsonAndGuardsEmptyInput() {
        val sampleJson = """
            {
              "goalTitle": "Master Saving & Study Balance",
              "summary": "A focused 7-day plan.",
              "priorityLevel": "High",
              "estimatedMinutes": 25,
              "steps": ["Audit daily schedule", "Automate weekly savings", "Review flashcards"]
            }
        """.trimIndent()
        val plan = com.example.ui.ai.AiBuiltInToolViewModel.parseStructuredPlanJson(sampleJson)
        assertEquals("Master Saving & Study Balance", plan.goalTitle)
        assertEquals("High", plan.priorityLevel)
        assertEquals(25, plan.estimatedMinutes)
        assertEquals(3, plan.steps.size)

        val vm = com.example.ui.ai.AiBuiltInToolViewModel()
        vm.runTool(inputPrompt = "   ", userRole = "student", userDisplayName = "Jordan")
        assertTrue(vm.uiState.value is com.example.ui.ai.AiBuiltInToolUiState.Error)
    }
}
