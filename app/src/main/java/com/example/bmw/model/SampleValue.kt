package com.example.bmw.model

import com.example.bmw.network.dto.BusDTO
import kotlin.random.Random

object SampleValue {
    fun getSampleList(): List<BusDTO> {
        val list = mutableListOf<BusDTO>()

        for(idx in 1.. Random.nextInt(9)) {
            list.add(BusDTO("BusStation $idx"))
        }
        return list
    }
}