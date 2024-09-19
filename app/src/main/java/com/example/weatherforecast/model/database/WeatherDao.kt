package com.example.weatherforecast.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather

@Dao
interface WeatherDao {
    @Query("SELECT * FROM currentWeather")
    suspend fun getCurrentWeather(): CurrentWeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long

    @Query("DELETE FROM currentWeather")
    suspend fun deleteCurrentWeather(): Int  // Correct table for deletion

    // Insert hourly weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long>  // Return List<Long>

    // Get hourly weather
    @Query("SELECT * FROM HourlyWeather")
    suspend fun getHourlyWeatherLocally(): List<HourlyWeather>  // Fixed typo

    @Query("DELETE FROM HourlyWeather")
    suspend fun deleteHourlyWeather(): Int

    // Insert daily weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long>  // Return List<Long>

    // Get daily weather
    @Query("SELECT * FROM DailyWeather")
    suspend fun getDailyWeatherLocally(): List<DailyWeather>

    @Query("DELETE FROM DailyWeather")
    suspend fun deleteDailyWeather(): Int  // Fixed typo in method name
}
