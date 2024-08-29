package com.example.compose_player.domain.usecase

import com.example.compose_player.domain.service.MusicController
import javax.inject.Inject

class DestroyMediaControllerUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke()
    {
        musicController.destroy()
    }

}