package com.example.bmw.network.service

import com.example.bmw.network.NetworkConstants
import com.example.bmw.network.dto.ArriveResponse
import com.example.bmw.network.dto.ServiceResult
import com.example.bmw.network.dto.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BusService {
    @Headers("Connection: close")
    @GET(NetworkConstants.GET_NEAR_STATION)
    fun getNearStation(@Query("serviceKey") serviceKey: String, @Query("gpsLati") latitude: Double, @Query("gpsLong") longitude: Double) : Call<Station>

    @Headers("Connection: close")
    @GET(NetworkConstants.GET_NEAR_STATION_SEOUL)
    fun getNearStationInSeoul(
        @Query("serviceKey") serviceKey: String,
        @Query("tmX") longitude: Double,
        @Query("tmY") latitude: Double,
        @Query("radius") radius: Int = 100 // search area range
    ): Call<ServiceResult>

    @Headers("Connection: close")
    @GET(NetworkConstants.ARRIVE_INFO)
    fun getArriveInfo(@Query("serviceKey") serviceKey: String, @Query("cityCode") cityCode: String, @Query("nodeId") nodeId: String) : Call<ArriveResponse>
}