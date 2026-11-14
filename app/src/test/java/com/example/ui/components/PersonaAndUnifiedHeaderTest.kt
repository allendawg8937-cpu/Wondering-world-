package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.data.model.buildDefaultPersonaStageData
import com.example.ui.DashboardTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersonaAndUnifiedHeaderTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun personaViewState_mapsAllSixLifeStagesDistinctly() {
        val kid = PersonaViewState.fromStage(DashboardLifeStage.KID)
        val teen = PersonaViewState.fromStage(DashboardLifeStage.TEEN)
        val student = PersonaViewState.fromStage(DashboardLifeStage.STUDENT)
        val adult = PersonaViewState.fromStage(DashboardLifeStage.ADULT)
        val business = PersonaViewState.fromStage(DashboardLifeStage.BUSINESS)
        val veteran = PersonaViewState.fromStage(DashboardLifeStage.VETERAN)

        assertTrue(kid is PersonaViewState.KidView)
        assertTrue(teen is PersonaViewState.TeenView)
        assertTrue(student is PersonaViewState.StudentView)
        assertTrue(adult is PersonaViewState.AdultView)
        assertTrue(business is PersonaViewState.BusinessView)
        assertTrue(veteran is PersonaViewState.VeteranView)
    }

    @Test
    fun personaDashboardComponent_togglesAcrossAllSixPersonaStates() {
        val stageMap = DashboardLifeStage.entries.associate { it.stageId to buildDefaultPersonaStageData(it.stageId) }
        var currentRole by mutableStateOf("adult")

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PersonaDashboardComponent(
                        currentRole = currentRole,
                        currentDisplayName = "Jordan Vance",
                        personaStageDataMap = stageMap,
                        isSaving = false,
                        statusBanner = null,
                        isAuthenticatedForCloud = false,
                        onSelectLifeStage = { stageId, _, _ -> currentRole = stageId },
                        onSavePersonaStageData = {},
                        onNavigateToTab = {},
                        onShareToWall = { _, _, _ -> }
                    )
                }
            }
        }

        val stages = listOf("kid", "teen", "student", "adult", "business", "veteran")
        stages.forEach { stageId ->
            composeRule.onNodeWithTag("persona_stage_pill_$stageId").performScrollTo().assertIsDisplayed().performClick()
            composeRule.waitForIdle()
            assertEquals(stageId, currentRole)
            composeRule.onNodeWithTag("persona_stage_hero_$stageId").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("persona_view_module_$stageId").performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun unifiedSearchAndThemeHeaderBar_combinesSearchDarkModeAndColorSwatches() {
        var isDark by mutableStateOf(true)
        var selectedHex by mutableStateOf("#2563EB")
        var navigatedTab: DashboardTab? = null

        composeRule.setContent {
            MaterialTheme {
                UnifiedSearchAndThemeHeaderBar(
                    isDarkMode = isDark,
                    followSystemTheme = false,
                    isLateNightEyeComfort = false,
                    nightDimmerAlpha = 0.0f,
                    useDynamicColor = false,
                    currentThemeHex = selectedHex,
                    onSelectThemeHex = { selectedHex = it },
                    backgroundTransparency = 0.0f,
                    onTransparencyChange = {},
                    onSetDarkMode = { isDark = it },
                    onSetFollowSystemTheme = {},
                    onSetLateNightEyeComfort = {},
                    onSetNightDimmerAlpha = {},
                    onSetUseDynamicColor = {},
                    currentUserDisplayName = "Jordan Vance",
                    currentUserRole = "veteran",
                    wallPosts = emptyList(),
                    onNavigateToTab = { navigatedTab = it }
                )
            }
        }

        // Combined header container and search bar exist together
        composeRule.onNodeWithTag("unified_search_and_theme_header_bar").assertIsDisplayed()
        composeRule.onNodeWithTag("universal_search_input_field").assertIsDisplayed()
        composeRule.onNodeWithTag("system_wide_dark_mode_switch").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        assertEquals(false, isDark)

        // Color swatch inside combined bar updates theme hex
        composeRule.onNodeWithTag("header_solid_color_solid_red").performClick()
        composeRule.waitForIdle()
        assertEquals("#DC2626", selectedHex)

        // Search input triggers live result and navigates cleanly
        composeRule.onNodeWithTag("universal_search_input_field").performTextInput("FAFSA")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("universal_search_result_res_fafsa").performClick()
        composeRule.waitForIdle()
        assertEquals(DashboardTab.RESOURCES, navigatedTab)
    }
}
