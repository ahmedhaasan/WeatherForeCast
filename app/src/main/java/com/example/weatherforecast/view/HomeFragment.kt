package com.example.weatherforecast.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.Constants
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_model.WeatherViewModel
import com.example.weatherforecast.model.view_model.WeatherViewModelFactory
import com.example.weatherforecast.setIcon
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**
         *  frist we need an instance of view model class to deal with
         */

        // 1- create db instance
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        // 2 create instance of reposiatory
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))

        // 3 create instance of factory
        val factory = WeatherViewModelFactory(repo)
        // 4 create view model
        val viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        // fetch the current weather remotly
        viewModel.getCurrentWeatherRemotly(30.013056, 31.208853, Constants.METRIC_UNIT)
        // Observe changes to the Weather list
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
            if (weather != null) {

                // Formatting for temperature, pressure, etc.
                val decimalFormat =
                    DecimalFormat("#.##") // For formatting numbers with two decimal places

                binding.tvLocationName.text = weather.city

                val dateTime = Instant.ofEpochSecond(weather.date.toLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM yyyy", Locale.ENGLISH)
                binding.tvCurrentDate.text = dateTime.format(dateFormatter)
                binding.tvWeatherStatus.text = weather.weatherStatus
                binding.tvCurrentDegree.text = "${decimalFormat.format(weather.temperature)}°C"
                setIcon(weather.weatherIcon, binding.ivWeatherIcon)
                binding.presser.text =
                    "${decimalFormat.format(weather.pressure)} hPa"  // Format pressure ( "1000 hPa")
                binding.humidity.text =
                    "${weather.humidity}%"  // Humidity in percentage ("77%")
                binding.wind.text
                "${decimalFormat.format(weather.windSpeed)} m/s"  // Wind speed (e.g., "2.06 m/s")
                binding.clouds.text =
                    "${weather.clouds}%"  // Cloud coverage in percentage ("0%")
                binding.visability.text =
                    "${decimalFormat.format(weather.visibility / 1000)} km"  // Visibility in kilometers ( "6.0 km")
            } else {
                Toast.makeText(
                    requireContext(),
                    "No currentWeatherAvilable available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

}
