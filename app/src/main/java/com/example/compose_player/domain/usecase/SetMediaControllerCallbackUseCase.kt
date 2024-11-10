package com.example.compose_player.domain.usecase

import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.other.PlayerState
import com.example.compose_player.domain.service.MusicController
import javax.inject.Inject

class SetMediaControllerCallbackUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(
        callback: (
            playerState: PlayerState,
            currentSong: Song?,
            currentPosition: Long,
            totalDuration: Long,
            isShuffleEnabled: Boolean,
            isRepeatOneEnabled: Boolean
        ) -> Unit
    ) {
        musicController.mediaControllerCallback = callback
    }
}