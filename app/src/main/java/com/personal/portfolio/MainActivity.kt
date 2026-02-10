package com.personal.portfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.personal.portfolio.ui.screens.HomeScreen
import com.personal.portfolio.ui.theme.SakuraPortfolioTheme // Đảm bảo dòng import này đúng

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tên hàm này phải khớp với hàm @Composable trong file Theme.kt
            SakuraPortfolioTheme {
                HomeScreen()
            }
        }
    }
}