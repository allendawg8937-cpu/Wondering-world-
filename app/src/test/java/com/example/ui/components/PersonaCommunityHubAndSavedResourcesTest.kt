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
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.data.model.ResourceDirectoryItem
import com.example.data.model.SavedResource
import com.example.data.model.UserProfile
import com.example.data.model.WallPost
import com.example.ui.UiState
import com.example.ui.screens.FindingResourceHubScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersonaCommunityHubAndSavedResourcesTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun personaCommunityHubView_dynamicallyFetchesTailoredResourcesAcrossStages() {
        var activeStage by mutableStateOf("kid")
        val fetchedStages = mutableListOf<String>()

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PersonaCommunityHubView(
                        activeRoleOrStageId = activeStage,
                        curatedResources = listOf(
                            ResourceDirectoryItem(
                                id = "res_va_pact",
                                title = "VA PACT Act Health Care Portal",
                                subtitle = "Expanded veteran healthcare benefits",
                                badge = "VA HEALTH",
                                url = "https://www.va.gov/resources/",
                                roles = listOf("veteran"),
                                tags = listOf("Veterans", "Healthcare")
                            )
                        ),
                        wallPostsState = UiState.Success(
                            listOf(
                                WallPost(
                                    id = "post_vet_1",
                                    userId = "u_vet",
                                    authorName = "Sgt. Rivera",
                                    authorRole = "veteran",
                                    category = "Resource Guide",
                                    title = "Veteran Housing & Peer Mentor Night",
                                    content = "Local Thursday peer check-in and benefits Q&A.",
                                    likesCount = 12,
                                    accentHex = "#EF4444"
                                )
                            )
                        ),
                        onFetchStagePosts = { stageId ->
                            fetchedStages.add(stageId)
                            Result.success(emptyList())
                        },
                        onSelectStage = { stage -> activeStage = stage.stageId }
                    )
                }
            }
        }

        composeRule.waitForIdle()
        assertTrue(fetchedStages.contains("kid"))
        composeRule.onNodeWithTag("community_hub_stage_header")
            .assertTextContains("Kid Community Hub", substring = true)
        composeRule.onNodeWithTag("community_hub_resource_card_prog_kid_nasa_stem")
            .performScrollTo()
            .assertIsDisplayed()

        // Switch persona to Veteran and verify dynamic fetch & tailored resources update
        composeRule.onNodeWithTag("community_hub_stage_chip_veteran")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        assertEquals("veteran", activeStage)
        assertTrue(fetchedStages.contains("veteran"))
        composeRule.onNodeWithTag("community_hub_stage_header")
            .assertTextContains("Veteran Community Hub", substring = true)
        composeRule.onNodeWithTag("community_hub_resource_card_prog_vet_988_va_care")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("community_hub_resource_card_dir_res_va_pact")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun personaCommunityHubView_showsErrorAndRetriesCleanly() {
        var shouldFail by mutableStateOf(true)

        composeRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PersonaCommunityHubView(
                        activeRoleOrStageId = "student",
                        onFetchStagePosts = {
                            if (shouldFail) {
                                Result.failure(IllegalStateException("Network offline"))
                            } else {
                                Result.success(emptyList())
                            }
                        }
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("community_hub_error_card")
            .performScrollTo()
            .assertIsDisplayed()

        shouldFail = false
        composeRule.onNodeWithTag("community_hub_retry_button")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("community_hub_resource_card_prog_student_campus_hall")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun findingResourceHub_saveResourceAndManageInProfileMySavedResources() {
        var savedList by mutableStateOf<List<SavedResource>>(emptyList())
        var navigatedToHub = false
        val sampleDirectory = listOf(
            ResourceDirectoryItem(
                id = "lifeline_988",
                title = "988 Suicide & Crisis Lifeline",
                subtitle = "24/7 free and confidential emotional support.",
                badge = "24/7 CRISIS LIFELINE",
                url = "https://988lifeline.org/",
                roles = listOf("all"),
                tags = listOf("Crisis", "Mental Health")
            ),
            ResourceDirectoryItem(
                id = "fafsa_aid",
                title = "FAFSA Federal Student Aid",
                subtitle = "Grants, work-study, and college funding portal.",
                badge = "GRANTS & AID",
                url = "https://studentaid.gov/",
                roles = listOf("student", "teen"),
                tags = listOf("Education", "Grants")
            )
        )

        composeRule.setContent {
            MaterialTheme {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    FindingResourceHubScreen(
                        profile = UserProfile(displayName = "Jordan Vance", role = "student", age = 21L),
                        resources = sampleDirectory,
                        searchFilter = "",
                        onOpenResourceUrl = {},
                        onShareResourceToWall = {},
                        onUpdateUserLocationFromGps = {},
                        onRecordLearnedSignal = {},
                        isBiometricUnlocked = true,
                        savedResourceIds = savedList.map { it.resourceId }.toSet(),
                        onToggleSaveResource = { item ->
                            val exists = savedList.any { it.resourceId == item.id }
                            savedList = if (exists) {
                                savedList.filterNot { it.resourceId == item.id }
                            } else {
                                savedList + SavedResource(
                                    id = item.id,
                                    userId = "u1",
                                    resourceId = item.id,
                                    title = item.title,
                                    subtitle = item.subtitle,
                                    badge = item.badge,
                                    url = item.url,
                                    category = "Lifeline & Resource Hub",
                                    stageId = item.roles.firstOrNull() ?: "all"
                                )
                            }
                        }
                    )

                    SavedResourcesProfileSection(
                        savedResources = savedList,
                        onRemoveSavedResource = { resId ->
                            savedList = savedList.filterNot { it.resourceId == resId || it.id == resId }
                        },
                        onExploreLifelineHub = { navigatedToHub = true }
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("saved_resources_empty_state")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("browse_lifeline_resources_cta")
            .performScrollTo()
            .performClick()
        assertTrue(navigatedToHub)

        // Save resource in Lifeline & Resource Hub
        composeRule.onNodeWithTag("save_lifeline_resource_btn_lifeline_988")
            .performScrollTo()
            .assertTextContains("Save", substring = false)
            .performClick()
        composeRule.waitForIdle()

        assertEquals(1, savedList.size)
        composeRule.onNodeWithTag("save_lifeline_resource_btn_lifeline_988")
            .performScrollTo()
            .assertTextContains("Saved", substring = false)

        // Verify it appears in Profile "My Saved Resources"
        composeRule.onNodeWithTag("profile_my_saved_resources_section")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("saved_resources_count_badge")
            .assertTextContains("1 Saved", substring = false)
        composeRule.onNodeWithTag("saved_resource_card_lifeline_988")
            .performScrollTo()
            .assertIsDisplayed()

        // Remove from Profile section and confirm empty state returns
        composeRule.onNodeWithTag("remove_saved_resource_btn_lifeline_988")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        assertEquals(0, savedList.size)
        composeRule.onNodeWithTag("saved_resources_empty_state")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
