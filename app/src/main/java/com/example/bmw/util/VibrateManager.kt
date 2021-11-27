package com.example.bmw.util

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object VibrateManager {
    const val REPEAT = 0
    const val NOT_REPEAT = -1

    fun runVibrate(vibrator: Vibrator) {
        // os version is higher then Oreo
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(200)
        }
        else {
            val effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }

    // repeat is 0 and not repeat is -1
    fun runVibrate(vibrator: Vibrator, pattern: LongArray, repeat: Int) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(pattern, repeat)
        }
        else {
            val effect = VibrationEffect.createWaveform(pattern, repeat)
            vibrator.vibrate(effect)
        }
    }
}