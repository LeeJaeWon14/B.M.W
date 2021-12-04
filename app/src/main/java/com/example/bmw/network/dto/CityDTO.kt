package com.example.bmw.network.dto

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class CityDTO(
        @PropertyElement(name= "citycode")
        var cityCode: Int,
        @PropertyElement(name= "cityname")
        var cityName: String
)