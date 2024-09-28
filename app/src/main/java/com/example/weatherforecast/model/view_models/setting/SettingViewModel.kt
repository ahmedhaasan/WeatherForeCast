package com.example.weatherforecast.model.view_models.setting

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.Constants

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

    private val _notificationSetting = MutableLiveData<String>()
    val notificationSetting: LiveData<String> get() = _notificationSetting
    //


    private val _mapCaller = MutableLiveData<String>()
    val mapCaller: LiveData<String> get() = _mapCaller

    /**
     *  needed to listen for location change
     */
    private val _locationSetting = MutableLiveData<String>()
    val locationSetting: LiveData<String> get() = _locationSetting
    private val _locationFlag = MutableLiveData<String>()
    val locationFlag: LiveData<String> get() = _locationFlag
    private val _latitue = MutableLiveData<String>()
    val latitue: LiveData<String> get() = _latitue
    private val _longitute = MutableLiveData<String>()
    val longitute: LiveData<String> get() = _longitute




    init {
        // Load saved preferences initially
        _languageSetting.value = sharedPreferences.getString("LanguagePreference", "en")
        _unitSetting.value = sharedPreferences.getString("UnitPreference", "metric")
        _windSetting.value = sharedPreferences.getString("WindPreference", "mile_hour")
        _notificationSetting.value = sharedPreferences.getString("NotificationPreference", "enabled") // Default to enabled
        _locationSetting.value = sharedPreferences.getString("LocationPreference", Constants.GPS) // Default to GPS
        _locationFlag.value = sharedPreferences.getString(Constants.LOCATIONFLAG, "0") // Default to GPS
        _latitue.value = sharedPreferences.getString(Constants.LATITUTE,"31.2769423") // Default to GPS
        _longitute.value = sharedPreferences.getString(Constants.LONGITUTE, "29.9626961") // Default to GPS
        _mapCaller.value = sharedPreferences.getString(Constants.MapCaller,Constants.FAVORITESCREEN) // set default caller is setting

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
    fun saveLocationFlag(flag: String) {
        sharedPreferences.edit().putString("LocationFlag", flag).apply()
        _locationFlag.value = flag
    }

    fun saveLocationLatAndLong(lat:String,long:String){
        sharedPreferences.edit().putString(Constants.LATITUTE, lat).apply()
        _latitue.value = lat
        sharedPreferences.edit().putString(Constants.LONGITUTE, long).apply()
        _longitute.value = long

    }
    fun saveNotificationPreference(notification: String) {
        sharedPreferences.edit().putString("NotificationPreference", notification).apply()
        _notificationSetting.value = notification
    }

    //
    fun saveMapCallerPrefrence(caller :String) {
        sharedPreferences.edit().putString(Constants.MapCaller, caller).apply()
        _mapCaller.value = caller
    }
}
