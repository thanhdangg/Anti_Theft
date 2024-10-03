package com.example.antitheft.ui.motion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.antitheft.R
import com.example.antitheft.databinding.ActivityMainBinding
import com.example.antitheft.databinding.ActivityMotionBinding
import com.example.antitheft.help.PreferenceManager
import com.example.antitheft.service.MotionSensorService
import com.example.antitheft.ui.main.MainActivity
import com.example.antitheft.utils.CustomPinView
import com.example.antitheft.utils.OnPinEnteredListener

class ActivityMotion : AppCompatActivity(), OnPinEnteredListener {
    private lateinit var binding: ActivityMotionBinding
    private lateinit var correctPasscode : String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMotionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correctPasscode = PreferenceManager.getPinCode(this).toString()

        binding.pinView.onPinEnteredListener = this

//        binding.pinView.setBackGroundColor(R.color.status_red)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Enter Pin to Deactivate Alarm"

        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivMotion.visibility = View.GONE
        }, 3000)
    }
    override fun onPinEntered(pinCode: String) {
        Log.d("CustomPinView", "On ActivityMotion: Pin entered: $pinCode")
        if (pinCode == correctPasscode) {

            startService(Intent(this, MotionSensorService::class.java))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(this, "Incorrect Pin", Toast.LENGTH_SHORT).show()
        }
    }
}