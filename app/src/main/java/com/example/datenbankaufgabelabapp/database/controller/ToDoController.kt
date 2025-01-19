package com.example.datenbankaufgabelabapp.database.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.datenbankaufgabelabapp.database.DBHelper
import com.example.datenbankaufgabelabapp.database.dataclass.TodoDataClass

/**
 * Controller class responsible for managing ToDo items in the database.
 * Provides methods to insert, update, delete, and fetch ToDos.
 *
 * @param context Application context for database access.
 */
class TodoController(context: Context) {
    private val dbHelper = DBHelper(context)

    /**
     * Inserts a new ToDo into the database.
     *
     * @param todo The ToDoDataClass object to insert.
     * @return True if insertion was successful, false otherwise.
     */
    fun insertTodo(todo: TodoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("due_date", todo.dueDate)
                put("description", todo.description)
                put("status", todo.status)
            }
            val result = db.insert("todos", null, values)
            result != -1L
        } catch (e: SQLiteException) {
            Log.e("TodoController", "Insert failed", e)
            false
        } catch (e: Exception) {
            Log.e("TodoController", "Unexpected error during insert", e)
            false
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TodoController", "Error closing database", e)
            }
        }
    }

    /**
     * Updates an existing ToDo in the database.
     *
     * @param todo The ToDoDataClass object with updated data.
     * @return True if update was successful, false otherwise.
     */
    fun updateTodo(todo: TodoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("due_date", todo.dueDate)
                put("description", todo.description)
                put("status", todo.status)
            }
            val result = db.update("todos", values, "id = ?", arrayOf(todo.id.toString()))
            Log.d("TodoController", "Update result: $result, ToDo ID: ${todo.id}")
            result > 0
        } catch (e: SQLiteException) {
            Log.e("TodoController", "Update failed", e)
            false
        } catch (e: Exception) {
            Log.e("TodoController", "Unexpected error during update", e)
            false
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TodoController", "Error closing database", e)
            }
        }
    }

    /**
     * Deletes a ToDo from the database.
     *
     * @param todoId The ID of the ToDo to delete.
     * @return True if deletion was successful, false otherwise.
     */
    fun deleteTodo(todoId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("todos", "id = ?", arrayOf(todoId.toString()))
            result > 0
        } catch (e: SQLiteException) {
            Log.e("TodoController", "Delete failed", e)
            false
        } catch (e: Exception) {
            Log.e("TodoController", "Unexpected error during delete", e)
            false
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TodoController", "Error closing database", e)
            }
        }
    }

    /**
     * Fetches all ToDos from the database.
     *
     * @return A list of TodoDataClass objects.
     */
    fun getAllTodos(): List<TodoDataClass> {
        val db = dbHelper.readableDatabase
        val todos = mutableListOf<TodoDataClass>()
        val cursor = db.rawQuery("SELECT * FROM todos", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val todo = TodoDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status"))
                    )
                    todos.add(todo)
                } while (cursor.moveToNext())
            }
        } catch (e: SQLiteException) {
            Log.e("TodoController", "Fetching todos failed", e)
        } catch (e: Exception) {
            Log.e("TodoController", "Unexpected error during fetch", e)
        } finally {
            try {
                cursor.close()
            } catch (e: Exception) {
                Log.e("TodoController", "Error closing cursor", e)
            }
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TodoController", "Error closing database", e)
            }
        }
        return todos
    }

    /**
     * Fetches ToDos filtered by their status.
     *
     * @param filterActive If true, fetches active ToDos (status = 0),
     *                     else fetches completed ToDos (status = 1).
     * @return A list of filtered TodoDataClass objects.
     */
    fun getAllTodos(filterActive: Boolean): List<TodoDataClass> {
        return getAllTodos().filter { it.status == if (filterActive) 0 else 1 }
    }

    /**
     * Fetches all active (open) ToDos.
     *
     * @return A list of active TodoDataClass objects.
     */
    fun getActiveTodos(): List<TodoDataClass> {
        return getAllTodos().filter { it.status == 0 }
    }

    /**
     * Fetches all completed ToDos.
     *
     * @return A list of completed TodoDataClass objects.
     */
    fun getDoneTodos(): List<TodoDataClass> {
        return getAllTodos().filter { it.status == 1 }
    }
}
