package com.example.a9todoapp.ui.screen

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.a9todoapp.database.controller.ToDoController
import com.example.a9todoapp.database.dataclass.TaskDataClass
import com.example.a9todoapp.database.dataclass.TaskStatus // Import TaskStatus enum
import java.util.Calendar

@Composable
fun EditTaskScreen(taskId: Int, navController: NavController) {
    val context = LocalContext.current
    val toDoController = ToDoController(context)

    // Retrieve the task from the database using the taskId
    val task = toDoController.getTaskById(taskId)

    // Remember the original task details
    var taskName by remember { mutableStateOf(task?.name ?: "") }
    var taskPriority by remember { mutableStateOf(task?.priority?.toString() ?: "") }
    var taskEndDate by remember { mutableStateOf(task?.endDate ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Edit Task", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Task Name input
        TextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Priority input
        TextField(
            value = taskPriority,
            onValueChange = { if (it.all { char -> char.isDigit() }) taskPriority = it },
            label = { Text("Priority") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            isError = errorMessage.isNotEmpty()
        )

        // End Date input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    showDatePicker(context) { date ->
                        taskEndDate = date
                    }
                }
        ) {
            TextField(
                value = taskEndDate,
                onValueChange = {},
                label = { Text("End Date") },
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
        }

        // Task Description input
        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Error message display
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(onClick = {
            // Validate input before updating the task
            val priorityInt = taskPriority.toIntOrNull() ?: run {
                errorMessage = "Priority must be a valid number."
                return@Button
            }

            if (taskName.isBlank()) {
                errorMessage = "Task name cannot be empty."
                return@Button
            }

            // Save the changes to the database
            val updatedTask = TaskDataClass(
                id = task?.id ?: 0,
                name = taskName,
                description = taskDescription,
                priority = priorityInt,
                endDate = taskEndDate,
                state = task?.state ?: TaskStatus.OPEN // Use TaskStatus.OPEN
            )

            val success = toDoController.updateTask(updatedTask)

            if (success) {
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Go back to the dashboard
            } else {
                Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Save")
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
