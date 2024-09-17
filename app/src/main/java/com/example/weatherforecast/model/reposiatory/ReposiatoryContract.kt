package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse


interface ReposiatoryContract {
    suspend fun getCurrentWeatherRemotely(lat :Double,lon:Double,unit:String): WeatherResponse?
    suspend fun getCurrentLocalWeather(): CurrentWeatherEntity
    suspend fun insertCurrentLocalWeather(c_weather:CurrentWeatherEntity):Long
    suspend fun deleteCurrentLocalWeather(c_weather: CurrentWeatherEntity):Int
}