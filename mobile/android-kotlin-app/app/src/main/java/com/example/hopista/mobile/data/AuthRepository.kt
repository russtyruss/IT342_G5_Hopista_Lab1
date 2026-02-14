package com.example.hopista.mobile.data

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val api = ApiService.create { getAccessToken() }

    fun getAccessToken(): String? = prefs.getString("accessToken", null)
    fun getRefreshToken(): String? = prefs.getString("refreshToken", null)

    private fun setTokens(access: String?, refresh: String?) {
        prefs.edit().apply {
            if (access != null) putString("accessToken", access)
            if (refresh != null) putString("refreshToken", refresh)
        }.apply()
    }

    suspend fun login(usernameOrEmail: String, password: String): Result<Unit> = runCatching {
        val resp = api.login(LoginRequest(usernameOrEmail, password))
        setTokens(resp.accessToken, resp.refreshToken)
    }

    suspend fun register(username: String, email: String, password: String): Result<Unit> = runCatching {
        api.register(RegisterRequest(username, email, password))
    }

    suspend fun refresh(): Result<Unit> = runCatching {
        val rt = getRefreshToken() ?: throw IllegalStateException("No refresh token")
        val resp = api.refresh(RefreshTokenRequest(rt))
        setTokens(resp.accessToken, null)
    }

    suspend fun logout(): Result<Unit> = runCatching {
        val rt = getRefreshToken()
        if (rt != null) {
            api.logout(RefreshTokenRequest(rt))
        }
        setTokens(null, null)
        prefs.edit().clear().apply()
    }
}
