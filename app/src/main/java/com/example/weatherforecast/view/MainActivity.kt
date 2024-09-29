package com.example.weatherforecast.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecast.LocaleHelper.setLocale
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Declare the view binding object
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ActionBarDrawerToggle
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout, // Use binding to access the DrawerLayout
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up NavController and NavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment // Use binding to access navHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(
            binding.navView,
            navController
        ) // Use binding to access NavigationView
        // Set custom icon for home (hamburger menu)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)
        // Update the ActionBar title based on the menu item's title
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menuItem = binding.navView.menu.findItem(destination.id)
            supportActionBar?.title = menuItem?.title ?: destination.label // Set ActionBar title
        }
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.rounded_action_bar))
    }



    // Handle item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the selection to the drawer toggle
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // when start the application again so reset the last app language
    override fun onStart() {
        val savedLanguage = sharedPreferences.getString("LanguagePreference", "en")
        super.onStart()
        if (savedLanguage != null) {
            setLocale(this, savedLanguage)
            binding.navView.menu.clear() // Clear the current menu
            binding.navView.inflateMenu(R.menu.new_menu) // Reinflate the menu
            // Update the ActionBar title based on the current fragment or destination
            val currentDestination = navController.currentDestination
            currentDestination?.let { destination ->
                val menuItem = binding.navView.menu.findItem(destination.id)
                supportActionBar?.title = menuItem?.title ?: destination.label
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        val navigateUp = NavigationUI.navigateUp(navController, binding.drawerLayout)
        return navigateUp || super.onSupportNavigateUp()
    }

    // Handle back button when the drawer is open
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
