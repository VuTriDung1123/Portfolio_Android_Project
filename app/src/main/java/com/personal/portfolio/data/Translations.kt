package com.personal.portfolio.data

import com.personal.portfolio.R

// --- 1. ĐỊNH NGHĨA CẤU TRÚC DỮ LIỆU (MODEL) ---
data class InfoItem(val label: String, val value: String)
data class ProjectItem(
    val id: String,
    val title: String,
    val desc: String,
    val tech: String,
    val imageRes: Int // ID ảnh trong res/drawable
)
data class ExpItem(
    val time: String,
    val role: String,
    val place: String,
    val details: List<String>
)
data class ContactItem(val type: String, val value: String, val icon: String)

// --- 2. KHO DỮ LIỆU CHÍNH (SAKURA DATA) ---
object SakuraData {
    // > Hero Section
    val hero = mapOf(
        "name" to "Vũ Trí Dũng",
        "sub_name_1" to "David Miller",
        "sub_name_2" to "Akina Aoi (明菜青い)",
        "greeting" to "XIN CHÀO! 🌸",
        "role" to "Lập trình viên Đam mê ✨",
        "desc" to "Sinh viên CNTT | Full-stack Developer | Cyber Security Enthusiast.\nĐam mê biến những dòng code khô khan thành trải nghiệm người dùng tuyệt vời."
    )

    // > About Section
    val about = "Tôi là sinh viên năm 3 ngành Công nghệ Thông tin tại trường Đại học Giao thông Vận tải TP.HCM (UTH). " +
            "Với niềm đam mê mãnh liệt về công nghệ, tôi luôn tìm tòi học hỏi các kỹ thuật mới từ Web 3D, Mobile App đến Hệ thống mạng.\n\n" +
            "Phương châm: \"Code bằng cả trái tim.\""

    // > Profile (Thông tin cá nhân)
    val profile = listOf(
        InfoItem("Họ tên", "Vũ Trí Dũng"),
        InfoItem("Năm sinh", "2005"),
        InfoItem("Công việc", "Sinh viên / Freelancer"),
        InfoItem("Địa chỉ", "TP. Hồ Chí Minh, Việt Nam")
    )

    // > Skills (Kỹ năng)
    val skills = listOf(
        InfoItem("Frontend", "React, Next.js, Jetpack Compose, Tailwind CSS"),
        InfoItem("Backend", "C#, .NET, Node.js, Spring Boot"),
        InfoItem("Database", "SQL Server, MySQL, MongoDB"),
        InfoItem("Tools", "Git, Docker, VMware, Figma, Android Studio")
    )

    // > Experience (Kinh nghiệm)
    val experience = listOf(
        ExpItem(
            time = "2023 - Nay",
            role = "Nghiên cứu viên & Sinh viên",
            place = "Lab Mạng Máy Tính (UTH)",
            details = listOf(
                "Nghiên cứu về hạ tầng mạng SD-WAN và các giao thức định tuyến.",
                "Cấu hình và quản trị hệ thống Server ảo hóa với VMware vSphere.",
                "Thực hành tấn công và phòng thủ mạng (Cyber Security)."
            )
        ),
        ExpItem(
            time = "2024",
            role = "Mobile Developer (Dự án cá nhân)",
            place = "DuckTrack App",
            details = listOf(
                "Phát triển ứng dụng quản lý thời gian và công việc (Pomodoro).",
                "Sử dụng Kotlin và Jetpack Compose kiến trúc MVVM.",
                "Tích hợp Room Database và WorkManager."
            )
        )
    )

    // > Projects (Dự án tiêu biểu)
    // LƯU Ý: Bạn hãy thay R.drawable.ic_launcher_foreground bằng ảnh chụp dự án thật của bạn nhé!
    val projects = listOf(
        ProjectItem(
            id = "1",
            title = "DuckTrack App",
            desc = "Ứng dụng Mobile giúp quản lý công việc và tăng sự tập trung (Pomodoro).",
            tech = "Android / Kotlin / Jetpack Compose",
            imageRes = R.drawable.ic_launcher_foreground
        ),
        ProjectItem(
            id = "2",
            title = "Sakura Portfolio",
            desc = "Website cá nhân phong cách Anime với hiệu ứng 3D và AI Chatbot.",
            tech = "Next.js / Three.js / Gemini AI",
            imageRes = R.drawable.ic_launcher_foreground
        ),
        ProjectItem(
            id = "3",
            title = "Algorithm Visualizer",
            desc = "Web App mô phỏng trực quan các thuật toán sắp xếp và tìm kiếm.",
            tech = "React / TypeScript / Vercel",
            imageRes = R.drawable.ic_launcher_foreground
        ),
        ProjectItem(
            id = "4",
            title = "TCP Chat System",
            desc = "Hệ thống chat mạng LAN bảo mật mô hình Client-Server.",
            tech = "C# / .NET / Socket Programming",
            imageRes = R.drawable.ic_launcher_foreground
        )
    )

    // > Contact (Liên hệ)
    val contact = listOf(
        ContactItem("Email", "dungvutri25@gmail.com", "✉️"),
        ContactItem("Phone", "(+84) 931 466 930", "📞"),
        ContactItem("GitHub", "github.com/VuTriDung1123", "🐙"),
        ContactItem("LinkedIn", "linkedin.com/in/dungvutri231", "💼")
    )
}