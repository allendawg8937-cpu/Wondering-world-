package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.BudgetEntry
import com.example.data.model.CareerMilestone
import com.example.data.model.Friend
import com.example.data.model.FriendRequest
import com.example.data.model.UserInteraction
import com.example.data.model.UserProfile
import com.example.data.model.WallPost
import com.example.data.model.buildDefaultStudentCareerMilestones
import com.example.data.remote.OperationType
import com.example.data.remote.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class WonderingWorldRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject<UserProfile>(
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> = runCatching {
        val docRef = db.collection("users").document(userId)
        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                snapshot.toObject<UserProfile>(
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val sanitizedName = profile.displayName.trim().ifEmpty { "Wondering Explorer" }.take(60)
        val sanitizedRole = if (profile.role in listOf("veteran", "business", "creator", "adult", "student")) {
            profile.role
        } else {
            "adult"
        }
        val sanitizedAge = profile.age.coerceIn(13L, 120L)
        val sanitizedLocation = profile.location.trim().take(80)
        val sanitizedBio = profile.bio.trim().take(300)
        val sanitizedColor = profile.themeColorHex.trim().ifEmpty { "#6366F1" }.take(9)
        val sanitizedVisualStyle = profile.themeVisualStyle.trim().ifEmpty { "HYPERCUBE_6D" }.take(40)
        val sanitizedDarkMode = profile.isDarkMode
        val sanitizedTrack = profile.musicTrackTitle.trim().take(120)
        val sanitizedPlatform = profile.musicPlatform.trim().ifEmpty { "Spotify" }.take(40)
        val sanitizedBalance = profile.savingsBalanceCents.coerceIn(0L, 1000000000L)
        val sanitizedGoal = profile.savingsGoalCents.coerceIn(0L, 1000000000L)
        val sanitizedScore = profile.arcadeHighScore.coerceIn(0L, 10000000L)
        val sanitizedInterests = profile.interests.map { it.trim().take(50) }.filter { it.isNotEmpty() }.take(10)
        val sanitizedPhone = profile.phoneNumber.trim().take(20)
        val email = profile.email.ifBlank { auth.currentUser?.email ?: "" }.take(100)

        try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updatePayload = mapOf(
                    "displayName" to sanitizedName,
                    "email" to email,
                    "role" to sanitizedRole,
                    "age" to sanitizedAge,
                    "location" to sanitizedLocation,
                    "bio" to sanitizedBio,
                    "themeColorHex" to sanitizedColor,
                    "themeVisualStyle" to sanitizedVisualStyle,
                    "isDarkMode" to sanitizedDarkMode,
                    "musicTrackTitle" to sanitizedTrack,
                    "musicPlatform" to sanitizedPlatform,
                    "savingsBalanceCents" to sanitizedBalance,
                    "savingsGoalCents" to sanitizedGoal,
                    "arcadeHighScore" to sanitizedScore,
                    "pledgeAccepted" to true,
                    "phoneNumber" to sanitizedPhone,
                    "interests" to sanitizedInterests,
                    "spotifyConnected" to profile.spotifyConnected,
                    "spotifyUserId" to profile.spotifyUserId,
                    "appleMusicConnected" to profile.appleMusicConnected,
                    "appleMusicStorefront" to profile.appleMusicStorefront,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            } else {
                val createPayload = mapOf(
                    "userId" to uid,
                    "displayName" to sanitizedName,
                    "email" to email,
                    "role" to sanitizedRole,
                    "age" to sanitizedAge,
                    "location" to sanitizedLocation,
                    "bio" to sanitizedBio,
                    "themeColorHex" to sanitizedColor,
                    "themeVisualStyle" to sanitizedVisualStyle,
                    "isDarkMode" to sanitizedDarkMode,
                    "musicTrackTitle" to sanitizedTrack,
                    "musicPlatform" to sanitizedPlatform,
                    "savingsBalanceCents" to sanitizedBalance,
                    "savingsGoalCents" to sanitizedGoal,
                    "arcadeHighScore" to sanitizedScore,
                    "pledgeAccepted" to true,
                    "phoneNumber" to sanitizedPhone,
                    "interests" to sanitizedInterests,
                    "spotifyConnected" to profile.spotifyConnected,
                    "spotifyUserId" to profile.spotifyUserId,
                    "appleMusicConnected" to profile.appleMusicConnected,
                    "appleMusicStorefront" to profile.appleMusicStorefront,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- Friends & Discovery Actions ---

    fun observeFriends(userId: String): Flow<List<Friend>> = flow {
        emitAll(
            db.collection("users").document(userId).collection("friends")
                .snapshots()
                .map { it.toObjects<Friend>() }
        )
    }

    fun observeIncomingFriendRequests(userId: String): Flow<List<FriendRequest>> = flow {
        emitAll(
            db.collection("friend_requests")
                .whereEqualTo("toUserId", userId)
                .whereEqualTo("status", "pending")
                .snapshots()
                .map { it.toObjects<FriendRequest>() }
        )
    }

    suspend fun sendFriendRequest(toUserId: String, fromName: String, fromAvatar: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        if (uid == toUserId) return@runCatching
        
        // Check for existing request
        val existing = db.collection("friend_requests")
            .whereEqualTo("fromUserId", uid)
            .whereEqualTo("toUserId", toUserId)
            .limit(1)
            .get()
            .await()
        
        if (existing.isEmpty) {
            val payload = mapOf(
                "fromUserId" to uid,
                "fromName" to fromName,
                "fromAvatarEmoji" to fromAvatar,
                "toUserId" to toUserId,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("friend_requests").add(payload).await()
        }
    }

    suspend fun acceptFriendRequest(request: FriendRequest, myName: String, myAvatar: String, myRole: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        if (request.toUserId != uid) return@runCatching

        val batch = db.batch()
        
        // Update request status
        val reqRef = db.collection("friend_requests").document(request.id)
        batch.update(reqRef, "status", "accepted")

        // Add to my friends
        val myFriendRef = db.collection("users").document(uid).collection("friends").document(request.fromUserId)
        batch.set(myFriendRef, mapOf(
            "friendUserId" to request.fromUserId,
            "displayName" to request.fromName,
            "avatarEmoji" to request.fromAvatarEmoji,
            "role" to "adult", // Defaulting role as it's not in request
            "createdAt" to FieldValue.serverTimestamp()
        ))

        // Add to their friends
        val theirFriendRef = db.collection("users").document(request.fromUserId).collection("friends").document(uid)
        batch.set(theirFriendRef, mapOf(
            "friendUserId" to uid,
            "displayName" to myName,
            "avatarEmoji" to myAvatar,
            "role" to myRole,
            "createdAt" to FieldValue.serverTimestamp()
        ))

        batch.commit().await()
    }

    suspend fun declineFriendRequest(requestId: String): Result<Unit> = runCatching {
        db.collection("friend_requests").document(requestId).update("status", "declined").await()
    }

    suspend fun searchUsersByPhone(phone: String): Result<UserProfile?> = runCatching {
        val snapshot = db.collection("users")
            .whereEqualTo("phoneNumber", phone)
            .limit(1)
            .get()
            .await()
        snapshot.toObjects(UserProfile::class.java).firstOrNull()
    }

    suspend fun searchUsersByNameOrEmail(query: String): Result<List<UserProfile>> = runCatching {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return@runCatching emptyList()

        // Search by email (exact)
        val emailQuery = db.collection("users")
            .whereEqualTo("email", trimmedQuery)
            .get()
            .await()

        // Search by name (prefix match for simple implementation)
        val nameQuery = db.collection("users")
            .whereGreaterThanOrEqualTo("displayName", trimmedQuery)
            .whereLessThanOrEqualTo("displayName", trimmedQuery + "\uf8ff")
            .limit(10)
            .get()
            .await()

        val results = (emailQuery.toObjects<UserProfile>() + nameQuery.toObjects<UserProfile>())
            .distinctBy { it.userId }
            .filter { it.userId != auth.currentUser?.uid } // Don't find yourself
        
        results
    }

    suspend fun updateUserPhoneNumber(phone: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        db.collection("users").document(uid).update("phoneNumber", phone).await()
    }

    suspend fun reportContent(
        targetId: String,
        targetType: String,
        reason: String,
        detail: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val payload = mapOf(
            "reporterUserId" to uid,
            "targetId" to targetId,
            "targetType" to targetType,
            "reason" to reason,
            "detail" to detail.take(500),
            "status" to "pending",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("moderation_flags").add(payload).await()
    }

    fun observeWallPosts(userId: String): Flow<List<WallPost>> = flow {
        val path = "users/$userId/posts"
        emitAll(
            db.collection("users").document(userId).collection("posts")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        WallPost::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createWallPost(
        authorName: String,
        authorRole: String,
        category: String,
        title: String,
        content: String,
        accentHex: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("posts").document()
        val validCategory = if (category in listOf("Update", "Resource Guide", "Studio Art", "Milestone")) {
            category
        } else {
            "Update"
        }
        val payload = mapOf(
            "userId" to uid,
            "authorName" to authorName.trim().ifEmpty { "Explorer" }.take(60),
            "authorRole" to authorRole.trim().ifEmpty { "adult" }.take(30),
            "category" to validCategory,
            "title" to title.trim().ifEmpty { "Wondering Update" }.take(100),
            "content" to content.trim().ifEmpty { "Sharing with the Wondering World community." }.take(1000),
            "likesCount" to 0L,
            "accentHex" to accentHex.trim().ifEmpty { "#6366F1" }.take(9),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun incrementPostLikes(post: WallPost): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(post.id.isNotBlank()) { "Post ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("posts").document(post.id)
        try {
            docRef.update(
                mapOf(
                    "likesCount" to (post.likesCount + 1L).coerceAtMost(1000000L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteWallPost(postId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(postId.isNotBlank()) { "Post ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("posts").document(postId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    fun observeBudgetEntries(userId: String): Flow<List<BudgetEntry>> = flow {
        val path = "users/$userId/budget_entries"
        emitAll(
            db.collection("users").document(userId).collection("budget_entries")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BudgetEntry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun addBudgetEntry(
        title: String,
        category: String,
        amountCents: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("budget_entries").document()
        val validCategory = if (category in listOf("Save", "Spend", "Give", "Goal")) {
            category
        } else {
            "Save"
        }
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().ifEmpty { "Savings Contribution" }.take(80),
            "category" to validCategory,
            "amountCents" to amountCents.coerceIn(1L, 100000000L),
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteBudgetEntry(entryId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(entryId.isNotBlank()) { "Entry ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("budget_entries").document(entryId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    fun observeUserInteractions(userId: String): Flow<List<UserInteraction>> = flow {
        val path = "users/$userId/interactions"
        emitAll(
            db.collection("users").document(userId).collection("interactions")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        UserInteraction::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun logUserInteraction(
        actionType: String,
        detail: String,
        category: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("interactions").document()
        val validActionTypes = listOf(
            "FeatureVisit",
            "MusicPlay",
            "SportSelect",
            "SportsPin",
            "SearchQuery",
            "ThemeChange",
            "StudySession",
            "GoalLog",
            "InsightAction",
            "MoodCheckIn",
            "FinanceLiteracy",
            "ActivityLog"
        )
        val sanitizedAction = if (actionType in validActionTypes) actionType else "FeatureVisit"
        val payload = mapOf(
            "userId" to uid,
            "actionType" to sanitizedAction,
            "detail" to detail.trim().ifEmpty { "User interaction in Wondering World" }.take(200),
            "category" to category.trim().ifEmpty { "General" }.take(50),
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteUserInteraction(interactionId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(interactionId.isNotBlank()) { "Interaction ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("interactions").document(interactionId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Student Career & School Milestone Objectives (Firestore Vertical Timeline) ---

    fun observeCareerMilestones(userId: String): Flow<List<CareerMilestone>> = flow {
        val path = "users/$userId/career_milestones"
        emitAll(
            db.collection("users").document(userId).collection("career_milestones")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CareerMilestone::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedWith(compareBy<CareerMilestone> { it.stepOrder }.thenBy { it.createdAt ?: Timestamp(0, 0) })
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getCareerMilestones(userId: String): Result<List<CareerMilestone>> = runCatching {
        val path = "users/$userId/career_milestones"
        try {
            val snapshot = db.collection("users").document(userId).collection("career_milestones")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects<CareerMilestone>(
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ).sortedBy { it.stepOrder }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }
    }

    suspend fun addCareerMilestone(
        title: String,
        stage: String,
        description: String,
        targetTerm: String,
        status: String = "In Progress",
        xpReward: Long = 150L,
        stepOrder: Long = 6L,
        portalUrl: String = ""
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("career_milestones").document()
        val validStages = listOf("Foundation", "Academic", "Application", "Internship", "Career Launch")
        val validStatuses = listOf("Upcoming", "In Progress", "Completed")
        val sanitizedStage = if (stage in validStages) stage else "Application"
        val sanitizedStatus = if (status in validStatuses) status else "In Progress"

        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().ifEmpty { "Student Career Milestone" }.take(120),
            "stage" to sanitizedStage,
            "description" to description.trim().ifEmpty { "Complete student objective in the Career Portal." }.take(400),
            "targetTerm" to targetTerm.trim().ifEmpty { "Current Academic Term" }.take(40),
            "status" to sanitizedStatus,
            "xpReward" to xpReward.coerceIn(10L, 1000L),
            "stepOrder" to stepOrder.coerceIn(1L, 100L),
            "portalUrl" to portalUrl.trim().take(300),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateCareerMilestoneStatus(
        milestoneId: String,
        newStatus: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(milestoneId.isNotBlank()) { "Milestone ID must not be blank" }
        val validStatuses = listOf("Upcoming", "In Progress", "Completed")
        val sanitizedStatus = if (newStatus in validStatuses) newStatus else "In Progress"
        val docRef = db.collection("users").document(uid).collection("career_milestones").document(milestoneId)
        try {
            docRef.update(
                mapOf(
                    "status" to sanitizedStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteCareerMilestone(milestoneId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(milestoneId.isNotBlank()) { "Milestone ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("career_milestones").document(milestoneId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun seedDefaultStudentCareerMilestonesIfEmpty(userId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        if (uid != userId) return@runCatching
        val colRef = db.collection("users").document(uid).collection("career_milestones")
        try {
            val existing = colRef.whereEqualTo("userId", uid).limit(1).get().await()
            if (existing.isEmpty) {
                val defaults = buildDefaultStudentCareerMilestones(uid)
                defaults.forEach { item ->
                    val docRef = colRef.document(item.id)
                    val payload = mapOf(
                        "userId" to uid,
                        "title" to item.title.take(120),
                        "stage" to item.stage,
                        "description" to item.description.take(400),
                        "targetTerm" to item.targetTerm.take(40),
                        "status" to item.status,
                        "xpReward" to item.xpReward.coerceIn(10L, 1000L),
                        "stepOrder" to item.stepOrder.coerceIn(1L, 100L),
                        "portalUrl" to item.portalUrl.take(300),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    docRef.set(payload).await()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/$uid/career_milestones")
            throw e
        }
    }
}
