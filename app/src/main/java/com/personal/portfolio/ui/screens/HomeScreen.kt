package com.personal.portfolio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.personal.portfolio.data.SakuraData
import com.personal.portfolio.ui.components.*
import com.personal.portfolio.ui.theme.*

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var showChatDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. LỚP NỀN: Cánh hoa rơi (Đã fix tốc độ & hình dáng)
        SakuraFallingEffect()

        // 2. LỚP GIAO DIỆN CHÍNH
        Scaffold(
            containerColor = Color.Transparent,
            // [FIX] Thêm TopNav vào đây
            topBar = {
                SakuraTopNav()
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showChatDialog = true },
                    containerColor = SakuraPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(65.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Chat")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // --- A. HERO SECTION (Avatar to & Intro) ---
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // [FIX] Dùng SakuraAvatar thật thay vì ảnh tạm
                        // Avatar to ở giữa màn hình
                        SakuraAvatar(modifier = Modifier.size(160.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = SakuraData.hero["greeting"] ?: "Hello",
                            color = SakuraPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = SakuraData.hero["name"] ?: "",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = SakuraTextDark
                        )

                        // Sub-names
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Badge(text = "🇬🇧 ${SakuraData.hero["sub_name_1"]}")
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(text = "🇯🇵 ${SakuraData.hero["sub_name_2"]}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = SakuraData.hero["desc"] ?: "",
                            textAlign = TextAlign.Center,
                            color = SakuraTextLight,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                // --- B. ABOUT ME ---
                item {
                    SectionCard(title = "GIỚI THIỆU") {
                        Text(
                            text = SakuraData.about,
                            color = SakuraTextDark,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // --- C. PROFILE ---
                item {
                    SectionCard(title = "HỒ SƠ") {
                        SakuraData.profile.forEach { item -> InfoRow(item) }
                    }
                }

                // --- D. KỸ NĂNG ---
                item {
                    SectionCard(title = "KỸ NĂNG") {
                        Column {
                            SakuraData.skills.forEach { item -> SkillItem(item) }
                        }
                    }
                }

                // --- E. KINH NGHIỆM ---
                item {
                    SectionCard(title = "KINH NGHIỆM") {
                        SakuraData.experience.forEach { item -> ExperienceCard(item) }
                    }
                }

                // --- F. DỰ ÁN ---
                item {
                    Text(
                        text = "✿ DỰ ÁN TIÊU BIỂU ✿",
                        style = MaterialTheme.typography.titleMedium,
                        color = SakuraPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                items(SakuraData.projects) { project ->
                    ProjectCard(project)
                }

                // --- G. LIÊN HỆ ---
                item {
                    SectionCard(title = "LIÊN HỆ") {
                        SakuraData.contact.forEach { item -> ContactRow(item) }
                    }
                }
            }
        }

        // 3. DIALOG CHAT AI
        if (showChatDialog) {
            ChatDialog(onDismiss = { showChatDialog = false })
        }
    }
}

// Giữ nguyên Badge và ChatDialog ở cuối file này...
// (Nếu bạn đã có Badge và ChatDialog ở phiên bản trước thì giữ nguyên,
// nếu chưa thì copy lại từ bước 4 cũ)
@Composable
fun Badge(text: String) {
    Surface(
        color = SakuraSecondary.copy(alpha = 0.3f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, SakuraSecondary)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = SakuraTextDark,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ChatDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(SakuraPrimary, SakuraSecondary)))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sakura AI Assistant", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SakuraBg)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(0.8f),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Xin chào! 🌸 Mình là trợ lý ảo của Dũng.",
                            modifier = Modifier.padding(12.dp),
                            color = SakuraTextDark
                        )
                    }
                }

                // Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Nhập tin nhắn...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SakuraSecondary,
                            focusedBorderColor = SakuraPrimary
                        )
                    )
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp).background(SakuraPrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}