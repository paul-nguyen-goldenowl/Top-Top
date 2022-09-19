package com.ln.toptop.ui.auth.register

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentRegisterBinding
import com.ln.toptop.ui.auth.AuthPagerAdapter.Companion.LOGIN_PAGE
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.ui.base.UiState
import com.ln.toptop.util.fromHtml
import com.ln.toptop.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment(R.layout.fragment_register) {
    private val binding by viewBindings(FragmentRegisterBinding::bind)
    override val viewModel by viewModels<RegisterViewModel>()
    lateinit var onCloseBottomSheet: () -> Unit
    lateinit var changePage: (Int) -> Unit

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.googleAuthRepo.handleGoogleOnResult(result?.data)
        }

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
        viewModel.liveCredential.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.registerWithCredential(it)
            }
        }
        viewModel.registerState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.LOADING -> binding.root.isEnabled = false
                else -> onCloseBottomSheet()
            }
        }
    }
}
