package com.personal.portfolio.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.personal.portfolio.R
import com.personal.portfolio.data.SakuraData
import com.personal.portfolio.ui.components.*
import com.personal.portfolio.ui.screens.admin.AdminScreen
import com.personal.portfolio.ui.theme.*
import com.personal.portfolio.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    // --- STATE QUẢN LÝ ---
    var currentLang by remember { mutableStateOf("vi") }
    var showAdmin by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) } // Bật Intro mặc định

    // --- DATA ---
    val uiState by viewModel.uiState.collectAsState()

    // 1. LẤY DATA TĨNH (Để hiển thị ngay lập tức, tránh màn hình trắng)
    val staticData = when(currentLang) {
        "en" -> SakuraData.en
        "jp" -> SakuraData.jp
        else -> SakuraData.vi
    }

    // Tự động tải lại data API khi đổi ngôn ngữ
    LaunchedEffect(currentLang) { viewModel.loadAllData(currentLang) }

    SakuraPortfolioTheme(lang = currentLang) {
        // A. MÀN HÌNH INTRO THƠ (Chạy trước tiên)
        if (showIntro) {
            PoeticLoadingScreen(
                lang = currentLang,
                onFinished = { showIntro = false }
            )
        }
        // B. MÀN HÌNH ADMIN
        else if (showAdmin) {
            AdminScreen(onGoBack = { showAdmin = false })
        }
        // C. GIAO DIỆN CHÍNH (HOME)
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                SakuraFallingEffect()

                Scaffold(
                    containerColor = Color.Transparent,
                    // --- TOP BAR (Giống Web: Avatar khung, Tên, Nút Lang) ---
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.9f)) // Nền trắng mờ
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cụm Trái: Avatar + Tên (Luôn hiện nhờ Static Data)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(R.drawable.vutridung),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(38.dp).clip(CircleShape)
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.sakura_avatar),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = staticData.hero["name"] ?: "Vũ Trí Dũng",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SakuraTextDark
                                    )
                                    Text(
                                        text = staticData.hero["role"] ?: "Lập trình viên",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SakuraPrimary
                                    )
                                }
                            }

                            // Cụm Phải: Nút Admin + Ngôn ngữ
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { showAdmin = true }) {
                                    Icon(Icons.Default.Lock, null, tint = SakuraSecondary.copy(0.5f))
                                }
                                Row(
                                    modifier = Modifier
                                        .background(SakuraBg, CircleShape)
                                        .border(1.dp, SakuraSecondary, CircleShape)
                                        .padding(4.dp)
                                ) {
                                    LanguageButton("VI", currentLang == "vi") { currentLang = "vi" }
                                    LanguageButton("EN", currentLang == "en") { currentLang = "en" }
                                    LanguageButton("JP", currentLang == "jp") { currentLang = "jp" }
                                }
                            }
                        }
                    },
                    // --- FAB CHAT ---
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showChatDialog = true }, containerColor = SakuraPrimary, contentColor = Color.White, shape = CircleShape) {
                            Icon(Icons.Default.AutoAwesome, "AI Chat")
                        }
                    }
                ) { padding ->
                    // --- NỘI DUNG CHÍNH (Cuộn) ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // 1. HERO SECTION (Web Banner Style)
                        item {
                            // Logic Fallback: Ưu tiên API, nếu không có thì dùng Static
                            val hName = if (uiState.hero.fullName.isNotEmpty()) uiState.hero.fullName else staticData.hero["name"] ?: "Vũ Trí Dũng"
                            val hGreet = if (uiState.hero.greeting.isNotEmpty()) uiState.hero.greeting else staticData.hero["greeting"] ?: "Xin Chào"
                            val hAvatar = if (uiState.hero.avatarUrl.isNotEmpty()) uiState.hero.avatarUrl else ""

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFFFFE4E1), Color(0xFFFFF0F5))))
                            ) {
                                // Background trang trí
                                Icon(
                                    painter = painterResource(R.drawable.sakura_avatar),
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(300.dp).align(Alignment.CenterEnd).offset(x = 50.dp)
                                )

                                Row(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Text bên trái
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
                                            Text(hGreet, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = SakuraPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        Text(hName, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = SakuraTextDark)
                                        Spacer(Modifier.height(15.dp))
                                        Row {
                                            Button(
                                                onClick = { navController.navigate("blog") },
                                                colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                            ) { Text("Dự Án", fontSize = 12.sp) }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedButton(
                                                onClick = { navController.navigate("faq") },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraTextDark),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                            ) { Text("FAQ", fontSize = 12.sp) }
                                        }
                                    }
                                    // Avatar bên phải
                                    Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                                        val painter = if (hAvatar.isNotEmpty()) rememberAsyncImagePainter(hAvatar) else painterResource(R.drawable.vutridung)
                                        Image(
                                            painter = painter,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(110.dp).clip(CircleShape).border(3.dp, Color.White, CircleShape)
                                        )
                                        Image(
                                            painter = painterResource(R.drawable.sakura_avatar),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().scale(1.2f)
                                        )
                                    }
                                }
                            }
                        }

                        // 2. CÁC SECTION (01 -> 12) - CÓ FALLBACK STATIC

                        // 01. ABOUT ME
                        item { SectionCard(staticData.sec_01_about) {
                            Text(if(uiState.about.isNotEmpty()) uiState.about else staticData.about, color = SakuraTextDark, lineHeight = 24.sp)
                        }}

                        // 02. PROFILE
                        item {
                            SectionCard(staticData.sec_02_profile) {
                                val data = if(uiState.profile.isNotEmpty()) uiState.profile else emptyList()
                                if (data.isNotEmpty()) {
                                    data.forEach { box ->
                                        if(box.title.isNotEmpty()) Text("★ ${box.title}", fontWeight = FontWeight.Bold, color = SakuraPrimary, modifier = Modifier.padding(top=10.dp))
                                        box.items.forEach { item -> Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) { Text(item.label, color = SakuraTextLight); Text(item.value, fontWeight = FontWeight.Bold, color = SakuraTextDark) } }
                                    }
                                } else {
                                    staticData.profile.forEach { item ->
                                        Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) { Text(item.label, color = SakuraTextLight); Text(item.value, fontWeight = FontWeight.Bold, color = SakuraTextDark) }
                                    }
                                }
                            }
                        }

                        // 03. CERTIFICATES
                        item { SectionCard(staticData.sec_03_cert) {
                            // Nếu API rỗng thì hiển thị từ Static Data
                            if(uiState.certificates.isNotEmpty()) Text(uiState.certificates, color = SakuraTextDark)
                            else staticData.certificates.forEach { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom=8.dp)) { Text("🏆 ", fontSize=16.sp); Column { Text(it.title, fontWeight = FontWeight.Bold); Text(it.subtitle, fontSize=12.sp, color = SakuraTextLight) } } }
                        }}

                        // 04. CAREER
                        item { SectionCard(staticData.sec_04_career) { Text(if(uiState.career.isNotEmpty()) uiState.career else staticData.career, fontStyle = FontStyle.Italic, color = SakuraTextDark) }}

                        // 05. ACHIEVEMENTS
                        item { SectionCard(staticData.sec_05_achievements) {
                            if(uiState.achievements.isNotEmpty()) Text(uiState.achievements, color = SakuraTextDark)
                            else staticData.achievements.forEach { Text("• ${it.title}: ${it.subtitle}", modifier = Modifier.padding(bottom=4.dp)) }
                        }}

                        // 06. SKILLS
                        item { SectionCard(staticData.sec_06_skills) {
                            if(uiState.skills.isNotEmpty()) Text(uiState.skills, color = SakuraTextDark)
                            else staticData.skills.forEach { Row(Modifier.padding(vertical=2.dp)) { Text("• ${it.label}: ", fontWeight = FontWeight.Bold); Text(it.value) } }
                        }}

                        // 07. EXPERIENCE
                        item {
                            SectionCard(staticData.sec_07_exp) {
                                if(uiState.experience.isNotEmpty()) {
                                    uiState.experience.forEach { group ->
                                        Text(group.title, fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                                        group.items.forEach { exp ->
                                            Column(Modifier.padding(bottom = 12.dp, start = 10.dp)) {
                                                Text(exp.role, fontWeight = FontWeight.Bold, color = SakuraTextDark)
                                                Text(exp.time, fontSize = 12.sp, color = SakuraPrimary)
                                                exp.details.forEach { d -> Text("• $d", fontSize = 13.sp, color = SakuraTextLight) }
                                            }
                                        }
                                    }
                                } else {
                                    staticData.experience.forEach { exp ->
                                        Column(Modifier.padding(bottom = 12.dp)) {
                                            Text(exp.role, fontWeight = FontWeight.Bold); Text(exp.time, fontSize = 12.sp, color = SakuraPrimary); exp.details.forEach { Text("• $it", fontSize=13.sp) }
                                        }
                                    }
                                }
                            }
                        }

                        // 08. PROJECTS (Lấy từ Posts)
                        item {
                            SectionCard(staticData.sec_08_proj) {
                                val projs = uiState.allPosts.filter { it.tag.contains("project") }.take(3)
                                if (projs.isNotEmpty()) {
                                    projs.forEach { ProjectPostCard(it, simple = true); Spacer(Modifier.height(8.dp)) }
                                } else {
                                    staticData.projects.forEach { p -> Text("➤ ${p.title}", fontWeight = FontWeight.Bold); Text(p.desc, fontSize=13.sp, color=SakuraTextLight, modifier=Modifier.padding(bottom=8.dp)) }
                                }
                                TextButton(onClick = { navController.navigate("blog") }, Modifier.fillMaxWidth()) { Text(staticData.btn_view_all) }
                            }
                        }

                        // 09. GALLERY
                        item { SectionCard(staticData.sec_09_gallery) {
                            if(uiState.gallery.isNotEmpty()) {
                                Row(Modifier.horizontalScroll(rememberScrollState())) { uiState.gallery.forEach { url -> Image(painter = rememberAsyncImagePainter(url), contentDescription = null, modifier = Modifier.size(120.dp).padding(end=8.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop) } }
                            } else {
                                EmptyData("Thư viện ảnh đang cập nhật...")
                            }
                        }}

                        // 10. BLOG
                        item { SectionCard(staticData.sec_10_blog) {
                            val blogs = uiState.allPosts.filter { !it.tag.contains("project") }.take(3)
                            if (blogs.isNotEmpty()) blogs.forEach { ProjectPostCard(it, simple = true); Spacer(Modifier.height(8.dp)) }
                            else EmptyData("Chưa có bài viết mới.")
                            TextButton(onClick = { navController.navigate("blog") }, Modifier.fillMaxWidth()) { Text(staticData.btn_view_all) }
                        }}

                        // 11. FAQ
                        item { SectionCard(staticData.sec_11_faq) {
                            if(uiState.faq.isNotEmpty()) uiState.faq.take(3).forEach { FAQItem(it.q, it.a) }
                            else staticData.faq.forEach { FAQItem(it.first, it.second) }
                            TextButton(onClick = { navController.navigate("faq") }, Modifier.fillMaxWidth()) { Text(staticData.btn_view_all) }
                        }}

                        // 12. CONTACT
                        item { SectionCard(staticData.sec_12_contact) {
                            if(uiState.contact.isNotEmpty()) uiState.contact.forEach { box -> box.items.forEach { ContactRowWrapper(it.label, it.value) } }
                            else staticData.contact.forEach { ContactRowWrapper(it.type, it.value) }
                        }}
                    }
                }

                // HỘP THOẠI CHAT
                if (showChatDialog) ChatDialog(onDismiss = { showChatDialog = false })
            }
        }
    }
}

// ------------------------------------------------------------------------
// CÁC COMPONENT PHỤ TRỢ (HELPER COMPONENTS)
// ------------------------------------------------------------------------

fun Modifier.scale(scale: Float): Modifier = this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))

@Composable
fun PoeticLoadingScreen(lang: String, onFinished: () -> Unit) {
    val poems = when(lang) {
        "en" -> listOf("Code is poetry written in binary,", "A silent language of logic and mystery...", "Building dreams, one line at a time.")
        "jp" -> listOf("桜舞う (Sakura mau)", "コードの中に (Kōdo no naka ni)", "夢を見る (Yume wo miru)...")
        else -> listOf("Code là gió, Bug là mây", "Em là nắng, giữa trời tây...", "Dệt mộng kỹ thuật số...")
    }
    var currentLineIndex by remember { mutableStateOf(0) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        poems.forEachIndexed { index, _ ->
            currentLineIndex = index
            alphaAnim.animateTo(1f, animationSpec = tween(1000))
            delay(1500)
            alphaAnim.animateTo(0f, animationSpec = tween(800))
        }
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        SakuraFallingEffect()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌸", fontSize = 40.sp, modifier = Modifier.padding(bottom = 20.dp))
            Text(poems.getOrElse(currentLineIndex) { "" }, color = SakuraTextDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 40.dp).alpha(alphaAnim.value))
        }
    }
}

@Composable
fun ChatDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().height(500.dp), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(SakuraPrimary, SakuraSecondary))).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("🤖", fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text("Sakura AI", color = Color.White, fontWeight = FontWeight.Bold) }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
                }
                Column(modifier = Modifier.weight(1f).background(Color(0xFFFFF0F5)).padding(16.dp), verticalArrangement = Arrangement.Bottom) {
                    Surface(shape = MaterialTheme.shapes.medium, color = Color.White, modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(0.8f), shadowElevation = 2.dp) {
                        Text("Xin chào! 🌸 Mình là trợ lý ảo của Dũng. Bạn cần giúp gì không?", modifier = Modifier.padding(12.dp), color = SakuraTextDark)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Nhập tin nhắn...", fontSize = 14.sp) }, modifier = Modifier.weight(1f).padding(end = 8.dp), shape = CircleShape, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = SakuraSecondary, focusedBorderColor = SakuraPrimary))
                    IconButton(onClick = {}, modifier = Modifier.size(48.dp).background(SakuraPrimary, CircleShape)) { Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White) }
                }
            }
        }
    }
}

@Composable
fun LanguageButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(CircleShape).background(if (isSelected) SakuraPrimary else Color.Transparent).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
        Text(text, color = if (isSelected) Color.White else SakuraTextLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun Badge(text: String) {
    Surface(color = SakuraSecondary.copy(alpha = 0.3f), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, SakuraSecondary)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold)
    }
}