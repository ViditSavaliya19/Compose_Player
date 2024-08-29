package com.example.compose_player.domain.usecase

import androidx.media3.session.MediaController
import com.example.compose_player.domain.service.MusicController
import javax.inject.Inject

class GetCurrentSongPositionUseCase @Inject constructor(val musicController: MusicController) {

    operator fun invoke() = musicController.getCurrentPosition()

}