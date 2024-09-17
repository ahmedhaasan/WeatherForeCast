package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentWeather")
data class WeatherEntity(
    @PrimaryKey
    val city: String,
    val temperature: Double,
    val weatherStatus :String,
    val weatherIcon :String,
    val date :Long,
    val lat :Double,
    val lon :Double,
    val windSpeed:Double,
    val pressure :Int,
    val humidity :Int,
    val clouds :Int,
)
