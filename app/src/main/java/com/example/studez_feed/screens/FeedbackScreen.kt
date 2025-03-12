package com.example.studez_feed.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.studez_feed.network.FeedbackTemplate
import com.example.studez_feed.utils.SharedPrefsHelper
import com.example.studez_feed.viewmodel.FeedbackViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController?, userToken: String) {
    val feedbackViewModel: FeedbackViewModel = viewModel()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sharedPrefsHelper = remember { SharedPrefsHelper.getInstance(context) }

    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submissionMessage by remember { mutableStateOf<String?>(null) }
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }
    var feedbackTemplate by remember { mutableStateOf<FeedbackTemplate?>(null) }

    // ✅ Load draft when category is selected
    LaunchedEffect(category) {
        if (category.isNotEmpty()) {
            feedbackViewModel.getFeedbackQuestions(userToken, category)
            feedbackViewModel.feedbackQuestionsResult = { response ->
                feedbackTemplate = response
            }

            val draft = sharedPrefsHelper.loadDraft(category)
            selectedAnswers.clear()
            selectedAnswers.putAll(draft)
        }
    }

    // ✅ Refresh Screen 2 Seconds After Submission
    LaunchedEffect(submissionMessage) {
        if (submissionMessage?.contains("✅") == true) {
            kotlinx.coroutines.delay(2000)
            submissionMessage = null
            category = ""
            selectedAnswers.clear()
            feedbackTemplate = null
            sharedPrefsHelper.clearDraft(category) // ✅ Clear draft after submission
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Submit Your Feedback",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Category Dropdown
                Text(text = "Category", fontWeight = FontWeight.Medium)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Select Category") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Course", "Facility", "Teacher").forEach { categoryOption ->
                            DropdownMenuItem(
                                text = { Text(categoryOption) },
                                onClick = {
                                    category = categoryOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Multiple Choice Questions (Fetched from Backend)
                feedbackTemplate?.questions?.forEach { questionItem ->
                    Text(text = questionItem.question, fontWeight = FontWeight.Medium)
                    Column {
                        questionItem.choices.forEach { choice ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAnswers[questionItem.question] == choice,
                                    onClick = { selectedAnswers[questionItem.question] = choice }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = choice)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons with Better Styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (category.isNotBlank()) {
                        sharedPrefsHelper.saveDraft(category, selectedAnswers)
                        submissionMessage = "✅ Draft Saved!"
                    } else {
                        submissionMessage = "❌ Please select a category first!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Draft", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ✅ Submission Button
            Button(
                onClick = {
                    if (category.isBlank()) {
                        submissionMessage = "❌ Please select a category!"
                        return@Button
                    }

                    if (selectedAnswers.isEmpty()) {
                        submissionMessage = "❌ Please answer all questions!"
                        return@Button
                    }

                    isSubmitting = true
                    submissionMessage = null

                    // ✅ Only keep answers for the selected category
                    val categoryQuestions = feedbackTemplate?.questions?.map { it.question } ?: emptyList()
                    val filteredAnswers = selectedAnswers.filterKeys { it in categoryQuestions }

                    if (filteredAnswers.isEmpty()) {
                        submissionMessage = "❌ Please answer questions for the selected category!"
                        isSubmitting = false
                        return@Button
                    }

                    val feedbackText = filteredAnswers.entries.joinToString("; ") { "${it.key}: ${it.value}" }

                    // ✅ Debug Log for API Call
                    Log.d("FEEDBACK_SUBMIT", "Submitting feedback -> Category: $category, Feedback: $feedbackText")

                    feedbackViewModel.submitFeedback(userToken, category, feedbackText)

                    feedbackViewModel.feedbackResult = { response ->
                        isSubmitting = false
                        submissionMessage = if (response != null) {
                            "✅ Feedback submitted successfully!"
                        } else {
                            "❌ Submission failed. Try again."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ Success/Failure Message (Updated UI)
        submissionMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.contains("✅")) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}