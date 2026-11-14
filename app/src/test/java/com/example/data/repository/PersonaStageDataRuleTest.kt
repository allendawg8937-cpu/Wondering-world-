package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.PersonaStageData
import com.example.data.model.buildDefaultPersonaStageData
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PersonaStageDataRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndObservePersonaStageData_authenticatedOwner_persistsStageSpecificData() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val kidData = buildDefaultPersonaStageData("kid", uid).copy(
            focusGoal = "Build 3 Solar System Models",
            primaryMetricValue = 4.0,
            completedTaskIds = listOf("kid_task_1", "kid_task_2"),
            stageScoreOrXp = 225L
        )
        val veteranData = buildDefaultPersonaStageData("veteran", uid).copy(
            focusGoal = "Verify VA Clinic & Post-9/11 GI Bill Housing",
            primaryMetricValue = 5.0,
            completedTaskIds = listOf("vet_task_1", "vet_task_3"),
            stageScoreOrXp = 510L
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.savePersonaStageData(kidData).getOrThrow()
            repository.savePersonaStageData(veteranData).getOrThrow()
        }

        val updatedKid = kidData.copy(
            focusGoal = "Finish Robotics Block Coding Badge",
            primaryMetricValue = 5.0,
            customNote = "Unlocked all 5 STEM planets!",
            stageScoreOrXp = 265L
        )
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.savePersonaStageData(updatedKid).getOrThrow()
        }

        val observedList = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeAllPersonaStageData(uid).first { list ->
                list.any { it.stageId == "kid" && it.stageScoreOrXp == 265L } &&
                    list.any { it.stageId == "veteran" && it.stageScoreOrXp == 510L }
            }
        }

        val fetchedKid = observedList.firstOrNull { it.stageId == "kid" }
        val fetchedVeteran = observedList.firstOrNull { it.stageId == "veteran" }
        assertNotNull(fetchedKid)
        assertNotNull(fetchedVeteran)
        assertEquals("Finish Robotics Block Coding Badge", fetchedKid?.focusGoal)
        assertEquals(5.0, fetchedKid?.primaryMetricValue ?: 0.0, 0.001)
        assertEquals("Verify VA Clinic & Post-9/11 GI Bill Housing", fetchedVeteran?.focusGoal)
        assertTrue(fetchedVeteran?.completedTaskIds?.contains("vet_task_3") == true)
    }

    @Test
    fun getPersonaStageData_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.savePersonaStageData(
                buildDefaultPersonaStageData("business", aliceUid)
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.getPersonaStageData(aliceUid, "business").getOrThrow()
            }
            fail("Expected PERMISSION_DENIED when Bob reads Alice's PersonaStageData")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observeAllPersonaStageData_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = WonderingWorldRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeAllPersonaStageData(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED when observing PersonaStageData unauthenticated")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_persona@wonderingworld.test"
        const val BOB_EMAIL = "bob_persona@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
