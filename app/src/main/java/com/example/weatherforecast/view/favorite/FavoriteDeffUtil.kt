package com.example.weatherforecast.view.favorite

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.Favorite

class FavoriteDeffUtil : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }
}