package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.reguerta.presentation.R

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
    val animatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        composition?.let {
            println("🔍 Composition loaded: $composition")
            animatable.animate(
                composition = it,
                iterations = com.airbnb.lottie.compose.LottieConstants.IterateForever,
                speed = 5f
            )
        }
    }

    LaunchedEffect(animatable.progress) {
        println("🌱 Lottie progress: ${animatable.progress}")
    }

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        modifier = modifier.size(120.dp)
    )
}