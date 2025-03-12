package com.example.studez_feed.models

data class FeedbackItem(
    val id: Int,
    val category: String,
    val date: String,
    val feedback: String,
    val status: String
)