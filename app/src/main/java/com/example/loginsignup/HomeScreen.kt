package com.example.loginsignup

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

class HomeScreen : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // Retrieve username from intent
        val username = intent.getStringExtra("username")
        val welcomeText = findViewById<TextView>(R.id.textView21)
        welcomeText.text = "Welcome, $username"

        // Find button by ID
        val btnHome = findViewById<ImageButton>(R.id.imageButton3)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        val btnSetupBudget = findViewById<ImageButton>(R.id.imageButton21)
        btnSetupBudget.setOnClickListener {
            val intent = Intent(this, SetupBudget::class.java)
            startActivity(intent)
        }

        val btnBudgetGoalSetup = findViewById<ImageButton>(R.id.imageButton22)
        btnBudgetGoalSetup.setOnClickListener {
            val intent = Intent(this, BudgetGoalSetup::class.java)
            startActivity(intent)
        }
        val btnTransact = findViewById<ImageButton>(R.id.imageButton4)
        btnTransact.setOnClickListener {
            val intent = Intent(this, Transactions::class.java)
            startActivity(intent)
        }
        val btnFAB = findViewById<ImageButton>(R.id.imageButton5)
        btnFAB.setOnClickListener {
            val intent = Intent(this, AddExpense::class.java)
            startActivity(intent)
        }
        val btnChart = findViewById<ImageButton>(R.id.imageButton6)
        btnChart.setOnClickListener {
            val intent = Intent(this, Chart::class.java)
            startActivity(intent)
        }
        val btnMore = findViewById<ImageButton>(R.id.imageButton7)
        btnMore.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}