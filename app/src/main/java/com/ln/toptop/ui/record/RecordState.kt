package com.ln.toptop.ui.record

data class RecordState(
    var started: Boolean = false,
    var recording: Boolean = false,
    var finished: Boolean = false,
    var stoppedByTime: Boolean = false,
)