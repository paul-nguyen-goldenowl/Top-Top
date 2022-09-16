package com.ln.toptop.ui.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ln.toptop.ui.auth.login.LoginFragment
import com.ln.toptop.ui.auth.register.RegisterFragment

class AuthPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    onClose: () -> Unit,
    onDirect: (Int) -> Unit
) :
    FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount() = listFragment.size

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

    private val listFragment = listOf(
        RegisterFragment().apply {
            onCloseBottomSheet = onClose
            changePage = onDirect
        },
        LoginFragment().apply {
            onCloseBottomSheet = onClose
            changePage = onDirect
        }
    )

    companion object {
        const val REGISTER_PAGE = 0
        const val LOGIN_PAGE = 1
    }
}
