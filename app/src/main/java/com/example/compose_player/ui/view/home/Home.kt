package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.utils.network.DataState


@SuppressLint("MutableCollectionMutableState")
@Composable
fun Home(modifier: Modifier = Modifier, navController: NavHostController) {
    val musicList = remember { mutableStateOf(arrayListOf<MusicModelItem>()) }

    val homeViewModel = hiltViewModel<HomeViewModel>()

    LaunchedEffect(key1 = 0) {
        homeViewModel.getMusicData()
    }

    if (homeViewModel.musicData.value is DataState.Success) {
        musicList.value =
            (homeViewModel.musicData.value as DataState.Success<List<MusicModelItem>>).data as ArrayList<MusicModelItem>
    }


    Scaffold {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(musicList.value.size) {
                MusicItem(musicList.value[it])
            }
        }
    }
}

@Composable
fun MusicItem(musicModelItem: MusicModelItem) {
        Text(text = musicModelItem.song?:"")
}