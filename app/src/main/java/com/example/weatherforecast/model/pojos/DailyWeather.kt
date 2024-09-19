package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DailyWeather")
data class DailyWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,             // Auto-incrementing primary key
    val day: Long,                // timestamp in seconds
    val icon: String,
    var minTemp: Double,
    var maxTemp: Double,
    val weatherStatus: String    // Add this field for weather condition description
)
