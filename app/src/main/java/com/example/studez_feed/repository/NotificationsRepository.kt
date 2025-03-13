package com.example.studez_feed.repository

import android.util.Log
import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.NotificationItem
import com.example.studez_feed.network.NotificationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationsRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    // ✅ Send Notification (Admin Only)
    suspend fun sendNotification(token: String, title: String, message: String, recipient: String? = null): Boolean {
        return try {
            val request = NotificationRequest(title, message, recipient)
            val response = withContext(Dispatchers.IO) {
                apiService.sendNotification("Bearer $token", request)
            }
            Log.d("API_SUCCESS", "Notification sent: ${response.message}")
            true
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to send notification: ${e.message}")
            false
        }
    }

    // ✅ Fetch All Notifications (Admin Only)
    suspend fun getAllNotifications(token: String): List<NotificationItem> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getAllNotifications("Bearer $token")
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to fetch all notifications: ${e.message}")
            emptyList()
        }
    }

    // ✅ Fetch User Notifications
    suspend fun getUserNotifications(token: String): List<NotificationItem> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getUserNotifications("Bearer $token")
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to fetch user notifications: ${e.message}")
            emptyList()
        }
    }
}