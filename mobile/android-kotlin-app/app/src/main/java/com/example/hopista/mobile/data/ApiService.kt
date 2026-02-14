package com.example.hopista.mobile.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// NOTE: For Android emulator, use 10.0.2.2 to reach host localhost
private const val BASE_URL = "http://10.0.2.2:8081/api/"

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): UserDto

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body body: RefreshTokenRequest): retrofit2.Response<String>

    companion object {
        fun create(tokenProvider: () -> String?): ApiService {
            val authInterceptor = object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    val token = tokenProvider()
                    val newReq = if (token != null && token.isNotBlank()) {
                        request.newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    } else request
                    return chain.proceed(newReq)
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

// DTOs aligned with backend
data class LoginRequest(val usernameOrEmail: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class RefreshTokenRequest(val refreshToken: String)

data class AuthResponse(val accessToken: String, val refreshToken: String)

data class UserDto(val id: Long?, val username: String?, val email: String?)
