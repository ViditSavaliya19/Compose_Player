package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.compose_player.R
import com.example.compose_player.data.model.MusicModelItem


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Home(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val musicList = homeViewModel.musicList.observeAsState()
    homeViewModel.getContext(context)

    var isPlay = rememberSaveable {
        mutableStateOf(false)
    }



    BottomSheetScaffold(
        sheetContent = {
            PlayerView(homeViewModel)
        },
        sheetShape = RoundedCornerShape(20.dp),//Rounded corners
        sheetPeekHeight = 200.dp,
        scaffoldState = rememberBottomSheetScaffoldState(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(it),
        ) {
            musicList.value?.let { it1 ->
                items(it1.size) {
                    MusicItem(musicList.value!![it]) {
                        if(!isPlay.value)
                        {
                            isPlay.value= true
//                            homeViewModel.controllerFuture.get().play()
                            Log.e("Song", "Home: Song Playing.....", )
                        }
                        else
                        {
                            isPlay.value= false
//                            homeViewModel.controllerFuture.get().pause()
                            Log.e("Song", "Home: Song Pause.....", )

                        }
//                        homeViewModel.clickToPlay(it)
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayerView(
    viewModel: HomeViewModel
) {

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LinearProgressIndicator(
                progress = {0f },
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ControlButton(icon = R.drawable.baseline_skip_previous_24, size = 50.dp, onClick = {
                })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(icon = R.drawable.baseline_play_circle_outline_24,
                    size = 100.dp,
                    onClick = {
                    })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(icon = R.drawable.baseline_skip_next_24, size = 50.dp, onClick = {
                })
            }
            Spacer(modifier = Modifier.height(5.dp))

            if (viewModel.musicList.value!!.isNotEmpty()) {
                Text(
                    "song",
                    Modifier.basicMarquee(),
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MusicItem(musicModelItem: MusicModelItem, onClick: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.padding(10.dp), onClick = { onClick() }) {
            GlideImage(
                model = musicModelItem.data!!.image, contentDescription = "", loading = placeholder(
                    R.drawable.ic_launcher_background
                ), modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = musicModelItem.song ?: "",
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

