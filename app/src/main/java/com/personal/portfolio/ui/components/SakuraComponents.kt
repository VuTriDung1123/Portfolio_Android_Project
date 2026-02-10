package com.personal.portfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.personal.portfolio.data.*
import com.personal.portfolio.ui.theme.*

// --- 1. SECTION CARD (Khung chứa từng mục) ---
@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SakuraGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Tiêu đề Section (Có hoa trang trí)
            Text(
                text = "✿ $title ✿",
                style = MaterialTheme.typography.titleMedium,
                color = SakuraPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = SakuraSecondary,
                thickness = 1.dp
            )
            // Nội dung bên trong
            content()
        }
    }
}

// --- 2. INFO ROW (Dùng cho Profile) ---
@Composable
fun InfoRow(item: InfoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.label, color = SakuraTextLight, fontSize = 14.sp)
        Text(text = item.value, color = SakuraTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// --- 3. SKILL CHIP (Dùng cho Kỹ năng) ---
@Composable
fun SkillItem(item: InfoItem) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = "✦ ${item.label}", color = SakuraPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = item.value, color = SakuraTextDark, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

// --- 4. EXPERIENCE ITEM (Kinh nghiệm) ---
@Composable
fun ExperienceCard(item: ExpItem) {
    Row(modifier = Modifier.padding(bottom = 20.dp)) {
        // Đường kẻ timeline bên trái
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.dp)) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(SakuraPrimary)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(80.dp) // Chiều cao đường kẻ
                    .background(SakuraSecondary)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Nội dung
        Column {
            Text(text = item.role, fontWeight = FontWeight.Bold, color = SakuraTextDark, fontSize = 16.sp)
            Text(text = item.place, color = SakuraTextLight, fontSize = 14.sp)

            // Badge thời gian
            Surface(
                color = SakuraBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = item.time,
                    color = SakuraPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Chi tiết (bullet points)
            item.details.forEach { detail ->
                Text(text = "• $detail", fontSize = 13.sp, color = SakuraTextDark, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

// --- 5. PROJECT CARD (Dự án) ---
@Composable
fun ProjectCard(project: ProjectItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Ảnh dự án
            Image(
                painter = painterResource(id = project.imageRes),
                contentDescription = project.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            // Thông tin
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SakuraTextDark)
                Text(text = project.tech, color = SakuraPrimary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = project.desc,
                    fontSize = 13.sp,
                    color = SakuraTextLight,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- 6. CONTACT ROW (Liên hệ) ---
@Composable
fun ContactRow(item: ContactItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, SakuraSecondary, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = item.type, fontSize = 12.sp, color = SakuraTextLight)
            Text(text = item.value, fontSize = 14.sp, color = SakuraTextDark, fontWeight = FontWeight.SemiBold)
        }
    }
}

// --- 7. SAKURA TOP NAV (Thanh điều hướng trên cùng) ---
@Composable
fun SakuraTopNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start // Căn trái
    ) {
        // 1. Avatar (Gọi Component SakuraAvatar của bạn)
        // Lưu ý: Đảm bảo file SakuraAvatar.kt nằm đúng package
        SakuraAvatar(modifier = Modifier.size(50.dp))

        Spacer(modifier = Modifier.width(15.dp))

        // 2. Tên & Danh hiệu
        Column {
            Text(
                text = "Vũ Trí Dũng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SakuraTextDark
            )
            Text(
                text = "Lập trình viên Đam mê ✨",
                style = MaterialTheme.typography.bodySmall,
                color = SakuraPrimary
            )
        }
    }
}