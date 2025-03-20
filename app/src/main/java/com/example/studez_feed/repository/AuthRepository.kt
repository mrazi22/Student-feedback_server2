package com.example.studez_feed.repository

import android.util.Log
import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.AuthResponse
import com.example.studez_feed.network.ForgotPasswordRequest
import com.example.studez_feed.network.ForgotPasswordResponse
import com.example.studez_feed.network.LoginRequest
import com.example.studez_feed.network.RegisterRequest
import com.example.studez_feed.network.ResetPasswordRequest
import com.example.studez_feed.network.ResetPasswordResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object AuthRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun login(email: String, password: String, onResult: (AuthResponse?) -> Unit) {
        println("🚀 [AuthRepository] Sending Login Request: Email=$email")
        apiService.login(LoginRequest(email, password)).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                println("✅ [AuthRepository] Login Response: ${response.body()}")
                onResult(response.body())
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                println("❌ [AuthRepository] Login API Call Failed: ${t.message}")
                onResult(null)
            }
        })
    }

    fun register(name: String, email: String, password: String, onResult: (AuthResponse?, String?) -> Unit) {
        apiService.register(RegisterRequest(name, email, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body(), null) // ✅ Pass response body and null error
                    } else {
                        val errorMessage = "Signup failed: ${response.code()} - ${response.message()}"
                        println("❌ [AuthRepository] Signup API Call Failed (HTTP Error): $errorMessage")
                        onResult(null, errorMessage) // ✅ Pass null response and error message
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    val errorMessage = "Signup failed: ${t.message}"
                    println("❌ [AuthRepository] Signup API Call Failed (Exception): $errorMessage")
                    t.printStackTrace() // Print the full stack trace
                    onResult(null, errorMessage) // ✅ Pass null response and error message
                }
            })
    }

    // ✅ Logout API (Deletes session from backend)
    suspend fun logout(token: String): Boolean {
        return try {
            println("📡 [AuthRepository] Calling Logout API with Token: Bearer $token") // ✅ Log API call

            withContext(Dispatchers.IO) {
                val response = apiService.logout("Bearer $token") // ✅ Ensure "Bearer " prefix
                println("✅ [AuthRepository] Logout API Response: ${response.code()} - ${response.message()}") // ✅ Log API response

                response.isSuccessful
            }
        } catch (e: Exception) {
            println("❌ [AuthRepository] Logout Failed: ${e.message}") // ✅ Log error
            false
        }
    }
    // ✅ Request Forgot Password
    fun forgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        val request = ForgotPasswordRequest(email)
        apiService.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Password reset email sent!")
                } else {
                    onResult(false, "Failed to send reset email")
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Log.e("ForgotPassword", "❌ Error: ${t.message}")
                onResult(false, "Network error")
            }
        })
    }

    // ✅ Reset Password
    fun resetPassword(token: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        val request = ResetPasswordRequest(token, newPassword)
        apiService.resetPassword(request).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Password reset successful!")
                } else {
                    onResult(false, "Failed to reset password")
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Log.e("ResetPassword", "❌ Error: ${t.message}")
                onResult(false, "Network error")
            }
        })
    }
}