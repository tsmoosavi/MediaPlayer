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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class MainActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted){
            Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show()
//            finish()
        }

    }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        binding.playButton.setOnClickListener {
            playMedia()
            Toast.makeText(this, mediaPlayer.duration.toString(), Toast.LENGTH_SHORT).show()
        }
        binding.pauseButton.setOnClickListener {
            pauseMedia()
        }
//        showProgressBar()
        binding.recordButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            startRecording()
        }
        binding.stopRecordButton.setOnClickListener {
            stopRecording()
        }
        binding.playRecordButton.setOnClickListener{
            startPlaying()
        }
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
//                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

//    private fun showProgressBar() {
//        binding.seekBar.max = mediaPlayer.duration
//        binding.seekBar.progress = mediaPlayer.currentPosition
//    }

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

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


}



