package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Find button by ID
        val btnLogin = findViewById<Button>(R.id.button3)
        val btnGetStarted = findViewById<Button>(R.id.button4)

        // Set click listener to navigate to LoginActivity
        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            Toast.makeText(this@MainActivity , "Opening Login page", Toast.LENGTH_LONG).show()
        }

        btnGetStarted.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            Toast.makeText(this@MainActivity , "Opening Signup page", Toast.LENGTH_LONG).show()
        }
    }
}

