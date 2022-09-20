package com.ln.toptop.ui.record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ln.toptop.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.FileDescriptor
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
//    private val recordRepo: RecordRepo,
) : BaseViewModel() {

    private val _state: MutableLiveData<RecordState> = MutableLiveData(RecordState())
    val state: LiveData<RecordState> = _state


    fun startVideo(context: Context): FileDescriptor? {
        _state.value?.let { state ->
            _state.value = state.copy(
                started = true,
                recording = true
            )
            val timeCreated = System.currentTimeMillis()

//            localRecordLocation = recordVideoRepo.initVideo(context, timeCreated)
        }

//        return localRecordLocation?.fileDescriptor
        return null
    }

    fun resumeVideo() {
        _state.value = _state.value?.copy(recording = true)
    }

    fun pauseVideo() {
        _state.value = _state.value?.copy(recording = false)
    }

    fun stopVideo(context: Context) {

    }

}