package com.example.weatherforecast.view.favorite

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.checknetwork.NetworkChangeListener
import com.example.weatherforecast.model.checknetwork.NetworkChangeReceiver
import com.example.weatherforecast.model.view_models.home.WeatherViewModel
import com.example.weatherforecast.model.view_models.home.WeatherViewModelFactory
import com.example.weatherforecast.model.view_models.setting.SettingViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), NetworkChangeListener {

    // declare binding
    lateinit var fav_binding: FragmentFavoriteBinding
    lateinit var favoriteAdapter: FavoriteAdapter
    var isConnected: Boolean? = null
    lateinit var settingViewModel: SettingViewModel

    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    private var isFabVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // register the BroadCast here to listen for network Changes
        networkChangeReceiver = NetworkChangeReceiver(this)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireActivity().registerReceiver(networkChangeReceiver, intentFilter)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("FavoriteFragment", "onCreateView called")
        fav_binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return fav_binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("FavoriteFragment", "onViewCreated called")

        super.onViewCreated(view, savedInstanceState)
        // creating instance form viewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        // now view Model
        val favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)
        isFabVisible = true // Set initial visibility state


        /**
         *      intialize home viw model to use when updating the data
         */
        val factory2 = WeatherViewModelFactory(repo)
        val homeViewModel = ViewModelProvider(this, factory2).get(WeatherViewModel::class.java)
        // intialize the favorite Adapter
        favoriteAdapter = FavoriteAdapter(
            onItemDeleted = { place ->
                // Create an AlertDialog to confirm deletion
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.deleteFavoriteDialog))
                    setMessage("${getString(R.string.areYouSureDeleteLocation)}${place.locationName}?")
                    setPositiveButton("Yes") { _, _ ->
                        // If user confirms, delete the location
                        favoriteViewModel.deleteFavoriteLocation(place)
                        Snackbar.make(
                            requireView(),
                            "${getString(R.string.deleteFavoriteDialog)}${place.locationName}",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction(getString(R.string.undo)) {
                                favoriteViewModel.insertFavoriteLocation(place)
                            }
                            show()
                        }
                    }
                    setNegativeButton("No", null) // Just dismiss the dialog
                    create().show() // Show the dialog
                }
            },  // when city is selected
            onItemSelected = { place ->
                if (isConnected == true) {
                    val fav_home = Fav_Home()  // im calling this to hide the fab when show details of favorite
                    fav_home.hideFB(fav_binding.favoriteFabButton)
                    // Create a Bundle and put the necessary data
                    val bundle = Bundle()
                    bundle.putDouble("lat", place.lat)
                    bundle.putDouble("lon", place.lon)
                    // Set the arguments for the fragment
                    fav_home.arguments = bundle

                    // Replace the fragment and add it to the back stack
                    childFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainer, fav_home)
                        .addToBackStack(null)
                        .commit()

                } else {
                    showNetworkDialog(requireContext())
                }
            }


        )



        fav_binding.favoriteRecycler.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        // observe on favorites locally and pass them to the reciclerView
        favoriteViewModel.getAllFavotiteLccations()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.favoriteState.collect { favorites ->
                    when (favorites) {
                        is FavoriteRoomState.Loading -> {
                            fav_binding.favoriteRecycler.visibility = View.GONE
                            fav_binding.lvNoFavourites.visibility = View.GONE
                            // Optionally show a loading indicator here
                        }

                        is FavoriteRoomState.Success -> {
                            if (favorites.favorites.isEmpty()) {
                                // Show no favorites view
                                fav_binding.lvNoFavourites.visibility = View.VISIBLE
                                fav_binding.favoriteRecycler.visibility = View.GONE
                            } else {
                                // Show the recycler view
                                fav_binding.lvNoFavourites.visibility = View.GONE
                                fav_binding.favoriteRecycler.visibility = View.VISIBLE
                                favoriteAdapter.submitList(favorites.favorites)
                            }
                        }

                        is FavoriteRoomState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load data: ${favorites.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is FavoriteRoomState.Empty -> {
                            // Show no favorites view
                            fav_binding.lvNoFavourites.visibility = View.VISIBLE
                            fav_binding.favoriteRecycler.visibility = View.GONE
                        }
                    }
                }
            }
        }


        /**
         *          now action when press on floating button to select favorite llocation
         */

        fav_binding.favoriteFabButton.setOnClickListener {
            settingViewModel.saveMapCallerPrefrence(Constants.FAVORITESCREEN) // who is caller the map
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment()
            findNavController().navigate(action)

        }
    }



    override fun onResume() {
        super.onResume()
        Log.d("FavoriteFragment", "onResume called, showing FAB")
      //  fav_binding.favoriteFabButton.visibility = View.VISIBLE

    }


    override fun onNetworkChanged(isConnected: Boolean) {
        this.isConnected = isConnected
    }


    fun showNetworkDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.network_equired))
        builder.setMessage(getString(R.string.connect_to_network))

        // Add buttons
        builder.setPositiveButton(R.string.network_equired) { dialog, _ ->
            // Open network settings
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            // Dismiss the dialog
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }


}