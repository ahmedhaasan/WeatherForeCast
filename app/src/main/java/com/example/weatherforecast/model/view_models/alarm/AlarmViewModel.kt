package com.example.weatherforecast.model.view_models.alarm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.view.alert.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AlarmViewModel(val repo : ReposiatoryImp) :ViewModel() {


    /**
     *      start with alarm view model
     */
    private val _alarmsStateFlow = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val alarmsStateFlow: StateFlow<List<AlarmEntity>> = _alarmsStateFlow


    private val _currentWeatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading())
    val currentWeatherState = _currentWeatherState

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

    fun insertAlarmLocally( alarm : AlarmEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlarmLocally(alarm)
        }
    }

    fun deleteAlarmLocally( alarm : AlarmEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlarm(alarm)

        }
    }

    fun getAllAlarmsLocally(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlarms().collect{ alarms ->
                Log.d("ViewModel", "Alarms from DB: $alarms")

                _alarmsStateFlow.value = alarms
            }
        }
    }
}