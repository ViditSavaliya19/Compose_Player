package com.example.compose_player.domain.usecase

import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.service.MusicController
import javax.inject.Inject

class SkipToNextSongUseCase @Inject constructor(private val musicController:    MusicController) {
    operator fun invoke(updateHomeUi: (Song?) -> Unit) {
        musicController.skipToNextSong()
        updateHomeUi(musicController.getCurrentSong())
    }
}