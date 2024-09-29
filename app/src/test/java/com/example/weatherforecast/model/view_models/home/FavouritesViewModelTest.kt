package com.example.weatherforecast.model.view_models.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 *      starting  to test ViewModel
 */
@RunWith(AndroidJUnit4::class)
class FavouritesViewModelTest {


    // declare the repo and the viewModel
    // Givin
    lateinit var reposiatory: ReposiatoryContract
    lateinit var favoriteViewModel: FavoriteViewModel


    // Creating three instances of Favorite
    val favorite1 = Favorite(locationName = "New York", lat = 40.7128, lon = -74.0060)
    val favorite2 = Favorite(locationName = "Los Angeles", lat = 34.0522, lon = -118.2437)
    val favorite3 = Favorite(locationName = "Tokyo", lat = 35.6762, lon = 139.6503)
    val favorite4 = Favorite(locationName = "Paris", lat = 48.8566, lon = 2.3522)

    // now setUp the instances
    @Before
    fun setUp() {
        reposiatory = FakeRepository()
        favoriteViewModel = FavoriteViewModel(reposiatory)
    }


    @Test
    fun addFavoritePlace_passArgument_gotFavorites() = runTest {

        // when
        favoriteViewModel.insertFavoriteLocation(favorite1)
        delay(100)
        // then
        val favouritePlaces = reposiatory.getAllFavoriteLocations().first()
        assertTrue(favouritePlaces.contains(favorite1))
        // another way
        Assert.assertEquals(favorite1, favouritePlaces.get(0))

    }

    @Test
    fun deleteFavorite_noArgument_gotDeleted() = runTest {

        // when
        favoriteViewModel.deleteFavoriteLocation(favorite1)

        // then
        val favouritePlaces = reposiatory.getAllFavoriteLocations().first()
        assertTrue(favouritePlaces.isEmpty())
    }

    @Test
    fun getAllFavorites_insertSome_gotAllFavorites() = runTest {
        // Arrange
        reposiatory.insertFavoriteLocation(favorite1)
        reposiatory.insertFavoriteLocation(favorite2)
        reposiatory.insertFavoriteLocation(favorite3)
        reposiatory.insertFavoriteLocation(favorite4)

        // Act
        favoriteViewModel.getAllFavotiteLccations()

        // this is the exepectd Result i've put
        val expectedFavorites = reposiatory.getAllFavoriteLocations().first()

        // here im collecting the fristValue got by viewModel once it success
        val flowValue = favoriteViewModel.favoriteState.first { it is FavoriteRoomState.Success }

        // Assert
        if (flowValue is FavoriteRoomState.Success) {
            assertEquals(flowValue.favorites, expectedFavorites)
        } else {
            fail("Expected a successful state but got something else")
        }
    }



}


