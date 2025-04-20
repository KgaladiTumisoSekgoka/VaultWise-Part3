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
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var userId: Int = -1
    private lateinit var monthSpinner: Spinner
    private lateinit var minGoalEditText: EditText
    private lateinit var maxGoalEditText: EditText
    private lateinit var budgetAmountEditText: EditText

    private val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_goal_setup)

        db = AppDatabase.getDatabase(applicationContext)

        // UI Elements
        val btnSubmit = findViewById<Button>(R.id.button7)
        val btnReset = findViewById<Button>(R.id.button8)
        val btnBack = findViewById<ImageButton>(R.id.imageButton18)
        progressBar = findViewById(R.id.budgetProgressBar)
        progressText = findViewById(R.id.budgetProgressText)

        minGoalEditText = findViewById(R.id.editTextNumberDecimal2)
        maxGoalEditText = findViewById(R.id.editTextNumber)
        budgetAmountEditText = findViewById(R.id.editTextBudgetAmount)
        monthSpinner = findViewById(R.id.spinner_month)

        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)

        btnBack.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)

        val savedMonth = sharedPref.getString("budget_month", "January") ?: "January"
        val monthIndex = months.indexOf(savedMonth)
        if (monthIndex >= 0) monthSpinner.setSelection(monthIndex)

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedMonth = months[position]
                sharedPref.edit().putString("budget_month", selectedMonth).apply()
                loadBudgetData(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSubmit.setOnClickListener {
            val minGoal = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoal = maxGoalEditText.text.toString().toDoubleOrNull()
            val budgetAmount = budgetAmountEditText.text.toString().toDoubleOrNull()
            val selectedMonth = monthSpinner.selectedItem.toString()

            if (minGoal == null || maxGoal == null || budgetAmount == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val dao = db.budgetGoalDao()
                val existingGoal = dao.getBudgetByUserAndMonth(userId, selectedMonth)

                if (existingGoal != null) {
                    val updatedGoal = existingGoal.copy(
                        minGoal = minGoal,
                        maxGoal = maxGoal,
                        budgetAmount = budgetAmount,
                        remainingBudget = budgetAmount
                    )
                    dao.updateGoal(updatedGoal)
                } else {
                    val newGoal = BudgetGoal(
                        user_id = userId,
                        month = selectedMonth,
                        minGoal = minGoal,
                        maxGoal = maxGoal,
                        budgetAmount = budgetAmount,
                        remainingBudget = budgetAmount
                    )
                    dao.insertGoal(newGoal)
                }

                runOnUiThread {
                    updateProgressBar(minGoal, maxGoal, budgetAmount)
                    Toast.makeText(this@BudgetGoalSetup, "Budget updated for $selectedMonth", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnReset.setOnClickListener {
            minGoalEditText.text.clear()
            maxGoalEditText.text.clear()
            budgetAmountEditText.text.clear()
            monthSpinner.setSelection(0)
            progressBar.progress = 0
            progressText.text = ""
        }
    }

    private fun loadBudgetData(month: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.budgetGoalDao()
            val goal = dao.getBudgetByUserAndMonth(userId, month)

            runOnUiThread {
                if (goal != null) {
                    minGoalEditText.setText(goal.minGoal.toString())
                    maxGoalEditText.setText(goal.maxGoal.toString())
                    budgetAmountEditText.setText(goal.budgetAmount?.toString() ?: "")

                    updateProgressBar(goal.minGoal, goal.maxGoal, goal.budgetAmount ?: 0.0)
                } else {
                    minGoalEditText.setText("")
                    maxGoalEditText.setText("")
                    budgetAmountEditText.setText("")
                    progressBar.progress = 0
                    progressText.text = ""
                }
            }
        }
    }

    private fun updateProgressBar(min: Double, max: Double, current: Double) {
        if (max > min) {
            val progress = ((current - min) / (max - min) * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progress
            progressText.text = "Progress: $progress%"
        } else {
            progressBar.progress = 0
            progressText.text = "Invalid goal range"
        }
    }
}
