package com.example.weatherforecast.view.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FavoriteItemBinding
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather

class FavoriteAdapter(
    private val onItemDeleted: (Favorite) -> Unit,
    private val onItemSelected: (Favorite) -> Unit
) : ListAdapter<Favorite, FavoriteAdapter.FavoriteViewHolder>(FavoriteDeffUtil()) {

    inner class FavoriteViewHolder(val itemBinding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val current: Favorite? = getItem(position)
        if (current != null) {
            holder.itemBinding.locationName.text = current.locationName
            holder.itemBinding.ivRemove.setOnClickListener {
                onItemDeleted.invoke(current) // Call the deletion function
            }
            holder.itemBinding.root.setOnClickListener {
                onItemSelected.invoke(current) // Call the item selection function
            }
        }
    }
}
