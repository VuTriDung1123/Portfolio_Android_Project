package com.personal.portfolio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Cấu hình Light Theme (Sakura chủ yếu dùng Light Mode)
private val SakuraColorScheme = lightColorScheme(
    primary = SakuraPrimary,
    secondary = SakuraSecondary,
    background = SakuraBg,
    surface = SakuraGlass,
    onPrimary = Color.White,
    onSecondary = SakuraTextDark,
    onBackground = SakuraTextDark,
    onSurface = SakuraTextDark
)

@Composable
fun SakuraPortfolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Tạm thời ưu tiên Light Theme
    content: @Composable () -> Unit
) {
    // Sakura Portfolio luôn dùng Light Scheme để giữ vẻ tươi sáng
    val colorScheme = SakuraColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Thanh trạng thái màu hồng nhạt
            window.statusBarColor = SakuraBg.toArgb()
            // Icon thanh trạng thái màu tối
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Sẽ update file Type.kt sau
        content = content
    )
}