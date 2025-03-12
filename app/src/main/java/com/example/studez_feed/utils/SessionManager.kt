package com.example.studez_feed.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ADMIN = "is_admin" // ✅ New key for admin role
    }

    // ✅ Save Token + Role
    fun saveUserSession(token: String, isAdmin: Boolean) {
        println("💾 [SessionManager] Saving user session: Token=$token, isAdmin=$isAdmin") // 🔍 Log token save

        editor.putString(KEY_USER_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putBoolean(KEY_IS_ADMIN, isAdmin) // ✅ Store if user is an admin
        editor.apply()
    }

    // ✅ Get Token
    fun getUserToken(): String? {
        val token = sharedPreferences.getString(KEY_USER_TOKEN, null)
        println("🔑 [SessionManager] Retrieved token: $token") // 🔍 Log token retrieval

        // ✅ Check if Token is Expired
        if (token != null && isTokenExpired(token)) {
            println("⚠️ [SessionManager] Token has expired! Logging out user.")
            logoutUser()
            return null
        }
        return token
    }

    // ✅ Check if User is Logged In
    fun isLoggedIn(): Boolean {
        val loggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val token = getUserToken()
        return loggedIn && token != null
    }

    // ✅ Check if User is Admin
    fun isAdmin(): Boolean {
        val isAdmin = sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
        println("👑 [SessionManager] isAdmin status: $isAdmin") // 🔍 Log isAdmin check
        return isAdmin
    }

    // ✅ Logout (Clear Data)
    fun logoutUser() {
        println("🗑 [SessionManager] Clearing user session...") // 🔍 Log before clearing
        editor.clear().apply()

        val checkToken = getUserToken()
        if (checkToken == null) {
            println("✅ [SessionManager] User session successfully cleared!") // 🔍 Log success
        } else {
            println("❌ [SessionManager] Failed to clear session, token still exists!") // 🔍 Log failure
        }
    }

    // ✅ Check if JWT Token is Expired
    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true // Invalid token

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)
            val exp = jsonObject.getLong("exp") // Get expiry time from token
            val currentTime = System.currentTimeMillis() / 1000 // Convert to seconds

            println("⏳ [SessionManager] Token Expiry: $exp | Current Time: $currentTime") // Log expiry time
            exp < currentTime
        } catch (e: Exception) {
            println("❌ [SessionManager] Error decoding token: ${e.message}") // Log error
            true // Assume expired if there's an error
        }
    }
}