package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.remote.RemoteDataSourceImp

class ReposiatoryImp(
    private val remote: RemoteDataSourceImp,
    private val local: LocalDataSourceImp
) : ReposiatoryContract {

    // remotly
    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        unit: String
    ): WeatherResponse? {
        return remote.getCurrentWeather(lat, lon, unit)
    }

    // remotelyy
    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        unit: String
    ): FiveDayResponse? {
        return remote.getFiveDayWeather(lat, lon, unit)
    }

    override suspend fun getCurrentLocalWeather(): CurrentWeatherEntity {
        return local.getCurrentWeather()
    }

    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        return local.insertCurrentWeather(c_weather)
    }


    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        return local.insertHourlyWeatherLocally(h_weather)
    }

    override suspend fun getHourlyWeatherLocally(): List<HourlyWeather> {
        return local.getHorlyWeatherLocally()
    }

    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        return local.insertDailyWeatherLocally(d_weather)
    }

    override suspend fun getDailyWeatherLocally(): List<DailyWeather> {
        return local.getDailyWeatherLocally()
    }

    // delete locally
    override suspend fun deleteCurrentLocalWeather(): Int {
        return local.deleteCurrentWeather()
    }

    override suspend fun deleteHourlyWeather(): Int {
        return local.deleteHourlyWeather()
    }

    override suspend fun deleteDailyWeatehr(): Int {
        return local.deleteDailyWeatehr()
    }
}