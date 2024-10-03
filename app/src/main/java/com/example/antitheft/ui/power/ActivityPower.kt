package com.example.antitheft.ui.power

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.antitheft.R
import com.example.antitheft.databinding.ActivityPowerBinding
import com.example.antitheft.help.PreferenceManager
import com.example.antitheft.service.ChargerService
import com.example.antitheft.utils.OnPinEnteredListener

class ActivityPower : AppCompatActivity(), OnPinEnteredListener {
    private lateinit var binding: ActivityPowerBinding
    private lateinit var correctPasscode : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPowerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correctPasscode = PreferenceManager.getPinCode(this).toString()

        binding.pinView.onPinEnteredListener = this

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Enter Pin to Deactivate Alarm"

        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivPower.visibility = View.GONE
        }, 3000)
    }
    override fun onPinEntered(pinCode: String) {
        if (pinCode == correctPasscode) {
            // put isAlarmTriggered to false to Intern

            val intent = Intent(this, ChargerService::class.java)
            intent.putExtra("isAlarmTriggered", false)
            startService(intent)
            finish()
        }
        else {
            Toast.makeText(this, "Incorrect Pin", Toast.LENGTH_SHORT).show()
        }

    }
}