package com.example.studez_feed.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studez_feed.network.FeedbackResponse
import com.example.studez_feed.network.FeedbackTemplate
import com.example.studez_feed.repository.FeedbackRepository
import com.example.studez_feed.utils.SharedPrefsHelper
import kotlinx.coroutines.launch
import org.json.JSONObject

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {
    var feedbackResult: ((FeedbackResponse?) -> Unit)? = null
    var feedbackQuestionsResult: ((FeedbackTemplate?) -> Unit)? = null

    private val sharedPrefsHelper = SharedPrefsHelper.getInstance(application.applicationContext)

    init {
        observeNetworkChanges() // ✅ Auto-sync when ViewModel is initialized
    }

    // ✅ **Submit Feedback (Handles Offline Storage)**
    fun submitFeedback(token: String, category: String, feedback: String) {
        viewModelScope.launch {
            if (isInternetAvailable()) {
                // ✅ Online: Send feedback directly
                FeedbackRepository.submitFeedback(token, category, feedback) { response ->
                    if (response != null) {
                        println("✅ [FeedbackViewModel] Feedback submitted successfully!")
                        feedbackResult?.invoke(response)
                    } else {
                        println("❌ [FeedbackViewModel] Feedback submission failed!")
                        feedbackResult?.invoke(FeedbackResponse(_id = "", category, feedback, "failed"))
                    }
                }
            } else {
                // ❌ Offline: Store feedback locally
                println("⚠️ [FeedbackViewModel] No internet! Storing feedback offline.")

                val offlineFeedback = sharedPrefsHelper.getOfflineFeedback().toMutableList()
                val feedbackData = mapOf(
                    "_id" to System.currentTimeMillis().toString(), // Unique local ID
                    "category" to category,
                    "feedback" to feedback,
                    "status" to "unsynced"
                )

                offlineFeedback.add(feedbackData)
                sharedPrefsHelper.saveOfflineFeedback(offlineFeedback)

                feedbackResult?.invoke(FeedbackResponse(_id = "", category, "Saved Offline", "success"))
            }
        }
    }

    fun getFeedbackQuestions(token: String, category: String) {
        viewModelScope.launch {
            FeedbackRepository.getFeedbackQuestions(token, category) { response ->
                feedbackQuestionsResult?.invoke(response) // ✅ Now correctly expects a single FeedbackTemplate
            }
        }
    }

    // ✅ **Sync Offline Feedback when Internet is Available**
    fun syncOfflineFeedback() {
        val token = sharedPrefsHelper.getUserToken()
        if (token == null) {
            println("❌ [FeedbackViewModel] No user token found, cannot sync.")
            return
        }

        val offlineFeedback = sharedPrefsHelper.getOfflineFeedback()
        if (offlineFeedback.isEmpty()) {
            println("✅ [FeedbackViewModel] No offline feedback to sync.")
            return
        }

        viewModelScope.launch {
            println("📡 [FeedbackViewModel] Sending ${offlineFeedback.size} unsynced feedback items to backend...")

            val formattedFeedbackList = offlineFeedback.map { feedbackMap ->
                feedbackMap.mapValues { it.value.toString() } // Convert all values to String
            }

            FeedbackRepository.syncOfflineFeedback(token, formattedFeedbackList) { success ->
                if (success) {
                    println("✅ [FeedbackViewModel] Offline feedback synced successfully!")
                    sharedPrefsHelper.clearOfflineFeedback() // ✅ Clear offline storage
                } else {
                    println("❌ [FeedbackViewModel] Failed to sync offline feedback.")
                }
            }
        }
    }

    // ✅ **Observe Network Changes**
    private fun observeNetworkChanges() {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                println("📶 [FeedbackViewModel] Internet is back! Attempting sync...")
                syncOfflineFeedback()
            }

            override fun onLost(network: Network) {
                println("❌ [FeedbackViewModel] Internet lost. Sync paused.")
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // ✅ **Check Internet Availability (Supports API 21+)**
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // ✅ Android 6.0+ (API 23+)
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            // ✅ Legacy support for Android 5.0+ (API 21+)
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

}