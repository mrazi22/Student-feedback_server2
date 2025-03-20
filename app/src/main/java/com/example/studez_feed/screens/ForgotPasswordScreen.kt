package com.example.studez_feed.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.viewmodel.AuthViewModel
import com.example.studez_feed.viewmodel.AuthViewModelFactory


@Composable
fun ForgotPasswordScreen(navController: NavController?) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Title
        Text(
            text = "Forgot Password?",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your registered email to receive reset instructions.",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Error & Success Messages
        errorMessage?.let {
            Text(it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        successMessage?.let {
            Text(it, color = Color(0xFF4CAF50), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ✅ Send Reset Email Button
        Button(
            onClick = {
                isLoading = true
                authViewModel.forgotPassword(email) // Calls forgot password function
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Send Reset Email", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Back to Login
        TextButton(onClick = { navController?.navigate("login") }) {
            Text("Back to Login", fontSize = 14.sp, color = Color.Blue)
        }
    }

    // ✅ Handle Forgot Password Result from ViewModel
    LaunchedEffect(authViewModel.forgotPasswordResult.value) {
        authViewModel.forgotPasswordResult.value?.let { (success, message) ->
            isLoading = false
            if (success) {
                successMessage = message
                errorMessage = null
            } else {
                errorMessage = message
                successMessage = null
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(navController = null)
}