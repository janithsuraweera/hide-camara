package com.example.backgroundcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.backgroundcamera.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request necessary permissions
        requestPermissions()

        // Start recording when the start button is clicked
        binding.startButton.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                Toast.makeText(this, "Already recording!", Toast.LENGTH_SHORT).show()
            }
        }

        // Stop recording when the stop button is clicked
        binding.stopButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                Toast.makeText(this, "No recording in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        // Open settings when settings icon is clicked
        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissions.any {
                    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
                }) {
                ActivityCompat.requestPermissions(this, permissions, 100)
            }
        }
    }

    private fun startRecording() {
        val outputFile = getOutputFile()
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        isRecording = true
        Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(this, "Recording stopped!", Toast.LENGTH_SHORT).show()
    }

    private fun getOutputFile(): File {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "BackgroundCamera"
        )
        if (!dir.exists()) dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "VID_$timestamp.mp4")
    }
}
