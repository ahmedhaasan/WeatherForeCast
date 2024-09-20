package com.example.weatherforecast.model.view_models.home

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
            val tempWeather =  repo.getCurrentWeatherRemotely(lat, lon, unit)
            val temp2 = tempWeather?.let { mapWeatherResponseToEntity(it) }
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(temp2)
            }
            temp2?.let { repo.insertCurrentLocalWeather(it) }
        }
    }


    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyWeatherRemotly(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon, unit)
                if (fiveDayResponse != null) {
                    val hourlyWeather: List<HourlyWeather> = mapHourlyWeatherForToday(fiveDayResponse)

                    // Clear the DB before adding new data
                    repo.deleteHourlyWeather()

                    // Insert new hourly weather data
                    repo.insertHourlyWeatherLocally(hourlyWeather)

                    // Update the UI on the main thread
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyWeatherRemotly(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon, unit)
                if (fiveDayResponse != null) {
                    val tempDailyWeather: List<DailyWeather> = mapDailyWeather(fiveDayResponse)

                    // Clear the DB before adding new data
                    repo.deleteDailyWeatehr()

                    // Insert new daily weather data
                    repo.insertDailyWeatherLocally(tempDailyWeather)

                    // Update the UI on the main thread
                    withContext(Dispatchers.Main) {
                        _dailyWeather.postValue(tempDailyWeather)
                    }
                } else {
                    Log.i(Constants.ERROR, "No data received from getFiveDayWeather")
                }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching daily weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    // get the currentWeatehrLocally
    fun getCurrentWeatherLocally() {
        viewModelScope.launch(Dispatchers.IO) {
             repo.getCurrentLocalWeather().collect{
                 currentWeather -> _currentWeather.postValue(currentWeather)
             }
        }
    }

    fun  getHourlyWeatherLocally(){
        viewModelScope.launch( Dispatchers.IO) {
            repo.getHourlyWeatherLocally().collect{
                hourlyWeather -> _hourlyWeather.postValue(hourlyWeather)
            }
        }
    }

    fun getDailyWeatehrLocally(){

        viewModelScope.launch(Dispatchers.IO) {
             repo.getDailyWeatherLocally().collect{
                 dailyWeather -> _dailyWeather.postValue(dailyWeather)
             }

        }
    }

/*    // insert weather in Db
    fun insertCurrentWeatherLocally(c_weather: CurrentWeatherEntity): Long? {
        var result: Long? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.insertCurrentLocalWeather(c_weather)

        }
        return result
    }


    // delete current Weather locally

    fun deleteCurrentWeatherLocally(): Int? {
        var result: Int? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.deleteCurrentLocalWeather()

        }
        return result
    }*/

}