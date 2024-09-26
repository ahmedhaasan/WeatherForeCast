package com.example.weatherforecast.view.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.AlarmApp
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.model.checknetwork.NetworkChangeListener
import com.example.weatherforecast.model.checknetwork.NetworkChangeReceiver
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.remote.RemoteDataSourceContract
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver(), NetworkChangeListener {

    var connected = true
    override fun onReceive(context: Context, intent: Intent?) {

        val alarm = intent?.getSerializableExtra(Constants.WEATHER_ALARM) as AlarmEntity

        var ApiMessage = " the weather in the best Condition"
        CoroutineScope(Dispatchers.IO).launch {
            LocalDataSourceImp(WeatherDataBase.getInstance(context).getWeatherDao()).deleteAlarm(
                alarm
            )


        }
    }


    /**
     *      creating a function that get a weather status once the alarm is trigger
     */
    private suspend fun getAlertWeatherStatusFromApi(
        context: Context,
        alarmItem: AlarmEntity
    ): String? {

        var message: String? = null
        try {
            if (connected) {

                val weatherResponse = RemoteDataSourceImp()
                    .getCurrentWeather(alarmItem.latitude, alarmItem.longitude, "en", "metric")
                if (weatherResponse != null)
                    message = weatherResponse.weather[0].description ?: " "
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return message
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        connected = isConnected
    }
}
