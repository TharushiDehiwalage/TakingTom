package com.example.takingtom

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
class MainActivity : AppCompatActivity() {
    private lateinit var recordButton: Button
    private lateinit var playbackButton: Button
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recordButton = findViewById(R.id.recordButton)
        playbackButton = findViewById(R.id.playbackButton)
        val characterView = findViewById<ImageView>(R.id.characterView)
        // Check for permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                111)
        }
        // Path to save audio
        audioFilePath = "${externalCacheDir?.absolutePath}/recording.3gp"
        // Set up Record Button
        recordButton.setOnClickListener {
            if (mediaRecorder == null) {
                startRecording()
            } else {
                stopRecording()
            }
        }
        // Set up Playback Button
        playbackButton.setOnClickListener {
            playRecording()
        }
        // Simple character interaction (change image on touch)
        characterView.setOnClickListener {
            characterView.setImageResource(R.drawable.bella) // Change image

        }
        characterView.setOnClickListener {
            characterView.setImageResource(R.drawable.bella)
        }
        }
    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
                recordButton.text = "Stop Recording"
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        recordButton.text = "Record Voice"
    }
    private fun playRecording() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }

}
