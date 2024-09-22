package com.example.weatherforecast

import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
        weatherStatus = response.weather[0].description,      // weather status (e.g., Rain)
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





/*@RequiresApi(Build.VERSION_CODES.O)
fun mapHourlyWeatherForToday(response: FiveDayResponse): List<HourlyWeatherEntity> {
    // Get the current date in the system default timezone
    val currentDate = LocalDate.now(ZoneId.systemDefault())
    println("Current Date: $currentDate") // Debugging output

    return response.list
        .map { item ->
            // Convert timestamp to LocalDate
            val forecastDate = Instant.ofEpochSecond(item.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            println("Forecast Date: $forecastDate, Timestamp: ${item.dt}") // Debugging output

            // Map each item to HourlyWeatherEntity
            HourlyWeatherEntity(
                hour = item.dt, // timestamp in seconds
                icon = item.weather[0].icon,
                temperature = item.main.temp
            ) to forecastDate // Return as a pair
        }
        .filter { (_, forecastDate) ->
            // Filter to only include items for the current date
            forecastDate == currentDate
        }
        .map { (hourlyWeatherEntity, _) ->
            hourlyWeatherEntity
        }
}*/



/*
@RequiresApi(Build.VERSION_CODES.O)
fun mapHourlyWeatherForToday(response: FiveDayResponse): List<HourlyWeatherEntity> {
    // Get the current date in the timezone
    val currentDate = LocalDate.now(ZoneId.systemDefault())
    println("Current Date: $currentDate") // Debugging output

    return response.list
        .filter { item ->
            // Convert timestamp to LocalDate
            val forecastDate = Instant.ofEpochSecond(item.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            println("Forecast Date: $forecastDate, Timestamp: ${item.dt}") // Debugging output


            // Check if the forecast date matches the current date
            forecastDate == currentDate
        }
        .map { item ->
            HourlyWeatherEntity(
                hour = item.dt, // timestamp in seconds
                icon = item.weather[0].icon,
                temperature = item.main.temp
            )
        }
}
*/

@RequiresApi(Build.VERSION_CODES.O)
fun mapHourlyWeatherForToday(response: FiveDayResponse): List<HourlyWeather> {
    // Get the current date in the timezone
    val currentDate = LocalDate.now(ZoneId.systemDefault())

    // Find hourly data for today
    val hourlyDataForToday = response.list.filter { item ->
        val forecastDate = Instant.ofEpochSecond(item.dt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        forecastDate == currentDate
    }

    // Check if there is data for today
    if (hourlyDataForToday.isEmpty()) {
        Log.i(Constants.ERROR, "No hourly data available for today")
        // Handle the case where there is no data for today
        // For example, you could return a default message or use the next day's data
        // return empty list or default data
    }

    return hourlyDataForToday.map { item ->
        HourlyWeather(
            day = item.dt, // timestamp in seconds
            icon = item.weather[0].icon,
            temperature = item.main.temp
        )
    }
}




@RequiresApi(Build.VERSION_CODES.O)
fun mapDailyWeather(response: FiveDayResponse): List<DailyWeather> {
    val dailyMap = mutableMapOf<LocalDate, DailyWeather>()

    response.list.forEach { item ->
        val timestamp = item.dt
        val date = Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val temperature = item.main.temp
        val icon = item.weather[0].icon
        val weatherStatus = item.weather[0].description // Assuming description provides weather status

        if (!dailyMap.containsKey(date)) {
            dailyMap[date] = DailyWeather(
                day = timestamp,
                icon = icon,
                minTemp = temperature,
                maxTemp = temperature,
                weatherStatus = weatherStatus // Set weather status for the first data point of the day
            )
        } else {
            val dailyWeather = dailyMap[date]!!
            dailyWeather.minTemp = minOf(dailyWeather.minTemp, temperature)
            dailyWeather.maxTemp = maxOf(dailyWeather.maxTemp, temperature)
            // Update weatherStatus only if it's the first data point or if you want to track the most common status
        }
    }

    return dailyMap.values.toList()
}
