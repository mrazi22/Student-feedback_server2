package com.example.studez_feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.studez_feed.navigation.Screen
import com.example.studez_feed.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    var userProfileResult: ((Screen.UserProfile?) -> Unit)? = null

    private val _updateProfileResult = mutableStateOf<Boolean?>(null)
    val updateProfileResult: State<Boolean?> get() = _updateProfileResult

    fun getUserProfile(token: String) {
        viewModelScope.launch {
            ProfileRepository.getUserProfile(token) { profile ->
                userProfileResult?.invoke(profile)
            }
        }
    }

    fun updateUserProfile(token: String, updatedProfile: Screen.UserProfile) {
        viewModelScope.launch {
            ProfileRepository.updateUserProfile(token, updatedProfile) { success ->
                _updateProfileResult.value = success
            }
        }
    }
}