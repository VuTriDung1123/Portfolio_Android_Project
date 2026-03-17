package com.personal.portfolio.data

// --- MODELS ---
data class InfoItem(val label: String, val value: String)
data class ProjectItem(val id: String, val title: String, val desc: String, val tech: String, val imageRes: Int)
data class ExpItem(val time: String, val role: String, val place: String, val details: List<String>)
data class ContactItem(val type: String, val value: String, val icon: String)
data class SimpleItem(val title: String, val subtitle: String, val imageRes: Int)

// --- DATA HOLDER ---
data class PortfolioContent(
    val hero: Map<String, String>,
    val about: String,
    val profile: List<InfoItem>,
    val skills: List<InfoItem>,
    val experience: List<ExpItem>,
    val projects: List<ProjectItem>,
    val certificates: List<SimpleItem>,
    val career: String,
    val achievements: List<SimpleItem>,
    val blog: List<SimpleItem>,
    val gallery: List<SimpleItem>,
    val faq: List<Pair<String, String>>,
    val contact: List<ContactItem>,

    // [MỚI] TIÊU ĐỀ SECTION
    val sec_01_about: String,
    val sec_02_profile: String,
    val sec_03_cert: String,
    val sec_04_career: String,
    val sec_05_achievements: String,
    val sec_06_skills: String,
    val sec_07_exp: String,
    val sec_08_proj: String,
    val sec_09_gallery: String,
    val sec_10_blog: String,
    val sec_11_faq: String,
    val sec_12_contact: String,
    val btn_view_all: String,

    // [MỚI] THÔNG BÁO DỮ LIỆU TRỐNG (LOCALIZED EMPTY STATES)
    val msg_no_about: String,
    val msg_no_profile: String,
    val msg_no_cert: String,
    val msg_no_career: String,
    val msg_no_achievements: String,
    val msg_no_skills: String,
    val msg_no_exp: String,
    val msg_no_proj: String,
    val msg_no_gallery: String,
    val msg_no_blog: String,
    val msg_no_faq: String,
    val msg_no_contact: String,

    // [MỚI] CHO TRANG BLOG
    val blog_title: String,
    val blog_subtitle: String,
    val blog_search_hint: String,
    val blog_sort_newest: String,
    val blog_sort_oldest: String,
    val blog_tab_all: String
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
        about = "Tôi là sinh viên năm 3 ngành Công nghệ Thông tin...",
        profile = listOf(), // Giữ nguyên data mẫu của bạn nếu muốn, hoặc để rỗng chờ API
        skills = listOf(),
        experience = listOf(),
        projects = listOf(),
        certificates = listOf(),
        career = "",
        achievements = listOf(),
        blog = listOf(),
        gallery = listOf(),
        faq = listOf(),
        contact = listOf(),

        // Tiêu đề
        sec_01_about = "01. GIỚI THIỆU",
        sec_02_profile = "02. HỒ SƠ",
        sec_03_cert = "03. CHỨNG CHỈ",
        sec_04_career = "04. MỤC TIÊU NGHỀ NGHIỆP",
        sec_05_achievements = "05. THÀNH TỰU",
        sec_06_skills = "06. KỸ NĂNG",
        sec_07_exp = "07. KINH NGHIỆM",
        sec_08_proj = "08. DỰ ÁN",
        sec_09_gallery = "09. THƯ VIỆN ẢNH",
        sec_10_blog = "10. BÀI VIẾT (BLOG)",
        sec_11_faq = "11. HỎI ĐÁP (FAQ)",
        sec_12_contact = "12. LIÊN HỆ",
        btn_view_all = "Xem tất cả",

        // [MỚI] Thông báo trống Tiếng Việt
        msg_no_about = "Đang cập nhật giới thiệu...",
        msg_no_profile = "Chưa có hồ sơ chi tiết.",
        msg_no_cert = "Chưa có chứng chỉ nào.",
        msg_no_career = "Chưa có mục tiêu nghề nghiệp.",
        msg_no_achievements = "Chưa có thành tựu nổi bật.",
        msg_no_skills = "Chưa cập nhật kỹ năng.",
        msg_no_exp = "Chưa có kinh nghiệm làm việc.",
        msg_no_proj = "Chưa có dự án nào.",
        msg_no_gallery = "Thư viện ảnh đang trống.",
        msg_no_blog = "Chưa có bài viết mới.",
        msg_no_faq = "Chưa có câu hỏi thường gặp.",
        msg_no_contact = "Chưa có thông tin liên hệ.",

        // Blog
        blog_title = "THƯ VIỆN BÀI VIẾT",
        blog_subtitle = "Code là thơ, viết bằng logic.",
        blog_search_hint = "Tìm kiếm bài viết...",
        blog_sort_newest = "Mới nhất",
        blog_sort_oldest = "Lâu nhất",
        blog_tab_all = "Tất cả"
    )

    // --- 2. TIẾNG ANH (EN) ---
    val en = vi.copy(
        hero = mapOf(
            "name" to "David Miller",
            "role" to "Passionate Dev ✨", // Tagline tiếng Anh
            "greeting" to "HELLO WORLD! 🌸"
        ),
        sec_01_about = "01. ABOUT ME",
        sec_02_profile = "02. PROFILE",
        sec_03_cert = "03. CERTIFICATES",
        sec_04_career = "04. CAREER GOALS",
        sec_05_achievements = "05. ACHIEVEMENTS",
        sec_06_skills = "06. SKILLS",
        sec_07_exp = "07. EXPERIENCE",
        sec_08_proj = "08. PROJECTS",
        sec_09_gallery = "09. GALLERY",
        sec_10_blog = "10. BLOG",
        sec_11_faq = "11. FAQ",
        sec_12_contact = "12. CONTACT",
        btn_view_all = "View All",

        // [MỚI] Thông báo trống Tiếng Anh
        msg_no_about = "Updating introduction...",
        msg_no_profile = "No profile details yet.",
        msg_no_cert = "No certificates found.",
        msg_no_career = "No career goals yet.",
        msg_no_achievements = "No achievements yet.",
        msg_no_skills = "No skills updated.",
        msg_no_exp = "No work experience yet.",
        msg_no_proj = "No projects found.",
        msg_no_gallery = "Gallery is empty.",
        msg_no_blog = "No posts available.",
        msg_no_faq = "No FAQs available.",
        msg_no_contact = "No contact info yet.",

        blog_title = "BLOG LIBRARY",
        blog_subtitle = "Code is poetry written in logic.",
        blog_search_hint = "Search stories...",
        blog_sort_newest = "Newest",
        blog_sort_oldest = "Oldest",
        blog_tab_all = "All Stories"
    )

    // --- 3. TIẾNG NHẬT (JP) ---
    val jp = vi.copy(
        hero = mapOf(
            "name" to "明菜青い (Akina Aoi)",
            "role" to "情熱的な開発者 ✨", // Tagline tiếng Nhật
            "greeting" to "こんにちは！ 🌸"
        ),
        sec_01_about = "01. 私について",
        sec_02_profile = "02. プロフィール",
        sec_03_cert = "03. 証明書",
        sec_04_career = "04. キャリア目標",
        sec_05_achievements = "05. 実績",
        sec_06_skills = "06. スキル",
        sec_07_exp = "07. 経験",
        sec_08_proj = "08. プロジェクト",
        sec_09_gallery = "09. ギャラリー",
        sec_10_blog = "10. ブログ",
        sec_11_faq = "11. よくある質問",
        sec_12_contact = "12. 連絡先",
        btn_view_all = "すべて見る",

        // [MỚI] Thông báo trống Tiếng Nhật
        msg_no_about = "紹介を更新中...",
        msg_no_profile = "プロフィール詳細なし。",
        msg_no_cert = "証明書がありません。",
        msg_no_career = "キャリア目標なし。",
        msg_no_achievements = "実績がありません。",
        msg_no_skills = "スキル未更新。",
        msg_no_exp = "実務経験なし。",
        msg_no_proj = "プロジェクトが見つかりません。",
        msg_no_gallery = "ギャラリーは空です。",
        msg_no_blog = "投稿はありません。",
        msg_no_faq = "よくある質問はありません。",
        msg_no_contact = "連絡先情報なし。",

        blog_title = "ブログライブラリ", // Blog Library
        blog_subtitle = "コードは論理で書かれた詩である。",
        blog_search_hint = "記事を検索...",
        blog_sort_newest = "最新順",
        blog_sort_oldest = "古い順",
        blog_tab_all = "すべての記事"
    )
}