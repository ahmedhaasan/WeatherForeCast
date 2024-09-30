package com.example.weatherforecast.view.map

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.Search

class CityDeffUtill : DiffUtil.ItemCallback<Search>() {
    override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
        return oldItem == newItem
    }
}