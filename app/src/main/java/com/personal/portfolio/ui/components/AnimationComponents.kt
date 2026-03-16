package com.personal.portfolio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun ScrollReveal(
    delayMillis: Long = 0L,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis) // Độ trễ để tạo hiệu ứng nối tiếp giống Web (delay = i * 0.1)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 100 }, // Trượt từ dưới lên 100px
            animationSpec = tween(durationMillis = 700)
        ) + fadeIn(animationSpec = tween(durationMillis = 700))
    ) {
        Box { content() }
    }
}