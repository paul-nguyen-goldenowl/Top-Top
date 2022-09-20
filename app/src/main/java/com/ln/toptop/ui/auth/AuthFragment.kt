package com.ln.toptop.ui.auth

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentAuthBinding
import com.ln.toptop.model.User
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.hide
import com.ln.toptop.util.loadUrl
import com.ln.toptop.util.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {
    private val binding by viewBindings(FragmentAuthBinding::bind)
    override val viewModel by viewModels<AuthViewModel>()

    override fun onResume() {
        super.onResume()
        viewModel.checkAuthStatus()
    }

    override fun setObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                setLayoutProfile(it)
            } else
                setLayoutAuth()
        }
    }

    override fun setLayout() {
        binding.toolBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menu_more -> findNavController().navigate(R.id.action_authFragment_to_settingFragment)
            }
            true
        }
    }

    private fun setLayoutAuth() {
        binding.layoutAuth.show()
        binding.layoutProfile.hide()
        setTitle(binding.toolBarText, R.string.profile)
        binding.btnSignUp.setOnClickListener {
            showRegisterBottomSheet()
        }
    }

    private fun setLayoutProfile(user: User) {
        binding.layoutAuth.hide()
        binding.layoutProfile.show()
        binding.toolBarText.text = user.name
        binding.ivAvatar.loadUrl(user.avatar, circle = true)
        binding.tvUserName.text = getString(R.string.anchor_name, user.name)
    }

    private fun showRegisterBottomSheet() {
        val bs = AuthBottomSheet()
        bs.show(childFragmentManager, bs.getBottomSheetTag())
    }
}

