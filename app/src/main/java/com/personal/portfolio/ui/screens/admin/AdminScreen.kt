package com.personal.portfolio.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.personal.portfolio.data.remote.Post
import com.personal.portfolio.data.remote.SectionBox
import com.personal.portfolio.data.remote.SectionBoxItem
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onGoBack: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    var activeTab by remember { mutableStateOf("blog") } // "blog" hoặc "sections"

    Scaffold(
        containerColor = SakuraBg,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("🌸 DASHBOARD ADMIN", fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 18.sp) },
                    navigationIcon = { IconButton(onClick = onGoBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = SakuraPrimary) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SakuraGlass)
                )
                // TABS CHUYỂN ĐỔI NHƯ WEB
                PrimaryTabRow(
                    selectedTabIndex = if (activeTab == "blog") 0 else 1,
                    containerColor = Color.White,
                    contentColor = SakuraPrimary
                ) {
                    Tab(
                        selected = activeTab == "blog",
                        onClick = { activeTab = "blog" },
                        text = { Text("📝 QUẢN LÝ BLOG", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == "sections",
                        onClick = { activeTab = "sections" },
                        text = { Text("⚙️ NỘI DUNG (SECTIONS)", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (activeTab == "blog") {
                AdminBlogTab(viewModel)
            } else {
                AdminSectionsTab(viewModel)
            }
        }
    }
}

// =========================================================
// 1. TAB QUẢN LÝ NỘI DUNG (SECTIONS)
// =========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSectionsTab(viewModel: HomeViewModel) {
    val gson = Gson()
    val sectionsList = listOf(
        "about" to "01. GIỚI THIỆU (Text)",
        "career" to "04. MỤC TIÊU NGHỀ (Text)",
        "profile" to "02. HỒ SƠ (Boxes)",
        "skills" to "06. KỸ NĂNG (Boxes)",
        "contact" to "10. LIÊN HỆ (Boxes)"
    )

    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedSectionKey by remember { mutableStateOf(sectionsList[0].first) }
    var selectedSectionName by remember { mutableStateOf(sectionsList[0].second) }

    // State lưu JSON tạm thời
    var jsonEn by remember { mutableStateOf("") }
    var jsonVi by remember { mutableStateOf("") }
    var jsonJp by remember { mutableStateOf("") }
    var isLoadingData by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSectionKey) {
        isLoadingData = true
        val rawData = viewModel.getRawSectionForAdmin(selectedSectionKey)
        jsonEn = rawData?.contentEn ?: ""
        jsonVi = rawData?.contentVi ?: ""
        jsonJp = rawData?.contentJp ?: ""
        viewModel.adminMessage = ""
        isLoadingData = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        AdminMessage(viewModel.adminMessage)

        // CHỌN MỤC
        ExposedDropdownMenuBox(expanded = expandedDropdown, onExpandedChange = { expandedDropdown = !expandedDropdown }) {
            OutlinedTextField(
                value = selectedSectionName, onValueChange = {}, readOnly = true,
                modifier = Modifier.menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                    enabled = true
                ).fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SakuraPrimary)
            )
            ExposedDropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }, modifier = Modifier.background(Color.White)) {
                sectionsList.forEach { (key, name) ->
                    DropdownMenuItem(text = { Text(name) }, onClick = {
                        selectedSectionKey = key; selectedSectionName = name; expandedDropdown = false
                    })
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        if (isLoadingData) {
            Text("Đang kéo dữ liệu...", color = SakuraPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Tùy theo loại section mà render UI khác nhau
            val isBoxSection = selectedSectionKey in listOf("profile", "skills", "contact")

            if (isBoxSection) {
                // UI DÀNH CHO BOX (Skills, Contact...)
                val parseBox = { json: String -> try { gson.fromJson<List<SectionBox>>(json, Array<SectionBox>::class.java).toList() } catch (_:Exception) { emptyList() } }
                var boxesVi by remember(jsonVi) { mutableStateOf(parseBox(jsonVi)) }

                Text("Chỉnh sửa (Bản Tiếng Việt) - Các bản khác sửa trên Web", color = SakuraTextLight, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                Spacer(Modifier.height(8.dp))

                BoxEditorUI(data = boxesVi, onUpdate = { newBoxes ->
                    boxesVi = newBoxes
                    jsonVi = gson.toJson(newBoxes) // Cập nhật lại chuỗi JSON
                })

            } else {
                // UI DÀNH CHO TEXT ĐƠN GIẢN (About, Career)
                OutlinedTextField(value = jsonVi, onValueChange = { jsonVi = it }, label = { Text("Tiếng Việt 🇻🇳") }, modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom=8.dp))
                OutlinedTextField(value = jsonEn, onValueChange = { jsonEn = it }, label = { Text("English 🇬🇧") }, modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom=8.dp))
                OutlinedTextField(value = jsonJp, onValueChange = { jsonJp = it }, label = { Text("日本語 🇯🇵") }, modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom=8.dp))
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveSectionJson(selectedSectionKey, jsonEn, jsonVi, jsonJp) },
                modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(SakuraPrimary)
            ) { Text("💾 LƯU LÊN DATABASE", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// UI Trình soạn thảo Box (Cho Profile, Skills, Contact)
@Composable
fun BoxEditorUI(data: List<SectionBox>, onUpdate: (List<SectionBox>) -> Unit) {
    Column {
        data.forEachIndexed { bIdx, box ->
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, SakuraSecondary)) {
                Column(Modifier.padding(12.dp)) {
                    OutlinedTextField(value = box.title, onValueChange = { val n = data.toMutableList(); n[bIdx] = box.copy(title = it); onUpdate(n) }, label = { Text("Tiêu đề nhóm") }, modifier = Modifier.fillMaxWidth())

                    box.items.forEachIndexed { iIdx, item ->
                        Row(Modifier.fillMaxWidth().padding(top=8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = item.label, onValueChange = { val n = data.toMutableList(); n[bIdx].items[iIdx].label = it; onUpdate(n) }, label = { Text("Label") }, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(4.dp))
                            OutlinedTextField(value = item.value, onValueChange = { val n = data.toMutableList(); n[bIdx].items[iIdx].value = it; onUpdate(n) }, label = { Text("Value") }, modifier = Modifier.weight(1.5f))
                            IconButton(onClick = { val n = data.toMutableList(); n[bIdx].items.removeAt(iIdx); onUpdate(n) }) { Icon(Icons.Default.Delete, null, tint=Color.Red) }
                        }
                    }
                    TextButton(onClick = { val n = data.toMutableList(); n[bIdx].items.add(SectionBoxItem("","")); onUpdate(n) }) { Text("+ Thêm dòng") }
                }
            }
        }
        Button(onClick = { onUpdate(data + SectionBox(id = System.currentTimeMillis().toString(), title = "Nhóm mới", items = mutableListOf())) }, colors = ButtonDefaults.buttonColors(SakuraSecondary), modifier = Modifier.fillMaxWidth()) { Text("+ THÊM NHÓM MỚI", color = SakuraPrimary) }
    }
}


// =========================================================
// 2. TAB QUẢN LÝ BLOG / DỰ ÁN
// =========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBlogTab(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var editingPost by remember { mutableStateOf<Post?>(null) } // Nút Edit sẽ gắn bài viết vào đây

    if (editingPost != null) {
        // FORM THÊM/SỬA BÀI VIẾT
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(if (editingPost!!.id.isEmpty()) "✨ BÀI VIẾT MỚI" else "✏️ SỬA BÀI VIẾT", fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = editingPost!!.title, onValueChange = { editingPost = editingPost!!.copy(title = it) }, label = { Text("Tiêu đề") }, modifier = Modifier.fillMaxWidth())

            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                editingPost!!.tag?.let { it -> OutlinedTextField(value = it, onValueChange = { editingPost = editingPost!!.copy(tag = it) }, label = { Text("Tag (VD: uni_projects)") }, modifier = Modifier.weight(1f)) }
                Spacer(Modifier.width(8.dp))
                editingPost!!.language?.let { it -> OutlinedTextField(value = it, onValueChange = { editingPost = editingPost!!.copy(language = it) }, label = { Text("Lang (vi/en/jp)") }, modifier = Modifier.weight(1f)) }
            }

            editingPost!!.images?.let { it -> OutlinedTextField(value = it, onValueChange = { editingPost = editingPost!!.copy(images = it) }, label = { Text("Link Ảnh (Định dạng JSON Array ['url'])") }, modifier = Modifier.fillMaxWidth().padding(top=8.dp)) }
            editingPost!!.content?.let { it -> OutlinedTextField(value = it, onValueChange = { editingPost = editingPost!!.copy(content = it) }, label = { Text("Nội dung (Hỗ trợ Markdown)") }, modifier = Modifier.fillMaxWidth().height(200.dp).padding(top=8.dp)) }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                Button(onClick = { viewModel.savePost(editingPost!!, editingPost!!.id.isNotEmpty()); editingPost = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(SakuraPrimary)) { Text("LƯU BÀI VIẾT") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { editingPost = null }, modifier = Modifier.weight(1f)) { Text("HỦY") }
            }
        }
    } else {
        // DANH SÁCH BÀI VIẾT
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            AdminMessage(viewModel.adminMessage)
            Button(
                onClick = {  Post("", "", "my_confessions", "vi", "", "[]", "") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = ButtonDefaults.buttonColors(SakuraPrimary)
            ) { Text("✍️ TẠO BÀI VIẾT MỚI", fontWeight = FontWeight.Bold) }

            LazyColumn {
                items(uiState.allPosts) { post ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(post.title, fontWeight = FontWeight.Bold, color = SakuraTextDark)
                                Text("${post.tag} | ${post.language}", color = SakuraPrimary, fontSize = 12.sp)
                            }
                            IconButton(onClick = { post.copy() }) { Icon(Icons.Default.Edit, "Edit", tint = Color.Blue) }
                            IconButton(onClick = { viewModel.deletePost(post.id) }) { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}

// Widget báo lỗi chung
@Composable
fun AdminMessage(msg: String) {
    if (msg.isNotEmpty()) {
        Surface(color = if (msg.contains("Lỗi")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(text = msg, modifier = Modifier.padding(12.dp), color = if (msg.contains("Lỗi")) Color.Red else Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
        }
    }
}