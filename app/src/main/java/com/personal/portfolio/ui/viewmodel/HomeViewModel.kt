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

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Data Static
    val hero: HeroData = HeroData(),
    val about: String = "",
    val profile: List<SectionBox> = emptyList(),
    val career: String = "",
    val skills: String = "",
    val experience: List<ExpGroup> = emptyList(),
    val contact: List<SectionBox> = emptyList(),
    val faq: List<FaqItem> = emptyList(),

    // [MỚI] Thêm data cho các section còn thiếu
    val certificates: String = "", // Có thể là text hoặc list tuỳ bạn lưu DB
    val achievements: String = "",
    val gallery: List<String> = emptyList(), // List URL ảnh

    // Data Dynamic (Posts)
    val allPosts: List<Post> = emptyList(),
    val filteredPosts: List<Post> = emptyList(),
    val selectedTag: String = "ALL"
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val gson = Gson()

    fun loadAllData(lang: String = "vi") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // 1. Gọi API lấy Posts
                val postsDeferred = async { try { RetrofitClient.api.getPosts() } catch (e: Exception) { emptyList() } }

                // 2. Helper fetch
                suspend fun fetchRawJson(key: String): String? {
                    val res = try { RetrofitClient.api.getSectionContent(key) } catch (e: Exception) { null }
                    return if (res != null) {
                        when(lang) { "en" -> res.contentEn; "jp" -> res.contentJp; else -> res.contentVi }
                    } else null
                }

                // 3. Fetch song song
                val heroJson = fetchRawJson("hero")
                val aboutJson = fetchRawJson("about")
                val careerJson = fetchRawJson("career")
                val skillsJson = fetchRawJson("skills")
                val profileJson = fetchRawJson("profile")
                val expJson = fetchRawJson("experience")
                val contactJson = fetchRawJson("contact")
                val faqJson = fetchRawJson("faq_data")
                // [MỚI] Fetch thêm
                val certJson = fetchRawJson("certificates")
                val achiJson = fetchRawJson("achievements")
                val galleryJson = fetchRawJson("gallery")

                // 4. Parse Data
                val heroData = if(!heroJson.isNullOrEmpty()) gson.fromJson(heroJson, HeroData::class.java) else HeroData()
                val profileList = if(!profileJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(profileJson, object : TypeToken<List<SectionBox>>() {}.type) else emptyList()
                val expList = if(!expJson.isNullOrEmpty()) gson.fromJson<List<ExpGroup>>(expJson, object : TypeToken<List<ExpGroup>>() {}.type) else emptyList()
                val contactList = if(!contactJson.isNullOrEmpty()) gson.fromJson<List<SectionBox>>(contactJson, object : TypeToken<List<SectionBox>>() {}.type) else emptyList()
                val faqList = if(!faqJson.isNullOrEmpty()) gson.fromJson<List<FaqItem>>(faqJson, object : TypeToken<List<FaqItem>>() {}.type) else emptyList()

                // [MỚI] Parse Gallery (Giả sử lưu dạng List<String> url)
                val galleryList = if(!galleryJson.isNullOrEmpty()) try { gson.fromJson<List<String>>(galleryJson, object : TypeToken<List<String>>() {}.type) } catch(e:Exception){ emptyList() } else emptyList()

                val allPosts = postsDeferred.await()

                _uiState.value = HomeUiState(
                    isLoading = false,
                    hero = heroData,
                    about = aboutJson ?: "",
                    career = careerJson ?: "",
                    skills = skillsJson ?: "",
                    profile = profileList,
                    experience = expList,
                    contact = contactList,
                    faq = faqList,
                    // [MỚI]
                    certificates = certJson ?: "",
                    achievements = achiJson ?: "",
                    gallery = galleryList,
                    allPosts = allPosts,
                    filteredPosts = allPosts
                )
                filterPosts("ALL")

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    // ... Giữ nguyên các hàm filter ...
    fun filterPosts(tag: String) {
        val currentPosts = _uiState.value.allPosts
        val filtered = if (tag == "ALL") currentPosts else currentPosts.filter { it.tag == tag }
        _uiState.value = _uiState.value.copy(filteredPosts = filtered, selectedTag = tag)
    }
    fun getPostById(postId: String): Post? = _uiState.value.allPosts.find { it.id == postId }
}