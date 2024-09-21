package com.example.weatherforecast.model.connection

import com.example.weatherforecast.Constants
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface   WeatherApiService {

    // Define the GET request to retrieve the 5-day weather forecast
    @GET("data/2.5/forecast")
    suspend fun getFiveDayWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String ,
        @Query("units") units: String,
        @Query("appid") apiKey:  String = Constants.API_KEY
    ): Response<FiveDayResponse>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String ,
        @Query("units") units: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): Response<WeatherResponse>

}
