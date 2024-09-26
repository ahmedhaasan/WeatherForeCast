package com.example.weatherforecast.view.alert
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.Constants
import com.example.weatherforecast.model.checknetwork.NetworkChangeListener
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver(), NetworkChangeListener {

    var connected = true
    override fun onReceive(context: Context, intent: Intent?) {
        val alarm = intent?.getSerializableExtra(Constants.WEATHER_ALARM) as? AlarmEntity
        if (alarm == null) {
            Log.e("AlarmReceiver", "AlarmEntity is null")
            return
        }
        var defaultMessage = " the weather in the best Condition"
        CoroutineScope(Dispatchers.IO).launch {
            LocalDataSourceImp(WeatherDataBase.getInstance(context).getWeatherDao()).deleteAlarm(alarm)

            val apiMessage = getAlertWeatherStatusFromApi(context,alarm)
            if (!apiMessage.isNullOrBlank()) {
                defaultMessage = apiMessage
            }

            withContext(Dispatchers.Main){
                when (alarm.type){
                    Constants.NOTIFICATION -> {
                        createNotification(context,defaultMessage,alarm.zoneName)
                    }
                    Constants.ALERT -> {
                        createAlertDialog(context,defaultMessage,alarm.zoneName)
                    }
                }
            }

        }
    }

    /**
     *      create a dialog
     */
    private fun createAlertDialog(context: Context, defaultMessage: String, zoneName: String) {

        Toast.makeText(context,"this is the dialog demo message :$defaultMessage in : $zoneName",Toast.LENGTH_SHORT).show()

    }

    /**'
     *      create a notification
     */
    private fun createNotification(context: Context, defaultMessage: String, zoneName: String) {
        val builder = NotificationManager.createNotification(context, defaultMessage, zoneName)
        with(NotificationManagerCompat.from(context)) {
            notify(Constants.NOTIFICATION_ID, builder.build())  // create the notification no
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
