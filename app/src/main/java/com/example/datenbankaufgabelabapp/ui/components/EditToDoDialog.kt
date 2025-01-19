package com.example.datenbankaufgabelabapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass

/**
 * Composable function for the Edit ToDo Dialog.
 *
 * @param todo The ToDoDataClass object to edit. Null if creating a new ToDo.
 * @param onDismiss Callback to dismiss the dialog.
 * @param onSave Callback to save the ToDo.
 * @param onDelete Callback to delete the ToDo.
 */
@Composable
fun EditTodoDialog(
    todo: TodoDataClass?,
    onDismiss: () -> Unit,
    onSave: (TodoDataClass) -> Unit,
    onDelete: (TodoDataClass) -> Unit
) {
    var name by remember { mutableStateOf(todo?.name ?: "") }
    var priority by remember { mutableIntStateOf(todo?.priority ?: 0) }
    var dueDate by remember { mutableStateOf(todo?.dueDate ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (todo == null) "Neues ToDo erstellen" else "ToDo bearbeiten") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                PriorityDropdownDialog(
                    priority = priority,
                    onPriorityChange = { priority = it }
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Fälligkeitsdatum (tt.mm.yyyy)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Beschreibung") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Validierung
                if (name.isBlank()) {
                    errorMessage = "Bitte Namen eingeben!"
                    return@Button
                }
                if (!dueDate.matches(Regex("""\d{2}\.\d{2}\.\d{4}"""))) {
                    errorMessage = "Datum muss im Format tt.mm.yyyy sein!"
                    return@Button
                }
                errorMessage = null

                val newTodo = TodoDataClass(
                    id = todo?.id ?: 0,
                    name = name,
                    priority = priority,
                    dueDate = dueDate,
                    description = description,
                    status = todo?.status ?: 0 // neu => 0 (offen)
                )
                onSave(newTodo)
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            if (todo != null) {
                Button(onClick = {
                    try {
                        onDelete(todo)
                    } catch (e: Exception) {
                        Log.e("EditTodoDialog", "Error deleting ToDo", e)
                    }
                }) {
                    Text("Löschen")
                }
            }
        }
    )

    // Display error message if present
    if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }
}
