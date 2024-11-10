package com.example.compose_player.data.mapper

import androidx.media3.common.MediaItem
import com.example.compose_player.domain.model.Song

fun MediaItem.toSong() =
    Song(
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        subtitle = mediaMetadata.subtitle.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString()
    )