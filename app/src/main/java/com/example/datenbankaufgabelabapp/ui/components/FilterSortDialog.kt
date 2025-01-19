package com.example.datenbankaufgabelabapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function for the Filter and Sort Dialog.
 *
 * @param currentSortBy The current sorting criteria.
 * @param currentSortOrder The current sorting order.
 * @param priorityFilter The current priority filter.
 * @param onDismiss Callback to dismiss the dialog.
 * @param onApply Callback to apply the selected filters and sorting.
 */
@Composable
fun FilterSortDialog(
    currentSortBy: String,
    currentSortOrder: String,
    priorityFilter: String,
    onDismiss: () -> Unit,
    onApply: (String, String, String) -> Unit
) {
    var sortByState by remember { mutableStateOf(currentSortBy) }
    var sortOrderState by remember { mutableStateOf(currentSortOrder) }
    var priorityFilterState by remember { mutableStateOf(priorityFilter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter / Sortierung") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Sortieren nach:", style = MaterialTheme.typography.titleMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (sortByState == "PRIORITY"),
                            onClick = { sortByState = "PRIORITY" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Priorität",
                            modifier = Modifier.padding(top = 2.dp) // Slightly shift text down
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (sortByState == "DATE"),
                            onClick = { sortByState = "DATE" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Datum",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                HorizontalDivider()

                Text("Reihenfolge:", style = MaterialTheme.typography.titleMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (sortOrderState == "ASC"),
                            onClick = { sortOrderState = "ASC" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Aufsteigend",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (sortOrderState == "DESC"),
                            onClick = { sortOrderState = "DESC" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Absteigend",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                HorizontalDivider()

                // Filter
                Text("Prioritätsfilter:", style = MaterialTheme.typography.titleMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (priorityFilterState == "ALL"),
                            onClick = { priorityFilterState = "ALL" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Alle",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (priorityFilterState == "0"),
                            onClick = { priorityFilterState = "0" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Niedrig",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (priorityFilterState == "1"),
                            onClick = { priorityFilterState = "1" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Mittel",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (priorityFilterState == "2"),
                            onClick = { priorityFilterState = "2" }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Hoch",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    onApply(sortByState, sortOrderState, priorityFilterState)
                } catch (e: Exception) {
                    Log.e("FilterSortDialog", "Error applying filters and sorting", e)
                }
            }) {
                Text("Übernehmen")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(onClick = {
                    // Reset filters to default values
                    sortByState = "PRIORITY"
                    sortOrderState = "DESC"
                    priorityFilterState = "ALL"
                }) {
                    Text("Filter löschen")
                }
                TextButton(onClick = onDismiss) {
                    Text("Abbrechen")
                }
            }
        }
    )
}
