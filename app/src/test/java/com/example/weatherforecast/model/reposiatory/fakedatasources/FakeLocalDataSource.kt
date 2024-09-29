package com.example.weatherforecast.model.reposiatory.fakedatasources

import com.example.weatherforecast.model.apistate.AlarmState
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.local.LocalDataSourceContract
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(
    var favorites: MutableList<Favorite>,
    val alarms: MutableList<AlarmEntity>,
    val currentWeather: CurrentWeatherEntity
) : LocalDataSourceContract {

    private var fakeFavorites = favorites
    private var fakeAlarms = alarms
    private var fakeCurrentWeather = currentWeather

    private var fakeHourlyWeatherList = mutableListOf<HourlyWeather>()
    private var fakeDailyWeather = mutableListOf<DailyWeather>()


    /**
     *      insert Functions
     */
    // Insert current weather
    override suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long {
        fakeCurrentWeather = c_weather
        return 1L
    }
    // Insert daily weather
    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        fakeDailyWeather.addAll(d_weather)
        return d_weather.map { 1L } // Simulate insertion return values
    }

    // Insert hourly weather
    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        fakeHourlyWeatherList = h_weather.toMutableList()
        return h_weather.map { 1L } // Simulate insertion return values
    }
    // Insert alarm
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        fakeAlarms.add(alarm)
    }
    // Insert favorite location
    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        fakeFavorites.add(fav_location)
        return 1L
    }

    /**
     *      get Functions
     */
    // Get hourly weather
    override suspend fun getHorlyWeatherLocally(): Flow<List<HourlyWeather>> {
        return flow {
            emit(fakeHourlyWeatherList)
        }
    }
    // For alarms
    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return flow {
            emit(fakeAlarms)
        }
    }

    // For favorites
    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return flow { emit(fakeFavorites) }
    }

    // For current weather
    override suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity> {
        return flow { emit(fakeCurrentWeather) }
    }

    // Get daily weather
    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        return flow {
            emit(fakeDailyWeather)
        }
    }

    /**
     *      delete Functions
     */

    // Delete hourly weather
    override suspend fun deleteHourlyWeather(): Int {
        fakeHourlyWeatherList = mutableListOf()
        return 1
    }

    // Delete current weather
    override suspend fun deleteCurrentWeather(): Int {
        fakeCurrentWeather  // Reset the current weather
        return 1
    }

    // Delete daily weather
    override suspend fun deleteDailyWeatehr(): Int {
        fakeDailyWeather.clear()
        return 1
    }


    // Delete favorite location
    override suspend fun deleteFavoriteLocation(favorite: Favorite): Int {
        fakeFavorites.remove(favorite)
        return 1
    }

    // Delete alarm
    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        fakeAlarms.remove(alarm)
        return 1
    }
}
