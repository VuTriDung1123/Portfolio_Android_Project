package com.personal.portfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.personal.portfolio.data.remote.Post
import com.personal.portfolio.ui.theme.*

// --- 1. SECTION CARD ---
@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SakuraGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "✿ $title ✿",
                style = MaterialTheme.typography.titleMedium,
                color = SakuraPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = SakuraSecondary, thickness = 1.dp)
            content()
        }
    }
}

// --- 2. PROJECT / POST CARD ---
@Composable
fun ProjectPostCard(post: Post, simple: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            val imgUrl = try {
                val list = Gson().fromJson(post.images, Array<String>::class.java)
                if (list.isNotEmpty()) list[0] else null
            } catch (e: Exception) { null }

            if (imgUrl != null && !simple) {
                Image(
                    painter = rememberAsyncImagePainter(imgUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(10.dp))
                )
                Spacer(Modifier.height(10.dp))
            }

            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SakuraTextDark)

            if (!simple) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = post.tag,
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.background(SakuraPrimary, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = post.content,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = SakuraTextLight,
                    fontSize = 13.sp
                )
            } else {
                Text(
                    text = post.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = SakuraTextLight,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --- 3. FAQ ITEM ---
@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, SakuraSecondary, RoundedCornerShape(10.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = question, fontWeight = FontWeight.Bold, color = SakuraPrimary, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SakuraTextLight)
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = answer, color = SakuraTextDark, fontSize = 14.sp)
        }
    }
}

// --- 4. EMPTY DATA ---
@Composable
fun EmptyData(msg: String = "Chưa có dữ liệu 🍃") {
    Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🍃", fontSize = 24.sp)
        Text(msg, color = SakuraTextLight, fontSize = 13.sp)
    }
}

// --- 5. CONTACT ROW ---
@Composable
fun ContactRowWrapper(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, SakuraSecondary, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when {
            label.contains("Mail", true) || label.contains("Email", true) -> "✉️"
            label.contains("Phone", true) || label.contains("Tel", true) -> "📞"
            label.contains("Git", true) -> "🐙"
            label.contains("Linked", true) -> "💼"
            else -> "🌐"
        }
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = SakuraTextLight)
            Text(text = value, fontSize = 14.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold)
        }
    }
}