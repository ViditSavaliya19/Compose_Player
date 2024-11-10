package com.example.compose_player.data.model

import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.other.PlayerState

data class MusicControllerUiState (
    val playerState: PlayerState? = null,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false
)