package com.example.takingtom

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecordingDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Recordings.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "recordings"
        const val COLUMN_ID = "id"
        const val COLUMN_PATH = "path"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PATH TEXT NOT NULL,
                $COLUMN_TIMESTAMP TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRecording(path: String, timestamp: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATH, path)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllRecordings(): List<Recording> {
        val db = readableDatabase
        val recordings = mutableListOf<Recording>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                recordings.add(Recording(id, path, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return recordings
    }

    data class Recording(
        val id: Int,
        val path: String,
        val timestamp: String
    )

    fun deleteRecording(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }



}
