package com.example.a9todoapp.database.controller

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.a9todoapp.database.DbHelper
import com.example.a9todoapp.database.dataclass.TaskDataClass
import com.example.a9todoapp.database.dataclass.TaskStatus

class ToDoController(context: Context) {
    private val dbHelper = DbHelper(context)

    // Function to retrieve all active tasks sorted by the given criteria (priority or endDate)
    fun getAllActiveTasks(sortBy: String): List<TaskDataClass> {
        val db = dbHelper.readableDatabase
        val tasks = mutableListOf<TaskDataClass>()

        // Determine sorting order based on input parameter
        val sortOrder = when (sortBy) {
            "priority" -> "priority DESC"  // Sort by priority in descending order
            "endDate" -> "enddate ASC"     // Sort by endDate in ascending order
            else -> null                   // No sorting applied if input is invalid
        }

        // SQL query to fetch tasks based on their state (active) and optional sorting
        val query = "SELECT * FROM tasks WHERE state = ?" +
                (sortOrder?.let { " ORDER BY $it" } ?: "")
        val cursor: Cursor = db.rawQuery(query, arrayOf(TaskStatus.OPEN.value.toString()))

        try {
            if (cursor.moveToFirst()) {
                do {
                    // Creating TaskDataClass object for each row in the cursor
                    val task = TaskDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        state = TaskStatus.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow("state")))
                    )
                    tasks.add(task)  // Adding task to the list
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()  // Closing cursor to avoid memory leak
            db.close()      // Closing database connection
        }
        return tasks  // Returning the list of active tasks
    }

    // Function to retrieve all completed tasks sorted by the given criteria
    fun getAllCompletedTasks(sortBy: String): List<TaskDataClass> {
        val db = dbHelper.readableDatabase
        val tasks = mutableListOf<TaskDataClass>()

        val sortOrder = when (sortBy) {
            "priority" -> "priority DESC"  // Sort by priority in descending order
            "endDate" -> "enddate ASC"     // Sort by endDate in ascending order
            else -> null                   // No sorting applied if input is invalid
        }

        val query = "SELECT * FROM tasks WHERE state = ?" +
                (sortOrder?.let { " ORDER BY $it" } ?: "")
        val cursor: Cursor = db.rawQuery(query, arrayOf(TaskStatus.COMPLETED.value.toString()))

        try {
            if (cursor.moveToFirst()) {
                do {
                    // Creating TaskDataClass object for each row in the cursor
                    val task = TaskDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        state = TaskStatus.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow("state")))
                    )
                    tasks.add(task)  // Adding task to the list
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()  // Closing cursor to avoid memory leak
            db.close()      // Closing database connection
        }
        return tasks  // Returning the list of completed tasks
    }

    // Function to mark a task as completed by updating its state in the database
    fun markTaskAsCompleted(taskId: Int): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put("state", TaskStatus.COMPLETED.value)  // Setting the state to completed
            }
            val db = dbHelper.writableDatabase
            val rowsUpdated = db.update(
                "tasks",
                contentValues,
                "id = ?",
                arrayOf(taskId.toString())  // Updating task by ID
            )
            db.close()  // Closing database connection
            rowsUpdated > 0  // Returning true if task was updated
        } catch (e: Exception) {
            false  // Returning false if an error occurs
        }
    }

    // Function to mark a task as incomplete by updating its state in the database
    fun markTaskAsIncomplete(taskId: Int): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put("state", TaskStatus.OPEN.value)  // Setting the state to open (incomplete)
            }
            val db = dbHelper.writableDatabase
            val rowsUpdated = db.update(
                "tasks",
                contentValues,
                "id = ?",
                arrayOf(taskId.toString())  // Updating task by ID
            )
            db.close()  // Closing database connection
            rowsUpdated > 0  // Returning true if task was updated
        } catch (e: Exception) {
            false  // Returning false if an error occurs
        }
    }

    // Function to insert a new task into the database
    fun insertTask(task: TaskDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", task.name)  // Inserting task name
                put("priority", task.priority)  // Inserting task priority
                put("enddate", task.endDate)  // Inserting task end date
                put("description", task.description)  // Inserting task description
                put("state", task.state.value)  // Inserting task state as integer value
            }
            val result = db.insert("tasks", null, values)  // Inserting task into the database
            result != -1L  // Returning true if insertion was successful
        } catch (e: Exception) {
            false  // Returning false if an error occurs
        } finally {
            db.close()  // Closing database connection
        }
    }

    // Function to update an existing task in the database
    fun updateTask(task: TaskDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", task.name)  // Updating task name
                put("priority", task.priority)  // Updating task priority
                put("enddate", task.endDate)  // Updating task end date
                put("description", task.description)  // Updating task description
                put("state", task.state.value)  // Updating task state as integer value
            }
            val result = db.update("tasks", values, "id = ?", arrayOf(task.id.toString()))  // Updating task by ID
            db.close()  // Closing database connection
            result > 0  // Returning true if task was updated
        } catch (e: Exception) {
            false  // Returning false if an error occurs
        }
    }

    // Function to delete a task from the database by its ID
    fun deleteTask(taskId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("tasks", "id = ?", arrayOf(taskId.toString()))  // Deleting task by ID
            result > 0  // Returning true if task was deleted
        } catch (e: Exception) {
            false  // Returning false if an error occurs
        } finally {
            db.close()  // Closing database connection
        }
    }

    // Function to retrieve a task by its ID
    fun getTaskById(taskId: Int): TaskDataClass? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM tasks WHERE id = ?", arrayOf(taskId.toString()))
        var task: TaskDataClass? = null

        try {
            if (cursor.moveToFirst()) {
                // Creating TaskDataClass object for the row in the cursor
                task = TaskDataClass(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                    endDate = cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    state = TaskStatus.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow("state")))
                )
            }
        } finally {
            cursor.close()  // Closing cursor to avoid memory leak
            db.close()      // Closing database connection
        }
        return task  // Returning the task object or null if not found
    }
}
