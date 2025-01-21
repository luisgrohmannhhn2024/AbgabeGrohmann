package com.example.datenbankaufgabelabapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.datenbankaufgabelabapp.database.controller.TodoController
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass
import com.example.datenbankaufgabelabapp.ui.components.EditTodoDialog
import com.example.datenbankaufgabelabapp.ui.components.ExpandableTodoCard
import com.example.datenbankaufgabelabapp.ui.components.FilterSortDialog
import java.text.SimpleDateFormat
import java.util.*

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
    var sortBy by remember { mutableStateOf("PRIORITY") }
    var sortOrder by remember { mutableStateOf("DESC") }
    var priorityFilter by remember { mutableStateOf("ALL") }

    // Overdue filter toggle
    var overdueOnly by remember { mutableStateOf(false) }

    /**
     * Applies sorting and filtering to the list of ToDos.
     */
    fun applySortAndFilter(list: List<TodoDataClass>): List<TodoDataClass> {
        val filteredByPriority = if (priorityFilter == "ALL") list else list.filter { it.priority == priorityFilter.toInt() }
        val overdueFiltered = if (overdueOnly) {
            filteredByPriority.filter { isOverdue(it) && it.status == 0 }
        } else {
            filteredByPriority
        }
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

    val finalTodos = remember(todos, sortBy, sortOrder, priorityFilter, overdueOnly) {
        try {
            applySortAndFilter(todos)
        } catch (e: Exception) {
            Log.e("ToDoScreen", "Error applying sort and filter", e)
            emptyList()
        }
    }

    if (snackbarMessage != null) {
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage!!)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (filterActive) "Aktive ToDos" else "Erledigte ToDos")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
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

            // Hinweis unter den Buttons (nur wenn ToDos existieren)
            if (finalTodos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tippen Sie auf ein ToDo, um es zu bearbeiten",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
                                if (!success) snackbarMessage = "Fehler beim Löschen!" else todos = todoController.getAllTodos(filterActive)
                            } catch (e: Exception) {
                                Log.e("ToDoScreen", "Fehler beim Löschen des ToDos", e)
                                snackbarMessage = "Ein unerwarteter Fehler ist aufgetreten."
                            }
                        },
                        onMarkDoneClick = {
                            try {
                                val updated = todo.copy(status = 1)
                                val success = todoController.updateTodo(updated)
                                if (!success) snackbarMessage = "Fehler beim Aktualisieren!" else todos = todoController.getAllTodos(filterActive)
                            } catch (e: Exception) {
                                Log.e("ToDoScreen", "Fehler beim Aktualisieren des ToDos", e)
                                snackbarMessage = "Ein unerwarteter Fehler ist aufgetreten."
                            }
                        },
                        showDeleteButton = !filterActive
                    )
                }
            }
        }
    }

    // Filter/Sort Dialog
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
                    Log.e("ToDoScreen", "Fehler beim Anwenden der Filter", e)
                    snackbarMessage = "Ein unerwarteter Fehler ist aufgetreten."
                }
                showFilterDialog = false
            }
        )
    }

    // Edit Dialog
    if (showEditDialog) {
        EditTodoDialog(
            todo = selectedTodo,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                try {
                    val success = if (updated.id == 0) todoController.insertTodo(updated) else todoController.updateTodo(updated)
                    if (!success) snackbarMessage = "Fehler beim Speichern!" else todos = todoController.getAllTodos(filterActive)
                } catch (e: Exception) {
                    Log.e("ToDoScreen", "Fehler beim Speichern des ToDos", e)
                    snackbarMessage = "Ein unerwarteter Fehler ist aufgetreten."
                }
                showEditDialog = false
            },
            onDelete = { toDelete ->
                try {
                    val success = todoController.deleteTodo(toDelete.id)
                    if (!success) snackbarMessage = "Fehler beim Löschen!" else todos = todoController.getAllTodos(filterActive)
                } catch (e: Exception) {
                    Log.e("ToDoScreen", "Fehler beim Löschen des ToDos", e)
                    snackbarMessage = "Ein unerwarteter Fehler ist aufgetreten."
                }
                showEditDialog = false
            }
        )
    }
}

/**
 * Checks if a ToDo is overdue based on its due date.
 */
fun isOverdue(todo: TodoDataClass): Boolean {
    if (todo.status == 1) return false
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return try {
        val dueTime = sdf.parse(todo.dueDate)?.time ?: 0L
        dueTime < System.currentTimeMillis()
    } catch (e: Exception) {
        Log.e("isOverdue", "Fehler beim Parsen des Fälligkeitsdatums", e)
        false
    }
}
