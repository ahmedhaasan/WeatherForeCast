package com.example.weatherforecast.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather

@Database(entities = arrayOf(CurrentWeatherEntity::class,HourlyWeather::class,DailyWeather::class,Favorite::class), version = 1 ,exportSchema = false)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {
            return instance ?: synchronized(this) {
                val tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDataBase::class.java,
                    "CurrentWeather"
                ).build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}
