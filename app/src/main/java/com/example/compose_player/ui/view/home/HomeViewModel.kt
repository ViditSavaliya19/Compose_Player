package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.data.repository.MusicRepository
import com.example.compose_player.utils.MusicPlayerService
import com.example.compose_player.utils.network.DataState
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("RestrictedApi")
@UnstableApi
@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MusicRepository) : ViewModel() {

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    //    lateinit var mediaController: MediaController
    val musicData: MutableState<DataState<List<MusicModelItem>>> = mutableStateOf(DataState.Loading)
    private val _musicList = MutableLiveData<List<MusicModelItem>>(emptyList())
    val musicList: LiveData<List<MusicModelItem>> = _musicList

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context


    init {
        getMusicData()

    }

    fun getContext(context: Context) {
        this.context = context
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//        controllerFuture.addListener({ mediaController = controllerFuture.get()
//        }, MoreExecutors.directExecutor())
        initMediaItem()

    }

    //Fetching Music Data
    private fun getMusicData() {
        viewModelScope.launch {
            repository.loadMusic().onEach {
                musicData.value = it
                Log.e("Data2", "getMusicData: ${musicData}")
                if (musicData.value is DataState.Success) {
                    _musicList.value = (musicData.value as DataState.Success<List<MusicModelItem>>).data as ArrayList<MusicModelItem>
                }
            }.launchIn(viewModelScope)
        }
    }


    fun initMediaItem()
    {
        val itemList =  musicList.value!!.map {
            MediaItem.fromUri(Uri.parse(it.data!!.url))
        }
        controllerFuture.get().addMediaItems(itemList)
    }


}