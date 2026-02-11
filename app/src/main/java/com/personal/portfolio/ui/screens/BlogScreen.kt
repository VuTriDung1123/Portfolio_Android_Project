package com.personal.portfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.personal.portfolio.ui.components.ProjectPostCard // [FIX] Đã import
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@Composable
fun BlogScreen(navController: NavController, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val tags = listOf(
        "ALL" to "Tất cả",
        "uni_projects" to "Dự án ĐH",
        "personal_projects" to "Dự án Cá nhân",
        "my_confessions" to "Blog/Tâm sự",
        "achievements" to "Thành tựu",
        "lang_certs" to "Chứng chỉ",
        "it_events" to "Sự kiện IT"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SakuraFallingEffect()

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("🌸 THƯ VIỆN BÀI VIẾT", style = MaterialTheme.typography.headlineMedium, color = SakuraPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            // Bộ lọc Horizontal
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 10.dp)) {
                tags.forEach { (tagKey, tagName) ->
                    val isSelected = uiState.selectedTag == tagKey
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SakuraPrimary else Color.White)
                            .border(1.dp, SakuraSecondary, CircleShape)
                            .clickable { viewModel.filterPosts(tagKey) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(tagName, color = if (isSelected) Color.White else SakuraTextLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Danh sách bài viết
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                if (uiState.filteredPosts.isEmpty()) {
                    item { Text("Không tìm thấy bài viết nào...", modifier = Modifier.padding(20.dp), color = SakuraTextLight) }
                } else {
                    items(uiState.filteredPosts) { post ->
                        Box(Modifier.clickable { navController.navigate("post_detail/${post.id}") }) {
                            ProjectPostCard(post) // Sử dụng Component chung
                        }
                    }
                }
            }
        }
    }
}