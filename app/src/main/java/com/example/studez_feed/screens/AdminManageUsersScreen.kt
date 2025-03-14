package com.example.studez_feed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.NavController


data class UserItem(val id: Int, val name: String, val email: String)

@Composable
fun AdminManageUsersScreen(navController: NavController?) {
    var userList by remember {
        mutableStateOf(
            listOf(
                UserItem(1, "John Doe", "john@example.com"),
                UserItem(2, "Jane Smith", "jane@example.com"),
                UserItem(3, "Michael Brown", "michael@example.com")
            )
        )
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
            // Title
            Text(
                text = "Manage Users",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List of Users
            userList.forEach { user ->
                UserManagementCard(user, onDelete = {
                    userList = userList.filter { it.id != user.id }
                })
            }
        }
    }
}

@Composable
fun UserManagementCard(user: UserItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = user.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { /* TODO: Implement Edit User */ }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

