package com.example.studez_feed.repository

import android.util.Log


import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.FeedbackItem
import com.example.studez_feed.network.FeedbackRequest
import com.example.studez_feed.network.FeedbackResponse
import com.example.studez_feed.network.FeedbackTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedbackRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    // ✅ Submit Feedback
    fun submitFeedback(token: String, category: String, feedback: String, onResult: (FeedbackResponse?) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.submitFeedback(authHeader, FeedbackRequest(category, feedback))
            .enqueue(object : Callback<FeedbackResponse> {
                override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                    onResult(response.body())
                }

                override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                    onResult(null)
                }
            })
    }

    // ✅ Fetch Feedback History
    fun getFeedbackHistory(token: String, onResult: (List<FeedbackItem>?) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.getFeedbackHistory(authHeader)
            .enqueue(object : Callback<List<FeedbackItem>> {
                override fun onResponse(call: Call<List<FeedbackItem>>, response: Response<List<FeedbackItem>>) {
                    onResult(response.body())
                }

                override fun onFailure(call: Call<List<FeedbackItem>>, t: Throwable) {
                    onResult(null)
                }
            })
    }

    // ✅ Fetch Feedback Questions by Category (NEW)
    fun getFeedbackQuestions(token: String, category: String, onResult: (FeedbackTemplate?) -> Unit) {
        val authHeader = "Bearer $token"

        apiService.getFeedbackQuestions(authHeader, category)
            .enqueue(object : Callback<FeedbackTemplate> {
                override fun onResponse(call: Call<FeedbackTemplate>, response: Response<FeedbackTemplate>) {
                    if (response.isSuccessful) {
                        onResult(response.body()) // ✅ Returns a single FeedbackTemplate
                    } else {
                        Log.e("API_ERROR", "Failed to fetch feedback questions: ${response.errorBody()?.string()}")
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<FeedbackTemplate>, t: Throwable) {
                    Log.e("API_ERROR", "Network request failed: ${t.message}")
                    onResult(null)
                }
            })
    }

    // ✅ Sync Offline Feedback with Backend
    suspend fun syncOfflineFeedback(token: String, feedbackList: List<Map<String, String>>, onResult: (Boolean) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                println("📡 [FeedbackRepository] Sending ${feedbackList.size} feedback items to backend...")
                val response = apiService.syncOfflineFeedback("Bearer $token", feedbackList)

                if (response.isSuccessful) {
                    println("✅ [FeedbackRepository] Feedback synced successfully!")
                    onResult(true)
                } else {
                    println("❌ [FeedbackRepository] Sync failed: ${response.errorBody()?.string()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                println("❌ [FeedbackRepository] Network Error: ${e.message}")
                onResult(false)
            }
        }
    }

}