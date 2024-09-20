package com.example.weatherforecast.view.favorite

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import androidx.navigation.Navigation;
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.model.pojos.Favorite
import com.google.android.material.snackbar.Snackbar


class FavoriteFragment : Fragment() {

    // declare binding
    lateinit var fav_binding: FragmentFavoriteBinding
    lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fav_binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return fav_binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creating instance form viewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        // now view Model
        val viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
        // intialize the favorite Adapter
        favoriteAdapter = FavoriteAdapter(
            onItemDeleted = { place ->
                // Create an AlertDialog to confirm deletion
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete Favorite")
                    setMessage("Are you sure you want to delete the location ${place.locationName}?")
                    setPositiveButton("Yes") { _, _ ->
                        // If user confirms, delete the location
                        viewModel.deleteFavoriteLocation(place.locationName)
                        Snackbar.make(
                            requireView(),
                            "Deleted Location ${place.locationName}",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("Undo") {
                                viewModel.insertFavoriteLocation(place)
                            }
                            show()
                        }
                    }
                    setNegativeButton("No", null) // Just dismiss the dialog
                    create().show() // Show the dialog
                }
            },  // when city is selected
            onItemSelected = { place ->
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment()
                findNavController().navigate(action)
                Log.d("FavoriteFragment", "Selected item: ${place.locationName}")
            }
        )



        fav_binding.recyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
            }
        // observe on favorites locally and pass them to the reciclerView
        viewModel.getAllFavotiteLccations()
        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->
            if (favorites != null) {
                favoriteAdapter.submitList(favorites)
                favoriteAdapter.notifyDataSetChanged() // Add this after submitList()

                Log.d("FavoriteFragment", "Favorites list updated: $favorites")
            } else {
                Log.d("FavoriteFragment", "No favorites found.")
            }
        })

        /**
         *          now action when press on floating button to select favorite llocation
         */

        fav_binding.favoriteFabButton.setOnClickListener {
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment()

            findNavController().navigate(action)

        }
    }


}