package com.example.datenbankaufgabelabapp.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable function representing an expandable ToDo card.
 *
 * @param todo The ToDoDataClass object to display.
 * @param onEditClick Callback when edit is clicked.
 * @param onDeleteClick Callback when delete is clicked.
 * @param onMarkDoneClick Callback when mark as done is clicked.
 * @param showDeleteButton Flag to show delete button.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandableTodoCard(
    todo: TodoDataClass,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMarkDoneClick: () -> Unit,
    showDeleteButton: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    // Confirmation dialog states
    var showConfirmDone by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    // Color code based on priority
    val priorityColor = when (todo.priority) {
        2 -> Color(0xFFFFC8C8) // High
        1 -> Color(0xFFFFE7C4) // Medium
        else -> Color(0xFFC8FFD2) // Low
    }

    // Check if ToDo is overdue (open + due date < today)
    val isOverdue = remember(todo.dueDate, todo.status) {
        if (todo.status == 0) {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            try {
                val dueTime = sdf.parse(todo.dueDate)?.time ?: 0L
                dueTime < System.currentTimeMillis()
            } catch (e: Exception) {
                Log.e("ExpandableTodoCard", "Error parsing due date", e)
                false
            }
        } else false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Set a maximum height to prevent excessive growth
            .heightIn(min = 100.dp, max = 200.dp)
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = { onEditClick() }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = priorityColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title + Warning Icon if overdue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title with limited lines and ellipsis
                Text(
                    text = todo.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1, // Limit to one line
                    overflow = TextOverflow.Ellipsis, // Show ellipsis if text is too long
                    modifier = Modifier.weight(1f) // Take up available space
                )
                // If overdue, show red warning icon
                if (isOverdue) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Überfällig",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Mark as done and Delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (todo.status == 0) {
                    // Mark as done button
                    FilledTonalButton(
                        onClick = { showConfirmDone = true },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Erledigen",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Erledigt?")
                    }
                }
                if (todo.status == 1 && showDeleteButton) {
                    // Delete button
                    FilledTonalButton(
                        onClick = { showConfirmDelete = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Löschen",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Löschen")
                    }
                }
            }

            // Expandable content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Priorität: " + when (todo.priority) {
                            2 -> "Hoch"
                            1 -> "Mittel"
                            else -> "Niedrig"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Due date + overdue info
                    if (isOverdue) {
                        Row {
                            Text("Fällig: ${todo.dueDate} ")
                            Text("(Überfällig!)", color = Color.Red)
                        }
                    } else {
                        Text("Fällig: ${todo.dueDate}", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Beschreibung: ${todo.description ?: "Keine Beschreibung"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Status: ${if (todo.status == 1) "Erledigt" else "Offen"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Confirmation dialog for marking as done
    if (showConfirmDone) {
        AlertDialog(
            onDismissRequest = { showConfirmDone = false },
            title = { Text("ToDo erledigen") },
            text = { Text("Möchtest du dieses ToDo wirklich als erledigt markieren?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDone = false
                        onMarkDoneClick() // Trigger the mark as done callback
                    }
                ) {
                    Text("Ja, erledigen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDone = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    // Confirmation dialog for deleting
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("ToDo löschen") },
            text = { Text("Möchtest du dieses ToDo wirklich löschen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDelete = false
                        onDeleteClick() // Trigger the delete callback
                    }
                ) {
                    Text("Ja, löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
