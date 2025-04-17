package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.UserDao
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        val btnLogin = findViewById<Button>(R.id.button)
        val usernameField = findViewById<EditText>(R.id.editTextText2)
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword)
        val goBackButton = findViewById<ImageButton>(R.id.imageButton)

        btnLogin.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            //Comment
            lifecycleScope.launch {
                val user = userDao.login(username, password)
                if (user != null) {
                    // ✅ Save user_id to SharedPreferences
                    val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    prefs.edit().putInt("USER_ID", user.user_id).apply()

                    val intent = Intent(this@Login, HomeScreen::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        goBackButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}