package com.ln.toptop.ui.settings

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.BuildConfig
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentSettingBinding
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.hide
import com.ln.toptop.util.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment(R.layout.fragment_setting) {
    private val binding by viewBindings(FragmentSettingBinding::bind)
    override val viewModel by viewModels<SettingViewModel>()
    override fun onResume() {
        super.onResume()
        viewModel.checkAuthStatus()
    }

    override fun setObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                setLayoutProfile()
            } else
                setLayoutAuth()
        }
        viewModel.logOutState.observe(viewLifecycleOwner) { state ->
            if (state)
                findNavController().navigateUp()
        }
    }

    override fun setLayout() {
        binding.toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.version.text = getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun setLayoutAuth() {
        binding.btnLogOut.hide()
    }

    private fun setLayoutProfile() {
        binding.btnLogOut.apply {
            show()
            setOnClickListener {
                viewModel.logOut(requireContext())
            }
        }
    }
}
