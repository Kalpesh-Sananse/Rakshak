package com.kalpesh.women_safety

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("process-audio")
    fun uploadAudio(@Part audio: MultipartBody.Part): Call<ApiResponse>
}

data class ApiResponse(
    val sos_detected: Boolean,
    val message: String
) {
    val transcript: String
        get() {
            TODO()
        }
}


object ApiClient {
    private const val BASE_URL = "https://voice-llm.onrender.com/"


    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
