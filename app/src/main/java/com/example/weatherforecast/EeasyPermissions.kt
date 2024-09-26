package com.example.weatherforecast

import android.Manifest
import android.app.Activity
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

/**
 *        //  easy permissions it is important  ( * Note*)
 */
 fun checkNotificationPermission(context: Context): Boolean {
    return EasyPermissions.hasPermissions(context, Manifest.permission.POST_NOTIFICATIONS)
}

 fun requestNotificationPermission(activity: Activity) {
    EasyPermissions.requestPermissions(
        activity,
        "We need your permission to show notifications",
        Constants.REQUEST_NOTIFICATION_PERMISSION_CODE,
        Manifest.permission.POST_NOTIFICATIONS
    )
}