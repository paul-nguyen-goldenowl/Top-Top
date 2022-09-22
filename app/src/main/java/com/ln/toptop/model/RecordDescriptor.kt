package com.ln.toptop.model

import android.net.Uri
import java.io.FileDescriptor

data class RecordDescriptor(
    val contentUri: Uri,
    val filePath: String,
    val fileDescriptor: FileDescriptor
)