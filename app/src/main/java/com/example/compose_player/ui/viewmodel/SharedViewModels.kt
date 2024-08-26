package com.example.compose_player.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_player.data.model.MusicControllerUiState
import com.example.compose_player.domain.other.PlayerState
import com.example.compose_player.domain.service.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModels @Inject constructor(val musicController: MusicController) : ViewModel() {
    var musicControllerUiState by  mutableStateOf(MusicControllerUiState())
        private set

    init {
        setMediaControllerCallback()
    }

    private fun setMediaControllerCallback() {
        musicController.mediaControllerCallback =
            { playerState, currentMusic, currentPosition, totalDuration, isShuffleEnabled, isRepeatOneEnabled ->
                musicControllerUiState = musicControllerUiState.copy(
                    playerState = playerState,
                    currentSong = currentMusic,
                    currentPosition = currentPosition,
                    totalDuration = totalDuration,
                    isShuffleEnabled = isShuffleEnabled,
                    isRepeatOneEnabled = isRepeatOneEnabled
                )

                if (playerState == PlayerState.PLAYING) {
                    viewModelScope.launch {
                        while (true) {
                            delay(3.seconds)
                            musicControllerUiState = musicControllerUiState.copy(
                                currentPosition = musicController.getCurrentPosition()
                            )
                        }
                    }
                }
            }
    }

    fun destroyMediaController() {
        musicController.destroy()
    }

}