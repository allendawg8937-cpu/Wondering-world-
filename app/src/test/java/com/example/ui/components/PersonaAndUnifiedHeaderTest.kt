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
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertTextContains
import android.graphics.Bitmap
import com.example.data.model.buildDefaultPersonaStageData
import com.example.ui.DashboardTab
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun selectDailyPersonaQuote_providesDistinctPersonalizedQuotesForAllSixPersonas() {
        val personas = listOf("kid", "teen", "student", "adult", "business", "veteran")
        val selectedQuotes = personas.map { persona ->
            selectDailyPersonaQuote(personaId = persona, dayOfYear = 120)
        }
        assertEquals(6, selectedQuotes.map { it.personaId }.distinct().size)
        assertEquals(6, selectedQuotes.map { it.quote }.distinct().size)
        assertNotEquals(
            selectDailyPersonaQuote("kid", dayOfYear = 120).id,
            selectDailyPersonaQuote("kid", dayOfYear = 121).id
        )
    }

    @Test
    fun personaDailyQuoteWidget_updatesQuoteAndEncouragementWhenPersonaChanges() {
        var activeRole by mutableStateOf("kid")
        var sharedHeadline: String? = null

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PersonaDailyQuoteWidget(
                        currentRole = activeRole,
                        userDisplayName = "Jordan Vance",
                        onShareQuoteToWall = { headline, _ -> sharedHeadline = headline }
                    )
                }
            }
        }

        val expectedKid = selectDailyPersonaQuote("kid")
        composeRule.onNodeWithTag("persona_daily_quote_widget").assertIsDisplayed()
        composeRule.onNodeWithTag("persona_quote_stage_badge").assertTextContains("Kid Mode", substring = true)
        composeRule.onNodeWithTag("persona_quote_text").assertTextContains(expectedKid.quote, substring = true)

        // Switch persona to veteran and verify immediate update
        activeRole = "veteran"
        composeRule.waitForIdle()
        val expectedVeteran = selectDailyPersonaQuote("veteran")
        composeRule.onNodeWithTag("persona_quote_stage_badge").assertTextContains("Veteran Mode", substring = true)
        composeRule.onNodeWithTag("persona_quote_text").assertTextContains(expectedVeteran.quote, substring = true)
        composeRule.onNodeWithTag("persona_quote_encouragement_text").assertTextContains(expectedVeteran.encouragementTip, substring = true)

        // Cycle next quote and verify quote changes within veteran pool
        composeRule.onNodeWithTag("persona_quote_next_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        val nextVeteran = selectDailyPersonaQuote("veteran", offset = 1)
        composeRule.onNodeWithTag("persona_quote_text").assertTextContains(nextVeteran.quote, substring = true)

        // Bookmark & share to wall
        composeRule.onNodeWithTag("persona_quote_favorite_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("persona_quote_status_banner").assertTextContains("Saved Veteran quote", substring = true)

        composeRule.onNodeWithTag("persona_quote_share_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(sharedHeadline?.contains("Veteran") == true)

        // Verify branded image export bitmap generator
        val bitmap = renderPersonaQuoteCardBitmap(
            quote = nextVeteran,
            activeStage = DashboardLifeStage.VETERAN,
            userDisplayName = "Jordan Vance"
        )
        assertEquals(1080, bitmap.width)
        assertEquals(1080, bitmap.height)
        val pngStream = ByteArrayOutputStream()
        assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, pngStream))
        assertTrue(pngStream.size() > 0)
        bitmap.recycle()
    }

    @Test
    fun personaDailyQuoteWidget_shareImageButtonExportsBrandedQuoteForActivePersona() {
        var exportedQuote: PersonaMotivationalQuote? = null
        var exportedStage: DashboardLifeStage? = null

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PersonaDailyQuoteWidget(
                        currentRole = "business",
                        userDisplayName = "Jordan Vance",
                        onExportQuoteImage = { quote, stage ->
                            exportedQuote = quote
                            exportedStage = stage
                        }
                    )
                }
            }
        }

        composeRule.onNodeWithTag("persona_quote_export_image_button")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        assertEquals(DashboardLifeStage.BUSINESS, exportedStage)
        assertEquals("business", exportedQuote?.personaId)
        composeRule.onNodeWithTag("persona_quote_status_banner")
            .assertTextContains("Exported Business branded quote image!", substring = true)
    }

    @Test
    fun lifeStageAvatarCatalog_providesSixDistinctGeneratedDrawablesForAllStages() {
        assertEquals(6, LIFE_STAGE_3D_AVATARS.size)
        val stageIds = LIFE_STAGE_3D_AVATARS.map { it.stageId }
        assertEquals(listOf("kid", "teen", "student", "adult", "business", "veteran"), stageIds)
        assertEquals(6, LIFE_STAGE_3D_AVATARS.map { it.drawableResId }.distinct().size)
    }

    @Test
    fun lifeStageAvatarSelectionGridCard_displaysSixAvatarsAndUpdatesSelection() {
        var activeRole by mutableStateOf("adult")
        var selectedResId by mutableStateOf<Int?>(null)
        var syncedAge by mutableStateOf(30L)
        var selectedPresetTitle by mutableStateOf<String?>(null)

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    LifeStageAvatarSelectionGridCard(
                        currentPersonaRole = activeRole,
                        selected3DAvatarResId = selectedResId,
                        onSelect3DAvatarPreset = { preset ->
                            selectedResId = preset.drawableResId
                            selectedPresetTitle = preset.title
                        },
                        onSyncLifeStagePersona = { stageId, defaultAge, _ ->
                            activeRole = stageId
                            syncedAge = defaultAge
                        }
                    )
                }
            }
        }

        composeRule.onNodeWithTag("life_stage_avatar_selection_grid").assertIsDisplayed()
        LIFE_STAGE_3D_AVATARS.forEach { avatar ->
            composeRule.onNodeWithTag("life_stage_avatar_card_${avatar.stageId}")
                .performScrollTo()
                .assertIsDisplayed()
        }

        // Select Teen avatar card and verify portrait + synced persona update
        composeRule.onNodeWithTag("life_stage_avatar_card_teen")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        assertEquals("teen", activeRole)
        assertEquals(16L, syncedAge)
        assertEquals(LIFE_STAGE_3D_AVATARS[1].drawableResId, selectedResId)
        assertTrue(selectedPresetTitle?.contains("Teen") == true)
        composeRule.onNodeWithTag("life_stage_avatar_card_teen").assertIsSelected()
        composeRule.onNodeWithTag("life_stage_avatar_selected_check_teen", useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()

        // Toggle Sync Stage off and verify clicking Veteran equips avatar without changing role
        composeRule.onNodeWithTag("life_stage_avatar_sync_toggle")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("life_stage_avatar_card_veteran")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        assertEquals("teen", activeRole)
        assertEquals(LIFE_STAGE_3D_AVATARS[5].drawableResId, selectedResId)
        composeRule.onNodeWithTag("life_stage_avatar_card_veteran").assertIsSelected()
        composeRule.onNodeWithTag("life_stage_avatar_selected_check_veteran", useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
