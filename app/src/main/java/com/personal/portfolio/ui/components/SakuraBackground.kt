package com.personal.portfolio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

// Dữ liệu cánh hoa
private data class Petal(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val rotationSpeed: Float, // Tốc độ xoay
    val initialRotation: Float,
    val swayAmount: Float,
    val swaySpeed: Float,
    var offset: Float
)

@Composable
fun SakuraFallingEffect() {
    val density = LocalDensity.current
    val petals = remember {
        List(30) { // Giảm số lượng chút cho đỡ rối
            Petal(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                size = Random.nextFloat() * 15f + 10f, // Kích thước to hơn chút
                speed = Random.nextFloat() * 0.5f + 0.2f, // [FIX] Tốc độ RẤT CHẬM (0.2 -> 0.7)
                rotationSpeed = Random.nextFloat() * 2f - 1f,
                initialRotation = Random.nextFloat() * 360f,
                swayAmount = Random.nextFloat() * 0.1f + 0.05f,
                swaySpeed = Random.nextFloat() * 2f + 1f,
                offset = Random.nextFloat() * 100f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SakuraLoop")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing), // [FIX] Tăng thời gian loop -> Chuyển động mượt chậm
            repeatMode = RepeatMode.Restart
        ), label = "Time"
    )

    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.8f }) {
        val width = size.width
        val height = size.height

        petals.forEach { petal ->
            // Tính toán vị trí
            var currentY = (petal.y + (time * petal.speed * 0.01f)) % 1.2f
            val sway = sin(time * petal.swaySpeed * 0.005f + petal.offset) * petal.swayAmount
            val currentX = (petal.x + sway) * width
            val drawY = if (currentY > 1f) -100f else currentY * height

            // Góc xoay
            val rotation = petal.initialRotation + (time * petal.rotationSpeed)

            if (drawY > -50f && drawY < height + 50f) {
                // [FIX] Vẽ hình cánh hoa thay vì hình tròn
                rotate(degrees = rotation, pivot = Offset(currentX, drawY)) {
                    val path = Path().apply {
                        moveTo(currentX, drawY)
                        // Vẽ đường cong Bezier tạo hình cánh hoa
                        cubicTo(
                            currentX + petal.size / 2, drawY - petal.size / 2,
                            currentX + petal.size, drawY + petal.size / 2,
                            currentX, drawY + petal.size
                        )
                        cubicTo(
                            currentX - petal.size, drawY + petal.size / 2,
                            currentX - petal.size / 2, drawY - petal.size / 2,
                            currentX, drawY
                        )
                        close()
                    }
                    // Màu hồng phấn đậm nhạt ngẫu nhiên
                    drawPath(path = path, color = Color(0xFFFFC1E3).copy(alpha = 0.7f))
                }
            }
        }
    }
}