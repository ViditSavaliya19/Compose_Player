package com.example.compose_player.ui.view.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.compose_player.R
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.utils.network.DataState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Home(modifier: Modifier = Modifier, navController: NavHostController) {
    val musicList = remember { mutableStateOf(arrayListOf<MusicModelItem>()) }
    var sheetState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )
    val homeViewModel = hiltViewModel<HomeViewModel>()
    LaunchedEffect(key1 = 0) {
        homeViewModel.getMusicData()
    }
    if (homeViewModel.musicData.value is DataState.Success) {
        musicList.value =
            (homeViewModel.musicData.value as DataState.Success<List<MusicModelItem>>).data as ArrayList<MusicModelItem>
    }

    Scaffold {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(it),
        ) {
            items(musicList.value.size) {
                MusicItem(musicList.value[it]) {
                    sheetState.value = !sheetState.value
                    coroutineScope.launch {
                        sheetScaffoldState.bottomSheetState.expand()
                    }
                }
            }
        }

        if (sheetState.value) {
            BottomSheetScaffold(
                sheetContent = {
                    ElevatedButton(onClick = {
                        sheetState.value = false
                    }) {
                        Text("Show Bottom Sheet")
                    }
                },
                sheetShape = RoundedCornerShape(20.dp),//Rounded corners
                sheetPeekHeight = 200.dp,
                scaffoldState = sheetScaffoldState,
            ) {
                PlayerSheet()
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
fun PlayerSheet(modifier: Modifier = Modifier) {

}