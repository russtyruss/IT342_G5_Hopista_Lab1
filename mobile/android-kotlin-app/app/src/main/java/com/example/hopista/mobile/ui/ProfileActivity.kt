package com.example.hopista.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hopista.mobile.R
import com.example.hopista.mobile.data.AuthRepository
import com.example.hopista.mobile.util.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity: AppCompatActivity() {
    private lateinit var repo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        repo = AuthRepository(this)

        val usernameView = findViewById<TextView>(R.id.usernameText)
        val iatView = findViewById<TextView>(R.id.iatText)
        val expView = findViewById<TextView>(R.id.expText)
        val refreshBtn = findViewById<Button>(R.id.refreshBtn)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        fun reload() {
            val claims = JwtUtils.decodeClaims(repo.getAccessToken())
            usernameView.text = "Username: ${claims?.sub ?: "-"}"
            iatView.text = "Issued At: ${claims?.iat ?: "-"}"
            expView.text = "Expires At: ${claims?.exp ?: "-"}"
        }

        reload()

        refreshBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = repo.refresh()
                if (result.isSuccess) {
                    Toast.makeText(this@ProfileActivity, "Token refreshed", Toast.LENGTH_SHORT).show()
                    reload()
                } else {
                    Toast.makeText(this@ProfileActivity, "Refresh failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                repo.logout()
                Toast.makeText(this@ProfileActivity, "Signed out", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
