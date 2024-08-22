package com.example.compose_player.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.legacy.MediaSessionCompat

@UnstableApi
class MusicPlayerService : MediaSessionService() {

    private lateinit var exoPlayer: ExoPlayer
    private var mediaSession: MediaSession? = null
    @SuppressLint("RestrictedApi")
    private lateinit var mediaCompat: MediaSessionCompat


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }


    @SuppressLint("RestrictedApi")
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()

    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }



}