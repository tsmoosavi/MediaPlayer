package com.example.mediaplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var recorder: MediaRecorder? = null
    private var fileName: String = ""
    val url = "https://dl.nicmusic.net/nicmusic/024/004/Dil%20Ki%20Hai%20Tamanna.mp3" // your URL here
    val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setDataSource(url)
        prepare() // might take long! (for buffering, etc)
    }
    lateinit var binding: ActivityMainBinding
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    "you granted this permission",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "you denied this permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        binding.playButton.setOnClickListener{
            playMedia()
            Toast.makeText(this, mediaPlayer.duration.toString(), Toast.LENGTH_SHORT).show()
        }
        binding.pauseButton.setOnClickListener{
            pauseMedia()
        }
        showProgressBar()
        binding.recordButton.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            }else{
                startRecording()
            }

        }

    }

    private fun showProgressBar() {
        binding.seekBar.max = mediaPlayer.duration
        binding.seekBar.progress = mediaPlayer.currentPosition
    }

    private fun pauseMedia() {
       mediaPlayer.pause()
    }


    private fun playMedia() {

        mediaPlayer.start()
    }
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {}

            start()
        }
    }


}