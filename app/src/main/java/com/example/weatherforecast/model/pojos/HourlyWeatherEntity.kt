package com.example.weatherforecast.model.pojos

data class HourlyWeatherEntity(
        val hour: Long,
        val icon: String,
        val temperature:Double,
    )

