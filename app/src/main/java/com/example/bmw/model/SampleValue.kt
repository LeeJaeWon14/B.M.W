package com.example.bmw.model

import com.example.bmw.network.dto.BusDTO
import java.util.Random

object SampleValue {
    fun getSampleList(): List<BusDTO> {
        val list = mutableListOf<BusDTO>()

        for(idx in 1 until Random().nextInt(7)) {
            list.add(BusDTO("BusStation $idx"))
        }
        return list
    }
}