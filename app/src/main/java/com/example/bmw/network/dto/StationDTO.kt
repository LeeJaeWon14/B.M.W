package com.example.bmw.network.dto

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
data class StationDTO(
        @PropertyElement(name = "citycode")
        var cityCode: String,

        @PropertyElement(name = "gpslati")
        var latitude: String,

        @PropertyElement(name = "gpslong")
        var longitude: String,

        @PropertyElement(name = "nodeid")
        var nodeId: String,

        @PropertyElement(name = "nodenm")
        var nodeName: String,

        @PropertyElement(name = "nodeno")
        var nodeNumber: String
)