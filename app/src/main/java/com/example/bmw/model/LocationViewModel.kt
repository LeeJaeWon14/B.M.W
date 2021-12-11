package com.example.bmw.model

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bmw.network.dto.StationDTO

class LocationViewModel : ViewModel() {
    val address: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val cityCode: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }

    val stationList: MutableLiveData<List<StationDTO>> by lazy {
        MutableLiveData<List<StationDTO>>()
    }
}