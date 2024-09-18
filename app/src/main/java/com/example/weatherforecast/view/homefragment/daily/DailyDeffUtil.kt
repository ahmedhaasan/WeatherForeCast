package com.example.weatherforecast.view.homefragment.daily

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.DailyWeather

class DailyDeffUtil :DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return  oldItem == newItem
    }
}