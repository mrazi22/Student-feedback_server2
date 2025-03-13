package com.example.studez_feed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studez_feed.network.AdminFeedbackItem
import com.example.studez_feed.network.FeedbackTemplate

import com.example.studez_feed.repository.AdminFeedbackRepository
import kotlinx.coroutines.launch

class AdminFeedbackViewModel : ViewModel() {
    var feedbackListResult: ((List<AdminFeedbackItem>?) -> Unit)? = null
    var feedbackActionResult: ((Boolean, String) -> Unit)? = null // ✅ Notify UI with message
    var feedbackTemplatesResult: ((List<FeedbackTemplate>?) -> Unit)? = null
    var feedbackTemplateActionResult: ((Boolean, String) -> Unit)? = null

    fun createFeedbackTemplate(
        token: String,
        category: String,
        questions: List<Map<String, Any>>, // ✅ Incoming List<Map<String, Any>>
        onResult: (Boolean) -> Unit
    ) {
        val formattedQuestions = questions.map { questionData ->
            mapOf(
                "question" to (questionData["question"] as? String ?: ""),
                "choices" to (questionData["choices"] as? List<String> ?: emptyList())
            )
        }

        viewModelScope.launch {
            AdminFeedbackRepository.createFeedbackTemplate(token, category, formattedQuestions, onResult)
        }
    }




    fun getAllFeedback(token: String) {
        viewModelScope.launch {
            AdminFeedbackRepository.getAllFeedback(token) { response ->
                feedbackListResult?.invoke(response)
            }
        }
    }

    fun updateFeedbackStatus(token: String, feedbackId: String, status: String) {
        val lowercaseStatus = status.lowercase() // ✅ Convert status to lowercase

        Log.d("FEEDBACK_ACTION", "Updating feedback: $feedbackId to $lowercaseStatus") // ✅ Debug Log

        viewModelScope.launch {
            AdminFeedbackRepository.manageFeedback(token, feedbackId, lowercaseStatus) { response ->
                if (response != null) {
                    feedbackActionResult?.invoke(true, "Feedback $lowercaseStatus successfully ✅")
                    getAllFeedback(token) // ✅ Refresh feedback list after update
                } else {
                    feedbackActionResult?.invoke(false, "Failed to update feedback ❌")
                }
            }
        }
    }

    fun deleteFeedback(token: String, feedbackId: String) {
        viewModelScope.launch {
            AdminFeedbackRepository.deleteFeedback(token, feedbackId) { isSuccess ->
                if (isSuccess) {
                    feedbackActionResult?.invoke(true, "Feedback deleted successfully 🗑️")
                    getAllFeedback(token) // ✅ Refresh feedback list after delete
                } else {
                    feedbackActionResult?.invoke(false, "Failed to delete feedback ❌")
                }
            }
        }
    }

    fun getAllFeedbackTemplates(token: String) {
        viewModelScope.launch {
            AdminFeedbackRepository.getAllFeedbackTemplates(token) { response ->
                feedbackTemplatesResult?.invoke(response)
            }
        }
    }

    fun updateFeedbackTemplate(
        token: String,
        category: String, // ✅ Use category instead of templateId
        formattedQuestions: List<Map<String, Any>>, // ✅ Ensure correct type
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            AdminFeedbackRepository.updateFeedbackTemplate(token, category, formattedQuestions, onResult)
        }
    }



    fun deleteFeedbackTemplate(token: String, templateId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            AdminFeedbackRepository.deleteFeedbackTemplate(token, templateId, onResult)
        }
    }


}