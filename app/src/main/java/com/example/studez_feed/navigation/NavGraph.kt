package com.example.studez_feed.navigation

import SignUpScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studez_feed.screens.AdminDashboardScreen
import com.example.studez_feed.screens.CreateFeedbackQuestionsScreen
import com.example.studez_feed.screens.FeedbackHistoryScreen
import com.example.studez_feed.screens.FeedbackManagementScreen
import com.example.studez_feed.screens.FeedbackScreen
import com.example.studez_feed.screens.ForgotPasswordScreen
import com.example.studez_feed.screens.HomeScreen
import com.example.studez_feed.screens.LoginScreen
import com.example.studez_feed.screens.ManageFeedbackQuestionsScreen
import com.example.studez_feed.screens.ManageUsersScreen
import com.example.studez_feed.screens.ResetPasswordScreen
import com.example.studez_feed.screens.StudentProfileScreen


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
    // ✅ Add Forgot Password Screen Route
    data object ForgotPassword : Screen("forgot_password")

    // ✅ Add Reset Password Screen Route (With Token Parameter)
    data object ResetPassword : Screen("reset_password/{resetToken}")

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
        composable("feedbackHistory/{userToken}") { backStackEntry ->
            val userToken = backStackEntry.arguments?.getString("userToken") ?: ""
            FeedbackHistoryScreen(navController = navController, userToken = userToken) // ✅ Pass token to FeedbackHistoryScreen
        }
        composable("feedbackManagement/{adminToken}") { backStackEntry ->
            val adminToken = backStackEntry.arguments?.getString("adminToken") ?: ""
            FeedbackManagementScreen(navController = navController, adminToken = adminToken) // ✅ Pass token to FeedbackManagementScreen
        }
        composable("createFeedbackQuestions/{adminToken}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("adminToken") ?: ""
            CreateFeedbackQuestionsScreen(navController, token)
        }

        composable("manageFeedbackQuestions/{adminToken}") { backStackEntry ->
            val adminToken = backStackEntry.arguments?.getString("adminToken") ?: ""
            ManageFeedbackQuestionsScreen(navController, adminToken)
        }
        composable("studentProfile/{userToken}") { backStackEntry ->
            val userToken = backStackEntry.arguments?.getString("userToken") ?: ""
            StudentProfileScreen(navController, userToken)
        }
        composable("manageUsers/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ManageUsersScreen(navController, token)
        }
        // ✅ Forgot Password Screen
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        // ✅ Reset Password Screen with Token
        composable("reset_password/{resetToken}") { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            ResetPasswordScreen(navController, resetToken)
        }



    }
}