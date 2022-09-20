package com.ln.toptop.ui.auth.login

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentLoginBinding
import com.ln.toptop.ui.auth.AuthPagerAdapter.Companion.REGISTER_PAGE
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.fromHtml
import com.ln.toptop.util.toast

class LoginFragment :
    BaseFragment(R.layout.fragment_login) {
    private val binding by viewBindings(FragmentLoginBinding::bind)
    lateinit var onCloseBottomSheet: () -> Unit
    lateinit var changePage: (Int) -> Unit
    override val viewModel by viewModels<LoginViewModel>()

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.googleAuthRepo.handleGoogleOnResult(result?.data)
        }

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
        setUpRegister()
    }

    private fun setUpRegister() {
        binding.btnEmail.setOnClickListener {
            toast(R.string.implement_later)
        }
        binding.btnFacebook.setOnClickListener {
            toast(R.string.implement_later)
        }
        binding.btnGoogle.setOnClickListener {
            viewModel.googleAuthRepo.doGoogleAuth(requireContext(), googleLauncher)
        }
    }

    override fun setObservers() {

    }
}
