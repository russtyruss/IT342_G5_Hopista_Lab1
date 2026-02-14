package com.example.hopista.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hopista.mobile.R
import com.example.hopista.mobile.data.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var repo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        repo = AuthRepository(this)

        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirm = findViewById<EditText>(R.id.confirmPassword)
        val submit = findViewById<Button>(R.id.registerBtn)

        submit.setOnClickListener {
            val u = username.text.toString()
            val e = email.text.toString()
            val p = password.text.toString()
            val c = confirm.text.toString()
            if (p != c) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {
                val result = repo.register(u, e, p)
                if (result.isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Account created!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
