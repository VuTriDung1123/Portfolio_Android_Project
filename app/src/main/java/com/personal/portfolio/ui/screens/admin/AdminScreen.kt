package com.personal.portfolio.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.portfolio.data.remote.Post
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onGoBack: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    // --- BẢO MẬT ---
    var isAuth by remember { mutableStateOf(false) }
    var userLogin by remember { mutableStateOf("") }
    var passLogin by remember { mutableStateOf("") }

    // --- QUẢN LÝ GIAO DIỆN ---
    var activeTab by remember { mutableStateOf("blog") }
    val uiState by viewModel.uiState.collectAsState()
    var isSaving by remember { mutableStateOf(false) }

    // --- FORM BLOG ---
    var editingPostId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("uni_projects") }
    var langPost by remember { mutableStateOf("vi") }
    val imageLinks = remember { mutableStateListOf<String>() }

    // --- FORM SECTIONS ---
    var selectedSectionKey by remember { mutableStateOf("about") }
    var secEn by remember { mutableStateOf("") }
    var secVi by remember { mutableStateOf("") }
    var secJp by remember { mutableStateOf("") }

    // 1. MÀN HÌNH ĐĂNG NHẬP [BẢO MẬT]
    if (!isAuth) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF5F7)), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f).padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌸 ADMIN LOGIN", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SakuraPrimary)
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(value = userLogin, onValueChange = { userLogin = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passLogin,
                        onValueChange = { passLogin = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Thay "admin" và "123" bằng tài khoản của bạn
                            if(userLogin == "admin" && passLogin == "123") isAuth = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)
                    ) { Text("LOGIN", fontWeight = FontWeight.Bold) }
                    TextButton(onClick = onGoBack) { Text("Quay lại Home", color = Color.Gray) }
                }
            }
        }
        return
    }

    // 2. MÀN HÌNH DASHBOARD CHÍNH (Sau khi Login)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌸 DASHBOARD", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onGoBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    TextButton(onClick = { activeTab = "blog" }) {
                        Text("BLOG", color = if(activeTab == "blog") SakuraPrimary else Color.Gray)
                    }
                    TextButton(onClick = { activeTab = "content" }) {
                        Text("SECTIONS", color = if(activeTab == "content") SakuraPrimary else Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFE4E1))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFFF5F7))) {
            if (activeTab == "blog") {
                // --- TAB QUẢN LÝ BLOG ---
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        AdminFormCard(title = if(editingPostId == null) "BÀI VIẾT MỚI" else "SỬA BÀI VIẾT") {
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề") }, modifier = Modifier.fillMaxWidth())

                            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                                val tags = listOf("uni_projects", "personal_projects", "achievements", "it_events", "lang_certs", "tech_certs", "other_certs", "my_confessions")
                                DropdownField(label = tag, options = tags, onSelect = { tag = it }, Modifier.weight(1f))
                                DropdownField(label = langPost.uppercase(), options = listOf("vi", "en", "jp"), onSelect = { langPost = it }, Modifier.weight(1f))
                            }

                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Nội dung") }, modifier = Modifier.fillMaxWidth().height(150.dp))

                            // Quản lý link ảnh JSON
                            Text("HÌNH ẢNH (Link từ Web Manager)", style = MaterialTheme.typography.labelSmall, color = SakuraPrimary, modifier = Modifier.padding(top = 8.dp))
                            imageLinks.forEachIndexed { index, link ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(value = link, onValueChange = { imageLinks[index] = it }, modifier = Modifier.weight(1f), placeholder = { Text("Dán link ảnh vào đây...") })
                                    IconButton(onClick = { if(imageLinks.size > 1) imageLinks.removeAt(index) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                                }
                            }
                            TextButton(onClick = { imageLinks.add("") }) { Text("+ Thêm ô nhập ảnh") }

                            Button(
                                onClick = { /* Thực thi hàm Save Blog qua ViewModel */ },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)
                            ) { Text("LƯU BÀI VIẾT 🌸") }
                        }
                    }

                    item { Text("DANH SÁCH BÀI VIẾT", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) }

                    items(uiState.allPosts) { post ->
                        AdminPostItem(post, onEdit = {
                            editingPostId = post.id
                            title = post.title
                            content = post.content
                            tag = post.tag
                            langPost = post.language
                            // Bóc tách JSON ảnh
                            imageLinks.clear()
                            val regex = "(https?://[^\"]+)".toRegex()
                            regex.findAll(post.images).forEach { imageLinks.add(it.value) }
                        }, onDelete = { /* Thực thi Delete */ })
                    }
                }
            } else {
                // --- TAB QUẢN LÝ NỘI DUNG (SECTIONS) ---
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    AdminFormCard(title = "CẤU HÌNH HỆ THỐNG") {
                        val sections = listOf("global_config", "hero", "about", "profile", "career", "skills", "experience", "contact", "faq_data")
                        SectionPicker(selected = selectedSectionKey, options = sections, onSelect = { selectedSectionKey = it })

                        Text("BẢN TIẾNG ANH", fontWeight = FontWeight.Bold, color = SakuraPrimary)
                        OutlinedTextField(value = secEn, onValueChange = { secEn = it }, modifier = Modifier.fillMaxWidth().height(150.dp))

                        Text("BẢN TIẾNG VIỆT", fontWeight = FontWeight.Bold, color = SakuraPrimary)
                        OutlinedTextField(value = secVi, onValueChange = { secVi = it }, modifier = Modifier.fillMaxWidth().height(150.dp))

                        Text("BẢN TIẾNG NHẬT", fontWeight = FontWeight.Bold, color = SakuraPrimary)
                        OutlinedTextField(value = secJp, onValueChange = { secJp = it }, modifier = Modifier.fillMaxWidth().height(150.dp))

                        Button(
                            onClick = { /* Thực thi Save Section qua ViewModel */ },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)
                        ) { Text(if(isSaving) "ĐANG LƯU..." else "CẬP NHẬT TOÀN BỘ ✨") }
                    }
                }
            }
        }
    }
}

// --- COMPONENT CON ---
@Composable
fun AdminFormCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = SakuraPrimary, fontWeight = FontWeight.Bold)
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFFFE4E1))
            content()
        }
    }
}

@Composable
fun AdminPostItem(post: Post, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFE4E1))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(post.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${post.tag} | ${post.language}", fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = SakuraPrimary) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
        }
    }
}

@Composable
fun DropdownField(label: String, options: List<String>, onSelect: (String) -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(label, fontSize = 11.sp) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false }) }
        }
    }
}

@Composable
fun SectionPicker(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))) {
            Text("MỤC SỬA: ${selected.uppercase()}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false }) }
        }
    }
}