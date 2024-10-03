package com.example.antitheft.utils

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.antitheft.R
import com.example.antitheft.databinding.ViewCustomPinBinding
import com.example.antitheft.ui.main.MainActivity

class CustomPinView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var pinCode = StringBuilder()
    private lateinit var circleViews: List<View>
    private val maxPinLength = 4
    private lateinit var binding: ViewCustomPinBinding

    fun setBackGroundColor(color: Int) {
        binding.root.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    var onPinEnteredListener: OnPinEnteredListener? = null


    init {
        try {
            binding = ViewCustomPinBinding.inflate(LayoutInflater.from(context), this, true)
            setupView()
        } catch (e: Exception) {
            Log.e("CustomPinView", "Error inflating layout", e)
            e.printStackTrace()
        }
    }

    private fun setupView() {
        circleViews = listOf(
            binding.circle1,
            binding.circle2,
            binding.circle3,
            binding.circle4
        )

        val buttons = listOf(
            binding.btn0,
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9
        )

        for (button in buttons) {
            button.setOnClickListener { onDigitPressed(button.text.toString()) }
        }

        binding.btnDelete.setOnClickListener { onDeletePressed() }
    }

    private fun onDigitPressed(digit: String) {
        if (pinCode.length < maxPinLength) {
            pinCode.append(digit)
            Log.d("CustomPinView", "Digit pressed: $digit, pinCode: $pinCode")
            updatePinDisplay()
            if (pinCode.length == maxPinLength) {
                onPinEntered()
            }
        }
    }

    private fun onDeletePressed() {
        if (pinCode.isNotEmpty()) {
            pinCode.deleteCharAt(pinCode.length - 1)
            updatePinDisplay()
        }
    }

    private fun updatePinDisplay() {
        for (i in circleViews.indices) {
            val circle = circleViews[i]
            if (i < pinCode.length) {
                circle.setBackgroundResource(R.drawable.pin_entered)
            }
            else {
                circle.setBackgroundResource(R.drawable.bg_circle)
            }
        }
    }

    private fun onPinEntered() {
        Log.d("CustomPinView", "On CustomViewPin:  Pin entered: $pinCode")
        if (onPinEnteredListener != null){
            onPinEnteredListener?.onPinEntered(pinCode.toString())
        }
        else {
            Log.e("CustomPinView", "OnPinEnteredListener is null")
        }

    }
}