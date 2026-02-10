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

// Giữ nguyên bảng màu
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
    lang: String = "en", // [MỚI] Thêm tham số ngôn ngữ
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = SakuraColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SakuraBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // [MỚI] Gọi hàm lấy Typography theo ngôn ngữ từ Type.kt
        typography = getTypography(lang),
        content = content
    )
}