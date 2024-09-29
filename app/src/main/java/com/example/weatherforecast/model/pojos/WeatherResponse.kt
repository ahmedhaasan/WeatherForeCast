
package com.example.weatherforecast.model.pojos

data class WeatherResponse(
 val id: Int,  // Use the 'id' as the primary key instead of 'coord'
 val coord: Coord,
 val weather: List<Weather>,
 val base: String,
 val main: Main,
 val visibility: Int,
 val wind: Wind,
 val clouds: Clouds,
 val dt: Int,  //date
 val sys: Sys,
 val timezone: Int,
 val name: String,
 val cod: Int
)