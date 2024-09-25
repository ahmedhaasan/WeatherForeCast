package com.example.weatherforecast.model.view_models.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.home.WeatherViewModel

class WeatherViewModelFactory(val repo : ReposiatoryImp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            AlarmViewModel(repo) as T
        }else{
            throw IllegalArgumentException("Look at factory something went wring")
        }
    }
}