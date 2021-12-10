package com.example.bmw.network

import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit

object RetroClient {
    private var instance: Retrofit? = null
    fun getInstance() : Retrofit {
        instance?.let {
            return it
        } ?: run {
            instance = Retrofit.Builder()
                    .baseUrl(NetworkConstants.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(TikXmlConverterFactory.create())
                    .build()
            return instance!!
        }
    }
}