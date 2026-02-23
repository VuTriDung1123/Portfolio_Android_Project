package com.personal.portfolio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.personal.portfolio.data.remote.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.personal.portfolio.BuildConfig

// --- 1. STATE QUẢN LÝ GIAO DIỆN ---
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: String = "vi",
    val hero: HeroData = HeroData(),
    val about: String = "",
    val profile: List<SectionBox> = emptyList(),
    val career: String = "",
    val skills: String = "",
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
    val isUser: Boolean
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

    fun setLanguage(lang: String) {
        if (_uiState.value.currentLanguage != lang) {
            _uiState.value = _uiState.value.copy(currentLanguage = lang)
            loadAllData(lang)
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
                val postsDeferred = async { try { RetrofitClient.api.getPosts() } catch (e: Exception) { emptyList() } }

                suspend fun fetchRawJson(key: String): String? {
                    val res = try { RetrofitClient.api.getSectionContent(key) } catch (e: Exception) { null }
                    return if (res != null) {
                        when(lang) { "en" -> res.contentEn; "jp" -> res.contentJp; else -> res.contentVi }
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

                val heroData = if(!heroJson.isNullOrEmpty()) gson.fromJson(heroJson, HeroData::class.java) else HeroData()
                val profileList = if(!profileJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(profileJson, object : TypeToken<List<SectionBox>>() {}.type) else emptyList()
                val expList = if(!expJson.isNullOrEmpty()) gson.fromJson<List<ExpGroup>>(expJson, object : TypeToken<List<ExpGroup>>() {}.type) else emptyList()
                val contactList = if(!contactJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(contactJson, object : TypeToken<List<SectionBox>>() {}.type) else emptyList()
                val faqList = if(!faqJson.isNullOrEmpty()) gson.fromJson<List<FaqItem>>(faqJson, object : TypeToken<List<FaqItem>>() {}.type) else emptyList()
                val galleryList = if(!galleryJson.isNullOrEmpty()) try { gson.fromJson<List<String>>(galleryJson, object : TypeToken<List<String>>() {}.type) } catch(e:Exception){ emptyList() } else emptyList()

                val allPosts = postsDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hero = heroData,
                    currentLanguage = lang,
                    about = aboutJson ?: "",
                    career = careerJson ?: "",
                    skills = skillsJson ?: "",
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

    fun sendMessage(userPrompt: String) {
        viewModelScope.launch {
            val currentList = _chatHistory.value.toMutableList()
            currentList.add(ChatMessage(userPrompt, isUser = true))
            _chatHistory.value = currentList

            try {
                val response = generativeModel.generateContent(
                    content {
                        text("Bạn là Sakura AI, trợ lý ảo của Vũ Trí Dũng. Hãy trả lời thân thiện, sử dụng icon hoa anh đào 🌸.")
                        text(userPrompt)
                    }
                )
                val botResponse = response.text ?: "Sakura chưa nghĩ ra câu trả lời... 🌸"
                val updatedList = _chatHistory.value.toMutableList()
                updatedList.add(ChatMessage(botResponse, isUser = false))
                _chatHistory.value = updatedList
            } catch (e: Exception) {
                val errorList = _chatHistory.value.toMutableList()
                errorList.add(ChatMessage("Lỗi: ${e.localizedMessage} 🌸", isUser = false))
                _chatHistory.value = errorList
            }
        }
    }

    fun filterPosts(tag: String) {
        val currentPosts = _uiState.value.allPosts
        val filtered = if (tag == "ALL") currentPosts else currentPosts.filter { it.tag == tag }
        _uiState.value = _uiState.value.copy(filteredPosts = filtered, selectedTag = tag)
    }
}