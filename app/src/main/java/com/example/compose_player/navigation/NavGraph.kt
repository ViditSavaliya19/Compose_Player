package com.example.compose_player.navigation

import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose_player.ui.view.VideoPlayerScreen
import com.example.compose_player.ui.view.home.Home
import com.example.compose_player.ui.view.home.HomeEvent
import com.example.compose_player.ui.view.home.HomeViewModel
import com.example.compose_player.ui.viewmodel.SharedViewModels

@Composable
fun NavHostScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sharedViewModel: SharedViewModels
) {

    val musicControllerUiState = sharedViewModel.musicControllerUiState
    val activity = (LocalContext.current as ComponentActivity)

    NavHost(navController = navController, startDestination = "/", modifier = modifier)
    {
        composable(route = "/") {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val isInitialized = rememberSaveable { mutableStateOf(false) }
            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    homeViewModel.onEvent(HomeEvent.FetchSong)
                    isInitialized.value = true
                }
            }
            Home(
                modifier = modifier, navController,
                onEvent = homeViewModel::onEvent,
                uiState = homeViewModel.homeUiState,
                playerState = musicControllerUiState.playerState,
                musicControllerUiState
            )
        }
        composable(route = "video") {
            VideoPlayerScreen(modifier = modifier, navController)
        }
    }

}