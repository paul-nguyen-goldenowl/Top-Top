package com.ln.toptop.ui.auth

import android.view.View
import com.ln.toptop.R
import com.ln.toptop.databinding.BottomSheetAuthBinding
import com.ln.toptop.ui.base.BaseBottomSheet
import timber.log.Timber

class AuthBottomSheet : BaseBottomSheet(R.layout.bottom_sheet_auth) {
    private lateinit var binding: BottomSheetAuthBinding

    override fun bindView(mView: View) {
        binding = BottomSheetAuthBinding.bind(mView)
    }

    override fun setLayout() {
        binding.viewPager.apply {
            adapter = AuthPagerAdapter(
                childFragmentManager,
                lifecycle,
                onClose = { dismiss() },
                onDirect = { position -> currentItem = position }
            )
        }

    }
}