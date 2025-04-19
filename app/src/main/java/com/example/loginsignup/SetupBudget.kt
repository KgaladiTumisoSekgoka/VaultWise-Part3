package com.example.loginsignup

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
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

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()
        budgetDao = db.budgetGoalDao()

        val btnSave = findViewById<Button>(R.id.btnSaveBudget)
        val budgetAmount = findViewById<EditText>(R.id.editTextNumber2)
        val spinner = findViewById<Spinner>(R.id.spinner_month)
        val minGoal = findViewById<EditText>(R.id.editTextMinGoal)
        val maxGoal = findViewById<EditText>(R.id.editTextMaxGoal)

        btnSave.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("user_id", -1)

            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMonth = spinner.selectedItem.toString()
            val budget = budgetAmount.text.toString().toFloatOrNull()
            val min = minGoal.text.toString().toDoubleOrNull()
            val max = maxGoal.text.toString().toDoubleOrNull()

            if (budget == null) {
                Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (min == null || max == null || selectedMonth.isEmpty()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save budget to SharedPreferences
            with(sharedPref.edit()) {
                putFloat("user_budget", budget)
                apply()
            }

            // Create and save BudgetGoal to database
            val goal = BudgetGoal(
                user_id = userId,
                month = selectedMonth,
                budgetAmount = budget.toDouble(),
                minGoal = min,
                maxGoal = max,
                remainingBudget = budget.toDouble()
            )

            CoroutineScope(Dispatchers.IO).launch {
                budgetDao.insertGoal(goal)
                runOnUiThread {
                    Toast.makeText(this@SetupBudget, "Budget saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val budgetAmount = findViewById<EditText>(R.id.editTextNumber2)
        val budget = budgetAmount.text.toString().toFloatOrNull()

        // Save budget amount to SharedPreferences when the activity is paused
        if (budget != null) {
            with(sharedPref.edit()) {
                putFloat("user_budget", budget)
                apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedBudget = sharedPref.getFloat("user_budget", 0f)
        val budgetAmount = findViewById<EditText>(R.id.editTextNumber2)

        // Restore the budget amount when the activity resumes
        if (savedBudget != 0f) {
            budgetAmount.setText(savedBudget.toString())
        }
    }
}
