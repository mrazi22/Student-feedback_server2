package com.example.studez_feed.repository

import com.example.studez_feed.network.ApiClient
import com.example.studez_feed.network.ApiService
import com.example.studez_feed.network.UserProfile

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    // ✅ Fetch User Profile
    fun getUserProfile(token: String, onResult: (UserProfile?) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.getUserProfile(authHeader).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                onResult(null)
            }
        })
    }

    // ✅ Update User Profile (Includes Password Change)
    fun updateUserProfile(token: String, profile: UserProfile, onResult: (Boolean) -> Unit) {
        val authHeader = "Bearer $token"
        apiService.updateUserProfile(authHeader, profile).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.isSuccessful)
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                onResult(false)
            }
        })
    }
}