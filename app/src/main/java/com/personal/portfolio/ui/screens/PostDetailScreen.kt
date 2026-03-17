package com.personal.portfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.personal.portfolio.ui.viewmodel.HomeViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.compose.ui.text.TextStyle
import com.personal.portfolio.ui.theme.SakuraGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String?,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val post = uiState.allPosts.find { it.id == postId }

    Scaffold(
        containerColor = SakuraGlass, // Áp dụng nền kính giống web
        topBar = {
            TopAppBar(
                title = { Text(post?.tag ?: "Story", fontSize = 14.sp, color = Color.Gray) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (post != null) {
                // Header giống Web
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF5D4037)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "Đăng ngày: ${post.createdAt.toString().take(10)}", color = Color.Gray, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFFFC1E3))

                // Nội dung Markdown (Thay vì Text thường)
                MarkdownText(
                    markdown = post.content ?: "",
                    style = TextStyle(
                        color = Color(0xFF4A3B32),
                        fontSize = 16.sp,
                        lineHeight = 26.sp
                    )
                )

                // Gallery Ảnh
                val images = remember(post.images) {
                    try {
                        val regex = "(https?://[^\"]+)".toRegex()
                        regex.findAll(post.images ?: "").map { it.value.trim() }.toList()
                    } catch (_: Exception) { emptyList() }
                }

                if (images.isNotEmpty()) {
                    Spacer(Modifier.height(30.dp))
                    Text("📸 Gallery", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF69B4))
                    Spacer(Modifier.height(16.dp))

                    images.forEachIndexed { _, imgUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imgUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp, max = 400.dp) // Auto height
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy nội dung bài viết 🍃")
                }
            }
        }
    }
}