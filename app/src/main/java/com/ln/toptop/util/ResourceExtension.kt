package com.ln.toptop.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment


fun Context.getResColor(@ColorRes colorRes: Int) =
    ResourcesCompat.getColor(resources, colorRes, null)

fun Fragment.getResColor(@ColorRes colorRes: Int) =
    ResourcesCompat.getColor(resources, colorRes, null)

