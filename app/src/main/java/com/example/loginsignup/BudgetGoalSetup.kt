package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
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
        val budgetAmountEditText = findViewById<EditText>(R.id.editTextBudgetAmount)
        val monthSpinner = findViewById<Spinner>(R.id.spinner_month)

        // Set spinner values (you can customize this)
        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)

        btnSubmit.setOnClickListener {
            val minGoalValue = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoalValue = maxGoalEditText.text.toString().toDoubleOrNull()
            val budgetAmount = budgetAmountEditText.text.toString().toFloatOrNull()
            val selectedMonth = monthSpinner.selectedItem.toString()

            if (minGoalValue == null || maxGoalValue == null || budgetAmount == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putFloat("minGoal", minGoalValue.toFloat())
                    putFloat("maxGoal", maxGoalValue.toFloat())
                    putFloat("user_budget", budgetAmount)
                    putString("budget_month", selectedMonth)
                    apply()
                }
                Toast.makeText(this, "Budget details updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear everything on reset
        btnReset.setOnClickListener {
            minGoalEditText.text.clear()
            maxGoalEditText.text.clear()
            budgetAmountEditText.text.clear()
            monthSpinner.setSelection(0)
        }
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        minGoalEditText.setText(sharedPref.getFloat("minGoal", 0f).toString())
        maxGoalEditText.setText(sharedPref.getFloat("maxGoal", 0f).toString())
        budgetAmountEditText.setText(sharedPref.getFloat("user_budget", 0f).toString())

        val savedMonth = sharedPref.getString("budget_month", "January")
        val index = months.indexOf(savedMonth)
        if (index >= 0) {
            monthSpinner.setSelection(index)
        }

    }

    // Function to save the budget goals (this could be saving to shared preferences or a database)
    private fun saveBudgetGoals(minGoal: Double, maxGoal: Double) {

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putFloat("minGoal", minGoal.toFloat())
            putFloat("maxGoal", maxGoal.toFloat())
            apply()
        }
    }
}
