package com.personal.portfolio.data.remote

// Model cho Post (Blog)
data class Post(
    val id: String,
    val title: String,
    val tag: String,
    val language: String,
    val content: String,
    val images: String // Lưu JSON string của mảng ảnh
)

// Model cho Section Content
data class SectionData(
    val contentEn: String,
    val contentVi: String,
    val contentJp: String
)

// Model Hero
data class HeroData(
    var fullName: String = "",
    var nickName1: String = "",
    var nickName2: String = "",
    var avatarUrl: String = "",
    var greeting: String = "",
    var description: String = "",
    var typewriter: String = ""
)

// Model Box (Profile/Contact)
data class SectionBoxItem(var label: String = "", var value: String = "")
data class SectionBox(
    val id: String,
    var title: String = "",
    var items: MutableList<SectionBoxItem> = mutableListOf()
)

// Model Experience
data class ExpItem(
    val id: String,
    var time: String = "",
    var role: String = "",
    var details: MutableList<String> = mutableListOf()
)
data class ExpGroup(
    val id: String,
    var title: String = "",
    var items: MutableList<ExpItem> = mutableListOf()
)

// Model FAQ
data class FaqItem(var q: String = "", var a: String = "")

// Login Request/Response
data class LoginRequest(val pass: String) // Giả sử API web của bạn check pass
data class LoginResponse(val success: Boolean)