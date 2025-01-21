package com.example.a9todoapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a9todoapp.database.controller.ToDoController
import com.example.a9todoapp.database.dataclass.TaskDataClass
import com.example.a9todoapp.database.dataclass.TaskStatus
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding


@Composable
fun Dashboard(navController: NavController) {
    val context = LocalContext.current
    val toDoController = ToDoController(context)

    // State for active and completed tasks, filter and sorting options
    var activeTasks by remember { mutableStateOf(toDoController.getAllActiveTasks("priority")) }
    var completedTasks by remember { mutableStateOf(toDoController.getAllCompletedTasks("priority")) }
    var filter by remember { mutableStateOf<TaskStatus?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    var sortBy by remember { mutableStateOf("priority") }

    // Function to refresh tasks after changes
    val refreshTasks = {
        activeTasks = toDoController.getAllActiveTasks(sortBy)
        completedTasks = toDoController.getAllCompletedTasks(sortBy)
    }

    Scaffold(
        // Floating Action Button for adding tasks
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTask") },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = "MY TODOS",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Filter and sort options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Button to open filter dialog
                    Button(onClick = { isDialogOpen = true }) {
                        Text("Filter by Status: ${filter?.name ?: "All"}")
                    }

                    // Button to open sort options dropdown
                    SortDropdownMenu(
                        onSelectSortOption = { selectedSortBy ->
                            sortBy = selectedSortBy
                            refreshTasks()
                        }
                    )
                }

                // Display filter dialog if open
                if (isDialogOpen) {
                    AlertDialog(
                        onDismissRequest = { isDialogOpen = false },
                        title = { Text(text = "Filter by Status") },
                        text = {
                            Column {
                                Text("All", modifier = Modifier
                                    .clickable {
                                        filter = null
                                        isDialogOpen = false
                                    }
                                    .padding(8.dp))
                                HorizontalDivider()
                                Text("Open", modifier = Modifier
                                    .clickable {
                                        filter = TaskStatus.OPEN
                                        isDialogOpen = false
                                    }
                                    .padding(8.dp))
                                HorizontalDivider()
                                Text("Completed", modifier = Modifier
                                    .clickable {
                                        filter = TaskStatus.COMPLETED
                                        isDialogOpen = false
                                    }
                                    .padding(8.dp))
                            }
                        },
                        confirmButton = {
                            Button(onClick = { isDialogOpen = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                // Section Title for Tasks
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // LazyColumn to display tasks
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filter tasks based on the selected filter
                    val filteredTasks = (activeTasks + completedTasks)
                        .filter { filter == null || it.state == filter }

                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            isExpanded = task.id == expandedTaskId,
                            onExpand = {
                                expandedTaskId = if (task.id == expandedTaskId) null else task.id
                            },
                            onEdit = { selectedTask ->
                                navController.navigate("editTaskScreen/${selectedTask.id}")
                            },
                            onDelete = { taskId ->
                                val success = toDoController.deleteTask(taskId)
                                if (success) {
                                    refreshTasks()
                                }
                            },
                            onToggleCompletion = { taskId, isCompleted ->
                                val success = if (isCompleted) {
                                    toDoController.markTaskAsCompleted(taskId)
                                } else {
                                    toDoController.markTaskAsIncomplete(taskId)
                                }
                                if (success) {
                                    refreshTasks()
                                }
                            },
                            isCompleted = task.state == TaskStatus.COMPLETED
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SortDropdownMenu(onSelectSortOption: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray, // Lighter color for the button
                contentColor = Color.Black
            )
        ) {
            Text("Sort By")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Sort options for priority and end date
            DropdownMenuItem(
                text = { Text("Priority") },
                onClick = {
                    onSelectSortOption("priority")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("End Date") },
                onClick = {
                    onSelectSortOption("endDate")
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskDataClass,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onEdit: (TaskDataClass) -> Unit,
    onDelete: (Int) -> Unit,
    onToggleCompletion: (Int, Boolean) -> Unit,
    isCompleted: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onExpand() }, // Clickable to expand/collapse the card
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFD7F3E3) else Color(0xFFF3D7D7) // Color based on task completion
        ),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row for task name, checkbox, and action buttons (edit/delete)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompletion(task.id, it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = task.name,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                )
                IconButton(onClick = { onEdit(task) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(task.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            // Conditionally render additional details if the card is expanded
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Priority: ${task.priority}")
                Text("End Date: ${task.endDate}")
                Text("Description: ${task.description}")
            } else {
                // Display "Click to see details" when the card is collapsed
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Click to see details",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
