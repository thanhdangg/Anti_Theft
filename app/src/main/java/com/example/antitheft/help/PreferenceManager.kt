package com.example.antitheft.help

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_PIN_CODE = "pin_code"
    private const val KEY_IS_PIN_SET = "is_pin_set"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePinCode(context: Context, pinCode: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_PIN_CODE, pinCode)
        editor.putBoolean(KEY_IS_PIN_SET, true)
        editor.apply()
    }

    fun isPinSet(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_PIN_SET, false)
    }

    fun getPinCode(context: Context): String? {
        return getPreferences(context).getString(KEY_PIN_CODE, null)
    }
}