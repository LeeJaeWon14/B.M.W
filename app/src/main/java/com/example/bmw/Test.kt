package com.example.bmw

import android.location.Location

fun main() {
    // Haversine 사용하기

    // 37.7356221217191, 127.04745659966453
    // longitude - 경도, latitude - 위도
    val loc_1 = Location("A")
    loc_1.apply {
        longitude = 37.7356221217191
        latitude = 127.04745659966453
    }
    // 37.7521297103713, 127.05061971774194
    val loc_2 = Location("B")
    loc_2.apply {
        longitude = 37.7521297103713
        latitude = 127.05061971774194
    }

    println("${loc_1.distanceTo(loc_2)}m")
}