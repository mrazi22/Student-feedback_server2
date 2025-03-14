package com.example.studez_feed.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.viewmodel.AdminFeedbackViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFeedbackQuestionsScreen(navController: NavController?, adminToken: String) {
    val feedbackViewModel: AdminFeedbackViewModel = viewModel()
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("") }
    val categories = listOf("Course", "Facility", "Teacher")
    var expanded by remember { mutableStateOf(false) }

    val questionList = remember { mutableStateListOf<QuestionWithChoices>() }


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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create Feedback Questions",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    label = { Text("Select Category") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Questions with Multiple Choices
            questionList.forEachIndexed { index, questionWithChoices ->
                QuestionCard(
                    questionWithChoices,
                    onUpdateQuestion = { newText ->
                        questionList[index] = questionList[index].copy(question = newText)
                    },
                    onAddChoice = {
                        questionList[index] = questionList[index].copy(
                            choices = questionList[index].choices.toMutableList().apply { add("") }
                        )
                    },
                    onUpdateChoice = { choiceIndex, newChoice ->
                        questionList[index] = questionList[index].copy(
                            choices = questionList[index].choices.toMutableList()
                                .apply { set(choiceIndex, newChoice) }
                        )
                    },
                    onRemoveChoice = { choiceIndex ->
                        questionList[index] = questionList[index].copy(
                            choices = questionList[index].choices.toMutableList()
                                .apply { removeAt(choiceIndex) }
                        )
                    },
                    onRemoveQuestion = { questionList.removeAt(index) }
                )
            }

            // Add New Question Button
            TextButton(
                onClick = { questionList.add(QuestionWithChoices("", mutableListOf(""))) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add question")
                Text("Add Question")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (selectedCategory.isEmpty()) {
                        Toast.makeText(context, "Please select a category!", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (questionList.any { it.question.isBlank() || it.choices.any { choice -> choice.isBlank() } }) {
                        Toast.makeText(
                            context,
                            "All questions and choices must be filled!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // Convert to API request format
                    val formattedQuestions = questionList.map {
                        mapOf(
                            "question" to it.question,
                            "choices" to it.choices.filter { choice -> choice.isNotBlank() } // Remove empty choices
                        )
                    }

                    feedbackViewModel.createFeedbackTemplate(
                        adminToken,
                        selectedCategory,
                        formattedQuestions
                    ) { success ->
                        if (success) {
                            Toast.makeText(
                                context,
                                "Questions saved successfully ✅",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController?.navigateUp()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to save questions ❌",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Questions", fontSize = 18.sp)
            }
        }
    }
}

// Data class to hold questions and their choices
data class QuestionWithChoices(
    val question: String,
    val choices: List<String> // Immutable List
)

// Composable for Individual Question Cards
@Composable
fun QuestionCard(
    question: QuestionWithChoices,
    onUpdateQuestion: (String) -> Unit,
    onAddChoice: () -> Unit,
    onUpdateChoice: (Int, String) -> Unit,
    onRemoveChoice: (Int) -> Unit,
    onRemoveQuestion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = question.question,
                onValueChange = onUpdateQuestion,
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Choices for the question
            question.choices.forEachIndexed { index, choice ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = choice,
                        onValueChange = { newChoice -> onUpdateChoice(index, newChoice) },
                        label = { Text("Choice ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onRemoveChoice(index) }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Remove choice", tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Choice Button
            TextButton(onClick = onAddChoice) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add choice")
                Text("Add Choice")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Remove Question Button
            TextButton(onClick = onRemoveQuestion, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                Text("Remove Question")
            }
        }
    }
}