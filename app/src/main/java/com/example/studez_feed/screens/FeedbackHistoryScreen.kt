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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Feedback History",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                errorMessage != null -> {
                    Text(
                        errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                feedbackList.isEmpty() -> {
                    Text(
                        text = "No feedback history available.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(feedbackList) { feedback ->
                            FeedbackHistoryCard(feedback)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackHistoryCard(feedback: FeedbackItem) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (feedback.status.lowercase()) {
        "pending" -> Color(0xFFFFC107) // Amber
        "approved" -> Color(0xFF4CAF50) // Green
        "rejected" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feedback.category.capitalize(Locale.current),
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
                Text(
                    text = feedback.status.capitalize(Locale.current),
                    fontSize = 16.sp,
                    color = statusColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }

            Text(
                text = feedback.feedback,
                fontSize = 16.sp,
                color = Color.Gray,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${feedback.status.capitalize(Locale.current)}",
                    fontSize = 14.sp,
                    color = statusColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
    }
}