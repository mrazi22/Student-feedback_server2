package com.example.studez_feed.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject

class SharedPrefsHelper(context: Context) {

    companion object {
        private const val PREF_NAME = "student_feedback_prefs"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_OFFLINE_FEEDBACK = "offline_feedback"

        // ✅ **New Keys for Drafts**
        private const val KEY_DRAFT_PREFIX = "draft_feedback_"

        @Volatile
        private var INSTANCE: SharedPrefsHelper? = null

        fun getInstance(context: Context): SharedPrefsHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefsHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    // ✅ **Save User Token & Role**
    fun saveUserSession(token: String, isAdmin: Boolean) {
        println("💾 [SharedPrefsHelper] Saving session: Token=$token, isAdmin=$isAdmin")
        editor.putString(KEY_USER_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putBoolean(KEY_IS_ADMIN, isAdmin)
        editor.apply()
    }

    // ✅ **Get User Token**
    fun getUserToken(): String? {
        val token = prefs.getString(KEY_USER_TOKEN, null)
        println("🔑 [SharedPrefsHelper] Retrieved token: $token")

        if (token != null && isTokenExpired(token)) {
            println("⚠️ [SharedPrefsHelper] Token expired! Clearing session.")
            clearAll()
            return null
        }
        return token
    }

    // ✅ **Check if Logged In**
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getUserToken() != null
    }

    // ✅ **Check if Admin**
    fun isAdmin(): Boolean {
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    // ✅ **Save Offline Feedback (List of JSON Objects)**
    fun saveOfflineFeedback(feedbackList: List<Map<String, Any>>) {
        val jsonArray = JSONArray()
        feedbackList.forEach { feedback ->
            val jsonObject = JSONObject(feedback)
            jsonArray.put(jsonObject)
        }
        editor.putString(KEY_OFFLINE_FEEDBACK, jsonArray.toString()).apply()
        println("💾 [SharedPrefsHelper] Saved offline feedback: $jsonArray")
    }

    // ✅ **Retrieve Offline Feedback**
    fun getOfflineFeedback(): List<Map<String, Any>> {
        val feedbackJson = prefs.getString(KEY_OFFLINE_FEEDBACK, "[]") ?: "[]"
        val feedbackList = mutableListOf<Map<String, Any>>()

        val jsonArray = JSONArray(feedbackJson)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val feedbackMap = mutableMapOf<String, Any>()
            jsonObject.keys().forEach { key -> feedbackMap[key] = jsonObject.get(key) }
            feedbackList.add(feedbackMap)
        }

        println("📄 [SharedPrefsHelper] Retrieved offline feedback: $feedbackList")
        return feedbackList
    }

    // ✅ **Clear Offline Feedback**
    fun clearOfflineFeedback() {
        editor.remove(KEY_OFFLINE_FEEDBACK).apply()
        println("🗑 [SharedPrefsHelper] Cleared offline feedback.")
    }

    // ✅ **Save Draft for a Specific Category**
    fun saveDraft(category: String, feedbackData: Map<String, String>) {
        val draftString = feedbackData.entries.joinToString(";") { "${it.key}:${it.value}" }
        editor.putString("$KEY_DRAFT_PREFIX$category", draftString)
        editor.apply()
        println("💾 [SharedPrefsHelper] Saved draft for category [$category]: $draftString")
    }

    // ✅ **Load Draft for a Specific Category**
    fun loadDraft(category: String): Map<String, String> {
        val draftString = prefs.getString("$KEY_DRAFT_PREFIX$category", null) ?: return emptyMap()
        val draftMap = draftString.split(";").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
        println("📄 [SharedPrefsHelper] Loaded draft for category [$category]: $draftMap")
        return draftMap
    }

    // ✅ **Clear Draft for a Specific Category**
    fun clearDraft(category: String) {
        editor.remove("$KEY_DRAFT_PREFIX$category").apply()
        println("🗑 [SharedPrefsHelper] Cleared draft for category [$category].")
    }

    // ✅ **Logout: Clear All Data**
    fun clearAll() {
        println("🗑 [SharedPrefsHelper] Clearing user session...")
        editor.clear().apply()

        val checkToken = getUserToken()
        if (checkToken == null) {
            println("✅ [SharedPrefsHelper] Session cleared successfully!")
        } else {
            println("❌ [SharedPrefsHelper] Failed to clear session!")
        }
    }

    // ✅ **Check if Token is Expired**
    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true // Invalid token

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)
            val exp = jsonObject.getLong("exp") // Get expiry time
            val currentTime = System.currentTimeMillis() / 1000

            println("⏳ [SharedPrefsHelper] Token Expiry: $exp | Current Time: $currentTime")
            exp < currentTime
        } catch (e: Exception) {
            println("❌ [SharedPrefsHelper] Token decoding failed: ${e.message}")
            true // Assume expired if error occurs
        }
    }
}