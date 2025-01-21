package com.example.a9todoapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException


class DbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // This method is called when the database is created. No need to create tables since we use an existing database.
    override fun onCreate(db: SQLiteDatabase?) {}

    // Called when the database is upgraded. Deletes the old database and copies a new one from assets.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    // Returns a readable database and ensures the database is copied from assets if needed.
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    // Returns a writable database and ensures the database is copied from assets if needed.
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    // Copies the database from assets to the app's database directory if not already present.
    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                android.util.Log.d("DbHelper", "Database copied successfully")
            } catch (e: IOException) {
                android.util.Log.e("DbHelper", "Error copying database", e)
            }
        }
    }

    companion object {
        const val DATABASE_NAME = "todo.db" // Database file name
        const val DATABASE_VERSION = 1 // Database version
    }
}

