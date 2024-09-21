package com.example.weatherforecast.view.map

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import java.util.*

class MapFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var binding: FragmentMapBinding
    private var marker: Marker? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load OSMDroid configuration
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        // Initialize ViewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
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
        setupSaveLocationButton()  // get city name and lat and long

        // Add an initial marker at the default location (Giza coordinates)
        addMarkerAtLocation(GeoPoint(29.9792, 31.1342))
    }

    private fun setupMap() {
        binding.mapView.apply {
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(29.9792, 31.1342))  // Default center at Giza
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
                geoPoint?.let {
                    addMarkerAtLocation(it)
                    lat = it.latitude
                    lon = it.longitude
                    cityName = getCityName(it.latitude, it.longitude)

                    cityName?.let { name ->
                        Toast.makeText(requireContext(), "City: $name", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(requireContext(), "City not found at this location.", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        binding.mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
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
        } catch (e: Exception) {
            null
        }
    }

    private fun showConfirmationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<View>(R.id.btnSure).setOnClickListener {
            lat?.let { latValue ->
                lon?.let { lonValue ->
                    cityName?.let { city ->
                        saveLocationToFavorites(latValue, lonValue, city)
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
}
