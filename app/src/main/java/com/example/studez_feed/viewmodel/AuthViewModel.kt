package com.example.studez_feed.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.example.studez_feed.network.AuthResponse
import com.example.studez_feed.repository.AuthRepository
import com.example.studez_feed.utils.SharedPrefsHelper

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefsHelper = SharedPrefsHelper.getInstance(application.applicationContext)

    // ✅ Forgot Password State
    private val _forgotPasswordResult = mutableStateOf<Pair<Boolean, String>?>(null)
    val forgotPasswordResult: State<Pair<Boolean, String>?> get() = _forgotPasswordResult

    // ✅ Reset Password State
    private val _resetPasswordResult = mutableStateOf<Pair<Boolean, String>?>(null)
    val resetPasswordResult: State<Pair<Boolean, String>?> get() = _resetPasswordResult

    var loginResult: ((AuthResponse?) -> Unit)? = null
    var signupResult: ((AuthResponse?) -> Unit)? = null
    var logoutResult: ((Boolean) -> Unit)? = null

    // ✅ Check if User is Logged In
    fun isUserLoggedIn(): Boolean {
        return sharedPrefsHelper.getUserToken() != null
    }

    // ✅ Get Admin Status
    fun isAdmin(): Boolean {
        return sharedPrefsHelper.isAdmin()
    }

    // ✅ Login and Save Token + Role
    fun login(email: String, password: String) {
        viewModelScope.launch {
            println("🚀 [AuthViewModel] Login called with Email: $email")

            AuthRepository.login(email, password) { response ->
                response?.let {
                    println("✅ [AuthViewModel] Login successful, saving session.")
                    sharedPrefsHelper.saveUserSession(it.token, it.isAdmin)
                    loginResult?.invoke(it)
                } ?: run {
                    println("❌ [AuthViewModel] Login failed.")
                    loginResult?.invoke(null)
                }
            }
        }
    }

    // ✅ Signup
    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            AuthRepository.register(name,email,password) { response, errorMessage ->
                if (response != null) {
                    signupResult?.invoke(response)
                } else {
                    println("❌ [AuthViewModel] Signup failed: $errorMessage")
                    signupResult?.invoke(null)
                }
            }
        }
    }

    // ✅ Logout and Clear Session
    fun logout() {
        viewModelScope.launch {
            println("🚀 [AuthViewModel] Logout started!")

            val token = sharedPrefsHelper.getUserToken()
            println("🔑 [AuthViewModel] Retrieved token: $token")

            if (token != null) {
                val success = AuthRepository.logout(token)
                println("✅ [AuthViewModel] Logout API Response: $success")

                if (success) {
                    sharedPrefsHelper.clearAll()
                    println("🗑 [AuthViewModel] User session cleared!")
                    logoutResult?.invoke(true)
                } else {
                    println("❌ [AuthViewModel] Logout API call failed!")
                    logoutResult?.invoke(false)
                }
            } else {
                println("❌ [AuthViewModel] No token found!")
                logoutResult?.invoke(false)
            }
        }
    }

}