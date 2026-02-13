package com.personal.portfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.personal.portfolio.ui.viewmodel.HomeViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.personal.portfolio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String?,
    navController: NavController, // Sửa kiểu dữ liệu ở đây
    viewModel: HomeViewModel = viewModel() // Sửa kiểu dữ liệu ở đây
) {
    // Thu thập State từ ViewModel - Cần "import androidx.compose.runtime.getValue"
    val uiState by viewModel.uiState.collectAsState()

    // Tìm bài viết khớp với ID
    val post = uiState.allPosts.find { it.id == postId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post?.title ?: "Loading...", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Bây giờ popBackStack đã hiểu
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFE4E1))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (post != null) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ngày đăng: ${post.createdAt}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // --- LOGIC TRÍCH XUẤT MẢNG ẢNH TỪ JSON STRING ---
                // 1. Trích xuất danh sách link từ chuỗi database
                val images = remember(post.images) {
                    try {
                        // Tìm tất cả các đoạn bắt đầu bằng http và kết thúc trước dấu nháy kép
                        val regex = "(https?://[^\"]+)".toRegex()
                        regex.findAll(post.images).map { it.value.trim() }.toList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                // 2. Hiển thị danh sách ảnh (Tự động theo số lượng trong list 'images')
                if (images.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ở đây images.size sẽ là 2, 4, hoặc 10 tùy vào dữ liệu bài viết
                        items(images.size) { index ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = images[index] // Lấy đúng ảnh theo vị trí
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(width = 280.dp, height = 180.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = Color(0xFF4A4A4A)
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Không tìm thấy nội dung bài viết.")
                }
            }
        }
    }
}