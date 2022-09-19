package com.ln.toptop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.ActivityMainBinding
import com.ln.toptop.ui.main.LayoutController
import com.ln.toptop.util.disableTooltip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), LayoutController {
    private lateinit var navController: NavController
    private lateinit var bottomNavBar: BottomNavigationView
    private val binding by viewBindings(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavBar = binding.bottomNavBar
        bottomNavBar.disableTooltip()
        bottomNavBar.setupWithNavController(navController)
    }

    override fun showLoading(loading: Boolean) {
        binding.progressBar.isVisible = loading
    }
}