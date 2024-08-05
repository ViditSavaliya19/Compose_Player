package com.example.compose_player.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()

    val mediaSource = remember("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4") {
        MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
    }

    val mediaSourceFactory: MediaSource.Factory = remember {
        DefaultMediaSourceFactory(context)
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }




    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Video Player") })
    }) {
        Column(modifier = modifier.padding(it))
        {
            AndroidView(factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            })
        }

    }

}