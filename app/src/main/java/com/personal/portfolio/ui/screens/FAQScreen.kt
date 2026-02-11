package com.personal.portfolio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personal.portfolio.ui.components.FAQItem // Import đúng
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class) // [FIX] Thêm OptIn
@Composable
fun FAQScreen(navController: NavController, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize()) {
        SakuraFallingEffect()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Dùng TopAppBar thường thay vì SmallTopAppBar (để ổn định hơn)
                TopAppBar(
                    title = { Text("HỎI ĐÁP (FAQ)", fontWeight = FontWeight.Bold, color = SakuraPrimary) },
                    navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SakuraGlass) // [FIX] Sửa tên tham số màu
                )
            }
        ) { padding ->
            LazyColumn(
                Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.faq.isEmpty()) {
                    item { Text("Chưa có câu hỏi nào.", color = SakuraTextLight) }
                } else {
                    items(uiState.faq) { item ->
                        FAQItem(question = item.q, answer = item.a)
                    }
                }
            }
        }
    }
}