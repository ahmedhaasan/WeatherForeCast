package com.example.weatherforecast.model.pojos

import City
import WeatherItem


data class FiveDayResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)