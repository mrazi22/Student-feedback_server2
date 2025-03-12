package com.example.studez_feed.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.viewmodel.AuthViewModel

@Composable
fun AdminDashboardScreen(navController: NavController?, adminToken: String) {
//    val analyticsViewModel: AnalyticsViewModel = viewModel()
//    val analyticsData by analyticsViewModel.analyticsData.collectAsState()

    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(navController!!.context.applicationContext as Application)
    )

    // ✅ Ensure data is fetched on screen load
//    LaunchedEffect(Unit) {
//        analyticsViewModel.fetchAnalytics(adminToken)
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // ✅ Enables scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Dashboard Title
        Text(
            text = "Admin Dashboard",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Show Loading Indicator when Data is Not Yet Available
//        if (analyticsData.totalFeedback == 0 && analyticsData.totalUsers == 0) {
//            CircularProgressIndicator()
//        } else {
//            AdminStatsCard(title = "Total Feedback", count = analyticsData.totalFeedback, color = Color(0xFF4CAF50))
//            AdminStatsCard(title = "Pending Reviews", count = analyticsData.pendingFeedback, color = Color(0xFFFF9800))
//        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Admin Dashboard Cards
        AdminDashboardCard(
            title = "Manage Feedback",
            icon = Icons.Default.Feedback,
            description = "View and manage feedback submissions",
            onClick = { navController?.navigate("feedbackManagement/$adminToken") }
        )

        AdminDashboardCard(
            title = "Create Feedback Questions",
            icon = Icons.Default.AddCircle,
            description = "Set and manage feedback questions per category",
            onClick = { navController?.navigate("createFeedbackQuestions/$adminToken") }
        )

        AdminDashboardCard(
            title = "Manage Feedback Questions",
            icon = Icons.Default.List,
            description = "View, update, or delete existing feedback questions",
            onClick = { navController?.navigate("manageFeedbackQuestions/$adminToken") }
        )

        AdminDashboardCard(
            title = "Analytics",
            icon = Icons.Default.Analytics,
            description = "View reports and insights",
            onClick = { navController?.navigate("analytics/$adminToken") }
        )

        AdminDashboardCard(
            title = "Notifications",
            icon = Icons.Default.Notifications,
            description = "Send and manage notifications",
            onClick = { navController?.navigate("notifications/$adminToken") }
        )

        AdminDashboardCard(
            title = "Manage Users",
            icon = Icons.Default.Person,
            description = "View and manage users",
            onClick = { navController?.navigate("manageUsers/$adminToken") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Styled Logout Button
        Button(
            onClick = {
                authViewModel.logoutResult = { success ->
                    if (success) {
                        navController?.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true } // ✅ Clear backstack
                        }
                    }
                }
                authViewModel.logout() // ✅ Call logout function
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp, // 🚪 Updated Logout Icon
                contentDescription = "Logout",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ✅ Stats Card Component
@Composable
fun AdminStatsCard(title: String, count: Int, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ✅ Dashboard Card Component
@Composable
fun AdminDashboardCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

