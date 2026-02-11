package com.personal.portfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class) // [FIX] Thêm OptIn
@Composable
fun PostDetailScreen(navController: NavController, viewModel: HomeViewModel, postId: String) {
    val post = viewModel.getPostById(postId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post?.title ?: "Chi tiết", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SakuraGlass)
            )
        }
    ) { padding ->
        if (post == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) { Text("Không tìm thấy bài viết!") }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Ảnh Cover
                val imgUrl = try {
                    val list = Gson().fromJson(post.images, Array<String>::class.java)
                    if (list.isNotEmpty()) list[0] else null
                } catch (e: Exception) { null }

                if (imgUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imgUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Tag & Title
                Text(
                    text = post.tag,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.background(SakuraPrimary, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = post.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakuraTextDark
                )
                Spacer(Modifier.height(16.dp))

                // Nội dung
                Text(
                    text = post.content,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = SakuraTextMain // Đã khai báo trong Color.kt
                )
            }
        }
    }
}