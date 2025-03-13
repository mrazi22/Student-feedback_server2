package com.example.studez_feed.repository

import android.util.Log
import com.example.studez_feed.network.AdminFeedbackItem
import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.FeedbackTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AdminFeedbackRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun getAllFeedback(token: String, onResult: (List<AdminFeedbackItem>?) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.getAllFeedbackForAdmin(authHeader)
            .enqueue(object : Callback<List<AdminFeedbackItem>> {
                override fun onResponse(call: Call<List<AdminFeedbackItem>>, response: Response<List<AdminFeedbackItem>>) {
                    onResult(response.body())
                }

                override fun onFailure(call: Call<List<AdminFeedbackItem>>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch feedback: ${t.message}")
                    onResult(null)
                }
            })
    }

    fun manageFeedback(token: String, feedbackId: String, status: String, onResult: (AdminFeedbackItem?) -> Unit) {
        apiService.manageFeedback("Bearer $token", feedbackId, mapOf("status" to status))
            .enqueue(object : Callback<AdminFeedbackItem> {
                override fun onResponse(call: Call<AdminFeedbackItem>, response: Response<AdminFeedbackItem>) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        Log.e("API_ERROR", "Failed to update feedback: ${response.errorBody()?.string()}")
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<AdminFeedbackItem>, t: Throwable) {
                    Log.e("API_ERROR", "Network error: ${t.message}")
                    onResult(null)
                }
            })
    }

    fun deleteFeedback(token: String, feedbackId: String, onResult: (Boolean) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.deleteFeedback(authHeader, feedbackId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResult(response.isSuccessful)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to delete feedback: ${t.message}")
                    onResult(false)
                }
            })
    }

    fun createFeedbackTemplate(
        token: String,
        category: String,
        questions: List<Map<String, Any>>, // ✅ Correct type!
        onResult: (Boolean) -> Unit
    ) {
        val authHeader = "Bearer $token"

        val requestBody = mapOf(
            "category" to category,
            "questions" to questions // ✅ Now correctly formatted
        )

        apiService.createFeedbackTemplate(authHeader, requestBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("API_SUCCESS", "Feedback template created successfully")
                        onResult(true)
                    } else {
                        Log.e("API_ERROR", "Error creating feedback template: ${response.errorBody()?.string()}")
                        onResult(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API_ERROR", "Network request failed: ${t.message}")
                    onResult(false)
                }
            })
    }


    fun getAllFeedbackTemplates(token: String, onResult: (List<FeedbackTemplate>?) -> Unit) {
        apiService.getAllFeedbackTemplates("Bearer $token")
            .enqueue(object : Callback<List<FeedbackTemplate>> {
                override fun onResponse(call: Call<List<FeedbackTemplate>>, response: Response<List<FeedbackTemplate>>) {
                    onResult(response.body())
                }

                override fun onFailure(call: Call<List<FeedbackTemplate>>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch templates: ${t.message}")
                    onResult(null)
                }
            })
    }


    fun updateFeedbackTemplate(
        token: String,
        category: String, // ✅ Use category instead of templateId
        formattedQuestions: List<Map<String, Any>>, // ✅ Ensure correct type
        onResult: (Boolean) -> Unit
    ) {
        val requestBody = mapOf(
            "questions" to formattedQuestions // ✅ Now a list of questions with choices
        )

        apiService.updateFeedbackTemplate("Bearer $token", category, requestBody) // ✅ Use category
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResult(response.isSuccessful)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onResult(false)
                }
            })
    }


    fun deleteFeedbackTemplate(token: String, category: String, onResult: (Boolean) -> Unit) { // ✅ Use category
        apiService.deleteFeedbackTemplate("Bearer $token", category) // ✅ Use category
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResult(response.isSuccessful)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onResult(false)
                }
            })
    }



}