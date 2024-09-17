package com.example.weatherforecast.model.pojos
data class DailyWeather(
    val day: Long,        // timestamp in seconds
    val icon: String,
    var minTemp: Double,
    var maxTemp: Double
)
