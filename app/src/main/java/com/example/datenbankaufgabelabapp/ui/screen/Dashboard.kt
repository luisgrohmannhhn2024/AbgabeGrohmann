package com.example.datenbankaufgabelabapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

/**
 * Composable function representing the Dashboard screen.
 */

@Composable
fun Dashboard() {
    val navController = rememberNavController()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("todo_add") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Neues ToDo hinzufügen")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onShowActive = { navController.navigate("todo_active") },
                    onShowDone = { navController.navigate("todo_done") }
                )
            }
            composable("todo_active") {
                ToDoScreen(
                    filterActive = true,
                    onBack = { navController.popBackStack() },
                    onAddTodo = { navController.navigate("todo_add") },
                    onEditTodo = { todoId -> navController.navigate("todo_edit/$todoId") }
                )
            }
            composable("todo_done") {
                ToDoScreen(
                    filterActive = false,
                    onBack = { navController.popBackStack() },
                    onAddTodo = { navController.navigate("todo_add") },
                    onEditTodo = { todoId -> navController.navigate("todo_edit/$todoId") }
                )
            }
            // New ToDo
            composable("todo_add") {
                EditToDoScreen(
                    existingToDoId = null,
                    onDone = { navController.popBackStack() },
                    navController = navController
                )
            }
            // Edit ToDo
            composable(
                route = "todo_edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getInt("id")
                if (todoId != null) {
                    EditToDoScreen(
                        existingToDoId = todoId,
                        onDone = { navController.popBackStack() },
                        navController = navController
                    )
                } else {
                    // Log or handle null todoId
                    Log.e("Dashboard", "Todo ID is null in navigation")
                }
            }
        }
    }
}

/**
 * Composable function for the Dashboard screen layout.
 * Arranges buttons in a clean and modern layout without additional icons.
 *
 * @param onShowActive Callback to navigate to active ToDos.
 * @param onShowDone Callback to navigate to completed ToDos.
 */
@Composable
fun DashboardScreen(
    onShowActive: () -> Unit,
    onShowDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Changed from Center to Top
    ) {
        /** Spacer to move the headline further up */
        Spacer(modifier = Modifier.height(40.dp))

        /** Title Text */
        Text(
            text = "ToDo App",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        /** Active ToDos Button with vibrant color */
        DashboardButton(
            text = "Aktive ToDos anzeigen",
            backgroundColor = Color(0xFFC50C0C), // Green
            contentColor = Color.White,
            onClick = onShowActive
        )

        Spacer(modifier = Modifier.height(20.dp))

        /** Completed ToDos Button with vibrant color */
        DashboardButton(
            text = "Erledigte ToDos anzeigen",
            backgroundColor = Color(0xE82FAD21), // Red
            contentColor = Color.White,
            onClick = onShowDone
        )
    }
}

/**
 * Reusable composable for dashboard buttons with text only.
 *
 * @param text The button label.
 * @param backgroundColor The background color of the button.
 * @param contentColor The content (text) color of the button.
 * @param onClick Callback when the button is clicked.
 */
@Composable
fun DashboardButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
