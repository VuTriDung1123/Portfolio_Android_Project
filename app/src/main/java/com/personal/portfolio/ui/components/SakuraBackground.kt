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
import kotlin.math.sin
import kotlin.random.Random

// Dữ liệu cánh hoa
private data class Petal(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val rotationSpeed: Float,
    val initialRotation: Float,
    val swayAmount: Float,
    val swaySpeed: Float,
    var offset: Float
)

@Composable
fun SakuraFallingEffect() {
    LocalDensity.current
    val petals = remember {
        List(30) {
            Petal(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                size = Random.nextFloat() * 15f + 10f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
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
            animation = tween(
                40000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "Time"
    )

    val reusablePath = remember { Path() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.8f }) {
        val width = size.width
        val height = size.height

        petals.forEach { petal ->
            val currentY = (petal.y + (time * petal.speed * 0.01f)) % 1.2f
            val sway = sin(time * petal.swaySpeed * 0.005f + petal.offset) * petal.swayAmount
            val currentX = (petal.x + sway) * width
            val drawY = if (currentY > 1f) -100f else currentY * height
            val rotation = petal.initialRotation + (time * petal.rotationSpeed)

            if (drawY > -50f && drawY < height + 50f) {
                rotate(degrees = rotation, pivot = Offset(currentX, drawY)) {
                    reusablePath.reset()
                    reusablePath.apply {
                        moveTo(currentX, drawY)
                        cubicTo(
                            currentX + petal.size / 2,
                            drawY - petal.size / 2,
                            currentX + petal.size,
                            drawY + petal.size / 2,
                            currentX,
                            drawY + petal.size
                        )
                        cubicTo(
                            currentX - petal.size,
                            drawY + petal.size / 2,
                            currentX - petal.size / 2,
                            drawY - petal.size / 2,
                            currentX,
                            drawY
                        )
                        close()
                    }
                    drawPath(path = reusablePath, color = Color(0xFFFFC1E3).copy(alpha = 0.7f))
                }
            }
        }
    }
}