package com.personal.portfolio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personal.portfolio.ui.components.FAQItem
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.SakuraGlass
import com.personal.portfolio.ui.theme.SakuraPrimary
import com.personal.portfolio.ui.theme.SakuraTextLight
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
                    title = {
                        Text(
                            "HỎI ĐÁP (FAQ)",
                            fontWeight = FontWeight.Bold,
                            color = SakuraPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SakuraGlass)
                )
            }
        ) { padding ->
            LazyColumn(
                Modifier
                    .padding(padding)
                    .padding(16.dp),
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