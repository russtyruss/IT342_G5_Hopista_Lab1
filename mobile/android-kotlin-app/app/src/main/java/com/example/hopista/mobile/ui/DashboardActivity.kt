package com.example.hopista.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hopista.mobile.R
import com.example.hopista.mobile.data.AuthRepository
import com.example.hopista.mobile.util.JwtUtils

class DashboardActivity: AppCompatActivity() {
    private lateinit var repo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        repo = AuthRepository(this)

        val welcome = findViewById<TextView>(R.id.welcomeText)
        val toProfile = findViewById<Button>(R.id.toProfileBtn)
        val toLogin = findViewById<Button>(R.id.toLoginBtn)

        val claims = JwtUtils.decodeClaims(repo.getAccessToken())
        welcome.text = if (claims?.sub != null) {
            "Welcome, ${claims.sub}!"
        } else {
            "Welcome!"
        }

        toProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        toLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
