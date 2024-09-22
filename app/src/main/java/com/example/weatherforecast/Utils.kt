package com.example.weatherforecast

import android.content.Context
import java.util.Locale

/**
 *      this is the function that change the language for the all application
 */
object LocaleHelper {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}