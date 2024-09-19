package com.example.weatherforecast.view

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Declare the view binding object
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout) || super.onSupportNavigateUp()
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
