package com.example.weatherforecast.view.homefragment.hourly

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.HourlyWeather

class HourlyDeffUtil : DiffUtil.ItemCallback<HourlyWeather>() {
    override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return  oldItem == newItem
    }
}