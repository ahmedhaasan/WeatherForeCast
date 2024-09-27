package com.example.weatherforecast.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity

import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM currentWeather")
    fun getCurrentWeather(): Flow<CurrentWeatherEntity>  // we remove suspeded and use flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long

    @Query("DELETE FROM currentWeather")
    suspend fun deleteCurrentWeather(): Int

    // Insert hourly weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long>

    // Get hourly weather
    @Query("SELECT * FROM HourlyWeather")
    fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> // we remove suspeded and use flow

    @Query("DELETE FROM HourlyWeather")
    suspend fun deleteHourlyWeather(): Int

    // Insert daily weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long>

    // Get daily weather
    @Query("SELECT * FROM DailyWeather")
    fun getDailyWeatherLocally(): Flow<List<DailyWeather>> // we remove suspeded and use flow

    @Query("DELETE FROM DailyWeather")
    suspend fun deleteDailyWeather(): Int

    /**
     *      working on favorite
     */
    // Insert favorite location
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(fav_location: Favorite): Long

    // Delete favorite location by ID
    @Query("DELETE FROM FAVORITELOCATIONS WHERE locationName = :fav_id")
    suspend fun deleteFavoriteLocation(fav_id: String)

    // Get all favorite locations as Flow
    @Query("SELECT * FROM FAVORITELOCATIONS")
    fun getAllFavoriteLocations(): Flow<List<Favorite>>


    /**
     *    // functions for alarm
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarm_table WHERE id = :alarm_id")
    suspend fun deleteAlarm(alarm_id: Int):Int

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}
