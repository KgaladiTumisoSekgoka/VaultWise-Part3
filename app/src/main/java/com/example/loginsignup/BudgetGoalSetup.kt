package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetGoalSetup : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private var userId: Int = -1
    private lateinit var monthSpinner: Spinner
    private lateinit var minGoalEditText: EditText
    private lateinit var maxGoalEditText: EditText
    private lateinit var budgetAmountEditText: EditText
    private var months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_goal_setup)

        db = AppDatabase.getDatabase(applicationContext)

        // UI elements
        val btnSubmit = findViewById<Button>(R.id.button7)
        val btnReset = findViewById<Button>(R.id.button8)
        val btnBack = findViewById<ImageButton>(R.id.imageButton18)

        minGoalEditText = findViewById(R.id.editTextNumberDecimal2)
        maxGoalEditText = findViewById(R.id.editTextNumber)
        budgetAmountEditText = findViewById(R.id.editTextBudgetAmount)
        monthSpinner = findViewById(R.id.spinner_month)

        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)

        btnBack.setOnClickListener {
            Log.d("SetupBudget", "Navigating back to HomeScreen")
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }
        // Load user ID
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)
        Log.d("BudgetGoalSetup", "User ID loaded: $userId")
        // Default to current month (or saved one)
        val savedMonth = sharedPref.getString("budget_month", "January") ?: "January"
        val monthIndex = months.indexOf(savedMonth)
        if (monthIndex >= 0) {
            monthSpinner.setSelection(monthIndex)
        }
        Log.d("BudgetGoalSetup", "Month loaded from sharedPref: $savedMonth")

        // Load existing data from RoomDB when month is selected
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedMonth = months[position]
                sharedPref.edit().putString("budget_month", selectedMonth).apply()
                Log.d("BudgetGoalSetup", "Month selected: $selectedMonth")
                loadBudgetData(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Submit: Save or Update
        btnSubmit.setOnClickListener {
            val minGoalValue = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoalValue = maxGoalEditText.text.toString().toDoubleOrNull()
            val budgetAmount = budgetAmountEditText.text.toString().toDoubleOrNull()
            val selectedMonth = monthSpinner.selectedItem.toString()
            Log.d("BudgetGoalSetup", "Submit clicked. minGoal: $minGoalValue, maxGoal: $maxGoalValue, budgetAmount: $budgetAmount")

            if (minGoalValue == null || maxGoalValue == null || budgetAmount == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                Log.d("BudgetGoalSetup", "Validation failed. One or more fields are empty.")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val dao = db.budgetGoalDao()
                val existingGoal = dao.getBudgetByUserAndMonth(userId, selectedMonth)

                if (existingGoal != null) {
                    Log.d("BudgetGoalSetup", "Updating existing goal for $selectedMonth")
                    val updated = existingGoal.copy(
                        minGoal = minGoalValue,
                        maxGoal = maxGoalValue,
                        budgetAmount = budgetAmount,
                        remainingBudget = budgetAmount
                    )
                    dao.updateGoal(updated)
                    Log.d("BudgetGoalSetup", "Goal updated: $updated")
                } else {
                    Log.d("BudgetGoalSetup", "Inserting new goal for $selectedMonth")
                    val newGoal = BudgetGoal(
                        user_id = userId,
                        month = selectedMonth,
                        minGoal = minGoalValue,
                        maxGoal = maxGoalValue,
                        budgetAmount = budgetAmount,
                        remainingBudget = budgetAmount
                    )
                    dao.insertGoal(newGoal)
                    Log.d("BudgetGoalSetup", "New goal inserted: $newGoal")
                }

                runOnUiThread {
                    Toast.makeText(this@BudgetGoalSetup, "Budget updated for $selectedMonth", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnReset.setOnClickListener {
            minGoalEditText.text.clear()
            maxGoalEditText.text.clear()
            budgetAmountEditText.text.clear()
            monthSpinner.setSelection(0)
            Log.d("BudgetGoalSetup", "Reset button clicked. All fields cleared.")
        }
    }

    private fun loadBudgetData(month: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.budgetGoalDao()
            val goal = dao.getBudgetByUserAndMonth(userId, month)
            Log.d("BudgetGoalSetup", "Loading budget data for month: $month")

            runOnUiThread {
                if (goal != null) {
                    Log.d("BudgetGoalSetup", "Budget data found: $goal")
                    minGoalEditText.setText(goal.minGoal.toString())
                    maxGoalEditText.setText(goal.maxGoal.toString())
                    budgetAmountEditText.setText(goal.budgetAmount?.toString() ?: "")
                } else {
                    Log.d("BudgetGoalSetup", "No budget data found for $month")
                    minGoalEditText.setText("")
                    maxGoalEditText.setText("")
                    budgetAmountEditText.setText("")
                }
            }
        }
    }
}
