package com.example.weatherforecast.model.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Constants
import com.example.weatherforecast.mapDailyWeather
import com.example.weatherforecast.mapHourlyWeatherForToday
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather

import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repo: ReposiatoryImp) : ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWeatherEntity?>()
    val currentWeather = _currentWeather

    private val _dailyWeather = MutableLiveData<List<DailyWeather>>()
    val dailyWeather = _dailyWeather

    private val _hourlyWeather = MutableLiveData<List<HourlyWeather>>()
    val hourlyWeather = _hourlyWeather
    // create functions

    /**
     *  take care you will use the courotines here with the viewModelLifeScope
     */

    fun getCurrentWeatherRemotly(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempWeather = repo.getCurrentWeatherRemotely(lat, lon, unit)
            val temp2 = tempWeather?.let { mapWeatherResponseToEntity(it) } // map what i need here to my response
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(temp2)
            }
        }
    }

    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyWeather(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon, unit)
                if (fiveDayResponse != null) {
                    val hourlyWeather: List<HourlyWeather> = mapHourlyWeatherForToday(fiveDayResponse)
                    // Update the hourly data on the main thread
                    withContext(Dispatchers.Main) {
                        _hourlyWeather.postValue(hourlyWeather)
                    }
                } else {
                    Log.i(Constants.ERROR, "No data received from getFiveDayWeather")
                }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching hourly weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyWeather(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon, unit)
                if (fiveDayResponse != null) {
                    val tempDailyWeather: List<DailyWeather> = mapDailyWeather(fiveDayResponse)
                    // Update the hourly data on the main thread
                    withContext(Dispatchers.Main) {
                        _dailyWeather.postValue(tempDailyWeather)
                    }
                } else {
                    Log.i(Constants.ERROR, "No data received from getFiveDayWeather")
                }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching hourly weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }



    // get the currentWeatehrLocally
    fun getCurrentWeatherLocally(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempWeather = repo.getCurrentLocalWeather()
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(tempWeather)
            }
        }
    }

    // insert weather in Db
    fun insertCurrentWeatherLocally(c_weather: CurrentWeatherEntity): Long? {
        var result: Long? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.insertCurrentLocalWeather(c_weather)

        }
        return result
    }

    // delete current Weather locally

    fun deleteCurrentWeatherLocally(c_weather: CurrentWeatherEntity): Int? {
        var result: Int? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.deleteCurrentLocalWeather(c_weather)

        }
        return result
    }

}