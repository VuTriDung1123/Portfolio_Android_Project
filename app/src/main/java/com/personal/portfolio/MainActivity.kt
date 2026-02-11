package com.personal.portfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.personal.portfolio.ui.screens.*
import com.personal.portfolio.ui.theme.SakuraPortfolioTheme
import com.personal.portfolio.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SakuraPortfolioTheme {
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel() // ViewModel dùng chung cho toàn app

                NavHost(navController = navController, startDestination = "home") {
                    // 1. Trang Chủ
                    composable("home") {
                        HomeScreen(navController, viewModel)
                    }

                    // 2. Trang Blog (Danh sách & Lọc)
                    composable("blog") {
                        BlogScreen(navController, viewModel)
                    }

                    // 3. Trang FAQ
                    composable("faq") {
                        FAQScreen(navController, viewModel)
                    }

                    // 4. Trang Chi tiết bài viết (Nhận ID)
                    composable("post_detail/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId") ?: ""
                        PostDetailScreen(navController, viewModel, postId)
                    }
                }
            }
        }
    }
}