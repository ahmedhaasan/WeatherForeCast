package com.example.weatherforecast.model.connection.map

import com.example.weatherforecast.model.pojos.NominatimResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): Call<List<NominatimResponse>>
}