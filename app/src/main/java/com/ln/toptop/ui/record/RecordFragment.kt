package com.ln.toptop.ui.record

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (shouldInterceptBackPress()) {
                    if (viewModel.state.value?.recording != true)
                        showConfirmDialog()
                } else {
                    showNavigation(true)
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }

    private fun shouldInterceptBackPress(): Boolean {
        return viewModel.state.value?.started == true
    }

    override fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
        }
    }

    override fun setLayout() {
        setCameraView()
        setButtons()
    }

    private fun setButtons() {
        setRecordButton()
        binding.btnClose.setOnClickListener {
            showNavigation(true)
            findNavController().navigateUp()
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
                }
                val file =
                    viewModel.startRecord(requireContext())
                cameraView.takeVideo(file)
            }

            override fun onResumeRecord() {
                Timber.d("onResumeRecord")
                viewModel.resumeRecord()
            }

            override fun onPauseRecord() {
                Timber.d("onPauseRecord")
                cameraView.stopVideo()
                viewModel.pauseRecord()
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
            addCameraListener(viewModel.getCameraListener(requireContext()))
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermission()) {
            showDialog()
        } else {
            if (!cameraView.isOpened) {
                cameraView.open()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        cameraView.close()
        viewModel.stopRecord(requireContext())
    }

    override fun onDestroy() {
        cameraView.destroy()
        super.onDestroy()
    }

    private fun showDialog() {
        binding.permissionsLayout.permissionsLayout.isVisible = true
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.discard_current_sound))
            .setPositiveButton(getString(R.string.discard)) { _, _ ->
                showNavigation(true)
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
