package com.example.studez_feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studez_feed.network.FeedbackItem
import com.example.studez_feed.repository.FeedbackRepository
import kotlinx.coroutines.launch

class FeedbackHistoryViewModel : ViewModel() {
    var feedbackHistoryResult: ((List<FeedbackItem>?) -> Unit)? = null

    fun fetchFeedbackHistory(token: String) {
        viewModelScope.launch {
            FeedbackRepository.getFeedbackHistory(token) { response ->
                feedbackHistoryResult?.invoke(response)
            }
        }
    }
}