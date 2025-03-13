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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studez_feed.network.UserProfile
import com.example.studez_feed.viewmodel.AdminUserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController?, adminToken: String) {
    val userViewModel: AdminUserViewModel = viewModel()
    val context = LocalContext.current
    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Fetch users when the screen loads
    LaunchedEffect(Unit) {
        userViewModel.getAllUsers(adminToken) { response ->
            isLoading = false
            if (response != null) {
                users = response
            } else {
                Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Manage Users", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(users) { user ->
                    UserCard(user, users, userViewModel, adminToken) { updatedUsers ->
                        users = updatedUsers // ✅ Refresh list after update or delete
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: UserProfile,
    users: List<UserProfile>,
    userViewModel: AdminUserViewModel,
    adminToken: String,
    onUserUpdate: (List<UserProfile>) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedName by remember { mutableStateOf(user.name) }
    var updatedRole by remember { mutableStateOf(user.isAdmin) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Full Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Role Dropdown
                Text(text = "Role", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (updatedRole) "Admin" else "User",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Admin") }, onClick = { updatedRole = true; expanded = false })
                        DropdownMenuItem(text = { Text("User") }, onClick = { updatedRole = false; expanded = false })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save & Cancel Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        isEditing = false
                        userViewModel.updateUser(adminToken, user._id, updatedName, updatedRole) { success, updatedUser ->
                            if (success && updatedUser != null) {
                                Toast.makeText(context, "User updated successfully!", Toast.LENGTH_SHORT).show()
                                onUserUpdate(users.map { if (it._id == updatedUser._id) updatedUser else it }) // ✅ Update list
                            } else {
                                Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Save")
                    }

                    Button(onClick = { isEditing = false }) {
                        Text("Cancel")
                    }
                }
            } else {
                // Display User Info
                Text(text = "Name: ${user.name}", fontSize = 18.sp)
                Text(text = "Email: ${user.email}", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Role: ${if (user.isAdmin) "Admin" else "User"}", fontSize = 14.sp, color = Color.Gray)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                    }

                    IconButton(onClick = {
                        userViewModel.deleteUser(adminToken, user._id) { success ->
                            if (success) {
                                Toast.makeText(context, "User deleted successfully!", Toast.LENGTH_SHORT).show()
                                onUserUpdate(users.filter { it._id != user._id }) // ✅ Correctly remove user
                            } else {
                                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}