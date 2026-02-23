package com.personal.portfolio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import com.personal.portfolio.ui.viewmodel.ChatMessage

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    // --- 1. KHAI BÁO UI STATE TRƯỚC ĐỂ THAM CHIẾU (Fix lỗi Unresolved reference) ---
    val uiState by viewModel.uiState.collectAsState()

    // --- 2. QUẢN LÝ TRẠNG THÁI ---
    var currentLang by remember { mutableStateOf("vi") }
    var nextLang by remember { mutableStateOf("vi") }
    var showAdmin by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var isSwitchingLang by remember { mutableStateOf(false) }

    // --- 3. QUẢN LÝ INTRO (Fix lỗi Cannot infer type & load lại) ---
    // Khởi tạo dựa trên việc đã có data Hero hay chưa
    var showIntro by remember {
        mutableStateOf<Boolean>(uiState.hero.fullName.isEmpty())
    }

    // Tự động tắt Intro khi data về
    LaunchedEffect(uiState.hero.fullName) {
        if (uiState.hero.fullName.isNotEmpty()) {
            showIntro = false
        }
    }

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

    // Tự động load data (ViewModel đã có logic chặn load lặp)
    LaunchedEffect(currentLang) {
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
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFE4E1).copy(alpha = 0.95f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                        item {
                            if (uiState.hero.fullName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 60.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                brush = Brush.linearGradient(
                                                    listOf(Color(0xFFFFE4E1), Color(0xFFFFF0F5))
                                                )
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.sakura_avatar),
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier.size(400.dp).align(Alignment.BottomCenter).offset(y = 50.dp)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 80.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Surface(
                                                color = Color.White,
                                                shape = RoundedCornerShape(20.dp),
                                                shadowElevation = 1.dp
                                            ) {
                                                Text(
                                                    text = uiState.hero.greeting,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                                    color = SakuraPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(Modifier.height(12.dp))

                                            Text(
                                                text = uiState.hero.fullName,
                                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                                                color = SakuraTextDark,
                                                textAlign = TextAlign.Center
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .padding(vertical = 12.dp)
                                                    .horizontalScroll(rememberScrollState()),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val enName = if(uiState.hero.nickName1.isNotEmpty()) uiState.hero.nickName1 else staticText.hero["sub_name_1"]
                                                val jpName = if(uiState.hero.nickName2.isNotEmpty()) uiState.hero.nickName2 else staticText.hero["sub_name_2"]

                                                if (!enName.isNullOrEmpty()) NicknameBadge(label = "EN", name = enName)
                                                if (!enName.isNullOrEmpty() && !jpName.isNullOrEmpty()) Spacer(Modifier.width(8.dp))
                                                if (!jpName.isNullOrEmpty()) NicknameBadge(label = "JP", name = jpName)
                                            }

                                            val typePrefix = when(currentLang) {
                                                "vi" -> "Tôi là "
                                                "jp" -> "私は"
                                                else -> "I am a "
                                            }
                                            TypewriterText(
                                                prefix = typePrefix,
                                                texts = when(currentLang) {
                                                    "vi" -> listOf("Lập trình viên", "Sinh viên CNTT", "Yêu công nghệ")
                                                    "jp" -> listOf("開発者", "学生", "技術愛好 gia")
                                                    else -> listOf("Developer", "IT Student", "Tech Enthusiast")
                                                },
                                                modifier = Modifier.padding(bottom = 20.dp)
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                            ) {
                                                Button(
                                                    onClick = { navController.navigate("blog") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)
                                                ) { Text("Project", fontSize = 13.sp) }

                                                Spacer(Modifier.width(8.dp))

                                                OutlinedButton(
                                                    onClick = { /* CV link */ },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraPrimary),
                                                    border = BorderStroke(1.dp, SakuraPrimary)
                                                ) {
                                                    Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("CV", fontSize = 13.sp)
                                                }

                                                Spacer(Modifier.width(8.dp))

                                                OutlinedButton(
                                                    onClick = { navController.navigate("faq") },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraTextDark),
                                                    border = BorderStroke(1.dp, Color.White)
                                                ) { Text("FAQ", fontSize = 13.sp) }
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier.size(130.dp).align(Alignment.TopCenter),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val painter = if (uiState.hero.avatarUrl.isNotEmpty())
                                            rememberAsyncImagePainter(uiState.hero.avatarUrl)
                                        else
                                            painterResource(R.drawable.vutridung)

                                        Image(
                                            painter = painter,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(130.dp).clip(CircleShape).border(4.dp, Color.White, CircleShape)
                                        )
                                        Image(
                                            painter = painterResource(R.drawable.sakura_avatar),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().scale(1.25f)
                                        )
                                    }
                                }
                            } else {
                                EmptyData("Đang tải thông tin...")
                            }
                        }

                        // --- CÁC SECTION KHÁC ---
                        item { SectionCard(staticText.sec_01_about) { if(uiState.about.isNotEmpty()) Text(uiState.about, color = SakuraTextDark, lineHeight = 24.sp) else EmptyData(staticText.msg_no_about) } }
                        item { SectionCard(staticText.sec_02_profile) { if(uiState.profile.isNotEmpty()) { uiState.profile.forEach { box -> if(box.title.isNotEmpty()) Text("★ ${box.title}", fontWeight = FontWeight.Bold, color = SakuraPrimary, modifier = Modifier.padding(top=10.dp)); box.items.forEach { item -> Row(Modifier.fillMaxWidth().padding(vertical=4.dp), Arrangement.SpaceBetween) { Text(item.label, color = SakuraTextLight); Text(item.value, fontWeight = FontWeight.Bold, color = SakuraTextDark) } } } } else EmptyData(staticText.msg_no_profile) } }
                        item { SectionCard(staticText.sec_03_cert) { Column { val itCerts = uiState.allPosts.filter { it.tag.lowercase() == "tech_certs" }; HorizontalPostLane("❖ IT Certificates", itCerts, navController); Spacer(Modifier.height(16.dp)); val langCerts = uiState.allPosts.filter { it.tag.lowercase() == "lang_certs" }; HorizontalPostLane("❖ Language Certificates", langCerts, navController); Spacer(Modifier.height(16.dp)); val otherCerts = uiState.allPosts.filter { it.tag.lowercase() == "other_certs" }; HorizontalPostLane("❖ Other Certificates", otherCerts, navController) } } }
                        item { SectionCard(staticText.sec_04_career) { if(uiState.career.isNotEmpty()) Text(uiState.career, fontStyle = FontStyle.Italic, color = SakuraTextDark) else EmptyData(staticText.msg_no_career) } }
                        item { SectionCard(staticText.sec_05_achievements) { val achievements = uiState.allPosts.filter { it.tag.lowercase() == "achievements" }; HorizontalPostLane(null, achievements, navController) } }
                        item { SectionCard(staticText.sec_06_skills) { if(uiState.skills.isNotEmpty()) Text(uiState.skills, color = SakuraTextDark) else EmptyData(staticText.msg_no_skills) } }
                        item { SectionCard(staticText.sec_07_exp) { if(uiState.experience.isNotEmpty()) { uiState.experience.forEach { group -> Text(group.title, fontWeight = FontWeight.Bold, color = SakuraPrimary, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp)); group.items.forEach { exp -> Column(Modifier.padding(bottom = 12.dp, start = 10.dp)) { Text(exp.role, fontWeight = FontWeight.Bold, color = SakuraTextDark); Text(exp.time, fontSize = 12.sp, color = SakuraPrimary); exp.details.forEach { d -> Text("• $d", fontSize = 13.sp, color = SakuraTextLight) } } } } } else EmptyData(staticText.msg_no_exp) } }
                        item { SectionCard(staticText.sec_08_proj) { Column { val uniProjs = uiState.allPosts.filter { it.tag.lowercase() == "uni_projects" }; HorizontalPostLane("❖ University Projects", uniProjs, navController); Spacer(Modifier.height(16.dp)); val perProjs = uiState.allPosts.filter { it.tag.lowercase() == "personal_projects" }; HorizontalPostLane("❖ Personal Projects", perProjs, navController) } } }
                        item { SectionCard(staticText.sec_09_gallery) { val itEvents = uiState.allPosts.filter { it.tag.lowercase() == "it_events" }; HorizontalPostLane(null, itEvents, navController) } }
                        item { SectionCard(staticText.sec_10_blog) { val blogs = uiState.allPosts.filter { !it.tag.lowercase().contains("project") }.take(3); if (blogs.isNotEmpty()) { Column { Row(Modifier.horizontalScroll(rememberScrollState())) { blogs.forEach { HomePostCard(it, navController) } }; TextButton(onClick = { navController.navigate("blog") }, Modifier.align(Alignment.End)) { Text(staticText.btn_view_all, color = SakuraPrimary, fontWeight = FontWeight.Bold) } } } else EmptyData(staticText.msg_no_blog) } }
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
fun ChatDialog(onDismiss: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    var userText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()


    // Tự động cuộn xuống khi có tin nhắn mới
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) scrollState.animateScrollToItem(chatHistory.size - 1)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().height(550.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Giữ nguyên UI Sakura cũ)
                Row(modifier = Modifier.fillMaxWidth().background(SakuraPrimary).padding(16.dp)) {
                    Text("🌸 Sakura AI Assistant", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Danh sách tin nhắn động
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.weight(1f).background(Color(0xFFFFF0F5)).padding(16.dp)
                ) {
                    items(chatHistory) { msg ->
                        ChatBubble(msg)
                    }
                }

                // Ô nhập liệu
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = userText,
                        onValueChange = { userText = it },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        placeholder = { Text("Hỏi Sakura...") }
                    )
                    IconButton(
                        onClick = {
                            if (userText.isNotBlank()) {
                                viewModel.sendMessage(userText)
                                userText = "" // Xóa text sau khi gửi
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp).background(SakuraPrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (msg.isUser) SakuraPrimary else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 0.dp,
                bottomEnd = if (msg.isUser) 0.dp else 16.dp
            ),
            shadowElevation = 1.dp
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(12.dp),
                color = if (msg.isUser) Color.White else SakuraTextDark,
                fontSize = 14.sp
            )
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

@Composable
fun HomePostCard(post: com.personal.portfolio.data.remote.Post, navController: NavController) {
    Card(
        modifier = Modifier
            .width(280.dp) // Độ rộng cố định để vuốt ngang đẹp
            .padding(end = 12.dp)
            .clickable { navController.navigate("post_detail/${post.id}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // 1. Ảnh Thumbnail
            val imageList = try {
                if (post.images.contains("http")) post.images.replace("[\"", "").replace("\"]", "").split("\",\"")[0]
                else ""
            } catch (e: Exception) { "" }

            if (imageList.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(imageList.trim()),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(SakuraGlass), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, null, tint = SakuraSecondary)
                }
            }

            // 2. Nội dung text
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakuraTextDark,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Surface(color = SakuraPrimary, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = post.tag,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalPostLane(
    title: String? = null,
    posts: List<com.personal.portfolio.data.remote.Post>,
    navController: NavController
) {
    Column {
        if (title != null) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = SakuraTextDark,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (posts.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                posts.forEach { post ->
                    HomePostCard(post = post, navController = navController)
                }
            }
        } else {
            Text("Chưa có dữ liệu 🍃", fontSize = 12.sp, color = SakuraTextLight, modifier = Modifier.padding(start = 8.dp))
        }
    }
}