package com.personal.portfolio.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.personal.portfolio.BuildConfig
import com.personal.portfolio.data.remote.ExpGroup
import com.personal.portfolio.data.remote.FaqItem
import com.personal.portfolio.data.remote.HeroData
import com.personal.portfolio.data.remote.Post
import com.personal.portfolio.data.remote.RetrofitClient
import com.personal.portfolio.data.remote.SectionBox
import com.personal.portfolio.data.remote.SectionData
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- 1. STATE QUẢN LÝ GIAO DIỆN ---
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: String = "vi",
    val hero: HeroData = HeroData(),
    val about: String = "",
    val profile: List<SectionBox> = emptyList(),
    val career: String = "",
    val skills: List<SectionBox> = emptyList(),
    val experience: List<ExpGroup> = emptyList(),
    val contact: List<SectionBox> = emptyList(),
    val faq: List<FaqItem> = emptyList(),
    val certificates: String = "",
    val achievements: String = "",
    val gallery: List<String> = emptyList(),
    val allPosts: List<Post> = emptyList(),
    val filteredPosts: List<Post> = emptyList(),
    val selectedTag: String = "ALL"
)

// --- 2. MODEL CHAT (Chỉ khai báo 1 lần duy nhất) ---
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val gson = Gson()

    // Khởi tạo Gemini AI dùng Key từ BuildConfig
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-3-flash-preview",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _chatHistory = MutableStateFlow(listOf<ChatMessage>())
    val chatHistory = _chatHistory.asStateFlow()

    // TRONG FILE: HomeViewModel.kt
    // TRONG: HomeViewModel.kt
    fun setLanguage(lang: String) {
        if (_uiState.value.currentLanguage != lang) {
            _uiState.value = _uiState.value.copy(currentLanguage = lang)
            loadAllData(lang, forceRefresh = true)
        }
    }

    // --- CƠ CHẾ CACHE-FIRST ĐỂ KHÔNG LOAD LẠI ---
    fun loadAllData(lang: String = "vi", forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Kiểm tra: Nếu đã có data và cùng ngôn ngữ thì thoát luôn
            if (!forceRefresh && _uiState.value.hero.fullName.isNotEmpty() && _uiState.value.currentLanguage == lang) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            // Chỉ hiện loading khi dữ liệu thực sự trống hoặc ép buộc làm mới
            val isFirstLoad = _uiState.value.hero.fullName.isEmpty()
            if (isFirstLoad || forceRefresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val postsDeferred = async {
                    try {
                        RetrofitClient.api.getPosts()
                    } catch (_: Exception) {
                        emptyList()
                    }
                }

                suspend fun fetchRawJson(key: String): String? {
                    val res = try {
                        RetrofitClient.api.getSectionContent(key)
                    } catch (_: Exception) {
                        null
                    }
                    return if (res != null) {
                        when (lang) {
                            "en" -> res.contentEn; "jp" -> res.contentJp; else -> res.contentVi
                        }
                    } else null
                }

                val heroJson = fetchRawJson("hero")
                val aboutJson = fetchRawJson("about")
                val careerJson = fetchRawJson("career")
                val skillsJson = fetchRawJson("skills")
                val profileJson = fetchRawJson("profile")
                val expJson = fetchRawJson("experience")
                val contactJson = fetchRawJson("contact")
                val faqJson = fetchRawJson("faq_data")
                val certJson = fetchRawJson("certificates")
                val achiJson = fetchRawJson("achievements")
                val galleryJson = fetchRawJson("gallery")

                val skillsList = if (!skillsJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(
                    skillsJson,
                    object : TypeToken<List<SectionBox>>() {}.type
                ) else emptyList()
                val heroData = if (!heroJson.isNullOrEmpty()) gson.fromJson(
                    heroJson,
                    HeroData::class.java
                ) else HeroData()
                val profileList = if (!profileJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(
                    profileJson,
                    object : TypeToken<List<SectionBox>>() {}.type
                ) else emptyList()
                val expList = if (!expJson.isNullOrEmpty()) gson.fromJson<List<ExpGroup>>(
                    expJson,
                    object : TypeToken<List<ExpGroup>>() {}.type
                ) else emptyList()
                val contactList = if (!contactJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(
                    contactJson,
                    object : TypeToken<List<SectionBox>>() {}.type
                ) else emptyList()
                val faqList = if (!faqJson.isNullOrEmpty()) gson.fromJson<List<FaqItem>>(
                    faqJson,
                    object : TypeToken<List<FaqItem>>() {}.type
                ) else emptyList()
                val galleryList = if (!galleryJson.isNullOrEmpty()) try {
                    gson.fromJson<List<String>>(
                        galleryJson,
                        object : TypeToken<List<String>>() {}.type
                    )
                } catch (_: Exception) {
                    emptyList()
                } else emptyList()

                val allPosts = postsDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hero = heroData,
                    currentLanguage = lang,
                    about = aboutJson ?: "",
                    career = careerJson ?: "",
                    skills = skillsList,
                    profile = profileList,
                    experience = expList,
                    contact = contactList,
                    faq = faqList,
                    certificates = certJson ?: "",
                    achievements = achiJson ?: "",
                    gallery = galleryList,
                    allPosts = allPosts,
                    filteredPosts = allPosts
                )
                filterPosts(_uiState.value.selectedTag)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    // Trong HomeViewModel.kt
    fun sendMessage(userPrompt: String) {
        viewModelScope.launch {
            val state = _uiState.value

            // Danh sách dự án dự phòng
            val projectData = state.allPosts.joinToString("\n") { "- ${it.title} (ID: ${it.id})" }

            val systemPrompt = """
            Bạn là Sakura AI 🌸.
            DỮ LIỆU CỦA DŨNG:
            - Giới thiệu: ${state.about}
            - Kỹ năng: ${state.skills}
            - Danh sách bài viết/dự án: 
            $projectData

            QUY TẮC TRẢ LỜI:
            1. Trả lời trực tiếp, thân thiện, dùng icon 🌸 và Markdown (**in đậm** cho các tiêu đề).
            2. CHỈ KÈM LINK (định dạng: post_detail/ID) khi người dùng hỏi đích danh về một bài blog hoặc dự án cụ thể. 
            3. Với các câu hỏi chung chung về thông tin cá nhân, KHÔNG liệt kê link.
        """.trimIndent()

            // Thêm tin nhắn an toàn với update để tránh văng app
            _chatHistory.update { it + ChatMessage(userPrompt, isUser = true) }
            _chatHistory.update { it + ChatMessage("", isUser = false, isTyping = true) }

            try {
                val response = generativeModel.generateContent(
                    content {
                        text(systemPrompt)
                        text(userPrompt)
                    }
                )
                val botResponse = response.text ?: "Sakura chưa có câu trả lời 🌸"

                _chatHistory.update { history ->
                    history.filter { !it.isTyping } + ChatMessage(botResponse, isUser = false)
                }
            } catch (_: Exception) {
                _chatHistory.update { history ->
                    history.filter { !it.isTyping } + ChatMessage(
                        "Lỗi kết nối AI rồi... 🌸",
                        isUser = false
                    )
                }
            }
        }
    }

    fun filterPosts(tag: String) {
        val currentPosts = _uiState.value.allPosts
        val filtered = if (tag == "ALL") currentPosts else currentPosts.filter { it.tag == tag }
        _uiState.value = _uiState.value.copy(filteredPosts = filtered, selectedTag = tag)
    }


    // Biến lưu trạng thái thông báo ở trang Admin
    var adminMessage by mutableStateOf("")

    // Hàm 1: Lấy dữ liệu GỐC (gồm cả Anh, Việt, Nhật) từ Database để đưa vào form Edit
    suspend fun getRawSectionForAdmin(key: String): SectionData? {
        return try {
            RetrofitClient.api.getSectionContent(key)
        } catch (_: Exception) {
            null
        }
    }

    // --- QUẢN LÝ BLOG ---
    fun savePost(post: Post, isEdit: Boolean) {
        viewModelScope.launch {
            adminMessage = "Đang lưu bài viết... ⏳"
            try {
                val response = if (isEdit) RetrofitClient.api.updatePost(post)
                else RetrofitClient.api.createPost(post)
                if (response.isSuccessful) {
                    adminMessage = "Lưu bài viết thành công! 🌸"
                    loadAllData(_uiState.value.currentLanguage, forceRefresh = true) // Tải lại list
                } else adminMessage = "Lỗi lưu bài viết 🍃"
            } catch (e: Exception) {
                adminMessage = "Lỗi mạng: ${e.message}"
            }
        }
    }

    fun deletePost(id: String) {
        viewModelScope.launch {
            adminMessage = "Đang xóa... ⏳"
            try {
                val response = RetrofitClient.api.deletePost(id)
                if (response.isSuccessful) {
                    adminMessage = "Đã xóa! 🌸"
                    loadAllData(_uiState.value.currentLanguage, forceRefresh = true)
                }
            } catch (e: Exception) {
                adminMessage = "Lỗi xóa: ${e.message}"
            }
        }
    }

    // --- QUẢN LÝ SECTIONS (CHO TẤT CẢ BOX, FAQ, TEXT) ---
    fun saveSectionJson(key: String, enJson: String, viJson: String, jpJson: String) {
        viewModelScope.launch {
            adminMessage = "Đang đồng bộ lên Web... ⏳"
            try {
                val requestData = mapOf(
                    "sectionKey" to key,
                    "contentEn" to enJson,
                    "contentVi" to viJson,
                    "contentJp" to jpJson
                )
                RetrofitClient.api.saveSectionContent(requestData)
                adminMessage = "Lưu mục $key thành công! 🌸"
                loadAllData(_uiState.value.currentLanguage, forceRefresh = true)
            } catch (e: Exception) {
                adminMessage = "Lỗi: ${e.message}"
            }
        }
    }
}