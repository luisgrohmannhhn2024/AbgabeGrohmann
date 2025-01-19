package com.example.datenbankaufgabelabapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.datenbankaufgabelabapp.database.controller.TodoController
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass
import com.example.datenbankaufgabelabapp.ui.components.EditTodoDialog
import com.example.datenbankaufgabelabapp.ui.components.ExpandableTodoCard
import com.example.datenbankaufgabelabapp.ui.components.FilterSortDialog
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable function for displaying the ToDo screen.
 *
 * This screen manages the display, filtering, sorting, and editing of ToDos.
 * It allows the user to view active or completed ToDos, apply filters, and edit or delete them.
 *
 * @param filterActive Indicates whether the screen should display active (true) or completed (false) ToDos.
 * @param onBack Callback for handling the "back" button action.
 * @param onAddTodo Callback for handling the "add ToDo" button action.
 * @param onEditTodo Callback for handling the "edit ToDo" action with the ToDo ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(
    filterActive: Boolean,
    onBack: () -> Unit,
    onAddTodo: () -> Unit,
    onEditTodo: (Int) -> Unit
) {
    val context = LocalContext.current
    val todoController = TodoController(context)

    // State to manage the list of ToDos
    var todos by remember { mutableStateOf(todoController.getAllTodos(filterActive)) }

    // State for the Edit Dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<TodoDataClass?>(null) }

    // State for Snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // State for Filter/Sort Dialog
    var showFilterDialog by remember { mutableStateOf(false) }

    // Sorting and filtering parameters
    var sortBy by remember { mutableStateOf("PRIORITY") }  // Options: "PRIORITY" | "DATE"
    var sortOrder by remember { mutableStateOf("DESC") }   // Options: "ASC" | "DESC"
    var priorityFilter by remember { mutableStateOf("ALL") }  // Options: "ALL", "0", "1", "2"

    // Overdue filter toggle
    var overdueOnly by remember { mutableStateOf(false) }

    /**
     * Applies sorting and filtering to the list of ToDos.
     *
     * @param list The original list of ToDos.
     * @return A sorted and filtered list based on user preferences.
     */
    fun applySortAndFilter(list: List<TodoDataClass>): List<TodoDataClass> {
        // Filter by priority
        val filteredByPriority = if (priorityFilter == "ALL") list else list.filter { it.priority == priorityFilter.toInt() }

        // Filter by overdue status
        val overdueFiltered = if (overdueOnly) {
            filteredByPriority.filter { isOverdue(it) && it.status == 0 }
        } else {
            filteredByPriority
        }

        // Apply sorting
        return when (sortBy) {
            "PRIORITY" -> if (sortOrder == "ASC") overdueFiltered.sortedBy { it.priority } else overdueFiltered.sortedByDescending { it.priority }
            "DATE" -> {
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val sorted = overdueFiltered.sortedWith(compareBy {
                    try {
                        sdf.parse(it.dueDate)?.time ?: Long.MAX_VALUE
                    } catch (_: Exception) {
                        Long.MAX_VALUE
                    }
                })
                if (sortOrder == "ASC") sorted else sorted.reversed()
            }
            else -> overdueFiltered
        }
    }

    // Derive the final list of ToDos
    val finalTodos = remember(todos, sortBy, sortOrder, priorityFilter, overdueOnly) {
        try {
            applySortAndFilter(todos)
        } catch (e: Exception) {
            Log.e("ToDoScreen", "Error applying sort and filter", e)
            emptyList()
        }
    }

    // Display Snackbar if a message exists
    if (snackbarMessage != null) {
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage!!)
            snackbarMessage = null
        }
    }

    // Main UI Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (filterActive) "Aktive ToDos" else "Erledigte ToDos")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Row for Filter/Sort and Overdue buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { showFilterDialog = true }) {
                    Text("Filter / Sortierung")
                }
                Button(
                    onClick = {
                        overdueOnly = !overdueOnly
                        todos = todoController.getAllTodos(filterActive)
                    }
                ) {
                    Text(if (overdueOnly) "Alle anzeigen" else "Überfällige anzeigen")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LazyColumn for displaying ToDos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(finalTodos) { todo ->
                    ExpandableTodoCard(
                        todo = todo,
                        onEditClick = {
                            selectedTodo = todo
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            try {
                                val success = todoController.deleteTodo(todo.id)
                                if (!success) snackbarMessage = "Failed to delete!" else todos = todoController.getAllTodos(filterActive)
                            } catch (e: Exception) {
                                Log.e("ToDoScreen", "Error deleting ToDo", e)
                                snackbarMessage = "An unexpected error occurred."
                            }
                        },
                        onMarkDoneClick = {
                            try {
                                val updated = todo.copy(status = 1)
                                val success = todoController.updateTodo(updated)
                                if (!success) snackbarMessage = "Failed to update!" else todos = todoController.getAllTodos(filterActive)
                            } catch (e: Exception) {
                                Log.e("ToDoScreen", "Error marking ToDo as done", e)
                                snackbarMessage = "An unexpected error occurred."
                            }
                        },
                        showDeleteButton = !filterActive
                    )
                }
            }
        }
    }

    // Edit ToDo Dialog
    if (showEditDialog) {
        EditTodoDialog(
            todo = selectedTodo,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                try {
                    val success = if (updated.id == 0) todoController.insertTodo(updated) else todoController.updateTodo(updated)
                    if (!success) snackbarMessage = "Failed to save!" else todos = todoController.getAllTodos(filterActive)
                } catch (e: Exception) {
                    Log.e("ToDoScreen", "Error saving ToDo", e)
                    snackbarMessage = "An unexpected error occurred."
                }
                showEditDialog = false
            },
            onDelete = { toDelete ->
                try {
                    val success = todoController.deleteTodo(toDelete.id)
                    if (!success) snackbarMessage = "Failed to delete!" else todos = todoController.getAllTodos(filterActive)
                } catch (e: Exception) {
                    Log.e("ToDoScreen", "Error deleting ToDo", e)
                    snackbarMessage = "An unexpected error occurred."
                }
                showEditDialog = false
            }
        )
    }

    // Filter and Sort Dialog
    if (showFilterDialog) {
        FilterSortDialog(
            currentSortBy = sortBy,
            currentSortOrder = sortOrder,
            priorityFilter = priorityFilter,
            onDismiss = { showFilterDialog = false },
            onApply = { newSortBy, newSortOrder, newPriorityFilter ->
                try {
                    sortBy = newSortBy
                    sortOrder = newSortOrder
                    priorityFilter = newPriorityFilter
                    todos = todoController.getAllTodos(filterActive)
                } catch (e: Exception) {
                    Log.e("ToDoScreen", "Error applying new filters and sorting", e)
                    snackbarMessage = "An unexpected error occurred."
                }
                showFilterDialog = false
            }
        )
    }
}

/**
 * Checks if a ToDo is overdue based on its due date.
 *
 * @param todo The ToDo item to check.
 * @return True if the ToDo is overdue, false otherwise.
 */
fun isOverdue(todo: TodoDataClass): Boolean {
    if (todo.status == 1) return false
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return try {
        val dueTime = sdf.parse(todo.dueDate)?.time ?: 0L
        dueTime < System.currentTimeMillis()
    } catch (e: Exception) {
        Log.e("isOverdue", "Error parsing due date", e)
        false
    }
}
