package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Profile3DConfig
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class Profile3DConfigRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveUpdateObserveAndDeleteProfile3DConfig_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WonderingWorldRepository(firestore)

        val initialConfig = Profile3DConfig(
            id = "active",
            userId = uid,
            configName = "Starlight Pioneer",
            baseModelId = "cyber_astronaut",
            baseModelName = "Cyber Astronaut",
            primaryColorHex = "#00E5FF",
            secondaryColorHex = "#FF2E93",
            auraColorHex = "#00E676",
            headgearId = "head_crown",
            eyewearId = "eye_visor",
            auraId = "aura_drones",
            rigId = "rig_pauldrons",
            metallicFinish = 0.78,
            scaleFactor = 1.05,
            autoRotate = true,
            turntableYawDeg = 22.0,
            isActive = true
        )

        val savedId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProfile3DConfig(initialConfig).getOrThrow()
        }
        assertEquals("active", savedId)

        val configs = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeProfile3DConfigs(uid).first { list -> list.any { it.id == "active" } }
        }
        val fetched = configs.first { it.id == "active" }
        assertEquals("Starlight Pioneer", fetched.configName)
        assertEquals("cyber_astronaut", fetched.baseModelId)
        assertEquals("#00E5FF", fetched.primaryColorHex)

        // Verify update preserves immutable fields and updates mutable customization fields
        val updatedConfig = initialConfig.copy(
            baseModelId = "holo_scholar",
            baseModelName = "Holo Scholar",
            headgearId = "head_halo",
            eyewearId = "eye_hud",
            auraId = "aura_wings",
            rigId = "rig_collar",
            primaryColorHex = "#8B5CF6",
            metallicFinish = 0.92
        )
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProfile3DConfig(updatedConfig).getOrThrow()
        }

        val reloaded = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getProfile3DConfig(uid, "active").getOrThrow()
        }
        assertNotNull(reloaded)
        assertEquals("holo_scholar", reloaded?.baseModelId)
        assertEquals("head_halo", reloaded?.headgearId)
        assertEquals("#8B5CF6", reloaded?.primaryColorHex)

        // Delete secondary slot
        val slotConfig = initialConfig.copy(id = "preset_eco", configName = "Eco Kit", baseModelId = "eco_guardian", isActive = false)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProfile3DConfig(slotConfig).getOrThrow()
            repository.deleteProfile3DConfig("preset_eco").getOrThrow()
        }
        val deletedCheck = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getProfile3DConfig(uid, "preset_eco").getOrThrow()
        }
        assertNull(deletedCheck)
    }

    @Test
    fun getProfile3DConfig_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveProfile3DConfig(
                Profile3DConfig(
                    id = "active",
                    userId = aliceUid,
                    configName = "Alice Private Build",
                    baseModelId = "neon_tactician",
                    baseModelName = "Neon Tactician"
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.getProfile3DConfig(aliceUid, "active").getOrThrow()
            }
            fail("Expected PERMISSION_DENIED when Bob reads Alice's 3D profile config")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observeProfile3DConfigs_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = WonderingWorldRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeProfile3DConfigs(aliceUid).first()
            }
            fail("Expected exception when observing 3D profile configs unauthenticated")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_3d@wonderingworld.test"
        const val BOB_EMAIL = "bob_3d@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
