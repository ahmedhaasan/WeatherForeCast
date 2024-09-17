package com.example.weatherforecast.view

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
import java.time.Instant
import java.time.ZoneId

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        val repo =ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))

        // 3 create instance of factory
        val factory = WeatherViewModelFactory(repo)
        // 4 create view model
        val viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        // fetch the current weather remotly
        viewModel.getCurrentWeatherRemotly(30.013056,31.208853,Constants.METRIC_UNIT)
        // Observe changes to the Weather list
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
            if (weather != null) {

                binding.tvLocationName.text = weather.name.toString()
                val dateTime = Instant.ofEpochSecond(weather.dt.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                binding.tvCurrentDate.text = dateTime.toString()
                binding.tvWeatherStatus.text = weather.weather[0].main
                binding.tvCurrentDegree.text = weather.main.temp.toString()
                /**
                 * // calling a method to change the icon based on the code
                 * exists in file ((formats ))
                 */
                setIcon(weather.weather[0].icon, binding.ivWeatherIcon)



            } else {
                Toast.makeText(requireContext(), "No products available", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
