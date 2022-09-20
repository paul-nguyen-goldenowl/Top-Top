package com.ln.toptop.ui.record

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ln.simplechat.ui.viewBindings
import com.ln.toptop.R
import com.ln.toptop.databinding.FragmentRecordBinding
import com.ln.toptop.ui.base.BaseFragment
import com.ln.toptop.util.hide
import com.ln.toptop.util.show
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RecordFragment : BaseFragment(R.layout.fragment_record) {

    private val binding by viewBindings(FragmentRecordBinding::bind)
    private lateinit var cameraView: CameraView
    override val viewModel by viewModels<RecordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showNavigation(false)
    }

    override fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            toggleUI(state)
        }
    }

    private fun toggleUI(state: RecordState) {
        binding.btnUpload.isGone = state.started
        binding.btnFinishRecord.isVisible = state.started
        binding.btnDiscard.isVisible = state.started && state.recording
    }

    override fun setLayout() {
        setCameraView()
        setButtons()
    }

    private fun setButtons() {
        setRecordButton()
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
            showNavigation(true)
        }
        binding.permissionsLayout.grantPermissionsBtn.setOnClickListener {
            binding.permissionsLayout.permissionsLayout.isVisible = false
            requestPermission()
        }
    }

    private fun setRecordButton() {
        binding.btnRecord.actionListener = object : RecordButton.ActionListener {
            override fun onStartRecord() {
                Timber.d("onStartRecord")
                binding.btnUpload.hide()
                binding.tvUpload.hide()
                binding.btnFinishRecord.show()
                binding.btnDiscard.show()

                lifecycleScope.launch {
                    val fileDescriptor =
                        viewModel.startVideo(requireContext()) ?: return@launch
                    cameraView.takeVideo(fileDescriptor)
                }
            }

            override fun onResumeRecord() {
                Timber.d("onResumeRecord")
                viewModel.resumeVideo()
            }

            override fun onPauseRecord() {
                Timber.d("onPauseRecord")
                cameraView.stopVideo()
                viewModel.pauseVideo()
            }

            override fun onEndRecord() {
                Timber.d("onEndRecord")
            }

            override fun onDiscardRecord() {
                Timber.d("onDiscardRecord")
            }

            override fun onDurationTooShortError() {
                Timber.d("onDurationTooShortError")
            }
        }
    }

    private fun setCameraView() {
        cameraView = binding.cameraView
        cameraView.apply {
            facing = Facing.BACK
            audio = Audio.ON
            mode = Mode.VIDEO
//            addCameraListener(viewModel.getCameraListener(requireContext()))
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermission()) {
            showDialog()
        }
    }

    override fun onStop() {
        super.onStop()
        cameraView.close()
        viewModel.stopVideo(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView.destroy()
    }

    private fun showDialog() {
        binding.permissionsLayout.permissionsLayout.isVisible = true
    }
}
