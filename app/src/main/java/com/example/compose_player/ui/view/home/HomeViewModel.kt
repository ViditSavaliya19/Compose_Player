package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.data.repository.MusicRepository
import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.service.MusicController
import com.example.compose_player.utils.network.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val repository: MusicRepository,
    val musicController: MusicController
) : ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    val musicData: MutableState<DataState<List<MusicModelItem>>> = mutableStateOf(DataState.Loading)
    private val _musicList = MutableLiveData<List<MusicModelItem>>(emptyList())
    val musicList: LiveData<List<MusicModelItem>> = _musicList


    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.PlaySong -> playSong()
            HomeEvent.PauseSong -> pauseSong()
            HomeEvent.ResumeSong -> resumeSong()
            HomeEvent.FetchSong -> getSong()
            is HomeEvent.OnSongSelected -> homeUiState =
                homeUiState.copy(selectedSong = event.selectedSong)

            is HomeEvent.SkipToNextSong -> skipToNextSong()

            is HomeEvent.SkipToPreviousSong -> skipToPreviousSong()
        }

    }


    //Fetching Music Data
    private fun getSong() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            repository.loadMusic()
                .catch {
                    homeUiState = homeUiState.copy(
                        loading = false,
                        errorMessage = it.message
                    )
                }
                .collect() {
                    homeUiState = when (it) {
                        is DataState.Success -> {
                            val mediaItemList = it.data?.let { song ->
                                song.map {
                                    Song(
                                        imageUrl = it.data?.image!!,
                                        songUrl = it.data.url!!,
                                        title = it.song!!,
                                        subtitle = it.album!!,
                                        mediaId = it.song!!
                                    )
                                }
                            }
                            musicController.addMediaItems(mediaItemList!!)
                            homeUiState.copy(
                                loading = false,
                                songs = mediaItemList
                            )
                        }
                        is DataState.Loading -> {
                            homeUiState.copy(loading = true, errorMessage = null)
                        }
                        is DataState.Error -> {
                            homeUiState.copy(
                                loading = false,
                                errorMessage = it.exception.message
                            )

                        }
                    }

                }
        }
    }

    private fun playSong() {
        homeUiState.apply {
            songs?.indexOf(selectedSong)?.let {
                musicController.play(it)
            }
        }
    }

    private fun pauseSong() {
        musicController.pause()
    }

    private fun resumeSong() {
        musicController.resume()
    }

    private fun skipToNextSong() {
        musicController.skipToNextSong()
        homeUiState = homeUiState.copy(selectedSong = musicController.getCurrentSong())
    }

    private fun skipToPreviousSong() {
        musicController.skipToPreviousSong()
        homeUiState = homeUiState.copy(selectedSong = musicController.getCurrentSong())
    }



}