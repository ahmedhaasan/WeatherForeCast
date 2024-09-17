package com.example.weatherforecast.model.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Constants
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.mapWeatherResponseToHourly
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.HourlyWeatherEntity

import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repo: ReposiatoryImp) : ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWeatherEntity?>()
    val currentWeather = _currentWeather

    private val _hourlyWeather = MutableLiveData<List<HourlyWeatherEntity>>()
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

    // get the dataHOurly
    fun getHourlyWeather(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                    val fiveDayResponse = repo.getFiveDayWeather(lat, lon, unit)
                    if (fiveDayResponse != null) {
                        val hourlyWeather: List<HourlyWeatherEntity> = mapWeatherResponseToHourly(fiveDayResponse)
                        // update the hourly data
                        withContext(Dispatchers.Main){
                            _hourlyWeather.postValue(hourlyWeather)
                        }
                    } else {
                        Log.i(Constants.ERROR,"check the getHourly IN view Model")
                    }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "check the getHourly IN view Model$e")
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