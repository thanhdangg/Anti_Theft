package com.example.antitheft.ui.main

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.antitheft.R
import com.example.antitheft.databinding.ActivityMainBinding
import com.example.antitheft.help.PreferenceManager
import com.example.antitheft.receiver.HeadsetReceiver
import com.example.antitheft.service.ChargerService
import com.example.antitheft.service.MotionSensorService
import com.example.antitheft.ui.headset.ActivityHeadset
import com.example.antitheft.ui.motion.ActivityMotion
import com.example.antitheft.ui.pocket.ActivityPocket
import com.example.antitheft.ui.power.ActivityPower
import com.example.antitheft.ui.setting.ActivitySetting
import com.example.antitheft.utils.OnPinEnteredListener

class MainActivity : AppCompatActivity(), OnPinEnteredListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var headsetReceiver: HeadsetReceiver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.pinView.onPinEnteredListener = this

        if (!PreferenceManager.isPinSet(this)) {
            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            "Set Pin to Activate Anti-Theft".also { tvTitle.text = it }
            binding.pinView.visibility = View.VISIBLE
        }

//        val intent = Intent(this, ChargerService::class.java)
//        startService(intent)
        // Register the HeadsetReceiver dynamically
        headsetReceiver = HeadsetReceiver()
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headsetReceiver, filter)

        binding.motionDetection.setOnClickListener {
            startActivity(Intent(this, ActivityMotion::class.java))
        }
        binding.pocketDetection.setOnClickListener {
            startActivity(Intent(this, ActivityPocket::class.java))
        }
        binding.headsetDetection.setOnClickListener {
            startActivity(Intent(this, ActivityHeadset::class.java))
        }
        binding.powerDetection.setOnClickListener {
            startActivity(Intent(this, ActivityPower::class.java))
        }
        binding.setting.setOnClickListener {
            startActivity(Intent(this, ActivitySetting::class.java))
        }

    }
    override fun onPinEntered(pinCode: String) {
        PreferenceManager.savePinCode(this, pinCode)
        binding.pinView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver
        unregisterReceiver(headsetReceiver)
    }
}