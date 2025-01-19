package com.example.datenbankaufgabelabapp.database.dataclass

/**
 * Data class representing a ToDo item.
 *
 * @property id Unique identifier for the ToDo.
 * @property name Name/title of the ToDo.
 * @property priority Priority level (0 = Low, 1 = Medium, 2 = High).
 * @property dueDate Due date in "dd.MM.yyyy" format.
 * @property description Optional description of the ToDo.
 * @property status Status of the ToDo (0 = Open, 1 = Completed).
 */
data class TodoDataClass(
    val id: Int = 0,
    val name: String,
    val priority: Int,
    val dueDate: String,
    val description: String?,
    val status: Int
)
