package com.example.datenbankaufgabelabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.datenbankaufgabelabapp.ui.screen.Dashboard
import com.example.datenbankaufgabelabapp.ui.theme.ToDoAppTheme

/**
 * Main activity of the ToDo app, setting the content to the Dashboard.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme {
                Dashboard()
            }
        }
    }
}
