package com.example.studez_feed.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import androidx.compose.ui.platform.LocalContext
import com.example.studez_feed.network.AdminFeedbackItem
import com.example.studez_feed.viewmodel.AdminFeedbackViewModel
import kotlinx.coroutines.launch

@Composable
fun FeedbackManagementScreen(navController: NavController?, adminToken: String) {
    val feedbackViewModel: AdminFeedbackViewModel = viewModel()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var feedbackList by remember { mutableStateOf<List<AdminFeedbackItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "pending", "approved", "rejected")

    // Fetch feedback when screen loads
    LaunchedEffect(Unit) {
        feedbackViewModel.feedbackListResult = { response ->
            isLoading = false
            if (response != null) {
                feedbackList = response
            } else {
                errorMessage = "Failed to fetch feedback."
            }
        }
        feedbackViewModel.getAllFeedback(adminToken)
    }

    // Handle feedback actions (approve, reject, delete)
    feedbackViewModel.feedbackActionResult = { success, message ->
        coroutineScope.launch {
            val toastMessage = when {
                message.contains("approved", ignoreCase = true) -> "Feedback Approved ✅"
                message.contains("rejected", ignoreCase = true) -> "Feedback Rejected ❌"
                message.contains("deleted", ignoreCase = true) -> "Feedback Deleted 🗑️"
                else -> message
            }
            snackbarHostState.showSnackbar(toastMessage)
        }
    }

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Feedback Management",
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Feedback") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.capitalize()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, fontSize = 16.sp)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        feedbackList
                            .filter { it.status == selectedFilter || selectedFilter == "All" }
                            .filter { it.feedback.contains(searchQuery, ignoreCase = true) }
                            .sortedByDescending { it._id }
                    ) { feedback ->
                        FeedbackManagementCard(
                            feedback,
                            feedbackViewModel,
                            adminToken,
                            context
                        )
                    }
                }
            }

            // Show Snackbar notifications
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
fun FeedbackManagementCard(
    feedback: AdminFeedbackItem,
    viewModel: AdminFeedbackViewModel,
    adminToken: String,
    context: android.content.Context
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = feedback.category, fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))

                // ✅ Styled Status with Colors
                val statusColor = when (feedback.status.lowercase()) {
                    "pending" -> Color(0xFFFFA500) // Orange for Pending
                    "approved" -> Color(0xFF4CAF50) // Green for Approved
                    "rejected" -> Color(0xFFFF5252) // Red for Rejected
                    else -> Color.Gray
                }

                Text(
                    text = feedback.status.capitalize(),
                    fontSize = 14.sp,
                    color = statusColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = feedback.feedback, fontSize = 16.sp)

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = {
                        viewModel.updateFeedbackStatus(adminToken, feedback._id, "Approved")
                    }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", tint = Color(0xFF4CAF50)) // Green
                    }
                    IconButton(onClick = {
                        viewModel.updateFeedbackStatus(adminToken, feedback._id, "Rejected")
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", tint = Color(0xFFFF5252)) // Red
                    }
                    IconButton(onClick = {
                        viewModel.deleteFeedback(adminToken, feedback._id)
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }
        }
    }
}