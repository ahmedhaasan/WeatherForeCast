package com.example.weatherforecast.view.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.SearchItemBinding
import com.example.weatherforecast.model.pojos.Search

class CityAdapter(
    private val onCityClick: (Search) -> Unit
) : ListAdapter<Search, CityAdapter.CityViewHolder>(CityDeffUtill()) {

    class CityViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchItemBinding.inflate(inflater, parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = getItem(position)
        holder.binding.tvZoneName.text = city.cityName

        holder.itemView.setOnClickListener {
            onCityClick(city) // Trigger click listener
        }
    }
}
