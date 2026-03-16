package com.personal.portfolio.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.portfolio.ui.theme.SakuraGlass
import com.personal.portfolio.ui.theme.SakuraPrimary
import com.personal.portfolio.ui.theme.SakuraSecondary
import com.personal.portfolio.ui.theme.SakuraTextDark
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onGoBack: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // Các State quản lý dữ liệu nhập vào
    val sections = listOf(
        "about" to "01. GIỚI THIỆU (About)",
        "career" to "04. MỤC TIÊU NGHỀ (Career)"
        // Bạn có thể thêm "skills", "profile" vào đây sau khi muốn edit chuỗi JSON
    )

    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedSectionKey by remember { mutableStateOf(sections[0].first) }
    var selectedSectionName by remember { mutableStateOf(sections[0].second) }

    var contentEn by remember { mutableStateOf("") }
    var contentVi by remember { mutableStateOf("") }
    var contentJp by remember { mutableStateOf("") }
    var isLoadingData by remember { mutableStateOf(false) }

    // Tự động kéo dữ liệu từ DB xuống mỗi khi đổi Section
    LaunchedEffect(selectedSectionKey) {
        isLoadingData = true
        val rawData = viewModel.getRawSectionForAdmin(selectedSectionKey)
        contentEn = rawData?.contentEn ?: ""
        contentVi = rawData?.contentVi ?: ""
        contentJp = rawData?.contentJp ?: ""
        viewModel.adminMessage = "" // Reset thông báo
        isLoadingData = false
    }

    Scaffold(
        containerColor = Color(0xFFFFF0F5),
        topBar = {
            TopAppBar(
                title = { Text("🌸 BẢNG ĐIỀU KHIỂN", fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onGoBack) { Icon(Icons.Default.ArrowBack, "Back", tint = SakuraPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SakuraGlass)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveSectionContent(selectedSectionKey, contentEn, contentVi, contentJp)
                },
                containerColor = SakuraPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(Modifier.width(8.dp))
                Text("LƯU LÊN WEB", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Thông báo trạng thái (Lưu thành công/Đang tải...)
            if (viewModel.adminMessage.isNotEmpty()) {
                Surface(
                    color = if (viewModel.adminMessage.contains("Lỗi")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = viewModel.adminMessage,
                        modifier = Modifier.padding(12.dp),
                        color = if (viewModel.adminMessage.contains("Lỗi")) Color.Red else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dropdown chọn Mục cần chỉnh sửa
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = selectedSectionName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Chọn mục cần sửa") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SakuraPrimary)
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    sections.forEach { (key, name) ->
                        DropdownMenuItem(
                            text = { Text(name, color = SakuraTextDark) },
                            onClick = {
                                selectedSectionKey = key
                                selectedSectionName = name
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (isLoadingData) {
                Text("Đang kéo dữ liệu từ PostgreSQL... ⏳", color = SakuraPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Form nhập liệu 3 ngôn ngữ
                AdminTextArea(label = "Tiếng Việt 🇻🇳", value = contentVi, onValueChange = { contentVi = it })
                AdminTextArea(label = "English 🇬🇧", value = contentEn, onValueChange = { contentEn = it })
                AdminTextArea(label = "日本語 🇯🇵", value = contentJp, onValueChange = { contentJp = it })
            }

            Spacer(Modifier.height(80.dp)) // Chừa chỗ cho nút Save nổi
        }
    }
}

// Khung Text nhập liệu nhiều dòng
@Composable
fun AdminTextArea(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontWeight = FontWeight.Bold) },
        modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SakuraPrimary,
            unfocusedBorderColor = SakuraSecondary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}