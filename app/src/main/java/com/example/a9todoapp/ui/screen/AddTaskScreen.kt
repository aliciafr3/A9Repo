package com.example.a9todoapp.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a9todoapp.database.controller.ToDoController
import com.example.a9todoapp.database.dataclass.TaskDataClass
import com.example.a9todoapp.database.dataclass.TaskStatus


@Composable
fun AddTaskScreen(onTaskAdded: () -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("1") }
    var taskEndDate by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Input field for task name
        TextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        // Input field for task priority, ensures only digits
        TextField(
            value = taskPriority,
            onValueChange = { if (it.all { char -> char.isDigit() }) taskPriority = it },
            label = { Text("Priority") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            isError = errorMessage.isNotEmpty()
        )
        // Input field for task end date with a date picker
        TextField(
            value = taskEndDate,
            onValueChange = {},
            label = { Text("End Date") },
            placeholder = { Text("Click the icon to select a date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    showDatePicker(context) { date ->
                        taskEndDate = date
                    }
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            }
        )
        // Input field for task description
        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Show error message if there is an issue with the input
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add the task after validation
        Button(onClick = {
            // Validate input before saving the task
            val priorityInt = taskPriority.toIntOrNull() ?: run {
                errorMessage = "Priority must be a valid number."
                return@Button
            }

            if (taskName.isBlank()) {
                errorMessage = "Task name cannot be empty."
                return@Button
            }

            // Save the task to the database
            val newTask = TaskDataClass(0, taskDescription, priorityInt, taskEndDate, TaskStatus.OPEN, taskName)
            val success = ToDoController(context).insertTask(newTask)
            if (success) {
                onTaskAdded()
            }
        }) {
            Text("Add Task")
        }
    }
}
