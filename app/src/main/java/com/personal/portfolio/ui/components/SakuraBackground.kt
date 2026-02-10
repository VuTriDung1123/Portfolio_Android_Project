package com.personal.portfolio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

// Dữ liệu của một cánh hoa
private data class Petal(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val swayAmount: Float, // Độ lắc lư ngang
    val swaySpeed: Float,  // Tốc độ lắc
    var offset: Float      // Offset thời gian để lắc không đều nhau
)

@Composable
fun SakuraFallingEffect() {
    val density = LocalDensity.current
    val petals = remember {
        List(40) { // Số lượng cánh hoa: 40
            Petal(
                x = Random.nextFloat(), // Vị trí ngẫu nhiên 0.0 -> 1.0 (tỉ lệ màn hình)
                y = Random.nextFloat() * -1f, // Bắt đầu từ phía trên màn hình
                size = Random.nextFloat() * 10f + 5f,
                speed = Random.nextFloat() * 2f + 1f,
                swayAmount = Random.nextFloat() * 2f + 0.5f,
                swaySpeed = Random.nextFloat() * 3f + 1f,
                offset = Random.nextFloat() * 100f
            )
        }
    }

    // Animation Loop vô tận
    val infiniteTransition = rememberInfiniteTransition(label = "SakuraLoop")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Time"
    )

    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.6f }) {
        val width = size.width
        val height = size.height

        petals.forEach { petal ->
            // Tính toán vị trí Y (rơi xuống)
            // Dùng time để cập nhật vị trí liên tục
            var currentY = (petal.y + (time * petal.speed * 0.05f)) % 1.5f
            // % 1.5f để khi rơi quá màn hình nó sẽ quay lại đỉnh (logic giả lập loop)

            // Tính toán vị trí X (lắc lư hình sin)
            val sway = sin(time * petal.swaySpeed * 0.01f + petal.offset) * petal.swayAmount * 10f
            val currentX = (petal.x * width) + sway

            // Chuyển đổi Y sang pixel thực tế
            val drawY = if (currentY > 1f) -100f else currentY * height

            // Vẽ cánh hoa (Hình tròn màu hồng phấn)
            if (drawY > -50f && drawY < height + 50f) {
                drawCircle(
                    color = Color(0xFFFFC1E3), // SakuraSecondary
                    radius = petal.size,
                    center = Offset(currentX, drawY)
                )
            }
        }
    }
}