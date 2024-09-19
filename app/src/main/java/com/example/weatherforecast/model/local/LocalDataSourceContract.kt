package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather


interface LocalDataSourceContract {

    suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long
    suspend fun getCurrentWeather(): CurrentWeatherEntity

    // daily and hourly
    suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long>
    suspend fun getHorlyWeatherLocally(): List<HourlyWeather>
    suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long>
    suspend fun getDailyWeatherLocally(): List<DailyWeather>

    // delete locally
    suspend fun deleteHourlyWeather(): Int

    suspend fun deleteCurrentWeather(): Int
    suspend fun deleteDailyWeatehr(): Int


}