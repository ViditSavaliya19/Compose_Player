package com.example.compose_player.ui.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.compose_player.data.services.MusicPlayerService
import com.example.compose_player.navigation.NavHostScreen
import com.example.compose_player.ui.theme.Compose_PlayerTheme
import com.example.compose_player.ui.viewmodel.SharedViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModels by viewModels()
    private val splashViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition { splashViewModel.isLoading.value }
        }
        setContent {

            var hasNotificationPermission = remember { mutableStateOf(false) }

            // Request notification permission and update state based on the result
            val permissionResult = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { hasNotificationPermission.value = it }
            )

            // Request notification permission when the component is launched
            LaunchedEffect(key1 = true) {
                permissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            Compose_PlayerTheme {
                val navController = rememberNavController()
                NavHostScreen(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.destroyMediaController()
        stopService(Intent(this, MusicPlayerService::class.java))
    }

}

