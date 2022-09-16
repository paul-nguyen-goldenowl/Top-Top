package com.ln.toptop.ui.auth

import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentAuthBinding
import com.ln.toptop.ui.base.BaseFragment

class AuthFragment : BaseFragment(R.layout.fragment_auth) {
    private val binding by viewBindings(FragmentAuthBinding::bind)


    override fun setObservers() {

    }

    override fun setLayout() {
        setTitle(binding.toolBarText, R.string.profile)

        binding.btnSignUp.setOnClickListener {
            showRegisterBottomSheet()
        }
    }

    private fun showRegisterBottomSheet() {
        val bs = AuthBottomSheet()
        bs.show(childFragmentManager, bs.getBottomSheetTag())
    }
}

