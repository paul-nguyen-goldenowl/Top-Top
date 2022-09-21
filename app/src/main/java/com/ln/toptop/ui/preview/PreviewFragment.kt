package com.ln.toptop.ui.preview

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentPreviewBinding
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.ui.exo.TPlayer
import timber.log.Timber

class PreviewFragment : BaseFragment(R.layout.fragment_preview) {

    private val binding by viewBindings(FragmentPreviewBinding::bind)

    private val args by navArgs<PreviewFragmentArgs>()
    private val localVideo by lazy { args.localVideo }
    private val player by lazy {
        TPlayer(
            playerView = binding.playerView,
            playBtn = binding.btnPause,
            context = requireContext(),
            url = localVideo.filePath.toString(),
            onVideoEnded = {
                it.restartPlayer()
            }
        )
    }

    override fun setObservers() {}

    override fun setLayout() {
        setPlayer()
        setButtons()
    }

    private fun setButtons() {
        binding.nextBtn.setOnClickListener {
            Timber.d("goto post video screen")
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setPlayer() {
        lifecycle.addObserver(player)
        player.init()
    }

    override fun onResume() {
        super.onResume()
        player.resumePlayer()
    }

    override fun onPause() {
        super.onPause()
        player.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stopPlayer()
    }
}