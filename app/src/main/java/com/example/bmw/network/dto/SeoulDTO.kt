package com.example.bmw.network.dto

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ServiceResult")
data class ServiceResult(
    @Element(name = "msgHeader")
    var msgHeader: MsgHeader,
    @Element(name = "msgBody")
    var msgBody: MsgBody
)

@Xml(name = "msgHeader")
data class MsgHeader(
    @PropertyElement
    var headerCd: Int,
    @PropertyElement
    var headerMsg: String,
    @PropertyElement
    var itemCount: Int
)

@Xml(name = "msgBody")
data class MsgBody(
    @Element(name = "itemList")
    var itemList: List<SeoulDTO>
)

@Xml
data class SeoulDTO(
    @PropertyElement
    var arsId: Int,
    @PropertyElement
    var dist: Int,
    @PropertyElement
    var gpsX: Double,
    @PropertyElement
    var gpsY: Double,
    @PropertyElement
    var posX: Double,
    @PropertyElement
    var posY: Double,
    @PropertyElement
    var stationId: Int,
    @PropertyElement
    var stationNm: String,
    @PropertyElement
    var stationTp: Int
)