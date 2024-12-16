package com.example.takingtom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recordButton: Button
    private lateinit var playbackButton: Button
    private lateinit var viewRecordingsButton: Button
    private lateinit var timerTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var audioWaveView: AudioWaveView

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""
    private var isRecording = false

    private val handler = Handler(Looper.getMainLooper())
    private var elapsedTime = 0
    private var timerRunnable: Runnable? = null

    private lateinit var dbHelper: RecordingDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        dbHelper = RecordingDatabaseHelper(this)

        setButtonListeners()
        checkPermissions()
    }

    private fun initializeViews() {
        recordButton = findViewById(R.id.recordButton)
        playbackButton = findViewById(R.id.playbackButton)
        viewRecordingsButton = findViewById(R.id.viewRecordingsButton)
        timerTextView = findViewById(R.id.timerTextView)
        logoutButton = findViewById(R.id.logoutButton)
        audioWaveView = findViewById(R.id.audioWaveView)
    }

    private fun setButtonListeners() {
        recordButton.setOnClickListener { toggleRecording() }
        playbackButton.setOnClickListener { togglePlayback() }
        viewRecordingsButton.setOnClickListener { viewRecordings() }
        logoutButton.setOnClickListener { logoutUser() }
    }

    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 101)
        }
    }

    private fun toggleRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        audioFilePath = "${externalCacheDir?.absolutePath}/recording_$timestamp.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)
        }
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            recordButton.text = "Stop Recording"
            startWaveformUpdates()
            startTimer()
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            recordButton.text = "Record Voice"
            stopWaveformUpdates()
            stopTimer()

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            dbHelper.insertRecording(audioFilePath, timestamp)
            Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun togglePlayback() {
        if (mediaPlayer?.isPlaying == true) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
            }
            playbackButton.text = "Stop Playback"
            mediaPlayer?.setOnCompletionListener { stopPlayback() }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to play the audio.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        playbackButton.text = "Play Back Voice"
    }

    private fun viewRecordings() {
        val intent = Intent(this, ViewRecordingsActivity::class.java)
        startActivity(intent)
    }

    private fun startWaveformUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                if (isRecording) {
                    val simulatedWaveform = FloatArray(100) { (Math.random().toFloat() * 2 - 1) }
                    audioWaveView.updateWaveform(simulatedWaveform)
                    handler.postDelayed(this, 100)
                }
            }
        })
    }

    private fun stopWaveformUpdates() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        elapsedTime = 0
        timerRunnable = object : Runnable {
            override fun run() {
                elapsedTime++
                val minutes = elapsedTime / 60
                val seconds = elapsedTime % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable!!)
    }

    private fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerTextView.text = "00:00"
    }

    private fun logoutUser() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}
