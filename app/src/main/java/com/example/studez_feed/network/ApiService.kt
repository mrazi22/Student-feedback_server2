package com.example.studez_feed.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// ✅ Authentication Models

data class AuthResponse(val _id: String, val name: String, val email: String, val isAdmin: Boolean, val token: String)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String,)
// ✅ Request Password Reset Model
data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String)

// ✅ Reset Password Model
data class ResetPasswordRequest(val token: String, val newPassword: String)
data class ResetPasswordResponse(val message: String)

// ✅ Feedback Models
data class FeedbackRequest(val category: String, val feedback: String)
data class FeedbackResponse(val _id: String, val category: String, val feedback: String, val status: String)
data class FeedbackItem(val _id: String, val category: String, val feedback: String, val status: String)

// ✅ Admin Feedback Models
data class AdminFeedbackItem(
    val _id: String,
    val category: String,
    val feedback: String,
    val status: String
)

// ✅ Feedback Template Models (Multiple-Choice Support)
data class FeedbackTemplate(
    val _id: String,
    val category: String,
    val questions: List<QuestionItem> // ✅ List of questions with choices
)

data class QuestionItem(
    val question: String,
    val choices: List<String> // ✅ Store multiple-choice options
)

data class UserProfile(
    val _id: String,
    val name: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean
)

data class AnalyticsResponse(
    val totalFeedback: Int = 0,
    val totalUsers: Int = 0,
    val pendingFeedback: Int = 0,
    val approvedFeedback: Int = 0,
    val rejectedFeedback: Int = 0,
    @SerializedName("feedbackByCategory") val feedbackByCategory: List<CategoryData> = emptyList()
)

data class CategoryData(
    @SerializedName("_id") val category: String = "Unknown",
    val count: Int = 0
)

data class NotificationItem(
    val _id: String,
    val title: String,
    val message: String,
    val recipient: String?,
    val createdAt: String
)

data class NotificationRequest(
    val title: String,
    val message: String,
    val recipient: String?
)

data class NotificationResponse(val message: String)

interface ApiService {

    // ✅ Authentication
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>


    // ✅ Student Feedback
    @POST("feedback")
    fun submitFeedback(
        @Header("Authorization") token: String,
        @Body request: FeedbackRequest
    ): Call<FeedbackResponse>

    @GET("feedback/myfeedback")
    fun getFeedbackHistory(
        @Header("Authorization") token: String
    ): Call<List<FeedbackItem>>

    // ✅ Admin Feedback Management
    @GET("admin/feedback/all")
    fun getAllFeedbackForAdmin(
        @Header("Authorization") token: String
    ): Call<List<AdminFeedbackItem>>

    @PUT("admin/feedback/{id}")
    fun manageFeedback(
        @Header("Authorization") token: String,
        @Path("id") feedbackId: String,
        @Body status: Map<String, String>
    ): Call<AdminFeedbackItem>

    @DELETE("admin/feedback/{id}")
    fun deleteFeedback(
        @Header("Authorization") token: String,
        @Path("id") feedbackId: String
    ): Call<Void>

    // ✅ Feedback Template Management (Multiple-Choice Support)
    @POST("admin/feedback-template")
    fun createFeedbackTemplate(
        @Header("Authorization") token: String,
        @Body requestBody: @JvmSuppressWildcards Map<String, Any>
    ): Call<Void>

    @GET("admin/feedback-templates")
    fun getAllFeedbackTemplates(
        @Header("Authorization") token: String
    ): Call<List<FeedbackTemplate>>

    @PUT("admin/feedback-template/{category}")
    fun updateFeedbackTemplate(
        @Header("Authorization") token: String,
        @Path("category") category: String,
        @Body requestBody: @JvmSuppressWildcards Map<String, Any>
    ): Call<Void>

    @DELETE("admin/feedback-template/{category}")
    fun deleteFeedbackTemplate(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): Call<Void>

    @GET("admin/feedback-template/{category}")
    fun getFeedbackQuestions(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): Call<FeedbackTemplate>

    @GET("profile")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfile>

    @PUT("profile")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body updatedProfile: UserProfile
    ): Call<Void>

    // ✅ Get all users
    @GET("/api/admin/users")
    fun getAllUsers(@Header("Authorization") token: String): Call<List<UserProfile>>

    @PUT("/api/admin/users/{id}")
    fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body requestBody: @JvmSuppressWildcards Map<String, Any>
    ): Call<UserProfile>

    @DELETE("/api/admin/users/{id}")
    fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Call<Void>

    @GET("analytics")
    suspend fun getAnalytics(@Header("Authorization") token: String): AnalyticsResponse

    // ✅ Send Notification (Admin Only)
    @POST("notifications/")
    suspend fun sendNotification(
        @Header("Authorization") token: String,
        @Body request: NotificationRequest
    ): NotificationResponse

    // ✅ Fetch All Notifications (Admin Only)
    @GET("notifications/all")
    suspend fun getAllNotifications(
        @Header("Authorization") token: String
    ): List<NotificationItem>

    // ✅ Fetch User Notifications (For logged-in users)
    @GET("notifications/my")
    suspend fun getUserNotifications(
        @Header("Authorization") token: String
    ): List<NotificationItem>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @POST("sync")
    suspend fun syncOfflineFeedback(
        @Header("Authorization") token: String,
        @Body feedbackList: List<Map<String, String>>
    ): Response<Map<String, Any>>

    // ✅ Forgot Password - Request Reset Email
    @POST("auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // ✅ Reset Password - Use Token to Set New Password
    @POST("auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>
}