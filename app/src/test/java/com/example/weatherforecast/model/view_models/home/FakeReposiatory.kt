package com.example.weatherforecast.model.view_models.home

import androidx.room.Delete
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
    private lateinit var currentWeather: CurrentWeatherEntity
    private var hourlyWeather: MutableList<HourlyWeather> = mutableListOf()
    private val dailyWeather: MutableList<DailyWeather> = mutableListOf()

    /**
     *      frist part related to test Favorites
     */
    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        favorites.add(fav_location)
        return 1 // Simulating a successful insertion
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return flowOf(favorites)
    }

    override suspend fun deleteFavoriteLocation(favorite: Favorite): Int {
        favorites.remove(favorite)
        return 1
    }


    /**
     *      second part related to test Current , houly , daily
     */

    override suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> {
        return flowOf(currentWeather)
    }

    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        currentWeather = c_weather
        return 1 // memic is inserted
    }

    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        hourlyWeather.addAll(h_weather)
        return listOf(1)
    }

    override suspend fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> {
        return flowOf(hourlyWeather)
    }

    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        dailyWeather.addAll(d_weather)
        return listOf(1)
    }

    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        return flowOf(dailyWeather)
    }

    override suspend fun deleteHourlyWeather(): Int {
        hourlyWeather.clear()
        return 1
    }

    override suspend fun deleteDailyWeatehr(): Int {

        dailyWeather.clear()
        return 1
    }

    override suspend fun deleteCurrentLocalWeather(): Int {

        return 1
    }


    /////////////

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