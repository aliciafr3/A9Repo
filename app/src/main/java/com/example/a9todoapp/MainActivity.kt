package com.example.a9todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a9todoapp.ui.screen.AddTaskScreen
import com.example.a9todoapp.ui.screen.Dashboard
import com.example.a9todoapp.ui.theme.A9TodoAppTheme
import com.example.a9todoapp.ui.screen.EditTaskScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A9TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Set up navigation for the app
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Initialize the navigation controller

    NavHost(navController = navController, startDestination = "dashboard") {
        // Define the navigation graph with different routes
        composable("dashboard") {
            Dashboard(navController = navController) // Navigate to the Dashboard screen
        }
        composable("addTask") {
            AddTaskScreen(onTaskAdded = { navController.popBackStack() }) // Navigate to AddTaskScreen after task is added
        }
        composable("editTaskScreen/{taskId}") { backStackEntry ->
            // Retrieve taskId from the back stack entry for editing
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
            EditTaskScreen(
                taskId = taskId,
                navController = navController) // Navigate to EditTaskScreen with taskId
        }
    }
}
