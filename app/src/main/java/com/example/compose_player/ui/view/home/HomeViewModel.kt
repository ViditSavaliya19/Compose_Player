package com.example.compose_player.ui.view.home

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.data.repository.MusicRepository
import com.example.compose_player.utils.MediaNotificationManager
import com.example.compose_player.utils.network.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MusicRepository) : ViewModel() {
    val musicData: MutableState<DataState<List<MusicModelItem>>> = mutableStateOf(DataState.Loading)
    private val _musicList = MutableLiveData<List<MusicModelItem>>(emptyList())
    val musicList: LiveData<List<MusicModelItem>> = _musicList
    private var _isPlaying = MutableLiveData<Boolean>(false)
    private var _player = MutableLiveData<ExoPlayer>()
    private var _currentMediaItemDuration = MutableLiveData<Long>(1)
    private var _currentMediaItemPosition = MutableLiveData<Long>(0)
    private var _currentMediaItemIndex = MutableLiveData<Int>(0)

    val currentMediaItemIndex: LiveData<Int> = _currentMediaItemIndex
    val currentPosition: LiveData<Long> = _currentMediaItemPosition
    val totalDuration: LiveData<Long> = _currentMediaItemDuration
    val isPlaying: LiveData<Boolean> = _isPlaying
    val player: LiveData<ExoPlayer> = _player

    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()

    private val _totalDurationInMS = MutableStateFlow(0L)
    val totalDurationInMS = _totalDurationInMS.asStateFlow()


    private lateinit var notificationManager: MediaNotificationManager
    protected lateinit var mediaSession: MediaSession


    init {
        getMusicData()
    }

    //Fetching Music Data
    private fun getMusicData() {
        viewModelScope.launch {
            repository.loadMusic().onEach {
                musicData.value = it
                Log.e("Data2", "getMusicData: ${musicData}")
                if (musicData.value is DataState.Success) {
                    _musicList.value =
                        (musicData.value as DataState.Success<List<MusicModelItem>>).data as ArrayList<MusicModelItem>
                    setMusicMediaItem()
                }
            }.launchIn(viewModelScope)
        }
    }

    //Setting Music Media Item
    private suspend fun setMusicMediaItem() {
        if (_musicList.value!!.isNotEmpty()) {

            musicList.value!!.forEach {
                val mediaItem = MediaItem.fromUri(Uri.parse(it.data!!.url))
                player.value!!.addMediaItem(mediaItem)
            }
            player.value!!.prepare()
            setCurrentPosition()
            setDuration()
        }
    }

    @OptIn(UnstableApi::class)
    fun setUpNotificationManager(context: Context) {
        val sessionActivityPendingIntent =
            context.packageManager?.getLaunchIntentForPackage(context.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        context,
                        1,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
        mediaSession = MediaSession.Builder(context, player.value!!)
            .setSessionActivity(sessionActivityPendingIntent!!).build()
         notificationManager =
            MediaNotificationManager(
                context,
                mediaSession.token,
                player.value!!,
               PlayerNotificationListener()
            )


        notificationManager.showNotificationForPlayer(player.value!!)
    }

    @UnstableApi
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {

        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

        }
    }

    //Playing or Pausing Music
    fun playOrPauseMusic() {
        setDuration()
        if (isPlaying.value == true) {
            player.value?.pause()
        } else {
            player.value?.play()
        }
        setCurrentPosition()
        _isPlaying.value = _isPlaying.value != true
    }

    //Changing Music
    fun nextSong() {
        player.value!!.seekToNextMediaItem()
        getCurrentMediaIndex()
        setCurrentPosition()
    }

    //Changing Music
    fun previousSong() {
        player.value!!.seekToPreviousMediaItem()
        getCurrentMediaIndex()
        setCurrentPosition()

    }

    //Changing Music
    fun clickToPlay(index: Int) {
        player.value?.seekToDefaultPosition(index)
        getCurrentMediaIndex()
        setCurrentPosition()
    }

    fun seekTo(position: Long) {
        player.value?.seekTo(position)
    }

    //Initializing Player
    suspend fun setPlayer(context: Context) {
        _player.value = ExoPlayer.Builder(context).build()
    }

    //Setting Current Position
    private fun setCurrentPosition() {


//        viewModelScope.launch {
//            while (true) {
//                _currentMediaItemPosition.value = player.value!!.currentPosition
//                Log.e("LiveDuration", "setCurrentPosition: ${_currentMediaItemPosition.value}")
//                Log.e("Total Duration ","setDuration:======= ${((currentPosition.value!!.toFloat() ) / totalDuration.value!!.toFloat())}",)
//
//                delay(1000L) // Update every second
//            }
//        }

    }

    private fun syncPlayerFlows() {
        _currentPlayingIndex.value = player.value!!.currentMediaItemIndex
        _totalDurationInMS.value = player.value!!.duration.coerceAtLeast(0L)
    }

    //Setting Duration
    private fun setDuration() {
        _currentMediaItemDuration.value = player.value!!.duration
        Log.e("Total Duration ", "setDuration:======= ${totalDuration.value!!}")
    }

    private fun getCurrentMediaIndex() {
        _currentMediaItemIndex.value = player.value!!.currentMediaItemIndex
    }


    val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            syncPlayerFlows()
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(player.value!!)
                }

                else -> {
                    notificationManager.hideNotification()
                }
            }
        }
    }


}