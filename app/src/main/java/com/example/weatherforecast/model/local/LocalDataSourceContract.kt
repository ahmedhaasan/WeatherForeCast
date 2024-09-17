package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.pojos.WeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse


interface LocalDataSourceContract {

    suspend  fun insertCurrentWeather(c_weather: WeatherEntity):Long
    suspend fun deleteCurrentWeather(c_weather: WeatherEntity):Int
    suspend fun getCurrentWeather():WeatherEntity
}