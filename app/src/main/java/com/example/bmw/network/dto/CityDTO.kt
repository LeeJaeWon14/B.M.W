package com.example.bmw.network.dto

import com.google.gson.annotations.Expose

data class CityDTO(
        @Expose
        var cityCode: Int,
        @Expose
        var cityName: String
)