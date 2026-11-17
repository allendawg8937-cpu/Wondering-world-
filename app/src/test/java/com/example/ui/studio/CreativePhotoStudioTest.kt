package com.example.ui.studio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.CreativePhotoStudioScreen
import com.example.ui.theme.WonderingWorldTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CreativePhotoStudioTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `persona stage mapping returns tailored filters for Business and all personas`() {
        val businessStage = StudioPersonaStage.fromRole("Business")
        assertEquals(StudioPersonaStage.BUSINESS, businessStage)

        val businessFilters = filtersForPersona(businessStage)
        assertTrue(businessFilters.size >= 4)
        assertTrue(businessFilters.any { it.name == "Executive Slate" })
        assertTrue(businessFilters.any { it.name == "Editorial Mono Pro" })

        StudioPersonaStage.entries.forEach { stage ->
            val stageFilters = filtersForPersona(stage)
            assertTrue("Expected filters for $stage", stageFilters.isNotEmpty())
            assertTrue(stageFilters.all { it.personaStage == stage })
        }
    }

    @Test
    fun `renderPersonaStylizedBitmap applies filter and all frame styles cleanly`() {
        val source = Bitmap.createBitmap(120, 160, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.rgb(120, 140, 160))
        }
        val businessFilter = filtersForPersona(StudioPersonaStage.BUSINESS).first()

        StudioFilterFrameStyle.entries.forEach { frameStyle ->
            val rendered = renderPersonaStylizedBitmap(
                source = source,
                filter = businessFilter,
                intensity = 0.85f,
                brightnessOffset = 8f,
                contrastBoost = 0.10f,
                frameStyle = frameStyle
            )
            assertEquals(120, rendered.width)
            assertEquals(160, rendered.height)
        }
    }

    @Test
    fun `local gallery storage saves loads and deletes photos persistently`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sampleBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.CYAN)
        }
        val filter = filtersForPersona(StudioPersonaStage.BUSINESS).first()

        val saved = CreativePhotoGalleryStorage.savePhotoToLocalGallery(
            context = context,
            bitmap = sampleBitmap,
            title = "Executive Headshot",
            persona = StudioPersonaStage.BUSINESS,
            filter = filter,
            frameStyle = StudioFilterFrameStyle.EXECUTIVE_PINSTRIPE
        )
        assertNotNull(saved)

        val loaded = CreativePhotoGalleryStorage.loadSavedPhotos(context)
        assertTrue(loaded.any { it.id == saved!!.id && it.title == "Executive Headshot" && it.personaId == "business" })

        val deleted = CreativePhotoGalleryStorage.deletePhoto(context, saved!!)
        assertTrue(deleted)

        val afterDelete = CreativePhotoGalleryStorage.loadSavedPhotos(context)
        assertTrue(afterDelete.none { it.id == saved.id })
    }

    @Test
    fun `creative photo studio screen displays persona filters and saves to local gallery`() {
        val sampleAvatar = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.BLUE)
        }

        composeTestRule.setContent {
            WonderingWorldTheme(isDarkMode = true) {
                CreativePhotoStudioScreen(
                    displayName = "Jordan Vance",
                    currentRole = "business",
                    currentProfileBitmap = sampleAvatar,
                    selected3DAvatarResId = null,
                    onSwitchPersonaStage = { _, _ -> },
                    onApplyToProfileAvatar = {},
                    onShareCreationToFeed = { _, _, _ -> },
                    onLogStudioInteraction = {},
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("creative_photo_studio_screen").assertIsDisplayed()

        // Load profile avatar into studio first (while onboarding button is in viewfinder), then apply Business filter
        composeTestRule.onNodeWithTag("creative_studio_use_avatar_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Scroll outer vertical container to bring the filter carousel into view, then select Business filter
        composeTestRule.onNodeWithTag("creative_studio_filters_carousel").performScrollTo()
        composeTestRule.onNodeWithTag("creative_studio_filter_biz_editorial_mono").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("creative_studio_title_input").performScrollTo().performTextReplacement("Boardroom Portrait")
        composeTestRule.onNodeWithTag("creative_studio_save_gallery_btn").performScrollTo().performClick()

        // Wait for Dispatchers.IO local gallery persistence to complete
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Gallery (1)", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        // Switch to Gallery tab and verify the saved item is shown
        composeTestRule.onNodeWithTag("studio_mode_gallery_tab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("studio_gallery_persona_filter_toggle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Boardroom Portrait").assertIsDisplayed()
    }
}
