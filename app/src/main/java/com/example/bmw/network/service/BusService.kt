package com.example.bmw.network.service

import com.example.bmw.network.NetworkConstants
import com.example.bmw.network.dto.CityDTO
import com.example.bmw.network.dto.StationDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BusService {
    @GET("${NetworkConstants.SELECT_CITY}}")
    fun getCityList(@Query("serviceKey") path: String) : Call<List<CityDTO>>

    @GET("${NetworkConstants.GET_NEAR_STATION}/{path}")
    fun getNearStation(@Path("path") path: String) : Call<StationDTO>
}