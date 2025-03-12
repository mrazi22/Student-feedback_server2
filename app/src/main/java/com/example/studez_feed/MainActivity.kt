package com.example.studez_feed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.rememberNavController
import com.example.studez_feed.navigation.NavGraph
import com.example.studez_feed.ui.theme.STUDEZFEEDTheme
import com.example.studez_feed.utils.SharedPrefsHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STUDEZFEEDTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val sharedPrefsHelper = remember { SharedPrefsHelper.getInstance(this) }
                    var userToken by remember { mutableStateOf<String?>(null) }
                    var isAdmin by remember { mutableStateOf(false) }

                    // ✅ Auto-login logic
                    LaunchedEffect(Unit) {
                        userToken = sharedPrefsHelper.getUserToken()
                        isAdmin = sharedPrefsHelper.isAdmin()

                        println("🚀 [MainActivity] Auto-login check triggered! Token: $userToken, isAdmin: $isAdmin")

                        when {
                            userToken == null -> {
                                println("🔐 [MainActivity] No valid session found! Redirecting to login.")
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            sharedPrefsHelper.getUserToken() == null -> {
                                println("⚠️ [MainActivity] Token expired! Redirecting to login.")
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            isAdmin -> {
                                println("👑 [MainActivity] Admin detected! Navigating to Admin Dashboard.")
                                navController.navigate("admin_dashboard/$userToken") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            else -> {
                                println("🏠 [MainActivity] User detected! Navigating to Home.")
                                navController.navigate("home/$userToken") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }

                    NavGraph(navController)
                }
            }

        }
    }
}



