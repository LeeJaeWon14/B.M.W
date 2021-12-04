package com.example.bmw.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StationDTO(
        @Expose
        @SerializedName("gpslati")
        var latitude : String
)