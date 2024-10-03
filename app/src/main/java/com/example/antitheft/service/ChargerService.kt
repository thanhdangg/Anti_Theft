package com.example.antitheft.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import com.example.antitheft.R
import com.example.antitheft.receiver.PowerDisconnectReceiver
import com.example.antitheft.ui.entry_pin.ActivityPinEntry

class ChargerService : Service() {

    private lateinit var powerDisconnectedReceiver: PowerDisconnectReceiver
    private val handler = Handler(Looper.getMainLooper())
    private var isAlarmTriggered = false

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        // Initialize and register the receiver
        powerDisconnectedReceiver = PowerDisconnectReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(powerDisconnectedReceiver, intentFilter)

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.getBooleanExtra("isAlarmTriggered", false)) {
                isAlarmTriggered = true
                triggerAlarm()
                showPinEntryScreen()
            }
        }
        return START_STICKY
    }

    private fun triggerAlarm() {

        handler.postDelayed({
            if (isAlarmTriggered) {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(1000)
                Log.d("MotionSensorService", "Alarm Triggered!")
                playAlarmSound()
                triggerAlarm()
            }
        }, 500)
    }

    private fun showPinEntryScreen() {
        val intent = Intent(this, ActivityPinEntry::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    fun stopAlarm() {
        isAlarmTriggered = false
        handler.removeCallbacksAndMessages(null)
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

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the service is destroyed
        unregisterReceiver(powerDisconnectedReceiver)
        stopAlarm()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
