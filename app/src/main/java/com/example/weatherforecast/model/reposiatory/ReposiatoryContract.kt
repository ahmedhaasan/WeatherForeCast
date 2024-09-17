package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.pojos.WeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse


interface ReposiatoryContract {
    suspend fun getCurrentWeatherRemotely(lat :Double,lon:Double,unit:String): WeatherResponse?
    suspend fun getCurrentLocalWeather(): WeatherEntity
    suspend fun insertCurrentLocalWeather(c_weather:WeatherEntity):Long
    suspend fun deleteCurrentLocalWeather(c_weather: WeatherEntity):Int
}