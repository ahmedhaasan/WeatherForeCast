package com.example.weatherforecast.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherforecast.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    // declare binding
    lateinit var fav_binding:FragmentFavoriteBinding
    lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fav_binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        return fav_binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // intialize the favorite Adapter
        favoriteAdapter = FavoriteAdapter(emptyList()){

            // implementation of removing from favorite here
        }
    }


}