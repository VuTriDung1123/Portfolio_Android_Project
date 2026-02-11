package com.personal.portfolio.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SakuraApiService {
    // 1. Lấy nội dung (Dùng cho cả Home và Admin)
    @GET("api/sections")
    suspend fun getSectionContent(@Query("key") key: String): SectionData?

    // 2. Lưu nội dung (Chỉ dùng cho Admin)
    @POST("api/sections")
    suspend fun saveSectionContent(@Body data: Map<String, Any>): LoginResponse

    // 3. Lấy danh sách bài viết/dự án
    @GET("api/posts")
    suspend fun getPosts(): List<Post>
}

object RetrofitClient {
    // [QUAN TRỌNG] ĐÂY LÀ CHÌA KHÓA KẾT NỐI
    // Chúng ta trỏ thẳng vào website của bạn trên Vercel
    private const val BASE_URL = "https://personal-portfolio-vu-tri-dung-saku.vercel.app/"

    val api: SakuraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SakuraApiService::class.java)
    }
}