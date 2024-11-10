package com.example.compose_player.domain.usecase

import androidx.media3.session.MediaController
import com.example.compose_player.domain.service.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(mediaItemIndex:Int) {
        musicController.play(mediaItemIndex)
    }

}