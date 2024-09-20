package com.example.weatherforecast.model.view_models.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *      this is the viwModel for Favorites
 */
class FavoriteViewModel (val repo :ReposiatoryImp) : ViewModel(){

    private val _favorites = MutableLiveData<List<Favorite>>()
    val favorite = _favorites

    fun insertFavoriteLocation(fa_location :Favorite){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavoriteLocation(fa_location)

        }
    }

    fun deleteFavoriteLocation(fav_id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteLocation(fav_id)

        }
    }

    fun getAllFavotiteLccations(){
        // collect the favorites from the Flow as the function return a flow
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations().collect{ favorites -> _favorites.postValue(favorites)}
        }

    }
}