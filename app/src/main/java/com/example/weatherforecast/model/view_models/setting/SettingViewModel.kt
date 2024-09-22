package com.example.weatherforecast.model.view_models.setting

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

    // LiveData for language, unit, wind speed, and location settings
    private val _languageSetting = MutableLiveData<String>()
    val languageSetting: LiveData<String> get() = _languageSetting

    private val _unitSetting = MutableLiveData<String>()
    val unitSetting: LiveData<String> get() = _unitSetting

    private val _windSetting = MutableLiveData<String>()
    val windSetting: LiveData<String> get() = _windSetting

    private val _locationSetting = MutableLiveData<String>()
    val locationSetting: LiveData<String> get() = _locationSetting

    private val _notificationSetting = MutableLiveData<String>()
    val notificationSetting: LiveData<String> get() = _notificationSetting


    init {
        // Load saved preferences initially
        _languageSetting.value = sharedPreferences.getString("LanguagePreference", "en")
        _unitSetting.value = sharedPreferences.getString("UnitPreference", "metric")
        _windSetting.value = sharedPreferences.getString("WindPreference", "m/s")
        _locationSetting.value = sharedPreferences.getString("LocationPreference", "GPS") // Default to GPS
        _notificationSetting.value = sharedPreferences.getString("NotificationPreference", "enabled") // Default to enabled

    }

    // Methods to save preferences and update LiveData
    fun saveLanguagePreference(language: String) {
        sharedPreferences.edit().putString("LanguagePreference", language).apply()
        _languageSetting.value = language
    }

    fun saveUnitPreference(unit: String) {
        sharedPreferences.edit().putString("UnitPreference", unit).apply()
        _unitSetting.value = unit
    }

    fun saveWindPreference(unit: String) {
        sharedPreferences.edit().putString("WindPreference", unit).apply()
        _windSetting.value = unit
    }

    fun saveLocationPreference(location: String) {
        sharedPreferences.edit().putString("LocationPreference", location).apply()
        _locationSetting.value = location
    }
    fun saveNotificationPreference(notification: String) {
        sharedPreferences.edit().putString("NotificationPreference", notification).apply()
        _notificationSetting.value = notification
    }
}
