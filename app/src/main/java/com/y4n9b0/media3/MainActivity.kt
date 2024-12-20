package com.y4n9b0.media3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.ui.PlayerView
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "Bob"
    private val url = "https://flutter.github.io/assets-for-api-docs/assets/videos/bee.mp4"

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            addListener(listener)
            addAnalyticsListener(analyticsListener)
        }
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    Log.d(TAG, "onPlayerStateChanged playbackState=Player.STATE_IDLE")
                }

                Player.STATE_BUFFERING -> {
                    Log.d(TAG, "onPlayerStateChanged playbackState=Player.STATE_BUFFERING")
                }

                Player.STATE_READY -> {
                    Log.d(TAG, "onPlayerStateChanged playbackState=Player.STATE_READY")
                }

                Player.STATE_ENDED -> {
                    Log.d(TAG, "onPlayerStateChanged playbackState=Player.STATE_ENDED")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            error.printStackTrace()
            Log.e(TAG, "Player.Listener onPlayerError ${buildErrorMessage(error)}")
        }
    }

    private val analyticsListener = @UnstableApi object : AnalyticsListener {
        override fun onAudioCodecError(
            eventTime: AnalyticsListener.EventTime,
            audioCodecError: Exception
        ) {
            super.onAudioCodecError(eventTime, audioCodecError)
            Log.e(TAG, "onAudioCodecError ${audioCodecError.message}")
        }

        override fun onAudioSinkError(
            eventTime: AnalyticsListener.EventTime,
            audioSinkError: Exception
        ) {
            super.onAudioSinkError(eventTime, audioSinkError)
            Log.e(TAG, "onAudioSinkError ${audioSinkError.message}")
        }

        override fun onLoadError(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData,
            error: IOException,
            wasCanceled: Boolean
        ) {
            super.onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled)
            Log.e(TAG, "onLoadError ${error.message}")
        }

        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException
        ) {
            super.onPlayerError(eventTime, error)
            Log.e(TAG, "AnalyticsListener onPlayerError ${buildErrorMessage(error)}")
        }

        override fun onVideoCodecError(
            eventTime: AnalyticsListener.EventTime,
            videoCodecError: Exception
        ) {
            super.onVideoCodecError(eventTime, videoCodecError)
            Log.e(TAG, "onPlayerError ${videoCodecError.message}")
        }
    }

    private fun buildErrorMessage(error: PlaybackException): String {
        val newline = "\r\n"
        val indent = "  "
        val sb = StringBuilder()
            .append("onPlayerError, ExoPlaybackException: ${error.message}")
            .append(newline)
            .append(indent)
        var cause: Throwable? = error.cause
        var level = 1
        while (cause != null) {
            sb.append(newline)
            repeat(level) {
                sb.append(indent)
            }
            sb.append("Cause $level: ${cause.message}")
            cause = cause.cause
            level++
        }
        return sb.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.player = exoPlayer
        @UnstableApi
        playerView.controllerAutoShow = false
        @UnstableApi
        playerView.controllerHideOnTouch = true
        @UnstableApi
        playerView.controllerShowTimeoutMs = 2000
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer.isPlaying) exoPlayer.pause()
    }

    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }
}
