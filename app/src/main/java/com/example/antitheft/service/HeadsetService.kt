package com.example.antitheft.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.example.antitheft.R
import com.example.antitheft.ui.entry_pin.ActivityPinEntry

class HeadsetService : Service() {

    private var isAlarmTriggered = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val state = it.getIntExtra("state", -1)
            val context = applicationContext
            Log.d("HeadsetService", "Received headset state: $state")
            when (state) {
                0 -> {
                    Log.d("HeadsetService", "Headphones disconnected")
                    Toast.makeText(context, "Headphones disconnected!", Toast.LENGTH_SHORT).show()
                    isAlarmTriggered = true
                    triggerAlarm()
                    showPinEntryScreen()
                }
                1 -> {
                    Log.d("HeadsetService", "Headphones connected")
                    Toast.makeText(context, "Headphones connected!", Toast.LENGTH_SHORT).show()
                    stopAlarm()
                }
                else -> {
                    Log.d("HeadsetService", "Unknown headset state")
                    Toast.makeText(context, "Unknown state", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return START_STICKY
    }

    private fun triggerAlarm() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(1000)
        Log.d("HeadsetService", "Alarm Triggered!")
        playAlarmSound()
    }

    private fun showPinEntryScreen() {
        val intent = Intent(this, ActivityPinEntry::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun stopAlarm() {
        isAlarmTriggered = false
        stopAlarmSound()
    }

    private fun playAlarmSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    private fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}