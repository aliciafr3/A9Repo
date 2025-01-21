package com.example.a9todoapp.database.dataclass

// Enum class to represent the possible states of a task (open or completed)
enum class TaskStatus(val value: Int) {
    OPEN(0), // Task is open and not completed
    COMPLETED(1); // Task has been completed

    companion object {
        // Converts an integer value to its corresponding TaskStatus enum
        fun fromInt(value: Int): TaskStatus {
            // Returns the matching TaskStatus or throws an exception if not found
            return entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unknown TaskStatus value: $value")
        }
    }
}

// Data class to represent a task with various properties like id, description, priority, etc.
data class TaskDataClass(
    val id: Int, // Unique identifier for the task
    val description: String, // Description of the task
    val priority: Int, // Priority of the task (e.g., low, medium, high)
    val endDate: String, // The end date for the task in String format
    val state: TaskStatus, // Current state of the task (OPEN or COMPLETED)
    val name: String, // Name or title of the task
)
