package com.ln.toptop.ui.main

interface LayoutController {
    fun showLoading(loading: Boolean)
    fun showNavigation(visible: Boolean)
    fun checkPermission(): Boolean
    fun requestPermission()
}