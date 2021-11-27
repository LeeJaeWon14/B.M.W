package com.example.bmw.util

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object VibrateManager {
    // os version is higher then Oreo
    fun runVibrate(vibrator: Vibrator) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(200)
        }
        else {
            val effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }

    fun runVibrate(vibrator: Vibrator, pattern: LongArray) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            vibrator.vibrate(200)
        }
        else {
            val effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)

        }
    }
}