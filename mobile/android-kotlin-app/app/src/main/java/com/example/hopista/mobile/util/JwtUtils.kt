package com.example.hopista.mobile.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    data class Claims(val sub: String?, val iat: Long?, val exp: Long?)

    fun decodeClaims(token: String?): Claims? {
        if (token.isNullOrBlank()) return null
        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val obj = JSONObject(payload)
            Claims(
                sub = obj.optString("sub", null),
                iat = obj.optLong("iat", 0L).takeIf { it != 0L },
                exp = obj.optLong("exp", 0L).takeIf { it != 0L }
            )
        } catch (e: Exception) {
            null
        }
    }
}
