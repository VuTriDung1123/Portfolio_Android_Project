package com.personal.portfolio

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.personal.portfolio.ui.screens.BlogScreen
import com.personal.portfolio.ui.screens.FAQScreen
import com.personal.portfolio.ui.screens.HomeScreen
import com.personal.portfolio.ui.screens.PostDetailScreen
import com.personal.portfolio.ui.theme.SakuraPortfolioTheme
import com.personal.portfolio.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SakuraPortfolioTheme {
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    // 1. Trang Chủ
                    composable("home") {
                        HomeScreen(navController, viewModel)
                    }

                    // 2. Trang Blog (Danh sách bài viết)
                    composable("blog") {
                        BlogScreen(navController, viewModel)
                    }

                    // 3. Trang FAQ
                    composable("faq") {
                        FAQScreen(navController, viewModel)
                    }

                    // 4. Trang Chi tiết bài viết (Dùng bản khai báo đầy đủ tham số)
                    composable(
                        route = "post_detail/{postId}", arguments = listOf(
                        navArgument("postId") {
                            type = NavType.StringType
                        })) { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")

                        // Truyền đúng thứ tự: postId, navController, viewModel
                        PostDetailScreen(
                            postId = postId, navController = navController, viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}