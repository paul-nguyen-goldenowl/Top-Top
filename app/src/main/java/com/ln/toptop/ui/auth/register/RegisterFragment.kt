package com.ln.toptop.ui.auth.register

import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentRegisterBinding
import com.ln.toptop.ui.auth.AuthPagerAdapter.Companion.LOGIN_PAGE
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.fromHtml

class RegisterFragment :
    BaseFragment(R.layout.fragment_register) {
    private val binding by viewBindings(FragmentRegisterBinding::bind)
    lateinit var onCloseBottomSheet: () -> Unit
    lateinit var changePage: (Int) -> Unit

    override fun setLayout() {
        with(binding) {
            btnClose.setOnClickListener {
                onCloseBottomSheet()
            }
            tvPolicy.fromHtml(R.string.by_continue)
            alreadyHaveAccount.apply {
                fromHtml(R.string.already_have_account)
                setOnClickListener {
                    changePage(LOGIN_PAGE)
                }
            }
        }
    }
}
