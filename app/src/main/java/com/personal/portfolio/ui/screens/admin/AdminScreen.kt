package com.personal.portfolio.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.personal.portfolio.data.remote.*
import com.personal.portfolio.ui.components.SakuraFallingEffect
import com.personal.portfolio.ui.theme.SakuraPrimary
import com.personal.portfolio.ui.theme.SakuraSecondary
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(onGoBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isAuth by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("content") }
    var sectionKey by remember { mutableStateOf("hero") }
    var isLoading by remember { mutableStateOf(false) }

    // Data States
    var heroEn by remember { mutableStateOf(HeroData()) }
    var heroVi by remember { mutableStateOf(HeroData()) }
    var heroJp by remember { mutableStateOf(HeroData()) }
    var boxesEn by remember { mutableStateOf<List<SectionBox>>(emptyList()) }
    var boxesVi by remember { mutableStateOf<List<SectionBox>>(emptyList()) }
    var boxesJp by remember { mutableStateOf<List<SectionBox>>(emptyList()) }

    fun handleLogin() {
        if (password == "admin123") isAuth = true
        else Toast.makeText(context, "Sai mật khẩu! 🌸", Toast.LENGTH_SHORT).show()
    }

    fun loadData() {
        scope.launch {
            isLoading = true
            try {
                // [FIX] Gọi hàm getSectionContent
                val data = RetrofitClient.api.getSectionContent(sectionKey)
                if (data != null) {
                    val gson = Gson()
                    if (sectionKey == "hero") {
                        // [FIX] Handle nullable contentEn/Vi/Jp
                        heroEn = if (!data.contentEn.isNullOrEmpty()) gson.fromJson(data.contentEn, HeroData::class.java) else HeroData()
                        heroVi = if (!data.contentVi.isNullOrEmpty()) gson.fromJson(data.contentVi, HeroData::class.java) else HeroData()
                        heroJp = if (!data.contentJp.isNullOrEmpty()) gson.fromJson(data.contentJp, HeroData::class.java) else HeroData()
                    } else if (sectionKey == "profile") {
                        // [FIX] Parse Array to List an toàn
                        boxesEn = if (!data.contentEn.isNullOrEmpty()) gson.fromJson(data.contentEn, Array<SectionBox>::class.java).toMutableList() else emptyList()
                        boxesVi = if (!data.contentVi.isNullOrEmpty()) gson.fromJson(data.contentVi, Array<SectionBox>::class.java).toMutableList() else emptyList()
                        boxesJp = if (!data.contentJp.isNullOrEmpty()) gson.fromJson(data.contentJp, Array<SectionBox>::class.java).toMutableList() else emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLoading = false
        }
    }

    LaunchedEffect(sectionKey) { if(isAuth) loadData() }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SakuraFallingEffect()

        if (!isAuth) {
            IconButton(
                onClick = onGoBack,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SakuraPrimary)
            }

            Card(
                modifier = Modifier.align(Alignment.Center).width(300.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌸 ADMIN ACCESS", color = SakuraPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SakuraPrimary,
                            unfocusedBorderColor = SakuraSecondary
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { handleLogin() }, colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)) {
                        Text("LOGIN")
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌸 DASHBOARD", fontWeight = FontWeight.Bold, color = SakuraPrimary)
                    IconButton(onClick = onGoBack) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = Color.Red)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row {
                    Button(onClick = { activeTab = "content" }, colors = ButtonDefaults.buttonColors(containerColor = if(activeTab=="content") SakuraPrimary else Color.Gray)) { Text("SECTIONS") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { activeTab = "blog" }, colors = ButtonDefaults.buttonColors(containerColor = if(activeTab=="blog") SakuraPrimary else Color.Gray)) { Text("BLOG") }
                }

                Spacer(Modifier.height(16.dp))

                if (activeTab == "content") {
                    Row(Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 10.dp)) {
                        listOf("hero", "profile", "about").forEach { key ->
                            FilterChip(
                                selected = sectionKey == key,
                                onClick = { sectionKey = key },
                                label = { Text(key.uppercase()) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Column(Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
                        if (isLoading) {
                            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            if (sectionKey == "hero") {
                                HeroEditor("EN", heroEn) { heroEn = it }
                                HeroEditor("VI", heroVi) { heroVi = it }
                                HeroEditor("JP", heroJp) { heroJp = it }
                            } else if (sectionKey == "profile") {
                                BoxEditor("EN", boxesEn) { boxesEn = it }
                                BoxEditor("VI", boxesVi) { boxesVi = it }
                                BoxEditor("JP", boxesJp) { boxesJp = it }
                            } else {
                                Text("Tính năng đang cập nhật...", color = Color.Gray, modifier = Modifier.padding(20.dp))
                            }

                            Spacer(Modifier.height(20.dp))
                            Button(onClick = { /* Save API */ }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimary)) {
                                Text("SAVE CHANGES")
                            }
                            Spacer(Modifier.height(50.dp))
                        }
                    }
                } else {
                    Text("Blog Manager đang phát triển...", modifier = Modifier.padding(20.dp))
                }
            }
        }
    }
}