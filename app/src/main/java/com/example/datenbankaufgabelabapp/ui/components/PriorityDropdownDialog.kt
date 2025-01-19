package com.example.datenbankaufgabelabapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Composable function for the Priority Dropdown Dialog.
 *
 * @param priority The current priority level.
 * @param onPriorityChange Callback when the priority is changed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdownDialog(
    priority: Int,
    onPriorityChange: (Int) -> Unit
) {
    val priorityLabels = listOf("Niedrig", "Mittel", "Hoch")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = priorityLabels.getOrElse(priority) { "Niedrig" },
            onValueChange = {},
            label = { Text("Priorität") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorityLabels.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onPriorityChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
