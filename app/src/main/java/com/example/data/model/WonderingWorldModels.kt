package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "veteran",
    val age: Long = 26L,
    val location: String = "",
    val bio: String = "",
    val profilePictureUrl: String = "",
    val themeColorHex: String = "#6366F1",
    val themeVisualStyle: String = "HYPERCUBE_6D",
    val isDarkMode: Boolean = true,
    val musicTrackTitle: String = "Comfortably Numb - Pink Floyd",
    val musicPlatform: String = "Spotify",
    val savingsBalanceCents: Long = 245075L,
    val savingsGoalCents: Long = 500000L,
    val arcadeHighScore: Long = 0L,
    val pledgeAccepted: Boolean = true,
    val phoneNumber: String = "",
    val interests: List<String> = emptyList(),
    val spotifyConnected: Boolean = false,
    val spotifyUserId: String = "",
    val appleMusicConnected: Boolean = false,
    val appleMusicStorefront: String = "",
    val virtualCurrencyBalance: Long = 250L,
    val earnedBadges: List<String> = listOf("welcome_pioneer"),
    val ownedVirtualItems: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Friend(
    @DocumentId val id: String = "",
    val friendUserId: String = "",
    val displayName: String = "",
    val avatarEmoji: String = "👤",
    val role: String = "adult",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FriendRequest(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val fromName: String = "",
    val fromAvatarEmoji: String = "👤",
    val toUserId: String = "",
    val status: String = "pending", // pending, accepted, declined
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BlockedUser(
    @DocumentId val id: String = "",
    val userId: String = "",
    val blockedUserId: String = "",
    val blockedDisplayName: String = "",
    val reason: String = "Boundary Preference",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SocialFollow(
    @DocumentId val id: String = "",
    val followerId: String = "",
    val followingId: String = "",
    val followingDisplayName: String = "",
    val followingRole: String = "adult",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PostLike(
    @DocumentId val id: String = "",
    val likerId: String = "",
    val postId: String = "",
    val postOwnerId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PostComment(
    @DocumentId val id: String = "",
    val postId: String = "",
    val postOwnerId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorRole: String = "adult",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WallPost(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorRole: String = "",
    val category: String = "Update",
    val title: String = "",
    val content: String = "",
    val likesCount: Long = 0L,
    val accentHex: String = "#6366F1",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BudgetEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Save",
    val amountCents: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CareerMilestone(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val stage: String = "Foundation",
    val description: String = "",
    val targetTerm: String = "Step 1 • Foundation",
    val status: String = "Upcoming",
    val xpReward: Long = 100L,
    val stepOrder: Long = 1L,
    val portalUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ResourceDirectoryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val url: String,
    val roles: List<String>,
    val tags: List<String>
)

data class CommunityConnectionCard(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val bio: String,
    val sharedInterests: List<String>,
    val themeHex: String
)

@IgnoreExtraProperties
data class ModerationFlag(
    @DocumentId val id: String = "",
    val reporterUserId: String = "",
    val targetId: String = "",
    val targetType: String = "", // post, profile, comment
    val reason: String = "",
    val detail: String = "",
    val status: String = "pending",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Profile3DConfig(
    @DocumentId val id: String = "active",
    val userId: String = "",
    val configName: String = "Cosmic Explorer",
    val baseModelId: String = "cyber_astronaut",
    val baseModelName: String = "Cyber Astronaut",
    val primaryColorHex: String = "#00E5FF",
    val secondaryColorHex: String = "#FF2E93",
    val auraColorHex: String = "#00E676",
    val headgearId: String = "head_crown",
    val eyewearId: String = "eye_visor",
    val auraId: String = "aura_drones",
    val rigId: String = "rig_pauldrons",
    val metallicFinish: Double = 0.72,
    val scaleFactor: Double = 1.0,
    val autoRotate: Boolean = true,
    val turntableYawDeg: Double = 14.0,
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PersonaStageData(
    @DocumentId val id: String = "adult",
    val userId: String = "",
    val stageId: String = "adult",
    val stageLabel: String = "Adult",
    val focusGoal: String = "",
    val primaryMetricLabel: String = "Weekly Milestones",
    val primaryMetricValue: Double = 0.0,
    val primaryMetricTarget: Double = 10.0,
    val pinnedTopics: List<String> = emptyList(),
    val completedTaskIds: List<String> = emptyList(),
    val customNote: String = "",
    val stageScoreOrXp: Long = 100L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun normalizePersonaStageId(rawRoleOrStage: String): String {
    val lower = rawRoleOrStage.trim().lowercase()
    return when {
        lower.contains("kid") || lower.contains("child") -> "kid"
        lower.contains("teen") -> "teen"
        lower.contains("student") -> "student"
        lower.contains("business") || lower.contains("founder") -> "business"
        lower.contains("veteran") -> "veteran"
        else -> "adult"
    }
}

fun buildDefaultPersonaStageData(
    stageId: String,
    userId: String = "local_user"
): PersonaStageData {
    val normalized = normalizePersonaStageId(stageId)
    return when (normalized) {
        "kid" -> PersonaStageData(
            id = "kid",
            userId = userId,
            stageId = "kid",
            stageLabel = "Kid",
            focusGoal = "Finish 5 Solar System & Block Coding Quests",
            primaryMetricLabel = "STEM Missions Completed",
            primaryMetricValue = 2.0,
            primaryMetricTarget = 5.0,
            pinnedTopics = listOf("Solar Quest & Space", "Block Coding 101"),
            completedTaskIds = listOf("kid_task_1"),
            customNote = "Building a 3D rocket ship and learning how planets orbit!",
            stageScoreOrXp = 180L
        )
        "teen" -> PersonaStageData(
            id = "teen",
            userId = userId,
            stageId = "teen",
            stageLabel = "Teen",
            focusGoal = "Log 20 Volunteer Hours & Ship Hackathon Demo",
            primaryMetricLabel = "Service Hours Logged",
            primaryMetricValue = 8.0,
            primaryMetricTarget = 20.0,
            pinnedTopics = listOf("Kotlin & Compose", "Scholarship Roadmap"),
            completedTaskIds = listOf("teen_task_1"),
            customNote = "Working on my first Jetpack Compose app and library volunteer shifts.",
            stageScoreOrXp = 240L
        )
        "student" -> PersonaStageData(
            id = "student",
            userId = userId,
            stageId = "student",
            stageLabel = "Student",
            focusGoal = "Submit FAFSA Priority Aid & Complete Midterm Prep",
            primaryMetricLabel = "Study Focus Hours",
            primaryMetricValue = 12.0,
            primaryMetricTarget = 25.0,
            pinnedTopics = listOf("FAFSA & Pell Grants", "Academic Research"),
            completedTaskIds = listOf("student_task_1"),
            customNote = "Reviewing flashcards and organizing summer internship applications.",
            stageScoreOrXp = 320L
        )
        "business" -> PersonaStageData(
            id = "business",
            userId = userId,
            stageId = "business",
            stageLabel = "Business",
            focusGoal = "Finalize SBA Microloan Packet & Launch Q4 Storefront",
            primaryMetricLabel = "Capital Readiness Score",
            primaryMetricValue = 65.0,
            primaryMetricTarget = 100.0,
            pinnedTopics = listOf("SBA 7(a) Capital", "LLC Formation & EIN"),
            completedTaskIds = listOf("biz_task_1"),
            customNote = "Preparing operating cashflow forecast and supplier delivery schedule.",
            stageScoreOrXp = 410L
        )
        "veteran" -> PersonaStageData(
            id = "veteran",
            userId = userId,
            stageId = "veteran",
            stageLabel = "Veteran",
            focusGoal = "Complete PACT Act Screening & GI Bill Housing Verification",
            primaryMetricLabel = "Transition Steps Done",
            primaryMetricValue = 3.0,
            primaryMetricTarget = 6.0,
            pinnedTopics = listOf("PACT Act & VA Health", "Post-9/11 GI Bill"),
            completedTaskIds = listOf("vet_task_1"),
            customNote = "Connected with local DAV peer mentor and verified clinic appointment.",
            stageScoreOrXp = 450L
        )
        else -> PersonaStageData(
            id = "adult",
            userId = userId,
            stageId = "adult",
            stageLabel = "Adult",
            focusGoal = "Build 3-Month Emergency Fund & Weekly Wellness Routine",
            primaryMetricLabel = "Stability Check-Ins",
            primaryMetricValue = 4.0,
            primaryMetricTarget = 10.0,
            pinnedTopics = listOf("211 Housing & Relief", "Smart Auto Finance"),
            completedTaskIds = listOf("adult_task_1"),
            customNote = "Tracking monthly savings contributions and family schedule balance.",
            stageScoreOrXp = 290L
        )
    }
}

fun buildDefaultStudentCareerMilestones(userId: String = "local_user"): List<CareerMilestone> = listOf(
    CareerMilestone(
        id = "seed_milestone_1",
        userId = userId,
        title = "Complete FAFSA & Federal Student Aid Profile",
        stage = "Foundation",
        description = "Submit your Federal Student Aid form on StudentAid.gov to unlock Pell Grants, state scholarships, and work-study funding.",
        targetTerm = "Step 1 • Financial Aid & Grants",
        status = "Completed",
        xpReward = 150L,
        stepOrder = 1L,
        portalUrl = "https://studentaid.gov/"
    ),
    CareerMilestone(
        id = "seed_milestone_2",
        userId = userId,
        title = "Master Core STEM & SAT/AP Prep Modules",
        stage = "Academic",
        description = "Complete 3 core study modules on Khan Academy & OpenLibrary and build your academic research portfolio.",
        targetTerm = "Step 2 • Academic Mastery",
        status = "Completed",
        xpReward = 175L,
        stepOrder = 2L,
        portalUrl = "https://www.khanacademy.org/"
    ),
    CareerMilestone(
        id = "seed_milestone_3",
        userId = userId,
        title = "Submit Common App College & Trade School Applications",
        stage = "Application",
        description = "Finalize personal statement essay, counselor recommendation letters, and submit applications to top-choice schools.",
        targetTerm = "Step 3 • School Admissions",
        status = "In Progress",
        xpReward = 225L,
        stepOrder = 3L,
        portalUrl = "https://www.commonapp.org/"
    ),
    CareerMilestone(
        id = "seed_milestone_4",
        userId = userId,
        title = "Apply for Paid Student Internships & Apprenticeships",
        stage = "Internship",
        description = "Apply to 3 paid student internships or earn-while-you-learn apprenticeships via Handshake, USAJOBS Pathways, or Apprenticeship.gov.",
        targetTerm = "Step 4 • Hands-On Experience",
        status = "In Progress",
        xpReward = 250L,
        stepOrder = 4L,
        portalUrl = "https://joinhandshake.com/"
    ),
    CareerMilestone(
        id = "seed_milestone_5",
        userId = userId,
        title = "Launch Full-Time Career Portfolio & Interview Readiness",
        stage = "Career Launch",
        description = "Publish your verified resume on CareerOneStop & LinkedIn Jobs and complete mock technical/career interviews.",
        targetTerm = "Step 5 • Career Launch",
        status = "Upcoming",
        xpReward = 300L,
        stepOrder = 5L,
        portalUrl = "https://www.careeronestop.org/"
    )
)
