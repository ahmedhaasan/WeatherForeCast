package com.example.weatherforecast.model.view_models.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp

/**
 *      viewModel factory for favorite we use because we will use viewModel and pass it a value in the constructor
 */
class FavoriteViewModelFactory (val repo :ReposiatoryImp):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(repo) as T
        }else
            throw IllegalArgumentException("Look at factory something went wring")

    }
}