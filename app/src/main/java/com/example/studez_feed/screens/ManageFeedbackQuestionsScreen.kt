package com.example.studez_feed.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.network.FeedbackTemplate
import com.example.studez_feed.viewmodel.AdminFeedbackViewModel


@Composable
fun ManageFeedbackQuestionsScreen(navController: NavController?, adminToken: String) {
    val feedbackViewModel: AdminFeedbackViewModel = viewModel()
    val context = LocalContext.current
    var feedbackTemplates by remember { mutableStateOf<List<FeedbackTemplate>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Fetch feedback templates on load
    LaunchedEffect(Unit) {
        feedbackViewModel.getAllFeedbackTemplates(adminToken)
    }

    feedbackViewModel.feedbackTemplatesResult = { response ->
        isLoading = false
        if (response != null) {
            feedbackTemplates = response
        } else {
            Toast.makeText(context, "Failed to load feedback templates!", Toast.LENGTH_SHORT).show()
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
                "Manage Feedback Questions",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(feedbackTemplates) { template ->
                        FeedbackTemplateCard(template, feedbackViewModel, adminToken, onUpdate = {
                            feedbackViewModel.getAllFeedbackTemplates(adminToken) // ✅ Refresh UI
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackTemplateCard(
    template: FeedbackTemplate,
    viewModel: AdminFeedbackViewModel,
    adminToken: String,
    onUpdate: () -> Unit // ✅ Callback for refreshing UI
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = template.category, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            template.questions.forEach { questionItem ->
                Text(text = "Q: ${questionItem.question}", fontSize = 16.sp)
                questionItem.choices.forEach { choice ->
                    Text(text = "- $choice", fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                }
                IconButton(onClick = {
                    viewModel.deleteFeedbackTemplate(adminToken, template.category) { success ->
                        if (success) {
                            Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                            onUpdate() // ✅ Refresh UI after delete
                        } else {
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }

            if (expanded) {
                EditFeedbackQuestionSection(template, viewModel, adminToken, onUpdate)
            }
        }
    }
}

@Composable
fun EditFeedbackQuestionSection(
    template: FeedbackTemplate,
    viewModel: AdminFeedbackViewModel,
    adminToken: String,
    onUpdate: () -> Unit // ✅ Callback for refreshing UI
) {
    val context = LocalContext.current
    var updatedQuestions by remember { mutableStateOf(template.questions.map { it.question }) }
    var updatedChoices by remember { mutableStateOf(template.questions.map { it.choices.toMutableList() }) }

    Column {
        template.questions.forEachIndexed { index, _ ->
            OutlinedTextField(
                value = updatedQuestions[index],
                onValueChange = { updatedQuestions = updatedQuestions.toMutableList().apply { set(index, it) } },
                label = { Text("Edit Question ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
            updatedChoices[index].forEachIndexed { choiceIndex, _ ->
                OutlinedTextField(
                    value = updatedChoices[index][choiceIndex],
                    onValueChange = { newValue ->
                        updatedChoices = updatedChoices.toMutableList().apply {
                            set(index, this[index].toMutableList().apply { set(choiceIndex, newValue) })
                        }
                    },
                    label = { Text("Edit Choice ${choiceIndex + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val formattedQuestions = updatedQuestions.mapIndexed { index, question ->
                    mapOf(
                        "question" to question,
                        "choices" to updatedChoices[index]
                    )
                }

                viewModel.updateFeedbackTemplate(adminToken, template.category, formattedQuestions) { success ->
                    if (success) {
                        Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show()
                        onUpdate() // ✅ Refresh UI after update
                    } else {
                        Toast.makeText(context, "Failed to update!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}