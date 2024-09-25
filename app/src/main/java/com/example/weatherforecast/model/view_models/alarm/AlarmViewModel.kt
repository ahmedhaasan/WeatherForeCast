package com.example.weatherforecast.model.view_models.alarm

import androidx.lifecycle.ViewModel
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AlarmViewModel(val repo : ReposiatoryImp) :ViewModel() {


    /**
     *      start with alarm view model
     */
    private val _alarmsMutableStateFlow: MutableStateFlow<List<AlarmEntity>> =
        MutableStateFlow(emptyList())
    val alarmsStateFlow= _alarmsMutableStateFlow
}