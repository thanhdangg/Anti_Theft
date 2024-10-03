package com.example.antitheft.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.antitheft.service.ChargerService

class PowerDisconnectReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_POWER_DISCONNECTED == intent?.action) {
            // Handle the unplugged event here
            Toast.makeText(context, "Charger unplugged!", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(context, ChargerService::class.java)
            serviceIntent.putExtra("isAlarmTriggered", true)
            context?.startService(serviceIntent)
        }
    }

}
