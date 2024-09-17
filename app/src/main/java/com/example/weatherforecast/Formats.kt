package com.example.weatherforecast

import android.widget.ImageView
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse

fun setIcon(id: String, iv: ImageView) {
    when (id) {
        "01d" -> iv.setImageResource(R.drawable.sun)
        "02d" -> iv.setImageResource(R.drawable._02d)
        "03d" -> iv.setImageResource(R.drawable._03d)
        "04d" -> iv.setImageResource(R.drawable._04n)
        "09d" -> iv.setImageResource(R.drawable._09n)
        "10d" -> iv.setImageResource(R.drawable._10d)
        "11d" -> iv.setImageResource(R.drawable._11d)
        "13d" -> iv.setImageResource(R.drawable._13d)
        "50d" -> iv.setImageResource(R.drawable._50d)
        "01n" -> iv.setImageResource(R.drawable._01n)
        "02n" -> iv.setImageResource(R.drawable._02n)
        "03n" -> iv.setImageResource(R.drawable._03d)
        "04n" -> iv.setImageResource(R.drawable._04n)
        "09n" -> iv.setImageResource(R.drawable._09n)
        "10n" -> iv.setImageResource(R.drawable._10n)
        "11n" -> iv.setImageResource(R.drawable._11d)
        "13n" -> iv.setImageResource(R.drawable._13d)
        "50n" -> iv.setImageResource(R.drawable._50d)
        else -> iv.setImageResource(R.drawable._load)
    }
}


/// here map the current weather format in this function to what i need only
fun mapWeatherResponseToEntity(response: WeatherResponse): CurrentWeatherEntity {
    return CurrentWeatherEntity(
        city = response.name,                          // city name
        temperature = response.main.temp,              // current temperature
        weatherStatus = response.weather[0].main,      // weather status (e.g., Rain)
        weatherIcon = response.weather[0].icon,        // weather icon
        date = response.dt.toLong(),                   // timestamp (dt)
        lat = response.coord.lat,                      // latitude
        lon = response.coord.lon,                      // longitude
        windSpeed = response.wind.speed,               // wind speed
        pressure = response.main.pressure,             // pressure
        humidity = response.main.humidity,             // humidity
        clouds = response.clouds.all,                  // cloudiness percentage
        visibility = response.visibility               // visibility
    )
}

// fun to map data into hourly data
fun mapWeatherResponseToHourly(response: FiveDayResponse): List<HourlyWeatherEntity> {
    return response.list.map { item ->
        HourlyWeatherEntity(
            hour = item.dt,
            icon = item.weather.firstOrNull()?.icon ?: "", // Safe access for icon
            temperature = item.main.temp
        )
    }
}
