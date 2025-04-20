package com.example.loginsignup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoal
import com.example.loginsignup.data.BudgetGoalDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetupBudget : AppCompatActivity() {
    private lateinit var budgetDao: BudgetGoalDao
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_budget)

        db = AppDatabase.getDatabase(applicationContext)
        budgetDao = db.budgetGoalDao()

        // UI Components
        val btnBack = findViewById<ImageButton>(R.id.imageButton25)
        val btnSave = findViewById<Button>(R.id.btnSaveBudget)
        val budgetEditText = findViewById<EditText>(R.id.editTextNumber2)
        val spinner = findViewById<Spinner>(R.id.spinner_month)
        val minGoalEditText = findViewById<EditText>(R.id.editTextMinGoal)
        val maxGoalEditText = findViewById<EditText>(R.id.editTextMaxGoal)

        // Back Navigation
        btnBack.setOnClickListener {
            Log.d("SetupBudget", "Navigating back to HomeScreen")
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        // Save Button Click
        btnSave.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("USER_ID", -1)

            if (userId == -1) {
                Log.e("SetupBudget", "User ID not found in SharedPreferences")
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMonth = spinner.selectedItem.toString().trim()
            val budgetStr = budgetEditText.text.toString().trim()
            val minStr = minGoalEditText.text.toString().trim()
            val maxStr = maxGoalEditText.text.toString().trim()

            Log.d("SetupBudget", "Inputs - month: $selectedMonth, budget: $budgetStr, min: $minStr, max: $maxStr")

            val budget = budgetStr.toFloatOrNull()
            val minGoal = minStr.toDoubleOrNull()
            val maxGoal = maxStr.toDoubleOrNull()

            if (budget == null || budget <= 0f) {
                Log.e("SetupBudget", "Invalid budget input: $budgetStr")
                Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal == null || maxGoal == null || selectedMonth.isEmpty()) {
                Log.e("SetupBudget", "One or more fields are empty or invalid")
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("SetupBudget", "Saving budget -> userId=$userId, month=$selectedMonth, budget=$budget, min=$minGoal, max=$maxGoal")

            with(sharedPref.edit()) {
                putFloat("user_budget", budget)
                apply()
                Log.d("SetupBudget", "Saved budget $budget to SharedPreferences")
            }

            val goal = BudgetGoal(
                user_id = userId,
                month = selectedMonth,
                budgetAmount = budget.toDouble(),
                minGoal = minGoal,
                maxGoal = maxGoal,
                //remainingBudget = budget.toDouble()
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("SetupBudget", "Attempting to insert goal into RoomDB")
                    budgetDao.insertGoal(goal)
                    Log.d("SetupBudget", "BudgetGoal inserted successfully into RoomDB")

                    runOnUiThread {
                        Toast.makeText(this@SetupBudget, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SetupBudget", "Failed to insert goal: ${e.message}", e)
                    runOnUiThread {
                        Toast.makeText(this@SetupBudget, "Error saving budget. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val budgetEditText = findViewById<EditText>(R.id.editTextNumber2)
        val budget = budgetEditText.text.toString().toFloatOrNull()

        if (budget != null) {
            with(sharedPref.edit()) {
                putFloat("user_budget", budget)
                apply()
            }
            Log.d("SetupBudget", "onPause: Saved budget $budget to SharedPreferences")
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedBudget = sharedPref.getFloat("user_budget", 0f)
        val budgetEditText = findViewById<EditText>(R.id.editTextNumber2)

        if (savedBudget > 0f) {
            budgetEditText.setText(savedBudget.toString())
            Log.d("SetupBudget", "onResume: Loaded saved budget $savedBudget into EditText")
        } else {
            Log.d("SetupBudget", "onResume: No saved budget found in SharedPreferences")
        }
    }
}
