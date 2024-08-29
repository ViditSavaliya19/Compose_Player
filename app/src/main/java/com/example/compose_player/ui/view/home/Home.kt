package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.compose_player.R
import com.example.compose_player.data.model.MusicControllerUiState
import com.example.compose_player.domain.model.Song
import com.example.compose_player.domain.other.PlayerState
import com.example.compose_player.ui.view.home.component.VinylAnimation
import kotlinx.coroutines.launch


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Home(
    modifier: Modifier = Modifier, navController: NavHostController, onEvent: (HomeEvent) -> Unit,
    uiState: HomeUiState,
    playerState: PlayerState?,
    musicControllerUiState: MusicControllerUiState
) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        skipHiddenState = true,
        initialValue = SheetValue.PartiallyExpanded,
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    BottomSheetScaffold(
        modifier = modifier,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            Log.e("TAG", "Home: ------>>>> $dragAmount")
                            if (dragAmount > 0) {
                                coroutineScope.launch {
                                    sheetState.partialExpand()
                                }
                            } else {
                                coroutineScope.launch {
                                    sheetState.expand()
                                }
                            }
                        }
                    }
            ) {
                with(uiState) {
                    when {
                        loading == true -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LinearProgressIndicator()
                            }
                        }

                        loading == false && errorMessage == null -> {
                            PlayerView(
                                onEvent,
                                playerState,
                                uiState,
                                musicControllerUiState,
                                sheetState.currentValue
                            )
                        }

                        errorMessage != null -> {
                            Text(text = errorMessage)
                        }
                    }

                }
            }
        },
        sheetShape = RoundedCornerShape(20.dp),//Rounded corners
        sheetPeekHeight = 200.dp,
        scaffoldState = bottomSheetScaffoldState,

        ) {
        Box(Modifier.padding(top = 20.dp)) {

            with(uiState) {
                when {
                    loading == true -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    loading == false && errorMessage == null -> {

                        if (songs != null) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.padding(it),
                            ) {
                                items(count = songs.size) {
                                    MusicItem(song = songs[it]) {
                                        onEvent(HomeEvent.OnSongSelected(songs[it]))
                                        onEvent(HomeEvent.PlaySong)
                                    }

                                }
                            }
                        }
                    }

                    errorMessage != null -> {
                        Text(text = errorMessage)
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun PlayerView(
    onEvent: (HomeEvent) -> Unit,
    playerState: PlayerState?,
    uiState: HomeUiState,
    musicControllerUiState: MusicControllerUiState,
    currentValue: SheetValue,
) {

    val animatedAlign by animateIntOffsetAsState(
        targetValue = if (currentValue == SheetValue.PartiallyExpanded) {
            IntOffset.Zero
        } else {
            IntOffset(0, 1600)
        },
        label = "offset"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        GlideImage(
            model = musicControllerUiState.currentSong?.imageUrl,
            contentDescription = "",
            loading = placeholder(
                R.drawable.ic_launcher_background
            ),
            modifier = Modifier
                .fillMaxSize(),
            colorFilter = ColorFilter.tint(
                color = Color.Black.copy(0.9f),
                blendMode = BlendMode.SrcOver
            ),
            contentScale = ContentScale.Crop
        )
        Column(
            Modifier
                .fillMaxWidth()
                .offset {
                    animatedAlign
                },
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Top,
//            verticalArrangement = if (currentValue == SheetValue.PartiallyExpanded) Arrangement.Top else Arrangement.Bottom,
        ) {
            LinearProgressIndicator(
                progress = { musicControllerUiState.currentPosition.toFloat() / musicControllerUiState.totalDuration.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ControlButton(icon = R.drawable.baseline_skip_previous_24, size = 50.dp, onClick = {
                    onEvent.invoke(HomeEvent.SkipToPreviousSong)
                })
                Spacer(modifier = Modifier.width(20.dp))
                Log.e("Screen State", "PlayerView: $$$$$ ${playerState?.name}")

                ControlButton(icon = if (playerState == PlayerState.PLAYING) R.drawable.baseline_pause_circle_outline_24 else R.drawable.baseline_play_circle_outline_24,
                    size = 100.dp,
                    onClick = {

                        if (playerState == PlayerState.PLAYING) {
                            onEvent(HomeEvent.PauseSong)
                        } else {
                            onEvent(HomeEvent.ResumeSong)
                        }
                    })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(icon = R.drawable.baseline_skip_next_24, size = 50.dp, onClick = {
                    onEvent.invoke(HomeEvent.SkipToNextSong)
                })
            }
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                "${musicControllerUiState.currentSong?.title}",
                Modifier.basicMarquee(),
            )
            Spacer(modifier = Modifier.height(20.dp))

        }
        musicControllerUiState.currentSong?.imageUrl?.let {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                VinylAnimation(
                    imageUrl = it,
                    isPlaySong = musicControllerUiState.playerState == PlayerState.PLAYING
                )
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MusicItem(song: Song, onClick: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.padding(10.dp), onClick = { onClick() }) {
            GlideImage(
                model = song.imageUrl, contentDescription = "", loading = placeholder(
                    R.drawable.ic_launcher_background
                ), modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = song.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(fraction = 0.7f),
            maxLines = 1
        )
    }
}


@Composable
fun ControlButton(icon: Int, size: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 1.5f),
            painter = painterResource(id = icon),
            contentDescription = null
        )
    }
}

