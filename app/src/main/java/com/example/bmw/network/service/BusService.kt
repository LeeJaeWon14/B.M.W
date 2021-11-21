package com.example.bmw.network.service

import com.example.bmw.network.dto.BusDTO
import retrofit2.Call
import retrofit2.http.GET

interface BusService {
    @GET
    fun getBusInfo(): Call<List<BusDTO>>
}