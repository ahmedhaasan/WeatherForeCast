package com.example.weatherforecast.view.alert

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.model.checknetwork.NetworkChangeListener
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
        Log.d("TestDelete", "alarm id befor deleting: $alarm.id")

        var defaultMessage = " the weather in the best Condition"

        CoroutineScope(Dispatchers.IO).launch {
            val dao = WeatherDataBase.getInstance(context).getWeatherDao()
            val rowsDeleted = dao.deleteAlarm(alarm) // Use a valid ID
            Log.d("TestDelete", "Rows deleted: $rowsDeleted")


            val apiMessage = getAlertWeatherStatusFromApi(context, alarm)
            if (!apiMessage.isNullOrBlank()) {
                defaultMessage = apiMessage
            }

            withContext(Dispatchers.Main) {
                when (alarm.type) {
                    Constants.NOTIFICATION -> {
                        createNotification(context, defaultMessage, alarm.zoneName)
                    }

                    Constants.ALERT -> {
                        createAlertDialog(context, defaultMessage, alarm.zoneName)
                    }
                }
            }

        }
    }

    /**
     *      create a dialog
     */
    private fun createAlertDialog(context: Context, defaultMessage: String, zoneName: String) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.alarm_dialog, null)
        val weatherStatus = dialogView.findViewById<TextView>(R.id.tv_alarm_status)
        val weatherCity = dialogView.findViewById<TextView>(R.id.tv_alarm_zone_name)
        val dismissButton = dialogView.findViewById<Button>(R.id.dismiss_alarm_button)

        weatherCity.text = zoneName
        weatherStatus.text = defaultMessage   // link data with attributes in dialg
                                                // define the media to play
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarmsound)

        val builder = AlertDialog.Builder(context, R.style.Base_Theme_WeatherForeCast)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.setCancelable(true)

        dialog.setOnShowListener {     // listen when the alarm is shown an play music
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }
        val window = dialog.window
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        dialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(15000)
                withContext(Dispatchers.Main) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                }
            } finally {
                cancel()
            }
        }

        dismissButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            mediaPlayer.stop()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
        }

    }

    /**'
     *      create a notification
     */
    @SuppressLint("MissingPermission")
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
