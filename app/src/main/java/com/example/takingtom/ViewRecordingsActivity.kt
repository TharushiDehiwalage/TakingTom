package com.example.takingtom

import RecordingsAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewRecordingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recordingsAdapter: RecordingsAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recordings)

        // Initialize RecyclerView and Database Helper
        recyclerView = findViewById(R.id.recordingsRecyclerView)
        databaseHelper = DatabaseHelper(this)

        // Set Layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch recordings from the database and bind to adapter
        val recordings = databaseHelper.getAllRecordings()
        recordingsAdapter = RecordingsAdapter(this, recordings)
        recyclerView.adapter = recordingsAdapter
    }
}
