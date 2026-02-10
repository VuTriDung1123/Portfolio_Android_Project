package com.personal.portfolio.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// 1. Định nghĩa các hàm gọi lên Web
interface SakuraApiService {
    // --- AUTH ---
    @POST("api/admin/login") // Đường dẫn API login của bạn
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // --- POSTS ---
    @GET("api/posts")
    suspend fun getAllPosts(): List<Post>

    // --- SECTIONS ---
    @GET("api/sections")
    suspend fun getSectionContent(@Query("key") key: String): SectionData?

    @POST("api/sections")
    suspend fun saveSectionContent(@Body data: Map<String, Any>): LoginResponse // Tái sử dụng LoginResponse vì nó trả về success
}

// 2. Tạo Instance kết nối
object RetrofitClient {
    // [QUAN TRỌNG] Thay đổi URL này thành URL website thật của bạn (VD: https://vutridung.vercel.app/)
    // Nếu chạy localhost thì dùng IP máy tính: http://10.0.2.2:3000/
    private const val BASE_URL = "https://personal-portfolio-vu-tri-dung-saku.vercel.app/R"

    val api: SakuraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SakuraApiService::class.java)
    }
}