package com.ln.toptop.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class RecordFile(
    var filePath: String?,
    var duration: Long?,
    var dateCreated: Long?
) : Parcelable