package com.ln.toptop.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.ActivityMainBinding
import com.ln.toptop.ui.main.LayoutController
import com.ln.toptop.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), LayoutController,
    NavController.OnDestinationChangedListener {
    private lateinit var navController: NavController
    private lateinit var bottomNavBar: BottomNavigationView
    private val binding by viewBindings(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavigation()
        navController.addOnDestinationChangedListener(this)
    }

    private fun setupBottomNavigation() {
        bottomNavBar = binding.bottomNavBar
        bottomNavBar.disableTooltip()
        bottomNavBar.setupWithNavController(navController)
    }

    override fun showLoading(loading: Boolean) {
        binding.progressBar.isVisible = loading
    }

    fun changeNavBarColor(systemBarColors: SystemBarColors) {
        binding.bottomNavBar.changeNavBarColor(systemBarColors)
        when (systemBarColors) {
            SystemBarColors.DARK -> binding.imageViewAddIcon.changeTint(R.color.white)
            SystemBarColors.LIGHT -> binding.imageViewAddIcon.changeTint(R.color.black)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id in mainPages) {
            showNavigation(true)
        } else
            showNavigation(false)

        when (destination.id) {
            R.id.feedFragment -> {
                window?.setBackgroundDrawableResource(R.color.black)
                changeSystemBars(SystemBarColors.DARK)
                changeNavBarColor(SystemBarColors.DARK)
            }
            else -> {
                window?.setBackgroundDrawableResource(R.color.white)
                changeSystemBars(SystemBarColors.LIGHT)
                changeNavBarColor(SystemBarColors.LIGHT)
            }
        }
    }

    override fun showNavigation(visible: Boolean) {
        binding.bottomNavBar.isVisible = visible
        binding.imageViewAddIcon.isVisible = visible
    }

    override fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ),
            READ_STORAGE_REQUEST_CODE
        )
    }

    companion object {
        const val READ_STORAGE_REQUEST_CODE = 1001
        val mainPages = listOf(R.id.feedFragment, R.id.authFragment)
    }
}