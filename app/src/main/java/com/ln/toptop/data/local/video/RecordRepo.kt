package com.ln.toptop.data.local.video

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.ln.toptop.model.RecordDescriptor
import java.io.File
import javax.inject.Inject

class RecordRepo @Inject constructor() {

    var recordFile: File? = null

    private var recordDescriptor: RecordDescriptor? = null

    fun initVideo(context: Context, timeCreated: Long): RecordDescriptor? {
        val resolver = context.contentResolver
        val videoCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val videoDetails = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$timeCreated.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, timeCreated)
            put(MediaStore.Video.Media.DATE_MODIFIED, timeCreated)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val contentUri =
            resolver.insert(videoCollection, videoDetails) ?: return null
        /*
        val filePath =
            getRealPathFromURI(context, contentUri) ?: ""
        */
        val filePath = "/storage/emulated/0/Movies/$timeCreated.mp4"
        val fileDescriptor = resolver
            .openFileDescriptor(contentUri, "rw")?.fileDescriptor ?: return null
        recordDescriptor = RecordDescriptor(contentUri, filePath, fileDescriptor)
        return recordDescriptor
    }

    fun stopVideo(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Audio.Media.IS_PENDING, 0)
            }
            context.contentResolver.update(
                recordDescriptor?.contentUri ?: return,
                contentValues,
                null,
                null
            )
        }
    }
}