package com.personal.portfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
// Import R từ đúng package mới
import com.personal.portfolio.R

@Composable
fun SakuraAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(55.dp),
        contentAlignment = Alignment.Center
    ) {
        // Lớp dưới: Ảnh thật
        Image(
            painter = painterResource(id = R.drawable.vutridung),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(0.8f)
                .clip(CircleShape)
        )
        // Lớp trên: Khung Sakura
        Image(
            painter = painterResource(id = R.drawable.sakura_avatar),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}