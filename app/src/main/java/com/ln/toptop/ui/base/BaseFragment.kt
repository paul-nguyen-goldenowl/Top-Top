package com.ln.toptop.ui.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ln.toptop.R
import com.ln.toptop.ui.main.LayoutController
import com.ln.toptop.util.getResColor
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
abstract class BaseFragment(@LayoutRes val resId: Int) : Fragment(resId) {
    open val viewModel by viewModels<BaseViewModel>()
    private lateinit var controller: LayoutController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = requireActivity() as LayoutController
        setLayout()
        observeEvents()
        setObservers()
    }

    private fun observeEvents() {
        viewModel.snackBarMessageRes.observe(viewLifecycleOwner) { resId ->
            resId?.let {
                showSnackBar(requireView(), resId)
                viewModel.clearMessage()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            controller.showLoading(it)
        }
    }

    abstract fun setObservers()

    private fun showSnackBar(
        view: View,
        @StringRes stringRes: Int,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(view, requireContext().getString(stringRes), duration)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setTextColor(getResColor(android.R.color.white))
            .setBackgroundTint(getResColor(R.color.light_black))
            .show()
    }

    abstract fun setLayout()

    fun setTitle(textView: TextView, @StringRes titleRes: Int) {
        textView.text = getString(titleRes).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    fun showLoading(loading: Boolean) = controller.showLoading(loading)
}