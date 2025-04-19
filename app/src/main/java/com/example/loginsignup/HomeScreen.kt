package com.example.loginsignup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class HomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // Retrieve username from SharedPreferences, fallback to null if not found
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = prefs.getString("username", null)

        // Check if the username is null and set a default message
        val welcomeText = findViewById<TextView>(R.id.textView21)
        if (username != null) {
            welcomeText.text = "Welcome, $username"
        }

        // Store username in SharedPreferences if not done already
        if (username == null && intent.hasExtra("username")) {
            val editor = prefs.edit()
            val newUsername = intent.getStringExtra("username") ?: "Guest"
            editor.putString("username", newUsername)
            editor.apply()
        }

        // Get budget from shared preferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val budget = sharedPref.getFloat("user_budget", 0.0f)
        val formattedAmount = "R%.2f".format(budget)  // Improved formatting
        val balanceTextView: TextView = findViewById(R.id.textView25)
        balanceTextView.text = formattedAmount

        // Set up button listeners
        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            // Home screen action is redundant since we're already here
        }
        findViewById<ImageButton>(R.id.imageButton21).setOnClickListener {
            startActivity(Intent(this, SetupBudget::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton22).setOnClickListener {
            startActivity(Intent(this, BudgetGoalSetup::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            startActivity(Intent(this, Transactions::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton6).setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton7).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the budget on resume
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val budget = sharedPref.getFloat("user_budget", 0.0f)
        findViewById<TextView>(R.id.textView25).text = "R%.2f".format(budget)
    }
}
