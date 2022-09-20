package com.ln.toptop.data.local.video

import android.content.Context
import java.io.File
import javax.inject.Inject

class RecordRepo @Inject constructor() {

    fun initVideo(context: Context, timeCreated: Long): File {
        return File(context.filesDir, "${timeCreated}.mp4")
    }
}