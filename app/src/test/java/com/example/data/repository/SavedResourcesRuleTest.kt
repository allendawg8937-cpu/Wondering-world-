package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.SavedResource
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SavedResourcesRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveObserveAndRemoveSavedResource_authenticatedOwner_persistsInFirestore() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val item = SavedResource(
            id = "lifeline_988",
            userId = uid,
            resourceId = "lifeline_988",
            title = "988 Suicide & Crisis Lifeline",
            subtitle = "24/7 confidential mental health & crisis support.",
            badge = "CRISIS LIFELINE • FREE",
            url = "https://988lifeline.org/",
            category = "Crisis Support",
            stageId = "all"
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveResource(item).getOrThrow()
        }

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeSavedResources(uid).first { list ->
                list.any { it.resourceId == "lifeline_988" }
            }
        }
        val fetched = observed.firstOrNull { it.resourceId == "lifeline_988" }
        assertNotNull(fetched)
        assertEquals("988 Suicide & Crisis Lifeline", fetched?.title)
        assertEquals("https://988lifeline.org/", fetched?.url)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.removeSavedResource("lifeline_988").getOrThrow()
        }

        val afterDelete = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeSavedResources(uid).first { list ->
                list.none { it.resourceId == "lifeline_988" }
            }
        }
        assertTrue(afterDelete.none { it.resourceId == "lifeline_988" })
    }

    @Test
    fun observeSavedResources_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveResource(
                SavedResource(
                    resourceId = "va_pact_act",
                    userId = aliceUid,
                    title = "VA PACT Act Portal",
                    subtitle = "Expanded veteran healthcare benefits",
                    badge = "VA HEALTH",
                    url = "https://www.va.gov/resources/",
                    category = "Veterans",
                    stageId = "veteran"
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeSavedResources(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED when Bob reads Alice's saved resources")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    @Test
    fun observeSavedResources_unauthenticated_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = WonderingWorldRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeSavedResources(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED when unauthenticated caller reads saved resources")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_saved@wonderingworld.test"
        const val BOB_EMAIL = "bob_saved@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
