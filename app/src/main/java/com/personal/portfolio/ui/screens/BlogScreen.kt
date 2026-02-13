package com.personal.portfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.personal.portfolio.data.SakuraData
import com.personal.portfolio.data.remote.Post
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel

@Composable
fun BlogScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLang = uiState.currentLanguage // Lấy ngôn ngữ từ ViewModel

    // State nội bộ của màn hình Blog
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("ALL") }
    var isNewestFirst by remember { mutableStateOf(true) }

    // Dữ liệu tĩnh (Title, Hint...)
    val staticText = when(currentLang) {
        "en" -> SakuraData.en
        "jp" -> SakuraData.jp
        else -> SakuraData.vi
    }

    // Danh sách Tags cố định (Tiếng Anh như yêu cầu)
    val tags = listOf("ALL", "Confessions", "Uni Projects", "Personal Code", "Achievements", "IT Events", "Certificates")

    // --- LOGIC LỌC BÀI VIẾT ---
    val displayedPosts = remember(uiState.allPosts, searchQuery, selectedTag, isNewestFirst, currentLang) {
        uiState.allPosts.filter { post ->
            // 1. Lọc theo ngôn ngữ hiện tại (Bài viết phải có language == currentLang)
            // Lưu ý: Nếu bài viết không có trường language hoặc bạn muốn hiện tất cả thì bỏ dòng này
            post.language == currentLang || post.language == "all"
        }.filter { post ->
            // 2. Lọc theo Search (Tìm trong Title)
            searchQuery.isEmpty() || post.title.contains(searchQuery, ignoreCase = true)
        }.filter { post ->
            // 3. Lọc theo Tag
            selectedTag == "ALL" || (post.tag?.contains(selectedTag, ignoreCase = true) == true)
        }.sortedBy { post ->
            // 4. Sắp xếp thời gian
            post.createdAt // Giả sử post.createdAt là String chuẩn ISO hoặc Long
        }.let {
            if (isNewestFirst) it.reversed() else it
        }
    }

    SakuraPortfolioTheme(lang = currentLang) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF0F5))) { // Nền hồng nhạt toàn màn hình
            SakuraFallingEffect()

            Scaffold(
                containerColor = Color.Transparent,
                // --- TOP BAR ---
                topBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFE4E1).copy(alpha = 0.95f)) // Nền hồng nhạt đồng bộ Home
                            .statusBarsPadding() // Đẩy xuống dưới thanh trạng thái hệ thống
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cụm trái: Nút quay lại + Tiêu đề nhỏ
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SakuraPrimary)
                                }
                                Column {
                                    Text(
                                        text = staticText.blog_title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = SakuraPrimary
                                    )
                                    Text(
                                        text = "vutridung.portfolio",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SakuraTextLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Cụm phải: Dropdown Ngôn ngữ
                            BlogLanguageDropdown(
                                currentLang = currentLang,
                                onLangSelect = { newLang -> viewModel.setLanguage(newLang) }
                            )
                        }
                        // Đường kẻ mảnh trang trí phía dưới
                        Divider(color = SakuraSecondary.copy(alpha = 0.3f), thickness = 1.dp)
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 50.dp)
                ) {
                    // 1. HEADER (Title & Quote)
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Logo hoa anh đào
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌸", fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = staticText.blog_title,
                                    color = SakuraPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("🌸", fontSize = 24.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "「 ${staticText.blog_subtitle} 」",
                                color = SakuraTextLight,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 2. SEARCH & SORT BAR
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Search Box
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(staticText.blog_search_hint, fontSize = 13.sp, color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = SakuraPrimary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(Color.White, CircleShape),
                                shape = CircleShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = SakuraSecondary,
                                    focusedBorderColor = SakuraPrimary
                                ),
                                singleLine = true
                            )

                            Spacer(Modifier.width(8.dp))

                            // Sort Button
                            Button(
                                onClick = { isNewestFirst = !isNewestFirst },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = SakuraPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPrimary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Icon(
                                    if(isNewestFirst) Icons.Default.AccessTime else Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (isNewestFirst) staticText.blog_sort_newest else staticText.blog_sort_oldest,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // 3. TAGS FILTER (Horizontal Scroll)
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tags) { tag ->
                                val isSelected = if(tag == "ALL") selectedTag == "ALL" else selectedTag == tag
                                val displayLabel = if(tag == "ALL") staticText.blog_tab_all else tag

                                FilterChip(
                                    selected = isSelected, // Tham số bắt buộc
                                    onClick = { selectedTag = tag }, // Tham số bắt buộc
                                    label = {
                                        Text(
                                            text = displayLabel,
                                            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    enabled = true, // [SỬA LỖI] Thêm tham số enabled
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SakuraPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = SakuraTextDark
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if(isSelected) SakuraPrimary else SakuraSecondary,
                                        borderWidth = 1.dp
                                    ),
                                    shape = CircleShape
                                )
                            }
                        }
                    }

                    // 4. POST LIST
                    if (displayedPosts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🍃", fontSize = 40.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Không tìm thấy bài viết nào.", color = SakuraTextLight)
                                }
                            }
                        }
                    } else {
                        items(displayedPosts) { post ->
                            BlogPostCard(post, navController)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENT: BLOG POST CARD (Giống mẫu) ---
@Composable
fun BlogPostCard(post: Post, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("post_detail/${post.id}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Ảnh Thumbnail
            val imageList = try {
                // Giả sử post.images là chuỗi JSON "['url1', 'url2']"
                // Ở đây mình parse sơ bộ hoặc dùng ảnh mặc định
                if (post.images.contains("http")) post.images.replace("[\"", "").replace("\"]", "").split("\",\"")[0]
                else ""
            } catch (e: Exception) { "" }

            if (imageList.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(imageList),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Ảnh placeholder nếu không có ảnh
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp).background(SakuraGlass),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, null, tint = SakuraSecondary, modifier = Modifier.size(40.dp))
                }
            }

            // Nội dung
            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = post.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakuraTextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // Tag Chip
                Surface(
                    color = SakuraPrimary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = post.tag ?: "General",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Date & Description
                val dateStr = try {
                    // Format lại ngày: 2025-10-16 -> October 16, 2025
                    // Lưu ý: Cần xử lý kỹ hơn tùy format server trả về
                    post.createdAt.toString().take(10)
                } catch (e: Exception) { "" }

                Text(
                    text = "Date: $dateStr",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = post.content,
                    fontSize = 14.sp,
                    color = SakuraTextLight,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- COMPONENT: DROPDOWN CHO BLOG ---
@Composable
fun BlogLanguageDropdown(currentLang: String, onLangSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val langs = mapOf("vi" to "VI", "en" to "EN", "jp" to "JP")

    Box {
        Row(
            modifier = Modifier
                .background(SakuraBg, CircleShape)
                .border(1.dp, SakuraSecondary, CircleShape)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentLang.uppercase(), color = SakuraPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, null, tint = SakuraPrimary, modifier = Modifier.size(16.dp))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 8.dp),
            modifier = Modifier.background(Color.White)
        ) {
            langs.forEach { (code, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            label,
                            color = if(currentLang == code) SakuraPrimary else SakuraTextDark,
                            fontWeight = if(currentLang == code) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onLangSelect(code)
                        expanded = false
                    }
                )
            }
        }
    }
}