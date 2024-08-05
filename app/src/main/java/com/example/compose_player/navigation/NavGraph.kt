package com.example.compose_player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose_player.ui.view.VideoPlayerScreen
import com.example.compose_player.ui.view.home.Home

@Composable
fun NavHostScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    NavHost(navController = navController, startDestination = "/", modifier = modifier)
    {
        composable(route = "/") {
            Home(modifier = modifier,navController)
        }
        composable(route = "video") {
            VideoPlayerScreen(modifier = modifier,navController)
        }
    }

}