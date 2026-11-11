package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserInteraction(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val actionType: String = "FeatureVisit",
    val detail: String = "",
    val category: String = "General",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

data class PersonalizedDailyInsight(
    val id: String,
    val title: String,
    val suggestion: String,
    val category: String,
    val actionLabel: String,
    val targetTabName: String,
    val impactScore: Int = 96
)
