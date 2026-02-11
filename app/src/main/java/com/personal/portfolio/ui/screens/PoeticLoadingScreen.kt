package com.personal.portfolio.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.SakuraPrimary
import com.personal.portfolio.ui.theme.SakuraTextDark
import kotlinx.coroutines.delay

@Composable
fun PoeticLoadingScreen(onFinished: () -> Unit) {
    // 3 câu thơ cho 3 ngôn ngữ (Bạn có thể sửa lại theo ý thích)
    val poems = listOf(
        "Code là gió, Bug là mây\nEm là nắng, giữa trời tây...", // VI
        "Code is poetry written in binary,\nA silent language of logic and mystery...", // EN
        "桜舞う (Sakura mau)\nコードの中に (Kōdo no naka ni)\n夢を見る (Yume wo miru)..." // JP
    )

    var currentLineIndex by remember { mutableStateOf(0) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Hiệu ứng hiện từng dòng thơ
        poems.forEachIndexed { index, _ ->
            currentLineIndex = index
            alphaAnim.animateTo(1f, animationSpec = tween(1000)) // Hiện trong 1s
            delay(1500) // Đọc trong 1.5s
            alphaAnim.animateTo(0f, animationSpec = tween(800)) // Ẩn trong 0.8s
        }
        onFinished() // Xong thì vào Home
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        SakuraFallingEffect() // Vẫn có hoa rơi

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🌸",
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = poems[currentLineIndex],
                color = SakuraTextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .alpha(alphaAnim.value) // Áp dụng độ mờ
            )
        }
    }
}