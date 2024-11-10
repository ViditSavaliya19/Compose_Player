package com.example.compose_player.ui.view.home.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.compose_player.R

@Composable
fun VinylAnimation(modifier: Modifier = Modifier, isPlaySong: Boolean = true, imageUrl: String) {
    var rotationDegree by remember {
        mutableStateOf(0f)
    }
    val rotation = remember {
        Animatable(rotationDegree)
    }
    LaunchedEffect(isPlaySong) {

        if (isPlaySong) {
            rotation.animateTo(
                targetValue = rotationDegree + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                rotationDegree = value
            }
        } else {
            if (rotationDegree > 0f) {
                rotation.animateTo(
                    targetValue = rotationDegree + 50, animationSpec = tween(
                        1250, easing = LinearOutSlowInEasing
                    )
                ) {
                    rotationDegree = value
                }
            }
        }

    }

    Vinyl(imageUrl = imageUrl, rotate = rotationDegree)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Vinyl(modifier: Modifier = Modifier, rotate: Float = 0f, imageUrl: String) {
    Box(
        modifier = modifier.aspectRatio(1.0f),
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = modifier
                .width(350.dp)
                .height(350.dp)
                .rotate(rotate),
            painter = painterResource(id = R.drawable.vinyl_background),
            contentDescription = ""
        )

        GlideImage(
            model = imageUrl, contentDescription = "", loading = placeholder(
                R.drawable.ic_launcher_background
            ), modifier = Modifier
                .height(150.dp)
                .rotate(rotate)
                .width(150.dp)
                .clip(CircleShape)
        )
    }


}   