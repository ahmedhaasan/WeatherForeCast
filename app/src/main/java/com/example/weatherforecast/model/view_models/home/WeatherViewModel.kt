package com.example.weatherforecast.model.view_models.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Constants
import com.example.weatherforecast.mapDailyWeather
import com.example.weatherforecast.mapHourlyWeatherForTwoDays
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.model.apistate.DailyApiState
import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract

import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *      applaying stateFlow Here instead of liveData
 */
class WeatherViewModel(private val repo: ReposiatoryContract) : ViewModel() {
    // intialState For current Weather is loading
    private val _currentWeatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading())
    val currentWeatherState = _currentWeatherState

    private val _dailyWeatherState = MutableStateFlow<DailyApiState>(DailyApiState.Loading())
    val dailyWeatherState = _dailyWeatherState

    private val _hourlyWeatherState = MutableStateFlow<HourlyApiState>(HourlyApiState.Loading())
    val hourlyWeatherState = _hourlyWeatherState
    // create functions

    /**
     *  take care you will use the courotines here with the viewModelLifeScope
     */

    fun getCurrentWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {


            repo.getCurrentWeatherRemotely(lat, lon, lang, unit)
                ?.catch { e -> _currentWeatherState.value = WeatherApiState.Failure(e) }
                // Take the CurrentWeather From The Response
                ?.map { weatherResponse -> mapWeatherResponseToEntity(weatherResponse) }
                ?.collect { weather ->
                    _currentWeatherState.value = WeatherApiState.Success(weather)
                    repo.insertCurrentLocalWeather(weather) // the insert the new one
                }
        }
    }


    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear the DB before adding new data
                repo.deleteHourlyWeather()
                repo.getFiveDayWeather(lat, lon, lang, unit)
                    ?.catch { error -> _hourlyWeatherState.value = HourlyApiState.Failure(error) }
                    ?.map { fiveDaily -> // map the five Daily into Hourly
                        mapHourlyWeatherForTwoDays(fiveDaily)
                    }?.collect { hourly ->
                        _hourlyWeatherState.value = HourlyApiState.Success(hourly)
                        repo.insertHourlyWeatherLocally(hourly) // then add the new one
                    }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching hourly weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     *      get Daily weather
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear the DB before adding new data
                val deletion = repo.deleteDailyWeatehr()
                Log.i("Deletion","items deleted : $deletion ")
                repo.getFiveDayWeather(lat, lon, lang, unit)
                    ?.catch { error -> _dailyWeatherState.value = DailyApiState.Failure(error) }
                    ?.map { fiveDaily -> mapDailyWeather(fiveDaily) }
                    ?.collect { daily ->
                        _dailyWeatherState.value = DailyApiState.Success(daily)
                       val insertion=  repo.insertDailyWeatherLocally(daily) // then add the new one
                        Log.i("Deletion","items inserted : $insertion ")

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
            repo.getCurrentLocalWeather()
                .catch { error ->
                    _currentWeatherState.value = WeatherApiState.Failure(error)
                }
                .collect { currentWeather ->
                    if (currentWeather != null) {
                        _currentWeatherState.value = WeatherApiState.Success(currentWeather)
                    } else {
                        _currentWeatherState.value =
                            WeatherApiState.Failure(Throwable("Current weather data is null"))
                    }
                }
        }
    }

    fun getHourlyWeatherLocally() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getHourlyWeatherLocally()
                .catch { error -> _hourlyWeatherState.value = HourlyApiState.Failure(error) }
                .collect { hourlyWeather ->
                    _hourlyWeatherState.value = HourlyApiState.Success(hourlyWeather)
                }
        }
    }

    fun getDailyWeatehrLocally() {

        viewModelScope.launch(Dispatchers.IO) {
            repo.getDailyWeatherLocally()
                .catch { error -> _dailyWeatherState.value = DailyApiState.Failure(error) }
                .collect { dailyWeather ->
                    _dailyWeatherState.value = DailyApiState.Success(dailyWeather)
                }

        }
    }


}