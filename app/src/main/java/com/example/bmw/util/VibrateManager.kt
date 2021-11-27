package com.example.bmw.util

import android.os.Build

object VibrateManager {
    // os version is higher then Oreo
    fun runVibrate() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

        }
        else {

        }
    }
}