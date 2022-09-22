@file:Suppress("DEPRECATION")

package com.ln.toptop.ui.exo

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ln.toptop.R
import timber.log.Timber

@Suppress("DEPRECATION")
class TPlayer(
    private val playerView: StyledPlayerView,
    private val playBtn: ImageView,
    private val context: Context,
    private val url: String?,
    private val onVideoEnded: (TPlayer) -> Unit
) : LifecycleObserver {
    private var playbackPosition = 0L
    private var simpleExoPlayer: ExoPlayer? = null

    var isCreated = false
    var isPlaying = false

    fun init() {
        createPlayer()

        playBtn.setOnClickListener {
            resumePlayer()
        }
    }

    private fun createPlayer() {
        Timber.d("createPlayer")
        isCreated = true

        simpleExoPlayer = ExoPlayer.Builder(context)
            .setUseLazyPreparation(true)
            .build()

        simpleExoPlayer?.addListener(playerListener)
        simpleExoPlayer?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))

        playerView.player = simpleExoPlayer
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.requestFocus()

        simpleExoPlayer?.prepare()
        resumePlayer()
        setButton()
    }

    private fun setButton() {
        playerView.setOnClickListener {
            Timber.d("clicked")
            if (isPlaying && isCreated) {
                pausePlayer()
                toggleResume(false)
            } else {
                resumePlayer()
                toggleResume(true)
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumePlayer() {
        if (isCreated && !isPlaying) {
            isPlaying = true
            simpleExoPlayer?.seekTo(playbackPosition)
            simpleExoPlayer?.play()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pausePlayer() {
        if (isCreated && isPlaying) {
            isPlaying = false
            playBtn.visibility = View.VISIBLE
            playbackPosition = simpleExoPlayer!!.currentPosition
            simpleExoPlayer?.pause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopPlayer() {
        if (isCreated) {
            pausePlayer()
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            isCreated = false
        }
    }

    fun restartPlayer() {
        if (isCreated) {
            playbackPosition = 0
            isPlaying = true
            simpleExoPlayer?.seekTo(0, playbackPosition)
            simpleExoPlayer?.play()
        }
    }

    fun changePlayerState() {
        if (isCreated) {
            if (isPlaying) {
                pausePlayer()
            } else {
                resumePlayer()
            }
        }
    }

    private val playerListener = object : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            playerView.hideController()
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                }
                Player.STATE_READY -> {
                }
                Player.STATE_ENDED -> {
                    onVideoEnded(this@TPlayer)
                }
                Player.STATE_IDLE -> {
                }
            }
        }
    }

    private fun toggleResume(isResume: Boolean) {
        playBtn.apply {
            setImageResource(if (isResume) R.drawable.ic_play else R.drawable.ic_pause)
            alpha = 1f
            animate().setDuration(200).alpha(0f)
        }
    }
}