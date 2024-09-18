package com.example.weatherforecast.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.HourlyWeather

@Dao
interface WeatherDao {
    @Query("SELECT * FROM currentWeather")
    suspend fun getCurrentWeather(): CurrentWeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long

    @Delete
    suspend fun deleteCurrentWeather(c_weather: CurrentWeatherEntity): Int

    // insert hourly
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(h_weather: HourlyWeather): Long

    // get hourly weather
    @Query("SELECT * FROM HourlyWeather")
    suspend fun getHorlyWeather(): HourlyWeather
}
