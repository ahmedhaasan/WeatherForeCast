package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface ReposiatoryContract {

    /**
     *  applaiying flow and state Flow
     */
    suspend fun getCurrentWeatherRemotely(lat :Double,lon:Double,lang:String,unit:String): Flow<WeatherResponse>?
    suspend fun getFiveDayWeather(lat :Double , lon :Double ,lang: String, unit :String): Flow<FiveDayResponse>?
    suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> // use flow when get data remotely
    suspend fun insertCurrentLocalWeather(c_weather:CurrentWeatherEntity):Long

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
    suspend fun deleteFavoriteLocation(fav_id: String)
    suspend fun getAllFavoriteLocations(): Flow<List<Favorite>>

    /**
     *      lets start with alarm
     */
    suspend fun insertAlarmLocally(alarm: AlarmEntity)
    suspend fun deleteAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>



    }