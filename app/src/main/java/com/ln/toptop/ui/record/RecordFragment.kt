package com.ln.toptop.ui.record

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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
import timber.log.Timber


@AndroidEntryPoint
class RecordFragment : BaseFragment(R.layout.fragment_record) {

    private val binding by viewBindings(FragmentRecordBinding::bind)
    private lateinit var cameraView: CameraView
    override val viewModel by viewModels<RecordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (shouldInterceptBackPress()) {
                    if (viewModel.state.value?.recording != true)
                        showConfirmDialog()
                } else {
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
        viewModel.localVideo.observe(viewLifecycleOwner) { localVideo ->
            localVideo?.let {
                findNavController().navigate(
                    RecordFragmentDirections.actionRecordFragmentToPreviewFragment(
                        localVideo
                    )
                )
                viewModel.resetLocalVideo()
            }
        }
    }

    override fun setLayout() {
        setCameraView()
        setButtons()

        binding.permissionsLayout.textView.text =
            getString(
                R.string.request_permission_title,
                getString(R.string.app_name)
            )
    }

    private fun setButtons() {
        setRecordButton()
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.permissionsLayout.grantPermissionsBtn.setOnClickListener {
            binding.permissionsLayout.permissionsLayout.isVisible = false
            requestPermission()
        }
        binding.btnFinishRecord.setOnClickListener {
            viewModel.stopRecord(requireContext())
            if (cameraView.isTakingVideo) {
                cameraView.stopVideo()
            }
        }
    }

    private fun setRecordButton() {
        binding.btnRecord.actionListener = object : RecordButton.ActionListener {
            override fun onStartRecord() {
                Timber.d("onStartRecord")
                Handler(Looper.getMainLooper()).postDelayed({
                    if (viewModel.state.value?.recording == true) {
                        binding.btnUpload.hide()
                        binding.tvUpload.hide()
                        binding.btnFinishRecord.show()
                        binding.btnDiscard.show()
                    }
                }, 1000)

                val file =
                    viewModel.startRecord(requireContext()) ?: return
                cameraView.takeVideo(file.fileDescriptor)
            }

            override fun onResumeRecord() {
                Timber.d("onResumeRecord")
                viewModel.resumeRecord()
            }

            override fun onPauseRecord() {
                if (cameraView.isTakingVideo) {
                    cameraView.stopVideo()
                }
                viewModel.pauseRecord()
            }

            override fun onEndRecord() {
                if (cameraView.isTakingVideo) {
                    cameraView.stopVideo()
                }
                viewModel.stopRecord(requireContext())
            }

            override fun onDiscardRecord() {
                if (cameraView.isTakingVideo) {
                    cameraView.stopVideo()
                }
            }

            override fun onDurationTooShortError() {

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
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
