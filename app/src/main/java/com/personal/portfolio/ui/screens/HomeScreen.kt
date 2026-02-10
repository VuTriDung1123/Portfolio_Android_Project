package com.personal.portfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.personal.portfolio.R
import com.personal.portfolio.data.SakuraData
import com.personal.portfolio.data.SimpleItem
import com.personal.portfolio.ui.components.*
import com.personal.portfolio.ui.screens.admin.AdminScreen // Import màn hình Admin
import com.personal.portfolio.ui.theme.*

@Composable
fun HomeScreen() {
    var currentLang by remember { mutableStateOf("vi") }
    // [MỚI] State để bật tắt màn hình Admin
    var showAdmin by remember { mutableStateOf(false) }

    val data = when(currentLang) {
        "en" -> SakuraData.en
        "jp" -> SakuraData.jp
        else -> SakuraData.vi
    }

    SakuraPortfolioTheme(lang = currentLang) {

        // [MỚI] Logic kiểm tra: Nếu showAdmin = true thì hiện AdminScreen, ngược lại hiện HomeScreen
        if (showAdmin) {
            // Truyền hàm onGoBack để khi bấm nút Exit trong Admin thì quay về đây
            AdminScreen(onGoBack = { showAdmin = false })
        } else {
            // --- GIAO DIỆN CHÍNH (HOME) ---
            var showChatDialog by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                SakuraFallingEffect()

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar + Tên
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SakuraAvatar(modifier = Modifier.size(45.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = data.hero["name"] ?: "",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SakuraTextDark
                                    )
                                    Text(
                                        text = data.hero["role"] ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SakuraPrimary
                                    )
                                }
                            }

                            // Cụm nút bên phải
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // [MỚI] NÚT ADMIN (Ổ KHÓA)
                                IconButton(onClick = { showAdmin = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Admin Login",
                                        tint = SakuraSecondary.copy(alpha = 0.5f), // Màu nhạt cho kín đáo
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Nút ngôn ngữ
                                Row(
                                    modifier = Modifier
                                        .background(SakuraGlass, CircleShape)
                                        .border(1.dp, SakuraSecondary, CircleShape)
                                        .padding(4.dp)
                                ) {
                                    LanguageButton("VI", currentLang == "vi") { currentLang = "vi" }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    LanguageButton("EN", currentLang == "en") { currentLang = "en" }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    LanguageButton("JP", currentLang == "jp") { currentLang = "jp" }
                                }
                            }
                        }
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
                        // ... (GIỮ NGUYÊN PHẦN NỘI DUNG LIST NHƯ CŨ) ...
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                SakuraAvatar(modifier = Modifier.size(160.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = data.hero["greeting"] ?: "", color = SakuraPrimary, style = MaterialTheme.typography.titleMedium)
                                Text(text = data.hero["name"] ?: "", style = MaterialTheme.typography.displaySmall, color = SakuraTextDark, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = data.hero["desc"] ?: "", textAlign = TextAlign.Center, color = SakuraTextLight, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        item { SectionCard("ABOUT", @Composable { Text(data.about, color = SakuraTextDark) }) }
                        item { SectionCard("PROFILE", @Composable { data.profile.forEach { InfoRow(it) } }) }
                        item { SectionCard("CERTIFICATES") { data.certificates.forEach { SimpleCard(it) } } }
                        item { SectionCard("CAREER GOALS", @Composable { Text(data.career, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = SakuraTextDark) }) }
                        item { SectionCard("ACHIEVEMENTS") { data.achievements.forEach { SimpleCard(it) } } }
                        item { SectionCard("SKILLS", @Composable { Column { data.skills.forEach { SkillItem(it) } } }) }
                        item { SectionCard("EXPERIENCE", @Composable { data.experience.forEach { ExperienceCard(it) } }) }
                        item {
                            Text("✿ PROJECTS ✿", style = MaterialTheme.typography.titleMedium, color = SakuraPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), textAlign = TextAlign.Center)
                        }
                        items(data.projects) { ProjectCard(it) }
                        item { SectionCard("BLOG") { data.blog.forEach { SimpleCard(it) } } }
                        item { SectionCard("GALLERY") { data.gallery.forEach { SimpleCard(it) } } }
                        item { SectionCard("FAQ") { data.faq.forEach { FAQItem(it.first, it.second) } } }
                        item { SectionCard("CONTACT", @Composable { data.contact.forEach { ContactRow(it) } }) }
                    }
                }

                if (showChatDialog) {
                    ChatDialog(onDismiss = { showChatDialog = false })
                }
            }
        }
    }
}

// --- CÁC COMPONENT PHỤ TRỢ (GIỮ NGUYÊN) ---
@Composable
fun SimpleCard(item: SimpleItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color.White, RoundedCornerShape(10.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = item.imageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
        Spacer(modifier = Modifier.width(12.dp))
        Column { Text(text = item.title, fontWeight = FontWeight.Bold, color = SakuraTextDark); Text(text = item.subtitle, fontSize = 12.sp, color = SakuraTextLight) }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Color.White, RoundedCornerShape(10.dp)).border(1.dp, SakuraSecondary, RoundedCornerShape(10.dp)).clickable { expanded = !expanded }.padding(12.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text(text = question, fontWeight = FontWeight.Bold, color = SakuraPrimary, modifier = Modifier.weight(1f)); Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SakuraTextLight) }
        if (expanded) { Spacer(modifier = Modifier.height(8.dp)); Text(text = answer, color = SakuraTextDark, fontSize = 14.sp) }
    }
}

@Composable
fun LanguageButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(CircleShape).background(if (isSelected) SakuraPrimary else Color.Transparent).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = if (isSelected) Color.White else SakuraTextLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun Badge(text: String) {
    Surface(color = SakuraSecondary.copy(alpha = 0.3f), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, SakuraSecondary)) {
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ChatDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().height(500.dp), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(SakuraPrimary, SakuraSecondary))).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text(text = "🤖", fontSize = 24.sp); Spacer(modifier = Modifier.width(8.dp)); Text(text = "Sakura AI Assistant", color = Color.White, fontWeight = FontWeight.Bold) }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
                }
                Column(modifier = Modifier.weight(1f).background(SakuraBg).padding(16.dp), verticalArrangement = Arrangement.Bottom) {
                    Surface(shape = MaterialTheme.shapes.medium, color = Color.White, modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(0.8f), shadowElevation = 2.dp) {
                        Text(text = "Xin chào! 🌸 Mình là trợ lý ảo của Dũng.", modifier = Modifier.padding(12.dp), color = SakuraTextDark)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Nhập tin nhắn...", fontSize = 14.sp) }, modifier = Modifier.weight(1f).padding(end = 8.dp), shape = CircleShape, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = SakuraSecondary, focusedBorderColor = SakuraPrimary))
                    IconButton(onClick = { }, modifier = Modifier.size(48.dp).background(SakuraPrimary, CircleShape)) { Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White) }
                }
            }
        }
    }
}