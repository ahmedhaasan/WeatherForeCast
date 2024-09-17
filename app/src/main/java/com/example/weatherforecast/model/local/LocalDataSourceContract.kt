package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity


interface LocalDataSourceContract {

    suspend  fun insertCurrentWeather(c_weather: CurrentWeatherEntity):Long
    suspend fun deleteCurrentWeather(c_weather: CurrentWeatherEntity):Int
    suspend fun getCurrentWeather():CurrentWeatherEntity
}