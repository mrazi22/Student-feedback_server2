package com.example.studez_feed.screens


import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.R
import com.example.studez_feed.navigation.Screen
import com.example.studez_feed.viewmodel.AuthViewModel
import com.example.studez_feed.viewmodel.AuthViewModelFactory

@Composable
fun LoginScreen(navController: NavController?) {
    // ✅ Create ViewModel with Application Context
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // ✅ Toggle Password Visibility
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // ✅ Handle Login Result
    authViewModel.loginResult = { response ->
        isLoading = false
        if (response != null) {
            val token = response.token
            if (response.isAdmin) {
                navController?.navigate("admin_dashboard/$token") {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            } else {
                navController?.navigate("home/$token") {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        } else {
            errorMessage = "Invalid email or password"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // ✅ App Logo
//        Image(
//            painter = painterResource(id = R.drawable.new_logo),
//            contentDescription = "App Logo",
//            modifier = Modifier.size(160.dp)
//        )

        Spacer(modifier = Modifier.height(180.dp))

        // ✅ Title
        Text(
            text = "Welcome to Feedback!",
            fontSize = 24.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ Password Input with Toggle Icon
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Forgot Password Link
//        Text(
//            text = "Forgot Password? Click Here",
//            fontSize = 14.sp,
//            color = Color.Blue,
//            modifier = Modifier.clickable { navController?.navigate(Screen.ForgotPassword.route) }
//        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Error Message
        errorMessage?.let {
            Text(it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ✅ Login Button with Loading Indicator
        Button(
            onClick = {
                isLoading = true
                authViewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading, // Disable button when loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Make the button red
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Blue, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Login", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Sign-up Navigation
        TextButton(onClick = { navController?.navigate(Screen.SignUp.route) }) {
            Text("Don't have an account? Sign up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = null)
}