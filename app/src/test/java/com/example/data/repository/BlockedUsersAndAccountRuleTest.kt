package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.example.data.model.buildDefaultPersonaStageData
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BlockedUsersAndAccountRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun blockAndUnblockUser_authenticatedOwner_persistsAndRemovesBlockRecord() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repo = WonderingWorldRepository(firestore)

        val targetId = "blocked_user_charlie"
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.blockUser(
                blockedUserId = targetId,
                blockedDisplayName = "Charlie Spam",
                reason = "Spam"
            ).getOrThrow()
        }

        val listAfterBlock = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeBlockedUsers(aliceUid).first { items ->
                items.any { it.blockedUserId == targetId }
            }
        }
        assertTrue(listAfterBlock.any { it.blockedDisplayName == "Charlie Spam" && it.reason == "Spam" })

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.unblockUser(targetId).getOrThrow()
        }

        val listAfterUnblock = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeBlockedUsers(aliceUid).first { items ->
                items.none { it.blockedUserId == targetId }
            }
        }
        assertTrue(listAfterUnblock.none { it.blockedUserId == targetId })
    }

    @Test
    fun observeBlockedUsers_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.blockUser("target_user_x", "User X", "Harassment").getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeBlockedUsers(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED when Bob reads Alice's blocked_users")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    @Test
    fun observeBlockedUsers_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repo = WonderingWorldRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeBlockedUsers(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED when unauthenticated user reads blocked_users")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    @Test
    fun deleteUserAccountData_authenticatedOwner_deletesProfileAndSubcollections() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.saveUserProfile(
                UserProfile(
                    userId = aliceUid,
                    displayName = "Alice DeleteTest",
                    role = "adult",
                    age = 29L,
                    location = "Austin, TX",
                    bio = "Testing account deletion",
                    themeColorHex = "#00E5FF"
                )
            ).getOrThrow()
            repo.savePersonaStageData(buildDefaultPersonaStageData("adult", aliceUid)).getOrThrow()
            repo.blockUser("temp_blocked", "Temp Blocked", "Test").getOrThrow()
        }

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.deleteUserAccountData().getOrThrow()
        }

        val profileAfterDelete = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.getUserProfile(aliceUid).getOrThrow()
        }
        assertNull(profileAfterDelete)

        val stageAfterDelete = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.getPersonaStageData(aliceUid, "adult").getOrThrow()
        }
        assertNull(stageAfterDelete)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_settings_block@wonderingworld.test"
        const val BOB_EMAIL = "bob_settings_block@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 6000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
