package com.example.studez_feed.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController?, userToken: String) {
    val profileViewModel: ProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(navController!!.context.applicationContext as Application)
    )

    var userName by remember { mutableStateOf("Student") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ✅ Fetch user profile when the screen loads
    LaunchedEffect(Unit) {
        profileViewModel.userProfileResult = { profile ->
            profile?.let { userName = it.name.split(" ").first() }
        }
        profileViewModel.getUserProfile(userToken)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(userName = userName)
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController?.navigate("studentProfile/$userToken")
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
                NavigationDrawerItem(
                    label = { Text("Notifications") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController?.navigate("userNotifications/$userToken")
                    },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.logoutResult = { success ->
                            if (success) {
                                navController?.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        authViewModel.logout()
                    },
                    icon = { Icon(Icons.Outlined.ExitToApp, contentDescription = "Logout") }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Home") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome, $userName!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    HomeScreenCard(
                        title = "Submit Feedback",
                        description = "Share your thoughts about your courses",
                        icon = Icons.Default.Feedback,
                        color = Color(0xFF64B5F6)
                    ) {
                        navController?.navigate("feedback/$userToken")
                    }
                    HomeScreenCard(
                        title = "View Feedback History",
                        description = "Check your past feedback submissions",
                        icon = Icons.Default.History,
                        color = Color(0xFF81C784)
                    ) {
                        navController?.navigate("feedbackHistory/$userToken")
                    }
                    HomeScreenCard(
                        title = "Settings",
                        description = "Edit your profile and preferences",
                        icon = Icons.Default.Settings,
                        color = Color(0xFFFFB74D)
                    ) {
                        navController?.navigate("studentProfile/$userToken")
                    }
                }
            }
        }
    )
}

@Composable
fun DrawerHeader(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Hello, $userName!", fontWeight = FontWeight.Bold)
    }
    Divider()
}

@Composable
fun HomeScreenCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(12.dp).size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
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