package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        // Find button by ID
        val btnSignup = findViewById<Button>(R.id.button2)
        val goBackButton = findViewById<ImageButton>(R.id.imageButton2)
        // Set click listener to navigate to LoginActivity & Back
        btnSignup.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            val editTextUsername = findViewById<EditText>(R.id.editTextText)
            val editTextPassword = findViewById<EditText>(R.id.editTextTextPassword2)
            val editConfirmPassword = findViewById<EditText>(R.id.editTextTextPassword3)
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editConfirmPassword.text.toString()

            if(username.isEmpty())
            {
                editTextUsername.error="Username is not empty"
                return@setOnClickListener
            }
            if(password.isEmpty())
            {
                editTextPassword.error="Password cannot be empty!"
            }
            if(confirmPassword.isEmpty())
            {
                editConfirmPassword.error= "Confirm password cannot be empty!"
            }
        }
        goBackButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}