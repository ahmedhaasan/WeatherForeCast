package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HourlyWeather")
data class HourlyWeather(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 ,
    val day: Long,
        val icon: String,
        val temperature:Double,
    )

