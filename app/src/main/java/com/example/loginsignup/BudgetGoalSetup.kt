package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetGoalSetup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_goal_setup)

        // Get references to the UI elements
        val btnBack = findViewById<ImageButton>(R.id.imageButton18)
        val btnSubmit = findViewById<Button>(R.id.button7)
        val btnReset = findViewById<Button>(R.id.button8)

        val minGoalEditText = findViewById<EditText>(R.id.editTextNumberDecimal2)
        val maxGoalEditText = findViewById<EditText>(R.id.editTextNumber)

        // Handle Back Button click
        btnBack.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        // Handle Submit Button click
        btnSubmit.setOnClickListener {
            // Capture the user input for min and max goals
            val minGoalValue = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoalValue = maxGoalEditText.text.toString().toDoubleOrNull()

            // Check if the input is valid
            if (minGoalValue == null || maxGoalValue == null) {
                // Show error message if input is invalid
                Toast.makeText(this, "Please enter valid minimum and maximum goals", Toast.LENGTH_SHORT).show()
            } else {
                // Save the valid budget goals
                saveBudgetGoals(minGoalValue, maxGoalValue)
                Toast.makeText(this, "Budget goals updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Reset Button click
        btnReset.setOnClickListener {
            // Clear the EditText fields
            minGoalEditText.setText("")
            maxGoalEditText.setText("")
        }
    }

    // Function to save the budget goals (this could be saving to shared preferences or a database)
    private fun saveBudgetGoals(minGoal: Double, maxGoal: Double) {
        // For simplicity, let's assume you are saving these to shared preferences (you can adjust it as per your requirements)
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putFloat("minGoal", minGoal.toFloat())
            putFloat("maxGoal", maxGoal.toFloat())
            apply()
        }
    }
}
