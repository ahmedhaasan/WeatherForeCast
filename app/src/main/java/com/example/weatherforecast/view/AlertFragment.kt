package com.example.weatherforecast.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.model.view_models.setting.SettingViewModel

class AlertFragment : Fragment() {


    //
    lateinit var settingViewModel: SettingViewModel

    lateinit var binding : FragmentAlertBinding

    lateinit var notificationStatus :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)

        settingViewModel.notificationSetting.observe(viewLifecycleOwner, Observer {  notificationStatus ->
            this.notificationStatus = notificationStatus


        })


        binding.addAlarmButton.setOnClickListener{

        }

        binding.alertRecycler

    }

}