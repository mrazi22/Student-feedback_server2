package com.example.studez_feed.repository

import android.util.Log
import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AdminUserRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun getAllUsers(token: String, onResult: (List<UserProfile>?) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.getAllUsers(authHeader)
            .enqueue(object : Callback<List<UserProfile>> {
                override fun onResponse(call: Call<List<UserProfile>>, response: Response<List<UserProfile>>) {
                    onResult(response.body()) // ✅ Expecting List<UserProfile>
                }

                override fun onFailure(call: Call<List<UserProfile>>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch users: ${t.message}")
                    onResult(null)
                }
            })
    }

    fun updateUser(token: String, userId: String, name: String, isAdmin: Boolean, onResult: (Boolean, UserProfile?) -> Unit) {
        val requestBody = mapOf("name" to name, "isAdmin" to isAdmin)
        apiService.updateUser(token, userId, requestBody) // ✅ Use userId instead of email
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    if (response.isSuccessful) {
                        onResult(true, response.body())
                    } else {
                        onResult(false, null)
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    onResult(false, null)
                }
            })
    }

    fun deleteUser(token: String, userId: String, onResult: (Boolean) -> Unit) {
        apiService.deleteUser(token, userId) // ✅ Use userId instead of email
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