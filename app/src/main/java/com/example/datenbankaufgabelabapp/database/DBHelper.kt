package com.example.datenbankaufgabelabapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream

/**
 * SQLiteOpenHelper subclass for managing the ToDo database.
 *
 * Handles database creation, upgrades, and copying from assets if necessary.
 *
 * @param context Application context for database access.
 */
class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Called when the database is created for the first time.
     * Remains empty as we use an existing database from assets.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // The method remains empty as we use an existing database from assets.
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            context.deleteDatabase(DATABASE_NAME)
            copyDatabaseFromAssets()
        } catch (e: Exception) {
            Log.e("DBHelper", "Error during database upgrade", e)
        }
    }

    /**
     * Provides a readable database instance.
     *
     * @return The readable SQLiteDatabase.
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return try {
            super.getReadableDatabase()
        } catch (e: SQLiteException) {
            Log.e("DBHelper", "Error getting readable database", e)
            throw e
        }
    }

    /**
     * Provides a writable database instance.
     *
     * @return The writable SQLiteDatabase.
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return try {
            super.getWritableDatabase()
        } catch (e: SQLiteException) {
            Log.e("DBHelper", "Error getting writable database", e)
            throw e
        }
    }

    /**
     * Copies the database from the assets folder to the application's database path if it doesn't exist.
     */
    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // Log success and database size
                Log.d("DBHelper", "Database copied successfully to: ${dbPath.absolutePath}")
                Log.d("DBHelper", "Database size: ${dbPath.length()} bytes")
            } catch (e: Exception) {
                // Log failure
                Log.e("DBHelper", "Error copying database", e)
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "ToDoApp.db"
        private const val DATABASE_VERSION = 1
    }
}
