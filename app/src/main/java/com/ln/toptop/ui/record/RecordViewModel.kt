package com.ln.toptop.ui.record

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ln.toptop.data.local.video.RecordRepo
import com.ln.toptop.ui.base.BaseViewModel
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
) : BaseViewModel() {

    private val _state: MutableLiveData<RecordState> = MutableLiveData(RecordState())
    val state: LiveData<RecordState> = _state

    private var timeCreated = 0L

    lateinit var recordFile: File
    fun startRecord(context: Context): File {
        _state.value = _state.value?.copy(started = true, recording = true)
        timeCreated = System.currentTimeMillis()
        recordFile = recordRepo.initVideo(context, timeCreated)
        return recordFile
    }

    fun resumeRecord() {
        _state.value = _state.value?.copy(recording = true)
    }

    fun pauseRecord() {
        _state.value = _state.value?.copy(recording = false)
    }

    fun stopRecord(context: Context) {

    }

    private fun getRecordDuration(context: Context): Int? {
        val mediaPlayer = MediaPlayer.create(context, recordFile.toUri())
        val duration = mediaPlayer?.duration
        mediaPlayer?.release()

        return duration
    }

    fun getCameraListener(context: Context) = object : CameraListener() {
        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            Timber.d("Video recording has started")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            Timber.d("Video recording has ended")
        }

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            Timber.d("Video has been taken. filePath=$recordFile")


            val duration = getRecordDuration(context)
            Timber.d("duration is $duration")

        }
    }
}