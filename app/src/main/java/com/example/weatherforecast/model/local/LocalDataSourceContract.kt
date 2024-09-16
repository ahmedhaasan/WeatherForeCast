package com.example.weatherforecast.model.local

import WeatherResponse

interface LocalDataSourceContract {

    suspend  fun insertCurrentWeather(c_weather:WeatherResponse):Long
    suspend fun deleteCurrentWeather(c_weather: WeatherResponse):Int
    suspend fun getCurrentWeather():WeatherResponse
}