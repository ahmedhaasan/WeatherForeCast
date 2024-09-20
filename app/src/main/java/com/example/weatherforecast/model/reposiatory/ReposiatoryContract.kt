package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface ReposiatoryContract {
    suspend fun getCurrentWeatherRemotely(lat :Double,lon:Double,unit:String): WeatherResponse?
    suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> // use flow when get data remotely
    suspend fun insertCurrentLocalWeather(c_weather:CurrentWeatherEntity):Long
    suspend fun getFiveDayWeather(lat :Double , lon :Double , unit :String): FiveDayResponse?

    // daily and hourly
    suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long>
    suspend fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> // use flow
    suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long>
    suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>>

    // delete locally
    // delete locally
    suspend fun deleteHourlyWeather(): Int
    suspend fun deleteDailyWeatehr(): Int
    suspend fun deleteCurrentLocalWeather():Int

    /**
     *      working on favorites
     */

    suspend fun insertFavoriteLocation(fav_location: Favorite): Long
    suspend fun deleteFavoriteLocation(fav_id: Int)
    fun getAllFavoriteLocations(): Flow<List<Favorite>>


}