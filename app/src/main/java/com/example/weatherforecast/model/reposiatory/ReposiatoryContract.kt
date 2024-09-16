package com.example.weatherforecast.model.reposiatory

import WeatherResponse

interface ReposiatoryContract {
    suspend fun getCurrentWeatherRemotely(lat :Double,lon:Double,unit:String): WeatherResponse?
    suspend fun getCurrentLocalWeather():WeatherResponse
    suspend fun insertCurrentLocalWeather(c_weather:WeatherResponse):Long
    suspend fun deleteCurrentLocalWeather(c_weather: WeatherResponse):Int
}