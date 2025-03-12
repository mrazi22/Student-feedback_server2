package com.example.studez_feed.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studez_feed.screens.AdminDashboardScreen
import com.example.studez_feed.screens.FeedbackScreen
import com.example.studez_feed.screens.HomeScreen
import com.example.studez_feed.screens.LoginScreen
import com.example.studez_feed.screens.SignUpScreen


sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Home : Screen("home")
    data object Feedback : Screen("feedback")
    data object FeedbackHistory : Screen("feedback_history")
    data object AdminDashboard : Screen("admin_dashboard")
    data object FeedbackManagement : Screen("feedback_management")
    data object Notifications : Screen("notifications")
    data object Analytics : Screen("analytics")
    data object UserProfile : Screen("user_profile") {
        val name: Any = ""
    }

}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }

        composable("home/{userToken}") { backStackEntry ->
            val userToken = backStackEntry.arguments?.getString("userToken") ?: ""
            HomeScreen(navController = navController, userToken = userToken) // ✅ Pass token to HomeScreen


        }
        composable("admin_dashboard/{adminToken}") { backStackEntry ->
            val adminToken = backStackEntry.arguments?.getString("adminToken") ?: ""
            AdminDashboardScreen(navController = navController, adminToken = adminToken) // ✅ Ensure adminToken is passed
        }


        composable("feedback/{userToken}") { backStackEntry ->
            val userToken = backStackEntry.arguments?.getString("userToken") ?: ""
            FeedbackScreen(navController = navController, userToken = userToken) // ✅ Pass userToken
        }
    }
}