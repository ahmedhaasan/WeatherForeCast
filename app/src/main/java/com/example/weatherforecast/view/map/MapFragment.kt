package com.example.weatherforecast.view.map

import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.model.connection.map.nominatimApi
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.NominatimResponse
import com.example.weatherforecast.model.pojos.Search
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import com.example.weatherforecast.model.view_models.setting.SettingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MapFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var binding: FragmentMapBinding
    private var marker: Marker? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private var cityName: String? = null
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var caller: String

    private lateinit var cityAdapter: CityAdapter
    private val cityList = mutableListOf<Search>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load OSMDroid configuration
        Configuration.getInstance()
            .load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        // Initialize ViewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupSaveLocationButton()
        setupSearchCity()
        addMarkerAtLocation(GeoPoint(29.9792, 31.1342)) // Add initial marker at Giza

        // Observe caller for actions
        settingViewModel.mapCaller.observe(viewLifecycleOwner) { caller ->
            this.caller = caller
        }
    }

    private fun setupMap() {
        binding.mapView.apply {
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(29.9792, 31.1342)) // Default center at Giza
        }

        // Enable map rotation gestures
        val rotationGestureOverlay = RotationGestureOverlay(binding.mapView)
        rotationGestureOverlay.isEnabled = true
        binding.mapView.overlays.add(rotationGestureOverlay)

        // Set map tap listener to add a marker at the tapped location
        addMapTapListener()
    }

    private fun setupSaveLocationButton() {
        binding.btnSaveLocation.setOnClickListener {
            showConfirmationBottomSheet()
        }
    }

    private fun addMapTapListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
                return handleSingleTap(geoPoint)
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        binding.mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun handleSingleTap(geoPoint: GeoPoint?): Boolean {
        Log.d("MapFragment", "Map tapped at: $geoPoint")

        geoPoint?.let {
            addMarkerAtLocation(it)
            lat = it.latitude
            lon = it.longitude
            cityName = getCityName(it.latitude, it.longitude)

            if (cityName != null) {
                Toast.makeText(requireContext(), "City: $cityName", Toast.LENGTH_SHORT).show()
                binding.btnSaveLocation.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    "City not found at this location.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnSaveLocation.visibility = View.GONE
            }
        }
        return true
    }

    private fun addMarkerAtLocation(geoPoint: GeoPoint) {
        marker?.let {
            binding.mapView.overlays.remove(it)
        }

        marker = Marker(binding.mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Selected Location: (${geoPoint.latitude}, ${geoPoint.longitude})"
        }

        marker?.let { binding.mapView.overlays.add(it) }
        binding.mapView.invalidate() // Redraw the map to show the marker
    }

    private fun getCityName(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            addresses?.let {
                if (it.isNotEmpty()) {
                    "${it[0].countryName} / ${it[0].locality ?: it[0].adminArea}"
                } else null
            }
        } catch (e: IOException) {
            Log.e("MapFragment", "Geocoder failed", e)
            null
        }
    }

    private fun showConfirmationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)
        view.findViewById<View>(R.id.btnSure).setOnClickListener {
            if (caller == Constants.SETTINGSCREEN) {
                navigateHome(lat, lon)
            } else if (caller == Constants.FAVORITESCREEN) {
                lat?.let { latValue ->
                    lon?.let { lonValue ->
                        cityName?.let { city ->
                            saveLocationToFavorites(latValue, lonValue, city)
                            Toast.makeText(
                                requireContext(),
                                "Added To Favorite",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            bottomSheetDialog.dismiss()
        }

        view.findViewById<View>(R.id.btnCancel).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun navigateHome(lat: Double?, long: Double?) {
        settingViewModel.saveLocationLatAndLong(lat.toString(), long.toString())
        val action = MapFragmentDirections.actionMapFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun saveLocationToFavorites(lat: Double, lon: Double, cityName: String) {
        viewModel.insertFavoriteLocation(Favorite(lat = lat, lon = lon, locationName = cityName))
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
        binding.mapView.onDetach() // OSMDroid lifecycle management
    }

    // Setup search functionality for cities
    private fun setupSearchCity() {
        cityAdapter = CityAdapter { selectedCity ->
            // Update lat, lon, and cityName when a city is selected
            lat = selectedCity.latitude
            lon = selectedCity.longitude
            cityName = selectedCity.cityName

            // Add marker and animate to selected city
            addMarkerAtLocation(GeoPoint(lat!!, lon!!))
            binding.mapView.controller.animateTo(GeoPoint(lat!!, lon!!))
            binding.recyclerViewCities.visibility = View.GONE // Hide RecyclerView when a city is selected
        }

        binding.recyclerViewCities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cityAdapter
        }

        binding.editTextSearchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    cityList.clear()
                    binding.recyclerViewCities.visibility = View.GONE // Hide RecyclerView if input is empty
                } else {
                    searchLocation(s.toString())
                    binding.recyclerViewCities.visibility = View.VISIBLE // Show RecyclerView when typing
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun searchLocation(cityName: String) {
        nominatimApi.searchLocation(cityName).enqueue(object : Callback<List<NominatimResponse>> {
            override fun onResponse(
                call: Call<List<NominatimResponse>>,
                response: Response<List<NominatimResponse>>
            ) {
                response.body()?.let { result ->
                    Log.d("MapFragment", "Search results: $result") // Log results
                    val cities = result.map { place ->
                        Search(place.display_name, place.lat.toDouble(), place.lon.toDouble())
                    }
                    cityAdapter.submitList(cities) // Submit the new list to the adapter
                } ?: run {
                    Log.e("MapFragment", "No results found")
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NominatimResponse>>, t: Throwable) {
                Log.e("MapFragment", "Search failed: ${t.message}")
                Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}