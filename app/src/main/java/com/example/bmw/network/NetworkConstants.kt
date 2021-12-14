package com.example.bmw.network

import java.net.URLDecoder

object NetworkConstants {
    const val BASE_URL = "http://openapi.tago.go.kr/openapi/service/"
    const val SELECT_CITY = "BusSttnInfoInqireService/getCtyCodeList"
    const val GET_NEAR_STATION = "BusSttnInfoInqireService/getCrdntPrxmtSttnList"

    // 서울
    const val BASE_URL_SEOUL = "http://ws.bus.go.kr/api/rest/stationinfo/"
    const val GET_NEAR_STATION_SEOUL = "getStationByPos"

    // UTF-8 decoding
    val BUS_STATION_SERVICE_KEY = URLDecoder.decode("jspmgHMBl7j%2B2aGeg6jhdY1fuCXppVqcaqwA4iVoTlUaYS0hH3H0vNhVxWaDzdWbjDWd%2FXPZ6DgnGdSygJ%2Fa%2Bg%3D%3D", "UTF-8")
}