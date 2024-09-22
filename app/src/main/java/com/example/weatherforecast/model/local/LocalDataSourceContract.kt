package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.Flow


interface LocalDataSourceContract {

    suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long
    suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity>

    // daily and hourly
    suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long>
    suspend fun getHorlyWeatherLocally(): Flow<List<HourlyWeather>>
    suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long>
    suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>>

    // delete locally
    suspend fun deleteHourlyWeather(): Int

    suspend fun deleteCurrentWeather(): Int
    suspend fun deleteDailyWeatehr(): Int

    /**
     *  working on favorite
     */

    suspend fun insertFavoriteLocation(fav_location: Favorite): Long
    suspend fun deleteFavoriteLocation(fav_id: String)
    suspend fun getAllFavoriteLocations(): Flow<List<Favorite>>



}