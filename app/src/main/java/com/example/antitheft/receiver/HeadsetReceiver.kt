package com.example.antitheft.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.antitheft.service.HeadsetService

class HeadsetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("HeadsetReceiver", "onReceive called")
        if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
            val state = intent.getIntExtra("state", -1)
            Log.d("HeadsetReceiver", "Received headset state: $state")
            val serviceIntent = Intent(context, com.example.antitheft.service.HeadsetService::class.java)
            serviceIntent.putExtra("state", state)
            context?.startService(serviceIntent)
        } else {
            Log.d("HeadsetReceiver", "Received unexpected action: ${intent?.action}")
        }
    }
}