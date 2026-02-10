package com.personal.portfolio.data

import com.personal.portfolio.R

// --- MODELS ---
data class InfoItem(val label: String, val value: String)
data class ProjectItem(val id: String, val title: String, val desc: String, val tech: String, val imageRes: Int)
data class ExpItem(val time: String, val role: String, val place: String, val details: List<String>)
data class ContactItem(val type: String, val value: String, val icon: String)
// [MỚI] Model cho Blog/Gallery/Cert
data class SimpleItem(val title: String, val subtitle: String, val imageRes: Int)

// --- DATA HOLDER ---
data class PortfolioContent(
    val hero: Map<String, String>,
    val about: String,
    val profile: List<InfoItem>,
    val skills: List<InfoItem>,
    val experience: List<ExpItem>,
    val projects: List<ProjectItem>,
    // [MỚI] Các mục bổ sung
    val certificates: List<SimpleItem>,
    val career: String,
    val achievements: List<SimpleItem>,
    val blog: List<SimpleItem>,
    val gallery: List<SimpleItem>,
    val faq: List<Pair<String, String>>, // Hỏi - Đáp
    val contact: List<ContactItem>
)

object SakuraData {
    // --- 1. TIẾNG VIỆT (VI) ---
    val vi = PortfolioContent(
        hero = mapOf(
            "name" to "Vũ Trí Dũng",
            "sub_name_1" to "David Miller",
            "sub_name_2" to "Akina Aoi (明菜青い)",
            "greeting" to "XIN CHÀO! 🌸",
            "role" to "Lập trình viên Đam mê ✨",
            "desc" to "Sinh viên CNTT | Full-stack Developer | Cyber Security Enthusiast."
        ),
        about = "Tôi là sinh viên năm 3 ngành Công nghệ Thông tin tại trường Đại học Giao thông Vận tải TP.HCM. Với niềm đam mê mãnh liệt về công nghệ, tôi luôn tìm tòi học hỏi các kỹ thuật mới.",
        profile = listOf(
            InfoItem("Họ tên", "Vũ Trí Dũng"),
            InfoItem("Năm sinh", "2005"),
            InfoItem("Công việc", "Sinh viên / Freelancer"),
            InfoItem("Địa chỉ", "TP. Hồ Chí Minh")
        ),
        skills = listOf(
            InfoItem("Frontend", "React, Next.js, Jetpack Compose"),
            InfoItem("Backend", "C#, .NET, Node.js"),
            InfoItem("Database", "SQL Server, MySQL"),
            InfoItem("Tools", "Git, Docker, VMware")
        ),
        experience = listOf(
            ExpItem("2023 - Nay", "Nghiên cứu viên", "Lab Mạng Máy Tính (UTH)", listOf("Nghiên cứu hạ tầng mạng SD-WAN.", "Quản trị hệ thống VMware vSphere.")),
            ExpItem("2024", "Mobile Developer", "DuckTrack App", listOf("Phát triển ứng dụng quản lý công việc.", "Sử dụng Kotlin & Jetpack Compose."))
        ),
        projects = listOf(
            ProjectItem("1", "DuckTrack App", "App quản lý thời gian.", "Android / Kotlin", R.drawable.ic_launcher_foreground),
            ProjectItem("2", "Sakura Portfolio", "Web cá nhân 3D.", "Next.js / Three.js", R.drawable.ic_launcher_foreground)
        ),
        // [MỚI] Dữ liệu bổ sung
        certificates = listOf(
            SimpleItem("IELTS 6.5", "Tiếng Anh", R.drawable.ic_launcher_foreground),
            SimpleItem("JLPT N3", "Tiếng Nhật (Đang học)", R.drawable.ic_launcher_foreground),
            SimpleItem("AWS Cloud", "Practitioner", R.drawable.ic_launcher_foreground)
        ),
        career = "Trong 5 năm tới, tôi đặt mục tiêu trở thành một Full-stack Developer chuyên nghiệp và chuyên gia về An ninh mạng (Cybersecurity).",
        achievements = listOf(
            SimpleItem("Giải Khuyến Khích", "Olympic Tin học Sinh viên", R.drawable.ic_launcher_foreground),
            SimpleItem("Top 10", "Dự án Sáng tạo UTH", R.drawable.ic_launcher_foreground)
        ),
        blog = listOf(
            SimpleItem("Cách học Jetpack Compose", "Chia sẻ kinh nghiệm", R.drawable.ic_launcher_foreground),
            SimpleItem("SD-WAN là gì?", "Kiến thức mạng", R.drawable.ic_launcher_foreground)
        ),
        gallery = listOf(
            SimpleItem("Hội thảo Tech", "2024", R.drawable.ic_launcher_foreground),
            SimpleItem("Team Building", "Lab Network", R.drawable.ic_launcher_foreground)
        ),
        faq = listOf(
            "Bạn có nhận Freelance không?" to "Có, mình luôn sẵn sàng!",
            "Tech stack chính là gì?" to "Mình chuyên về .NET và React/Next.js."
        ),
        contact = listOf(
            ContactItem("Email", "dungvutri25@gmail.com", "✉️"),
            ContactItem("GitHub", "github.com/VuTriDung1123", "🐙")
        )
    )

    // --- 2. TIẾNG ANH (EN) ---
    val en = vi.copy( // Copy cấu trúc VI và sửa nội dung
        hero = mapOf(
            "name" to "David Miller",
            "sub_name_1" to "Vu Tri Dung",
            "sub_name_2" to "Akina Aoi",
            "greeting" to "HELLO WORLD! 🌸",
            "role" to "Passionate Developer ✨",
            "desc" to "IT Student | Full-stack Developer | Cyber Security Enthusiast."
        ),
        about = "I am a 3rd-year IT student at HCMC University of Transport (UTH). Passionate about technology and building amazing products.",
        profile = listOf(InfoItem("Name", "David Miller"), InfoItem("Job", "Student"), InfoItem("Location", "HCMC")),
        career = "My goal is to become a professional Full-stack Developer and Cybersecurity Expert within the next 5 years.",
        certificates = listOf(SimpleItem("IELTS 6.5", "English", R.drawable.ic_launcher_foreground), SimpleItem("AWS Cloud", "Practitioner", R.drawable.ic_launcher_foreground)),
        faq = listOf("Available for Freelance?" to "Yes, I am!", "Main Tech Stack?" to ".NET & React/Next.js")
    )

    // --- 3. TIẾNG NHẬT (JP) ---
    val jp = vi.copy(
        hero = mapOf(
            "name" to "明菜青い (Akina Aoi)",
            "sub_name_1" to "Vu Tri Dung",
            "sub_name_2" to "David Miller",
            "greeting" to "こんにちは！ 🌸",
            "role" to "情熱的な開発者 ✨",
            "desc" to "IT学生 | フルスタック開発者 | サイバーセキュリティ愛好家。"
        ),
        about = "ホーチミン市交通大学 (UTH) の情報技術学部の3年生です。技術への強い情熱を持っています。",
        profile = listOf(InfoItem("氏名", "ヴー・チー・ズン"), InfoItem("職業", "学生"), InfoItem("場所", "ホーチミン市")),
        career = "今後5年以内に、プロのフルスタック開発者およびサイバーセキュリティの専門家になることを目指しています。",
        certificates = listOf(SimpleItem("JLPT N3", "日本語", R.drawable.ic_launcher_foreground)),
        faq = listOf("フリーランスは可能ですか？" to "はい、可能です！", "主な技術スタックは？" to ".NET と React/Next.js")
    )
}