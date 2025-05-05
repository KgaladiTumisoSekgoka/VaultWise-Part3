package com.example.loginsignup

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginsignup.data.AppDatabase

class Profile : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        db = AppDatabase.getDatabase(applicationContext)

        // Find the back button
        val backButton = findViewById<ImageButton>(R.id.imageButton28)

        // Set the click listener for the back button
        backButton.setOnClickListener {
            // Call onBackPressed to navigate back
            onBackPressed()
        }
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)
        val profileTextView = findViewById<TextView>(R.id.profileName)

        if (userId != -1) {
            Thread {
                val user = db.userDao().getUserById(userId)
                runOnUiThread {
                    if (user != null) {
                        profileTextView.text = "Username: ${user.username}\nPassword: ${user.password}"
                    } else {
                        profileTextView.text = "User not found"
                    }
                }
            }.start()
        } else {
            profileTextView.text = "No user ID found in preferences"
        }
    }
}
