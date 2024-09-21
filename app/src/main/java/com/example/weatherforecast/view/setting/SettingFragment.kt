package com.example.weatherforecast.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        // Location RadioGroup
        binding.rgLocation.setOnCheckedChangeListener { _, checkedId ->
            val location = when (checkedId) {
                R.id.rb_gps -> getString(R.string.gps)
                R.id.rb_map -> getString(R.string.map)
                else -> ""
            }
            Toast.makeText(requireContext(), "Location set to: $location", Toast.LENGTH_SHORT).show()
        }

        // Wind Speed RadioGroup
        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            val windSpeed = when (checkedId) {
                R.id.rb_meter_second -> getString(R.string.meter_second)
                R.id.rb_mile_hour -> getString(R.string.mile_hour)
                else -> ""
            }
            Toast.makeText(requireContext(), "Wind speed unit: $windSpeed", Toast.LENGTH_SHORT).show()
        }

        // Language RadioGroup
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.rb_english -> getString(R.string.english)
                R.id.rb_arabic -> getString(R.string.arabic)
                else -> ""
            }
            Toast.makeText(requireContext(), "Language set to: $language", Toast.LENGTH_SHORT).show()
        }

        // Notification RadioGroup
        binding.rgNotification.setOnCheckedChangeListener { _, checkedId ->
            val notification = when (checkedId) {
                R.id.rb_enable -> getString(R.string.enable)
                R.id.rb_disable -> getString(R.string.disable)
                else -> ""
            }
            Toast.makeText(requireContext(), "Notifications: $notification", Toast.LENGTH_SHORT).show()
        }

        // Temperature RadioGroup
        binding.rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            val temperatureUnit = when (checkedId) {
                R.id.rb_celsius -> getString(R.string.celsius)
                R.id.rb_kelvin -> getString(R.string.kelvin)
                R.id.rb_fahrenheit -> getString(R.string.fahrenheit)
                else -> ""
            }
            Toast.makeText(requireContext(), "Temperature unit: $temperatureUnit", Toast.LENGTH_SHORT).show()
        }
    }
}
