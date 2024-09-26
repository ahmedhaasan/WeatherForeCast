package com.example.weatherforecast.view.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.view.MainActivity

object NotificationManager {

    private var channelIsCreated = false

    fun createNotificationChannel(context: Context) {    // step 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.CHANNEL_NAME
            val description = Constants.CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            channelIsCreated = true

        }
    }

    fun createPendingIntent(context: Context): PendingIntent {  // step 2 create pending intent
        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent
    }


    fun createNotification(   // step 3
        context: Context,
        description: String,
        zoneName: String
    ): NotificationCompat.Builder {
        val pendingIntent = createPendingIntent(context)  // from step 2
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.img)
            .setContentTitle(Constants.WEATHER_STATUS)
            .setContentText("$description in $zoneName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$description in $zoneName")
            )
            .setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        return builder
    }

}