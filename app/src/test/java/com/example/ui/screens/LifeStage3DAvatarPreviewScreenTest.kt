package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.components.DashboardLifeStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LifeStage3DAvatarPreviewScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun buildLifeStage3DModelSpec_returnsDistinctAttributesForAllSixStages() {
        val specs = DashboardLifeStage.entries.map { buildLifeStage3DModelSpec(it) }
        assertEquals(6, specs.size)
        assertEquals(6, specs.map { it.gearType }.toSet().size)
        assertEquals(6, specs.map { it.primarySuitColor }.toSet().size)
        assertTrue(specs.first { it.stage == DashboardLifeStage.KID }.heightScale < specs.first { it.stage == DashboardLifeStage.ADULT }.heightScale)
        assertTrue(specs.first { it.stage == DashboardLifeStage.KID }.headScale > specs.first { it.stage == DashboardLifeStage.BUSINESS }.headScale)
    }

    @Test
    fun togglingAllSixLifeStages_updatesCanvas3DModelAttributesAndAppliesPersona() {
        var appliedStage: DashboardLifeStage? = null

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    LifeStage3DAvatarPreviewScreen(
                        initialRoleOrStageId = "adult",
                        onApplyStagePersona = { stage -> appliedStage = stage }
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("avatar_3d_canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("avatar_3d_attributes_card").performScrollTo().assertIsDisplayed()

        val expectedStages = listOf(
            DashboardLifeStage.KID to Stage3DGearType.HOLO_VISOR,
            DashboardLifeStage.TEEN to Stage3DGearType.SONIC_HEADPHONES,
            DashboardLifeStage.STUDENT to Stage3DGearType.AR_SMART_GLASSES,
            DashboardLifeStage.ADULT to Stage3DGearType.HORIZON_HALO,
            DashboardLifeStage.BUSINESS to Stage3DGearType.SOVEREIGN_CROWN,
            DashboardLifeStage.VETERAN to Stage3DGearType.SOLAR_SHIELD_CREST
        )

        expectedStages.forEach { (stage, expectedGear) ->
            composeRule.onNodeWithTag("persona_preview_toggle_${stage.stageId}")
                .performScrollTo()
                .performClick()
            composeRule.waitForIdle()

            composeRule.onNodeWithText("${stage.emoji} ${stage.label} 3D Attributes")
                .performScrollTo()
                .assertIsDisplayed()
            composeRule.onNodeWithText(expectedGear.label, substring = false)
                .performScrollTo()
                .assertIsDisplayed()
        }

        // Toggle pose and wireframe, then apply Veteran persona
        composeRule.onNodeWithTag("pose_chip_heroic").performScrollTo().performClick()
        composeRule.onNodeWithTag("wireframe_toggle_chip").performScrollTo().performClick()
        composeRule.onNodeWithTag("apply_stage_avatar_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(DashboardLifeStage.VETERAN, appliedStage)
        composeRule.onNodeWithTag("avatar_preview_status_text").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("avatar_3d_default_placeholder_thumb", useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun defaultPersonaPlaceholderAvatars_provideSixDistinct3DPlaceholdersAndPreserveCustomSelections() {
        val roles = listOf("kid", "teen", "student", "adult", "business", "veteran")
        val placeholders = roles.map { role ->
            com.example.ui.components.defaultPlaceholderAvatarResIdForRole(role)
        }

        assertEquals(6, placeholders.distinct().size)
        roles.forEach { role ->
            assertEquals(
                com.example.ui.components.defaultPlaceholderAvatarResIdForRole(role),
                com.example.ui.components.resolveEffectiveAvatarResId(selected3DAvatarResId = null, role = role)
            )
        }

        val customResId = com.example.R.drawable.img_avatar_cyber_astronaut
        assertEquals(
            customResId,
            com.example.ui.components.resolveEffectiveAvatarResId(selected3DAvatarResId = customResId, role = "teen")
        )
    }
}
