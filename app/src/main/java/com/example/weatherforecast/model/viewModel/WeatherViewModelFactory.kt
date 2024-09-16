package com.example.weatherforecast.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp

class WeatherViewModelFactory(val repo : ReposiatoryImp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            WeatherViewModel(repo) as T
        }else{
            throw IllegalArgumentException("Look at factory something went wring")
        }
    }
}