package com.ln.toptop.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import timber.log.Timber


fun Context.getResColor(@ColorRes colorRes: Int) =
    ResourcesCompat.getColor(resources, colorRes, null)

fun Fragment.getResColor(@ColorRes colorRes: Int) =
    ResourcesCompat.getColor(resources, colorRes, null)

fun Fragment.toast(@StringRes stringRes: Int) {
    Timber.d(getString(stringRes))
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(exception: Exception) {
    Timber.e(exception, "Exception")
    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
}