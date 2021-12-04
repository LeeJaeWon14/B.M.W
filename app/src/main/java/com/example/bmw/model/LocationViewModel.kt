package com.example.bmw.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    val location: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val cityCode: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    var longitude: Double = 0.0
    var latitude: Double = 0.0
}