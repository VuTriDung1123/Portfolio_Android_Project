package com.personal.portfolio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
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
    var nextLang by remember { mutableStateOf("vi") }

    var showAdmin by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) }
    var isSwitchingLang by remember { mutableStateOf(false) }

    // --- DATA TỪ API ---
    val uiState by viewModel.uiState.collectAsState()

    // LẤY TEXT TIÊU ĐỀ
    val staticText = when(currentLang) {
        "en" -> SakuraData.en
        "jp" -> SakuraData.jp
        else -> SakuraData.vi
    }

    // Logic đổi ngôn ngữ
    fun switchLanguage(lang: String) {
        if (lang == currentLang) return
        nextLang = lang
        isSwitchingLang = true
        showIntro = true
        viewModel.loadAllData(lang)
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllData(currentLang)
    }

    SakuraPortfolioTheme(lang = currentLang) {
        if (showIntro) {
            val displayLang = if (isSwitchingLang) nextLang else currentLang
            PoeticLoadingScreen(
                lang = displayLang,
                onFinished = {
                    if (isSwitchingLang) {
                        currentLang = nextLang
                        isSwitchingLang = false
                    }
                    showIntro = false
                }
            )
        } else if (showAdmin) {
            AdminScreen(onGoBack = { showAdmin = false })
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                SakuraFallingEffect()

                Scaffold(
                    containerColor = Color.Transparent,
                    // --- TOP BAR ---
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cụm Trái
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .pointerInput(Unit) { detectTapGestures(onLongPress = { showAdmin = true }) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val avatarPainter = if (uiState.hero.avatarUrl.isNotEmpty())
                                        rememberAsyncImagePainter(uiState.hero.avatarUrl)
                                    else
                                        painterResource(R.drawable.vutridung)

                                    Image(painter = avatarPainter, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp).clip(CircleShape))
                                    Image(painter = painterResource(R.drawable.sakura_avatar), contentDescription = null, modifier = Modifier.fillMaxSize())
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (uiState.hero.fullName.isNotEmpty()) uiState.hero.fullName else "Loading...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SakuraTextDark
                                    )
                                    Text(
                                        text = staticText.hero["role"] ?: "Developer",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SakuraPrimary
                                    )
                                }
                            }
                            // Cụm Phải: Dropdown
                            LanguageDropdown(currentLang = currentLang, onLangSelect = { switchLanguage(it) })
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showChatDialog = true }, containerColor = SakuraPrimary, contentColor = Color.White, shape = CircleShape) {
                            Icon(Icons.Default.AutoAwesome, "AI Chat")
                        }
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // 1. HERO SECTION (ĐÃ NÂNG CẤP)
                        item {
                            if (uiState.hero.fullName.isNotEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFFFFE4E1), Color(0xFFFFF0F5))))) {
                                    Icon(painter = painterResource(R.drawable.sakura_avatar), contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(300.dp).align(Alignment.CenterEnd).offset(x = 50.dp))

                                    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                                        // CỘT TRÁI: TEXT INFO
                                        Column(modifier = Modifier.weight(1f)) {
                                            // Greeting Pill
                                            Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
                                                Text(uiState.hero.greeting, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = SakuraPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(Modifier.height(10.dp))

                                            // Main Name
                                            Text(uiState.hero.fullName, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = SakuraTextDark)

                                            // [MỚI] 1. SUB-NAMES (Badge EN/JP giống Web)
                                            Row(modifier = Modifier.padding(vertical = 8.dp).horizontalScroll(rememberScrollState())) {
                                                // Fallback lấy từ staticData nếu API chưa có
                                                val enName = if(uiState.hero.nickName1.isNotEmpty()) uiState.hero.nickName1 else staticText.hero["sub_name_1"]
                                                val jpName = if(uiState.hero.nickName2.isNotEmpty()) uiState.hero.nickName2 else staticText.hero["sub_name_2"]

                                                if (!enName.isNullOrEmpty()) {
                                                    NicknameBadge(label = "EN", name = enName)
                                                    Spacer(Modifier.width(8.dp))
                                                }
                                                if (!jpName.isNullOrEmpty()) {
                                                    NicknameBadge(label = "JP", name = jpName)
                                                }
                                            }

                                            // [MỚI] 2. TYPEWRITER EFFECT (Chữ chạy)
                                            val typePrefix = when(currentLang) {
                                                "vi" -> "Tôi là "
                                                "jp" -> "私は" // Watashi wa
                                                else -> "I am a "
                                            }

                                            TypewriterText(
                                                prefix = typePrefix, // Truyền vào đây
                                                texts = when(currentLang) {
                                                    "vi" -> listOf("Lập trình viên", "Sinh viên CNTT", "Yêu công nghệ")
                                                    "jp" -> listOf("開発者", "学生", "技術愛好家")
                                                    else -> listOf("Developer", "IT Student", "Tech Enthusiast")
                                                },
                                                modifier = Modifier.padding(bottom = 12.dp)
                                            )

                                            // Buttons
                                            Row {
                                                Button(onClick = { navController.navigate("blog") }, colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) { Text("Project", fontSize = 12.sp) }
                                                Spacer(Modifier.width(8.dp))
                                                OutlinedButton(onClick = { navController.navigate("faq") }, colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraTextDark), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) { Text("FAQ", fontSize = 12.sp) }
                                            }
                                        }

                                        // CỘT PHẢI: AVATAR
                                        Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                                            val painter = if (uiState.hero.avatarUrl.isNotEmpty()) rememberAsyncImagePainter(uiState.hero.avatarUrl) else painterResource(R.drawable.vutridung)
                                            Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(125.dp).clip(CircleShape).border(3.dp, Color.White, CircleShape))
                                            Image(painter = painterResource(R.drawable.sakura_avatar), contentDescription = null, modifier = Modifier.fillMaxSize().scale(1.2f))
                                        }
                                    }
                                }
                            } else {
                                EmptyData("Đang tải thông tin...")
                            }
                        }

                        // ... CÁC SECTION KHÁC GIỮ NGUYÊN ...
                        item { SectionCard(staticText.sec_01_about) { if(uiState.about.isNotEmpty()) Text(uiState.about, color = SakuraTextDark, lineHeight = 24.sp) else EmptyData(staticText.msg_no_about) } }
                        item { SectionCard(staticText.sec_02_profile) { if(uiState.profile.isNotEmpty()) { uiState.profile.forEach { box -> if(box.title.isNotEmpty()) Text("★ ${box.title}", fontWeight = FontWeight.Bold, color = SakuraPrimary, modifier = Modifier.padding(top=10.dp)); box.items.forEach { item -> Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) { Text(item.label, color = SakuraTextLight); Text(item.value, fontWeight = FontWeight.Bold, color = SakuraTextDark) } } } } else EmptyData(staticText.msg_no_profile) } }
                        item { SectionCard(staticText.sec_03_cert) { if(uiState.certificates.isNotEmpty()) Text(uiState.certificates, color = SakuraTextDark) else EmptyData(staticText.msg_no_cert) } }
                        item { SectionCard(staticText.sec_04_career) { if(uiState.career.isNotEmpty()) Text(uiState.career, fontStyle = FontStyle.Italic, color = SakuraTextDark) else EmptyData(staticText.msg_no_career) } }
                        item { SectionCard(staticText.sec_05_achievements) { if(uiState.achievements.isNotEmpty()) Text(uiState.achievements, color = SakuraTextDark) else EmptyData(staticText.msg_no_achievements) } }
                        item { SectionCard(staticText.sec_06_skills) { if(uiState.skills.isNotEmpty()) Text(uiState.skills, color = SakuraTextDark) else EmptyData(staticText.msg_no_skills) } }
                        item { SectionCard(staticText.sec_07_exp) { if(uiState.experience.isNotEmpty()) { uiState.experience.forEach { group -> Text(group.title, fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp)); group.items.forEach { exp -> Column(Modifier.padding(bottom = 12.dp, start = 10.dp)) { Text(exp.role, fontWeight = FontWeight.Bold, color = SakuraTextDark); Text(exp.time, fontSize = 12.sp, color = SakuraPrimary); exp.details.forEach { d -> Text("• $d", fontSize = 13.sp, color = SakuraTextLight) } } } } } else EmptyData(staticText.msg_no_exp) } }
                        item { SectionCard(staticText.sec_08_proj) { val projs = uiState.allPosts.filter { it.tag.contains("project") }.take(3); if (projs.isNotEmpty()) { projs.forEach { ProjectPostCard(it, simple = true); Spacer(Modifier.height(8.dp)) }; TextButton(onClick = { navController.navigate("blog") }, Modifier.fillMaxWidth()) { Text(staticText.btn_view_all) } } else EmptyData(staticText.msg_no_proj) } }
                        item { SectionCard(staticText.sec_09_gallery) { if(uiState.gallery.isNotEmpty()) Row(Modifier.horizontalScroll(rememberScrollState())) { uiState.gallery.forEach { url -> Image(painter = rememberAsyncImagePainter(url), contentDescription = null, modifier = Modifier.size(120.dp).padding(end=8.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop) } } else EmptyData(staticText.msg_no_gallery) } }
                        item { SectionCard(staticText.sec_10_blog) { val blogs = uiState.allPosts.filter { !it.tag.contains("project") }.take(3); if (blogs.isNotEmpty()) { blogs.forEach { ProjectPostCard(it, simple = true); Spacer(Modifier.height(8.dp)) }; TextButton(onClick = { navController.navigate("blog") }, Modifier.fillMaxWidth()) { Text(staticText.btn_view_all) } } else EmptyData(staticText.msg_no_blog) } }
                        item { SectionCard(staticText.sec_11_faq) { if(uiState.faq.isNotEmpty()) { uiState.faq.take(3).forEach { FAQItem(it.q, it.a) }; TextButton(onClick = { navController.navigate("faq") }, Modifier.fillMaxWidth()) { Text(staticText.btn_view_all) } } else EmptyData(staticText.msg_no_faq) } }
                        item { SectionCard(staticText.sec_12_contact) { if(uiState.contact.isNotEmpty()) uiState.contact.forEach { box -> box.items.forEach { ContactRowWrapper(it.label, it.value) } } else EmptyData(staticText.msg_no_contact) } }
                    }
                }
                if (showChatDialog) ChatDialog(onDismiss = { showChatDialog = false })
            }
        }
    }
}

// ------------------------------------------------------------------------
// CÁC COMPONENT PHỤ TRỢ (HELPER COMPONENTS)
// ------------------------------------------------------------------------

// [MỚI] Badge Tên phụ (Giống Web)
@Composable
fun NicknameBadge(label: String, name: String) {
    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, SakuraSecondary, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakuraPrimary)
        Spacer(Modifier.width(4.dp))
        Text(text = name, fontSize = 11.sp, color = SakuraTextDark, fontWeight = FontWeight.Bold)
    }
}

// [MỚI] Hiệu ứng chữ chạy (Typewriter)
@Composable
fun TypewriterText(prefix: String, texts: List<String>, modifier: Modifier = Modifier) {
    var textIndex by remember { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(textIndex, texts) {
        val targetText = texts[textIndex % texts.size]
        for (i in 1..targetText.length) {
            displayedText = targetText.substring(0, i)
            delay(100)
        }
        delay(1500)
        for (i in targetText.length downTo 0) {
            displayedText = targetText.substring(0, i)
            delay(50)
        }
        delay(500)
        textIndex++
    }

    Text(
        text = "$prefix$displayedText|",
        color = SakuraTextLight,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        modifier = modifier
    )
}

@Composable
fun LanguageDropdown(currentLang: String, onLangSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val langs = mapOf("vi" to "Tiếng Việt 🇻🇳", "en" to "English 🇺🇸", "jp" to "日本語 🇯🇵")

    Box {
        Row(modifier = Modifier.background(SakuraGlass, CircleShape).border(1.dp, SakuraSecondary, CircleShape).clickable { expanded = true }.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(currentLang.uppercase(), color = SakuraPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, null, tint = SakuraPrimary, modifier = Modifier.size(16.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, offset = DpOffset(0.dp, 8.dp), modifier = Modifier.background(Color.White)) {
            langs.forEach { (code, label) -> DropdownMenuItem(text = { Text(label, color = if(currentLang == code) SakuraPrimary else SakuraTextDark, fontWeight = if(currentLang == code) FontWeight.Bold else FontWeight.Normal) }, onClick = { onLangSelect(code); expanded = false }) }
        }
    }
}

// ... Giữ nguyên các component PoeticLoadingScreen, ChatDialog, scale, etc. từ code cũ ...
fun Modifier.scale(scale: Float): Modifier = this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))

@Composable
fun PoeticLoadingScreen(lang: String, onFinished: () -> Unit) {
    val poems = when(lang) {
        "en" -> listOf("Compiling coffee into code...", "It's not a bug, it's a feature! 🐞", "git push --force (just kidding)...", "Checking StackOverflow for answers...", "Sudo make me a sandwich...", "Works on my machine! ¯\\_(ツ)_/¯", "Trying to exit Vim...", "Hello World! Loading dreams...", "Refactoring spaghetti code...", "Deleting production database... (JK)")
        "jp" -> listOf("バグ修正中... (Fixing bugs...)", "コーヒーをコードに変換中... ☕", "本番環境でテスト中... (Testing in prod)", "全集中、コードの呼吸... (Total concentration)", "サーバーが応答しません... 冗談です (JK)", "猫がキーボードの上を歩いています... 🐈", "WiFiを探しています...", "AIが支配する前にデプロイ中...", "技術的負債を返済中... (Paying tech debt)", "Ctrl + Z の魔法...")
        else -> listOf("Code chạy đúng là do tâm linh...", "3000 dòng code, 1 dòng lỗi...", "Đang triệu hồi bug đi chỗ khác...", "Uống cafe, fix bug, lặp lại... ☕", "Tính năng này là 'tính năng ẩn'...", "Server đang thở oxy...", "Code xong rồi, chỉ chưa chạy thôi...", "Đang Google cách sửa lỗi...", "Đừng tắt máy, đang hack NASA...", "Dev đang ngủ, vui lòng đợi...")
    }
    val randomPoems = remember { poems.shuffled().take(3) }
    var currentLineIndex by remember { mutableStateOf(0) }
    val alphaAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        randomPoems.forEachIndexed { index, _ ->
            currentLineIndex = index
            alphaAnim.animateTo(1f, animationSpec = tween(800))
            delay(1200)
            alphaAnim.animateTo(0f, animationSpec = tween(600))
        }
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        SakuraFallingEffect()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("👾", fontSize = 40.sp, modifier = Modifier.padding(bottom = 20.dp).graphicsLayer { rotationZ = alphaAnim.value * 20f })
            Text(randomPoems.getOrElse(currentLineIndex) { "" }, color = SakuraTextDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 40.dp).alpha(alphaAnim.value))
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
fun Badge(text: String) {
    Surface(color = SakuraSecondary.copy(alpha = 0.3f), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, SakuraSecondary)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun EmptyData(msg: String = "Chưa có dữ liệu 🍃") {
    Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🍃", fontSize = 24.sp)
        Text(msg, color = SakuraTextLight, fontSize = 13.sp)
    }
}

@Composable
fun ContactRowWrapper(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, SakuraSecondary, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        val icon = when { label.contains("Mail", true) || label.contains("Email", true) -> "✉️"; label.contains("Phone", true) || label.contains("Tel", true) -> "📞"; label.contains("Git", true) -> "🐙"; label.contains("Linked", true) -> "💼"; else -> "🌐" }
        Text(icon, fontSize = 20.sp); Spacer(Modifier.width(12.dp))
        Column { Text(label, fontSize = 11.sp, color = SakuraTextLight); Text(value, fontSize = 14.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold) }
    }
}