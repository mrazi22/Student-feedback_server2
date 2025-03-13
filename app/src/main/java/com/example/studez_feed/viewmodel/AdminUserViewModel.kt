package com.example.studez_feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studez_feed.network.UserProfile
import com.example.studez_feed.repository.AdminUserRepository
import kotlinx.coroutines.launch

class AdminUserViewModel : ViewModel() {

    fun getAllUsers(token: String, onResult: (List<UserProfile>?) -> Unit) {
        viewModelScope.launch {
            AdminUserRepository.getAllUsers(token, onResult)
        }
    }

    fun updateUser(token: String, userId: String, name: String, isAdmin: Boolean, onResult: (Boolean, UserProfile?) -> Unit) {
        viewModelScope.launch {
            AdminUserRepository.updateUser(token, userId, name, isAdmin, onResult) // ✅ Pass userId
        }
    }

    fun deleteUser(token: String, userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            AdminUserRepository.deleteUser(token, userId, onResult) // ✅ Pass userId
        }
    }
}