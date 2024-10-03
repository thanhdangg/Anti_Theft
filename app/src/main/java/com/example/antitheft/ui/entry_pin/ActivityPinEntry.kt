package com.example.antitheft.ui.entry_pin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.antitheft.R
import com.example.antitheft.databinding.ActivityPinEntryBinding
import com.example.antitheft.help.PreferenceManager
import com.example.antitheft.service.ChargerService
import com.example.antitheft.service.HeadsetService
import com.example.antitheft.service.MotionSensorService
import com.example.antitheft.utils.OnPinEnteredListener

class ActivityPinEntry : AppCompatActivity(), OnPinEnteredListener {

    private lateinit var binding: ActivityPinEntryBinding
    private lateinit var correctPasscode : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPinEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correctPasscode = PreferenceManager.getPinCode(this).toString()

        binding.pinView.onPinEnteredListener = this
        binding.pinView.setBackGroundColor(R.color.status_red)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        "Enter pin to unlock".also { tvTitle.text = it }
    }
    override fun onPinEntered(pinCode: String) {
        if (pinCode == correctPasscode) {
            // to do
            stopAlarm()
            finish()
        }
    }

    private fun stopAlarm() {
        val intentMotion = Intent(this, MotionSensorService::class.java)
        stopService(intentMotion)

        val intentPower = Intent(this, ChargerService::class.java)
        stopService(intentPower)

        val intentHeadset = Intent(this, HeadsetService::class.java)
        stopService(intentHeadset)
    }
}