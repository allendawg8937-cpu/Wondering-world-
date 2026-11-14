package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class SocialGraphRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun userProfile_persistsProfilePictureUrl_andSocialFollowLikeCommentLifecycle() = runBlocking {
        val bobUid = signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.saveUserProfile(
                UserProfile(
                    userId = bobUid,
                    displayName = "Bob Mentor",
                    email = BOB_EMAIL,
                    role = "veteran",
                    age = 36L,
                    bio = "Supporting transitions",
                    profilePictureUrl = "https://example.com/avatars/bob.png"
                )
            ).getOrThrow()
        }

        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveUserProfile(
                UserProfile(
                    userId = aliceUid,
                    displayName = "Alice Explorer",
                    email = ALICE_EMAIL,
                    role = "student",
                    age = 21L,
                    bio = "Learning daily",
                    profilePictureUrl = "https://example.com/avatars/alice.png"
                )
            ).getOrThrow()
        }
        val fetchedAlice = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.getUserProfile(aliceUid).getOrThrow()
        }
        assertEquals("https://example.com/avatars/alice.png", fetchedAlice?.profilePictureUrl)

        // Follow & unfollow Bob
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.followUser(bobUid, "Bob Mentor", "veteran").getOrThrow()
        }
        val following = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observeFollowing(aliceUid).first { items -> items.any { it.followingId == bobUid } }
        }
        assertTrue(following.any { it.followingDisplayName == "Bob Mentor" })

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.unfollowUser(bobUid).getOrThrow()
        }
        val afterUnfollow = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observeFollowing(aliceUid).first { items -> items.none { it.followingId == bobUid } }
        }
        assertTrue(afterUnfollow.none { it.followingId == bobUid })

        // Like & unlike post
        val postId = "post_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.likePost(postId = postId, postOwnerId = bobUid).getOrThrow()
        }
        val likes = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observePostLikes(aliceUid).first { items -> items.any { it.postId == postId } }
        }
        assertTrue(likes.any { it.postOwnerId == bobUid })

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.unlikePost(postId).getOrThrow()
        }
        val afterUnlike = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observePostLikes(aliceUid).first { items -> items.none { it.postId == postId } }
        }
        assertTrue(afterUnlike.none { it.postId == postId })

        // Comment & delete comment on post
        val commentId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.addPostComment(
                postId = postId,
                postOwnerId = bobUid,
                authorName = "Alice Explorer",
                authorRole = "student",
                text = "Encouraging milestone!"
            ).getOrThrow()
        }
        val comments = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observePostComments(postId).first { items -> items.any { it.id == commentId } }
        }
        assertTrue(comments.any { it.text == "Encouraging milestone!" })

        // Cross-user comment deletion fails with PERMISSION_DENIED
        signInTestUser(BOB_EMAIL)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.deletePostComment(commentId).getOrThrow()
            }
            fail("Expected PERMISSION_DENIED when Bob deletes Alice's comment")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }

        // Author deletes own comment
        signInTestUser(ALICE_EMAIL)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.deletePostComment(commentId).getOrThrow()
        }
    }

    @Test
    fun socialGraph_unauthenticatedAndCrossUserReads_failWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = WonderingWorldRepository(firestore)
        val mentorId = "mentor_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.followUser(mentorId, "Mentor One", "adult").getOrThrow()
        }

        // Cross-user isolation on following & post_likes
        signInTestUser(BOB_EMAIL)
        val bobRepo = WonderingWorldRepository(firestore)
        try {
            withTimeout(FLOW_TIMEOUT_MS) { bobRepo.observeFollowing(aliceUid).first() }
            fail("Expected PERMISSION_DENIED for cross-user following read")
        } catch (e: Exception) {
            assertEquals(
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                checkNotNull(e.unwrapFirestoreException()).code
            )
        }

        // Unauthenticated read rejection
        auth.signOut()
        try {
            withTimeout(FLOW_TIMEOUT_MS) { bobRepo.observePostComments("any_post").first() }
            fail("Expected PERMISSION_DENIED for unauthenticated comment read")
        } catch (e: Exception) {
            assertEquals(
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                checkNotNull(e.unwrapFirestoreException()).code
            )
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_social_graph@wonderingworld.test"
        const val BOB_EMAIL = "bob_social_graph@wonderingworld.test"
        const val DEFAULT_TIMEOUT_MS = 6000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
