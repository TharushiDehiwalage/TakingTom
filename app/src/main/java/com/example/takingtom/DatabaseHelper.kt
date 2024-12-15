package com.example.takingtom

import Recording
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "recordings.db"
        const val DATABASE_VERSION = 1

        const val TABLE_RECORDINGS = "recordings"
        const val COLUMN_ID = "id"
        const val COLUMN_FILE_PATH = "file_path"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_RECORDINGS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FILE_PATH TEXT NOT NULL,
                $COLUMN_TIMESTAMP TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDINGS")
        onCreate(db)
    }

    // Function to add a new recording to the database
    fun addRecording(filePath: String, timestamp: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FILE_PATH, filePath)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        val id = db.insert(TABLE_RECORDINGS, null, values)
        db.close()
        return id
    }

    // Function to get all recordings
    fun getAllRecordings(): List<Recording> {
        val recordings = mutableListOf<Recording>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_RECORDINGS ORDER BY $COLUMN_TIMESTAMP DESC"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                recordings.add(Recording(id, filePath, timestamp))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return recordings
    }

    // Function to delete a specific recording
    fun deleteRecording(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_RECORDINGS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }
}
