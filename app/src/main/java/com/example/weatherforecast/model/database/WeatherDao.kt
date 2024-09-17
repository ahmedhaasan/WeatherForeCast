package com.example.weatherforecast.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.pojos.WeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse

@Dao
interface WeatherDao {
    @Query("SELECT * FROM currentWeather")
    suspend fun getCurrentWeather(): WeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(c_weather: WeatherEntity): Long

    @Delete
    suspend fun deleteCurrentWeather(c_weather: WeatherEntity): Int
}
