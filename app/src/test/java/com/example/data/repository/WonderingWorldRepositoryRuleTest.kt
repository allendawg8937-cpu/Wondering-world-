package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class WonderingWorldRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndGetUserProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            displayName = "Jordan Vance",
            role = "veteran",
            age = 28L,
            location = "Austin, TX",
            bio = "We are all equal.",
            themeColorHex = "#84CC16",
            themeVisualStyle = "GALAXY_VORTEX",
            musicTrackTitle = "Comfortably Numb - Pink Floyd",
            musicPlatform = "Spotify",
            savingsBalanceCents = 245075L,
            savingsGoalCents = 500000L,
            arcadeHighScore = 150L,
            pledgeAccepted = true,
            interests = listOf("Healthcare", "Music")
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.saveUserProfile(profile) }
        assertTrue("Expected saveUserProfile to succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getUserProfile(uid).getOrThrow() }
        assertNotNull(fetched)
        assertEquals("Jordan Vance", fetched?.displayName)
        assertEquals("veteran", fetched?.role)
        assertEquals("#84CC16", fetched?.themeColorHex)
        assertEquals("GALAXY_VORTEX", fetched?.themeVisualStyle)
    }

    @Test
    fun createAndObserveWallPost_authenticatedOwner_emitsRealtimePost() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val postId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createWallPost(
                authorName = "Jordan Vance",
                authorRole = "veteran",
                category = "Resource Guide",
                title = "VA Benefits Walkthrough",
                content = "How to schedule clinic appointments and apply for education benefits.",
                accentHex = "#6366F1"
            ).getOrThrow()
        }
        assertTrue(postId.isNotEmpty())

        val posts = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeWallPosts(uid).first { list -> list.any { it.id == postId } }
        }
        assertTrue(posts.any { it.id == postId && it.title == "VA Benefits Walkthrough" })
    }

    @Test
    fun getUserProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveUserProfile(
                UserProfile(
                    userId = aliceUid,
                    displayName = "Alice",
                    role = "adult",
                    age = 25L,
                    location = "Denver, CO",
                    bio = "Hello world",
                    pledgeAccepted = true
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getUserProfile(aliceUid).getOrThrow() }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observeWallPosts_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = WonderingWorldRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeWallPosts(aliceUid).first()
            }
            fail("Expected exception when observing wall posts unauthenticated")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    @Test
    fun logAndObserveUserInteractions_authenticatedOwner_emitsRealtimeInteraction() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val interactionId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.logUserInteraction(
                actionType = "SportsPin",
                detail = "Pinned NBA & NFL Live Scores",
                category = "Sports"
            ).getOrThrow()
        }
        assertTrue(interactionId.isNotEmpty())

        val interactions = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserInteractions(uid).first { list -> list.any { it.id == interactionId } }
        }
        assertTrue(interactions.any { it.id == interactionId && it.actionType == "SportsPin" && it.category == "Sports" })
    }

    @Test
    fun addUpdateAndObserveCareerMilestones_authenticatedOwner_succeedsAndBlocksCrossUser() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val milestoneId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addCareerMilestone(
                title = "Submit Common App College Applications",
                stage = "Application",
                description = "Submit applications to 3 state universities with STEM scholarship essays.",
                targetTerm = "Step 3 • Fall Admissions",
                status = "In Progress",
                xpReward = 225L,
                stepOrder = 3L,
                portalUrl = "https://www.commonapp.org/"
            ).getOrThrow()
        }
        assertTrue(milestoneId.isNotEmpty())

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateCareerMilestoneStatus(milestoneId, "Completed").getOrThrow()
        }

        val milestones = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCareerMilestones(aliceUid).first { list ->
                list.any { it.id == milestoneId && it.status == "Completed" }
            }
        }
        assertTrue(milestones.any { it.id == milestoneId && it.title == "Submit Common App College Applications" })

        // Cross-user access must fail with PERMISSION_DENIED
        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getCareerMilestones(aliceUid).getOrThrow() }
            fail("Expected PERMISSION_DENIED when Bob reads Alice's career milestones")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@wonderingworld.test"
        const val BOB_EMAIL = "bob@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
