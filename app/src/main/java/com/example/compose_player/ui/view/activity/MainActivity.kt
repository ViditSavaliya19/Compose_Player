package com.example.compose_player.ui.view.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.example.compose_player.navigation.NavHostScreen
import com.example.compose_player.ui.theme.Compose_PlayerTheme
import com.example.compose_player.utils.MusicPlayerService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                NavHostScreen(navController = navController)
            }
        }
    }

}

