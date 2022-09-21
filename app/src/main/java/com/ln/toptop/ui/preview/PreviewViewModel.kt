package com.ln.toptop.ui.preview

import com.ln.toptop.data.local.video.RecordRepo
import com.ln.toptop.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
) : BaseViewModel() {
}