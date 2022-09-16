package com.ln.toptop.ui.auth.login

import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentLoginBinding
import com.ln.toptop.ui.auth.AuthPagerAdapter.Companion.REGISTER_PAGE
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.fromHtml

class LoginFragment :
    BaseFragment(R.layout.fragment_login) {
    private val binding by viewBindings(FragmentLoginBinding::bind)
    lateinit var onCloseBottomSheet: () -> Unit
    lateinit var changePage: (Int) -> Unit
    override fun setLayout() {
        with(binding) {
            btnClose.setOnClickListener {
                onCloseBottomSheet()
            }
            dontHaveAccount.apply {
                fromHtml(R.string.dont_have_account)
                setOnClickListener {
                    changePage(REGISTER_PAGE)
                }
            }
        }
    }
}
