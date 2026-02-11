package com.personal.portfolio.data.remote

// --- 1. MODEL CHO POST ---
data class Post(
    val id: String,
    var title: String, // var để sửa được
    var tag: String,
    var language: String,
    var content: String,
    var images: String,
    val createdAt: String
)

// --- 2. MODEL API RESPONSE ---
data class SectionData(
    val contentEn: String?,
    val contentVi: String?,
    val contentJp: String?
)

// --- 3. MODEL DATA (EDITABLE) ---

// Hero Section
data class HeroData(
    var fullName: String = "",
    var nickName1: String = "",
    var nickName2: String = "",
    var avatarUrl: String = "",
    var greeting: String = "",
    var description: String = "",
    var typewriter: String = "[]"
)

// Profile / Contact Box
data class SectionBoxItem(
    var label: String = "",
    var value: String = ""
)

data class SectionBox(
    val id: String = "",
    var title: String = "",
    var items: MutableList<SectionBoxItem> = mutableListOf() // MutableList để dùng .add, .removeAt
)

// Experience
data class ExpItem(
    val id: String = "",
    var time: String = "",
    var role: String = "",
    var details: MutableList<String> = mutableListOf()
)

data class ExpGroup(
    val id: String = "",
    var title: String = "",
    var items: MutableList<ExpItem> = mutableListOf()
)

// FAQ
data class FaqItem(
    var q: String = "",
    var a: String = ""
)

// Config
data class GlobalConfig(
    var resumeUrl: String = "",
    var isOpenForWork: Boolean = true
)

// Login
data class LoginRequest(val pass: String)
data class LoginResponse(val success: Boolean)