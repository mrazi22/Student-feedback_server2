package com.example.studez_feed.utils

import androidx.compose.ui.graphics.Color

data class PasswordStrength(val label: String, val color: Color)

fun getPasswordStrength(password: String): PasswordStrength {
    val weakPattern = "^(?=.*[a-zA-Z]).{1,4}\$".toRegex()
    val mediumPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,9}\$".toRegex()
    val strongPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+{}:;<>?]).{10}\$".toRegex()

    return when {
        strongPattern.matches(password) -> PasswordStrength("Strong 💪", Color(0xFF4CAF50)) // Green
        mediumPattern.matches(password) -> PasswordStrength("Medium ⚠", Color(0xFFFF9800)) // Orange
        weakPattern.matches(password) -> PasswordStrength("Weak ❌", Color(0xFFFF5252)) // Red
        else -> PasswordStrength("", Color.Transparent)
    }
}