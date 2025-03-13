package com.example.studez_feed.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.network.FeedbackItem
import com.example.studez_feed.viewmodel.FeedbackHistoryViewModel


@Composable
fun FeedbackHistoryScreen(navController: NavController?, userToken: String) {
    val feedbackHistoryViewModel: FeedbackHistoryViewModel = viewModel()
    var feedbackList by remember { mutableStateOf<List<FeedbackItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch feedback history when screen loads
    LaunchedEffect(Unit) {
        feedbackHistoryViewModel.feedbackHistoryResult = { response ->
            isLoading = false
            if (response != null) {
                feedbackList = response
            } else {
                errorMessage = "Failed to fetch feedback history."
            }
        }
        feedbackHistoryViewModel.fetchFeedbackHistory(userToken)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Title
        Text(
            text = "Feedback History",
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(errorMessage!!, color = Color.Red, fontSize = 16.sp)
            }
            feedbackList.isEmpty() -> {
                Text(
                    text = "No feedback history available.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(feedbackList) { feedback ->
                        FeedbackHistoryCard(feedback)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackHistoryCard(feedback: FeedbackItem) {
    var expanded by remember { mutableStateOf(false) }

    // ✅ Define status colors
    val statusColor = when (feedback.status.lowercase()) {
        "pending" -> Color(0xFFFFA500) // Orange
        "approved" -> Color(0xFF4CAF50) // Green
        "rejected" -> Color(0xFFFF5252) // Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = feedback.category,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                // ✅ Styled Status Text
                Text(
                    text = feedback.status.capitalize(),
                    fontSize = 14.sp,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = feedback.feedback, fontSize = 16.sp)

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${feedback.status.capitalize()}",
                    fontSize = 14.sp,
                    color = statusColor
                )
            }
        }
    }
}