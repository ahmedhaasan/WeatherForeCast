package com.example.weatherforecast.model.connection.map

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit = Retrofit.Builder()
    .baseUrl("https://nominatim.openstreetmap.org/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val nominatimApi = retrofit.create(NominatimApi::class.java)