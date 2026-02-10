package com.personal.portfolio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.personal.portfolio.R

// 1. Định nghĩa các bộ Font
val FontEN = FontFamily(Font(R.font.noto_sans_regular)) // Đảm bảo bạn đã có file này
val FontVI = FontFamily(Font(R.font.noto_serif_regular)) // Đảm bảo bạn đã có file này
val FontJP = FontFamily(Font(R.font.noto_serif_jp_regular)) // Đảm bảo bạn đã có file này

// 2. Hàm lấy Typography theo ngôn ngữ
fun getTypography(lang: String): Typography {
    val selectedFont = when (lang) {
        "vi" -> FontVI
        "jp" -> FontJP
        else -> FontEN // Mặc định là EN
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp
        ),
        displayMedium = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            lineHeight = 52.sp
        ),
        displaySmall = TextStyle( // Dùng cho Tên to ở Hero
            fontFamily = selectedFont,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),
        titleMedium = TextStyle( // Dùng cho tiêu đề Section
            fontFamily = selectedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        bodyLarge = TextStyle( // Dùng cho nội dung chính
            fontFamily = selectedFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelSmall = TextStyle( // Dùng cho badge, tag
            fontFamily = selectedFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}