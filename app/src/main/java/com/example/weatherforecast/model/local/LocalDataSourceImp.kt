package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.database.WeatherDao
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.Flow


// start in local weather data
class LocalDataSourceImp(val dao: WeatherDao) : LocalDataSourceContract {
    override suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long {
        return dao.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentWeather(): Int {
        return dao.deleteCurrentWeather()
    }

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity> {
        return dao.getCurrentWeather()
    }

    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        return dao.insertHourlyWeatherLocally(h_weather)
    }

    override suspend fun getHorlyWeatherLocally(): Flow<List<HourlyWeather>> {
        return dao.getHourlyWeatherLocally()
    }

    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        return dao.insertDailyWeatherLocally(d_weather)
    }

    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        return dao.getDailyWeatherLocally()
    }

    override suspend fun deleteHourlyWeather(): Int {
        return dao.deleteHourlyWeather()
    }

    override suspend fun deleteDailyWeatehr(): Int {
        return dao.deleteDailyWeather()
    }

    /**
     *      working on favorite
     */
    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        return dao.insertFavoriteLocation(fav_location)
    }

    override     suspend fun deleteFavoriteLocation(favorite:Favorite):Int{
        return dao.deleteFavoriteLocation(favorite)
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return dao.getAllFavoriteLocations()
    }


    /**
     *      functons from alarm
     */
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        dao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity):Int {
        return dao.deleteAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {

        return dao.getAllAlarms()
    }
}