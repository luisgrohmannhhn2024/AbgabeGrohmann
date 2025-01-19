package com.example.datenbankaufgabelabapp.ui.screen

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.datenbankaufgabelabapp.database.controller.TodoController
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass
import com.example.datenbankaufgabelabapp.ui.components.PriorityDropdownDialog
import java.util.*

/**
 * Composable function for the Edit ToDo Screen.
 *
 * @param existingToDoId The ID of the ToDo to edit. Null if creating a new ToDo.
 * @param onDone Callback when editing is done.
 * @param navController Navigation controller for handling navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditToDoScreen(
    existingToDoId: Int?,
    onDone: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val todoController = TodoController(context)

    // Retrieve the existing ToDo if editing
    val existingToDo by remember {
        mutableStateOf(
            existingToDoId?.let { id -> todoController.getAllTodos().find { it.id == id } }
        )
    }

    // Initialize state variables with existing ToDo data or defaults
    var name by remember { mutableStateOf(existingToDo?.name ?: "") }
    var priority by remember { mutableIntStateOf(existingToDo?.priority ?: 0) }
    var dueDate by remember { mutableStateOf(existingToDo?.dueDate ?: "") }
    var description by remember { mutableStateOf(existingToDo?.description ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Setup DatePickerDialog with error handling
    val calendar = Calendar.getInstance()
    val parts = dueDate.split(".")
    if (parts.size == 3) {
        try {
            calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
            calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
            calendar.set(Calendar.YEAR, parts[2].toInt())
        } catch (e: Exception) {
            Log.e("EditToDoScreen", "Error parsing due date", e)
        }
    }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val dd = dayOfMonth.toString().padStart(2, '0')
            val mm = (month + 1).toString().padStart(2, '0')
            dueDate = "$dd.$mm.$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (existingToDo == null) "Neues ToDo" else "ToDo bearbeiten")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Name Input Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Priority Dropdown
            PriorityDropdownDialog(
                priority = priority,
                onPriorityChange = { priority = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Due Date Input Field with Date Picker
            Row {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Fälligkeitsdatum (tt.mm.yyyy)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    try {
                        datePickerDialog.show()
                    } catch (e: Exception) {
                        Log.e("EditToDoScreen", "Error showing DatePickerDialog", e)
                    }
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Datum wählen")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Description Input Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display error message if any
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Save Button with Validation and Crash Safety
            Button(
                onClick = {
                    // Validation
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
                        id = existingToDo?.id ?: 0,
                        name = name,
                        priority = priority,
                        dueDate = dueDate,
                        description = description,
                        status = existingToDo?.status ?: 0 // neu => 0 (offen)
                    )
                    try {
                        if (existingToDo == null) {
                            val success = todoController.insertTodo(newTodo)
                            if (!success) {
                                errorMessage = "Einfügen fehlgeschlagen!"
                                return@Button
                            }
                        } else {
                            val success = todoController.updateTodo(newTodo)
                            if (!success) {
                                errorMessage = "Aktualisieren fehlgeschlagen!"
                                return@Button
                            }
                        }
                        onDone()
                    } catch (e: Exception) {
                        Log.e("EditToDoScreen", "Error saving ToDo", e)
                        errorMessage = "Ein unerwarteter Fehler ist aufgetreten."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Speichern")
            }

            // Delete Button, visible only if editing an existing ToDo
            if (existingToDo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Use safe calls and let to avoid smart cast issues
                        existingToDo?.id?.let { id ->
                            try {
                                val success = todoController.deleteTodo(id)
                                if (!success) {
                                    errorMessage = "Löschen fehlgeschlagen!"
                                } else {
                                    onDone()
                                }
                            } catch (e: Exception) {
                                Log.e("EditToDoScreen", "Error deleting ToDo", e)
                                errorMessage = "Löschen fehlgeschlagen!"
                            }
                        } ?: run {
                            errorMessage = "ToDo existiert nicht."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Löschen")
                }
            }
        }
    }
}
