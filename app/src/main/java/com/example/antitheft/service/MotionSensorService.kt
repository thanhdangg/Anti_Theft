package com.example.antitheft.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import com.example.antitheft.ui.entry_pin.ActivityPinEntry
import kotlin.math.abs
import com.example.antitheft.R


class MotionSensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gravity: Sensor? = null

    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private val handler = Handler(Looper.getMainLooper())
    private var isAlarmTriggered = false

    private var mediaPlayer: MediaPlayer? = null



    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

//        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
//        gravity?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }

        initializeLastValues()

        registerSensorListeners()

    }
    private fun registerSensorListeners() {
        accelerometer?.let {
            if (!sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)) {
                Log.e("MotionSensorService", "Failed to register accelerometer listener")
            }
        } ?: Log.e("MotionSensorService", "Accelerometer sensor not available")

        gravity?.let {
            if (!sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)) {
                Log.e("MotionSensorService", "Failed to register gravity sensor listener")
            }
        } ?: Log.e("MotionSensorService", "Gravity sensor not available")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
    private fun initializeLastValues() {
        val initialEvent = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensor ->
            val event = FloatArray(3)
            sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).firstOrNull()?.let {
                sensorManager.registerListener(object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        Log.d("MotionSensorService", "initialEvent: $event")
                        event?.let {
                            lastX = it.values[0]
                            lastY = it.values[1]
                            lastZ = it.values[2]
                            sensorManager.unregisterListener(this)
                        }
                        Log.d("MotionSensorService", "initialEvent  lastX: $lastX, lastY: $lastY, lastZ: $lastZ")
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
//            Log.d("MotionSensorService", "lastX: $lastX, lastY: $lastY, lastZ: $lastZ")
//            Log.d("MotionSensorService", "x: $x, y: $y, z: $z")

            if (abs(lastX - x) > 1 || abs(lastY - y) > 1 || abs(lastZ - z) > 1) {
                Log.d("MotionSensorService", "Motion detected!")
                if (!isAlarmTriggered) {
                    isAlarmTriggered = true
                    triggerAlarm()
                    showPinEntryScreen()
                }
            }

            lastX = x
            lastY = y
            lastZ = z
        }
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
        }, 500) // Repeat every 0.5 seconds
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        stopAlarm()
    }
}