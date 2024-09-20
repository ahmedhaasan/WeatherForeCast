package com.example.weatherforecast.view.map

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import com.example.weatherforecast.model.view_models.home.WeatherViewModel
import com.example.weatherforecast.model.view_models.home.WeatherViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.util.Locale


class MapFragment : Fragment() {
    lateinit var viewModel: FavoriteViewModel

    lateinit var binding: FragmentMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load OSMDroid configuration
        Configuration.getInstance()
            .load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        // intialize viewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set default map controls and zoom using binding
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(15.0)

//        // Retrieve latitude and longitude from arguments or default to Eiffel Tower
//        val latitude = arguments?.getDouble("latitude") ?: 44.34
//        val longitude = arguments?.getDouble("longitude") ?: 10.99

        // Center map on the specified location
        val geoPoint = GeoPoint(29.9792, 31.1342)
        binding.mapView.controller.setCenter(geoPoint)

        // Enable rotation gestures
        val rotationGestureOverlay = RotationGestureOverlay(binding.mapView)
        rotationGestureOverlay.isEnabled = true
        binding.mapView.overlays.add(rotationGestureOverlay)

        // Set map tap listener to add a marker at the tapped location
        addMapTapListener()

        // Add an initial marker at the default location
        addMarkerAtLocation(geoPoint)
    }

    private fun addMapTapListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
                geoPoint?.let {
                    // Remove all previous markers
                    binding.mapView.overlays.removeAll { overlay -> overlay is Marker }
                    addMarkerAtLocation(it)
                    // Get latitude and longitude from the tapped location (geoPoint)
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // Handle the lat and long as needed (e.g., pass it to another function)
                    onLocationSelected(latitude, longitude)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {  // any action  i need
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        binding.mapView.overlays.add(mapEventsOverlay)
    }

    private fun addMarkerAtLocation(geoPoint: GeoPoint) {
        val marker = Marker(binding.mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Selected Location: (${geoPoint.latitude}, ${geoPoint.longitude})"
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate() // Redraw the map to show the marker
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume() // OSMDroid lifecycle management
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause() // OSMDroid lifecycle management
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDetach() // Detach the map to free up resources
    }


    fun onLocationSelected(lat: Double, lon: Double) {
        // Use Coroutine to handle Geocoder task in the background
        CoroutineScope(Dispatchers.IO).launch {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())

            try {
                // Reverse geocode the latitude and longitude into an address
                val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)

                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address: Address = addresses[0]
                        val cityName: String? = address.locality

                        // Switch to the main thread to update UI
                        withContext(Dispatchers.Main) {
                            if (cityName != null) {
                                Toast.makeText(
                                    requireContext(),
                                    "City: $cityName",
                                    Toast.LENGTH_SHORT
                                ).show()

                                viewModel.insertFavoriteLocation(Favorite(lat = lat,lon = lon, locationName = cityName))
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "City not found at this location.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Unable to get city name.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}

