package com.example.weatherforecast.model.pojos
import com.google.gson.annotations.SerializedName


/**
 *  these are some classes used in Api call
 */
data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int? = null,
    val grndLevel: Int? = null,
    val temp_kf: Double
)


data class Wind(
    val speed: Double,
    val deg: Int? = null,
    val gust: Double? = null
)

data class Clouds(
    val all: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)
data class Sys2 (
    val pod: String // Day or night indicator
)


// used in FiveDayResponse
data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int
)


data class NominatimResponse(
    val display_name: String,
    val lat: String,
    val lon: String
)

data class Search(
    val cityName: String,
    val latitude: Double,
    val longitude: Double){}

data class NominatimResponseList(
    val places: List<NominatimResponse>
)

// used in fiveDayResponse
data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val sys: Sys2,
    val dt_txt: String,
    val pop:Double?,
    val rain:Rain,
)
data class Rain(
    @SerializedName("3h") val volume: Double? = null // Rain volume for the last 3 hours
)
