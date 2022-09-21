package com.ln.toptop.ui.record

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import com.ln.toptop.R

class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface ActionListener {
        fun onStartRecord() {}
        fun onResumeRecord() {}
        fun onPauseRecord() {}
        fun onEndRecord() {}
        fun onDiscardRecord() {}
        fun onDurationTooShortError() {}
    }

    var actionListener: ActionListener? = null

    private var started = false
    private var finished = false
    private var progress: Float = 0f
    private val maxProgress: Float = 360f
    private val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outerArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var desiredWidth: Int
    private var desiredHeight: Int

    private var outerCircleRadius: Float
    private var innerCircleRadius: Float
    private var outerCircleAnimatingFactor: Float
    private var innerCircleAnimatingFactor: Float

    private var STROKE_WIDTH: Float = 13f

    private var initialInnerCircleRadius: Float = 0f
    private var initialOuterCircleRadius: Float = 0f

    private var diffInnerCircleRadius: Float = 0f
    private var diffOuterCircleRadius: Float = 0f

    private var sweepAngle: Float = 0f

    private var arcRect: RectF
    private var innerRect: RectF

    private var startTimeInMills: Long = 0
    private var endTimeInMills: Long = 0

    private var minimumVideoDuration: Long = 0L
    private var videoDuration: Long = 0L

    private val DEFAULT_MINIMUM_RECORDING_TIME = 500L
    private val DEFAULT_VIDEO_RECORDING_TIME = 10000L
    private val START_ANGLE = 270f

    private var recording: Boolean = false
        set(value) {
            if (value)
                if (started) resumeRecord() else startRecord()
            else
                pauseRecord()
            field = value
        }

    private var divisionFactor: Float = 0f

    /*
    * Initializing and getting the value from xml attributes defying for the custom view
    * */
    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RecordButton,
            defStyleAttr,
            defStyleAttr
        )
        outerCirclePaint.color =
            typedArray.getColor(R.styleable.RecordButton_outerCircleColor, Color.WHITE)
        innerCirclePaint.color =
            typedArray.getColor(R.styleable.RecordButton_innerCircleColor, Color.WHITE)
        outerArcPaint.color =
            typedArray.getColor(R.styleable.RecordButton_progressColor, Color.GRAY)
        outerCirclePaint.strokeWidth =
            typedArray.getFloat(R.styleable.RecordButton_outerCircleWidth, STROKE_WIDTH)

        outerCirclePaint.style = Paint.Style.STROKE
        outerArcPaint.style = Paint.Style.STROKE
        outerArcPaint.strokeWidth = STROKE_WIDTH
        outerArcPaint.strokeCap = Paint.Cap.ROUND
        desiredWidth = 200
        desiredHeight = 200
        outerCircleRadius = 0f

        outerCircleAnimatingFactor = 0f
        innerCircleRadius = 0f
        outerCircleAnimatingFactor = 20f
        innerCircleAnimatingFactor = 20f
        arcRect = RectF()
        innerRect = RectF()
        minimumVideoDuration = DEFAULT_MINIMUM_RECORDING_TIME
        videoDuration = DEFAULT_VIDEO_RECORDING_TIME
    }

    private val progressAnimator = ObjectAnimator.ofFloat(this, "progress", 360f).apply {
        duration = videoDuration
        interpolator = LinearInterpolator()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                finishRecord()
            }
        })
    }

    private var singleTapValueAnimator = ValueAnimator.ofFloat().apply {
        interpolator = AccelerateInterpolator()
        duration = 300L
        addUpdateListener {
            innerCircleRadius = it.animatedValue as Float
            invalidate()
        }
    }

    private val pauseAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
        addUpdateListener { animation ->
            run {
                val value = animation.animatedValue as Int

                outerCircleRadius -= value * (diffOuterCircleRadius / 100)

                if (innerCircleRadius <= initialInnerCircleRadius)
                    innerCircleRadius -= value * divisionFactor

                if (outerCircleRadius >= initialOuterCircleRadius)
                    invalidate()
                else {
                    outerCircleRadius = initialOuterCircleRadius
                    innerCircleRadius = initialInnerCircleRadius
                    invalidate()
                    return@addUpdateListener
                }
            }
        }
    }

    private val recordAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
        addUpdateListener { animation ->
            run {
                val value = animation.animatedValue as Int
                outerCircleRadius += value * (diffOuterCircleRadius / 100)
                if (innerCircleRadius >= (width / 5f))
                    innerCircleRadius += value * (diffInnerCircleRadius / 100)
                if (outerCircleRadius <= width / 2f)
                    invalidate()
                else {
                    outerCircleRadius = width / 2f
                    invalidate()
                    return@addUpdateListener
                }
            }
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
//                recording = true
//                progressAnimator.start()
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null)
            return

        if (finished) {
            /* todo: draw finish */
        } else {
            if (recording) {
                canvas.drawRect(
                    innerRect,
                    innerCirclePaint
                )
            } else {
                canvas.drawCircle(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    innerCircleRadius,
                    innerCirclePaint
                )
            }
        }

        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            outerCircleRadius - STROKE_WIDTH / 2,
            outerCirclePaint
        )

        canvas.drawArc(arcRect, START_ANGLE, sweepAngle, false, outerArcPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initialOuterCircleRadius = w / 4f + STROKE_WIDTH
        outerCircleRadius = initialOuterCircleRadius

        initialInnerCircleRadius = w / 4f - STROKE_WIDTH
        innerCircleRadius = initialInnerCircleRadius
        singleTapValueAnimator.setFloatValues(
            initialInnerCircleRadius,
            w / 6f,
            initialInnerCircleRadius
        )
        val top = STROKE_WIDTH / 2
        val left = top
        val right = w - STROKE_WIDTH / 2
        val bottom = right
        arcRect.set(left, top, right, bottom)

        val centerPoint = Point(width / 2, height / 2)
        innerRect.set(
            centerPoint.x - innerCircleRadius,
            centerPoint.y - innerCircleRadius,
            centerPoint.x + innerCircleRadius,
            centerPoint.y + innerCircleRadius
        )

        diffOuterCircleRadius = ((width / 2f) - outerCircleRadius)
        diffInnerCircleRadius = ((width / 5f - 20) - innerCircleRadius)
        divisionFactor = (diffInnerCircleRadius / 100)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desireW = desiredWidth + paddingLeft + paddingRight
        val desireH = desiredHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desireW, widthMeasureSpec),
            measureDimension(desireH, heightMeasureSpec)
        )
    }

    private fun measureDimension(desireSize: Int, measureSpec: Int): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = desireSize

            if (mode == MeasureSpec.AT_MOST) {
                result = size.coerceAtMost(result)
            }
        }
        return result
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val detectDown = event.action == MotionEvent.ACTION_DOWN
        if (detectDown && !finished) {
            this.recording = !this.recording
        }
        return true
    }

    private fun startRecord() {
        started = true
        startTimeInMills = System.currentTimeMillis()
        actionListener?.onStartRecord()
        recordAnimator.start()
        progressAnimator.start()
    }

    private fun resumeRecord() {
        recordAnimator.start()
        if (progressAnimator.isPaused)
            progressAnimator.resume()
        actionListener?.onResumeRecord()
    }

    fun discardRecording() {
        endTimeInMills = System.currentTimeMillis()
        if (recordAnimator.isRunning)
            recordAnimator.end()
        progressAnimator.end()
        actionListener?.onDiscardRecord()
    }

    private fun pauseRecord() {
        if (recordAnimator.isRunning)
            recordAnimator.pause()
        if (progressAnimator.isRunning)
            progressAnimator.pause()
        pauseAnimator.start()
        actionListener?.onPauseRecord()
    }

    fun setProgress(progress: Float) {
        if (progress <= maxProgress)
            this.progress = progress
        else
            this.progress = maxProgress
        sweepAngle = (maxProgress * this.progress / maxProgress)
        invalidate()
    }

    private fun finishRecord() {
        finished = true
        sweepAngle = maxProgress
        endTimeInMills = System.currentTimeMillis()
        if (recordAnimator.isRunning)
            recordAnimator.end()
        if (isRecordingTooSmall(startTimeInMills, endTimeInMills, minimumVideoDuration)) {
            actionListener?.onDurationTooShortError()
        } else
            actionListener?.onEndRecord()
        resetTimeFields()
    }

    private fun isRecordingTooSmall(start: Long, end: Long, defaultTime: Long): Boolean {
        return defaultTime > end - start
    }

    private fun resetTimeFields() {
        startTimeInMills = 0
        endTimeInMills = 0
    }

    fun setMinimumVideoDuration(recordingTime: Long) {
        minimumVideoDuration = DEFAULT_MINIMUM_RECORDING_TIME + recordingTime
    }

    fun setVideoDuration(recordingTime: Long) {
        videoDuration = recordingTime
    }

    fun setProgressColor(color: Int) {
        outerArcPaint.color = color
        invalidate()
    }

    fun setInnerCircleColor(color: Int) {
        innerCirclePaint.color = color
        invalidate()
    }

    fun setOuterCircleColor(color: Int) {
        outerCirclePaint.color = color
        invalidate()
    }

    fun setOuterCircleWidth(width: Float) {
        outerCirclePaint.strokeWidth = width
        invalidate()
    }
}