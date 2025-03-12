package com.example.studez_feed.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.studez_feed.viewmodel.ProfileViewModel


@Composable
fun HomeScreen(navController: NavController?, userToken: String) {
    val profileViewModel: ProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(navController!!.context.applicationContext as Application)
    )

    var userName by remember { mutableStateOf("Student") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome, $userName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = { navController.navigate("userNotifications/$userToken") }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "User Notifications",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HomeScreenCard("Submit Feedback", "Share your thoughts about your courses", Icons.Default.Feedback) {
            navController.navigate("feedback/$userToken")
        }
        HomeScreenCard("View Feedback History", "Check your past feedback submissions", Icons.Default.History) {
            navController.navigate("feedbackHistory/$userToken")
        }
        HomeScreenCard("Settings", "Edit your profile and preferences", Icons.Default.Settings) {
            navController.navigate("studentProfile/$userToken")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Logout Button
        Button(
            onClick = {
                authViewModel.logoutResult = { success ->
                    if (success) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
                authViewModel.logout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // 🔴 Deep Red for Logout
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = "Logout",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
fun HomeScreenCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // HomeScreen(navController = null, userToken = "sample_token") // ✅ Provide a mock token
}