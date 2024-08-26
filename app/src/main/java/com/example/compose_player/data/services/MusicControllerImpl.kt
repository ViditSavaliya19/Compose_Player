package com.example.compose_player.data.services

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.compose_player.data.mapper.toSong
import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.other.PlayerState
import com.example.compose_player.domain.service.MusicController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MusicControllerImpl(context: Context) : MusicController {

    private var mediaControllerFuture: ListenableFuture<MediaController>


    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override var mediaControllerCallback: ((
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit)? = null


    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
    }

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                with(player) {
                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.toSong(),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayer", "Error: ====-----==== ${error.message}")
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) = when (this) {
        Player.STATE_IDLE -> PlayerState.STOPPED
        Player.STATE_ENDED -> PlayerState.STOPPED
        else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
    }


    override fun addMediaItems(songs: List<Song>) {

        val mediaItems = songs.map {
            MediaItem.Builder()
                .setUri(it.songUrl)
                .setMediaMetadata(
                MediaMetadata.Builder()
                    .setSubtitle(it.subtitle)
                    .setTitle(it.title)
                    .setArtist(it.subtitle)
                    .setArtworkUri(Uri.parse(it.imageUrl))
                    .build()
            ).build()
        }
        mediaController?.setMediaItems(mediaItems)

        Log.e("Media Assign", "addMediaItems: ${mediaController}")
        Log.e("Media Controller", "addMediaItems: ${mediaController!!.getMediaItemAt(0).toSong()}")
    }

    override fun play(mediaItemIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(mediaItemIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L


    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

    override fun getCurrentSong(): Song? = mediaController?.currentMediaItem?.toSong()


    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }
}