package com.example.weatherforecast.view.homefragment.hourly

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather

class HourlyDeffUtil : DiffUtil.ItemCallback<HourlyWeather>() {
    override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return  oldItem == newItem
    }
}