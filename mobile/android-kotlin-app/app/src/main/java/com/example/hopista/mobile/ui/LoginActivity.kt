package com.example.hopista.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hopista.mobile.R
import com.example.hopista.mobile.data.AuthRepository
import com.example.hopista.mobile.ui.DashboardActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {
    private lateinit var repo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        repo = AuthRepository(this)

        val usernameOrEmail = findViewById<EditText>(R.id.usernameOrEmail)
        val password = findViewById<EditText>(R.id.password)
        val submit = findViewById<Button>(R.id.loginBtn)
        val toRegister = findViewById<Button>(R.id.toRegisterBtn)

        submit.setOnClickListener {
            val u = usernameOrEmail.text.toString()
            val p = password.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                val result = repo.login(u, p)
                if (result.isSuccess) {
                    Toast.makeText(this@LoginActivity, "Signed in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
