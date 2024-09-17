
package com.example.weatherforecast.model.pojos

import Clouds
import Coord
import Main
import Sys
import Weather
import Wind
import androidx.room.Entity
import androidx.room.PrimaryKey

data class WeatherResponse(
 val id: Int,  // Use the 'id' as the primary key instead of 'coord'
 val coord: Coord,
 val weather: List<Weather>,
 val base: String,
 val main: Main,
 val visibility: Int,
 val wind: Wind,
 val clouds: Clouds,
 val dt: Int,
 val sys: Sys,
 val timezone: Int,
 val name: String,
 val cod: Int
)