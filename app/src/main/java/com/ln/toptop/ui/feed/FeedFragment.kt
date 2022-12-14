package com.ln.toptop.ui.feed

import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentFeedBinding
import com.ln.toptop.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment :
    BaseFragment(R.layout.fragment_feed) {
    private val binding by viewBindings(FragmentFeedBinding::bind)

    override fun setObservers() {}

    override fun setLayout() {}

}
