package com.example.weatherforecast.model.view_models.home

import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 *      here in this exmple i will test two functions
 */
class FakeRepository(
) : ReposiatoryContract {
    private val favorites: MutableList<Favorite> = mutableListOf()

    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        favorites.add(fav_location)
        return 1 // Simulating a successful insertion
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return flowOf(favorites)
    }

    override suspend fun deleteFavoriteLocation(favorite:Favorite):Int {
         favorites.remove(favorite)
        return 1
    }


    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse>? {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<FiveDayResponse>? {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHourlyWeather(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDailyWeatehr(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentLocalWeather(): Int {
        TODO("Not yet implemented")
    }


    override suspend fun insertAlarmLocally(alarm: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        TODO("Not yet implemented")
    }

}