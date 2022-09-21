package com.ln.toptop.util

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ln.toptop.R

enum class SystemBarColors { LIGHT, DARK }

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
private const val darkStatusIcons = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

@RequiresApi(Build.VERSION_CODES.M)
private const val whiteStatusIcons = 0

fun Activity.changeSystemBars(systemBarColors: SystemBarColors) {
    setUpStatusBarAndNavigationBar()
    if (systemBarColors == SystemBarColors.LIGHT) {
        changeStatusBarIcons(isWhiteIcons = false)
        changeStatusBarColor(android.R.color.white)
        changeSystemNavigationBarColor(android.R.color.white)
    } else {
        changeStatusBarIcons(isWhiteIcons = true)
        changeStatusBarColor(android.R.color.transparent)
        changeSystemNavigationBarColor(R.color.black)
    }
}

@Suppress("DEPRECATION")
fun Activity.setUpStatusBarAndNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}

@Suppress("DEPRECATION")
fun Activity.changeStatusBarIcons(isWhiteIcons: Boolean) {
    setUpStatusBarAndNavigationBar()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window?.decorView?.systemUiVisibility =
            if (isWhiteIcons) whiteStatusIcons else darkStatusIcons
    }
}

fun Activity.changeStatusBarColor(@ColorRes colorRes: Int) {
    setUpStatusBarAndNavigationBar()
    window?.statusBarColor = getResColor(colorRes)
}

fun Activity.changeSystemNavigationBarColor(@ColorRes colorRes: Int) {
    window?.navigationBarColor = getResColor(colorRes)
}

fun showStatusAndNavBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowInsetsController = activity.window.insetsController
        windowInsetsController?.show(WindowInsets.Type.statusBars())
        windowInsetsController?.show(WindowInsets.Type.systemBars())
    }
}


@SuppressLint("ResourceType")
fun BottomNavigationView.changeNavBarColor(systemBarColors: SystemBarColors) {
    val iconRes =
        if (systemBarColors == SystemBarColors.LIGHT) R.drawable.dark_nav_tint
        else R.drawable.light_nav_tint

    val iconTint = ResourcesCompat.getColorStateList(resources, iconRes, null)

    itemIconTintList = iconTint
    itemTextColor = iconTint
}

fun ImageView.changeTint(@ColorRes colorRes: Int) {
    val tint = ResourcesCompat.getColorStateList(resources, colorRes, null)
    imageTintList = tint
}

@Suppress("DEPRECATION")
fun Activity.hideSystemBars() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}

@Suppress("DEPRECATION")
fun Activity.hideStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}